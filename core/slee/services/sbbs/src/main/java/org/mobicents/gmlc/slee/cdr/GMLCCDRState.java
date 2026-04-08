package org.mobicents.gmlc.slee.cdr;

import com.google.common.collect.Multimap;
import net.java.slee.resource.diameter.slg.events.avp.LCSFormatIndicator;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import net.java.slee.resource.diameter.slg.events.avp.LocationEvent;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.mobicents.gmlc.slee.diameter.sh.LocalTimeZone;
import org.mobicents.gmlc.slee.primitives.EUTRANCGI;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningData;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningDataImpl;
import org.mobicents.gmlc.slee.primitives.EllipsoidPoint;
import org.mobicents.gmlc.slee.primitives.LocationInformation5GS;
import org.mobicents.gmlc.slee.primitives.NRCellGlobalId;
import org.mobicents.gmlc.slee.primitives.Polygon;
import org.mobicents.gmlc.slee.primitives.PolygonImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;

import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientID;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.PositioningDataInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranAdditionalPositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranCivicAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranPositioningDataInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.GeranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.AccuracyFulfilmentIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;
import org.restcomm.protocols.ss7.map.api.service.lsm.ServingNodeAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoS;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredmtlrData;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMNList;

import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRTAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;

import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents state associated with ongoing dialog required for proper CDR generation.
 * Data which should be used for CDR is spread across many objects.
 * So, we need an object which can be used to store them in one convenient place.
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 * @author <a href="mailto:bbaranow@redhat.com"> Bartosz Baranowski </a>
 */
public class GMLCCDRState implements Serializable {

  private static final long serialVersionUID = -1L;

  private static final Logger logger = Logger.getLogger(GMLCCDRState.class.getName());

  //public static final String GMLC_STRING_SEPARATOR = "|";

  protected boolean initiated, generated;

  protected String id;

  protected String curlUser;

  protected RecordStatus recordStatus;

  protected Long statusCode;

  // Dialog params
  protected Long localDialogId, remoteDialogId;
  //NB: once we fully update to JDK8, we should revert to using standard java.time package
  protected DateTime dialogStartTime, dialogEndTime;
  protected Long dialogDuration;

  // Circuit-Switched Core Network / SS7 params
  protected AddressString origReference, destReference;

  protected IMSI imsi;
  protected AddressString vlrAddress;

  protected ISDNAddressString isdnAddressString;
  protected SccpAddress localAddress, remoteAddress;

  // Evolved Packet-Switched Core Network / LTE Diameter params
  protected net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginHost;
  protected net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginRealm;
  protected int diameterOriginPort;
  protected net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestHost;
  protected net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestRealm;
  protected int diameterDestPort;
  protected String diameterSessionId;

  // SUPL TLS Socket parameters
  protected InetAddress slpSocketAddress;
  protected int slpSocketPort;
  protected InetAddress setSocketAddress;
  protected int setSocketPort;

  // MAP ATI and PSI response parameters
  protected SubscriberInfo subscriberInfo;
  protected LocationInformation locationInformation;
  protected PSSubscriberState epsSubscriberState;
  protected LocationInformationEPS locationInformationEPS;
  protected LocationInformationGPRS locationInformationGPRS;
  protected Boolean saiPresent;
  protected Boolean currentLocationRetrieved;
  protected MSClassmark2 msClassmark2;
  protected GPRSMSClass gprsmsClass;
  protected TAId taId;
  protected EUTRANCGI eUtranCgi;
  protected LocationNumberMap locationNumberMap;
  protected MNPInfoRes mnpInfoRes;

  // MAP LSM operations exclusive parameters.
  protected LCSClientID lcsClientID;
  protected LMSI lmsi;
  protected ISDNAddressString networkNodeNumber;
  protected Boolean gprsNodeIndicator;
  protected AdditionalNumber additionalNumber;
  protected ISDNAddressString mscNumber, sgsnNumber;
  protected DiameterIdentity mmeName, mmeRealm, sgsnName, sgsnRealm, aaaServerName;
  protected SupportedLCSCapabilitySets supportedLCSCapabilitySets; // MAP
  protected Long lcsCapabilitiesSets; // Diameter
  protected GSNAddress hGmlcAddress, vGmlcAddress, pprAddress;
  protected ExtGeographicalInformation locationEstimate;
  protected TypeOfShape typeOfShape;
  protected Polygon polygon;
  protected Integer numberOfPoints;
  EllipsoidPoint[] polygonEllipsoidPoints;
  protected Polygon additionalPolygon;
  protected Integer additionalNumberOfPoints;
  EllipsoidPoint[] additionalPolygonEllipsoidPoints;
  protected Boolean moLrShortCircuitIndicator;
  protected PositioningDataInformation geranPositioningDataInformation;
  protected UtranPositioningDataInfo utranPositioningDataInfo;
  protected GeranGANSSpositioningData geranGANSSpositioningData;
  protected UtranGANSSpositioningData utranGANSSpositioningData;
  protected Integer ageOfLocationEstimate;
  protected AddGeographicalInformation additionalLocationEstimate;
  protected Boolean deferredMTLRResponseIndicator;
  protected CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
  protected AccuracyFulfilmentIndicator accuracyFulfilmentIndicator;
  protected VelocityEstimate velocityEstimate;
  protected ServingNodeAddress servingNodeAddress;
  protected LCSQoS lcsQoS;
  protected Integer lcsReferenceNumber;
  protected Integer clientReferenceNumber;
  protected Long barometricPressureMeasurement;
  protected String civicAddress;
  protected LCSEvent lcsEvent;
  protected ISDNAddressString msisdn;
  protected IMEI imei;
  protected DeferredmtlrData deferredmtlrData;
  protected Integer lcsServiceTypeID;
  protected Boolean pseudonymIndicator;
  protected Integer sequenceNumber;
  protected PeriodicLDRInfo periodicLDRInfo;
  protected ReportingPLMNList reportingPLMNList;
  protected UtranAdditionalPositioningData utranAdditionalPositioningData;
  protected Integer utranBaroPressureMeas;
  protected UtranCivicAddress utranCivicAddress;
  protected Boolean naEsrkRequest;
  protected ISDNAddressString naESRD;
  protected ISDNAddressString naESRK;

  // EPC Network / LTE params
  // LTE LCS operations parameters (not analogue to SS7 location services parameters)
  protected String lcsEpsClientName;
  protected LCSFormatIndicator lcsEpsClientFormatIndicator;
  protected EUTRANPositioningData eutranPositioningData;
  protected String cellGlobalIdentity;
  protected String serviceAreaIdentity;
  protected Long cellPortionId;
  protected LocationEvent locationEvent;
  protected LCSQoSClass lteLcsQoSClass;
  protected String oneXRTTRCID;
  protected Long riaFlags;
  protected Long plrFlags;
  protected Long plaFlags;
  protected Long lrrFlags;
  protected Long lraFlags;
  protected String amfInstanceId;

  // 5GS params for Diameter Sh
  LocationInformation5GS locationInformation5GS;
  protected NRCellGlobalId nrCellGlobalId;
  protected Integer ageOfLocationInformation;
  protected String amfAddress;
  protected String smsfAddress;
  protected PlmnId visitedPlmnId;
  protected LocalTimeZone localTimeZone;
  protected Integer ratType;

  /**
   * From MAP v18.0.0
   * LocationInformation5GS ::= SEQUENCE {
   *    nrCellGlobalIdentity          [0] NR-CGI OPTIONAL,
   *    e-utranCellGlobalIdentity     [1] E-UTRAN-CGI OPTIONAL,
   *    geographicalInformation       [2] GeographicalInformation OPTIONAL,
   *    geodeticInformation           [3] GeodeticInformation OPTIONAL,
   *    amf-address                   [4] FQDN OPTIONAL,
   *    trackingAreaIdentity          [5] TA-Id OPTIONAL,
   *    currentLocationRetrieved      [6] NULL OPTIONAL,
   *    ageOfLocationInformation      [7] AgeOfLocationInformation OPTIONAL,
   *    vplmnId                       [8] PLMN-Id OPTIONAL,
   *    localtimeZone                 [9] TimeZone OPTIONAL,
   *    rat-Type                      [10] Used-RAT-Type OPTIONAL,
   *    extensionContainer            [11] ExtensionContainer OPTIONAL,
   *    ...,
   *    nrTrackingAreaIdentity        [12] NR-TA-Id OPTIONAL
   *  }
   */
  private org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS locationInformation5GSFromMap;
  private org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRCellGlobalId nrCellGlobalIdentity;
  private EUtranCgi eUtranCellGlobalId;
  private TAId trackingAreaId;
  private NRTAId nrTrackingAreaIdentity;
  private FQDN amfAddressFromMap;
  private PlmnId vPlmnId;
  private UsedRATType usedRATType;
  private Time lastUEActivityTime;
  private UsedRATType lastRATType;



  /****************/
  /*** GETTERS ***/
  /**************/

  /**
   * @return the curl username
   */
  public String getCurlUser() {
    return curlUser;
  }

  /**
   * @return the MAP or Diameter status code
   */
  public Long getStatusCode() {
    return statusCode;
  }

  /**
   * @return the id for MAP
   */
  public String getId() {
    return id;
  }

  /**
   * @return the initiated
   */
  public boolean isInitiated() {
    return initiated;
  }

  /**
   * @return the initiated
   */
  public boolean isInitialized() {
    return this.initiated;
  }

  /**
   * @return the generated
   */
  public boolean isGenerated() {
    return generated;
  }

  /**
   * @return the origReference
   */
  public AddressString getOrigReference() {
    return origReference;
  }

  /**
   * @return the destReference
   */
  public AddressString getDestReference() {
    return destReference;
  }

  /**
   * @return the imsi
   */
  public IMSI getImsi() {
    return imsi;
  }

  /**
   * @return the vlrAddress
   */
  public AddressString getVlrAddress() {
    return vlrAddress;
  }

  /**
   * @return the ISDNAddressString
   */
  public ISDNAddressString getISDNAddressString() {
    return isdnAddressString;
  }

  /**
   * @return the ISDNAddressString
   */
  public ISDNAddressString getIsdnAddressString() {
    return isdnAddressString;
  }

  /**
   * @return the localAddress (GMLC)
   */
  public SccpAddress getLocalAddress() {
    return localAddress;
  }

  /**
   * @return the remoteAddress (Core Network Entity responding address)
   */
  public SccpAddress getRemoteAddress() {
    return remoteAddress;
  }

  /**
   * @return the Diameter session id
   */
  public String getDiameterSessionId() {
    return diameterSessionId;
  }

  /**
   * @return the Diameter Origin Host (EPC Diameter origin host)
   */
  public net.java.slee.resource.diameter.base.events.avp.DiameterIdentity getDiameterOriginHost() {
    return diameterOriginHost;
  }

  /**
   * @return the Diameter Origin Host (EPC Diameter origin realm)
   */
  public net.java.slee.resource.diameter.base.events.avp.DiameterIdentity getDiameterOriginRealm() {
    return diameterOriginRealm;
  }

  /**
   * @return the Diameter Origin Port (EPC Diameter origin port)
   */
  public int getDiameterOriginPort() {
    return diameterOriginPort;
  }

  /**
   * @return the Diameter Destination Host (EPC Diameter destination host)
   */
  public net.java.slee.resource.diameter.base.events.avp.DiameterIdentity getDiameterDestHost() {
    return diameterDestHost;
  }

  /**
   * @return the Diameter Destination Realm (EPC Diameter destination realm)
   */
  public net.java.slee.resource.diameter.base.events.avp.DiameterIdentity getDiameterDestRealm() {
    return diameterDestRealm;
  }

  /**
   * @return the Diameter Destination Port (EPC Diameter destination port)
   */
  public Integer getDiameterDestPort() {
    return diameterDestPort;
  }

  /**
   * @return the SLP socket InetAddress (GMLC)
   */
  public InetAddress getSlpSocketAddress() {
    return slpSocketAddress;
  }

  /**
   * @return the SLP socket port (GMLC)
   */
  public int getSlpSocketPort() {
    return slpSocketPort;
  }

  /**
   * @return the SET socket InetAddress
   */
  public InetAddress getSetSocketAddress() {
    return setSocketAddress;
  }

  /**
   * @return the SET socket port
   */
  public int getSetSocketPort() {
    return setSocketPort;
  }

  /**
   * @return the localDialogId
   */
  public Long getLocalDialogId() {
    return localDialogId;
  }

