package org.mobicents.protocols.ss7.gmlc.load;

import org.apache.commons.lang3.RandomUtils;
import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.asn.BitSetStrictLength;
import org.mobicents.protocols.sctp.netty.NettySctpManagementImpl;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.LocationNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;
import org.restcomm.protocols.ss7.m3ua.As;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.AspFactory;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.restcomm.protocols.ss7.m3ua.parameter.RoutingContext;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.restcomm.protocols.ss7.map.MAPParameterFactoryImpl;
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
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
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
import org.restcomm.protocols.ss7.map.api.primitives.Time;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.lsm.AccuracyFulfilmentIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredLocationEventType;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredmtlrData;
import org.restcomm.protocols.ss7.map.api.service.lsm.EllipsoidPoint;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientExternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientName;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationType;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPDialogLsm;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.Polygon;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingOptionMilliseconds;
import org.restcomm.protocols.ss7.map.api.service.lsm.SLRArgExtensionContainer;
import org.restcomm.protocols.ss7.map.api.service.lsm.SLRArgPCSExtensions;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.ServingNodeAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.TerminationCause;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranAdditionalPositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranCivicAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityType;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
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
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateGprsLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UpdateLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeModificationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeSubscriptionInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DaylightSavingTime;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DomainType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeodeticInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.IMSVoiceOverPsSessionsIndication;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationNumberMap;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NotReachableReason;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NumberPortabilityStatus;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PDPContextInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberStateChoice;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RouteingNumber;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberStateChoice;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TimeZone;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.UserCSGInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APN;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.CSGId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.DeleteSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.InsertSubscriberDataResponse;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.AddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.DiameterIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.GSNAddressImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.LMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.TimeImpl;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AddGeographicalInformationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AdditionalNumberImpl;
import org.restcomm.protocols.ss7.map.service.lsm.DeferredLocationEventTypeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.DeferredmtlrDataImpl;
import org.restcomm.protocols.ss7.map.service.lsm.GeranGANSSpositioningDataImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientExternalIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientNameImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSLocationInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.PeriodicLDRInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.PolygonImpl;
import org.restcomm.protocols.ss7.map.service.lsm.PositioningDataInformationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ReportingOptionMillisecondsImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SLRArgExtensionContainerImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SLRArgPCSExtensionsImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ServingNodeAddressImpl;
import org.restcomm.protocols.ss7.map.service.lsm.UtranAdditionalPositioningDataImpl;
import org.restcomm.protocols.ss7.map.service.lsm.UtranCivicAddressImpl;
import org.restcomm.protocols.ss7.map.service.lsm.UtranGANSSpositioningDataImpl;
import org.restcomm.protocols.ss7.map.service.lsm.UtranPositioningDataInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.VelocityEstimateImpl;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedLCSCapabilitySetsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.EUtranCgiImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.GeodeticInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.GeographicalInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformation5GSImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationInformationEPSImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.LocationNumberMapImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.NRCellGlobalIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.NRTAIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.PSSubscriberStateImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RouteingNumberImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.SubscriberInfoImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.TAIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.TimeZoneImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.UserCSGInformationImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.APNImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.CSGIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.FQDNImpl;
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
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import static org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent.emergencyCallOrigination;
import static org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent.emergencyCallRelease;
import static org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent.molr;

/**
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 *
 */
public class Server extends TestHarness {

  private static final Logger logger = Logger.getLogger(Server.class);

  // MAP
  private MAPStackImpl mapStack;
  private MAPProvider mapProvider;

  // TCAP
  private TCAPStack tcapStack;

  // SCCP
  SccpExtModuleImpl sccpExtModule;
  private SccpStackImpl sccpStack;

  // M3UA
  private M3UAManagementImpl serverM3UAMgmt;

  // SCTP
  private NettySctpManagementImpl sctpManagement;

  int endCount = 0;
  volatile long start = System.currentTimeMillis();

  protected void initializeStack(IpChannelType ipChannelType) throws Exception {

    this.initSCTP(ipChannelType);

    // Initialize M3UA first
    this.initM3UA();

    // Initialize SCCP
    this.initSCCP();

    // Initialize TCAP
    this.initTCAP();

    // Initialize MAP
    this.initMAP();

    // 7. Start ASP
    serverM3UAMgmt.startAsp("RASP1");
  }

  private void initSCTP(IpChannelType ipChannelType) throws Exception {
    this.sctpManagement = new NettySctpManagementImpl("Server");
    // this.sctpManagement.setSingleThread(false);
    this.sctpManagement.start();
    this.sctpManagement.setConnectDelay(10000);
    this.sctpManagement.removeAllResources();

    // 1. Create SCTP Server
    sctpManagement.addServer(SERVER_NAME, SERVER_IP, SERVER_PORT, ipChannelType, null);

    // 2. Create SCTP Server Association
    sctpManagement.addServerAssociation(CLIENT_IP, CLIENT_PORT, SERVER_NAME, SERVER_ASSOCIATION_NAME, ipChannelType);

    // 3. Start Server
    sctpManagement.startServer(SERVER_NAME);
  }

  private void initM3UA() throws Exception {
    this.serverM3UAMgmt = new M3UAManagementImpl("Server", null,new Ss7ExtInterfaceImpl() );
    this.serverM3UAMgmt.setTransportManagement(this.sctpManagement);
    this.serverM3UAMgmt.setDeliveryMessageThreadCount(DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);
    this.serverM3UAMgmt.start();
    this.serverM3UAMgmt.removeAllResources();

    // Step 1 : Create App Server
    RoutingContext rc = factory.createRoutingContext(new long[]{100L});
    TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);
    As as = this.serverM3UAMgmt.createAs("RAS1", Functionality.SGW, ExchangeType.SE, IPSPType.CLIENT, rc, trafficModeType,
        1, null);
    logger.info("AS="+as);

    // Step 2 : Create ASP
    AspFactory aspFactory = this.serverM3UAMgmt.createAspFactory("RASP1", SERVER_ASSOCIATION_NAME);
    logger.info("AspFactory="+aspFactory);


    // Step3 : Assign ASP to AS
    Asp asp = this.serverM3UAMgmt.assignAspToAs("RAS1", "RASP1");
    logger.info("ASP="+asp);

