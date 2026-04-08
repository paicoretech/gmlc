package org.mobicents.gmlc.slee.diameter.slg;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.slg.events.avp.AccuracyFulfilmentIndicator;
import net.java.slee.resource.diameter.slg.events.avp.DeferredMTLRDataAvp;
import net.java.slee.resource.diameter.slg.events.avp.DelayedLocationReportingDataAvp;
import net.java.slee.resource.diameter.slg.events.avp.ESMLCCellInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.GERANPositioningInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSEPSClientNameAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSFormatIndicator;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import net.java.slee.resource.diameter.slg.events.avp.LocationEvent;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLDRInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.PseudonymIndicator;
import net.java.slee.resource.diameter.slg.events.avp.ServingNodeAvp;
import net.java.slee.resource.diameter.slg.events.avp.UTRANPositioningInfoAvp;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningDataImpl;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SLgLrrAvpValues implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final int DIAMETER_SLg_VENDOR_ID = 10415;

  /*
    3GPP TS 29.172 v18.1.0 reference
      7.3.3	Location-Report-Request (LRR) Command
      The Location-Report-Request (LRR) command, indicated by the Command-Code field set to 8388621 and the 'R' bit set in the Command Flags field,
      is sent by the MME or SGSN in order to provide subscriber location data to the GMLC.

      Message Format

        < Location-Report-Request> ::=  < Diameter Header: 8388621, REQ, PXY, 16777255 >
                                   < Session-Id >
                                   [ DRMP ]
                                   [ Vendor-Specific-Application-Id ]
                                   { Auth-Session-State }
                                   { Origin-Host }
                                   { Origin-Realm }
                                   { Destination-Host }
                                   { Destination-Realm }
                                   { Location-Event }
                                   [ LCS-EPS-Client-Name ]
                                   [ User-Name ]
                                   [ MSISDN]
                                   [ IMEI ]
                                   [ Location-Estimate ]
                                   [ Accuracy-Fulfilment-Indicator ]
                                   [ Age-Of-Location-Estimate ]
                                   [ Velocity-Estimate ]
                                   [ EUTRAN-Positioning-Data ]
                                   [ ECGI]
                                   [ GERAN-Positioning-Info ]
                                   [ Cell-Global-Identity ]
                                   [ UTRAN-Positioning-Info ]
                                   [ Service-Area-Identity ]
                                   [ LCS-Service-Type-ID ]
                                   [ Pseudonym-Indicator ]
                                   [ LCS-QoS-Class ]
                                   [ Serving-Node ]
                                   [ LRR-Flags ]
                                   [ LCS-Reference-Number ]
                                   [ Deferred-MT-LR-Data]
                                   [ GMLC-Address ]
                                   [ Reporting-Amount ]
                                   [ Periodic-LDR-Information ]
                                   [ ESMLC-Cell-Info ]
                                   [ 1xRTT-RCID ] ]
                                   [ Delayed-Location-Reporting-Data ]
                                   [ Civic-Address ]
                                   [ Barometric-Pressure ]
                                   [ AMF-Instance-Id ]
                                   *[ Supported-Features ]
                                   *[ AVP ]
                                   *[ Proxy-Info ]
                                   *[ Route-Record ]

  */

  private LocationEvent locationEvent;
  private LCSEPSClientNameAvp lcsEPSClientName;
  private String lcsNameString;
  private LCSFormatIndicator lcsFormatIndicator;
  private String userName; // i.e. IMSI
  private byte[] msisdn;
  private String imei;
  private byte[] locationEstimate;
  private AccuracyFulfilmentIndicator accuracyFulfilmentIndicator;
  private Long ageOfLocationEstimate;
  private byte[] velocityEstimate;
  private byte[] eUtranPositioningData;
  private EUTRANPositioningDataImpl eutranPositioningData;
  private byte[] ecgi;
  private GERANPositioningInfoAvp geranPositioningInfoAvp;
  private byte[] geranPositioningData;
  private byte[] geranGANSSPositioningData;
  private byte[] cellGlobalIdentity;
  private UTRANPositioningInfoAvp utranPositioningInfoAvp;
  private byte[] utranPositioningData;
  private byte[] utranGANSSPositioningData;
  private byte[] utranAdditionalPositioningData;
  private byte[] serviceAreaIdentity;
  private Long lcsServiceTypeId;
  private PseudonymIndicator pseudonymIndicator;
  private LCSQoSClass lcsQoSClass;
  private ServingNodeAvp servingNodeAvp;
  private Address servingNodeGmlcAddress;
  private Long sequenceNumber;
  private Address gmlcAddress;
  private Long lrrFlags;
  private byte[] lcsReferenceNumber;
  private DeferredMTLRDataAvp deferredMTLRDataAvp;
  private Long deferredLocationType;
  private Long terminationCause;
  private PeriodicLDRInfoAvp periodicLDRInformation;
  private Long reportingAmount;
  private Long reportingInterval;
  private ESMLCCellInfoAvp esmlcCellInfoAvp;
  private Long cellPortionId;
  private byte[] oneXRttRcid;
  private DelayedLocationReportingDataAvp delayedLocationReportingDataAvp;
  private String civicAddress;
  private Long barometricPressure;
  private String amfInstanceId;

  private Integer originatorLcsReferenceNumber;

  public SLgLrrAvpValues() {
    super();
  }

  public LocationEvent getLocationEvent() {
    return locationEvent;
  }

  public void setLocationEvent(LocationEvent locationEvent) {
    this.locationEvent = locationEvent;
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

  public byte[] getLocationEstimate() {
    return locationEstimate;
  }

  public void setLocationEstimate(byte[] locationEstimate) {
    this.locationEstimate = locationEstimate;
  }

  public AccuracyFulfilmentIndicator getAccuracyFulfilmentIndicator() {
    return accuracyFulfilmentIndicator;
  }

  public void setAccuracyFulfilmentIndicator(AccuracyFulfilmentIndicator accuracyFulfilmentIndicator) {
    this.accuracyFulfilmentIndicator = accuracyFulfilmentIndicator;
  }

  public Long getAgeOfLocationEstimate() {
    return ageOfLocationEstimate;
  }

  public void setAgeOfLocationEstimate(Long ageOfLocationEstimate) {
    this.ageOfLocationEstimate = ageOfLocationEstimate;
  }

  public byte[] getVelocityEstimate() {
    return velocityEstimate;
  }

  public void setVelocityEstimate(byte[] velocityEstimate) {
    this.velocityEstimate = velocityEstimate;
  }

  public byte[] getEUtranPositioningData() {
    return eUtranPositioningData;
  }

  public void setEUtranPositioningData(byte[] eUtranPositioningData) {
    this.eUtranPositioningData = eUtranPositioningData;
  }

  public EUTRANPositioningDataImpl getEutranPositioningData() {
    return eutranPositioningData;
  }

  public void setEutranPositioningData(EUTRANPositioningDataImpl eutranPositioningData) {
    this.eutranPositioningData = eutranPositioningData;
  }

  public byte[] getEcgi() {
    return ecgi;
  }

  public void setEcgi(byte[] ecgi) {
    this.ecgi = ecgi;
  }

  public GERANPositioningInfoAvp getGeranPositioningInfoAvp() {
    return geranPositioningInfoAvp;
  }

  public void setGeranPositioningInfoAvp(GERANPositioningInfoAvp geranPositioningInfoAvp) {
    this.geranPositioningInfoAvp = geranPositioningInfoAvp;
  }

  public byte[] getGeranPositioningData() {
    return geranPositioningData;
  }

  public void setGeranPositioningData(byte[] geranPositioningData) {
    this.geranPositioningData = geranPositioningData;
  }

  public byte[] getGeranGANSSPositioningData() {
    return geranGANSSPositioningData;
  }

  public void setGeranGANSSPositioningData(byte[] geranGANSSPositioningData) {
    this.geranGANSSPositioningData = geranGANSSPositioningData;
  }

  public byte[] getCellGlobalIdentity() {
    return cellGlobalIdentity;
  }

  public void setCellGlobalIdentity(byte[] cellGlobalIdentity) {
    this.cellGlobalIdentity = cellGlobalIdentity;
  }

  public UTRANPositioningInfoAvp getUtranPositioningInfoAvp() {
    return utranPositioningInfoAvp;
  }

  public void setUtranPositioningInfoAvp(UTRANPositioningInfoAvp utranPositioningInfoAvp) {
    this.utranPositioningInfoAvp = utranPositioningInfoAvp;
  }

  public byte[] getUtranPositioningData() {
    return utranPositioningData;
  }

  public void setUtranPositioningData(byte[] utranPositioningData) {
    this.utranPositioningData = utranPositioningData;
  }

  public byte[] getUtranGANSSPositioningData() {
    return utranGANSSPositioningData;
  }

  public void setUtranGANSSPositioningData(byte[] utranGANSSPositioningData) {
    this.utranGANSSPositioningData = utranGANSSPositioningData;
  }

  public byte[] getUtranAdditionalPositioningData() {
    return utranAdditionalPositioningData;
  }

  public void setUtranAdditionalPositioningData(byte[] utranAdditionalPositioningData) {
    this.utranAdditionalPositioningData = utranAdditionalPositioningData;
  }

  public byte[] getServiceAreaIdentity() {
    return serviceAreaIdentity;
  }

  public void setServiceAreaIdentity(byte[] serviceAreaIdentity) {
    this.serviceAreaIdentity = serviceAreaIdentity;
  }

  public Long getLcsServiceTypeId() {
    return lcsServiceTypeId;
  }

  public void setLcsServiceTypeId(Long lcsServiceTypeId) {
    this.lcsServiceTypeId = lcsServiceTypeId;
  }

  public PseudonymIndicator getPseudonymIndicator() {
    return pseudonymIndicator;
  }

  public void setPseudonymIndicator(PseudonymIndicator pseudonymIndicator) {
    this.pseudonymIndicator = pseudonymIndicator;
  }

  public LCSQoSClass getLcsQoSClass() {
    return lcsQoSClass;
  }

  public void setLcsQoSClass(LCSQoSClass lcsQoSClass) {
    this.lcsQoSClass = lcsQoSClass;
  }

  public ServingNodeAvp getServingNodeAvp() {
    return servingNodeAvp;
  }

  public void setServingNodeAvp(ServingNodeAvp servingNodeAvp) {
    this.servingNodeAvp = servingNodeAvp;
  }

  public Address getServingNodeGmlcAddress() {
    return servingNodeGmlcAddress;
  }

  public void setServingNodeGmlcAddress(Address servingNodeGmlcAddress) {
    this.servingNodeGmlcAddress = servingNodeGmlcAddress;
  }

  public Address getGmlcAddress() {
    return gmlcAddress;
  }

  public void setGmlcAddress(Address gmlcAddress) {
    this.gmlcAddress = gmlcAddress;
  }

  public Long getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Long sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public Long getLrrFlags() {
    return lrrFlags;
  }

  public void setLrrFlags(Long lrrFlags) {
    this.lrrFlags = lrrFlags;
  }

  public byte[] getLcsReferenceNumber() {
    return lcsReferenceNumber;
  }

  public void setLcsReferenceNumber(byte[] lcsReferenceNumber) {
    this.lcsReferenceNumber = lcsReferenceNumber;
  }

  public DeferredMTLRDataAvp getDeferredMTLRDataAvp() {
    return deferredMTLRDataAvp;
  }

  public void setDeferredMTLRDataAvp(DeferredMTLRDataAvp deferredMTLRDataAvp) {
    this.deferredMTLRDataAvp = deferredMTLRDataAvp;
  }

  public Long getDeferredLocationType() {
    return deferredLocationType;
  }

  public void setDeferredLocationType(Long deferredLocationType) {
    this.deferredLocationType = deferredLocationType;
  }

  public Long getTerminationCause() {
    return terminationCause;
  }

  public void setTerminationCause(Long terminationCause) {
    this.terminationCause = terminationCause;
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

  public ESMLCCellInfoAvp getEsmlcCellInfoAvp() {
    return esmlcCellInfoAvp;
  }

  public void setEsmlcCellInfoAvp(ESMLCCellInfoAvp esmlcCellInfoAvp) {
    this.esmlcCellInfoAvp = esmlcCellInfoAvp;
  }

  public Long getCellPortionId() {
    return cellPortionId;
  }

  public void setCellPortionId(Long cellPortionId) {
    this.cellPortionId = cellPortionId;
  }

  public byte[] getOneXRttRcid() {
    return oneXRttRcid;
  }

  public void setOneXRttRcid(byte[] oneXRttRcid) {
    this.oneXRttRcid = oneXRttRcid;
  }

  public DelayedLocationReportingDataAvp getDelayedLocationReportingDataAvp() {
    return delayedLocationReportingDataAvp;
  }

  public void setDelayedLocationReportingDataAvp(DelayedLocationReportingDataAvp delayedLocationReportingDataAvp) {
    this.delayedLocationReportingDataAvp = delayedLocationReportingDataAvp;
  }

  public String getCivicAddress() {
    return civicAddress;
  }

  public void setCivicAddress(String civicAddress) {
    this.civicAddress = civicAddress;
  }

  public Long getBarometricPressure() {
    return barometricPressure;
  }

  public void setBarometricPressure(Long barometricPressure) {
    this.barometricPressure = barometricPressure;
  }

  public String getAmfInstanceId() {
    return amfInstanceId;
  }

  public void setAmfInstanceId(String amfInstanceId) {
    this.amfInstanceId = amfInstanceId;
  }

  public Integer getOriginatorLcsReferenceNumber() {
    return originatorLcsReferenceNumber;
  }

  public void setOriginatorLcsReferenceNumber(Integer originatorLcsReferenceNumber) {
    this.originatorLcsReferenceNumber = originatorLcsReferenceNumber;
  }

}