  /**
   * @return the remoteDialogId
   */
  public Long getRemoteDialogId() {
    return this.remoteDialogId;
  }

  /**
   * @return the dialogStartTime
   */
  public DateTime getDialogStartTime() {
    return this.dialogStartTime;
  }

  /**
   * @return dialogEndTime
   */
  public DateTime getDialogEndTime() {
    return this.dialogEndTime;
  }

  /**
   * @return dialogDuration to set
   */
  public Long getDialogDuration() {
    return this.dialogDuration;
  }

  /**
   * @return the recordStatus
   */
  public RecordStatus getRecordStatus() {
    return recordStatus;
  }

  /**
   * @return the target subscriber MS Classmark from MAP ATI
   */
  public MSClassmark2 getMsClassmark2() {
    return msClassmark2;
  }

  /**
   * @return the target subscriber GPRS MS Class from MAP ATI
   */
  public GPRSMSClass getGprsmsClass() {
    return gprsmsClass;
  }

  /**
   * @return the LCS Client ID
   */
  public LCSClientID getLcsClientID() {
    return lcsClientID;
  }

  /**
   * @return the Cell Global Identity
   */
  public String getCellGlobalIdentity() {
    return cellGlobalIdentity;
  }

  /**
   * @return the Service Area Identity
   */
  public String getServiceAreaIdentity() {
    return serviceAreaIdentity;
  }

  /**
   * @return the LMSI
   */
  public LMSI getLmsi() {
    return lmsi;
  }

  /**
   * @return the Network Node Number
   */
  public ISDNAddressString getNetworkNodeNumber() {
    return networkNodeNumber;
  }

  public void setMscNumber(ISDNAddressString mscNumber) {
    this.mscNumber = mscNumber;
  }

  public void setSgsnNumber(ISDNAddressString sgsnNumber) {
    this.sgsnNumber = sgsnNumber;
  }

  /**
   * @return GPRS Node Indicator
   */
  public Boolean isGprsNodeIndicator() {
    return gprsNodeIndicator;
  }

  /**
   * @return the Additional Number
   */
  public AdditionalNumber getAdditionalNumber() {
    return additionalNumber;
  }

  /**
   * @return the Additional MSC Number
   */
  public ISDNAddressString getMscNumber() {
    return mscNumber;
  }

  /**
   * @return the Additional MSC Number
   */
  public ISDNAddressString getSgsnNumber() {
    return sgsnNumber;
  }

  /**
   * @return the MME Name
   */
  public DiameterIdentity getMmeRealm() {
    return mmeRealm;
  }

  /**
   * @return the MME Name
   */
  public DiameterIdentity getMmeName() {
    return mmeName;
  }

  /**
   * @return the SGSN Name
   */
  public DiameterIdentity getSgsnName() {
    return sgsnName;
  }

  /**
   * @return the SGSN Realm
   */
  public DiameterIdentity getSgsnRealm() {
    return sgsnRealm;
  }

  /**
   * @return the AAA Server Name
   */
  public DiameterIdentity getAaaServerName() {
    return aaaServerName;
  }

  /**
   * @return the LCS Capability Sets (MAP)
   */
  public SupportedLCSCapabilitySets getLcsCapabilitySets() {
    return supportedLCSCapabilitySets;
  }

  /**
   *
   * @return the LCs Capabilities Sets (Diameter)
   */
  public Long getLcsCapabilitiesSets() {
    return lcsCapabilitiesSets;
  }

  /**
   * @return the Home GMLC Address
   */
  public GSNAddress gethGmlcAddress() {
    return hGmlcAddress;
  }

  /**
   * @return the Visited GMLC Address
   */
  public GSNAddress getvGmlcAddress() {
    return vGmlcAddress;
  }

  /**
   * @return the PPR Address
   */
  public GSNAddress getPprAddress() {
    return pprAddress;
  }

  /**
   * @return the location estimate
   */
  public ExtGeographicalInformation getLocationEstimate() {
    return locationEstimate;
  }

  /**
   * @return the Polygon of the Location Estimate
   */
  public Polygon getPolygon() {
    return polygon;
  }

  /**
   * @return the number of points of the Polygon
   */
  public Integer getNumberOfPoints() {
    return numberOfPoints;
  }

  /**
   * @return the EllipsoidPoint array of the Polygon
   */
  public EllipsoidPoint[] getPolygonEllipsoidPoints() {
    return polygonEllipsoidPoints;
  }

  /**
   * @return the Polygon of the Additional Location Estimate
   */
  public Polygon getAdditionalPolygon() {
    return additionalPolygon;
  }

  /**
   * @return the number of points of the Polygon of the Additional Location Estimate
   */
  public Integer getAdditionalNumberOfPoints() {
    return additionalNumberOfPoints;
  }

  /**
   * @return the EllipsoidPoint array of the Polygon of the Additional Location Estimate
   */
  public EllipsoidPoint[] getAdditionalPolygonEllipsoidPoints() {
    return additionalPolygonEllipsoidPoints;
  }

  /**
   * @return the MO-LR Short Circuit indicator
   */
  public Boolean isMoLrShortCircuitIndicator() {
    return moLrShortCircuitIndicator;
  }

  /**
   * @return the GERAN positioning Data info
   */
  public PositioningDataInformation getGeranPositioningDataInformation() {
    return geranPositioningDataInformation;
  }

  /**
   * @return the UTRAN positioning data info
   */
  public UtranPositioningDataInfo getUtranPositioningDataInfo() {
    return utranPositioningDataInfo;
  }

  /**
   * @return the GERAN GANSS positioning data info
   */
  public GeranGANSSpositioningData getGeranGANSSpositioningData() {
    return geranGANSSpositioningData;
  }

  /**
   * @return the UTRAN GANSS positioning data info
   */
  public UtranGANSSpositioningData getUtranGANSSpositioningData() {
    return utranGANSSpositioningData;
  }

  /**
   * @return the age of location estimate
   */
  public Integer getAgeOfLocationEstimate() {
    return ageOfLocationEstimate;
  }

  /**
   * @return the additional location estimate
   */
  public AddGeographicalInformation getAdditionalLocationEstimate() {
    return additionalLocationEstimate;
  }

  /**
   * @return the deferred MT LR response indicator
   */
  public Boolean isDeferredMTLRResponseIndicator() {
    return deferredMTLRResponseIndicator;
  }

  /**
   * @return the CGI or SAI or LAI
   */
  public CellGlobalIdOrServiceAreaIdOrLAI getCellGlobalIdOrServiceAreaIdOrLAI() {
    return cellGlobalIdOrServiceAreaIdOrLAI;
  }

  /**
   * @return the accuracy fulfillment indicator
   */
  public AccuracyFulfilmentIndicator getAccuracyFulfilmentIndicator() {
    return accuracyFulfilmentIndicator;
  }

  /**
   * @return the velocity estimate
   */
  public VelocityEstimate getVelocityEstimate() {
    return velocityEstimate;
  }

  /**
   * @return the serving node address
   */
  public ServingNodeAddress getServingNodeAddress() {
    return servingNodeAddress;
  }

  /**
   * @return the LCS QoS
   */
  public LCSQoS getLcsQoS() {
    return lcsQoS;
  }

  /**
   * @return the LCS reference number
   */
  public Integer getLcsReferenceNumber() {
    return lcsReferenceNumber;
  }

  /**
   * @return the client LCS reference number
   */
  public Integer getClientReferenceNumber() {
    return clientReferenceNumber;
  }

  /**
   * @return the barometric pressure
   */
  public Long getBarometricPressureMeasurement() {
    return barometricPressureMeasurement;
  }

  /**
   * @return the civic address
   */
  public String getCivicAddress() {
    return civicAddress;
  }

  /**
   * @return the LCS event (MAP)
   */
  public LCSEvent getLcsEvent() {
    return lcsEvent;
  }

  /**
   * @return the LCS event (LTE)
   */
  public LocationEvent getLocationEvent() {
    return locationEvent;
  }

  /**
   * @return the MSISDN
   */
  public ISDNAddressString getMsisdn() {
    return msisdn;
  }

  /**
   * @return the IMEI
   */
  public IMEI getImei() {
    return imei;
  }

  /**
   * @return the deferred MT LR data
   */
  public DeferredmtlrData getDeferredmtlrData() {
    return deferredmtlrData;
  }

  /**
   * @return the LCS Service type ID
   */
  public Integer getLcsServiceTypeID() {
    return lcsServiceTypeID;
  }

  /**
   * @return the pseudonym indicator
   */
  public Boolean isPseudonymIndicator() {
    return pseudonymIndicator;
  }

  /**
   * @return the location report sequence number
   */
  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  /**
   * @return the periodic LDR info
   */
  public PeriodicLDRInfo getPeriodicLDRInfo() {
    return periodicLDRInfo;
  }

  /**
   * @return the reporting PLMN list
   */
  public ReportingPLMNList getReportingPLMNList() {
    return reportingPLMNList;
  }

  /**
   * @return the UTRAN additional positioning data
   */
  public UtranAdditionalPositioningData getUtranAdditionalPositioningData() {
    return utranAdditionalPositioningData;
  }

  /**
   * @return the UTRAN barometric pressure measurement
   */
  public Integer getUtranBaroPressureMeas() {
    return utranBaroPressureMeas;
  }

  /**
   * @return the reporting UTRAN civic address
   */
  public UtranCivicAddress getUtranCivicAddress() {
    return utranCivicAddress;
  }

  /**
   * @return if na-ESRK has been requested in MAP SLR
   */
  public Boolean getNaEsrkRequest() {
    return naEsrkRequest;
  }

  /**
   * @return the na-ESRD
   */
  public ISDNAddressString getNaESRD() {
    return naESRD;
  }

  /**
   * @return the na-ESRD
   */
  public ISDNAddressString getNaESRK() {
    return naESRK;
  }

  /**
   * @return the subscriber info
   */
  public SubscriberInfo getSubscriberInfo() {
    return subscriberInfo;
  }

  /**
   * @return the subscriber's location info
   */
  public LocationInformation getLocationInformation() {
    return locationInformation;
  }

  /**
   * @return the subscriber's EPS location info
   */
  public LocationInformationEPS getLocationInformationEPS() {
    return locationInformationEPS;
  }

  public PSSubscriberState getEpsSubscriberState() {
    return epsSubscriberState;
  }

  public void setEpsSubscriberState(PSSubscriberState epsSubscriberState) {
    this.epsSubscriberState = epsSubscriberState;
  }

  /**
   * @return the subscriber's location info GPRS
   */
  public LocationInformationGPRS getLocationInformationGPRS() {
    return locationInformationGPRS;
  }

  /**
   * @return the subscriber's geodetic or geographical type of shape
   */
  public TypeOfShape getTypeOfShape() {
    return typeOfShape;
  }

  /**
   * @return the subscriber's MNP info result
   */
  public MNPInfoRes getMnpInfoRes() {
    return mnpInfoRes;
  }

  /**
   * @return if SAI is present
   */
  public Boolean isPsiSaiPresent() {
    return saiPresent;
  }

  /**
   * @return if current location is retrieved
   */
  public Boolean isCurrentLocationRetrieved() {
    return currentLocationRetrieved;
  }

  /**
   * @return the subscriber's location Tracking Area Id
   */
  public TAId getTaId() {
    return taId;
  }

  /**
   * @return the subscriber's E-UTRAN Cell Id
   */
  public EUTRANCGI getEUtranCgi() {
    return eUtranCgi;
  }

  /**
   * @return the subscriber's E-UTRAN Cell Portion Id
   */
  public Long getCellPortionId() {
    return cellPortionId;
  }

  /**
   * @return the subscriber's information Location Number
   */
  public LocationNumberMap getLocationNumberMap() {
    return locationNumberMap;
  }

  /**
   * @return the LCS EPS Client ID Name
   */
  public String getLcsEpsClientName() {
    return lcsEpsClientName;
  }

  /**
   * @return the LCS EPS Client ID Format Indicator
   */
  public LCSFormatIndicator getLcsEpsClientFormatIndicator() {
    return lcsEpsClientFormatIndicator;
  }

  /**
   * @return the subscriber's EUTRAN positioning data
   */
  public EUTRANPositioningData getEUTRANPositioningData() {
    return eutranPositioningData;
  }

  /**
   * @return the LCS QoS Class
   */
  public LCSQoSClass getLteLcsQoSClass() {
    return lteLcsQoSClass;
  }

  /**
   * @return the oneXRTTRCID
   */
  public String getOneXRTTRCID() {
    return oneXRTTRCID;
  }

