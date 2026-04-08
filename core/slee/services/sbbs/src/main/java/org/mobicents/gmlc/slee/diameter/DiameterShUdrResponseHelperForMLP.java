package org.mobicents.gmlc.slee.diameter;

import org.mobicents.gmlc.slee.diameter.sh.ShUdaAvpValues;
import org.mobicents.gmlc.slee.primitives.EUTRANCGI;
import org.mobicents.gmlc.slee.primitives.NRCellGlobalId;
import org.mobicents.gmlc.slee.primitives.RoutingAreaId;
import org.mobicents.gmlc.slee.primitives.TrackingAreaId;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.LAIFixedLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class DiameterShUdrResponseHelperForMLP {

    protected final Logger logger = LoggerFactory.getLogger(DiameterShUdrResponseHelperForMLP.class.getName());
    protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
    protected static final DecimalFormat radiusFormat = new DecimalFormat("#0.00");

    private Integer mcc, mnc, lac, ci, sac, tac, rac, ratType;
    private Long eci, enbId, nci;
    private Integer ageOfLocationInfo;
    private String imsPublicId, msisdn, vlrNumber, mscNumber, sgsnNumber, nnn, mmeName, amfAddress, smsfAddress;
    private String subscriberState;
    private String typeOfShape;
    private Double latitude;
    private Double longitude;
    private Double uncertainty;
    private Double radius;
    private Boolean currentLocationInfoRetrieved;
    private String imei;

    public DiameterShUdrResponseHelperForMLP() {
    }

    public void handleUdrAnswerValues(ShUdaAvpValues uda) {

        if (uda != null) {
            // Get User-Data-Answer values
            if (uda.getUserData() != null) {
                // Public Identifiers
                if (uda.getImsPublicIdentity() != null) {
                    this.imsPublicId = uda.getImsPublicIdentity();
                }
                if (uda.getMsisdn() != null) {
                    this.msisdn = uda.getMsisdn();
                }

                // CS Location Information
                if (uda.getCsLocationInformation() != null) {
                    if (uda.getLocationNumber() != null) {
                        LocationNumber locationNumber = uda.getLocationNumber().getLocationNumber();
                        //oddFlag = locationNumber.isOddFlag();
                        //natureOfAddressIndicator = locationNumber.getNatureOfAddressIndicator();
                        //internalNetworkNumberIndicator = locationNumber.getInternalNetworkNumberIndicator();
                        //numberingPlanIndicator = locationNumber.getNumberingPlanIndicator();
                        //addressPresentationRestrictedIndicator = locationNumber.getAddressRepresentationRestrictedIndicator();
                        //screeningIndicator = locationNumber.getScreeningIndicator();
                        //locationNumberAddressDigits = locationNumber.getAddress();
                    }
                    if (uda.getCsCellGlobalId() != null) {
                        CellGlobalIdOrServiceAreaIdFixedLength csCgi = uda.getCsCellGlobalId().getCellGlobalIdOrServiceAreaIdFixedLength();
                        try {
                            this.mcc = csCgi.getMCC();
                            this.mnc = csCgi.getMNC();
                            this.lac = csCgi.getLac();
                            this.ci = csCgi.getCellIdOrServiceAreaCode();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getCsServiceAreaId() != null) {
                        CellGlobalIdOrServiceAreaIdFixedLength csSai = uda.getCsServiceAreaId().getCellGlobalIdOrServiceAreaIdFixedLength();
                        try {
                            this.mcc = csSai.getMCC();
                            this.mnc = csSai.getMNC();
                            this.lac = csSai.getLac();
                            this.sac = csSai.getCellIdOrServiceAreaCode();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getCsLocationAreaId() != null) {
                        LAIFixedLength csLai = uda.getCsLocationAreaId().getLaiFixedLength();
                        try {
                            this.mcc = csLai.getMCC();
                            this.mnc = csLai.getMNC();
                            this.lac = csLai.getLac();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getCsGeographicalInformation() != null) {
                        if (uda.getCsGeographicalInformation().getGeographicalInformation() != null) {
                            this.typeOfShape = uda.getCsGeographicalInformation().getGeographicalInformation().getTypeOfShape().name();
                            this.latitude = uda.getCsGeographicalInformation().getGeographicalInformation().getLatitude();
                            this.longitude = uda.getCsGeographicalInformation().getGeographicalInformation().getLongitude();
                            this.uncertainty = uda.getCsGeographicalInformation().getGeographicalInformation().getUncertainty();
                        }
                    }
                    if (uda.getCsGeodeticInformation() != null) {
                        if (uda.getCsGeodeticInformation().getGeodeticInformation() != null) {
                            this.typeOfShape = uda.getCsGeodeticInformation().getGeodeticInformation().getTypeOfShape().name();
                            this.latitude = uda.getCsGeodeticInformation().getGeodeticInformation().getLatitude();
                            this.longitude = uda.getCsGeodeticInformation().getGeodeticInformation().getLongitude();
                            this.uncertainty = uda.getCsGeodeticInformation().getGeodeticInformation().getUncertainty();
                            //csConfidence = uda.getCsGeodeticInformation().getGeodeticInformation().getConfidence();
                            //csScreeningAndPresentationIndicators = uda.getCsGeodeticInformation().getGeodeticInformation().getScreeningAndPresentationIndicators();

                        }
                    }
                    if (uda.getMscNumber() != null) {
                        this.mscNumber = uda.getMscNumber().getAddress();
                    }
                    if (uda.getVlrNumber() != null) {
                        this.vlrNumber = uda.getVlrNumber().getAddress();
                    }
                    if (uda.getCsCurrentLocationInfoRetrieved() != null) {
                        String csCurrentLocationInfoRetrieved = uda.getCsCurrentLocationInfoRetrieved();
                        if (csCurrentLocationInfoRetrieved.equalsIgnoreCase("0")||
                            csCurrentLocationInfoRetrieved.equalsIgnoreCase("true"))
                            this.currentLocationInfoRetrieved = true;
                        else if (csCurrentLocationInfoRetrieved.equalsIgnoreCase("1")||
                            csCurrentLocationInfoRetrieved.equalsIgnoreCase("false"))
                            this.currentLocationInfoRetrieved = false;
                    }
                    if (uda.getCsAgeOfLocationInfo() != null) {
                        ageOfLocationInfo = uda.getCsAgeOfLocationInfo();
                        if (ageOfLocationInfo > 0)
                            this.currentLocationInfoRetrieved = false;
                    }
                    if (uda.getUserCSGInformation() != null) {
                        //csCsgId = uda.getUserCSGInformation().getUserCSGInformationStr();
                    }
                    if (uda.getEutrancgi() != null) {
                        EUTRANCGI eUtranCgi = uda.getEutrancgi().getEutranCgi();
                        try {
                            this.mcc = eUtranCgi.getMCC();
                            this.mnc = eUtranCgi.getMNC();
                            this.enbId = eUtranCgi.getENodeBId();
                            this.eci = eUtranCgi.getEci();
                            this.ci = eUtranCgi.getCi();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getTrackingAreaId() != null) {
                        TrackingAreaId trackingAreaId = uda.getTrackingAreaId().getTrackingAreaId();
                        try {
                            this.mcc = trackingAreaId.getMCC();
                            this.mnc = trackingAreaId.getMNC();
                            this.tac = trackingAreaId.getTAC();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getCsLocalTimeZone() != null) {
                        //csTimeZone = uda.getCsLocalTimeZone().getTimeZone();
                        //csDaylightSavingTime = uda.getCsLocalTimeZone().getDaylightSavingTime();
                    }
                }

                // PS Location Information
                if (uda.getPsLocationInformation() != null) {
                    if (uda.getPsCellGlobalId() != null) {
                        CellGlobalIdOrServiceAreaIdFixedLength psCgi = uda.getPsCellGlobalId().getCellGlobalIdOrServiceAreaIdFixedLength();
                        try {
                            this.mcc = psCgi.getMCC();
                            this.mnc = psCgi.getMNC();
                            this.lac = psCgi.getLac();
                            this.ci = psCgi.getCellIdOrServiceAreaCode();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getPsServiceAreaId() != null) {
                        CellGlobalIdOrServiceAreaIdFixedLength psSai = uda.getPsServiceAreaId().getCellGlobalIdOrServiceAreaIdFixedLength();
                        try {
                            this.mcc = psSai.getMCC();
                            this.mnc = psSai.getMNC();
                            this.lac = psSai.getLac();
                            this.sac = psSai.getCellIdOrServiceAreaCode();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getPsLocationAreaId() != null) {
                        LAIFixedLength psLai = uda.getPsLocationAreaId().getLaiFixedLength();
                        try {
                            this.mcc = psLai.getMCC();
                            this.mnc = psLai.getMNC();
                            this.lac = psLai.getLac();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getRoutingAreaId() != null) {
                        RoutingAreaId raId = uda.getRoutingAreaId().getRoutingAreaIdentity();
                        try {
                            this.mcc = raId.getMCC();
                            this.mnc = raId.getMNC();
                            this.lac = raId.getLAC();
                            this.rac = raId.getRAC();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getPsGeographicalInformation() != null) {
                        if (uda.getPsGeographicalInformation().getGeographicalInformation() != null) {
                            this.typeOfShape = uda.getPsGeographicalInformation().getGeographicalInformation().getTypeOfShape().name();
                            this.latitude = uda.getPsGeographicalInformation().getGeographicalInformation().getLatitude();
                            this.longitude = uda.getPsGeographicalInformation().getGeographicalInformation().getLongitude();
                            this.uncertainty = uda.getPsGeographicalInformation().getGeographicalInformation().getUncertainty();
                        }
                    }
                    if (uda.getPsGeodeticInformation() != null) {
                        if (uda.getPsGeodeticInformation().getGeodeticInformation() != null) {
                            this.typeOfShape = uda.getPsGeodeticInformation().getGeodeticInformation().getTypeOfShape().name();
                            this.latitude = uda.getPsGeodeticInformation().getGeodeticInformation().getLatitude();
                            this.longitude = uda.getPsGeodeticInformation().getGeodeticInformation().getLongitude();
                            this.uncertainty = uda.getPsGeodeticInformation().getGeodeticInformation().getUncertainty();
                            //psConfidence = uda.getPsGeodeticInformation().getGeodeticInformation().getConfidence();
                            //psScreeningAndPresentationIndicators = uda.getPsGeodeticInformation().getGeodeticInformation().getScreeningAndPresentationIndicators();
                        }
                    }
                    if (uda.getSgsnNumber() != null) {
                        this.sgsnNumber = uda.getSgsnNumber().getAddress();
                    }
                    if (uda.getPsCurrentLocationInfoRetrieved() != null) {
                        String psCurrentLocationInfoRetrieved = uda.getPsCurrentLocationInfoRetrieved();
                        if (psCurrentLocationInfoRetrieved.equalsIgnoreCase("0") ||
                            psCurrentLocationInfoRetrieved.equalsIgnoreCase("true"))
                            this.currentLocationInfoRetrieved = true;
                        else if (psCurrentLocationInfoRetrieved.equalsIgnoreCase("1") ||
                            psCurrentLocationInfoRetrieved.equalsIgnoreCase("false"))
                            this.currentLocationInfoRetrieved = false;
                    }
                    if (uda.getPsAgeOfLocationInfo() != null) {
                        this.ageOfLocationInfo = uda.getPsAgeOfLocationInfo();
                        if (this.ageOfLocationInfo > 0) {
                            this.currentLocationInfoRetrieved = false;
                        }
                    }
                    if (uda.getUserCSGInformation() != null) {
                        //psCsgId = uda.getUserCSGInformation().getUserCSGInformationStr();
                    }
                    if (uda.getPsVisitedPLMNId() != null) {
                        if (uda.getPsVisitedPLMNId().getVisitedPlmnId() != null) {
                            //psVPlmnIdMcc = uda.getPsVisitedPLMNId().getVisitedPlmnId().getMcc();
                            //psVPlmnIdMnc = uda.getPsVisitedPLMNId().getVisitedPlmnId().getMnc();
                        }
                    }
                    if (uda.getPsLocalTimeZone() != null) {
                        //psTimeZone = uda.getPsLocalTimeZone().getTimeZone();
                        //psDaylightSavingTime = uda.getPsLocalTimeZone().getDaylightSavingTime();
                    }
                    if (uda.getPsRatType() != null) {
                        this.ratType = uda.getPsRatType();
                    }
                }

                // EPS Location Information
                if (uda.getEpsLocationInformation() != null) {
                    if (uda.getEutrancgi() != null) {
                        EUTRANCGI eUtranCgi = uda.getEutrancgi().getEutranCgi();
                        try {
                            this.mcc = eUtranCgi.getMCC();
                            this.mnc = eUtranCgi.getMNC();
                            this.eci = eUtranCgi.getEci();
                            // eNBId = eUtranCgi.getENodeBId();
                            this.ci = eUtranCgi.getCi();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getTrackingAreaId() != null) {
                        TrackingAreaId trackingAreaId = uda.getTrackingAreaId().getTrackingAreaId();
                        try {
                            this.mcc = trackingAreaId.getMCC();
                            this.mnc = trackingAreaId.getMNC();
                            this.tac = trackingAreaId.getTAC();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getEpsGeographicalInformation() != null) {
                        if (uda.getEpsGeographicalInformation().getGeographicalInformation() != null) {
                            this.typeOfShape = uda.getEpsGeographicalInformation().getGeographicalInformation().getTypeOfShape().name();
                            this.latitude = uda.getEpsGeographicalInformation().getGeographicalInformation().getLatitude();
                            this.longitude = uda.getEpsGeographicalInformation().getGeographicalInformation().getLongitude();
                            this.uncertainty = uda.getEpsGeographicalInformation().getGeographicalInformation().getUncertainty();
                        }
                    }
                    if (uda.getEpsGeodeticInformation() != null) {
                        if (uda.getEpsGeodeticInformation().getGeodeticInformation() != null) {
                            this.typeOfShape = uda.getEpsGeodeticInformation().getGeodeticInformation().getTypeOfShape().name();
                            this.latitude = uda.getEpsGeodeticInformation().getGeodeticInformation().getLatitude();
                            this.longitude = uda.getEpsGeodeticInformation().getGeodeticInformation().getLongitude();
                            this.uncertainty = uda.getEpsGeodeticInformation().getGeodeticInformation().getUncertainty();
                            // epsConfidence = uda.getEpsGeodeticInformation().getGeodeticInformation().getConfidence();
                            // epsScreeningAndPresentationIndicators = uda.getEpsGeodeticInformation().getGeodeticInformation().getScreeningAndPresentationIndicators();
                        }
                    }
                    if (uda.getMmeName() != null) {
                        this.mmeName = uda.getMmeName();
                    }
                    if (uda.getEpsCurrentLocationInfoRetrieved() != null) {
                        String epsCurrentLocationInfoRetrieved = uda.getEpsCurrentLocationInfoRetrieved();
                        if (epsCurrentLocationInfoRetrieved.equalsIgnoreCase("0") ||
                            epsCurrentLocationInfoRetrieved.equalsIgnoreCase("true"))
                            this.currentLocationInfoRetrieved = true;
                        else if (epsCurrentLocationInfoRetrieved.equalsIgnoreCase("1") ||
                            epsCurrentLocationInfoRetrieved.equalsIgnoreCase("false"))
                            this.currentLocationInfoRetrieved = false;
                    }
                    if (uda.getEpsAgeOfLocationInfo() != null) {
                        this.ageOfLocationInfo = uda.getEpsAgeOfLocationInfo();
                        if (this.ageOfLocationInfo > 0) {
                            this.currentLocationInfoRetrieved = false;
                        }
                    }
                    if (uda.getUserCSGInformation() != null) {
                        //epsCsgId = uda.getUserCSGInformation().getUserCSGInformationStr();
                    }
                    if (uda.getEpsVisitedPLMNId() != null) {
                        if (uda.getEpsVisitedPLMNId().getVisitedPlmnId() != null) {
                            //epsVPlmnIdMcc = uda.getEpsVisitedPLMNId().getVisitedPlmnId().getMcc();
                            //epsVPlmnIdMnc = uda.getEpsVisitedPLMNId().getVisitedPlmnId().getMnc();
                        }
                    }
                    if (uda.getEpsLocalTimeZone() != null) {
                        //epsTimeZone = uda.getEpsLocalTimeZone().getTimeZone();
                        //epsDaylightSavingTime = uda.getEpsLocalTimeZone().getDaylightSavingTime();
                    }
                    if (uda.getEpsRatType() != null) {
                        this.ratType = uda.getEpsRatType();
                    }
                }

                // 5GS Location Information
                if (uda.getSh5GSLocationInformation() != null) {
                    if (uda.getShNRCellGlobalId() != null) {
                        NRCellGlobalId nrCellGlobalId = uda.getShNRCellGlobalId().getNRCellGlobalId();
                        try {
                            this.mcc = nrCellGlobalId.getMCC();
                            this.mnc = nrCellGlobalId.getMNC();
                            this.nci = nrCellGlobalId.getNCI();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getEutrancgi() != null) {
                        EUTRANCGI eUtranCgi = uda.getEutrancgi().getEutranCgi();
                        try {
                            this.mcc = eUtranCgi.getMCC();
                            this.mnc = eUtranCgi.getMNC();
                            this.eci =eUtranCgi.getEci();
                            //sh5gsENBid = eUtranCgi.getENodeBId();
                            this.ci = eUtranCgi.getCi();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getTrackingAreaId() != null) {
                        TrackingAreaId trackingAreaId = uda.getTrackingAreaId().getTrackingAreaId();
                        try {
                            this.mcc = trackingAreaId.getMCC();
                            this.mnc = trackingAreaId.getMNC();
                            this.tac = trackingAreaId.getTAC();
                        } catch (MAPException e) {
                            logger.error(e.getMessage());
                        }
                    }
                    if (uda.getSh5GSGeographicalInformation() != null) {
                        if (uda.getSh5GSGeographicalInformation().getGeographicalInformation() != null) {
                            this.typeOfShape = uda.getSh5GSGeographicalInformation().getGeographicalInformation().getTypeOfShape().name();
                            this.latitude = uda.getSh5GSGeographicalInformation().getGeographicalInformation().getLatitude();
                            this.longitude = uda.getSh5GSGeographicalInformation().getGeographicalInformation().getLongitude();
                            this.uncertainty = uda.getSh5GSGeographicalInformation().getGeographicalInformation().getUncertainty();
                        }
                    }
                    if (uda.getAmfAddress() != null) {
                        this.amfAddress = uda.getAmfAddress();
                    }
                    if (uda.getSmsfAddress() != null) {
                        this.smsfAddress = uda.getSmsfAddress();
                    }
                    if (uda.getSh5GSCurrentLocationInfoRetrieved() != null) {
                        String sh5gsCurrentLocationInfoRetrieved = uda.getSh5GSCurrentLocationInfoRetrieved();
                        if (sh5gsCurrentLocationInfoRetrieved.equalsIgnoreCase("0") ||
                            sh5gsCurrentLocationInfoRetrieved.equalsIgnoreCase("true"))
                            this.currentLocationInfoRetrieved = true;
                        else if (sh5gsCurrentLocationInfoRetrieved.equalsIgnoreCase("1") ||
                            sh5gsCurrentLocationInfoRetrieved.equalsIgnoreCase("false"))
                            this.currentLocationInfoRetrieved = false;
                    }
                    if (uda.getSh5GSAgeOfLocationInfo() != null) {
                        this.ageOfLocationInfo = uda.getSh5GSAgeOfLocationInfo();
                        if (this.ageOfLocationInfo > 0) {
                            this.currentLocationInfoRetrieved = false;
                        }
                    }
                    if (uda.getSh5gsVisitedPLMNId() != null) {
                        if (uda.getSh5gsVisitedPLMNId().getVisitedPlmnId() != null) {
                            //sh5gsVPlmnIdMcc = uda.getSh5gsVisitedPLMNId().getVisitedPlmnId().getMcc();
                            //sh5gsVPlmnIdMnc = uda.getSh5gsVisitedPLMNId().getVisitedPlmnId().getMnc();
                        }
                    }
                    if (uda.getSh5gsLocalTimeZone() != null) {
                        //sh5gsTimeZone = uda.getSh5gsLocalTimeZone().getTimeZone();
                        //sh5gsDaylightSavingTime = uda.getSh5gsLocalTimeZone().getDaylightSavingTime();
                    }
                    if (uda.getSh5gsRatType() != null) {
                        this.ratType = uda.getSh5gsRatType();
                    }
                }
            }
        }
    }

    /////////////////////////
    // Getter and Setters //
    ///////////////////////

    public Double getLatitude() {
        if (this.latitude != null) {
            String formattedLatitude = coordinatesFormat.format(this.latitude);
            return Double.valueOf(formattedLatitude);
        }
        return null;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        if (this.longitude != null) {
            String formattedLongitude = coordinatesFormat.format(this.longitude);
            return Double.valueOf(formattedLongitude);
        }
        return null;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(Double uncertainty) {
        this.uncertainty = uncertainty;
    }

    public Double getRadius() {
        if (this.uncertainty != null) {
            this.radius = this.uncertainty;
            String formattedRadius = radiusFormat.format(this.radius);
            return Double.valueOf(formattedRadius);
        } else {
            return this.radius;
        }
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Integer getMcc() {
        return mcc;
    }

    public void setMcc(Integer mcc) {
        this.mcc = mcc;
    }

    public Integer getMnc() {
        return mnc;
    }

    public void setMnc(Integer mnc) {
        this.mnc = mnc;
    }

    public Integer getLac() {
        return lac;
    }

    public void setLac(Integer lac) {
        this.lac = lac;
    }

    public Integer getCi() {
        return ci;
    }

    public void setCi(Integer ci) {
        this.ci = ci;
    }

    public Integer getSac() {
        return sac;
    }

    public void setSac(Integer sac) {
        this.sac = sac;
    }

    public Integer getTac() {
        return tac;
    }

    public void setTac(Integer tac) {
        this.tac = tac;
    }

    public Integer getRac() {
        return rac;
    }

    public void setRac(Integer rac) {
        this.rac = rac;
    }

    public Integer getRatType() {
        return ratType;
    }

    public void setRatType(Integer ratType) {
        this.ratType = ratType;
    }

    public Long getEci() {
        return eci;
    }

    public void setEci(Long eci) {
        this.eci = eci;
    }

    public Long getEnbId() {
        return enbId;
    }

    public void setEnbId(Long enbId) {
        this.enbId = enbId;
    }

    public Long getNci() {
        return nci;
    }

    public void setNci(Long nci) {
        this.nci = nci;
    }

    public Integer getAgeOfLocationInfo() {
        return ageOfLocationInfo;
    }

    public void setAgeOfLocationInfo(Integer ageOfLocationInfo) {
        this.ageOfLocationInfo = ageOfLocationInfo;
    }

    public String getImsPublicId() {
        return imsPublicId;
    }

    public void setImsPublicId(String imsPublicId) {
        this.imsPublicId = imsPublicId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getVlrNumber() {
        return vlrNumber;
    }

    public void setVlrNumber(String vlrNumber) {
        this.vlrNumber = vlrNumber;
    }

    public String getMscNumber() {
        return mscNumber;
    }

    public void setMscNumber(String mscNumber) {
        this.mscNumber = mscNumber;
    }

    public String getSgsnNumber() {
        return sgsnNumber;
    }

    public void setSgsnNumber(String sgsnNumber) {
        this.sgsnNumber = sgsnNumber;
    }

    public String getNnn() {
        return nnn;
    }

    public void setNnn(String nnn) {
        this.nnn = nnn;
    }

    public String getMmeName() {
        return mmeName;
    }

    public void setMmeName(String mmeName) {
        this.mmeName = mmeName;
    }

    public String getAmfAddress() {
        return amfAddress;
    }

    public void setAmfAddress(String amfAddress) {
        this.amfAddress = amfAddress;
    }

    public String getSmsfAddress() {
        return smsfAddress;
    }

    public void setSmsfAddress(String smsfAddress) {
        this.smsfAddress = smsfAddress;
    }

    public String getSubscriberState() {
        return subscriberState;
    }

    public void setSubscriberState(String subscriberState) {
        this.subscriberState = subscriberState;
    }

    public String getTypeOfShape() {
        return typeOfShape;
    }

    public void setTypeOfShape(String typeOfShape) {
        this.typeOfShape = typeOfShape;
    }

    public Boolean getCurrentLocationInfoRetrieved() {
        return currentLocationInfoRetrieved;
    }

    public void setCurrentLocationInfoRetrieved(Boolean currentLocationInfoRetrieved) {
        this.currentLocationInfoRetrieved = currentLocationInfoRetrieved;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
