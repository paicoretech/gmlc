package org.mobicents.gmlc.slee.supl;

import com.cloudhopper.smpp.SmppSession;
import org.mobicents.gmlc.slee.smpp.SmppSessionManager;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/*
 * For locally testing purpose, first you need to generate keystore/certificate/truststore with keytool
 * > keytool -genkey -keyalg RSA -keypass password \
                  -storepass password \
                  -keystore serverkeystore.jks
 * When keytool ask for FirsName and LastName, please set: localhost. This is very important for locally testing.
 * Now we will generate the server certificate based on the keystore generated before.
 * > keytool -export -storepass password \
                  -file server.cer \
                  -keystore serverkeystore.jks
 * Finally we will generate the truststore based on the server certificate, for locally testing this truststore
 * must be added in the client simulator.
 * > keytool -import -v -trustcacerts \
                     -file server.cer \
                     -keypass password \
                     -storepass password \
                     -keystore clienttruststore.jks
 *
 *
 * */

/**
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public class TlsServerSocket extends TlsServerSocketThread {

    private static final Logger logger = Logger.getLogger(TlsServerSocketThread.class.getName());
    private static int DefaultServerPort = 7275;
    private static String cert_password = "password";
    private static String cert_file_name = "serverkeystore.jks";
    /**
     * Constructs a TlsServerSocket.
     *
     * @param serverSocket SSL Server Socket
     */
    public TlsServerSocket(SSLServerSocket serverSocket) {
        super(serverSocket);
    }

    public static void main(String[] args) {
        try {
            logger.info("Lets start with it");
            SmppSession smppSession = SmppSessionManager.getInstance();
            logger.info("Status smpp session: " + smppSession.getStateName());
            logger.info("Status smpp session is open? " + smppSession.isOpen());
            TimeUnit.MINUTES.sleep(1);
            SmppSessionManager.destroySession();
            logger.info("After destroy status smpp session: " + smppSession.getStateName());
            logger.info("After destroy status smpp session is open? " + smppSession.isOpen());
        } catch (Exception ex) {
            logger.severe("Error, " + ex.fillInStackTrace());
        }
        // start();
    }

    public static void loadSSLCert() {
        try {
            URL pathToCert = TlsServerSocket.class.getClassLoader().getResource(cert_file_name);
            String absolutePath = pathToCert.getPath();
            System.setProperty("javax.net.ssl.keyStore", absolutePath);
            System.setProperty("javax.net.ssl.keyStorePassword", cert_password);
        } catch (Exception ex) {
            logger.severe("SSL Certificate could not be loaded: " + ex.getMessage());
        }
    }

    public static void start() {
        try {
            loadSSLCert();
            ServerSocketFactory ssf = TlsServerSocket.getServerSocketFactory();
            SSLServerSocket ss = (SSLServerSocket)ssf.createServerSocket(DefaultServerPort);
            new TlsServerSocket(ss);
        } catch (IOException e) {
            logger.severe("Unable to start ClassServer: " + e.getMessage());
        }
    }

    private static ServerSocketFactory getServerSocketFactory() {
        return SSLServerSocketFactory.getDefault();
    }
}