  /**
   * @return the riaFlags
   */
  public Long getRiaFlags() {
    return riaFlags;
  }

  /**
   * @return the plrFlags
   */
  public Long getPlrFlags() {
    return plrFlags;
  }

  /**
   * @return the plaFlags
   */
  public Long getPlaFlags() {
    return plaFlags;
  }

  /**
   * @return the lrrFlags
   */
  public Long getLrrFlags() {
    return lrrFlags;
  }

  /**
   * @return the lraFlags
   */
  public Long getLraFlags() {
    return lraFlags;
  }

  /**
   * @return the amfInstanceId
   */
  public String getAmfInstanceId() {
    return amfInstanceId;
  }

  /**
   * @return the LocationInformation5GS
   */
  public LocationInformation5GS getLocationInformation5GS() {
    return locationInformation5GS;
  }

  /**
   * @return the 5G NR CellGlobalId
   */
  public NRCellGlobalId getNrCellGlobalId() {
    return nrCellGlobalId;
  }

  /**
   * @return the age of Location Information
   */
  public Integer getAgeOfLocationInformation() {
    return ageOfLocationInformation;
  }

  /**
   * @return the 5G AMF Address
   */
  public String getAmfAddress() {
    return amfAddress;
  }

  /**
   * @return the 5G SMSF Address
   */
  public String getSmsfAddress() {
    return smsfAddress;
  }

  /**
   * @return the visited PLMN Id
   */
  public PlmnId getVisitedPlmnId() {
    return visitedPlmnId;
  }

  /**
   * @return the Local Time Zone
   */
  public LocalTimeZone getLocalTimeZone() {
    return localTimeZone;
  }

  /**
   * @return the RAT Type
   */
  public Integer getRatType() {
    return ratType;
  }

  /****************/
  /*** SETTERS ***/
  /**************/

  /**
   * @param curlUser the id to set
   */
  public void setCurlUser(String curlUser) {
    this.curlUser = curlUser;
  }

