package org.mobicents.gmlc.slee.supl;

import com.objsys.asn1j.runtime.Asn1BitString;
import com.objsys.asn1j.runtime.Asn1Exception;
import com.objsys.asn1j.runtime.Asn1Integer;
import com.objsys.asn1j.runtime.Asn1OctetString;
import com.objsys.asn1j.runtime.Asn1PerDecodeBuffer;
import com.objsys.asn1j.runtime.Asn1PerEncodeBuffer;
import com.objsys.asn1j.runtime.Asn1UTCTime;
import org.mobicents.gmlc.slee.concurrent.SuplTransaction;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.A_GNSS_ProvideAssistanceData;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.GNSS_ReferenceLocation;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.Initiator;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.LPP_Message;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.LPP_MessageBody;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.LPP_MessageBody_c1;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.LPP_TransactionID;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideAssistanceData;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideAssistanceData_criticalExtensions;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideAssistanceData_r9_IEs;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideLocationInformation;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideLocationInformation_criticalExtensions;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideLocationInformation_criticalExtensions_c1;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.ProvideLocationInformation_r9_IEs;
import org.mobicents.gmlc.slee.lpp.LPP_PDU_Definitions.TransactionNumber;
import org.mobicents.gmlc.slee.primitives.EUTRANCGIImpl;
import org.mobicents.gmlc.slee.primitives.NRCellGlobalIdImpl;
import org.mobicents.gmlc.slee.supl.SUPL_AUTH_REQ.SUPLAUTHREQ;
import org.mobicents.gmlc.slee.supl.SUPL_AUTH_RESP.SUPLAUTHRESP;
import org.mobicents.gmlc.slee.supl.SUPL_END.SUPLEND;
import org.mobicents.gmlc.slee.supl.SUPL_INIT.SLPMode;
import org.mobicents.gmlc.slee.supl.SUPL_INIT.SUPLINIT;
import org.mobicents.gmlc.slee.supl.SUPL_POS.PosPayLoad;
import org.mobicents.gmlc.slee.supl.SUPL_POS.SUPLPOS;
import org.mobicents.gmlc.slee.supl.SUPL_POS_INIT.SUPLPOSINIT;
import org.mobicents.gmlc.slee.supl.SUPL_REPORT.PositionData;
import org.mobicents.gmlc.slee.supl.SUPL_REPORT.ReportData;
import org.mobicents.gmlc.slee.supl.SUPL_REPORT.ReportDataList;
import org.mobicents.gmlc.slee.supl.SUPL_REPORT.Ver2_SUPLREPORT;
import org.mobicents.gmlc.slee.supl.SUPL_START.SETCapabilities;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_RESPONSE.Ver2_SUPLTRIGGEREDRESPONSE;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.TriggerParams;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.Ver2_SUPLTRIGGEREDSTART;
import org.mobicents.gmlc.slee.supl.ULP.ULP_PDU;
import org.mobicents.gmlc.slee.supl.ULP.UlpMessage;
import org.mobicents.gmlc.slee.supl.ULP_Components.CellInfo;
import org.mobicents.gmlc.slee.supl.ULP_Components.FQDN;
import org.mobicents.gmlc.slee.supl.ULP_Components.GsmCellInformation;
import org.mobicents.gmlc.slee.supl.ULP_Components.IPAddress;
import org.mobicents.gmlc.slee.supl.ULP_Components.PosMethod;
import org.mobicents.gmlc.slee.supl.ULP_Components.Position;
import org.mobicents.gmlc.slee.supl.ULP_Components.PositionEstimate;
import org.mobicents.gmlc.slee.supl.ULP_Components.PositionEstimate_latitudeSign;
import org.mobicents.gmlc.slee.supl.ULP_Components.PositionEstimate_uncertainty;
import org.mobicents.gmlc.slee.supl.ULP_Components.QoP;
import org.mobicents.gmlc.slee.supl.ULP_Components.SETId;
import org.mobicents.gmlc.slee.supl.ULP_Components.SessionID;
import org.mobicents.gmlc.slee.supl.ULP_Components.SetSessionID;
import org.mobicents.gmlc.slee.supl.ULP_Components.Status;
import org.mobicents.gmlc.slee.supl.ULP_Components.Ver;
import org.mobicents.gmlc.slee.supl.ULP_Components.Version;
import org.mobicents.gmlc.slee.supl.ULP_Components.WcdmaCellInformation;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_message_extensions.Ver2_SUPL_INIT_extension;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_parameter_extensions.Ver2_PosPayLoad_extension;
import org.mobicents.gmlc.slee.supl.ULP_Version_2_parameter_extensions.Ver2_PosPayLoad_extension_lPPPayload;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.CellGlobalIdEUTRA;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.CellIdentity;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.CellIdentityNR;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.LteCellInformation;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.MCC;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.MNC;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.NRCellInformation;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.PLMN_Identity;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SPCSETKey;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SPCSETKeylifetime;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.SPCTID;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.ServCellNR;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.ServingCellInformationNR;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.TrackingAreaCode;
import org.mobicents.gmlc.slee.utils.ByteUtils;
import org.mobicents.gmlc.slee.utils.GADShapesUtils;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;

