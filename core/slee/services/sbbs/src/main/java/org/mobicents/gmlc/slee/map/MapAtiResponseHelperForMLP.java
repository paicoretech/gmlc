package org.mobicents.gmlc.slee.map;

import org.mobicents.gmlc.slee.diameter.AVPHandler;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DaylightSavingTime;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRCellGlobalId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRTAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TimeZone;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
 * Helper class to handle a MAP ATI response value and populate an MLP SLIA
 *
 * @author  <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class MapAtiResponseHelperForMLP {

    private final Logger logger = LoggerFactory.getLogger(MapAtiResponseHelperForMLP.class.getName());
    protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
    protected static final DecimalFormat radiusFormat = new DecimalFormat("#0.00");

    private Integer mcc, mnc, lac, ci, sac, tac, rac, ratType, nrTac, confidence, screeningAndPresentationIndicators;
    private Long eci, enbId, nci;
    private Integer ageOfLocationInfo;
    private String vlrNumber, mscNumber, sgsnNumber, nnn, mmeName, subscriberState, imei;
    private String typeOfShape;
    private Double latitude, longitude, uncertainty, radius;
    private Boolean saiPresent = false;
    private Time lastUEActivityTime;
    private UsedRATType lastRatType;
    private PSSubscriberState epsSubscriberState;

    private LocationInformation locationInformation;
    private CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
    private CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength;

    private GeographicalInformation geographicalInformation;
    private GeodeticInformation geodeticInformation;
    private LocationInformationGPRS locationInformationGPRS;
    private LocationInformationEPS locationInformationEPS;

    private EUtranCgi eUtranCgi;

    private TAId taId ;
    private TimeZone timeZone;
    private DaylightSavingTime daylightSavingTime;

    private LocationInformation5GS locationInformation5GS;
    private NRCellGlobalId nrCellGlobalId;
    private FQDN amfAddress;
    private PlmnId vPlmnId;
    private TimeZone localTimeZone;
    private UsedRATType usedRATType;
    private NRTAId nrTrackingAreaIdentity;

    public MapAtiResponseHelperForMLP() {
    }

    public void handleAtiResponseValues(AtiResponseParams atiResponseParams) {
        try {
            if (atiResponseParams != null) {

                if (atiResponseParams.getLocationInformation() != null) {
                    this.locationInformation = atiResponseParams.getLocationInformation();
                    if (atiResponseParams.getLocationInformation().getSaiPresent())
                        this.saiPresent = true;
                    if (locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                        this.cellGlobalIdOrServiceAreaIdOrLAI = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI();
                        if (cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength() != null) {
                            this.mcc = cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength().getMCC();
                            this.mnc = cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength().getMNC();
                            this.lac = cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength().getLac();
                        } else if (cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                            this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength();
                            this.mcc = cellGlobalIdOrServiceAreaIdFixedLength.getMCC();
                            this.mnc = cellGlobalIdOrServiceAreaIdFixedLength.getMNC();
                            this.lac = cellGlobalIdOrServiceAreaIdFixedLength.getLac();
                            if (this.saiPresent)
                                this.sac = cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode();
                            else
                                this.ci = cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode();
                        }
                    }
                    if (locationInformation.getGeographicalInformation() != null) {
                        this.geographicalInformation = locationInformation.getGeographicalInformation();
                        if (geographicalInformation.getTypeOfShape() != null)
                            this.typeOfShape = geographicalInformation.getTypeOfShape().name();
                        this.latitude = geographicalInformation.getLatitude();
                        this.longitude = geographicalInformation.getLongitude();
                        this.uncertainty = geographicalInformation.getUncertainty();
                    } else if (locationInformation.getGeodeticInformation() != null) {
                        this.geodeticInformation = locationInformation.getGeodeticInformation();
                        if (geodeticInformation.getTypeOfShape() != null)
                            this.typeOfShape = geodeticInformation.getTypeOfShape().name();
                        this.latitude = geodeticInformation.getLatitude();
                        this.longitude = geodeticInformation.getLongitude();
                        this.uncertainty = geodeticInformation.getUncertainty();
                    }
                    if (locationInformation.getAgeOfLocationInformation() != null) {
                        this.ageOfLocationInfo = locationInformation.getAgeOfLocationInformation();
                    }
                    if (locationInformation.getLocationInformationEPS() != null) {
                        this.locationInformationEPS = locationInformation.getLocationInformationEPS();
                        this.ageOfLocationInfo = locationInformationEPS.getAgeOfLocationInformation();
                        if (locationInformationEPS.getEUtranCellGlobalIdentity() != null) {
                            this.eUtranCgi = locationInformationEPS.getEUtranCellGlobalIdentity();
                            this.mcc = eUtranCgi.getMCC();
                            this.mnc = eUtranCgi.getMNC();
                            this.eci = eUtranCgi.getEci();
                            this.enbId = eUtranCgi.getENodeBId();
                            this.ci = eUtranCgi.getCi();
                        }
                        if (locationInformationEPS.getTrackingAreaIdentity() != null) {
                            this.taId = locationInformationEPS.getTrackingAreaIdentity();
                            this.mcc = taId.getMCC();
                            this.mnc = taId.getMNC();
                            this.tac = taId.getTAC();
                        }
                        if (locationInformationEPS.getGeographicalInformation() != null) {
                            this.geographicalInformation = locationInformationEPS.getGeographicalInformation();
                            if (geographicalInformation.getTypeOfShape()  != null)
                                this.typeOfShape = geographicalInformation.getTypeOfShape().name();
                            this.latitude = geographicalInformation.getLatitude();
                            this.longitude = geographicalInformation.getLongitude();
                            this.uncertainty = geographicalInformation.getUncertainty();
                        } else if (locationInformationEPS.getGeodeticInformation() != null) {
                            this.geodeticInformation = locationInformationEPS.getGeodeticInformation();
                            if (geodeticInformation.getTypeOfShape() != null)
                                this.typeOfShape = geodeticInformation.getTypeOfShape().name();
                            this.latitude = geodeticInformation.getLatitude();
                            this.longitude = geodeticInformation.getLongitude();
                            this.uncertainty = geodeticInformation.getUncertainty();
                            this.confidence = geodeticInformation.getConfidence();
                            this.screeningAndPresentationIndicators = geodeticInformation.getScreeningAndPresentationIndicators();
                        }
                        if (locationInformationEPS.getMmeName() != null) {
                            this.mmeName = AVPHandler.byte2String(locationInformationEPS.getMmeName().getData());
                        }
                    }
                    if (locationInformation.getVlrNumber() != null) {
                        this.nnn = this.vlrNumber = locationInformation.getVlrNumber().getAddress();
                    }
                    if (locationInformation.getMscNumber() != null) {
                        this.nnn = this.mscNumber = locationInformation.getMscNumber().getAddress();
                    }
                }
                if (atiResponseParams.getLocationInformationGPRS() != null) {
                    this.locationInformationGPRS = atiResponseParams.getLocationInformationGPRS();
                    if (locationInformationGPRS.isSaiPresent())
                        this.saiPresent = true;
                    if (locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                        this.cellGlobalIdOrServiceAreaIdOrLAI = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI();
                        if (cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength() != null) {
                            this.mcc = cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength().getMCC();
                            this.mnc = cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength().getMNC();
                            this.lac = cellGlobalIdOrServiceAreaIdOrLAI.getLAIFixedLength().getLac();
                        } else if (cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                            this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength();
                            this.mcc = cellGlobalIdOrServiceAreaIdFixedLength.getMCC();
                            this.mnc = cellGlobalIdOrServiceAreaIdFixedLength.getMNC();
                            this.lac = cellGlobalIdOrServiceAreaIdFixedLength.getLac();
                            if (this.saiPresent)
                                this.sac = cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode();
                            else
                                this.ci = cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode();
                        }
                    }
                    if (locationInformationGPRS.getGeographicalInformation() != null) {
                        this.geographicalInformation = locationInformationGPRS.getGeographicalInformation();
                        if (geographicalInformation.getTypeOfShape() != null)
                            this.typeOfShape = geographicalInformation.getTypeOfShape().name();
                        this.latitude = geographicalInformation.getLatitude();
                        this.longitude = geographicalInformation.getLongitude();
                        this.uncertainty = geographicalInformation.getUncertainty();
                    } else if (locationInformationGPRS.getGeodeticInformation() != null) {
                        this.geodeticInformation = locationInformationGPRS.getGeodeticInformation();
                        if (geodeticInformation.getTypeOfShape() != null)
                            this.typeOfShape = geodeticInformation.getTypeOfShape().name();
                        this.latitude = geodeticInformation.getLatitude();
                        this.longitude = geodeticInformation.getLongitude();
                        this.uncertainty = geodeticInformation.getUncertainty();
                        this.confidence = geodeticInformation.getConfidence();
                        this.screeningAndPresentationIndicators = geodeticInformation.getScreeningAndPresentationIndicators();
                    }
                    if (locationInformationGPRS.getAgeOfLocationInformation() != null) {
                        this.ageOfLocationInfo = locationInformationGPRS.getAgeOfLocationInformation();
                    }
                    if (locationInformationGPRS.getSGSNNumber() != null) {
                        this.nnn = this.sgsnNumber = locationInformationGPRS.getSGSNNumber().getAddress();
                    }
                }
                if (atiResponseParams.getSubscriberState() != null) {
                    if (atiResponseParams.getSubscriberState().getSubscriberStateChoice() != null) {
                        this.subscriberState = atiResponseParams.getSubscriberState().getSubscriberStateChoice().toString();
                    }
                }
                if (atiResponseParams.getImei() != null) {
                    this.imei = atiResponseParams.getImei().getIMEI();
                }
                if (atiResponseParams.getLastUEActivityTime() != null) {
                    this.lastUEActivityTime = atiResponseParams.getLastUEActivityTime();
                }
                if (atiResponseParams.getLastRATType() != null) {
                    this.lastRatType = atiResponseParams.getLastRATType();
                }
                if (atiResponseParams.getEpsSubscriberState() != null) {
                    this.epsSubscriberState = atiResponseParams.getEpsSubscriberState();
                }
                if (atiResponseParams.getLocationInformationEPS() != null) {
                    this.locationInformationEPS = atiResponseParams.getLocationInformationEPS();
                    this.ageOfLocationInfo = locationInformationEPS.getAgeOfLocationInformation();
                    if (locationInformationEPS.getTrackingAreaIdentity() != null) {
                        this.taId = locationInformationEPS.getTrackingAreaIdentity();
                        this.mcc = taId.getMCC();
                        this.mnc = taId.getMNC();
                        this.tac = taId.getTAC();
                    }
                    if (locationInformationEPS.getEUtranCellGlobalIdentity() != null) {
                       this.eUtranCgi = locationInformationEPS.getEUtranCellGlobalIdentity();
                        this.mcc = eUtranCgi.getMCC();
                        this.mnc = eUtranCgi.getMNC();
                        this.eci = eUtranCgi.getEci();
                        this.enbId = eUtranCgi.getENodeBId();
                        this.ci = eUtranCgi.getCi();
                    }
                    if (locationInformationEPS.getGeographicalInformation() != null) {
                        this.geographicalInformation = locationInformationEPS.getGeographicalInformation();
                        if (geographicalInformation.getTypeOfShape() != null)
                            this.typeOfShape = geographicalInformation.getTypeOfShape().name();
                        this.latitude = geographicalInformation.getLatitude();
                        this.longitude = geographicalInformation.getLongitude();
                        this.uncertainty = geographicalInformation.getUncertainty();
                    } else if (locationInformationEPS.getGeodeticInformation() != null) {
                        this.geodeticInformation = locationInformationEPS.getGeodeticInformation();
                        if (geodeticInformation.getTypeOfShape() != null)
                            this.typeOfShape = geodeticInformation.getTypeOfShape().name();
                        this.latitude = geodeticInformation.getLatitude();
                        this.longitude = geodeticInformation.getLongitude();
                        this.uncertainty = geodeticInformation.getUncertainty();
                        this.confidence = geodeticInformation.getConfidence();
                        this.screeningAndPresentationIndicators = geodeticInformation.getScreeningAndPresentationIndicators();
                    }
                    if (locationInformationEPS.getMmeName() != null) {
                        this.mmeName = AVPHandler.byte2String(locationInformationEPS.getMmeName().getData());
                    }
                }
                if (atiResponseParams.getTimeZone() != null) {
                    this.timeZone = atiResponseParams.getTimeZone();
                }
                if (atiResponseParams.getDaylightSavingTime() != null) {
                    this.daylightSavingTime = atiResponseParams.getDaylightSavingTime();
                }
                if (atiResponseParams.getLocationInformation5GS() != null) {
                    this.locationInformation5GS = atiResponseParams.getLocationInformation5GS();
                    if (locationInformation5GS.getTAId() != null) {
                        this.taId = locationInformation5GS.getTAId();
                        this.mcc = taId.getMCC();
                        this.mnc = taId.getMCC();
                        this.tac = taId.getTAC();
                    }
                    if (locationInformation5GS.getEUtranCgi() != null) {
                        this.eUtranCgi = locationInformation5GS.getEUtranCgi();
                        this.mcc = eUtranCgi.getMCC();
                        this.mnc = eUtranCgi.getMNC();
                        this.eci = eUtranCgi.getEci();
                        this.enbId = eUtranCgi.getENodeBId();
                        this.ci = eUtranCgi.getCi();
                    }
                    if (locationInformation5GS.getNRCellGlobalId() != null) {
                        this.nrCellGlobalId = locationInformation5GS.getNRCellGlobalId();
                        if (this.mcc != null && this.mnc != null) {
                            this.mcc = this.nrCellGlobalId.getMCC();
                            this.mnc = this.nrCellGlobalId.getMNC();
                        }
                        this.nci = this.nrCellGlobalId.getNCI();
                    }
                    if (locationInformation5GS.getNRTAId() != null) {
                        this.nrTrackingAreaIdentity = locationInformation5GS.getNRTAId();
                        if (this.mcc != null && this.mnc != null) {
                            this.mcc = this.nrTrackingAreaIdentity.getMCC();
                            this.mnc = this.nrTrackingAreaIdentity.getMNC();
                        }
                        this.nrTac = this.nrTrackingAreaIdentity.getNrTAC();
                    }
                    if (locationInformation5GS.getGeographicalInformation() != null) {
                        this.geographicalInformation = locationInformation5GS.getGeographicalInformation();
                        if (geographicalInformation.getTypeOfShape()  != null)
                            this.typeOfShape = geographicalInformation.getTypeOfShape().name();
                        this.latitude = geographicalInformation.getLatitude();
                        this.longitude = geographicalInformation.getLongitude();
                        this.uncertainty = geographicalInformation.getUncertainty();
                    } else if (locationInformation5GS.getGeodeticInformation() != null) {
                        this.geodeticInformation = locationInformation5GS.getGeodeticInformation();
                        if (geodeticInformation.getTypeOfShape() != null)
                            this.typeOfShape = geodeticInformation.getTypeOfShape().name();
                        this.latitude = geodeticInformation.getLatitude();
                        this.longitude = geodeticInformation.getLongitude();
                        this.uncertainty = geodeticInformation.getUncertainty();
                        this.confidence = geodeticInformation.getConfidence();
                        this.screeningAndPresentationIndicators = geodeticInformation.getScreeningAndPresentationIndicators();
                    }
                    if (locationInformation5GS.getAMFAddress() != null) {
                        this.amfAddress = locationInformation5GS.getAMFAddress();
                    }
                    if (locationInformation5GS.getLocalTimeZone() != null) {
                        this.timeZone = locationInformation5GS.getLocalTimeZone();
                    }
                    if (locationInformation5GS.getUsedRATType() != null) {
                        this.usedRATType = locationInformation5GS.getUsedRATType();
                    }
                    if (locationInformation5GS.getVPlmnId() != null) {
                        this.vPlmnId = locationInformation5GS.getVPlmnId();
                    }

                }

            }

        } catch (MAPException e) {
            logger.error("MAP exception while processing ATI response values: {}", String.valueOf(e));
        } catch (Exception e) {
            logger.error("Exception while processing ATI response values: {}", String.valueOf(e));
        }
    }

    /////////////////////////
    // Getter and Setters //
    ///////////////////////


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

    public Boolean getSaiPresent() {
        return saiPresent;
    }

    public void setSaiPresent(Boolean saiPresent) {
        this.saiPresent = saiPresent;
    }

    public LocationInformation getLocationInformation() {
        return locationInformation;
    }

    public void setLocationInformation(LocationInformation locationInformation) {
        this.locationInformation = locationInformation;
    }

    public CellGlobalIdOrServiceAreaIdOrLAI getCellGlobalIdOrServiceAreaIdOrLAI() {
        return cellGlobalIdOrServiceAreaIdOrLAI;
    }

    public void setCellGlobalIdOrServiceAreaIdOrLAI(CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI) {
        this.cellGlobalIdOrServiceAreaIdOrLAI = cellGlobalIdOrServiceAreaIdOrLAI;
    }

    public CellGlobalIdOrServiceAreaIdFixedLength getCellGlobalIdOrServiceAreaIdFixedLength() {
        return cellGlobalIdOrServiceAreaIdFixedLength;
    }

    public void setCellGlobalIdOrServiceAreaIdFixedLength(CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength) {
        this.cellGlobalIdOrServiceAreaIdFixedLength = cellGlobalIdOrServiceAreaIdFixedLength;
    }

    public GeographicalInformation getGeographicalInformation() {
        return geographicalInformation;
    }

    public void setGeographicalInformation(GeographicalInformation geographicalInformation) {
        this.geographicalInformation = geographicalInformation;
    }

    public GeodeticInformation getGeodeticInformation() {
        return geodeticInformation;
    }

    public void setGeodeticInformation(GeodeticInformation geodeticInformation) {
        this.geodeticInformation = geodeticInformation;
    }

    public LocationInformationGPRS getLocationInformationGPRS() {
        return locationInformationGPRS;
    }

    public void setLocationInformationGPRS(LocationInformationGPRS locationInformationGPRS) {
        this.locationInformationGPRS = locationInformationGPRS;
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

    public Long getEnbId() {
        return enbId;
    }

    public void setEnbId(Long enbId) {
        this.enbId = enbId;
    }

    public Long getEci() {
        return eci;
    }

    public void setEci(Long eci) {
        this.eci = eci;
    }

    public Integer getAgeOfLocationInfo() {
        return ageOfLocationInfo;
    }

    public void setAgeOfLocationInfo(Integer ageOfLocationInfo) {
        this.ageOfLocationInfo = ageOfLocationInfo;
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

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public Integer getScreeningAndPresentationIndicators() {
        return screeningAndPresentationIndicators;
    }

    public void setScreeningAndPresentationIndicators(Integer screeningAndPresentationIndicators) {
        this.screeningAndPresentationIndicators = screeningAndPresentationIndicators;
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Long getNci() {
        return nci;
    }

    public void setNci(Long nci) {
        this.nci = nci;
    }

    public Time getLastUEActivityTime() {
        return lastUEActivityTime;
    }

    public void setLastUEActivityTime(Time lastUEActivityTime) {
        this.lastUEActivityTime = lastUEActivityTime;
    }

    public UsedRATType getLastRatType() {
        return lastRatType;
    }

    public void setLastRatType(UsedRATType lastRatType) {
        this.lastRatType = lastRatType;
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
    }

    public EUtranCgi geteUtranCgi() {
        return eUtranCgi;
    }

    public void seteUtranCgi(EUtranCgi eUtranCgi) {
        this.eUtranCgi = eUtranCgi;
    }

    public TAId getTaId() {
        return taId;
    }

    public void setTaId(TAId taId) {
        this.taId = taId;
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
    }

    public NRCellGlobalId getNrCellGlobalId() {
        return nrCellGlobalId;
    }

    public void setNrCellGlobalId(NRCellGlobalId nrCellGlobalId) {
        this.nrCellGlobalId = nrCellGlobalId;
    }

    public FQDN getAmfAddress() {
        return amfAddress;
    }

    public void setAmfAddress(FQDN amfAddress) {
        this.amfAddress = amfAddress;
    }

    public PlmnId getvPlmnId() {
        return vPlmnId;
    }

    public void setvPlmnId(PlmnId vPlmnId) {
        this.vPlmnId = vPlmnId;
    }

    public TimeZone getLocalTimeZone() {
        return localTimeZone;
    }

    public void setLocalTimeZone(TimeZone localTimeZone) {
        this.localTimeZone = localTimeZone;
    }

    public UsedRATType getUsedRATType() {
        return usedRATType;
    }

    public void setUsedRATType(UsedRATType usedRATType) {
        this.usedRATType = usedRATType;
    }

    public NRTAId getNrTrackingAreaIdentity() {
        return nrTrackingAreaIdentity;
    }

    public void setNrTrackingAreaIdentity(NRTAId nrTrackingAreaIdentity) {
        this.nrTrackingAreaIdentity = nrTrackingAreaIdentity;
        try {
            setNrTac(nrTrackingAreaIdentity.getNrTAC());
        } catch (MAPException e) {
            logger.error("Map exception while setting NR-TAC: {}", String.valueOf(e));
        }
    }

    public Integer getNrTac() {
        return nrTac;
    }

    public void setNrTac(Integer nrTac) {
        this.nrTac = nrTac;
    }
}