  /**
   * @param statusCode the MAP or Diameter status code to set
   */
  public void setStatusCode(Long statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param initiated the initiated to set
   */
  public void setInitiated(boolean initiated) {
    this.initiated = initiated;
  }

  /**
   * @param generated to set
   */
  public void setGenerated(boolean generated) {
    this.generated = generated;
  }

  /**
   * @param origReference the origReference to set
   */
  public void setOrigReference(AddressString origReference) {
    this.origReference = origReference;
  }

  /**
   * @param destReference the destReference to set
   */
  public void setDestReference(AddressString destReference) {
    this.destReference = destReference;
  }

  /**
   * @param imsi the IMSI to set
   */
  public void setImsi(IMSI imsi) {
    this.imsi = imsi;
  }

  /**
   * @param vlrAddress the VLR Address to set
   */
  public void setVlrAddress(AddressString vlrAddress) {
    this.vlrAddress = vlrAddress;
  }

  /**
   * @param iSDNString the ISDNString to set
   */
  public void setISDNAddressString(ISDNAddressString iSDNString) {
    isdnAddressString = iSDNString;
  }

  /**
   * @param isdnAddressString the ISDNString to set
   */
  public void setIsdnAddressString(ISDNAddressString isdnAddressString) {
    this.isdnAddressString = isdnAddressString;
  }

  /**
   * @param localAddress the localAddress to set
   */
  public void setLocalAddress(SccpAddress localAddress) {
    this.localAddress = localAddress;
  }

  /**
   * @param remoteAddress the remoteAddress to set
   */
  public void setRemoteAddress(SccpAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  /**
   * @param diameterSessionId the Diameter session Id to set
   */
  public void setDiameterSessionId(String diameterSessionId) {
    this.diameterSessionId = diameterSessionId;
  }

  /**
   * @param diameterOriginHost the Diameter origin host to set
   */
  public void setDiameterOriginHost(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginHost) {
    this.diameterOriginHost = diameterOriginHost;
  }

  /**
   * @param diameterOriginRealm the Diameter origin realm to set
   */
  public void setDiameterOriginRealm(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginRealm) {
    this.diameterOriginRealm = diameterOriginRealm;
  }

  /**
   * @param diameterOriginPort the Diameter origin port to set
   */
  public void setDiameterOriginPort(int diameterOriginPort) {
    this.diameterOriginPort = diameterOriginPort;
  }

  /**
   * @param diameterDestHost the Diameter destination host to set
   */
  public void setDiameterDestHost(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestHost) {
    this.diameterDestHost = diameterDestHost;
  }

  /**
   * @param diameterDestRealm the Diameter destination realm to set
   */
  public void setDiameterDestRealm(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestRealm) {
    this.diameterDestRealm = diameterDestRealm;
  }

  /**
   * @param diameterDestPort the Diameter destination port to set
   */
  public void setDiameterDestPort(int diameterDestPort) {
    this.diameterDestPort = diameterDestPort;
  }

  /**
   * @param slpSocketAddress the SLP (GMLC) socket InetAddress to set
   */
  public void setSlpSocketAddress(InetAddress slpSocketAddress) {
    this.slpSocketAddress = slpSocketAddress;
  }

  /**
   * @param slpSocketPort the SLP (GMLC) socket port to set
   */
  public void setSlpSocketPort(int slpSocketPort) {
    this.slpSocketPort = slpSocketPort;
  }

  /**
   * @param setSocketAddress the SET socket InetAddress to set
   */
  public void setSetSocketAddress(InetAddress setSocketAddress) {
    this.setSocketAddress = setSocketAddress;
  }

  /**
   * @param setSocketPort the SET socket port to set
   */
  public void setSetSocketPort(int setSocketPort) {
    this.setSocketPort = setSocketPort;
  }

  /**
   * @param localDialogId the localDialogId to set
   */
  public void setLocalDialogId(Long localDialogId) {
    this.localDialogId = localDialogId;
  }

  /**
   * @param remoteDialogId to set
   */
  public void setRemoteDialogId(Long remoteDialogId) {
    this.remoteDialogId = remoteDialogId;
  }

  /**
   * @param dialogStartTime to set
   */
  public void setDialogStartTime(DateTime dialogStartTime) {
    this.dialogStartTime = dialogStartTime;
  }

  /**
   * @param dialogEndTime to set
   */
  public void setDialogEndTime(DateTime dialogEndTime) {
    this.dialogEndTime = dialogEndTime;
  }

  /**
   * @param dialogDuration to set
   */
  public void setDialogDuration(Long dialogDuration) {
    this.dialogDuration = dialogDuration;
  }

  /**
   * @param recordStatus the recordStatus to set
   */
  public void setRecordStatus(RecordStatus recordStatus) {
    this.recordStatus = recordStatus;
  }

  /**
   * @param cellGlobalIdentity to set
   */
  public void setCellGlobalIdentity(String cellGlobalIdentity) {
    this.cellGlobalIdentity = cellGlobalIdentity;
  }

  /**
   * @param serviceAreaIdentity to set
   */
  public void setServiceAreaIdentity(String serviceAreaIdentity) {
    this.serviceAreaIdentity = serviceAreaIdentity;
  }

  /**
   * @param msClassmark2 to set
   */
  public void setMsClassmark2(MSClassmark2 msClassmark2) {
    this.msClassmark2 = msClassmark2;
  }

  /**
   * @param gprsmsClass to set
   */
  public void setGprsmsClass(GPRSMSClass gprsmsClass) {
    this.gprsmsClass = gprsmsClass;
  }

  /**
   * @param lcsClientID to set
   */
  public void setLcsClientID(LCSClientID lcsClientID) {
    this.lcsClientID = lcsClientID;
  }

  /**
   * @param lmsi to set
   */
  public void setLmsi(LMSI lmsi) {
    this.lmsi = lmsi;
  }

  /**
   * @param networkNodeNumber to set
   */
  public void setNetworkNodeNumber(ISDNAddressString networkNodeNumber) {
    this.networkNodeNumber = networkNodeNumber;
  }

  /**
   * @param gprsNodeIndicator to set
   */
  public void setGprsNodeIndicator(Boolean gprsNodeIndicator) {
    this.gprsNodeIndicator = gprsNodeIndicator;
  }

  /**
   * @param additionalNumber to set
   */
  public void setAdditionalNumber(AdditionalNumber additionalNumber) {
    this.additionalNumber = additionalNumber;
  }

  /**
   * @param mmeRealm to set
   */
  public void setMmeRealm(DiameterIdentity mmeRealm) {
    this.mmeRealm = mmeRealm;
  }

  /**
   * @param mmeName to set
   */
  public void setMmeName(DiameterIdentity mmeName) {
    this.mmeName = mmeName;
  }

  /**
   * @param sgsnName to set
   */
  public void setSgsnName(DiameterIdentity sgsnName) {
    this.sgsnName = sgsnName;
  }

  /**
   * @param sgsnRealm to set
   */
  public void setSgsnRealm(DiameterIdentity sgsnRealm) {
    this.sgsnRealm = sgsnRealm;
  }

  /**
   * @param aaaServerName to set
   */
  public void setAaaServerName(DiameterIdentity aaaServerName) {
    this.aaaServerName = aaaServerName;
  }

  /**
   *
   * @param supportedLCSCapabilitySets the serving or additional serving node LCS Capability Sets (MAP)
   */
  public void setLcsCapabilitySets(SupportedLCSCapabilitySets supportedLCSCapabilitySets) {
    this.supportedLCSCapabilitySets = supportedLCSCapabilitySets;
  }

  /**
   *
   * @param lcsCapabilitiesSets the serving or additional serving node LCS Capabilities Sets (Diameter)
   */
  public void setLcsCapabilitiesSets(Long lcsCapabilitiesSets) {
    this.lcsCapabilitiesSets = lcsCapabilitiesSets;
  }

  /**
   * @param hGmlcAddress to set
   */
  public void sethGmlcAddress(GSNAddress hGmlcAddress) {
    this.hGmlcAddress = hGmlcAddress;
  }

  /**
   * @param vGmlcAddress to set
   */
  public void setvGmlcAddress(GSNAddress vGmlcAddress) {
    this.vGmlcAddress = vGmlcAddress;
  }

  /**
   * @param pprAddress to set
   */
  public void setPprAddress(GSNAddress pprAddress) {
    this.pprAddress = pprAddress;
  }

  /**
   * @param locationEstimate to set
   */
  public void setLocationEstimate(ExtGeographicalInformation locationEstimate) {
    this.locationEstimate = locationEstimate;
    // setup for polygon type of shape case
    if (locationEstimate != null) {
      if (locationEstimate.getTypeOfShape() == TypeOfShape.Polygon) {
        this.typeOfShape = TypeOfShape.Polygon;
      }
    }
  }

  /**
   * @param polygon to set
   */
  public void setPolygon(Polygon polygon) {
    this.polygon = polygon;
  }

  /**
   * @param numberOfPoints to set
   */
  public void setNumberOfPoints(Integer numberOfPoints) {
    this.numberOfPoints = numberOfPoints;
  }

  /**
   * @param polygonEllipsoidPoints to set
   */
  public void setPolygonEllipsoidPoints(EllipsoidPoint[] polygonEllipsoidPoints) {
    this.polygonEllipsoidPoints = polygonEllipsoidPoints;
  }

  /**
   * @param additionalPolygon to set
   */
  public void setAdditionalPolygon(Polygon additionalPolygon) {
    this.additionalPolygon = additionalPolygon;
  }

  /**
   * @param additionalNumberOfPoints to set
   */
  public void setAdditionalNumberOfPoints(Integer additionalNumberOfPoints) {
    this.additionalNumberOfPoints = additionalNumberOfPoints;
  }

  /**
   * @param additionalPolygonEllipsoidPoints to set
   */
  public void setAdditionalPolygonEllipsoidPoints(EllipsoidPoint[] additionalPolygonEllipsoidPoints) {
    this.additionalPolygonEllipsoidPoints = additionalPolygonEllipsoidPoints;
  }

  /**
   * @param moLrShortCircuitIndicator to set
   */
  public void setMoLrShortCircuitIndicator(Boolean moLrShortCircuitIndicator) {
    this.moLrShortCircuitIndicator = moLrShortCircuitIndicator;
  }

  /**
   * @param geranPositioningDataInformation to set
   */
  public void setGeranPositioningDataInformation(PositioningDataInformation geranPositioningDataInformation) {
    this.geranPositioningDataInformation = geranPositioningDataInformation;
  }

  /**
   * @param utranPositioningDataInfo to set
   */
  public void setUtranPositioningDataInfo(UtranPositioningDataInfo utranPositioningDataInfo) {
    this.utranPositioningDataInfo = utranPositioningDataInfo;
  }

  /**
   * @param geranGANSSpositioningData to set
   */
  public void setGeranGANSSpositioningData(GeranGANSSpositioningData geranGANSSpositioningData) {
    this.geranGANSSpositioningData = geranGANSSpositioningData;
  }

  /**
   * @param utranGANSSpositioningData to set
   */
  public void setUtranGANSSpositioningData(UtranGANSSpositioningData utranGANSSpositioningData) {
    this.utranGANSSpositioningData = utranGANSSpositioningData;
  }

  /**
   * @param ageOfLocationEstimate to set
   */
  public void setAgeOfLocationEstimate(Integer ageOfLocationEstimate) {
    this.ageOfLocationEstimate = ageOfLocationEstimate;
  }

  /**
   * @param additionalLocationEstimate to set
   */
  public void setAdditionalLocationEstimate(AddGeographicalInformation additionalLocationEstimate) {
    this.additionalLocationEstimate = additionalLocationEstimate;
    // setup for polygon type of shape case
    if (additionalLocationEstimate != null) {
      if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.Polygon) {
        this.additionalPolygon = new PolygonImpl(this.additionalLocationEstimate.getData());
        this.additionalNumberOfPoints = additionalPolygon.getNumberOfPoints();
        this.additionalPolygonEllipsoidPoints = new EllipsoidPoint[this.additionalNumberOfPoints];
        for (int point = 0; point < this.additionalNumberOfPoints; point++) {
          this.additionalPolygonEllipsoidPoints[point] = this.additionalPolygon.getEllipsoidPoint(point);
        }
        try {
          ((PolygonImpl) this.additionalPolygon).setData(this.additionalPolygonEllipsoidPoints);
        } catch (MAPException e) {
          logger.error(e.getMessage());
        }
      }
    }
  }

  /**
   * @param deferredMTLRResponseIndicator to set
   */
  public void setDeferredMTLRResponseIndicator(Boolean deferredMTLRResponseIndicator) {
    this.deferredMTLRResponseIndicator = deferredMTLRResponseIndicator;
  }

  /**
   * @param cellGlobalIdOrServiceAreaIdOrLAI to set
   */
  public void setCellGlobalIdOrServiceAreaIdOrLAI(CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI) {
    this.cellGlobalIdOrServiceAreaIdOrLAI = cellGlobalIdOrServiceAreaIdOrLAI;
  }

  /**
   * @param accuracyFulfilmentIndicator to set
   */
  public void setAccuracyFulfilmentIndicator(AccuracyFulfilmentIndicator accuracyFulfilmentIndicator) {
    this.accuracyFulfilmentIndicator = accuracyFulfilmentIndicator;
  }

  /**
   * @param velocityEstimate to set
   */
  public void setVelocityEstimate(VelocityEstimate velocityEstimate) {
    this.velocityEstimate = velocityEstimate;
  }

  /**
   * @param servingNodeAddress to set
   */
  public void setServingNodeAddress(ServingNodeAddress servingNodeAddress) {
    this.servingNodeAddress = servingNodeAddress;
  }

  /**
   * @param lcsQoS to set
   */
  public void setLcsQoS(LCSQoS lcsQoS) {
    this.lcsQoS = lcsQoS;
  }

  /**
   * @param lcsReferenceNumber to set
   */
  public void setLcsReferenceNumber(Integer lcsReferenceNumber) {
    this.lcsReferenceNumber = lcsReferenceNumber;
  }

  /**
   * @param clientReferenceNumber to set
   */
  public void setClientReferenceNumber(Integer clientReferenceNumber) {
    this.clientReferenceNumber = clientReferenceNumber;
  }

  /**
   * @param barometricPressureMeasurement to set
   */
  public void setBarometricPressureMeasurement(Long barometricPressureMeasurement) {
    this.barometricPressureMeasurement = barometricPressureMeasurement;
  }

  /**
   * @param civicAddress to set
   */
  public void setCivicAddress(String civicAddress) {
    this.civicAddress = civicAddress;
  }

  /**
   * @param lcsEvent to set
   */
  public void setLcsEvent(LCSEvent lcsEvent) {
    this.lcsEvent = lcsEvent;
  }

  /**
   * @param locationEvent to set
   */
  public void setLocationEvent(LocationEvent locationEvent) {
    this.locationEvent = locationEvent;
  }

  /**
   * @param msisdn to set
   */
  public void setMsisdn(ISDNAddressString msisdn) {
    this.msisdn = msisdn;
  }

  /**
   * @param imei to set
   */
  public void setImei(IMEI imei) {
    this.imei = imei;
  }

  /**
   * @param deferredmtlrData to set
   */
  public void setDeferredmtlrData(DeferredmtlrData deferredmtlrData) {
    this.deferredmtlrData = deferredmtlrData;
  }

  /**
   * @param lcsServiceTypeID to set
   */
  public void setLcsServiceTypeID(Integer lcsServiceTypeID) {
    this.lcsServiceTypeID = lcsServiceTypeID;
  }

  /**
   * @param pseudonymIndicator to set
   */
  public void setPseudonymIndicator(Boolean pseudonymIndicator) {
    this.pseudonymIndicator = pseudonymIndicator;
  }

  /**
   * @param sequenceNumber to set
   */
  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  /**
   * @param periodicLDRInfo to set
   */
  public void setPeriodicLDRInfo(PeriodicLDRInfo periodicLDRInfo) {
    this.periodicLDRInfo = periodicLDRInfo;
  }

  /**
   * @param reportingPLMNList to set
   */
  public void setReportingPLMNList(ReportingPLMNList reportingPLMNList) {
    this.reportingPLMNList = reportingPLMNList;
  }

  /**
   * @param utranAdditionalPositioningData to set
   */
  public void setUtranAdditionalPositioningData(UtranAdditionalPositioningData utranAdditionalPositioningData) {
    this.utranAdditionalPositioningData = utranAdditionalPositioningData;
  }

  /**
   * @param utranBaroPressureMeas to set
   */
  public void setUtranBaroPressureMeas(Integer utranBaroPressureMeas) {
    this.utranBaroPressureMeas = utranBaroPressureMeas;
  }

  /**
   * @param utranCivicAddress to set
   */
  public void setUtranCivicAddress(UtranCivicAddress utranCivicAddress) {
    this.utranCivicAddress = utranCivicAddress;
  }

  /**
   * @param naEsrkRequest to set
   */
  public void setNaEsrkRequest(Boolean naEsrkRequest) {
    this.naEsrkRequest = naEsrkRequest;
  }

  /**
   * @param naESRD to set
   */
  public void setNaESRD(ISDNAddressString naESRD) {
    this.naESRD = naESRD;
  }

  /**
   * @param naESRK to set
   */
  public void setNaESRK(ISDNAddressString naESRK) {
    this.naESRK = naESRK;
  }

  /**
   * @param subscriberInfo to set
   */
  public void setSubscriberInfo(SubscriberInfo subscriberInfo) {
    this.subscriberInfo = subscriberInfo;
  }

  /**
   * @param locationInformation to set
   */
  public void setLocationInformation(LocationInformation locationInformation) {
    this.locationInformation = locationInformation;
  }

  /**
   * @param locationInformationEPS to set
   */
  public void setLocationInformationEPS(LocationInformationEPS locationInformationEPS) {
    this.locationInformationEPS = locationInformationEPS;
  }

  /**
   * @param locationInformationGPRS to set
   */
  public void setLocationInformationGPRS(LocationInformationGPRS locationInformationGPRS) {
    this.locationInformationGPRS = locationInformationGPRS;
  }

  /**
   * @param typeOfShape subscriber's location information to set
   */
  public void setTypeOfShape(TypeOfShape typeOfShape) {
    this.typeOfShape = typeOfShape;
  }

  /**
   * @param saiPresent subscriber's location information to set
   */
  public void setSaiPresent(Boolean saiPresent) {
    this.saiPresent = saiPresent;
  }

  public void setCurrentLocationRetrieved(Boolean currentLocationRetrieved) {
    this.currentLocationRetrieved = currentLocationRetrieved;
  }

  /**
   * @param mnpInfoRes subscriber's location information to set
   */
  public void setMnpInfoRes(MNPInfoRes mnpInfoRes) {
    this.mnpInfoRes = mnpInfoRes;
  }

  /**
   * @param taId subscriber's Tracking Area Id to set
   */
  public void setTaId(TAId taId) {
    this.taId = taId;
  }

  /**
   * @param eUtranCgi subscriber's E-UTRAN Cell Id  to set
   */
  public void setEUtranCgi(EUTRANCGI eUtranCgi) {
    this.eUtranCgi = eUtranCgi;
  }

  /**
   * @param cellPortionId subscriber's E-UTRAN Cell Id  to set
   */
  public void setCellPortionId(Long cellPortionId) {
    this.cellPortionId = cellPortionId;
  }

  /**
   * @param locationNumberMap subscriber's E-UTRAN Cell Id  to set
   */
  public void setLocationNumberMap(LocationNumberMap locationNumberMap) {
    this.locationNumberMap = locationNumberMap;
  }

  /**
   * @param lcsEpsClientName LCS EPS Client ID name
   */
  public void setLcsEpsClientName(String lcsEpsClientName) {
    this.lcsEpsClientName = lcsEpsClientName;
  }

  /**
   * @param lcsEpsClientFormatIndicator LCS EPS Client ID format indicator
   */
  public void setLcsEpsClientFormatIndicator(LCSFormatIndicator lcsEpsClientFormatIndicator) {
    this.lcsEpsClientFormatIndicator = lcsEpsClientFormatIndicator;
  }

  /**
   * @param eutranPositioningData subscriber's UTRAN positioning data to set
   */
  public void setEUTRANPositioningData(EUTRANPositioningDataImpl eutranPositioningData) {
    this.eutranPositioningData = eutranPositioningData;
  }

  /**
   * @param lteLcsQoSClass LCS QoS Class to set
   */
  public void setLteLcsQoSClass(LCSQoSClass lteLcsQoSClass) {
    this.lteLcsQoSClass = lteLcsQoSClass;
  }

  /**
   * @param oneXRTTRCID LCS QoS Class to set
   */
  public void setOneXRTTRCID(String oneXRTTRCID) {
    this.oneXRTTRCID = oneXRTTRCID;
  }

  /**
   * @param riaFlags to set out of SLh RIA
   */
  public void setRiaFlags(Long riaFlags) {
    this.riaFlags = riaFlags;
  }

  /**
   * @param plrFlags to set out of SLg PLR
   */
  public void setPlrFlags(Long plrFlags) {
    this.plrFlags = plrFlags;
  }

  /**
   * @param plaFlags to set out of SLg PLA
   */
  public void setPlaFlags(Long plaFlags) {
    this.plaFlags = plaFlags;
  }

  /**
   * @param lrrFlags to set out of SLg LRR
   */
  public void setLrrFlags(Long lrrFlags) {
    this.lrrFlags = lrrFlags;
  }

  /**
   * @param lraFlags to set out of SLg LRA
   */
  public void setLraFlags(Long lraFlags) {
    this.lraFlags = lraFlags;
  }

  /**
   * @param amfInstanceId to set out of SLg LRE
   */
  public void setAmfInstanceId(String amfInstanceId) {
    this.amfInstanceId = amfInstanceId;
  }

  /**
   * @param locationInformation5GS 5GS Location Information to set
   */
  public void setLocationInformation5GS(LocationInformation5GS locationInformation5GS) {
    this.locationInformation5GS = locationInformation5GS;
    if (locationInformation5GS != null) {
      this.nrCellGlobalId = locationInformation5GS.getNRCellGlobalIdentity();
      this.eUtranCgi = locationInformation5GS.getEUtranCellGlobalIdentity();
      this.taId = locationInformation5GS.getTrackingAreaIdentity();
      if (locationInformation5GS.getGeographicalInformation() != null) {
        this.typeOfShape = locationInformation5GS.getGeographicalInformation().getTypeOfShape();
      }
      this.amfAddress = locationInformation5GS.getAMFAddress();
      this.smsfAddress = locationInformation5GS.getSMSFAddress();
      this.currentLocationRetrieved = locationInformation5GS.getCurrentLocationRetrieved();
      this.ageOfLocationInformation = locationInformation5GS.getAgeOfLocationInformation();
      this.visitedPlmnId = locationInformation5GS.getVisitedPlmnId();
      LocalTimeZone sh5GSLocalTimeZone = new LocalTimeZone();
      sh5GSLocalTimeZone.setTimeZone(locationInformation5GS.getTimeZone());
      sh5GSLocalTimeZone.setDaylightSavingTime(locationInformation5GS.getDaylightSavingTime());
      this.localTimeZone = sh5GSLocalTimeZone;
      this.ratType = locationInformation5GS.getRatType();
    }
  }

  /**
   * @param nrCellGlobalId 5GS CGI to set
   */
  public void setNrCellGlobalId(NRCellGlobalId nrCellGlobalId) {
    this.nrCellGlobalId = nrCellGlobalId;
  }

  /**
   * @param ageOfLocationInformation 5GS Age of Location Information to set
   */
  public void setAgeOfLocationInformation(Integer ageOfLocationInformation) {
    this.ageOfLocationInformation = ageOfLocationInformation;
  }

  /**
   * @param amfAddress 5GS AMF Address to set
   */
  public void setAmfAddress(String amfAddress) {
    this.amfAddress = amfAddress;
  }

  /**
   * @param smsfAddress 5GS SMSF Address to set
   */
  public void setSmsfAddress(String smsfAddress) {
    this.smsfAddress = smsfAddress;
  }

  /**
   * @param visitedPlmnId visited PLMN ID to set
   */
  public void setVisitedPlmnId(PlmnId visitedPlmnId) {
    this.visitedPlmnId = visitedPlmnId;
  }

  /**
   * @param localTimeZone time zone to set
   */
  public void setLocalTimeZone(LocalTimeZone localTimeZone) {
    this.localTimeZone = localTimeZone;
  }

  /**
   * @param ratType RAT type to set
   */
  public void setRatType(Integer ratType) {
    this.ratType = ratType;
  }

  public org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS getLocationInformation5GSFromMap() {
    return locationInformation5GSFromMap;
  }

  public void setLocationInformation5GSFromMap(org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS locationInformation5GSFromMap) {
    this.locationInformation5GSFromMap = locationInformation5GSFromMap;
    if (locationInformation5GSFromMap != null) {
      this.nrCellGlobalIdentity = locationInformation5GSFromMap.getNRCellGlobalId();
      this.trackingAreaId = locationInformation5GSFromMap.getTAId();
      this.eUtranCellGlobalId = locationInformation5GSFromMap.getEUtranCgi();
      if (locationInformation5GSFromMap.getGeographicalInformation() != null) {
        this.typeOfShape = locationInformation5GSFromMap.getGeographicalInformation().getTypeOfShape();
      } else if (locationInformation5GSFromMap.getGeodeticInformation() != null) {
        this.typeOfShape = locationInformation5GSFromMap.getGeodeticInformation().getTypeOfShape();
      }
      this.currentLocationRetrieved = locationInformation5GSFromMap.isCurrentLocationRetrieved();
      this.ageOfLocationInformation = locationInformation5GSFromMap.getAgeOfLocationInformation();
      this.amfAddressFromMap = locationInformation5GSFromMap.getAMFAddress();
      this.vPlmnId = locationInformation5GSFromMap.getVPlmnId();
      // TODO TimeZone
      this.usedRATType = locationInformation5GSFromMap.getUsedRATType();
      this.nrTrackingAreaIdentity = locationInformation5GSFromMap.getNRTAId();
    }
  }

  public org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRCellGlobalId getNrCellGlobalIdentity() {
    return nrCellGlobalIdentity;
  }

  public void setNrCellGlobalIdentity(org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRCellGlobalId nrCellGlobalIdentity) {
    this.nrCellGlobalIdentity = nrCellGlobalIdentity;
  }

  public EUtranCgi geteUtranCellGlobalId() {
    return eUtranCellGlobalId;
  }

  public void seteUtranCellGlobalId(EUtranCgi eUtranCellGlobalId) {
    this.eUtranCellGlobalId = eUtranCellGlobalId;
  }

  public TAId getTrackingAreaId() {
    return trackingAreaId;
  }

  public void setTrackingAreaId(TAId trackingAreaId) {
    this.trackingAreaId = trackingAreaId;
  }

  public FQDN getAmfAddressFromMap() {
    return amfAddressFromMap;
  }

  public void setAmfAddressFromMap(FQDN amfAddressFromMap) {
    this.amfAddressFromMap = amfAddressFromMap;
  }

  public PlmnId getvPlmnId() {
    return vPlmnId;
  }

  public void setvPlmnId(PlmnId vPlmnId) {
    this.vPlmnId = vPlmnId;
  }

  public UsedRATType getUsedRATType() {
    return usedRATType;
  }

  public void setUsedRATType(UsedRATType usedRATType) {
    this.usedRATType = usedRATType;
  }

  public UsedRATType getLastRATType() {
    return lastRATType;
  }

  public void setLastRATType(UsedRATType lastRATType) {
    this.lastRATType = lastRATType;
  }

  public NRTAId getNrTrackingAreaIdentity() {
    return nrTrackingAreaIdentity;
  }

  public void setNrTrackingAreaIdentity(NRTAId nrTrackingAreaIdentity) {
    this.nrTrackingAreaIdentity = nrTrackingAreaIdentity;
  }

  /*********************/
  /*** CONSTRUCTORS ***/
  /*******************/

  public GMLCCDRState() {
    super();
  }

  /*******************/
  /*** INITIATORS ***/
  /*****************/

  public void init(final Long dialogId, final AddressString destRef, final AddressString origRef, final ISDNAddressString isdnAddressString,
                   final SccpAddress localAddress, final SccpAddress remoteAddress) {
    this.localDialogId = dialogId;
    this.destReference = destRef;
    this.origReference = origRef;
    this.isdnAddressString = isdnAddressString;
    this.localAddress = localAddress;
    this.remoteAddress = remoteAddress;
    // This should be enough to be unique
    this.id = UUID.randomUUID().toString();
    this.initiated = true;
    this.dialogStartTime = null;
    this.dialogEndTime = null;
    this.dialogDuration = null;
  }

  public void init(String id, net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestRealm,
                   net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestHost, Integer diameterDestPort) {
    this.id = UUID.randomUUID().toString();
    this.initiated = true;
    this.dialogStartTime = null;
    this.dialogEndTime = null;
    this.dialogDuration = null;
    this.diameterSessionId = id;
    this.diameterDestRealm = diameterDestRealm;
    this.diameterDestHost = diameterDestHost;
    this.diameterDestPort = diameterDestPort;
  }

  public void init(String diameterSessionId, net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginHost,
                   net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginRealm,
                   net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestHost,
                   net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestRealm) {
    this.id = UUID.randomUUID().toString();
    this.initiated = true;
    this.dialogStartTime = null;
    this.dialogEndTime = null;
    this.dialogDuration = null;
    this.diameterSessionId = diameterSessionId;
    this.diameterOriginHost = diameterOriginHost;
    this.diameterOriginRealm = diameterOriginRealm;
    this.diameterDestHost = diameterDestHost;
    this.diameterDestRealm = diameterDestRealm;
  }

  public void init(InetAddress slpSocketAddress, int slpSocketPort, InetAddress setSocketAddress, int setSocketPort) {
    this.id = UUID.randomUUID().toString();
    this.initiated = true;
    this.dialogStartTime = null;
    this.dialogEndTime = null;
    this.dialogDuration = null;
    this.slpSocketAddress = slpSocketAddress;
    this.slpSocketPort = slpSocketPort;
    this.setSocketAddress = setSocketAddress;
    this.setSocketPort = setSocketPort;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((isdnAddressString == null) ? 0 : isdnAddressString.hashCode());
    result = prime * result + ((destReference == null) ? 0 : destReference.hashCode());
    result = prime * result + ((diameterOriginHost == null) ? 0 : diameterDestHost.hashCode());
    result = prime * result + ((diameterOriginRealm == null) ? 0 : diameterOriginRealm.hashCode());
    result = prime * result + ((diameterDestHost == null) ? 0 : diameterDestHost.hashCode());
    result = prime * result + ((diameterDestRealm == null) ? 0 : diameterDestRealm.hashCode());
    result = prime * result + ((diameterSessionId == null) ? 0 : diameterSessionId.hashCode());
    result = prime * result + ((slpSocketAddress == null) ? 0 : slpSocketAddress.hashCode());
    result = prime * result + ((setSocketAddress == null) ? 0 : setSocketAddress.hashCode());
    result = prime * result + ((localDialogId == null) ? 0 : localDialogId.hashCode());
    result = prime * result + ((remoteDialogId == null) ? 0 : remoteDialogId.hashCode());
    result = prime * result + (generated ? 1231 : 1237);
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + (initiated ? 1231 : 1237);
    result = prime * result + ((localAddress == null) ? 0 : localAddress.hashCode());
    result = prime * result + ((origReference == null) ? 0 : origReference.hashCode());
    result = prime * result + ((recordStatus == null) ? 0 : recordStatus.hashCode());
    result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
    result = prime * result + ((subscriberInfo == null) ? 0 : subscriberInfo.hashCode());
    result = prime * result + ((locationInformation == null) ? 0 : locationInformation.hashCode());
    result = prime * result + ((epsSubscriberState == null) ? 0 : epsSubscriberState.hashCode());
    result = prime * result + ((locationInformationEPS == null) ? 0 : locationInformationEPS.hashCode());
    result = prime * result + ((locationInformationGPRS == null) ? 0 : locationInformationGPRS.hashCode());
    result = prime * result + ((locationInformation5GS == null) ? 0 : locationInformation5GS.hashCode());
    result = prime * result + ((locationNumberMap == null) ? 0 : locationNumberMap.hashCode());
    result = prime * result + ((mnpInfoRes == null) ? 0 : mnpInfoRes.hashCode());
    result = prime * result + ((msClassmark2 == null) ? 0 : msClassmark2.hashCode());
    result = prime * result + ((gprsmsClass == null) ? 0 : gprsmsClass.hashCode());
    result = prime * result + ((eUtranCgi == null) ? 0 : eUtranCgi.hashCode());
    result = prime * result + ((taId == null) ? 0 : taId.hashCode());
    result = prime * result + ((msisdn == null) ? 0 : msisdn.hashCode());
    result = prime * result + ((imsi == null) ? 0 : imsi.hashCode());
    result = prime * result + ((lmsi == null) ? 0 : lmsi.hashCode());
    result = prime * result + ((imei == null) ? 0 : imei.hashCode());
    result = prime * result + ((lcsClientID == null) ? 0 : lcsClientID.hashCode());
    result = prime * result + ((networkNodeNumber == null) ? 0 : networkNodeNumber.hashCode());
    result = prime * result + ((additionalNumber == null) ? 0 : additionalNumber.hashCode());
    result = prime * result + ((vlrAddress == null) ? 0 : vlrAddress.hashCode());
    result = prime * result + ((mmeName == null) ? 0 : mmeName.hashCode());
    result = prime * result + ((mmeRealm == null) ? 0 : mmeRealm.hashCode());
    result = prime * result + ((sgsnName == null) ? 0 : sgsnName.hashCode());
    result = prime * result + ((sgsnRealm == null) ? 0 : sgsnRealm.hashCode());
    result = prime * result + ((aaaServerName == null) ? 0 : aaaServerName.hashCode());
    result = prime * result + ((supportedLCSCapabilitySets == null) ? 0 : supportedLCSCapabilitySets.hashCode());
    result = prime * result + ((hGmlcAddress == null) ? 0 : hGmlcAddress.hashCode());
    result = prime * result + ((vGmlcAddress == null) ? 0 : vGmlcAddress.hashCode());
    result = prime * result + ((pprAddress == null) ? 0 : pprAddress.hashCode());
    result = prime * result + ((locationEstimate == null) ? 0 : locationEstimate.hashCode());
    result = prime * result + ((geranPositioningDataInformation == null) ? 0 : geranPositioningDataInformation.hashCode());
    result = prime * result + ((utranPositioningDataInfo == null) ? 0 : utranPositioningDataInfo.hashCode());
    result = prime * result + ((geranGANSSpositioningData == null) ? 0 : geranGANSSpositioningData.hashCode());
    result = prime * result + ((utranGANSSpositioningData == null) ? 0 : utranGANSSpositioningData.hashCode());
    result = prime * result + ((additionalLocationEstimate == null) ? 0 : additionalLocationEstimate.hashCode());
    result = prime * result + ((cellGlobalIdOrServiceAreaIdOrLAI == null) ? 0 : cellGlobalIdOrServiceAreaIdOrLAI.hashCode());
    result = prime * result + ((accuracyFulfilmentIndicator == null) ? 0 : accuracyFulfilmentIndicator.hashCode());
    result = prime * result + ((velocityEstimate == null) ? 0 : velocityEstimate.hashCode());
    result = prime * result + ((servingNodeAddress == null) ? 0 : servingNodeAddress.hashCode());
    result = prime * result + ((lcsQoS == null) ? 0 : lcsQoS.hashCode());
    result = prime * result + ((lteLcsQoSClass == null) ? 0 : lteLcsQoSClass.hashCode());
    result = prime * result + ((barometricPressureMeasurement == null) ? 0 : barometricPressureMeasurement.hashCode());
    result = prime * result + ((civicAddress == null) ? 0 : civicAddress.hashCode());
    result = prime * result + ((lcsEvent == null) ? 0 : lcsEvent.hashCode());
    result = prime * result + ((locationEvent == null) ? 0 : locationEvent.hashCode());
    result = prime * result + ((deferredmtlrData == null) ? 0 : deferredmtlrData.hashCode());
    result = prime * result + ((periodicLDRInfo == null) ? 0 : periodicLDRInfo.hashCode());
    result = prime * result + ((reportingPLMNList == null) ? 0 : reportingPLMNList.hashCode());
    result = prime * result + ((utranAdditionalPositioningData == null) ? 0 : utranAdditionalPositioningData.hashCode());
    result = prime * result + ((utranBaroPressureMeas == null) ? 0 : utranBaroPressureMeas.hashCode());
    result = prime * result + ((utranCivicAddress == null) ? 0 : utranCivicAddress.hashCode());
    result = prime * result + ((naESRD == null) ? 0 : naESRD.hashCode());
    result = prime * result + ((naESRK == null) ? 0 : naESRK.hashCode());
    result = prime * result + ((nrCellGlobalId == null) ? 0 : nrCellGlobalId.hashCode());
    result = prime * result + ((eUtranCellGlobalId == null) ? 0 : eUtranCellGlobalId.hashCode());
    result = prime * result + ((trackingAreaId == null) ? 0 : trackingAreaId.hashCode());
    result = prime * result + ((ageOfLocationInformation == null) ? 0 : ageOfLocationInformation.hashCode());
    result = prime * result + ((nrTrackingAreaIdentity == null) ? 0 : nrTrackingAreaIdentity.hashCode());
    result = prime * result + ((amfAddress == null) ? 0 : amfAddress.hashCode());
    result = prime * result + ((amfAddressFromMap == null) ? 0 : amfAddressFromMap.hashCode());
    result = prime * result + ((vPlmnId == null) ? 0 : vPlmnId.hashCode());
    result = prime * result + ((usedRATType == null) ? 0 : usedRATType.hashCode());
    result = prime * result + ((lastRATType == null) ? 0 : lastRATType.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;
    GMLCCDRState other = (GMLCCDRState) obj;

    if (curlUser == null) {
      if (other.curlUser != null)
        return false;
    } else if (!curlUser.equals(other.curlUser))
      return false;

    if (isdnAddressString == null) {
      if (other.isdnAddressString != null)
        return false;
    } else if (!isdnAddressString.equals(other.isdnAddressString))
      return false;

    if (destReference == null) {
      if (other.destReference != null)
        return false;
    } else if (!destReference.equals(other.destReference))
      return false;

    if (localDialogId == null) {
      if (other.localDialogId != null)
        return false;
    } else if (!localDialogId.equals(other.localDialogId))
      return false;

    if (remoteDialogId == null) {
      if (other.remoteDialogId != null)
        return false;
    } else if (!remoteDialogId.equals(other.remoteDialogId))
      return false;

    if (imsi == null) {
      if (other.imsi != null)
        return false;
    } else if (!imsi.equals(other.imsi))
      return false;

    if (vlrAddress == null) {
      if (other.vlrAddress != null)
        return false;
    } else if (!vlrAddress.equals(other.vlrAddress))
      return false;

    if (generated != other.generated)
      return false;

    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;

    if (initiated != other.initiated)
      return false;

    if (localAddress == null) {
      if (other.localAddress != null)
        return false;
    } else if (!localAddress.equals(other.localAddress))
      return false;

    if (origReference == null) {
      if (other.origReference != null)
        return false;
    } else if (!origReference.equals(other.origReference))
      return false;

    if (remoteAddress == null) {
      if (other.remoteAddress != null)
        return false;
    } else if (!remoteAddress.equals(other.remoteAddress))
      return false;

    if (diameterOriginHost == null) {
      if (other.diameterOriginHost != null)
        return false;
    } else if (!diameterOriginHost.equals(other.diameterOriginHost))
      return false;

    if (diameterOriginRealm == null) {
      if (other.diameterOriginRealm != null)
        return false;
    } else if (!diameterOriginRealm.equals(other.diameterOriginRealm))
      return false;

    if (diameterOriginPort == -1) {
      if (other.diameterOriginPort > 0)
        return false;
    } else if (diameterOriginPort != other.diameterOriginPort)
      return false;

    if (diameterDestHost == null) {
      if (other.diameterDestHost != null)
        return false;
    } else if (!diameterDestHost.equals(other.diameterDestHost))
      return false;

    if (diameterDestRealm == null) {
      if (other.diameterDestRealm != null)
        return false;
    } else if (!diameterDestRealm.equals(other.diameterDestRealm))
      return false;

    if (diameterDestPort == -1) {
      if (other.diameterDestPort > 0)
        return false;
    } else if (diameterDestPort != other.diameterDestPort)
      return false;

    if (diameterSessionId == null) {
      if (other.diameterSessionId != null)
        return false;
    } else if (!diameterSessionId.equals(other.diameterSessionId))
      return false;

    if (slpSocketAddress == null) {
      if (other.slpSocketAddress != null)
        return false;
    } else if (!slpSocketAddress.equals(other.slpSocketAddress))
      return false;

    if (slpSocketPort == -1) {
      if (other.slpSocketPort > 0)
        return false;
    } else if (slpSocketPort != other.slpSocketPort)
      return false;

    if (setSocketAddress == null) {
      if (other.setSocketAddress != null)
        return false;
    } else if (!setSocketAddress.equals(other.setSocketAddress))
      return false;

    if (setSocketPort == -1) {
      if (other.setSocketPort > 0)
        return false;
    } else if (setSocketPort != other.setSocketPort)
      return false;

    if (recordStatus == null) {
      if (other.recordStatus != null)
        return false;
    } else if (!recordStatus.equals(other.recordStatus))
      return false;

    if (statusCode == null) {
      if (other.statusCode != null)
        return false;
    } else if (!statusCode.equals(other.statusCode))
      return false;

    if (cellGlobalIdentity == null) {
      if (other.cellGlobalIdentity != null)
        return false;
    } else if (!cellGlobalIdentity.equals(other.cellGlobalIdentity))
      return false;

    if (serviceAreaIdentity == null) {
      if (other.serviceAreaIdentity != null)
        return false;
    } else if (!serviceAreaIdentity.equals(other.serviceAreaIdentity))
      return false;

    if (msClassmark2 == null) {
      if (other.msClassmark2 != null)
        return false;
    } else if (!msClassmark2.equals(other.msClassmark2))
      return false;

    if (gprsmsClass == null) {
      if (other.gprsmsClass != null)
        return false;
    } else if (!gprsmsClass.equals(other.gprsmsClass))
      return false;

    if (lcsClientID == null) {
      if (other.lcsClientID != null)
        return false;
    } else if (!lcsClientID.equals(other.lcsClientID))
      return false;

    if (lmsi == null) {
      if (other.lmsi != null)
        return false;
    } else if (!lmsi.equals(other.lmsi))
      return false;

    if (networkNodeNumber == null) {
      if (other.networkNodeNumber != null)
        return false;
    } else if (!networkNodeNumber.equals(other.networkNodeNumber))
      return false;

    if (gprsNodeIndicator == null) {
      if (other.gprsNodeIndicator != null)
        return false;
    } else if (!gprsNodeIndicator.equals(other.gprsNodeIndicator))
      return false;

    if (additionalNumber == null) {
      if (other.additionalNumber != null)
        return false;
    } else if (!additionalNumber.equals(other.additionalNumber))
      return false;

    if (mscNumber == null) {
      if (other.mscNumber != null)
        return false;
    } else if (!mscNumber.equals(other.mscNumber)) {
      return false;
    }

    if (sgsnNumber == null) {
      if (other.sgsnNumber != null)
        return false;
    } else if (!sgsnNumber.equals(other.sgsnNumber)) {
      return false;
    }

    if (mmeName == null) {
      if (other.mmeName != null)
        return false;
    } else if (!mmeName.equals(other.mmeName))
      return false;

    if (mmeRealm == null) {
      if (other.mmeRealm != null)
        return false;
    } else if (!mmeRealm.equals(other.mmeRealm))
      return false;

    if (sgsnName == null) {
      if (other.sgsnName != null)
        return false;
    } else if (!sgsnName.equals(other.sgsnName))
      return false;

    if (sgsnRealm == null) {
      if (other.sgsnRealm != null)
        return false;
    } else if (!sgsnRealm.equals(other.sgsnRealm))
      return false;

    if (aaaServerName == null) {
      if (other.aaaServerName != null)
        return false;
    } else if (!aaaServerName.equals(other.aaaServerName))
      return false;

    if (supportedLCSCapabilitySets == null) {
      if (other.supportedLCSCapabilitySets != null)
        return false;
    } else if (!supportedLCSCapabilitySets.equals(other.supportedLCSCapabilitySets))
      return false;

    if (hGmlcAddress == null) {
      if (other.hGmlcAddress != null)
        return false;
    } else if (!hGmlcAddress.equals(other.hGmlcAddress))
      return false;

    if (vGmlcAddress == null) {
      if (other.vGmlcAddress != null)
        return false;
    } else if (!vGmlcAddress.equals(other.vGmlcAddress))
      return false;

    if (pprAddress == null) {
      if (other.pprAddress != null)
        return false;
    } else if (!pprAddress.equals(other.pprAddress))
      return false;

    if (locationEstimate == null) {
      if (other.locationEstimate != null)
        return false;
    } else if (!locationEstimate.equals(other.locationEstimate))
      return false;

    if (polygon == null) {
      if (other.polygon != null)
        return false;
    } else if (!polygon.equals(other.polygon))
      return false;

    if (numberOfPoints == null) {
      if (other.numberOfPoints != null)
        return false;
    } else if (!numberOfPoints.equals(other.numberOfPoints))
      return false;

    if (polygonEllipsoidPoints == null) {
      if (other.polygonEllipsoidPoints != null)
        return false;
    } else if (!Arrays.equals(polygonEllipsoidPoints, other.polygonEllipsoidPoints))
      return false;

    if (additionalPolygon == null) {
      if (other.additionalPolygon != null)
        return false;
    } else if (!additionalPolygon.equals(other.additionalPolygon))
      return false;

    if (additionalNumberOfPoints == null) {
      if (other.additionalNumberOfPoints != null)
        return false;
    } else if (!additionalNumberOfPoints.equals(other.additionalNumberOfPoints))
      return false;

    if (additionalPolygonEllipsoidPoints == null) {
      if (other.additionalPolygonEllipsoidPoints != null)
        return false;
    } else if (!Arrays.equals(additionalPolygonEllipsoidPoints, other.additionalPolygonEllipsoidPoints))
      return false;

    if (moLrShortCircuitIndicator == null) {
      if (other.moLrShortCircuitIndicator != null)
        return false;
    } else if (!(moLrShortCircuitIndicator == (other.moLrShortCircuitIndicator)))
      return false;

    if (geranPositioningDataInformation == null) {
      if (other.geranPositioningDataInformation != null)
        return false;
    } else if (!geranPositioningDataInformation.equals(other.geranPositioningDataInformation))
      return false;

    if (utranPositioningDataInfo == null) {
      if (other.utranPositioningDataInfo != null)
        return false;
    } else if (!utranPositioningDataInfo.equals(other.utranPositioningDataInfo))
      return false;

    if (geranGANSSpositioningData == null) {
      if (other.geranGANSSpositioningData != null)
        return false;
    } else if (!geranGANSSpositioningData.equals(other.geranGANSSpositioningData))
      return false;

    if (utranGANSSpositioningData == null) {
      if (other.utranGANSSpositioningData != null)
        return false;
    } else if (!utranGANSSpositioningData.equals(other.utranGANSSpositioningData))
      return false;

    if (ageOfLocationEstimate < 0) {
      if (other.ageOfLocationEstimate > 0)
        return false;
    } else if (!ageOfLocationEstimate.equals(other.ageOfLocationEstimate))
      return false;

    if (additionalLocationEstimate == null) {
      if (other.additionalLocationEstimate != null)
        return false;
    } else if (!additionalLocationEstimate.equals(other.additionalLocationEstimate))
      return false;

    if (deferredMTLRResponseIndicator == null) {
      if (other.deferredMTLRResponseIndicator != null)
        return false;
    } else if (!deferredMTLRResponseIndicator.equals(other.deferredMTLRResponseIndicator))
      return false;

    if (cellGlobalIdOrServiceAreaIdOrLAI == null) {
      if (other.cellGlobalIdOrServiceAreaIdOrLAI != null)
        return false;
    } else if (!cellGlobalIdOrServiceAreaIdOrLAI.equals(other.cellGlobalIdOrServiceAreaIdOrLAI))
      return false;

    if (accuracyFulfilmentIndicator == null) {
      if (other.accuracyFulfilmentIndicator != null)
        return false;
    } else if (!accuracyFulfilmentIndicator.equals(other.accuracyFulfilmentIndicator))
      return false;

    if (velocityEstimate == null) {
      if (other.velocityEstimate != null)
        return false;
    } else if (!velocityEstimate.equals(other.velocityEstimate))
      return false;

    if (servingNodeAddress == null) {
      if (other.servingNodeAddress != null)
        return false;
    } else if (!servingNodeAddress.equals(other.servingNodeAddress))
      return false;

    if (lcsQoS == null) {
      if (other.lcsQoS != null)
        return false;
    } else if (!lcsQoS.equals(other.lcsQoS))
      return false;

    if (lteLcsQoSClass == null) {
      if (other.lteLcsQoSClass != null)
        return false;
    } else if (!lteLcsQoSClass.equals(other.lteLcsQoSClass))
      return false;

    if (lcsReferenceNumber < 0) {
      if (other.lcsReferenceNumber > 0)
        return false;
    } else if (!lcsReferenceNumber.equals(other.lcsReferenceNumber))
      return false;

    if (clientReferenceNumber < 0) {
      if (other.clientReferenceNumber > 0)
        return false;
    } else if (!clientReferenceNumber.equals(other.clientReferenceNumber))
      return false;

    if (barometricPressureMeasurement == null) {
      if (other.barometricPressureMeasurement != null)
        return false;
    } else if (!barometricPressureMeasurement.equals(other.barometricPressureMeasurement))
      return false;

    if (civicAddress == null) {
      if (other.civicAddress != null)
        return false;
    } else if (!civicAddress.equals(other.civicAddress))
      return false;

    if (lcsEvent == null) {
      if (other.lcsEvent != null)
        return false;
    } else if (!lcsEvent.equals(other.lcsEvent))
      return false;

    if (locationEvent == null) {
      if (other.locationEvent != null)
        return false;
    } else if (!locationEvent.equals(other.locationEvent))
      return false;

    if (msisdn == null) {
      if (other.msisdn != null)
        return false;
    } else if (!msisdn.equals(other.msisdn))
      return false;

    if (imei == null) {
      if (other.imei != null)
        return false;
    } else if (!imei.equals(other.imei))
      return false;

    if (deferredmtlrData == null) {
      if (other.deferredmtlrData != null)
        return false;
    } else if (!deferredmtlrData.equals(other.deferredmtlrData))
      return false;

    if (lcsServiceTypeID < 0) {
      if (other.lcsServiceTypeID > 0)
        return false;
    } else if (!lcsServiceTypeID.equals(other.lcsServiceTypeID))
      return false;

    if (pseudonymIndicator == null) {
      if (other.pseudonymIndicator != null)
        return false;
    } else if (!(pseudonymIndicator == (other.pseudonymIndicator)))
      return false;

    if (sequenceNumber < 0) {
      if (other.sequenceNumber > 0)
        return false;
    } else if (!sequenceNumber.equals(other.sequenceNumber))
      return false;

    if (periodicLDRInfo == null) {
      if (other.periodicLDRInfo != null)
        return false;
    } else if (!periodicLDRInfo.equals(other.periodicLDRInfo))
      return false;

    if (reportingPLMNList == null) {
      if (other.reportingPLMNList != null)
        return false;
    } else if (!reportingPLMNList.equals(other.reportingPLMNList))
      return false;

    if (utranAdditionalPositioningData == null) {
      if (other.utranAdditionalPositioningData != null)
        return false;
    } else if (!utranAdditionalPositioningData.equals(other.utranAdditionalPositioningData))
      return false;

    if (utranBaroPressureMeas == null) {
      if (other.utranBaroPressureMeas != null)
        return false;
    } else if (!utranBaroPressureMeas.equals(other.utranBaroPressureMeas))
      return false;

    if (utranCivicAddress == null) {
      if (other.utranCivicAddress != null)
        return false;
    } else if (!utranCivicAddress.equals(other.utranCivicAddress))
      return false;

    if (naEsrkRequest == null) {
      if (other.naEsrkRequest != null)
        return false;
    } else if (!naEsrkRequest.equals(other.naEsrkRequest))
      return false;

    if (naESRD == null) {
      if (other.naESRD != null)
        return false;
    } else if (!naESRD.equals(other.naESRD))
      return false;

    if (naESRK == null) {
      if (other.naESRK != null)
        return false;
    } else if (!naESRK.equals(other.naESRK))
      return false;

    if (subscriberInfo == null) {
      if (other.subscriberInfo != null)
        return false;
    } else if (!subscriberInfo.equals(other.subscriberInfo))
      return false;

    if (locationInformation == null) {
      if (other.locationInformation != null)
        return false;
    } else if (!locationInformation.equals(other.locationInformation))
      return false;

    if (epsSubscriberState == null) {
      if (other.epsSubscriberState != null)
        return false;
    } else if (!epsSubscriberState.equals(other.epsSubscriberState))
      return false;

    if (locationInformationEPS == null) {
      if (other.locationInformationEPS != null)
        return false;
    } else if (!locationInformationEPS.equals(other.locationInformationEPS))
      return false;

    if (locationInformationGPRS == null) {
      if (other.locationInformationGPRS != null)
        return false;
    } else if (!locationInformationGPRS.equals(other.locationInformationGPRS))
      return false;

    if (typeOfShape == null) {
      if (other.typeOfShape != null)
        return false;
    } else if (!typeOfShape.equals(other.typeOfShape))
      return false;

    if (mnpInfoRes == null) {
      if (other.mnpInfoRes != null)
        return false;
    } else if (!mnpInfoRes.equals(other.mnpInfoRes))
      return false;

    if (saiPresent == null) {
      if (other.saiPresent != null)
        return false;
    } else if (!saiPresent.equals(other.saiPresent))
      return false;

    if (currentLocationRetrieved == null) {
      if (other.currentLocationRetrieved != null)
        return false;
    } else if (!currentLocationRetrieved.equals(other.currentLocationRetrieved))
      return false;

    if (taId == null) {
      if (other.taId != null)
        return false;
    } else if (!taId.equals(other.taId))
      return false;

    if (eUtranCgi == null) {
      if (other.eUtranCgi != null)
        return false;
    } else if (!eUtranCgi.equals(other.eUtranCgi))
      return false;

    if (cellPortionId == null) {
      if (other.cellPortionId != null)
        return false;
    } else if (!cellPortionId.equals(other.cellPortionId))
      return false;

    if (locationNumberMap == null) {
      if (other.locationNumberMap != null)
        return false;
    } else if (!locationNumberMap.equals(other.locationNumberMap))
      return false;

    if (lcsEpsClientName == null) {
      if (other.lcsEpsClientName != null)
        return false;
    } else if (!lcsEpsClientName.equals(other.lcsEpsClientName))
      return false;

    if (lcsEpsClientFormatIndicator == null) {
      if (other.lcsEpsClientFormatIndicator != null)
        return false;
    } else if (!lcsEpsClientFormatIndicator.equals(other.lcsEpsClientFormatIndicator))
      return false;

    if (eutranPositioningData == null) {
      if (other.eutranPositioningData != null)
        return false;
    } else if (!eutranPositioningData.equals(other.eutranPositioningData))
      return false;

    if (oneXRTTRCID == null) {
      if (other.oneXRTTRCID != null)
        return false;
    } else if (!oneXRTTRCID.equals(other.oneXRTTRCID))
      return false;

    if (riaFlags == null) {
      if (other.riaFlags != null)
        return false;
    } else if (!riaFlags.equals(other.riaFlags))
      return false;

    if (plrFlags == null) {
      if (other.plrFlags != null)
        return false;
    } else if (!plrFlags.equals(other.plrFlags))
      return false;

    if (plaFlags == null) {
      if (other.plaFlags != null)
        return false;
    } else if (!plaFlags.equals(other.plaFlags))
      return false;

    if (lrrFlags == null) {
      if (other.lrrFlags != null)
        return false;
    } else if (!lrrFlags.equals(other.lrrFlags))
      return false;

    if (lraFlags == null) {
      if (other.lraFlags != null)
        return false;
    } else if (!lraFlags.equals(other.lraFlags))
      return false;

    if (amfInstanceId == null) {
      if (other.amfInstanceId != null)
        return false;
    } else if (!amfInstanceId.equals(other.amfInstanceId))
      return false;

    if (nrCellGlobalId == null) {
      if (other.nrCellGlobalId != null)
        return false;
    } else if (!nrCellGlobalId.equals(other.nrCellGlobalId))
      return false;

    if (eUtranCellGlobalId == null) {
      if (other.eUtranCellGlobalId != null)
        return false;
    } else if (!eUtranCellGlobalId.equals(other.eUtranCellGlobalId))
      return false;

    if (trackingAreaId == null) {
      if (other.trackingAreaId != null)
        return false;
    } else if (!trackingAreaId.equals(other.trackingAreaId))
      return false;

    if (nrTrackingAreaIdentity == null) {
      if (other.nrTrackingAreaIdentity != null)
        return false;
    } else if (!nrTrackingAreaIdentity.equals(other.nrTrackingAreaIdentity))
      return false;

    if (ageOfLocationInformation == null) {
      if (other.ageOfLocationInformation != null)
        return false;
    } else if (!ageOfLocationInformation.equals(other.ageOfLocationInformation))
      return false;

    if (amfAddress == null) {
      if (other.amfAddress != null)
        return false;
    } else if (!amfAddress.equals(other.amfAddress))
      return false;

    if (smsfAddress == null) {
      if (other.smsfAddress != null)
        return false;
    } else if (!smsfAddress.equals(other.smsfAddress))
      return false;

    if (visitedPlmnId == null) {
      if (other.visitedPlmnId != null)
        return false;
    } else if (!visitedPlmnId.equals(other.visitedPlmnId))
      return false;

    if (localTimeZone == null) {
      if (other.localTimeZone != null)
        return false;
    } else if (!localTimeZone.equals(other.localTimeZone))
      return false;

    if (ratType == null) {
      if (other.ratType != null)
        return false;
    } else if (!ratType.equals(other.ratType))
      return false;

    if (amfAddressFromMap == null) {
      if (other.amfAddressFromMap != null)
        return false;
    } else if (!amfAddressFromMap.equals(other.amfAddressFromMap))
      return false;

    if (vPlmnId == null) {
      if (other.vPlmnId != null)
        return false;
    } else if (!vPlmnId.equals(other.vPlmnId))
      return false;

    if (usedRATType == null) {
      if (other.usedRATType != null)
        return false;
    } else if (!usedRATType.equals(other.usedRATType))
      return false;

    if (lastRATType == null) {
      if (other.lastRATType != null)
        return false;
    } else if (!lastRATType.equals(other.lastRATType))
      return false;

    return true;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    String reportingPLMNListPrioritized = null, eUTRANPositioningDataStr = null;

    StringBuilder reportingPLMNListArray = null, geranPositioningDataInfo = null, utranPosDataInfo = null, geranGANSSPosDataInfo = null,
            utranGANSSPosDataInfo = null, utranAddPositioningData = null;

    try {

      if (geranPositioningDataInformation!= null) {
        HashMap<String, Integer> methodsAndUsage = geranPositioningDataInformation.getPositioningDataSet();
        geranPositioningDataInfo = new StringBuilder();
        int itemCounter = 0;
        geranPositioningDataInfo.append("[GERAN Positioning data: ");
        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
          itemCounter++;
          String method = item.getKey();
          Integer usage = item.getValue();
          geranPositioningDataInfo.append("Method=").append(method).append(", usage=").append(usage);
          if (methodsAndUsage.size() != itemCounter)
            geranPositioningDataInfo.append(", ");
        }
        geranPositioningDataInfo.append("]");
      }

      if (utranPositioningDataInfo != null) {
        HashMap<String, Integer> methodsAndUsage = utranPositioningDataInfo.getUtranPositioningDataSet();
        utranPosDataInfo = new StringBuilder();
        int itemCounter = 0;
        utranPosDataInfo.append("[UTRAN Positioning data: ");
        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
          itemCounter++;
          String method = item.getKey();
          Integer usage = item.getValue();
          utranPosDataInfo.append("Method=").append(method).append(", usage=").append(usage);
          if (methodsAndUsage.size() != itemCounter)
            utranPosDataInfo.append(", ");
        }
        utranPosDataInfo.append("]");
      }

      if (geranGANSSpositioningData != null) {
        Multimap<String, String> methodsAndGanssIds = geranGANSSpositioningData.getGeranGANSSPositioningMethodsAndGANSSIds();
        geranGANSSPosDataInfo = new StringBuilder();
        String method = null, ganssId = null;
        int i = 0, usage;
        geranGANSSPosDataInfo.append("[GERAN GANSS Positioning data: ");
        for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
          if (method != null || ganssId != null)
            geranGANSSPosDataInfo.append("; ");
          method = item.getKey();
          ganssId = item.getValue();
          usage = geranGANSSpositioningData.getUsageCode(geranGANSSpositioningData.getData(), i+1);
          geranGANSSPosDataInfo.append("Method=").append(method).append(", GANSSId=").append(ganssId).append(", usage=").append(usage);
          i++;
        }
        geranGANSSPosDataInfo.append("]");
      }

      if (utranGANSSpositioningData != null) {
        Multimap<String, String> methodsAndGanssIds = utranGANSSpositioningData.getUtranGANSSPositioningMethodsAndGANSSIds();
        utranGANSSPosDataInfo = new StringBuilder();
        String method = null, ganssId = null;
        int i = 0, usage;
        utranGANSSPosDataInfo.append("[UTRAN GANSS Positioning data: ");
        for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
          if (method != null || ganssId != null)
            utranGANSSPosDataInfo.append("; ");
          method = item.getKey();
          ganssId = item.getValue();
          usage = utranGANSSpositioningData.getUsageCode(utranGANSSpositioningData.getData(), i);
          utranGANSSPosDataInfo.append("Method=").append(method).append(", GANSSId=").append(ganssId).append(", usage=").append(usage);
          i++;
        }
        utranGANSSPosDataInfo.append("]");

      }

