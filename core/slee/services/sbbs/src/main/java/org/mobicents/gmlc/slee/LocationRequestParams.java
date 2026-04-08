package org.mobicents.gmlc.slee;

import net.java.slee.resource.diameter.slg.events.avp.ReportingPLMNListAvp;
import org.mobicents.gmlc.slee.mlp.MLPLocationRequest;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class LocationRequestParams implements Serializable {

    private static final long serialVersionUID = -1L;

    public String operation, targetingMSISDN, targetingIMSI, targetingIMEI, domainType, locationInfoEps, activeLocation, atiExtraInfoRequested, locationInfo5gs, ratTypeRequested;
    protected String pslOccurrenceInfo, pslAreaType, pslAreaId, pslLocationEstimateType, pslDeferredLocationEventType,
        pslLcsPriority, pslVerticalCoordinateRequest, pslResponseTimeCategory, slrCallbackUrl, psiServiceType, psiOnlyImsi, psiOnlyNnn;
    protected String pslClientExternalID, pslClientName, pslClientDialedByMS, pslRequestorIdString, pslLcsCodeword;
    protected Boolean pslVelocityRequest;
    Integer pslClientInternalID, pslClientFormatIndicator, pslRequestorFormatIndicator;
    protected Integer pslLcsClientType, pslLcsHorizontalAccuracy, pslLcsVerticalAccuracy, pslLcsServiceTypeID, pslIntervalTime, pslReportingAmount, pslReportingInterval, pslQoSClass;
    protected String pslPLMNIdList;
    protected Integer pslVisitedPLMNIdRAN, pslPeriodicLocationSupportIndicator, pslPrioritizedListIndicator;

    public String httpRespType, curlUser, curlToken;
    protected Integer pslLcsReferenceNumber, translationType;

    protected String plrLcsNameString, plrLcsRequestorIdString, plrLcsCodeword, plrServiceSelection, plrAreaIdentification,
        plrAdditionalAreaIdentification, plrVisitedPLMNIdList,
        udrMsisdn, udrImsPublicId;
    protected Long plrLcsPriority, plrHorizontalAccuracy, plrVerticalAccuracy, plrLcsSupportedGadShapes, plrLcsServiceTypeId, plrDeferredLocationType,
        plrFlags, plrAreaType, plrAdditionalAreaType, plrAreaEventIntervalTime, plrAreaEventSamplingInterval, plrAreaEventMaxInterval,
        plrAreaEventReportingDuration, plrAreaEventRepLocRequirements, plrPeriodicLDRReportingAmount, plrPeriodicLDRReportingInterval, plrMotionEventLinearDistance,
        plrMotionEventIntervalTime, plrMotionEventMaximumInterval, plrMotionEventSamplingInterval, plrMotionEvenReportingDuration, plrMotionEvenReportingLocationRequirements;
    protected Integer plrSlgLocationType, plrLcsFormatInd, plrLcsClientType, plrLcsRequestorFormatIndicator, plrLcsReferenceNumber, plrQoSClass,
        plrVerticalRequested, plrResponseTime, plrVelocityRequested, plrPrivacyCheckNonSession, plrPrivacyCheckSession,
        plrAreaEventOccurrenceInfo, plrPeriodicLocationSupportIndicator, plrPrioritizedListIndicator, plrMotionEventOccurrenceInfo;
    protected ReportingPLMNListAvp reportingPLMNListAvp;
    protected Integer lteLcsReferenceNumber;
    protected String lrrCallbackUrl;
    protected String hssDiameterHost, hssDiameterRealm;

    public MLPLocationRequest.ReportingService reportingService;
    protected Integer suplTransactionId;
    public String suplAgentCallbackUrl;
    public Long suplHorizontalAccuracy, suplVerticalAccuracy, suplMaximumLocationAge, suplDelay, suplResponseTime;
    public Integer suplAreaEventType;
    public Double suplAreaEventLatitude, suplAreaEventLongitude, suplAreaEventSemiMajor, suplAreaEventSemiMinor, suplAreaEventAngle;
    public Boolean suplLocationEstimateRequested;
    public Integer suplMinimumIntervalTime, suplMaximumNumberOfReports, suplAreaEventRadius;
    public Long suplStartTime, suplStopTime;
    public String suplGeographicTargetArea;
    protected Boolean suplAreaEventRepeatedReporting;
    protected String suplAreaIdSetType, suplAreaId;
    public Long suplReportingInterval, suplReportingAmount;

    protected String numberFormatException;

    public LocationRequestParams() {
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTargetingMSISDN() {
        return targetingMSISDN;
    }

    public void setTargetingMSISDN(String targetingMSISDN) {
        this.targetingMSISDN = targetingMSISDN;
    }

    public String getTargetingIMSI() {
        return targetingIMSI;
    }

    public void setTargetingIMSI(String targetingIMSI) {
        this.targetingIMSI = targetingIMSI;
    }

    public String getTargetingIMEI() {
        return targetingIMEI;
    }

    public void setTargetingIMEI(String targetingIMEI) {
        this.targetingIMEI = targetingIMEI;
    }

    public Integer getTranslationType() {
        return translationType;
    }

    public void setTranslationType(Integer translationType) {
        this.translationType = translationType;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getLocationInfoEps() {
        return locationInfoEps;
    }

    public void setLocationInfoEps(String locationInfoEps) {
        this.locationInfoEps = locationInfoEps;
    }

    public String getActiveLocation() {
        return activeLocation;
    }

    public void setActiveLocation(String activeLocation) {
        this.activeLocation = activeLocation;
    }

    public String getAtiExtraInfoRequested() {
        return atiExtraInfoRequested;
    }

    public void setAtiExtraInfoRequested(String atiExtraInfoRequested) {
        this.atiExtraInfoRequested = atiExtraInfoRequested;
    }

    public String getLocationInfo5gs() {
        return locationInfo5gs;
    }

    public void setLocationInfo5gs(String locationInfo5gs) {
        this.locationInfo5gs = locationInfo5gs;
    }

    public String getRatTypeRequested() {
        return ratTypeRequested;
    }

    public void setRatTypeRequested(String ratTypeRequested) {
        this.ratTypeRequested = ratTypeRequested;
    }

    public String getPslOccurrenceInfo() {
        return pslOccurrenceInfo;
    }

    public void setPslOccurrenceInfo(String pslOccurrenceInfo) {
        this.pslOccurrenceInfo = pslOccurrenceInfo;
    }

    public String getPslAreaType() {
        return pslAreaType;
    }

    public void setPslAreaType(String pslAreaType) {
        this.pslAreaType = pslAreaType;
    }

    public String getPslAreaId() {
        return pslAreaId;
    }

    public void setPslAreaId(String pslAreaId) {
        this.pslAreaId = pslAreaId;
    }

    public String getPslLocationEstimateType() {
        return pslLocationEstimateType;
    }

    public void setPslLocationEstimateType(String pslLocationEstimateType) {
        this.pslLocationEstimateType = pslLocationEstimateType;
    }

    public String getPslDeferredLocationEventType() {
        return pslDeferredLocationEventType;
    }

    public void setPslDeferredLocationEventType(String pslDeferredLocationEventType) {
        this.pslDeferredLocationEventType = pslDeferredLocationEventType;
    }

    public String getPslLcsPriority() {
        return pslLcsPriority;
    }

    public void setPslLcsPriority(String pslLcsPriority) {
        this.pslLcsPriority = pslLcsPriority;
    }

    public String getPslVerticalCoordinateRequest() {
        return pslVerticalCoordinateRequest;
    }

    public void setPslVerticalCoordinateRequest(String pslVerticalCoordinateRequest) {
        this.pslVerticalCoordinateRequest = pslVerticalCoordinateRequest;
    }

    public String getPslResponseTimeCategory() {
        return pslResponseTimeCategory;
    }

    public void setPslResponseTimeCategory(String pslResponseTimeCategory) {
        this.pslResponseTimeCategory = pslResponseTimeCategory;
    }

    public Boolean getPslVelocityRequest() {
        return pslVelocityRequest;
    }

    public void setPslVelocityRequest(Boolean pslVelocityRequest) {
        this.pslVelocityRequest = pslVelocityRequest;
    }

    public Integer getPslQoSClass() {
        return pslQoSClass;
    }

    public void setPslQoSClass(Integer pslQoSClass) {
        this.pslQoSClass = pslQoSClass;
    }

    public String getPslLcsCodeword() {
        return pslLcsCodeword;
    }

    public void setPslLcsCodeword(String pslLcsCodeword) {
        this.pslLcsCodeword = pslLcsCodeword;
    }

    public String getSlrCallbackUrl() {
        return slrCallbackUrl;
    }

    public void setSlrCallbackUrl(String slrCallbackUrl) {
        this.slrCallbackUrl = slrCallbackUrl;
    }

    public String getPsiServiceType() {
        return psiServiceType;
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

    public Integer getPslLcsClientType() {
        return pslLcsClientType;
    }

    public void setPslLcsClientType(Integer pslLcsClientType) {
        this.pslLcsClientType = pslLcsClientType;
    }

    public String getPslClientExternalID() {
        return pslClientExternalID;
    }

    public void setPslClientExternalID(String pslClientExternalID) {
        this.pslClientExternalID = pslClientExternalID;
    }

    public String getPslClientName() {
        return pslClientName;
    }

    public void setPslClientName(String pslClientName) {
        this.pslClientName = pslClientName;
    }

    public Integer getPslClientFormatIndicator() {
        return pslClientFormatIndicator;
    }

    public void setPslClientFormatIndicator(Integer pslClientFormatIndicator) {
        this.pslClientFormatIndicator = pslClientFormatIndicator;
    }

    public String getPslClientDialedByMS() {
        return pslClientDialedByMS;
    }

    public void setPslClientDialedByMS(String pslClientDialedByMS) {
        this.pslClientDialedByMS = pslClientDialedByMS;
    }

    public String getPslRequestorIdString() {
        return pslRequestorIdString;
    }

    public void setPslRequestorIdString(String pslRequestorIdString) {
        this.pslRequestorIdString = pslRequestorIdString;
    }

    public Integer getPslClientInternalID() {
        return pslClientInternalID;
    }

    public void setPslClientInternalID(Integer pslClientInternalID) {
        this.pslClientInternalID = pslClientInternalID;
    }

    public Integer getPslRequestorFormatIndicator() {
        return pslRequestorFormatIndicator;
    }

    public void setPslRequestorFormatIndicator(Integer pslRequestorFormatIndicator) {
        this.pslRequestorFormatIndicator = pslRequestorFormatIndicator;
    }

    public Integer getPslLcsHorizontalAccuracy() {
        return pslLcsHorizontalAccuracy;
    }

    public void setPslLcsHorizontalAccuracy(Integer pslLcsHorizontalAccuracy) {
        this.pslLcsHorizontalAccuracy = pslLcsHorizontalAccuracy;
    }

    public Integer getPslLcsVerticalAccuracy() {
        return pslLcsVerticalAccuracy;
    }

    public void setPslLcsVerticalAccuracy(Integer pslLcsVerticalAccuracy) {
        this.pslLcsVerticalAccuracy = pslLcsVerticalAccuracy;
    }

    public Integer getPslLcsServiceTypeID() {
        return pslLcsServiceTypeID;
    }

    public void setPslLcsServiceTypeID(Integer pslLcsServiceTypeID) {
        this.pslLcsServiceTypeID = pslLcsServiceTypeID;
    }

    public Integer getPslIntervalTime() {
        return pslIntervalTime;
    }

    public void setPslIntervalTime(Integer pslIntervalTime) {
        this.pslIntervalTime = pslIntervalTime;
    }

    public Integer getPslReportingAmount() {
        return pslReportingAmount;
    }

    public void setPslReportingAmount(Integer pslReportingAmount) {
        this.pslReportingAmount = pslReportingAmount;
    }

    public Integer getPslReportingInterval() {
        return pslReportingInterval;
    }

    public void setPslReportingInterval(Integer pslReportingInterval) {
        this.pslReportingInterval = pslReportingInterval;
    }

    public String getPslPLMNIdList() {
        return pslPLMNIdList;
    }

    public void setPslPLMNIdList(String pslPLMNIdList) {
        this.pslPLMNIdList = pslPLMNIdList;
    }

    public Integer getPslVisitedPLMNIdRAN() {
        return pslVisitedPLMNIdRAN;
    }

    public void setPslVisitedPLMNIdRAN(Integer pslVisitedPLMNIdRAN) {
        this.pslVisitedPLMNIdRAN = pslVisitedPLMNIdRAN;
    }

    public Integer getPslPeriodicLocationSupportIndicator() {
        return pslPeriodicLocationSupportIndicator;
    }

    public void setPslPeriodicLocationSupportIndicator(Integer pslPeriodicLocationSupportIndicator) {
        this.pslPeriodicLocationSupportIndicator = pslPeriodicLocationSupportIndicator;
    }

    public Integer getPslPrioritizedListIndicator() {
        return pslPrioritizedListIndicator;
    }

    public void setPslPrioritizedListIndicator(Integer pslPrioritizedListIndicator) {
        this.pslPrioritizedListIndicator = pslPrioritizedListIndicator;
    }

    public String getHttpRespType() {
        return httpRespType;
    }

    public void setHttpRespType(String httpRespType) {
        this.httpRespType = httpRespType;
    }

    public String getCurlUser() {
        return curlUser;
    }

    public void setCurlUser(String curlUser) {
        this.curlUser = curlUser;
    }

    public String getCurlToken() {
        return curlToken;
    }

    public void setCurlToken(String curlToken) {
        this.curlToken = curlToken;
    }

    public Integer getPslLcsReferenceNumber() {
        return pslLcsReferenceNumber;
    }

    public void setPslLcsReferenceNumber(Integer pslLcsReferenceNumber) {
        this.pslLcsReferenceNumber = pslLcsReferenceNumber;
    }

    public String getPlrLcsNameString() {
        return plrLcsNameString;
    }

    public void setPlrLcsNameString(String plrLcsNameString) {
        this.plrLcsNameString = plrLcsNameString;
    }

    public String getPlrLcsRequestorIdString() {
        return plrLcsRequestorIdString;
    }

    public void setPlrLcsRequestorIdString(String plrLcsRequestorIdString) {
        this.plrLcsRequestorIdString = plrLcsRequestorIdString;
    }

    public String getPlrLcsCodeword() {
        return plrLcsCodeword;
    }

    public void setPlrLcsCodeword(String plrLcsCodeword) {
        this.plrLcsCodeword = plrLcsCodeword;
    }

    public String getPlrServiceSelection() {
        return plrServiceSelection;
    }

    public void setPlrServiceSelection(String plrServiceSelection) {
        this.plrServiceSelection = plrServiceSelection;
    }

    public String getPlrAreaIdentification() {
        return plrAreaIdentification;
    }

    public void setPlrAreaIdentification(String plrAreaIdentification) {
        this.plrAreaIdentification = plrAreaIdentification;
    }

    public String getPlrAdditionalAreaIdentification() {
        return plrAdditionalAreaIdentification;
    }

    public void setPlrAdditionalAreaIdentification(String plrAdditionalAreaIdentification) {
        this.plrAdditionalAreaIdentification = plrAdditionalAreaIdentification;
    }

    public String getPlrVisitedPLMNIdList() {
        return plrVisitedPLMNIdList;
    }

    public void setPlrVisitedPLMNIdList(String plrVisitedPLMNIdList) {
        this.plrVisitedPLMNIdList = plrVisitedPLMNIdList;
    }

    public String getUdrMsisdn() {
        return udrMsisdn;
    }

    public void setUdrMsisdn(String udrMsisdn) {
        this.udrMsisdn = udrMsisdn;
    }

    public String getUdrImsPublicId() {
        return udrImsPublicId;
    }

    public void setUdrImsPublicId(String udrImsPublicId) {
        this.udrImsPublicId = udrImsPublicId;
    }

    public Long getPlrLcsPriority() {
        return plrLcsPriority;
    }

    public void setPlrLcsPriority(Long plrLcsPriority) {
        this.plrLcsPriority = plrLcsPriority;
    }

    public Long getPlrHorizontalAccuracy() {
        return plrHorizontalAccuracy;
    }

    public void setPlrHorizontalAccuracy(Long plrHorizontalAccuracy) {
        this.plrHorizontalAccuracy = plrHorizontalAccuracy;
    }

    public Long getPlrVerticalAccuracy() {
        return plrVerticalAccuracy;
    }

    public void setPlrVerticalAccuracy(Long plrVerticalAccuracy) {
        this.plrVerticalAccuracy = plrVerticalAccuracy;
    }

    public Long getPlrLcsSupportedGadShapes() {
        return plrLcsSupportedGadShapes;
    }

    public void setPlrLcsSupportedGadShapes(Long plrLcsSupportedGadShapes) {
        this.plrLcsSupportedGadShapes = plrLcsSupportedGadShapes;
    }

    public Long getPlrLcsServiceTypeId() {
        return plrLcsServiceTypeId;
    }

    public void setPlrLcsServiceTypeId(Long plrLcsServiceTypeId) {
        this.plrLcsServiceTypeId = plrLcsServiceTypeId;
    }

    public Long getPlrDeferredLocationType() {
        return plrDeferredLocationType;
    }

    public void setPlrDeferredLocationType(Long plrDeferredLocationType) {
        this.plrDeferredLocationType = plrDeferredLocationType;
    }

    public Long getPlrFlags() {
        return plrFlags;
    }

    public void setPlrFlags(Long plrFlags) {
        this.plrFlags = plrFlags;
    }

    public Long getPlrAreaType() {
        return plrAreaType;
    }

    public void setPlrAreaType(Long plrAreaType) {
        this.plrAreaType = plrAreaType;
    }

    public Long getPlrAdditionalAreaType() {
        return plrAdditionalAreaType;
    }

    public void setPlrAdditionalAreaType(Long plrAdditionalAreaType) {
        this.plrAdditionalAreaType = plrAdditionalAreaType;
    }

    public Long getPlrAreaEventIntervalTime() {
        return plrAreaEventIntervalTime;
    }

    public void setPlrAreaEventIntervalTime(Long plrAreaEventIntervalTime) {
        this.plrAreaEventIntervalTime = plrAreaEventIntervalTime;
    }

    public Long getPlrAreaEventSamplingInterval() {
        return plrAreaEventSamplingInterval;
    }

    public void setPlrAreaEventSamplingInterval(Long plrAreaEventSamplingInterval) {
        this.plrAreaEventSamplingInterval = plrAreaEventSamplingInterval;
    }

    public Long getPlrAreaEventMaxInterval() {
        return plrAreaEventMaxInterval;
    }

    public void setPlrAreaEventMaxInterval(Long plrAreaEventMaxInterval) {
        this.plrAreaEventMaxInterval = plrAreaEventMaxInterval;
    }

    public Long getPlrAreaEventReportingDuration() {
        return plrAreaEventReportingDuration;
    }

    public void setPlrAreaEventReportingDuration(Long plrAreaEventReportingDuration) {
        this.plrAreaEventReportingDuration = plrAreaEventReportingDuration;
    }

    public Long getPlrAreaEventRepLocRequirements() {
        return plrAreaEventRepLocRequirements;
    }

    public void setPlrAreaEventRepLocRequirements(Long plrAreaEventRepLocRequirements) {
        this.plrAreaEventRepLocRequirements = plrAreaEventRepLocRequirements;
    }

    public Long getPlrPeriodicLDRReportingAmount() {
        return plrPeriodicLDRReportingAmount;
    }

    public void setPlrPeriodicLDRReportingAmount(Long plrPeriodicLDRReportingAmount) {
        this.plrPeriodicLDRReportingAmount = plrPeriodicLDRReportingAmount;
    }

    public Long getPlrPeriodicLDRReportingInterval() {
        return plrPeriodicLDRReportingInterval;
    }

    public void setPlrPeriodicLDRReportingInterval(Long plrPeriodicLDRReportingInterval) {
        this.plrPeriodicLDRReportingInterval = plrPeriodicLDRReportingInterval;
    }

    public Long getPlrMotionEventLinearDistance() {
        return plrMotionEventLinearDistance;
    }

    public void setPlrMotionEventLinearDistance(Long plrMotionEventLinearDistance) {
        this.plrMotionEventLinearDistance = plrMotionEventLinearDistance;
    }

    public Long getPlrMotionEventIntervalTime() {
        return plrMotionEventIntervalTime;
    }

    public void setPlrMotionEventIntervalTime(Long plrMotionEventIntervalTime) {
        this.plrMotionEventIntervalTime = plrMotionEventIntervalTime;
    }

    public Long getPlrMotionEventMaximumInterval() {
        return plrMotionEventMaximumInterval;
    }

    public void setPlrMotionEventMaximumInterval(Long plrMotionEventMaximumInterval) {
        this.plrMotionEventMaximumInterval = plrMotionEventMaximumInterval;
    }

    public Long getPlrMotionEventSamplingInterval() {
        return plrMotionEventSamplingInterval;
    }

    public void setPlrMotionEventSamplingInterval(Long plrMotionEventSamplingInterval) {
        this.plrMotionEventSamplingInterval = plrMotionEventSamplingInterval;
    }

    public Long getPlrMotionEvenReportingDuration() {
        return plrMotionEvenReportingDuration;
    }

    public void setPlrMotionEvenReportingDuration(Long plrMotionEvenReportingDuration) {
        this.plrMotionEvenReportingDuration = plrMotionEvenReportingDuration;
    }

    public Long getPlrMotionEvenReportingLocationRequirements() {
        return plrMotionEvenReportingLocationRequirements;
    }

    public void setPlrMotionEvenReportingLocationRequirements(Long plrMotionEvenReportingLocationRequirements) {
        this.plrMotionEvenReportingLocationRequirements = plrMotionEvenReportingLocationRequirements;
    }

    public Integer getPlrSlgLocationType() {
        return plrSlgLocationType;
    }

    public void setPlrSlgLocationType(Integer plrSlgLocationType) {
        this.plrSlgLocationType = plrSlgLocationType;
    }

    public Integer getPlrLcsFormatInd() {
        return plrLcsFormatInd;
    }

    public void setPlrLcsFormatInd(Integer plrLcsFormatInd) {
        this.plrLcsFormatInd = plrLcsFormatInd;
    }

    public Integer getPlrLcsClientType() {
        return plrLcsClientType;
    }

    public void setPlrLcsClientType(Integer plrLcsClientType) {
        this.plrLcsClientType = plrLcsClientType;
    }

    public Integer getPlrLcsRequestorFormatIndicator() {
        return plrLcsRequestorFormatIndicator;
    }

    public void setPlrLcsRequestorFormatIndicator(Integer plrLcsRequestorFormatIndicator) {
        this.plrLcsRequestorFormatIndicator = plrLcsRequestorFormatIndicator;
    }

    public Integer getPlrLcsReferenceNumber() {
        return plrLcsReferenceNumber;
    }

    public void setPlrLcsReferenceNumber(Integer plrLcsReferenceNumber) {
        this.plrLcsReferenceNumber = plrLcsReferenceNumber;
    }

    public Integer getPlrQoSClass() {
        return plrQoSClass;
    }

    public void setPlrQoSClass(Integer plrQoSClass) {
        this.plrQoSClass = plrQoSClass;
    }

    public Integer getPlrVerticalRequested() {
        return plrVerticalRequested;
    }

    public void setPlrVerticalRequested(Integer plrVerticalRequested) {
        this.plrVerticalRequested = plrVerticalRequested;
    }

    public Integer getPlrResponseTime() {
        return plrResponseTime;
    }

    public void setPlrResponseTime(Integer plrResponseTime) {
        this.plrResponseTime = plrResponseTime;
    }

    public Integer getPlrVelocityRequested() {
        return plrVelocityRequested;
    }

    public void setPlrVelocityRequested(Integer plrVelocityRequested) {
        this.plrVelocityRequested = plrVelocityRequested;
    }

    public Integer getPlrPrivacyCheckNonSession() {
        return plrPrivacyCheckNonSession;
    }

    public void setPlrPrivacyCheckNonSession(Integer plrPrivacyCheckNonSession) {
        this.plrPrivacyCheckNonSession = plrPrivacyCheckNonSession;
    }

    public Integer getPlrPrivacyCheckSession() {
        return plrPrivacyCheckSession;
    }

    public void setPlrPrivacyCheckSession(Integer plrPrivacyCheckSession) {
        this.plrPrivacyCheckSession = plrPrivacyCheckSession;
    }

    public Integer getPlrAreaEventOccurrenceInfo() {
        return plrAreaEventOccurrenceInfo;
    }

    public void setPlrAreaEventOccurrenceInfo(Integer plrAreaEventOccurrenceInfo) {
        this.plrAreaEventOccurrenceInfo = plrAreaEventOccurrenceInfo;
    }

    public Integer getPlrPeriodicLocationSupportIndicator() {
        return plrPeriodicLocationSupportIndicator;
    }

    public void setPlrPeriodicLocationSupportIndicator(Integer plrPeriodicLocationSupportIndicator) {
        this.plrPeriodicLocationSupportIndicator = plrPeriodicLocationSupportIndicator;
    }

    public Integer getPlrPrioritizedListIndicator() {
        return plrPrioritizedListIndicator;
    }

    public void setPlrPrioritizedListIndicator(Integer plrPrioritizedListIndicator) {
        this.plrPrioritizedListIndicator = plrPrioritizedListIndicator;
    }

    public Integer getPlrMotionEventOccurrenceInfo() {
        return plrMotionEventOccurrenceInfo;
    }

    public void setPlrMotionEventOccurrenceInfo(Integer plrMotionEventOccurrenceInfo) {
        this.plrMotionEventOccurrenceInfo = plrMotionEventOccurrenceInfo;
    }

    public ReportingPLMNListAvp getReportingPLMNListAvp() {
        return reportingPLMNListAvp;
    }

    public void setReportingPLMNListAvp(ReportingPLMNListAvp reportingPLMNListAvp) {
        this.reportingPLMNListAvp = reportingPLMNListAvp;
    }

    public Integer getLteLcsReferenceNumber() {
        return lteLcsReferenceNumber;
    }

    public void setLteLcsReferenceNumber(Integer lteLcsReferenceNumber) {
        this.lteLcsReferenceNumber = lteLcsReferenceNumber;
    }

    public String getLrrCallbackUrl() {
        return lrrCallbackUrl;
    }

    public void setLrrCallbackUrl(String lrrCallbackUrl) {
        this.lrrCallbackUrl = lrrCallbackUrl;
    }

    public MLPLocationRequest.ReportingService getReportingService() {
        return reportingService;
    }

    public void setReportingService(MLPLocationRequest.ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    public Integer getSuplTransactionId() {
        return suplTransactionId;
    }

    public void setSuplTransactionId(Integer suplTransactionId) {
        this.suplTransactionId = suplTransactionId;
    }

    public String getSuplAgentCallbackUrl() {
        return suplAgentCallbackUrl;
    }

    public void setSuplAgentCallbackUrl(String suplAgentCallbackUrl) {
        this.suplAgentCallbackUrl = suplAgentCallbackUrl;
    }

    public String getNumberFormatException() {
        return numberFormatException;
    }

    public void setNumberFormatException(String numberFormatException) {
        this.numberFormatException = numberFormatException;
    }

    public String getHssDiameterHost() {
        return hssDiameterHost;
    }

    public void setHssDiameterHost(String hssDiameterHost) {
        this.hssDiameterHost = hssDiameterHost;
    }

    public String getHssDiameterRealm() {
        return hssDiameterRealm;
    }

    public void setHssDiameterRealm(String hssDiameterRealm) {
        this.hssDiameterRealm = hssDiameterRealm;
    }

    public Long getSuplHorizontalAccuracy() {
        return suplHorizontalAccuracy;
    }

    public void setSuplHorizontalAccuracy(Long suplHorizontalAccuracy) {
        this.suplHorizontalAccuracy = suplHorizontalAccuracy;
    }

    public Long getSuplVerticalAccuracy() {
        return suplVerticalAccuracy;
    }

    public void setSuplVerticalAccuracy(Long suplVerticalAccuracy) {
        this.suplVerticalAccuracy = suplVerticalAccuracy;
    }

    public Long getSuplMaximumLocationAge() {
        return suplMaximumLocationAge;
    }

    public void setSuplMaximumLocationAge(Long suplMaximumLocationAge) {
        this.suplMaximumLocationAge = suplMaximumLocationAge;
    }

    public Long getSuplDelay() {
        return suplDelay;
    }

    public void setSuplDelay(Long suplDelay) {
        this.suplDelay = suplDelay;
    }

    public Long getSuplResponseTime() {
        return suplResponseTime;
    }

    public void setSuplResponseTime(Long suplResponseTime) {
        this.suplResponseTime = suplResponseTime;
    }

    public Integer getSuplAreaEventType() {
        return suplAreaEventType;
    }

    public void setSuplAreaEventType(Integer suplAreaEventType) {
        this.suplAreaEventType = suplAreaEventType;
    }

    public String getSuplAreaId() {
        return suplAreaId;
    }

    public void setSuplAreaId(String suplAreaId) {
        this.suplAreaId = suplAreaId;
    }

    public Double getSuplAreaEventLatitude() {
        return suplAreaEventLatitude;
    }

    public void setSuplAreaEventLatitude(Double suplAreaEventLatitude) {
        this.suplAreaEventLatitude = suplAreaEventLatitude;
    }

    public Double getSuplAreaEventLongitude() {
        return suplAreaEventLongitude;
    }

    public void setSuplAreaEventLongitude(Double suplAreaEventLongitude) {
        this.suplAreaEventLongitude = suplAreaEventLongitude;
    }

    public Double getSuplAreaEventSemiMajor() {
        return suplAreaEventSemiMajor;
    }

    public void setSuplAreaEventSemiMajor(Double suplAreaEventSemiMajor) {
        this.suplAreaEventSemiMajor = suplAreaEventSemiMajor;
    }

    public Double getSuplAreaEventSemiMinor() {
        return suplAreaEventSemiMinor;
    }

    public void setSuplAreaEventSemiMinor(Double suplAreaEventSemiMinor) {
        this.suplAreaEventSemiMinor = suplAreaEventSemiMinor;
    }

    public Double getSuplAreaEventAngle() {
        return suplAreaEventAngle;
    }

    public void setSuplAreaEventAngle(Double suplAreaEventAngle) {
        this.suplAreaEventAngle = suplAreaEventAngle;
    }

    public Boolean getSuplLocationEstimateRequested() {
        return suplLocationEstimateRequested;
    }

    public void setSuplLocationEstimateRequested(Boolean suplLocationEstimateRequested) {
        this.suplLocationEstimateRequested = suplLocationEstimateRequested;
    }

    public Integer getSuplMinimumIntervalTime() {
        return suplMinimumIntervalTime;
    }

    public void setSuplMinimumIntervalTime(Integer suplMinimumIntervalTime) {
        this.suplMinimumIntervalTime = suplMinimumIntervalTime;
    }

    public Integer getSuplMaximumNumberOfReports() {
        return suplMaximumNumberOfReports;
    }

    public void setSuplMaximumNumberOfReports(Integer suplMaximumNumberOfReports) {
        this.suplMaximumNumberOfReports = suplMaximumNumberOfReports;
    }

    public Long getSuplStartTime() {
        return suplStartTime;
    }

    public void setSuplStartTime(Long suplStartTime) {
        this.suplStartTime = suplStartTime;
    }

    public Long getSuplStopTime() {
        return suplStopTime;
    }

    public void setSuplStopTime(Long suplStopTime) {
        this.suplStopTime = suplStopTime;
    }

    public Integer getSuplAreaEventRadius() {
        return suplAreaEventRadius;
    }

    public void setSuplAreaEventRadius(Integer suplAreaEventRadius) {
        this.suplAreaEventRadius = suplAreaEventRadius;
    }

    public String getSuplGeographicTargetArea() {
        return suplGeographicTargetArea;
    }

    public void setSuplGeographicTargetArea(String suplGeographicTargetArea) {
        this.suplGeographicTargetArea = suplGeographicTargetArea;
    }

    public Boolean getSuplAreaEventRepeatedReporting() {
        return suplAreaEventRepeatedReporting;
    }

    public void setSuplAreaEventRepeatedReporting(Boolean suplAreaEventRepeatedReporting) {
        this.suplAreaEventRepeatedReporting = suplAreaEventRepeatedReporting;
    }

    public String getSuplAreaIdSetType() {
        return suplAreaIdSetType;
    }

    public void setSuplAreaIdSetType(String suplAreaIdSetType) {
        this.suplAreaIdSetType = suplAreaIdSetType;
    }

    public Long getSuplReportingInterval() {
        return suplReportingInterval;
    }

    public void setSuplReportingInterval(Long suplReportingInterval) {
        this.suplReportingInterval = suplReportingInterval;
    }

    public Long getSuplReportingAmount() {
        return suplReportingAmount;
    }

    public void setSuplReportingAmount(Long suplReportingAmount) {
        this.suplReportingAmount = suplReportingAmount;
    }
}
