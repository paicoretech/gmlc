package org.mobicents.gmlc.slee.diameter.slg;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.slg.SLgAVPFactory;
import net.java.slee.resource.diameter.slg.events.avp.AdditionalAreaAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaDefinitionAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaEventInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.ELPAVPCodes;
import net.java.slee.resource.diameter.slg.events.avp.LCSClientType;
import net.java.slee.resource.diameter.slg.events.avp.LCSEPSClientNameAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSFormatIndicator;
import net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheck;
import net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheckNonSessionAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheckSessionAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import net.java.slee.resource.diameter.slg.events.avp.LCSRequestorNameAvp;
import net.java.slee.resource.diameter.slg.events.avp.MotionEventInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo;
import net.java.slee.resource.diameter.slg.events.avp.PLMNIDListAvp;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLDRInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLocationSupportIndicator;
import net.java.slee.resource.diameter.slg.events.avp.PrioritizedListIndicator;
import net.java.slee.resource.diameter.slg.events.avp.ReportingPLMNListAvp;
import net.java.slee.resource.diameter.slg.events.avp.ResponseTime;
import net.java.slee.resource.diameter.slg.events.avp.SLgLocationType;
import net.java.slee.resource.diameter.slg.events.avp.VelocityRequested;
import net.java.slee.resource.diameter.slg.events.avp.VerticalRequested;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SLgPlrAvpValues implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final int DIAMETER_SLg_VENDOR_ID = 10415;

  private static final Logger logger = Logger.getLogger(SLgPlrAvpValues.class.getName());

  /*
    3GPP TS 29.172 v18.1.0
      7.3.1	Provide-Location-Request (PLR) Command
      The Provide-Location-Request (PLR) command, indicated by the Command-Code field set to 8388620 and the 'R' bit set in the Command Flags field,
      is sent by the GMLC in order to request subscriber location to the MME or SGSN.

      Message Format:

      < Provide-Location-Request> ::=    < Diameter Header: 8388620, REQ, PXY, 16777255 >
                                    < Session-Id >
                                    [ DRMP ]
                                    [ Vendor-Specific-Application-Id ]
                                    { Auth-Session-State }
                                    { Origin-Host }
                                    { Origin-Realm }
                                    { Destination-Host }
                                    { Destination-Realm }
                                    { SLg-Location-Type }
                                    [ User-Name ]
                                    [ MSISDN ]
                                    [ IMEI ]
                                    { LCS-EPS-Client-Name }
                                    { LCS-Client-Type }
                                    [ LCS-Requestor-Name ]
                                    [ LCS-Priority ]
                                    [ LCS-QoS ]
                                    [ Velocity-Requested ]
                                    [ LCS-Supported-GAD-Shapes ]
                                    [ LCS-Service-Type-ID ]
                                    [ LCS-Codeword ]
                                    [ LCS-Privacy-Check-Non-Session ]
                                    [ LCS-Privacy-Check-Session ]
                                    [ Service-Selection ]
                                    [ Deferred-Location-Type ]
                                    [ LCS-Reference-Number ]
                                    [ Area-Event-Info ]
                                    [ GMLC-Address ]
                                    [ PLR-Flags ]
                                    [ Periodic-LDR-Information ]
                                    [ Reporting-PLMN-List ]
                                    [ Motion-Event-Info ]
                                    *[ Supported-Features ]
                                    *[ AVP ]
                                    *[ Proxy-Info ]
  */

  private SLgLocationType sLgLocationType;
  private String userName; // i.e. IMSI
  private byte[] msisdn;
  private String imei;
  private LCSEPSClientNameAvp lcsEPSClientName;
  private String lcsNameString;
  private LCSFormatIndicator lcsFormatIndicator;
  private LCSClientType lcsClientType;
  private LCSRequestorNameAvp lcsRequestorName;
  private String lcsRequestorIdString;
  private Integer  reqLcsFormatIndicator;
  private Long lcsPriority;
  private LCSQoSAvp lcsQoS;
  private LCSQoSClass lcsQoSClass;
  private Long horizontalAccuracy;
  private Long verticalAccuracy;
  private VerticalRequested verticalRequested;
  private ResponseTime responseTime;
  private VelocityRequested velocityRequested;
  private Long lcsSupportedGADShapes;
  private Long lcsServiceTypeId;
  private String lcsCodeword;
  private String serviceSelection; // IE: APN
  private LCSPrivacyCheckSessionAvp lcsPrivacyCheckSession;
  private LCSPrivacyCheckNonSessionAvp lcsPrivacyCheckNonSession;
  private LCSPrivacyCheck lcsPrivacyCheck;
  private Long deferredLocationType;
  private Long plrFLags;
  private byte[] lcsReferenceNumber;
  private AreaEventInfoAvp areaEventInfo;
  private OccurrenceInfo areaEventOccurrenceInfo;
  private Long areaEventIntervalTime;
  private Long areaEventMaximumInterval;
  private Long areaEventSamplingInterval;
  private Long areaEventReportingDuration;
  private Long areaEventReportingLocationRequirements;
  private AreaDefinitionAvp areaDefinition;
  private AreaAvp areaAvp;
  private AdditionalAreaAvp additionalAreaAvp;
  private Long areaType;
  private byte[] areaIdentification;
  private Address gmlcAddress;
  private PeriodicLDRInfoAvp periodicLDRInformation;
  private Long reportingAmount;
  private Long reportingInterval;
  private ReportingPLMNListAvp reportingPLMNList;
  private PrioritizedListIndicator prioritizedListIndicator;
  private PLMNIDListAvp plmnIdList;
  private byte[] visitedPLMNId;
  private PeriodicLocationSupportIndicator periodicLocationSupportIndicator;
  private MotionEventInfoAvp motionEventInfoAvp;
  private Long linearDistance;
  private OccurrenceInfo motionEventOccurrenceInfo;
  private Long motionEventIntervalTime;
  private Long motionEventMaximumInterval;
  private Long motionEventSamplingInterval;
  private Long motionEventReportingDuration;
  private Long motionEventReportingLocationRequirements;


  public SLgPlrAvpValues() {
    super();
  }

  public SLgLocationType getsLgLocationType() {
    return sLgLocationType;
  }

  public void setsLgLocationType(SLgLocationType sLgLocationType) {
    this.sLgLocationType = sLgLocationType;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public byte[] getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(byte[] msisdn) {
    this.msisdn = msisdn;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public LCSEPSClientNameAvp getLcsEPSClientName() {
    return lcsEPSClientName;
  }

  public void setLcsEPSClientName(LCSEPSClientNameAvp lcsEPSClientName) {
    this.lcsEPSClientName = lcsEPSClientName;
  }

  public String getLcsNameString() {
    return lcsNameString;
  }

  public void setLcsNameString(String lcsNameString) {
    this.lcsNameString = lcsNameString;
  }

  public LCSFormatIndicator getLcsFormatIndicator() {
    return lcsFormatIndicator;
  }

  public void setLcsFormatIndicator(LCSFormatIndicator lcsFormatIndicator) {
    this.lcsFormatIndicator = lcsFormatIndicator;
  }

  public LCSClientType getLcsClientType() {
    return lcsClientType;
  }

  public void setLcsClientType(LCSClientType lcsClientType) {
    this.lcsClientType = lcsClientType;
  }

  public LCSRequestorNameAvp getLcsRequestorName() {
    return lcsRequestorName;
  }

  public void setLcsRequestorName(LCSRequestorNameAvp lcsRequestorName) {
    this.lcsRequestorName = lcsRequestorName;
  }

  public String getLcsRequestorIdString() {
    return lcsRequestorIdString;
  }

  public void setLcsRequestorIdString(String lcsRequestorIdString) {
    this.lcsRequestorIdString = lcsRequestorIdString;
  }

  public Integer  getReqLcsFormatIndicator() {
    return reqLcsFormatIndicator;
  }

  public void setReqLcsFormatIndicator(Integer  reqLcsFormatIndicator) {
    this.reqLcsFormatIndicator = reqLcsFormatIndicator;
  }

  public Long getLcsPriority() {
    return lcsPriority;
  }

  public void setLcsPriority(Long lcsPriority) {
    this.lcsPriority = lcsPriority;
  }

  public LCSQoSAvp getLcsQoS() {
    return lcsQoS;
  }

  public void setLcsQoS(LCSQoSAvp lcsQoS) {
    this.lcsQoS = lcsQoS;
  }

  public LCSQoSClass getLcsQoSClass() {
    return lcsQoSClass;
  }

  public void setLcsQoSClass(LCSQoSClass lcsQoSClass) {
    this.lcsQoSClass = lcsQoSClass;
  }

  public Long getHorizontalAccuracy() {
    return horizontalAccuracy;
  }

  public void setHorizontalAccuracy(Long horizontalAccuracy) {
    this.horizontalAccuracy = horizontalAccuracy;
  }

  public Long getVerticalAccuracy() {
    return verticalAccuracy;
  }

  public void setVerticalAccuracy(Long verticalAccuracy) {
    this.verticalAccuracy = verticalAccuracy;
  }

  public VerticalRequested getVerticalRequested() {
    return verticalRequested;
  }

  public void setVerticalRequested(VerticalRequested verticalRequested) {
    this.verticalRequested = verticalRequested;
  }

  public ResponseTime getResponseTime() {
    return responseTime;
  }

  public void setResponseTime(ResponseTime responseTime) {
    this.responseTime = responseTime;
  }

  public VelocityRequested getVelocityRequested() {
    return velocityRequested;
  }

  public void setVelocityRequested(VelocityRequested velocityRequested) {
    this.velocityRequested = velocityRequested;
  }

  public Long getLcsSupportedGADShapes() {
    return lcsSupportedGADShapes;
  }

  public void setLcsSupportedGADShapes(Long lcsSupportedGADShapes) {
    this.lcsSupportedGADShapes = lcsSupportedGADShapes;
  }

  public Long getLcsServiceTypeId() {
    return lcsServiceTypeId;
  }

  public void setLcsServiceTypeId(Long lcsServiceTypeId) {
    this.lcsServiceTypeId = lcsServiceTypeId;
  }

  public String getLcsCodeword() {
    return lcsCodeword;
  }

  public void setLcsCodeword(String lcsCodeword) {
    this.lcsCodeword = lcsCodeword;
  }

  public String getServiceSelection() {
    return serviceSelection;
  }

  public void setServiceSelection(String serviceSelection) {
    this.serviceSelection = serviceSelection;
  }

  public LCSPrivacyCheck getLcsPrivacyCheck() {
    return lcsPrivacyCheck;
  }

  public void setLcsPrivacyCheck(LCSPrivacyCheck lcsPrivacyCheck) {
    this.lcsPrivacyCheck = lcsPrivacyCheck;
  }

  public LCSPrivacyCheckSessionAvp getLcsPrivacyCheckSession() {
    return lcsPrivacyCheckSession;
  }

  public void setLcsPrivacyCheckSession(LCSPrivacyCheckSessionAvp lcsPrivacyCheckSession) {
    this.lcsPrivacyCheckSession = lcsPrivacyCheckSession;
  }

  public LCSPrivacyCheckNonSessionAvp getLcsPrivacyCheckNonSession() {
    return lcsPrivacyCheckNonSession;
  }

  public void setLcsPrivacyCheckNonSession(LCSPrivacyCheckNonSessionAvp lcsPrivacyCheckNonSession) {
    this.lcsPrivacyCheckNonSession = lcsPrivacyCheckNonSession;
  }

  public Long getDeferredLocationType() {
    return deferredLocationType;
  }

  public void setDeferredLocationType(Long deferredLocationType) {
    this.deferredLocationType = deferredLocationType;
  }

  public Long getPlrFLags() {
    return plrFLags;
  }

  public void setPlrFLags(Long plrFLags) {
    this.plrFLags = plrFLags;
  }

  public byte[] getLcsReferenceNumber() {
    return lcsReferenceNumber;
  }

  public void setLcsReferenceNumber(byte[] lcsReferenceNumber) {
    this.lcsReferenceNumber = lcsReferenceNumber;
  }

  public AreaEventInfoAvp getAreaEventInfo() {
    return areaEventInfo;
  }

  public void setAreaEventInfo(AreaEventInfoAvp areaEventInfo) {
    this.areaEventInfo = areaEventInfo;
  }

  public OccurrenceInfo getAreaEventOccurrenceInfo() {
    return areaEventOccurrenceInfo;
  }

  public void setAeOccurrenceInfo(OccurrenceInfo areaEventOccurrenceInfo) {
    this.areaEventOccurrenceInfo = areaEventOccurrenceInfo;
  }

  public Long getAreaEventIntervalTime() {
    return areaEventIntervalTime;
  }

  public void setAeIntervalTime(Long areaEventIntervalTime) {
    this.areaEventIntervalTime = areaEventIntervalTime;
  }

  public Long getAreaEventMaximumInterval() {
    return areaEventMaximumInterval;
  }

  public void setAeMaximumInterval(Long areaEventMaximumInterval) {
    this.areaEventMaximumInterval = areaEventMaximumInterval;
  }

  public Long getAreaEventSamplingInterval() {
    return areaEventSamplingInterval;
  }

  public void setAeSamplingInterval(Long areaEventSamplingInterval) {
    this.areaEventSamplingInterval = areaEventSamplingInterval;
  }

  public Long getAreaEventReportingDuration() {
    return areaEventReportingDuration;
  }

  public void setAeReportingDuration(Long areaEventReportingDuration) {
    this.areaEventReportingDuration = areaEventReportingDuration;
  }

  public Long getAreaEventReportingLocationRequirements() {
    return areaEventReportingLocationRequirements;
  }

  public void setAeReportingLocationRequirements(Long areaEventReportingLocationRequirements) {
    this.areaEventReportingLocationRequirements = areaEventReportingLocationRequirements;
  }

  public AreaDefinitionAvp getAreaDefinition() {
    return areaDefinition;
  }

  public void setAreaDefinition(AreaDefinitionAvp areaDefinition) {
    this.areaDefinition = areaDefinition;
  }

  public AreaAvp getAreaAvp() {
    return areaAvp;
  }

  public void setAreaAvp(AreaAvp areaAvp) {
    this.areaAvp = areaAvp;
  }

  public AdditionalAreaAvp getAdditionalAreaAvp() {
    return additionalAreaAvp;
  }

  public void setAdditionalAreaAvp(AdditionalAreaAvp additionalAreaAvp) {
    this.additionalAreaAvp = additionalAreaAvp;
  }

  public Long getAreaType() {
    return areaType;
  }

  public void setAreaType(Long areaType) {
    this.areaType = areaType;
  }

  public byte[] getAreaIdentification() {
    return areaIdentification;
  }

  public void setAreaIdentification(byte[] areaIdentification) {
    this.areaIdentification = areaIdentification;
  }

  public Address getGmlcAddress() {
    return gmlcAddress;
  }

  public void setGmlcAddress(Address gmlcAddress) {
    this.gmlcAddress = gmlcAddress;
  }

  public PeriodicLDRInfoAvp getPeriodicLDRInformation() {
    return periodicLDRInformation;
  }

  public void setPeriodicLDRInformation(PeriodicLDRInfoAvp periodicLDRInformation) {
    this.periodicLDRInformation = periodicLDRInformation;
  }

  public Long getReportingAmount() {
    return reportingAmount;
  }

  public void setReportingAmount(Long reportingAmount) {
    this.reportingAmount = reportingAmount;
  }

  public Long getReportingInterval() {
    return reportingInterval;
  }

  public void setReportingInterval(Long reportingInterval) {
    this.reportingInterval = reportingInterval;
  }

  public ReportingPLMNListAvp getReportingPLMNList() {
    return reportingPLMNList;
  }

  public void setReportingPLMNList(ReportingPLMNListAvp reportingPLMNList) {
    this.reportingPLMNList = reportingPLMNList;
  }

  public PrioritizedListIndicator getPrioritizedListIndicator() {
    return prioritizedListIndicator;
  }

  public void setPrioritizedListIndicator(PrioritizedListIndicator prioritizedListIndicator) {
    this.prioritizedListIndicator = prioritizedListIndicator;
  }

  public PLMNIDListAvp getPlmnIdList() {
    return plmnIdList;
  }

  public void setPlmnIdList(PLMNIDListAvp plmnIdList) {
    this.plmnIdList = plmnIdList;
  }

  public byte[] getVisitedPLMNId() {
    return visitedPLMNId;
  }

  public void setVisitedPLMNId(byte[] visitedPLMNId) {
    this.visitedPLMNId = visitedPLMNId;
  }

  public PeriodicLocationSupportIndicator getPeriodicLocationSupportIndicator() {
    return periodicLocationSupportIndicator;
  }

  public void setPeriodicLocationSupportIndicator(PeriodicLocationSupportIndicator periodicLocationSupportIndicator) {
    this.periodicLocationSupportIndicator = periodicLocationSupportIndicator;
  }

  public MotionEventInfoAvp getMotionEventInfoAvp() {
    return motionEventInfoAvp;
  }

  public void setMotionEventInfoAvp(MotionEventInfoAvp motionEventInfoAvp) {
    this.motionEventInfoAvp = motionEventInfoAvp;
  }

  public Long getLinearDistance() {
    return linearDistance;
  }

  public void setLinearDistance(Long linearDistance) {
    this.linearDistance = linearDistance;
  }

  public OccurrenceInfo getMeOccurrenceInfo() {
    return motionEventOccurrenceInfo;
  }

  public void setMeOccurrenceInfo(OccurrenceInfo motionEventOccurrenceInfo) {
    this.motionEventOccurrenceInfo = motionEventOccurrenceInfo;
  }

  public Long getMeIntervalTime() {
    return motionEventIntervalTime;
  }

  public void setMeIntervalTime(Long motionEventIntervalTime) {
    this.motionEventIntervalTime = motionEventIntervalTime;
  }

  public Long getMeMaximumInterval() {
    return motionEventMaximumInterval;
  }

  public void setMeMaximumInterval(Long motionEventMaximumInterval) {
    this.motionEventMaximumInterval = motionEventMaximumInterval;
  }

  public Long getMeSamplingInterval() {
    return motionEventSamplingInterval;
  }

  public void setMeSamplingInterval(Long motionEventSamplingInterval) {
    this.motionEventSamplingInterval = motionEventSamplingInterval;
  }

  public Long getMeReportingDuration() {
    return motionEventReportingDuration;
  }

  public void setMeReportingDuration(Long motionEventReportingDuration) {
    this.motionEventReportingDuration = motionEventReportingDuration;
  }

  public Long getMeReportingLocationRequirements() {
    return motionEventReportingLocationRequirements;
  }

  public void setMeReportingLocationRequirements(Long motionEventReportingLocationRequirements) {
    this.motionEventReportingLocationRequirements = motionEventReportingLocationRequirements;
  }

  /** PLR AVPs building method **/
  public void createPLRAvps(SLgAVPFactory slgAVPFactory) {

    try {

      // AVP name="slg-Location-Type" code="2500" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp slgLocationTypeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.SLg_LOCATION_TYPE, this.sLgLocationType);
      SLgLocationType sLgLocationTypeCode = SLgLocationType.fromInt(slgLocationTypeAvp.getCode());
      setsLgLocationType(sLgLocationTypeCode);

      // AVP name="User-Name" code="1" mandatory="must" protected="may" may-encrypt="yes" vendor-bit="mustnot" type-name="UTF8String"
      DiameterAvp userNAmeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.USER_NAME, this.userName);
      setUserName(userNAmeAvp.stringValue());

      // AVP name="MSISDN" code="701" vendor-id="TGPP" mandatory="must" protected="may" may-encrypt="no" vendor-bit="must" type-name="OctetString"
      DiameterAvp msisdnAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.MSISDN, this.msisdn);
      setMsisdn(msisdnAvp.byteArrayValue());

      // AVP name="IMEI" code="1402" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="UTF8String"
      DiameterAvp imeiAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.IMEI, this.imei);
      setUserName(imeiAvp.stringValue());

      // AVP name="LCS-Name-String" code="1238" vendor-id="TGPP" mandatory="must" protected="may" may-encrypt="no" vendor-bit="must" type-name="UTF8String"
      DiameterAvp lcsNameStringAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_NAME_STRING, this.lcsNameString);
      setLcsNameString(lcsNameStringAvp.stringValue());
      // AVP name="LCS-Format-Indicator" code="1237" vendor-id="TGPP" mandatory="may" protected="may" may-encrypt="yes" vendor-bit="must" type-name="Enumerated"
      DiameterAvp lcsFormatIndicatorAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_FORMAT_INDICATOR, this.lcsFormatIndicator);
      LCSFormatIndicator lcsFormatIndicatorCode = LCSFormatIndicator.fromInt(lcsFormatIndicatorAvp.getCode());
      setLcsFormatIndicator(lcsFormatIndicatorCode);
      // AVP name="LCS-EPS-Client-Name" code="2501" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="LCS-Name-String" multiplicity="0-1" />
      //			<avp name="LCS-Format-Indicator" multiplicity="0-1" />
      //		</grouped>
      lcsEPSClientName = slgAVPFactory.createLCSEPSClientName();
      lcsEPSClientName.setLCSNameString(getLcsNameString());
      lcsEPSClientName.setLCSFormatIndicator(lcsFormatIndicator);

      // AVP name="LCS-Client-Type" code="1241" vendor-id="TGPP" mandatory="may" protected="may" may-encrypt="yes" vendor-bit="must" type-name="Enumerated"
      DiameterAvp lcsClientTypeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_CLIENT_TYPE, this.lcsClientType);
      LCSClientType lcsClientTypeCode = LCSClientType.fromInt(lcsClientTypeAvp.getCode());
      setLcsClientType(lcsClientTypeCode);

      // AVP name="LCS-Requestor-Id-String" code="1240" vendor-id="TGPP" mandatory="must" protected="may" may-encrypt="no" vendor-bit="must" type type-name="UTF8String" />
      DiameterAvp lcsRequestorIdStringAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_REQUESTOR_ID_STRING, this.lcsRequestorIdString);
      setLcsRequestorIdString(lcsRequestorIdStringAvp.stringValue());
      // AVP name="LCS-Requestor-Name" code="2502" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="LCS-Requestor-Id-String" multiplicity="0-1" />
      //			<avp name="LCS-Format-Indicator" multiplicity="0-1" />
      //		</grouped>
      lcsRequestorName = slgAVPFactory.createLCSRequestorName();
      setReqLcsFormatIndicator(lcsFormatIndicatorAvp.intValue());
      lcsRequestorName.setLCSRequestorIDString(getLcsRequestorIdString());
      lcsRequestorName.setLCSFormatIndicator(lcsFormatIndicator);

      // AVP name="LCS-Priority" code="2503" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp lcsPriorityAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_PRIORITY, this.lcsPriority);
      setLcsPriority(lcsPriorityAvp.longValue());

      // AVP name="LCS-QoS-Class" code="2523" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp lcsQosClassAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_QoS_CLASS, this.lcsQoSClass);
      LCSQoSClass lcsQoSClassCode = LCSQoSClass.fromInt(lcsQosClassAvp.getCode());
      setLcsQoSClass(lcsQoSClassCode);
      // AVP ame="Horizontal-Accuracy" code="2505" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp horizontalAccuracyAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.HORIZONTAL_ACCURACY, this.horizontalAccuracy);
      setHorizontalAccuracy(horizontalAccuracyAvp.longValue());
      // AVP name="Vertical-Accuracy" code="2506" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp verticalAccuracyAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.VERTICAL_ACCURACY, this.verticalAccuracy);
      setVerticalAccuracy(verticalAccuracyAvp.longValue());
      // AVP ame="Vertical-Requested" code="2507" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp verticalRequestedAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.VERTICAL_REQUESTED, this.verticalRequested);
      VerticalRequested verticalRequestedCode = VerticalRequested.fromInt(verticalRequestedAvp.getCode());
      setVerticalRequested(verticalRequestedCode);
      // AVP name="Response-Time" code="2509" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp responseTimeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.RESPONSE_TIME, this.responseTime);
      ResponseTime responseTimeCode = ResponseTime.fromInt(responseTimeAvp.getCode());
      setResponseTime(responseTimeCode);
      // AVP name="LCS-QoS" code="2504" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="LCS-QoS-Class" multiplicity="0-1" />
      //			<avp name="Horizontal-Accuracy" multiplicity="0-1" />
      //			<avp name="Vertical-Accuracy" multiplicity="0-1" />
      //			<avp name="Vertical-Requested" multiplicity="0-1" />
      //			<avp name="Response-Time" multiplicity="0-1" />
      //		</grouped>
      lcsQoS = slgAVPFactory.createLCSQoS();
      lcsQoS.setLCSQoSClass(getLcsQoSClass());
      lcsQoS.setHorizontalAccuracy(getHorizontalAccuracy());
      lcsQoS.setVerticalAccuracy(getVerticalAccuracy());
      lcsQoS.setVerticalRequested(getVerticalRequested());
      lcsQoS.setResponseTime(getResponseTime());

      // AVP name="Velocity-Requested" code="2508" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp velocityRequestedAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.VELOCITY_REQUESTED, this.velocityRequested);
      VelocityRequested velReqCode = VelocityRequested.fromInt(velocityRequestedAvp.getCode());
      setVelocityRequested(velReqCode);

      // AVP name="LCS-Supported-GAD-Shapes" code="2510" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp lcsSupportedGADShapesAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.SUPPORTED_GAD_SHAPES, this.lcsSupportedGADShapes);
      setLcsSupportedGADShapes(lcsSupportedGADShapesAvp.longValue());

      // AVP name="LCS-Service-Type-ID" code="2520" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp lcsServiceTypeIdAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_SERVICE_TYPE_ID, this.lcsServiceTypeId);
      setLcsServiceTypeId(lcsServiceTypeIdAvp.longValue());

      // AVP name="LCS-Codeword" code="2511" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="UTF8String"
      DiameterAvp lcsCodewordAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_CODEWORD, this.lcsCodeword);
      setLcsCodeword(lcsCodewordAvp.stringValue());

      // AVP name="Service-Selection" code="493" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="mustnot" type-name="UTF8String"
      DiameterAvp serviceSelectionAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.SERVICE_SELECTION, this.serviceSelection);
      setServiceSelection(serviceSelectionAvp.stringValue());

      // AVP name="LCS-Privacy-Check" code="2512" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp lcsPrivacyCheckAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_PRIVACY_CHECK, this.lcsPrivacyCheck);
      LCSPrivacyCheck lcsPrivacyCheckCode = LCSPrivacyCheck.fromInt(lcsPrivacyCheckAvp.getCode());
      setLcsPrivacyCheck(lcsPrivacyCheckCode);
      // AVP name="LCS-Privacy-Check-Session" code="2522" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="LCS-Privacy-Check" multiplicity="1" />
      //		</grouped>
      lcsPrivacyCheckSession = slgAVPFactory.createLCSPrivacyCheckSession();
      lcsPrivacyCheckSession.setLCSPrivacyCheck(getLcsPrivacyCheck());
      // AVP name="LCS-Privacy-Check-Non-Session" code="2521" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="LCS-Privacy-Check" multiplicity="1" />
      //		</grouped>
      lcsPrivacyCheckNonSession = slgAVPFactory.createLCSPrivacyCheckNonSession();
      lcsPrivacyCheckNonSession.setLCSPrivacyCheck(getLcsPrivacyCheck());

      // AVP ame="Deferred-Location-Type" code="2532" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp deferredLocationTypeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.DEFERRED_LOCATION_TYPE, this.deferredLocationType);
      setDeferredLocationType(deferredLocationTypeAvp.longValue());

      // AVP name="PLR-Flags" code="2545" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp plrFlagsAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.PLR_FLAGS, this.plrFLags);
      setPlrFLags(plrFlagsAvp.longValue());

      // AVP ame="LCS-Reference-Number" code="2531" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="OctetString"
      DiameterAvp lcsReferenceNumberAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_REFERENCE_NUMBER, this.lcsReferenceNumber);
      setLcsReferenceNumber(lcsReferenceNumberAvp.byteArrayValue());

      // AVP ame="Area-Type" code="2536" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32" />
      DiameterAvp areaTypeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.AREA_TYPE, this.areaType);
      setAreaType(areaTypeAvp.longValue());
      // AVP name="Area-Identification" code="2537" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="OctetString" />
      DiameterAvp areaIdentificationAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.AREA_IDENTIFICATION, this.areaIdentification);
      setAreaIdentification(areaIdentificationAvp.byteArrayValue());
      // AVP <avpdefn name="Area" code="2535" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="Area-Type" multiplicity="1" />
      //			<avp name="Area-Identification" multiplicity="1" />
      //		</grouped>
      areaAvp = slgAVPFactory.createArea();
      areaAvp.setAreaType(getAreaType());
      areaAvp.setAreaIdentification(getAreaIdentification());
      // AVP name="Additional-Area" code="2565" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="Area-Type" multiplicity="1" />
      //			<avp name="Area-Identification" multiplicity="1" />
      //		</grouped>
      additionalAreaAvp = slgAVPFactory.createAdditionalArea();
      additionalAreaAvp.setAreaType(getAreaType());
      additionalAreaAvp.setAreaIdentification(getAreaIdentification());
      // AVP name="Area-Definition" code="2534" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="Area" multiplicity="1+" /> <!-- 1-10 -->
      //			<avp name="Additional-Area" multiplicity="0-240" />
      //		</grouped>
      areaDefinition = slgAVPFactory.createAreaDefinition();
      areaDefinition.setArea(getAreaAvp());
      areaDefinition.setAdditionalArea(getAdditionalAreaAvp());
      // AVP name="Interval-Time" code="2539" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32" />
      DiameterAvp areaEventInfoIntervalTimeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.INTERVAL_TIME, this.areaEventIntervalTime);
      setAeIntervalTime(areaEventInfoIntervalTimeAvp.longValue());
      // AVP name="Occurrence-Info" code="2538" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated">
      DiameterAvp areaEventInfoOccurrenceInformationAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.OCCURRENCE_INFO, this.areaEventOccurrenceInfo);
      OccurrenceInfo occurrenceInfoCode = OccurrenceInfo.fromInt(areaEventInfoOccurrenceInformationAvp.getCode());
      setAeOccurrenceInfo(occurrenceInfoCode);
      // AVP name="Maximum-Interval" code="2561" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp areaEventInfoMaximumIntervalAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.MAXIMUM_INTERVAL, this.areaEventMaximumInterval);
      setAeMaximumInterval(areaEventInfoMaximumIntervalAvp.longValue());
      // AVP name="Sampling-Interval" code="2562" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp areaEventInfoSamplingIntervalAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.SAMPLING_INTERVAL, this.areaEventSamplingInterval);
      setAeSamplingInterval(areaEventInfoSamplingIntervalAvp.longValue());
      // AVP name="Reporting-Duration" code="2563" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp areaEventInfoReportingDurationAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.REPORTING_DURATION, this.areaEventReportingDuration);
      setAeReportingDuration(areaEventInfoReportingDurationAvp.longValue());
      // AVP name="Reporting-Location-Requirements" code="2564" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp areaEventInfoReportingLocationRequirementsAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.REPORTING_LOCATION_REQUIREMENTS, this.areaEventReportingLocationRequirements);
      setAeReportingLocationRequirements(areaEventInfoReportingLocationRequirementsAvp.longValue());
      // AVP name="Area-Event-Info" code="2533" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="Area-Definition" multiplicity="1" />
      //			<avp name="Occurrence-Info" multiplicity="0-1" />
      //			<avp name="Interval-Time" multiplicity="0-1" />
			//      <avp name="Maximum-Interval" multiplicity="0-1" />
			//      <avp name="Sampling-Interval" multiplicity="0-1" />
			//      <avp name="Reporting-Duration" multiplicity="0-1" />
			//      <avp name="Reporting-Location-Requirements" multiplicity="0-1" />
      //		</grouped>
      areaEventInfo = slgAVPFactory.createAreaEventInfo();
      areaEventInfo.setAreaDefinition(getAreaDefinition());
      areaEventInfo.setOccurrenceInfo(getAreaEventOccurrenceInfo());
      areaEventInfo.setIntervalTime(getAreaEventIntervalTime());
      areaEventInfo.setMaximumInterval(getAreaEventMaximumInterval());
      areaEventInfo.setSamplingInterval(getAreaEventSamplingInterval());
      areaEventInfo.setReportDuration(getAreaEventReportingDuration());
      areaEventInfo.setReportingLocationRequirements(getAreaEventReportingLocationRequirements());

      // AVP name="GMLC-Address" code="2405" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Address"
      DiameterAvp gmlcAddressAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.GMLC_ADDRESS);
      setGmlcAddress((Address) gmlcAddressAvp);

      // AVP name="Reporting-Amount" code="2541" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp reportingAmountAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.REPORTING_AMOUNT, this.reportingAmount);
      setReportingAmount(reportingAmountAvp.longValue());
      // AVP name="Reporting-Interval" code="2542" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp reportingIntervalAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.REPORTING_INTERVAL, this.reportingInterval);
      setReportingInterval(reportingIntervalAvp.longValue());
      // AVP name="Periodic-LDR-Info" code="2540" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must"
      //		<grouped>
      //			<avp name="Reporting-Amount" multiplicity="1" />
      //			<avp name="Reporting-Interval" multiplicity="1" />
      //		</grouped>
      periodicLDRInformation = slgAVPFactory.createPeriodicLDRInformation();
      periodicLDRInformation.setReportingAmount(getReportingAmount());
      periodicLDRInformation.setReportingInterval(getReportingInterval());

      // AVP ame="Prioritized-List-Indicator" code="2551" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp prioritizedListIndicatorAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.PRIORITIZED_LIST_INDICATOR, this.prioritizedListIndicator);
      PrioritizedListIndicator prioritizedListIndicatorCode = PrioritizedListIndicator.fromInt(prioritizedListIndicatorAvp.getCode());
      setPrioritizedListIndicator(prioritizedListIndicatorCode);
      // AVP name="Periodic-Location-Support-Indicator" code="2550" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp periodicLocSupportIndicatorAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.PERIODIC_LOCATION_SUPPORT_INDICATOR, this.periodicLocationSupportIndicator);
      PeriodicLocationSupportIndicator periodicLocationSupportIndicatorCode = PeriodicLocationSupportIndicator.fromInt(periodicLocSupportIndicatorAvp.getCode());
      setPeriodicLocationSupportIndicator(periodicLocationSupportIndicatorCode);
      // APN name="Visited-PLMN-Id" code="1407" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="OctetString"
      DiameterAvp visitedPlmnIdAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.VISITED_PLMN_ID, this.visitedPLMNId);
      setVisitedPLMNId(visitedPlmnIdAvp.byteArrayValue());
      // AVP name="PLMN-ID-List" code="2544" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" >
      //		<grouped>
      //			<avp name="Visited-PLMN-Id" multiplicity="1" />
      //			<avp name="Periodic-Location-Support-Indicator" multiplicity="0-1" />
      //		</grouped>
      plmnIdList = slgAVPFactory.createPLMNIDList();
      plmnIdList.setVisitedPLMNId(getVisitedPLMNId());
      plmnIdList.setPeriodicLocationSupportIndicator(getPeriodicLocationSupportIndicator());
      // AVP name="Reporting-PLMN-List" code="2543" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" >
      //		<grouped>
      //			<avp name="PLMN-ID-List" multiplicity="1+" /> <!-- 1-20 -->
      //			<avp name="Prioritized-List-Indicator" multiplicity="0-1" />
      //		</grouped>
      reportingPLMNList = slgAVPFactory.createReportingPLMNList();
      reportingPLMNList.setPLMNIDList(getPlmnIdList());
      reportingPLMNList.setPrioritizedListIndicator(getPrioritizedListIndicator());

      // AVP name="Interval-Time" code="2539" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32" />
      DiameterAvp motionEventIntervalTimeAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.INTERVAL_TIME, this.motionEventIntervalTime);
      setMeIntervalTime(motionEventIntervalTimeAvp.longValue());
      // AVP name="Occurrence-Info" code="2538" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated">
      DiameterAvp motionEventOccurrenceInfoAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.OCCURRENCE_INFO, this.motionEventOccurrenceInfo);
      OccurrenceInfo motionEventOccurrenceInfoCode =OccurrenceInfo.fromInt(motionEventOccurrenceInfoAvp.getCode());
      setMeOccurrenceInfo(motionEventOccurrenceInfoCode);
      // AVP namotionEvent="Linear-Distance" code="2560" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp motionEventLinearDistanceAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LINEAR_DISTANCE, this.linearDistance);
      setLinearDistance(motionEventLinearDistanceAvp.longValue());
      // AVP name="Maximum-Interval" code="2561" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp motionEventMaxIntervalAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.MAXIMUM_INTERVAL, this.motionEventMaximumInterval);
      setMeMaximumInterval(motionEventMaxIntervalAvp.longValue());
      // AVP name="Sampling-Interval" code="2562" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp motionEventSamplingIntervalAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.SAMPLING_INTERVAL, this.motionEventSamplingInterval);
      setMeSamplingInterval(motionEventSamplingIntervalAvp.longValue());
      // AVP namotionEvent="Reporting-Duration" code="2563" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp motionEventReportingDurationAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.REPORTING_DURATION, this.motionEventReportingDuration);
      setMeReportingDuration(motionEventReportingDurationAvp.longValue());
      // AVP name="Reporting-Location-Requirements" code="2564" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp motionEventReportingLocationRequirementsAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.REPORTING_LOCATION_REQUIREMENTS, this.motionEventReportingLocationRequirements);
      setMeReportingLocationRequirements(motionEventReportingLocationRequirementsAvp.longValue());
      // AVP name="Motion-Event-Info" code="2559" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="mustnot" >
      //		<grouped>
      //			<avp name="Linear-Distance" multiplicity="1" />
      //			<avp name="Occurrence-Info" multiplicity="0-1" />
      //			<avp name="Interval-Time" multiplicity="0-1" />
      //			<avp name="Maximum-Interval" multiplicity="0-1" />
      //			<avp name="Sampling-Interval" multiplicity="0-1" />
      //			<avp name="Reporting-Duration" multiplicity="0-1" />
      //			<avp name="Reporting-Location-Requirements" multiplicity="0-1" />
      //		</grouped>
      motionEventInfoAvp = slgAVPFactory.createMotionEventInfo();
      motionEventInfoAvp.setIntervalTime(getMeIntervalTime());
      motionEventInfoAvp.setLinearDistance(getLinearDistance());
      motionEventInfoAvp.setOccurrenceInfo(getMeOccurrenceInfo());
      motionEventInfoAvp.setMaximumInterval(getMeMaximumInterval());
      motionEventInfoAvp.setSamplingInterval(getMeSamplingInterval());
      motionEventInfoAvp.setReportDuration(getMeReportingDuration());
      motionEventInfoAvp.setReportingLocationRequirements(getMeReportingLocationRequirements());

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }


}