      if (utranAdditionalPositioningData != null) {
        Multimap<String, String> methodsAndAddPosIds = utranAdditionalPositioningData.getUtranAdditionalPositioningMethodsAndIds();
        utranAddPositioningData = new StringBuilder();
        String method = null, id = null;
        int i = 0, usage;
        utranAddPositioningData.append("[UTRAN Additional Positioning data");
        for (Map.Entry<String, String> item : methodsAndAddPosIds.entries()) {
          if (method != null || id != null)
            utranAddPositioningData.append("; ");
          method = item.getKey();
          id = item.getValue();
          usage = utranAdditionalPositioningData.getUsageCode(utranAdditionalPositioningData.getData(), i);
          utranAddPositioningData.append("Method=").append(method).append(", addPosId=").append(id).append(", usage =").append(usage);
          i++;
        }
        utranAddPositioningData.append("]");
      }

      if (eutranPositioningData != null) {
        try {
          if (eutranPositioningData.getPositioningDataSet() != null) {
            HashMap<String, Integer> methodsAndUsage = eutranPositioningData.getPositioningDataMethodsAndUsage(eutranPositioningData.getPositioningDataSet());
            StringBuilder positioningDataInfo = new StringBuilder();
            int itemCounter = 0;
            for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
              String method = item.getKey();
              Integer usage = item.getValue();
              positioningDataInfo.append("method=").append(method).append(" usage=").append(usage);
              if (methodsAndUsage.size() != itemCounter)
                positioningDataInfo.append(" -- ");
              itemCounter++;
            }
            eUTRANPositioningDataStr = String.valueOf(positioningDataInfo);
          } else if (eutranPositioningData.getGNSSPositioningDataSet() != null) {
            Multimap<String, String> methodsAndGanssIds = eutranPositioningData.getGNSSPositioningMethodsAndGNSSIds(eutranPositioningData.getGNSSPositioningDataSet());
            StringBuilder gnssPositioningDataInfo = new StringBuilder();
            String method = null, gnssId = null;
            int i = 0, usage;
            for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
              if (method != null || gnssId != null)
                gnssPositioningDataInfo.append(" -- ");
              method = entry.getKey();
              gnssId = entry.getValue();
              usage = eutranPositioningData.getUsageCode(eutranPositioningData.getGNSSPositioningDataSet(), i);
              gnssPositioningDataInfo.append("method=").append(method).append(" gnssId=").append(gnssId).append(" usage=").append(usage);
              i++;
            }
            eUTRANPositioningDataStr = String.valueOf(gnssPositioningDataInfo);
          } else if (eutranPositioningData.getAdditionalPositioningDataSet() != null) {
            Multimap<String, String> methodsAndAddPosIds = eutranPositioningData.getEUtranAdditionalPositioningMethodsAndIds(eutranPositioningData.getAdditionalPositioningDataSet());
            StringBuilder eutranAddPositioningData = new StringBuilder();
            String method = null, id = null;
            int i = 0, usage;
            for (Map.Entry<String, String> entry : methodsAndAddPosIds.entries()) {
              if (method != null || id != null)
                eutranAddPositioningData.append(" -- ");
              method = entry.getKey();
              id = entry.getValue();
              usage = eutranPositioningData.getUsageCode(eutranPositioningData.getAdditionalPositioningDataSet(), i);
              eutranAddPositioningData.append("method=").append(method).append(" addPosId=").append(id).append(" usage=").append(usage);
              i++;
            }
            eUTRANPositioningDataStr = String.valueOf(eutranAddPositioningData);
          }
        } catch (Exception e) {
          logger.error(e.getMessage());
        }
      }

      if (reportingPLMNList != null) {
        if (reportingPLMNList.getPlmnList() != null) {
          int plmnCounter = 0;
          reportingPLMNListArray = new StringBuilder("[");
          while (reportingPLMNList.getPlmnList().iterator().hasNext()) {
            reportingPLMNListArray.append(reportingPLMNList.getPlmnList().get(plmnCounter));
            plmnCounter++;
            if (reportingPLMNList.getPlmnList().get(plmnCounter) != null) {
              reportingPLMNListArray.append(", ");
            } else {
              reportingPLMNListArray.append("]");
            }
          }
        }
        if (reportingPLMNList.getPlmnListPrioritized() || !reportingPLMNList.getPlmnListPrioritized())
          reportingPLMNListPrioritized = String.valueOf(reportingPLMNList.getPlmnListPrioritized());
      }

    } catch (MAPException e) {
      logger.error(e.getMessage());
    }

    return "GMLCCDRState [initiated=" + initiated +
            ", generated=" + generated +
            ", user=" + curlUser +
            ", id=" + id +
            ", recordStatus=" + recordStatus +
            ", statusCode=" + statusCode +
            ", localDialogId=" + localDialogId  +
            ", remoteDialogId=" + remoteDialogId +
            ", dialogDuration" + dialogDuration +
            ", origReference=" + origReference +
            ", destReference=" + destReference +
            ", localAddress=" + localAddress +
            ", remoteAddress=" + remoteAddress +
            ", diameterOriginHost=" + diameterOriginHost +
            ", diameterOriginRealm=" + diameterOriginRealm +
            ", diameterOriginPort=" + diameterOriginPort +
            ", diameterDestHost=" + diameterDestHost +
            ", diameterDestRealm=" + diameterDestRealm +
            ", diameterDestPort=" + diameterDestPort +
            ", slpSocketAddress=" + slpSocketAddress +
            ", slpSocketPort=" + slpSocketPort +
            ", setSocketAddress=" + setSocketAddress +
            ", setSocketPort=" + setSocketPort +
            ", vlrAddress=" + vlrAddress +
            ", ISDNString=" + isdnAddressString +
            ", msClassmark=" + msClassmark2 +
            ", gprsMsClass=" + gprsmsClass +
            ", MSISDN=" + msisdn +
            ", IMSI=" + imsi +
            ", IMEI=" + imei +
            ", LMSI=" + lmsi +
            ", networkNodeNumber=" + networkNodeNumber +
            ", GPRSNodeIndicator=" + gprsNodeIndicator +
            ", additionalNumber=" + additionalNumber +
            ", MMEName=" + mmeName +
            ", MMERealm=" + mmeRealm +
            ", SGSNName=" + sgsnName +
            ", SGSNRealm=" + sgsnRealm +
            ", AAAServerName=" + aaaServerName +
            ", LCSCapabilitySets=" + supportedLCSCapabilitySets +
            ", vGMLCAddressData=" + vGmlcAddress +
            ", hGMLCAddressData=" + hGmlcAddress +
            ", PPRAddressData=" + pprAddress +
            ", location=" + locationEstimate +
            ", geranPositioningDataInformation=" + geranPositioningDataInfo +
            ", utranPositioningDataInfo=" + utranPosDataInfo +
            ", geranGANSSPositioningData=" + geranGANSSPosDataInfo +
            ", utranGANSSPositioningData=" + utranGANSSPosDataInfo +
            ", utranAdditionalPositioningDataInfo=" + utranAddPositioningData +
            ", EUTRANPositioningData=" + eUTRANPositioningDataStr +
            ", ageOfLocationEstimate=" + ageOfLocationEstimate +
            ", accuracyFulfilmentIndicator=" + accuracyFulfilmentIndicator +
            ", additionalLocationEstimate=" + additionalLocationEstimate +
            ", deferredMTLRResponseIndicator=" + deferredMTLRResponseIndicator +
            ", CGI or SAI or LAI=" + cellGlobalIdOrServiceAreaIdOrLAI +
            ", ECGI=" + eUtranCgi +
            ", cellPortionId=" + cellPortionId +
            ", sequenceNumber" + sequenceNumber +
            ", velocityEstimate=" + velocityEstimate +
            ", pseudonymIndicator" + pseudonymIndicator +
            ", servingNode=" + servingNodeAddress +
            ", lcsClientID=" + lcsClientID +
            ", lcsEpsClientName=" + lcsEpsClientName +
            ", lcsEpsClientFormatIndicator=" + lcsEpsClientFormatIndicator +
            ", lcsQoS=" + lcsQoS +
            ", lcsQoClass=" + lteLcsQoSClass +
            ", lcsReferenceNumber=" + lcsReferenceNumber +
            ", clientReferenceNumber=" + clientReferenceNumber +
            ", lcsServiceTypeID" + lcsServiceTypeID +
            ", barometricPressureMeasurement=" + barometricPressureMeasurement +
            ", civicAddress=" + civicAddress +
            ", lcsEvent=" + lcsEvent +
            ", locationEvent=" + locationEvent +
            ", deferredMtLrData=" + deferredmtlrData +
            ", periodicLDRInfo=" + periodicLDRInfo +
            ", moLrShortCircuitIndicator=" + moLrShortCircuitIndicator +
            ", ReportingPLMNList=" + reportingPLMNListArray +
            ", reportingPLMNListPrioritized=" + reportingPLMNListPrioritized +
            ", 1XRTTRCID=" + oneXRTTRCID +
            ", naESRK-Request=" + naEsrkRequest +
            ", naESRD=" + naESRD +
            ", naERK=" + naESRK +
            ", amfInstanceId=" + amfInstanceId +
            ", locationInformation=" + locationInformation +
            ", locationInformationGPRS=" + locationInformationGPRS +
            ", locationInformationEPS=" + locationInformationEPS +
            ", locationInformation5GS=" + locationInformation5GS +
            ", currentLocationRetrieved=" + currentLocationRetrieved +
            ", ageOfLocationInformation=" + ageOfLocationInformation +
            ", trackingAreaId=" + taId +
            ", mnpInfoRes=" + mnpInfoRes +
            "]@" + super.hashCode();
  }

}
