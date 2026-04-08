package org.mobicents.gmlc.slee.supl;

import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.primitives.EUTRANCGI;
import org.mobicents.gmlc.slee.primitives.NRCellGlobalId;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.TriggerParams;
import org.mobicents.gmlc.slee.supl.ULP_Components.PosMethod;
import org.mobicents.gmlc.slee.supl.ULP_Components.SessionID;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SuplResponseHelperForMLP implements Serializable {

    private static final Logger logger = Logger.getLogger(SuplResponseHelperForMLP.class.getName());

    protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
    protected static final DecimalFormat radiusFormat = new DecimalFormat("#0.00");
    protected static final DecimalFormat angleFormat = new DecimalFormat("#0.00");
    protected static final DecimalFormat axisFormat = new DecimalFormat("#0.00");

    private String msisdn, imsi;
    private Integer mcc, mnc, lac, ci, sac, rac, tac;
    private Long uci, eci, enbId, nrCi, timingAdvance;
    private TypeOfShape typeOfShape;
    private Double latitude;
    private Double longitude;
    private Integer altitude;
    private Double uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, orientationMajorAxis,
        uncertaintyAltitude, uncertaintyInnerRadius, offsetAngle, includedAngle, radius;
    private Integer confidence, innerRadius;
    Integer horizontalSpeed, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed, bearing;
    String velocityType;
    private Integer transactionId;
    private CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
    private EUTRANCGI lteCGI;
    private NRCellGlobalId nrCellGlobalId;
    private PosMethod posMethod;
    private TriggerParams triggerParams;
    private Boolean isReport = false;
    public Integer suplReportReferenceNumber;
    private SessionID sessionID;

    private Boolean isTimeOut = false;

    public SuplResponseHelperForMLP() {
    }

    //////////////////////////
    // Getters and Setters //
    ////////////////////////

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
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

    public Integer getRac() {
        return rac;
    }

    public void setRac(Integer rac) {
        this.rac = rac;
    }

    public Integer getTac() {
        return tac;
    }

    public void setTac(Integer tac) {
        this.tac = tac;
    }

    public Long getUci() {
        return uci;
    }

    public void setUci(Long uci) {
        this.uci = uci;
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

    public Long getNrCi() {
        return nrCi;
    }

    public void setNrCi(Long nrCi) {
        this.nrCi = nrCi;
    }

    public TypeOfShape getTypeOfShape() {
        return typeOfShape;
    }

    public void setTypeOfShape(TypeOfShape typeOfShape) {
        this.typeOfShape = typeOfShape;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(Double uncertainty) {
        this.uncertainty = uncertainty;
    }

    public Double getUncertaintySemiMajorAxis() {
        return uncertaintySemiMajorAxis;
    }

    public void setUncertaintySemiMajorAxis(Double uncertaintySemiMajorAxis) {
        this.uncertaintySemiMajorAxis = uncertaintySemiMajorAxis;
    }

    public Double getUncertaintySemiMinorAxis() {
        return uncertaintySemiMinorAxis;
    }

    public void setUncertaintySemiMinorAxis(Double uncertaintySemiMinorAxis) {
        this.uncertaintySemiMinorAxis = uncertaintySemiMinorAxis;
    }

    public Double getAngleOfMajorAxis() {
        if (orientationMajorAxis != null)
            angleOfMajorAxis = orientationMajorAxis;
        return angleOfMajorAxis;
    }

    public void setAngleOfMajorAxis(Double angleOfMajorAxis) {
        this.angleOfMajorAxis = angleOfMajorAxis;
    }

    public Double getOrientationMajorAxis() {
        return orientationMajorAxis;
    }

    public void setOrientationMajorAxis(Double orientationMajorAxis) {
        this.orientationMajorAxis = orientationMajorAxis;
    }

    public Double getUncertaintyAltitude() {
        return uncertaintyAltitude;
    }

    public void setUncertaintyAltitude(Double uncertaintyAltitude) {
        this.uncertaintyAltitude = uncertaintyAltitude;
    }

    public Integer getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(Integer innerRadius) {
        this.innerRadius = innerRadius;
    }

    public Double getUncertaintyInnerRadius() {
        return uncertaintyInnerRadius;
    }

    public void setUncertaintyInnerRadius(Double uncertaintyInnerRadius) {
        this.uncertaintyInnerRadius = uncertaintyInnerRadius;
    }

    public Double getOffsetAngle() {
        return offsetAngle;
    }

    public void setOffsetAngle(Double offsetAngle) {
        this.offsetAngle = offsetAngle;
    }

    public Double getIncludedAngle() {
        return includedAngle;
    }

    public void setIncludedAngle(Double includedAngle) {
        this.includedAngle = includedAngle;
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Integer getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(Integer horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public Integer getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(Integer verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public Integer getUncertaintyHorizontalSpeed() {
        return uncertaintyHorizontalSpeed;
    }

    public void setUncertaintyHorizontalSpeed(Integer uncertaintyHorizontalSpeed) {
        this.uncertaintyHorizontalSpeed = uncertaintyHorizontalSpeed;
    }

    public Integer getUncertaintyVerticalSpeed() {
        return uncertaintyVerticalSpeed;
    }

    public void setUncertaintyVerticalSpeed(Integer uncertaintyVerticalSpeed) {
        this.uncertaintyVerticalSpeed = uncertaintyVerticalSpeed;
    }

    public Integer getBearing() {
        return bearing;
    }

    public void setBearing(Integer bearing) {
        this.bearing = bearing;
    }

    public String getVelocityType() {
        return velocityType;
    }

    public void setVelocityType(String velocityType) {
        this.velocityType = velocityType;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public Long getTimingAdvance() {
        return timingAdvance;
    }

    public void setTimingAdvance(Long timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    public CellGlobalIdOrServiceAreaIdOrLAI getCellGlobalIdOrServiceAreaIdOrLAI() {
        return cellGlobalIdOrServiceAreaIdOrLAI;
    }

    public void setCellGlobalIdOrServiceAreaIdOrLAI(CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI) {
        this.cellGlobalIdOrServiceAreaIdOrLAI = cellGlobalIdOrServiceAreaIdOrLAI;
    }

    public EUTRANCGI getLteCGI() {
        return lteCGI;
    }

    public void setLteCGI(EUTRANCGI lteCGI) {
        this.lteCGI = lteCGI;
        try {
            this.mcc = lteCGI.getMCC();
            this.mnc = lteCGI.getMNC();
            this.eci = lteCGI.getEci();
            this.enbId = lteCGI.getENodeBId();
        } catch (MAPException e) {
            logger.error(e.getMessage());
        }
    }

    public NRCellGlobalId getNrCellGlobalId() {
        return nrCellGlobalId;
    }

    public void setNrCellGlobalId(NRCellGlobalId nrCellGlobalId) {
        this.nrCellGlobalId = nrCellGlobalId;
        try {
            this.mcc = nrCellGlobalId.getMCC();
            this.mnc = nrCellGlobalId.getMNC();
            this.nrCi = nrCellGlobalId.getNCI();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public PosMethod getPosMethod() {
        return posMethod;
    }

    public void setPosMethod(PosMethod posMethod) {
        this.posMethod = posMethod;
    }

    public TriggerParams getTriggerParams() {
        return triggerParams;
    }

    public void setTriggerParams(TriggerParams triggerParams) {
        this.triggerParams = triggerParams;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Boolean isReport() {
        return isReport;
    }

    public void setReport(Boolean report) {
        isReport = report;
    }

    public Integer getSuplReportReferenceNumber() {
        return suplReportReferenceNumber;
    }

    public void setSuplReportReferenceNumber(Integer suplReportReferenceNumber) {
        this.suplReportReferenceNumber = suplReportReferenceNumber;
    }

    public SessionID getSessionID() {
        return sessionID;
    }

    public void setSessionID(SessionID sessionID) {
        this.sessionID = sessionID;
    }

    public Boolean getTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        isTimeOut = timeOut;
    }

    @Override
    public String toString() {
        return "SuplResponseHelperForMLP{" +
            "msisdn='" + msisdn + '\'' +
            ", imsi='" + imsi + '\'' +
            ", mcc=" + mcc +
            ", mnc=" + mnc +
            ", lac=" + lac +
            ", ci=" + ci +
            ", sac=" + sac +
            ", rac=" + rac +
            ", tac=" + tac +
            ", uci=" + uci +
            ", eci=" + eci +
            ", enbId=" + enbId +
            ", nrCi=" + nrCi +
            ", timingAdvance=" + timingAdvance +
            ", typeOfShape=" + typeOfShape +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", altitude=" + altitude +
            ", uncertainty=" + uncertainty +
            ", uncertaintySemiMajorAxis=" + uncertaintySemiMajorAxis +
            ", uncertaintySemiMinorAxis=" + uncertaintySemiMinorAxis +
            ", angleOfMajorAxis=" + angleOfMajorAxis +
            ", orientationMajorAxis=" + orientationMajorAxis +
            ", uncertaintyAltitude=" + uncertaintyAltitude +
            ", uncertaintyInnerRadius=" + uncertaintyInnerRadius +
            ", offsetAngle=" + offsetAngle +
            ", includedAngle=" + includedAngle +
            ", radius=" + radius +
            ", confidence=" + confidence +
            ", innerRadius=" + innerRadius +
            ", transactionId=" + transactionId +
            ", cellGlobalIdOrServiceAreaIdOrLAI=" + cellGlobalIdOrServiceAreaIdOrLAI +
            ", lteCGI=" + lteCGI +
            ", nrCellGlobalId=" + nrCellGlobalId +
            ", posMethod=" + posMethod +
            ", triggerParams=" + triggerParams +
            ", isReport=" + isReport +
            ", sessionId=" + suplReportReferenceNumber +
            ", sessionID=" + sessionID +
            '}';
    }
}
