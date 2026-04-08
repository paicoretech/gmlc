package org.mobicents.gmlc.slee.map;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DaylightSavingTime;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.IMSVoiceOverPsSessionsIndication;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TimeZone;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class PsiResponseParams {

    private final Logger logger = LoggerFactory.getLogger(PsiResponseParams.class.getName());

    /**
     * ProvideSubscriberInfoArg ::= SEQUENCE {
     * 	imsi                [0] IMSI,
     * 	lmsi                [1] LMSI  OPTIONAL,
     * 	requestedInfo       [2] RequestedInfo,
     * 	extensionContainer  [3] ExtensionContainer  OPTIONAL,
     * 	...,
     * 	callPriority        [4] EMLPP-Priority  OPTIONAL
     *        }

     *  ProvideSubscriberInfoRes ::= SEQUENCE {
     * 	subscriberInfo      SubscriberInfo,
     * 	extensionContainer  ExtensionContainer  OPTIONAL,
     * 	...}

     *    RequestedInfo ::= SEQUENCE {
     *     locationInformation    [0] NULL                OPTIONAL,
     *     subscriberState        [1] NULL                OPTIONAL,
     *     extensionContainer     [2] ExtensionContainer  OPTIONAL,
     *     ...,
     *     currentLocation        [3] NULL                OPTIONAL,
     *     requestedDomain        [4] DomainType          OPTIONAL,
     *     imei                   [6] NULL                OPTIONAL,
     *     ms-classmark           [5] NULL                OPTIONAL,
     *     mnpRequestedInfo       [7] NULL                OPTIONAL,
     *     locationInformationEPS-Supported [11] NULL     OPTIONAL,
     *     t-adsData              [8] NULL                OPTIONAL,
     *     requestedNodes         [9] RequestedNodes      OPTIONAL,
     *     servingNodeIndication [10] NULL                OPTIONAL,
     *     localTimeZoneRequest  [12] NULL                OPTIONAL
     *    }
     *    -- currentLocation and locationInformationEPS-Supported shall be absent if
     *    -- locationInformation is absent
     *    -- t-adsData shall be absent in messages sent to the VLR
     *    -- requestedNodes shall be absent if requestedDomain is "cs-Domain"
     *    -- servingNodeIndication shall be absent if locationInformation is absent;
     *    -- servingNodeIndication shall be absent if current location is present;
     *    -- servingNodeIndication indicates by its presence that only the serving node's
     *    -- address (MME-Name or SGSN-Number or VLR-Number) is requested.

     *    SubscriberInfo,
     * 	extensionContainer	ExtensionContainer	OPTIONAL,
     * 	...}

     *   SubscriberInfo ::= SEQUENCE {
     *     locationInformation                 [0] LocationInformation         OPTIONAL,
     *     subscriberState                     [1] SubscriberState             OPTIONAL,
     *     extensionContainer                  [2] ExtensionContainer          OPTIONAL,
     *     ... ,
     *     locationInformationGPRS             [3] LocationInformationGPRS     OPTIONAL,
     *     ps-SubscriberState                  [4] PS-SubscriberState          OPTIONAL,
     *     imei                                [5] IMEI                        OPTIONAL,
     *     ms-Classmark2                       [6] MS-Classmark2               OPTIONAL,
     *     gprs-MS-Class                       [7] GPRSMSClass                 OPTIONAL,
     *     mnpInfoRes                          [8] MNPInfoRes                  OPTIONAL,
     *     imsVoiceOverPS-SessionsIndication   [9] IMS-VoiceOverPS-SessionsInd	OPTIONAL,
     *     lastUE-ActivityTime                [10] Time                        OPTIONAL,
     *     lastRAT-Type                       [11] Used-RAT-Type               OPTIONAL,
     *     eps-SubscriberState                [12] PS-SubscriberState          OPTIONAL,
     *     locationInformationEPS             [13] LocationInformationEPS      OPTIONAL,
     *     timeZone                           [14] TimeZone                    OPTIONAL,
     *     daylightSavingTime                 [15] DaylightSavingTime          OPTIONAL,
     *     locationInformation5GS             [16] LocationInformation5GS      OPTIONAL
     *   }
     *   --  If the HLR receives locationInformation, subscriberState or ms-Classmark2 from an SGSN or
     *   --  MME (via an IWF), it shall discard them.
     *   --  If the HLR receives locationInformationGPRS, ps-SubscriberState, gprs-MS-Class or
     *   --  locationInformationEPS (outside the locationInformation IE) from a VLR, it shall discard them.
     *   --  If the HLR receives parameters which it has not requested, it shall discard them.
     *   --  The locationInformation5GS IE should be absent if UE did not access via 5GS and IM-SSF.
     */

    private LocationInformation locationInformation;
    private SubscriberState subscriberState;
    private MAPExtensionContainer extensionContainer;
    private LocationInformationGPRS locationInformationGPRS;
    private PSSubscriberState psSubscriberState;
    private IMEI imei;
    private MSClassmark2 msClassmark2;
    private GPRSMSClass gprsMSClass;
    private MNPInfoRes mnpInfoRes;
    private IMSVoiceOverPsSessionsIndication imsVoiceOverPsSessionsIndication;
    private Time lastUEActivityTime;
    private UsedRATType lastRATType;
    private PSSubscriberState epsSubscriberState;
    private LocationInformationEPS locationInformationEPS;
    private TimeZone timeZone;
    private DaylightSavingTime daylightSavingTime;
    private LocationInformation5GS locationInformation5GS;

    private int mcc, mnc, lac, sac, rac, tac, nrTac, ci;
    private long enbId, eci, nci;
    private double latitude, longitude, uncertainty;
    private TypeOfShape typeOfShape; // must always be EllipsoidPointWithUncertaintyCircle for PSI

    private IMSI imsi;
    private String sriForSMImsi;
    private SriSmResponseParams sriForSmResponse;
    private SriResponseValues sriResponse;
    String psiServiceType;
    String psiOnlyImsi;
    String psiOnlyNnn;

    public PsiResponseParams() {
    }

    public LocationInformation getLocationInformation() {
        return locationInformation;
    }

    public void setLocationInformation(LocationInformation locationInformation) {
        this.locationInformation = locationInformation;
        try {
            if (locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                if (locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                    setMcc(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
                    setMnc(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
                    setLac(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
                    if (locationInformation.getSaiPresent())
                        setSac(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
                    else
                        setCi(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
                } else {
                    setMcc(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
                    setMnc(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
                    setLac(locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
                }
            }
            if (locationInformation.getGeographicalInformation() != null) {
                setTypeOfShape(locationInformation.getGeographicalInformation().getTypeOfShape());
                setLatitude(locationInformation.getGeographicalInformation().getLatitude());
                setLongitude(locationInformation.getGeographicalInformation().getLongitude());
                setUncertainty(locationInformation.getGeographicalInformation().getUncertainty());
            } else if (locationInformation.getGeodeticInformation() != null) {
                setTypeOfShape(locationInformation.getGeodeticInformation().getTypeOfShape());
                setLatitude(locationInformation.getGeodeticInformation().getLatitude());
                setLongitude(locationInformation.getGeodeticInformation().getLongitude());
                setUncertainty(locationInformation.getGeodeticInformation().getUncertainty());
            }
            if (locationInformation.getLocationInformationEPS() != null) {
                if (locationInformation.getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                    setMcc(locationInformation.getLocationInformationEPS().getTrackingAreaIdentity().getMCC());
                    setMnc(locationInformation.getLocationInformationEPS().getTrackingAreaIdentity().getMNC());
                    setTac(locationInformation.getLocationInformationEPS().getTrackingAreaIdentity().getTAC());
                }
                if (locationInformation.getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                    setMcc(locationInformation.getLocationInformationEPS().getEUtranCellGlobalIdentity().getMCC());
                    setMnc(locationInformation.getLocationInformationEPS().getEUtranCellGlobalIdentity().getMNC());
                    setEci(locationInformation.getLocationInformationEPS().getEUtranCellGlobalIdentity().getEci());
                    setEnbId(locationInformation.getLocationInformationEPS().getEUtranCellGlobalIdentity().getENodeBId());
                    setCi(locationInformation.getLocationInformationEPS().getEUtranCellGlobalIdentity().getCi());
                }
                if (locationInformation.getLocationInformationEPS().getGeographicalInformation() != null) {
                    setTypeOfShape(locationInformation.getLocationInformationEPS().getGeographicalInformation().getTypeOfShape());
                    setLatitude(locationInformation.getLocationInformationEPS().getGeographicalInformation().getLatitude());
                    setLongitude(locationInformation.getLocationInformationEPS().getGeographicalInformation().getLongitude());
                    setUncertainty(locationInformation.getLocationInformationEPS().getGeographicalInformation().getUncertainty());
                } else if (locationInformation.getLocationInformationEPS().getGeodeticInformation() != null) {
                    setTypeOfShape(locationInformation.getLocationInformationEPS().getGeodeticInformation().getTypeOfShape());
                    setLatitude(locationInformation.getLocationInformationEPS().getGeodeticInformation().getLatitude());
                    setLongitude(locationInformation.getLocationInformationEPS().getGeodeticInformation().getLongitude());
                    setUncertainty(locationInformation.getLocationInformationEPS().getGeodeticInformation().getUncertainty());
                }
            }
        } catch (MAPException e) {
            logger.error(e.getMessage());
        }
    }

    public SubscriberState getSubscriberState() {
        return subscriberState;
    }

    public void setSubscriberState(SubscriberState subscriberState) {
        this.subscriberState = subscriberState;
    }

    public MAPExtensionContainer getExtensionContainer() {
        return extensionContainer;
    }

    public void setExtensionContainer(MAPExtensionContainer extensionContainer) {
        this.extensionContainer = extensionContainer;
    }

    public LocationInformationGPRS getLocationInformationGPRS() {
        return locationInformationGPRS;
    }

    public void setLocationInformationGPRS(LocationInformationGPRS locationInformationGPRS) {
        this.locationInformationGPRS = locationInformationGPRS;
        try {
            if (locationInformationGPRS.getRouteingAreaIdentity() != null) {
                setMcc(locationInformationGPRS.getRouteingAreaIdentity().getMCC());
                setMnc(locationInformationGPRS.getRouteingAreaIdentity().getMNC());
                setRac(locationInformationGPRS.getRouteingAreaIdentity().getRAC());
            }
            if (locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                if (locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                    setMcc(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
                    setMnc(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
                    setLac(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
                    if (locationInformationGPRS.isSaiPresent())
                        setSac(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
                    else
                        setCi(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode());
                } else {
                    setMcc(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC());
                    setMnc(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC());
                    setLac(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac());
                }
            }
            if (locationInformationGPRS.getGeographicalInformation() != null) {
                setTypeOfShape(locationInformationGPRS.getGeographicalInformation().getTypeOfShape());
                setLatitude(locationInformationGPRS.getGeographicalInformation().getLatitude());
                setLongitude(locationInformationGPRS.getGeographicalInformation().getLongitude());
                setUncertainty(locationInformationGPRS.getGeographicalInformation().getUncertainty());
            } else if (locationInformationGPRS.getGeodeticInformation() != null) {
                setTypeOfShape(locationInformationGPRS.getGeodeticInformation().getTypeOfShape());
                setLatitude(locationInformationGPRS.getGeodeticInformation().getLatitude());
                setLongitude(locationInformationGPRS.getGeodeticInformation().getLongitude());
                setUncertainty(locationInformationGPRS.getGeodeticInformation().getUncertainty());
            }
        } catch (MAPException e) {
            logger.error(e.getMessage());
        }
    }

    public PSSubscriberState getPsSubscriberState() {
        return psSubscriberState;
    }

    public void setPsSubscriberState(PSSubscriberState psSubscriberState) {
        this.psSubscriberState = psSubscriberState;
    }

    public IMEI getImei() {
        return imei;
    }

    public void setImei(IMEI imei) {
        this.imei = imei;
    }

    public MSClassmark2 getMsClassmark2() {
        return msClassmark2;
    }

    public void setMsClassmark2(MSClassmark2 msClassmark2) {
        this.msClassmark2 = msClassmark2;
    }

    public GPRSMSClass getGprsMSClass() {
        return gprsMSClass;
    }

    public void setGprsMSClass(GPRSMSClass gprsMSClass) {
        this.gprsMSClass = gprsMSClass;
    }

    public MNPInfoRes getMnpInfoRes() {
        return mnpInfoRes;
    }

    public void setMnpInfoRes(MNPInfoRes mnpInfoRes) {
        this.mnpInfoRes = mnpInfoRes;
    }

    public IMSVoiceOverPsSessionsIndication getImsVoiceOverPsSessionsIndication() {
        return imsVoiceOverPsSessionsIndication;
    }

    public void setImsVoiceOverPsSessionsIndication(IMSVoiceOverPsSessionsIndication imsVoiceOverPsSessionsIndication) {
        this.imsVoiceOverPsSessionsIndication = imsVoiceOverPsSessionsIndication;
    }

    public Time getLastUEActivityTime() {
        return lastUEActivityTime;
    }

    public void setLastUEActivityTime(Time lastUEActivityTime) {
        this.lastUEActivityTime = lastUEActivityTime;
    }

    public UsedRATType getLastRATType() {
        return lastRATType;
    }

    public void setLastRATType(UsedRATType lastRATType) {
        this.lastRATType = lastRATType;
    }

    public PSSubscriberState getEpsSubscriberState() {
        return epsSubscriberState;
    }

    public void setEpsSubscriberState(PSSubscriberState epsSubscriberState) {
        this.epsSubscriberState = epsSubscriberState;
    }

    public LocationInformationEPS getLocationInformationEPS() {
        return locationInformationEPS;
    }

    public void setLocationInformationEPS(LocationInformationEPS locationInformationEPS) {
        this.locationInformationEPS = locationInformationEPS;
        try {
            if (locationInformationEPS.getTrackingAreaIdentity() != null) {
                setMcc(locationInformationEPS.getTrackingAreaIdentity().getMCC());
                setMnc(locationInformationEPS.getTrackingAreaIdentity().getMNC());
                setTac(locationInformationEPS.getTrackingAreaIdentity().getTAC());
            }
            if (locationInformationEPS.getEUtranCellGlobalIdentity() != null) {
                setMcc(locationInformationEPS.getEUtranCellGlobalIdentity().getMCC());
                setMnc(locationInformationEPS.getEUtranCellGlobalIdentity().getMNC());
                setEci(locationInformationEPS.getEUtranCellGlobalIdentity().getEci());
                setEnbId(locationInformationEPS.getEUtranCellGlobalIdentity().getENodeBId());
                setCi(locationInformationEPS.getEUtranCellGlobalIdentity().getCi());
            }
            if (locationInformationEPS.getGeographicalInformation() != null) {
                setTypeOfShape(locationInformationEPS.getGeographicalInformation().getTypeOfShape());
                setLatitude(locationInformationEPS.getGeographicalInformation().getLatitude());
                setLongitude(locationInformationEPS.getGeographicalInformation().getLongitude());
                setUncertainty(locationInformationEPS.getGeographicalInformation().getUncertainty());
            } else if (locationInformationEPS.getGeodeticInformation() != null) {
                setTypeOfShape(locationInformationEPS.getGeodeticInformation().getTypeOfShape());
                setLatitude(locationInformationEPS.getGeodeticInformation().getLatitude());
                setLongitude(locationInformationEPS.getGeodeticInformation().getLongitude());
                setUncertainty(locationInformationEPS.getGeodeticInformation().getUncertainty());
            }
        } catch (MAPException e) {
            logger.error(e.getMessage());
        }
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public DaylightSavingTime getDaylightSavingTime() {
        return daylightSavingTime;
    }

    public void setDaylightSavingTime(DaylightSavingTime daylightSavingTime) {
        this.daylightSavingTime = daylightSavingTime;
    }

    public LocationInformation5GS getLocationInformation5GS() {
        return locationInformation5GS;
    }

    public void setLocationInformation5GS(LocationInformation5GS locationInformation5GS) {
        this.locationInformation5GS = locationInformation5GS;
        try {
            if (locationInformation5GS.getTAId() != null) {
                setMcc(locationInformation5GS.getTAId().getMCC());
                setMnc(locationInformation5GS.getTAId().getMNC());
                setTac(locationInformation5GS.getTAId().getTAC());
            }
            if (locationInformation5GS.getEUtranCgi() != null) {
                setMcc(locationInformation5GS.getEUtranCgi().getMCC());
                setMnc(locationInformation5GS.getEUtranCgi().getMNC());
                setEci(locationInformation5GS.getEUtranCgi().getEci());
                setEnbId(locationInformation5GS.getEUtranCgi().getENodeBId());
                setCi(locationInformation5GS.getEUtranCgi().getCi());
            }
            if (locationInformation5GS.getNRTAId() != null) {
                setMcc(locationInformation5GS.getNRTAId().getMCC());
                setMnc(locationInformation5GS.getNRTAId().getMNC());
                setNrTac(locationInformation5GS.getNRTAId().getNrTAC());
            }
            if (locationInformation5GS.getNRCellGlobalId() != null) {
                setMcc(locationInformation5GS.getNRCellGlobalId().getMCC());
                setMnc(locationInformation5GS.getNRCellGlobalId().getMNC());
                setNci(locationInformation5GS.getNRCellGlobalId().getNCI());
            }
            if (locationInformation5GS.getGeographicalInformation() != null) {
                setTypeOfShape(locationInformation5GS.getGeographicalInformation().getTypeOfShape());
                setLatitude(locationInformation5GS.getGeographicalInformation().getLatitude());
                setLongitude(locationInformation5GS.getGeographicalInformation().getLongitude());
                setUncertainty(locationInformation5GS.getGeographicalInformation().getUncertainty());
            } else if (locationInformation5GS.getGeodeticInformation() != null) {
                setTypeOfShape(locationInformation5GS.getGeodeticInformation().getTypeOfShape());
                setLatitude(locationInformation5GS.getGeodeticInformation().getLatitude());
                setLongitude(locationInformation5GS.getGeodeticInformation().getLongitude());
                setUncertainty(locationInformation5GS.getGeodeticInformation().getUncertainty());
            }
        } catch (MAPException e) {
            logger.error(e.getMessage());
        }
    }

    public int getMcc() {
        return mcc;
    }

    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    public int getMnc() {
        return mnc;
    }

    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getSac() {
        return sac;
    }

    public void setSac(int sac) {
        this.sac = sac;
    }

    public int getRac() {
        return rac;
    }

    public void setRac(int rac) {
        this.rac = rac;
    }

    public int getTac() {
        return tac;
    }

    public void setTac(int tac) {
        this.tac = tac;
    }

    public int getNrTac() {
        return nrTac;
    }

    public void setNrTac(int nrTac) {
        this.nrTac = nrTac;
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public long getEnbId() {
        return enbId;
    }

    public void setEnbId(long enbId) {
        this.enbId = enbId;
    }

    public long getEci() {
        return eci;
    }

    public void setEci(long eci) {
        this.eci = eci;
    }

    public long getNci() {
        return nci;
    }

    public void setNci(long nci) {
        this.nci = nci;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(double uncertainty) {
        this.uncertainty = uncertainty;
    }

    public TypeOfShape getTypeOfShape() {
        return typeOfShape;
    }

    public void setTypeOfShape(TypeOfShape typeOfShape) {
        this.typeOfShape = typeOfShape;
    }

    // SRI - PSI
    public IMSI getImsi() {
        return imsi;
    }

    public void setImsi(IMSI imsi) {
        this.imsi = imsi;
    }

    public String getSriForSMImsi() {
        return sriForSMImsi;
    }

    public void setSriForSMImsi(String sriForSMImsi) {
        this.sriForSMImsi = sriForSMImsi;
    }

    public SriSmResponseParams getSriForSmResponse() {
        return this.sriForSmResponse;
    }

    public void setSriForSmResponse(SriSmResponseParams sriForSmResponse) {
        this.sriForSmResponse = sriForSmResponse;
    }

    public SriResponseValues getSriResponse() {
        return sriResponse;
    }

    public void setSriResponse(SriResponseValues sriResponse) {
        this.sriResponse = sriResponse;
    }


    // PSI Only IMSI & NNN
    public String getPsiServiceType() {
        return this.psiServiceType;
    }

    public void setPsiServiceType(String psiServiceType) {
        this.psiServiceType = psiServiceType;
    }

    public String getPsiOnlyImsi() {
        return psiOnlyImsi;
    }

    public void setPsiOnlyImsi(String psiOnlyImsi) {
        this.psiOnlyImsi = psiOnlyImsi;
    }

    public String getPsiOnlyNnn() {
        return psiOnlyNnn;
    }

    public void setPsiOnlyNnn(String psiOnlyNnn) {
        this.psiOnlyNnn = psiOnlyNnn;
    }

    @Override
    public String toString() {
        return "PsiResponseParams{" +
                "locationInformation=" + locationInformation +
                ", subscriberState=" + subscriberState +
                ", extensionContainer=" + extensionContainer +
                ", locationInformationGPRS=" + locationInformationGPRS +
                ", psSubscriberState=" + psSubscriberState +
                ", imei=" + imei +
                ", msClassmark2=" + msClassmark2 +
                ", gprsMSClass=" + gprsMSClass +
                ", mnpInfoRes=" + mnpInfoRes +
                ", imsVoiceOverPsSessionsIndication=" + imsVoiceOverPsSessionsIndication +
                ", lastUEActivityTime=" + lastUEActivityTime +
                ", lastRATType=" + lastRATType +
                ", epsSubscriberState=" + epsSubscriberState +
                ", locationInformationEPS=" + locationInformationEPS +
                ", timeZone=" + timeZone +
                ", daylightSavingTime=" + daylightSavingTime +
                ", locationInformation5GS=" + locationInformation5GS +
                ", imsi=" + imsi +
                '}';
    }
}
