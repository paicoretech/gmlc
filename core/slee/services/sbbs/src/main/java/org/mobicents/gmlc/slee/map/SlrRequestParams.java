package org.mobicents.gmlc.slee.map;

import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.service.lsm.AccuracyFulfilmentIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredmtlrData;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.GeranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.PositioningDataInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMNList;
import org.restcomm.protocols.ss7.map.api.service.lsm.SLRArgExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.ServingNodeAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranAdditionalPositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranCivicAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranPositioningDataInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SlrRequestParams  implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * subscriberLocationReport OPERATION ::= { --Timer m ARGUMENT
   *   SubscriberLocationReport-Arg RESULT SubscriberLocationReport-Res
   *   ERRORS { systemFailure | dataMissing | resourceLimitation | unexpectedDataValue | unknownSubscriber |
   *   unauthorizedRequestingNetwork | unknownOrUnreachableLCSClient} CODE local:86 }

   *  SubscriberLocationReport-Arg ::= SEQUENCE {
   *  lcs-Event                              LCS-Event,
   *  lcs-ClientID                           LCS-ClientID,
   *  lcsLocationInfo                        LCSLocationInfo,
   *  msisdn                                 [0] ISDN-AddressString OPTIONAL,
   *  imsi                                   [1] IMSI  OPTIONAL,
   *  imei                                   [2] IMEI  OPTIONAL,
   *  na-ESRD                                [3] ISDN-AddressString OPTIONAL,
   *  na-ESRK                                [4] ISDN-AddressString OPTIONAL,
   *  locationEstimate                       [5] Ext-GeographicalInformation OPTIONAL,
   *  ageOfLocationEstimate                  [6] AgeOfLocationInformation OPTIONAL,
   *  slr-ArgExtensionContainer              [7] SLR-ArgExtensionContainer OPTIONAL,
   *  ...,
   *  add-LocationEstimate                   [8] Add-GeographicalInformation OPTIONAL,
   *  deferredmt-lrData                      [9] Deferredmt-lrData OPTIONAL,
   *  lcs-ReferenceNumber                    [10] LCS-ReferenceNumber OPTIONAL,
   *  geranPositioningData                   [11] PositioningDataInformation OPTIONAL,
   *  utranPositioningData                   [12] UtranPositioningDataInfo OPTIONAL,
   *  cellIdOrSai                            [13] CellGlobalIdOrServiceAreaIdOrLAI OPTIONAL,
   *  h-gmlc-Address                         [14] GSN-Address OPTIONAL,
   *  lcsServiceTypeID                       [15] LCSServiceTypeID OPTIONAL,
   *  sai-Present                            [17] NULL OPTIONAL,
   *  pseudonymIndicator                     [18] NULL  OPTIONAL,
   *  accuracyFulfilmentIndicator            [19] AccuracyFulfilmentIndicator OPTIONAL,
   *  velocityEstimate                       [20] VelocityEstimate OPTIONAL,
   *  sequenceNumber                         [21] SequenceNumber OPTIONAL,
   *  periodicLDRInfo                        [22] PeriodicLDRInfo OPTIONAL,
   *  mo-lrShortCircuitIndicator             [23] NULL  OPTIONAL,
   *  geranGANSSpositioningData              [24] GeranGANSSpositioningData OPTIONAL,
   *  utranGANSSpositioningData              [25] UtranGANSSpositioningData OPTIONAL,
   *  targetServingNodeForHandover           [26] ServingNodeAddress OPTIONAL,
   *  utranAdditionalPositioningData         [27] UtranAdditionalPositioningData OPTIONAL,
   *  utranBaroPressureMeas                  [28] UtranBaroPressureMeas OPTIONAL,
   *  utranCivicAddress                      [29] UtranCivicAddress OPTIONAL }
   */
  private LCSEvent lcsEvent;
  private LCSClientID lcsClientID;
  private LCSLocationInfo lcsLocationInfo;
  private IMSI imsi;
  private ISDNAddressString msisdn;
  private IMEI imei;
  private ISDNAddressString naESRD, naESRK;
  private ExtGeographicalInformation locationEstimate;
  private Integer ageOfLocationEstimate;
  private SLRArgExtensionContainer slrArgExtensionContainer;
  private PositioningDataInformation geranPositioningDataInformation;
  private UtranPositioningDataInfo utranPositioningDataInfo;
  private GeranGANSSpositioningData geranGANSSpositioningData;
  private UtranGANSSpositioningData utranGANSSpositioningData;
  private LMSI lmsi;
  private boolean gprsNodeIndicator;
  private AddGeographicalInformation additionalLocationEstimate;
  private DeferredmtlrData deferredmtlrData;
  private Integer lcsReferenceNumber;
  private CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
  private boolean saiPresent;
  private GSNAddress hGmlcAddress;
  private Integer lcsServiceTypeID;
  private boolean pseudonymIndicator;
  private AccuracyFulfilmentIndicator accuracyFulfilmentIndicator;
  private Integer sequenceNumber;
  private PeriodicLDRInfo periodicLDRInfo;
  private boolean moLrShortCircuitIndicator;
  private ReportingPLMNList reportingPLMNList;
  private VelocityEstimate velocityEstimate;
  private ServingNodeAddress targetServingNodeForHandover;
  private UtranAdditionalPositioningData utranAdditionalPositioningData;
  private Integer utranBaroPressureMeas;
  private UtranCivicAddress utranCivicAddress;

  public SlrRequestParams() {
    super();
  }

  public LCSEvent getLcsEvent() {
    return lcsEvent;
  }

  public void setLcsEvent(LCSEvent lcsEvent) {
    this.lcsEvent = lcsEvent;
  }

  public LCSClientID getLcsClientID() {
    return lcsClientID;
  }

  public void setLcsClientID(LCSClientID lcsClientID) {
    this.lcsClientID = lcsClientID;
  }

  public LCSLocationInfo getLcsLocationInfo() {
    return lcsLocationInfo;
  }

  public void setLcsLocationInfo(LCSLocationInfo lcsLocationInfo) {
    this.lcsLocationInfo = lcsLocationInfo;
  }

  public IMSI getImsi() {
    return imsi;
  }

  public void setImsi(IMSI imsi) {
    this.imsi = imsi;
  }

  public ISDNAddressString getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(ISDNAddressString msisdn) {
    this.msisdn = msisdn;
  }

  public IMEI getImei() {
    return imei;
  }

  public void setImei(IMEI imei) {
    this.imei = imei;
  }

  public ExtGeographicalInformation getLocationEstimate() {
    return locationEstimate;
  }

  public void setLocationEstimate(ExtGeographicalInformation locationEstimate) {
    this.locationEstimate = locationEstimate;
  }

  public Integer getAgeOfLocationEstimate() {
    return ageOfLocationEstimate;
  }

  public void setAgeOfLocationEstimate(Integer ageOfLocationEstimate) {
    this.ageOfLocationEstimate = ageOfLocationEstimate;
  }

  public SLRArgExtensionContainer getSlrArgExtensionContainer() {
    return slrArgExtensionContainer;
  }

  public void setSlrArgExtensionContainer(SLRArgExtensionContainer slrArgExtensionContainer) {
    this.slrArgExtensionContainer = slrArgExtensionContainer;
  }

  public PositioningDataInformation getGeranPositioningDataInformation() {
    return geranPositioningDataInformation;
  }

  public void setGeranPositioningDataInformation(PositioningDataInformation geranPositioningDataInformation) {
    this.geranPositioningDataInformation = geranPositioningDataInformation;
  }

  public UtranPositioningDataInfo getUtranPositioningDataInfo() {
    return utranPositioningDataInfo;
  }

  public void setUtranPositioningDataInfo(UtranPositioningDataInfo utranPositioningDataInfo) {
    this.utranPositioningDataInfo = utranPositioningDataInfo;
  }

  public GeranGANSSpositioningData getGeranGANSSpositioningData() {
    return geranGANSSpositioningData;
  }

  public void setGeranGANSSpositioningData(GeranGANSSpositioningData geranGANSSpositioningData) {
    this.geranGANSSpositioningData = geranGANSSpositioningData;
  }

  public UtranGANSSpositioningData getUtranGANSSpositioningData() {
    return utranGANSSpositioningData;
  }

  public void setUtranGANSSpositioningData(UtranGANSSpositioningData utranGANSSpositioningData) {
    this.utranGANSSpositioningData = utranGANSSpositioningData;
  }

  public LMSI getLmsi() {
    return lmsi;
  }

  public void setLmsi(LMSI lmsi) {
    this.lmsi = lmsi;
  }

  public Boolean isGprsNodeIndicator() {
    return gprsNodeIndicator;
  }

  public void setGprsNodeIndicator(Boolean gprsNodeIndicator) {
    this.gprsNodeIndicator = gprsNodeIndicator;
  }

  public Boolean isSaiPresent() {
    return saiPresent;
  }

  public void setSaiPresent(Boolean saiPresent) {
    this.saiPresent = saiPresent;
  }

  public AddGeographicalInformation getAdditionalLocationEstimate() {
    return additionalLocationEstimate;
  }

  public void setAdditionalLocationEstimate(AddGeographicalInformation additionalLocationEstimate) {
    this.additionalLocationEstimate = additionalLocationEstimate;
  }

  public DeferredmtlrData getDeferredmtlrData() {
    return deferredmtlrData;
  }

  public void setDeferredmtlrData(DeferredmtlrData deferredmtlrData) {
    this.deferredmtlrData = deferredmtlrData;
  }

  public Integer getLcsReferenceNumber() {
    return lcsReferenceNumber;
  }

  public void setLcsReferenceNumber(Integer lcsReferenceNumber) {
    this.lcsReferenceNumber = lcsReferenceNumber;
  }

  public CellGlobalIdOrServiceAreaIdOrLAI getCellGlobalIdOrServiceAreaIdOrLAI() {
    return cellGlobalIdOrServiceAreaIdOrLAI;
  }

  public void setCellGlobalIdOrServiceAreaIdOrLAI(CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI) {
    this.cellGlobalIdOrServiceAreaIdOrLAI = cellGlobalIdOrServiceAreaIdOrLAI;
  }

  public Boolean getGprsNodeIndicator() {
    return gprsNodeIndicator;
  }

  public Boolean getSaiPresent() {
    return saiPresent;
  }

  public Boolean getMoLrShortCircuitIndicator() {
    return moLrShortCircuitIndicator;
  }

  public GSNAddress gethGmlcAddress() {
    return hGmlcAddress;
  }

  public void sethGmlcAddress(GSNAddress hGmlcAddress) {
    this.hGmlcAddress = hGmlcAddress;
  }

  public Integer getLcsServiceTypeID() {
    return lcsServiceTypeID;
  }

  public void setLcsServiceTypeID(Integer lcsServiceTypeID) {
    this.lcsServiceTypeID = lcsServiceTypeID;
  }

  public Boolean getPseudonymIndicator() {
    return pseudonymIndicator;
  }

  public void setPseudonymIndicator(Boolean pseudonymIndicator) {
    this.pseudonymIndicator = pseudonymIndicator;
  }

  public AccuracyFulfilmentIndicator getAccuracyFulfilmentIndicator() {
    return accuracyFulfilmentIndicator;
  }

  public void setAccuracyFulfilmentIndicator(AccuracyFulfilmentIndicator accuracyFulfilmentIndicator) {
    this.accuracyFulfilmentIndicator = accuracyFulfilmentIndicator;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public PeriodicLDRInfo getPeriodicLDRInfo() {
    return periodicLDRInfo;
  }

  public void setPeriodicLDRInfo(PeriodicLDRInfo periodicLDRInfo) {
    this.periodicLDRInfo = periodicLDRInfo;
  }

  public Boolean isMoLrShortCircuitIndicator() {
    return moLrShortCircuitIndicator;
  }

  public void setMoLrShortCircuitIndicator(Boolean moLrShortCircuitIndicator) {
    this.moLrShortCircuitIndicator = moLrShortCircuitIndicator;
  }

  public ReportingPLMNList getReportingPLMNList() {
    return reportingPLMNList;
  }

  public void setReportingPLMNList(ReportingPLMNList reportingPLMNList) {
    this.reportingPLMNList = reportingPLMNList;
  }

  public VelocityEstimate getVelocityEstimate() {
    return velocityEstimate;
  }

  public void setVelocityEstimate(VelocityEstimate velocityEstimate) {
    this.velocityEstimate = velocityEstimate;
  }

  public ServingNodeAddress getTargetServingNodeForHandover() {
    return targetServingNodeForHandover;
  }

  public void setTargetServingNodeForHandover(ServingNodeAddress targetServingNodeForHandover) {
    this.targetServingNodeForHandover = targetServingNodeForHandover;
  }

  public UtranAdditionalPositioningData getUtranAdditionalPositioningData() {
    return utranAdditionalPositioningData;
  }

  public void setUtranAdditionalPositioningData(UtranAdditionalPositioningData utranAdditionalPositioningData) {
    this.utranAdditionalPositioningData = utranAdditionalPositioningData;
  }

  public Integer getUtranBaroPressureMeas() {
    return utranBaroPressureMeas;
  }

  public void setUtranBaroPressureMeas(Integer utranBaroPressureMeas) {
    this.utranBaroPressureMeas = utranBaroPressureMeas;
  }

  public UtranCivicAddress getUtranCivicAddress() {
    return utranCivicAddress;
  }

  public void setUtranCivicAddress(UtranCivicAddress utranCivicAddress) {
    this.utranCivicAddress = utranCivicAddress;
  }

  public ISDNAddressString getNaESRD() {
    return naESRD;
  }

  public void setNaESRD(ISDNAddressString naESRD) {
    this.naESRD = naESRD;
  }

  public ISDNAddressString getNaESRK() {
    return naESRK;
  }

  public void setNaESRK(ISDNAddressString naESRK) {
    this.naESRK = naESRK;
  }

  @Override
  public String toString() {
    return "SlrRequestValues{" +
        "lcsEvent=" + lcsEvent +
        ", lcsClientID=" + lcsClientID +
        ", lcsLocationInfo=" + lcsLocationInfo +
        ", imsi=" + imsi +
        ", msisdn=" + msisdn +
        ", imei=" + imei +
        ", naESRD=" + naESRD +
        ", naESRK=" + naESRK +
        ", locationEstimate=" + locationEstimate +
        ", geranPositioningDataInformation=" + geranPositioningDataInformation +
        ", utranPositioningDataInfo=" + utranPositioningDataInfo +
        ", geranGANSSpositioningData=" + geranGANSSpositioningData +
        ", utranGANSSpositioningData=" + utranGANSSpositioningData +
        ", ageOfLocationEstimate=" + ageOfLocationEstimate +
        ", lmsi=" + lmsi +
        ", gprsNodeIndicator=" + gprsNodeIndicator +
        ", additionalLocationEstimate=" + additionalLocationEstimate +
        ", deferredmtlrData=" + deferredmtlrData +
        ", lcsReferenceNumber=" + lcsReferenceNumber +
        ", cellGlobalIdOrServiceAreaIdOrLAI=" + cellGlobalIdOrServiceAreaIdOrLAI +
        ", saiPresent=" + saiPresent +
        ", hGmlcAddress=" + hGmlcAddress +
        ", lcsServiceTypeID=" + lcsServiceTypeID +
        ", pseudonymIndicator=" + pseudonymIndicator +
        ", accuracyFulfilmentIndicator=" + accuracyFulfilmentIndicator +
        ", sequenceNumber=" + sequenceNumber +
        ", periodicLDRInfo=" + periodicLDRInfo +
        ", moLrShortCircuitIndicator=" + moLrShortCircuitIndicator +
        ", reportingPLMNList=" + reportingPLMNList +
        ", velocityEstimate=" + velocityEstimate +
        ", targetServingNodeForHandover=" + targetServingNodeForHandover +
        '}';
  }
}
