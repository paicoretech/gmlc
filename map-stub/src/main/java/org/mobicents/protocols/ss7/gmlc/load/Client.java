package org.mobicents.protocols.ss7.gmlc.load;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.map.MAPStackImpl;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPMessage;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.restcomm.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddressAddressType;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.lsm.AccuracyFulfilmentIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.Area;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaDefinition;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaEventInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaIdentification;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaType;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredLocationEventType;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.GeranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientExternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientName;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSCodeword;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPriority;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPrivacyCheck;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoS;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSRequestorID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationEstimateType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationType;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPDialogLsm;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPServiceLsmListener;
import org.restcomm.protocols.ss7.map.api.service.lsm.OccurrenceInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.PositioningDataInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.PrivacyCheckRelatedAction;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.RANTechnology;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingOptionMilliseconds;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMN;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMNList;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTime;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;
import org.restcomm.protocols.ss7.map.api.service.lsm.SLRArgExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.ServingNodeAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SupportedGADShapes;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranPositioningDataInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.CancelLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.PurgeMSResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SendIdentificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DomainType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APN;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaDefinitionImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaEventInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaIdentificationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaImpl;
import org.restcomm.protocols.ss7.map.service.lsm.DeferredLocationEventTypeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientExternalIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientNameImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSCodewordImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSPrivacyCheckImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSQoSImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSRequestorIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LocationTypeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.PeriodicLDRInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ReportingOptionMillisecondsImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ReportingPLMNImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ReportingPLMNListImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ResponseTimeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SupportedGADShapesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RequestedInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNImpl;
import org.restcomm.protocols.ss7.sccp.LoadSharingAlgorithm;
import org.restcomm.protocols.ss7.sccp.LongMessageRuleType;
import org.restcomm.protocols.ss7.sccp.NetworkIdState;
import org.restcomm.protocols.ss7.sccp.OriginationType;
import org.restcomm.protocols.ss7.sccp.RuleType;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.BCDEvenEncodingScheme;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.EncodingScheme;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.sccpext.impl.SccpExtModuleImpl;
import org.restcomm.protocols.ss7.sccpext.router.RouterExt;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterface;
import org.restcomm.protocols.ss7.ss7ext.Ss7ExtInterfaceImpl;
import org.restcomm.protocols.ss7.tcap.TCAPStackImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPStack;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.Problem;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class Client extends TestHarness implements MAPServiceMobilityListener, MAPServiceLsmListener {

  private static final Logger logger = Logger.getLogger(Client.class);

  // TCAP
  private TCAPStack tcapStack;

  // MAP
  private MAPStackImpl mapStack;
  private MAPProvider mapProvider;

  // SCCP
  SccpExtModuleImpl sccpExtModule;
  private SccpStackImpl sccpStack;

  // M3UA
  private M3UAManagementImpl clientM3UAMgmt;

  // SCTP
  private NettySctpManagementImpl sctpManagement;

  // a ramp-up period is required for performance testing.
  int endCount = -100;

  // AtomicInteger nbConcurrentDialogs = new AtomicInteger(0);

  volatile long start = 0L;
  volatile long prev = 0L;

  private RateLimiter rateLimiterObj = null;

  protected void initializeStack(IpChannelType ipChannelType) throws Exception {

    this.rateLimiterObj = RateLimiter.create(MAXCONCURRENTDIALOGS); // rate

    this.initSCTP(ipChannelType);

    // Initialize M3UA first
    this.initM3UA();

    // Initialize SCCP
    this.initSCCP();

    // Initialize TCAP
    this.initTCAP();

    // Initialize MAP
    this.initMAP();

    // Finally start ASP
    // Set 5: Finally start ASP
    this.clientM3UAMgmt.startAsp("ASP1");
  }

  private void initSCTP(IpChannelType ipChannelType) throws Exception {
    this.sctpManagement = new NettySctpManagementImpl("Client");
    // this.sctpManagement.setSingleThread(false);
    this.sctpManagement.start();
    this.sctpManagement.setConnectDelay(10000);
    this.sctpManagement.removeAllResources();

    // 1. Create SCTP Association
    sctpManagement.addAssociation(CLIENT_IP, CLIENT_PORT, SERVER_IP, SERVER_PORT, CLIENT_ASSOCIATION_NAME, ipChannelType,
        null);
  }

  private void initM3UA() throws Exception {
    this.clientM3UAMgmt = new M3UAManagementImpl("Client", null, new Ss7ExtInterfaceImpl());
    this.clientM3UAMgmt.setTransportManagement(this.sctpManagement);
    this.clientM3UAMgmt.setDeliveryMessageThreadCount(DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
    this.clientM3UAMgmt.start();
    this.clientM3UAMgmt.removeAllResources();

    // m3ua as create rc <rc> <ras-name>
    RoutingContext rc = factory.createRoutingContext(new long[]{100L});
    TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
    this.clientM3UAMgmt.createAs("AS1", Functionality.AS, ExchangeType.SE, IPSPType.CLIENT, rc, trafficModeType, 1, null);

    // Step 2 : Create ASP
    this.clientM3UAMgmt.createAspFactory("ASP1", CLIENT_ASSOCIATION_NAME);

    // Step3 : Assign ASP to AS
    Asp asp = this.clientM3UAMgmt.assignAspToAs("AS1", "ASP1");
    logger.info("ASP="+asp);

    // Step 4: Add Route. Remote point code is 2
    clientM3UAMgmt.addRoute(SERVER_SPC, -1, -1, "AS1");

  }

  private void initSCCP() throws Exception {
    Ss7ExtInterface ss7ExtInterface = new Ss7ExtInterfaceImpl();
    sccpExtModule = new SccpExtModuleImpl();
    ss7ExtInterface.setSs7ExtSccpInterface(sccpExtModule);
    this.sccpStack = new SccpStackImpl("MapLoadClientSccpStack", ss7ExtInterface);
    this.sccpStack.setMtp3UserPart(1, this.clientM3UAMgmt);

    // this.sccpStack.setCongControl_Algo(SccpCongestionControlAlgo.levelDepended);

    this.sccpStack.start();
    this.sccpStack.removeAllResources();
    RouterExt routerExt = sccpExtModule.getRouterExt();

    this.sccpStack.getSccpResource().addRemoteSpc(0, SERVER_SPC, 0, 0);
    this.sccpStack.getSccpResource().addRemoteSsn(0, SERVER_SPC, SERVER_SSN, 0, false);

    this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, CLIENT_SPC, NETWORK_INDICATOR, 0, null);
    this.sccpStack.getRouter().addMtp3Destination(1, 1, SERVER_SPC, SERVER_SPC, 0, 255, 255);

    ParameterFactoryImpl fact = new ParameterFactoryImpl();
    EncodingScheme ec = new BCDEvenEncodingScheme();
    GlobalTitle gt1 = fact.createGlobalTitle("-", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
        NatureOfAddress.INTERNATIONAL);
    GlobalTitle gt2 = fact.createGlobalTitle("-", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
        NatureOfAddress.INTERNATIONAL);
    SccpAddress localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt1, CLIENT_SPC,
        CLIENT_SSN);
    routerExt.addRoutingAddress(1, localAddress);
    SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt2, SERVER_SPC,
        SERVER_SSN);
    routerExt.addRoutingAddress(2, remoteAddress);

    GlobalTitle gt = fact.createGlobalTitle("*", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
        NatureOfAddress.INTERNATIONAL);
    SccpAddress pattern = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, 0);
    routerExt.addRule(1, RuleType.SOLITARY, LoadSharingAlgorithm.Bit0, OriginationType.REMOTE, pattern,
        "K", 1, -1, null, 0, null);
    routerExt.addRule(2, RuleType.SOLITARY, LoadSharingAlgorithm.Bit0, OriginationType.LOCAL, pattern, "K",
        2, -1, null, 0, null);
    this.sccpStack.getRouter().addLongMessageRule(1, 1, 16384, LongMessageRuleType.XUDT_ENABLED);
  }

  private void initTCAP() throws Exception {
    this.tcapStack = new TCAPStackImpl("Test", this.sccpStack.getSccpProvider(), CLIENT_SSN);
    this.tcapStack.start();
    this.tcapStack.setDialogIdleTimeout(60000);
    this.tcapStack.setInvokeTimeout(30000);
    this.tcapStack.setMaxDialogs(MAX_DIALOGS);
  }

  private void initMAP() throws Exception {

    // this.mapStack = new MAPStackImpl(CLIENT_ASSOCIATION_NAME, this.sccpStack.getSccpProvider(), SSN);
    this.mapStack = new MAPStackImpl("TestClient", this.tcapStack.getProvider());
    this.mapProvider = this.mapStack.getMAPProvider();

    this.mapProvider.addMAPDialogListener(this);

    this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);
    this.mapProvider.getMAPServiceMobility().activate();

    this.mapProvider.getMAPServiceLsm().addMAPServiceListener(this);
    this.mapProvider.getMAPServiceLsm().activate();

    this.mapStack.start();
  }

  public static void main(String[] args) {

    int noOfCalls = Integer.parseInt(args[0]);
    int noOfConcurrentCalls = Integer.parseInt(args[1]);

    IpChannelType ipChannelType = IpChannelType.SCTP;
    if (args.length >= 3 && args[2].equalsIgnoreCase("tcp")) {
      ipChannelType = IpChannelType.TCP;
    }

    logger.info("IpChannelType=" + ipChannelType);

    if (args.length >= 4) {
      TestHarness.CLIENT_IP = args[3];
    }

    logger.info("CLIENT_IP=" + TestHarness.CLIENT_IP);

    if (args.length >= 5) {
      TestHarness.CLIENT_PORT = Integer.parseInt(args[4]);
    }

    logger.info("CLIENT_PORT=" + TestHarness.CLIENT_PORT);

    if (args.length >= 6) {
      TestHarness.SERVER_IP = args[5];
    }

    logger.info("SERVER_IP=" + TestHarness.SERVER_IP);

    if (args.length >= 7) {
      TestHarness.SERVER_PORT = Integer.parseInt(args[6]);
    }

    logger.info("SERVER_PORT=" + TestHarness.SERVER_PORT);

    if (args.length >= 8) {
      TestHarness.CLIENT_SPC = Integer.parseInt(args[7]);
    }

    logger.info("CLIENT_SPC=" + TestHarness.CLIENT_SPC);

    if (args.length >= 9) {
      TestHarness.SERVER_SPC = Integer.parseInt(args[8]);
    }

    logger.info("SERVER_SPC=" + TestHarness.SERVER_SPC);

    if (args.length >= 10) {
      TestHarness.NETWORK_INDICATOR = Integer.parseInt(args[9]);
    }

    logger.info("NETWORK_INDICATOR=" + TestHarness.NETWORK_INDICATOR);

    if (args.length >= 11) {
      TestHarness.SERVICE_INDICATOR = Integer.parseInt(args[10]);
    }

    logger.info("SERVICE_INDICATOR=" + TestHarness.SERVICE_INDICATOR);

    if (args.length >= 12) {
      TestHarness.CLIENT_SSN = Integer.parseInt(args[11]);
    }

    logger.info("SSN=" + TestHarness.CLIENT_SSN);

    if (args.length >= 13) {
      TestHarness.ROUTING_CONTEXT = Integer.parseInt(args[12]);
    }

    logger.info("ROUTING_CONTEXT=" + TestHarness.ROUTING_CONTEXT);

    if (args.length >= 14) {
      TestHarness.DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT = Integer.parseInt(args[13]);
    }

    logger.info("DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT=" + TestHarness.DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);

    /*
     * logger.info("Number of calls to be completed = " + noOfCalls + " Number of concurrent calls to be maintained = " +
     * noOfConcurrentCalls);
     */

    NDIALOGS = noOfCalls;

    logger.info("NDIALOGS=" + NDIALOGS);

    MAXCONCURRENTDIALOGS = noOfConcurrentCalls;

    logger.info("MAXCONCURRENTDIALOGS=" + MAXCONCURRENTDIALOGS);

    final Client client = new Client();

    try {
      client.initializeStack(ipChannelType);

      Thread.sleep(20000);

      while (client.endCount < NDIALOGS) {
        /*
         * while (client.nbConcurrentDialogs.intValue() >= MAXCONCURRENTDIALOGS) {
         *
         * logger.warn("Number of concurrent MAP dialog's = " + client.nbConcurrentDialogs.intValue() +
         * " Waiting for max dialog count to go down!");
         *
         * synchronized (client) { try { client.wait(); } catch (Exception ex) { } } }// end of while
         * (client.nbConcurrentDialogs.intValue() >= MAXCONCURRENTDIALOGS)
         */

        if (client.endCount < 0) {
          client.start = System.currentTimeMillis();
          client.prev = client.start;
          // logger.warn("StartTime = " + client.start);
        }

        client.initiateMapAti();
        client.initiateSRILCS();
      }

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private void initiateMapAti() {
    try {
      NetworkIdState networkIdState = this.mapStack.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          logger.error(String.format("Error on initiateMapAti method:" + e));
        }
      }

      this.rateLimiterObj.acquire();

      // First create Dialog
      AddressString origRef = this.mapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = this.mapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
      MAPDialogMobility mapDialogMobility = this.mapProvider.getMAPServiceMobility()
          .createNewDialog(
              MAPApplicationContext.getInstance(MAPApplicationContextName.anyTimeEnquiryContext,
                  MAPApplicationContextVersion.version3),
              SCCP_CLIENT_ADDRESS, origRef, SCCP_SERVER_ADDRESS, destRef);

      // Then, create parameters for concerning MAP operation
      ISDNAddressString isdnAdd = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, "3797554321");
      SubscriberIdentity msisdn = new SubscriberIdentityImpl(isdnAdd);

      boolean locationInformation = true;
      boolean subscriberState = true;
      boolean currentLocation = true;
      DomainType requestedDomain = DomainType.csDomain;
      boolean imei = true;
      boolean msClassmark = true;
      boolean mnpRequestedInfo = true;
      RequestedInfo requestedInfo = new RequestedInfoImpl(locationInformation, subscriberState, null,
          currentLocation, requestedDomain, imei, msClassmark, mnpRequestedInfo, true);
      // requestedInfo (MAP ATI): last known location and state (idle or busy), no IMEI/MS Classmark/MNP

      ISDNAddressString gsmSCFAddress = new ISDNAddressStringImpl(AddressNature.international_number,
          NumberingPlan.ISDN, "222333");

      mapDialogMobility.addAnyTimeInterrogationRequest(msisdn, requestedInfo, gsmSCFAddress, null);
      logger.info("ATI msisdn:" + msisdn + ", requestedInfo: " + requestedInfo + ", atiIsdnAddress:" + gsmSCFAddress);

      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogMobility.send();

    } catch (MAPException e) {
      logger.error(String.format("Error while sending MAP ATI:" + e));
    }

  }

  private void initiateSRILCS() {

    try {
      NetworkIdState networkIdState = this.mapStack.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          logger.error(String.format("Error on initiateSRILCS method:" + e));
        }
      }

      this.rateLimiterObj.acquire();

      // First create Dialog
      AddressString origRef = this.mapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = this.mapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
      MAPDialogLsm mapDialogLsm = mapProvider.getMAPServiceLsm()
          .createNewDialog(MAPApplicationContext.getInstance(MAPApplicationContextName.locationSvcGatewayContext,
              MAPApplicationContextVersion.version3),
              SCCP_CLIENT_ADDRESS, origRef, SCCP_SERVER_ADDRESS, destRef);

      // Then, create parameters for concerning MAP operation
      long msisdnDigits = RandomUtils.nextLong(59898000000L, 59899000000L);
      SubscriberIdentity subscriberIdentity = new SubscriberIdentityImpl(new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, String.valueOf(msisdnDigits)));
      ISDNAddressString mlcNumber = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "491710470201");

      mapDialogLsm.addSendRoutingInfoForLCSRequest(mlcNumber, subscriberIdentity, null);

      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogLsm.send();

    } catch (MAPException e) {
      logger.error(String.format("Error while sending MAP SRILCS:" + e));
    }

  }

  private void initiateMapPSL(SendRoutingInfoForLCSResponse sendRoutingInfoForLCSResponse) {

    try {
      NetworkIdState networkIdState = this.mapStack.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          logger.error(String.format("Error on initiateMapPSL method:" + e));
        }
      }

      this.rateLimiterObj.acquire();

      // First create Dialog
      AddressString origRef = this.mapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "12345");
      AddressString destRef = this.mapProvider.getMAPParameterFactory()
          .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "67890");
      MAPDialogLsm mapDialogLsm = mapProvider.getMAPServiceLsm()
          .createNewDialog(MAPApplicationContext.getInstance(MAPApplicationContextName.locationSvcEnquiryContext,
              MAPApplicationContextVersion.version3),
              SCCP_CLIENT_ADDRESS, origRef, SCCP_SERVER_ADDRESS, destRef);

      // Then, create parameters for concerning MAP operation
      LocationEstimateType locationEstimateType = null;
      DeferredLocationEventType deferredLocationEventType = null;
      Integer lcsReferenceNumber = null;
      Random rand = new Random();
      switch (rand.nextInt(6) + 1) {
        case 1:
          locationEstimateType = LocationEstimateType.currentLocation;
          break;
        case 2:
          locationEstimateType = LocationEstimateType.currentOrLastKnownLocation;
          break;
        case 3:
          locationEstimateType = LocationEstimateType.initialLocation;
          break;
        case 4:
          locationEstimateType = LocationEstimateType.activateDeferredLocation;
          break;
        case 5:
          locationEstimateType = LocationEstimateType.cancelDeferredLocation;
          break;
        case 6:
          locationEstimateType = LocationEstimateType.notificationVerificationOnly;
          break;
      }
      OccurrenceInfo occurrenceInfo = null;
      if (locationEstimateType == LocationEstimateType.activateDeferredLocation ||
              locationEstimateType == LocationEstimateType.cancelDeferredLocation) {
        boolean msAvailable = false;
        boolean enteringIntoArea = false;
        boolean leavingFromArea = false;
        boolean beingInsideArea = false;
        boolean periodicLDR = false;
        switch (rand.nextInt(5) + 1) {
          case 1:
            msAvailable = true;
            occurrenceInfo = OccurrenceInfo.oneTimeEvent;
            // beingInsideArea is always treated as oneTimeEvent regardless of the possible value of occurrenceInfo inside areaEventInfo.
            break;
          case 2:
            enteringIntoArea = true;
            occurrenceInfo = OccurrenceInfo.multipleTimeEvent;
            break;
          case 3:
            leavingFromArea = true;
            occurrenceInfo = OccurrenceInfo.multipleTimeEvent;
            break;
          case 4:
            beingInsideArea = true;
            occurrenceInfo = OccurrenceInfo.multipleTimeEvent;
            break;
          case 5:
            periodicLDR = true;
            occurrenceInfo = OccurrenceInfo.multipleTimeEvent;
            break;
        }
        deferredLocationEventType = new DeferredLocationEventTypeImpl(msAvailable, enteringIntoArea, leavingFromArea, beingInsideArea, periodicLDR);
        lcsReferenceNumber = rand.nextInt(Integer.MAX_VALUE);
      }
      LocationType locationType = new LocationTypeImpl(locationEstimateType, deferredLocationEventType);

      ISDNAddressString mlcNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710470201");

      LCSClientType lcsClientType = null;
      LCSClientInternalID lcsClientInternalID = null;
      LCSPriority lcsPriority = LCSPriority.normalPriority;
      ResponseTimeCategory responseTimeCategory = ResponseTimeCategory.delaytolerant;
      Integer lcsServiceTypeID = null;
      switch (rand.nextInt(4) + 1) {
        case 1:
          lcsClientType = LCSClientType.emergencyServices;
          lcsClientInternalID = LCSClientInternalID.broadcastService;
          lcsPriority = LCSPriority.highestPriority;
          responseTimeCategory = ResponseTimeCategory.lowdelay;
          lcsServiceTypeID = 0;
          break;
        case 2:
          lcsClientType = LCSClientType.valueAddedServices;
          lcsClientInternalID = LCSClientInternalID.targetMSsubscribedService;
          lcsServiceTypeID = rand.nextInt(19) + 2;
          break;
        case 3:
          lcsClientType = LCSClientType.plmnOperatorServices;
          lcsClientInternalID = LCSClientInternalID.oandMHPLMN;
          lcsServiceTypeID = rand.nextInt(100) + 20;
          break;
        case 4:
          lcsClientType = LCSClientType.lawfulInterceptServices;
          lcsClientInternalID = LCSClientInternalID.oandMVPLMN;
          lcsServiceTypeID = rand.nextInt(80) + 20;
          break;
      }
      ISDNAddressString externalAddress = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "340444567");
      LCSClientExternalID lcsClientExternalID = new LCSClientExternalIDImpl(externalAddress, null);
      int cbsDataCodingSchemeCode = 15;
      CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(cbsDataCodingSchemeCode);
      String ussdLcsString = "*911#";
      Charset gsm8Charset = Charset.defaultCharset();
      USSDString ussdString = new USSDStringImpl(ussdLcsString, cbsDataCodingScheme, gsm8Charset);
      LCSFormatIndicator lcsFormatIndicator = LCSFormatIndicator.url;
      LCSClientName lcsClientName = new LCSClientNameImpl(cbsDataCodingScheme, ussdString, lcsFormatIndicator);
      AddressString lcsClientDialedByMS = new AddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "340012");
      APN lcsAPN = new APNImpl("ims");
      LCSRequestorID lcsRequestorID = new LCSRequestorIDImpl(cbsDataCodingScheme, ussdString, lcsFormatIndicator);
      LCSClientID lcsClientID = new LCSClientIDImpl(lcsClientType, lcsClientExternalID, lcsClientInternalID, lcsClientName,
              lcsClientDialedByMS, lcsAPN, lcsRequestorID);

      boolean privacyOverride = true;

      IMSI imsi = sendRoutingInfoForLCSResponse.getTargetMS().getIMSI();

      ISDNAddressString msisdn = sendRoutingInfoForLCSResponse.getTargetMS().getMSISDN();

      LMSI lmsi = sendRoutingInfoForLCSResponse.getLCSLocationInfo().getLMSI();

      long imeiDigits = RandomUtils.nextLong(100710000000000L, 100720000000000L);
      IMEI imei = new IMEIImpl(String.valueOf(imeiDigits));

      Integer horizontalAccuracy = 10;
      Integer verticalAccuracy = 50;
      boolean verticalCoordinateRequest = true;
      ResponseTime responseTime = new ResponseTimeImpl(responseTimeCategory);
      boolean velocityRequest = true;
      LCSQoS lcsQoS = new LCSQoSImpl(horizontalAccuracy, verticalAccuracy, verticalCoordinateRequest, responseTime, null,
              velocityRequest, null);

      boolean ellipsoidPoint = true;
      boolean ellipsoidPointWithUncertaintyCircle = true;
      boolean ellipsoidPointWithUncertaintyEllipse = true;
      boolean polygon = true;
      boolean ellipsoidPointWithAltitude = false;
      boolean ellipsoidPointWithAltitudeAndUncertaintyEllipsoid = true;
      boolean ellipsoidArc = true;
      SupportedGADShapes supportedGADShapes = new SupportedGADShapesImpl(ellipsoidPoint, ellipsoidPointWithUncertaintyCircle,
              ellipsoidPointWithUncertaintyEllipse, polygon, ellipsoidPointWithAltitude, ellipsoidPointWithAltitudeAndUncertaintyEllipsoid, ellipsoidArc);
      USSDString lcsCodewordString = new USSDStringImpl(ussdLcsString, cbsDataCodingScheme, gsm8Charset);
      LCSCodeword lcsCodeword = new LCSCodewordImpl(cbsDataCodingScheme, lcsCodewordString);
      PrivacyCheckRelatedAction callSessionUnrelated = PrivacyCheckRelatedAction.allowedWithNotification;
      PrivacyCheckRelatedAction callSessionRelated = PrivacyCheckRelatedAction.allowedIfNoResponse;
      LCSPrivacyCheck lcsPrivacyCheck = new LCSPrivacyCheckImpl(callSessionUnrelated, callSessionRelated);

      AreaEventInfo areaEventInfo = null;
      PeriodicLDRInfo periodicLDRInfo = null;
      if (locationEstimateType == LocationEstimateType.activateDeferredLocation ||
              locationEstimateType == LocationEstimateType.cancelDeferredLocation) {
        if (deferredLocationEventType.getPeriodicLDR()) {
          int reportingAmount = 3;
          int reportingInterval = 600;
          int reportingAmountMilliseconds = 863999; // ReportingAmountMilliseconds ::= INTEGER (1..8639999000)
          int reportingIntervalMilliseconds = 100; // ReportingIntervalMilliseconds ::= INTEGER (1..999)
          ReportingOptionMilliseconds reportingOptionMilliseconds = new ReportingOptionMillisecondsImpl(reportingAmountMilliseconds, reportingIntervalMilliseconds);
          int randReporting = rand.nextInt(2) + 1;
          if (randReporting == 1)
            periodicLDRInfo = new PeriodicLDRInfoImpl(reportingAmount, reportingInterval, reportingOptionMilliseconds);
          else
            periodicLDRInfo = new PeriodicLDRInfoImpl(reportingAmount, reportingInterval, null);
        } else {
          AreaDefinition areaDefinition = getAreaDefinition(rand.nextInt(10) + 1);
          Integer intervalTime = 10;
          areaEventInfo = new AreaEventInfoImpl(areaDefinition, occurrenceInfo, intervalTime);
        }
      }

      GSNAddress hGmlcAddress = new GSNAddressImpl(GSNAddressAddressType.IPv4, new byte[] { 0x0a, 0x00, 0x00, 0x0e });

      boolean moLrShortCircuitIndicator = true;

      ReportingPLMNList reportingPLMNList = getReportingPLMNList();

      mapDialogLsm.addProvideSubscriberLocationRequest(locationType, mlcNumber, lcsClientID, privacyOverride, imsi, msisdn, lmsi,
              imei, lcsPriority, lcsQoS, null, supportedGADShapes, lcsReferenceNumber, lcsServiceTypeID, lcsCodeword,
              lcsPrivacyCheck, areaEventInfo, hGmlcAddress, moLrShortCircuitIndicator, periodicLDRInfo, reportingPLMNList);
      logger.info("MAP PSL: msisdn:" + msisdn + ", MLC Number:" + mlcNumber);

      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogLsm.send();

    } catch (MAPException e) {
      logger.error(String.format("Error while sending MAP PSL:" + e));
    }

  }

  @Override
  public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest atiReq) {
    /*
     * This is an error condition. Client should never receive onAnyTimeInterrogationRequest.
     */
    logger.error(String.format("onAnyTimeInterrogationRequest for Dialog=%d and invokeId=%d",
        atiReq.getMAPDialog().getLocalDialogId(), atiReq.getInvokeId()));

  }

  @Override
  public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse atiResp) {

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onAnyTimeInterrogationResponse  for DialogId=%d",
          atiResp.getMAPDialog().getLocalDialogId()));
    } else {
      logger.info(String.format("onAnyTimeInterrogationResponse  for DialogId=%d",
          atiResp.getMAPDialog().getLocalDialogId()));
    }

    try {

      SubscriberInfo si = atiResp.getSubscriberInfo();

      if (si != null) {

        if (si.getLocationInformation() != null) {

          if (si.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
            CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = si.getLocationInformation()
                .getCellGlobalIdOrServiceAreaIdOrLAI();

            if (cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
              int mcc = cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
              int mnc = cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
              int lac = cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
              int cellId = cellGlobalIdOrServiceAreaIdOrLAI.getCellGlobalIdOrServiceAreaIdFixedLength()
                  .getCellIdOrServiceAreaCode();
              if (logger.isDebugEnabled()) {
                logger.debug(String.format(
                    "Rx onAnyTimeInterrogationResponse:  CI=%d, LAC=%d, MNC=%d, MCC=%d, for DialogId=%d",
                    cellId, lac, mnc, mcc, atiResp.getMAPDialog().getLocalDialogId()));
              } else {
                logger.info(String.format(
                    "Rx onAnyTimeInterrogationResponse:  CI=%d, LAC=%d, MNC=%d, MCC=%d, for DialogId=%d",
                    cellId, lac, mnc, mcc, atiResp.getMAPDialog().getLocalDialogId()));
              }
            }
          }

          if (si.getLocationInformation().getAgeOfLocationInformation() != null) {
            int aol = si.getLocationInformation().getAgeOfLocationInformation();
            if (logger.isDebugEnabled()) {
              logger.debug(String.format("Rx onAnyTimeInterrogationResponse:  AoL=%d for DialogId=%d", aol,
                  atiResp.getMAPDialog().getLocalDialogId()));
            } else {
              logger.info(String.format("Rx onAnyTimeInterrogationResponse:  AoL=%d for DialogId=%d", aol,
                  atiResp.getMAPDialog().getLocalDialogId()));
            }
          }

          if (si.getLocationInformation().getVlrNumber() != null) {
            String vlrAddress = si.getLocationInformation().getVlrNumber().getAddress();
            if (logger.isDebugEnabled()) {
              logger.debug(String.format("Rx onAnyTimeInterrogationResponse:  VLR address=%s for DialogId=%d",
                  vlrAddress, atiResp.getMAPDialog().getLocalDialogId()));
            } else {
              logger.info(String.format("Rx onAnyTimeInterrogationResponse:  VLR address=%s for DialogId=%d",
                  vlrAddress, atiResp.getMAPDialog().getLocalDialogId()));
            }
          }
        }

        if (si.getSubscriberState() != null) {

          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx onAnyTimeInterrogationResponse SubscriberState: "
                + si.getSubscriberState() + "for DialogId=%d", atiResp.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format("Rx onAnyTimeInterrogationResponse SubscriberState: "
                + si.getSubscriberState() + "for DialogId=%d", atiResp.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onAnyTimeInterrogationResponse, Incorrect Subscriber State received: " + si + "for DialogId=%d",
                atiResp.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onAnyTimeInterrogationResponse, Incorrect Subscriber State received: " + si + "for DialogId=%d",
                atiResp.getMAPDialog().getLocalDialogId()));
          }
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Bad AnyTimeInterrogationResponse received: " + atiResp + "for DialogId=%d",
              atiResp.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format("Bad AnyTimeInterrogationResponse received: " + atiResp + "for DialogId=%d",
              atiResp.getMAPDialog().getLocalDialogId()));
        }
      }

    } catch (Exception e) {
      logger.error(String.format("Error while processing onAnyTimeInterrogationResponse, Exception: " + e));

    }
  }

  @Override
  public void onAnyTimeSubscriptionInterrogationRequest(AnyTimeSubscriptionInterrogationRequest anyTimeSubscriptionInterrogationRequest) {

  }

  @Override
  public void onAnyTimeSubscriptionInterrogationResponse(AnyTimeSubscriptionInterrogationResponse anyTimeSubscriptionInterrogationResponse) {

  }

  @Override
  public void onAnyTimeModificationRequest(AnyTimeModificationRequest anyTimeModificationRequest) {

  }

  @Override
  public void onAnyTimeModificationResponse(AnyTimeModificationResponse anyTimeModificationResponse) {

  }

  @Override
  public void onSendRoutingInfoForLCSRequest(SendRoutingInfoForLCSRequest sendRoutingInfoForLCSRequest) {
    /*
     * This is an error condition. Client should never receive onSendRoutingInfoForLCSRequest.
     */
    logger.error(String.format("onSendRoutingInfoForLCSRequest for Dialog=%d and invokeId=%d",
        sendRoutingInfoForLCSRequest.getMAPDialog().getLocalDialogId(), sendRoutingInfoForLCSRequest.getInvokeId()));
  }

  @Override
  public void onSendRoutingInfoForLCSResponse(SendRoutingInfoForLCSResponse sendRoutingInfoForLCSResponse) {

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onSendRoutingInfoForLCSResponse  for DialogId=%d",
          sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
    } else {
      logger.info(String.format("onSendRoutingInfoForLCSResponse  for DialogId=%d",
          sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
    }

    try {

      LCSLocationInfo lcsLocationInfo = sendRoutingInfoForLCSResponse.getLCSLocationInfo();

      if (lcsLocationInfo != null) {

        if (lcsLocationInfo.getNetworkNodeNumber() != null) {
          String networkNodeNumber = lcsLocationInfo.getNetworkNodeNumber().toString();
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx onSendRoutingInfoForLCSResponse NetworkNodeNumber = " + networkNodeNumber +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format("Rx onSendRoutingInfoForLCSResponse NetworkNodeNumber: "
                + lcsLocationInfo.getNetworkNodeNumber() + "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect NetworkNodeNumber received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect NetworkNodeNumber received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        }

        if (lcsLocationInfo.getLMSI() != null) {
          String lmsi = lcsLocationInfo.getLMSI().toString();
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx onSendRoutingInfoForLCSResponse LMSI = " + lmsi +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format("Rx onSendRoutingInfoForLCSResponse LMSI: "
                + lcsLocationInfo.getLMSI() + "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect LMSI received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect LMSI received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        }

        if (lcsLocationInfo.getSupportedLCSCapabilitySets() != null) {
          String supportedLCSCapabilitySets = lcsLocationInfo.getSupportedLCSCapabilitySets().toString();
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx onSendRoutingInfoForLCSResponse Supported LCS Capability Sets = " + supportedLCSCapabilitySets +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format("Rx onSendRoutingInfoForLCSResponse Supported LCS Capability Sets: "
                + lcsLocationInfo.getSupportedLCSCapabilitySets() + "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect Supported LCS Capability Sets received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect Supported LCS Capability Sets received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        }

        if (lcsLocationInfo.getAdditionalLCSCapabilitySets() != null) {
          String additionalLCSCapabilitySets = lcsLocationInfo.getAdditionalLCSCapabilitySets().toString();
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx onSendRoutingInfoForLCSResponse Additional LCS Capability Sets = " + additionalLCSCapabilitySets +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format("Rx onSendRoutingInfoForLCSResponse Additional LCS Capability Sets: "
                + lcsLocationInfo.getAdditionalLCSCapabilitySets() + "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect Additional LCS Capability Sets received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect Additional LCS Capability Sets received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        }

        if (lcsLocationInfo.getAdditionalNumber() != null) {
          String sgsnNumber = lcsLocationInfo.getAdditionalNumber().getSGSNNumber().toString();
          String mscNumber = lcsLocationInfo.getAdditionalNumber().getSGSNNumber().toString();
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx Additional Number onSendRoutingInfoForLCSResponse, " +
                "SGSN Number = " + sgsnNumber + ", MSC Number: " + mscNumber +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format("Rx Additional Number onSendRoutingInfoForLCSResponse, " +
                "SGSN Number: " + sgsnNumber + ", MSC Number: " + mscNumber +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect Additional Number received for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect Additional Number received for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        }

        if (lcsLocationInfo.getGprsNodeIndicator()) {
          String gprsNodeIndicator = "false";
          if (logger.isDebugEnabled()) {
            logger.debug(String.format("Rx onSendRoutingInfoForLCSResponse GPRS Node Indicator = " + gprsNodeIndicator +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            gprsNodeIndicator = "true";
            logger.info(String.format("Rx onSendRoutingInfoForLCSResponse GPRS Node Indicator = " + gprsNodeIndicator +
                "for DialogId=%d", sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect GPRS Node Indicator received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onSendRoutingInfoForLCSResponse, Incorrect GPRS Node Indicator received: " + lcsLocationInfo + "for DialogId=%d",
                sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
          }
        }
        initiateMapPSL(sendRoutingInfoForLCSResponse);
      }

    } catch (Exception e) {
      logger.error(String.format("Error while processing onSendRoutingInfoForLCSResponse for Dialog=%d",
          sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId()));
    }
  }

  @Override
  public void onProvideSubscriberLocationRequest(ProvideSubscriberLocationRequest provideSubscriberLocationRequest) {
    /*
     * This is an error condition. Client should never receive onProvideSubscriberLocationRequest.
     */
    logger.error(String.format("onProvideSubscriberLocationRequest for Dialog=%d and invokeId=%d",
        provideSubscriberLocationRequest.getMAPDialog().getLocalDialogId(), provideSubscriberLocationRequest.getInvokeId()));

  }

  @Override
  public void onProvideSubscriberLocationResponse(ProvideSubscriberLocationResponse provideSubscriberLocationResponse) {

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onProvideSubscriberLocationResponse  for DialogId=%d",
          provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
    } else {
      logger.info(String.format("onProvideSubscriberLocationResponse  for DialogId=%d",
          provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
    }

    try {

      if (provideSubscriberLocationResponse.getLocationEstimate() != null) {
        ExtGeographicalInformation locationEstimate = provideSubscriberLocationResponse.getLocationEstimate();
        double latitude = locationEstimate.getLatitude();
        double longitude = locationEstimate.getLongitude();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse LocationEstimate, latitude = %d " + latitude + ", longitude: " +
              longitude + "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse LocationEstimate: "
                  + provideSubscriberLocationResponse.getLocationEstimate() + "for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect LocationEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect LocationEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getGeranPositioningData() != null) {
        PositioningDataInformation geranPositioningData = provideSubscriberLocationResponse.getGeranPositioningData();
        String geranPositioning = Arrays.toString(geranPositioningData.getData());
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse GeranPositioningData = %s " + geranPositioning +
              "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse GeranPositioningData: "
                  + provideSubscriberLocationResponse.getGeranPositioningData() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect GeranPositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect GeranPositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getUtranPositioningData() != null) {
        UtranPositioningDataInfo utranPositioningData = provideSubscriberLocationResponse.getUtranPositioningData();
        String utranPositioning = Arrays.toString(utranPositioningData.getData());
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse UtranPositioningData = %s " + utranPositioning +
              "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse UtranPositioningData: "
                  + provideSubscriberLocationResponse.getUtranPositioningData() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect UtranPositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect UtranPositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getAgeOfLocationEstimate() != null) {
        Integer ageOfLocationEstimate = provideSubscriberLocationResponse.getAgeOfLocationEstimate();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse AgeOfLocationEstimate = %d " + ageOfLocationEstimate +
              "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse AgeOfLocationEstimate: "
                  + provideSubscriberLocationResponse.getAgeOfLocationEstimate() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect AgeOfLocationEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect AgeOfLocationEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getAdditionalLocationEstimate() != null) {
        AddGeographicalInformation additionalLocationEstimate = provideSubscriberLocationResponse.getAdditionalLocationEstimate();
        double additionalLatitude = additionalLocationEstimate.getLatitude();
        double additionalLongitude = additionalLocationEstimate.getLongitude();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse AdditionalLocationEstimate, latitude = %d "
                  + additionalLatitude + ", longitude: " +
                  additionalLongitude + "for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse AdditionalLocationEstimate: "
                  + provideSubscriberLocationResponse.getAdditionalLocationEstimate() + "for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect AdditionalLocationEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect AdditionalLocationEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getExtensionContainer() != null) {
        MAPExtensionContainer mapExtensionContainer = provideSubscriberLocationResponse.getExtensionContainer();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse MAPExtensionContainer not null" +
              "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse MAPExtensionContainer: "
                  + mapExtensionContainer + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, NULL MAPExtensionContainer received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, NULL MAPExtensionContainer received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (!provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator() ||
          provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator()) {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse DeferredMTLRResponseIndicator: "
                  + provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse DeferredMTLRResponseIndicator: "
                  + provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, INCORRECT DeferredMTLRResponseIndicator = "
                  + provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator() + " " +
                  "received for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse, INCORRECT DeferredMTLRResponseIndicator = "
              + provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator() + " " +
              "received for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getCellIdOrSai() != null) {
        CellGlobalIdOrServiceAreaIdOrLAI cellIdOrSai = provideSubscriberLocationResponse.getCellIdOrSai();
        String cidOrSai = cellIdOrSai.getCellGlobalIdOrServiceAreaIdFixedLength().toString();
        String laiFixedLength = cellIdOrSai.getLAIFixedLength().toString();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse CellIdOrSai, CellGlobalIdOrServiceAreaIdOrLAI = %s " + cidOrSai +
              ", LAIFixedLength: " + laiFixedLength +
              ", for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse CellIdOrSai: "
                  + provideSubscriberLocationResponse.getCellIdOrSai() + "for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect CellIdOrSai received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect CellIdOrSai received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (!provideSubscriberLocationResponse.getSaiPresent() ||
              provideSubscriberLocationResponse.getSaiPresent()) {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse SaiPresent: "
                  + provideSubscriberLocationResponse.getSaiPresent() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                "Rx onProvideSubscriberLocationResponse, Incorrect SaiPresent received for DialogId=%d",
                provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
          } else {
            logger.info(String.format(
                "Rx onProvideSubscriberLocationResponse, Incorrect SaiPresent received for DialogId=%d",
                provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
          }
        }
      }

      if (provideSubscriberLocationResponse.getAccuracyFulfilmentIndicator() != null) {
        AccuracyFulfilmentIndicator accuracyFulfilmentIndicator = provideSubscriberLocationResponse.getAccuracyFulfilmentIndicator();
        int indicator = accuracyFulfilmentIndicator.getIndicator();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse AccuracyFulfilmentIndicator, indicator = %d " + indicator +
              ", for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse AccuracyFulfilmentIndicator: "
                  + provideSubscriberLocationResponse.getAccuracyFulfilmentIndicator() + "for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect AccuracyFulfilmentIndicator received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect AccuracyFulfilmentIndicator received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getVelocityEstimate() != null) {
        VelocityEstimate velocityEstimate = provideSubscriberLocationResponse.getVelocityEstimate();
        long horizontalSpeed = velocityEstimate.getHorizontalSpeed();
        long verticalSpeed = velocityEstimate.getVerticalSpeed();
        int velocityType = velocityEstimate.getVelocityType().getCode();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse VelocityEstimate, horizontal speed = %d " + horizontalSpeed +
              ", vertical speed: " + verticalSpeed + "velocity type: " + velocityType
              + ", for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse VelocityEstimate: "
                  + provideSubscriberLocationResponse.getVelocityEstimate() + "for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect VelocityEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect VelocityEstimate received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getMoLrShortCircuitIndicator() ||
          provideSubscriberLocationResponse.getMoLrShortCircuitIndicator()) {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse SaiPresent: "
                  + provideSubscriberLocationResponse.getMoLrShortCircuitIndicator() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse SaiPresent: "
                  + provideSubscriberLocationResponse.getMoLrShortCircuitIndicator() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect MoLrShortCircuitIndicator received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect MoLrShortCircuitIndicator received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getGeranGANSSpositioningData() != null) {
        GeranGANSSpositioningData geranGANSSpositioningDataPositioningData = provideSubscriberLocationResponse.getGeranGANSSpositioningData();
        String geranGanssPositioning = Arrays.toString(geranGANSSpositioningDataPositioningData.getData());
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse GeranGANSSPositioningData = %s " + geranGanssPositioning +
              "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse GeranGANSSPositioningData: "
                  + provideSubscriberLocationResponse.getGeranGANSSpositioningData() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect GeranGANSSPositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect GeranGANSSPositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getUtranGANSSpositioningData() != null) {
        UtranGANSSpositioningData utranGANSSpositioningDataPositioningData = provideSubscriberLocationResponse.getUtranGANSSpositioningData();
        String utranGanssPositioning = Arrays.toString(utranGANSSpositioningDataPositioningData.getData());
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse UtranGANSSpositioningData = %s " + utranGanssPositioning +
              "for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse UtranGANSSpositioningData: "
                  + provideSubscriberLocationResponse.getUtranGANSSpositioningData() + ", for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect UtranGANSSpositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect UtranGANSSpositioningData received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (provideSubscriberLocationResponse.getTargetServingNodeForHandover() != null) {
        ServingNodeAddress servingNodeAddress = provideSubscriberLocationResponse.getTargetServingNodeForHandover();
        String mscNumber = servingNodeAddress.getMscNumber().toString();
        String sgsnNumber = servingNodeAddress.getSgsnNumber().toString();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onProvideSubscriberLocationResponse ServingNode, MSC Number = %s " + mscNumber +
              ", SGSN Number = %s " + sgsnNumber +
              ", for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onProvideSubscriberLocationResponse ServingNode, MSC Number = %s " + mscNumber +
              ", SGSN Number = %s " + sgsnNumber +
              ", for DialogId=%d", provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect ServingNode received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onProvideSubscriberLocationResponse, Incorrect ServingNode received for DialogId=%d",
              provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId()));
        }
      }

    } catch (Exception e) {
      logger.info(String.format("onProvideSubscriberLocationResponse thrown: " + e));
    }
  }

  @Override
  public void onSubscriberLocationReportRequest(SubscriberLocationReportRequest subscriberLocationReportRequest) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onSubscriberLocationReportRequest for DialogId=%d", subscriberLocationReportRequest
              .getMAPDialog().getLocalDialogId()));
    }
    try {
      long invokeId = subscriberLocationReportRequest.getInvokeId();
      MAPDialogLsm slrDialog = subscriberLocationReportRequest.getMAPDialog();

      // Create SLR response parameters for concerning MAP operation
      ISDNAddressString naEsrd = null;
      ISDNAddressString naEsrk = null;
      SLRArgExtensionContainer slrArgExtensionContainer = subscriberLocationReportRequest.getSLRArgExtensionContainer();
      if (slrArgExtensionContainer != null) {
        if (slrArgExtensionContainer.getSlrArgPcsExtensions() != null) {
          if (slrArgExtensionContainer.getSlrArgPcsExtensions().getNaEsrkRequest()) {
            naEsrk = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "9289277009");
          }
        }
      } else {
        if (subscriberLocationReportRequest.getNaESRD() != null) {
          naEsrd = new ISDNAddressStringImpl(AddressNature.international_number,
                  NumberingPlan.ISDN, subscriberLocationReportRequest.getNaESRD().getAddress());
        } else if (subscriberLocationReportRequest.getNaESRK() != null) {
          naEsrk = new ISDNAddressStringImpl(AddressNature.international_number,
                  NumberingPlan.ISDN, subscriberLocationReportRequest.getNaESRK().getAddress());
        }
      }

      GSNAddress hGmlcAddress = subscriberLocationReportRequest.getHGMLCAddress();
      boolean molrShortCircuitIndicator = subscriberLocationReportRequest.getMoLrShortCircuitIndicator();
      ReportingPLMNList reportingPLMNList = getReportingPLMNList();
      Integer lcsReferenceNumber = subscriberLocationReportRequest.getLCSReferenceNumber();

      slrDialog.addSubscriberLocationReportResponse(invokeId, naEsrd, naEsrk, null, hGmlcAddress,
              molrShortCircuitIndicator, reportingPLMNList, lcsReferenceNumber);
      slrDialog.close(false);

    } catch (MAPException e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void onSubscriberLocationReportResponse(SubscriberLocationReportResponse subscriberLocationReportResponse) {

    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onSubscriberLocationReportResponse  for DialogId=%d",
          subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
    } else {
      logger.info(String.format("onSubscriberLocationReportResponse  for DialogId=%d",
          subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
    }

    try {

      if (subscriberLocationReportResponse.getNaESRD() != null) {
        ISDNAddressString naEsrd = subscriberLocationReportResponse.getNaESRD();
        String naESRDaddress = naEsrd.getAddress();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onSubscriberLocationReportResponse NaESRD = %s " + naESRDaddress +
              "for DialogId=%d", subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onSubscriberLocationReportResponse NaESRD: "
                  + subscriberLocationReportResponse.getNaESRD() + ", for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onSubscriberLocationReportResponse, Incorrect NaESRD received for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onSubscriberLocationReportResponse, Incorrect NaESRD received for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (subscriberLocationReportResponse.getNaESRK() != null) {
        ISDNAddressString naEsrk = subscriberLocationReportResponse.getNaESRD();
        String naESRKaddress = naEsrk.getAddress();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onSubscriberLocationReportResponse NaESRK = %s " + naESRKaddress +
              "for DialogId=%d", subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onSubscriberLocationReportResponse NaESRK: "
                  + subscriberLocationReportResponse.getNaESRK() + ", for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onSubscriberLocationReportResponse, Incorrect NaESRK received for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onSubscriberLocationReportResponse, Incorrect NaESRK received for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        }
      }

      if (subscriberLocationReportResponse.getExtensionContainer() != null) {
        MAPExtensionContainer extContainer = subscriberLocationReportResponse.getExtensionContainer();
        String mapExtensionContainer = extContainer.toString();
        if (logger.isDebugEnabled()) {
          logger.debug(String.format("Rx onSubscriberLocationReportResponse MAPExtensionContainer = %s " + mapExtensionContainer +
              "for DialogId=%d", subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));

        } else {
          logger.info(String.format("Rx onSubscriberLocationReportResponse MAPExtensionContainer: "
                  + subscriberLocationReportResponse.getExtensionContainer() + ", for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        }
      } else {
        if (logger.isDebugEnabled()) {
          logger.debug(String.format(
              "Rx onSubscriberLocationReportResponse, Incorrect MAPExtensionContainer received for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        } else {
          logger.info(String.format(
              "Rx onSubscriberLocationReportResponse, Incorrect MAPExtensionContainer received for DialogId=%d",
              subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
        }
      }

    } catch (Exception e) {
      logger.error(String.format("Error while processing onSubscriberLocationReportResponse for Dialog=%d",
          subscriberLocationReportResponse.getMAPDialog().getLocalDialogId()));
    }

  }

  private static ReportingPLMNList getReportingPLMNList() {
    ArrayList<ReportingPLMN> reportingPLMNs = new ArrayList<>();
    PlmnId plmnId1 = new PlmnIdImpl(748, 1);
    RANTechnology rat1 = RANTechnology.umts;
    boolean ranPeriodicLocationSupport1 = true;
    PlmnId plmnId2 = new PlmnIdImpl(748, 7);
    RANTechnology rat2 = RANTechnology.gsm;
    boolean ranPeriodicLocationSupport2 = false;
    ReportingPLMN rPlmn1 = new ReportingPLMNImpl(plmnId1, rat1, ranPeriodicLocationSupport1);
    ReportingPLMN rPlmn2 = new ReportingPLMNImpl(plmnId2, rat2, ranPeriodicLocationSupport2);
    reportingPLMNs.add(rPlmn1);
    reportingPLMNs.add(rPlmn2);
    boolean plmnListPrioritized = true;
    return new ReportingPLMNListImpl(plmnListPrioritized, reportingPLMNs);
  }

  private static AreaDefinition getAreaDefinition(int areaRand) throws MAPException {
    ArrayList<Area> areaList = new ArrayList<>();
    AreaType areaType;
    AreaIdentification areaIdentification;
    Area area1, area2, area3;

    switch (areaRand) {
      case 1:
        areaType = AreaType.countryCode;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 0, 0, 0);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        break;
      case 2:
        areaType = AreaType.plmnId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 0, 0);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        break;
      case 3:
        areaType = AreaType.locationAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 1201, 0);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        break;
      case 4:
        areaType = AreaType.routingAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 102, 1263);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        break;
      case 5:
        areaType = AreaType.cellGlobalId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 104, 32047);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        break;
      case 6:
        areaType = AreaType.utranCellId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 7, 0, 134283263);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        break;
      case 7:
        areaType = AreaType.countryCode;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 0, 0, 0);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaType = AreaType.locationAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 1201, 0);
        area2 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        areaList.add(area2);
        break;
      case 8:
        areaType = AreaType.locationAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 1201, 0);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaType = AreaType.utranCellId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 7, 0, 134283263);
        area2 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        areaList.add(area2);
        break;
      case 9:
        areaType = AreaType.routingAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 102, 1263);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaType = AreaType.cellGlobalId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 104, 32047);
        area2 = new AreaImpl(areaType, areaIdentification);
        areaType = AreaType.utranCellId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 7, 0, 134283263);
        area3 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        areaList.add(area2);
        areaList.add(area3);
        break;
      case 10:
        areaType = AreaType.plmnId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 0, 0);
        area1 = new AreaImpl(areaType, areaIdentification);
        areaType = AreaType.locationAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 1201, 0);
        area2 = new AreaImpl(areaType, areaIdentification);
        areaType = AreaType.routingAreaId;
        areaIdentification = new AreaIdentificationImpl(areaType, 748, 1, 102, 1263);
        area3 = new AreaImpl(areaType, areaIdentification);
        areaList.add(area1);
        areaList.add(area2);
        areaList.add(area3);
        break;
    }
    return new AreaDefinitionImpl(areaList);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogDelimiter (org.mobicents.protocols.ss7.map.api.MAPDialog)
   *
   */
  @Override
  public void onDialogDelimiter(MAPDialog mapDialog) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onDialogDelimiter for DialogId=%d", mapDialog.getLocalDialogId()));
    } else {
      logger.info(String.format("onDialogDelimiter for DialogId=%d", mapDialog.getLocalDialogId()));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogRequest
   * (org.mobicents.protocols.ss7.map.api.MAPDialog, org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                              MAPExtensionContainer extensionContainer) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format(
          "onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s MAPExtensionContainer=%s",
          mapDialog.getLocalDialogId(), destReference, origReference, extensionContainer));
    } else {
      logger.info(String.format(
          "onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s MAPExtensionContainer=%s",
          mapDialog.getLocalDialogId(), destReference, origReference, extensionContainer));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogRequestEricsson(org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString, org.mobicents.protocols.ss7.map.api.primitives.IMSI,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString)
   */
  @Override
  public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                                      AddressString arg3, AddressString arg4) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s ",
          mapDialog.getLocalDialogId(), destReference, origReference));
    } else {
      logger.info(String.format("onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s ",
          mapDialog.getLocalDialogId(), destReference, origReference));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogAccept( org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer extensionContainer) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onDialogAccept for DialogId=%d MAPExtensionContainer=%s", mapDialog.getLocalDialogId(),
          extensionContainer));
    } else {
      logger.info(String.format("onDialogAccept for DialogId=%d MAPExtensionContainer=%s", mapDialog.getLocalDialogId(),
          extensionContainer));
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogReject( org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason, org.mobicents.protocols.ss7.map.api.dialog.MAPProviderError,
   * org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason,
                             ApplicationContextName alternativeApplicationContext, MAPExtensionContainer extensionContainer) {
    logger.error(String.format(
        "onDialogReject for DialogId=%d MAPRefuseReason=%s ApplicationContextName=%s MAPExtensionContainer=%s",
        mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext, extensionContainer));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogUserAbort (org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason,
                                MAPExtensionContainer extensionContainer) {
    logger.error(String.format("onDialogUserAbort for DialogId=%d MAPUserAbortChoice=%s MAPExtensionContainer=%s",
        mapDialog.getLocalDialogId(), userReason, extensionContainer));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogProviderAbort(org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource,
   * org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer)
   */
  @Override
  public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason,
                                    MAPAbortSource abortSource, MAPExtensionContainer extensionContainer) {
    logger.error(String.format(
        "onDialogProviderAbort for DialogId=%d MAPAbortProviderReason=%s MAPAbortSource=%s MAPExtensionContainer=%s",
        mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogClose(org .mobicents.protocols.ss7.map.api.MAPDialog)
   */
  @Override
  public void onDialogClose(MAPDialog mapDialog) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("DialogClose for Dialog=%d", mapDialog.getLocalDialogId()));
    } else {
      logger.info(String.format("DialogClose for Dialog=%d", mapDialog.getLocalDialogId()));
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogNotice( org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic)
   */
  @Override
  public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
    logger.error(String.format("onDialogNotice for DialogId=%d MAPNoticeProblemDiagnostic=%s ",
        mapDialog.getLocalDialogId(), noticeProblemDiagnostic));
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogResease(org.mobicents.protocols.ss7.map.api.MAPDialog)
   *
   */
  @Override
  public void onDialogRelease(MAPDialog mapDialog) {
    if (logger.isDebugEnabled()) {
      logger.debug(String.format("onDialogResease for DialogId=%d", mapDialog.getLocalDialogId()));
    } else {
      logger.info(String.format("onDialogResease for DialogId=%d", mapDialog.getLocalDialogId()));
    }

    this.endCount++;

    if (this.endCount < NDIALOGS) {
      if ((this.endCount % 10000) == 0) {
        long current = System.currentTimeMillis();
        float sec = (float) (current - prev) / 1000f;
        prev = current;
        logger.warn("Completed 10000 Dialogs, dlg/sec: " + (10000 / sec));
      }
    } else {
      if (this.endCount == NDIALOGS) {
        long current = System.currentTimeMillis();
        logger.warn("Start Time = " + start);
        logger.warn("Current Time = " + current);
        float sec = (float) (current - start) / 1000f;

        logger.warn("Total time in sec = " + sec);
        logger.warn("Throughput = " + (NDIALOGS / sec));
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogTimeout(org.mobicents.protocols.ss7.map.api.MAPDialog)
   *
   */
  @Override
  public void onDialogTimeout(MAPDialog mapDialog) {
    logger.error(String.format("onDialogTimeout for DialogId=%d", mapDialog.getLocalDialogId()));
  }

  @Override
  public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberData) {

  }

  @Override
  public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest deleteSubsData) {

  }

  @Override
  public void onErrorComponent(MAPDialog arg0, Long arg1, MAPErrorMessage arg2) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onInvokeTimeout(MAPDialog arg0, Long arg1) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onMAPMessage(MAPMessage arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onRejectComponent(MAPDialog arg0, Long arg1, Problem arg2, boolean arg3) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onCancelLocationRequest(CancelLocationRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onCancelLocationResponse(CancelLocationResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onCheckImeiRequest(CheckImeiRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onCheckImeiResponse(CheckImeiResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPurgeMSRequest(PurgeMSRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onPurgeMSResponse(PurgeMSResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onResetRequest(ResetRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onRestoreDataRequest(RestoreDataRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onRestoreDataResponse(RestoreDataResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSendIdentificationRequest(SendIdentificationRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onSendIdentificationResponse(SendIdentificationResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onUpdateLocationRequest(UpdateLocationRequest arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onUpdateLocationResponse(UpdateLocationResponse arg0) {
    // TODO Auto-generated method stub

  }

}
