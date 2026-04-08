package org.mobicents.gmlc.slee.supl;

import com.cloudhopper.smpp.SmppSession;
import com.objsys.asn1j.runtime.Asn1Boolean;
import com.objsys.asn1j.runtime.Asn1OctetString;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.mobicents.gmlc.slee.concurrent.SuplTransaction;
import org.mobicents.gmlc.slee.smpp.SmppSender;
import org.mobicents.gmlc.slee.smpp.SmppSessionManager;
import org.mobicents.gmlc.slee.supl.SUPL_INIT.SLPMode;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.TriggerParams;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.TriggerType;
import org.mobicents.gmlc.slee.supl.ULP.ULP_PDU;
import org.mobicents.gmlc.slee.supl.ULP_Components.IPAddress;
import org.mobicents.gmlc.slee.supl.ULP_Components.PosMethod;
import org.mobicents.gmlc.slee.supl.ULP_Components.QoP;
import org.mobicents.gmlc.slee.supl.ULP_Components.SLPAddress;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.AllowedReportingType;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.BasicProtectionParams;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.HistoricReporting;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.NotificationMode;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.ProtLevel;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.ProtectionLevel;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.ReportingCriteria;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.Ver2_SUPL_INIT_extension;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.GNSSPosTechnology;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SupportedNetworkInformation;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SupportedWCDMAInfo;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SupportedWLANApsList;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SupportedWLANInfo;
import org.mobicents.gmlc.slee.utils.ByteUtils;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 * @author <a href="mailto:joram.herrera2@gmail.com"> Joram Herrera </a>
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public class NetworkInitiatedSuplLocation implements Runnable {

    private static final Logger logger = Logger.getLogger(NetworkInitiatedSuplLocation.class.getName());
    private static final GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();
    private SmppSession smppSession;
    private boolean isSSLEnabled = gmlcPropertiesManagement.getSuplSslEnabled();
    private int SSLServerPort = gmlcPropertiesManagement.getSuplSslPort();
    private int NonSSLServerPort = gmlcPropertiesManagement.getSuplNoSslPort();
    private String cert_file_name = gmlcPropertiesManagement.getSuplTlsCertPath();
    private String cert_password = gmlcPropertiesManagement.getSuplTlsCertPwd();
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private static List<SuplChannel> targets = new ArrayList<>();
    private static ServerSocket serverSocket;
    private static final SuplTransaction suplTransaction = SuplTransaction.Instance();

    public NetworkInitiatedSuplLocation() throws IOException {
        this.serverSocket = createServerSocket();
            /* Initiate the SMPP connection */
        smppSession = SmppSessionManager.getInstance();
        new Thread(this).start();
    }

    public void loadSSLCert() {
        try {
            URL pathToCert = NetworkInitiatedSuplLocation.class.getClassLoader().getResource(cert_file_name);
            assert pathToCert != null;
            String absolutePath = pathToCert.getPath();
            System.setProperty("javax.net.ssl.keyStore", absolutePath);
            System.setProperty("javax.net.ssl.keyStorePassword", cert_password);
        } catch (Exception ex) {
            logger.severe("SSL Certificate could not be loaded: " + ex.getMessage());
        }
    }

    public ServerSocket createServerSocket() throws IOException {
        if (serverSocket != null)
            return serverSocket;
        if (isSSLEnabled) {
            logger.info(String.format("Create SSL Server on port %s", SSLServerPort));
            loadSSLCert();

            ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
            return ssf.createServerSocket(SSLServerPort);
        } else {
            logger.info(String.format("Create Non-SSL Server on port %s", NonSSLServerPort));
            return new ServerSocket(NonSSLServerPort);
        }
    }

    public SuplResponseHelperForMLP processNetworkInitiatedSuplBySmppStandardService(QoP qop, SLPMode slpMode, Integer transactionId, String msisdn) throws IOException {
       // final String suplInitDummy = "040002a0245418400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        SuplResponseHelperForMLP suplResponseHelperForMLP = new SuplResponseHelperForMLP();
        ULP_PDU suplInitUlpPdu = SuplChannel.buildUlpPduSuplInit(qop, null, null, slpMode);
        suplResponseHelperForMLP.setSessionID(suplInitUlpPdu.getSessionID());
        suplResponseHelperForMLP.setPosMethod(null);
        suplResponseHelperForMLP.setTriggerParams(null);
        suplResponseHelperForMLP.setTransactionId(transactionId);
        byte[] suplInitByteArray = ByteUtils.encodeUlp(suplInitUlpPdu);
        //byte[] suplInitByteArray = ByteUtils.hexStringToByteArray(suplInitDummy);
        suplTransaction.setValue(suplInitUlpPdu.getSessionID().getSlpSessionID(),"suplResponseHelperForMLP", suplResponseHelperForMLP);
        logger.info("Sending MT on SMPP");
        SmppSender.sendMt(msisdn, suplInitByteArray);
        return suplResponseHelperForMLP;
    }

    public SuplResponseHelperForMLP processNetworkInitiatedSuplBySmppAreaEventTriggeredService(QoP qop, int areaEventTrigger, PosMethod posMethod, SLPMode slpMode, TriggerParams triggerParams, Integer transactionId, String msisdn) {
        SuplResponseHelperForMLP suplResponseHelperForMLP = new SuplResponseHelperForMLP();
        Ver2_SUPL_INIT_extension ver2_supl_init_extension = buildVer2SuplInitExtension(areaEventTrigger);
        ULP_PDU suplInitUlpPdu = SuplChannel.buildUlpPduSuplInit(qop, ver2_supl_init_extension, posMethod, slpMode);
        suplResponseHelperForMLP.setSessionID(suplInitUlpPdu.getSessionID());
        suplResponseHelperForMLP.setPosMethod(posMethod);
        suplResponseHelperForMLP.setTriggerParams(triggerParams);
        suplResponseHelperForMLP.setTransactionId(transactionId);
        byte[] suplInitByteArray = ByteUtils.encodeUlp(suplInitUlpPdu);
        suplTransaction.setValue(suplInitUlpPdu.getSessionID().getSlpSessionID(),"suplResponseHelperForMLP", suplResponseHelperForMLP);
        SmppSender.sendMt(msisdn, suplInitByteArray);
        return suplResponseHelperForMLP;
    }


    //TODO implement (SET Lookup, Routing Info), default is the first connection
    public SuplResponseHelperForMLP processNetworkInitiatedSuplStandardService(QoP qop, SLPMode slpMode, Integer transactionId) throws IOException {
        if (!targets.isEmpty()) {
            logger.warning("targets is not empty");
            return targets.get(0).sendSuplInit(qop, null, null, slpMode, null, transactionId);
        }
        return null;
    }

    public SuplResponseHelperForMLP processNetworkInitiatedSuplAreaEventTriggeredService(QoP qop, int areaEventTrigger, PosMethod posMethod, SLPMode slpMode, TriggerParams triggerParams, Integer transactionId) throws IOException {
        if (!targets.isEmpty()) {
            logger.warning("targets is not empty");
            Ver2_SUPL_INIT_extension ver2_supl_init_extension = buildVer2SuplInitExtension(areaEventTrigger);
            return targets.get(0).sendSuplInit(qop, ver2_supl_init_extension, posMethod, slpMode, triggerParams, transactionId);
        }
        return null;
    }

    private Ver2_SUPL_INIT_extension buildVer2SuplInitExtension(int areaEventTrigger) {
        NotificationMode notificationMode = NotificationMode.normal();
        Asn1Boolean wlan, gsm, wcdma, cdma, hrdp, umb, lte, wimax, historic, nonServing, nr;
        gsm = wcdma = cdma = lte = nr = new Asn1Boolean(true);
        wlan = hrdp = umb = wimax = historic = nonServing = new Asn1Boolean(false);
        SupportedWLANInfo supportedWLANInfo = null;
        SupportedWLANApsList supportedWLANApsList = null;
        SupportedWCDMAInfo supportedWCDMAInfo = new SupportedWCDMAInfo(true);
        Asn1Boolean uTRANGPSReferenceTime = new Asn1Boolean(true);
        Asn1Boolean uTRANGANSSReferenceTime = new Asn1Boolean(true);
        SupportedNetworkInformation supportedNetworkInformation = new SupportedNetworkInformation(wlan, supportedWLANInfo, supportedWLANApsList,
                gsm, wcdma , supportedWCDMAInfo, cdma, hrdp, umb, lte, wimax, historic, nonServing, uTRANGPSReferenceTime, uTRANGANSSReferenceTime, nr);
        TriggerType triggerType = TriggerType.valueOf(areaEventTrigger);
        SLPAddress e_SLPAddress = new SLPAddress();
        Asn1OctetString socketAddress = new Asn1OctetString(serverSocket.getInetAddress().getAddress());
        IPAddress ipAddress = new IPAddress();
        ipAddress.setIpv4Address(socketAddress);
        e_SLPAddress.setIPAddress(ipAddress);
        AllowedReportingType allowedReportingType = AllowedReportingType.positionsAndMeasurements();
        ReportingCriteria reportingCriteria = new ReportingCriteria();
        reportingCriteria.setMinTimeInterval(10);
        reportingCriteria.setMaxNumberofReports(30);
        HistoricReporting historicReporting = new HistoricReporting(allowedReportingType, reportingCriteria);
        ProtLevel protLevel = ProtLevel.nullProtection();
        BasicProtectionParams basicProtectionParams = null;
        ProtectionLevel protectionLevel = new ProtectionLevel(protLevel, basicProtectionParams);
        Asn1Boolean gps, galileo, sbas, modernized_gps, qzss, glonass, bds, rtk_osr;
        gps = galileo = modernized_gps = glonass = new Asn1Boolean(true);
        sbas = qzss = bds = rtk_osr = new Asn1Boolean(false);
        GNSSPosTechnology gnssPosTechnology = new GNSSPosTechnology(gps, galileo,sbas, modernized_gps, qzss, glonass, bds, rtk_osr);
        long minimumMajorVersion = 0;
        return new Ver2_SUPL_INIT_extension (notificationMode, supportedNetworkInformation,
                triggerType, e_SLPAddress, historicReporting, protectionLevel, gnssPosTechnology, minimumMajorVersion);
    }

    public void removeConnection(SuplChannel suplChannel) {
        targets.remove(suplChannel);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    @Override
    public void run() {

        while (true) { // accept al connections
            try {
                Socket socket = serverSocket.accept();
                SuplChannel suplChannel = new SuplChannel(this, socket);
                targets.clear();
                targets.add(suplChannel);
            } catch (IOException e) {
                logger.severe("Error accepting connection: " + e.getMessage());
            }
        }
    }
}