import javax.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * @author <a href="mailto:joram.herrera2@gmail.com"> Joram Herrera </a>
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 * @author <a href="mailto:kennymendieta89@gmail.com"> Kenny Mendieta </a>
 */
public class SuplChannel {

    private static final Logger logger = Logger.getLogger(SuplChannel.class.getName());

    private static final SuplTransaction suplTransaction = SuplTransaction.Instance();

    private Socket socket;
    private final short HEADER_SIZE = 2;
    /**
     * BUFFER_SIZE data size that is enough to hold SUPL responses
     */
    private static final int RESPONSE_BUFFER_SIZE = 16384;
    /**
     * SET_KEY_LIFETIME: Value in hours. Valid range from 1 to 24
    **/
    private static final Long SET_KEY_LIFETIME = 24L;
    /**
     * DEFAULT_FQDN: 0.0.0.0
     **/
    private static final String DEFAULT_FQDN = "0.0.0.0";
    private final ByteBuffer messageLengthReadBuffer = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN);
    SuplResponseHelperForMLP suplResponseHelperForMLP = new SuplResponseHelperForMLP();
    private NetworkInitiatedSuplLocation networkInitiatedSuplLocation;

    public SuplChannel(NetworkInitiatedSuplLocation networkInitiatedSuplLocation, Socket socket) throws IOException {
        this.socket = socket;
        this.networkInitiatedSuplLocation = networkInitiatedSuplLocation;
        logger.warning("New connection established: " + socket);
        processMessage(readBuffer());
    }

    public static ULP_PDU buildUlpPduSuplInit(QoP qop, Ver2_SUPL_INIT_extension ver2_supl_init_extension, PosMethod posMethod, SLPMode slpMode) {
        SUPLINIT suplInit = new SUPLINIT();
        if (posMethod == null)
            suplInit.setPosMethod(PosMethod.agpsSETbased());
        else
            suplInit.setPosMethod(posMethod);
        suplInit.setVer2_SUPL_INIT_extension(ver2_supl_init_extension);
        suplInit.setQoP(qop);
        suplInit.setSLPMode(slpMode);
        suplInit.setSLPAddress(suplTransaction.getSLPAddress());
        UlpMessage ulpMessageSuplInit = new UlpMessage();
        ulpMessageSuplInit.setMsSUPLINIT(suplInit);
        SessionID sessionID = new SessionID();
        sessionID.setSlpSessionID(suplTransaction.create(SuplSessionType.SUPL_SINGLE));
        return createULP_PDU(ulpMessageSuplInit, sessionID, null);
    }


    public SuplResponseHelperForMLP sendSuplInit(QoP qop, Ver2_SUPL_INIT_extension ver2_supl_init_extension, PosMethod posMethod,
                                                 SLPMode slpMode, TriggerParams triggerParams, Integer transactionId) throws IOException {
        logger.warning("To send SUPL INIT message to the SET");
        suplResponseHelperForMLP = new SuplResponseHelperForMLP();
        ULP_PDU suplInitUlpPdu = buildUlpPduSuplInit(qop, ver2_supl_init_extension, posMethod, slpMode);
        byte[] suplInitByteArray = encodeUlp(suplInitUlpPdu);
        socket.getOutputStream().write(suplInitByteArray);
        suplResponseHelperForMLP.setPosMethod(posMethod);
        suplResponseHelperForMLP.setTriggerParams(triggerParams);
        suplResponseHelperForMLP.setTransactionId(transactionId);
        logger.info("SUPL INIT message sent to the SET");
        return processMessage(readBuffer());
    }

    private void sendSuplPosAck(SessionID sessionID) throws IOException {
        UlpMessage ulpMessage = new UlpMessage();
        SUPLPOS suplpos = new SUPLPOS();
        PosPayLoad posPayLoad = new PosPayLoad();
        Ver2_PosPayLoad_extension extension = new Ver2_PosPayLoad_extension();
        Ver2_PosPayLoad_extension_lPPPayload lPPPayload = new Ver2_PosPayLoad_extension_lPPPayload();
        lPPPayload.setElements(new Asn1OctetString[]{new Asn1OctetString(encodeLpp(newProvideLocationMessage()))});
        extension.setLPPPayload(lPPPayload);
        posPayLoad.setVer2_PosPayLoad_extension(extension);
        suplpos.setPosPayLoad(posPayLoad);
        ulpMessage.setMsSUPLPOS(suplpos);
        ULP_PDU ulpPdu = createULP_PDU(ulpMessage, sessionID, null);
        byte[] bytes = encodeUlp(ulpPdu);
        socket.getOutputStream().write(bytes);
        logger.info("Sent the SUPL POS message to the SET");
    }

    private void sendSuplEnd(SessionID sessionID) throws IOException {
        logger.warning("Send the SUPL END message to the SET");
        //suplTransaction.destroy(sessionID.getSlpSessionID());
        SUPLEND suplend = new SUPLEND();
        UlpMessage ulpMessageSuplEnd = new UlpMessage();
        ulpMessageSuplEnd.setMsSUPLEND(suplend);
        ULP_PDU suplEndUlpPdu = createULP_PDU(ulpMessageSuplEnd, sessionID, null);
        socket.getOutputStream().write(encodeUlp(suplEndUlpPdu));
    }

    private void sendSuplTriggeredResponse(SessionID sessionID, PosMethod posMethod, TriggerParams triggerParams) throws IOException {
        logger.warning("Send the SUPL TRIGGERED RESPONSE message to the SET");
        Ver2_SUPLTRIGGEREDRESPONSE suplTriggeredResponse = new Ver2_SUPLTRIGGEREDRESPONSE();
        UlpMessage ulpMessageSuplTriggeredResponse = new UlpMessage();
        if (posMethod == null)
            suplTriggeredResponse.setPosMethod(PosMethod.agpsSETbased());
        else
            suplTriggeredResponse.setPosMethod(posMethod);
        suplTriggeredResponse.setTriggerParams(triggerParams);
        ulpMessageSuplTriggeredResponse.setMsSUPLTRIGGEREDRESPONSE(suplTriggeredResponse);
        ULP_PDU suplTriggeredResponseUlpPdu = createULP_PDU(ulpMessageSuplTriggeredResponse, sessionID, null);
        socket.getOutputStream().write(encodeUlp(suplTriggeredResponseUlpPdu));
    }

    private void sendSuplReport(SessionID sessionID, Position position) throws IOException {
        UlpMessage ulpMessageSuplReport = new UlpMessage();
        Ver2_SUPLREPORT ver2SuplReport = new Ver2_SUPLREPORT();

        ReportDataList reportDataList = new ReportDataList();
        ReportData reportData = new ReportData();
        PositionData positionData = new PositionData();
        positionData.setPosition(position);
        reportData.setPositionData(positionData);
        reportDataList.setElements(new ReportData[]{reportData});

        ver2SuplReport.setReportDataList(reportDataList);

        ulpMessageSuplReport.setMsSUPLREPORT(ver2SuplReport);
        ULP_PDU suplTriggeredResponseUlpPdu = createULP_PDU(ulpMessageSuplReport, sessionID, null);
        socket.getOutputStream().write(encodeUlp(suplTriggeredResponseUlpPdu));
    }

    private void sendSuplAuthResponse(SessionID sessionID, SUPLAUTHREQ suplauthreq) throws IOException {
        String keyString = ByteUtils.generateRandomStringByLength(16, false);
        SPCSETKey spcsetKey = new SPCSETKey(keyString.getBytes());
        String randNumber = ByteUtils.generateRandomStringByLength(16, true);
        FQDN fqdn = sessionID.getSlpSessionID() != null
                        && sessionID.getSlpSessionID().getSlpId() != null
                        && sessionID.getSlpSessionID().getSlpId().getFqdn() != null
                        ? sessionID.getSlpSessionID().getSlpId().getFqdn() : new FQDN(DEFAULT_FQDN);
        SPCTID spctid = new SPCTID(new Asn1BitString(randNumber), fqdn);
        SPCSETKeylifetime keylifetime = new SPCSETKeylifetime(SET_KEY_LIFETIME);
        SUPLAUTHRESP suplauthresp = new SUPLAUTHRESP(spcsetKey, spctid, keylifetime);
        UlpMessage ulpMessageSuplAuthResp = new UlpMessage();
        ulpMessageSuplAuthResp.setMsSUPLAUTHRESP(suplauthresp);
        ULP_PDU suplInitUlpPdu = createULP_PDU(ulpMessageSuplAuthResp, sessionID, null);
        byte[] suplAuthRespByteArray = encodeUlp(suplInitUlpPdu);
        socket.getOutputStream().write(suplAuthRespByteArray);
    }

    private LPP_Message newProvideLocationMessage() {
        LPP_MessageBody messageBody = new LPP_MessageBody();
        LPP_MessageBody_c1 messageBodyC1 = new LPP_MessageBody_c1();
        ProvideLocationInformation provideLocationInformation = new ProvideLocationInformation();
        ProvideLocationInformation_criticalExtensions criticalExtensions = new ProvideLocationInformation_criticalExtensions();
        ProvideLocationInformation_criticalExtensions_c1 criticalExtensionsC1 = new ProvideLocationInformation_criticalExtensions_c1();
        criticalExtensionsC1.setProvideLocationInformation_r9(new ProvideLocationInformation_r9_IEs());
        criticalExtensions.setC1(criticalExtensionsC1);
        provideLocationInformation.setCriticalExtensions(criticalExtensions);
        messageBodyC1.setProvideLocationInformation(provideLocationInformation);
        messageBody.setC1(messageBodyC1);
        LPP_Message message = new LPP_Message();
        message.setTransactionID(newTransactionId(4));
        message.setEndTransaction(false);
        message.setLpp_MessageBody(messageBody);

        return message;
    }

    private LPP_TransactionID newTransactionId(int id) {
        LPP_TransactionID transactionId = new LPP_TransactionID();
        TransactionNumber transactionNumber = new TransactionNumber(id);
        transactionId.setTransactionNumber(transactionNumber);
        transactionId.setInitiator(Initiator.targetDevice());

        return transactionId;
    }

    private SuplResponseHelperForMLP processMessage(byte[] msg) throws IOException {
        SuplMessageParams params = new SuplMessageParams();
        if (msg != null && msg.length > 0) {
            ULP_PDU ulpPdu = new ULP_PDU();
            ulpPdu.decode(new Asn1PerDecodeBuffer(msg, false));
            switch (ulpPdu.getMessage().getElemName()) {
                case "msSUPLSTART":
                    logger.info("Received the SUPL START message from the SET: ");
                    // TODO: implement SUPL START
                    break;
                case "msSUPLPOSINIT":
                    SUPLPOSINIT msSuplPosInit = ulpPdu.getMessage().getMsSUPLPOSINIT();
                    logger.warning("Received the SUPL POS INIT message from the SET: " + msSuplPosInit);
                    try {
                        if (msSuplPosInit.getLocationId() != null) {
                            if (msSuplPosInit.getLocationId().getCellInfo() != null) {
                                CellInfo cellInfo = msSuplPosInit.getLocationId().getCellInfo();
                                suplResponseHelperForMLP = params.setCellInfo(cellInfo, suplResponseHelperForMLP);
                            }
                            if (msSuplPosInit.getPosition() != null) {
                                Position position = msSuplPosInit.getPosition();
                                if (position.getPositionEstimate() != null) {
                                    PositionEstimate positionEstimate = position.getPositionEstimate();
                                    suplResponseHelperForMLP = params.setPositionEstimate(suplResponseHelperForMLP, positionEstimate);
                                }

                                if (suplResponseHelperForMLP.getTriggerParams() != null) {
                                    //TODO check QoP and if true, send SUPL REPORT
                                    sendSuplReport(ulpPdu.getSessionID(), position);
                                }
                            }
                        }
                    } catch (Asn1Exception asn1Exception) {
                        logger.severe(asn1Exception.getMessage());
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                    return processMessage(readBuffer());
                    //break;
                case "msSUPLPOS":
                    SUPLPOS msSuplPos = ulpPdu.getMessage().getMsSUPLPOS();
                    logger.warning("Received the SUPL POS message from the SET: " + msSuplPos);
                    if (msSuplPos.getPosPayLoad().isVer2_PosPayLoad_extension()) {
                        Ver2_PosPayLoad_extension_lPPPayload lPPPayload = msSuplPos.getPosPayLoad().getVer2_PosPayLoad_extension().getLPPPayload();
                        LPP_Message lpp_message = new LPP_Message();
                        try {
                            lpp_message.decode(new Asn1PerDecodeBuffer(lPPPayload.getElements()[0].value, false));
                            LPP_MessageBody lppMessageBody = lpp_message.getLpp_MessageBody();
                            if (lppMessageBody.isC1()) {
                                LPP_MessageBody_c1 lppMessageBodyC1 = lppMessageBody.getC1();
                                if (lppMessageBodyC1 != null) {
                                    if (lppMessageBodyC1.isProvideAssistanceData()) {
                                        ProvideAssistanceData lppAssistanceData = lppMessageBodyC1.getProvideAssistanceData();
                                        if (lppAssistanceData.getCriticalExtensions() != null) {
                                            ProvideAssistanceData_criticalExtensions lppAssistDataCriticalExt = lppAssistanceData.getCriticalExtensions();
                                            if (lppAssistDataCriticalExt.isC1()) {
                                                if (lppAssistDataCriticalExt.getC1().isProvideAssistanceData_r9()) {
                                                    ProvideAssistanceData_r9_IEs lppAssistDataR9 = lppAssistDataCriticalExt.getC1().getProvideAssistanceData_r9();
                                                    if (lppAssistDataR9.hasA_gnss_ProvideAssistanceData()) {
                                                        A_GNSS_ProvideAssistanceData aGnssAssistData = lppAssistDataR9.getA_gnss_ProvideAssistanceData();
                                                        if (aGnssAssistData.hasGnss_CommonAssistData()) {
                                                            if (aGnssAssistData.getGnss_CommonAssistData().getGnss_ReferenceLocation() != null) {
                                                                GNSS_ReferenceLocation gnssReferenceLocation = aGnssAssistData.getGnss_CommonAssistData().getGnss_ReferenceLocation();
                                                                if (gnssReferenceLocation.getThreeDlocation() != null) {
                                                                    EllipsoidPointWithAltitudeAndUncertaintyEllipsoid ellipsoid = gnssReferenceLocation.getThreeDlocation();
                                                                    suplResponseHelperForMLP = params.setEllipsoidParams(suplResponseHelperForMLP, ellipsoid);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //Send SUPL POS ACK
                            sendSuplPosAck(ulpPdu.getSessionID());

                            if (suplResponseHelperForMLP.getTriggerParams() == null) {
                                //Send SUPL END
                                sendSuplEnd(ulpPdu.getSessionID());
                            } else {
                                // TODO: calculate position
                                Position position = new Position();

                                Calendar currentTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
                                Asn1UTCTime asn1UTCTime = new Asn1UTCTime();
                                asn1UTCTime.setTime(currentTime);
                                position.setTimestamp(asn1UTCTime);

                                PositionEstimate posEstimate = new PositionEstimate();

                                posEstimate.setLatitude(params.latitude);
                                posEstimate.setLongitude(params.longitude);
                                posEstimate.setLatitudeSign(params.latitude.value < 0 ? PositionEstimate_latitudeSign.south() : PositionEstimate_latitudeSign.north());

                                position.setPositionEstimate(posEstimate);
                                sendSuplReport(ulpPdu.getSessionID(), position);
                                return processMessage(readBuffer());
                                //END
                            }
                            updateSupSession(ulpPdu,suplResponseHelperForMLP);
                            return suplResponseHelperForMLP;
                        } catch (Asn1Exception asn1Exception) {
                            logger.severe(asn1Exception.getMessage());
                        } catch (IOException ioException) {
                            logger.severe(ioException.getMessage());
                        }
                    }
                    break;
                case "msSUPLTRIGGEREDSTART":
                    Ver2_SUPLTRIGGEREDSTART msSuplTriggeredStart = ulpPdu.getMessage().getMsSUPLTRIGGEREDSTART();
                    logger.warning("Received the SUPL TRIGGERED START message from the SET: " + msSuplTriggeredStart);
                    String splId = new String(ulpPdu.getSessionID().getSlpSessionID().getSessionID().value);
                    suplResponseHelperForMLP = (SuplResponseHelperForMLP) suplTransaction.getValue(splId,"suplResponseHelperForMLP");
                    try {
                        if (msSuplTriggeredStart.getLocationId() != null) {
                            CellInfo cellInfo = msSuplTriggeredStart.getLocationId().getCellInfo();
                            params.setCellInfo(cellInfo, suplResponseHelperForMLP);
                        }
                        if (msSuplTriggeredStart.getPosition() != null) {
                            Position position = msSuplTriggeredStart.getPosition();
                            if (position.getPositionEstimate() != null) {
                                PositionEstimate positionEstimate = position.getPositionEstimate();
                                suplResponseHelperForMLP = params.setPositionEstimate(suplResponseHelperForMLP, positionEstimate);
                            }
                        }

                        SETCapabilities setCapabilities = msSuplTriggeredStart.getSETCapabilities();
                        Ver ver = msSuplTriggeredStart.getVer();
                        Status status = msSuplTriggeredStart.getLocationId().getStatus();

                        // Send SUPL TRIGGERED RESPONSE
                        sendSuplTriggeredResponse(ulpPdu.getSessionID(), suplResponseHelperForMLP.getPosMethod(), suplResponseHelperForMLP.getTriggerParams());
                        return processMessage(readBuffer());
                    } catch (Asn1Exception asn1Exception) {
                        logger.severe(asn1Exception.getMessage());
                    } catch (MAPException e) {
                        logger.severe(e.getMessage());
                    }
                    break;
                case "msSUPLREPORT":
                    Ver2_SUPLREPORT msSuplReport = ulpPdu.getMessage().getMsSUPLREPORT();
                    logger.warning("Received the SUPL REPORT message from the SET: " + msSuplReport);
                    suplResponseHelperForMLP.setReport(true);

                    sendSuplEnd(ulpPdu.getSessionID());
                    updateSupSession(ulpPdu,suplResponseHelperForMLP);
                    return suplResponseHelperForMLP;

                case "msSUPLAUTHREQ":
                    SUPLAUTHREQ suplauthreq = ulpPdu.getMessage().getMsSUPLAUTHREQ();
                    logger.warning("Received the message msSUPLAUTHREQ: " + suplauthreq);
                    sendSuplAuthResponse(ulpPdu.getSessionID(), suplauthreq);
                    logger.info("Sent SUPLAUTHRESP");
                    return processMessage(readBuffer());
            }
        }
        return null;
    }

    private void updateSupSession(ULP_PDU ulpPdu, SuplResponseHelperForMLP suplResponseHelperForMLP) throws IOException {
        suplTransaction.setValue(ulpPdu.getSessionID().getSlpSessionID(),"sessionStatus",0);
        suplTransaction.setValue(ulpPdu.getSessionID().getSlpSessionID(),"suplResponseHelperForMLP", suplResponseHelperForMLP);
    }

    /**
     * Reads SUPL server response and returns it as a byte array.
     *
     * <p>Upon the SUPL protocol, the size of the payload is stored in the first two bytes of the
     * response, hence these two bytes are read first followed by reading a payload of that size. Null
     * is returned if the size of the payload is not readable.
     */
    public byte[] getBytes(InputStream is) throws IOException {
        byte[] buffer = new byte[RESPONSE_BUFFER_SIZE];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
        // TODO: currently this fails, after "cast" bytes to SUPL class this will be fixed
        int sizeOfRead = bufferedInputStream.read(buffer, 0, HEADER_SIZE);
        if (sizeOfRead == HEADER_SIZE) {
            messageLengthReadBuffer.clear();
            messageLengthReadBuffer.put(0, buffer[0]);
            messageLengthReadBuffer.put(1, buffer[1]);
            int messageLength = messageLengthReadBuffer.getShort(0);

            int bytesRead = sizeOfRead;
            while (bytesRead < messageLength) {
                sizeOfRead = bufferedInputStream.read(buffer, bytesRead, messageLength - bytesRead);
                bytesRead = bytesRead + sizeOfRead;
            }
            return Arrays.copyOf(buffer, messageLength);
        } else if (sizeOfRead == -1) {
            socket.close();
            return null;
        } else {
            return new byte[]{};
        }
    }

    protected static ULP_PDU createULP_PDU(UlpMessage message, @Nullable SessionID sessionId, @Nullable InetAddress ipAddress) {
        ULP_PDU ULP_PDU = new ULP_PDU();
        ULP_PDU.setMessage(message);
        ULP_PDU.setLength(new Asn1Integer(0));

        // Set the version of SUPL protocol
        // V2.1 or above is required for GLONASS assistance data
        Version version = new Version();
        version.setMaj(new Asn1Integer((2L)));
        version.setMin(new Asn1Integer((1L)));
        version.setServind(new Asn1Integer(0));
        ULP_PDU.setVersion(version);

        if (sessionId == null) {
            sessionId = new SessionID();
            SetSessionID setSessionId = new SetSessionID();
            setSessionId.setSessionId(new Asn1Integer((new Random().nextInt(65536))));
            if (ipAddress == null) {
                try {
                    ipAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
            byte[] ipAsBytes = ipAddress.getAddress();
            SETId setId = new SETId();
            IPAddress ipAddress1 = new IPAddress();
            ipAddress1.setIpv4Address(new Asn1OctetString(ipAsBytes));
            setId.setIPAddress(ipAddress1);
            setSessionId.setSetId(setId);
            sessionId.setSetSessionID(setSessionId);
            ULP_PDU.setSessionID(sessionId);
        }
        ULP_PDU.setSessionID(sessionId);

        return ULP_PDU;
    }

    private byte[] encodeUlp(ULP_PDU message) {
        logger.info("Encoding ULP \n" + message.toString());
        try {
            Asn1PerEncodeBuffer outputStream = new Asn1PerEncodeBuffer(false);
            message.setLength(new Asn1Integer(0));
            message.encode(outputStream);
            ByteBuffer buffer = ByteBuffer.wrap(outputStream.getBuffer());
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putShort((short) outputStream.getBuffer().length);
            byte[] bytes = buffer.array();
            return bytes;
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    public byte[] encodeLpp(LPP_Message message) {
        logger.info("Encoding LPP Message \n" + message);
        Asn1PerEncodeBuffer outputStream = new Asn1PerEncodeBuffer(false);
        try {
            message.encode(outputStream);
            return outputStream.getMsgCopy();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    private byte[] readBuffer() {
        InputStream input = null;
        try {
            input = socket.getInputStream();
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        while (input != null) {
            try {
                byte[] receive = getBytes(input);
                if (receive == null) {
                    socket.close();
                    networkInitiatedSuplLocation.removeConnection(this);
                    break;
                }
                if (receive.length == 0) continue;
                return receive;
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
        return null;
    }

    private class SuplMessageParams {
        Asn1Integer mcc, mnc, lac, ci, uci, ta = null, latitude, longitude, confidence, uncertaintySemiMajor, uncertaintySemiMinor,
                orientationMajorAxis, altitude, uncertaintyAltitude;
        Integer mCC = null, mNC = null, lAC = null, cid = null, tac = null, conf = null;
        Long uCi = null, eci, nci, timingAdvance = null;
        Integer lat = null, lng = null, uSemiMajor = null, uSemiMinor = null, oMajorAxis = null, alt = null, uAlt = null;
        TypeOfShape typeOfShape = null;

        SuplResponseHelperForMLP setCellInfo(CellInfo cellInfo, SuplResponseHelperForMLP suplResponseHelperForMLP) throws MAPException {
            if (cellInfo.isGsmCell()) {
                GsmCellInformation gsmCellInfo = cellInfo.getGsmCell();
                mcc = gsmCellInfo.getRefMCC();
                mCC = mcc.getIntValue();
                mnc = gsmCellInfo.getRefMNC();
                mNC = mnc.getIntValue();
                lac = gsmCellInfo.getRefLAC();
                lAC = lac.getIntValue();
                ci = gsmCellInfo.getRefCI();
                cid = ci.getIntValue();
                ta = gsmCellInfo.getTa();
                timingAdvance = ta.value;
                logger.warning("GSM Cell Info: mcc=" + mcc + ", mnc=" + mnc + ", lac=" + lac + ", ci=" + ci + ", ta=" + ta);
            } else if (cellInfo.isWcdmaCell()) {
                WcdmaCellInformation wcdmaCellInfo = cellInfo.getWcdmaCell();
                mcc = wcdmaCellInfo.getRefMCC();
                mCC = mcc.getIntValue();
                mnc = wcdmaCellInfo.getRefMNC();
                mNC = mnc.getIntValue();
                uci = wcdmaCellInfo.getRefUC();
                uCi = uci.value;
                if (wcdmaCellInfo.getTimingAdvance() != null)
                    ta = wcdmaCellInfo.getTimingAdvance().getTa();
                timingAdvance = (long) ta.getIntValue();
                logger.warning("WCDMA Cell Info: mcc=" + mcc + ", mnc=" + mnc + ", uci=" + uci + ", ta=" + ta);
            } else if (cellInfo.isVer2_CellInfo_extension()) {
                if (cellInfo.getVer2_CellInfo_extension().isLteCell()) {
                    LteCellInformation lteCellInfo = cellInfo.getVer2_CellInfo_extension().getLteCell();
                    TrackingAreaCode trackingAreaCode = lteCellInfo.getTrackingAreaCode();
                    tac = Integer.parseInt(trackingAreaCode.toHexString(), 16);
                    suplResponseHelperForMLP.setTac(tac);
                    logger.warning(String.format("TAC: %s", tac));
                    CellGlobalIdEUTRA ecgi = lteCellInfo.getCellGlobalIdEUTRA();
                    PLMN_Identity plmnId = ecgi.getPlmn_Identity();
                    MCC ecgiMcc = plmnId.getMcc();
                    MNC ecgiMnc = plmnId.getMnc();
                    CellIdentity ecgiCellId = ecgi.getCellIdentity();
                    logger.warning(String.format("ecgiCellId Info: %s", ecgiCellId.toHexString()));
                    String decodeMcc = Arrays.stream(ecgiMcc.getElements()).map(String::valueOf).collect(Collectors.joining());
                    String decodeMnc = Arrays.stream(ecgiMnc.getElements()).map(String::valueOf).collect(Collectors.joining());
                    EUTRANCGIImpl eUtranCgi = new EUTRANCGIImpl();
                    mCC = Integer.valueOf(decodeMcc);
                    mNC = Integer.valueOf(decodeMnc);
                    eci = Long.parseLong(ecgiCellId.toHexString().replaceFirst("0$", ""), 16);
                    eUtranCgi.setData(mCC, mNC, eci);
                    logger.warning(String.format("EUtranCgi Info: %s", eUtranCgi));
                    suplResponseHelperForMLP.setLteCGI(eUtranCgi);
                }
                if (cellInfo.getVer2_CellInfo_extension().isNrCell()) {
                    NRCellInformation nrCellInfo = cellInfo.getVer2_CellInfo_extension().getNrCell();
                    ServingCellInformationNR servingCellInformationNR = nrCellInfo.getServingCellInformation();
                    ServCellNR[] servCellNR = servingCellInformationNR.getElements();
                    for (ServCellNR cellNR : servCellNR) {
                        MCC plmnMcc = cellNR.getCellGlobalId().getPlmn_Identity().getMcc();
                        MNC plmnMnc = cellNR.getCellGlobalId().getPlmn_Identity().getMnc();
                        CellIdentityNR cellIdentityNR = cellNR.getCellGlobalId().getCellIdentityNR();

                        String decodeMcc = Arrays.stream(plmnMcc.getElements()).map(String::valueOf).collect(Collectors.joining());
                        String decodeMnc = Arrays.stream(plmnMnc.getElements()).map(String::valueOf).collect(Collectors.joining());
                        mCC = Integer.valueOf(decodeMcc);
                        mNC = Integer.valueOf(decodeMnc);
                        nci = Long.parseLong(cellIdentityNR.toHexString().replaceFirst("0$", ""), 16);
                        logger.warning(String.format("NRCellGlobalId Info: mcc=%s, mnc=%s, nci=%s", mCC, mNC, nci));
                        NRCellGlobalIdImpl nrCellGlobalId = new NRCellGlobalIdImpl();
                        nrCellGlobalId.setData(mCC, mNC, nci);
                        suplResponseHelperForMLP.setNrCellGlobalId(nrCellGlobalId);
                    }
                }
            }
            suplResponseHelperForMLP.setMcc(mCC);
            suplResponseHelperForMLP.setMnc(mNC);
            suplResponseHelperForMLP.setLac(lAC);
            suplResponseHelperForMLP.setCi(cid);
            if (uCi != null)
                suplResponseHelperForMLP.setUci(uCi);
            logger.warning(String.format("Cell Info: mcc=%s, mnc=%s, lac=%s, ci=%s, uci=%s, eci=%s, nci=%s", mCC, mNC, lAC, cid, uCi, eci, nci));

            return suplResponseHelperForMLP;
        }

        SuplResponseHelperForMLP setPositionEstimate(SuplResponseHelperForMLP suplResponseHelperForMLP, PositionEstimate positionEstimate) {
            latitude = positionEstimate.getLatitude();
            lat = latitude.getIntValue();
            longitude = positionEstimate.getLongitude();
            lng = longitude.getIntValue();
            typeOfShape = TypeOfShape.EllipsoidPoint;
            PositionEstimate_uncertainty uncertainty = positionEstimate.getUncertainty();
            if (uncertainty != null) {
                uncertaintySemiMajor = uncertainty.getUncertaintySemiMajor();
                uSemiMajor = uncertaintySemiMajor.getIntValue();
                uncertaintySemiMinor = uncertainty.getUncertaintySemiMinor();
                uSemiMinor = uncertaintySemiMinor.getIntValue();
                orientationMajorAxis = uncertainty.getOrientationMajorAxis();
                oMajorAxis = orientationMajorAxis.getIntValue();
                typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyEllipse;
            }
            if (positionEstimate.hasAltitudeInfo()) {
                altitude = positionEstimate.getAltitudeInfo().getAltitude();
                alt = altitude.getIntValue();
                uncertaintyAltitude = positionEstimate.getAltitudeInfo().getAltUncertainty();
                uAlt = uncertaintyAltitude.getIntValue();
                typeOfShape = TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid;
            }
            if (positionEstimate.hasConfidence()) {
                confidence = positionEstimate.getConfidence();
                conf = confidence.getIntValue();
            }
            logger.warning("Position Estimate: latitude=" + lat + ", longitude=" + lng +
                    ", uncertaintySemiMajor=" + uSemiMajor + ", uncertaintySemiMinor=" + uSemiMinor +
                    ", confidence = " + conf);
            suplResponseHelperForMLP = updateSuplPositionParams(suplResponseHelperForMLP, typeOfShape);

            return suplResponseHelperForMLP;
        }

        SuplResponseHelperForMLP setEllipsoidParams(SuplResponseHelperForMLP suplResponseHelperForMLP, EllipsoidPointWithAltitudeAndUncertaintyEllipsoid ellipsoid) {
            altitude = ellipsoid.getAltitude();
            alt = altitude.getIntValue();
            latitude = ellipsoid.getDegreesLatitude();
            lat = latitude.getIntValue();
            longitude = ellipsoid.getDegreesLongitude();
            lng = longitude.getIntValue();
            orientationMajorAxis = ellipsoid.getOrientationMajorAxis();
            oMajorAxis = orientationMajorAxis.getIntValue();
            uncertaintySemiMajor = ellipsoid.getUncertaintySemiMajor();
            uSemiMajor = uncertaintySemiMajor.getIntValue();
            uncertaintySemiMinor = ellipsoid.getUncertaintySemiMinor();
            uSemiMinor = uncertaintySemiMinor.getIntValue();
            confidence = ellipsoid.getConfidence();
            conf = confidence.getIntValue();
            uncertaintyAltitude = ellipsoid.getUncertaintyAltitude();
            uAlt = uncertaintyAltitude.getIntValue();
            typeOfShape = TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid;
            logger.warning("LPP_MessageBody AGNSS reference location, shape: " + typeOfShape);
            logger.warning("LPP_MessageBody AGNSS reference location: Latitude = " + lat);
            logger.warning("LPP_MessageBody AGNSS reference location: Longitude = " + lng);
            logger.warning("LPP_MessageBody AGNSS reference location: orientationMajorAxis = " + oMajorAxis);
            logger.warning("LPP_MessageBody AGNSS reference location: uncertaintySemiMajor = " + uSemiMajor);
            logger.warning("LPP_MessageBody AGNSS reference location: uncertaintySemiMinor = " + uSemiMinor);
            logger.warning("LPP_MessageBody AGNSS reference location: confidence = " + conf);
            logger.warning("LPP_MessageBody AGNSS reference location: altitude = " + alt);
            logger.warning("LPP_MessageBody AGNSS reference location: uncertaintyAltitude = " + uAlt);
            suplResponseHelperForMLP = updateSuplPositionParams(suplResponseHelperForMLP, typeOfShape);

            return suplResponseHelperForMLP;
        }

        SuplResponseHelperForMLP updateSuplPositionParams(SuplResponseHelperForMLP suplResponseHelperForMLP, TypeOfShape typeOfShape) {
            this.typeOfShape = typeOfShape;
            suplResponseHelperForMLP.setTypeOfShape(typeOfShape);
            if (lat != null & lng != null) {
                suplResponseHelperForMLP.setLatitude(GADShapesUtils.decodeLatitude(lat));
                suplResponseHelperForMLP.setLongitude(GADShapesUtils.decodeLongitude(lng));
            }
            if (uSemiMajor != null && uSemiMinor != null && oMajorAxis != null) {
                suplResponseHelperForMLP.setUncertaintySemiMajorAxis(GADShapesUtils.decodeUncertaintySemiMajor(uSemiMajor));
                suplResponseHelperForMLP.setUncertaintySemiMinorAxis(GADShapesUtils.decodeUncertaintySemiMinor(uSemiMinor));
                suplResponseHelperForMLP.setOrientationMajorAxis(oMajorAxis.doubleValue()); //TODO This value remains to be decoded
            }
            suplResponseHelperForMLP.setConfidence(conf);
            suplResponseHelperForMLP.setAltitude(alt);
            if (uAlt != null)
                suplResponseHelperForMLP.setUncertaintyAltitude(GADShapesUtils.decodeUncertaintyAltitude(uAlt));
            if (timingAdvance != null)
                suplResponseHelperForMLP.setTimingAdvance(timingAdvance);

            return suplResponseHelperForMLP;
        }
    }
}
