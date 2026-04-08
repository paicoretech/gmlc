package org.mobicents.gmlc.slee.mlp;

import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.mobicents.gmlc.slee.supl.SuplAreaEventType;
import org.mobicents.gmlc.slee.supl.SuplGeoTargetArea;
import org.mobicents.gmlc.slee.supl.SuplTriggerType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPriority;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoS;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationEstimateType;
import org.restcomm.protocols.ss7.map.api.service.lsm.OccurrenceInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTime;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;
import org.restcomm.protocols.ss7.map.service.lsm.LCSQoSImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ResponseTimeImpl;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class MLPLocationRequest {

  public enum ReportingService {Immediate, Triggered}

  GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();

  ReportingService reportingService;
  Boolean psiService = false;
  String psiServiceType;
  String operation = null, curlToken = null, msisdn = null, imsi = null, imei = null,
      locationInfoEps = null, atiExtraRequestedInfo = null, domain = null;
  LCSClientType lcsClientType = null;
  String lcsClientName, lcsRequestorId, lcsClientExternalId, lcsClientInternalId;
  String slpClientId, slpClientPwd, slpClientSessionId, slpClientServiceId, slpClientRequestModeType,
      slpClientApplicationName, slpClientApplicationProvider, slpClientApplicationVersion;
  String slpSubClientId, slpSubClientPwd, slpSubClientServiceId, slpSubClientLastClient;
  Integer lcsClientNameFormatIndicator, lcsRequestorFormatIndicator;
  Integer clientReferenceNumber = 0;
  Integer translationType = 0;
  LCSPriority lcsPriority = LCSPriority.normalPriority;
  LCSQoS lcsQoS = null;
  LCSQoSClass lcsQoSClass = null;
  ResponseTimeCategory responseTimeCategory;
  String lcsServiceTypeId;
  LocationEstimateType locationEstimateType;
  Long deferredLocationEventType;
  /** Area and Motion Events params **/
  // { Area-Definition }
  String areaType;
  String areaId;
  // { Linear-Distance }
  Long motionEventDistance;
  // [ Occurrence-Info ]
  OccurrenceInfo occurrenceInfo;
  // [ Interval-Time ]
  Integer intervalTime;
  // [ Maximum-Interval ]
  Integer maximumInterval;
  // [ Sampling-Interval ]
  Integer samplingInterval;
  // [ Reporting-Duration ]
  Integer reportingDuration;
  // [ Reporting-Location-Requirements ]
  Integer reportingLocationReqs;
  /** Periodic LDR params **/
  Integer reportingAmount, reportingInterval;
  /** Reporting-PLMN-List **/
  String visitedPLMNId;
  Integer visitedPLMNIdRAN, periodicLocationSupportIndicator, prioritizedListIndicator;
  Integer referenceNumber;
  String locationReportCallbackUrl = gmlcPropertiesManagement.getLcsUrlCallback();
  /*** SUPL Triggered Events ***/
  SuplTriggerType suplTriggerType;
  /** SUPL Area Event Params **/
  SuplAreaEventType suplAreaEventType;
  String suplAreaId;
  Double suplAreaEventLatitude, suplAreaEventLongitude, suplAreaEventSemiMajor, suplAreaEventSemiMinor, suplAreaEventAngle;
  Integer suplAreaEventRadius;
  Boolean suplLocationEstimateRequested;
  Integer suplMinimumIntervalTime, suplMaximumNumberOfReports;
  Long suplStartTime;
  Long suplStopTime;

  SuplGeoTargetArea suplGeographicTargetArea;
  Boolean suplAreaEventRepeatedReporting;
  String suplAreaIdSetType;

  public MLPLocationRequest() {
  }

  public MLPLocationRequest(ReportingService reportingService) {
    this.reportingService = reportingService;
  }

  //////////////
  // Getters //
  /////////////

  public ReportingService getReportingService() {
    return reportingService;
  }

  public String getOperation() {
    return operation;
  }

  public String getCurlToken() {
    return curlToken;
  }

  public String getMsisdn() {
    return msisdn;
  }

  public String getImsi() {
    return imsi;
  }

  public String getImei() {
    return imei;
  }

  public String getLocationInfoEps() {
    return locationInfoEps;
  }

  public String getAtiExtraRequestedInfo() {
    return atiExtraRequestedInfo;
  }

  public String getDomain() {
    return domain;
  }

  public LCSClientType getLcsClientType() {
    return lcsClientType;
  }

  public String getLcsClientName() {
    return lcsClientName;
  }

  public String getLcsRequestorId() {
    return lcsRequestorId;
  }

  public String getLcsClientExternalId() {
    return lcsClientExternalId;
  }

  public String getLcsClientInternalId() {
    return lcsClientInternalId;
  }

  public Integer getLcsClientNameFormatIndicator() {
    return lcsClientNameFormatIndicator;
  }

  public Integer getLcsRequestorFormatIndicator() {
    return lcsRequestorFormatIndicator;
  }

  public Integer getClientReferenceNumber() {
    return clientReferenceNumber;
  }

  public Integer getTranslationType() {
    return translationType;
  }

  public LCSPriority getLcsPriority() {
    return lcsPriority;
  }

  public LCSQoS getLcsQoS() {
    return lcsQoS;
  }

  public LCSQoSClass getLcsQoSClass() {
    return lcsQoSClass;
  }

  public ResponseTimeCategory getResponseTimeCategory() {
    return responseTimeCategory;
  }

  public String getLcsServiceTypeId() {
    return lcsServiceTypeId;
  }

  public LocationEstimateType getLocationEstimateType() {
    return locationEstimateType;
  }

  public Long getDeferredLocationEventType() {
    return deferredLocationEventType;
  }

  public String getAreaType() {
    return areaType;
  }

  public String getAreaId() {
    return areaId;
  }

  public Long getMotionEventDistance() {
    return motionEventDistance;
  }

  public OccurrenceInfo getOccurrenceInfo() {
    return occurrenceInfo;
  }

  public Integer getIntervalTime() {
    return intervalTime;
  }

  public Integer getMaximumInterval() {
    return maximumInterval;
  }

  public Integer getSamplingInterval() {
    return samplingInterval;
  }

  public Integer getReportingDuration() {
    return reportingDuration;
  }

  public Integer getReportingLocationReqs() {
    return reportingLocationReqs;
  }

  public Integer getReportingAmount() {
    return reportingAmount;
  }

  public Integer getReportingInterval() {
    return reportingInterval;
  }

  public String getVisitedPLMNId() {
    return visitedPLMNId;
  }

  public Integer getVisitedPLMNIdRAN() {
    return visitedPLMNIdRAN;
  }

  public Integer getPeriodicLocationSupportIndicator() {
    return periodicLocationSupportIndicator;
  }

  public Integer getPrioritizedListIndicator() {
    return prioritizedListIndicator;
  }

  public Integer getReferenceNumber() {
    return referenceNumber;
  }

  public String getLocationReportCallbackUrl() {
    return locationReportCallbackUrl;
  }

  public Boolean getPsiService() {
    return psiService;
  }

  public String getPsiServiceType() {
    return psiServiceType;
  }

  public String getSlpClientId() {
    return slpClientId;
  }

  public String getSlpClientPwd() {
    return slpClientPwd;
  }

  public String getSlpClientSessionId() {
    return slpClientSessionId;
  }

  public String getSlpClientServiceId() {
    return slpClientServiceId;
  }

  public String getSlpClientRequestModeType() {
    return slpClientRequestModeType;
  }

  public String getSlpClientApplicationName() {
    return slpClientApplicationName;
  }

  public String getSlpClientApplicationProvider() {
    return slpClientApplicationProvider;
  }

  public String getSlpClientApplicationVersion() {
    return slpClientApplicationVersion;
  }

  public String getSlpSubClientId() {
    return slpSubClientId;
  }

  public String getSlpSubClientPwd() {
    return slpSubClientPwd;
  }

  public String getSlpSubClientServiceId() {
    return slpSubClientServiceId;
  }

  public String getSlpSubClientLastClient() {
    return slpSubClientLastClient;
  }

  //////////////
  // Setters //
  /////////////

  public void setReportingService(ReportingService reportingService) {
    this.reportingService = reportingService;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public void setCurlToken(String curlToken) {
    this.curlToken = curlToken;
  }

  public void setMsisdn(String msisdn) {
    this.msisdn = msisdn;
  }

  public void setImsi(String imsi) {
    this.imsi = imsi;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public void setLocationInfoEps(String locationInfoEps) {
    this.locationInfoEps = locationInfoEps;
  }

  public void setAtiExtraRequestedInfo(String atiExtraRequestedInfo) {
    this.atiExtraRequestedInfo = atiExtraRequestedInfo;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public void setLcsClientType(LCSClientType lcsClientType) {
    this.lcsClientType = lcsClientType;
  }

  public void setLcsClientName(String lcsClientName) {
    this.lcsClientName = lcsClientName;
  }

  public void setLcsRequestorId(String lcsRequestorId) {
    this.lcsRequestorId = lcsRequestorId;
  }

  public void setLcsClientExternalId(String lcsClientExternalId) {
    this.lcsClientExternalId = lcsClientExternalId;
  }

  public void setLcsClientInternalId(String lcsClientInternalId) {
    this.lcsClientInternalId = lcsClientInternalId;
  }

  public void setLcsClientNameFormatIndicator(Integer lcsClientNameFormatIndicator) {
    this.lcsClientNameFormatIndicator = lcsClientNameFormatIndicator;
  }

  public void setLcsRequestorFormatIndicator(Integer lcsRequestorFormatIndicator) {
    this.lcsRequestorFormatIndicator = lcsRequestorFormatIndicator;
  }

  public void setClientReferenceNumber(Integer clientReferenceNumber) {
    this.clientReferenceNumber = clientReferenceNumber;
  }

  public void setTranslationType(Integer translationType) {
    this.translationType = translationType;
  }

  public void setLcsPriority(LCSPriority lcsPriority) {
    this.lcsPriority = lcsPriority;
  }

  public void setLcsQoSClass(LCSQoSClass lcsQoSClass) {
    this.lcsQoSClass = lcsQoSClass;
  }

  public void setResponseTimeCategory(ResponseTimeCategory responseTimeCategory) {
    this.responseTimeCategory = responseTimeCategory;
  }

  public void setLcsServiceTypeId(String lcsServiceTypeId) {
    this.lcsServiceTypeId = lcsServiceTypeId;
  }

  public void setLocationEstimateType(LocationEstimateType locationEstimateType) {
    this.locationEstimateType = locationEstimateType;
  }

  public void setDeferredLocationEventType(Long deferredLocationEventType) {
    this.deferredLocationEventType = deferredLocationEventType;
  }

  public void setAreaType(String areaType) {
    this.areaType = areaType;
  }

  public void setAreaId(String areaId) {
    this.areaId = areaId;
  }

  public void setMotionEventDistance(Long motionEventDistance) {
    this.motionEventDistance = motionEventDistance;
  }

  public void setOccurrenceInfo(OccurrenceInfo occurrenceInfo) {
    this.occurrenceInfo = occurrenceInfo;
  }

  public void setIntervalTime(Integer intervalTime) {
    this.intervalTime = intervalTime;
  }

  public void setMaximumInterval(Integer maximumInterval) {
    this.maximumInterval = maximumInterval;
  }

  public void setSamplingInterval(Integer samplingInterval) {
    this.samplingInterval = samplingInterval;
  }

  public void setReportingDuration(Integer reportingDuration) {
    this.reportingDuration = reportingDuration;
  }

  public void setReportingLocationReqs(Integer reportingLocationReqs) {
    this.reportingLocationReqs = reportingLocationReqs;
  }

  public void setReportingAmount(Integer reportingAmount) {
    this.reportingAmount = reportingAmount;
  }

  public void setReportingInterval(Integer reportingInterval) {
    this.reportingInterval = reportingInterval;
  }

  public void setVisitedPLMNId(String visitedPLMNId) {
    this.visitedPLMNId = visitedPLMNId;
  }

  public void setVisitedPLMNIdRAN(Integer visitedPLMNIdRAN) {
    this.visitedPLMNIdRAN = visitedPLMNIdRAN;
  }

  public void setPeriodicLocationSupportIndicator(Integer periodicLocationSupportIndicator) {
    this.periodicLocationSupportIndicator = periodicLocationSupportIndicator;
  }

  public void setPrioritizedListIndicator(Integer prioritizedListIndicator) {
    this.prioritizedListIndicator = prioritizedListIndicator;
  }

  public void setReferenceNumber(Integer referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public void setLocationReportCallbackUrl(String locationReportCallbackUrl) {
    this.locationReportCallbackUrl = locationReportCallbackUrl;
  }

  public void setPsiService(Boolean psiService) {
    this.psiService = psiService;
  }

  public void setPsiServiceType(String psiServiceType) {
    this.psiServiceType = psiServiceType;
  }

  public void setLcsQoS(Integer horizontalAccuracy, Integer verticalAccuracy, Boolean verticalCoordinateRequest, Integer responseTimeCategory) {
    ResponseTime responseTime = null;
    if (responseTimeCategory != null)
      responseTime = new ResponseTimeImpl(ResponseTimeCategory.getResponseTimeCategory(responseTimeCategory));
    this.lcsQoS = new LCSQoSImpl(horizontalAccuracy, verticalAccuracy, verticalCoordinateRequest, responseTime, null, false, null);
  }

  public void setLcsQoS(LCSQoS lcsQoS) {
    this.lcsQoS = lcsQoS;
  }

  public void setSlpClientId(String slpClientId) {
    this.slpClientId = slpClientId;
  }

  public void setSlpClientPwd(String slpClientPwd) {
    this.slpClientPwd = slpClientPwd;
  }

  public void setSlpClientSessionId(String slpClientSessionId) {
    this.slpClientSessionId = slpClientSessionId;
  }

  public void setSlpClientServiceId(String slpClientServiceId) {
    this.slpClientServiceId = slpClientServiceId;
  }

  public void setSlpClientRequestModeType(String slpClientRequestModeType) {
    this.slpClientRequestModeType = slpClientRequestModeType;
  }

  public void setSlpClientApplicationName(String slpClientApplicationName) {
    this.slpClientApplicationName = slpClientApplicationName;
  }

  public void setSlpClientApplicationProvider(String slpClientApplicationProvider) {
    this.slpClientApplicationProvider = slpClientApplicationProvider;
  }

  public void setSlpClientApplicationVersion(String slpClientApplicationVersion) {
    this.slpClientApplicationVersion = slpClientApplicationVersion;
  }

  public void setSlpSubClientId(String slpSubClientId) {
    this.slpSubClientId = slpSubClientId;
  }

  public void setSlpSubClientPwd(String slpSubClientPwd) {
    this.slpSubClientPwd = slpSubClientPwd;
  }

  public void setSlpSubClientServiceId(String slpSubClientServiceId) {
    this.slpSubClientServiceId = slpSubClientServiceId;
  }

  public void setSlpSubClientLastClient(String slpSubClientLastClient) {
    this.slpSubClientLastClient = slpSubClientLastClient;
  }

  public SuplTriggerType getSuplTriggerType() {
    return suplTriggerType;
  }

  public void setSuplTriggerType(SuplTriggerType suplTriggerType) {
    this.suplTriggerType = suplTriggerType;
  }

  public SuplAreaEventType getSuplAreaEventType() {
    return suplAreaEventType;
  }

  public void setSuplAreaEventType(SuplAreaEventType suplAreaEventType) {
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

  public SuplGeoTargetArea getSuplGeographicTargetArea() {
    return suplGeographicTargetArea;
  }

  public void setSuplGeographicTargetArea(SuplGeoTargetArea suplGeographicTargetArea) {
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

  @Override
  public String toString() {
    return "MLPLocationRequest{" +
        "reportingService=" + reportingService +
        ", psiService=" + psiService +
        ", psiServiceType='" + psiServiceType + '\'' +
        ", operation='" + operation + '\'' +
        ", curlToken='" + curlToken + '\'' +
        ", msisdn='" + msisdn + '\'' +
        ", imsi='" + imsi + '\'' +
        ", locationInfoEps='" + locationInfoEps + '\'' +
        ", atiExtraRequestedInfo='" + atiExtraRequestedInfo + '\'' +
        ", domain='" + domain + '\'' +
        ", lcsClientType=" + lcsClientType +
        ", lcsClientName='" + lcsClientName + '\'' +
        ", lcsRequestorId='" + lcsRequestorId + '\'' +
        ", lcsClientExternalId='" + lcsClientExternalId + '\'' +
        ", lcsClientInternalId='" + lcsClientInternalId + '\'' +
        ", slpClientId='" + slpClientId + '\'' +
        ", slpClientPwd='" + slpClientPwd + '\'' +
        ", slpClientSessionId='" + slpClientSessionId + '\'' +
        ", slpClientServiceId='" + slpClientServiceId + '\'' +
        ", slpClientRequestModeType='" + slpClientRequestModeType + '\'' +
        ", slpClientApplicationName='" + slpClientApplicationName + '\'' +
        ", slpClientApplicationProvider='" + slpClientApplicationProvider + '\'' +
        ", slpClientApplicationVersion='" + slpClientApplicationVersion + '\'' +
        ", slpSubClientId='" + slpSubClientId + '\'' +
        ", slpSubClientPwd='" + slpSubClientPwd + '\'' +
        ", slpSubClientServiceId='" + slpSubClientServiceId + '\'' +
        ", slpSubClientLastClient='" + slpSubClientLastClient + '\'' +
        ", lcsClientNameFormatIndicator=" + lcsClientNameFormatIndicator +
        ", lcsRequestorFormatIndicator=" + lcsRequestorFormatIndicator +
        ", clientReferenceNumber=" + clientReferenceNumber +
        ", translationType=" + translationType +
        ", lcsPriority=" + lcsPriority +
        ", lcsQoS=" + lcsQoS +
        ", lcsQoSClass=" + lcsQoSClass +
        ", responseTimeCategory=" + responseTimeCategory +
        ", lcsServiceTypeId='" + lcsServiceTypeId + '\'' +
        ", locationEstimateType=" + locationEstimateType +
        ", deferredLocationEventType=" + deferredLocationEventType +
        ", areaType='" + areaType + '\'' +
        ", areaId='" + areaId + '\'' +
        ", motionEventDistance=" + motionEventDistance +
        ", occurrenceInfo=" + occurrenceInfo +
        ", intervalTime=" + intervalTime +
        ", maximumInterval=" + maximumInterval +
        ", samplingInterval=" + samplingInterval +
        ", reportingDuration=" + reportingDuration +
        ", reportingLocationReqs=" + reportingLocationReqs +
        ", reportingAmount=" + reportingAmount +
        ", reportingInterval=" + reportingInterval +
        ", visitedPLMNId='" + visitedPLMNId + '\'' +
        ", visitedPLMNIdRAN=" + visitedPLMNIdRAN +
        ", periodicLocationSupportIndicator=" + periodicLocationSupportIndicator +
        ", prioritizedListIndicator=" + prioritizedListIndicator +
        ", referenceNumber=" + referenceNumber +
        ", locationReportCallbackUrl='" + locationReportCallbackUrl + '\'' +
        ", suplTriggerType=" + suplTriggerType +
        ", suplAreaEventType=" + suplAreaEventType +
        ", suplAreaId='" + suplAreaId + '\'' +
        ", suplAreaEventLatitude=" + suplAreaEventLatitude +
        ", suplAreaEventLongitude=" + suplAreaEventLongitude +
        ", suplAreaEventSemiMajor=" + suplAreaEventSemiMajor +
        ", suplAreaEventSemiMinor=" + suplAreaEventSemiMinor +
        ", suplAreaEventAngle=" + suplAreaEventAngle +
        ", suplAreaEventRadius=" + suplAreaEventRadius +
        ", suplLocationEstimateRequested=" + suplLocationEstimateRequested +
        ", suplMinimumIntervalTime=" + suplMinimumIntervalTime +
        ", suplMaximumNumberOfReports=" + suplMaximumNumberOfReports +
        ", suplStartTime=" + suplStartTime +
        ", suplStopTime=" + suplStopTime +
        ", suplGeographicTargetArea=" + suplGeographicTargetArea +
        ", suplAreaEventRepeatedReporting=" + suplAreaEventRepeatedReporting +
        ", suplAreaIdSetType='" + suplAreaIdSetType + '\'' +
        '}';
  }
}