    // Step 4: Add Route. Remote point code is 2
    this.serverM3UAMgmt.addRoute(CLIENT_SPC, -1, -1, "RAS1");
  }

  private void initSCCP() throws Exception {
    Ss7ExtInterface ss7ExtInterface = new Ss7ExtInterfaceImpl();
    sccpExtModule = new SccpExtModuleImpl();
    ss7ExtInterface.setSs7ExtSccpInterface(sccpExtModule);
    this.sccpStack = new SccpStackImpl("MapLoadServerSccpStack", ss7ExtInterface);
    this.sccpStack.setMtp3UserPart(1, this.serverM3UAMgmt);

    this.sccpStack.start();
    this.sccpStack.removeAllResources();

    RouterExt routerExt = sccpExtModule.getRouterExt();

    this.sccpStack.getSccpResource().addRemoteSpc(0, CLIENT_SPC, 0, 0);
    this.sccpStack.getSccpResource().addRemoteSsn(0, CLIENT_SPC, CLIENT_SSN, 0, false);

    this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, SERVER_SPC, NETWORK_INDICATOR, 0, null);
    this.sccpStack.getRouter().addMtp3Destination(1, 1, CLIENT_SPC, CLIENT_SPC, 0, 255, 255);

    ParameterFactoryImpl fact = new ParameterFactoryImpl();
    EncodingScheme ec = new BCDEvenEncodingScheme();
    GlobalTitle gt1 = fact.createGlobalTitle("-", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
        NatureOfAddress.INTERNATIONAL);
    GlobalTitle gt2 = fact.createGlobalTitle("-", 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, ec,
        NatureOfAddress.INTERNATIONAL);
    SccpAddress localAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt1, SERVER_SPC, SERVER_SSN);
    routerExt.addRoutingAddress(1, localAddress);
    SccpAddress remoteAddress = new SccpAddressImpl(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt2, CLIENT_SPC, CLIENT_SSN);
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
    this.tcapStack = new TCAPStackImpl("TestServer", this.sccpStack.getSccpProvider(), SERVER_SSN);
    this.tcapStack.start();
    this.tcapStack.setDialogIdleTimeout(60000);
    this.tcapStack.setInvokeTimeout(30000);
    this.tcapStack.setMaxDialogs(MAX_DIALOGS);
  }

  private void initMAP() throws Exception {
    this.mapStack = new MAPStackImpl("TestServer", this.tcapStack.getProvider());
    this.mapProvider = this.mapStack.getMAPProvider();

    this.mapProvider.addMAPDialogListener(this);

    this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);
    this.mapProvider.getMAPServiceMobility().activate();

    this.mapProvider.getMAPServiceLsm().addMAPServiceListener(this);
    this.mapProvider.getMAPServiceLsm().activate();

    this.mapStack.start();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogDelimiter(org.mobicents.protocols.ss7.map.api.MAPDialog)
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
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogRequest(org.mobicents.protocols.ss7.map.api.MAPDialog,
   * org.mobicents.protocols.ss7.map.api.primitives.AddressString,
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

  @Override
  public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                                      AddressString addressString, AddressString vlr) {
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
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogUserAbort(org.mobicents.protocols.ss7.map.api.MAPDialog,
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
   * @see org.mobicents.protocols.ss7.map.api.MAPDialogListener#onDialogNotice(org.mobicents.protocols.ss7.map.api.MAPDialog,
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

    if (((this.endCount % 10000) == 0) && endCount > 1) {
      long currentTime = System.currentTimeMillis();
      long processingTime = currentTime - start;
      start = currentTime;
      logger.warn("Completed 10000 Dialogs in=" + processingTime + " ms. Dialogs per sec: " + (float) (10000000 / processingTime));
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

  public static void main(String[] args) {
    IpChannelType ipChannelType = IpChannelType.SCTP;
    if (args.length >= 1 && args[0].equalsIgnoreCase("tcp")) {
      ipChannelType = IpChannelType.TCP;
    }
    logger.info("IpChannelType=" + ipChannelType);

    if (args.length >= 2) {
      TestHarness.CLIENT_IP = args[1];
    }
    logger.info("CLIENT_IP=" + TestHarness.CLIENT_IP);

    if (args.length >= 3) {
      TestHarness.CLIENT_PORT = Integer.parseInt(args[2]);
    }
    logger.info("CLIENT_PORT=" + TestHarness.CLIENT_PORT);

    if (args.length >= 4) {
      TestHarness.SERVER_IP = args[3];
    }
    logger.info("SERVER_IP=" + TestHarness.SERVER_IP);

    if (args.length >= 5) {
      TestHarness.SERVER_PORT = Integer.parseInt(args[4]);
    }
    logger.info("SERVER_PORT=" + TestHarness.SERVER_PORT);

    if (args.length >= 6) {
      TestHarness.CLIENT_SPC = Integer.parseInt(args[5]);
    }
    logger.info("CLIENT_SPC=" + TestHarness.CLIENT_SPC);

    if (args.length >= 7) {
      TestHarness.SERVER_SPC = Integer.parseInt(args[6]);
    }
    logger.info("SERVER_SPC=" + TestHarness.SERVER_SPC);

    if (args.length >= 8) {
      TestHarness.NETWORK_INDICATOR = Integer.parseInt(args[7]);
    }
    logger.info("NETWORK_INDICATOR=" + TestHarness.NETWORK_INDICATOR);

    if (args.length >= 9) {
      TestHarness.SERVICE_INDICATOR = Integer.parseInt(args[8]);
    }
    logger.info("SERVICE_INDICATOR=" + TestHarness.SERVICE_INDICATOR);

    if (args.length >= 10) {
      TestHarness.SERVER_SSN = Integer.parseInt(args[9]);
    }
    logger.info("SSN=" + TestHarness.SERVER_SSN);

    if (args.length >= 11) {
      TestHarness.ROUTING_CONTEXT = Integer.parseInt(args[10]);
    }
    logger.info("ROUTING_CONTEXT=" + TestHarness.ROUTING_CONTEXT);

    if (args.length >= 12) {
      TestHarness.DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT = Integer.parseInt(args[11]);
    }
    logger.info("DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT=" + TestHarness.DELIVERY_TRANSFER_MESSAGE_THREAD_COUNT);

    final Server server = new Server();
    try {
      server.initializeStack(ipChannelType);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  private static SccpAddress createSccpAddress(RoutingIndicator ri, int dpc, int ssn, String address) {
    ParameterFactoryImpl fact = new ParameterFactoryImpl();
    GlobalTitle gt = fact.createGlobalTitle(address, 0, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,
            BCDEvenEncodingScheme.INSTANCE, NatureOfAddress.INTERNATIONAL);
    if (ssn < 0) {
      ssn = SERVER_SSN;
    }
    return fact.createSccpAddress(ri, gt, dpc, ssn);
  }

  private SccpAddress getVLRSCCPAddress(String vlrAddress) {
    ParameterFactory sccpParam = new ParameterFactoryImpl();
    int translationType = 0; // Translation Type = 0 : Unknown
    EncodingScheme encodingScheme = null;
    GlobalTitle gt = sccpParam.createGlobalTitle(vlrAddress, translationType, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, encodingScheme, NatureOfAddress.INTERNATIONAL);
    int vlrSsn = 7;
    return sccpParam.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, gt, translationType, vlrSsn);
  }

  private SccpAddress getMSCSCCPAddress(String mscAddress) {
    ParameterFactory sccpParam = new ParameterFactoryImpl();
    int translationType = 0; // Translation Type = 0 : Unknown
    EncodingScheme encodingScheme = null;
    GlobalTitle gt = sccpParam.createGlobalTitle(mscAddress, translationType, org.restcomm.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, encodingScheme, NatureOfAddress.INTERNATIONAL);
    int mscSsn = 8;
    return sccpParam.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, gt, translationType, mscSsn);
  }

  @Override
  public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest atiReq) {

    if (logger.isDebugEnabled()) {
      logger.debug(
          String.format("onAnyTimeInterrogationRequest for DialogId=%d", atiReq.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("onAnyTimeInterrogationRequest for DialogId=%d", atiReq.getMAPDialog().getLocalDialogId()));
    }

    try {
      long invokeId = atiReq.getInvokeId();
      MAPDialogMobility mapDialogMobility = atiReq.getMAPDialog();
      mapDialogMobility.setUserObject(invokeId);
      RequestedInfo requestedInfo = atiReq.getRequestedInfo();


      SubscriberInfo subscriberInfo;
      LocationInformation locationInformation = null;
      SubscriberState subscriberState = null;
      IMEI imei = null;
      MSClassmark2 msClassmark2 = null;
      MNPInfoRes mnpInfoRes = null;
      int ageOfLocationInformation = 0;
      Boolean currentLocationRetrieved = null;
      boolean saiPresent = false;
      int mcc, mnc, lac, cellId;
      CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
      CellGlobalIdOrServiceAreaIdFixedLength cgiOrSai = null;
      LocationNumber locationNumber;
      LocationNumberMap locationNumberMap;
      String mscAddress = getVLRSCCPAddress("491710460000").getGlobalTitle().getDigits();
      String vlrAddress = getMSCSCCPAddress("491710460000").getGlobalTitle().getDigits();
      ISDNAddressString mscNumber, vlrNumber;
      GeographicalInformation geographicalInformation;
      GeodeticInformation geodeticInformation;
      byte[] lteCgi;
      EUtranCgi eUtranCgi;
      byte[] trackingAreaId;
      TAId taId;
      RouteingNumber routeingNumber;
      SubscriberStateChoice subscriberStateChoice = null;
      PSSubscriberStateChoice psSubscriberStateChoice = null;
      NotReachableReason notReachableReason = null;
      ArrayList<PDPContextInfo> pdpContextInfoList = null;
      NumberPortabilityStatus numberPortabilityStatus;
      MAPExtensionContainer extensionContainer = null;
      UserCSGInformation userCSGInformation;
      IMSVoiceOverPsSessionsIndication imsVoiceOverPsSessionsIndication;
      Time lastUEActivityTime;
      UsedRATType lastRATType;
      PSSubscriberState epsSubscriberState = null;
      LocationInformationEPS locationInformationEPS = null;
      TimeZone timeZone = null;
      DaylightSavingTime daylightSavingTime = null;
      NRCellGlobalIdImpl nrCellGlobalIdentity = new NRCellGlobalIdImpl();
      FQDN amfAddress;
      PlmnId vplmnId;
      TimeZone localTimeZone;
      UsedRATType ratType;
      NRTAIdImpl nrTrackingAreaIdentity;
      LocationInformation5GS locationInformation5GS = null;
      Random rand = new Random();

      if (requestedInfo.getLocationInformation()) {
        switch(rand.nextInt(2) + 1) {
          case 1:
            saiPresent = true; // set saiPresent to false
            break;
          case 2:
            // keep saiPresent to false
            break;
        }
        switch (rand.nextInt(10) + 1) {
          case 1:
          case 2:
          case 3:
            subscriberStateChoice = SubscriberStateChoice.assumedIdle;
            psSubscriberStateChoice = PSSubscriberStateChoice.psAttachedReachableForPaging;
            break;
          case 4:
          case 5:
          case 6:
            subscriberStateChoice = SubscriberStateChoice.camelBusy;
            psSubscriberStateChoice = PSSubscriberStateChoice.psAttachedReachableForPaging;
            break;
          case 7:
            subscriberStateChoice = SubscriberStateChoice.netDetNotReachable;
            notReachableReason = NotReachableReason.imsiDetached;
            psSubscriberStateChoice = PSSubscriberStateChoice.netDetNotReachable;
            break;
          case 8:
            subscriberStateChoice = SubscriberStateChoice.notProvidedFromVLR;
            psSubscriberStateChoice = PSSubscriberStateChoice.notProvidedFromSGSNorMME;
            break;
          case 9:
            subscriberStateChoice = SubscriberStateChoice.netDetNotReachable;
            notReachableReason = NotReachableReason.restrictedArea;
            psSubscriberStateChoice = PSSubscriberStateChoice.netDetNotReachable;
            break;
          case 10:
            subscriberStateChoice = SubscriberStateChoice.netDetNotReachable;
            notReachableReason = NotReachableReason.msPurged;
            psSubscriberStateChoice = PSSubscriberStateChoice.psAttachedNotReachableForPaging;
            break;
          default:
            subscriberStateChoice = SubscriberStateChoice.assumedIdle;
            psSubscriberStateChoice = PSSubscriberStateChoice.notProvidedFromSGSNorMME;
            break;
        }
        if (requestedInfo.getSubscriberState()) {
          if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain)
            subscriberState = mapProvider.getMAPParameterFactory().createSubscriberState(subscriberStateChoice, notReachableReason);
        }
        TypeOfShape typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
        double geographicalLatitude;
        double geographicalLongitude;
        double geographicalUncertainty;
        double geodeticLatitude;
        double geodeticLongitude;
        double geodeticUncertainty;
        int geodeticConfidence = 1;
        int screeningAndPresentationIndicators = 3;
        switch(rand.nextInt(10) + 1) {
          case 1:
            mcc = 748;
            mnc = 1;
            lac = 101;
            cellId = 10263;
            geographicalLatitude = -34.909744;
            geographicalLongitude = -56.146317;
            geographicalUncertainty = 1.0;
            geographicalInformation = new GeographicalInformationImpl(typeOfShape, geographicalLatitude, geographicalLongitude, geographicalUncertainty);
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f8100007ea02"); // ECGI = 748-1-518658; TBCD encoded: 47f8100007ea02
            trackingAreaId = hexStringToByteArray("47f810006d"); // TAI = 748-1-109; TBCD encoded: 47f810006d
            break;
          case 2:
            mcc = 748;
            mnc = 1;
            lac = 119;
            cellId = 15336;
            geographicalInformation = null;
            geodeticLatitude = -34.910349;
            geodeticLongitude = -56.149832;
            geodeticUncertainty = 2.0;
            geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, typeOfShape, geodeticLatitude, geodeticLongitude, geodeticUncertainty, geodeticConfidence);
            lteCgi = hexStringToByteArray("47f81000095f02"); // ECGI = 748-1-614146; TBCD encoded: 47f81000095f02
            trackingAreaId = hexStringToByteArray("47f810006d"); // TAI = 748-1-109; TBCD encoded: 47f810006d
            break;
          case 3:
            mcc = 748;
            mnc = 1;
            lac = 118;
            cellId = 292;
            geographicalInformation = null;
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f870004b2c04"); // ECGI = 748-7-4926468; TBCD encoded: 47f870004b2c04
            trackingAreaId = hexStringToByteArray("47f8701b58"); // TAI = 748-7-7000; TBCD encoded: 47f8701b58
            break;
          case 4:
            mcc = 748;
            mnc = 1;
            lac = 109;
            cellId = 10175;
            geographicalInformation = null;
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f8100007f001"); // // ECGI = 748-1-520193; TBCD encoded: 47f8100007f001
            trackingAreaId = hexStringToByteArray("47f810006d"); // TAI = 748-1-109; TBCD encoded: 47f810006d
            break;
          case 5:
            mcc = 748;
            mnc = 1;
            lac = 11;
            cellId = 4812;
            geographicalInformation = null;
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f870004c2e08"); // ECGI = 748-7-4992520; TBCD encoded: 47f870004c2e08
            trackingAreaId = hexStringToByteArray("47f8701b58"); // TAI = 748-7-7000; TBCD encoded: 47f8701b58
            break;
          case 6:
            mcc = 748;
            mnc = 7;
            lac = 8820;
            cellId = 9748;
            geographicalInformation = null;
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f87000477304"); // ECGI = 748-7-4682500; TBCD encoded: 47f87000477304
            trackingAreaId = hexStringToByteArray("47f8701b6c"); // TAI = 748-7-7020; TBCD encoded: 47f8701b6c
            break;
          case 7:
            mcc = 748;
            mnc = 7;
            lac = 8552;
            cellId = 8239;
            geographicalInformation = null;
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f870004b3605"); // ECGI = 748-7-4929029; TBCD encoded: 47f870004b3605
            trackingAreaId = hexStringToByteArray("47f8701b58"); // TAI = 748-7-7000; TBCD encoded: 47f8701b58
            break;
          case 8:
            mcc = 748;
            mnc = 10;
            lac = 9501;
            cellId = 35100;
            geographicalInformation = null;
            geodeticLatitude = -34.905624;
            geodeticLongitude = -55.042191;
            geodeticUncertainty = 4.0;
            geodeticConfidence = 10;
            geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, typeOfShape, geodeticLatitude, geodeticLongitude, geodeticUncertainty, geodeticConfidence);
            lteCgi = hexStringToByteArray("47f81000004802"); // ECGI = 748-1-18434; TBCD encoded: 47f81000004802
            trackingAreaId = hexStringToByteArray("47f8100002"); // TAI = 748-1-2; TBCD encoded: 47f8100002
            break;
          case 9:
            mcc = 748;
            mnc = 7;
            lac = 8313;
            cellId = 9281;
            geographicalInformation = null;
            geodeticLatitude = -34.891032;
            geodeticLongitude = -56.0008102;
            geodeticUncertainty = 4.0;
            geodeticConfidence = 2;
            screeningAndPresentationIndicators = 1;
            geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, typeOfShape, geodeticLatitude, geodeticLongitude, geodeticUncertainty, geodeticConfidence);
            lteCgi = hexStringToByteArray("47f81000089700"); // ECGI = 748-1-562944; TBCD encoded: 47f81000089700
            trackingAreaId = hexStringToByteArray("47f8100067"); // TAI = 748-1-103; TBCD encoded: 47f8100067
            break;
          case 10:
            mcc = 748;
            mnc = 7;
            lac = 8820;
            cellId = 8051;
            geographicalInformation = null;
            geodeticInformation = null;
            lteCgi = hexStringToByteArray("47f8010000d502"); // ECGI = 748-10-54530; TBCD encoded: 47f8010000d502
            trackingAreaId = hexStringToByteArray("47f8017238"); // TAI = 748-10-29240; TBCD encoded: 47f8017238
            break;
          default:
            mcc = 748;
            mnc = 10;
            lac = 9501;
            cellId = 35100;
            geographicalInformation = null;
            geodeticLatitude = -34.905624;
            geodeticLongitude = -55.042190;
            geodeticUncertainty = 4.0;
            geodeticConfidence = 10;
            screeningAndPresentationIndicators = 3;
            geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, typeOfShape, geodeticLatitude, geodeticLongitude, geodeticUncertainty, geodeticConfidence);
            lteCgi = hexStringToByteArray("47f81000004802"); // ECGI = 748-1-18434; TBCD encoded: 47f81000004802
            trackingAreaId = hexStringToByteArray("47f8100002"); // TAI = 748-1-2; TBCD encoded: 47f8100002
            break;
        }
        if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
          mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,NumberingPlan.ISDN, mscAddress);
          vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                  NumberingPlan.ISDN, vlrAddress);
          int natureOfAddressIndicator = 4;
          String locationNumberAddressDigits= "819203961904";
          int numberingPlanIndicator = 1;
          int internalNetworkNumberIndicator = 1;
          int addressRepresentationRestrictedIndicator = 1;
          int screeningIndicator = 3;
          locationNumber = new LocationNumberImpl(natureOfAddressIndicator, locationNumberAddressDigits, numberingPlanIndicator,
                  internalNetworkNumberIndicator, addressRepresentationRestrictedIndicator, screeningIndicator);
          locationNumberMap = null;
          try {
            locationNumberMap = new LocationNumberMapImpl(locationNumber);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          eUtranCgi = new EUtranCgiImpl(lteCgi);
          taId = new TAIdImpl(trackingAreaId);
          String mmeNameStr = "mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org";
          byte[] mme = mmeNameStr.getBytes();
          DiameterIdentity mmeName = new DiameterIdentityImpl(mme);

          try {
            cgiOrSai = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, cellId);
          } catch (MAPException ex) {
            logger.error(ex.getMessage());
          }
          cellGlobalIdOrServiceAreaIdOrLAI = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cgiOrSai);
          if (subscriberStateChoice == SubscriberStateChoice.assumedIdle) {
            currentLocationRetrieved = true;
          } else if (subscriberStateChoice == SubscriberStateChoice.camelBusy) {
            currentLocationRetrieved = true;
          } else if (subscriberStateChoice == SubscriberStateChoice.notProvidedFromVLR) {
            ageOfLocationInformation = 3;
            currentLocationRetrieved = false;
          } else if (subscriberStateChoice == SubscriberStateChoice.netDetNotReachable) {
            if (notReachableReason == NotReachableReason.imsiDetached) {
              ageOfLocationInformation = 1575;
              currentLocationRetrieved = false;
              geographicalInformation = null;
              geodeticInformation = null;
              mscNumber = null;
              vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
            } else if (notReachableReason == NotReachableReason.restrictedArea) {
              ageOfLocationInformation = 300;
              currentLocationRetrieved = false;
              geographicalInformation = null;
              geodeticInformation = null;
              mscNumber = null;
              vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
            } else if (notReachableReason == NotReachableReason.msPurged) {
              ageOfLocationInformation = 221;
              currentLocationRetrieved = false;
              geographicalInformation = null;
              geodeticInformation = null;
              mscNumber = null;
              vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
            } else {
              ageOfLocationInformation = 1879;
              currentLocationRetrieved = false;
              geographicalInformation = null;
              geodeticInformation = null;
              mscNumber = null;
              vlrNumber = mapProvider.getMAPParameterFactory().createISDNAddressString(AddressNature.international_number,
                      NumberingPlan.ISDN, vlrAddress);
            }
          }
          if (!requestedInfo.getLocationInformationEPSSupported()) {
            locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(ageOfLocationInformation, geographicalInformation,
                    vlrNumber, locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, null, null, mscNumber, geodeticInformation,
                    currentLocationRetrieved, saiPresent, locationInformationEPS, null);
          } else {
            switch(rand.nextInt(10) + 1) {
              case 1:
                BitSetStrictLength csgIdBitSet = new BitSetStrictLength(27);
                csgIdBitSet.set(0);
                csgIdBitSet.set(1);
                csgIdBitSet.set(25);
                csgIdBitSet.set(26);
                CSGId csgId = new CSGIdImpl(csgIdBitSet);
                Integer accessMode = 1;
                Integer cmi = 2;
                userCSGInformation = new UserCSGInformationImpl(csgId, null, accessMode, cmi);
                currentLocationRetrieved = ageOfLocationInformation == 0;
                // location information not containing EPS location as the target subscriber is not under E-UTRAN
                locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(ageOfLocationInformation, geographicalInformation,
                        vlrNumber, locationNumberMap, cellGlobalIdOrServiceAreaIdOrLAI, null, null, mscNumber, geodeticInformation,
                        currentLocationRetrieved, saiPresent, locationInformationEPS, userCSGInformation);
                break;
              case 2:
              case 3:
                currentLocationRetrieved = ageOfLocationInformation == 0;
                // target subscriber is under 5G NR SA
                nrCellGlobalIdentity.setData(748, 1, 42949672954L);
                amfAddress = new FQDNImpl("amf1.cluster1.net2.amf.5gc.mnc01.mcc748.3gppnetwork.org".getBytes());
                vplmnId = new PlmnIdImpl(748, 1);
                localTimeZone = new TimeZoneImpl(new byte[] {0,9});
                ratType = UsedRATType.eUtran;
                nrTrackingAreaIdentity = new NRTAIdImpl();
                nrTrackingAreaIdentity.setData(748, 1, 595578);
                geographicalLatitude = -34.909744;
                geographicalLongitude = -56.146317;
                geographicalUncertainty = 1.0;
                geographicalInformation = new GeographicalInformationImpl(typeOfShape, geographicalLatitude, geographicalLongitude, geographicalUncertainty);
                geodeticInformation = null;
                locationInformation5GS = new LocationInformation5GSImpl(nrCellGlobalIdentity, eUtranCgi, geographicalInformation,
                        geodeticInformation, amfAddress, taId, currentLocationRetrieved, ageOfLocationInformation, vplmnId,
                        localTimeZone, ratType, null, nrTrackingAreaIdentity);
                break;
              case 4:
              case 5:
                // target subscriber is under 5G NSA (E-UTRAN and NR)
                nrCellGlobalIdentity.setData(748, 2, 34359738376L);
                amfAddress = new FQDNImpl("amf3.cluster2.net2.amf.5gc.mnc02.mcc748.3gppnetwork.org".getBytes());
                vplmnId = new PlmnIdImpl(748, 2);
                localTimeZone = new TimeZoneImpl(new byte[] {0, 8});
                ratType = UsedRATType.eUtran;
                nrTrackingAreaIdentity = new NRTAIdImpl();
                nrTrackingAreaIdentity.setData(748, 2, 495570);
                currentLocationRetrieved = ageOfLocationInformation == 0;
                geodeticLatitude = -34.910349;
                geodeticLongitude = -56.149832;
                geodeticUncertainty = 2.0;
                geodeticInformation = new GeodeticInformationImpl(screeningAndPresentationIndicators, typeOfShape, geodeticLatitude, geodeticLongitude, geodeticUncertainty, geodeticConfidence);
                locationInformation5GS = new LocationInformation5GSImpl(nrCellGlobalIdentity, eUtranCgi, geographicalInformation,
                        geodeticInformation, amfAddress, taId, currentLocationRetrieved, ageOfLocationInformation, vplmnId,
                        localTimeZone, ratType, null, nrTrackingAreaIdentity);
                lteCgi = hexStringToByteArray("47f81000095f02"); // ECGI = 748-1-614146; TBCD encoded: 47f81000095f02
                trackingAreaId = hexStringToByteArray("47f810006d"); // TAI = 748-1-109; TBCD encoded: 47f810006d
                eUtranCgi = new EUtranCgiImpl(lteCgi);
                taId = new TAIdImpl(trackingAreaId);
                mmeNameStr = "mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org";
                mme = mmeNameStr.getBytes();
                mmeName = new DiameterIdentityImpl(mme);
                locationInformationEPS = new LocationInformationEPSImpl(eUtranCgi, taId, extensionContainer, geographicalInformation,
                        geodeticInformation, currentLocationRetrieved, ageOfLocationInformation, mmeName);
                locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(null, null,
                        null, null, null, null, null, null, null,
                        false, false, locationInformationEPS, null);
                locationInformationEPS = null; // If the HLR receives locationInformationEPS (outside the locationInformation IE) from a VLR, it shall discard it.
                break;
              default:
                currentLocationRetrieved = ageOfLocationInformation == 0;
                // target subscriber has EPS location information within CS location information
                locationInformationEPS = new LocationInformationEPSImpl(eUtranCgi, taId, extensionContainer, geographicalInformation,
                        geodeticInformation, currentLocationRetrieved, ageOfLocationInformation, mmeName);
                locationInformation = mapProvider.getMAPParameterFactory().createLocationInformation(null, null,
                        null, null, null, null, null, null, null,
                        false, false, locationInformationEPS, null);
                break;
            }
          }
        }
      }

      if (requestedInfo.getMnpRequestedInfo()) {
        if (subscriberStateChoice != SubscriberStateChoice.netDetNotReachable &&
                psSubscriberStateChoice != PSSubscriberStateChoice.psAttachedNotReachableForPaging &&
                psSubscriberStateChoice != PSSubscriberStateChoice.netDetNotReachable) {
          routeingNumber = new RouteingNumberImpl("491710");
          long imsiDigits = RandomUtils.nextLong(748020000000000L, 748030000000000L);
          IMSI mnpImsi = new IMSIImpl(String.valueOf(imsiDigits));
          ISDNAddressString mnpMsisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                  NumberingPlan.ISDN, "59899077937");
          numberPortabilityStatus = NumberPortabilityStatus.ownNumberNotPortedOut;
          mnpInfoRes = mapProvider.getMAPParameterFactory().createMNPInfoRes(routeingNumber, mnpImsi, mnpMsisdn, numberPortabilityStatus, extensionContainer);
        }
      }

      if (requestedInfo.getImei()) {
        if (subscriberStateChoice != SubscriberStateChoice.netDetNotReachable &&
                psSubscriberStateChoice != PSSubscriberStateChoice.psAttachedNotReachableForPaging &&
                psSubscriberStateChoice != PSSubscriberStateChoice.netDetNotReachable) {
          if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
            imei = mapProvider.getMAPParameterFactory().createIMEI("011714004661050");
          } else {
            imei = mapProvider.getMAPParameterFactory().createIMEI("011714004661051");
          }
        }
      }

      if (requestedInfo.getMsClassmark()) {
        if (requestedInfo.getRequestedDomain() == null || requestedInfo.getRequestedDomain() == DomainType.csDomain) {
          if (subscriberStateChoice != SubscriberStateChoice.netDetNotReachable) {
            byte[] classmark = {57, 58, 82};
            msClassmark2 = mapProvider.getMAPParameterFactory().createMSClassmark2(classmark);
          }
        }
      }

      imsVoiceOverPsSessionsIndication = IMSVoiceOverPsSessionsIndication.imsVoiceOverPSSessionsNotSupported;
      lastUEActivityTime = new TimeImpl(2024, 8, 5, 10, 27, 49);
      lastRATType = UsedRATType.eUtran;
      if (requestedInfo.getSubscriberState() && requestedInfo.getLocationInformationEPSSupported()) {
        if (locationInformationEPS != null)
          epsSubscriberState = new PSSubscriberStateImpl(psSubscriberStateChoice, notReachableReason, pdpContextInfoList);
      }
      if (requestedInfo.getLocalTimeZoneRequest()) {
        timeZone = new TimeZoneImpl(new byte[]{0, 3});
        daylightSavingTime = DaylightSavingTime.noAdjustment;
      }

      // If the HLR receives locationInformationGPRS, ps-SubscriberState, gprs-MS-Class or
      // locationInformationEPS (outside the locationInformation IE) from a VLR, it shall discard them.
      subscriberInfo = new SubscriberInfoImpl(locationInformation, subscriberState, null,
              null, null, imei, msClassmark2, null, mnpInfoRes,
              imsVoiceOverPsSessionsIndication, lastUEActivityTime, lastRATType, epsSubscriberState,
              null, timeZone, daylightSavingTime, locationInformation5GS);

      mapDialogMobility.addAnyTimeInterrogationResponse(invokeId, subscriberInfo, null);

      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogMobility.close(false);

    } catch (MAPException mapException) {
      logger.error("MAP Exception while processing AnyTimeInterrogationRequest ", mapException);
    } catch (Exception e) {
      logger.error("Exception while processing AnyTimeInterrogationRequest ", e);
    }

  }

  public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse atiResp) {
    /*
     * This is an error condition. Server should never receive onAnyTimeInterrogationResponse.
     */
    logger.error(String.format("onAnyTimeInterrogationRequest for Dialog=%d and invokeId=%d",
        atiResp.getMAPDialog().getLocalDialogId(), atiResp.getInvokeId()));

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
  public void onSendRoutingInfoForLCSRequest(SendRoutingInfoForLCSRequest sendRoutingInfoForLCSRequestIndication) {

    if (logger.isDebugEnabled()) {
      logger.debug(
          String.format("onSendRoutingInfoForLCSRequest for DialogId=%d", sendRoutingInfoForLCSRequestIndication.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("onAnyTimeInterrogationRequest for DialogId=%d", sendRoutingInfoForLCSRequestIndication.getMAPDialog().getLocalDialogId()));
    }

    try {
      long invokeId = sendRoutingInfoForLCSRequestIndication.getInvokeId();
      MAPDialogLsm mapDialogLsm = sendRoutingInfoForLCSRequestIndication.getMAPDialog();
      mapDialogLsm.setUserObject(invokeId);

      // Create Routing Information parameters for concerning MAP operation
      MAPParameterFactoryImpl mapFactory = new MAPParameterFactoryImpl();
      // Create Routing Information parameters for concerning MAP operation
      Random rand = new Random();
      SubscriberIdentity subscriberIdentity;
      if (sendRoutingInfoForLCSRequestIndication.getTargetMS().getIMSI() != null) {
        long msisdnDigits = RandomUtils.nextLong(59898000000L, 59899000000L);
        ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN,
                String.valueOf(msisdnDigits));
        subscriberIdentity = new SubscriberIdentityImpl(msisdn);
      } else {
        long imsiDigits = RandomUtils.nextLong(748020000000000L, 748030000000000L);
        subscriberIdentity = new SubscriberIdentityImpl(new IMSIImpl(String.valueOf(imsiDigits)));
      }
      ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "491710460015");
      ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "491710460025");
      AdditionalNumber additionalNumber = new AdditionalNumberImpl(null, sgsnNumber);
      LMSI lmsi;
      switch (rand.nextInt(10) + 1) {
        case 1:
          lmsi = new LMSIImpl(new byte[] {114, 2, (byte) 233, (byte) 140});
          break;
        case 2:
          lmsi = new LMSIImpl(new byte[] {113, (byte) 255, (byte) 172, (byte) 206});
          break;
        case 3:
          lmsi = new LMSIImpl(new byte[] {114, 2, (byte) 235, 55});
          break;
        case 4:
          lmsi = new LMSIImpl(new byte[] {114, 2, (byte) 231, (byte) 213});
          break;
        default:
          lmsi = null;
          break;
      }
      boolean gprsNodeIndicator = false;
      boolean lcsCapabilitySetRelease98_99 = true;
      boolean lcsCapabilitySetRelease4 = true;
      boolean lcsCapabilitySetRelease5 = true;
      boolean lcsCapabilitySetRelease6 = true;
      boolean lcsCapabilitySetRelease7 = false;
      SupportedLCSCapabilitySets supportedLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99, lcsCapabilitySetRelease4,
              lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
      lcsCapabilitySetRelease7 = true;
      SupportedLCSCapabilitySets additionalLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99, lcsCapabilitySetRelease4,
              lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
      DiameterIdentity mmeName = new DiameterIdentityImpl("mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      DiameterIdentity aaaServerName = new DiameterIdentityImpl("aaa3000.aaa.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      DiameterIdentity sgsnName = new DiameterIdentityImpl("mme.20.mag.epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      DiameterIdentity sgsnRealm = new DiameterIdentityImpl("epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
      LCSLocationInfo lcsLocationInfo = mapFactory.createLCSLocationInfo(mscNumber, lmsi, null, gprsNodeIndicator,
              additionalNumber, supportedLCSCapabilitySets, additionalLCSCapabilitySets, mmeName, aaaServerName, sgsnName, sgsnRealm);

      GSNAddress vGmlcAddress = new GSNAddressImpl(GSNAddressAddressType.IPv4, new byte[] { 0x5a, 0x03, 0x78, 5 });
      GSNAddress hGmlcAddress = new GSNAddressImpl(GSNAddressAddressType.IPv4, new byte[] { 0x0a, 0x00, 0x00, 0x0e });
      GSNAddress pprAddress = new GSNAddressImpl(GSNAddressAddressType.IPv4, new byte[] { 0x0a, 0x00, 0x00, 0x12 });
      GSNAddress additionalVGmlcAddress = new GSNAddressImpl(GSNAddressAddressType.IPv6, new byte[] { 0x5a, 0, 0, 0, 0, 2, 65, 4, 0, 0, 0, 3, 42, 5, 120, 91 });

      mapDialogLsm.addSendRoutingInfoForLCSResponse(invokeId, subscriberIdentity, lcsLocationInfo, null, vGmlcAddress, hGmlcAddress,
              pprAddress, additionalVGmlcAddress);
      // This will initiate the TC-BEGIN with INVOKE component
      mapDialogLsm.close(false);

      /*
       * Create Dialog for sending not deferred MAP SLR to the GMLC
       */
      AddressString origRef = mapProvider.getMAPParameterFactory()
              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "491710460015");
      AddressString destRef = mapProvider.getMAPParameterFactory()
              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "491710470201");
      SccpAddress origSccpAddress = createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, SERVER_SPC, SERVER_SSN, "491710460015");
      SccpAddress destSccpAddress = createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, CLIENT_SPC, CLIENT_SSN, "491710470201");
      MAPDialogLsm slrDialog = mapProvider.getMAPServiceLsm()
              .createNewDialog(MAPApplicationContext.getInstance(MAPApplicationContextName.locationSvcEnquiryContext,
                      MAPApplicationContextVersion.version3), origSccpAddress, origRef, destSccpAddress, destRef);

      sendMapSLR(slrDialog, false, null);

    } catch (MAPException mapException) {
      logger.error("MAP Exception while processing onSendRoutingInfoForLCSRequest ", mapException);
    } catch (Exception e) {
      logger.error("Exception while processing onSendRoutingInfoForLCSRequest ", e);
    }
  }

  @Override
  public void onSendRoutingInfoForLCSResponse(SendRoutingInfoForLCSResponse sendRoutingInforForLCSResponse) {
    /*
     * This is an error condition. Server should never receive onSendRoutingInfoForLCSResponse.
     */
    logger.error(String.format("onSendRoutingInfoForLCSResponse for Dialog=%d and invokeId=%d",
        sendRoutingInforForLCSResponse.getMAPDialog().getLocalDialogId(), sendRoutingInforForLCSResponse.getInvokeId()));

  }

  @Override
  public void onProvideSubscriberLocationRequest(ProvideSubscriberLocationRequest provideSubscriberLocationRequestIndication) {

    if (logger.isDebugEnabled()) {
      logger.debug(
          String.format("onProvideSubscriberLocationRequest for DialogId=%d", provideSubscriberLocationRequestIndication.getMAPDialog().getLocalDialogId()));
    }
    if (logger.isInfoEnabled()) {
      logger.info(String.format("onProvideSubscriberLocationRequest for DialogId=%d", provideSubscriberLocationRequestIndication.getMAPDialog().getLocalDialogId()));
    }

    try {
      long invokeId = provideSubscriberLocationRequestIndication.getInvokeId();
      MAPDialogLsm mapDialogPslResponse = provideSubscriberLocationRequestIndication.getMAPDialog();
      mapDialogPslResponse.setUserObject(invokeId);

      // Create Routing Information parameters for concerning MAP operation
      MAPParameterFactoryImpl mapParameterFactory = new MAPParameterFactoryImpl();
      Random rand = new Random();

      ExtGeographicalInformation locationEstimate = null;
      TypeOfShape typeOfShape = null;
      double latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, uncertaintyAltitude, uncertaintyRadius,
              offsetAngle, includedAngle;
      int confidence, altitude, innerRadius;
      EllipsoidPoint ellipsoidPoint1, ellipsoidPoint2, ellipsoidPoint3, ellipsoidPoint4, ellipsoidPoint5, ellipsoidPoint6;
      // ellipsoidPoint7, ellipsoidPoint8, ellipsoidPoint9, ellipsoidPoint10, ellipsoidPoint11, ellipsoidPoint12, ellipsoidPoint13,
      // ellipsoidPoint14, ellipsoidPoint15;
      // 3 <= numberOfPoints <= 15
      Integer ageOfLocationEstimate = null;
      AddGeographicalInformation additionalLocationEstimate = null;
      AccuracyFulfilmentIndicator accuracyFulfilmentIndicator = AccuracyFulfilmentIndicator.requestedAccuracyFulfilled;
      switch (rand.nextInt(6) + 1) {
        case 1:
          typeOfShape = TypeOfShape.EllipsoidPoint;
          latitude = 34.909744;
          longitude = -56.146317;
          try {
            locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPoint(latitude, longitude);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          ageOfLocationEstimate = 0;
          break;
        case 2:
          typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
          latitude = -34.910349;
          longitude = -56.149832;
          uncertainty = 5.1;
          accuracyFulfilmentIndicator = AccuracyFulfilmentIndicator.requestedAccuracyNotFulfilled;
          try {
            locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPointWithUncertaintyCircle(latitude, longitude, uncertainty);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          ageOfLocationEstimate = 1;
          break;
        case 3:
          typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyEllipse;
          latitude = -34.905624;
          longitude = -55.042191;
          uncertaintySemiMajorAxis = 21.2;
          uncertaintySemiMinorAxis = 10.4;
          angleOfMajorAxis = 30.0; // orientation of major axis
          confidence = 1;
          try {
            locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPointWithUncertaintyEllipse(latitude, longitude,
                    uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, confidence);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          ageOfLocationEstimate = 0;
          break;
        case 4:
          typeOfShape = TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid;
          latitude = -34.956436;
          longitude = -54.937820;
          altitude = 570;
          uncertaintySemiMajorAxis = 25.4;
          uncertaintySemiMinorAxis = 12.1;
          angleOfMajorAxis = 30.2; // orientation of major axis
          uncertaintyAltitude = 80.1;
          confidence = 5;
          try {
            locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPointWithAltitudeAndUncertaintyEllipsoid(latitude,
                    longitude, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, confidence, altitude, uncertaintyAltitude);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          ageOfLocationEstimate = 5;
          accuracyFulfilmentIndicator = AccuracyFulfilmentIndicator.requestedAccuracyNotFulfilled;
          break;
        case 5:
          typeOfShape = TypeOfShape.EllipsoidArc;
          latitude = -34.939956;
          longitude = -54.914474;
          innerRadius = 5;
          uncertaintyRadius = 1.50;
          offsetAngle = 20.0;
          includedAngle = 20.0;
          confidence = 2;
          try {
            locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidArc(latitude, longitude, innerRadius,
                    uncertaintyRadius, offsetAngle, includedAngle, confidence);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          ageOfLocationEstimate = 10;
          break;
        case 6:
          typeOfShape = TypeOfShape.Polygon;
          latitude = 0.0;
          longitude = 0.0;
          try {
            locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPoint(latitude, longitude);
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
          ageOfLocationEstimate = 0;
          break;
      }

      if (typeOfShape == TypeOfShape.Polygon) {
        try {
          switch (rand.nextInt(6) + 1) {
            case 1:
              byte[] polygonData1 = { 83,
                      41, (byte) 234, (byte) 138, 55, 67, 17,
                      41, (byte) 234, (byte) 136, 55, 67, 3,
                      41, (byte) 234, 0, 55, 67, 24};
              Polygon polygon1 = new PolygonImpl(polygonData1);
              additionalLocationEstimate = new AddGeographicalInformationImpl(polygon1.getData());
              break;
            case 2:
              byte[] polygonData2 = { 83,
                      44, 29, (byte) 188, 53, (byte) 227, (byte) 135,
                      44, 29, (byte) 193, 53, (byte) 227, (byte) 130,
                      44, 29, (byte) 190, 53, (byte) 227, 123};
              Polygon polygon2 = new PolygonImpl(polygonData2);
              additionalLocationEstimate = new AddGeographicalInformationImpl(polygon2.getData());
              break;
            case 3:
              byte[] polygonData3 = { 83,
                      36, (byte) 167, 60, 52, 37, 0,
                      36, (byte) 167, 49, 52, 36, (byte) 255,
                      36, (byte) 167, 50, 52, 37, 0};
              Polygon polygon3 = new PolygonImpl(polygonData3);
              additionalLocationEstimate = new AddGeographicalInformationImpl(polygon3.getData());
              break;
            case 4:
              byte[] polygonData4 = { 83,
                      36, 124, (byte) 163, 59, 49, 112,
                      36, 126, 7, 59, 49, (byte) 138,
                      36, 127, (byte) 224, 59, 49, 72};
              Polygon polygon4 = new PolygonImpl(polygonData4);
              additionalLocationEstimate = new AddGeographicalInformationImpl(polygon4.getData());
              break;
            case 5:
              byte[] polygonData5 = { 84,
                      37, (byte) 229, (byte) 179, 52, 66, (byte) 211,
                      37, (byte) 230, 64, 52, 67, 124,
                      37, (byte) 230, (byte) 131, 52, 67, 121,
                      37, (byte) 230, (byte) 132, 52, 67, 125};
              Polygon polygon5 = new PolygonImpl(polygonData5);
              additionalLocationEstimate = new AddGeographicalInformationImpl(polygon5.getData());
              break;
            case 6:
              ellipsoidPoint1 = new EllipsoidPoint(-2.907010, 70.778014);
              ellipsoidPoint2 = new EllipsoidPoint(-3.017238, 70.708922);
              ellipsoidPoint3 = new EllipsoidPoint(-2.941387, 70.432091);
              ellipsoidPoint4 = new EllipsoidPoint(-3.040019, 70.681903);
              ellipsoidPoint5 = new EllipsoidPoint(-3.045001, 70.700109);
              ellipsoidPoint6 = new EllipsoidPoint(-2.989001, 71.000004);
              EllipsoidPoint[] ellipsoidPoints = {ellipsoidPoint1, ellipsoidPoint2, ellipsoidPoint3, ellipsoidPoint4, ellipsoidPoint5, ellipsoidPoint6};
              PolygonImpl polygon6 = new PolygonImpl();
              polygon6.setData(ellipsoidPoints);
              additionalLocationEstimate = new AddGeographicalInformationImpl(polygon6.getData());
              break;
          }
        } catch (MAPException e) {
          logger.error(e.getMessage());
        }
      }

      PositioningDataInformationImpl geranPositioningDataInfo =  null;
      UtranPositioningDataInfoImpl utranPositioningDataInfo = null;
      GeranGANSSpositioningDataImpl geranGanssPositioningData = null;
      UtranGANSSpositioningDataImpl utranGanssPositioningData = null;
      UtranAdditionalPositioningData utranAdditionalPositioningData = null;
      // Method=Mobile Based E-OTD, Usage=1: Attempted successfully: results not used to generate location
      // Method=Mobile Assisted E-OTD, Usage=3: Attempted successfully: results used to generate location
      // Method=U-TDOA, Usage=3: Attempted successfully: results used to generate location
      // Method=Cell ID, Usage=0: Attempted unsuccessfully due to failure or interruption
      // Method=Mobile Assisted GPS, Usage=3: Attempted successfully: results used to generate location
      // Method=Timing Advance, Usage=3: Attempted successfully: results used to generate location
      // Method=Conventional GPS, Usage=2: Attempted successfully: results used to verify but not generate location
      // byte[] geranPosData = new byte[] {0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60};

      // Method=OTDOA, Usage=3: Attempted successfully: results used to generate location
      // Method=Reserved (GERAN use only), Usage=0: Attempted unsuccessfully due to failure or interruption - not used
      // Method=U-TDOA, Usage=3: Attempted successfully: results used to generate location
      // Method=Cell ID, Usage=2: Attempted successfully: results used to verify but not generate location - not used
      // Method=Mobile Assisted GPS, Usage=3: Attempted successfully: results used to generate location
      // byte[] utranPosData = new byte[] {0x00, 0x00, 0x43, 0x4b, 0x00, 0x62, 0x2b};

      // Method=MS-Based, GANSSId=Galileo
      // Method=MS-Assisted, GANSSId=GLONASS
      // Method=Conventional, GANSSId=SBAS
      // byte[] geranGANSSData = new byte[] {0x00, 0x63, (byte) 0x8b, 0x02, 0x03};

      // Method=MS-Based, GANSSId=Galileo
      // Method=MS-Assisted, GANSSId=GLONASS
      // Method=Conventional, GANSSId=SBAS
      // byte[] utranGanssData = new byte[] {0x01, 0x63, (byte) 0x8b, 0x02, 0x03};

      // Method=Standalone, AddPosId=WLAN
      // Method=MS-Assisted, AddPosId=Bluetooth
      // byte[] utranAddPosData = new byte[] {0x57, (byte) 0x8F};

      switch (rand.nextInt(4) + 1) {
        case 1:
          geranPositioningDataInfo = new PositioningDataInformationImpl(new byte[] {0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60});
          break;
        case 2:
          geranPositioningDataInfo = new PositioningDataInformationImpl(new byte[] {0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60});
          geranGanssPositioningData = new GeranGANSSpositioningDataImpl(new byte[] {0x00, 0x63, (byte) 0x8b, 0x02, 0x03});
          break;
        case 3:
          utranPositioningDataInfo = new UtranPositioningDataInfoImpl(new byte[] {0x00, 0x00, 0x43, 0x4b, 0x00, 0x62, 0x2b});
          break;
        case 4:
          utranPositioningDataInfo = new UtranPositioningDataInfoImpl(new byte[] {0x00, 0x00, 0x43, 0x4b, 0x00, 0x62, 0x2b});
          utranGanssPositioningData = new UtranGANSSpositioningDataImpl(new byte[] {0x01, 0x63, (byte) 0x8b, 0x02, 0x03});
          utranAdditionalPositioningData = new UtranAdditionalPositioningDataImpl(new byte[] {0x57, (byte) 0x8F});
          break;
      }

      boolean deferredMTLRResponseIndicator = false;
      LocationType locationType = provideSubscriberLocationRequestIndication.getLocationType();
      if (locationType.getDeferredLocationEventType() != null) {
        deferredMTLRResponseIndicator = true;
      }

      int mcc, mnc, lac, ci;
      mcc = 748;
      mnc = 1;
      lac = 101;
      ci = 10263;
      boolean saiPresent = false;
      switch(rand.nextInt(10) + 1) {
        case 1:
          saiPresent = true;
          break;
        case 2:
          lac = 119;
          ci = 15336;
          break;
        case 3:
          lac = 118;
          ci = 292;
          break;
        case 4:
          lac = 109;
          ci = 10175;
          saiPresent = true;
          break;
        case 5:
          lac = 11;
          ci = 4812;
          saiPresent = true;
          break;
        case 6:
          mnc = 7;
          lac = 8820;
          ci = 9748;
          break;
        case 7:
          mnc = 7;
          lac = 8552;
          ci = 8239;
          saiPresent = true;
          break;
        case 8:
          mnc = 10;
          lac = 9501;
          ci = 35100;
          break;
        case 9:
          mnc = 7;
          lac = 8313;
          ci = 9281;
          saiPresent = true;
          break;
        case 10:
          mnc = 7;
          lac = 8820;
          ci = 8051;
          break;
      }
      CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
      CellGlobalIdOrServiceAreaIdFixedLength cgiOrSai = null;
      try {
        cgiOrSai = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, ci);
      } catch (MAPException ex) {
        logger.error(ex.getMessage());
      }
      cellGlobalIdOrServiceAreaIdOrLAI = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cgiOrSai);

      VelocityEstimate velocityEstimate = null;
      if (provideSubscriberLocationRequestIndication.getLCSQoS() != null) {
        if (provideSubscriberLocationRequestIndication.getLCSQoS().getVelocityRequest()) {
          VelocityType velocityType = VelocityType.HorizontalWithVerticalVelocityAndUncertainty;
          int horizontalSpeed = 101;
          int bearing = 3;
          int verticalSpeed = 2;
          int uncertaintyHorizontalSpeed = 5;
          int uncertaintyVerticalSpeed = 1;
          velocityEstimate = new VelocityEstimateImpl(velocityType, horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed);
        }
      }

      boolean moLrShortCircuitIndicator = provideSubscriberLocationRequestIndication.getMoLrShortCircuitIndicator();

      ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number,
              NumberingPlan.ISDN, "491710460015");
      ServingNodeAddress targetServingNodeForHandover = new ServingNodeAddressImpl(networkNodeNumber, true);

      Integer utranBaroPressureMeas = 110000; // UtranBaroPressureMeas ::= INTEGER (30000..115000)

      String civicAddressString = "<cl:civicAddress>\n" +
              "                        <cl:country>US</cl:country>\n" +
              "                        <cl:A1>New York</cl:A1>\n" +
              "                        <cl:A3>New York</cl:A3>\n" +
              "                        <cl:A6>Broadway</cl:A6>\n" +
              "                        <cl:HNO>123</cl:HNO>\n" +
              "                        <cl:LOC>Suite 75</cl:LOC>\n" +
              "                        <cl:PC>10027-0401</cl:PC>\n" +
              "                    </cl:civicAddress>";
      byte[] civicAddressByteArray = civicAddressString.getBytes(StandardCharsets.UTF_8);
      UtranCivicAddress utranCivicAddress = new UtranCivicAddressImpl(civicAddressByteArray);

      mapDialogPslResponse.addProvideSubscriberLocationResponse(invokeId, locationEstimate, geranPositioningDataInfo, utranPositioningDataInfo,
              ageOfLocationEstimate, additionalLocationEstimate, null, deferredMTLRResponseIndicator,
              cellGlobalIdOrServiceAreaIdOrLAI, saiPresent, accuracyFulfilmentIndicator, velocityEstimate,
              moLrShortCircuitIndicator, geranGanssPositioningData, utranGanssPositioningData,
              targetServingNodeForHandover, utranAdditionalPositioningData, utranBaroPressureMeas, utranCivicAddress);

      mapDialogPslResponse.close(false);

      Thread.sleep(2000);
      /*
       * Create Dialog for sending not deferred MAP SLR to the GMLC
       */
      AddressString origRef = mapProvider.getMAPParameterFactory()
              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "491710460015");
      AddressString destRef = mapProvider.getMAPParameterFactory()
              .createAddressString(AddressNature.international_number, NumberingPlan.ISDN, "491710470201");
      SccpAddress origSccpAddress = createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, SERVER_SPC, SERVER_SSN, "491710460015");
      SccpAddress destSccpAddress = createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, CLIENT_SPC, CLIENT_SSN, "491710470201");
      MAPDialogLsm slrDialog = mapProvider.getMAPServiceLsm()
              .createNewDialog(MAPApplicationContext.getInstance(MAPApplicationContextName.locationSvcEnquiryContext,
                      MAPApplicationContextVersion.version3), origSccpAddress, origRef, destSccpAddress, destRef);

      Integer lcsReferenceNumber = rand.nextInt(Integer.MAX_VALUE) - 1;
      sendMapSLR(slrDialog, true, lcsReferenceNumber);

    } catch (MAPException mapException) {
      logger.error("MAP Exception while processing onProvideSubscriberLocationRequest ", mapException);
    } catch (Exception e) {
      logger.error("Exception while processing onProvideSubscriberLocationRequest ", e);
    }

  }

  @Override
  public void onProvideSubscriberLocationResponse(ProvideSubscriberLocationResponse provideSubscriberLocationResponse) {

    /*
     * This is an error condition. Server should never receive onProvideSubscriberLocationResponse.
     */
    logger.error(String.format("onProvideSubscriberLocationResponse for Dialog=%d and invokeId=%d",
        provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId(), provideSubscriberLocationResponse.getInvokeId()));

  }

  private void sendMapSLR(MAPDialogLsm mapDialogSLR, boolean isDeferred, Integer pslReferenceNumber) {
    try {
      NetworkIdState networkIdState = this.mapStack.getMAPProvider().getNetworkIdState(0);
      if (!(networkIdState == null || networkIdState.isAvailable() && networkIdState.getCongLevel() == 0)) {
        // congestion or unavailable
        logger.warn("Outgoing congestion control: MAP load test client: networkIdState=" + networkIdState);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          logger.error(String.format("Error on sendMapSLR method:" + e));
        }
      }

      /*
       * subscriberLocationReport OPERATION ::= { --Timer m ARGUMENT
       *   SubscriberLocationReport-Arg RESULT SubscriberLocationReport-Res
       *   ERRORS { systemFailure | dataMissing | resourceLimitation | unexpectedDataValue | unknownSubscriber |
       *   unauthorizedRequestingNetwork | unknownOrUnreachableLCSClient} CODE local:86 }
       *
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
       *
       *  -- one of msisdn or imsi is mandatory
       *
       *  -- a location estimate that is valid for the locationEstimate parameter should
       *  -- be transferred in this parameter in preference to the add-LocationEstimate.
       *
       *  -- the deferredmt-lrData parameter shall be included if and only if the lcs-Event
       *  -- indicates a deferredmt-lrResponse.
       *
       *  -- if the lcs-Event indicates a deferredmt-lrResponse then the locationEstimate
       *  -- and the add-locationEstimate parameters shall not be sent if the
       *  -- supportedGADShapes parameter had been received in ProvideSubscriberLocation-Arg
       *  -- and the shape encoded in locationEstimate or add-LocationEstimate was not marked
       *  -- as supported in supportedGADShapes. In such a case terminationCause
       *  -- in deferredmt-lrData shall be present with value
       *  -- shapeOfLocationEstimateNotSupported.
       *
       *  -- If a lcs event indicates deferred mt-lr response, the lcs-Reference number shall be
       *  -- included.
       *
       *  -- sai-Present indicates that the cellIdOrSai parameter contains a Service Area Identity
       *
       *  SequenceNumber ::= INTEGER (1..8639999)
       */

      // Then, create parameters for concerning MAP operation
      try {
        MAPParameterFactoryImpl mapParameterFactory = new MAPParameterFactoryImpl();
        Random rand = new Random();
        ISDNAddressString msisdn = null;
        IMSI imsi = null;
        int msisdnOrImsi = rand.nextInt(10) + 1;
        // -- one of msisdn or imsi is mandatory
        if (msisdnOrImsi == 1) {
          long msisdnDigits = RandomUtils.nextLong(59898000000L, 59899000000L);
          msisdn = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, String.valueOf(msisdnDigits));
        } else {
          long imsiDigits = RandomUtils.nextLong(748020000000000L, 748030000000000L);
          imsi = new IMSIImpl(String.valueOf(imsiDigits));
        }

        long imeiDigits = RandomUtils.nextLong(100710000000000L, 100720000000000L);
        IMEI imei = new IMEIImpl(String.valueOf(imeiDigits));
        //ISDNAddressString mscNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710460015");
        ISDNAddressString sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "491710460025");

        ISDNAddressString naEsrd = null;
        ISDNAddressString naEsrk = null;
        boolean naEsrkRequest = false;
        switch (rand.nextInt(3) + 1) {
          case 1:
            naEsrd = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "1210101075");
            break;
          case 2:
            naEsrk = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, "9289277009");
            break;
          default:
            naEsrkRequest = true;
            break;
        }

        // LCS-Event ::= ENUMERATED { emergencyCallOrigination (0), emergencyCallRelease (1), mo-lr (2), ..., deferredmt-lrResponse (3) }
        LCSEvent lcsEvent = null;
        if (isDeferred) {
          lcsEvent = LCSEvent.deferredmtlrResponse;
        } else {
          switch (rand.nextInt(3) + 1) {
            case 1:
              lcsEvent = emergencyCallOrigination;
              break;
            case 2:
              lcsEvent = emergencyCallRelease;
              break;
            case 3:
              lcsEvent = molr;
              break;
          }
        }

        ISDNAddressString externalAddress = new ISDNAddressStringImpl(AddressNature.international_number,
                NumberingPlan.ISDN, "444567");
        LCSClientExternalID lcsClientExternalID = new LCSClientExternalIDImpl(externalAddress, null);
        LCSClientInternalID lcsClientInternalID = LCSClientInternalID.broadcastService;
        String clientName = "219023";
        int cbsDataCodingSchemeCode = 15;
        CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(cbsDataCodingSchemeCode);
        String ussdLcsString = "911";
        Charset gsm8Charset = Charset.defaultCharset();
        USSDString ussdString = new USSDStringImpl(ussdLcsString, cbsDataCodingScheme, gsm8Charset);
        LCSFormatIndicator lcsFormatIndicator = LCSFormatIndicator.url;
        LCSClientName lcsClientName = new LCSClientNameImpl(cbsDataCodingScheme, ussdString, lcsFormatIndicator);
        AddressString lcsClientDialedByMS = new AddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, clientName);
        APN lcsAPN = new APNImpl("e911");
        LCSClientID lcsClientID = new LCSClientIDImpl(LCSClientType.valueAddedServices, lcsClientExternalID, lcsClientInternalID, lcsClientName, lcsClientDialedByMS, lcsAPN, null);

        ISDNAddressString networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number,
                NumberingPlan.ISDN, "491710460015");

        LMSI lmsi;
        switch (rand.nextInt(10) + 1) {
          case 1:
            lmsi = new LMSIImpl(new byte[]{114, 2, (byte) 233, (byte) 140});
            break;
          case 2:
            lmsi = new LMSIImpl(new byte[]{113, (byte) 255, (byte) 172, (byte) 206});
            break;
          case 3:
            lmsi = new LMSIImpl(new byte[]{114, 2, (byte) 235, 55});
            break;
          case 4:
            lmsi = new LMSIImpl(new byte[]{114, 2, (byte) 231, (byte) 213});
            break;
          default:
            lmsi = null;
            break;
        }
        boolean gprsNodeIndicator = true;
        AdditionalNumber additionalNumber = new AdditionalNumberImpl(null, sgsnNumber);
        boolean lcsCapabilitySetRelease98_99 = true;
        boolean lcsCapabilitySetRelease4 = true;
        boolean lcsCapabilitySetRelease5 = true;
        boolean lcsCapabilitySetRelease6 = true;
        boolean lcsCapabilitySetRelease7 = false;
        SupportedLCSCapabilitySets supportedLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99, lcsCapabilitySetRelease4,
                lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
        lcsCapabilitySetRelease7 = true;
        SupportedLCSCapabilitySets additionalLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99, lcsCapabilitySetRelease4,
                lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
        DiameterIdentity mmeName = new DiameterIdentityImpl("mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
        DiameterIdentity aaaServerName = new DiameterIdentityImpl("aaa3000.aaa.mnc002.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
        DiameterIdentity sgsnName = new DiameterIdentityImpl("mme.20.mag.epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
        DiameterIdentity sgsnRealm = new DiameterIdentityImpl("epc.mnc001.mcc748.3gppnetwork.org".getBytes(StandardCharsets.UTF_8));
        LCSLocationInfo lcsLocationInfo = new LCSLocationInfoImpl(networkNodeNumber, lmsi, null, gprsNodeIndicator, additionalNumber,
                supportedLCSCapabilitySets, additionalLCSCapabilitySets, mmeName, aaaServerName, sgsnName, sgsnRealm);

        Integer ageOfLocationEstimate = 0;
        ExtGeographicalInformation locationEstimate = null;
        TypeOfShape typeOfShape = null;
        double latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, uncertaintyAltitude, uncertaintyRadius,
                offsetAngle, includedAngle;
        int confidence, altitude, innerRadius;
        EllipsoidPoint ellipsoidPoint1, ellipsoidPoint2, ellipsoidPoint3, ellipsoidPoint4, ellipsoidPoint5, ellipsoidPoint6;
        // ellipsoidPoint7, ellipsoidPoint8, ellipsoidPoint9, ellipsoidPoint10, ellipsoidPoint11, ellipsoidPoint12, ellipsoidPoint13,
        // ellipsoidPoint14, ellipsoidPoint15;
        // 3 <= numberOfPoints <= 15
        switch (rand.nextInt(6) + 1) {
          case 1:
            typeOfShape = TypeOfShape.EllipsoidPoint;
            latitude = 34.909744;
            longitude = -56.146317;
            try {
              locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPoint(latitude, longitude);
            } catch (MAPException e) {
              logger.error(e.getMessage());
            }
            break;
          case 2:
            typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
            latitude = -34.910349;
            longitude = -56.149832;
            uncertainty = 5.1;
            try {
              locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPointWithUncertaintyCircle(latitude, longitude, uncertainty);
            } catch (MAPException e) {
              logger.error(e.getMessage());
            }
            break;
          case 3:
            typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyEllipse;
            latitude = -34.905624;
            longitude = -55.042191;
            uncertaintySemiMajorAxis = 21.2;
            uncertaintySemiMinorAxis = 10.4;
            angleOfMajorAxis = 30.0; // orientation of major axis
            confidence = 1;
            try {
              locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPointWithUncertaintyEllipse(latitude, longitude,
                      uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, confidence);
            } catch (MAPException e) {
              logger.error(e.getMessage());
            }
            break;
          case 4:
            typeOfShape = TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid;
            latitude = -34.956436;
            longitude = -54.937820;
            altitude = 570;
            uncertaintySemiMajorAxis = 25.4;
            uncertaintySemiMinorAxis = 12.1;
            angleOfMajorAxis = 30.2; // orientation of major axis
            uncertaintyAltitude = 80.1;
            confidence = 5;
            try {
              locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPointWithAltitudeAndUncertaintyEllipsoid(latitude,
                      longitude, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis, confidence, altitude, uncertaintyAltitude);
            } catch (MAPException e) {
              logger.error(e.getMessage());
            }
            break;
          case 5:
            typeOfShape = TypeOfShape.EllipsoidArc;
            latitude = -34.939956;
            longitude = -54.914474;
            innerRadius = 5;
            uncertaintyRadius = 1.50;
            offsetAngle = 20.0;
            includedAngle = 20.0;
            confidence = 2;
            try {
              locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidArc(latitude, longitude, innerRadius,
                      uncertaintyRadius, offsetAngle, includedAngle, confidence);
            } catch (MAPException e) {
              logger.error(e.getMessage());
            }
            break;
          case 6:
            typeOfShape = TypeOfShape.Polygon;
            latitude = 0.0;
            longitude = 0.0;
            try {
              locationEstimate = mapParameterFactory.createExtGeographicalInformation_EllipsoidPoint(latitude, longitude);
            } catch (MAPException e) {
              logger.error(e.getMessage());
            }
            break;
        }
        AddGeographicalInformation additionalLocationEstimate = null;
        int additionalLocationEstimateRandomOption = rand.nextInt(6) + 1;
        if (typeOfShape == TypeOfShape.Polygon) {
          ellipsoidPoint1 = new EllipsoidPoint(-2.907010, 70.778014);
          ellipsoidPoint2 = new EllipsoidPoint(-3.017238, 70.708922);
          ellipsoidPoint3 = new EllipsoidPoint(-2.941387, 70.432091);
          ellipsoidPoint4 = new EllipsoidPoint(-3.040019, 70.681903);
          ellipsoidPoint5 = new EllipsoidPoint(-3.045001, 70.700109);
          ellipsoidPoint6 = new EllipsoidPoint(-2.989001, 71.000004);
          EllipsoidPoint[] ellipsoidPoints = {ellipsoidPoint1, ellipsoidPoint2, ellipsoidPoint3, ellipsoidPoint4, ellipsoidPoint5, ellipsoidPoint6};

          try {
            switch (additionalLocationEstimateRandomOption) {
              case 1:
                byte[] polygonData1 = {83,
                        41, (byte) 234, (byte) 138, 55, 67, 17,
                        41, (byte) 234, (byte) 136, 55, 67, 3,
                        41, (byte) 234, 0, 55, 67, 24};
                Polygon polygon1 = new PolygonImpl(polygonData1);
                additionalLocationEstimate = new AddGeographicalInformationImpl(polygon1.getData());
                break;
              case 2:
                byte[] polygonData2 = {83,
                        44, 29, (byte) 188, 53, (byte) 227, (byte) 135,
                        44, 29, (byte) 193, 53, (byte) 227, (byte) 130,
                        44, 29, (byte) 190, 53, (byte) 227, 123};
                Polygon polygon2 = new PolygonImpl(polygonData2);
                additionalLocationEstimate = new AddGeographicalInformationImpl(polygon2.getData());
                break;
              case 3:
                byte[] polygonData3 = {83,
                        36, (byte) 167, 60, 52, 37, 0,
                        36, (byte) 167, 49, 52, 36, (byte) 255,
                        36, (byte) 167, 50, 52, 37, 0};
                Polygon polygon3 = new PolygonImpl(polygonData3);
                additionalLocationEstimate = new AddGeographicalInformationImpl(polygon3.getData());
                break;
              case 4:
                byte[] polygonData4 = {83,
                        36, 124, (byte) 163, 59, 49, 112,
                        36, 126, 7, 59, 49, (byte) 138,
                        36, 127, (byte) 224, 59, 49, 72};
                Polygon polygon4 = new PolygonImpl(polygonData4);
                additionalLocationEstimate = new AddGeographicalInformationImpl(polygon4.getData());
                break;
              case 5:
                byte[] polygonData5 = {84,
                        37, (byte) 229, (byte) 179, 52, 66, (byte) 211,
                        37, (byte) 230, 64, 52, 67, 124,
                        37, (byte) 230, (byte) 131, 52, 67, 121,
                        37, (byte) 230, (byte) 132, 52, 67, 125};
                Polygon polygon5 = new PolygonImpl(polygonData5);
                additionalLocationEstimate = new AddGeographicalInformationImpl(polygon5.getData());
                break;
              case 6:
                PolygonImpl polygon6 = new PolygonImpl();
                polygon6.setData(ellipsoidPoints);
                additionalLocationEstimate = new AddGeographicalInformationImpl(polygon6.getData());
                break;
            }
          } catch (MAPException e) {
            logger.error(e.getMessage());
          }
        }

        SLRArgExtensionContainer slrArgExtensionContainer = null;
        if (naEsrkRequest) {
          SLRArgPCSExtensions slrArgPcsExtensions = new SLRArgPCSExtensionsImpl(naEsrkRequest);
          slrArgExtensionContainer = new SLRArgExtensionContainerImpl(null, slrArgPcsExtensions);
        }

        DeferredLocationEventType deferredLocationEventType;
        TerminationCause terminationCause;
        DeferredmtlrData deferredmtlrData = null;
        // the deferredmt-lrData parameter shall be included if and only if the lcs-Event indicates a deferredmt-lrResponse.
        PeriodicLDRInfo periodicLDRInfo = null; // This parameter refers to the periodic reporting interval and reporting amount of the deferred periodic location.
        Integer sequenceNumber = null; // SequenceNumber ::= INTEGER (1..8639999)
        // sequenceNumber parameter refers to the number of the periodic location reports completed.
        // The sequence number would be set to 1 in the first location report and increment by 1 for each new report.
        // When the number reaches the reporting amount value,
        // the H-GMLC (for a periodic MT-LR or a periodic MO-LR transfer to third party) will know the procedure is complete
        if (lcsEvent == LCSEvent.deferredmtlrResponse) {
          boolean msAvailable = false;
          boolean enteringIntoArea = false;
          boolean leavingFromArea = false;
          boolean beingInsideArea = false;
          boolean periodicLDR = false;
          switch (rand.nextInt(5) + 1) {
            case 1:
              msAvailable = true;
              break;
            case 2:
              enteringIntoArea = true;
              break;
            case 3:
              leavingFromArea = true;
              break;
            case 4:
              beingInsideArea = true;
              break;
            case 5:
              periodicLDR = true;
              int reportingAmount = 3;
              int reportingInterval = 600;
              int randReporting = rand.nextInt(2) + 1;
              if (randReporting == 1) {
                int reportingAmountMilliseconds = 863999; // ReportingAmountMilliseconds ::= INTEGER (1..8639999000)
                int reportingIntervalMilliseconds = 100; // ReportingIntervalMilliseconds ::= INTEGER (1..999)
                ReportingOptionMilliseconds reportingOptionMilliseconds = new ReportingOptionMillisecondsImpl(reportingAmountMilliseconds, reportingIntervalMilliseconds);
                periodicLDRInfo = new PeriodicLDRInfoImpl(reportingAmount, reportingInterval, reportingOptionMilliseconds);
              } else {
                periodicLDRInfo = new PeriodicLDRInfoImpl(reportingAmount, reportingInterval, null);
              }
              sequenceNumber = 1;
              break;
          }
          deferredLocationEventType = new DeferredLocationEventTypeImpl(msAvailable, enteringIntoArea, leavingFromArea, beingInsideArea, periodicLDR);
          terminationCause = TerminationCause.mtlrRestart;
          deferredmtlrData = new DeferredmtlrDataImpl(deferredLocationEventType, terminationCause, lcsLocationInfo);
        }

        Integer lcsServiceTypeID = 1;
        boolean pseudonymIndicator = false;
        AccuracyFulfilmentIndicator accuracyFulfilmentIndicator = AccuracyFulfilmentIndicator.requestedAccuracyNotFulfilled;

        VelocityType velocityType = VelocityType.HorizontalWithVerticalVelocityAndUncertainty;
        int horizontalSpeed = 101;
        int bearing = 3;
        int verticalSpeed = 2;
        int uncertaintyHorizontalSpeed = 5;
        int uncertaintyVerticalSpeed = 1;
        VelocityEstimate velocityEstimate = null;
        try {
          velocityEstimate = new VelocityEstimateImpl(velocityType, horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed);
        } catch (MAPException e) {
          logger.error(e.getMessage());
        }

        boolean moLrShortCircuitIndicator = true;

        int mcc, mnc, lac, ci;
        mcc = 748;
        mnc = 1;
        lac = 101;
        ci = 10263;
        boolean saiPresent = false;
        switch (rand.nextInt(10) + 1) {
          case 1:
            saiPresent = true;
            break;
          case 2:
            lac = 119;
            ci = 15336;
            break;
          case 3:
            lac = 118;
            ci = 292;
            break;
          case 4:
            lac = 109;
            ci = 10175;
            saiPresent = true;
            break;
          case 5:
            lac = 11;
            ci = 4812;
            saiPresent = true;
            break;
          case 6:
            mnc = 7;
            lac = 8820;
            ci = 9748;
            break;
          case 7:
            mnc = 7;
            lac = 8552;
            ci = 8239;
            saiPresent = true;
            break;
          case 8:
            mnc = 10;
            lac = 9501;
            ci = 35100;
            break;
          case 9:
            mnc = 7;
            lac = 8313;
            ci = 9281;
            saiPresent = true;
            break;
          case 10:
            mnc = 7;
            lac = 8820;
            ci = 8051;
            break;
        }
        CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI;
        CellGlobalIdOrServiceAreaIdFixedLength cgiOrSai = null;
        try {
          cgiOrSai = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc, mnc, lac, ci);
        } catch (MAPException ex) {
          logger.error(ex.getMessage());
        }
        cellGlobalIdOrServiceAreaIdOrLAI = mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cgiOrSai);

        PositioningDataInformationImpl geranPositioningDataInfo = null;
        UtranPositioningDataInfoImpl utranPositioningDataInfo = null;
        GeranGANSSpositioningDataImpl geranGanssPositioningData = null;
        UtranGANSSpositioningDataImpl utranGanssPositioningData = null;
        UtranAdditionalPositioningData utranAdditionalPositioningData = null;
        // Method=Mobile Based E-OTD, Usage=1: Attempted successfully: results not used to generate location
        // Method=Mobile Assisted E-OTD, Usage=3: Attempted successfully: results used to generate location
        // Method=U-TDOA, Usage=3: Attempted successfully: results used to generate location
        // Method=Cell ID, Usage=0: Attempted unsuccessfully due to failure or interruption
        // Method=Mobile Assisted GPS, Usage=3: Attempted successfully: results used to generate location
        // Method=Timing Advance, Usage=3: Attempted successfully: results used to generate location
        // Method=Conventional GPS, Usage=2: Attempted successfully: results used to verify but not generate location
        // byte[] geranPosData = new byte[] {0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60};

        // Method=OTDOA, Usage=3: Attempted successfully: results used to generate location
        // Method=Reserved (GERAN use only), Usage=0: Attempted unsuccessfully due to failure or interruption - not used
        // Method=U-TDOA, Usage=3: Attempted successfully: results used to generate location
        // Method=Cell ID, Usage=2: Attempted successfully: results used to verify but not generate location - not used
        // Method=Mobile Assisted GPS, Usage=3: Attempted successfully: results used to generate location
        // byte[] utranPosData = new byte[] {0x00, 0x00, 0x43, 0x4b, 0x00, 0x62, 0x2b};

        // Method=MS-Based, GANSSId=Galileo
        // Method=MS-Assisted, GANSSId=GLONASS
        // Method=Conventional, GANSSId=SBAS
        // byte[] geranGANSSData = new byte[] {0x00, 0x63, (byte) 0x8b, 0x02, 0x03};

        // Method=MS-Based, GANSSId=Galileo
        // Method=MS-Assisted, GANSSId=GLONASS
        // Method=Conventional, GANSSId=SBAS
        // byte[] utranGanssData = new byte[] {0x01, 0x63, (byte) 0x8b, 0x02, 0x03};

        // Method=Standalone, AddPosId=WLAN
        // Method=MS-Assisted, AddPosId=Bluetooth
        // byte[] utranAddPosData = new byte[] {0x57, (byte) 0x8F};

        switch (rand.nextInt(4) + 1) {
          case 1:
            geranPositioningDataInfo = new PositioningDataInformationImpl(new byte[]{0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60});
            break;
          case 2:
            geranPositioningDataInfo = new PositioningDataInformationImpl(new byte[]{0x00, 0x03, 0x1b, 0x21, 0x2b, 0x3a, 0x43, 0x60});
            geranGanssPositioningData = new GeranGANSSpositioningDataImpl(new byte[]{0x00, 0x63, (byte) 0x8b, 0x02, 0x03});
            break;
          case 3:
            utranPositioningDataInfo = new UtranPositioningDataInfoImpl(new byte[]{0x00, 0x00, 0x43, 0x4b, 0x00, 0x62, 0x2b});
            break;
          case 4:
            utranPositioningDataInfo = new UtranPositioningDataInfoImpl(new byte[]{0x00, 0x00, 0x43, 0x4b, 0x00, 0x62, 0x2b});
            utranGanssPositioningData = new UtranGANSSpositioningDataImpl(new byte[]{0x01, 0x63, (byte) 0x8b, 0x02, 0x03});
            utranAdditionalPositioningData = new UtranAdditionalPositioningDataImpl(new byte[]{0x57, (byte) 0x8F});
            break;
        }

        boolean isMsc = true;
        ServingNodeAddress servingNodeAddress = new ServingNodeAddressImpl(networkNodeNumber, isMsc);

        Integer lcsReferenceNumber = null;
        if (isDeferred) // If a lcs event indicates deferred mt-lr response, the lcs-Reference number shall be included.
          lcsReferenceNumber = pslReferenceNumber;

        GSNAddress hGmlcAddress = new GSNAddressImpl(GSNAddressAddressType.IPv4, new byte[]{0x0a, 0x00, 0x00, 0x0e});

        Integer utranBaroPressureMeas = null;
        UtranCivicAddress utranCivicAddress = null;
        if (geranPositioningDataInfo == null && geranGanssPositioningData == null) {
          utranBaroPressureMeas = 110000; // UtranBaroPressureMeas ::= INTEGER (30000..115000)
          String civicAddressString = "<cl:civicAddress>\n" +
                  "                        <cl:country>US</cl:country>\n" +
                  "                        <cl:A1>New York</cl:A1>\n" +
                  "                        <cl:A3>New York</cl:A3>\n" +
                  "                        <cl:A6>Broadway</cl:A6>\n" +
                  "                        <cl:HNO>123</cl:HNO>\n" +
                  "                        <cl:LOC>Suite 75</cl:LOC>\n" +
                  "                        <cl:PC>10027-0401</cl:PC>\n" +
                  "                    </cl:civicAddress>";
          byte[] civicAddressByteArray = civicAddressString.getBytes(StandardCharsets.UTF_8);
          utranCivicAddress = new UtranCivicAddressImpl(civicAddressByteArray);
        }

        mapDialogSLR.addSubscriberLocationReportRequest(lcsEvent, lcsClientID, lcsLocationInfo, msisdn, imsi, imei, naEsrd, naEsrk,
                locationEstimate, ageOfLocationEstimate, slrArgExtensionContainer, additionalLocationEstimate, deferredmtlrData,
                lcsReferenceNumber, geranPositioningDataInfo, utranPositioningDataInfo, cellGlobalIdOrServiceAreaIdOrLAI,
                hGmlcAddress, lcsServiceTypeID, saiPresent, pseudonymIndicator, accuracyFulfilmentIndicator, velocityEstimate,
                sequenceNumber, periodicLDRInfo, moLrShortCircuitIndicator, geranGanssPositioningData, utranGanssPositioningData,
                servingNodeAddress, utranAdditionalPositioningData, utranBaroPressureMeas, utranCivicAddress);

        // This will initiate the TC-BEGIN with INVOKE component
        mapDialogSLR.send();

      } catch (MAPException e) {
        logger.error(String.format("Error while sending MAP SLR:" + e));
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void onSubscriberLocationReportRequest(SubscriberLocationReportRequest subscriberLocationReportRequest) {
    /*
     * This is an error condition. Server should never receive onProvideSubscriberLocationResponse.
     */
    if (logger.isDebugEnabled()) {
      logger.debug(
          String.format("onSubscriberLocationReportRequest for DialogId=%d", subscriberLocationReportRequest.getMAPDialog().getLocalDialogId()));
    }

  }

  @Override
  public void onSubscriberLocationReportResponse(SubscriberLocationReportResponse subscriberLocationReportResponse) {
    logger.error(String.format("onSubscriberLocationReportResponse for Dialog=%d and invokeId=%d",
        subscriberLocationReportResponse.getMAPDialog().getLocalDialogId(), subscriberLocationReportResponse.getInvokeId()));
  }

  public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberData) {

  }

  public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest deleteSubsData) {

  }

  public void onErrorComponent(MAPDialog arg0, Long arg1, MAPErrorMessage arg2) {
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

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }
}
