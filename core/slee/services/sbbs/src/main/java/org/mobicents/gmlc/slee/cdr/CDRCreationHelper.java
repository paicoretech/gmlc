package org.mobicents.gmlc.slee.cdr;

import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.events.UserDataAnswer;
import net.java.slee.resource.diameter.slg.events.LocationReportRequest;
import net.java.slee.resource.diameter.slg.events.ProvideLocationAnswer;
import net.java.slee.resource.diameter.slh.events.LCSRoutingInfoAnswer;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.mobicents.gmlc.slee.LocationRequestParams;
import org.mobicents.gmlc.slee.supl.NetworkInitiatedSuplLocation;
import org.mobicents.gmlc.slee.supl.SuplResponseHelperForMLP;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPDialogLsm;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;

import javax.slee.ActivityContextInterface;
import javax.slee.SbbLocalObject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class CDRCreationHelper {

    private static final Logger logger = Logger.getLogger(CDRCreationHelper.class.getName());

    public CDRCreationHelper() {
    }

    public static GmlcCdrStateString mapAtiCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface,
                                                          AnyTimeInterrogationResponse atiEvent, String nnn) {
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        MAPDialogMobility mapDialogMobility = atiEvent.getMAPDialog();
        SubscriberInfo subscriberInfo = atiEvent.getSubscriberInfo();
        GmlcCdrStateString gmlcCdrStateString = new GmlcCdrStateString(gmlcCdrState, nnn);

        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonAnyTimeInterrogationResponse: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");

            if (subscriberInfo.getLocationInformation() != null) {
                if (subscriberInfo.getLocationInformation().getVlrNumber() != null) {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        subscriberInfo.getLocationInformation().getVlrNumber(), mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    nnn = subscriberInfo.getLocationInformation().getVlrNumber().getAddress();
                } else if (subscriberInfo.getLocationInformation().getMscNumber() != null) {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        subscriberInfo.getLocationInformation().getMscNumber(), mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    nnn = subscriberInfo.getLocationInformation().getMscNumber().getAddress();
                } else {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        null, mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    if (mapDialogMobility.getRemoteAddress().getGlobalTitle() != null)
                        nnn = mapDialogMobility.getRemoteAddress().getGlobalTitle().getDigits();
                }
            } else if (subscriberInfo.getLocationInformationGPRS() != null) {
                if (subscriberInfo.getLocationInformationGPRS().getSGSNNumber() != null) {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        subscriberInfo.getLocationInformationGPRS().getSGSNNumber(), mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    nnn = subscriberInfo.getLocationInformationGPRS().getSGSNNumber().getAddress();
                } else {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        null, mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    if (mapDialogMobility.getRemoteAddress().getGlobalTitle() != null)
                        nnn = mapDialogMobility.getRemoteAddress().getGlobalTitle().getDigits();
                }
            } else {
                gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                    null, mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                if (mapDialogMobility.getRemoteAddress().getGlobalTitle() != null)
                    nnn = mapDialogMobility.getRemoteAddress().getGlobalTitle().getDigits();
            }
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogMobility.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        gmlcCdrStateString.setGmlcCdrState(gmlcCdrState);
        gmlcCdrStateString.setNnn(nnn);
        return gmlcCdrStateString;
    }

    public static GMLCCDRState mapSriSmCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, SendRoutingInfoForSMResponse sriSmEvent) {
        MAPDialog mapDialogSriForSM = sriSmEvent.getMAPDialog();
        LocationInfoWithLMSI locationInfoWithLMSI = sriSmEvent.getLocationInfoWithLMSI();
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonSendRoutingInfoForSmResponse: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(mapDialogSriForSM.getLocalDialogId(), mapDialogSriForSM.getReceivedDestReference(), mapDialogSriForSM.getReceivedOrigReference(),
                locationInfoWithLMSI.getNetworkNodeNumber(), mapDialogSriForSM.getLocalAddress(), mapDialogSriForSM.getRemoteAddress());
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogSriForSM.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static GMLCCDRState mapSriCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, SendRoutingInformationResponse sriEvent) {
        MAPDialogCallHandling mapDialogSri = sriEvent.getMAPDialog();
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonSendRoutingInformationResponse: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(mapDialogSri.getLocalDialogId(), mapDialogSri.getReceivedDestReference(), mapDialogSri.getReceivedOrigReference(),
                sriEvent.getVmscAddress(), mapDialogSri.getLocalAddress(), mapDialogSri.getRemoteAddress());
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogSri.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static GmlcCdrStateString mapPsiCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface,
                                                          ProvideSubscriberInfoResponse psiEvent, String nnn) {
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        GmlcCdrStateString gmlcCdrStateString = new GmlcCdrStateString(gmlcCdrState, nnn);
        MAPDialogMobility mapDialogMobility = psiEvent.getMAPDialog();
        SubscriberInfo subscriberInfo = psiEvent.getSubscriberInfo();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonProvideSubscriberInformationResponse: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            if (subscriberInfo.getLocationInformation() != null) {
                if (subscriberInfo.getLocationInformation().getVlrNumber() != null) {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        subscriberInfo.getLocationInformation().getVlrNumber(), mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    nnn = subscriberInfo.getLocationInformation().getVlrNumber().getAddress();
                } else if (subscriberInfo.getLocationInformation().getMscNumber() != null) {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        subscriberInfo.getLocationInformation().getMscNumber(), mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    nnn = subscriberInfo.getLocationInformation().getMscNumber().getAddress();
                } else {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        null, mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    if (mapDialogMobility.getRemoteAddress().getGlobalTitle() != null)
                        nnn = mapDialogMobility.getRemoteAddress().getGlobalTitle().getDigits();
                }
            } else if (subscriberInfo.getLocationInformationGPRS() != null) {
                if (subscriberInfo.getLocationInformationGPRS().getSGSNNumber() != null) {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        subscriberInfo.getLocationInformationGPRS().getSGSNNumber(), mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    nnn = subscriberInfo.getLocationInformationGPRS().getSGSNNumber().getAddress();
                } else {
                    gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                        null, mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
                    if (mapDialogMobility.getRemoteAddress().getGlobalTitle() != null)
                        nnn = mapDialogMobility.getRemoteAddress().getGlobalTitle().getDigits();
                }
            } else {
                gmlcCdrState.init(mapDialogMobility.getLocalDialogId(), mapDialogMobility.getReceivedDestReference(), mapDialogMobility.getReceivedOrigReference(),
                    null, mapDialogMobility.getLocalAddress(), mapDialogMobility.getRemoteAddress());
            }
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogMobility.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);

            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        } else {
            gmlcCdrState.setLocalDialogId(mapDialogMobility.getLocalDialogId());
            gmlcCdrState.setRemoteDialogId(mapDialogMobility.getRemoteDialogId());
            gmlcCdrState.setLocalAddress(mapDialogMobility.getLocalAddress());
            gmlcCdrState.setRemoteAddress(mapDialogMobility.getRemoteAddress());
        }
        gmlcCdrStateString.setGmlcCdrState(gmlcCdrState);
        gmlcCdrStateString.setNnn(nnn);
        return gmlcCdrStateString;
    }

    public static GMLCCDRState mapSriLcsCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, SendRoutingInfoForLCSResponse sriLcsEvent) {
        MAPDialogLsm mapDialogLsmSriLcs = sriLcsEvent.getMAPDialog();
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonSendRoutingInfoForLCSResponse: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(mapDialogLsmSriLcs.getLocalDialogId(), mapDialogLsmSriLcs.getReceivedDestReference(), mapDialogLsmSriLcs.getReceivedOrigReference(),
                null, mapDialogLsmSriLcs.getLocalAddress(), mapDialogLsmSriLcs.getRemoteAddress());
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogLsmSriLcs.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static GMLCCDRState mapPSLCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, ProvideSubscriberLocationResponse pslEvent) {
        MAPDialogLsm mapDialogLsmPsl = pslEvent.getMAPDialog();
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonProvideSubscriberLocationResponse: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(mapDialogLsmPsl.getLocalDialogId(), mapDialogLsmPsl.getReceivedDestReference(), mapDialogLsmPsl.getReceivedOrigReference(),
                null, mapDialogLsmPsl.getLocalAddress(), mapDialogLsmPsl.getRemoteAddress());
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogLsmPsl.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        } else {
            gmlcCdrState.setLocalDialogId(mapDialogLsmPsl.getLocalDialogId());
            gmlcCdrState.setRemoteDialogId(mapDialogLsmPsl.getRemoteDialogId());
            gmlcCdrState.setLocalAddress(mapDialogLsmPsl.getLocalAddress());
            gmlcCdrState.setRemoteAddress(mapDialogLsmPsl.getRemoteAddress());
        }
        return gmlcCdrState;
    }

    public static GMLCCDRState mapSLRCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, SubscriberLocationReportRequest slrEvent,
                                             ISDNAddressString msisdn) {
        MAPDialogLsm mapDialogLsmSlr = slrEvent.getMAPDialog();
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
                logger.debug("\nonSubscriberLocationReportRequest: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(mapDialogLsmSlr.getLocalDialogId(), mapDialogLsmSlr.getReceivedDestReference(), mapDialogLsmSlr.getReceivedOrigReference(),
                msisdn, mapDialogLsmSlr.getLocalAddress(), mapDialogLsmSlr.getRemoteAddress());
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setRemoteDialogId(mapDialogLsmSlr.getRemoteDialogId());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }


    public static GMLCCDRState onMapDialogEventCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, MAPDialog mapDialog) {

        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        gmlcCdrState.init(mapDialog.getLocalDialogId(), mapDialog.getReceivedDestReference(), mapDialog.getReceivedOrigReference(),
            null, mapDialog.getLocalAddress(), mapDialog.getRemoteAddress());
        gmlcCdrState.setDialogEndTime(DateTime.now());
        gmlcCdrState.setRemoteDialogId(mapDialog.getRemoteDialogId());
        cdrInterface.setState(gmlcCdrState);
        SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
        aci.attach(sbbLO);

        return gmlcCdrState;
    }

    public static GMLCCDRState slhSlgCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface,
                                                 LCSRoutingInfoAnswer riaEvent, ProvideLocationAnswer plaEvent, LocationReportRequest lrrEvent,
                                                 DiameterIdentity originHost, DiameterIdentity originRealm,
                                                 DiameterIdentity gmlcHost, DiameterIdentity gmlcRealm) {
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            if (riaEvent != null) {
                gmlcCdrState.init(riaEvent.getSessionId(), originHost, originRealm, gmlcHost, gmlcRealm);
            }
            else if (plaEvent != null) {
                logger.debug("\nonProvideLocationAnswer: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
                gmlcCdrState.init(plaEvent.getSessionId(), originHost, originRealm, gmlcHost, gmlcRealm);
            }
            else if (lrrEvent != null) {
                logger.debug("\nonLocationReportRequest: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
                gmlcCdrState.init(lrrEvent.getSessionId(), originHost, originRealm, gmlcHost, gmlcRealm);
            }
            gmlcCdrState.setDialogEndTime(DateTime.now());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static GMLCCDRState shUdaCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, UserDataAnswer udaEvent,
                                                   DiameterIdentity gmlcHost, DiameterIdentity gmlcRealm) {
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonUserDataAnswer: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(udaEvent != null ? udaEvent.getSessionId() : null,
                udaEvent != null ? udaEvent.getOriginHost() : null, udaEvent != null ? udaEvent.getOriginRealm() : null, gmlcHost, gmlcRealm);
            gmlcCdrState.setDialogEndTime(DateTime.now());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static GMLCCDRState diameterErrorCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, ErrorAnswer diameterErrorAnswerEvent,
                                                           DiameterIdentity gmlcHost, DiameterIdentity gmlcRealm) {
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\nonErrorAnswer (Diameter): CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(diameterErrorAnswerEvent != null ? diameterErrorAnswerEvent.getSessionId() : null,
                diameterErrorAnswerEvent != null ? diameterErrorAnswerEvent.getOriginHost() : null,
                diameterErrorAnswerEvent != null ? diameterErrorAnswerEvent.getOriginRealm() : null, gmlcHost, gmlcRealm);
            gmlcCdrState.setDialogEndTime(DateTime.now());
            cdrInterface.setState(gmlcCdrState);
            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static GMLCCDRState suplCdrInitializer(ActivityContextInterface aci, CDRInterface cdrInterface, LocationRequestParams locationRequestParams,
                                                  InetAddress localSocketAddress, int localSocketPort, InetAddress remoteSocketAddress,
                                                  int remoteSocketPort) {
        GMLCCDRState gmlcCdrState = cdrInterface.getState();
        if (!gmlcCdrState.isInitialized()) {
            logger.debug("\ngetLocationViaSUPL: CDR state is NOT initialized: " + gmlcCdrState + ", initiating\n");
            gmlcCdrState.init(localSocketAddress, localSocketPort, remoteSocketAddress, remoteSocketPort);
            DateTime dialogStart = DateTime.now();
            gmlcCdrState.setDialogStartTime(dialogStart);
            gmlcCdrState.setCurlUser(locationRequestParams.getCurlUser());
            ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, locationRequestParams.getTargetingMSISDN());
            gmlcCdrState.setMsisdn(msisdn);
            IMSI imsi = new IMSIImpl(locationRequestParams.getTargetingIMSI());
            gmlcCdrState.setImsi(imsi);
            cdrInterface.setState(gmlcCdrState);

            // attach, in case impl wants to use more of dialog.
            SbbLocalObject sbbLO = (SbbLocalObject) cdrInterface;
            aci.attach(sbbLO);
        }
        return gmlcCdrState;
    }

    public static void updateSuplCdrState(GMLCCDRState gmlcCdrState, SuplResponseHelperForMLP suplResponseHelperForMLP,
                                          NetworkInitiatedSuplLocation networkInitiatedSuplLocation, Integer transactionId) {
        try {
            if (networkInitiatedSuplLocation != null) {
                if (networkInitiatedSuplLocation.getServerSocket() != null) {
                    gmlcCdrState.setSlpSocketAddress(networkInitiatedSuplLocation.getServerSocket().getInetAddress());
                    gmlcCdrState.setSlpSocketPort(networkInitiatedSuplLocation.getServerSocket().getLocalPort());
                    SocketAddress socketAddress = networkInitiatedSuplLocation.getServerSocket().getLocalSocketAddress();
                    InetSocketAddress remoteInetSocketAddress = (InetSocketAddress) socketAddress;
                    gmlcCdrState.setSetSocketAddress(remoteInetSocketAddress.getAddress());
                    gmlcCdrState.setSetSocketPort(remoteInetSocketAddress.getPort());
                }
            }
            DateTime dialogStart = gmlcCdrState.getDialogStartTime();
            DateTime dialogEnd = DateTime.now();
            gmlcCdrState.setDialogEndTime(DateTime.now());
            gmlcCdrState.setDialogDuration(dialogEnd.getMillis() - dialogStart.getMillis());
            gmlcCdrState.setClientReferenceNumber(transactionId);
            if (suplResponseHelperForMLP != null) {
                if (suplResponseHelperForMLP.getCellGlobalIdOrServiceAreaIdOrLAI() != null)
                    gmlcCdrState.setCellGlobalIdOrServiceAreaIdOrLAI(suplResponseHelperForMLP.getCellGlobalIdOrServiceAreaIdOrLAI());
                if (suplResponseHelperForMLP.getLteCGI() != null)
                    gmlcCdrState.setEUtranCgi(suplResponseHelperForMLP.getLteCGI());
                if (suplResponseHelperForMLP.getNrCellGlobalId() != null)
                    gmlcCdrState.setNrCellGlobalId(suplResponseHelperForMLP.getNrCellGlobalId());
                if (suplResponseHelperForMLP.getTypeOfShape() != null) {
                    ExtGeographicalInformation extGeographicalInformation = new ExtGeographicalInformation() {
                        @Override
                        public byte[] getData() {
                            return new byte[0];
                        }

                        @Override
                        public TypeOfShape getTypeOfShape() {
                            return suplResponseHelperForMLP.getTypeOfShape();
                        }

                        @Override
                        public double getLatitude() {
                            return suplResponseHelperForMLP.getLatitude();
                        }

                        @Override
                        public double getLongitude() {
                            return suplResponseHelperForMLP.getLongitude();
                        }

                        @Override
                        public double getUncertainty() {
                            return suplResponseHelperForMLP.getUncertainty();
                        }

                        @Override
                        public double getUncertaintySemiMajorAxis() {
                            return suplResponseHelperForMLP.getUncertaintySemiMajorAxis();
                        }

                        @Override
                        public double getUncertaintySemiMinorAxis() {
                            return suplResponseHelperForMLP.getUncertaintySemiMinorAxis();
                        }

                        @Override
                        public double getAngleOfMajorAxis() {
                            return suplResponseHelperForMLP.getAngleOfMajorAxis();
                        }

                        @Override
                        public int getConfidence() {
                            return suplResponseHelperForMLP.getConfidence();
                        }

                        @Override
                        public int getAltitude() {
                            return suplResponseHelperForMLP.getAltitude();
                        }

                        @Override
                        public double getUncertaintyAltitude() {
                            return suplResponseHelperForMLP.getUncertaintyAltitude();
                        }

                        @Override
                        public int getInnerRadius() {
                            return suplResponseHelperForMLP.getInnerRadius();
                        }

                        @Override
                        public double getUncertaintyRadius() {
                            return suplResponseHelperForMLP.getUncertaintyInnerRadius();
                        }

                        @Override
                        public double getOffsetAngle() {
                            return suplResponseHelperForMLP.getOffsetAngle();
                        }

                        @Override
                        public double getIncludedAngle() {
                            return suplResponseHelperForMLP.getIncludedAngle();
                        }

                        @Override
                        public double getHiAccLatitude() {
                            return 0;
                        }

                        @Override
                        public double getHiAccLongitude() {
                            return 0;
                        }

                        @Override
                        public int getHorizontalConfidence() {
                            return 0;
                        }

                        @Override
                        public int getVerticalConfidence() {
                            return 0;
                        }

                        @Override
                        public int getHiAccAltitude() {
                            return 0;
                        }

                        @Override
                        public double getHiAccUncertaintyAltitude() {
                            return 0;
                        }
                    };
                    gmlcCdrState.setLocationEstimate(extGeographicalInformation);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in updateCdrState class: " + e.getMessage());
        }
    }

    public static class GmlcCdrStateString {
        GMLCCDRState gmlcCdrState;
        String nnn;

        public GmlcCdrStateString(GMLCCDRState gmlcCdrState, String nnn) {
            this.gmlcCdrState = gmlcCdrState;
            this.nnn = nnn;
        }

        public GMLCCDRState getGmlcCdrState() {
            return gmlcCdrState;
        }

        public void setGmlcCdrState(GMLCCDRState gmlcCdrState) {
            this.gmlcCdrState = gmlcCdrState;
        }

        public String getNnn() {
            return nnn;
        }

        public void setNnn(String nnn) {
            this.nnn = nnn;
        }
    }
}
