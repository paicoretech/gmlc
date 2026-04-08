package org.mobicents.gmlc.slee.smpp;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SmppSessionManager {

    private static final GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(SmppSessionManager.class);

    private static final String SMPP_HOST = gmlcPropertiesManagement.getSmppHost() != null ? gmlcPropertiesManagement.getSmppHost() : "localhost";
    private static final int SMPP_PORT = gmlcPropertiesManagement.getSmppPort() != null ? gmlcPropertiesManagement.getSmppPort() : 2776;
    private static final String SMPP_SID = gmlcPropertiesManagement.getSmppSid() != null ? gmlcPropertiesManagement.getSmppSid() : "gmlc_ulp_smpp";
    private static final String SMPP_PWD = gmlcPropertiesManagement.getSmppPwd() != null ? gmlcPropertiesManagement.getSmppPwd() : "gmlc_ulp_smpp";

    private static SmppSession smppSession;
    private static DefaultSmppClient clientBootstrap;
    private static ScheduledThreadPoolExecutor monitorExecutor;

    private static final long UNBIND_MILLIS = 1L;

    /**
     * Returns an instance for an SMPP Session using Singleton pattern to ensure only one instance is alive while the app is running
     *
     * @return an SmppSession object
     */
    public static SmppSession getInstance() {
        if (smppSession == null || !smppSession.isBound()) {
            return initSmppSession();
        }
        return smppSession;
    }

    private static SmppSession initSmppSession() {
        // If the smppSession is not null, but is not bound,
        // we will prior destroy the session to create a new one again
        if (smppSession != null && !smppSession.isBound()) {
            destroySession();
        }
        monitorExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private final AtomicInteger sequence = new AtomicInteger(0);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                Thread t = new Thread(r);
                t.setName("SmppClientSessionWindowMonitorPool-" + sequence.getAndIncrement());
                return t;
            }
        });
        try {
            clientBootstrap = new DefaultSmppClient(Executors.newCachedThreadPool(), 1, monitorExecutor);

            SmppSessionConfiguration config0 = new SmppSessionConfiguration();
            config0.setWindowSize(1);
            config0.setName("client-smpp");
            config0.setType(SmppBindType.TRANSCEIVER);
            config0.setHost(SMPP_HOST);
            config0.setPort(SMPP_PORT);
            config0.setConnectTimeout(10000);
            config0.setSystemId(SMPP_SID);
            config0.setPassword(SMPP_PWD);
            config0.getLoggingOptions().setLogBytes(true);
            config0.setRequestExpiryTimeout(30000);
            config0.setWindowMonitorInterval(15000);
            config0.setCountersEnabled(true);
            ClientSmppSessionHandler sessionHandler = new ClientSmppSessionHandler();

            smppSession = clientBootstrap.bind(config0, sessionHandler);
            sessionHandler.setSession(smppSession);
            return smppSession;
        } catch (Exception e) {
          logger.error("An exception has occurred while attempting to bind an SMPP Session {}", e.getMessage());
          return null;
        }
    }

    /**
     * Destroys the current (and only) SMPP Session when the app is stopped
     */
    public static void destroySession() {
        try {
            //Sending SMPP unbind signal
            smppSession.unbind(UNBIND_MILLIS);
            smppSession.close();
            smppSession.destroy();
            clientBootstrap.destroy();
            monitorExecutor.shutdownNow();
        } catch (Exception e) {
          logger.error("An exception has occurred while attempting to stop current SMPP Session {}", e.getMessage());
        }
    }
}
