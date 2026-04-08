package org.mobicents.gmlc.slee;

import com.objsys.asn1j.runtime.Asn1BitString;
import com.objsys.asn1j.runtime.Asn1Boolean;
import com.objsys.asn1j.runtime.Asn1Integer;
import com.paicbd.slee.resource.ulp.ULPMessageFactory;
import com.paicbd.slee.resource.ulp.ULPSessionActivity;
import com.paicbd.slee.resource.ulp.UlpActivityContextInterfaceFactory;
import com.paicbd.slee.resource.ulp.UlpResourceAdaptorSbbInterface;
import net.java.slee.resource.diameter.base.DiameterActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.DiameterMessageFactory;
import net.java.slee.resource.diameter.base.DiameterProvider;
import net.java.slee.resource.diameter.base.events.ErrorAnswer;
import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.AddressType;
import net.java.slee.resource.diameter.base.events.avp.AuthSessionStateType;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.DiameterIdentity;
import net.java.slee.resource.diameter.sh.DiameterShAvpFactory;
import net.java.slee.resource.diameter.sh.client.ShClientActivity;
import net.java.slee.resource.diameter.sh.client.ShClientActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.client.ShClientMessageFactory;
import net.java.slee.resource.diameter.sh.client.ShClientProvider;
import net.java.slee.resource.diameter.sh.events.UserDataAnswer;
import net.java.slee.resource.diameter.sh.events.UserDataRequest;
import net.java.slee.resource.diameter.sh.events.avp.CurrentLocationType;
import net.java.slee.resource.diameter.sh.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.events.avp.DiameterShAvpCodes;
import net.java.slee.resource.diameter.sh.events.avp.RequestedDomainType;
import net.java.slee.resource.diameter.sh.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.ShServerActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.sh.server.ShServerMessageFactory;
import net.java.slee.resource.diameter.sh.server.ShServerProvider;
import net.java.slee.resource.diameter.slg.SLgAVPFactory;
import net.java.slee.resource.diameter.slg.SLgActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.slg.SLgClientSessionActivity;
import net.java.slee.resource.diameter.slg.SLgMessageFactory;
import net.java.slee.resource.diameter.slg.SLgProvider;
import net.java.slee.resource.diameter.slg.events.LocationReportAnswer;
import net.java.slee.resource.diameter.slg.events.LocationReportRequest;
import net.java.slee.resource.diameter.slg.events.ProvideLocationAnswer;
import net.java.slee.resource.diameter.slg.events.ProvideLocationRequest;
import net.java.slee.resource.diameter.slg.events.avp.AdditionalAreaAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaDefinitionAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaEventInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.ELPAVPCodes;
import net.java.slee.resource.diameter.slg.events.avp.LCSClientType;
import net.java.slee.resource.diameter.slg.events.avp.LCSEPSClientNameAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSFormatIndicator;
import net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheckNonSessionAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheckSessionAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import net.java.slee.resource.diameter.slg.events.avp.LCSRequestorNameAvp;
import net.java.slee.resource.diameter.slg.events.avp.MotionEventInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.PLMNIDListAvp;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLDRInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLocationSupportIndicator;
import net.java.slee.resource.diameter.slg.events.avp.PrioritizedListIndicator;
import net.java.slee.resource.diameter.slg.events.avp.ReportingPLMNListAvp;
import net.java.slee.resource.diameter.slg.events.avp.SLgLocationType;
import net.java.slee.resource.diameter.slg.events.avp.VelocityRequested;
import net.java.slee.resource.diameter.slg.events.avp.VerticalRequested;
import net.java.slee.resource.diameter.slh.SLhAVPFactory;
import net.java.slee.resource.diameter.slh.SLhActivityContextInterfaceFactory;
import net.java.slee.resource.diameter.slh.SLhClientSessionActivity;
import net.java.slee.resource.diameter.slh.SLhMessageFactory;
import net.java.slee.resource.diameter.slh.SLhProvider;
import net.java.slee.resource.diameter.slh.events.LCSRoutingInfoAnswer;
import net.java.slee.resource.diameter.slh.events.LCSRoutingInfoRequest;
import net.java.slee.resource.http.events.HttpServletRequestEvent;
import org.joda.time.DateTime;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.mobicents.gmlc.slee.cdr.CDRCreationHelper;
import org.mobicents.gmlc.slee.cdr.CDRInterface;
import org.mobicents.gmlc.slee.cdr.CDRInterfaceParent;
import org.mobicents.gmlc.slee.cdr.GMLCCDRState;
import org.mobicents.gmlc.slee.cdr.RecordStatus;
import org.mobicents.gmlc.slee.concurrent.MlpResponseExecutor;
import org.mobicents.gmlc.slee.concurrent.SuplSessionStatus;
import org.mobicents.gmlc.slee.concurrent.SuplTransaction;
import org.mobicents.gmlc.slee.concurrent.Transaction;
import org.mobicents.gmlc.slee.diameter.AVPHandler;
import org.mobicents.gmlc.slee.diameter.DiameterBaseError;
import org.mobicents.gmlc.slee.diameter.DiameterLcsResponseHelperForMLP;
import org.mobicents.gmlc.slee.diameter.DiameterShUdrResponseHelperForMLP;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformationExtension;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformationExtension2;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformationExtension3;
import org.mobicents.gmlc.slee.diameter.sh.EPSLocationInformation;
import org.mobicents.gmlc.slee.diameter.sh.EPSLocationInformationExtension;
import org.mobicents.gmlc.slee.diameter.sh.EPSLocationInformationExtension2;
import org.mobicents.gmlc.slee.diameter.sh.Extension;
import org.mobicents.gmlc.slee.diameter.sh.LocalTimeZone;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformationExtension;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformationExtension2;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformationExtension3;
import org.mobicents.gmlc.slee.diameter.sh.Sh5GSLocationInformation;
import org.mobicents.gmlc.slee.diameter.sh.ShDataReader;
import org.mobicents.gmlc.slee.diameter.sh.ShSpecificErrors;
import org.mobicents.gmlc.slee.diameter.sh.ShUdaAvpValues;
import org.mobicents.gmlc.slee.diameter.sh.UserCSGInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShCellGlobalId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShEUTRANCellGlobalId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShGeodeticInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShGeographicalInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShLocationAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShLocationNumber;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShNRCellGlobalId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShRoutingAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShServiceAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShTrackingAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShUserCSGInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShVisitedPLMNId;
import org.mobicents.gmlc.slee.diameter.slg.RequestValuesForPLR;
import org.mobicents.gmlc.slee.diameter.slg.SLgLrrAvpValues;
import org.mobicents.gmlc.slee.diameter.slg.SLgPlaAvpValues;
import org.mobicents.gmlc.slee.diameter.slg.SLgSpecificErrors;
import org.mobicents.gmlc.slee.diameter.slh.SLhException;
import org.mobicents.gmlc.slee.diameter.slh.SLhRiaAvpValues;
import org.mobicents.gmlc.slee.diameter.slh.SLhSpecificErrors;
import org.mobicents.gmlc.slee.http.AtiResponseJsonBuilder;
import org.mobicents.gmlc.slee.http.HttpReport;
import org.mobicents.gmlc.slee.http.MongoGmlc;
import org.mobicents.gmlc.slee.http.OnErrorResponseJsonBuilder;
import org.mobicents.gmlc.slee.http.PlrResponseJsonBuilder;
import org.mobicents.gmlc.slee.http.PsiResponseJsonBuilder;
import org.mobicents.gmlc.slee.http.PslResponseJsonBuilder;
import org.mobicents.gmlc.slee.http.SriResponseJsonBuilder;
import org.mobicents.gmlc.slee.http.UdrResponseJsonBuilder;
import org.mobicents.gmlc.slee.map.AtiResponseParams;
import org.mobicents.gmlc.slee.map.MapAtiResponseHelperForMLP;
import org.mobicents.gmlc.slee.map.MapLsmResponseHelperForMLP;
import org.mobicents.gmlc.slee.map.MapSriPsiResponseHelperForMLP;
import org.mobicents.gmlc.slee.map.PsiResponseParams;
import org.mobicents.gmlc.slee.map.PslResponseParams;
import org.mobicents.gmlc.slee.map.SlrRequestParams;
import org.mobicents.gmlc.slee.map.SriLcsResponseParams;
import org.mobicents.gmlc.slee.map.SriResponseValues;
import org.mobicents.gmlc.slee.map.SriSmResponseParams;
import org.mobicents.gmlc.slee.mlp.MLPException;
import org.mobicents.gmlc.slee.mlp.MLPLocationRequest;
import org.mobicents.gmlc.slee.mlp.MLPRequest;
import org.mobicents.gmlc.slee.mlp.MLPResponse;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningDataImpl;
import org.mobicents.gmlc.slee.primitives.LocationInformation5GS;
import org.mobicents.gmlc.slee.supl.NetworkInitiatedSuplLocation;
import org.mobicents.gmlc.slee.supl.SUPL_INIT.SLPMode;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.AreaEventParams;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.AreaEventParams_areaIdLists;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.AreaEventType;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.AreaId;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.AreaIdList;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.AreaIdSet;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.GeographicTargetArea;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.GeographicTargetAreaList;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.LTEAreaId;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.PeriodicParams;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.RepeatedReportingParams;
import org.mobicents.gmlc.slee.supl.SUPL_TRIGGERED_START.TriggerParams;
import org.mobicents.gmlc.slee.supl.SuplGeoTargetArea;
import org.mobicents.gmlc.slee.supl.SuplResponseHelperForMLP;
import org.mobicents.gmlc.slee.supl.SuplTriggerType;
import org.mobicents.gmlc.slee.supl.ULP_Components.PosMethod;
import org.mobicents.gmlc.slee.supl.ULP_Components.QoP;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.CircularArea;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.Coordinate;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.Coordinate_latitudeSign;
import org.mobicents.gmlc.slee.supl.Ver2_ULP_Components.EllipticalArea;
import org.mobicents.gmlc.slee.utils.GADShapesUtils;
import org.mobicents.slee.ChildRelationExt;
import org.mobicents.slee.SbbContextExt;
import org.mobicents.slee.resource.diameter.sh.events.avp.UserIdentityAvpImpl;
import org.restcomm.protocols.ss7.indicator.NatureOfAddress;
import org.restcomm.protocols.ss7.indicator.NumberingPlan;
import org.restcomm.protocols.ss7.indicator.RoutingIndicator;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContext;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextName;
import org.restcomm.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.restcomm.protocols.ss7.map.api.MAPDialog;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.MAPParameterFactory;
import org.restcomm.protocols.ss7.map.api.MAPProvider;
import org.restcomm.protocols.ss7.map.api.datacoding.CBSDataCodingScheme;
import org.restcomm.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.primitives.AddressNature;
import org.restcomm.protocols.ss7.map.api.primitives.AddressString;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.primitives.SubscriberIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.USSDString;
import org.restcomm.protocols.ss7.map.api.service.callhandling.InterrogationType;
import org.restcomm.protocols.ss7.map.api.service.callhandling.MAPDialogCallHandling;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationRequest;
import org.restcomm.protocols.ss7.map.api.service.callhandling.SendRoutingInformationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.Area;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaDefinition;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaEventInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaIdentification;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaType;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredLocationEventType;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientExternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientName;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSCodeword;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPriority;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPrivacyCheck;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoS;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSRequestorID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationEstimateType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationType;
import org.restcomm.protocols.ss7.map.api.service.lsm.MAPDialogLsm;
import org.restcomm.protocols.ss7.map.api.service.lsm.OccurrenceInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.ProvideSubscriberLocationResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.RANTechnology;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMN;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMNList;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTime;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SendRoutingInfoForLCSResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportRequest;
import org.restcomm.protocols.ss7.map.api.service.lsm.SubscriberLocationReportResponse;
import org.restcomm.protocols.ss7.map.api.service.lsm.SupportedGADShapes;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;
import org.restcomm.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.DomainType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.RequestedInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.APN;
import org.restcomm.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMRequest;
import org.restcomm.protocols.ss7.map.api.service.sms.SendRoutingInfoForSMResponse;
import org.restcomm.protocols.ss7.map.datacoding.CBSDataCodingSchemeImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdOrLAIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMEIImpl;
import org.restcomm.protocols.ss7.map.primitives.IMSIImpl;
import org.restcomm.protocols.ss7.map.primitives.ISDNAddressStringImpl;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;
import org.restcomm.protocols.ss7.map.primitives.SubscriberIdentityImpl;
import org.restcomm.protocols.ss7.map.primitives.USSDStringImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaDefinitionImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaEventInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaIdentificationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaImpl;
import org.restcomm.protocols.ss7.map.service.lsm.DeferredLocationEventTypeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ExtGeographicalInformationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientExternalIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSClientNameImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSCodewordImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSQoSImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSRequestorIDImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LocationTypeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.PeriodicLDRInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ReportingPLMNImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ReportingPLMNListImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ResponseTimeImpl;
import org.restcomm.protocols.ss7.map.service.lsm.SupportedGADShapesImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.RequestedInfoImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.ParameterFactoryImpl;
import org.restcomm.protocols.ss7.sccp.impl.parameter.SccpAddressImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.ParameterFactory;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;
import org.restcomm.protocols.ss7.tcap.api.MessageType;
import org.restcomm.protocols.ss7.tcap.asn.ApplicationContextName;
import org.restcomm.protocols.ss7.tcap.asn.comp.InvokeProblemType;
import org.restcomm.slee.resource.map.MAPContextInterfaceFactory;
import org.restcomm.slee.resource.map.events.DialogAccept;
import org.restcomm.slee.resource.map.events.DialogClose;
import org.restcomm.slee.resource.map.events.DialogDelimiter;
import org.restcomm.slee.resource.map.events.DialogNotice;
import org.restcomm.slee.resource.map.events.DialogProviderAbort;
import org.restcomm.slee.resource.map.events.DialogReject;
import org.restcomm.slee.resource.map.events.DialogRelease;
import org.restcomm.slee.resource.map.events.DialogTimeout;
import org.restcomm.slee.resource.map.events.DialogUserAbort;
import org.restcomm.slee.resource.map.events.ErrorComponent;
import org.restcomm.slee.resource.map.events.InvokeTimeout;
import org.restcomm.slee.resource.map.events.RejectComponent;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.EventContext;
import javax.slee.SLEEException;
import javax.slee.Sbb;
import javax.slee.SbbContext;
import javax.slee.SbbLocalObject;
import javax.slee.TransactionRequiredLocalException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.TimerPreserveMissed;
import javax.slee.facilities.Tracer;
import javax.slee.resource.ResourceAdaptorTypeID;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.mobicents.gmlc.slee.diameter.slg.SLgAreaEventInfoHelper.convertAreaTypeToString;
import static org.mobicents.gmlc.slee.diameter.slg.SLgAreaEventInfoHelper.setAreaIdToTbcd;
import static org.mobicents.gmlc.slee.http.HttpResponseType.setHttpServletResponseStatusCode;
import static org.mobicents.gmlc.slee.utils.TBCDUtil.parseTBCD;
import static org.mobicents.gmlc.slee.utils.TBCDUtil.setAreaIdParams;
import static org.mobicents.gmlc.slee.utils.TBCDUtil.toTBCDString;
import static org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID.getLCSClientInternalID;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public abstract class MobileCoreNetworkInterfaceSbb extends GMLCBaseSbb implements Sbb, CDRInterfaceParent {

    protected SbbContextExt sbbContext;

    protected Tracer logger;

    private static final GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();

    private static final SuplTransaction suplTransaction = SuplTransaction.Instance();

    protected MAPContextInterfaceFactory mapAcif;
    protected MAPProvider mapProvider;
    protected MAPParameterFactory mapParameterFactory;
    protected ParameterFactory sccpParameterFact;

    private static final int MAP_OPERATION_TIMEOUT = 60000; // milliseconds
    protected static final ResourceAdaptorTypeID mapRATypeID = new ResourceAdaptorTypeID("MAPResourceAdaptorType", "org.restcomm", "2.0");
    protected static final String mapRaLink = "MAPRA";

    private SccpAddress gmlcSCCPAddress = null;
    private MAPApplicationContext anyTimeEnquiryContext = null;
    private MAPApplicationContext locationSvcEnquiryContext = null;
    private MAPApplicationContext locationSvcGatewayContext = null;
    private MAPApplicationContext shortMsgGatewayContext = null;
    private MAPApplicationContext subscriberInfoEnquiryContext = null;
    private MAPApplicationContext locationInfoRetrievalContext = null;

    private static final int DIAMETER_COMMAND_TIMEOUT = 60000; // milliseconds
    public static final int TGPP_VENDOR_ID = 10415;

    protected SLhProvider slhProvider;
    protected SLhMessageFactory slhMessageFactory;
    protected SLhAVPFactory slhAVPFactory;
    protected SLhActivityContextInterfaceFactory slhAcif;

    protected SLgProvider slgProvider;
    protected SLgMessageFactory slgMessageFactory;
    protected SLgAVPFactory slgAVPFactory;
    protected SLgActivityContextInterfaceFactory slgAcif;

    protected static final ResourceAdaptorTypeID diameterSLhRATypeID = new ResourceAdaptorTypeID("Diameter SLh", "java.net", "0.8.1");
    protected static final String slhRaLink = "DiameterSLh";

    protected static final ResourceAdaptorTypeID diameterSLgRATypeID = new ResourceAdaptorTypeID("Diameter SLg", "java.net", "0.8.1");
    protected static final String slgRaLink = "DiameterSLg";

    protected ShClientProvider shClientProvider;
    protected ShServerProvider shServerProvider;
    protected ShClientMessageFactory shClientMessageFactory;
    protected ShServerMessageFactory shServerMessageFactory;
    protected DiameterShAvpFactory shAvpFactory;
    protected ShClientActivityContextInterfaceFactory shClientAcif;
    protected ShServerActivityContextInterfaceFactory shServerAcif;

    protected static final ResourceAdaptorTypeID diameterShClientRATypeID = new ResourceAdaptorTypeID("Diameter Sh-Client", "java.net", "0.8.1");
    protected static final String shClientRaLink = "DiameterShClient";

    protected static final ResourceAdaptorTypeID diameterShServerRATypeID = new ResourceAdaptorTypeID("Diameter Sh-Server", "java.net", "0.8.1");
    protected static final String shServerRaLink = "DiameterShServer";

    protected DiameterProvider diameterBaseProvider;
    protected DiameterMessageFactory diameterBaseMessageFactory;
    protected DiameterAvpFactory diameterBaseAvpFactory;
    protected DiameterActivityContextInterfaceFactory diameterBaseAcif;

    protected static final ResourceAdaptorTypeID diameterBaseRATypeID = new ResourceAdaptorTypeID("Diameter Base", "java.net", "0.8.1");
    protected static final String diameterBaseRaLink = "DiameterBaseResourceAdaptor";

    protected UlpResourceAdaptorSbbInterface ulpProvider;
    protected ULPMessageFactory ulpMessageFactory;
    protected ULPSessionActivity ulpSessionActivity;
    protected UlpActivityContextInterfaceFactory ulpActContextIFactory;

    protected static final ResourceAdaptorTypeID suplRATypeID = new ResourceAdaptorTypeID("ULPResourceAdaptorType", "com.paicbd", "1.0");
    protected static final String suplRaLink = "ULPRA";

    private TimerFacility timerFacility = null;
    private static final TimerOptions defaultTimerOptions = createDefaultTimerOptions();
    private NetworkInitiatedSuplLocation networkInitiatedSuplLocation;

    //time in millisecond
    private static final int SUPL_SESSION_TIMEOUT = 15000;

    //time in millisecond
    private static final int SUPL_SESSION_SCHEDULE = 500;

    private static TimerOptions createDefaultTimerOptions() {
        TimerOptions timerOptions = new TimerOptions();
        timerOptions.setTimeout(gmlcPropertiesManagement.getEventContextSuspendDeliveryTimeout());
        timerOptions.setPreserveMissed(TimerPreserveMissed.ALL);
        return timerOptions;
    }

    private Transaction mobileCoreNetworkTransactions = Transaction.Instance();

    private HttpReport httpSubscriberLocationReport;
    private MongoGmlc mongoGmlc;
    public static final Map<Integer, LocationRequestParams> listLocationRequestParams = new HashMap<>();
    // private SuplEventProcessing suplEventProcessing;

    /**
     * Creates a new instance of MobileCoreNetworkInterfaceSbb
     */
    public MobileCoreNetworkInterfaceSbb() throws UnknownHostException {
        super("MobileCoreNetworkInterfaceSbb");
    }


    /**
     * For debugging - fake location data
     */
    private final String fakeNumber = "19395550113";
    private final MLPResponse.MLPResultType fakeLocationType = MLPResponse.MLPResultType.OK;
    private final String fakeLocationAdditionalInfoErrorString = "Internal positioning failure occurred";
    private final int fakeMcc = 999;
    private final int fakeMnc = 789;
    private final int fakeCellId = 300;
    private final double fakeLocationUncertainty = 500;

    ////////////////////
    // Sbb callbacks //
    //////////////////

    public void setSbbContext(SbbContext sbbContext) {

        this.sbbContext = (SbbContextExt) sbbContext;
        this.logger = sbbContext.getTracer(MobileCoreNetworkInterfaceSbb.class.getSimpleName());

        try {
            /*
             * SS7 MAP
             */
            this.mapAcif = (MAPContextInterfaceFactory) this.sbbContext.getActivityContextInterfaceFactory(mapRATypeID);
            this.mapProvider = (MAPProvider) this.sbbContext.getResourceAdaptorInterface(mapRATypeID, mapRaLink);
            this.mapParameterFactory = this.mapProvider.getMAPParameterFactory();
            this.sccpParameterFact = new ParameterFactoryImpl();

            /*
             * Diameter
             */
            Context diameterCtx = (Context) new InitialContext().lookup("java:comp/env");

            // Diameter SLh
            try {
                this.slhProvider = (SLhProvider) this.sbbContext.getResourceAdaptorInterface(diameterSLhRATypeID, slhRaLink);
                this.slhMessageFactory = slhProvider.getSLhMessageFactory();
                this.slhAVPFactory = slhProvider.getSLhAvpFactory();
                this.slhAcif = (SLhActivityContextInterfaceFactory) diameterCtx.lookup("slee/resources/JDiameterSLhResourceAdaptor/java.net/0.8.1/acif");
            } catch (Exception e) {
                logger.severe("Unable to set SBB context parameters for SLh", e);
            }

            // Diameter SLg
            try {
                this.slgProvider = (SLgProvider) this.sbbContext.getResourceAdaptorInterface(diameterSLgRATypeID, slgRaLink);
                this.slgMessageFactory = slgProvider.getSLgMessageFactory();
                this.slgAVPFactory = slgProvider.getSLgAVPFactory();
                this.slgAcif = (SLgActivityContextInterfaceFactory) diameterCtx.lookup("slee/resources/JDiameterSLgResourceAdaptor/java.net/0.8.1/acif");
            } catch (Exception e) {
                logger.severe("Unable to set SBB context parameters for SLg", e);
            }

            // Diameter Sh Client
            try {
                this.shClientProvider = (ShClientProvider) this.sbbContext.getResourceAdaptorInterface(diameterShClientRATypeID, shClientRaLink);
                this.shClientMessageFactory = shClientProvider.getClientMessageFactory();
                this.shAvpFactory = shClientProvider.getClientAvpFactory();
                this.shClientAcif = (ShClientActivityContextInterfaceFactory) diameterCtx.lookup("slee/resources/JDiameterShClientResourceAdaptor/java.net/0.8.1/acif");
            } catch (Exception e) {
                logger.severe("Unable to set SBB context parameters for Sh", e);
            }

            // Diameter Sh Server
            try {
                this.shServerProvider = (ShServerProvider) this.sbbContext.getResourceAdaptorInterface(diameterShServerRATypeID, shServerRaLink);
                this.shServerMessageFactory = shServerProvider.getServerMessageFactory();
                this.shAvpFactory = shServerProvider.getServerAvpFactory();
                this.shServerAcif = (ShServerActivityContextInterfaceFactory) diameterCtx.lookup("slee/resources/JDiameterShServerResourceAdaptor/java.net/0.8.1/acif");
            } catch (Exception e) {
                logger.severe("Unable to set SBB context parameters for Sh", e);
            }

            // Diameter Base
            try {
                this.diameterBaseProvider = (DiameterProvider) this.sbbContext.getResourceAdaptorInterface(diameterBaseRATypeID, diameterBaseRaLink);
                this.diameterBaseMessageFactory = diameterBaseProvider.getDiameterMessageFactory();
                this.diameterBaseAvpFactory = diameterBaseProvider.getDiameterAvpFactory();
                this.diameterBaseAcif = (DiameterActivityContextInterfaceFactory) diameterCtx.lookup("slee/resources/DiameterBaseResourceAdaptor/java.net/0.8.1/acif");
            } catch (Exception e) {
                logger.severe("Unable to set SBB context parameters for Diameter Base", e);
            }

            /*
             * OMA ULP for SUPL
             */
            /* try {
                this.ulpActContextIFactory = (UlpActivityContextInterfaceFactory) this.sbbContext.getActivityContextInterfaceFactory(suplRATypeID);
                this.ulpProvider = (UlpResourceAdaptorSbbInterface) this.sbbContext.getResourceAdaptorInterface(suplRATypeID, suplRaLink);
                this.ulpMessageFactory = this.ulpProvider.getULPMessageFactory();
                this.ulpSessionActivity = this.ulpProvider.createActivity();
            } catch (Exception e) {
                logger.severe("Unable to set SBB context parameters for OMA ULP", e);
            }
            logger.info("ULP-Provider: "+this.ulpProvider);
            */

            this.timerFacility = this.sbbContext.getTimerFacility();
            // Initialize SUPL Server
            if (gmlcPropertiesManagement.getSuplEnabled()) {
                networkInitiatedSuplLocation = new NetworkInitiatedSuplLocation();
            }

        } catch (Exception e) {
            logger.severe("Could not set SBB context:", e);
        }

        // this.suplEventProcessing = new SuplEventProcessing(this.logger, this.ulpSessionActivity);
    }

    protected SbbContext getSbbContext() {
        return sbbContext;
    }

    private void forwardEvent(SbbLocalObject child, ActivityContextInterface aci) {
        try {
            aci.attach(child);
            aci.detach(sbbContext.getSbbLocalObject());
        } catch (Exception e) {
            logger.severe("Unexpected error: ", e);
        }
    }

    // ///////////////////////
    // MAP Events handlers //
    // /////////////////////

    /**
     * Subscriber Information services
     * MAP_ANY_TIME_INTERROGATION (ATI) Events
     */

    /**
     * MAP ATI Request Event
     */
    public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest anyTimeInterrogationRequest, ActivityContextInterface aci) {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onAnyTimeInterrogationRequest = " + anyTimeInterrogationRequest + ", ActivityContextInterface=" + aci);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onAnyTimeInterrogationRequest=%s", anyTimeInterrogationRequest), e);
        }
    }

    /**
     * MAP ATI Response Event
     */
    public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse anyTimeInterrogationResponse, ActivityContextInterface aci) {

        this.logger.fine("\nReceived onAnyTimeInterrogationResponse = " + anyTimeInterrogationResponse);
        String msisdnDigitsForAti = null, imsiForAti = null, atiImei = null, nnn;
        String atiHttpRespType, curlUser;
        Long transaction;
        DateTime eventTime = DateTime.now();

        try {

            AtiResponseParams atiResponseParams = new AtiResponseParams();
            MLPResponse.MLPResultType mlpRespResult = null;
            String mlpClientErrorMessage;

            if (anyTimeInterrogationResponse != null) {
                // CDR initialization
                CDRCreationHelper.GmlcCdrStateString gmlcCdrStateString = CDRCreationHelper.mapAtiCdrInitializer(aci, this.getCDRInterface(), anyTimeInterrogationResponse, null);
                GMLCCDRState gmlcCdrState = gmlcCdrStateString.getGmlcCdrState();
                nnn = gmlcCdrStateString.getNnn();
                // Set timer last
                this.setTimer(aci);

                // Transaction
                transaction = mobileCoreNetworkTransactions.getTransactionId(anyTimeInterrogationResponse.getMAPDialog().getLocalDialogId());

                if (transaction != null) {
                    TimerID atiTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                    if (atiTimerID != null)
                        this.timerFacility.cancelTimer(atiTimerID);
                    msisdnDigitsForAti = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    imsiForAti = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    atiHttpRespType = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiHttpRespType");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                    gmlcCdrState.setDialogStartTime(dialogStartTime);
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCurlUser(curlUser);
                        if (dialogStartTime != null) {
                            Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                            gmlcCdrState.setDialogDuration(dialogDuration);
                        }
                    }
                } else {
                    throw new Exception();
                }
                mobileCoreNetworkTransactions.destroy(transaction);

                if (msisdnDigitsForAti != null) {
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, msisdnDigitsForAti));
                    }
                } else if (imsiForAti != null) {
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setImsi(new IMSIImpl(imsiForAti));
                    }
                }

                MessageType tcapMessageType = anyTimeInterrogationResponse.getMAPDialog().getTCAPMessageType();
                if (tcapMessageType == MessageType.Abort) {
                    mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                    mlpClientErrorMessage = "TCAP ABORT on ATI";
                    if (gmlcCdrState.isInitialized()) {
                        this.createCDRRecord(RecordStatus.ATI_TCAP_DIALOG_ABORT);
                    }
                    if (anyTimeInterrogationResponse.getMAPDialog().getRemoteAddress() != null)
                        if (anyTimeInterrogationResponse.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                            nnn = anyTimeInterrogationResponse.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "ATI", msisdnDigitsForAti,
                        imsiForAti, null, null, nnn, null, null, null, false);
                    return;
                }

                // Inquire if MAP ATI response includes subscriber's info
                if (anyTimeInterrogationResponse.getSubscriberInfo() != null) {
                    this.logger.fine("\nonAnyTimeInterrogationResponse, subscriberInfo=" + anyTimeInterrogationResponse.getSubscriberInfo());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setSubscriberInfo(anyTimeInterrogationResponse.getSubscriberInfo());
                    }
                    // Inquire if Location information is included in MAP ATI response subscriber's info
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation() != null) {
                        // Location information is included in MAP ATI response
                        atiResponseParams.setLocationInformation(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation());
                        // Inquire if Current Location Retrieved information is included in MAP ATI response subscriber's info
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getCurrentLocationRetrieved()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(true);
                            }
                        } else {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(false);
                            }
                        }
                        // Inquire if SAI is present in MAP ATI response subscriber's info
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getSaiPresent()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSaiPresent(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getSaiPresent());
                            }
                        } else {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSaiPresent(false);
                            }
                        }
                        // Inquire if location information includes EPS location info (LTE)
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS() != null) {
                            // EPS location information is included in MAP ATI response
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setLocationInformationEPS(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS());
                            }
                            // Inquire if EPS location information retrieves the current location info
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved() ||
                                !anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved()) {
                                if (gmlcCdrState.isInitialized()) {
                                    gmlcCdrState.setCurrentLocationRetrieved(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved()
                                        && anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved());
                                }
                            }
                        }
                    }

                    // Inquire if CS subscriber state is included in MAP ATI response subscriber's info
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState() != null) {
                        // CS Subscriber state is included in MAP ATI response, get it and store it as a response parameter
                        atiResponseParams.setSubscriberState(anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState());
                    }

                    // Inquire if location information includes GPRS location information
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS() != null) {
                        // GPRS location information is included in MAP ATI response
                        atiResponseParams.setLocationInformationGPRS(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationInformationGPRS(atiResponseParams.getLocationInformationGPRS());
                        }
                        // Inquire GPRS location information includes SGSN number (Global Title)
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber() != null) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSgsnNumber(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber());
                            }
                        }
                        // Inquire if GPRS location information is current in MAP ATI
                        // Get if GPRS location information is current
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isCurrentLocationRetrieved() ||
                            !anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isCurrentLocationRetrieved()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isCurrentLocationRetrieved());
                            }
                        }
                        // Get if GPRS location information is current
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent() ||
                            !anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSaiPresent(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent());
                            }
                        }
                    }

                    // Inquire if PS subscriber state is included in MAP ATI response subscriber's info
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                        // PS subscriber state is included in MAP ATI response, get it and store it as a response parameter
                        atiResponseParams.setPsSubscriberState(anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState());
                    }

                    // Inquire if subscriber's IMEI is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getIMEI() != null) {
                        // Subscriber's IMEI is included in MAP ATI response, get it and store it as a response parameter
                        atiResponseParams.setImei(anyTimeInterrogationResponse.getSubscriberInfo().getIMEI());
                        atiImei = anyTimeInterrogationResponse.getSubscriberInfo().getIMEI().getIMEI();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setImei(anyTimeInterrogationResponse.getSubscriberInfo().getIMEI());
                        }
                    }

                    // Inquire if subscriber's MS Classmark is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getMSClassmark2() != null) {
                        // Subscriber's MS Classmark is included in MAP ATI response, get it and store it as a response parameter
                        atiResponseParams.setMsClassmark2(anyTimeInterrogationResponse.getSubscriberInfo().getMSClassmark2());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMsClassmark2(anyTimeInterrogationResponse.getSubscriberInfo().getMSClassmark2());
                        }
                    }

                    // Inquire if subscriber's GPRS MS Class is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getGPRSMSClass() != null) {
                        // Subscriber's GPRS MS Class is included in MAP ATI response, get it and store it as a response parameter
                        atiResponseParams.setGprsMSClass(anyTimeInterrogationResponse.getSubscriberInfo().getGPRSMSClass());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsmsClass(anyTimeInterrogationResponse.getSubscriberInfo().getGPRSMSClass());
                        }
                    }

                    // Inquire if MNP Information Result is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getMNPInfoRes() != null) {
                        // MNP info result is included in MAP ATI response
                        // Subscriber's MNP Info Result is included in MAP ATI response, get it and store it as a response parameter
                        atiResponseParams.setMnpInfoRes(anyTimeInterrogationResponse.getSubscriberInfo().getMNPInfoRes());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMnpInfoRes(anyTimeInterrogationResponse.getSubscriberInfo().getMNPInfoRes());
                        }
                    }

                    // Inquire if imsVoiceOverPS-SessionsIndication is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getIMSVoiceOverPsSessionsIndication() != null) {
                        // imsVoiceOverPS-SessionsIndication is included in MAP ATI response
                        atiResponseParams.setImsVoiceOverPsSessionsIndication(anyTimeInterrogationResponse.getSubscriberInfo().getIMSVoiceOverPsSessionsIndication());
                    }

                    // Inquire if lastUEActivityTime is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLastUEActivityTime() != null) {
                        // Inquire if lastUEActivityTime is included in MAP ATI response
                        atiResponseParams.setLastUEActivityTime(anyTimeInterrogationResponse.getSubscriberInfo().getLastUEActivityTime());
                        // TODO
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if lastRATType is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLastRATType() != null) {
                        // Inquire if lastUEActivityTime is included in MAP ATI response
                        atiResponseParams.setLastRATType(anyTimeInterrogationResponse.getSubscriberInfo().getLastRATType());
                        // TODO
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if epsSubscriberState is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getEPSSubscriberState() != null) {
                        // Inquire if epsSubscriberState is included in MAP ATI response
                        atiResponseParams.setEpsSubscriberState(anyTimeInterrogationResponse.getSubscriberInfo().getEPSSubscriberState());
                        // TODO check if makes sense include this IE in CDR
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if locationInformationEPS (outside the locationInformation IE) is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS() != null) {
                        // locationInformationEPS (outside the locationInformation IE) is included in MAP ATI response
                        atiResponseParams.setLocationInformationEPS(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationInformationEPS(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS());
                        }
                    }

                    // Inquire if timeZone is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getTimeZone() != null) {
                        // timeZone is included in MAP ATI response
                        atiResponseParams.setTimeZone(anyTimeInterrogationResponse.getSubscriberInfo().getTimeZone());
                        // TODO check if makes sense include this IE in CDR
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if daylightSavingTime is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getDaylightSavingTime() != null) {
                        // daylightSavingTime is included in MAP ATI response
                        atiResponseParams.setDaylightSavingTime(anyTimeInterrogationResponse.getSubscriberInfo().getDaylightSavingTime());
                        // TODO check if makes sense include this IE in CDR
                        //if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if locationInformation5GS is included in MAP ATI response
                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS() != null) {
                        // locationInformation5GS is included in MAP ATI response
                        atiResponseParams.setLocationInformation5GS(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationInformation5GSFromMap(atiResponseParams.getLocationInformation5GS());
                        }
                        // Inquire if EPS location information retrieves the current location info
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved() ||
                            !anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved()
                                    && anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved());
                            }
                        }
                        // TODO check if anything else is needed here

                    }

                    if (gmlcCdrState.isInitialized()) {
                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getGeographicalInformation() != null ||
                                anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getGeodeticInformation() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_GEO_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS() != null) {
                                if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getGeographicalInformation() != null
                                    || anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getGeodeticInformation() != null) {
                                    if (gmlcCdrState.isInitialized()) {
                                        this.createCDRRecord(RecordStatus.ATI_GEO_SUCCESS);
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                                    if (gmlcCdrState.isInitialized()) {
                                        this.createCDRRecord(RecordStatus.ATI_ECGI_SUCCESS);
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                                    if (gmlcCdrState.isInitialized()) {
                                        this.createCDRRecord(RecordStatus.ATI_TAI_SUCCESS);
                                    }
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                                if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState() != null) {
                                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getSaiPresent())
                                            this.createCDRRecord(RecordStatus.ATI_SAI_STATE_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.ATI_CGI_STATE_SUCCESS);
                                    } else {
                                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getSaiPresent())
                                            this.createCDRRecord(RecordStatus.ATI_SAI_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.ATI_CGI_SUCCESS);
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                                        if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState() != null) {
                                            this.createCDRRecord(RecordStatus.ATI_LAI_STATE_SUCCESS);
                                        } else {
                                            this.createCDRRecord(RecordStatus.ATI_LAI_SUCCESS);
                                        }
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getVlrNumber() != null) {
                                    if (gmlcCdrState.getVlrAddress() != null) {
                                        this.createCDRRecord(RecordStatus.ATI_NNN_SUCCESS);
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation().getMscNumber() != null) {
                                    if (gmlcCdrState.getMscNumber() != null) {
                                        this.createCDRRecord(RecordStatus.ATI_NNN_SUCCESS);
                                    }
                                }
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getGeographicalInformation() != null ||
                                anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getGeodeticInformation() != null) {
                                this.createCDRRecord(RecordStatus.ATI_PS_GEO_SUCCESS);
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                                if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent())
                                            this.createCDRRecord(RecordStatus.ATI_PS_SAI_STATE_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.ATI_PS_CGI_STATE_SUCCESS);
                                    } else {
                                        if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent())
                                            this.createCDRRecord(RecordStatus.ATI_PS_SAI_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.ATI_PS_CGI_SUCCESS);
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                                        if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                                            this.createCDRRecord(RecordStatus.ATI_PS_LAI_STATE_SUCCESS);
                                        } else {
                                            this.createCDRRecord(RecordStatus.ATI_PS_LAI_SUCCESS);
                                        }
                                    }
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getRouteingAreaIdentity() != null) {
                                    this.createCDRRecord(RecordStatus.ATI_PS_RAI_SUCCESS);
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getLSAIdentity() != null) {
                                    this.createCDRRecord(RecordStatus.ATI_PS_LSA_SUCCESS);
                                } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber() != null) {
                                    if (gmlcCdrState.getSgsnNumber() != null) {
                                        this.createCDRRecord(RecordStatus.ATI_PS_NNN_SUCCESS);
                                    }
                                }
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().getGeographicalInformation() != null
                                || anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().getGeodeticInformation() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_GEO_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().getNRCellGlobalId() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_NCGI_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().getNRTAId() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_NRTAI_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().getEUtranCgi() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_ECGI_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformation5GS().getTAId() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_TAI_SUCCESS);
                                }
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getIMEI() != null) {
                            if (gmlcCdrState.getImei() != null) {
                                this.createCDRRecord(RecordStatus.ATI_IMEI_SUCCESS);
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getMNPInfoRes() != null) {
                            if (gmlcCdrState.getMnpInfoRes() != null) {
                                this.createCDRRecord(RecordStatus.ATI_MNP_INFO_SUCCESS);
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState().getNotReachableReason() != null) {
                                this.createCDRRecord(RecordStatus.ATI_STATE_NOT_REACHABLE);
                            } else {
                                this.createCDRRecord(RecordStatus.ATI_STATE_SUCCESS);
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState().getNetDetNotReachable() != null) {
                                this.createCDRRecord(RecordStatus.ATI_STATE_NOT_REACHABLE);
                            } else {
                                this.createCDRRecord(RecordStatus.ATI_STATE_SUCCESS);
                            }
                        /*} else if (anyTimeInterrogationResponse.getSubscriberInfo().getIMSVoiceOverPsSessionsIndication() != null) {
                            // TODO
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLastUEActivityTime() != null) {
                            // TODO
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLastRATType() != null) {
                            // TODO
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getEPSSubscriberState() != null) {
                            // TODO*/
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getSubscriberState().getNotReachableReason() != null) {
                                this.createCDRRecord(RecordStatus.ATI_STATE_NOT_REACHABLE);
                            } else {
                                this.createCDRRecord(RecordStatus.ATI_STATE_SUCCESS);
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getPSSubscriberState().getNetDetNotReachable() != null) {
                                this.createCDRRecord(RecordStatus.ATI_STATE_NOT_REACHABLE);
                            } else {
                                this.createCDRRecord(RecordStatus.ATI_STATE_SUCCESS);
                            }
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS() != null) {
                            if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS().getGeographicalInformation() != null
                                || anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS().getGeodeticInformation() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_GEO_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_ECGI_SUCCESS);
                                }
                            } else if (anyTimeInterrogationResponse.getSubscriberInfo().getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.ATI_TAI_SUCCESS);
                                }
                            }
                        } /*else if (anyTimeInterrogationResponse.getSubscriberInfo().getTimeZone() != null) {
                        // TODO
                        } else if (anyTimeInterrogationResponse.getSubscriberInfo().getDaylightSavingTime() != null) {
                            // TODO
                        }*/
                    }
                }

                // Handle successful retrieval of subscriber's info
                handleAtiLocationResponse(mlpRespResult, atiResponseParams, msisdnDigitsForAti, imsiForAti, null, "SUCCESS", atiHttpRespType);
            }

        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process AnyTimeInterrogationResponse=%s", anyTimeInterrogationResponse), e);
            this.createCDRRecord(RecordStatus.ATI_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP ATI response: " + e.getMessage(),
                    "ATI", msisdnDigitsForAti, imsiForAti, atiImei, null, null, null, null, null, false);
        } finally {
            detachFromMAPDialogMobility(aci);
        }
    }

    /**
     * Subscriber Information Services (another way to get location information via MAP)
     * MAP-SEND-ROUTING-INFO-FOR-SM (SRI) Events
     */
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequest, ActivityContextInterface aci) {
        try {
            this.logger.fine("\nReceived onSendRoutingInfoForSmRequest = " + sendRoutingInfoForSMRequest + ", ActivityContextInterface = " + aci);
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSendRoutingInfoForSmRequest=%s", sendRoutingInfoForSMRequest), e);
        }
    }

    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponse, ActivityContextInterface aci) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("\nReceived onSendRoutingInfoForSmResponse = " + sendRoutingInfoForSMResponse);
        }
        String psiMsisdn = null, psiImsi = null, nnn = null, eps, curlUser;
        Long sriForSMTransaction, transaction = null;
        boolean sriSmOnly;
        DateTime eventTime = DateTime.now();

        try {
            if (sendRoutingInfoForSMResponse != null) {
                if (sendRoutingInfoForSMResponse.getIMSI() != null)
                    psiImsi = sendRoutingInfoForSMResponse.getIMSI().getData();
                if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI() != null)
                    if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber() != null)
                        nnn = sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber().getAddress();

                SriSmResponseParams sriSmResponseParams = new SriSmResponseParams();
                MLPResponse.MLPResultType mlpRespResult = null;
                String mlpClientErrorMessage;

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.mapSriSmCdrInitializer(aci, this.getCDRInterface(), sendRoutingInfoForSMResponse);
                // Set timer last
                this.setTimer(aci);

                sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(sendRoutingInfoForSMResponse.getMAPDialog().getLocalDialogId());
                if (sriForSMTransaction == null) {
                    throw new Exception();
                }
                TimerID sriSmTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "timerID");
                if (sriSmTimerID != null)
                    this.timerFacility.cancelTimer(sriSmTimerID);
                String domainForPsi = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "sriPsiDomain");
                psiMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                eps = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "locationInfoEPS");
                curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                sriSmOnly = (Boolean) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "sriSmOnly");
                DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "transactionStart");
                gmlcCdrState.setDialogStartTime(dialogStartTime);
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, psiMsisdn));
                    gmlcCdrState.setCurlUser(curlUser);
                    if (dialogStartTime != null) {
                        Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                        gmlcCdrState.setDialogDuration(dialogDuration);
                    }
                }
                mobileCoreNetworkTransactions.destroy(sriForSMTransaction);

                MessageType tcapMessageType = sendRoutingInfoForSMResponse.getMAPDialog().getTCAPMessageType();
                if (tcapMessageType == MessageType.Abort) {
                    mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                    mlpClientErrorMessage = "TCAP ABORT on SRISM";
                    if (gmlcCdrState.isInitialized()) {
                        this.createCDRRecord(RecordStatus.SRISM_TCAP_DIALOG_ABORT);
                    }
                    if (sendRoutingInfoForSMResponse.getMAPDialog().getRemoteAddress() != null)
                        if (sendRoutingInfoForSMResponse.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                            nnn = sendRoutingInfoForSMResponse.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "SRISM", psiMsisdn, psiImsi, null, null, nnn,
                        null, null, null, false);
                    return;
                }

                if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI() != null) {
                    this.logger.fine("\nonSendRoutingInfoForSmResponse, LocationInfoWithLMSI parameter");
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    sriSmResponseParams.setLocationInfoWithLMSI(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI());
                    if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setNetworkNodeNumber(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber());
                        }
                    }
                    if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getAdditionalNumber() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAdditionalNumber(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getAdditionalNumber());
                        }
                    }
                    if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getLMSI() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLmsi(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getLMSI());
                        }
                    }
                    if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getGprsNodeIndicator()) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsNodeIndicator(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getGprsNodeIndicator());
                        }
                    } else {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsNodeIndicator(false);
                        }
                    }
                }

                if (sendRoutingInfoForSMResponse.getIMSI() != null) {
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    sriSmResponseParams.setImsi(sendRoutingInfoForSMResponse.getIMSI());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setImsi(sriSmResponseParams.getImsi());
                    }
                }

                this.setSendRoutingInfoForSMResponse(sendRoutingInfoForSMResponse);

                if (this.getSendRoutingInfoForSMResponse() != null) {

                    if (sriSmOnly) {
                        // handle retrieval of SRISM response when SRISM is the operation requested
                        if (gmlcCdrState.isInitialized()) {
                            if (sendRoutingInfoForSMResponse.getIMSI() != null)
                                this.createCDRRecord(RecordStatus.SRISM_SUCCESS);
                            else
                                this.createCDRRecord(RecordStatus.SRISM_ERROR);
                        }
                        handleSriResponseValue(mlpRespResult, null, sriSmResponseParams, null, "SRISM", psiMsisdn, null, null);
                        // destroy transaction, detach and return
                        mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                        detachFromMAPDialogSms(aci);
                        return;
                    }

                    LMSI lmsi = null;
                    if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI() != null) {
                        if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getLMSI() != null) {
                            lmsi = sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getLMSI();
                        }
                    }
                    boolean locationInformation = true;
                    boolean subscriberState = true;
                    boolean currentLocation = true;
                    DomainType requestedDomain;
                    if (domainForPsi.equalsIgnoreCase("ps"))
                        requestedDomain = DomainType.psDomain;
                    else
                        requestedDomain = DomainType.csDomain;
                    boolean imei = true;
                    boolean msClassmark = true;
                    boolean mnpRequestedInfo = true;
                    boolean locationInformationEPSsupported;
                    if (eps == null)
                        locationInformationEPSsupported = true;
                    else
                        locationInformationEPSsupported = Boolean.parseBoolean(eps);
                    RequestedInfo requestedInfo = new RequestedInfoImpl(locationInformation, subscriberState, null, currentLocation,
                        requestedDomain, imei, msClassmark, mnpRequestedInfo, locationInformationEPSsupported);

                    MAPDialogMobility mapDialogMobility = null;

                    try {
                        if (sendRoutingInfoForSMResponse.getLocationInfoWithLMSI() != null) {
                            if (this.subscriberInfoEnquiryContext == null) {
                                mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(
                                    this.getMAPPsiApplicationContext(), this.getGmlcSccpAddress(), null,
                                    sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getGprsNodeIndicator() ?
                                        getSgsnSccpAddress(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber().getAddress()) :
                                        getVlrSccpAddress(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber().getAddress()), null);
                            } else {
                                mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(
                                    this.subscriberInfoEnquiryContext, this.getGmlcSccpAddress(), null,
                                    sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getGprsNodeIndicator() ?
                                        getSgsnSccpAddress(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber().getAddress()) :
                                        getVlrSccpAddress(sendRoutingInfoForSMResponse.getLocationInfoWithLMSI().getNetworkNodeNumber().getAddress()), null);
                            }
                        }
                    } catch (MAPException e) {
                        logger.severe(e.getMessage());
                    }

                    // Transaction
                    transaction = mobileCoreNetworkTransactions.create();
                    mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
                    mobileCoreNetworkTransactions.setValue(transaction, "sriForSMImsi", sendRoutingInfoForSMResponse.getIMSI() != null ? new String(sendRoutingInfoForSMResponse.getIMSI().getData().getBytes()) : null);
                    mobileCoreNetworkTransactions.setValue(transaction, "psiMsisdn", psiMsisdn);
                    mobileCoreNetworkTransactions.setValue(transaction, "psiNNN", nnn);
                    mobileCoreNetworkTransactions.setValue(transaction, "sriForSmResponse", sriSmResponseParams);
                    mobileCoreNetworkTransactions.setDialog(transaction, mapDialogMobility != null ? mapDialogMobility.getLocalDialogId() : null);
                    mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", dialogStartTime);
                    if (mapDialogMobility != null)
                        mapDialogMobility.addProvideSubscriberInfoRequest(sendRoutingInfoForSMResponse.getIMSI(), lmsi, requestedInfo, null, null);

                    // Keep ACI in across MAP dialog for PSI
                    ActivityContextInterface psiDialogACI = this.mapAcif.getActivityContextInterface(mapDialogMobility);
                    psiDialogACI.attach(this.sbbContext.getSbbLocalObject());

                    // set new timer for the case PSI does not arrive in time
                    TimerID timerID = timerFacility.setTimer(psiDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                    mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

                    // ProvideSubscriberInfoRequest is now composed by values taken from SRISM response
                    // Send PSI
                    assert mapDialogMobility != null;
                    mapDialogMobility.send();

                }
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSendRoutingInfoForSmResponse=%s", sendRoutingInfoForSMResponse), e);
            this.createCDRRecord(RecordStatus.SRISM_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP SRISM response: " + e.getMessage(),
                "SRISM", psiMsisdn, psiImsi, null, null, nnn, null, null, null, false);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromMAPDialogSms(aci);
        }
    }

    /**
     * Subscriber Information Services (via MAP call handling service)
     * MAP-SEND-ROUTING-INFO (SRI) Events
     */
    public void onSendRoutingInformationRequest(SendRoutingInformationRequest sendRoutingInformationRequest, ActivityContextInterface aci) {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onSendRoutingInformationRequest = " + sendRoutingInformationRequest + ", ActivityContextInterface = " + aci);
            }

        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSendRoutingInformationRequest=%s", sendRoutingInformationRequest), e);
        }
    }

    public void onSendRoutingInformationResponse(SendRoutingInformationResponse sendRoutingInformationResponse, ActivityContextInterface aci) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("\nReceived onSendRoutingInformationResponse = " + sendRoutingInformationResponse);
        }
        String psiMsisdn = null, psiImsi = null, nnn = null, eps, curlUser;
        Long sriTransaction, transaction = null;
        boolean sriOnly;
        DateTime eventTime = DateTime.now();

        try {
            if (sendRoutingInformationResponse != null) {
                if (sendRoutingInformationResponse.getIMSI() != null)
                    psiImsi = sendRoutingInformationResponse.getIMSI().getData();
                if (sendRoutingInformationResponse.getVmscAddress() != null)
                    nnn = sendRoutingInformationResponse.getVmscAddress().getAddress();

                SriResponseValues sriResponseValues = new SriResponseValues();
                MLPResponse.MLPResultType mlpRespResult;
                String mlpClientErrorMessage;

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.mapSriCdrInitializer(aci, this.getCDRInterface(), sendRoutingInformationResponse);
                // Set timer last
                this.setTimer(aci);

                sriTransaction = mobileCoreNetworkTransactions.getTransactionId(sendRoutingInformationResponse.getMAPDialog().getLocalDialogId());
                TimerID sriTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(sriTransaction, "timerID");
                if (sriTimerID != null)
                    this.timerFacility.cancelTimer(sriTimerID);
                String domainForPsi = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "sriPsiDomain");
                eps = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "locationInfoEPS");
                psiMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(sriTransaction, "transactionStart");
                sriOnly = (Boolean) mobileCoreNetworkTransactions.getValue(sriTransaction, "sriOnly");
                gmlcCdrState.setDialogStartTime(dialogStartTime);
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setCurlUser(curlUser);
                    if (dialogStartTime != null) {
                        Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                        gmlcCdrState.setDialogDuration(dialogDuration);
                    }
                    if (psiMsisdn != null)
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, psiMsisdn));
                }
                mobileCoreNetworkTransactions.destroy(sriTransaction);

                MessageType tcapMessageType = sendRoutingInformationResponse.getMAPDialog().getTCAPMessageType();
                if (tcapMessageType == MessageType.Abort) {
                    mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                    mlpClientErrorMessage = "TCAP ABORT on SRI";
                    if (gmlcCdrState.isInitialized()) {
                        this.createCDRRecord(RecordStatus.SRI_TCAP_DIALOG_ABORT);
                    }
                    if (sendRoutingInformationResponse.getMAPDialog().getRemoteAddress() != null)
                        if (sendRoutingInformationResponse.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                            nnn = sendRoutingInformationResponse.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "SRI", psiMsisdn, psiImsi, null, null, nnn, null,
                        null, null, false);
                    return;
                }


                if (sendRoutingInformationResponse.getIMSI() != null) {
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    this.logger.fine("\nonSendRoutingInformationResponse, IMSI=" + sendRoutingInformationResponse.getIMSI());
                    sriResponseValues.setImsi(sendRoutingInformationResponse.getIMSI());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setImsi(sriResponseValues.getImsi());
                    }
                } else {
                    mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                    mlpClientErrorMessage = "SYSTEM FAILURE on SRI response, IMSI is null";
                    this.createCDRRecord(RecordStatus.SRI_SYSTEM_FAILURE);
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "SRI", psiMsisdn, psiImsi, null, null,
                        nnn, null, null, null, false);
                    return;
                }

                if (sendRoutingInformationResponse.getVmscAddress() != null) {
                    sriResponseValues.setVmscAddress(sendRoutingInformationResponse.getVmscAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setNetworkNodeNumber(sriResponseValues.getVmscAddress());
                        if (this.logger.isFineEnabled()) {
                            this.logger.fine("\nonSendRoutingInformationResponse: CDR state is initialized, NNN (VMSC Address) set");
                        }
                    }
                } else {
                    if (sendRoutingInformationResponse.getExtendedRoutingInfo() != null) {
                        sriResponseValues.setExtendedRoutingInfo(sendRoutingInformationResponse.getExtendedRoutingInfo());
                        if (sendRoutingInformationResponse.getExtendedRoutingInfo().getRoutingInfo() != null)
                            if (this.logger.isFineEnabled()) {
                                this.logger.fine("\nonSendRoutingInformationResponse: roamingNumber received: " + sendRoutingInformationResponse.getExtendedRoutingInfo().getRoutingInfo().getRoamingNumber());
                            }
                    } else {
                        mlpClientErrorMessage = "SYSTEM FAILURE on SRI response, vmscAddress is null";
                        this.createCDRRecord(RecordStatus.SRI_VMSC_ADDRESS_NOT_PRESENT);
                        this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "SRI", psiMsisdn, psiImsi, null, null,
                            nnn, null, null, null, false);
                        return;
                    }
                }

                this.setSendRoutingInformationResponse(sendRoutingInformationResponse);

                if (this.getSendRoutingInformationResponse() != null) {

                    if (sriOnly) {
                        // handle retrieval of SRI response when SRI is the operation requested
                        if (gmlcCdrState.isInitialized()) {
                            this.createCDRRecord(RecordStatus.SRI_SUCCESS);
                        }
                        handleSriResponseValue(mlpRespResult, sriResponseValues, null, null, "SRI", psiMsisdn, null, null);
                        // destroy transaction if exists, detach and return
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                        detachFromMAPDialogCallHandling(aci);
                        return;
                    }

                    boolean locationInformation = true;
                    boolean subscriberState = true;
                    boolean currentLocation = true;
                    DomainType requestedDomain;
                    if (domainForPsi.equalsIgnoreCase("ps"))
                        requestedDomain = DomainType.psDomain;
                    else
                        requestedDomain = DomainType.csDomain;
                    boolean imei = true;
                    boolean msClassmark = true;
                    boolean mnpRequestedInfo = true;
                    boolean locationInformationEPSsupported;
                    if (eps == null)
                        locationInformationEPSsupported = true;
                    else
                        locationInformationEPSsupported = Boolean.parseBoolean(eps);
                    RequestedInfo requestedInfo = new RequestedInfoImpl(locationInformation, subscriberState, null, currentLocation,
                        requestedDomain, imei, msClassmark, mnpRequestedInfo, locationInformationEPSsupported);

                    MAPDialogMobility mapDialogMobility = null;

                    try {
                        if (this.subscriberInfoEnquiryContext == null) {
                            if (sendRoutingInformationResponse.getVmscAddress() != null)
                                mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(this.getMAPPsiApplicationContext(),
                                    this.getGmlcSccpAddress(), null, getVlrSccpAddress(sendRoutingInformationResponse.getVmscAddress().getAddress()), null);
                        } else {
                            if (sendRoutingInformationResponse.getVmscAddress() != null)
                                mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(this.subscriberInfoEnquiryContext,
                                    this.getGmlcSccpAddress(), null,
                                    getVlrSccpAddress(sendRoutingInformationResponse.getVmscAddress().getAddress()), null);
                        }
                    } catch (MAPException e) {
                        logger.severe(e.getMessage());
                    }

                    // Transaction
                    transaction = mobileCoreNetworkTransactions.create();
                    mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
                    mobileCoreNetworkTransactions.setValue(transaction, "sriImsi", new String(sendRoutingInformationResponse.getIMSI().getData().getBytes()));
                    mobileCoreNetworkTransactions.setValue(transaction, "psiMsisdn", psiMsisdn);
                    mobileCoreNetworkTransactions.setValue(transaction, "sriResponse", sriResponseValues);
                    mobileCoreNetworkTransactions.setValue(transaction, "psiNNN", nnn);
                    mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", dialogStartTime);
                    assert mapDialogMobility != null;
                    mobileCoreNetworkTransactions.setDialog(transaction, mapDialogMobility.getLocalDialogId());

                    mapDialogMobility.addProvideSubscriberInfoRequest(sendRoutingInformationResponse.getIMSI(), null, requestedInfo, null, null);

                    // Keep ACI in across MAP dialog for PSI
                    ActivityContextInterface psiDialogACI = this.mapAcif.getActivityContextInterface(mapDialogMobility);
                    psiDialogACI.attach(this.sbbContext.getSbbLocalObject());

                    // set new timer for the PSI request/response cycle
                    TimerID timerID = timerFacility.setTimer(psiDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                    mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

                    // ProvideSubscriberInfoRequest is now composed by values taken from SRI response
                    // Send PSI
                    mapDialogMobility.send();
                }
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSendRoutingInformationResponse=%s", sendRoutingInformationResponse), e);
            this.createCDRRecord(RecordStatus.SRI_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP SRI response: " + e.getMessage(),
                "SRI", psiMsisdn, psiImsi, null, null, nnn, null, null, null, false);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromMAPDialogCallHandling(aci);
        }
    }

    /**
     * MAP-PROVIDE-SUBSCRIBER-INFO (PSI) Events
     */
    public void onProvideSubscriberInformationRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest, ActivityContextInterface aci) {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onProvideSubscriberInformationRequest = " + provideSubscriberInfoRequest + ", ActivityContextInterface = " + aci);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onProvideSubscriberInformationRequest=%s", provideSubscriberInfoRequest), e);
        }
    }

    /**
     * MAP-PROVIDE-SUBSCRIBER-INFO (PSI)
     */
    public void provideSubscriberInfoRequestFirst(String imsiStr, String nnn, String domain, String psiMsisdn, String epsLocationInfo, String curlUser) {

        IMSI imsi = new IMSIImpl(imsiStr);
        boolean locationInformation = true;
        boolean subscriberState = true;
        boolean currentLocation = true;
        DomainType requestedDomain;
        Long transaction;
        if (domain.equalsIgnoreCase("ps"))
            requestedDomain = DomainType.psDomain;
        else
            requestedDomain = DomainType.csDomain;
        boolean imei = true;
        boolean msClassmark = true;
        boolean mnpRequestedInfo = true;
        boolean locationInformationEPSsupported;
        if (epsLocationInfo == null)
            locationInformationEPSsupported = true;
        else
            locationInformationEPSsupported = Boolean.parseBoolean(epsLocationInfo);
        RequestedInfo requestedInfo = new RequestedInfoImpl(locationInformation, subscriberState, null, currentLocation,
            requestedDomain, imei, msClassmark, mnpRequestedInfo, locationInformationEPSsupported);

        SccpAddress networkNodeAddress = requestedDomain.equals(DomainType.psDomain) ? getSgsnSccpAddress(nnn) : getVlrSccpAddress(nnn);

        MAPDialogMobility mapDialogMobility = null;
        try {
            if (this.subscriberInfoEnquiryContext == null) {
                mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(this.getMAPPsiApplicationContext(),
                        this.getGmlcSccpAddress(), null, networkNodeAddress, null);
            } else {
                mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(this.subscriberInfoEnquiryContext,
                    this.getGmlcSccpAddress(), null, networkNodeAddress, null);
            }

        } catch (MAPException e) {
            logger.severe(e.getMessage());
        }

        try {
            if (mapDialogMobility != null)
                mapDialogMobility.addProvideSubscriberInfoRequest(imsi, null, requestedInfo, null, null);
        } catch (MAPException e) {
            logger.severe(String.format("MAP Exception while trying to add PSI to MAP dialog mobility dialog on provideSubscriberInfoRequestFirst=%s", e.getMessage()));
        }

        // Keep ACI in across MAP dialog for PSI
        ActivityContextInterface psiDialogACI = this.mapAcif.getActivityContextInterface(mapDialogMobility);
        psiDialogACI.attach(this.sbbContext.getSbbLocalObject());

        // Transaction
        transaction = mobileCoreNetworkTransactions.create();
        mobileCoreNetworkTransactions.setValue(transaction, "psiServiceType", "psiFirst");
        mobileCoreNetworkTransactions.setValue(transaction, "psiOnlyImsi", imsiStr);
        mobileCoreNetworkTransactions.setValue(transaction, "psiOnlyNnn", nnn);
        mobileCoreNetworkTransactions.setValue(transaction, "psiMsisdn", psiMsisdn);
        mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
        DateTime transactionStart = DateTime.now();
        mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
        assert mapDialogMobility != null;
        mobileCoreNetworkTransactions.setDialog(transaction, mapDialogMobility.getLocalDialogId());
        // set new timer for the PSI request/response cycle
        TimerID timerID = timerFacility.setTimer(psiDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
        mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

        // RequestedInfo for ProvideSubscriberInfoRequest is composed by values taken from HTTP request (IMSI and NNN)
        try {
            // Send PSI
            mapDialogMobility.send();

        } catch (MAPException e) {
            logger.severe(String.format("MAP Exception while trying to send PSI on provideSubscriberInfoRequestFirst=%s", e.getMessage()));
            logger.severe(e.getMessage());
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        }
    }

    public void onProvideSubscriberInformationResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse, ActivityContextInterface aci) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("\nReceived onProvideSubscriberInformationResponse = " + provideSubscriberInfoResponse);
        }
        MAPErrorMessage mapErrorMessage = this.getErrorResponse();
        this.setProvideSubscriberInformationResponse(provideSubscriberInfoResponse);
        String psiMsisdn = null, psiImsi = null, psiImei = null, nnn = null, curlUser;
        Long transaction = null;
        DateTime eventTime = DateTime.now();

        try {
            PsiResponseParams psiResponseParams = new PsiResponseParams();
            MLPResponse.MLPResultType mlpRespResult = null;
            String mlpClientErrorMessage = null;

            if (provideSubscriberInfoResponse != null) {
                // CDR initialization
                CDRCreationHelper.GmlcCdrStateString gmlcCdrStateString = CDRCreationHelper.mapPsiCdrInitializer(aci, this.getCDRInterface(), provideSubscriberInfoResponse, null);
                GMLCCDRState gmlcCdrState = gmlcCdrStateString.getGmlcCdrState();
                nnn = gmlcCdrStateString.getNnn();
                // Set timer last
                this.setTimer(aci);

                // Transaction
                transaction = mobileCoreNetworkTransactions.getTransactionId(provideSubscriberInfoResponse.getMAPDialog().getLocalDialogId());

                if (transaction != null) {

                    TimerID psiTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                    if (psiTimerID != null)
                        this.timerFacility.cancelTimer(psiTimerID);

                    // PSI only
                    String psiServiceType = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiServiceType");
                    String psiOnlyImsi = psiImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                    String psiOnlyNnn = nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyNnn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");

                    psiResponseParams.setPsiServiceType(psiServiceType);
                    psiResponseParams.setPsiOnlyImsi(psiOnlyImsi);
                    psiResponseParams.setPsiOnlyNnn(psiOnlyNnn);

                    if (this.logger.isFineEnabled()) {
                        logger.fine(String.format("*** psiOnly values fixed to { '%s', '%s', '%s' } obtained from Transaction.", psiServiceType, psiOnlyImsi, psiOnlyNnn));
                    }

                    // SRISM/SRI - PSI
                    String sriForSMImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    String sriImsi = psiImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    SriSmResponseParams sriSmResponse = (SriSmResponseParams) mobileCoreNetworkTransactions.getValue(transaction, "sriForSmResponse");
                    SriResponseValues sriResponse = (SriResponseValues) mobileCoreNetworkTransactions.getValue(transaction, "sriResponse");
                    psiMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                    gmlcCdrState.setDialogStartTime(dialogStartTime);
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCurlUser(curlUser);
                        if (dialogStartTime != null) {
                            Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                            gmlcCdrState.setDialogDuration(dialogDuration);
                        }
                    }
                    mobileCoreNetworkTransactions.destroy(transaction);

                    if (sriForSMImsi != null)
                        psiResponseParams.setSriForSMImsi(sriForSMImsi);
                    else if (sriImsi != null)
                        psiResponseParams.setSriForSMImsi(sriImsi);
                    if (sriSmResponse != null)
                        psiResponseParams.setSriForSmResponse(sriSmResponse);
                    else if (sriResponse != null)
                        psiResponseParams.setSriResponse(sriResponse);

                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCurlUser(curlUser);
                        if (psiServiceType != null) {
                            if (psiServiceType.equalsIgnoreCase("psiFirst") && psiOnlyImsi != null && psiOnlyNnn != null) {
                                IMSI imsi = new IMSIImpl(psiOnlyImsi);
                                gmlcCdrState.setImsi(imsi);
                            }
                        }
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, psiMsisdn));
                    }
                } else {
                    throw new Exception();
                }

                MessageType tcapMessageType = provideSubscriberInfoResponse.getMAPDialog().getTCAPMessageType();
                if (tcapMessageType == MessageType.Abort) {
                    mobileCoreNetworkTransactions.destroy(transaction);
                    mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                    mlpClientErrorMessage = "TCAP ABORT on PSI";
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, psiMsisdn));
                        this.createCDRRecord(RecordStatus.SRISM_TCAP_DIALOG_ABORT);
                    }
                    if (provideSubscriberInfoResponse.getMAPDialog().getRemoteAddress() != null)
                        if (provideSubscriberInfoResponse.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                            nnn = provideSubscriberInfoResponse.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "PSI", psiMsisdn, psiImsi, null, null,
                        nnn, null, null, null, false);
                    return;
                }

                // Inquire if MAP PSI response includes subscriber's info
                if (provideSubscriberInfoResponse.getSubscriberInfo() != null) {
                    this.logger.fine("\nonProvideSubscriberInformationResponse, subscriberInfo=" + provideSubscriberInfoResponse.getSubscriberInfo());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setSubscriberInfo(provideSubscriberInfoResponse.getSubscriberInfo());
                    }

                    // Inquire if Location information is included in MAP PSI response subscriber's info
                    // Inquire if Location information is included in MAP PSI response subscriber's info
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation() != null) {
                        // Location information is included in MAP PSI response
                        psiResponseParams.setLocationInformation(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation());
                        // Inquire if Current Location Retrieved information is included in MAP PSI response subscriber's info
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCurrentLocationRetrieved()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(true);
                            }
                        } else {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(false);
                            }
                        }
                        // Inquire if SAI is present in MAP PSI response subscriber's info
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getSaiPresent()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSaiPresent(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getSaiPresent());
                            }
                        } else {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSaiPresent(false);
                            }
                        }
                        // Inquire if location information includes EPS location info (LTE)
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS() != null) {
                            // EPS location information is included in MAP PSI response
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setLocationInformationEPS(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS());
                            }
                            // Inquire if EPS location information retrieves the current location info
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved() ||
                                !provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved()) {
                                if (gmlcCdrState.isInitialized()) {
                                    gmlcCdrState.setCurrentLocationRetrieved(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved()
                                        && provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved());
                                }
                            }
                        }
                    }

                    // Inquire if CS subscriber state is included in MAP PSI response subscriber's info
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getSubscriberState() != null) {
                        // CS Subscriber state is included in MAP PSI response, get it and store it as a response parameter
                        psiResponseParams.setSubscriberState(provideSubscriberInfoResponse.getSubscriberInfo().getSubscriberState());
                    }

                    // Inquire if location information includes GPRS location information
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS() != null) {
                        // GPRS location information is included in MAP PSI response
                        psiResponseParams.setLocationInformationGPRS(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationInformationGPRS(psiResponseParams.getLocationInformationGPRS());
                        }
                        // Inquire GPRS location information includes SGSN number (Global Title)
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber() != null) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSgsnNumber(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber());
                            }
                        }
                        // Inquire if GPRS location information is current in MAP PSI
                        // Get if GPRS location information is current
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isCurrentLocationRetrieved() ||
                            !provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isCurrentLocationRetrieved()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isCurrentLocationRetrieved());
                            }
                        }
                        // Get if GPRS location information is current
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent() ||
                            !provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSaiPresent(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent());
                            }
                        }
                    }

                    // Inquire if PS subscriber state is included in MAP PSI response subscriber's info
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                        // PS subscriber state is included in MAP PSI response, get it and store it as a response parameter
                        psiResponseParams.setPsSubscriberState(provideSubscriberInfoResponse.getSubscriberInfo().getPSSubscriberState());
                    }

                    // Inquire if subscriber's IMEI is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getIMEI() != null) {
                        // Subscriber's IMEI is included in MAP PSI response, get it and store it as a response parameter
                        psiResponseParams.setImei(provideSubscriberInfoResponse.getSubscriberInfo().getIMEI());
                        psiImei = provideSubscriberInfoResponse.getSubscriberInfo().getIMEI().getIMEI();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setImei(provideSubscriberInfoResponse.getSubscriberInfo().getIMEI());
                        }
                    }

                    // Inquire if subscriber's MS Classmark is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getMSClassmark2() != null) {
                        // Subscriber's MS Classmark is included in MAP PSI response, get it and store it as a response parameter
                        psiResponseParams.setMsClassmark2(provideSubscriberInfoResponse.getSubscriberInfo().getMSClassmark2());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMsClassmark2(provideSubscriberInfoResponse.getSubscriberInfo().getMSClassmark2());
                        }
                    }

                    // Inquire if subscriber's GPRS MS Class is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getGPRSMSClass() != null) {
                        // Subscriber's GPRS MS Class is included in MAP PSI response, get it and store it as a response parameter
                        psiResponseParams.setGprsMSClass(provideSubscriberInfoResponse.getSubscriberInfo().getGPRSMSClass());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsmsClass(provideSubscriberInfoResponse.getSubscriberInfo().getGPRSMSClass());
                        }
                    }

                    // Inquire if MNP Information Result is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getMNPInfoRes() != null) {
                        // MNP info result is included in MAP PSI response
                        // Subscriber's MNP Info Result is included in MAP PSI response, get it and store it as a response parameter
                        psiResponseParams.setMnpInfoRes(provideSubscriberInfoResponse.getSubscriberInfo().getMNPInfoRes());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMnpInfoRes(provideSubscriberInfoResponse.getSubscriberInfo().getMNPInfoRes());
                        }
                    }

                    // Inquire if imsVoiceOverPS-SessionsIndication is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getIMSVoiceOverPsSessionsIndication() != null) {
                        // imsVoiceOverPS-SessionsIndication is included in MAP PSI response
                        psiResponseParams.setImsVoiceOverPsSessionsIndication(provideSubscriberInfoResponse.getSubscriberInfo().getIMSVoiceOverPsSessionsIndication());
                    }

                    // Inquire if lastUEActivityTime is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLastUEActivityTime() != null) {
                        // Inquire if lastUEActivityTime is included in MAP PSI response
                        psiResponseParams.setLastUEActivityTime(provideSubscriberInfoResponse.getSubscriberInfo().getLastUEActivityTime());
                        // TODO
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if lastRATType is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLastRATType() != null) {
                        // Inquire if lastUEActivityTime is included in MAP PSI response
                        psiResponseParams.setLastRATType(provideSubscriberInfoResponse.getSubscriberInfo().getLastRATType());
                        // TODO
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if epsSubscriberState is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getEPSSubscriberState() != null) {
                        // Inquire if epsSubscriberState is included in MAP PSI response
                        psiResponseParams.setEpsSubscriberState(provideSubscriberInfoResponse.getSubscriberInfo().getEPSSubscriberState());
                        // TODO check if makes sense include this IE in CDR
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if locationInformationEPS (outside the locationInformation IE) is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS() != null) {
                        // locationInformationEPS (outside the locationInformation IE) is included in MAP PSI response
                        psiResponseParams.setLocationInformationEPS(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationInformationEPS(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS());
                        }
                    }

                    // Inquire if timeZone is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getTimeZone() != null) {
                        // timeZone is included in MAP PSI response
                        psiResponseParams.setTimeZone(provideSubscriberInfoResponse.getSubscriberInfo().getTimeZone());
                        // TODO check if makes sense include this IE in CDR
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if daylightSavingTime is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getDaylightSavingTime() != null) {
                        // daylightSavingTime is included in MAP PSI response
                        psiResponseParams.setDaylightSavingTime(provideSubscriberInfoResponse.getSubscriberInfo().getDaylightSavingTime());
                        // TODO check if makes sense include this IE in CDR
                        // if (gmlcCdrState.isInitialized()) {}
                    }

                    // Inquire if locationInformation5GS is included in MAP PSI response
                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS() != null) {
                        // locationInformation5GS is included in MAP PSI response
                        psiResponseParams.setLocationInformation5GS(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationInformation5GSFromMap(psiResponseParams.getLocationInformation5GS());
                        }
                        // Inquire if EPS location information retrieves the current location info
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved() ||
                            !provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved()) {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCurrentLocationRetrieved(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved()
                                    && provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().isCurrentLocationRetrieved());
                            }
                        }
                        // TODO check if anything else is needed here
                    }
                }

                if (gmlcCdrState.isInitialized()) {
                    if (provideSubscriberInfoResponse.getSubscriberInfo() != null) {
                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation() != null) {
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getGeographicalInformation() != null ||
                                provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getGeodeticInformation() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_GEO_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS() != null) {
                                if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getGeographicalInformation() != null
                                    || provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getGeodeticInformation() != null) {
                                    if (gmlcCdrState.isInitialized()) {
                                        this.createCDRRecord(RecordStatus.PSI_GEO_SUCCESS);
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                                    if (gmlcCdrState.isInitialized()) {
                                        this.createCDRRecord(RecordStatus.PSI_ECGI_SUCCESS);
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                                    if (gmlcCdrState.isInitialized()) {
                                        this.createCDRRecord(RecordStatus.PSI_TAI_SUCCESS);
                                    }
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                                if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (provideSubscriberInfoResponse.getSubscriberInfo().getSubscriberState() != null) {
                                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getSaiPresent())
                                            this.createCDRRecord(RecordStatus.PSI_SAI_STATE_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.PSI_CGI_STATE_SUCCESS);
                                    } else {
                                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getSaiPresent())
                                            this.createCDRRecord(RecordStatus.PSI_SAI_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.PSI_CGI_SUCCESS);
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                                        if (provideSubscriberInfoResponse.getSubscriberInfo().getSubscriberState() != null) {
                                            this.createCDRRecord(RecordStatus.PSI_LAI_STATE_SUCCESS);
                                        } else {
                                            this.createCDRRecord(RecordStatus.PSI_LAI_SUCCESS);
                                        }
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getVlrNumber() != null) {
                                    if (gmlcCdrState.getVlrAddress() != null) {
                                        this.createCDRRecord(RecordStatus.PSI_NNN_SUCCESS);
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getMscNumber() != null) {
                                    if (gmlcCdrState.getMscNumber() != null) {
                                        this.createCDRRecord(RecordStatus.PSI_NNN_SUCCESS);
                                    }
                                }
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS() != null) {
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getGeographicalInformation() != null ||
                                provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getGeodeticInformation() != null) {
                                this.createCDRRecord(RecordStatus.PSI_PS_GEO_SUCCESS);
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                                if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (provideSubscriberInfoResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent())
                                            this.createCDRRecord(RecordStatus.PSI_PS_SAI_STATE_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.PSI_PS_CGI_STATE_SUCCESS);
                                    } else {
                                        if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().isSaiPresent())
                                            this.createCDRRecord(RecordStatus.PSI_PS_SAI_SUCCESS);
                                        else
                                            this.createCDRRecord(RecordStatus.PSI_PS_CGI_SUCCESS);
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                                    if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                                        if (provideSubscriberInfoResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                                            this.createCDRRecord(RecordStatus.PSI_PS_LAI_STATE_SUCCESS);
                                        } else {
                                            this.createCDRRecord(RecordStatus.PSI_PS_LAI_SUCCESS);
                                        }
                                    }
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getRouteingAreaIdentity() != null) {
                                    this.createCDRRecord(RecordStatus.PSI_PS_RAI_SUCCESS);
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getLSAIdentity() != null) {
                                    this.createCDRRecord(RecordStatus.PSI_PS_LSA_SUCCESS);
                                } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber() != null) {
                                    if (gmlcCdrState.getSgsnNumber() != null) {
                                        this.createCDRRecord(RecordStatus.PSI_PS_NNN_SUCCESS);
                                    }
                                }
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS() != null) {
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().getGeographicalInformation() != null
                                || provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().getGeodeticInformation() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_GEO_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().getNRCellGlobalId() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_NCGI_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().getNRTAId() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_NRTAI_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().getEUtranCgi() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_ECGI_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation5GS().getTAId() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_TAI_SUCCESS);
                                }
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getIMEI() != null) {
                            if (gmlcCdrState.getImei() != null) {
                                this.createCDRRecord(RecordStatus.PSI_IMEI_SUCCESS);
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getMNPInfoRes() != null) {
                            if (gmlcCdrState.getMnpInfoRes() != null) {
                                this.createCDRRecord(RecordStatus.PSI_MNP_INFO_SUCCESS);
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getSubscriberState() != null) {
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getSubscriberState().getNotReachableReason() != null) {
                                this.createCDRRecord(RecordStatus.PSI_STATE_NOT_REACHABLE);
                            } else {
                                this.createCDRRecord(RecordStatus.PSI_STATE_SUCCESS);
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getPSSubscriberState() != null) {
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getPSSubscriberState().getNetDetNotReachable() != null) {
                                this.createCDRRecord(RecordStatus.PSI_STATE_NOT_REACHABLE);
                            } else {
                                this.createCDRRecord(RecordStatus.PSI_STATE_SUCCESS);
                            }
                        } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS() != null) {
                            if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS().getGeographicalInformation() != null
                                || provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS().getGeodeticInformation() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_GEO_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_ECGI_SUCCESS);
                                }
                            } else if (provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                                if (gmlcCdrState.isInitialized()) {
                                    this.createCDRRecord(RecordStatus.PSI_TAI_SUCCESS);
                                }
                            }
                        } /*else if (provideSubscriberInfoResponse.getSubscriberInfo().getTimeZone() != null) {
                        // TODO
                    } else if (provideSubscriberInfoResponse.getSubscriberInfo().getDaylightSavingTime() != null) {
                        // TODO
                    }*/
                    } else {
                        if (mapErrorMessage != null) {
                            // PSI error CDR creation
                            if (gmlcCdrState.isInitialized()) {
                                mlpClientErrorMessage = handleRecordAndLocationReportOnMapError(mapErrorMessage, mlpRespResult, psiMsisdn, psiImsi, psiImei,
                                    "PSI", null, nnn, null, gmlcCdrState, false);
                            }
                        }
                    }
                }

                // handle successful retrieval of PSI response
                handlePsiResponse(mlpRespResult, psiResponseParams, psiMsisdn, mlpClientErrorMessage);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onProvideSubscriberInformationResponse=%s", provideSubscriberInfoResponse), e);
            this.createCDRRecord(RecordStatus.PSI_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP PSI response: " + e.getMessage(),
                "PSI", psiMsisdn, psiImsi, psiImei, null, nnn, null, null, null, false);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromMAPDialogMobility(aci);
        }
    }


    /**
     * Location Service Management (LSM) services
     * MAP_SEND_ROUTING_INFO_FOR_LCS (SRILCS) Events
     */
    /**
     * MAP SRILCS Request Event
     */
    public void onSendRoutingInfoForLCSRequest(SendRoutingInfoForLCSRequest sendRoutingInfoForLCSRequest, ActivityContextInterface aci) {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onSendRoutingInfoForLCSRequest = " + sendRoutingInfoForLCSRequest + ", ActivityContextInterface = " + aci);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSendRoutingInfoForLCSRequest=%s", sendRoutingInfoForLCSRequest), e);
        }
    }

    /**
     * MAP SRILCS Response Event
     */
    public void onSendRoutingInfoForLCSResponse(SendRoutingInfoForLCSResponse sendRoutingInfoForLCSResponse, ActivityContextInterface aci, EventContext eventContext) {

        this.logger.fine("\nReceived onSendRoutingInfoForLCSResponse = " + sendRoutingInfoForLCSResponse + ", ActivityContextInterface = " + aci + ", EventContext=" + eventContext);
        MAPErrorMessage mapErrorMessage = this.getErrorResponse();
        String pslMsisdn = null, pslImsi = null, pslImei = null, nnn = null, additionalNumberAddress = null,
                sgsnName = null, sgsnRealm = null, mmeName = null, hlr = null, curlUser, httpRequestType;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        boolean sriLcsOnly;
        DateTime eventTime = DateTime.now();

        try {
            SriLcsResponseParams sriLcsResponseParams = new SriLcsResponseParams();
            MLPResponse.MLPResultType mlpRespResult = null;
            String mlpClientErrorMessage = null;

            if (sendRoutingInfoForLCSResponse != null) {
                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.mapSriLcsCdrInitializer(aci, this.getCDRInterface(), sendRoutingInfoForLCSResponse);
                // Set timer last
                this.setTimer(aci);

                transaction = mobileCoreNetworkTransactions.getTransactionId(sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId());
                if (transaction == null) {
                    throw new Exception();
                }
                pslMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                pslImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                pslImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                httpRequestType = (String) mobileCoreNetworkTransactions.getValue(transaction, "httpRequestType");
                sriLcsOnly = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "sriLcsOnly");
                DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
                gmlcCdrState.setDialogStartTime(dialogStartTime);
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setCurlUser(curlUser);
                    if (dialogStartTime != null && eventTime != null) {
                        Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                        gmlcCdrState.setDialogDuration(dialogDuration);
                    }
                    if (pslImsi != null) {
                        gmlcCdrState.setImsi(new IMSIImpl(pslImsi));
                    } else if (pslMsisdn != null) {
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, pslMsisdn));
                    }
                }

                MessageType tcapMessageType = sendRoutingInfoForLCSResponse.getMAPDialog().getTCAPMessageType();
                if (tcapMessageType == MessageType.Abort) {
                    mobileCoreNetworkTransactions.destroy(transaction);
                    mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                    mlpClientErrorMessage = "TCAP ABORT on SRILCS";
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, pslMsisdn));
                        this.createCDRRecord(RecordStatus.SRISM_TCAP_DIALOG_ABORT);
                    }
                    if (sendRoutingInfoForLCSResponse.getMAPDialog().getRemoteAddress() != null)
                        if (sendRoutingInfoForLCSResponse.getMAPDialog().getRemoteAddress().getGlobalTitle() != null) {
                            hlr = sendRoutingInfoForLCSResponse.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                        }
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "SRILCS", pslMsisdn, pslImsi,
                        pslImei, null, hlr, null, null, null, mlpTriggeredReportingService);
                    return;
                }

                if (sendRoutingInfoForLCSResponse.getTargetMS() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received SubscriberIdentity=" + sendRoutingInfoForLCSResponse.getTargetMS());
                    if (sendRoutingInfoForLCSResponse.getTargetMS().getMSISDN() != null) {
                        sriLcsResponseParams.setMsisdn(sendRoutingInfoForLCSResponse.getTargetMS().getMSISDN());
                        pslMsisdn = sendRoutingInfoForLCSResponse.getTargetMS().getMSISDN().getAddress();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMsisdn(sriLcsResponseParams.getMsisdn());
                        }
                    }
                    if (sendRoutingInfoForLCSResponse.getTargetMS().getIMSI() != null) {
                        sriLcsResponseParams.setImsi(sendRoutingInfoForLCSResponse.getTargetMS().getIMSI());
                        pslImsi = sendRoutingInfoForLCSResponse.getTargetMS().getIMSI().getData();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setImsi(sriLcsResponseParams.getImsi());
                        }
                    }
                } else {
                    this.createCDRRecord(RecordStatus.SRILCS_UNKNOWN_SUBSCRIBER);
                }

                if (sendRoutingInfoForLCSResponse.getLCSLocationInfo() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received LCSLocationInfo: " + sendRoutingInfoForLCSResponse.getLCSLocationInfo());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    sriLcsResponseParams.setLcsLocationInfo(sendRoutingInfoForLCSResponse.getLCSLocationInfo());

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getNetworkNodeNumber() != null) {
                        nnn = sendRoutingInfoForLCSResponse.getLCSLocationInfo().getNetworkNodeNumber().getAddress();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setNetworkNodeNumber(sriLcsResponseParams.getLcsLocationInfo().getNetworkNodeNumber());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getLMSI() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLmsi(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getLMSI());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber() != null) {
                        if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getGprsNodeIndicator()) {
                            if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber() != null) {
                                additionalNumberAddress = sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress();
                            }
                        } else {
                            if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getMSCNumber() != null) {
                                additionalNumberAddress = sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getMSCNumber().getAddress();
                            }
                        }
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAdditionalNumber(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getSupportedLCSCapabilitySets() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsCapabilitySets(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getSupportedLCSCapabilitySets());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsCapabilitySets(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalLCSCapabilitySets());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getGprsNodeIndicator()) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsNodeIndicator(sriLcsResponseParams.getLcsLocationInfo().getGprsNodeIndicator());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getMmeName() != null) {
                        mmeName = Arrays.toString(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getMmeName().getData());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMmeName(sriLcsResponseParams.getLcsLocationInfo().getMmeName());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAaaServerName() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAaaServerName(sriLcsResponseParams.getLcsLocationInfo().getAaaServerName());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getSgsnName() != null) {
                        sgsnName = Arrays.toString(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getSgsnName().getData());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setSgsnName(sriLcsResponseParams.getLcsLocationInfo().getSgsnName());
                        }
                    }

                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getSgsnRealm() != null) {
                        sgsnRealm = Arrays.toString(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getSgsnRealm().getData());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setSgsnRealm(sriLcsResponseParams.getLcsLocationInfo().getSgsnRealm());
                        }
                    }

                } else {
                    this.createCDRRecord(RecordStatus.SRILCS_ABSENT_SUBSCRIBER);
                }

                if (sendRoutingInfoForLCSResponse.getExtensionContainer() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received MAPExtensionContainer=" + sendRoutingInfoForLCSResponse.getExtensionContainer());
                }

                if (sendRoutingInfoForLCSResponse.getVgmlcAddress() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received VGmlcAddress=" + sendRoutingInfoForLCSResponse.getVgmlcAddress());
                    sriLcsResponseParams.setVGmlcAddress(sendRoutingInfoForLCSResponse.getVgmlcAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setvGmlcAddress(sriLcsResponseParams.getVGmlcAddress());
                    }
                }

                if (sendRoutingInfoForLCSResponse.getHGmlcAddress() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received HGmlcAddress=" + sendRoutingInfoForLCSResponse.getHGmlcAddress());
                    sriLcsResponseParams.setHGmlcAddress(sendRoutingInfoForLCSResponse.getHGmlcAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.sethGmlcAddress(sriLcsResponseParams.getHGmlcAddress());
                    }
                }

                if (sendRoutingInfoForLCSResponse.getPprAddress() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received PPRAddress=" + sendRoutingInfoForLCSResponse.getPprAddress());
                    sriLcsResponseParams.setPprAddress(sendRoutingInfoForLCSResponse.getPprAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setPprAddress(sriLcsResponseParams.getPprAddress());
                    }
                }

                if (sendRoutingInfoForLCSResponse.getAdditionalVGmlcAddress() != null) {
                    this.logger.fine("\nonSendRoutingInfoForLCSResponse, received AdditionalVGmlcAddress=" + sendRoutingInfoForLCSResponse.getAdditionalVGmlcAddress());
                    sriLcsResponseParams.setAddVGmlcAddress(sendRoutingInfoForLCSResponse.getAdditionalVGmlcAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setvGmlcAddress(sriLcsResponseParams.getAddVGmlcAddress());
                    }
                }

                this.setSendRoutingInfoForLCSResponse(sendRoutingInfoForLCSResponse);

                mobileCoreNetworkTransactions.setValue(transaction, "sriForLcsResponseParams", sriLcsResponseParams);

                if (this.getSendRoutingInfoForLCSResponse() != null && transaction != null) {

                    if (sriLcsOnly) {
                        // handle retrieval of SRILCS response when SRILCS is the operation requested
                        if (gmlcCdrState.isInitialized()) {
                            if (sendRoutingInfoForLCSResponse.getTargetMS() != null)
                                this.createCDRRecord(RecordStatus.SRILCS_SUCCESS);
                            else
                                this.createCDRRecord(RecordStatus.SRILCS_ERROR);
                        }
                        handleSriResponseValue(mlpRespResult, null, null, sriLcsResponseParams, "SRILCS", pslMsisdn, pslImsi, mlpClientErrorMessage);
                        // destroy transactions if exists, detach and return
                        if (transaction != null)
                            mobileCoreNetworkTransactions.destroy(transaction);
                        detachFromMAPDialogLsm(aci);
                        return;
                    }

                    ISDNAddressString mlcNumber = new ISDNAddressStringImpl(AddressNature.international_number,
                        org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN,
                        this.getGmlcSccpAddress().getGlobalTitle().getDigits());

                    ISDNAddressString msisdnForPsl = null;
                    if (sriLcsResponseParams.getMsisdn() != null) {
                        msisdnForPsl = sriLcsResponseParams.getMsisdn();
                    } else {
                        if (pslMsisdn != null)
                            msisdnForPsl = new ISDNAddressStringImpl(AddressNature.international_number,
                                org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, pslMsisdn);
                    }

                    IMSI imsiForPsl = null;
                    if (sriLcsResponseParams.getImsi() != null) {
                        imsiForPsl = sriLcsResponseParams.getImsi();
                    } else {
                        if (pslImsi != null)
                            imsiForPsl = new IMSIImpl(pslImsi);
                    }

                    // LocationType for PSL composed from HTTP request values
                    LocationType locationType;
                    LocationEstimateType locationEstimateType = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLocationEstimateType") != null)
                        locationEstimateType = LocationEstimateType.valueOf((String) mobileCoreNetworkTransactions.getValue(transaction, "pslLocationEstimateType"));

                    String requestedDeferredLocationEventType = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslDeferredLocationEventType") != null)
                        requestedDeferredLocationEventType = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslDeferredLocationEventType");
                    DeferredLocationEventType deferredLocationEventType = null;
                    if (requestedDeferredLocationEventType != null) {
                        switch (requestedDeferredLocationEventType) {
                            case "available":
                                deferredLocationEventType = new DeferredLocationEventTypeImpl(true, false, false, false, false);
                                break;
                            case "entering":
                                deferredLocationEventType = new DeferredLocationEventTypeImpl(false, true, false, false, false);
                                break;
                            case "leaving":
                                deferredLocationEventType = new DeferredLocationEventTypeImpl(false, false, true, false, false);
                                break;
                            case "inside":
                                deferredLocationEventType = new DeferredLocationEventTypeImpl(false, false, false, true, false);
                                break;
                            case "periodicLDR":
                                deferredLocationEventType = new DeferredLocationEventTypeImpl(false, false, false, false, true);
                                break;
                            default:
                                deferredLocationEventType = null;
                                break;
                        }
                    }

                    locationType = new LocationTypeImpl(locationEstimateType, deferredLocationEventType);

                    // LCSClientID
                    int lcsClientTypeFromCurl = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsClientType");
                    org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType lcsClientType =
                        org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.getLCSClientType(lcsClientTypeFromCurl);
                    LCSClientExternalID lcsClientExternalID = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslClientExternalID") != null) {
                        String lcsClientExternalIdFromCurl = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslClientExternalID");
                        ISDNAddressString lcsClExtIdIsdnAddress = new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, lcsClientExternalIdFromCurl);
                        lcsClientExternalID = new LCSClientExternalIDImpl(lcsClExtIdIsdnAddress, null);
                    }
                    LCSClientInternalID lcsClientInternalID = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslClientInternalID") != null) {
                        Integer lcsClientInternalIdFromCurl = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslClientInternalID");
                        lcsClientInternalID = getLCSClientInternalID(lcsClientInternalIdFromCurl);
                    }
                    LCSClientName lcsClientName = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslClientName") != null) {
                        String lcsClientNameFromCurl = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslClientName");
                        if (mobileCoreNetworkTransactions.getValue(transaction, "pslClientFormatIndicator") != null) {
                            Integer lcsClientFormatIndFromCurl = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslClientFormatIndicator");
                            int cbsDataCodingSchemeCode = 15;
                            CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(cbsDataCodingSchemeCode);
                            Charset gsm8Charset = Charset.defaultCharset();
                            USSDString ussdString = new USSDStringImpl(lcsClientNameFromCurl, cbsDataCodingScheme, gsm8Charset);
                            org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator lcsFormatIndicator;
                            lcsFormatIndicator = org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.getLCSFormatIndicator(lcsClientFormatIndFromCurl);
                            lcsClientName = new LCSClientNameImpl(cbsDataCodingScheme, ussdString, lcsFormatIndicator);
                        }
                    }
                    // lcsClientDialedByMS (O*) : This component shall be present if the MT-LR is associated to either CS call or PS session.
                    AddressString lcsClientDialedByMS = null;  // set to null
                    APN lcsAPN = null; // set to null (Otherwise if the MT-LR is associated with the PS session, the APN-NI is used)
                    LCSRequestorID lcsRequestorID = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslRequestorIdString") != null) {
                        String lcsRequestorIdStringFromCurl = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslRequestorIdString");
                        if (mobileCoreNetworkTransactions.getValue(transaction, "pslRequestorFormatIndicator") != null) {
                            Integer lcsRequestorFormatIndFromCurl = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslRequestorFormatIndicator");
                            int cbsDataCodingSchemeCode = 15;
                            CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(cbsDataCodingSchemeCode);
                            Charset gsm8Charset = Charset.defaultCharset();
                            USSDString ussdString = new USSDStringImpl(lcsRequestorIdStringFromCurl, cbsDataCodingScheme, gsm8Charset);
                            org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator lcsFormatIndicator;
                            lcsFormatIndicator = org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.getLCSFormatIndicator(lcsRequestorFormatIndFromCurl);
                            lcsRequestorID = new LCSRequestorIDImpl(cbsDataCodingScheme, ussdString, lcsFormatIndicator);
                        }
                    }
                    LCSClientID lcsClientID = new LCSClientIDImpl(lcsClientType, lcsClientExternalID, lcsClientInternalID, lcsClientName, lcsClientDialedByMS, lcsAPN, lcsRequestorID);

                    // Privacy Override
                    // This parameter indicates if MS privacy is overridden by the LCS client when the GMLC and VMSC/SGSN for an MTLR are in the same country.
                    boolean privacyOverride = false;

                    // IMEI
                    IMEI imei;
                    if (pslImei != null)
                        imei = mapProvider.getMAPParameterFactory().createIMEI(pslImei);
                    else
                        imei = null;

                    // LCSPriority for PSL composed of HTTP request values
                    LCSPriority lcsPriority = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsPriority") != null)
                        lcsPriority = LCSPriority.valueOf((String) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsPriority"));

                    // LCSQoS for PSL composed from HTTP request values
                    LCSQoS lcsQoS = null;
                    Integer horizontalAccuracy = null, verticalAccuracy = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsHorizontalAccuracy") != null)
                        horizontalAccuracy = Integer.valueOf(String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslLcsHorizontalAccuracy")));
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsVerticalAccuracy") != null)
                        verticalAccuracy = Integer.valueOf(String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslLcsVerticalAccuracy")));
                    Boolean verticalCoordinateRequest = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslVerticalCoordinateRequest") != null)
                        verticalCoordinateRequest = Boolean.parseBoolean(String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslVerticalCoordinateRequest")));
                    ResponseTime responseTime = null;
                    ResponseTimeCategory responseTimeCategory = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslResponseTimeCategory") != null)
                        responseTimeCategory = ResponseTimeCategory.valueOf(String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslResponseTimeCategory")));
                    if (responseTimeCategory != null)
                        responseTime = new ResponseTimeImpl(responseTimeCategory);
                    MAPExtensionContainer pslMapExtensionContainer = null;
                    Boolean velocityRequest = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslVelocityRequested") != null)
                        velocityRequest = Boolean.parseBoolean(String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslVelocityRequested")));
                    Integer qosClass = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslQoSClass") != null)
                        qosClass = Integer.parseInt(String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslQoSClass")));
                    if (horizontalAccuracy != null && verticalAccuracy != null && verticalCoordinateRequest != null && responseTime != null && velocityRequest != null  && qosClass != null)
                        lcsQoS = new LCSQoSImpl(horizontalAccuracy, verticalAccuracy, verticalCoordinateRequest, responseTime, pslMapExtensionContainer,
                            velocityRequest, org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass.getInstance(qosClass));
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setLcsQoS(lcsQoS);
                    }

                    // SupportedGADShapes hardcoded to true for all shapes for now
                    boolean ellipsoidPoint = true, ellipsoidPointWithUncertaintyCircle = true, ellipsoidPointWithUncertaintyEllipse = true,
                        polygon = true, ellipsoidPointWithAltitude = true, ellipsoidPointWithAltitudeAndUncertaintyEllipsoid = true,
                        ellipsoidArc = true;
                    SupportedGADShapes supportedGADShapes = new SupportedGADShapesImpl(ellipsoidPoint, ellipsoidPointWithUncertaintyCircle,
                        ellipsoidPointWithUncertaintyEllipse, polygon, ellipsoidPointWithAltitude, ellipsoidPointWithAltitudeAndUncertaintyEllipsoid, ellipsoidArc);

                    // LCSCodeword
                    LCSCodeword lcsCodeword = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsCodeword") != null) {
                        String lcsCodeW = null;
                        if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsCodeword") != null)
                            lcsCodeW = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsCodeword");
                        int cbsDataCodingSchemeCode = 15;
                        CBSDataCodingScheme cbsDataCodingScheme = new CBSDataCodingSchemeImpl(cbsDataCodingSchemeCode);
                        Charset gsm8Charset = Charset.defaultCharset();
                        USSDString ussdString = new USSDStringImpl(lcsCodeW, cbsDataCodingScheme, gsm8Charset);
                        lcsCodeword = new LCSCodewordImpl(cbsDataCodingScheme, ussdString);
                    }

                    // LCSPrivacyCheck hardcoded to nul for now
                    LCSPrivacyCheck lcsPrivacyCheck = null;

                    // AreaEventInfo for PSL composed from HTTP request values
                    ArrayList<Area> areaList = new ArrayList<>();
                    AreaType areaType = null;
                    AreaIdentification areaIdentification = null;
                    Area area = null;
                    AreaDefinition areaDefinition = null;
                    OccurrenceInfo occurrenceInfo = null;
                    Integer intervalTime = null;
                    AreaEventInfo areaEventInfo = null;
                    String[] areaIdArray;
                    String areaIdStr;
                    Integer[] areaIdParams;
                    PlmnIdImpl plmnId;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslAreaId") != null) {
                        areaIdStr = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslAreaId");
                        areaIdArray = areaIdStr.split("-");
                        areaIdParams = setAreaIdParams(areaIdArray, (String) mobileCoreNetworkTransactions.getValue(transaction, "pslAreaType"));
                        if (mobileCoreNetworkTransactions.getValue(transaction, "pslAreaType") != null) {
                            areaType = AreaType.valueOf((String) mobileCoreNetworkTransactions.getValue(transaction, "pslAreaType"));
                            if (areaType.equals(AreaType.countryCode)) {
                                areaIdentification = new AreaIdentificationImpl(parseTBCD(areaIdArray[0]));
                            } else if (areaType.equals(AreaType.plmnId)) {
                                plmnId = new PlmnIdImpl(areaIdParams[0], areaIdParams[1]);
                                areaIdentification = new AreaIdentificationImpl(plmnId.getData());
                            } else if (areaType.equals(AreaType.locationAreaId)) {
                                areaIdentification = new AreaIdentificationImpl(AreaType.locationAreaId, areaIdParams[0], areaIdParams[1], areaIdParams[2], 0);
                            } else if (areaType.equals(AreaType.routingAreaId)) {
                                areaIdentification = new AreaIdentificationImpl(AreaType.routingAreaId, areaIdParams[0], areaIdParams[1], areaIdParams[2], areaIdParams[3]);
                            } else if (areaType.equals(AreaType.cellGlobalId)) {
                                areaIdentification = new AreaIdentificationImpl(AreaType.cellGlobalId, areaIdParams[0], areaIdParams[1], areaIdParams[2], areaIdParams[3]);
                            } else if (areaType.equals(AreaType.utranCellId)) {
                                areaIdentification = new AreaIdentificationImpl(AreaType.utranCellId, areaIdParams[0], areaIdParams[1], -1, areaIdParams[3]);
                            }
                        }
                    }
                    if (areaType != null && areaIdentification != null)
                        area = new AreaImpl(areaType, areaIdentification);
                    if (area != null)
                        areaList.add(area);
                    if (areaList != null)
                        areaDefinition = new AreaDefinitionImpl(areaList);
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslOccurrenceInfo") != null)
                        occurrenceInfo = OccurrenceInfo.valueOf((String) mobileCoreNetworkTransactions.getValue(transaction, "pslOccurrenceInfo"));
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslIntervalTime") != null)
                        intervalTime = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslIntervalTime");
                    if (areaDefinition != null && occurrenceInfo != null)
                        areaEventInfo = new AreaEventInfoImpl(areaDefinition, occurrenceInfo, intervalTime);

                    // PeriodicLDRInfo for PSL composed from HTTP request values
                    PeriodicLDRInfo periodicLDRInfo = null;
                    if (deferredLocationEventType != null) {
                        // FIXME with ReportingOptionMilliseconds
                        if (deferredLocationEventType.getPeriodicLDR()) {
                            if (mobileCoreNetworkTransactions.getValue(transaction, "pslReportingAmount") != null &&
                                mobileCoreNetworkTransactions.getValue(transaction, "pslReportingInterval") != null)
                                periodicLDRInfo = new PeriodicLDRInfoImpl((Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslReportingAmount"),
                                    (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslReportingInterval"), null);
                        }
                    }

                    // moLrShortCircuitIndicator hardcoded to false for now
                    boolean moLrShortCircuitIndicator = false;

                    // ReportingPLMNList
                    ReportingPLMNList reportingPLMNList = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslPLMNIdList") != null) {
                        String vPlmnId = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslPLMNIdList");
                        String[] vPlmnIdArray = vPlmnId.split("-");
                        PlmnId visitedPlmnId = new PlmnIdImpl(Integer.parseInt(vPlmnIdArray[0]), Integer.parseInt(vPlmnIdArray[1]));
                        Integer ranTechInd = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslVisitedPLMNIdRAN");
                        RANTechnology ranTechnology = RANTechnology.gsm;
                        if (ranTechInd != null) {
                            if (ranTechInd == 1)
                                ranTechnology = RANTechnology.umts;
                        }
                        Integer ranPeriodicLocationSupportInd = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslPeriodicLocationSupportIndicator");
                        boolean ranPeriodicLocationSupport = false;
                        if (ranPeriodicLocationSupportInd != null) {
                            if (ranPeriodicLocationSupportInd == 1)
                                ranPeriodicLocationSupport = true;
                        }
                        ReportingPLMN reportingPLMN = new ReportingPLMNImpl(visitedPlmnId, ranTechnology, ranPeriodicLocationSupport);
                        ArrayList<ReportingPLMN> plmnList = new ArrayList<>();
                        plmnList.add(reportingPLMN);
                        Integer plmnListPrioritizedInd = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslPrioritizedListIndicator");
                        boolean plmnListPrioritized = false;
                        if (plmnListPrioritizedInd != null) {
                            if (plmnListPrioritizedInd == 1)
                                plmnListPrioritized = true;
                        }
                        reportingPLMNList = new ReportingPLMNListImpl(plmnListPrioritized, plmnList);
                    }

                    Integer pslLcsServiceTypeID = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsServiceTypeID") != null)
                        pslLcsServiceTypeID = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsServiceTypeID");

                    Integer pslLcsReferenceNumber = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber") != null)
                        pslLcsReferenceNumber = referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");

                    String slrCallbackUrl = null;
                    if (mobileCoreNetworkTransactions.getValue(transaction, "slrCallbackUrl") != null)
                        slrCallbackUrl = (String) mobileCoreNetworkTransactions.getValue(transaction, "slrCallbackUrl");

                    Integer pslReferenceNumber = null;
                    if (deferredLocationEventType != null) {
                        httpSubscriberLocationReport = getHttpSubscriberLocationReport();
                        boolean mlp = false;
                        if (httpRequestType != null) {
                            if (httpRequestType.equalsIgnoreCase("MLP"))
                                mlp = true;
                        }
                        pslReferenceNumber = httpSubscriberLocationReport.Register(pslLcsReferenceNumber, slrCallbackUrl, null, mlp, curlUser);
                        if (this.logger.isFineEnabled()) {
                            logger.fine(String.format("Sending MAP PSL request with LCS-ReferenceNumber %d from HTTP request clientReferenceNumber# %d and callback URL: '%s'",
                                pslReferenceNumber, pslLcsReferenceNumber, slrCallbackUrl));
                        }
                        httpSubscriberLocationReport.closeMongo();
                    }

                    AddressString originAddressString, destinationAddressString;
                    originAddressString = destinationAddressString = null;

                    SccpAddress pslDestinationSccpAddress = new SccpAddressImpl();

                    // If the Network Node Number and Additional Number are received in the GMLC,
                    // the Network Node Number is used in preference to the Additional Number.
                    if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getNetworkNodeNumber() != null) {
                        sendRoutingInfoForLCSResponse.getLCSLocationInfo().getNetworkNodeNumber().getNumberingPlan();
                        if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getGprsNodeIndicator())
                            pslDestinationSccpAddress = getSgsnSccpAddress(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getNetworkNodeNumber().getAddress());
                        else
                            pslDestinationSccpAddress = getMscSccpAddress(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getNetworkNodeNumber().getAddress());
                    } else {
                        if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber() == null) {
                            if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber() != null) {
                                pslDestinationSccpAddress = getSgsnSccpAddress(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress());
                            } else if (sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getMSCNumber() != null) {
                                pslDestinationSccpAddress = getMscSccpAddress(sendRoutingInfoForLCSResponse.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress());
                            }
                        }
                    }

                    MAPDialogLsm mapDialogLsmPsl = this.mapProvider.getMAPServiceLsm().createNewDialog(this.getMAPPslSlrApplicationContext(),
                        this.getGmlcSccpAddress(), originAddressString, pslDestinationSccpAddress, destinationAddressString);

                    // We are selecting the IMSI over the MSISDN if present for MAP PSL, although as per 3GPP TS 29.002 v18.0.0 both could be included:
                    // The IMSI is provided to identify the target MS. At least one of the IMSI or MSISDN is mandatory.
                    // The MSISDN is provided to identify the target MS. At least one of the IMSI or MSISDN is mandatory.
                    if (imsiForPsl != null) {
                        pslImsi = imsiForPsl.getData();
                        msisdnForPsl = null;
                    }

                    // 3GPP TS 29.002 v18.0.0
                    // H-GMLC address
                    // The parameter shall be included if a deferred MT-LR procedure is performed for a
                    // UE available event, an area event or a periodic positioning event.

                    mapDialogLsmPsl.addProvideSubscriberLocationRequest(locationType, mlcNumber, lcsClientID, privacyOverride,
                        imsiForPsl, msisdnForPsl, sriLcsResponseParams.getLmsi(), imei, lcsPriority, lcsQoS, sendRoutingInfoForLCSResponse.getExtensionContainer(),
                        supportedGADShapes, pslReferenceNumber, pslLcsServiceTypeID, lcsCodeword, lcsPrivacyCheck,
                        areaEventInfo, deferredLocationEventType != null ? sendRoutingInfoForLCSResponse.getHGmlcAddress() : null, moLrShortCircuitIndicator,
                        periodicLDRInfo, reportingPLMNList);

                    mobileCoreNetworkTransactions.setValue(transaction, "pslReferenceNumber", pslReferenceNumber);
                    mobileCoreNetworkTransactions.setValue(transaction, "pslNNN", nnn);
                    mobileCoreNetworkTransactions.setValue(transaction, "pslAddNum", additionalNumberAddress);
                    mobileCoreNetworkTransactions.setValue(transaction, "pslImsi", pslImsi);
                    mobileCoreNetworkTransactions.setValue(transaction, "pslImei", pslImei);
                    mobileCoreNetworkTransactions.setValue(transaction, "lcsClientId", lcsClientID);
                    mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
                    mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", mlpTriggeredReportingService);
                    mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", dialogStartTime);
                    mobileCoreNetworkTransactions.unsetDialog(sendRoutingInfoForLCSResponse.getMAPDialog().getLocalDialogId());
                    mobileCoreNetworkTransactions.setDialog(transaction, mapDialogLsmPsl.getLocalDialogId());

                    // Keep ACI in across MAP dialog for PSL
                    ActivityContextInterface pslDialogACI = this.mapAcif.getActivityContextInterface(mapDialogLsmPsl);
                    pslDialogACI.attach(this.sbbContext.getSbbLocalObject());

                    // ProvideSubscriberLocationRequest is now composed by values taken from SRILCS response and HTTP request
                    // Send PSL
                    mapDialogLsmPsl.send();

                    // set new timer for the PSL request/response cycle
                    TimerID timerID = timerFacility.setTimer(pslDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                    mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

                } else {
                    // SRILCS Error CDR creation
                    if (mapErrorMessage != null) {
                        if (gmlcCdrState.isInitialized()) {
                            if (transaction != null) {
                                referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                                mobileCoreNetworkTransactions.destroy(transaction);
                            }
                            mlpClientErrorMessage = handleRecordAndLocationReportOnMapError(mapErrorMessage, mlpRespResult, pslMsisdn, pslImsi, pslImei,
                                "SRILCS", referenceNumber, nnn, additionalNumberAddress, gmlcCdrState, mlpTriggeredReportingService);
                        }
                    }
                }
            }
        } catch (MAPException e) {
            logger.severe(String.format("MAPException while trying to process SendRoutingInfoForLCSResponse=%s", sendRoutingInfoForLCSResponse), e);
            logger.severe("MAP error message when processing onSendRoutingInfoForLCSResponse: " + mapErrorMessage);
            this.createCDRRecord(RecordStatus.SRILCS_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP SRILCS response: " + e.getMessage(),
                "SRILCS", pslMsisdn, pslImsi, pslImei, referenceNumber, nnn, additionalNumberAddress, sgsnName != null ? sgsnName : mmeName, sgsnRealm, mlpTriggeredReportingService);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process SendRoutingInfoForLCSResponse=%s", sendRoutingInfoForLCSResponse), e);
            this.createCDRRecord(RecordStatus.SRILCS_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP SRILCS response: " + e.getMessage(),
                "SRILCS", pslMsisdn, pslImsi, pslImei, referenceNumber, nnn, additionalNumberAddress, sgsnName != null ? sgsnName : mmeName, sgsnRealm, mlpTriggeredReportingService);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromMAPDialogLsm(aci);
        }
    }

    /**
     * Location Service Management (LSM) services
     * MAP_PROVIDE_SUBSCRIBER_LOCATION (PSL) Events
     */

    /**
     * MAP PSL Request Event
     */
    public void onProvideSubscriberLocationRequest(ProvideSubscriberLocationRequest provideSubscriberLocationRequest, ActivityContextInterface aci) {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onProvideSubscriberLocationRequest = " + provideSubscriberLocationRequest + ", ActivityContextInterface = " + aci);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onProvideSubscriberLocationRequest=%s", provideSubscriberLocationRequest), e);
        }
    }

    /**
     * MAP PSL Response Event
     */
    public void onProvideSubscriberLocationResponse(ProvideSubscriberLocationResponse provideSubscriberLocationResponse, ActivityContextInterface aci) {

        this.logger.fine("\nReceived onProvideSubscriberLocationResponse = " + provideSubscriberLocationResponse + ", ActivityContextInterface = " + aci);
        MAPErrorMessage mapErrorMessage = this.getErrorResponse();
        this.setProvideSubscriberLocationResponse(provideSubscriberLocationResponse);
        Long transaction = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        String pslMsisdn = null, pslImsi = null, pslImei = null, pslNnn = null, pslAddNumber = null, curlUser;
        DateTime eventTime = DateTime.now();

        try {
            if (provideSubscriberLocationResponse != null) {
                PslResponseParams pslResponseParams = new PslResponseParams();
                MLPResponse.MLPResultType mlpRespResult = null;
                String mlpClientErrorMessage = null;

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.mapPSLCdrInitializer(aci, this.getCDRInterface(), provideSubscriberLocationResponse);
                // Set timer last
                this.setTimer(aci);

                transaction = mobileCoreNetworkTransactions.getTransactionId(provideSubscriberLocationResponse.getMAPDialog().getLocalDialogId());
                if (transaction == null) {
                    throw new Exception();
                }
                pslMsisdn = String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn"));
                pslImsi = String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslImsi"));
                if (mobileCoreNetworkTransactions.getValue(transaction, "pslImei") != null)
                    pslImei = String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslImei"));
                LCSClientID lcsClientID = (LCSClientID) mobileCoreNetworkTransactions.getValue(transaction, "lcsClientId");
                referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                pslNnn = String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslNNN"));
                pslAddNumber = String.valueOf(mobileCoreNetworkTransactions.getValue(transaction, "pslAddNum"));
                curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
                gmlcCdrState.setDialogStartTime(dialogStartTime);
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setCurlUser(curlUser);
                    if (dialogStartTime != null) {
                        Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                        gmlcCdrState.setDialogDuration(dialogDuration);
                    }
                    if (pslImsi != null) {
                        gmlcCdrState.setImsi(new IMSIImpl(pslImsi));
                    } else if (pslMsisdn != null) {
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, pslMsisdn));
                    }
                    if (pslImei != null) {
                        gmlcCdrState.setImei(new IMEIImpl(pslImei));
                    }
                    if (lcsClientID != null) {
                        gmlcCdrState.setLcsClientID(lcsClientID);
                    }
                }

                MessageType tcapMessageType = provideSubscriberLocationResponse.getMAPDialog().getTCAPMessageType();
                if (tcapMessageType == MessageType.Abort) {
                    mobileCoreNetworkTransactions.destroy(transaction);
                    mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                    mlpClientErrorMessage = "TCAP ABORT on PSL";
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, pslMsisdn));
                        IMSI imsi = new IMSIImpl(pslImsi);
                        gmlcCdrState.setImsi(imsi);
                        this.createCDRRecord(RecordStatus.SRISM_TCAP_DIALOG_ABORT);
                    }
                    if (provideSubscriberLocationResponse.getMAPDialog().getRemoteAddress() != null)
                        if (provideSubscriberLocationResponse.getMAPDialog().getRemoteAddress().getGlobalTitle() != null) {
                            pslNnn = provideSubscriberLocationResponse.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                        }
                    this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, "PSL", pslMsisdn, pslImsi, pslImei,
                        referenceNumber, pslNnn, pslAddNumber, null, null, mlpTriggeredReportingService);
                    return;
                }

                if (provideSubscriberLocationResponse.getLocationEstimate() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse: LocationEstimate=" + provideSubscriberLocationResponse.getLocationEstimate());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    if (provideSubscriberLocationResponse.getLocationEstimate().getTypeOfShape() != null) {
                        if (provideSubscriberLocationResponse.getLocationEstimate().getTypeOfShape() != TypeOfShape.Polygon) {
                            pslResponseParams.setLocationEstimate(provideSubscriberLocationResponse.getLocationEstimate());
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setLocationEstimate(pslResponseParams.getLocationEstimate());
                            }
                        } else {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setTypeOfShape(provideSubscriberLocationResponse.getLocationEstimate().getTypeOfShape());
                            }
                        }
                    }
                }

                if (provideSubscriberLocationResponse.getAgeOfLocationEstimate() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, AgeOfLocationInformation=" + provideSubscriberLocationResponse.getAgeOfLocationEstimate());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setAgeOfLocationEstimate(provideSubscriberLocationResponse.getAgeOfLocationEstimate());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setAgeOfLocationEstimate(pslResponseParams.getAgeOfLocationEstimate());
                    }
                }

                if (provideSubscriberLocationResponse.getExtensionContainer() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, MAPExtensionContainer=" + provideSubscriberLocationResponse.getExtensionContainer());
                }

                if (provideSubscriberLocationResponse.getAdditionalLocationEstimate() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, AdditionalLocationEstimate=" + provideSubscriberLocationResponse.getAdditionalLocationEstimate());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setAdditionalLocationEstimate(provideSubscriberLocationResponse.getAdditionalLocationEstimate());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setAdditionalLocationEstimate(pslResponseParams.getAdditionalLocationEstimate());
                    }
                }

                if (provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator()) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, Deferred MT-LR ResponseIndicator=" + provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setDeferredMTLRResponseIndicator(provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCellGlobalIdOrServiceAreaIdOrLAI(pslResponseParams.getCellGlobalIdOrServiceAreaIdOrLAI());
                        gmlcCdrState.setDeferredMTLRResponseIndicator(provideSubscriberLocationResponse.getDeferredMTLRResponseIndicator());
                    }
                }

                if (provideSubscriberLocationResponse.getGeranPositioningData() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, GeranPositioningDataInformation=" + provideSubscriberLocationResponse.getGeranPositioningData());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setGeranPositioningDataInformation(provideSubscriberLocationResponse.getGeranPositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setGeranPositioningDataInformation(pslResponseParams.getGeranPositioningDataInformation());
                    }
                }

                if (provideSubscriberLocationResponse.getUtranPositioningData() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, UtranPositioningDataInformation=" + provideSubscriberLocationResponse.getUtranPositioningData());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setUtranPositioningDataInfo(provideSubscriberLocationResponse.getUtranPositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranPositioningDataInfo(pslResponseParams.getUtranPositioningDataInfo());
                    }
                }

                if (provideSubscriberLocationResponse.getCellIdOrSai() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, CellGlobalIdOrServiceAreaIdOrLAI=" + provideSubscriberLocationResponse.getCellIdOrSai());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    if (provideSubscriberLocationResponse.getCellIdOrSai().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                        pslResponseParams.setCellGlobalIdOrServiceAreaIdOrLAI(provideSubscriberLocationResponse.getCellIdOrSai());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setCellGlobalIdOrServiceAreaIdOrLAI(pslResponseParams.getCellGlobalIdOrServiceAreaIdOrLAI());
                        }
                    } else if (provideSubscriberLocationResponse.getCellIdOrSai().getLAIFixedLength() != null) {
                        pslResponseParams.setCellGlobalIdOrServiceAreaIdOrLAI(provideSubscriberLocationResponse.getCellIdOrSai());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setCellGlobalIdOrServiceAreaIdOrLAI(pslResponseParams.getCellGlobalIdOrServiceAreaIdOrLAI());
                        }
                    }
                }

                if (provideSubscriberLocationResponse.getSaiPresent()) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, saiPresent=" + provideSubscriberLocationResponse.getSaiPresent());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setSaiPresent(provideSubscriberLocationResponse.getSaiPresent());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setSaiPresent(provideSubscriberLocationResponse.getSaiPresent());
                    }
                }

                if (provideSubscriberLocationResponse.getAccuracyFulfilmentIndicator() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, AccuracyFulfilmentIndicator=" + provideSubscriberLocationResponse.getAccuracyFulfilmentIndicator());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setAccuracyFulfilmentIndicator(provideSubscriberLocationResponse.getAccuracyFulfilmentIndicator());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setAccuracyFulfilmentIndicator(pslResponseParams.getAccuracyFulfilmentIndicator());
                    }
                }

                if (provideSubscriberLocationResponse.getVelocityEstimate() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, VelocityEstimate=" + provideSubscriberLocationResponse.getVelocityEstimate());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setVelocityEstimate(provideSubscriberLocationResponse.getVelocityEstimate());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setVelocityEstimate(pslResponseParams.getVelocityEstimate());
                    }
                }

                if (provideSubscriberLocationResponse.getMoLrShortCircuitIndicator()) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, MoLrShortCircuitIndicator=" + provideSubscriberLocationResponse.getMoLrShortCircuitIndicator());
                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    pslResponseParams.setMoLrShortCircuitIndicator(provideSubscriberLocationResponse.getMoLrShortCircuitIndicator());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMoLrShortCircuitIndicator(provideSubscriberLocationResponse.getMoLrShortCircuitIndicator());
                    }
                }

                if (provideSubscriberLocationResponse.getGeranGANSSpositioningData() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, GERAN GANSS positioning data=" + provideSubscriberLocationResponse.getGeranGANSSpositioningData());
                    pslResponseParams.setGeranGANSSpositioningData(provideSubscriberLocationResponse.getGeranGANSSpositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setGeranGANSSpositioningData(provideSubscriberLocationResponse.getGeranGANSSpositioningData());
                    }
                }

                if (provideSubscriberLocationResponse.getUtranGANSSpositioningData() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, UTRAN GANSS positioning data=" + provideSubscriberLocationResponse.getUtranGANSSpositioningData());
                    pslResponseParams.setUtranGANSSpositioningData(provideSubscriberLocationResponse.getUtranGANSSpositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranGANSSpositioningData(provideSubscriberLocationResponse.getUtranGANSSpositioningData());
                    }
                }

                if (provideSubscriberLocationResponse.getTargetServingNodeForHandover() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, targetServingNodeForHandover=" + provideSubscriberLocationResponse.getTargetServingNodeForHandover());
                    pslResponseParams.setTargetServingNodeForHandover(provideSubscriberLocationResponse.getTargetServingNodeForHandover());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setServingNodeAddress(provideSubscriberLocationResponse.getTargetServingNodeForHandover());
                    }
                }

                if (provideSubscriberLocationResponse.getUtranAdditionalPositioningData() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, UTRAN additional positioning data=" + provideSubscriberLocationResponse.getUtranAdditionalPositioningData());
                    pslResponseParams.setUtranAdditionalPositioningData(provideSubscriberLocationResponse.getUtranAdditionalPositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranAdditionalPositioningData(provideSubscriberLocationResponse.getUtranAdditionalPositioningData());
                    }
                }

                if (provideSubscriberLocationResponse.getUtranBaroPressureMeas() != null) {
                    this.logger.fine("\nonProvideSubscriberLocationResponse, UTRAN barometric pressure measurement=" + provideSubscriberLocationResponse.getUtranBaroPressureMeas());
                    pslResponseParams.setUtranBaroPressureMeas(provideSubscriberLocationResponse.getUtranBaroPressureMeas());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranBaroPressureMeas(provideSubscriberLocationResponse.getUtranBaroPressureMeas());
                    }
                }

                if (provideSubscriberLocationResponse.getUtranCivicAddress() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, UTRAN civic address=" + provideSubscriberLocationResponse.getUtranCivicAddress());
                    pslResponseParams.setUtranCivicAddress(provideSubscriberLocationResponse.getUtranCivicAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranCivicAddress(provideSubscriberLocationResponse.getUtranCivicAddress());
                    }
                }

                SriLcsResponseParams sriForLcsResponse = (SriLcsResponseParams) mobileCoreNetworkTransactions.getValue(transaction, "sriForLcsResponseParams");

                sriForLcsResponse.setPslMsisdn(pslMsisdn);
                sriForLcsResponse.setPslImsi(pslImsi);
                referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                String pslLcsReferenceNumber = (String.valueOf(referenceNumber));
                sriForLcsResponse.setPslLcsReferenceNumber(Integer.parseInt(pslLcsReferenceNumber));
                Integer pslReferenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslReferenceNumber");
                sriForLcsResponse.setPslReferenceNumber(pslReferenceNumber);
                mobileCoreNetworkTransactions.destroy(transaction);
                if (gmlcCdrState.isInitialized()) {
                    if (pslImsi != null) {
                        IMSI imsi = new IMSIImpl(pslImsi);
                        gmlcCdrState.setImsi(imsi);
                    }
                    gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, pslMsisdn));
                    gmlcCdrState.setClientReferenceNumber(referenceNumber);
                    gmlcCdrState.setLcsReferenceNumber(pslReferenceNumber);
                }

                // PSL CDR creation
                if (provideSubscriberLocationResponse.getLocationEstimate() != null || provideSubscriberLocationResponse.getGeranPositioningData() != null || provideSubscriberLocationResponse.getUtranPositioningData() != null
                    || provideSubscriberLocationResponse.getAdditionalLocationEstimate() != null || provideSubscriberLocationResponse.getCellIdOrSai() != null || provideSubscriberLocationResponse.getVelocityEstimate() != null
                    || provideSubscriberLocationResponse.getGeranGANSSpositioningData() != null || provideSubscriberLocationResponse.getUtranGANSSpositioningData() != null
                    || provideSubscriberLocationResponse.getTargetServingNodeForHandover() != null || provideSubscriberLocationResponse.getUtranAdditionalPositioningData() != null
                    || provideSubscriberLocationResponse.getUtranBaroPressureMeas() != null || provideSubscriberLocationResponse.getUtranCivicAddress() != null) {
                    if (gmlcCdrState.isInitialized()) {
                        this.createCDRRecord(RecordStatus.PSL_SUCCESS);
                    } else {
                        if (mapErrorMessage != null) {
                            if (gmlcCdrState.isInitialized()) {
                                mlpClientErrorMessage = handleRecordAndLocationReportOnMapError(mapErrorMessage, mlpRespResult,
                                    pslMsisdn, pslImsi, pslImei, "PSL", referenceNumber, pslNnn, pslAddNumber, gmlcCdrState, mlpTriggeredReportingService);
                            }
                        }
                    }
                }

                // Handle successful retrieval of response to subscriber's location request (PSL response) info
                this.handleLsmLocationResponse(mlpRespResult, sriForLcsResponse, pslResponseParams, pslImei, mlpClientErrorMessage, mlpTriggeredReportingService);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onProvideSubscriberLocationResponse=%s", provideSubscriberLocationResponse), e);
            this.createCDRRecord(RecordStatus.PSL_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP PSL response: " + e.getMessage(),
                "PSL", pslMsisdn, pslImsi, pslImei, referenceNumber, pslNnn, pslAddNumber, null, null, mlpTriggeredReportingService);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromMAPDialogLsm(aci);
        }
    }


    /**
     * Location Service Management (LSM) services
     * MAP_SUBSCRIBER_LOCATION_REPORT (SLR) Events
     */

    /**
     * MAP SLR Request Event
     */
    public void onSubscriberLocationReportRequest(SubscriberLocationReportRequest slrEvent, ActivityContextInterface aci) {

        this.logger.fine("\nReceived onSubscriberLocationReportRequest = " + slrEvent + ", ActivityContextInterface = " + aci);
        MAPErrorMessage mapErrorMessage = this.getErrorResponse();

        try {
            if (slrEvent != null) {
                SlrRequestParams slrRequestParams = new SlrRequestParams();

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.mapSLRCdrInitializer(aci, this.getCDRInterface(), slrEvent, slrEvent.getMSISDN());
                // Set timer last
                this.setTimer(aci);

                if (slrEvent.getLCSEvent() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, LCSEvent=" + slrEvent.getLCSEvent());
                    slrRequestParams.setLcsEvent(slrEvent.getLCSEvent());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setLcsEvent(slrRequestParams.getLcsEvent());
                    }
                }

                if (slrEvent.getLCSClientID() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, LCSClientID=" + slrEvent.getLCSClientID());
                    slrRequestParams.setLcsClientID(slrEvent.getLCSClientID());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setLcsClientID(slrRequestParams.getLcsClientID());
                    }
                }

                if (slrEvent.getLCSLocationInfo() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, LCSLocationInfo=" + slrEvent.getLCSLocationInfo());
                    slrRequestParams.setLcsLocationInfo(slrEvent.getLCSLocationInfo());
                    if (slrEvent.getLCSLocationInfo().getNetworkNodeNumber() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setNetworkNodeNumber(slrEvent.getLCSLocationInfo().getNetworkNodeNumber());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getLMSI() != null) {
                        slrRequestParams.setLmsi(slrEvent.getLCSLocationInfo().getLMSI());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLmsi(slrRequestParams.getLmsi());
                        }
                    }
                    // TODO ???
                    // if (event.getLCSLocationInfo().getExtensionContainer() != null) {}
                    if (slrEvent.getLCSLocationInfo().getGprsNodeIndicator()) {
                        slrRequestParams.setGprsNodeIndicator(slrEvent.getLCSLocationInfo().getGprsNodeIndicator());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsNodeIndicator(true);
                        }
                    } else {
                        slrRequestParams.setGprsNodeIndicator(false);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setGprsNodeIndicator(false);
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getAdditionalNumber() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAdditionalNumber(slrEvent.getLCSLocationInfo().getAdditionalNumber());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getSupportedLCSCapabilitySets() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsCapabilitySets(slrEvent.getLCSLocationInfo().getSupportedLCSCapabilitySets());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsCapabilitySets(slrEvent.getLCSLocationInfo().getAdditionalLCSCapabilitySets());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getMmeName() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMmeName(slrEvent.getLCSLocationInfo().getMmeName());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getAaaServerName() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAaaServerName(slrEvent.getLCSLocationInfo().getAaaServerName());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getSgsnName() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setSgsnName(slrEvent.getLCSLocationInfo().getSgsnName());
                        }
                    }
                    if (slrEvent.getLCSLocationInfo().getSgsnRealm() != null) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setSgsnRealm(slrEvent.getLCSLocationInfo().getSgsnRealm());
                        }
                    }

                }

                if (slrEvent.getMSISDN() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, MSISDN=" + slrEvent.getMSISDN());
                    slrRequestParams.setMsisdn(slrEvent.getMSISDN());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMsisdn(slrRequestParams.getMsisdn());
                    }
                }

                if (slrEvent.getIMSI() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, IMSI=" + slrEvent.getIMSI());
                    slrRequestParams.setImsi(slrEvent.getIMSI());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setImsi(slrRequestParams.getImsi());
                    }
                }

                if (slrEvent.getIMEI() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, IMEI=" + slrEvent.getIMEI());
                    slrRequestParams.setImei(slrEvent.getIMEI());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setImei(slrRequestParams.getImei());
                    }
                }

                if (slrEvent.getNaESRD() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, NaESRD=" + slrEvent.getNaESRD());
                    slrRequestParams.setNaESRD(slrEvent.getNaESRD());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setNaESRD(slrEvent.getNaESRD());
                    }
                }

                if (slrEvent.getNaESRK() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, NaESRK=" + slrEvent.getNaESRK());
                    slrRequestParams.setNaESRK(slrEvent.getNaESRK());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setNaESRK(slrEvent.getNaESRK());
                    }
                }

                if (slrEvent.getLocationEstimate() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest: "
                        + "received LocationEstimate=" + slrEvent.getLocationEstimate());
                    if (slrEvent.getLocationEstimate().getTypeOfShape() != null) {
                        if (slrEvent.getLocationEstimate().getTypeOfShape() != TypeOfShape.Polygon) {
                            slrRequestParams.setLocationEstimate(slrEvent.getLocationEstimate());
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setLocationEstimate(slrRequestParams.getLocationEstimate());
                            }
                        } else {
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setTypeOfShape(slrEvent.getLocationEstimate().getTypeOfShape());
                            }
                        }
                    }
                }

                if (slrEvent.getAgeOfLocationEstimate() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, AgeOfLocationInformation=" + slrEvent.getAgeOfLocationEstimate());
                    slrRequestParams.setAgeOfLocationEstimate(slrEvent.getAgeOfLocationEstimate());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setAgeOfLocationEstimate(slrRequestParams.getAgeOfLocationEstimate());
                    }
                }

                if (slrEvent.getSLRArgExtensionContainer() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, slrArgExtensionContainer=" + slrEvent.getSLRArgExtensionContainer());
                    slrRequestParams.setSlrArgExtensionContainer(slrEvent.getSLRArgExtensionContainer());
                    if (gmlcCdrState.isInitialized()) {
                        if (slrEvent.getSLRArgExtensionContainer().getSlrArgPcsExtensions() != null) {
                            gmlcCdrState.setNaEsrkRequest(slrEvent.getSLRArgExtensionContainer().getSlrArgPcsExtensions().getNaEsrkRequest());
                        }
                    }
                }

                if (slrEvent.getAdditionalLocationEstimate() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, "
                        + " AdditionalLocationEstimate=" + slrEvent.getAdditionalLocationEstimate());
                    slrRequestParams.setAdditionalLocationEstimate(slrEvent.getAdditionalLocationEstimate());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setAdditionalLocationEstimate(slrRequestParams.getAdditionalLocationEstimate());
                    }
                }

                if (slrEvent.getDeferredmtlrData() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, DeferredMTLRData=" + slrEvent.getDeferredmtlrData());
                    slrRequestParams.setDeferredmtlrData(slrEvent.getDeferredmtlrData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setDeferredmtlrData(slrRequestParams.getDeferredmtlrData());
                    }
                }

                if (slrEvent.getLCSReferenceNumber() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, LCS Reference Number=" + slrEvent.getLCSReferenceNumber());
                    slrRequestParams.setLcsReferenceNumber(slrEvent.getLCSReferenceNumber());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setLcsReferenceNumber(slrRequestParams.getLcsReferenceNumber());
                    }
                }

                if (slrEvent.getGeranPositioningData() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, GeranPositioningDataInformation=" + slrEvent.getGeranPositioningData());
                    slrRequestParams.setGeranPositioningDataInformation(slrEvent.getGeranPositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setGeranPositioningDataInformation(slrRequestParams.getGeranPositioningDataInformation());
                    }
                }

                if (slrEvent.getUtranPositioningData() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, UTRAN Positioning Data Information=" + slrEvent.getUtranPositioningData());
                    slrRequestParams.setUtranPositioningDataInfo(slrEvent.getUtranPositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranPositioningDataInfo(slrRequestParams.getUtranPositioningDataInfo());
                    }
                }

                if (slrEvent.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, cellGlobalIdOrServiceAreaIdOrLAI=" + slrEvent.getCellGlobalIdOrServiceAreaIdOrLAI());
                    slrRequestParams.setCellGlobalIdOrServiceAreaIdOrLAI(slrEvent.getCellGlobalIdOrServiceAreaIdOrLAI());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCellGlobalIdOrServiceAreaIdOrLAI(slrRequestParams.getCellGlobalIdOrServiceAreaIdOrLAI());
                    }
                }

                if (slrEvent.getHGMLCAddress() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, hGMLCAddress=" + slrEvent.getHGMLCAddress());
                    slrRequestParams.sethGmlcAddress(slrEvent.getHGMLCAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.sethGmlcAddress(slrRequestParams.gethGmlcAddress());
                    }
                }

                if (slrEvent.getLCSServiceTypeID() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, LCSServiceTypeID=" + slrEvent.getLCSServiceTypeID());
                    slrRequestParams.setLcsServiceTypeID(slrEvent.getLCSServiceTypeID());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setLcsServiceTypeID(slrRequestParams.getLcsServiceTypeID());
                    }
                }

                if (slrEvent.getSaiPresent()) {
                    this.logger.fine("\nonSubscriberLocationReportRequest: SAIPresent=" + slrEvent.getSaiPresent());
                    slrRequestParams.setSaiPresent(slrEvent.getSaiPresent());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setSaiPresent(slrEvent.getSaiPresent());
                    }
                }

                if (slrEvent.getPseudonymIndicator()) {
                    this.logger.fine("\nonSubscriberLocationReportRequest: PseudonymIndicator=" + slrEvent.getPseudonymIndicator());
                    slrRequestParams.setPseudonymIndicator(slrEvent.getPseudonymIndicator());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setPseudonymIndicator(slrEvent.getPseudonymIndicator());
                    }
                }

                if (slrEvent.getAccuracyFulfilmentIndicator() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest: AccuracyFulfilmentIndicator=" + slrEvent.getAccuracyFulfilmentIndicator());
                    slrRequestParams.setAccuracyFulfilmentIndicator(slrEvent.getAccuracyFulfilmentIndicator());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setAccuracyFulfilmentIndicator(slrRequestParams.getAccuracyFulfilmentIndicator());
                    }
                }

                if (slrEvent.getVelocityEstimate() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, VelocityEstimate=" + slrEvent.getVelocityEstimate());
                    slrRequestParams.setVelocityEstimate(slrEvent.getVelocityEstimate());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setVelocityEstimate(slrRequestParams.getVelocityEstimate());
                    }
                }

                if (slrEvent.getSequenceNumber() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, SequenceNumber=" + slrEvent.getSequenceNumber());
                    slrRequestParams.setSequenceNumber(slrEvent.getSequenceNumber());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setSequenceNumber(slrRequestParams.getSequenceNumber());
                    }
                }

                if (slrEvent.getPeriodicLDRInfo() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, periodicLDRInfo=" + slrEvent.getPeriodicLDRInfo());
                    slrRequestParams.setPeriodicLDRInfo(slrEvent.getPeriodicLDRInfo());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setPeriodicLDRInfo(slrRequestParams.getPeriodicLDRInfo());
                    }
                }

                if (slrEvent.getMoLrShortCircuitIndicator()) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, MoLrShortCircuitIndicator=" + slrEvent.getMoLrShortCircuitIndicator());
                    slrRequestParams.setMoLrShortCircuitIndicator(slrEvent.getMoLrShortCircuitIndicator());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMoLrShortCircuitIndicator(slrEvent.getMoLrShortCircuitIndicator());
                    }
                }

                if (slrEvent.getGeranGANSSpositioningData() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, Geran GANSS positioning data=" + slrEvent.getGeranGANSSpositioningData());
                    slrRequestParams.setGeranGANSSpositioningData(slrEvent.getGeranGANSSpositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setGeranGANSSpositioningData(slrRequestParams.getGeranGANSSpositioningData());
                    }
                }

                if (slrEvent.getUtranGANSSpositioningData() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, Utran GANSS positioning data=" + slrEvent.getUtranGANSSpositioningData());
                    slrRequestParams.setUtranGANSSpositioningData(slrEvent.getUtranGANSSpositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranGANSSpositioningData(slrRequestParams.getUtranGANSSpositioningData());
                    }
                }

                if (slrEvent.getTargetServingNodeForHandover() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, targetServingNodeForHandover=" + slrEvent.getTargetServingNodeForHandover());
                    slrRequestParams.setTargetServingNodeForHandover(slrEvent.getTargetServingNodeForHandover());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setServingNodeAddress(slrRequestParams.getTargetServingNodeForHandover());
                    }
                }

                if (slrEvent.getUtranAdditionalPositioningData() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, UTRAN additional positioning data=" + slrEvent.getUtranAdditionalPositioningData());
                    slrRequestParams.setUtranAdditionalPositioningData(slrEvent.getUtranAdditionalPositioningData());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranAdditionalPositioningData(slrEvent.getUtranAdditionalPositioningData());
                    }
                }

                if (slrEvent.getUtranBaroPressureMeas() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, UTRAN barometric pressure measurement=" + slrEvent.getUtranBaroPressureMeas());
                    slrRequestParams.setUtranBaroPressureMeas(slrEvent.getUtranBaroPressureMeas());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranBaroPressureMeas(slrEvent.getUtranBaroPressureMeas());
                    }
                }

                if (slrEvent.getUtranCivicAddress() != null) {
                    this.logger.fine("\nonSubscriberLocationReportRequest, UTRAN civic address=" + slrEvent.getUtranCivicAddress());
                    slrRequestParams.setUtranCivicAddress(slrEvent.getUtranCivicAddress());
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setUtranCivicAddress(slrEvent.getUtranCivicAddress());
                    }
                }

                // SLR CDR creation
                if (gmlcCdrState.isInitialized()) {
                    this.createCDRRecord(RecordStatus.SLR_SUCCESS);
                }
                // TODO review CDR creation with options depending on the received information within slrEvent

                this.setSubscriberLocationReportRequest(slrEvent);
                if (this.getSubscriberLocationReportRequest() != null) {

                    slrEvent.getMAPDialog().addSubscriberLocationReportResponse(slrEvent.getInvokeId(), slrEvent.getNaESRD(), slrEvent.getNaESRK(), null,
                        slrEvent.getHGMLCAddress(), slrEvent.getMoLrShortCircuitIndicator(), null, slrEvent.getLCSReferenceNumber());

                    // SubscriberLocationReportResponse is now composed by values taken from SubscriberLocationReportRequest and ready to be sent:
                    slrEvent.getMAPDialog().close(false);

                    // Handle successful retrieval of subscriber's location report request (SLR request) info by sending HTTP POST back to the requestor
                    if (this.logger.isFineEnabled()) {
                        logger.fine(String.format("Handling SubscriberLocationReport POST ReferenceNumber '%s'\n", slrEvent.getLCSReferenceNumber()));
                    }
                    httpSubscriberLocationReport = getHttpSubscriberLocationReport();
                    httpSubscriberLocationReport.Perform(HttpReport.HttpMethod.POST, slrEvent.getLCSReferenceNumber(), slrRequestParams, true);
                    httpSubscriberLocationReport.closeMongo();
                }
            }
        } catch (MAPException me) {
            logger.severe(String.format("MAPException while trying to process onSubscriberLocationReportRequest=%s", slrEvent), me);
            logger.severe("MAP error message when processing onSubscriberLocationReportRequest: " + mapErrorMessage);
            this.createCDRRecord(RecordStatus.SLR_SYSTEM_FAILURE);
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSubscriberLocationReportRequest=%s", slrEvent), e);
            this.createCDRRecord(RecordStatus.SLR_SYSTEM_FAILURE);
        }
    }

    /**
     * MAP SLR Response Event
     */
    public void onSubscriberLocationReportResponse(SubscriberLocationReportResponse subscriberLocationReportResponse, ActivityContextInterface aci) {
        try {
            this.logger.fine("\nReceived onSubscriberLocationReportResponse = " + subscriberLocationReportResponse + ", ActivityContextInterface = " + aci);
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onSubscriberLocationReportResponse=%s", subscriberLocationReportResponse), e);
        }

    }

    /**
     * SS7 DIALOG Events
     */
    public void onDialogTimeout(DialogTimeout dialogTimeout, ActivityContextInterface aci) {
        this.logger.fine("\nRx :  onDialogTimeout " + dialogTimeout);
        String operation = null, targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, addNum = null, curlUser = null;
        Long transaction = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onDialogTimeoutRecordStatus = RecordStatus.MAP_DIALOG_TIMEOUT;

        if (dialogTimeout.getMAPDialog() != null) {
            transaction = mobileCoreNetworkTransactions.getTransactionId(dialogTimeout.getMAPDialog().getLocalDialogId());
            dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
            TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
            mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
            if (timerID != null)
                this.timerFacility.cancelTimer(timerID);
            if (dialogTimeout.getMAPDialog().getApplicationContext() != null) {
                MAPApplicationContextName mapApplicationContextName = dialogTimeout.getMAPDialog().getApplicationContext().getApplicationContextName();
                MAPApplicationContextVersion mapApplicationContextVersion = dialogTimeout.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    onDialogTimeoutRecordStatus = RecordStatus.ATI_DIALOG_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    mobileCoreNetworkTransactions.destroy(transaction);
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    onDialogTimeoutRecordStatus = RecordStatus.SRILCS_DIALOG_TIMEOUT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslNNN");
                    addNum = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslAddNum");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    mobileCoreNetworkTransactions.destroy(transaction);
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    onDialogTimeoutRecordStatus = RecordStatus.PSL_DIALOG_TIMEOUT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslNNN");
                    addNum = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslAddNum");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    mobileCoreNetworkTransactions.destroy(transaction);
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    onDialogTimeoutRecordStatus = RecordStatus.SRI_DIALOG_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiNNN");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    mobileCoreNetworkTransactions.destroy(transaction);
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    onDialogTimeoutRecordStatus = RecordStatus.SRISM_DIALOG_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiNNN");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    mobileCoreNetworkTransactions.destroy(transaction);
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    onDialogTimeoutRecordStatus = RecordStatus.PSI_DIALOG_TIMEOUT;
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiNNN");
                    if (nnn == null)
                        nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyNnn");
                    mobileCoreNetworkTransactions.destroy(transaction);
                }
            }
        }
        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        this.reportLocationRequestError(MLPResponse.MLPResultType.TIMEOUT, "MAP Dialog Timeout", operation, targetMsisdn,
            targetImsi, targetImei, referenceNumber, nnn, addNum, null, null, mlpTriggeredReportingService);
        MAPDialog mapDialog = dialogTimeout.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            this.createCDRRecord(onDialogTimeoutRecordStatus);
        }
    }

    public void onDialogDelimiter(DialogDelimiter dialogDelimiter, ActivityContextInterface aci) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("\nReceived onDialogDelimiter = " + dialogDelimiter + ", ActivityContextInterface = " + aci);
        }
    }

    public void onDialogAccept(DialogAccept dialogAccept, ActivityContextInterface aci) {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("\nReceived onDialogAccept = " + dialogAccept + ", ActivityContextInterface = " + aci);
        }
    }

    public void onDialogReject(DialogReject dialogReject, ActivityContextInterface aci) {
        this.logger.fine("\nRx :  onDialogReject " + dialogReject);
        String operation = "NA", reason = "NA", targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, addNum = null, curlUser = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        MAPApplicationContextName mapApplicationContextName = null;
        MAPApplicationContextVersion mapApplicationContextVersion;
        MAPRefuseReason mapRefuseReason = null;
        int translationType;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onDialogRejectRecordStatus = RecordStatus.MAP_DIALOG_REJECTED;
        LocationRequestParams locationRequestParams;

        try {

            if (dialogReject != null) {
                mapRefuseReason = dialogReject.getRefuseReason();
                if (dialogReject.getMAPDialog() != null) {
                    transaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                    dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                    TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                    mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
                    if (timerID != null)
                        this.timerFacility.cancelTimer(timerID);
                    if (dialogReject.getMAPDialog().getApplicationContext() != null)
                        mapApplicationContextName = dialogReject.getMAPDialog().getApplicationContext().getApplicationContextName();
                    if (dialogReject.getMAPDialog().getRemoteAddress() != null)
                        if (dialogReject.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                            nnn = dialogReject.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
                }
            }

            if (mapRefuseReason == null) {
                mapApplicationContextVersion = dialogReject.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    onDialogRejectRecordStatus = RecordStatus.ATI_MAP_DIALOG_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    onDialogRejectRecordStatus = RecordStatus.SRILCS_MAP_DIALOG_TIMEOUT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    onDialogRejectRecordStatus = RecordStatus.PSL_MAP_DIALOG_TIMEOUT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    onDialogRejectRecordStatus = RecordStatus.SRI_MAP_DIALOG_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    onDialogRejectRecordStatus = RecordStatus.SRISM_MAP_DIALOG_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    onDialogRejectRecordStatus = RecordStatus.PSI_MAP_DIALOG_TIMEOUT;
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                }

            } else {

                /*****************************/
                /**** ACN not supported  ****/
                /***************************/
                // If ACN not supported, swap to the new one suggested
                if (mapRefuseReason == MAPRefuseReason.ApplicationContextNotSupported) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / ApplicationContextNotSupported: " + dialogReject);
                    }

                    // Now, send new the SS7 operation with supported ACN
                    ApplicationContextName tcapApplicationContextName = dialogReject.getAlternativeApplicationContext();
                    MAPApplicationContext supportedMAPApplicationContext = MAPApplicationContext.getInstance(tcapApplicationContextName.getOid());

                    // SRI ACN check and refactor to the appropriate value
                    if (supportedMAPApplicationContext.getApplicationContextName() == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("WARNING: ACN version " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion() +
                                    " not supported for MAP SRI (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() + ")");
                        }
                        operation = "SRI";
                        reason = "SRI Application Context Not Supported";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_ACN_NOT_SUPPORTED;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        String domainForPsi = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "sriPsiDomain");
                        String locationInformationEps = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "locationInfoEPS");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        String sriAcnVersion = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "sriAcnVersion");
                        translationType = (Integer) mobileCoreNetworkTransactions.getValue(sriTransaction, "tt");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                        boolean sriOnly = (Boolean) mobileCoreNetworkTransactions.getValue(sriTransaction, "sriOnly");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);

                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("Reattempt SRI (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() +
                                    ") with suggested version: " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion());
                        }
                        if (sriAcnVersion.equalsIgnoreCase("version3")) {
                            locationRequestParams = new LocationRequestParams();
                            locationRequestParams.setTargetingMSISDN(targetMsisdn);
                            locationRequestParams.setDomainType(domainForPsi);
                            locationRequestParams.setLocationInfoEps(locationInformationEps);
                            locationRequestParams.setTranslationType(translationType);
                            locationRequestParams.setCurlUser(curlUser);
                            getLocationViaSubscriberInformationCallHandling(locationRequestParams, true, sriOnly);
                            return;
                        }
                    }

                    // SRISM ACN check and refactor to the appropriate value
                    if (supportedMAPApplicationContext.getApplicationContextName() == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("WARNING: ACN version " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion() +
                                    " not supported for MAP SRISM (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() + ")");
                        }
                        operation = "SRISM";
                        reason = "SRISM Application Context Not Supported";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_ACN_NOT_SUPPORTED;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        String domainForPsi = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "sriPsiDomain");
                        String locationInformationEps = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "locationInfoEPS");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        String sriAcnVersion = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "sriAcnVersion");
                        translationType = (Integer) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "tt");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        boolean sriSmOnly = (Boolean) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "sriSmOnly");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);

                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("Reattempt SRISM (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() +
                                    ") with suggested version: " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion());
                        }
                        if (sriAcnVersion.equalsIgnoreCase("version3")) {
                            locationRequestParams = new LocationRequestParams();
                            locationRequestParams.setTargetingMSISDN(targetMsisdn);
                            locationRequestParams.setDomainType(domainForPsi);
                            locationRequestParams.setLocationInfoEps(locationInformationEps);
                            locationRequestParams.setTranslationType(translationType);
                            locationRequestParams.setCurlUser(curlUser);
                            getLocationViaSubscriberInformation(locationRequestParams, true, sriSmOnly);
                            return;
                        }
                    }

                    // ATI ACN check and log if rejected for not being supported by the network
                    if (supportedMAPApplicationContext.getApplicationContextName() == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("WARNING: ACN version " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion() +
                                    " not supported for MAP ATI (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() + ")");
                        }
                        operation = "ATI";
                        reason = "ATI Application Context Not Supported";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_ACN_NOT_SUPPORTED;
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "curlUser");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI ACN check and log if rejected for not being supported by the network
                    if (supportedMAPApplicationContext.getApplicationContextName() == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("WARNING: ACN version " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion() +
                                    " not supported for MAP PSI (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() + ")");
                        }
                        operation = "PSI";
                        reason = "PSI Application Context Not Supported";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_ACN_NOT_SUPPORTED;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "curlUser");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }

                    // SRILCS ACN check and log if rejected for not being supported by the network
                    if (supportedMAPApplicationContext.getApplicationContextName() == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("WARNING: ACN version " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion() +
                                    " not supported for MAP SRILCS (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() + ")");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS Application Context Not Supported";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_ACN_NOT_SUPPORTED;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }

                    // PSL ACN check and log if rejected for not being supported by the network
                    if (supportedMAPApplicationContext.getApplicationContextName() == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            if (supportedMAPApplicationContext.getApplicationContextVersion() != null)
                                logger.warning("WARNING: ACN version " + supportedMAPApplicationContext.getApplicationContextVersion().getVersion() +
                                    " not supported for MAP PSL (ACN code " + supportedMAPApplicationContext.getApplicationContextName().getApplicationContextCode() + ")");
                        }
                        operation = "PSL";
                        reason = "PSL Application Context Not Supported";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_ACN_NOT_SUPPORTED;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }

                /***************************************/
                /**** InvalidDestinationReference ****/
                /************************************/
                if (mapRefuseReason == MAPRefuseReason.InvalidDestinationReference) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / InvalidDestinationReference: " + dialogReject);
                    }
                    // SRI InvalidDestinationReference
                    if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidDestinationReference for attempted SRI");
                        }
                        operation = "SRI";
                        reason = "SRI Invalid Destination Reference";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_INVALID_DESTINATION_REFERENCE;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                    }
                    // SRISM InvalidDestinationReference
                    if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidDestinationReference for attempted SRISM");
                        }
                        operation = "SRISM";
                        reason = "SRISM Invalid Destination Reference";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_INVALID_DESTINATION_REFERENCE;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                    }
                    // ATI InvalidDestinationReference
                    if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidDestinationReference for attempted ATI");
                        }
                        operation = "ATI";
                        reason = "ATI Invalid Destination Reference";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_INVALID_DESTINATION_REFERENCE;
                        // Transaction
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "curlUser");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI InvalidDestinationReference
                    if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidDestinationReference for attempted PSI");
                        }
                        operation = "PSI";
                        reason = "PSI Invalid Destination Reference";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_INVALID_DESTINATION_REFERENCE;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "curlUser");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriForSMImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiOnlyImsi");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }
                    // SRILCS InvalidDestinationReference
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidDestinationReference for attempted SRILCS");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS Invalid Destination Reference";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_INVALID_DESTINATION_REFERENCE;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }
                    // PSL InvalidDestinationReference
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidDestinationReference for attempted PSL");
                        }
                        operation = "PSL";
                        reason = "PSL Invalid Destination Reference";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_INVALID_DESTINATION_REFERENCE;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }

                /**************************************/
                /**** InvalidOriginatingReference ****/
                /************************************/
                if (mapRefuseReason == MAPRefuseReason.InvalidOriginatingReference) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / InvalidOriginatingReference: " + dialogReject);
                    }
                    // SRI InvalidOriginatingReference
                    if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidOriginatingReference for attempted SRI");
                        }
                        operation = "SRI";
                        reason = "SRI Invalid Originating Reference";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_INVALID_ORIGINATING_REFERENCE;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                    }
                    // SRISM InvalidOriginatingReference
                    if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidOriginatingReference for attempted SRISM");
                        }
                        operation = "SRISM";
                        reason = "SRISM Invalid Originating Reference";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_INVALID_ORIGINATING_REFERENCE;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                    }
                    // ATI InvalidOriginatingReference
                    if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidOriginatingReference for attempted ATI");
                        }
                        operation = "ATI";
                        reason = "ATI Invalid Originating Reference";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_INVALID_ORIGINATING_REFERENCE;
                        // Transaction
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "curlUser");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI InvalidOriginatingReference
                    if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidOriginatingReference for attempted PSI");
                        }
                        operation = "PSI";
                        reason = "PSI Invalid Originating Reference";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_INVALID_ORIGINATING_REFERENCE;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "curlUser");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriForSMImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiOnlyImsi");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }
                    // SRILCS InvalidOriginatingReference
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidOriginatingReference for attempted SRILCS");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS Invalid Originating Reference";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_INVALID_ORIGINATING_REFERENCE;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }
                    // PSL InvalidOriginatingReference
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: InvalidOriginatingReference for attempted PSL");
                        }
                        operation = "PSL";
                        reason = "PSL Invalid Originating Reference";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_INVALID_ORIGINATING_REFERENCE;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }

                /************************/
                /**** NoReasonGiven ****/
                /**********************/
                if (mapRefuseReason == MAPRefuseReason.NoReasonGiven) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / NoReasonGiven: " + dialogReject);
                    }
                    // SRI NoReasonGiven
                    if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: NoReasonGiven for attempted SRI");
                        }
                        operation = "SRI";
                        reason = "SRI No Reason Given";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_NO_REASON_GIVEN;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                    }
                    // SRISM NoReasonGiven
                    if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: NoReasonGiven for attempted SRISM");
                        }
                        operation = "SRISM";
                        reason = "SRISM No Reason Given";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_NO_REASON_GIVEN;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                    }
                    // ATI NoReasonGiven
                    if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: NoReasonGiven for attempted ATI");
                        }
                        operation = "ATI";
                        reason = "ATI No Reason Given";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_NO_REASON_GIVEN;
                        // Transaction
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "curlUser");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI NoReasonGiven
                    if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: NoReasonGiven for attempted PSI");
                        }
                        operation = "PSI";
                        reason = "PSI No Reason Given";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_NO_REASON_GIVEN;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "curlUser");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriForSMImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiOnlyImsi");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }
                    // SRILCS NoReasonGiven
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: NoReasonGiven for attempted SRILCS");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS No Reason Given";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_NO_REASON_GIVEN;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }
                    // PSL NoReasonGiven
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: NoReasonGiven for attempted PSL");
                        }
                        operation = "PSL";
                        reason = "PSL No Reason Given";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_NO_REASON_GIVEN;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }

                /*********************************/
                /**** RemoteNodeNotReachable ****/
                /*******************************/
                if (mapRefuseReason == MAPRefuseReason.RemoteNodeNotReachable) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / RemoteNodeNotReachable: " + dialogReject);
                    }
                    // SRI RemoteNodeNotReachable
                    if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: RemoteNodeNotReachable for attempted SRI");
                        }
                        operation = "SRI";
                        reason = "SRI Remote Node Not Reachable";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_REMOTE_NODE_NOT_REACHABLE;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                    }
                    // SRISM RemoteNodeNotReachable
                    if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: RemoteNodeNotReachable for attempted SRISM");
                        }
                        operation = "SRISM";
                        reason = "SRISM Remote Node Not Reachable";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_REMOTE_NODE_NOT_REACHABLE;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                    }
                    // ATI RemoteNodeNotReachable
                    if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: RemoteNodeNotReachable for attempted ATI");
                        }
                        operation = "ATI";
                        reason = "ATI Remote Node Not Reachable";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_REMOTE_NODE_NOT_REACHABLE;
                        // Transaction
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI RemoteNodeNotReachable
                    if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: RemoteNodeNotReachable for attempted PSI");
                        }
                        operation = "PSI";
                        reason = "PSI Remote Node Not Reachable";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_REMOTE_NODE_NOT_REACHABLE;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "curlUser");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriForSMImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiOnlyImsi");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }
                    // SRILCS RemoteNodeNotReachable
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: RemoteNodeNotReachable for attempted SRILCS");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS Remote Node Not Reachable";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_REMOTE_NODE_NOT_REACHABLE;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }
                    // PSL RemoteNodeNotReachable
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: RemoteNodeNotReachable for attempted PSL");
                        }
                        operation = "PSL";
                        reason = "PSL Remote Node Not Reachable";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_REMOTE_NODE_NOT_REACHABLE;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }

                /******************************************/
                /**** PotentialVersionIncompatibility ****/
                /****************************************/
                if (mapRefuseReason == MAPRefuseReason.PotentialVersionIncompatibility) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / PotentialVersionIncompatibility: " + dialogReject);
                    }
                    // SRI PotentialVersionIncompatibility
                    if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibility for attempted SRI");
                        }
                        operation = "SRI";
                        reason = "SRI Potential Version Incompatibility";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                    }
                    // SRISM PotentialVersionIncompatibility
                    if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibility for attempted SRISM");
                        }
                        operation = "SRISM";
                        reason = "SRISM Potential Version Incompatibility";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                    }
                    // ATI PotentialVersionIncompatibility
                    if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibility for attempted ATI");
                        }
                        operation = "ATI";
                        reason = "ATI Potential Version Incompatibility";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY;
                        // Transaction
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "curlUser");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI PotentialVersionIncompatibility
                    if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibility for attempted PSI");
                        }
                        operation = "PSI";
                        reason = "PSI Potential Version Incompatibility";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "curlUser");
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriForSMImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiOnlyImsi");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }
                    // SRILCS PotentialVersionIncompatibility
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibility for attempted SRILCS");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS Potential Version Incompatibility";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }
                    // PSL PotentialVersionIncompatibility
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibility for attempted PSL");
                        }
                        operation = "PSL";
                        reason = "PSL Potential Version Incompatibility";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }

                /**********************************************/
                /**** PotentialVersionIncompatibilityTcap ****/
                /********************************************/
                if (mapRefuseReason == MAPRefuseReason.PotentialVersionIncompatibilityTcap) {
                    if (logger.isWarningEnabled()) {
                        this.logger.warning("Rx : onDialogReject / PotentialVersionIncompatibilityTcap: " + dialogReject);
                    }
                    // SRI PotentialVersionIncompatibilityTcap
                    if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibilityTcap for attempted SRI");
                        }
                        operation = "SRI";
                        reason = "SRI Potential Version Incompatibility TCAP";
                        onDialogRejectRecordStatus = RecordStatus.SRI_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY_TCAP;
                        // Transaction
                        Long sriTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriTransaction, "curlUser");
                        if (sriTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriTransaction);
                    }
                    // SRISM PotentialVersionIncompatibilityTcap
                    if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibilityTcap for attempted SRISM");
                        }
                        operation = "SRISM";
                        reason = "SRISM Potential Version Incompatibility TCAP";
                        onDialogRejectRecordStatus = RecordStatus.SRISM_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY_TCAP;
                        // Transaction
                        Long sriForSMTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "psiMsisdn");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForSMTransaction, "curlUser");
                        if (sriForSMTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForSMTransaction);
                    }
                    // ATI PotentialVersionIncompatibilityTcap
                    if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibilityTcap for attempted ATI");
                        }
                        operation = "ATI";
                        reason = "ATI Potential Version Incompatibility TCAP";
                        onDialogRejectRecordStatus = RecordStatus.ATI_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY_TCAP;
                        // Transaction
                        Long atiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "atiImsi");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(atiTransaction, "curlUser");
                        if (atiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(atiTransaction);
                    }
                    // PSI PotentialVersionIncompatibilityTcap
                    if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibilityTcap for attempted PSI");
                        }
                        operation = "PSI";
                        reason = "PSI Potential Version Incompatibility TCAP";
                        onDialogRejectRecordStatus = RecordStatus.PSI_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY_TCAP;
                        Long psiTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "psiMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriForSMImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(psiTransaction, "sriImsi");
                        if (targetImsi == null)
                            targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                        if (psiTransaction != null)
                            mobileCoreNetworkTransactions.destroy(psiTransaction);
                    }
                    // SRILCS PotentialVersionIncompatibilityTcap
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibilityTcap for attempted SRILCS");
                        }
                        operation = "SRILCS";
                        reason = "SRILCS Potential Version Incompatibility TCAP";
                        onDialogRejectRecordStatus = RecordStatus.SRILCS_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY_TCAP;
                        Long sriForLcsTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(sriForLcsTransaction, "curlUser");
                        if (sriForLcsTransaction != null)
                            mobileCoreNetworkTransactions.destroy(sriForLcsTransaction);
                    }
                    // PSL PotentialVersionIncompatibilityTcap
                    if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext) {
                        if (logger.isWarningEnabled()) {
                            logger.warning("WARNING: PotentialVersionIncompatibilityTcap for attempted PSL");
                        }
                        operation = "PSL";
                        reason = "PSL Potential Version Incompatibility TCAP";
                        onDialogRejectRecordStatus = RecordStatus.PSL_REJECTED_POTENTIAL_VERSION_INCOMPATIBILITY_TCAP;
                        Long pslTransaction = mobileCoreNetworkTransactions.getTransactionId(dialogReject.getMAPDialog().getLocalDialogId());
                        targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslMsisdn");
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslImsi");
                        targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                        referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(pslTransaction, "pslLcsReferenceNumber");
                        curlUser = (String) mobileCoreNetworkTransactions.getValue(pslTransaction, "curlUser");
                        if (pslTransaction != null)
                            mobileCoreNetworkTransactions.destroy(pslTransaction);
                    }
                }
            }

        } catch (Exception e) {
            logger.severe("Exception on onDialogReject(): " + e.getMessage(), e);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on MAP Dialog Rejected",
                operation, targetMsisdn, targetImsi, targetImei, referenceNumber, nnn, addNum, null, null, mlpTriggeredReportingService);
        }

        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        this.reportLocationRequestError(MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED, "Dialog Rejected: " + reason,
            operation, targetMsisdn, targetImsi, targetImei, referenceNumber, nnn, addNum,null, null, mlpTriggeredReportingService);
        MAPDialog mapDialog = dialogReject.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null && eventTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            this.createCDRRecord(onDialogRejectRecordStatus);
        }
    }

    public void onDialogUserAbort(DialogUserAbort dialogUserAbort, ActivityContextInterface aci) {
        this.logger.fine("\nRx :  onDialogUserAbort " + dialogUserAbort);
        String operation = null, targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, curlUser = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onDialogUserAbortRecordStatus = RecordStatus.MAP_USER_DIALOG_ABORT;

        if (dialogUserAbort.getMAPDialog() != null) {
            transaction = mobileCoreNetworkTransactions.getTransactionId(dialogUserAbort.getMAPDialog().getLocalDialogId());
            dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
            TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
            mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
            if (timerID != null)
                this.timerFacility.cancelTimer(timerID);
            if (dialogUserAbort.getMAPDialog().getRemoteAddress() != null)
                if (dialogUserAbort.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                    nnn = dialogUserAbort.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
            if (dialogUserAbort.getMAPDialog().getApplicationContext() != null) {
                MAPApplicationContextName mapApplicationContextName = dialogUserAbort.getMAPDialog().getApplicationContext().getApplicationContextName();
                MAPApplicationContextVersion mapApplicationContextVersion = dialogUserAbort.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    onDialogUserAbortRecordStatus = RecordStatus.ATI_DIALOG_U_ABORT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    onDialogUserAbortRecordStatus = RecordStatus.SRILCS_DIALOG_U_ABORT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    onDialogUserAbortRecordStatus = RecordStatus.PSL_DIALOG_U_ABORT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    onDialogUserAbortRecordStatus = RecordStatus.SRI_DIALOG_U_ABORT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    onDialogUserAbortRecordStatus = RecordStatus.SRISM_DIALOG_U_ABORT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    onDialogUserAbortRecordStatus = RecordStatus.PSI_DIALOG_U_ABORT;
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                }
            }
        }
        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        this.reportLocationRequestError(MLPResponse.MLPResultType.UNSPECIFIED_ERROR, "Dialog U-ABORT", operation, targetMsisdn,
            targetImsi, targetImei, referenceNumber, nnn, null, null, null, mlpTriggeredReportingService);
        MAPDialog mapDialog = dialogUserAbort.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            this.createCDRRecord(onDialogUserAbortRecordStatus);
        }
    }

    public void onDialogProviderAbort(DialogProviderAbort dialogProviderAbort, ActivityContextInterface aci) {
        this.logger.fine("\nRx :  onDialogProviderAbort " + dialogProviderAbort);
        String operation = null, targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, curlUser = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onDialogProviderAbortRecordStatus = RecordStatus.MAP_PROVIDER_DIALOG_ABORT;

        if (dialogProviderAbort.getMAPDialog() != null) {
            transaction = mobileCoreNetworkTransactions.getTransactionId(dialogProviderAbort.getMAPDialog().getLocalDialogId());
            dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
            TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
            mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
            if (timerID != null)
                this.timerFacility.cancelTimer(timerID);
            if (dialogProviderAbort.getMAPDialog().getRemoteAddress() != null)
                if (dialogProviderAbort.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                    nnn = dialogProviderAbort.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
            if (dialogProviderAbort.getMAPDialog().getApplicationContext() != null) {
                MAPApplicationContextName mapApplicationContextName = dialogProviderAbort.getMAPDialog().getApplicationContext().getApplicationContextName();
                MAPApplicationContextVersion mapApplicationContextVersion = dialogProviderAbort.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    onDialogProviderAbortRecordStatus = RecordStatus.ATI_DIALOG_P_ABORT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    onDialogProviderAbortRecordStatus = RecordStatus.SRILCS_DIALOG_P_ABORT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    onDialogProviderAbortRecordStatus = RecordStatus.PSL_DIALOG_P_ABORT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    onDialogProviderAbortRecordStatus = RecordStatus.SRI_DIALOG_P_ABORT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    onDialogProviderAbortRecordStatus = RecordStatus.SRISM_DIALOG_P_ABORT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    onDialogProviderAbortRecordStatus = RecordStatus.PSI_DIALOG_P_ABORT;
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                }
            }
        }
        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        this.reportLocationRequestError(MLPResponse.MLPResultType.UNSPECIFIED_ERROR, "Dialog P-ABORT", operation, targetMsisdn,
            targetImsi, targetImei, referenceNumber, nnn, null, null, null, mlpTriggeredReportingService);
        MAPDialog mapDialog = dialogProviderAbort.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            this.createCDRRecord(onDialogProviderAbortRecordStatus);
        }
    }

    public void onDialogClose(DialogClose dialogClose, ActivityContextInterface aci) {
        this.logger.fine("\nReceived onDialogClose = " + dialogClose + ", ActivityContextInterface = " + aci);
    }

    public void onDialogNotice(DialogNotice dialogNotice, ActivityContextInterface aci) {
        this.logger.fine("\nReceived onDialogNotice = " + dialogNotice + ", ActivityContextInterface = " + aci);
    }

    public void onDialogRelease(DialogRelease dialogRelease, ActivityContextInterface aci) {
        this.logger.fine("\nReceived onDialogRelease = " + dialogRelease + ", ActivityContextInterface = " + aci);
    }

    public void onInvokeTimeout(InvokeTimeout invokeTimeout, ActivityContextInterface aci) {
        this.logger.fine("\nReceived onInvokeTimeout = " + invokeTimeout + ", ActivityContextInterface = " + aci);
        String operation = null, targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, curlUser = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onInvokeTimeoutRecordStatus = RecordStatus.FAILED_INVOKE_TIMEOUT;

        if (invokeTimeout.getMAPDialog() != null) {
            transaction = mobileCoreNetworkTransactions.getTransactionId(invokeTimeout.getMAPDialog().getLocalDialogId());
            dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
            TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
            mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
            if (timerID != null)
                this.timerFacility.cancelTimer(timerID);
            if (invokeTimeout.getMAPDialog().getApplicationContext() != null) {
                MAPApplicationContextName mapApplicationContextName = invokeTimeout.getMAPDialog().getApplicationContext().getApplicationContextName();
                MAPApplicationContextVersion mapApplicationContextVersion = invokeTimeout.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    onInvokeTimeoutRecordStatus = RecordStatus.ATI_INVOKE_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    onInvokeTimeoutRecordStatus = RecordStatus.SRILCS_INVOKE_TIMEOUT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    onInvokeTimeoutRecordStatus = RecordStatus.PSL_INVOKE_TIMEOUT;
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    onInvokeTimeoutRecordStatus = RecordStatus.SRI_INVOKE_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiNNN");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    onInvokeTimeoutRecordStatus = RecordStatus.SRISM_INVOKE_TIMEOUT;
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiNNN");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    onInvokeTimeoutRecordStatus = RecordStatus.PSI_INVOKE_TIMEOUT;
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    nnn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiNNN");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                }
            }
        }
        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        this.reportLocationRequestError(MLPResponse.MLPResultType.TIMEOUT, "Invoke timeout", operation, targetMsisdn, targetImsi, targetImei,
            referenceNumber, nnn, null, null, null, mlpTriggeredReportingService);
        MAPDialog mapDialog = invokeTimeout.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            this.createCDRRecord(onInvokeTimeoutRecordStatus);
        }
    }

    public void onErrorComponent(ErrorComponent errorComponent, ActivityContextInterface aci) {
        this.logger.fine("\nReceived onErrorComponent = " + errorComponent);
        String operation = null, targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, addNum = null, curlUser = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        String mapErrorComponentString;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onErrorComponentRecordStatus = RecordStatus.MAP_COMPONENT_ERROR;

        if (errorComponent.getMAPDialog() != null) {
            transaction = mobileCoreNetworkTransactions.getTransactionId(errorComponent.getMAPDialog().getLocalDialogId());
            dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
            TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
            mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
            if (timerID != null)
                this.timerFacility.cancelTimer(timerID);
            if (errorComponent.getMAPDialog().getRemoteAddress() != null)
                if (errorComponent.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                    nnn = errorComponent.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
            if (errorComponent.getMAPDialog().getApplicationContext() != null) {
                MAPApplicationContextName mapApplicationContextName = errorComponent.getMAPDialog().getApplicationContext().getApplicationContextName();
                MAPApplicationContextVersion mapApplicationContextVersion = errorComponent.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                }
            }
        }
        MAPErrorMessage mapErrorMessage = errorComponent.getMAPErrorMessage();
        long error_code = mapErrorMessage.getErrorCode();
        MLPResponse.MLPResultType mlpResultType;
        switch ((int) error_code) {
            case 1:
                mapErrorComponentString = "Unknown Subscriber";
                mlpResultType = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_UNKNOWN_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_UNKNOWN_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_UNKNOWN_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_UNKNOWN_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_UNKNOWN_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_UNKNOWN_SUBSCRIBER;
                break;
            case 2:
                mapErrorComponentString = "Unknown Base Station";
                mlpResultType = MLPResponse.MLPResultType.TARGET_MOVED_TO_NEW_MSC_SGSN;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 3:
                mapErrorComponentString = "Unknown MSC";
                mlpResultType = MLPResponse.MLPResultType.TARGET_MOVED_TO_NEW_MSC_SGSN;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_UNKNOWN_MSC;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_UNKNOWN_MSC;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_UNKNOWN_MSC;
                break;
            case 5:
                mapErrorComponentString = "Unidentified Subscriber";
                mlpResultType = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_UNIDENTIFIED_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_UNIDENTIFIED_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_UNIDENTIFIED_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_UNIDENTIFIED_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_UNIDENTIFIED_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_UNIDENTIFIED_SUBSCRIBER;
                break;
            case 6:
                mapErrorComponentString = "Absent Subscriber SM";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 7:
                mapErrorComponentString = "Unknown Equipment";
                mlpResultType = MLPResponse.MLPResultType.UNSUPPORTED_VERSION;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_UNKNOWN_EQUIPMENT;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_UNKNOWN_EQUIPMENT;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_UNKNOWN_EQUIPMENT;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_UNKNOWN_EQUIPMENT;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_UNKNOWN_EQUIPMENT;
                break;
            case 8:
                mapErrorComponentString = "Roaming Not Allowed";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ROAMING_NOT_ALLOWED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ROAMING_NOT_ALLOWED;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ROAMING_NOT_ALLOWED;
                break;
            case 9:
                mapErrorComponentString = "Illegal Subscriber";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ILLEGAL_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ILLEGAL_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ILLEGAL_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ILLEGAL_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ILLEGAL_SUBSCRIBER;
                break;
            case 10:
                mapErrorComponentString = "Bearer Service Not Provisioned";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_BEARER_SERVICE_NOT_PROVISIONED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_BEARER_SERVICE_NOT_PROVISIONED;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_BEARER_SERVICE_NOT_PROVISIONED;
                break;
            case 11:
                mapErrorComponentString = "Teleservice Not Provisioned";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_TELESERVICE_NOT_PROVISIONED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_TELESERVICE_NOT_PROVISIONED;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_TELESERVICE_NOT_PROVISIONED;
                break;
            case 12:
                mapErrorComponentString = "Illegal Equipment";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ILLEGAL_EQUIPMENT;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ILLEGAL_EQUIPMENT;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ILLEGAL_EQUIPMENT;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ILLEGAL_EQUIPMENT;
                break;
            case 13:
                mapErrorComponentString = "Call Barred";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_CALL_BARRED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 14:
                mapErrorComponentString = "Forwarding Violation";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_FORWARDING_VIOLATION;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 15:
                mapErrorComponentString = "CUG Reject";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_CUG_REJECT;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 16:
                mapErrorComponentString = "Illegal SS Operation";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 17:
                mapErrorComponentString = "SS Error Status";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 18:
                mapErrorComponentString = "SS Not Available";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 19:
                mapErrorComponentString = "SS Subscription Violation";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 20:
                mapErrorComponentString = "SS Incompatibility";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 21:
                mapErrorComponentString = "Facility Not Supported";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_FACILITY_NOT_SUPPORTED;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_FACILITY_NOT_SUPPORTED;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_FACILITY_NOT_SUPPORTED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_FACILITY_NOT_SUPPORTED;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_FACILITY_NOT_SUPPORTED;
                break;
            case 22:
                mapErrorComponentString = "Ongoing Group Call";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 23:
                mapErrorComponentString = "Invalid Target Base Station";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 24:
                mapErrorComponentString = "No Radio Resource Available";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 25:
                mapErrorComponentString = "No Handover Number Available";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 26:
                mapErrorComponentString = "Subsequent Handover Failure";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 27:
                mapErrorComponentString = "Absent Subscriber";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ABSENT_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ABSENT_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ABSENT_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ABSENT_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ABSENT_SUBSCRIBER;
                break;
            case 28:
                mapErrorComponentString = "Incompatible Terminal";
                mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_INCOMPATIBLE_TERMINAL;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_INCOMPATIBLE_TERMINAL;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_INCOMPATIBLE_TERMINAL;
                break;
            case 29:
                mapErrorComponentString = "Short Term Denial";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 30:
                mapErrorComponentString = "Long Term Denial";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 31:
                mapErrorComponentString = "Subscriber Busy For MT SMS";
                mlpResultType = MLPResponse.MLPResultType.CONGESTION_IN_MOBILE_NETWORK;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 32:
                mapErrorComponentString = "SM Delivery Failure";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 33:
                mapErrorComponentString = "Message Waiting List Full";
                mlpResultType = MLPResponse.MLPResultType.CONGESTION_IN_MOBILE_NETWORK;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 34:
                mapErrorComponentString = "System Failure";
                mlpResultType = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_SYSTEM_FAILURE;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_SYSTEM_FAILURE;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_SYSTEM_FAILURE;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_SYSTEM_FAILURE;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_SYSTEM_FAILURE;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_SYSTEM_FAILURE;
                break;
            case 35:
                mapErrorComponentString = "Data Missing";
                mlpResultType = MLPResponse.MLPResultType.FORMAT_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_DATA_MISSING;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_DATA_MISSING;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_DATA_MISSING;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_DATA_MISSING;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_DATA_MISSING;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_DATA_MISSING;
                break;
            case 36:
                mapErrorComponentString = "Unexpected Data Value";
                mlpResultType = MLPResponse.MLPResultType.FORMAT_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_UNEXPECTED_DATA_VALUE;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_UNEXPECTED_DATA_VALUE;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_UNEXPECTED_DATA_VALUE;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_UNEXPECTED_DATA_VALUE;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_UNEXPECTED_DATA_VALUE;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_UNEXPECTED_DATA_VALUE;
                break;
            case 37:
                mapErrorComponentString = "PW Registration Failure";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 38:
                mapErrorComponentString = "Negative PW Check";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 39:
                mapErrorComponentString = "No Roaming Number Available";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 40:
                mapErrorComponentString = "Tracing Buffer Full";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 42:
                mapErrorComponentString = "Target Cell Outside Group Call Area";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 43:
                mapErrorComponentString = "Number Of PW Attempts Violation";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 44:
                mapErrorComponentString = "Number Changed";
                mlpResultType = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_NUMBER_CHANGED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_NUMBER_CHANGED;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_NUMBER_CHANGED;
                break;
            case 45:
                mapErrorComponentString = "Busy Subscriber";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_BUSY_SUBSCRIBER;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 46:
                mapErrorComponentString = "No Subscriber Reply";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 47:
                mapErrorComponentString = "Forwarding Failed";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 48:
                mapErrorComponentString = "OR Not Allowed";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 49:
                mapErrorComponentString = "ATI Not Allowed";
                mlpResultType = MLPResponse.MLPResultType.POSITIONING_NOT_ALLOWED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_OR_NOT_ALLOWED;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 50:
                mapErrorComponentString = "No Group Call Number Available";
                mlpResultType = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 51:
                mapErrorComponentString = "Resource Limitation";
                mlpResultType = MLPResponse.MLPResultType.QOP_NOT_ATTAINABLE;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_RESOURCE_LIMITATION;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_RESOURCE_LIMITATION;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_RESOURCE_LIMITATION;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_RESOURCE_LIMITATION;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_RESOURCE_LIMITATION;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_RESOURCE_LIMITATION;
                break;
            case 52:
                mapErrorComponentString = "Unauthorized Requesting Network";
                mlpResultType = MLPResponse.MLPResultType.POSITIONING_NOT_ALLOWED;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_UNAUTHORIZED_REQUESTING_NETWORK;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_UNAUTHORIZED_REQUESTING_NETWORK;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 53:
                mapErrorComponentString = "Unauthorized LCS Client";
                mlpResultType = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_UNAUTHORIZED_LCS_CLIENT;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_UNAUTHORIZED_LCS_CLIENT;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 54:
                mapErrorComponentString = "Position Method Failure";
                mlpResultType = MLPResponse.MLPResultType.POSITION_METHOD_FAILURE;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_POSITION_METHOD_FAILURE;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 58:
                mapErrorComponentString = "Unknown or Unreachable LCS Client";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 59:
                mapErrorComponentString = "MM Event Not Supported";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 60:
                mapErrorComponentString = "ATSI Not Allowed";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 61:
                mapErrorComponentString = "ATM Not Allowed";
                mlpResultType = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 62:
                mapErrorComponentString = "Information Not Available";
                mlpResultType = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 71:
                mapErrorComponentString = "Unknown Alphabet";
                mlpResultType = MLPResponse.MLPResultType.UNSUPPORTED_VERSION;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            case 72:
                mapErrorComponentString = "USSD Busy";
                mlpResultType = MLPResponse.MLPResultType.CONGESTION_IN_MOBILE_NETWORK;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
            default:
                mapErrorComponentString = "System Failure";
                mlpResultType = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                if (operation.equalsIgnoreCase("ATI"))
                    onErrorComponentRecordStatus = RecordStatus.ATI_ERROR;
                else if (operation.equalsIgnoreCase("SRILCS"))
                    onErrorComponentRecordStatus = RecordStatus.SRILCS_ERROR;
                else if (operation.equalsIgnoreCase("PSL"))
                    onErrorComponentRecordStatus = RecordStatus.PSL_ERROR;
                else if (operation.equalsIgnoreCase("SRI"))
                    onErrorComponentRecordStatus = RecordStatus.SRI_ERROR;
                else if (operation.equalsIgnoreCase("SRISM"))
                    onErrorComponentRecordStatus = RecordStatus.SRISM_ERROR;
                else if (operation.equalsIgnoreCase("PSI"))
                    onErrorComponentRecordStatus = RecordStatus.PSI_ERROR;
                break;
        }
        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        this.reportLocationRequestError(mlpResultType, "MAP Component error: " + mapErrorComponentString + ", MAP error code value: " + error_code,
            operation, targetMsisdn, targetImsi, targetImei, referenceNumber, nnn, addNum, null, null, mlpTriggeredReportingService);
        MAPDialog mapDialog = errorComponent.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null && eventTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            if (gmlcCdrState != null) {
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setStatusCode(error_code);
                }
            }
            this.createCDRRecord(onErrorComponentRecordStatus);
        }
    }

    public void onRejectComponent(RejectComponent rejectComponent, ActivityContextInterface aci) {
        this.logger.fine("\nRx : onRejectComponent " + rejectComponent);
        String operation = null, targetMsisdn = null, targetImsi = null, targetImei = null, nnn = null, curlUser = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        DateTime eventTime = DateTime.now();
        DateTime dialogStartTime = null;
        RecordStatus onRejectComponentRecordStatus = null;
        InvokeProblemType onRejectComponentInvokeProblemType = null;
        MLPResponse.MLPResultType mlpResultType = MLPResponse.MLPResultType.SYSTEM_FAILURE;
        if (rejectComponent.getMAPDialog() != null) {
            transaction = mobileCoreNetworkTransactions.getTransactionId(rejectComponent.getMAPDialog().getLocalDialogId());
            dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
            TimerID timerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
            mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
            if (timerID != null)
                this.timerFacility.cancelTimer(timerID);
            if (rejectComponent.getMAPDialog().getRemoteAddress() != null)
                if (rejectComponent.getMAPDialog().getRemoteAddress().getGlobalTitle() != null)
                    nnn = rejectComponent.getMAPDialog().getRemoteAddress().getGlobalTitle().getDigits();
            if (rejectComponent.getMAPDialog().getApplicationContext() != null) {
                MAPApplicationContextName mapApplicationContextName = rejectComponent.getMAPDialog().getApplicationContext().getApplicationContextName();
                MAPApplicationContextVersion mapApplicationContextVersion = rejectComponent.getMAPDialog().getApplicationContext().getApplicationContextVersion();
                if (mapApplicationContextName == MAPApplicationContextName.anyTimeEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "ATI";
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "atiImsi");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcGatewayContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "SRILCS";
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationSvcEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSL";
                    referenceNumber = (Integer) mobileCoreNetworkTransactions.getValue(transaction, "pslLcsReferenceNumber");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImsi");
                    targetImei = (String) mobileCoreNetworkTransactions.getValue(transaction, "pslImei");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.locationInfoRetrievalContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRI";
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.shortMsgGatewayContext &&
                    (mapApplicationContextVersion == MAPApplicationContextVersion.version3 || mapApplicationContextVersion == MAPApplicationContextVersion.version2)) {
                    operation = "SRISM";
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                }
                if (mapApplicationContextName == MAPApplicationContextName.subscriberInfoEnquiryContext && mapApplicationContextVersion == MAPApplicationContextVersion.version3) {
                    operation = "PSI";
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    targetMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiMsisdn");
                    targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriForSMImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "sriImsi");
                    if (targetImsi == null)
                        targetImsi = (String) mobileCoreNetworkTransactions.getValue(transaction, "psiOnlyImsi");
                }
            }
        }
        if (transaction != null)
            mobileCoreNetworkTransactions.destroy(transaction);
        if (rejectComponent.getProblem() != null) {
            onRejectComponentInvokeProblemType = rejectComponent.getProblem().getInvokeProblemType();
            if (rejectComponent.getProblem().getInvokeProblemType() != null)
                switch ((int) rejectComponent.getProblem().getInvokeProblemType().getType()) {
                    case 0:
                    case 5:
                        mlpResultType = MLPResponse.MLPResultType.INVALID_PROTOCOL_ELEMENT_ATTRIBUTE_VALUE; // DuplicateInvokeID (0) or Unrecognized LinkedID (5)
                        break;
                    case 1:
                    case 3:
                        mlpResultType = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED; // UnrecognizedOperation (1) or ResourceLimitation (3)
                        break;
                    case 2:
                        mlpResultType = MLPResponse.MLPResultType.FORMAT_ERROR; // MistypedParameter
                        break;
                    case 4:
                        // InitiatingRelease mlpResultType = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                        break;
                    case 6:
                        mlpResultType = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_VALUE_NOT_SUPPORTED; // LinkedResponseUnexpected
                        break;
                    case 7:
                        mlpResultType = MLPResponse.MLPResultType.INVALID_PROTOCOL_ELEMENT_VALUE; // LinkedResponseUnexpected
                        break;
                    default:
                        break;
                }

            this.reportLocationRequestError(mlpResultType, "Rejected, event type: " + rejectComponent.getEventTypeName() + ", " +
                    "invoke problem: " + onRejectComponentInvokeProblemType.name(), operation, targetMsisdn, targetImsi, targetImei,
                     referenceNumber, nnn, null, null, null, mlpTriggeredReportingService);
        } else {
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Rejected, event type: "
                    + rejectComponent.getEventTypeName(), operation, targetMsisdn, targetImsi, targetImei, referenceNumber, nnn,
                null, null, null, mlpTriggeredReportingService);
        }

        MAPDialog mapDialog = rejectComponent.getMAPDialog();
        GMLCCDRState gmlcCdrState = CDRCreationHelper.onMapDialogEventCdrInitializer(aci, this.getCDRInterface(), mapDialog);
        this.setTimer(aci);
        if (gmlcCdrState.isInitialized()) {
            gmlcCdrState.setCurlUser(curlUser);
            gmlcCdrState.setDialogStartTime(dialogStartTime);
            if (dialogStartTime != null) {
                Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                gmlcCdrState.setDialogDuration(dialogDuration);
            }
            gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
            gmlcCdrState.setImsi(new IMSIImpl(targetImsi));
            if (onRejectComponentInvokeProblemType != null) {
                if (onRejectComponentInvokeProblemType == InvokeProblemType.DuplicateInvokeID) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_DUPLICATE_INVOKE_ID;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_DUPLICATE_INVOKE_ID;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_DUPLICATE_INVOKE_ID;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_DUPLICATE_INVOKE_ID;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_DUPLICATE_INVOKE_ID;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_DUPLICATE_INVOKE_ID;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.UnrecognizedOperation) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_UNRECOGNIZED_OPERATION;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_UNRECOGNIZED_OPERATION;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_UNRECOGNIZED_OPERATION;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_UNRECOGNIZED_OPERATION;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_UNRECOGNIZED_OPERATION;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_UNRECOGNIZED_OPERATION;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.MistypedParameter) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_MISTYPED_PARAMETER;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_MISTYPED_PARAMETER;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_MISTYPED_PARAMETER;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_MISTYPED_PARAMETER;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_MISTYPED_PARAMETER;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_MISTYPED_PARAMETER;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.ResourceLimitation) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_RESOURCE_LIMITATION;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_RESOURCE_LIMITATION;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_RESOURCE_LIMITATION;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_RESOURCE_LIMITATION;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_RESOURCE_LIMITATION;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_RESOURCE_LIMITATION;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.InitiatingRelease) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_INITIATING_RELEASE;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_INITIATING_RELEASE;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_INITIATING_RELEASE;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_INITIATING_RELEASE;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_INITIATING_RELEASE;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_INITIATING_RELEASE;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.UnrechognizedLinkedID) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_UNRECOGNIZED_LINKED_ID;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_UNRECOGNIZED_LINKED_ID;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_UNRECOGNIZED_LINKED_ID;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_UNRECOGNIZED_LINKED_ID;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_UNRECOGNIZED_LINKED_ID;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_UNRECOGNIZED_LINKED_ID;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.LinkedResponseUnexpected) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_LINKED_RESPONSE_UNEXPECTED;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_LINKED_RESPONSE_UNEXPECTED;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_LINKED_RESPONSE_UNEXPECTED;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_LINKED_RESPONSE_UNEXPECTED;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_LINKED_RESPONSE_UNEXPECTED;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_LINKED_RESPONSE_UNEXPECTED;
                    }
                } else if (onRejectComponentInvokeProblemType == InvokeProblemType.UnexpectedLinkedOperation) {
                    if (operation != null) {
                        if (operation.equalsIgnoreCase("ATI"))
                            onRejectComponentRecordStatus = RecordStatus.ATI_UNEXPECTED_LINKED_OPERATION;
                        if (operation.equalsIgnoreCase("SRILCS"))
                            onRejectComponentRecordStatus = RecordStatus.SRILCS_UNEXPECTED_LINKED_OPERATION;
                        if (operation.equalsIgnoreCase("PSL"))
                            onRejectComponentRecordStatus = RecordStatus.PSL_UNEXPECTED_LINKED_OPERATION;
                        if (operation.equalsIgnoreCase("SRI"))
                            onRejectComponentRecordStatus = RecordStatus.SRI_UNEXPECTED_LINKED_OPERATION;
                        if (operation.equalsIgnoreCase("SRISM"))
                            onRejectComponentRecordStatus = RecordStatus.SRISM_UNEXPECTED_LINKED_OPERATION;
                        if (operation.equalsIgnoreCase("PSI"))
                            onRejectComponentRecordStatus = RecordStatus.PSI_UNEXPECTED_LINKED_OPERATION;
                    }
                }
            }
            this.createCDRRecord(onRejectComponentRecordStatus);
        }
    }

    public String handleRecordAndLocationReportOnMapError(MAPErrorMessage mapErrorMessage, MLPResponse.MLPResultType mlpRespResult,
                                                          String msisdn, String imsi, String imei, String mapOperation, Integer refNumber,
                                                          String networkNodeNumber, String addNetworkNodeNumber, GMLCCDRState gmlcCdrState,
                                                          Boolean mlpTriggeredReportingService) {

        if (gmlcCdrState != null) {
            if (gmlcCdrState.isInitialized()) {
                if (mapErrorMessage != null)
                    gmlcCdrState.setStatusCode(mapErrorMessage.getErrorCode());
            }
        }

        String mlpClientErrorMessage = "";

        if (mapErrorMessage != null) {
            /*** UNKNOWN_SUBSCRIBER ***/
            if (mapErrorMessage.getErrorCode() == 1) {
                mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                mlpClientErrorMessage = "UNKNOWN SUBSCRIBER on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("ATI"))
                    this.createCDRRecord(RecordStatus.ATI_UNKNOWN_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_UNKNOWN_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_UNKNOWN_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_UNKNOWN_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_UNKNOWN_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_UNKNOWN_SUBSCRIBER);
            }
            /*** UNKNOWN_MSC ***/
            else if (mapErrorMessage.getErrorCode() == 3) {
                mlpRespResult = MLPResponse.MLPResultType.TARGET_MOVED_TO_NEW_MSC_SGSN;
                mlpClientErrorMessage = "UNKNOWN MSC on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_UNKNOWN_MSC);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_UNKNOWN_MSC);
            }
            /*** NUMBER_CHANGED ***/
            else if (mapErrorMessage.getErrorCode() == 4) {
                mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                mlpClientErrorMessage = "NUMBER CHANGED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_NUMBER_CHANGED);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_NUMBER_CHANGED);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_NUMBER_CHANGED);
            }
            /*** UNIDENTIFIED_SUBSCRIBER ***/
            else if (mapErrorMessage.getErrorCode() == 5) {
                mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                mlpClientErrorMessage = "UNIDENTIFIED SUBSCRIBER on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_UNIDENTIFIED_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_UNIDENTIFIED_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_UNIDENTIFIED_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_UNIDENTIFIED_SUBSCRIBER);
            }
            /*** SM_ABSENT_SUBSCRIBER ***/
            else if (mapErrorMessage.getErrorCode() == 6) {
                mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                mlpClientErrorMessage = "ABSENT SUBSCRIBER SM on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                this.createCDRRecord(RecordStatus.SRISM_ABSENT_SUBSCRIBER);
            }
            /*** UNKNOWN_EQUIPMENT ***/
            else if (mapErrorMessage.getErrorCode() == 7) {
                mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                mlpClientErrorMessage = "UNKNOWN EQUIPMENT on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_UNKNOWN_EQUIPMENT);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_UNKNOWN_EQUIPMENT);
            }
            /*** ROAMING_NOT_ALLOWED ***/
            if (mapErrorMessage.getErrorCode() == 8) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "ROAMING NOT ALLOWED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_ROAMING_NOT_ALLOWED);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_ROAMING_NOT_ALLOWED);
            }
            /*** ILLEGAL_SUBSCRIBER ***/
            else if (mapErrorMessage.getErrorCode() == 9) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "ILLEGAL SUBSCRIBER on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_ILLEGAL_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_ILLEGAL_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_ILLEGAL_SUBSCRIBER);
            }
            /*** BEARER_SERVICE_NOT_PROVISIONED ***/
            else if (mapErrorMessage.getErrorCode() == 10) {
                mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                mlpClientErrorMessage = "BEARER SERVICE NOT PROVISIONED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_BEARER_SERVICE_NOT_PROVISIONED);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_BEARER_SERVICE_NOT_PROVISIONED);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_BEARER_SERVICE_NOT_PROVISIONED);
            }
            /*** TELESERVICE NOT PROVISIONED ***/
            if (mapErrorMessage.getErrorCode() == 11) {
                mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                mlpClientErrorMessage = "TELESERVICE NOT PROVISIONED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_TELESERVICE_NOT_PROVISIONED);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_TELESERVICE_NOT_PROVISIONED);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_TELESERVICE_NOT_PROVISIONED);
            }
            /*** ILLEGAL_EQUIPMENT ***/
            else if (mapErrorMessage.getErrorCode() == 12) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "ILLEGAL EQUIPMENT on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_ILLEGAL_EQUIPMENT);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_ILLEGAL_EQUIPMENT);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_ILLEGAL_EQUIPMENT);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_ILLEGAL_EQUIPMENT);
            }
            /*** CALL_BARRED ***/
            else if (mapErrorMessage.getErrorCode() == 13) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "CALL BARRED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_CALL_BARRED);
            }
            /*** FORWARDING_VIOLATION ***/
            else if (mapErrorMessage.getErrorCode() == 14) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "FORWARDING VIOLATION on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_FORWARDING_VIOLATION);
            }
            /*** CUG_REJECT ***/
            else if (mapErrorMessage.getErrorCode() == 15) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "CUG REJECT on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_CUG_REJECT);
            }
            /*** FACILITY_NOT_SUPPORTED ***/
            else if (mapErrorMessage.getErrorCode() == 21) {
                mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                mlpClientErrorMessage = "FACILITY NOT SUPPORTED on SRISM, MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_FACILITY_NOT_SUPPORTED);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_FACILITY_NOT_SUPPORTED);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_FACILITY_NOT_SUPPORTED);
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_FACILITY_NOT_SUPPORTED);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_FACILITY_NOT_SUPPORTED);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_FACILITY_NOT_SUPPORTED);
            }
            /*** ABSENT_SUBSCRIBER ***/
            else if (mapErrorMessage.getErrorCode() == 27) {
                mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                mlpClientErrorMessage = "ABSENT SUBSCRIBER on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_ABSENT_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_ABSENT_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_ABSENT_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_ABSENT_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_ABSENT_SUBSCRIBER);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_FACILITY_NOT_SUPPORTED);
            }
            /*** INCOMPATIBLE_TERMINAL ***/
            else if (mapErrorMessage.getErrorCode() == 28) {
                mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                mlpClientErrorMessage = "INCOMPATIBLE TERMINAL on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_INCOMPATIBLE_TERMINAL);
            }
            /*** SYSTEM_FAILURE ***/
            if (mapErrorMessage.getErrorCode() == 34) {
                mlpRespResult = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                mlpClientErrorMessage = "SYSTEM FAILURE on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("ATI"))
                    this.createCDRRecord(RecordStatus.ATI_SYSTEM_FAILURE);
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_SYSTEM_FAILURE);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_SYSTEM_FAILURE);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_SYSTEM_FAILURE);
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_SYSTEM_FAILURE);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_SYSTEM_FAILURE);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_SYSTEM_FAILURE);
            }
            /*** DATA_MISSING ***/
            if (mapErrorMessage.getErrorCode() == 35) {
                mlpRespResult = MLPResponse.MLPResultType.FORMAT_ERROR;
                mlpClientErrorMessage = "DATA MISSING on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("ATI"))
                    this.createCDRRecord(RecordStatus.ATI_DATA_MISSING);
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_DATA_MISSING);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_DATA_MISSING);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_DATA_MISSING);
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_DATA_MISSING);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_DATA_MISSING);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_DATA_MISSING);
            }
            /*** UNEXPECTED_DATA_VALUE ***/
            else if (mapErrorMessage.getErrorCode() == 36) {
                mlpRespResult = MLPResponse.MLPResultType.FORMAT_ERROR;
                mlpClientErrorMessage = "UNEXPECTED DATA VALUE on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("ATI"))
                    this.createCDRRecord(RecordStatus.ATI_UNEXPECTED_DATA_VALUE);
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_UNEXPECTED_DATA_VALUE);
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_UNEXPECTED_DATA_VALUE);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_UNEXPECTED_DATA_VALUE);
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_UNEXPECTED_DATA_VALUE);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_UNEXPECTED_DATA_VALUE);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_UNEXPECTED_DATA_VALUE);
            }
            /*** NUMBER_CHANGED ***/
            else if (mapErrorMessage.getErrorCode() == 44) {
                mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                mlpClientErrorMessage = "NUMBER CHANGED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_NUMBER_CHANGED);
            }
            /*** BUSY_SUBSCRIBER ***/
            else if (mapErrorMessage.getErrorCode() == 45) {
                mlpRespResult = MLPResponse.MLPResultType.CONGESTION_IN_MOBILE_NETWORK;
                mlpClientErrorMessage = "BUSY SUBSCRIBER on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_BUSY_SUBSCRIBER);
            }
            /*** OR_NOT_ALLOWED ***/
            else if (mapErrorMessage.getErrorCode() == 48) {
                mlpRespResult = MLPResponse.MLPResultType.DISALLOWED_BY_LOCAL_REGULATIONS;
                mlpClientErrorMessage = "OR NOT ALLOWED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRI"))
                    this.createCDRRecord(RecordStatus.SRI_OR_NOT_ALLOWED);
            }
            /*** ATI_NOT_ALLOWED ***/
            else if (mapErrorMessage.getErrorCode() == 49) {
                mlpRespResult = MLPResponse.MLPResultType.POSITIONING_NOT_ALLOWED;
                mlpClientErrorMessage = "ATI NOT ALLOWED, MAP error code: " + mapErrorMessage.getErrorCode();
                this.createCDRRecord(RecordStatus.ATI_NOT_ALLOWED);
            }
            /*** RESOURCE_LIMITATION ***/
            else if (mapErrorMessage.getErrorCode() == 51) {
                mlpRespResult = MLPResponse.MLPResultType.QOP_NOT_ATTAINABLE;
                mlpClientErrorMessage = "RESOURCE LIMITATION on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRISM"))
                    this.createCDRRecord(RecordStatus.SRISM_RESOURCE_LIMITATION);
                if (mapOperation.equalsIgnoreCase("PSI"))
                    this.createCDRRecord(RecordStatus.PSI_RESOURCE_LIMITATION);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_RESOURCE_LIMITATION);
            }
            /*** UNAUTHORIZED_REQUESTING_NETWORK ***/
            else if (mapErrorMessage.getErrorCode() == 52) {
                mlpRespResult = MLPResponse.MLPResultType.POSITIONING_NOT_ALLOWED;
                mlpClientErrorMessage = "SYSTEM_FAILURE on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SRILCS"))
                    this.createCDRRecord(RecordStatus.SRILCS_UNAUTHORIZED_REQUESTING_NETWORK);
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_UNAUTHORIZED_REQUESTING_NETWORK);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_UNAUTHORIZED_REQUESTING_NETWORK);
            }
            /*** UNAUTHORIZED_LCS_CLIENT ***/
            else if (mapErrorMessage.getErrorCode() == 53) {
                mlpRespResult = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                mlpClientErrorMessage = "UNAUTHORIZED LCS CLIENT on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_UNAUTHORIZED_LCS_CLIENT);
            }
            /*** POSITION_METHOD_FAILURE ***/
            else if (mapErrorMessage.getErrorCode() == 54) {
                mlpRespResult = MLPResponse.MLPResultType.POSITION_METHOD_FAILURE;
                mlpClientErrorMessage = "POSITION METHOD FAILURE on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("PSL"))
                    this.createCDRRecord(RecordStatus.PSL_POSITION_METHOD_FAILURE);
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_POSITION_METHOD_FAILURE);
            }
            /*** UNKNOWN_OR_UNREACHABLE_LCS_CLIENT ***/
            else if (mapErrorMessage.getErrorCode() == 58) {
                mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                mlpClientErrorMessage = "UNKNOWN OR UNREACHABLE LCS CLIENT on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_UNKNOWN_OR_UNREACHABLE_LCS_CLIENT);
            }
            /*** MM_EVENT_NOT_SUPPORTED ***/
            else if (mapErrorMessage.getErrorCode() == 59) {
                mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                mlpClientErrorMessage = "MM EVENT NOT SUPPORTED on " + mapOperation + ", MAP error code: " + mapErrorMessage.getErrorCode();
                if (mapOperation.equalsIgnoreCase("SLR"))
                    this.createCDRRecord(RecordStatus.SLR_MM_EVENT_NOT_SUPPORTED);
            }

        } else {
            mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
            mlpClientErrorMessage = "MAP " + mapOperation + " ERROR: " + mapErrorMessage;
            if (mapOperation.equalsIgnoreCase("ATI"))
                this.createCDRRecord(RecordStatus.ATI_ERROR);
            if (mapOperation.equalsIgnoreCase("SRI"))
                this.createCDRRecord(RecordStatus.SRI_ERROR);
            if (mapOperation.equalsIgnoreCase("SRISM"))
                this.createCDRRecord(RecordStatus.SRISM_ERROR);
            if (mapOperation.equalsIgnoreCase("PSI"))
                this.createCDRRecord(RecordStatus.PSI_ERROR);
            if (mapOperation.equalsIgnoreCase("SRILCS"))
                this.createCDRRecord(RecordStatus.SRILCS_ERROR);
            if (mapOperation.equalsIgnoreCase("PSL"))
                this.createCDRRecord(RecordStatus.PSL_ERROR);
            if (mapOperation.equalsIgnoreCase("SLR"))
                this.createCDRRecord(RecordStatus.SLR_ERROR);
        }

        if (!mapOperation.equalsIgnoreCase("SLR"))
            this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, mapOperation, msisdn, imsi, imei, refNumber, networkNodeNumber,
                addNetworkNodeNumber, null, null, mlpTriggeredReportingService);

        return mlpClientErrorMessage;
    }


    //////////////////////////
    // LTE Events handlers //
    /////////////////////////

    /**
     * LTE Location Services
     * SLh and SLg Diameter-based interfaces Events for LCS according to 3GPP TS 29.172 / 29.173
     */

    /**
     * SLh RIR Event
     */
    public void onLCSRoutingInfoRequest(LCSRoutingInfoRequest rirEvent, ActivityContextInterface aci) {
        try {
            this.logger.fine("\nReceived onLCSRoutingInfoRequest = " + rirEvent + ", ActivityContextInterface = " + aci);
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onLCSRoutingInfoRequest=%s", rirEvent), e);
        }
    }

    /**
     * SLh RIA Event
     */
    public void onLCSRoutingInfoAnswer(LCSRoutingInfoAnswer lcsRoutingInfoAnswer, ActivityContextInterface aci) {

        String imsi = null, msisdnAddress = null, imei = null, curlUser, httpRequestType;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        long resultCode = -1;
        DateTime eventTime = DateTime.now();

        try {
            logger.fine("\n\nReceived SLh RIA with session Id: " + lcsRoutingInfoAnswer.getSessionId() +
                    ", host '" + lcsRoutingInfoAnswer.getOriginHost()
                    + "', realm '" + lcsRoutingInfoAnswer.getOriginRealm() + "'");
            this.logger.fine("\nonLCSRoutingInfoAnswer event details : " + lcsRoutingInfoAnswer);
            DiameterIdentity riaOriginHost, riaOriginRealm, gmlcHost, gmlcRealm, destHost = null, destRealm = null;
            riaOriginHost = lcsRoutingInfoAnswer.getOriginHost();
            riaOriginRealm = lcsRoutingInfoAnswer.getOriginRealm();
            SLhRiaAvpValues slhRiaAvpValues = new SLhRiaAvpValues();
            byte[] msisdn = null;

            try {

                MLPResponse.MLPResultType mlpRespResult = null;
                String mlpClientErrorMessage = null;
                gmlcHost = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginHost());
                gmlcRealm = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginRealm());

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.slhSlgCdrInitializer(aci, this.getCDRInterface(), lcsRoutingInfoAnswer, null, null,
                    riaOriginHost, riaOriginRealm, gmlcHost, gmlcRealm);
                // Set timer last
                this.setTimer(aci);

                transaction = mobileCoreNetworkTransactions.getTransactionId(lcsRoutingInfoAnswer.getSessionId());
                if (transaction == null) {
                    throw new SLhException("No transaction found from RIA Diameter Session Id");
                }
                curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                httpRequestType = (String) mobileCoreNetworkTransactions.getValue(transaction, "httpRequestType");
                DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
                gmlcCdrState.setDialogStartTime(dialogStartTime);
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setCurlUser(curlUser);
                    if (dialogStartTime != null) {
                        Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                        gmlcCdrState.setDialogDuration(dialogDuration);
                    }
                }
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setCurlUser(curlUser);
                }
                TimerID riaTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                if (riaTimerID != null)
                    this.timerFacility.cancelTimer(riaTimerID);
                RequestValuesForPLR requestValuesForPLR = (RequestValuesForPLR) mobileCoreNetworkTransactions.getValue(transaction, "requestValuesForPLR");
                if (requestValuesForPLR != null) {
                    if (requestValuesForPLR.plrMsisdn != null) {
                        byte[] tbcdMsisdn = parseTBCD(requestValuesForPLR.plrMsisdn);
                        msisdnAddress = toTBCDString(tbcdMsisdn);
                    }
                    if (requestValuesForPLR.plrUserName != null)
                        imsi = requestValuesForPLR.plrUserName;
                    if (requestValuesForPLR.plrLcsReferenceNumber != null)
                        referenceNumber = requestValuesForPLR.plrLcsReferenceNumber;
                }

                if (lcsRoutingInfoAnswer.hasResultCode()) {
                    resultCode = lcsRoutingInfoAnswer.getResultCode();
                } else if (lcsRoutingInfoAnswer.hasExperimentalResult()) {
                    resultCode = lcsRoutingInfoAnswer.getExperimentalResult().getExperimentalResultCode();
                }

                if (resultCode == 2001) {

                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setStatusCode(resultCode);
                    }

                    if (lcsRoutingInfoAnswer.hasUserName()) {
                        imsi = lcsRoutingInfoAnswer.getUserName();
                        this.logger.fine("\nonLCSRoutingInfoAnswer, User-Name AVP (IMSI): " + imsi);
                        slhRiaAvpValues.setUserName(imsi);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setImsi(AVPHandler.userName2Imsi(imsi));
                        }
                    }

                    if (lcsRoutingInfoAnswer.hasMSISDN()) {
                        msisdn = lcsRoutingInfoAnswer.getMSISDN();
                        this.logger.fine("\nonLCSRoutingInfoAnswer, MSISDN AVP: " + Arrays.toString(msisdn));
                        slhRiaAvpValues.setMsisdn(msisdn);
                        msisdnAddress = AVPHandler.tbcd2IsdnAddressString(msisdn).getAddress();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setISDNAddressString(AVPHandler.tbcd2IsdnAddressString(msisdn));
                        }
                    }

                    if (lcsRoutingInfoAnswer.hasLMSI()) {
                        this.logger.fine("\nonLCSRoutingInfoAnswer, LMSI AVP: " + Arrays.toString(lcsRoutingInfoAnswer.getLMSI()));
                        slhRiaAvpValues.setLmsi(lcsRoutingInfoAnswer.getLMSI());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLmsi(AVPHandler.byte2Lmsi(lcsRoutingInfoAnswer.getLMSI()));
                        }
                    }

                    DiameterIdentity sgsnName, sgsnRealm, mmeName, mmeRealm;
                    sgsnName = sgsnRealm = mmeName = mmeRealm = null;
                    long lcsCapabilitiesSets = -1;
                    Address servingNodeGmlcAddress = null;
                    if (lcsRoutingInfoAnswer.hasServingNode()) {
                        this.logger.fine("\nonLCSRoutingInfoAnswer, Serving-Node AVP: " + lcsRoutingInfoAnswer.getServingNode());
                        slhRiaAvpValues.setServingNodeAvp(lcsRoutingInfoAnswer.getServingNode());
                        byte[] sgsnNumber = lcsRoutingInfoAnswer.getServingNode().getSGSNNumber();
                        sgsnName = lcsRoutingInfoAnswer.getServingNode().getSGSNName();
                        sgsnRealm = lcsRoutingInfoAnswer.getServingNode().getSGSNRealm();
                        mmeName = lcsRoutingInfoAnswer.getServingNode().getMMEName();
                        mmeRealm = lcsRoutingInfoAnswer.getServingNode().getMMERealm();
                        byte[] mscNumber = lcsRoutingInfoAnswer.getServingNode().getMSCNumber();
                        DiameterIdentity tgppAAAServerName = lcsRoutingInfoAnswer.getServingNode().get3GPPAAAServerName();
                        if (lcsRoutingInfoAnswer.getServingNode().hasLcsCapabilitiesSets())
                            lcsCapabilitiesSets = lcsRoutingInfoAnswer.getServingNode().getLcsCapabilitiesSets();
                        servingNodeGmlcAddress = lcsRoutingInfoAnswer.getServingNode().getGMLCAddress();
                        if (gmlcCdrState.isInitialized()) {
                            if (sgsnNumber != null)
                                gmlcCdrState.setSgsnNumber(AVPHandler.tbcd2IsdnAddressString(sgsnNumber));
                            if (sgsnName != null)
                                gmlcCdrState.setSgsnName(AVPHandler.diameterIdToMapDiameterId(sgsnName));
                            if (sgsnRealm != null)
                                gmlcCdrState.setSgsnRealm(AVPHandler.diameterIdToMapDiameterId(sgsnRealm));
                            if (mmeName != null)
                                gmlcCdrState.setMmeName(AVPHandler.diameterIdToMapDiameterId(mmeName));
                            if (mmeRealm != null)
                                gmlcCdrState.setMmeRealm(AVPHandler.diameterIdToMapDiameterId(mmeRealm));
                            if (mscNumber != null)
                                gmlcCdrState.setMscNumber(AVPHandler.tbcd2IsdnAddressString(mscNumber));
                            if (tgppAAAServerName != null)
                                gmlcCdrState.setAaaServerName(AVPHandler.diameterIdToMapDiameterId(tgppAAAServerName));
                            if (lcsCapabilitiesSets > -1)
                                gmlcCdrState.setLcsCapabilitiesSets(lcsCapabilitiesSets);
                            if (servingNodeGmlcAddress != null)
                                gmlcCdrState.sethGmlcAddress(AVPHandler.address2GsnAddress(servingNodeGmlcAddress));
                        }
                    }

                    DiameterIdentity additionalSgsnName, additionalSgsnRealm, additionalMmeName, additionalMmeRealm;
                    additionalSgsnName = additionalSgsnRealm = additionalMmeName = additionalMmeRealm = null;
                    long addLcsCapabilitiesSets  = -1;
                    Address additionalGmlcAddress = null;
                    if (lcsRoutingInfoAnswer.hasAdditionalServingNode()) {
                        this.logger.fine("\nonLCSRoutingInfoAnswer, Additional-Serving-Node AVP: " + lcsRoutingInfoAnswer.getAdditionalServingNode());
                        slhRiaAvpValues.setAdditionalServingNodeAvp(lcsRoutingInfoAnswer.getAdditionalServingNode());
                        byte[] additionalSgsnNumber = lcsRoutingInfoAnswer.getAdditionalServingNode().getSGSNNumber();
                        additionalSgsnName = lcsRoutingInfoAnswer.getAdditionalServingNode().getSGSNName();
                        additionalSgsnRealm = lcsRoutingInfoAnswer.getAdditionalServingNode().getSGSNRealm();
                        additionalMmeName = lcsRoutingInfoAnswer.getAdditionalServingNode().getMMEName();
                        additionalMmeRealm = lcsRoutingInfoAnswer.getAdditionalServingNode().getMMERealm();
                        byte[] additionalMscNumber = lcsRoutingInfoAnswer.getAdditionalServingNode().getMSCNumber();
                        DiameterIdentity additional3gppAAAServerName = lcsRoutingInfoAnswer.getAdditionalServingNode().get3GPPAAAServerName();
                        if (lcsRoutingInfoAnswer.getAdditionalServingNode().hasLcsCapabilitiesSets())
                            addLcsCapabilitiesSets = lcsRoutingInfoAnswer.getAdditionalServingNode().getLcsCapabilitiesSets();
                        additionalGmlcAddress = lcsRoutingInfoAnswer.getAdditionalServingNode().getGMLCAddress();
                        if (gmlcCdrState.isInitialized()) {
                            if (additionalSgsnNumber != null)
                                gmlcCdrState.setSgsnNumber(AVPHandler.tbcd2IsdnAddressString(additionalSgsnNumber));
                            if (additionalSgsnName != null)
                                gmlcCdrState.setSgsnName(AVPHandler.diameterIdToMapDiameterId(additionalSgsnName));
                            if (additionalSgsnRealm != null)
                                gmlcCdrState.setSgsnRealm(AVPHandler.diameterIdToMapDiameterId(additionalSgsnRealm));
                            if (additionalMmeName != null)
                                gmlcCdrState.setMmeName(AVPHandler.diameterIdToMapDiameterId(additionalMmeName));
                            if (additionalMmeRealm != null)
                                gmlcCdrState.setMmeRealm(AVPHandler.diameterIdToMapDiameterId(additionalMmeRealm));
                            if (additionalMscNumber != null)
                                gmlcCdrState.setMscNumber(AVPHandler.tbcd2IsdnAddressString(additionalMscNumber));
                            if (additional3gppAAAServerName != null)
                                gmlcCdrState.setAaaServerName(AVPHandler.diameterIdToMapDiameterId(additional3gppAAAServerName));
                            if (addLcsCapabilitiesSets > -1)
                                gmlcCdrState.setLcsCapabilitiesSets(addLcsCapabilitiesSets);
                            if (additionalGmlcAddress != null)
                                gmlcCdrState.sethGmlcAddress(AVPHandler.address2GsnAddress(additionalGmlcAddress));
                        }
                    }

                    Address gmlcAddress = null;
                    if (lcsRoutingInfoAnswer.hasGMLCAddress()) {
                        gmlcAddress = lcsRoutingInfoAnswer.getGMLCAddress();
                        this.logger.fine("\nonLCSRoutingInfoAnswer, GMLC-Address AVP: " + lcsRoutingInfoAnswer.getGMLCAddress());
                        slhRiaAvpValues.setGmlcAddress(lcsRoutingInfoAnswer.getGMLCAddress());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.sethGmlcAddress(AVPHandler.address2GsnAddress(lcsRoutingInfoAnswer.getGMLCAddress()));
                        }
                    }

                    if (lcsRoutingInfoAnswer.hasPPRAddress()) {
                        this.logger.fine("\nonLCSRoutingInfoAnswer, PPR-Address AVP: " + lcsRoutingInfoAnswer.getPPRAddress());
                        slhRiaAvpValues.setPprAddress(lcsRoutingInfoAnswer.getPPRAddress());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setPprAddress(AVPHandler.address2GsnAddress(lcsRoutingInfoAnswer.getPPRAddress()));
                        }
                    }

                    long riaFlags = -1;
                    if (lcsRoutingInfoAnswer.hasRIAFlags()) {
                        riaFlags = lcsRoutingInfoAnswer.getRIAFlags();
                        /*
                        6.4.15	RIA-Flags
                        The RIA-Flags AVP is of type Unsigned32, and it shall contain a bit mask.
                        The meaning of the bits shall be as defined in table 6.4.15/1:
                        Table 6.4.15/1: RIA-Flags
                        Bit Name                                            Description
                        0   Combined-MME/SGSN-Supporting-Optimized-LCS-Proc This bit, when set, indicates that the UE is served
                                                                            by the MME and the SGSN parts of the same
                                                                            combined MME/SGSN and this combined MME/SGSN
                                                                            supports the optimized LCS procedure.
                        NOTE1: Bits not defined in this table shall be cleared by the sending HSS and discarded by the receiving GMLC.
                         */
                        this.logger.fine("\nonLCSRoutingInfoAnswer, RIA-Flags AVP: " + riaFlags);
                        slhRiaAvpValues.setRiaFLags(riaFlags);
                    }

                    if (sgsnName != null) {
                        if (!sgsnName.toString().isEmpty())
                            destHost = sgsnName;
                    } else if (additionalSgsnName != null) {
                        if (!additionalSgsnName.toString().isEmpty())
                            destHost = additionalSgsnName;
                    }

                    if (sgsnRealm != null) {
                        if (!sgsnRealm.toString().isEmpty()) {
                            destRealm = sgsnRealm;
                        }
                    } else if (additionalSgsnRealm != null) {
                        if (!additionalSgsnRealm.toString().isEmpty()) {
                            destRealm = additionalSgsnRealm;
                        }
                    }

                    if (mmeName != null) {
                        if (!mmeName.toString().isEmpty())
                            destHost = mmeName;
                    } else if (additionalMmeName != null) {
                        if (!additionalMmeName.toString().isEmpty())
                            destHost = additionalMmeName;
                    }

                    if (mmeRealm != null) {
                        if (!mmeRealm.toString().isEmpty()) {
                            destRealm = mmeRealm;
                        }
                    } else if (additionalMmeRealm != null) {
                        if (!additionalMmeRealm.toString().isEmpty()) {
                            destRealm = additionalMmeRealm;
                        }
                    }

                    if (destHost == null || destRealm == null) {
                        throw new SLhException("MME or SGSN hostname/realm not found in RIA Serving-Node AVP");
                    }

                    this.logger.fine(String.format("\nRecovered requestValuesForPLR as %s object from Diameter session '%s'",
                            "valid", lcsRoutingInfoAnswer.getSessionId()));

                    SLgClientSessionActivity slgClientSessionActivity = this.slgProvider.createSLgClientSessionActivity(destHost, destRealm);
                    // Keep ACI in across Diameter session for PLR/PLA
                    ActivityContextInterface slgACIF = slgAcif.getActivityContextInterface(slgClientSessionActivity);
                    slgACIF.attach(this.sbbContext.getSbbLocalObject());

                    // < Provide-Location-Request> ::=	< Diameter Header: 8388620, REQ, PXY, 16777255 >
                    ProvideLocationRequest plr = slgClientSessionActivity.createProvideLocationRequest();

                    SLgLocationType slgLocationType;
                    LCSEPSClientNameAvp lcsEpsClientNameAvp;
                    LCSRequestorNameAvp lcsRequestorNameAvp;
                    LCSFormatIndicator lcsFormatIndicator;
                    LCSClientType lcsClientType;
                    LCSFormatIndicator lcsReqFormatIndicator;
                    LCSQoSClass lcsQoSClass;
                    VerticalRequested verticalRequested;
                    VelocityRequested velocityRequested;
                    LCSPrivacyCheckSessionAvp lcsPrivacyCheckSessionAvp;
                    net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheck lcsPrivacyCheckSession;
                    LCSPrivacyCheckNonSessionAvp lcsPrivacyCheckNonSessionAvp;
                    net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheck lcsPrivacyCheckNonSession;
                    LCSQoSAvp lcsQoSAvp;
                    net.java.slee.resource.diameter.slg.events.avp.ResponseTime responseTime;
                    PLMNIDListAvp plmnidListAvp;
                    AreaEventInfoAvp areaEventInfoAvp = null;
                    AreaDefinitionAvp areaDefinitionAvp;
                    AreaAvp areaAvp;
                    AdditionalAreaAvp additionalAreaAvp = null;
                    net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo areaEventOccurrenceInfo;
                    MotionEventInfoAvp motionEventInfoAvp = null;
                    net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo motionEventOccurrenceInfo;
                    PeriodicLDRInfoAvp periodicLDRInfoAvp;
                    ReportingPLMNListAvp reportingPLMNListAvp;
                    PeriodicLocationSupportIndicator periodicLocationSupportIndicator = null;
                    PrioritizedListIndicator prioritizedListIndicator = null;

                    if (requestValuesForPLR != null) {

                        /*** PLR conditional AVP: [ User-Name ] ***/
                        if (imsi != null) {
                            plr.setUserName(imsi);
                        } else {
                            plr.setUserName(requestValuesForPLR.plrUserName);
                        }

                        /*** PLR conditional AVP: [ MSISDN ] ***/
                        if (msisdn != null) {
                            plr.setMSISDN(msisdn);
                        } else {
                            plr.setMSISDN(parseTBCD(requestValuesForPLR.plrMsisdn));
                        }

                        /*** PLR mandatory AVP: { SLg-Location-Type } ***/
                        // SLg-Location-Type AVP is of type Enumerated. The following values are defined:
                        // CURRENT_LOCATION (0), CURRENT_OR_LAST_KNOWN_LOCATION (1), INITIAL_LOCATION (2)
                        // ACTIVATE_DEFERRED_LOCATION (3), CANCEL_DEFERRED_LOCATION (4), NOTIFICATION_VERIFICATION_ONLY (5)
                        if (requestValuesForPLR.plrSlgLocationType != null) {
                            if (requestValuesForPLR.plrSlgLocationType == SLgLocationType._CURRENT_LOCATION ||
                                requestValuesForPLR.plrSlgLocationType == SLgLocationType._CURRENT_OR_LAST_KNOWN_LOCATION ||
                                requestValuesForPLR.plrSlgLocationType == SLgLocationType._INITIAL_LOCATION ||
                                requestValuesForPLR.plrSlgLocationType == SLgLocationType._ACTIVATE_DEFERRED_LOCATION ||
                                requestValuesForPLR.plrSlgLocationType == SLgLocationType._CANCEL_DEFERRED_LOCATION ||
                                requestValuesForPLR.plrSlgLocationType == SLgLocationType._NOTIFICATION_VERIFICATION_ONLY) {
                                slgLocationType = SLgLocationType.fromInt(requestValuesForPLR.plrSlgLocationType);
                                plr.setSLgLocationType(slgLocationType);
                            }
                        }

                        /*** PLR mandatory AVP: { LCS-EPS-Client-Name } ***/
                        // The LCS-EPS-Client-Name AVP is of type Grouped.
                        //   LCS-EPS-Client-Name ::= <AVP header: 2501 10415>
                        //      [ LCS-Name-String ]
                        //      [ LCS-Format-Indicator ]
                        if (requestValuesForPLR.plrLcsNameString != null && requestValuesForPLR.plrLcsFormatInd != null) {
                            lcsEpsClientNameAvp = this.slgAVPFactory.createLCSEPSClientName();
                            // [ LCS-Format-Indicator ]
                            // The LCS-Format-Indicator AVP is of type Enumerated
                            // and contains the format of the LCS Client name.
                            // It can be one of the following values: 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), 2 (MSISDN), 3 URL, 4 SIP_URL
                            lcsFormatIndicator = LCSFormatIndicator.fromInt(requestValuesForPLR.plrLcsFormatInd);
                            lcsEpsClientNameAvp.setLCSFormatIndicator(lcsFormatIndicator);
                            // [ LCS-Name-String ]
                            // The LCS-Name-String AVP is of type UTF8String and contains the LCS Client name.
                            lcsEpsClientNameAvp.setLCSNameString(requestValuesForPLR.plrLcsNameString);
                            // { LCS-EPS-Client-Name }
                            plr.setLCSEPSClientName(lcsEpsClientNameAvp);
                        }

                        /*** PLR mandatory AVP: { LCS-Client-Type } ***/
                        // The LCS-Client-Type AVP is of type Enumerated and contains the type of services requested by the LCS Client.
                        // It can be one of the following values: 0	EMERGENCY_SERVICES, 1 VALUE_ADDED_SERVICES,
                        // 2 PLMN_OPERATOR_SERVICES, 3 LAWFUL_INTERCEPT_SERVICES
                        if (requestValuesForPLR.plrLcsClientType != null) {
                            if (requestValuesForPLR.plrLcsClientType == LCSClientType._EMERGENCY_SERVICES ||
                                requestValuesForPLR.plrLcsClientType == LCSClientType._VALUE_ADDED_SERVICES ||
                                requestValuesForPLR.plrLcsClientType == LCSClientType._PLMN_OPERATOR_SERVICES ||
                                requestValuesForPLR.plrLcsClientType == LCSClientType._LAWFUL_INTERCEPT_SERVICES) {
                                lcsClientType = LCSClientType.fromInt(requestValuesForPLR.plrLcsClientType);
                                plr.setLCSClientType(lcsClientType);
                            }
                        }

                        /*** PLR optional AVP: [ LCS-Requestor-Name ] ***/
                        if (requestValuesForPLR.plrLcsRequestorIdString != null) {
                            if (requestValuesForPLR.plrLcsRequestorFormatIndicator != null) {
                                if (requestValuesForPLR.plrLcsRequestorFormatIndicator == LCSFormatIndicator._LOGICAL_NAME ||
                                    requestValuesForPLR.plrLcsRequestorFormatIndicator == LCSFormatIndicator._EMAIL_ADDRESS ||
                                    requestValuesForPLR.plrLcsRequestorFormatIndicator == LCSFormatIndicator._MSISDN ||
                                    requestValuesForPLR.plrLcsRequestorFormatIndicator == LCSFormatIndicator._URL ||
                                    requestValuesForPLR.plrLcsRequestorFormatIndicator == LCSFormatIndicator._SIP_URL) {
                                    lcsRequestorNameAvp = this.slgAVPFactory.createLCSRequestorName();
                                    lcsRequestorNameAvp.setLCSRequestorIDString(requestValuesForPLR.plrLcsRequestorIdString);
                                    lcsReqFormatIndicator = LCSFormatIndicator.fromInt(requestValuesForPLR.plrLcsRequestorFormatIndicator);
                                    if (lcsReqFormatIndicator != null) {
                                        lcsRequestorNameAvp.setLCSFormatIndicator(lcsReqFormatIndicator);
                                        plr.setLCSRequestorName(lcsRequestorNameAvp);
                                    }
                                }
                            }
                        }

                        /*** PLR optional AVP: [ LCS-QoS ] ***/
                        // LCS-QoS ::= <AVP header: 2504 10415>
                        //      [ LCS-QoS-Class ]
                        //      [ Horizontal-Accuracy ]
                        //      [ Vertical-Accuracy ]
                        //      [ Vertical-Requested ]
                        //      [ Response-Time]
                        if (requestValuesForPLR.plrQoSClass != null || requestValuesForPLR.plrHorizontalAccuracy != null ||
                            requestValuesForPLR.plrVerticalAccuracy != null || requestValuesForPLR.plrVerticalRequested != null ||
                            requestValuesForPLR.plrResponseTime != null) {
                            lcsQoSAvp = this.slgAVPFactory.createLCSQoS();
                            // [ LCS-QoS-Class ]
                            // The LCS-QoS-Class AVP is of the type Enumerated. The following values are defined: ASSURED (0), BEST EFFORT (1)
                            if (requestValuesForPLR.plrQoSClass != null) {
                                if (requestValuesForPLR.plrQoSClass == LCSQoSClass._ASSURED ||
                                    requestValuesForPLR.plrQoSClass == LCSQoSClass._BEST_EFFORT) {
                                    lcsQoSClass = LCSQoSClass.fromInt(requestValuesForPLR.plrQoSClass);
                                    lcsQoSAvp.setLCSQoSClass(lcsQoSClass);
                                }
                            } else {
                                lcsQoSAvp.setLCSQoSClass(LCSQoSClass.BEST_EFFORT);
                            }
                            // [ Horizontal-Accuracy ]
                            // The Horizontal-Accuracy AVP is of type Unsigned32.
                            // Bits 6-0 corresponds to Uncertainty Code defined in 3GPP TS 23.032 [3].
                            // The horizontal location error should be less than the error indicated by the uncertainty code with 67% confidence.
                            // Bits 7 to 31 shall be ignored.
                            if (requestValuesForPLR.plrHorizontalAccuracy != null) {
                                if (requestValuesForPLR.plrHorizontalAccuracy > -1 && requestValuesForPLR.plrHorizontalAccuracy < 128)
                                    lcsQoSAvp.setHorizontalAccuracy(requestValuesForPLR.plrHorizontalAccuracy);
                            }
                            // [ Vertical-Accuracy ]
                            // The Vertical-Accuracy AVP is of type Unsigned32.
                            // Bits 6-0 corresponds to Uncertainty Code defined in 3GPP TS 23.032 [3].
                            // The vertical location error should be less than the error indicated by the uncertainty code with 67% confidence.
                            // Bits 7 to 31 shall be ignored
                            if (requestValuesForPLR.plrVerticalAccuracy != null) {
                                if (requestValuesForPLR.plrVerticalAccuracy > -1 && requestValuesForPLR.plrVerticalAccuracy < 128)
                                    lcsQoSAvp.setVerticalAccuracy(requestValuesForPLR.plrVerticalAccuracy);
                            }
                            // [ Vertical-Requested ]
                            // The Vertical-Requested AVP is of type Enumerated. The following values are defined:
                            //	VERTICAL_COORDINATE_IS_NOT REQUESTED (0)
                            //	VERTICAL_COORDINATE_IS_REQUESTED (1)
                            // Default value if AVP is not present is: VERTICAL_COORDINATE_IS_NOT_REQUESTED (0).
                            if (requestValuesForPLR.plrVerticalRequested != null) {
                                if (requestValuesForPLR.plrVerticalRequested == VerticalRequested._VERTICAL_COORDINATE_IS_NOT_REQUESTED ||
                                    requestValuesForPLR.plrVerticalRequested == VerticalRequested._VERTICAL_COORDINATE_IS_REQUESTED) {
                                    verticalRequested = VerticalRequested.fromInt(requestValuesForPLR.plrVerticalRequested);
                                    lcsQoSAvp.setVerticalRequested(verticalRequested);
                                }
                            } else {
                                // Default value if Vertical-Requested AVP is not present is: VERTICAL_COORDINATE_IS_NOT_REQUESTED (0)
                                lcsQoSAvp.setVerticalRequested(VerticalRequested.VERTICAL_COORDINATE_IS_NOT_REQUESTED);
                            }
                            // [ Response-Time]
                            // The Response-Time AVP is of type Enumerated. The following values are defined: LOW_DELAY (0), DELAY_TOLERANT (1)
                            if (requestValuesForPLR.plrResponseTime != null) {
                                if (requestValuesForPLR.plrResponseTime == net.java.slee.resource.diameter.slg.events.avp.ResponseTime._LOW_DELAY ||
                                    requestValuesForPLR.plrResponseTime == net.java.slee.resource.diameter.slg.events.avp.ResponseTime._DELAY_TOLERANT) {
                                    responseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime.fromInt((requestValuesForPLR.plrResponseTime));
                                    lcsQoSAvp.setResponseTime(responseTime);
                                }
                            } else {
                                lcsQoSAvp.setResponseTime(net.java.slee.resource.diameter.slg.events.avp.ResponseTime.DELAY_TOLERANT);
                            }
                            plr.setLCSQoS(lcsQoSAvp);
                            if (gmlcCdrState.isInitialized()) {
                                LCSQoS lcsQoS = AVPHandler.lteLcsQos2MapLcsQoS(lcsQoSAvp);
                                gmlcCdrState.setLcsQoS(lcsQoS);
                            }
                        }

                        /*** PLR optional AVP: [ LCS-Priority ] ***/
                        if (requestValuesForPLR.plrLcsPriority != null)
                            plr.setLCSPriority(requestValuesForPLR.plrLcsPriority);

                        /*** PLR optional AVP: [ LCS-Service-Type-ID ] ***/
                        if (requestValuesForPLR.plrLcsServiceTypeId != null)
                            plr.setLCSServiceTypeID(requestValuesForPLR.plrLcsServiceTypeId);

                        /*** PLR optional AVP: [ Velocity-Requested ] ***/
                        if (requestValuesForPLR.plrVelocityRequested != null) {
                            velocityRequested = VelocityRequested.fromInt(requestValuesForPLR.plrVelocityRequested);
                            plr.setVelocityRequested(velocityRequested);
                        }

                        /*** PLR optional AVP: [ LCS-Privacy-Check-Non-Session ] ***/
                        if (requestValuesForPLR.plrPrivacyCheckNonSession != null) {
                            if (requestValuesForPLR.plrPrivacyCheckNonSession > -1) {
                                lcsPrivacyCheckNonSession = net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheck.fromInt(requestValuesForPLR.plrPrivacyCheckNonSession);
                                lcsPrivacyCheckNonSessionAvp = this.slgAVPFactory.createLCSPrivacyCheckNonSession();
                                lcsPrivacyCheckNonSessionAvp.setLCSPrivacyCheck(lcsPrivacyCheckNonSession);
                                plr.setLCSPrivacyCheckNonSession(lcsPrivacyCheckNonSessionAvp);
                            }
                        }

                        /*** PLR optional AVP: [ LCS-Privacy-Check-Session ] ***/
                        if (requestValuesForPLR.plrPrivacyCheckSession != null) {
                            if (requestValuesForPLR.plrPrivacyCheckSession > -1) {
                                lcsPrivacyCheckSession = net.java.slee.resource.diameter.slg.events.avp.LCSPrivacyCheck.fromInt(requestValuesForPLR.plrPrivacyCheckSession);
                                lcsPrivacyCheckSessionAvp = this.slgAVPFactory.createLCSPrivacyCheckSession();
                                lcsPrivacyCheckSessionAvp.setLCSPrivacyCheck(lcsPrivacyCheckSession);
                                plr.setLCSPrivacyCheckSession(lcsPrivacyCheckSessionAvp);
                            }
                        }

                        /*** PLR optional AVP: [ Deferred-Location-Type ] ***/
                        if (requestValuesForPLR.plrDeferredLocationType != null)
                            plr.setDeferredLocationType(requestValuesForPLR.plrDeferredLocationType);

                        /*** PLR optional AVP: [ Area-Event-Info ] ***/
                        // Area-Event-Info grouped AVP
                        // Area-Event-Info ::= <AVP header: 2533 10415>
                        //      { Area-Definition }
                        //      [ Occurrence-Info ]
                        //      [ Interval-Time ]
                        //      [ Maximum-Interval ]
                        //      [ Sampling-Interval ]
                        //      [ Reporting-Duration ]
                        //      [ Reporting-Location-Requirements ]
                        // Area-Definition grouped AVP
                        // Area-Definition ::= <AVP header: 2534 10415>
                        //        1*10{ Area }
                        //        *240[ Additional-Area ]
                        // Area grouped AVP
                        // Area ::= <AVP header: 2535 10415>
                        //      { Area-Type }
                        //      { Area-Identification }

                        // { Area-Definition }
                        // { Area }
                        if (requestValuesForPLR.plrAreaType != null) {
                            // AreaDefinitionAvp areaDefinitionAvp = this.slgAVPFactory.createAreaDefinition();
                            /* AreaAvp areaAvp = this.slgAVPFactory.createArea(); */
                            /*AdditionalAreaAvp additionalAreaAvp = this.slgAVPFactory.createAdditionalArea();*/
                            // { Area-Type } of { Area }
                            // The Area-Type AVP is of type Unsigned32. The following values are defined:
                            // "Country Code" 0, "PLMN ID", "Location Area ID 2, "Routing Area ID"	3, "Cell Global ID"	4, "UTRAN Cell ID"	5,
                            // "Tracking Area ID" 6 and "E-UTRAN Cell Global ID" 7
                            if (requestValuesForPLR.plrAreaType >= 0 &&
                                requestValuesForPLR.plrAreaType <= 7) {
                                // { Area-Identification } of { Area }
                                // The Area-Identification AVP is of type OctetString and shall contain the identification
                                // of the area applicable for the change of area event based deferred location reporting.
                                if (requestValuesForPLR.plrAreaIdentification != null) {
                                    String[] areaIdArray = requestValuesForPLR.plrAreaIdentification.split("-");
                                    long lcsAreaTypeValue = requestValuesForPLR.plrAreaType;
                                    String lcsAreaType = convertAreaTypeToString(lcsAreaTypeValue);
                                    byte[] areaIdTbcd = setAreaIdToTbcd(areaIdArray, lcsAreaType);
                                    areaAvp = this.slgAVPFactory.createArea();
                                    areaAvp.setAreaType(requestValuesForPLR.plrAreaType);
                                    areaAvp.setAreaIdentification(areaIdTbcd);
                                    // [ Additional-Area ]
                                    if (requestValuesForPLR.plrAdditionalAreaType != null) {
                                        // { Area-Type } of [ Additional-Area ]
                                        if (requestValuesForPLR.plrAdditionalAreaType >= 0 &&
                                            requestValuesForPLR.plrAdditionalAreaType <= 7) {
                                            // { Area-Identification } of [ Additional-Area ]
                                            if (requestValuesForPLR.plrAdditionalAreaIdentification != null) {
                                                String[] additionalAreaIdArray = requestValuesForPLR.plrAdditionalAreaIdentification.split("-");
                                                long lcsAdditionalAreaTypeValue = requestValuesForPLR.plrAdditionalAreaType;
                                                String lcsAdditionalAreaType = convertAreaTypeToString(lcsAdditionalAreaTypeValue);
                                                byte[] additionalAreaIdTbcd = setAreaIdToTbcd(additionalAreaIdArray, lcsAdditionalAreaType);
                                                additionalAreaAvp = this.slgAVPFactory.createAdditionalArea();
                                                additionalAreaAvp.setAreaType(requestValuesForPLR.plrAdditionalAreaType);
                                                additionalAreaAvp.setAreaIdentification(additionalAreaIdTbcd);
                                            }
                                        }
                                    }
                                    areaDefinitionAvp = this.slgAVPFactory.createAreaDefinition();
                                    areaDefinitionAvp.setArea(areaAvp);
                                    if (additionalAreaAvp != null)
                                        areaDefinitionAvp.setAdditionalArea(additionalAreaAvp);
                                    areaEventInfoAvp = this.slgAVPFactory.createAreaEventInfo();
                                    areaEventInfoAvp.setAreaDefinition(areaDefinitionAvp);
                                }
                            }
                        }
                        // [ Occurrence-Info ]
                        if (requestValuesForPLR.plrAreaEventOccurrenceInfo != null) {
                            if (areaEventInfoAvp != null) {
                                if (requestValuesForPLR.plrAreaEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT ||
                                    requestValuesForPLR.plrAreaEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                    areaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo.fromInt(requestValuesForPLR.plrAreaEventOccurrenceInfo);
                                    areaEventInfoAvp.setOccurrenceInfo(areaEventOccurrenceInfo);
                                }
                            }
                        } else {
                            if (areaEventInfoAvp != null) {
                                // If not included,
                                // the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
                                areaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo.ONE_TIME_EVENT;
                                areaEventInfoAvp.setOccurrenceInfo(areaEventOccurrenceInfo);
                            }
                        }
                        // [ Interval-Time ]
                        if (requestValuesForPLR.plrAreaEventIntervalTime != null) {
                            // Interval-Time AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (areaEventInfoAvp != null) {
                                if (areaEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (areaEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        // The Interval-Time AVP (Unsigned32) contains the minimum time interval
                                        // between area reports or motion reports, in seconds.
                                        // The minimum value shall be 1 second and the maximum value 32767 seconds.
                                        if (requestValuesForPLR.plrAreaEventIntervalTime >= 1 &&
                                            requestValuesForPLR.plrAreaEventIntervalTime <= 32767) {
                                            areaEventInfoAvp.setIntervalTime(requestValuesForPLR.plrAreaEventIntervalTime);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (areaEventInfoAvp != null) {
                                if (areaEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (areaEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        // If not included,
                                        // the default value of Interval-Time shall be considered as one.
                                        areaEventInfoAvp.setIntervalTime(1);
                                    }
                                }
                            }
                        }
                        // [ Maximum-Interval ]
                        if (requestValuesForPLR.plrAreaEventMaxInterval != null) {
                            // The Maximum-Interval AVP (Unsigned32) contains the maximum time interval between consecutive event reports, in seconds.
                            // The minimum value shall be 1 second and the maximum value 86400 seconds.
                            //  Maximum-Interval AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (areaEventInfoAvp != null) {
                                if (areaEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (areaEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        if (requestValuesForPLR.plrAreaEventMaxInterval >= 1 &&
                                            requestValuesForPLR.plrAreaEventMaxInterval <= 86400) {
                                            areaEventInfoAvp.setMaximumInterval(requestValuesForPLR.plrAreaEventMaxInterval);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (areaEventInfoAvp != null) {
                                if (areaEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (areaEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        // If not included,
                                        // the default value of Maximum-Interval shall be considered as the maximum value.
                                        areaEventInfoAvp.setMaximumInterval(86400);
                                    }
                                }
                            }
                        }
                        // [ Sampling-Interval ]
                        if (requestValuesForPLR.plrAreaEventSamplingInterval != null) {
                            // The Sampling-Interval AVP (Unsigned32) contains the maximum time interval between consecutive evaluations by a UE
                            // of an area event or motion event, in seconds.
                            // The minimum value shall be 1 second and the maximum value 3600 seconds.
                            if (areaEventInfoAvp != null) {
                                if (requestValuesForPLR.plrAreaEventSamplingInterval >= 1 &&
                                    requestValuesForPLR.plrAreaEventSamplingInterval <= 3600) {
                                    areaEventInfoAvp.setSamplingInterval(requestValuesForPLR.plrAreaEventSamplingInterval);
                                }
                            }
                        } else {
                            if (areaEventInfoAvp != null) {
                                // If not included,
                                // the default value of Sampling-Interval shall be considered as the maximum value.
                                areaEventInfoAvp.setSamplingInterval(3600);
                            }
                        }
                        // [ Reporting-Duration ]
                        if (requestValuesForPLR.plrAreaEventReportingDuration != null) {
                            // The Reporting-Duration AVP (Unsigned32) contains the maximum duration of event reporting, in seconds.
                            // Its minimum value shall be 1 and maximum value shall be 8640000.
                            if (areaEventInfoAvp != null) {
                                if (requestValuesForPLR.plrAreaEventReportingDuration >= 1 &&
                                    requestValuesForPLR.plrAreaEventReportingDuration <= 8640000) {
                                    areaEventInfoAvp.setReportDuration(requestValuesForPLR.plrAreaEventReportingDuration);
                                }
                            }
                        } else {
                            if (areaEventInfoAvp != null) {
                                // If not included,
                                // the default value of Reporting-Duration shall be considered as the maximum value.
                                areaEventInfoAvp.setReportDuration(8640000);
                            }
                        }
                        // [ Reporting-Location-Requirements ]
                        if (requestValuesForPLR.plrAreaEventRepLocRequirements != null) {
                            if (areaEventInfoAvp != null) {
                                areaEventInfoAvp.setReportingLocationRequirements(requestValuesForPLR.plrAreaEventRepLocRequirements);
                            }
                        } else {
                            if (areaEventInfoAvp != null) {
                                // Bit 0 - A location estimate is required for each area event,
                                // motion event report or expiration of the maximum time interval between event reports.
                                areaEventInfoAvp.setReportingLocationRequirements(1);
                            }
                        }
                        // [ Area-Event-Info ]
                        if (areaEventInfoAvp != null) {
                            plr.setAreaEventInfo(areaEventInfoAvp);
                        }

                        /*** PLR optional AVP: [ Periodic-LDR-Information ] ***/
                        // Periodic-LDR-Info grouped AVP
                        // Periodic-LDR-Info ::= <AVP header: 2540 10415>
                        //    { Reporting-Amount }
                        //    { Reporting-Interval }
                        if (requestValuesForPLR.plrPeriodicLDRReportingAmount != null &&
                            requestValuesForPLR.plrPeriodicLDRReportingInterval != null) {
                            periodicLDRInfoAvp = this.slgAVPFactory.createPeriodicLDRInformation();
                            // { Reporting-Amount }
                            this.slgAVPFactory.createPeriodicLDRInformation();
                            if (requestValuesForPLR.plrPeriodicLDRReportingAmount != null) {
                                // The Reporting-Amount AVP is of type Unsigned32, and it contains reporting frequency.
                                // Its minimum value shall be 1 and maximum value shall be 8639999
                                if (requestValuesForPLR.plrPeriodicLDRReportingAmount >= 1 &&
                                    requestValuesForPLR.plrPeriodicLDRReportingAmount <= 8639999)
                                    periodicLDRInfoAvp.setReportingAmount(requestValuesForPLR.plrPeriodicLDRReportingAmount);
                            }
                            // { Reporting-Interval }
                            if (requestValuesForPLR.plrPeriodicLDRReportingInterval != null) {
                                // The Interval-Time AVP is of type Unsigned32, and it contains reporting interval in seconds.
                                // Its minimum value shall be 1 and maximum value shall be 8639999.
                                if (requestValuesForPLR.plrPeriodicLDRReportingInterval >= 1 &&
                                    requestValuesForPLR.plrPeriodicLDRReportingInterval <= 8639999)
                                    periodicLDRInfoAvp.setReportingInterval(requestValuesForPLR.plrPeriodicLDRReportingInterval);
                            }
                            // [ Periodic-LDR-Information ]
                            if (periodicLDRInfoAvp.getReportingAmount() >= 1 && periodicLDRInfoAvp.getReportingInterval() >= 1) {
                                plr.setPeriodicLDRInformation(periodicLDRInfoAvp);
                            }
                        }

                        /*** PLR optional AVP: [ Motion-Event-Info ] ***/
                        // Motion-Event-Info ::= <AVP header: 2559 10415>
                        //      { Linear-Distance }
                        //      [ Occurrence-Info ]
                        //      [ Interval-Time ]
                        //      [ Maximum-Interval ]
                        //      [ Sampling-Interval ]
                        //      [ Reporting-Duration ]
                        //      [ Reporting-Location-Requirements ]
                        // { Linear-Distance }
                        if (requestValuesForPLR.plrMotionEventLinearDistance != null) {
                            // The Linear-Distance AVP is of type Unsigned32, and it contains the minimum linear (straight line) distance
                            // for motion event reports, in meters. The minimum value shall be 1 and maximum value shall be 10,000.
                            if (requestValuesForPLR.plrMotionEventLinearDistance >= 0 && requestValuesForPLR.plrMotionEventLinearDistance <= 10000) {
                                motionEventInfoAvp = this.slgAVPFactory.createMotionEventInfo();
                                motionEventInfoAvp.setLinearDistance(requestValuesForPLR.plrMotionEventLinearDistance);
                            }
                        }
                        // [ Occurrence-Info ]
                        if (requestValuesForPLR.plrMotionEventOccurrenceInfo != null) {
                            if (motionEventInfoAvp != null) {
                                if (requestValuesForPLR.plrMotionEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT ||
                                    requestValuesForPLR.plrMotionEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                    motionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo.fromInt(requestValuesForPLR.plrMotionEventOccurrenceInfo);
                                    motionEventInfoAvp.setOccurrenceInfo(motionEventOccurrenceInfo);
                                }
                            }
                        } else {
                            if (motionEventInfoAvp != null) {
                                // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0)
                                motionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo.ONE_TIME_EVENT;
                                motionEventInfoAvp.setOccurrenceInfo(motionEventOccurrenceInfo);
                            }
                        }
                        // [ Interval-Time ]
                        if (requestValuesForPLR.plrMotionEventIntervalTime != null) {
                            // Interval-Time AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (motionEventInfoAvp != null) {
                                if (motionEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (motionEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        // The Interval-Time AVP (Unsigned32) contains the minimum time interval
                                        // between area reports or motion reports, in seconds.
                                        // The minimum value shall be 1 second and the maximum value 32767 seconds.
                                        if (requestValuesForPLR.plrMotionEventIntervalTime >= 1 &&
                                            requestValuesForPLR.plrMotionEventIntervalTime <= 32767) {
                                            motionEventInfoAvp.setIntervalTime(requestValuesForPLR.plrMotionEventIntervalTime);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (motionEventInfoAvp != null) {
                                if (motionEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (motionEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        // If not included, the default value of Interval-Time shall be considered as one.
                                        motionEventInfoAvp.setIntervalTime(1);
                                    }
                                }
                            }
                        }
                        // [ Maximum-Interval ]
                        if (requestValuesForPLR.plrMotionEventMaximumInterval != null) {
                            // The Maximum-Interval AVP (Unsigned32) contains the maximum time interval between consecutive event reports, in seconds.
                            // The minimum value shall be 1 second and the maximum value 86400 seconds.
                            //  Maximum-Interval AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (motionEventInfoAvp != null) {
                                if (motionEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (motionEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        if (requestValuesForPLR.plrMotionEventMaximumInterval >= 1 &&
                                            requestValuesForPLR.plrMotionEventMaximumInterval <= 86400) {
                                            motionEventInfoAvp.setMaximumInterval(requestValuesForPLR.plrMotionEventMaximumInterval);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (motionEventInfoAvp != null) {
                                if (motionEventInfoAvp.getOccurrenceInfo() != null) {
                                    if (motionEventInfoAvp.getOccurrenceInfo().getValue() == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                        // If not included,
                                        // the default value of Maximum-Interval shall be considered as the maximum value.
                                        motionEventInfoAvp.setMaximumInterval(86400);
                                    }
                                }
                            }
                        }
                        // [ Sampling-Interval ]
                        if (requestValuesForPLR.plrMotionEventSamplingInterval != null) {
                            // The Sampling-Interval AVP (Unsigned32) contains the maximum time interval between consecutive evaluations by a UE
                            // of an area event or motion event, in seconds.
                            // The minimum value shall be 1 second and the maximum value 3600 seconds.
                            if (motionEventInfoAvp != null) {
                                if (requestValuesForPLR.plrMotionEventSamplingInterval >= 1 &&
                                    requestValuesForPLR.plrMotionEventSamplingInterval <= 3600) {
                                    motionEventInfoAvp.setSamplingInterval(requestValuesForPLR.plrMotionEventSamplingInterval);
                                }
                            }
                        } else {
                            if (motionEventInfoAvp != null) {
                                // If not included,
                                // the default value of Sampling-Interval shall be considered as the maximum value.
                                motionEventInfoAvp.setSamplingInterval(3600);
                            }
                        }
                        // [ Reporting-Duration ]
                        if (requestValuesForPLR.plrMotionEvenReportingDuration != null) {
                            // The Reporting-Duration AVP (Unsigned32) contains the maximum duration of event reporting, in seconds.
                            // Its minimum value shall be 1 and maximum value shall be 8640000.
                            if (motionEventInfoAvp != null) {
                                if (requestValuesForPLR.plrMotionEvenReportingDuration >= 1 &&
                                    requestValuesForPLR.plrMotionEvenReportingDuration <= 8640000) {
                                    motionEventInfoAvp.setReportDuration(requestValuesForPLR.plrMotionEvenReportingDuration);
                                }
                            }
                        } else {
                            if (motionEventInfoAvp != null) {
                                // If not included,
                                // the default value of Reporting-Duration shall be considered as the maximum value.
                                motionEventInfoAvp.setReportDuration(8640000);
                            }
                        }
                        // [ Reporting-Location-Requirements ]
                        if (requestValuesForPLR.plrMotionEvenReportingLocationRequirements != null) {
                            if (motionEventInfoAvp != null) {
                                motionEventInfoAvp.setReportingLocationRequirements(requestValuesForPLR.plrMotionEvenReportingLocationRequirements);
                            }
                        } else {
                            if (motionEventInfoAvp != null) {
                                // Bit 0 - A location estimate is required for each area event,
                                // motion event report or expiration of the maximum time interval between event reports.
                                motionEventInfoAvp.setReportingLocationRequirements(1);
                            }
                        }
                        // [ Motion-Event-Info ]
                        if (motionEventInfoAvp != null) {
                            plr.setMotionEventInfo(motionEventInfoAvp);
                        }

                        /*** PLR optional AVP: [ Reporting-PLMN-List ] ***/
                        // The Reporting-PLMN-List AVP is of type Grouped.
                        //  AVP format:
                        //      Reporting-PLMN-List ::= <AVP header: 2543 10415>
                        //          1*20{ PLMN-ID-List }
                        //          [ Prioritized-List-Indicator ]
                        if (requestValuesForPLR.plrVisitedPLMNIdList != null) {
                            // { PLMN-ID-List }
                            // The PLMN-ID-List AVP is of type Grouped.
                            //  PLMN-ID-List ::= <AVP header: 2544 10415>
                            //      { Visited-PLMN-Id }
                            //      [ Periodic-Location-Support-Indicator ]
                            //      *[ AVP ]
                            // { Visited-PLMN-Id }
                            String[] vPlmnIdArray = requestValuesForPLR.plrVisitedPLMNIdList.split("-");
                            PlmnIdImpl visitedPlmnId = new PlmnIdImpl(Integer.parseInt(vPlmnIdArray[0]), Integer.parseInt(vPlmnIdArray[1]));
                            // [ Periodic-Location-Support-Indicator ]
                            // The Periodic-Location-Support-Indicator AVP is of type Enumerated, and
                            // it indicates if the given PLMN-ID (indicated by Visited-PLMN-Id) supports periodic location or not.
                            // The following values are defined: NOT_SUPPORTED (0), SUPPORTED (1)
                            if (requestValuesForPLR.plrPeriodicLocationSupportIndicator != null) {
                                if (requestValuesForPLR.plrPeriodicLocationSupportIndicator == PeriodicLocationSupportIndicator._NOT_SUPPORTED ||
                                    requestValuesForPLR.plrPeriodicLocationSupportIndicator == PeriodicLocationSupportIndicator._SUPPORTED) {
                                    periodicLocationSupportIndicator = PeriodicLocationSupportIndicator.fromInt(requestValuesForPLR.plrPeriodicLocationSupportIndicator);

                                    // [ Prioritized-List-Indicator ]
                                    // The Prioritized-List-Indicator AVP is of type Enumerated,
                                    // and it indicates if the PLMN-ID-List is provided in prioritized order or not.
                                    // The following values are defined:
                                    // NOT_PRIORITIZED  (0)
                                    // PRIORITIZED (1)
                                    if (requestValuesForPLR.plrPrioritizedListIndicator != null) {
                                        if (requestValuesForPLR.plrPrioritizedListIndicator == PrioritizedListIndicator._NOT_PRIORITIZED ||
                                            requestValuesForPLR.plrPrioritizedListIndicator == PrioritizedListIndicator._PRIORITIZED) {
                                            prioritizedListIndicator = PrioritizedListIndicator.fromInt(requestValuesForPLR.plrPrioritizedListIndicator);
                                        }
                                    } else {
                                        // If not included,
                                        // the default value of Prioritized-List-Indicator shall be considered as "NOT_PRIORITIZED" (0).
                                        prioritizedListIndicator = PrioritizedListIndicator.NOT_PRIORITIZED;
                                    }
                                }
                            } else {
                                //  If not included,
                                //  the default value of Periodic-Location-Support-Indicator shall be considered as "NOT_SUPPORTED" (0).
                                periodicLocationSupportIndicator = PeriodicLocationSupportIndicator.NOT_SUPPORTED;
                            }
                            plmnidListAvp = this.slgAVPFactory.createPLMNIDList();
                            plmnidListAvp.setVisitedPLMNId(visitedPlmnId.getData());
                            if (periodicLocationSupportIndicator != null) {
                                plmnidListAvp.setPeriodicLocationSupportIndicator(periodicLocationSupportIndicator);
                                reportingPLMNListAvp = this.slgAVPFactory.createReportingPLMNList();
                                reportingPLMNListAvp.setPLMNIDList(plmnidListAvp);
                                if (requestValuesForPLR.reportingPLMNListAvp != null)
                                    requestValuesForPLR.reportingPLMNListAvp = reportingPLMNListAvp;
                                if (prioritizedListIndicator != null)
                                    reportingPLMNListAvp.setPrioritizedListIndicator(prioritizedListIndicator);
                                plr.setReportingPLMNList(reportingPLMNListAvp);
                            }
                        }

                        // Manage lcsReferenceNumber and lrrCallbackUrl for deferred location reports
                        referenceNumber = requestValuesForPLR.plrLcsReferenceNumber;
                        if (requestValuesForPLR.plrDeferredLocationType != null) {
                            httpSubscriberLocationReport = getHttpSubscriberLocationReport();
                            boolean mlp = false;
                            if (httpRequestType != null) {
                                if (httpRequestType.equalsIgnoreCase("MLP"))
                                    mlp = true;
                            }
                            slhRiaAvpValues.lteLcsReferenceNumber =
                                    httpSubscriberLocationReport.Register(requestValuesForPLR.plrLcsReferenceNumber, requestValuesForPLR.lrrCallbackUrl, null, mlp, curlUser);
                            logger.fine(String.format("Sending SLg PLR with LCS-Reference-Number: %d from HTTP request clientReferenceNumber: %d and callback URL: '%s'",
                                    slhRiaAvpValues.lteLcsReferenceNumber, requestValuesForPLR.plrLcsReferenceNumber, requestValuesForPLR.lrrCallbackUrl));
                            httpSubscriberLocationReport.closeMongo();
                            if (slhRiaAvpValues.lteLcsReferenceNumber != null) {
                                byte[] lcsReferenceNumber = ByteBuffer.allocate(Integer.SIZE / 8).putInt(slhRiaAvpValues.lteLcsReferenceNumber).array();
                                plr.setLCSReferenceNumber(lcsReferenceNumber);
                            }
                        }

                        /*** PLR conditional AVP: [ IMEI ] ***/
                        if (requestValuesForPLR.plrImei != null)
                            plr.setIMEI(requestValuesForPLR.plrImei);

                        /*** PLR optional AVP: [ LCS-Supported-GAD-Shapes ] ***/
                        // The Supported-GAD-Shapes AVP is of type Unsigned32, and it shall contain a bitmask.
                        //  A node shall mark in the BIT STRING all Shapes defined in 3GPP TS 23.032 [3] it supports.
                        //  Bits 8-0 in shall indicate the supported Shapes defined in 3GPP TS 23.032 [3]. Bits 9 to 31 shall be ignored.
                        //  ellipsoidPoint (0)
                        //  ellipsoidPointWithUncertaintyCircle (1)
                        //  ellipsoidPointWithUncertaintyEllipse (2)
                        //  polygon (3)
                        //  ellipsoidPointWithAltitude (4)
                        //  ellipsoidPointWithAltitudeAndUncertaintyEllipsoid (5)
                        //  ellipsoidArc (6)
                        //  highAccuracyEllipsoidPointWithUncertaintyEllipse (7)
                        //  highAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid (8)
                        if (requestValuesForPLR.plrLcsSupportedGadShapes != null) {
                            plr.setLCSSupportedGADShapes(requestValuesForPLR.plrLcsSupportedGadShapes);
                        }

                        /*** PLR optional AVP: [ LCS-Codeword ] ***/
                        if (requestValuesForPLR.plrLcsCodeword != null)
                            plr.setLCSCodeword(requestValuesForPLR.plrLcsCodeword);

                        /*** PLR optional AVP: [ Service-Selection ] ***/
                        if (requestValuesForPLR.plrServiceSelection != null)
                            plr.setServiceSelection(requestValuesForPLR.plrServiceSelection);

                        /*** PLR optional AVP: [ GMLC-Address ] ***/
                        if (!gmlcPropertiesManagement.getDiameterGmlcAddress().equalsIgnoreCase("")) {
                            // NOTE: this is a workaround for MNOs asking to add the GMLC-Address AVP even when not following
                            // 3GPP TS 29.172 guidelines for this AVP's presence in PLR:
                            // H-GMLC Address	GMLC-Address	O
                            // If present, this Information Element shall contain the address identifying the H-GMLC which
                            // initiated the deferred MT-LR procedure.
                            // This Information Element is applicable only when the deferred MT-LR procedure is performed.
                            AddressType addressType;
                            if (!gmlcPropertiesManagement.getDiameterGmlcAddressType().equalsIgnoreCase("")) {
                                if (gmlcPropertiesManagement.getDiameterGmlcAddressType().equalsIgnoreCase("6"))
                                    addressType = AddressType.ADDRESS_IPV6;
                                else
                                    addressType = AddressType.ADDRESS_IP;
                                InetAddress ip = InetAddress.getByName(gmlcPropertiesManagement.getDiameterGmlcAddress());
                                byte[] hGmlcAddressData = ip.getAddress();
                                gmlcAddress = new Address(addressType, hGmlcAddressData);
                                plr.setGMLCAddress(gmlcAddress);
                            }
                        } else if (gmlcPropertiesManagement.getUseRiaGmlcAddress() &&
                                (gmlcAddress != null || servingNodeGmlcAddress != null || additionalGmlcAddress != null)) {
                            if (servingNodeGmlcAddress != null)
                                plr.setGMLCAddress(servingNodeGmlcAddress);
                            else if (gmlcAddress != null)
                                plr.setGMLCAddress(gmlcAddress);
                            else
                                plr.setGMLCAddress(additionalGmlcAddress);
                        }

                        /*** PLR optional AVP: [ PLR-Flags ] ***/
                        // The PLR-Flags AVP is of type Unsigned32, and it shall contain a bit mask.
                        // The meaning of the bits shall be as defined in table 7.4.52/1:
                        // Table 7.4.52/1: PLR-Flags
                        // Bit  Name                                            Description
                        // 0    MO-LR-ShortCircuit-Indicator                    This bit, when set, indicates that
                        //                                                      the MO-LR short circuit feature is requested
                        //                                                      for the periodic location.
                        //                                                      This bit is applicable only when the
                        //                                                      deferred MT-LR procedure is initiated for
                        //                                                      a periodic location event and when
                        //                                                      the message is sent over Lgd interface.
                        // 1    Optimized-LCS-Proc-Req	                        This bit, when set, indicates that the GMLC
                        //                                                      is requesting the optimized LCS procedure
                        //                                                      for the combined MME/SGSN.
                        //                                                      This bit is applicable only when the MT-LR procedure
                        //                                                      is initiated by the GMLC over the Lgd interface.
                        //                                                      The GMLC shall set this bit only when
                        //                                                      the HSS indicates the combined MME/SGSN node
                        //                                                      supporting the optimized LCS procedure.
                        // 2    Delayed-Location-Reporting-Support-Indicator    This bit, when set, indicates that the
                        //                                                      GMLC supports delayed location reporting for
                        //                                                      UEs transiently not reachable
                        //                                                      (e.g. UEs in extended idle mode DRX or
                        //                                                      Power Saving Mode) as specified in clauses
                        //                                                      9.1.6 and 9.1.15 of 3GPP TS 23.271,
                        //                                                      i.e. that the GMLC supports receiving a
                        //                                                      PROVIDE SUBSCRIBER LOCATION RESPONSE with
                        //                                                      the UE-Transiently-Not-Reachable-Indicator set in the PLA-Flags IE;
                        //                                                      and receiving the location information in a
                        //                                                      subsequent SUBSCRIBER LOCATION REPORT
                        //                                                      when the UE becomes reachable.
                        // NOTE1: Bits not defined in this table shall be cleared by the sending GMLC and discarded by the receiving MME or SGSN.
                        long plrFlags = 4; // / Delayed-Location-Reporting-Support-Indicator shall be set by default
                        if (riaFlags > -1) {
                            if (riaFlags == 1) {
                                // Combined-MME/SGSN-Supporting-Optimized-LCS-Pro bit is set meaning that
                                // UE is served by the MME and the SGSN parts of the same combined MME/SGSN
                                // and this combined MME/SGSN supports the optimized LCS procedure
                                if ((destHost == sgsnName || destHost == additionalSgsnName) &&
                                        (destRealm == sgsnRealm || destRealm == additionalSgsnRealm)) {
                                    // PLR shall be sent over the Lgd interface
                                    plrFlags = 6;
                                    if (requestValuesForPLR.plrSlgLocationType == SLgLocationType._ACTIVATE_DEFERRED_LOCATION &&
                                            plr.getPeriodicLDRInformation() != null) {
                                        // MO-LR short circuit feature is requested for the periodic location
                                        // applicable only as the deferred MT-LR procedure is initiated for a periodic location
                                        // and the message is sent over Lgd interface.
                                        plrFlags = 7;
                                    }
                                }
                            }
                            requestValuesForPLR.plrFlags = plrFlags;
                            plr.setPLRFlags(plrFlags);
                        }

                        /*** { Auth-Session-State } AVP for PLR according to 3GPP TS 29.172 ***/
                        AuthSessionStateType authSessionStateType = AuthSessionStateType.NO_STATE_MAINTAINED;
                        plr.setAuthSessionState(authSessionStateType);

                        mobileCoreNetworkTransactions.setValue(transaction, "sLhRiaAvpResponseValues", slhRiaAvpValues);
                        mobileCoreNetworkTransactions.setValue(transaction, "requestValuesForPLR", requestValuesForPLR);
                        mobileCoreNetworkTransactions.setValue(transaction, "plrFlags", plrFlags);
                        mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
                        mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", dialogStartTime);
                        mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", mlpTriggeredReportingService);
                        mobileCoreNetworkTransactions.setSession(transaction, plr.getSessionId());

                        // set new timer for PLR/PLA cycle
                        TimerID timerID = timerFacility.setTimer(slgACIF, null, System.currentTimeMillis() + DIAMETER_COMMAND_TIMEOUT, defaultTimerOptions);
                        mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

                        slgClientSessionActivity.sendProvideLocationRequest(plr);
                        logger.fine("\n\nSent SLg PLR with session Id: " + plr.getSessionId() + ", host '" + plr.getDestinationHost()
                                + "', realm '" + plr.getDestinationRealm() + "'");
                        logger.fine("\nSLg Provide-Location-Request details: " + plr);
                    } else {
                        //this should never happen
                        logger.warning("\nRIA received on session Id: " + lcsRoutingInfoAnswer.getSessionId() + ", for MSISDN '" + Arrays.toString(lcsRoutingInfoAnswer.getMSISDN())
                                + "', IMSI '" + lcsRoutingInfoAnswer.getUserName() + "' but no PLR parameters set. No PLR to send.");

                    }
                } else {
                    if (gmlcCdrState.isInitialized()) {
                        if (transaction != null)
                            mobileCoreNetworkTransactions.destroy(transaction);
                        handleRecordAndLocationReportOnDiameterResultCode(resultCode, mlpRespResult, mlpClientErrorMessage, msisdnAddress, imsi,
                            "RIR", referenceNumber, gmlcCdrState, true,
                            riaOriginHost != null ? riaOriginHost.toString() : null, riaOriginRealm != null ? riaOriginRealm.toString() : null,
                            mlpTriggeredReportingService);
                    }
                }
            } catch (SLhException slhException) {
                logger.warning("Exception when processing LCSRoutingInfoAnswer response: " + slhException.getMessage(), slhException);
                this.createCDRRecord(RecordStatus.LTE_RIR_SYSTEM_FAILURE);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, slhException.getMessage(), "RIR", msisdnAddress, imsi, imei,
                    referenceNumber, null, null,
                    riaOriginHost != null ? riaOriginHost.toString() : null, riaOriginRealm != null ? riaOriginRealm.toString() : null,
                    mlpTriggeredReportingService);
                if (transaction != null)
                    mobileCoreNetworkTransactions.destroy(transaction);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onLCSRoutingInfoAnswer=%s", lcsRoutingInfoAnswer), e);
            this.createCDRRecord(RecordStatus.LTE_RIR_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on SLh RIA: " + e.getMessage(),
                "RIR", msisdnAddress, imsi, imei, referenceNumber, null, null,
                lcsRoutingInfoAnswer != null ? lcsRoutingInfoAnswer.getOriginHost().toString() : null, lcsRoutingInfoAnswer != null ? lcsRoutingInfoAnswer.getOriginRealm().toString() : null,
                mlpTriggeredReportingService);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromSLhClientActivity(aci);
        }
    }

    /**
     * SLg PLR Event
     */
    public void onProvideLocationRequest(ProvideLocationRequest plrEvent, ActivityContextInterface aci) {
        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onProvideLocationRequest = " + plrEvent + ", ActivityContextInterface = " + aci);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onProvideLocationRequest=%s", plrEvent), e);
        }
    }

    /**
     * SLg PLA Event
     */
    public void onProvideLocationAnswer(ProvideLocationAnswer provideLocationAnswer, ActivityContextInterface aci) {

        String msisdnAddress = null, imsi = null, imei = null, lcsEpsClientNameString = null, curlUser;
        LCSFormatIndicator lcsEpsClientNameFi = null;
        Integer referenceNumber = null;
        Boolean mlpTriggeredReportingService = false;
        Long transaction = null;
        long resultCode = -1;
        DateTime eventTime = DateTime.now();
        DiameterIdentity plaOriginHost, plaOriginRealm, gmlcHost, gmlcRealm;
        if (provideLocationAnswer != null) {
            plaOriginHost = provideLocationAnswer.getOriginHost();
            plaOriginRealm = provideLocationAnswer.getOriginRealm();
        } else {
            String origHost = gmlcPropertiesManagement.getDiameterDestHost();
            String origRealm = gmlcPropertiesManagement.getDiameterDestRealm();
            plaOriginHost = new DiameterIdentity(origHost);
            plaOriginRealm = new DiameterIdentity(origRealm);
        }

        try {

            SLgPlaAvpValues slgPlaAvpValues;

            try {

                if (provideLocationAnswer != null) {
                    logger.fine("\n\nReceived SLg PLA with session Id: " + provideLocationAnswer.getSessionId() +
                            ", host '" + provideLocationAnswer.getOriginHost() + "', realm '" + provideLocationAnswer.getOriginRealm() + "'");
                    this.logger.fine("\nonProvideLocationAnswer event details " + provideLocationAnswer);
                    MLPResponse.MLPResultType mlpRespResult = null;
                    String mlpClientErrorMessage = null;
                    gmlcHost = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginHost());
                    gmlcRealm = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginRealm());

                    // CDR initialization
                    GMLCCDRState gmlcCdrState = CDRCreationHelper.slhSlgCdrInitializer(aci, this.getCDRInterface(), null, provideLocationAnswer, null,
                            plaOriginHost, plaOriginRealm, gmlcHost, gmlcRealm);
                    // Set timer last
                    this.setTimer(aci);

                    // Transaction
                    transaction = mobileCoreNetworkTransactions.getTransactionId(provideLocationAnswer.getSessionId());
                    if (transaction == null) {
                        throw new Exception();
                    }
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                    gmlcCdrState.setDialogStartTime(dialogStartTime);
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCurlUser(curlUser);
                        if (dialogStartTime != null) {
                            Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                            gmlcCdrState.setDialogDuration(dialogDuration);
                        }
                    }
                    TimerID plaTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                    if (plaTimerID != null)
                        this.timerFacility.cancelTimer(plaTimerID);
                    SLhRiaAvpValues sLhRiaAvpResponseValues = (SLhRiaAvpValues) mobileCoreNetworkTransactions.getValue(transaction, "sLhRiaAvpResponseValues");
                    RequestValuesForPLR requestValuesForPLR = (RequestValuesForPLR) mobileCoreNetworkTransactions.getValue(transaction, "requestValuesForPLR");
                    referenceNumber = requestValuesForPLR.plrLcsReferenceNumber;
                    long plrFlags = (Long) mobileCoreNetworkTransactions.getValue(transaction, "plrFlags");
                    if (gmlcCdrState.isInitialized()) {
                        if (plrFlags > -1)
                            gmlcCdrState.setPlrFlags(plrFlags);
                    }
                    mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
                    mobileCoreNetworkTransactions.destroy(transaction);
                    if (requestValuesForPLR.plrMsisdn != null) {
                        byte[] tbcdMsisdn = parseTBCD(requestValuesForPLR.plrMsisdn);
                        msisdnAddress = toTBCDString(tbcdMsisdn);
                    } else if (sLhRiaAvpResponseValues != null) {
                        if (sLhRiaAvpResponseValues.getMsisdn() != null) {
                            msisdnAddress = toTBCDString(sLhRiaAvpResponseValues.getMsisdn());
                        }
                    }
                    if (requestValuesForPLR.plrUserName != null) {
                        imsi = requestValuesForPLR.plrUserName;
                    }
                    if (sLhRiaAvpResponseValues != null) {
                        if (sLhRiaAvpResponseValues.getUserName() != null) {
                            imsi = sLhRiaAvpResponseValues.getUserName();
                        }
                    }
                    if (requestValuesForPLR.plrLcsNameString != null) {
                        lcsEpsClientNameString = requestValuesForPLR.plrLcsNameString;
                        if (requestValuesForPLR.plrLcsFormatInd != null) {
                            lcsEpsClientNameFi = LCSFormatIndicator.fromInt(requestValuesForPLR.plrLcsFormatInd);
                        }
                    }
                    if (gmlcCdrState.isInitialized()) {
                        if (imsi != null) {
                            IMSI slgImsi = new IMSIImpl(imsi);
                            gmlcCdrState.setImsi(slgImsi);
                        }
                        gmlcCdrState.setMsisdn(new ISDNAddressStringImpl(AddressNature.international_number,
                                org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, msisdnAddress));
                        if (sLhRiaAvpResponseValues != null) {
                            gmlcCdrState.setLcsReferenceNumber(sLhRiaAvpResponseValues.lteLcsReferenceNumber);
                            if (sLhRiaAvpResponseValues.getRiaFLags() != null)
                                gmlcCdrState.setRiaFlags(sLhRiaAvpResponseValues.getRiaFLags());
                        }
                        gmlcCdrState.setClientReferenceNumber(referenceNumber);
                        // lcsEpsClientName
                        gmlcCdrState.setLcsEpsClientName(lcsEpsClientNameString);
                        // lcsEpsClientFormatIndicator
                        gmlcCdrState.setLcsEpsClientFormatIndicator(lcsEpsClientNameFi);
                    }

                    slgPlaAvpValues = new SLgPlaAvpValues();

                    if (provideLocationAnswer.hasResultCode()) {
                        resultCode = provideLocationAnswer.getResultCode();
                    } else if (provideLocationAnswer.hasExperimentalResult()) {
                        resultCode = provideLocationAnswer.getExperimentalResult().getExperimentalResultCode();
                    }

                    if (provideLocationAnswer.hasLocationEstimate()) {
                        this.logger.fine("\nonProvideLocationAnswer, Location-Estimate AVP: " + Arrays.toString(provideLocationAnswer.getLocationEstimate()));
                        ExtGeographicalInformation lteLocationEstimate = AVPHandler.lteLocationEstimate2ExtGeographicalInformation(provideLocationAnswer.getLocationEstimate());
                        AddGeographicalInformation lteAddLocationEstimate = null;
                        TypeOfShape typeOfShape = lteLocationEstimate.getTypeOfShape();
                        if (typeOfShape == TypeOfShape.Polygon) {
                            lteAddLocationEstimate = AVPHandler.lteLocationEstimate2AddGeographicalInformation(provideLocationAnswer.getLocationEstimate());
                            slgPlaAvpValues.setLocationEstimate(lteAddLocationEstimate.getData());
                        } else {
                            slgPlaAvpValues.setLocationEstimate(provideLocationAnswer.getLocationEstimate());
                        }
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationEstimate(lteLocationEstimate);
                            if (lteAddLocationEstimate != null)
                                gmlcCdrState.setAdditionalLocationEstimate(lteAddLocationEstimate);
                        }
                    }

                    if (provideLocationAnswer.hasAccuracyFulfilmentIndicator()) {
                        slgPlaAvpValues.setAccuracyFulfilmentIndicator(provideLocationAnswer.getAccuracyFulfilmentIndicator());
                        this.logger.fine("\nonProvideLocationAnswer, Accuracy-Fulfilment-Indicator AVP: " + provideLocationAnswer.getAccuracyFulfilmentIndicator());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAccuracyFulfilmentIndicator(AVPHandler.diamAccFulInd2MapAccFulInd(provideLocationAnswer.getAccuracyFulfilmentIndicator()));
                        }
                    }

                    if (provideLocationAnswer.hasAgeOfLocationEstimate()) {
                        slgPlaAvpValues.setAgeOfLocationEstimate(provideLocationAnswer.getAgeOfLocationEstimate());
                        this.logger.fine("\nonProvideLocationAnswer, Age-Of-Location-Estimate AVP: " + provideLocationAnswer.getAgeOfLocationEstimate());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAgeOfLocationEstimate(AVPHandler.long2Int(provideLocationAnswer.getAgeOfLocationEstimate()));
                        }
                    }

                    if (provideLocationAnswer.hasVelocityEstimate()) {
                        slgPlaAvpValues.setVelocityEstimate(provideLocationAnswer.getVelocityEstimate());
                        this.logger.fine("\nonProvideLocationAnswer, Velocity-Estimate AVP: " + Arrays.toString(provideLocationAnswer.getVelocityEstimate()));
                        VelocityEstimate lteVelocityEstimate = AVPHandler.lteVelocityEstimate2MapVelocityEstimate(provideLocationAnswer.getVelocityEstimate());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setVelocityEstimate(lteVelocityEstimate);
                        }
                    }

                    if (provideLocationAnswer.hasEUTRANPositioningData()) {
                        slgPlaAvpValues.setEUtranPositioningData(provideLocationAnswer.getEUTRANPositioningData());
                        this.logger.fine("\nonProvideLocationAnswer, E-UTRAN-Positioning-Data AVP: " + Arrays.toString(provideLocationAnswer.getEUTRANPositioningData()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setEUTRANPositioningData(new EUTRANPositioningDataImpl(provideLocationAnswer.getEUTRANPositioningData()));
                        }
                    }

                    byte[] ecgi = null;
                    if (provideLocationAnswer.hasECGI()) {
                        ecgi = provideLocationAnswer.getECGI();
                        slgPlaAvpValues.setEcgi(ecgi);
                        this.logger.fine("\nonProvideLocationAnswer, ECGI AVP: " + Arrays.toString(ecgi));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setEUtranCgi(AVPHandler.lteEcgi2MapEutranCgi(ecgi));
                        }
                    }

                    if (provideLocationAnswer.hasGERANPositioningInfo()) {
                        slgPlaAvpValues.setGeranPositioningInfoAvp(provideLocationAnswer.getGERANPositioningInfo());
                        this.logger.fine("\nonProvideLocationAnswer, GERAN-Positioning-Info AVP: " + provideLocationAnswer.getGERANPositioningInfo());
                        byte[] geranPositioningData = provideLocationAnswer.getGERANPositioningInfo().getGERANPositioningData();
                        if (geranPositioningData != null) {
                            slgPlaAvpValues.setGeranPositioningData(geranPositioningData);
                            this.logger.fine("\nonProvideLocationAnswer, GERAN-Positioning-Data AVP: " + Arrays.toString(geranPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setGeranPositioningDataInformation(AVPHandler.lteGeranPosDataInfo2MapGeranPosDataInfo(geranPositioningData));
                            }
                        }
                        byte[] geranGANSSPositioningData = provideLocationAnswer.getGERANPositioningInfo().getGERANGANSSPositioningData();
                        if (geranGANSSPositioningData != null) {
                            slgPlaAvpValues.setGeranGANSSPositioningData(geranGANSSPositioningData);
                            this.logger.fine("\nonProvideLocationAnswer, GERAN-GANSS-Positioning-Data AVP: " + Arrays.toString(geranGANSSPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setGeranGANSSpositioningData(AVPHandler.lteGeranGanssPosDataInfo2MapGeranGanssPosDataInfo(geranGANSSPositioningData));
                            }
                        }
                    }

                    if (provideLocationAnswer.hasCellGlobalIdentity()) {
                        slgPlaAvpValues.setCellGlobalIdentity(provideLocationAnswer.getCellGlobalIdentity());
                        this.logger.fine("\nonProvideLocationAnswer, Cell-Global-Identity AVP: " + Arrays.toString(provideLocationAnswer.getCellGlobalIdentity()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setCellGlobalIdentity(AVPHandler.byte2String(provideLocationAnswer.getCellGlobalIdentity()));
                        }
                    }

                    if (provideLocationAnswer.hasUTRANPositioningInfo()) {
                        slgPlaAvpValues.setUtranPositioningInfoAvp(provideLocationAnswer.getUTRANPositioningInfo());
                        this.logger.fine("\nonProvideLocationAnswer, UTRAN-Positioning-Info AVP: " + provideLocationAnswer.getUTRANPositioningInfo());
                        byte[] utranPositioningData = provideLocationAnswer.getUTRANPositioningInfo().getUTRANPositioningData();
                        if (utranPositioningData != null) {
                            slgPlaAvpValues.setUtranPositioningData(utranPositioningData);
                            this.logger.fine("\nonProvideLocationAnswer, UTRAN-Positioning-Data AVP: "
                                    + Arrays.toString(utranPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setUtranPositioningDataInfo(AVPHandler.lteUtranPosData2MapUtranPosDataInfo(utranPositioningData));
                            }
                        }
                        byte[] utranGANSSPositioningData = provideLocationAnswer.getUTRANPositioningInfo().getUTRANGANSSPositioningData();
                        if (utranGANSSPositioningData != null) {
                            slgPlaAvpValues.setUtranGANSSPositioningData(utranGANSSPositioningData);
                            this.logger.fine("\nonProvideLocationAnswer, UTRAN-GANSS-Positioning-Data AVP: "
                                    + Arrays.toString(utranGANSSPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setUtranGANSSpositioningData(AVPHandler.lteUtranGanssPosData2MapUtranGanssPosDataInfo(utranGANSSPositioningData));
                            }
                        }
                        byte[] utranAdditionalPositioningData = provideLocationAnswer.getUTRANPositioningInfo().getUTRANAdditionalPositioningData();
                        if (utranAdditionalPositioningData != null) {
                            slgPlaAvpValues.setUtranAdditionalPositioningData(utranAdditionalPositioningData);
                            this.logger.fine("\nonProvideLocationAnswer, UTRAN-Additional-Positioning-Data AVP: " +
                                    Arrays.toString(utranAdditionalPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setUtranAdditionalPositioningData(AVPHandler.lteUtranAddPosData2MapUtranAdditionalPositioningdata(utranAdditionalPositioningData));
                            }
                        }
                    }

                    if (provideLocationAnswer.hasServiceAreaIdentity()) {
                        slgPlaAvpValues.setServiceAreaIdentity(provideLocationAnswer.getServiceAreaIdentity());
                        this.logger.fine("\nonProvideLocationAnswer, Service-Area-Identity AVP: "
                                + Arrays.toString(provideLocationAnswer.getServiceAreaIdentity()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setServiceAreaIdentity(AVPHandler.byte2String(provideLocationAnswer.getServiceAreaIdentity()));
                        }
                    }

                    DiameterIdentity sgsnName, sgsnRealm, mmeName, mmeRealm, tgppAAAServerName;
                    long plaLcsCapabilitiesSets = -1;
                    Address gmlcAddress;
                    if (provideLocationAnswer.hasServingNode()) {
                        slgPlaAvpValues.setServingNodeAvp(provideLocationAnswer.getServingNode());
                        this.logger.fine("\nonProvideLocationAnswer, Serving-Node AVP: " + provideLocationAnswer.getServingNode());
                        byte[] sgsnNumber = provideLocationAnswer.getServingNode().getSGSNNumber();
                        sgsnName = provideLocationAnswer.getServingNode().getSGSNName();
                        sgsnRealm = provideLocationAnswer.getServingNode().getSGSNRealm();
                        mmeName = provideLocationAnswer.getServingNode().getMMEName();
                        mmeRealm = provideLocationAnswer.getServingNode().getMMERealm();
                        byte[] mscNumber = provideLocationAnswer.getServingNode().getMSCNumber();
                        tgppAAAServerName = provideLocationAnswer.getServingNode().get3GPPAAAServerName();
                        if (provideLocationAnswer.getServingNode().hasLcsCapabilitiesSets())
                            plaLcsCapabilitiesSets = provideLocationAnswer.getServingNode().getLcsCapabilitiesSets();
                        gmlcAddress = provideLocationAnswer.getServingNode().getGMLCAddress();
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setSgsnNumber(AVPHandler.tbcd2IsdnAddressString(sgsnNumber));
                            gmlcCdrState.setSgsnName(AVPHandler.diameterIdToMapDiameterId(sgsnName));
                            gmlcCdrState.setSgsnRealm(AVPHandler.diameterIdToMapDiameterId(sgsnRealm));
                            gmlcCdrState.setMmeName(AVPHandler.diameterIdToMapDiameterId(mmeName));
                            gmlcCdrState.setMmeRealm(AVPHandler.diameterIdToMapDiameterId(mmeRealm));
                            gmlcCdrState.setMscNumber(AVPHandler.tbcd2IsdnAddressString(mscNumber));
                            gmlcCdrState.setAaaServerName(AVPHandler.diameterIdToMapDiameterId(tgppAAAServerName));
                            if (plaLcsCapabilitiesSets > -1)
                                gmlcCdrState.setLcsCapabilitiesSets(plaLcsCapabilitiesSets);
                            gmlcCdrState.sethGmlcAddress(AVPHandler.address2GsnAddress(gmlcAddress));
                        }
                    }

                    if (provideLocationAnswer.hasPLAFlags()) {
                        slgPlaAvpValues.setPlaFlags(provideLocationAnswer.getPLAFlags());
                        this.logger.fine("\nonProvideLocationAnswer, PLA-Flags AVP: " + provideLocationAnswer.getPLAFlags());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setPlaFlags(provideLocationAnswer.getPLAFlags());
                        }
                    }

                    if (provideLocationAnswer.hasESMLCCellInfo()) {
                        slgPlaAvpValues.setEsmlcCellInfoAvp(provideLocationAnswer.getESMLCCellInfo());
                        this.logger.fine("\nonProvideLocationAnswer, ESMLC-Cell-Info AVP: " + provideLocationAnswer.getESMLCCellInfo());
                        if (ecgi == null) {
                            ecgi = provideLocationAnswer.getESMLCCellInfo().getECGI();
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setEUtranCgi(AVPHandler.lteEcgi2MapEutranCgi(ecgi));
                            }
                        }
                        long cellPortionId = provideLocationAnswer.getESMLCCellInfo().getCellPortionID();
                        if (cellPortionId > -1) {
                            slgPlaAvpValues.setCellPortionId(cellPortionId);
                            this.logger.fine("\nonProvideLocationAnswer, Cell-Portion-ID AVP: " + cellPortionId);
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setCellPortionId(provideLocationAnswer.getESMLCCellInfo().getCellPortionID());
                            }
                        }
                    }

                    if (provideLocationAnswer.hasCivicAddress()) {
                        slgPlaAvpValues.setCivicAddress(provideLocationAnswer.getCivicAddress());
                        this.logger.fine("\nonProvideLocationAnswer, Civic-Address AVP: " + provideLocationAnswer.getCivicAddress());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setCivicAddress(provideLocationAnswer.getCivicAddress());
                        }
                    }

                    if (provideLocationAnswer.hasBarometricPressure()) {
                        slgPlaAvpValues.setBarometricPressure(provideLocationAnswer.getBarometricPressure());
                        this.logger.fine("\nonProvideLocationAnswer, Barometric-Pressure AVP: " + provideLocationAnswer.getBarometricPressure());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setBarometricPressureMeasurement(provideLocationAnswer.getBarometricPressure());
                        }
                    }

                    if (resultCode == 2001 || resultCode == SLgSpecificErrors.DIAMETER_ERROR_POSITIONING_DENIED ||
                        resultCode == SLgSpecificErrors.DIAMETER_ERROR_POSITIONING_FAILED) {
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setStatusCode(resultCode);
                        }
                        mlpRespResult = MLPResponse.MLPResultType.OK;
                        // Handle successful retrieval of response to subscriber's location request (SLg ELP Provide-Location-Answer)
                        this.handleLTELocationServicesResponse(mlpRespResult, requestValuesForPLR, sLhRiaAvpResponseValues, slgPlaAvpValues, mlpClientErrorMessage, mlpTriggeredReportingService);
                        switch ((int) resultCode) {
                            case 2001:
                                this.createCDRRecord(RecordStatus.LTE_PLR_SUCCESS);
                                break;
                            case SLgSpecificErrors.DIAMETER_ERROR_POSITIONING_DENIED:
                                this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_POSITIONING_DENIED);
                                break;
                            case SLgSpecificErrors.DIAMETER_ERROR_POSITIONING_FAILED:
                                this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_POSITIONING_FAILED);
                                break;
                        }
                    } else {
                        Map<MLPResponse.MLPResultType, String> mlpResultTypeStringMap = handleRecordAndLocationReportOnDiameterResultCode(resultCode, mlpRespResult, mlpClientErrorMessage, msisdnAddress, imsi,
                                "PLR", referenceNumber, gmlcCdrState, false,
                                plaOriginHost != null ? plaOriginHost.toString() : null, plaOriginRealm != null ? plaOriginRealm.toString() : null,
                                mlpTriggeredReportingService);
                        if (mlpResultTypeStringMap != null) {
                            mlpRespResult = mlpResultTypeStringMap.entrySet().iterator().next().getKey();
                            mlpClientErrorMessage = mlpResultTypeStringMap.entrySet().iterator().next().getValue();
                        }
                        this.handleLTELocationServicesResponse(mlpRespResult, requestValuesForPLR, sLhRiaAvpResponseValues, slgPlaAvpValues, mlpClientErrorMessage, mlpTriggeredReportingService);
                    }
                }
            } catch (Throwable e) {
                logger.warning("Exception when processing onProvideLocationAnswer: " + e.getMessage(), e);
                this.createCDRRecord(RecordStatus.LTE_PLR_SYSTEM_FAILURE);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on SLg PLA: " + e.getMessage(),
                    "PLR", msisdnAddress, imsi, imei, referenceNumber, null, null,
                    plaOriginHost != null ? plaOriginHost.toString() : null, plaOriginRealm != null ? plaOriginRealm.toString() : null,
                    mlpTriggeredReportingService);
                if (transaction != null)
                    mobileCoreNetworkTransactions.destroy(transaction);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onProvideLocationAnswer=%s", provideLocationAnswer), e);
            this.createCDRRecord(RecordStatus.LTE_PLR_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on SLg PLA: " + e.getMessage(),
                "PLR", msisdnAddress, imsi, imei, referenceNumber, null, null,
                    plaOriginHost != null ? plaOriginHost.toString() : null, plaOriginRealm != null ? plaOriginRealm.toString() : null,
                    mlpTriggeredReportingService);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromSLgClientActivity(aci);
        }
    }

    /**
     * SLg LRR Event
     */
    public void onLocationReportRequest(LocationReportRequest locationReportRequest, ActivityContextInterface aci) {

        try {
            DiameterIdentity lrrOriginHost, lrrOriginRealm, gmlcHost, gmlcRealm;
            if (locationReportRequest != null) {
                logger.fine("\n\nReceived SLg LRA with session Id: " + locationReportRequest.getSessionId()
                        + ", host '" + locationReportRequest.getOriginHost() + "', realm '" + locationReportRequest.getOriginRealm() + "'");
                this.logger.fine("\nonLocationReportRequest event details= " + locationReportRequest);
                lrrOriginHost = locationReportRequest.getOriginHost();
                lrrOriginRealm = locationReportRequest.getOriginRealm();
            } else {
                String origHost = gmlcPropertiesManagement.getDiameterDestHost();
                String origRealm = gmlcPropertiesManagement.getDiameterDestRealm();
                lrrOriginHost = new DiameterIdentity(origHost);
                lrrOriginRealm = new DiameterIdentity(origRealm);
            }
            SLgLrrAvpValues slgLrrAvpValues = new SLgLrrAvpValues();

            try {
                gmlcHost = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginHost());
                gmlcRealm = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginRealm());

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.slhSlgCdrInitializer(aci, this.getCDRInterface(), null, null, locationReportRequest,
                    lrrOriginHost, lrrOriginRealm, gmlcHost, gmlcRealm);
                // Set timer last
                this.setTimer(aci);

                if (locationReportRequest != null) {
                    // < Location-Report-Request> ::=	< Diameter Header: 8388621, REQ, PXY, 16777255 >
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setStatusCode(2001L);
                    }

                    if (locationReportRequest.hasLocationEvent()) {
                        slgLrrAvpValues.setLocationEvent(locationReportRequest.getLocationEvent());
                        this.logger.fine("\nonLocationReportRequest, Location-Event AVP: " + locationReportRequest.getLocationEvent());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationEvent(locationReportRequest.getLocationEvent());
                        }
                    }

                    if (locationReportRequest.hasLCSEPSClientName()) {
                        slgLrrAvpValues.setLcsEPSClientName(locationReportRequest.getLCSEPSClientName());
                        this.logger.fine("\nonLocationReportRequest, LCS-EPS-Client-Name AVP: " + locationReportRequest.getLCSEPSClientName());
                        String lcsNameString = locationReportRequest.getLCSEPSClientName().getLCSNameString();
                        if (lcsNameString != null) {
                            slgLrrAvpValues.setLcsNameString(lcsNameString);
                            this.logger.fine("\nonLocationReportRequest, LCS-Name-String AVP: " + lcsNameString);
                        }
                        LCSFormatIndicator lcsFormatIndicator = locationReportRequest.getLCSEPSClientName().getLCSFormatIndicator();
                        if (lcsFormatIndicator != null) {
                            slgLrrAvpValues.setLcsFormatIndicator(lcsFormatIndicator);
                            this.logger.fine("\nonLocationReportRequest, LCS-Format-Indicator AVP: " + lcsFormatIndicator);
                        }
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsEpsClientName(lcsNameString);
                            gmlcCdrState.setLcsEpsClientFormatIndicator(lcsFormatIndicator);
                        }
                    }

                    if (locationReportRequest.hasUserName()) {
                        slgLrrAvpValues.setUserName(locationReportRequest.getUserName());
                        this.logger.fine("\nonLocationReportRequest, User-Name AVP: " + locationReportRequest.getUserName());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setImsi(AVPHandler.userName2Imsi(locationReportRequest.getUserName()));
                        }
                    }

                    if (locationReportRequest.hasMSISDN()) {
                        slgLrrAvpValues.setMsisdn(locationReportRequest.getMSISDN());
                        this.logger.fine("\nonLocationReportRequest, MSISDN AVP: " + Arrays.toString(locationReportRequest.getMSISDN()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMsisdn(AVPHandler.tbcd2IsdnAddressString(locationReportRequest.getMSISDN()));
                        }
                    }

                    if (locationReportRequest.hasIMEI()) {
                        slgLrrAvpValues.setImei(locationReportRequest.getIMEI());
                        this.logger.fine("\nonLocationReportRequest, IMEI AVP: " + locationReportRequest.getIMEI());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setImei(AVPHandler.string2MapImei(locationReportRequest.getIMEI()));
                        }
                    }

                    if (locationReportRequest.hasLocationEstimate()) {
                        byte[] locationEstimate = locationReportRequest.getLocationEstimate();
                        this.logger.fine("\nnonLocationReportRequest, Location-Estimate AVP: " + Arrays.toString(locationEstimate));
                        ExtGeographicalInformation lteLocationEstimate = AVPHandler.lteLocationEstimate2ExtGeographicalInformation(locationEstimate);
                        AddGeographicalInformation lteAddLocationEstimate = null;
                        TypeOfShape typeOfShape = lteLocationEstimate.getTypeOfShape();
                        if (typeOfShape == TypeOfShape.Polygon) {
                            lteAddLocationEstimate = AVPHandler.lteLocationEstimate2AddGeographicalInformation(locationEstimate);
                            slgLrrAvpValues.setLocationEstimate(lteAddLocationEstimate.getData());
                        } else {
                            slgLrrAvpValues.setLocationEstimate(locationEstimate);
                        }
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLocationEstimate(lteLocationEstimate);
                            if (lteAddLocationEstimate != null)
                                gmlcCdrState.setAdditionalLocationEstimate(lteAddLocationEstimate);
                        }
                    }

                    if (locationReportRequest.hasAccuracyFulfilmentIndicator()) {
                        slgLrrAvpValues.setAccuracyFulfilmentIndicator(locationReportRequest.getAccuracyFulfilmentIndicator());
                        this.logger.fine("\nonLocationReportRequest, Accuracy-Fulfilment-Indicator AVP: " + locationReportRequest.getAccuracyFulfilmentIndicator());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAccuracyFulfilmentIndicator(AVPHandler.diamAccFulInd2MapAccFulInd(locationReportRequest.getAccuracyFulfilmentIndicator()));
                        }
                    }

                    if (locationReportRequest.hasAgeOfLocationEstimate()) {
                        slgLrrAvpValues.setAgeOfLocationEstimate(locationReportRequest.getAgeOfLocationEstimate());
                        this.logger.fine("\nonLocationReportRequest, Age-Of-Location-Estimate AVP: " + locationReportRequest.getAgeOfLocationEstimate());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAgeOfLocationEstimate(AVPHandler.long2Int(locationReportRequest.getAgeOfLocationEstimate()));
                        }
                    }

                    if (locationReportRequest.hasVelocityEstimate()) {
                        slgLrrAvpValues.setVelocityEstimate(locationReportRequest.getVelocityEstimate());
                        this.logger.fine("\nonLocationReportRequest, Velocity-Estimate AVP: " + Arrays.toString(locationReportRequest.getVelocityEstimate()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setVelocityEstimate(AVPHandler.lteVelocityEstimate2MapVelocityEstimate(locationReportRequest.getVelocityEstimate()));
                        }
                    }

                    if (locationReportRequest.hasEUTRANPositioningData()) {
                        slgLrrAvpValues.setEUtranPositioningData(locationReportRequest.getEUTRANPositioningData());
                        this.logger.fine("\nonLocationReportRequest, E-UTRAN-Positioning-Data AVP: "
                                + Arrays.toString(locationReportRequest.getEUTRANPositioningData()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setEUTRANPositioningData(new EUTRANPositioningDataImpl(locationReportRequest.getEUTRANPositioningData()));
                        }
                    }

                    byte[] ecgi = null;
                    if (locationReportRequest.hasECGI()) {
                        ecgi = locationReportRequest.getECGI();
                        slgLrrAvpValues.setEcgi(ecgi);
                        this.logger.fine("\nonLocationReportRequest, ECGI AVP: " + Arrays.toString(ecgi));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setEUtranCgi(AVPHandler.lteEcgi2MapEutranCgi(ecgi));
                        }
                    }

                    if (locationReportRequest.hasGERANPositioningInfo()) {
                        slgLrrAvpValues.setGeranPositioningInfoAvp(locationReportRequest.getGERANPositioningInfo());
                        this.logger.fine("\nonLocationReportRequest, GERAN-Positioning-Info AVP: " + locationReportRequest.getGERANPositioningInfo());
                        byte[] geranPositioningData = locationReportRequest.getGERANPositioningInfo().getGERANPositioningData();
                        if (geranPositioningData != null) {
                            slgLrrAvpValues.setGeranPositioningData(geranPositioningData);
                            this.logger.fine("\nonLocationReportRequest, GERAN-Positioning-Data AVP: "
                                    + Arrays.toString(geranPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setGeranPositioningDataInformation(AVPHandler.lteGeranPosDataInfo2MapGeranPosDataInfo(geranPositioningData));
                            }
                        }
                        byte[] geranGANSSPositioningData = locationReportRequest.getGERANPositioningInfo().getGERANGANSSPositioningData();
                        if (geranGANSSPositioningData != null) {
                            slgLrrAvpValues.setGeranGANSSPositioningData(geranGANSSPositioningData);
                            this.logger.fine("\nonLocationReportRequest, GERAN-GANSS-Positioning-Data AVP: "
                                    + Arrays.toString(geranGANSSPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setGeranGANSSpositioningData(AVPHandler.lteGeranGanssPosDataInfo2MapGeranGanssPosDataInfo(geranGANSSPositioningData));
                            }
                        }
                    }

                    if (locationReportRequest.hasCellGlobalIdentity()) {
                        slgLrrAvpValues.setCellGlobalIdentity(locationReportRequest.getCellGlobalIdentity());
                        this.logger.fine("\nonLocationReportRequest, Cell-Global-Identity AVP: " + Arrays.toString(locationReportRequest.getCellGlobalIdentity()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setCellGlobalIdentity(AVPHandler.byte2String(locationReportRequest.getCellGlobalIdentity()));

                        }
                    }

                    if (locationReportRequest.hasUTRANPositioningInfo()) {
                        slgLrrAvpValues.setUtranPositioningInfoAvp(locationReportRequest.getUTRANPositioningInfo());
                        this.logger.fine("\nonLocationReportRequest, UTRAN-Positioning-Info AVP: " + locationReportRequest.getUTRANPositioningInfo());
                        byte[] utranPositioningData = locationReportRequest.getUTRANPositioningInfo().getUTRANPositioningData();
                        if (utranPositioningData != null) {
                            slgLrrAvpValues.setUtranPositioningData(utranPositioningData);
                            this.logger.fine("\nonLocationReportRequest, UTRAN-Positioning-Data AVP: "
                                    + Arrays.toString(utranPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setUtranPositioningDataInfo(AVPHandler.lteUtranPosData2MapUtranPosDataInfo(utranPositioningData));
                            }
                        }
                        byte[] utranGANSSPositioningData = locationReportRequest.getUTRANPositioningInfo().getUTRANGANSSPositioningData();
                        if (utranGANSSPositioningData != null) {
                            slgLrrAvpValues.setUtranGANSSPositioningData(utranGANSSPositioningData);
                            this.logger.fine("\nonLocationReportRequest, UTRAN-GANSS-Positioning-Data AVP: "
                                    + Arrays.toString(utranGANSSPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setUtranGANSSpositioningData(AVPHandler.lteUtranGanssPosData2MapUtranGanssPosDataInfo(utranGANSSPositioningData));
                            }
                        }
                        byte[] utranAdditionalPositioningData = locationReportRequest.getUTRANPositioningInfo().getUTRANAdditionalPositioningData();
                        if (utranAdditionalPositioningData != null) {
                            slgLrrAvpValues.setUtranAdditionalPositioningData(utranAdditionalPositioningData);
                            this.logger.fine("\nonLocationReportRequest, UTRAN-Additional-Positioning-Data AVP: "
                                    + Arrays.toString(utranAdditionalPositioningData));
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setUtranAdditionalPositioningData(AVPHandler.lteUtranAddPosData2MapUtranAdditionalPositioningdata(utranAdditionalPositioningData));
                            }
                        }
                    }

                    if (locationReportRequest.hasServiceAreaIdentity()) {
                        slgLrrAvpValues.setServiceAreaIdentity(locationReportRequest.getServiceAreaIdentity());
                        this.logger.fine("\nonLocationReportRequest, Service-Area-Identity AVP: " + Arrays.toString(locationReportRequest.getServiceAreaIdentity()));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setServiceAreaIdentity(AVPHandler.byte2String(locationReportRequest.getServiceAreaIdentity()));
                        }
                    }

                    if (locationReportRequest.hasLCSServiceTypeID()) {
                        slgLrrAvpValues.setLcsServiceTypeId(locationReportRequest.getLCSServiceTypeID());
                        this.logger.fine("\nonLocationReportRequest, LCS-Service-Type-ID AVP: " + locationReportRequest.getLCSServiceTypeID());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsServiceTypeID(AVPHandler.long2Int(locationReportRequest.getLCSServiceTypeID()));
                        }
                    }

                    if (locationReportRequest.hasPseudonymIndicator()) {
                        slgLrrAvpValues.setPseudonymIndicator(locationReportRequest.getPseudonymIndicator());
                        this.logger.fine("\nonLocationReportRequest, Pseudonym-Indicator AVP: " + locationReportRequest.getPseudonymIndicator());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setPseudonymIndicator(AVPHandler.ltePseudonymInd2Boolean(locationReportRequest.getPseudonymIndicator()));
                        }
                    }

                    if (locationReportRequest.hasLCSQoSClass()) {
                        slgLrrAvpValues.setLcsQoSClass(locationReportRequest.getLCSQoSClass());
                        this.logger.fine("\nonLocationReportRequest, LCS-QoS-Class AVP: " + locationReportRequest.getLCSQoSClass());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLteLcsQoSClass(locationReportRequest.getLCSQoSClass());
                        }
                    }

                    DiameterIdentity sgsnName, sgsnRealm, mmeName, mmeRealm, tgppAAAServerName;
                    Address servingNodeGmlcAddress;
                    long lcsCapabilitiesSets = -1;
                    if (locationReportRequest.hasServingNode()) {
                        slgLrrAvpValues.setServingNodeAvp(locationReportRequest.getServingNode());
                        this.logger.fine("\nonLocationReportRequest, Serving-Node AVP: " + locationReportRequest.getServingNode());
                        byte[] sgsnNumber = locationReportRequest.getServingNode().getSGSNNumber();
                        sgsnName = locationReportRequest.getServingNode().getSGSNName();
                        sgsnRealm = locationReportRequest.getServingNode().getSGSNRealm();
                        mmeName = locationReportRequest.getServingNode().getMMEName();
                        mmeRealm = locationReportRequest.getServingNode().getMMERealm();
                        byte[] mscNumber = locationReportRequest.getServingNode().getMSCNumber();
                        tgppAAAServerName = locationReportRequest.getServingNode().get3GPPAAAServerName();
                        if (locationReportRequest.getServingNode().hasLcsCapabilitiesSets())
                            lcsCapabilitiesSets = locationReportRequest.getServingNode().getLcsCapabilitiesSets();
                        servingNodeGmlcAddress = locationReportRequest.getServingNode().getGMLCAddress();
                        slgLrrAvpValues.setServingNodeGmlcAddress(servingNodeGmlcAddress);
                        if (gmlcCdrState.isInitialized()) {
                            if (sgsnNumber != null)
                                gmlcCdrState.setSgsnNumber(AVPHandler.tbcd2IsdnAddressString(sgsnNumber));
                            if (sgsnName != null)
                                gmlcCdrState.setSgsnName(AVPHandler.diameterIdToMapDiameterId(sgsnName));
                            if (sgsnRealm != null)
                                gmlcCdrState.setSgsnRealm(AVPHandler.diameterIdToMapDiameterId(sgsnRealm));
                            if (mmeName != null)
                                gmlcCdrState.setMmeName(AVPHandler.diameterIdToMapDiameterId(mmeName));
                            if (mmeRealm != null)
                                gmlcCdrState.setMmeRealm(AVPHandler.diameterIdToMapDiameterId(mmeRealm));
                            if (mscNumber != null)
                                gmlcCdrState.setMscNumber(AVPHandler.tbcd2IsdnAddressString(mscNumber));
                            if (lcsCapabilitiesSets > -1)
                                gmlcCdrState.setAaaServerName(AVPHandler.diameterIdToMapDiameterId(tgppAAAServerName));
                            gmlcCdrState.setLcsCapabilitiesSets(lcsCapabilitiesSets);
                            if (servingNodeGmlcAddress != null)
                                gmlcCdrState.sethGmlcAddress(AVPHandler.address2GsnAddress(servingNodeGmlcAddress));
                        }
                    }

                    byte[] lcsReferenceNumber = null;
                    if (locationReportRequest.hasLCSReferenceNumber()) {
                        lcsReferenceNumber = locationReportRequest.getLCSReferenceNumber();
                        slgLrrAvpValues.setLcsReferenceNumber(lcsReferenceNumber);
                        this.logger.fine("\nonLocationReportRequest, LCS-Reference-Number AVP: " + Arrays.toString(lcsReferenceNumber));
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLcsReferenceNumber(AVPHandler.byte2Int(lcsReferenceNumber));
                        }
                    }

                    if (locationReportRequest.hasDeferredMTLRData()) {
                        slgLrrAvpValues.setDeferredMTLRDataAvp(locationReportRequest.getDeferredMTLRData());
                        this.logger.fine("\nonLocationReportRequest, Deferred-MT-LR-Data AVP");
                        long deferredLocationType = locationReportRequest.getDeferredMTLRData().getDeferredLocationType();
                        long terminationCause = locationReportRequest.getDeferredMTLRData().getTerminationCause();
                        this.logger.fine("\nonLocationReportRequest, Deferred-MT-LR-Data AVP, " +
                                "Deferred-Location-Type=" + deferredLocationType + ",Termination-Cause=" + terminationCause);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setDeferredmtlrData(AVPHandler.lteDeferredMtlrData2MapDeferredmtlrData(locationReportRequest.getDeferredMTLRData()));
                        }
                    }

                    Address hGmlcAddress = null;
                    if (locationReportRequest.hasGMLCAddress()) {
                        hGmlcAddress = locationReportRequest.getGMLCAddress();
                        slgLrrAvpValues.setGmlcAddress(hGmlcAddress);
                        this.logger.fine("\nonLocationReportRequest, (H-GMLC Address) GMLC-Address AVP: " + hGmlcAddress);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.sethGmlcAddress(AVPHandler.address2GsnAddress(hGmlcAddress));
                        }
                    }

                    if (locationReportRequest.hasReportingAmount()) {
                        long sequenceNumber = locationReportRequest.getReportingAmount();
                        if (sequenceNumber > -1) {
                            slgLrrAvpValues.setSequenceNumber(sequenceNumber);
                            this.logger.fine("\nonLocationReportRequest, (Sequence Number) Reporting-Amount AVP: " + sequenceNumber);
                            if (gmlcCdrState.isInitialized()) {
                                gmlcCdrState.setSequenceNumber((int) sequenceNumber);
                            }
                        }
                    }

                    if (locationReportRequest.hasPeriodicLDRInformation()) {
                        slgLrrAvpValues.setPeriodicLDRInformation(locationReportRequest.getPeriodicLDRInformation());
                        long reportingAmount = locationReportRequest.getPeriodicLDRInformation().getReportingAmount();
                        long reportingInterval = locationReportRequest.getPeriodicLDRInformation().getReportingInterval();
                        this.logger.fine("\nonLocationReportRequest, Periodic-LDR-Information AVP, " +
                                "Reporting-Amount=" + reportingAmount + ",Reporting-Interval=" + reportingInterval);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setPeriodicLDRInfo(AVPHandler.ltePeriodicLDRInfo2MapPeriodicLDRInfo(locationReportRequest.getPeriodicLDRInformation()));
                        }
                    }

                    if (locationReportRequest.hasESMLCCellInfo()) {
                        slgLrrAvpValues.setEsmlcCellInfoAvp(locationReportRequest.getESMLCCellInfo());
                        this.logger.fine("\nonLocationReportRequest, ESMLC-Cell-Info AVP: " + locationReportRequest.getESMLCCellInfo());
                        if (ecgi == null) {
                            ecgi = locationReportRequest.getESMLCCellInfo().getECGI();
                            slgLrrAvpValues.setEcgi(ecgi);
                            this.logger.fine("\nonLocationReportRequest, ECGI AVP: " + Arrays.toString(ecgi));
                        }
                        long cellPortionId = locationReportRequest.getESMLCCellInfo().getCellPortionID();
                        if (cellPortionId > -1) {
                            slgLrrAvpValues.setCellPortionId(cellPortionId);
                            this.logger.fine("\nonLocationReportRequest, Cell-Portion-ID AVP: " + cellPortionId);
                        }
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setEUtranCgi(AVPHandler.lteEcgi2MapEutranCgi(ecgi));
                            gmlcCdrState.setCellPortionId(cellPortionId);
                        }
                    }

                    if (locationReportRequest.has1xRTTRCID()) {
                        slgLrrAvpValues.setOneXRttRcid(locationReportRequest.get1xRTTRCID());
                        this.logger.fine("\nonLocationReportRequest, 1x-RTT-RCID AVP: " + Arrays.toString(locationReportRequest.get1xRTTRCID()));
                    }

                    if (locationReportRequest.hasDelayedLocationReportingData()) {
                        slgLrrAvpValues.setDelayedLocationReportingDataAvp(locationReportRequest.getDelayedLocationReportingData());
                        this.logger.fine("\nonLocationReportRequest, Delayed-Location-Reporting-Data AVP: " + locationReportRequest.getDelayedLocationReportingData());
                    }

                    long lrrFlags = -1;
                    if (locationReportRequest.hasLRRFlags()) {
                        lrrFlags = locationReportRequest.getLRRFlags();
                        slgLrrAvpValues.setLrrFlags(lrrFlags);
                        /*
                        The LRR-Flags AVP is of type Unsigned32, and it shall contain a bit mask. The meaning of the bits shall be as defined in table 7.4.35/1:
                        Table 7.4.35/1: LRR-Flags
                        Bit	Name                            Description
                        0   Lgd/SLg-Indicator               This bit, when set, indicates that the Location Report Request message is sent on the Lgd interface,
                                                            i.e. the source node is an SGSN (or a combined MME/SGSN to which the UE is attached via UTRAN or GERAN).
                                                            This bit, when cleared, indicates that the Location Report Request message is sent on the SLg interface,
                                                            i.e. the source node is an MME (or a combined MME/SGSN to which the UE is attached via E-UTRAN).
                        1   MO-LR-ShortCircuit-Indicator    This bit, when set, indicates that the MO-LR short circuit feature is used by the UE for location estimate.
                                                            This bit is applicable only when for deferred MT-LR procedure and when the message is sent over Lgd interface.
                        2   MO-LR-ShortCircuit-Requested    This bit, when set, indicates that the UE is requesting to use MO-LR short circuit feature for location estimate.
                                                            This bit is applicable only when periodic MO-LR TTTP procedure is initiated by the UE
                                                            and when the message is sent over Lgd interface.
                         */
                        this.logger.fine("\nonLocationReportRequest, LRR-Flags AVP: " + lrrFlags);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLrrFlags(lrrFlags);
                        }
                    }

                    if (locationReportRequest.hasCivicAddress()) {
                        slgLrrAvpValues.setCivicAddress(locationReportRequest.getCivicAddress());
                        this.logger.fine("\nonLocationReportRequest, Civic-Address AVP: " + locationReportRequest.getCivicAddress());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setCivicAddress(locationReportRequest.getCivicAddress());
                        }
                    }

                    if (locationReportRequest.hasBarometricPressure()) {
                        slgLrrAvpValues.setBarometricPressure(locationReportRequest.getBarometricPressure());
                        this.logger.fine("\nonLocationReportRequest, Barometric-Pressure AVP: " + locationReportRequest.getBarometricPressure());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setBarometricPressureMeasurement(locationReportRequest.getBarometricPressure());
                        }
                    }

                    if (locationReportRequest.hasAmfInstanceId()) {
                        slgLrrAvpValues.setAmfInstanceId(locationReportRequest.getAmfInstanceId());
                        this.logger.fine("\nonLocationReportRequest, AMF-Instance-Id AVP: " + locationReportRequest.getAmfInstanceId());
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setAmfInstanceId(locationReportRequest.getAmfInstanceId());
                        }
                    }

                    long lraFlags = -1;
                    /*
                    The LRA-Flags AVP is of type Unsigned32, and it shall contain a bit mask.
                    The meaning of the bits shall be as defined in table 7.4.56/1:
                    Table 7.4.56/1: LRA-Flags
                    Bit Name                            Description
                    0	MO-LR-ShortCircuit-Indicator    This bit, when set, indicates that the MO-LR short circuit feature
                                                        is used for obtaining location estimate.
                                                        This bit is applicable only when the message is sent over Lgd interface.

                     */
                    if (lrrFlags == 3 || lrrFlags == 5 || lrrFlags == 7) {
                        // we only set LRA-Flags if LRR-Flags AVP indicates the message is sent over Lgd interface
                        // and MO-LR-ShortCircuit-Indicator and/or MO-LR-ShortCircuit-Requested is/are set
                        lraFlags = 1L;
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setLraFlags(lraFlags);
                        }
                    }

                    if (gmlcCdrState.isInitialized()) {
                        this.createCDRRecord(RecordStatus.LTE_LRR_SUCCESS);
                    }

                    SLgClientSessionActivity slgClientSessionActivity = this.slgProvider.createSLgClientSessionActivity(locationReportRequest.getOriginHost(), locationReportRequest.getOriginRealm(), locationReportRequest.getSessionId());
                    ActivityContextInterface slgACIF = slgAcif.getActivityContextInterface(slgClientSessionActivity);
                    slgACIF.attach(getSbbContext().getSbbLocalObject());

                    // < Location-Report-Answer > ::= < Diameter Header: 8388621, PXY, 16777255>
                    LocationReportAnswer lra = slgClientSessionActivity.createLocationReportAnswer(locationReportRequest.getHeader());

                    if (hGmlcAddress != null)
                        lra.setGMLCAddress(hGmlcAddress);
                    if (lraFlags > -1) {
                        DiameterAvp lraFlagsAvp = slgAVPFactory.createAvp(TGPP_VENDOR_ID, ELPAVPCodes.LRA_FLAGS, lraFlags);
                        lra.setLRAFlags(lraFlagsAvp.longValue());
                    }
                    if (lcsReferenceNumber != null) {
                        DiameterAvp lcsReferenceNumberAvp = slgAVPFactory.createAvp(TGPP_VENDOR_ID, ELPAVPCodes.LCS_REFERENCE_NUMBER, lcsReferenceNumber);
                        lra.setLCSReferenceNumber(lcsReferenceNumberAvp.byteArrayValue());
                    }

                    /**
                     * TODO
                     * Set Reporting-PLMN-List AVP in LRA
                     * If present, Reporting-PLMN-List AVP shall contain a list of PLMNs in which
                     * the subsequent location estimates must be obtained as part of periodic MO-LR Transfer To Third Party (TTTP) procedure.
                     * Reporting-PLMN-List is applicable only when the message is sent towards the SGSN or the SGSN part of the combined MME/SGSN.

                     * Get Reporting-PLMN-List from MongoDB by adding it when sending Provide-Location-Request?
                     * then, following: lra.setReportingPLMNList(reportingPLMNListAvp);
                     */

                    slgClientSessionActivity.sendLocationReportAnswer(lra);

                    // Handle successful retrieval of subscriber's location report request (SLR request) info by sending HTTP POST back to the requestor
                    if (this.logger.isFineEnabled()) {
                        if (lcsReferenceNumber != null)
                            logger.fine(String.format("Handling SubscriberLocationReport POST ReferenceNumber '%s'\n", AVPHandler.byte2Int(lcsReferenceNumber)));
                        else
                            logger.fine("Handling SubscriberLocationReport POST (non-triggered by PSL)\n");
                    }
                    httpSubscriberLocationReport = getHttpSubscriberLocationReport();
                    if (lcsReferenceNumber != null)
                        httpSubscriberLocationReport.Perform(HttpReport.HttpMethod.POST, AVPHandler.byte2Int(lcsReferenceNumber), slgLrrAvpValues, false);
                    else
                        httpSubscriberLocationReport.Perform(HttpReport.HttpMethod.POST, null, slgLrrAvpValues, false);
                    httpSubscriberLocationReport.closeMongo();
                } else {
                    if (getGMLCCDRState().isInitialized()) {
                        this.createCDRRecord(RecordStatus.LTE_LRR_UNSPECIFIED_ERROR);
                    }
                }
            } catch (Throwable e) {
                logger.warning("Exception when processing onLocationReportRequest response: " + e.getMessage(), e);
                this.createCDRRecord(RecordStatus.LTE_LRR_SYSTEM_FAILURE);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onLocationReportRequest=%s", locationReportRequest), e);
            this.createCDRRecord(RecordStatus.LTE_LRR_SYSTEM_FAILURE);
        } finally {
            detachFromSLgClientActivity(aci);
        }
    }

    /**
     * SLg LRA Event
     */
    public void onLocationReportAnswer(LocationReportAnswer locationReportAnswer, ActivityContextInterface aci) {
        try {
            this.logger.fine("\nReceived onLocationReportAnswer = " + locationReportAnswer + ", ActivityContextInterface = " + aci);
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onLocationReportAnswer=%s", locationReportAnswer), e);
        }
    }

    //////////////////////////
    // IMS Events handlers //
    /////////////////////////

    /**
     * IMS User Data retrieval between AS (GMLC) and HSS
     * Sh (AS-HSS) Diameter-based interface events for user data retrieval according to 3GPP TS 29.328 / 29.329
     */

    /**
     * Sh UDR Event
     */
    public void onUserDataRequest(UserDataRequest userDataRequest, ActivityContextInterface aci) {

        try {
            if (this.logger.isFineEnabled()) {
                this.logger.fine("\nReceived onUserDataRequest = " + userDataRequest + ", ActivityContextInterface = " + aci);
            }

        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onUserDataRequest=%s", userDataRequest), e);
        }

    }

    /**
     * Sh UDA Event
     */
    public void onUserDataAnswer(UserDataAnswer userDataAnswer, ActivityContextInterface aci) {

        String shUdrMsisdn = null, publicIdsMsisdn, imsPublicIdentity, eUtranCellGlobalId, trackingAreaId, geographicalInfoEps = null,
            geodeticInfoEps, mmeName, currentLocationRetrieved, ageOfLocationInformation, csgId = null, visitedPlmnId,
            ratType = null, nrCellGlobalId, amfAddress, smsfAddress, curlUser;
        LocalTimeZone localTimeZone = null;
        Long transaction = null;
        long resultCode = -1;
        byte[] shUserData;
        DateTime eventTime = DateTime.now();

        try {
            logger.fine("\n\nReceived Sh UDA with session Id: " + userDataAnswer.getSessionId() + ", host '" + userDataAnswer.getOriginHost()
                    + "', realm '" + userDataAnswer.getOriginRealm() + "'");
            this.logger.fine("\nonUserDataAnswer event details: " + userDataAnswer);

            ShUdaAvpValues shUdaAvpValues = new ShUdaAvpValues();

            try {

                MLPResponse.MLPResultType mlpRespResult = null;
                String mlpClientErrorMessage = null;
                DiameterIdentity gmlcHost =
                    new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginHost());
                DiameterIdentity gmlcRealm =
                    new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginRealm());

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.shUdaCdrInitializer(aci, this.getCDRInterface(), userDataAnswer, gmlcHost, gmlcRealm);
                // Set timer last
                this.setTimer(aci);

                if (userDataAnswer.hasResultCode()) {
                    resultCode = userDataAnswer.getResultCode();
                } else if (userDataAnswer.hasExperimentalResult()) {
                    resultCode = userDataAnswer.getExperimentalResult().getExperimentalResultCode();
                }

                // Transaction
                transaction = mobileCoreNetworkTransactions.getTransactionId(userDataAnswer.getSessionId());
                if (transaction == null) {
                    throw new Exception();
                }
                TimerID udaTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                if (udaTimerID != null)
                    this.timerFacility.cancelTimer(udaTimerID);
                shUdrMsisdn = (String) mobileCoreNetworkTransactions.getValue(transaction, "shUdrMsisdn");
                curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                gmlcCdrState.setDialogStartTime(dialogStartTime);
                if (gmlcCdrState.isInitialized()) {
                    gmlcCdrState.setCurlUser(curlUser);
                    if (dialogStartTime != null) {
                        Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                        gmlcCdrState.setDialogDuration(dialogDuration);
                    }
                }
                mobileCoreNetworkTransactions.destroy(transaction);

                if (resultCode == 2001) {

                    mlpRespResult = MLPResponse.MLPResultType.OK;
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setStatusCode(resultCode);
                    }

                    if (userDataAnswer.hasUserData()) {
                        shUserData = userDataAnswer.getUserData();
                        shUdaAvpValues.setUserData(shUserData);
                        String xmlShUserData = new String(shUserData, StandardCharsets.UTF_8);
                        ShDataReader shDataReader = new ShDataReader();
                        shDataReader.ShXMLReader(xmlShUserData);

                        if (shDataReader.getShPublicIdentifiers() != null) {
                            publicIdsMsisdn = shDataReader.getShPublicIdentifiers().getMsisdn();
                            shUdaAvpValues.setMsisdn(shDataReader.getShPublicIdentifiers().getMsisdn());
                            imsPublicIdentity = shDataReader.getShPublicIdentifiers().getImsPublicIdentity();
                            shUdaAvpValues.setImsPublicIdentity(imsPublicIdentity);
                            this.logger.fine("\nPublicIdentifiers:");
                            if (shUdaAvpValues.getMsisdn() != null)
                                this.logger.fine("\nMSISDN: " + shUdaAvpValues.getMsisdn());
                            if (shUdaAvpValues.getCsCellGlobalId() != null)
                                this.logger.fine("\nIMSPublicIdentity: " + shUdaAvpValues.getImsPublicIdentity());

                        } else {
                            this.logger.fine("\nPublicIdentifiers is NULL !!!");
                            publicIdsMsisdn = shUdrMsisdn;
                            shUdaAvpValues.setMsisdn(publicIdsMsisdn);
                        }

                        if (shDataReader.getShCSLocationInfo() != null) {
                            shUdaAvpValues.setCsLocationInformation(shDataReader.getShCSLocationInfo());
                            String cellGlobalId = shDataReader.getShCSLocationInfo().getCellGlobalId();
                            String serviceAreaId = shDataReader.getShCSLocationInfo().getServiceAreaId();
                            String locationAreaId = shDataReader.getShCSLocationInfo().getLocationAreaId();
                            String locationNumber = shDataReader.getShCSLocationInfo().getLocationNumber();
                            String geographicalInfo = shDataReader.getShCSLocationInfo().getGeographicalInformation();
                            String geodeticInfo = shDataReader.getShCSLocationInfo().getGeodeticInformation();
                            ShCellGlobalId csCellGlobalId = new ShCellGlobalId();
                            ShServiceAreaId csServiceAreaId = new ShServiceAreaId();
                            ShLocationAreaId csLocationAreaId = new ShLocationAreaId();
                            ShLocationNumber csLocationNumber = new ShLocationNumber();
                            ShGeographicalInformation csGeographicalInformation = new ShGeographicalInformation();
                            ShGeodeticInformation csGeodeticInformation = new ShGeodeticInformation();
                            if (cellGlobalId != null) {
                                csCellGlobalId.setCellGlobalIdStr(cellGlobalId);
                                shUdaAvpValues.setCsCellGlobalId(csCellGlobalId);
                            }
                            if (serviceAreaId != null) {
                                csServiceAreaId.setServiceAreaIdStr(serviceAreaId);
                                shUdaAvpValues.setCsServiceAreaId(csServiceAreaId);
                            }
                            if (locationAreaId != null) {
                                csLocationAreaId.setLocationAreIdStr(locationAreaId);
                                shUdaAvpValues.setCsLocationAreaId(csLocationAreaId);
                            }
                            if (locationNumber != null) {
                                csLocationNumber.setLocationNumberStr(locationNumber);
                                shUdaAvpValues.setLocationNumber(csLocationNumber);
                            }
                            if (geographicalInfo != null) {
                                csGeographicalInformation.setGeographicalInfoStr(geographicalInfo);
                                shUdaAvpValues.setCsGeographicalInformation(csGeographicalInformation);
                            }
                            if (geodeticInfo != null) {
                                csGeodeticInformation.setGeodeticInfoStr(geodeticInfo);
                                shUdaAvpValues.setCsGeodeticInformation(csGeodeticInformation);
                            }
                            if (shDataReader.getShCSLocationInfo().getMscNumber() != null)
                                shUdaAvpValues.setMscNumber(shDataReader.getShCSLocationInfo().getMscNumber());
                            if (shDataReader.getShCSLocationInfo().getVlrNumber() != null)
                                shUdaAvpValues.setVlrNumber(shDataReader.getShCSLocationInfo().getVlrNumber());
                            if (shDataReader.getShCSLocationInfo().getCurrentLocationRetrieved() != null)
                                shUdaAvpValues.setCsCurrentLocationInfoRetrieved(shDataReader.getShCSLocationInfo().getCurrentLocationRetrieved());
                            if (shDataReader.getShCSLocationInfo().getAgeOfLocationInformation() != null)
                                shUdaAvpValues.setCsAgeOfLocationInfo(Integer.valueOf(shDataReader.getShCSLocationInfo().getAgeOfLocationInformation()));
                            CSLocationInformationExtension csLocationExtension = shDataReader.getShCSLocationInfo().getCsLocationInformationExtension();
                            if (csLocationExtension != null) {
                                UserCSGInformation userCSGInformationCS = csLocationExtension.getUserCSGInformation();
                                if (userCSGInformationCS != null) {
                                    csgId = userCSGInformationCS.getCsgid();
                                    ShUserCSGInformation shCsUserCSGInformation = new ShUserCSGInformation();
                                    shCsUserCSGInformation.setUserCSGInformationStr(csgId);
                                    shUdaAvpValues.setUserCSGInformation(shCsUserCSGInformation);
                                    // TODO CDR (doesn't seem to be needed)
                                }
                                CSLocationInformationExtension2 csLocationInformationExtension2 = csLocationExtension.getCsLocationInformationExtension2();
                                if (csLocationInformationExtension2 != null) {
                                    eUtranCellGlobalId = csLocationInformationExtension2.geteUTRANCellGlobalId();
                                    if (eUtranCellGlobalId != null) {
                                        ShEUTRANCellGlobalId shCsEUTRANCellGlobalId = new ShEUTRANCellGlobalId();
                                        shCsEUTRANCellGlobalId.setECGIStr(eUtranCellGlobalId);
                                        shUdaAvpValues.setEutrancgi(shCsEUTRANCellGlobalId);
                                    }
                                    trackingAreaId = csLocationInformationExtension2.getTrackingAreaId();
                                    if (trackingAreaId != null) {
                                        ShTrackingAreaId shCsTrackingAreaId = new ShTrackingAreaId();
                                        shCsTrackingAreaId.setTrackingAreaIdStr(trackingAreaId);
                                        shUdaAvpValues.setTrackingAreaId(shCsTrackingAreaId);
                                    }
                                    CSLocationInformationExtension3 csLocationInformationExtension3 = csLocationInformationExtension2.getCsLocationInformationExtension3();
                                    if (csLocationInformationExtension3 != null) {
                                        localTimeZone = csLocationInformationExtension3.getLocalTimeZone();
                                        if (localTimeZone != null) {
                                            shUdaAvpValues.setCsLocalTimeZone(localTimeZone);
                                            if (gmlcCdrState.isInitialized()) {
                                                gmlcCdrState.setLocalTimeZone(localTimeZone);
                                            }
                                        }
                                    }
                                    if (this.logger.isFineEnabled()) {
                                        this.logger.fine("\nCSLocationInformation:");
                                        this.logger.fine("\nLocationNumber: " + shUdaAvpValues.getLocationNumber().toString());
                                        this.logger.fine("\nCellGlobalId: " + shUdaAvpValues.getCsCellGlobalId().toString());
                                        this.logger.fine("\nGeographicalInformation: " + shUdaAvpValues.getCsGeographicalInformation().toString());
                                        this.logger.fine("\nGeodeticInformation: " + shUdaAvpValues.getCsGeodeticInformation().toString());
                                        this.logger.fine("\nMSC Number [address = " + shUdaAvpValues.getMscNumber().getAddress() + "]");
                                        this.logger.fine("\nVLR Number [address = " + shUdaAvpValues.getVlrNumber().getAddress() + "]");
                                        this.logger.fine("\nCurrentLocationRetrieved = " + shUdaAvpValues.getCsCurrentLocationInfoRetrieved());
                                        this.logger.fine("\nAgeOfLocationInformation = " + shUdaAvpValues.getCsAgeOfLocationInfo());
                                        if (shUdaAvpValues.getUserCSGInformation() != null)
                                            this.logger.fine("\nUserCSGInformation [ " + shUdaAvpValues.getUserCSGInformation().toString() + "]");
                                        if (eUtranCellGlobalId != null)
                                            this.logger.fine("\nE-UTRANCellGlobalId: " + shUdaAvpValues.getEutrancgi().toString());
                                        if (trackingAreaId != null)
                                            this.logger.fine("\nE-UTRANCellGlobalId: " + shUdaAvpValues.getTrackingAreaId().toString());
                                        if (localTimeZone != null) {
                                            this.logger.fine("\nTime Zone = " + shUdaAvpValues.getCsLocalTimeZone());
                                        }
                                    }
                                }
                            }
                        }

                        if (shDataReader.getShPSLocationInfo() != null) {
                            shUdaAvpValues.setPsLocationInformation(shDataReader.getShPSLocationInfo());
                            String cellGlobalIdPs = shDataReader.getShPSLocationInfo().getCellGlobalId();
                            String serviceAreaIdPs = shDataReader.getShPSLocationInfo().getServiceAreaId();
                            String locationAreaIdPs = shDataReader.getShPSLocationInfo().getLocationAreaId();
                            String routingAreaIdPs = shDataReader.getShPSLocationInfo().getRoutingAreaId();
                            String geographicalInfoPs = shDataReader.getShPSLocationInfo().getGeographicalInformation();
                            String geodeticInfoPs = shDataReader.getShPSLocationInfo().getGeodeticInformation();
                            ShCellGlobalId psCellGlobalId = new ShCellGlobalId();
                            ShServiceAreaId psServiceAreaId = new ShServiceAreaId();
                            ShLocationAreaId psLocationAreaId = new ShLocationAreaId();
                            ShRoutingAreaId psRAI = new ShRoutingAreaId();
                            ShGeographicalInformation psGeographicalInformation = new ShGeographicalInformation();
                            ShGeodeticInformation psGeodeticInformation = new ShGeodeticInformation();
                            if (cellGlobalIdPs != null) {
                                psCellGlobalId.setCellGlobalIdStr(cellGlobalIdPs);
                                shUdaAvpValues.setPsCellGlobalId(psCellGlobalId);
                            }
                            if (serviceAreaIdPs != null) {
                                psServiceAreaId.setServiceAreaIdStr(serviceAreaIdPs);
                                shUdaAvpValues.setPsServiceAreaId(psServiceAreaId);
                            }
                            if (locationAreaIdPs != null) {
                                psLocationAreaId.setLocationAreIdStr(locationAreaIdPs);
                                shUdaAvpValues.setPsLocationAreaId(psLocationAreaId);
                            }
                            if (routingAreaIdPs != null) {
                                psRAI.setRoutingAreaIdentityStr(routingAreaIdPs);
                                shUdaAvpValues.setRoutingAreaId(psRAI);
                            }
                            if (geographicalInfoPs != null) {
                                psGeographicalInformation.setGeographicalInfoStr(geographicalInfoPs);
                                shUdaAvpValues.setPsGeographicalInformation(psGeographicalInformation);
                            }
                            if (geodeticInfoPs != null) {
                                psGeodeticInformation.setGeodeticInfoStr(geodeticInfoPs);
                                shUdaAvpValues.setPsGeodeticInformation(psGeodeticInformation);
                            }
                            if (shDataReader.getShPSLocationInfo().getSgsnNumber() != null)
                                shUdaAvpValues.setSgsnNumber(shDataReader.getShPSLocationInfo().getSgsnNumber());
                            if (shDataReader.getShPSLocationInfo().getCurrentLocationRetrieved() != null)
                                shUdaAvpValues.setPsCurrentLocationInfoRetrieved(shDataReader.getShPSLocationInfo().getCurrentLocationRetrieved());
                            if (shDataReader.getShPSLocationInfo().getAgeOfLocationInformation() != null)
                                shUdaAvpValues.setPsAgeOfLocationInfo(Integer.valueOf(shDataReader.getShPSLocationInfo().getAgeOfLocationInformation()));
                            PSLocationInformationExtension psLocationInformationExtension = shDataReader.getShPSLocationInfo().getPsLocationInformationExtension();
                            if (psLocationInformationExtension != null) {
                                UserCSGInformation userCSGInformationPS = psLocationInformationExtension.getUserCSGInformation();
                                if (userCSGInformationPS != null) {
                                    csgId = userCSGInformationPS.getCsgid();
                                    ShUserCSGInformation shPsUserCSGInformation = new ShUserCSGInformation();
                                    shPsUserCSGInformation.setUserCSGInformationStr(csgId);
                                    shUdaAvpValues.setUserCSGInformation(shPsUserCSGInformation);
                                    // TODO CDR (doesn't seem to be needed)
                                }
                                PSLocationInformationExtension2 psLocationInformationExtension2 = psLocationInformationExtension.getPsLocationInformationExtension2();
                                if (psLocationInformationExtension2 != null) {
                                    visitedPlmnId = psLocationInformationExtension2.getVisitedPLMNId();
                                    if (visitedPlmnId != null) {
                                        ShVisitedPLMNId shPsVisitedPLMNId = new ShVisitedPLMNId();
                                        shPsVisitedPLMNId.setVisitedPlmnIdStr(visitedPlmnId);
                                        shUdaAvpValues.setPsVisitedPLMNId(shPsVisitedPLMNId);
                                        if (gmlcCdrState.isInitialized()) {
                                            gmlcCdrState.setVisitedPlmnId(shUdaAvpValues.getPsVisitedPLMNId().getVisitedPlmnId());
                                        }
                                    }
                                    localTimeZone = psLocationInformationExtension2.getLocalTimeZone();
                                    if (localTimeZone != null) {
                                        shUdaAvpValues.setPsLocalTimeZone(localTimeZone);
                                        if (gmlcCdrState.isInitialized()) {
                                            gmlcCdrState.setLocalTimeZone(localTimeZone);
                                        }
                                    }
                                    PSLocationInformationExtension3 psLocationInformationExtension3 = psLocationInformationExtension2.getPsLocationInformationExtension3();
                                    if (psLocationInformationExtension3 != null) {
                                        ratType = psLocationInformationExtension3.getRatType();
                                        if (ratType != null) {
                                            shUdaAvpValues.setPsRatType(Integer.valueOf(ratType));
                                            if (gmlcCdrState.isInitialized()) {
                                                gmlcCdrState.setRatType(Integer.valueOf(ratType));
                                            }
                                        }
                                    }
                                    if (this.logger.isFineEnabled()) {
                                        this.logger.fine("\nPSLocationInformation:");
                                        if (shUdaAvpValues.getPsCellGlobalId() != null)
                                            this.logger.fine("\nCellGlobalId: " + shUdaAvpValues.getPsCellGlobalId().toString());
                                        if (shUdaAvpValues.getRoutingAreaId() != null)
                                            this.logger.fine("\nRouting Area Id: " + shUdaAvpValues.getRoutingAreaId().toString());
                                        if (shUdaAvpValues.getPsGeographicalInformation() != null)
                                            this.logger.fine("\nGeographicalInformation: " + shUdaAvpValues.getPsGeographicalInformation().toString());
                                        if (shUdaAvpValues.getPsGeodeticInformation() != null)
                                            this.logger.fine("\nGeodeticInformation: " + shUdaAvpValues.getPsGeodeticInformation().toString());
                                        this.logger.fine("\nSGSN Number [address = " + shUdaAvpValues.getSgsnNumber().toString() + "]");
                                        this.logger.fine("\nCurrentLocationRetrieved = " + shUdaAvpValues.getPsCurrentLocationInfoRetrieved());
                                        this.logger.fine("\nAgeOfLocationInformation = " + shUdaAvpValues.getPsAgeOfLocationInfo().toString());
                                        if (visitedPlmnId != null) {
                                            this.logger.fine("\nVisitedPLMNID = " + shUdaAvpValues.getPsVisitedPLMNId().toString());
                                        }
                                        if (localTimeZone != null) {
                                            this.logger.fine("\nTime Zone = " + shUdaAvpValues.getPsLocalTimeZone());
                                        }
                                        if (ratType != null) {
                                            this.logger.fine("\nRAT Type = " + shUdaAvpValues.getPsRatType());
                                        }
                                    }
                                }
                            }
                        }

                        EPSLocationInformation epsLocationInformation = null;
                        if (shDataReader.getShEPSLocationInfo() != null) {
                            if (shDataReader.getShEPSLocationInfo().getExtension() != null) {
                                if (shDataReader.getShEPSLocationInfo().getExtension().getExtension() != null) {
                                    if (shDataReader.getShEPSLocationInfo().getExtension().getExtension().getExtension() != null) {
                                        Extension shDataExtension4 = shDataReader.getShEPSLocationInfo().getExtension().getExtension().getExtension();
                                        if (shDataExtension4.getEpsLocationInformation() != null) {
                                            epsLocationInformation = shDataExtension4.getEpsLocationInformation();
                                            shUdaAvpValues.setEpsLocationInformation(epsLocationInformation);
                                            eUtranCellGlobalId = epsLocationInformation.geteUTRANCellGlobalId();
                                            trackingAreaId = epsLocationInformation.getTrackingAreaId();
                                            geographicalInfoEps = epsLocationInformation.getGeographicalInformation();
                                            geodeticInfoEps = epsLocationInformation.getGeodeticInformation();
                                            mmeName = epsLocationInformation.getMmeName();
                                            currentLocationRetrieved = epsLocationInformation.getCurrentLocationRetrieved();
                                            ageOfLocationInformation = epsLocationInformation.getAgeOfLocationInformation();
                                            if (epsLocationInformation.getUserCSGInformation() != null) {
                                                csgId = epsLocationInformation.getUserCSGInformation().getCsgid();
                                            }
                                            if (eUtranCellGlobalId != null) {
                                                ShEUTRANCellGlobalId shEUTRANCellGlobalId = new ShEUTRANCellGlobalId();
                                                shEUTRANCellGlobalId.setECGIStr(eUtranCellGlobalId);
                                                shUdaAvpValues.setEutrancgi(shEUTRANCellGlobalId);
                                            }
                                            if (trackingAreaId != null) {
                                                ShTrackingAreaId shTrackingAreaId = new ShTrackingAreaId();
                                                shTrackingAreaId.setTrackingAreaIdStr(trackingAreaId);
                                                shUdaAvpValues.setTrackingAreaId(shTrackingAreaId);
                                            }
                                            if (geographicalInfoEps != null) {
                                                ShGeographicalInformation shGeographicalInformationEps = new ShGeographicalInformation();
                                                shGeographicalInformationEps.setGeographicalInfoStr(geographicalInfoEps);
                                                shUdaAvpValues.setEpsGeographicalInformation(shGeographicalInformationEps);
                                            }
                                            if (geodeticInfoEps != null) {
                                                ShGeodeticInformation shGeodeticInformationEps = new ShGeodeticInformation();
                                                shGeodeticInformationEps.setGeodeticInfoStr(geodeticInfoEps);
                                                shUdaAvpValues.setEpsGeodeticInformation(shGeodeticInformationEps);
                                            }
                                            if (mmeName != null)
                                                shUdaAvpValues.setMmeName(mmeName);
                                            if (currentLocationRetrieved != null)
                                                shUdaAvpValues.setEpsCurrentLocationInfoRetrieved(currentLocationRetrieved);
                                            if (ageOfLocationInformation != null)
                                                shUdaAvpValues.setEpsAgeOfLocationInfo(Integer.valueOf(ageOfLocationInformation));
                                            if (csgId != null) {
                                                ShUserCSGInformation shUserCSGInformation = new ShUserCSGInformation();
                                                shUserCSGInformation.setUserCSGInformationStr(csgId);
                                                shUdaAvpValues.setUserCSGInformation(shUserCSGInformation);
                                                // TODO CDR (doesn't seem to be needed)
                                            }
                                            EPSLocationInformationExtension epsLocationInformationExtension = epsLocationInformation.getEpsLocationInformationExtension();
                                            if (epsLocationInformationExtension != null) {
                                                visitedPlmnId = epsLocationInformationExtension.getVisitedPLMNId();
                                                if (visitedPlmnId != null) {
                                                    ShVisitedPLMNId shEpsVisitedPLMNId = new ShVisitedPLMNId();
                                                    shEpsVisitedPLMNId.setVisitedPlmnIdStr(visitedPlmnId);
                                                    shUdaAvpValues.setEpsVisitedPLMNId(shEpsVisitedPLMNId);
                                                    if (gmlcCdrState.isInitialized()) {
                                                        gmlcCdrState.setVisitedPlmnId(shUdaAvpValues.getEpsVisitedPLMNId().getVisitedPlmnId());
                                                    }
                                                }
                                                localTimeZone = epsLocationInformationExtension.getLocalTimeZone();
                                                if (localTimeZone != null) {
                                                    shUdaAvpValues.setEpsLocalTimeZone(localTimeZone);
                                                    if (gmlcCdrState.isInitialized()) {
                                                        gmlcCdrState.setLocalTimeZone(localTimeZone);
                                                    }
                                                }
                                                EPSLocationInformationExtension2 epsLocationInformationExtension2 = epsLocationInformationExtension.getEpsLocationInformationExtension2();
                                                if (epsLocationInformationExtension2 != null) {
                                                    ratType = epsLocationInformationExtension2.getRatType();
                                                    shUdaAvpValues.setEpsRatType(Integer.valueOf(ratType));
                                                    if (gmlcCdrState.isInitialized()) {
                                                        gmlcCdrState.setRatType(Integer.valueOf(ratType));
                                                    }
                                                }
                                                if (this.logger.isFineEnabled()) {
                                                    this.logger.fine("\nEPSLocationInformation:");
                                                    if (shUdaAvpValues.getEutrancgi() != null)
                                                        this.logger.fine("\nE-UTRANCellGlobalId: " + shUdaAvpValues.getEutrancgi().toString());
                                                    if (shUdaAvpValues.getTrackingAreaId() != null)
                                                        this.logger.fine("\nTracking Area Id: " + shUdaAvpValues.getTrackingAreaId().toString());
                                                    if (shUdaAvpValues.getEpsGeographicalInformation() != null)
                                                        this.logger.fine("\nGeographicalInformation: " + shUdaAvpValues.getEpsGeographicalInformation().toString());
                                                    if (shUdaAvpValues.getEpsGeodeticInformation() != null)
                                                        this.logger.fine("\nGeodeticInformation: " + shUdaAvpValues.getEpsGeodeticInformation().toString());
                                                    this.logger.fine("\nMME name = " + shUdaAvpValues.getMmeName());
                                                    this.logger.fine("\nCurrentLocationRetrieved = " + shUdaAvpValues.getEpsCurrentLocationInfoRetrieved());
                                                    this.logger.fine("\nAgeOfLocationInformation = " + shUdaAvpValues.getEpsAgeOfLocationInfo());
                                                    if (shUdaAvpValues.getUserCSGInformation() != null)
                                                        this.logger.fine("\nUserCSGInformation [ " + shUdaAvpValues.getUserCSGInformation().toString() + "]");
                                                    if (visitedPlmnId != null) {
                                                        this.logger.fine("\nVisitedPLMNID = " + shUdaAvpValues.getEpsVisitedPLMNId().toString());
                                                    }
                                                    if (localTimeZone != null) {
                                                        this.logger.fine("\nTime Zone = " + shUdaAvpValues.getEpsLocalTimeZone());
                                                    }
                                                    if (ratType != null) {
                                                        this.logger.fine("\nRAT Type = " + shUdaAvpValues.getEpsRatType());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Sh5GSLocationInformation sh5GSLocationInformation = null;
                        if (shDataReader.getSh5GSLocationInfo() != null) {
                            if (shDataReader.getSh5GSLocationInfo().getExtension() != null) {
                                if (shDataReader.getSh5GSLocationInfo().getExtension().getExtension() != null) {
                                    if (shDataReader.getSh5GSLocationInfo().getExtension().getExtension().getExtension() != null) {
                                        if (shDataReader.getSh5GSLocationInfo().getExtension().getExtension().getExtension().getExtension() != null) {
                                            if (shDataReader.getSh5GSLocationInfo().getExtension().getExtension().getExtension().getExtension().getExtension() != null) {
                                                if (shDataReader.getSh5GSLocationInfo().getExtension().getExtension().getExtension().getExtension().getExtension().getExtension() != null) {
                                                    Extension shDataExtension7 = shDataReader.getSh5GSLocationInfo().getExtension().getExtension().getExtension().getExtension().getExtension().getExtension();
                                                    if (shDataExtension7 != null) {
                                                        sh5GSLocationInformation = shDataExtension7.getSh5GSLocationInformation();
                                                        shUdaAvpValues.setSh5GSLocationInformation(sh5GSLocationInformation);
                                                        nrCellGlobalId = sh5GSLocationInformation.getNRCellGlobalId();
                                                        eUtranCellGlobalId = sh5GSLocationInformation.getEUTRANCellGlobalId();
                                                        trackingAreaId = sh5GSLocationInformation.getTrackingAreaId();
                                                        amfAddress = sh5GSLocationInformation.getAMFAddress();
                                                        smsfAddress = sh5GSLocationInformation.getSMSFAddress();
                                                        currentLocationRetrieved = sh5GSLocationInformation.getCurrentLocationRetrieved();
                                                        ageOfLocationInformation = sh5GSLocationInformation.getAgeOfLocationInformation();
                                                        visitedPlmnId = sh5GSLocationInformation.getVisitedPLMNId();
                                                        ratType = sh5GSLocationInformation.getRatType();
                                                        localTimeZone = sh5GSLocationInformation.getLocalTimeZone();
                                                        if (nrCellGlobalId != null) {
                                                            ShNRCellGlobalId shNRCellGlobalId = new ShNRCellGlobalId();
                                                            shNRCellGlobalId.setNRCellGlobalIdStr(nrCellGlobalId);
                                                            shUdaAvpValues.setShNRCellGlobalId(shNRCellGlobalId);
                                                        }
                                                        if (eUtranCellGlobalId != null) {
                                                            ShEUTRANCellGlobalId shEUTRANCellGlobalId = new ShEUTRANCellGlobalId();
                                                            shEUTRANCellGlobalId.setECGIStr(eUtranCellGlobalId);
                                                            shUdaAvpValues.setEutrancgi(shEUTRANCellGlobalId);
                                                        }
                                                        if (trackingAreaId != null) {
                                                            ShTrackingAreaId shTrackingAreaId = new ShTrackingAreaId();
                                                            shTrackingAreaId.setTrackingAreaIdStr(trackingAreaId);
                                                            shUdaAvpValues.setTrackingAreaId(shTrackingAreaId);
                                                        }
                                                        if (geographicalInfoEps != null) {
                                                            ShGeographicalInformation shGeographicalInformation5GS = new ShGeographicalInformation();
                                                            shGeographicalInformation5GS.setGeographicalInfoStr(geographicalInfoEps);
                                                            shUdaAvpValues.setSh5GSGeographicalInformation(shGeographicalInformation5GS);
                                                        }
                                                        if (amfAddress != null) {
                                                            shUdaAvpValues.setAmfAddress(amfAddress);
                                                        }
                                                        if (smsfAddress != null) {
                                                            shUdaAvpValues.setSmsfAddress(smsfAddress);
                                                        }
                                                        if (currentLocationRetrieved != null) {
                                                            shUdaAvpValues.setSh5GSCurrentLocationInfoRetrieved(currentLocationRetrieved);
                                                        }
                                                        if (ageOfLocationInformation != null) {
                                                            shUdaAvpValues.setSh5GSAgeOfLocationInfo(Integer.valueOf(ageOfLocationInformation));
                                                        }
                                                        if (visitedPlmnId != null) {
                                                            ShVisitedPLMNId shVisitedPLMNId = new ShVisitedPLMNId();
                                                            shVisitedPLMNId.setVisitedPlmnIdStr(visitedPlmnId);
                                                            shUdaAvpValues.setSh5gsVisitedPLMNId(shVisitedPLMNId);
                                                            if (gmlcCdrState.isInitialized()) {
                                                                gmlcCdrState.setVisitedPlmnId(shUdaAvpValues.getSh5gsVisitedPLMNId().getVisitedPlmnId());
                                                            }
                                                        }
                                                        if (localTimeZone != null) {
                                                            shUdaAvpValues.setSh5gsLocalTimeZone(localTimeZone);
                                                            if (gmlcCdrState.isInitialized()) {
                                                                gmlcCdrState.setLocalTimeZone(localTimeZone);
                                                            }
                                                        }
                                                        if (ratType != null) {
                                                            shUdaAvpValues.setSh5gsRatType(Integer.valueOf(ratType));
                                                        }
                                                        if (this.logger.isFineEnabled()) {
                                                            this.logger.fine("\n5GSLocationInformation:");
                                                            if (shUdaAvpValues.getShNRCellGlobalId() != null)
                                                                this.logger.fine("\nNRCellGlobalId: " + shUdaAvpValues.getShNRCellGlobalId().toString());
                                                            if (shUdaAvpValues.getEutrancgi() != null)
                                                                this.logger.fine("\nE-UTRANCellGlobalId: " + shUdaAvpValues.getEutrancgi().toString());
                                                            if (shUdaAvpValues.getTrackingAreaId() != null)
                                                                this.logger.fine("\nTracking Area Id: " + shUdaAvpValues.getTrackingAreaId().toString());
                                                            if (shUdaAvpValues.getEpsGeographicalInformation() != null)
                                                                this.logger.fine("\nGeographicalInformation: " + shUdaAvpValues.getSh5GSGeographicalInformation().toString());
                                                            this.logger.fine("\nAMF address = " + shUdaAvpValues.getAmfAddress());
                                                            this.logger.fine("\nSMSF address = " + shUdaAvpValues.getSmsfAddress());
                                                            this.logger.fine("\nCurrentLocationRetrieved = " + shUdaAvpValues.getSh5GSCurrentLocationInfoRetrieved());
                                                            this.logger.fine("\nAgeOfLocationInformation = " + shUdaAvpValues.getSh5GSAgeOfLocationInfo());
                                                            if (visitedPlmnId != null) {
                                                                this.logger.fine("\nVisitedPLMNID = " + shUdaAvpValues.getSh5gsVisitedPLMNId().toString());
                                                            }
                                                            if (localTimeZone != null) {
                                                                this.logger.fine("\nTime Zone = " + shUdaAvpValues.getSh5gsLocalTimeZone());
                                                            }
                                                            if (ratType != null) {
                                                                this.logger.fine("\nRAT Type = " + shUdaAvpValues.getSh5gsRatType());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // Handle successful retrieval of response to subscriber's location request (SLg ELP Provide-Location-Answer)
                        this.handleShUserDataRequestResponse(mlpRespResult, shUdaAvpValues, mlpClientErrorMessage);
                        detachFromShClientActivity(aci);

                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setStatusCode(2001L);
                            ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, publicIdsMsisdn);
                            gmlcCdrState.setMsisdn(msisdn);
                            LocationInformation locationInformation;
                            LocationInformationGPRS locationInformationGPRS;
                            LocationInformationEPS locationInformationEPS;
                            LocationInformation5GS locationInformation5GS;
                            if (shDataReader.getShCSLocationInfo() != null) {
                                locationInformation = AVPHandler.shLocationInfo2MapLocationInformation(shUdaAvpValues);
                                gmlcCdrState.setLocationInformation(locationInformation);
                                locationInformationEPS = locationInformation.getLocationInformationEPS();
                                gmlcCdrState.setLocationInformationEPS(locationInformationEPS);
                            }
                            if (shDataReader.getShPSLocationInfo() != null) {
                                locationInformationGPRS = AVPHandler.shPSLocationInfo2MapLocationInformationGPRS(shUdaAvpValues);
                                gmlcCdrState.setLocationInformationGPRS(locationInformationGPRS);
                            }
                            if (epsLocationInformation != null) {
                                locationInformation = AVPHandler.shLocationInfo2MapLocationInformation(shUdaAvpValues);
                                locationInformationEPS = locationInformation.getLocationInformationEPS();
                                gmlcCdrState.setLocationInformationEPS(locationInformationEPS);
                            }
                            if (sh5GSLocationInformation != null) {
                                locationInformation5GS = AVPHandler.sh5GSLocationInfo2LocationInformation5GS(shUdaAvpValues);
                                gmlcCdrState.setLocationInformation5GS(locationInformation5GS);
                            }
                            this.createCDRRecord(RecordStatus.IMS_UDR_SUCCESS);
                        }

                    } else {
                        ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN,
                            shUdrMsisdn);
                        if (gmlcCdrState.isInitialized()) {
                            gmlcCdrState.setMsisdn(msisdn);
                            handleRecordAndLocationReportOnDiameterResultCode(resultCode, mlpRespResult, mlpClientErrorMessage, shUdrMsisdn, null, "UDR", null, gmlcCdrState, true,
                                userDataAnswer.getOriginHost().toString(), userDataAnswer.getOriginRealm().toString(), false);
                        }
                    }

                } else {
                    ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN,
                        shUdrMsisdn);
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setMsisdn(msisdn);
                        handleRecordAndLocationReportOnDiameterResultCode(resultCode, mlpRespResult, mlpClientErrorMessage, shUdrMsisdn, null, "UDR", null, gmlcCdrState, true,
                            userDataAnswer.getOriginHost().toString(), userDataAnswer.getOriginRealm().toString(), false);
                    }
                }

            } catch (Throwable e) {
                logger.warning("Exception when processing onUserDataAnswer response: " + e.getMessage(), e);
                this.createCDRRecord(RecordStatus.IMS_UDR_SYSTEM_FAILURE);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on Sh UDA: " + e.getMessage(),
                    "UDR", shUdrMsisdn, null, null, null, null, null,
                    userDataAnswer.getOriginHost().toString(),
                    userDataAnswer.getOriginRealm().toString(),
                    false);
                if (transaction != null)
                    mobileCoreNetworkTransactions.destroy(transaction);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onUserDataAnswer=%s", userDataAnswer), e);
            this.createCDRRecord(RecordStatus.IMS_UDR_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on Sh UDA: " + e.getMessage(),
                "UDR", shUdrMsisdn, null, null, null, null, null,
                userDataAnswer != null ? userDataAnswer.getOriginHost().toString() : null,
                userDataAnswer != null ? userDataAnswer.getOriginRealm().toString() : null,
                false);
            if (transaction != null)
                mobileCoreNetworkTransactions.destroy(transaction);
        } finally {
            detachFromShClientActivity(aci);
        }
    }

    /*
     * Diameter Base Error-Answer Event
     */
    public void onErrorAnswer(ErrorAnswer errorAnswer, ActivityContextInterface aci) {

        String msisdnAddress = null, imsiStr = null, imei = null, diameterCommand = null, mlpClientErrorMessage = "", curlUser;
        Long transaction = null;
        Integer referenceNumber = null;
        boolean isImsi = false, mlpTriggeredReportingService = false;
        long resultCode = -1;
        DateTime eventTime = DateTime.now();
        if (errorAnswer != null) {
            resultCode = errorAnswer.getResultCode();
            mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(resultCode);
        }

        if (errorAnswer != null) {
            logger.fine("\nReceived Diameter error answer with session Id: " + errorAnswer.getSessionId() + ", host='"
                + errorAnswer.getOriginHost() + "', realm='" + errorAnswer.getOriginRealm() + "'" +
                    ", 'result code='" + errorAnswer.getResultCode());
        }

        try {

            try {
                DiameterIdentity gmlcHost = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginHost());
                DiameterIdentity gmlcRealm = new DiameterIdentity(gmlcPropertiesManagement.getDiameterOriginRealm());

                // CDR initialization
                GMLCCDRState gmlcCdrState = CDRCreationHelper.diameterErrorCdrInitializer(aci, this.getCDRInterface(), errorAnswer,
                    gmlcHost, gmlcRealm);
                // Set timer last
                this.setTimer(aci);

                if (errorAnswer != null) {

                    // Transaction
                    transaction = mobileCoreNetworkTransactions.getTransactionId(errorAnswer.getSessionId());
                    if (transaction == null) {
                        throw new Exception();
                    }
                    TimerID diameterOperationTimerID = (TimerID) mobileCoreNetworkTransactions.getValue(transaction, "timerID");
                    if (diameterOperationTimerID != null)
                        this.timerFacility.cancelTimer(diameterOperationTimerID);
                    msisdnAddress = (String) mobileCoreNetworkTransactions.getValue(transaction, "shUdrMsisdn");
                    curlUser = (String) mobileCoreNetworkTransactions.getValue(transaction, "curlUser");
                    DateTime dialogStartTime = (DateTime) mobileCoreNetworkTransactions.getValue(transaction, "transactionStart");
                    mlpTriggeredReportingService = (Boolean) mobileCoreNetworkTransactions.getValue(transaction, "mlpTriggeredReportingService");
                    gmlcCdrState.setDialogStartTime(dialogStartTime);
                    if (gmlcCdrState.isInitialized()) {
                        gmlcCdrState.setCurlUser(curlUser);
                        if (dialogStartTime != null) {
                            Long dialogDuration = eventTime.getMillis() - dialogStartTime.getMillis();
                            gmlcCdrState.setDialogDuration(dialogDuration);
                        }
                    }
                    if (msisdnAddress != null) {
                        diameterCommand = "UDR";
                    } else {
                        RequestValuesForPLR requestValuesForPLR = (RequestValuesForPLR) mobileCoreNetworkTransactions.getValue(transaction, "requestValuesForPLR");
                        if (requestValuesForPLR != null) {
                            if (requestValuesForPLR.plrMsisdn != null)
                                msisdnAddress = AVPHandler.byte2IsdnAddressString(requestValuesForPLR.plrMsisdn.getBytes()).getAddress();
                            if (requestValuesForPLR.plrUserName != null) {
                                imsiStr = requestValuesForPLR.plrUserName;
                                isImsi = true;
                            }
                            if (msisdnAddress != null || imsiStr != null) {
                                diameterCommand = "RIR";
                            }
                            if (requestValuesForPLR.plrLcsReferenceNumber != null)
                                referenceNumber = requestValuesForPLR.plrLcsReferenceNumber;
                        }
                        SLhRiaAvpValues sLhRiaAvpResponseValues = (SLhRiaAvpValues) mobileCoreNetworkTransactions.getValue(transaction, "sLhRiaAvpResponseValues");
                        referenceNumber = requestValuesForPLR.plrLcsReferenceNumber;
                        if (requestValuesForPLR.plrUserName != null || requestValuesForPLR.plrMsisdn != null) {
                            if (requestValuesForPLR.plrUserName != null) {
                                imsiStr = requestValuesForPLR.plrUserName;
                            }
                            if (requestValuesForPLR.plrMsisdn != null) {
                                msisdnAddress = AVPHandler.byte2IsdnAddressString(requestValuesForPLR.plrMsisdn.getBytes()).getAddress();
                            }
                        } else if (sLhRiaAvpResponseValues != null) {
                            if (sLhRiaAvpResponseValues.getUserName() != null) {
                                imsiStr = sLhRiaAvpResponseValues.getUserName();
                                diameterCommand = "PLR";
                            }
                            if (sLhRiaAvpResponseValues.getMsisdn() != null) {
                                msisdnAddress = AVPHandler.byte2IsdnAddressString(sLhRiaAvpResponseValues.getMsisdn()).getAddress();
                                diameterCommand = "PLR";
                            }
                        }
                    }

                    if (gmlcCdrState.isInitialized()) {
                        if (isImsi) {
                            IMSI imsi = new IMSIImpl(imsiStr);
                            gmlcCdrState.setImsi(imsi);
                        } else {
                            ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number, org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN,
                                msisdnAddress);
                            gmlcCdrState.setMsisdn(msisdn);
                        }
                        handleRecordAndLocationReportOnDiameterResultCode(resultCode, MLPResponse.MLPResultType.SYSTEM_FAILURE, mlpClientErrorMessage,
                            msisdnAddress, imsiStr, diameterCommand, referenceNumber, gmlcCdrState, true,
                            errorAnswer.getOriginHost() != null ? errorAnswer.getOriginHost().toString() : null,
                            errorAnswer.getOriginRealm() != null ? errorAnswer.getOriginRealm().toString() : null,
                            mlpTriggeredReportingService);
                    }
                }
            } catch (Throwable e) {
                logger.warning("Exception when processing Diameter Error Answer: " + e.getMessage(), e);
                this.createCDRRecord(RecordStatus.DIAMETER_SYSTEM_FAILURE);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, mlpClientErrorMessage,
                    diameterCommand, msisdnAddress, imsiStr, imei, referenceNumber, null, null,
                    errorAnswer.getOriginHost() != null ? errorAnswer.getOriginHost().toString() : null,
                    errorAnswer.getOriginRealm() != null ? errorAnswer.getOriginRealm().toString() : null,
                    mlpTriggeredReportingService);
            }
        } catch (Exception e) {
            logger.severe(String.format("Error while trying to process onErrorAnswer=%s", errorAnswer), e);
            this.createCDRRecord(RecordStatus.DIAMETER_SYSTEM_FAILURE);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception on Diameter ErrorAnswer: " + e.getMessage(),
                diameterCommand, msisdnAddress, imsiStr, imei, referenceNumber, null, null,
                errorAnswer != null ? errorAnswer.getOriginHost().toString() : null,
                errorAnswer != null ? errorAnswer.getOriginRealm().toString() : null,
                mlpTriggeredReportingService);
        } finally {
            mobileCoreNetworkTransactions.destroy(transaction);
            aci.detach(this.sbbContext.getSbbLocalObject());
        }
    }

    /*
     * Diameter error handling for CDR and HTTP response
     */
    public Map<MLPResponse.MLPResultType, String> handleRecordAndLocationReportOnDiameterResultCode(long resultCode, MLPResponse.MLPResultType mlpRespResult, String mlpClientErrorMessage,
                                                                                                    String msisdn, String imsi, String diameterCommand, Integer refNumber,
                                                                                                    GMLCCDRState gmlcCdrState, Boolean reportError, String host, String realm, Boolean mlpTriggeredReportingService) {

        Map<MLPResponse.MLPResultType, String> mlpResultTypeStringMap = new LinkedHashMap<>();
        if (gmlcCdrState != null) {
            if (gmlcCdrState.isInitialized()) {
                gmlcCdrState.setStatusCode(resultCode);
                switch ((int) resultCode) {
                    case 3001:
                        mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3001);
                        this.createCDRRecord(RecordStatus.DIAMETER_COMMAND_UNSUPPORTED);
                        break;
                    case 3002:
                        mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3002);
                        this.createCDRRecord(RecordStatus.DIAMETER_UNABLE_TO_DELIVER);
                        break;
                    case 3003:
                        mlpRespResult = MLPResponse.MLPResultType.MISCONFIGURATION_OF_LOCATION_SERVER;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3003);
                        this.createCDRRecord(RecordStatus.DIAMETER_REALM_NOT_SERVED);
                        break;
                    case 3004:
                        mlpRespResult = MLPResponse.MLPResultType.CONGESTION_IN_MOBILE_NETWORK;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3004);
                        this.createCDRRecord(RecordStatus.DIAMETER_TOO_BUSY);
                        break;
                    case 3005:
                        mlpRespResult = MLPResponse.MLPResultType.MISCONFIGURATION_OF_LOCATION_SERVER;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3005);
                        this.createCDRRecord(RecordStatus.DIAMETER_LOOP_DETECTED);
                        break;
                    case 3006:
                        mlpRespResult = MLPResponse.MLPResultType.MISCONFIGURATION_OF_LOCATION_SERVER;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3006);
                        this.createCDRRecord(RecordStatus.DIAMETER_REDIRECT_INDICATION);
                        break;
                    case 3007:
                        mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3007);
                        this.createCDRRecord(RecordStatus.DIAMETER_APPLICATION_UNSUPPORTED);
                        break;
                    case 3008:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3008);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_HDR_BITS);
                        break;
                    case 3009:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3009);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_AVP_BITS);
                        break;
                    case 3010:
                        mlpRespResult = MLPResponse.MLPResultType.MISCONFIGURATION_OF_LOCATION_SERVER;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(3010);
                        this.createCDRRecord(RecordStatus.DIAMETER_UNKNOWN_PEER);
                        break;
                    case 4001:
                        mlpRespResult = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(4001);
                        this.createCDRRecord(RecordStatus.DIAMETER_AUTHENTICATION_REJECTED);
                        break;
                    case 4002:
                        mlpRespResult = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(4002);
                        this.createCDRRecord(RecordStatus.DIAMETER_OUT_OF_SPACE);
                        break;
                    case 4003:
                        mlpRespResult = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(4003);
                        this.createCDRRecord(RecordStatus.DIAMETER_ELECTION_LOST);
                        break;
                    case 4100:
                        mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(4100);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_USER_DATA_NOT_AVAILABLE);
                        break;
                    case 4101:
                        mlpRespResult = MLPResponse.MLPResultType.UNSPECIFIED_ERROR;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(4101);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_PRIOR_UPDATE_IN_PROGRESS);
                        break;
                    case 4201:
                        mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                        mlpClientErrorMessage = SLhSpecificErrors.diameterErrorMessage(4201);
                        this.createCDRRecord(RecordStatus.LTE_RIR_DIAMETER_ERROR_ABSENT_USER);
                        break;
                    case 4221:
                        mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                        mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(4221);
                        this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_UNREACHABLE_USER);
                        break;
                    case 4222:
                        mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                        mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(4222);
                        this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_SUSPENDED_USER);
                        break;
                    case 4223:
                        mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                        mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(4223);
                        this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_DETACHED_USER);
                        break;
                    case 4224:
                        mlpRespResult = MLPResponse.MLPResultType.POSITIONING_NOT_ALLOWED;
                        mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(4224);
                        this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_POSITIONING_DENIED);
                        break;
                    case 4225:
                        mlpRespResult = MLPResponse.MLPResultType.POSITION_METHOD_FAILURE;
                        mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(4225);
                        this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_POSITIONING_FAILED);
                        break;
                    case 4226:
                        mlpRespResult = MLPResponse.MLPResultType.MLS_CLIENT_ERROR;
                        mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(4226);
                        this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_UNKNOWN_UNREACHABLE_LCS_CLIENT);
                        break;
                    case 5001:
                        if (diameterCommand.equalsIgnoreCase("RIR")) {
                            mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                            mlpClientErrorMessage = SLhSpecificErrors.diameterErrorMessage(5001);
                            this.createCDRRecord(RecordStatus.LTE_RIR_DIAMETER_ERROR_USER_UNKNOWN);
                        } else if (diameterCommand.equalsIgnoreCase("PLR")) {
                            mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                            mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(5001);
                            this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_USER_UNKNOWN);
                        } else {
                            mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_VALUE_NOT_SUPPORTED;
                            mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5001);
                            this.createCDRRecord(RecordStatus.DIAMETER_AVP_UNSUPPORTED);
                        }
                        break;
                    case 5002:
                        mlpRespResult = MLPResponse.MLPResultType.FORMAT_ERROR;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5002);
                        this.createCDRRecord(RecordStatus.DIAMETER_UNKNOWN_SESSION_ID);
                        break;
                    case 5003:
                        mlpRespResult = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5003);
                        this.createCDRRecord(RecordStatus.DIAMETER_AUTHORIZATION_REJECTED);
                        break;
                    case 5004:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5004);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_AVP_VALUE);
                        break;
                    case 5005:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5005);
                        this.createCDRRecord(RecordStatus.DIAMETER_MISSING_AVP);
                        break;
                    case 5006:
                        mlpRespResult = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5006);
                        this.createCDRRecord(RecordStatus.DIAMETER_RESOURCES_EXCEEDED);
                        break;
                    case 5007:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5007);
                        this.createCDRRecord(RecordStatus.DIAMETER_CONTRADICTING_AVPS);
                        break;
                    case 5008:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5008);
                        this.createCDRRecord(RecordStatus.DIAMETER_AVP_NOT_ALLOWED);
                        break;
                    case 5009:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5009);
                        this.createCDRRecord(RecordStatus.DIAMETER_AVP_OCCURS_TOO_MANY_TIMES);
                        break;
                    case 5010:
                        mlpRespResult = MLPResponse.MLPResultType.INVALID_PROTOCOL_ELEMENT_VALUE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5010);
                        this.createCDRRecord(RecordStatus.DIAMETER_NO_COMMON_APPLICATION);
                        break;
                    case 5011:
                        mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5011);
                        this.createCDRRecord(RecordStatus.DIAMETER_UNSUPPORTED_VERSION);
                        break;
                    case 5012:
                        mlpRespResult = MLPResponse.MLPResultType.SERVICE_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5012);
                        this.createCDRRecord(RecordStatus.DIAMETER_UNABLE_TO_COMPLY);
                        break;
                    case 5013:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5013);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_BIT_IN_HEADER);
                        break;
                    case 5014:
                        mlpRespResult = MLPResponse.MLPResultType.INVALID_PROTOCOL_ELEMENT_ATTRIBUTE_VALUE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5014);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_AVP_LENGTH);
                        break;
                    case 5015:
                        mlpRespResult = MLPResponse.MLPResultType.INVALID_PROTOCOL_ELEMENT_VALUE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5015);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_MESSAGE_LENGTH);
                        break;
                    case 5016:
                        mlpRespResult = MLPResponse.MLPResultType.INVALID_PROTOCOL_ELEMENT_ATTRIBUTE_VALUE;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5016);
                        this.createCDRRecord(RecordStatus.DIAMETER_INVALID_AVP_BIT_COMBO);
                        break;
                    case 5017:
                        mlpRespResult = MLPResponse.MLPResultType.PROTOCOL_ELEMENT_NOT_SUPPORTED;
                        mlpClientErrorMessage = DiameterBaseError.diameterErrorMessage(5017);
                        this.createCDRRecord(RecordStatus.DIAMETER_NO_COMMON_SECURITY);
                        break;
                    case 5100:
                        mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5100);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_USER_DATA_NOT_RECOGNIZED);
                        break;
                    case 5101:
                        mlpRespResult = MLPResponse.MLPResultType.POSITIONING_NOT_ALLOWED;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5101);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_OPERATION_NOT_ALLOWED);
                        break;
                    case 5102:
                        mlpRespResult = MLPResponse.MLPResultType.FORMAT_ERROR;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5102);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_USER_DATA_CANNOT_BE_READ);
                        break;
                    case 5103:
                        mlpRespResult = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5103);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_USER_DATA_CANNOT_BE_MODIFIED);
                        break;
                    case 5104:
                        mlpRespResult = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5104);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_USER_DATA_CANNOT_BE_NOTIFIED);
                        break;
                    case 5105:
                        mlpRespResult = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5105);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_TRANSPARENT_DATA_OUT_OF_SYNC);
                        break;
                    case 5106:
                        mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5106);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_SUBS_DATA_ABSENT);
                        break;
                    case 5107:
                        mlpRespResult = MLPResponse.MLPResultType.UNKNOWN_SUBSCRIBER;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5107);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_NO_SUBSCRIPTION_TO_DATA);
                        break;
                    case 5108:
                        mlpRespResult = MLPResponse.MLPResultType.FORMAT_ERROR;
                        mlpClientErrorMessage = ShSpecificErrors.diameterErrorMessage(5108);
                        this.createCDRRecord(RecordStatus.IMS_UDR_DIAMETER_ERROR_DSAI_NOT_AVAILABLE);
                        break;
                    case 5490:
                        mlpRespResult = MLPResponse.MLPResultType.UNAUTHORIZED_APPLICATION;
                        if (diameterCommand.equalsIgnoreCase("RIR")) {
                            this.createCDRRecord(RecordStatus.LTE_RIR_DIAMETER_ERROR_UNAUTHORIZED_REQUESTING_NETWORK);
                            mlpClientErrorMessage = SLhSpecificErrors.diameterErrorMessage(5490);
                        } else if (diameterCommand.equalsIgnoreCase("PLR")) {
                            this.createCDRRecord(RecordStatus.LTE_PLR_DIAMETER_ERROR_UNAUTHORIZED_REQUESTING_NETWORK);
                            mlpClientErrorMessage = SLgSpecificErrors.diameterErrorMessage(5490);
                        }
                        break;
                    case 2001:
                        if (diameterCommand.equalsIgnoreCase("UDR")) {
                            // Special Case: Diameter SUCCESS in Sh UDA but Sh-User-Data AVP is absent
                            mlpRespResult = MLPResponse.MLPResultType.ABSENT_SUBSCRIBER;
                            mlpClientErrorMessage = "DIAMETER SUCCESS (2001) for UDR to " + msisdn + " but Sh-User-Data AVP is absent in UDA";
                            this.createCDRRecord(RecordStatus.IMS_UDA_Sh_USER_DATA_ABSENT);
                        }
                        break;
                    default:
                        mlpRespResult = MLPResponse.MLPResultType.SYSTEM_FAILURE;
                        mlpClientErrorMessage = "Diameter error code: " + resultCode;
                        if (diameterCommand.equalsIgnoreCase("RIR"))
                            this.createCDRRecord(RecordStatus.LTE_RIR_SYSTEM_FAILURE);
                        else if (diameterCommand.equalsIgnoreCase("PLR"))
                            this.createCDRRecord(RecordStatus.LTE_PLR_SYSTEM_FAILURE);
                        else if (diameterCommand.equalsIgnoreCase("UDR"))
                            this.createCDRRecord(RecordStatus.IMS_UDR_SYSTEM_FAILURE);
                        break;
                }
            }
        }
        mlpResultTypeStringMap.put(mlpRespResult, mlpClientErrorMessage);
        if (reportError) {
            this.reportLocationRequestError(mlpRespResult, mlpClientErrorMessage, diameterCommand, msisdn, imsi, null, refNumber,
                null, null, host, realm, mlpTriggeredReportingService);
        }
        return mlpResultTypeStringMap;
    }

    /**
     * SUPL POS INIT Event
     */
    /* public void onSuplInit(SuplInit suplInit, ActivityContextInterface aci){
        logger.info("[TEST] SuplInit");
         this.suplEventProcessing.onSuplInit(suplInit, aci);
    }
    public void onSuplPosInit(com.paicbd.slee.resource.ulp.events.SuplPosInit suplPosInit, ActivityContextInterface aci){
        logger.info("[TEST] SuplPosInit" + suplPosInit+", ac=" + aci);
        // this.suplEventProcessing.onSuplPosInit(suplPosInit,aci);
    }
    public void onSuplPos(SuplPos suplPos, ActivityContextInterface aci){
        logger.info("[TEST] SuplPos" + suplPos+", ac=" + aci);
        this.suplEventProcessing.onSuplPos(suplPos, aci);
    }
    public void onSuplEnd(SuplEnd suplEnd, ActivityContextInterface aci){
        logger.info("[TEST] SuplPos" + suplEnd+", ac=" + aci);
        this.suplEventProcessing.onSuplEnd(suplEnd, aci);
    }
    public void onSuplTriggeredStart(SuplTriggeredStart suplTriggeredStart, ActivityContextInterface aci){
        logger.info("[TEST] SuplPos" + suplTriggeredStart+", ac=" + aci);
        this.suplEventProcessing.onSuplTriggeredStart(suplTriggeredStart, aci);
    }
    public void onSuplTriggeredResponse(SuplTriggeredResponse suplTriggeredResponse, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplTriggeredResponse(suplTriggeredResponse, aci);
    }
    public void onSuplTriggeredStop(SuplTriggeredStop suplTriggeredStop, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplTriggeredStop(suplTriggeredStop, aci);
    }
    public void onSuplReport(SuplReport suplReport, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplReport(suplReport, aci);
    }
    public void onSuplStart(SuplStart suplStart, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplStart(suplStart, aci);
    }
    public void onSuplAuthReq(SuplAuthReq suplAuthReq, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplAuthReq(suplAuthReq, aci);
    }
    public void onSuplAuthResp(SuplAuthResp suplAuthResp, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplAuthResp(suplAuthResp, aci);
    }
    public void onSuplNotify(SuplNotify suplNotify, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplNotify(suplNotify, aci);
    }
    public void onSuplNotifyResponse(SuplNotifyResponse suplNotifyResponse,ActivityContextInterface aci){
        this.suplEventProcessing.onSuplNotifyResponse(suplNotifyResponse, aci);
    }
    public void onSuplResponse(SuplResponse suplResponse, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplResponse(suplResponse,aci);
    }
    public void onSuplSetInit(SuplSetInit suplSetInit, ActivityContextInterface aci){
        this.suplEventProcessing.onSuplSetInit(suplSetInit, aci);
    }*/

    /**
     * Handle HTTP POST request
     *
     * @param event        net.java.slee.resource.http.events.HttpServletRequestEvent
     * @param aci          javax.slee.ActivityContextInterface)
     * @param eventContext javax.slee.EventContext
     */
    public void onPost(HttpServletRequestEvent event, ActivityContextInterface aci, EventContext eventContext) {
        onRequest(event, aci, eventContext);
    }

    /**
     * Handle HTTP GET request
     *
     * @param event        net.java.slee.resource.http.events.HttpServletRequestEvent
     * @param aci          javax.slee.ActivityContextInterface)
     * @param eventContext javax.slee.EventContext
     */
    public void onGet(HttpServletRequestEvent event, ActivityContextInterface aci, EventContext eventContext) {
        onRequest(event, aci, eventContext);
    }

    /**
     * Entry point for all location lookups
     * Assigns a protocol handler to the request based on the path
     */
    private void onRequest(HttpServletRequestEvent event, ActivityContextInterface aci, EventContext eventContext) throws IllegalArgumentException {
        setEventContext(eventContext);
        HttpServletRequest httpServletRequest = event.getRequest();
        HttpRequestType httpRequestType = HttpRequestType.fromPath(httpServletRequest.getPathInfo());
        String operation = null, inputIllegalArgument;

        LocationRequestParams locationRequestParams = new LocationRequestParams();
        locationRequestParams.targetingMSISDN = locationRequestParams.targetingIMSI = locationRequestParams.httpRespType = "";
        locationRequestParams.translationType = 0;

        HttpServletRequestParams httpServletRequestParams = new HttpServletRequestParams();

        switch (httpRequestType) {
            case REST: {
                try {

                    locationRequestParams.targetingMSISDN = httpServletRequest.getParameter("msisdn");

                    locationRequestParams.targetingIMSI = httpServletRequest.getParameter("imsi");

                    locationRequestParams = httpServletRequestParams.createGlobalLocationRequestParamsFromHttpRequest(httpServletRequest, locationRequestParams);
                    if (locationRequestParams.numberFormatException != null) {
                        handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, operation, locationRequestParams.numberFormatException);
                        return;
                    }

                    /*********************************************************************************************************************************************************************/
                    /** SS7 operations arguments for ATI and PSI ************************************************************************************************************************/
                    /*******************************************************************************************************************************************************************/

                    locationRequestParams = httpServletRequestParams.createLocationRequestParamsForMapAtiOrPsi(httpServletRequest, locationRequestParams);
                    if (locationRequestParams.numberFormatException != null) {
                        handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, operation, locationRequestParams.numberFormatException);
                        return;
                    }

                    /*********************************************************************************************************************************************************************/
                    /** SS7 LSM operations (SRILCS-PSL) arguments ***********************************************************************************************************************/
                    /*******************************************************************************************************************************************************************/
                    if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {

                        operation = "SRILCS"; // operation for error report on illegal/incorrect arguments

                        locationRequestParams = httpServletRequestParams.createLocationRequestParamsForMapPsl(httpServletRequest, locationRequestParams);
                        if (locationRequestParams.numberFormatException != null) {
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, operation, locationRequestParams.numberFormatException);
                            return;
                        }
                    }

                    /*********************************************************************************************************************************************************************/
                    /** LTE SLh & SLg operations arguments *******************************************************************************************************************************/
                    /*********************************************************************************************************************************************************************/
                    if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {

                        operation = "RIR"; // operation for error report on illegal/incorrect arguments

                        locationRequestParams = httpServletRequestParams.createLocationRequestParamsForDiameterPLR(httpServletRequest, locationRequestParams);
                        if (locationRequestParams.numberFormatException != null) {
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, operation, locationRequestParams.numberFormatException);
                            return;
                        }
                    }

                    /** LTE/IMS Sh User-Data-Request operation arguments **/
                    // Already defined domain and locationInfoEps also apply //
                    if (locationRequestParams.operation.equalsIgnoreCase("UDR")) {
                        locationRequestParams.udrMsisdn = locationRequestParams.targetingMSISDN = httpServletRequest.getParameter("msisdn");
                        locationRequestParams.udrImsPublicId = httpServletRequest.getParameter("imsPublicId");
                        if (locationRequestParams.udrMsisdn == null && locationRequestParams.udrImsPublicId == null) {
                            inputIllegalArgument = "One of MSISDN or Public Identity AVP are mandatory in UDR, at least one of msisdn or imsPublicId parameters must be not null";
                            throw new IllegalArgumentException(inputIllegalArgument);
                        }
                        locationRequestParams.hssDiameterHost = httpServletRequest.getParameter("hssDiameterHost");
                        locationRequestParams.hssDiameterRealm = httpServletRequest.getParameter("hssDiameterRealm");
                    }

                    /*********************************************************************************************************************************************************************/
                    /** SUPL arguments *******************************************************************************************************************************/
                    /*********************************************************************************************************************************************************************/
                    if (locationRequestParams.operation.equalsIgnoreCase("SUPL")) {

                        operation = "SUPL"; // operation for error report on illegal/incorrect arguments

                        // TODO
                        /*locationRequestParams = httpServletRequestParams.createLocationRequestParamsForSUPL(httpServletRequest, locationRequestParams);
                        if (locationRequestParams.numberFormatException != null) {
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, operation, locationRequestParams.numberFormatException);
                            return;
                        }*/
                    }

                    setHttpRequest(httpRequestType, locationRequestParams);

                } catch (IllegalArgumentException iae) {
                    if (locationRequestParams.operation != null) {
                        if (locationRequestParams.operation.equalsIgnoreCase("ATI"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "ATI", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("PSI"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "PSI", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("SRI"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "SRI", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("SRISM"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "SRISM", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("PSL"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "SRILCS", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("PLR"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "RIR", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "UDR", iae.getMessage());
                        else if (locationRequestParams.operation.equalsIgnoreCase("SUPL"))
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, "SUPL", iae.getMessage());
                        else {
                            handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, locationRequestParams.operation, iae.getMessage());
                            return;
                        }
                        return;
                    } else {
                        handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, null, iae.getMessage());
                        return;
                    }
                }
            }
            break;

            case MLP:
                try {
                    // Get the XML request from the POST data
                    InputStream body = httpServletRequest.getInputStream();
                    // Parse the request and retrieve its parameters (MSISDN, operation, token, etc.)
                    MLPRequest mlpRequest = new MLPRequest(logger);

                    MLPLocationRequest mlpLocationRequest = mlpRequest.parse(body);
                    locationRequestParams = httpServletRequestParams.createLocationRequestParamsFromMLP(locationRequestParams, mlpLocationRequest);
                    if (locationRequestParams.numberFormatException != null) {
                        handleExceptionOnRequest(httpRequestType, locationRequestParams, eventContext, operation, locationRequestParams.numberFormatException);
                        return;
                    }

                    locationRequestParams.httpRespType = "MLP";
                    setHttpRequest(httpRequestType, locationRequestParams);
                } catch (MLPException mlpException) {
                    logger.severe(mlpException.getMessage());
                    setHttpRequest(httpRequestType, locationRequestParams);
                    eventContext.suspendDelivery();
                    setEventContextCMP(eventContext);
                    Boolean mlpTriggeredReportingService = false;
                    MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
                    if (reportingService == MLPLocationRequest.ReportingService.Triggered)
                        mlpTriggeredReportingService = true;
                    if (locationRequestParams.operation != null) {
                        if (locationRequestParams.operation.equalsIgnoreCase("PSI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "PSI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        if (locationRequestParams.operation.equalsIgnoreCase("SRI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "SRI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        if (locationRequestParams.operation.equalsIgnoreCase("SRISM"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "SRISM", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("PSL"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "PSL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("SRILCS"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "SRILCS", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("PLR"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "RIR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.plrLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN or IMSPublicIdentity) is null", "UDR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("ATI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "ATI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("SUPL"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", "SUPL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "MLP Exception", null, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        return;
                    } else {
                        reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "operation request parameter is null", null, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        return;
                    }

                } catch (IOException e) {
                    logger.severe(e.getMessage());
                    setHttpRequest(httpRequestType, locationRequestParams);
                    eventContext.suspendDelivery();
                    setEventContextCMP(eventContext);
                    Boolean mlpTriggeredReportingService = false;
                    MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
                    if (reportingService == MLPLocationRequest.ReportingService.Triggered)
                        mlpTriggeredReportingService = true;
                    if (locationRequestParams.operation != null) {
                        if (locationRequestParams.operation.equalsIgnoreCase("PSI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "PSI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        if (locationRequestParams.operation.equalsIgnoreCase("SRI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "SRI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        if (locationRequestParams.operation.equalsIgnoreCase("SRISM"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "SRISM", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("PSL"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "PSL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("SRILCS"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "SRILCS", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("PLR"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "RIR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.plrLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "UDR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("ATI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "ATI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("SUPL"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", "SUPL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Internal IO or parsing error occurred", null, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        return;
                    } else {
                        reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "operation request parameter is null", null, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        return;
                    }
                } catch (IllegalArgumentException iae) {
                    setHttpRequest(httpRequestType, locationRequestParams);
                    eventContext.suspendDelivery();
                    setEventContextCMP(eventContext);
                    Boolean mlpTriggeredReportingService = false;
                    MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
                    if (reportingService == MLPLocationRequest.ReportingService.Triggered)
                        mlpTriggeredReportingService = true;
                    if (locationRequestParams.operation != null) {
                        if (locationRequestParams.operation.equalsIgnoreCase("PSI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "PSI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        if (locationRequestParams.operation.equalsIgnoreCase("SRI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "SRI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        if (locationRequestParams.operation.equalsIgnoreCase("SRISM"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "SRISM", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("PSL"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "PSL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("SRILCS"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "SRILCS", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("PLR"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "RIR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.plrLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "UDR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("ATI"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "ATI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else if (locationRequestParams.operation.equalsIgnoreCase("SUPL"))
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), "SUPL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        else
                            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, iae.getMessage(), null, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        return;
                    } else {
                        reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "operation request parameter is null", null, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
                        return;
                    }
                }
                break;
            default:
                sendHTTPResult(HttpServletResponse.SC_NOT_FOUND, "Request URI unsupported");
                return;
        }

        if (logger.isFineEnabled()) {
            logger.fine(String.format("Handling %s request, MSISDN: %s via %s operation", httpRequestType.name().toUpperCase(), locationRequestParams.targetingMSISDN,
                locationRequestParams.operation));
        }

        if (locationRequestParams.targetingMSISDN != null || locationRequestParams.targetingIMSI != null) {
            eventContext.suspendDelivery(gmlcPropertiesManagement.getEventContextSuspendDeliveryTimeout());
            setEventContextCMP(eventContext);
            if (locationRequestParams.operation != null) {
                if (locationRequestParams.operation.equalsIgnoreCase("PSI")) {
                    if (!locationRequestParams.psiServiceType.equalsIgnoreCase("psiFirst")) {
                        if (locationRequestParams.psiServiceType.equalsIgnoreCase("useSri"))
                            getLocationViaSubscriberInformationCallHandling(locationRequestParams, false, false);
                        else if (locationRequestParams.psiServiceType.equalsIgnoreCase("useSriSm"))
                            getLocationViaSubscriberInformation(locationRequestParams, false, false);
                    } else {
                        if (locationRequestParams.psiOnlyImsi != null && locationRequestParams.psiOnlyNnn != null) {
                            if (locationRequestParams.targetingMSISDN != null)
                                provideSubscriberInfoRequestFirst(locationRequestParams.psiOnlyImsi, locationRequestParams.psiOnlyNnn,
                                    locationRequestParams.domainType, locationRequestParams.targetingMSISDN, locationRequestParams.locationInfoEps,
                                    locationRequestParams.curlUser);
                        } else {
                            if (locationRequestParams.targetingMSISDN != null)
                                if (locationRequestParams.psiServiceType.equalsIgnoreCase("useSri"))
                                    getLocationViaSubscriberInformationCallHandling(locationRequestParams, false, false);
                                else if (locationRequestParams.psiServiceType.equalsIgnoreCase("useSriSm"))
                                    getLocationViaSubscriberInformation(locationRequestParams, false, false);
                        }
                    }
                } else {
                    if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
                        getLocationViaMapLsm(locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams, false);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("ATI")) {
                        getLocationViaMapAti(locationRequestParams);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
                        getLocationViaLTELocationServices(locationRequestParams.targetingIMSI, locationRequestParams.targetingMSISDN, locationRequestParams);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("UDR")) {
                        getLocationViaIMSSh(locationRequestParams);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("SRI")) {
                        getLocationViaSubscriberInformationCallHandling(locationRequestParams, false, true);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("SRISM")) {
                        getLocationViaSubscriberInformation(locationRequestParams, false, true);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("SRILCS")) {
                        getLocationViaMapLsm(locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams, true);
                    } else if (locationRequestParams.operation.equalsIgnoreCase("SUPL")) {
                        getLocationViaSUPL(locationRequestParams, locationRequestParams.suplTransactionId, aci);
                    }
                }
            } else {
                logger.warning("Operation is null");
                Boolean mlpTriggeredReportingService = false;
                MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
                if (reportingService == MLPLocationRequest.ReportingService.Triggered)
                    mlpTriggeredReportingService = true;
                if (locationRequestParams.pslLcsReferenceNumber != null)
                    reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Operation is null", "SRILCS", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                else if (locationRequestParams.plrLcsReferenceNumber != null)
                    reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Operation is null", "RIR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.plrLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
                else
                    reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Operation is null", "UDR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
            }
        } else {
            logger.warning("Target Subscriber Identity (MSISDN or IMSI) is null");
            Boolean mlpTriggeredReportingService = false;
            MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
            if (reportingService == MLPLocationRequest.ReportingService.Triggered)
                mlpTriggeredReportingService = true;
            if (locationRequestParams.operation.equalsIgnoreCase("PSI"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN) is null", "PSI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, false);
            else if (locationRequestParams.operation.equalsIgnoreCase("PSL"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN or IMSI) is null", "SRILCS", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
            else if (locationRequestParams.operation.equalsIgnoreCase("PLR"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN or IMSI) is null", "RIR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, locationRequestParams.plrLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
            else if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN or IMSPublicIdentity) is null", "UDR", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, false);
            else if (locationRequestParams.operation.equalsIgnoreCase("ATI"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN) is null", "ATI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, false);
            else if (locationRequestParams.operation.equalsIgnoreCase("SRI"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN) is null", "SRI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, false);
            else if (locationRequestParams.operation.equalsIgnoreCase("SRISM"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN) is null", "SRISM", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, false);
            else if (locationRequestParams.operation.equalsIgnoreCase("SRILCS"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN) is null", "SRILCS", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, false);
            else if (locationRequestParams.operation.equalsIgnoreCase("SUPL"))
                reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Target Subscriber Identity (MSISDN) is null", "SUPL", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.targetingIMEI, null, null, null, null, null, mlpTriggeredReportingService);
        }
    }

    private void handleExceptionOnRequest(HttpRequestType httpRequestType, LocationRequestParams locationRequestParams, EventContext eventContext,
                                          String operation, String exceptionString) {
        setHttpRequest(httpRequestType, locationRequestParams);
        eventContext.suspendDelivery();
        setEventContextCMP(eventContext);
        String msisdn = null, imsi = null, imei = null;
        Integer referenceNumber = null;
        boolean mlpTriggeredReportingService = false;
        MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
        if (reportingService == MLPLocationRequest.ReportingService.Triggered)
            mlpTriggeredReportingService = true;
        if (locationRequestParams.targetingMSISDN != null)
            msisdn = locationRequestParams.targetingMSISDN;
        else if (locationRequestParams.targetingIMSI != null)
            imsi = locationRequestParams.targetingIMSI;
        if (locationRequestParams.targetingIMEI != null)
            imei = locationRequestParams.targetingIMEI;
        if (locationRequestParams.pslLcsReferenceNumber != null)
            referenceNumber = locationRequestParams.pslLcsReferenceNumber;
        else if (locationRequestParams.plrLcsReferenceNumber != null)
            referenceNumber = locationRequestParams.plrLcsReferenceNumber;
        if (msisdn != null || imsi != null || imei != null)
            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, exceptionString, operation, msisdn, imsi, imei,
                referenceNumber, null, null, null, null, mlpTriggeredReportingService);
    }

    private void setHttpRequest(HttpRequestType httpRequestType, LocationRequestParams locationRequestParams) {
        if (locationRequestParams != null) {
            if (locationRequestParams.operation != null) {
                if (!locationRequestParams.operation.equalsIgnoreCase("PLR")) {
                    if (locationRequestParams.operation.equalsIgnoreCase("UDR")) {
                        // set HttpRequest with params for Sh UDR
                        setHttpRequest(new HttpRequest(httpRequestType, locationRequestParams.udrMsisdn, "", locationRequestParams.udrImsPublicId,
                            locationRequestParams.operation, locationRequestParams.domainType, null, null, null,
                                null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null,
                            null, null, null, null, null, null,
                            null, null, null, null, null,
                            locationRequestParams.httpRespType, locationRequestParams.locationInfoEps, locationRequestParams.activeLocation,
                            locationRequestParams.locationInfo5gs, locationRequestParams.ratTypeRequested, null));
                    } else {
                        // set HttpRequest with params for MAP PSL, MAP ATI and MAP SRIx-PSI
                        setHttpRequest(new HttpRequest(httpRequestType, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.udrImsPublicId,
                            locationRequestParams.operation, locationRequestParams.domainType, locationRequestParams.pslLcsPriority, locationRequestParams.pslLcsHorizontalAccuracy,
                            locationRequestParams.pslLcsVerticalAccuracy, locationRequestParams.pslVerticalCoordinateRequest, locationRequestParams.pslResponseTimeCategory, locationRequestParams.pslVelocityRequest,
                            locationRequestParams.pslLcsReferenceNumber, locationRequestParams.pslLcsServiceTypeID, locationRequestParams.pslAreaType, locationRequestParams.pslAreaId,
                            null, null, locationRequestParams.pslOccurrenceInfo, locationRequestParams.pslIntervalTime, null, null,
                            null, null, null, null, null, null, null,
                            null, null, locationRequestParams.pslReportingAmount, locationRequestParams.pslReportingInterval,
                            locationRequestParams.slrCallbackUrl, locationRequestParams.pslQoSClass, locationRequestParams.httpRespType,
                            null, null, null, null, locationRequestParams.translationType));
                    }
                } else {
                    // set HttpRequest with params for SLh-Slg RIR/PLR
                    String priority = null, verticalRequested = null, responseTime = null, areaType = null, additionalAreaType = null, areaEventOccurrenceInfo = null;
                    Integer horizontalAccuracy = null, verticalAccuracy = null, serviceTypeId = null, areaEventIntervalTime = null, periodicLDRReportingAmount = null,
                        periodicLDRReportingInterval = null;
                    if (locationRequestParams.plrLcsPriority != null)
                        priority = String.valueOf(locationRequestParams.plrLcsPriority);
                    if (locationRequestParams.plrHorizontalAccuracy != null)
                        horizontalAccuracy = locationRequestParams.plrHorizontalAccuracy.intValue();
                    if (locationRequestParams.plrVerticalAccuracy != null)
                        verticalAccuracy = locationRequestParams.plrVerticalAccuracy.intValue();
                    if (locationRequestParams.plrVerticalRequested != null)
                        verticalRequested = String.valueOf(locationRequestParams.plrVerticalRequested);
                    if (locationRequestParams.plrResponseTime != null)
                        responseTime = String.valueOf(locationRequestParams.plrResponseTime);
                    if (locationRequestParams.plrLcsServiceTypeId != null)
                        serviceTypeId = locationRequestParams.plrLcsServiceTypeId.intValue();
                    if (locationRequestParams.plrAreaType != null)
                        areaType = String.valueOf(locationRequestParams.plrAreaType);
                    if (locationRequestParams.plrAdditionalAreaType != null)
                        additionalAreaType = String.valueOf(locationRequestParams.plrAdditionalAreaType);
                    if (locationRequestParams.plrAreaEventOccurrenceInfo != null)
                        areaEventOccurrenceInfo = String.valueOf(locationRequestParams.plrAreaEventOccurrenceInfo);
                    if (locationRequestParams.plrAreaEventIntervalTime != null)
                        areaEventIntervalTime = locationRequestParams.plrAreaEventIntervalTime.intValue();
                    if (locationRequestParams.plrPeriodicLDRReportingAmount != null)
                        periodicLDRReportingAmount = locationRequestParams.plrPeriodicLDRReportingAmount.intValue();
                    if (locationRequestParams.plrPeriodicLDRReportingInterval != null)
                        periodicLDRReportingInterval = locationRequestParams.plrPeriodicLDRReportingInterval.intValue();

                    setHttpRequest(new HttpRequest(httpRequestType, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, locationRequestParams.udrImsPublicId,
                        locationRequestParams.operation, locationRequestParams.domainType, priority, horizontalAccuracy, verticalAccuracy, verticalRequested,
                        responseTime, null, locationRequestParams.plrLcsReferenceNumber, serviceTypeId, areaType, locationRequestParams.plrAreaIdentification, additionalAreaType,
                        locationRequestParams.plrAdditionalAreaIdentification, areaEventOccurrenceInfo, areaEventIntervalTime, locationRequestParams.plrAreaEventMaxInterval,
                        locationRequestParams.plrAreaEventSamplingInterval, locationRequestParams.plrAreaEventReportingDuration, locationRequestParams.plrAreaEventRepLocRequirements,
                        locationRequestParams.plrMotionEventOccurrenceInfo, locationRequestParams.plrMotionEventLinearDistance, locationRequestParams.plrMotionEventSamplingInterval,
                        locationRequestParams.plrMotionEventIntervalTime, locationRequestParams.plrMotionEventMaximumInterval, locationRequestParams.plrMotionEvenReportingDuration,
                        locationRequestParams.plrMotionEvenReportingLocationRequirements, periodicLDRReportingAmount, periodicLDRReportingInterval,
                        locationRequestParams.lrrCallbackUrl, locationRequestParams.plrQoSClass, locationRequestParams.httpRespType,
                        locationRequestParams.locationInfoEps, locationRequestParams.activeLocation, locationRequestParams.locationInfo5gs, locationRequestParams.ratTypeRequested,
                        null));
                }
            } else {
                setHttpRequest(new HttpRequest(httpRequestType, locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, null,
                    null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null, null));
            }
        } else {
            setHttpRequest(new HttpRequest(httpRequestType, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null,
                null, null, null, null, null, null));
        }
    }

    /**
     * CMP
     */
    public abstract void setEventContext(EventContext eventContext);

    public abstract EventContext getEventContext();

    public abstract void setEventContextCMP(EventContext eventContext);

    public abstract EventContext getEventContextCMP();

    public abstract void setHttpRequest(HttpRequest httpRequest);

    public abstract HttpRequest getHttpRequest();

    public abstract void setTimerID(TimerID value);

    public abstract TimerID getTimerID();

    public abstract void setGMLCCDRState(GMLCCDRState gmlcSdrState);

    public abstract GMLCCDRState getGMLCCDRState();

    public abstract void setSendRoutingInfoForLCSResponse(SendRoutingInfoForLCSResponse sendRoutingInfoForLCSResponse);

    public abstract SendRoutingInfoForLCSResponse getSendRoutingInfoForLCSResponse();

    public abstract void setProvideSubscriberLocationResponse(ProvideSubscriberLocationResponse provideSubscriberLocationResponse);

    public abstract ProvideSubscriberLocationResponse getProvideSubscriberLocationResponse();

    public abstract void setSubscriberLocationReportRequest(SubscriberLocationReportRequest subscriberLocationReportRequest);

    public abstract SubscriberLocationReportRequest getSubscriberLocationReportRequest();

    public abstract void setSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponse);

    public abstract SendRoutingInfoForSMResponse getSendRoutingInfoForSMResponse();

    public abstract void setProvideSubscriberInformationResponse(ProvideSubscriberInfoResponse subscriberLocationReportRequest);

    public abstract ProvideSubscriberInfoResponse getProvideSubscriberInformationResponse();

    public abstract void setSendRoutingInformationResponse(SendRoutingInformationResponse sendRoutingInformationResponse);

    public abstract SendRoutingInformationResponse getSendRoutingInformationResponse();

    public abstract void setErrorResponse(MAPErrorMessage errorResponse);

    public abstract MAPErrorMessage getErrorResponse();


    /*** Private helper methods ***/

    /**
     * Handle generating the appropriate ATI generated by the HTTP request
     *
     * @param locationRequestParams Parameters to set in MAP ATI gathered from HTTP request
     */
    private void getLocationViaMapAti(LocationRequestParams locationRequestParams) {

        SubscriberIdentity subscriberIdentity = null;

        if (locationRequestParams.targetingIMSI != null ||
            (locationRequestParams.targetingMSISDN != null && !locationRequestParams.targetingMSISDN.equals(fakeNumber))) {
            try {
                MAPDialogMobility mapDialogMobility;
                int translationType = 0; // Translation Type = 0 : Unknown (default value)
                if (locationRequestParams.translationType != null)
                    translationType = locationRequestParams.translationType; // Translation Type taken from HTTP request
                if (locationRequestParams.targetingIMSI != null) {
                    subscriberIdentity = new SubscriberIdentityImpl(new IMSIImpl(locationRequestParams.targetingIMSI));
                    mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(
                        this.getMAPAtiApplicationContext(), this.getGmlcSccpAddress(), null,
                        getSccpAddress(locationRequestParams.targetingIMSI, NumberingPlan.LAND_MOBILE, translationType), null);
                } else {
                    subscriberIdentity = new SubscriberIdentityImpl(new ISDNAddressStringImpl(AddressNature.international_number,
                        org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, locationRequestParams.targetingMSISDN));
                    mapDialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(
                        this.getMAPAtiApplicationContext(), this.getGmlcSccpAddress(), null,
                        getSccpAddress(locationRequestParams.targetingMSISDN, NumberingPlan.ISDN_TELEPHONY, translationType), null);
                }
                boolean locationInformation = true;
                boolean subscriberState = true;
                boolean currentLocation = true;
                DomainType requestedDomain;
                if (locationRequestParams.domainType.equalsIgnoreCase("ps"))
                    requestedDomain = DomainType.psDomain;
                else
                    requestedDomain = DomainType.csDomain;
                boolean imei = true;
                boolean msClassmark = true;
                boolean mnpRequestedInfo = true;
                if (locationRequestParams.atiExtraInfoRequested.equalsIgnoreCase("false")) {
                    imei = false;
                    msClassmark = false;
                    mnpRequestedInfo = false;
                }
                boolean locationInformationEPSsupported;
                if (locationRequestParams.locationInfoEps == null)
                    locationInformationEPSsupported = true;
                else
                    locationInformationEPSsupported = Boolean.parseBoolean(locationRequestParams.locationInfoEps);
                RequestedInfo requestedInfo = new RequestedInfoImpl(locationInformation, subscriberState, null, currentLocation,
                    requestedDomain, imei, msClassmark, mnpRequestedInfo, locationInformationEPSsupported);

                ISDNAddressString gscmSCFAddress = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN,
                    gmlcPropertiesManagement.getGmlcGt());

                mapDialogMobility.addAnyTimeInterrogationRequest(subscriberIdentity, requestedInfo, gscmSCFAddress, null);

                Long transaction = mobileCoreNetworkTransactions.create();
                if (locationRequestParams.targetingIMSI != null)
                    mobileCoreNetworkTransactions.setValue(transaction, "atiImsi", locationRequestParams.targetingIMSI);
                else
                    mobileCoreNetworkTransactions.setValue(transaction, "atiMsisdn", locationRequestParams.targetingMSISDN);
                mobileCoreNetworkTransactions.setValue(transaction, "atiHttpRespType", locationRequestParams.httpRespType);
                mobileCoreNetworkTransactions.setValue(transaction, "curlUser", locationRequestParams.curlUser);
                mobileCoreNetworkTransactions.setDialog(transaction, mapDialogMobility.getLocalDialogId());
                DateTime transactionStart = DateTime.now();
                mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
                mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", false);

                ActivityContextInterface atiDialogACI = this.mapAcif.getActivityContextInterface(mapDialogMobility);
                atiDialogACI.attach(this.sbbContext.getSbbLocalObject());
                mapDialogMobility.send();

                // set new timer for ATI cycle
                TimerID timerID = timerFacility.setTimer(atiDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

            } catch (MAPException e) {
                this.logger.severe("MAPException while trying to send MAP ATI request for subscriber: " + subscriberIdentity, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP ATI request",
                    "ATI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, null, null, null, null, null, null, false);
            } catch (Exception e) {
                this.logger.severe("Exception while trying to send MAP ATI request for subscriber=" + subscriberIdentity, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP ATI request",
                    "ATI", locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, null, null, null, null, null, null, false);
            }

        } else {
            if (locationRequestParams.targetingMSISDN != null) {
                // Handle fake location type given fake number set as MSISDN
                if (this.fakeLocationType == MLPResponse.MLPResultType.OK) {
                    AtiResponseParams response = new AtiResponseParams();
                    try {
                        response.setCi(fakeCellId);
                        response.setMcc(fakeMcc);
                        response.setMcc(fakeMnc);
                        response.setUncertainty(fakeLocationUncertainty);
                    } catch (Exception e) {
                        logger.severe("MAP Exception while assigning ATI response values for MLP fake location:" + e);
                    }
                    this.handleAtiLocationResponse(MLPResponse.MLPResultType.OK, response, locationRequestParams.targetingMSISDN,
                        locationRequestParams.targetingIMSI, null, "Fake number", locationRequestParams.httpRespType);
                } else {
                    this.handleAtiLocationResponse(this.fakeLocationType, null, locationRequestParams.targetingMSISDN,
                        locationRequestParams.targetingIMSI, this.fakeLocationAdditionalInfoErrorString, "Fake number", locationRequestParams.httpRespType);
                }
            }
        }
    }


    /**
     * Handle generating the appropriate SRILCS-PSL (LSM) operations generated by the HTTP request
     *
     * @param targetMsisdn          Target MSISDN
     * @param targetImsi            Target IMSI
     * @param locationRequestParams Parameters to set in SRILCS and PSL gathered from HTTP request
     */
    private void getLocationViaMapLsm(String targetMsisdn, String targetImsi, LocationRequestParams locationRequestParams, boolean sriLcsOnly) {

        String targetSubscriberIdentity;
        if (targetMsisdn != null)
            targetSubscriberIdentity = targetMsisdn;
        else
            targetSubscriberIdentity = targetImsi;
        SubscriberIdentity subscriberIdentity;
        Boolean mlpTriggeredReportingService = false;
        MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
        if (reportingService == MLPLocationRequest.ReportingService.Triggered)
            mlpTriggeredReportingService = true;

        if (!targetSubscriberIdentity.equals(fakeNumber)) {
            try {
                MAPDialogLsm mapDialogLsmSRIforLCS;
                int translationType = 0; // Translation Type = 0 : Unknown (default value)
                if (locationRequestParams.translationType != null)
                    translationType = locationRequestParams.translationType; // Translation Type taken from HTTP request
                if (targetMsisdn != null) {
                    subscriberIdentity = new SubscriberIdentityImpl(new ISDNAddressStringImpl(AddressNature.international_number,
                        org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, targetMsisdn));
                    mapDialogLsmSRIforLCS = this.mapProvider.getMAPServiceLsm().createNewDialog(
                        this.getMAPSriforLcsApplicationContext(), this.getGmlcSccpAddress(), null,
                        getSccpAddress(targetMsisdn, NumberingPlan.ISDN_TELEPHONY, translationType), null);
                } else {
                    subscriberIdentity = new SubscriberIdentityImpl(new IMSIImpl(targetImsi));
                    mapDialogLsmSRIforLCS = this.mapProvider.getMAPServiceLsm().createNewDialog(
                        this.getMAPSriforLcsApplicationContext(), this.getGmlcSccpAddress(), null,
                        getSccpAddress(targetImsi, NumberingPlan.LAND_MOBILE, translationType), null);
                }
                ISDNAddressString gmlcAddress = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, this.getGmlcSccpAddress().getGlobalTitle().getDigits());
                mapDialogLsmSRIforLCS.addSendRoutingInfoForLCSRequest(gmlcAddress, subscriberIdentity, null);

                // Transaction
                Long transaction = mobileCoreNetworkTransactions.create();
                mobileCoreNetworkTransactions.setValue(transaction, "pslMsisdn", locationRequestParams.targetingMSISDN);
                mobileCoreNetworkTransactions.setValue(transaction, "pslImsi", locationRequestParams.targetingIMSI);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsReferenceNumber", locationRequestParams.pslLcsReferenceNumber);
                mobileCoreNetworkTransactions.setValue(transaction, "pslImei", locationRequestParams.targetingIMEI);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLocationEstimateType", locationRequestParams.pslLocationEstimateType);
                mobileCoreNetworkTransactions.setValue(transaction, "pslDeferredLocationEventType", locationRequestParams.pslDeferredLocationEventType);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsClientType", locationRequestParams.pslLcsClientType);
                mobileCoreNetworkTransactions.setValue(transaction, "pslClientExternalID", locationRequestParams.pslClientExternalID);
                mobileCoreNetworkTransactions.setValue(transaction, "pslClientInternalID", locationRequestParams.pslClientInternalID);
                mobileCoreNetworkTransactions.setValue(transaction, "pslClientName", locationRequestParams.pslClientName);
                mobileCoreNetworkTransactions.setValue(transaction, "pslClientFormatIndicator", locationRequestParams.pslClientFormatIndicator);
                mobileCoreNetworkTransactions.setValue(transaction, "pslRequestorIdString", locationRequestParams.pslRequestorIdString);
                mobileCoreNetworkTransactions.setValue(transaction, "pslRequestorFormatIndicator", locationRequestParams.pslRequestorFormatIndicator);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsServiceTypeID", locationRequestParams.pslLcsServiceTypeID);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsPriority", locationRequestParams.pslLcsPriority);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsHorizontalAccuracy", locationRequestParams.pslLcsHorizontalAccuracy);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsVerticalAccuracy", locationRequestParams.pslLcsVerticalAccuracy);
                mobileCoreNetworkTransactions.setValue(transaction, "pslVerticalCoordinateRequest", locationRequestParams.pslVerticalCoordinateRequest);
                mobileCoreNetworkTransactions.setValue(transaction, "pslResponseTimeCategory", locationRequestParams.pslResponseTimeCategory);
                mobileCoreNetworkTransactions.setValue(transaction, "pslVelocityRequested", locationRequestParams.pslVelocityRequest);
                mobileCoreNetworkTransactions.setValue(transaction, "pslQoSClass", locationRequestParams.pslQoSClass);
                mobileCoreNetworkTransactions.setValue(transaction, "pslAreaType", locationRequestParams.pslAreaType);
                mobileCoreNetworkTransactions.setValue(transaction, "pslAreaId", locationRequestParams.pslAreaId);
                mobileCoreNetworkTransactions.setValue(transaction, "pslOccurrenceInfo", locationRequestParams.pslOccurrenceInfo);
                mobileCoreNetworkTransactions.setValue(transaction, "pslIntervalTime", locationRequestParams.pslIntervalTime);
                mobileCoreNetworkTransactions.setValue(transaction, "pslReportingAmount", locationRequestParams.pslReportingAmount);
                mobileCoreNetworkTransactions.setValue(transaction, "pslReportingInterval", locationRequestParams.pslReportingInterval);
                mobileCoreNetworkTransactions.setValue(transaction, "pslPLMNIdList", locationRequestParams.pslPLMNIdList);
                mobileCoreNetworkTransactions.setValue(transaction, "pslVisitedPLMNIdRAN", locationRequestParams.pslVisitedPLMNIdRAN);
                mobileCoreNetworkTransactions.setValue(transaction, "pslPeriodicLocationSupportIndicator", locationRequestParams.pslPeriodicLocationSupportIndicator);
                mobileCoreNetworkTransactions.setValue(transaction, "pslPrioritizedListIndicator", locationRequestParams.pslPrioritizedListIndicator);
                mobileCoreNetworkTransactions.setValue(transaction, "pslLcsCodeword", locationRequestParams.pslLcsCodeword);
                mobileCoreNetworkTransactions.setValue(transaction, "slrCallbackUrl", locationRequestParams.slrCallbackUrl);
                mobileCoreNetworkTransactions.setValue(transaction, "httpRequestType", locationRequestParams.httpRespType);
                mobileCoreNetworkTransactions.setValue(transaction, "curlUser", locationRequestParams.curlUser);
                mobileCoreNetworkTransactions.setDialog(transaction, mapDialogLsmSRIforLCS.getLocalDialogId());
                DateTime transactionStart = DateTime.now();
                mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
                mobileCoreNetworkTransactions.setValue(transaction, "sriLcsOnly", sriLcsOnly);
                mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", mlpTriggeredReportingService);

                // Create the ACI and attach this SBB
                ActivityContextInterface sriForLcsDialogACI = this.mapAcif.getActivityContextInterface(mapDialogLsmSRIforLCS);
                sriForLcsDialogACI.attach(this.sbbContext.getSbbLocalObject());
                mapDialogLsmSRIforLCS.send();

                // set new timer for SRILCS cycle
                TimerID timerID = timerFacility.setTimer(sriForLcsDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

            } catch (MAPException e) {
                this.logger.severe("MAPException while trying to send MAP SRILCS request for Subscriber Identity=" + targetSubscriberIdentity, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP SRILCS request",
                    "SRILCS", targetMsisdn, targetImsi, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
            } catch (Exception e) {
                this.logger.severe("Exception while trying to send MAP SRILCS request for Subscriber Identity=" + targetSubscriberIdentity, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP SRILCS request",
                    "SRILCS", targetMsisdn, targetImsi, locationRequestParams.targetingIMEI, locationRequestParams.pslLcsReferenceNumber, null, null, null, null, mlpTriggeredReportingService);
            }

        } else {
            // Handle fake location type given fake number set as MSISDN
            if (this.fakeLocationType == MLPResponse.MLPResultType.OK) {
                PslResponseParams response = new PslResponseParams();
                final CellGlobalIdOrServiceAreaIdFixedLength cellFake;
                try {
                    cellFake = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(0, 0, 0, fakeCellId);
                } catch (MAPException e) {
                    throw new RuntimeException(e);
                }
                CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(cellFake);
                response.setCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdOrLAI);
                ExtGeographicalInformation extGeographicalInformation;
                try {
                  // fakeLocationX = "27 28 25.00S"
                  double fakeXLocation = 27.458902;
                  // fakeLocationY = "153 01 43.00E"
                  double fakeYLocation = 153.014394;
                  int fakeLocationInnerRadius = 100;
                  extGeographicalInformation = new ExtGeographicalInformationImpl(null, fakeXLocation, fakeYLocation, 0, 0, 0, 0, 0, 0, 0, fakeLocationInnerRadius, 0, 0, 0, 0, 0);
                } catch (MAPException e) {
                    throw new RuntimeException(e);
                }
                response.setLocationEstimate(extGeographicalInformation);
                this.handleLsmLocationResponse(MLPResponse.MLPResultType.OK, null, response, locationRequestParams.targetingIMEI, null, mlpTriggeredReportingService);
            } else {
                this.handleLsmLocationResponse(this.fakeLocationType, null, null, locationRequestParams.targetingIMEI, this.fakeLocationAdditionalInfoErrorString, mlpTriggeredReportingService);
            }
        }
    }

    /**
     * Handle generating the appropriate SRISM-PSI generated by the HTTP request
     *
     * @param locationRequestParams Parameters to set in SRISM and PSI gathered from HTTP request
     * @param onDialogRejected      Boolean value for set in when the dialog has been rejected by the SS7 network
     * @param sriSmOnly             Boolean value that indicates if the PSI shall be executed after SRISM or not
     */
    private void getLocationViaSubscriberInformation(LocationRequestParams locationRequestParams,
                                                     boolean onDialogRejected, boolean sriSmOnly) {

        String requestingMSISDN = locationRequestParams.targetingMSISDN;
        String domain = locationRequestParams.domainType;
        String epsLocationInfo = locationRequestParams.locationInfoEps;
        Integer calledPartyTranslationType = locationRequestParams.translationType;
        String curlUser = locationRequestParams.curlUser;

        if (!requestingMSISDN.equals(fakeNumber)) {
            try {
                MAPDialogSms mapDialogSms;
                MAPApplicationContext mapApplicationContext = this.getMAPSRISMApplicationContext(onDialogRejected);
                String acnVersion;
                if (mapApplicationContext.getApplicationContextVersion() == MAPApplicationContextVersion.version2)
                    acnVersion = "version2";
                else
                    acnVersion = "version3";

                int translationType = 0; // Translation Type = 0 : Unknown (default value)
                if (calledPartyTranslationType != null)
                    translationType = calledPartyTranslationType; // Translation Type taken from HTTP request
                mapDialogSms = this.mapProvider.getMAPServiceSms().createNewDialog(mapApplicationContext, this.getGmlcSccpAddress(),
                    null, getHlrSccpAddress(requestingMSISDN, translationType), null);
                ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, requestingMSISDN);
                boolean sm_RP_PRI = true;
                AddressString serviceCentreAddressString = this.mapParameterFactory.createAddressString(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, gmlcPropertiesManagement.getGmlcGt());
                mapDialogSms.addSendRoutingInfoForSMRequest(msisdn, sm_RP_PRI, serviceCentreAddressString, null, false, null, null,
                        null, false, null, false, false, null, null, false);

                // Transaction
                Long transaction = mobileCoreNetworkTransactions.create();
                mobileCoreNetworkTransactions.setValue(transaction, "sriPsiDomain", domain);
                mobileCoreNetworkTransactions.setValue(transaction, "locationInfoEPS", epsLocationInfo);
                mobileCoreNetworkTransactions.setValue(transaction, "psiMsisdn", requestingMSISDN);
                mobileCoreNetworkTransactions.setValue(transaction, "sriAcnVersion", acnVersion);
                mobileCoreNetworkTransactions.setValue(transaction, "tt", translationType);
                mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
                mobileCoreNetworkTransactions.setDialog(transaction, mapDialogSms.getLocalDialogId());
                DateTime transactionStart = DateTime.now();
                mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
                mobileCoreNetworkTransactions.setValue(transaction, "sriSmOnly", sriSmOnly);
                mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", false);

                // Create the ACI and attach this SBB
                ActivityContextInterface sriForSmDialogACI = this.mapAcif.getActivityContextInterface(mapDialogSms);
                sriForSmDialogACI.attach(this.sbbContext.getSbbLocalObject());

                // Send SRISM
                mapDialogSms.send();

                // set new timer for SRISM cycle
                TimerID timerID = timerFacility.setTimer(sriForSmDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

            } catch (MAPException e) {
                this.logger.severe("MAP Exception while trying to send MAP SRISM request for MSISDN=" + requestingMSISDN, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP SRISM request",
                    "SRISM", requestingMSISDN, null, null, null, null, null, null, null, false);
            } catch (Exception e) {
                this.logger.severe("Exception while trying to send MAP SRISM request for MSISDN=" + requestingMSISDN, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP SRISM request",
                    "SRISM", requestingMSISDN, null, null, null, null, null, null, null, false);
            }

        } else {
            // Handle fake location type given fake number set as MSISDN
            PsiResponseParams response = new PsiResponseParams();
            if (this.fakeLocationType == MLPResponse.MLPResultType.OK) {
                try {
                    response.setCi(fakeCellId);
                    response.setMcc(fakeMcc);
                    response.setMcc(fakeMnc);
                    response.setUncertainty(fakeLocationUncertainty);
                } catch (Exception e) {
                    logger.severe("MAP Exception while assigning ATI response values for MLP fake location:" + e);
                }

                this.handlePsiResponse(MLPResponse.MLPResultType.OK, response, requestingMSISDN, null);

            } else {
                this.handlePsiResponse(this.fakeLocationType, null, requestingMSISDN, this.fakeLocationAdditionalInfoErrorString);
            }
        }
    }

    /**
     * Handle generating the appropriate SRI-PSI generated by the HTTP request
     *
     * @param locationRequestParams Parameters to set in SRI and PSI gathered from HTTP request
     * @param onDialogRejected      Boolean value for set in when the dialog has been rejected by the SS7 network
     * @param sriOnly               Boolean value that indicates if the PSI shall be executed after SRI or not
     */
    private void getLocationViaSubscriberInformationCallHandling(LocationRequestParams locationRequestParams,
                                                                 boolean onDialogRejected, boolean sriOnly) {

        String requestingMSISDN = locationRequestParams.targetingMSISDN;
        String domain = locationRequestParams.domainType;
        String epsLocationInfo = locationRequestParams.locationInfoEps;
        Integer calledPartyTranslationType = locationRequestParams.translationType;
        String curlUser = locationRequestParams.curlUser;

        if (!requestingMSISDN.equals(fakeNumber)) {
            try {
                MAPDialogCallHandling mapDialogCallHandling;
                MAPApplicationContext mapApplicationContext = this.getMAPSRIApplicationContext(onDialogRejected);
                String acnVersion;
                if (mapApplicationContext.getApplicationContextVersion() == MAPApplicationContextVersion.version2)
                    acnVersion = "version2";
                else
                    acnVersion = "version3";

                int translationType = 0; // Translation Type = 0 : Unknown (default value)
                if (calledPartyTranslationType != null)
                    translationType = calledPartyTranslationType; // Translation Type taken from HTTP request
                mapDialogCallHandling = this.mapProvider.getMAPServiceCallHandling().createNewDialog(mapApplicationContext,
                    this.getGmlcSccpAddress(), null, getHlrSccpAddress(requestingMSISDN, translationType), null);
                ISDNAddressString msisdn = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, requestingMSISDN);
                ISDNAddressString gmlcAddress = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, this.getGmlcSccpAddress().getGlobalTitle().getDigits());

                mapDialogCallHandling.addSendRoutingInformationRequest(msisdn, null, null,
                    InterrogationType.basicCall, false, null, gmlcAddress, null,
                    null, null, null, null, false,
                    null, null, false, null, null,
                    null, false, null, false, false, false,
                    false, null, null, null, false, null);

                // Transaction
                Long transaction = mobileCoreNetworkTransactions.create();
                mobileCoreNetworkTransactions.setValue(transaction, "sriPsiDomain", domain);
                mobileCoreNetworkTransactions.setValue(transaction, "locationInfoEPS", epsLocationInfo);
                mobileCoreNetworkTransactions.setValue(transaction, "psiMsisdn", requestingMSISDN);
                mobileCoreNetworkTransactions.setValue(transaction, "sriAcnVersion", acnVersion);
                mobileCoreNetworkTransactions.setValue(transaction, "tt", translationType);
                mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
                mobileCoreNetworkTransactions.setDialog(transaction, mapDialogCallHandling.getLocalDialogId());
                DateTime transactionStart = DateTime.now();
                mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
                mobileCoreNetworkTransactions.setValue(transaction, "sriOnly", sriOnly);
                mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", false);

                // Create the ACI and attach this SBB
                ActivityContextInterface sriDialogACI = this.mapAcif.getActivityContextInterface(mapDialogCallHandling);
                sriDialogACI.attach(this.sbbContext.getSbbLocalObject());

                // Send SRI
                mapDialogCallHandling.send();

                // set new timer for SRI cycle
                TimerID timerID = timerFacility.setTimer(sriDialogACI, null, System.currentTimeMillis() + MAP_OPERATION_TIMEOUT, defaultTimerOptions);
                mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

            } catch (MAPException e) {
                this.logger.severe("MAP Exception while trying to send MAP SRI request for MSISDN=" + requestingMSISDN, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP SRI request",
                    "SRI", requestingMSISDN, null, null, null, null, null, null, null, false);
            } catch (Exception e) {
                this.logger.severe("Exception while trying to send MAP SRI request for MSISDN=" + requestingMSISDN, e);
                this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "MAPException while trying to send MAP SRI request",
                    "SRI", requestingMSISDN, null, null, null, null, null, null, null, false);
            }
        } else {
            // Handle fake location type given fake number set as MSISDN
            PsiResponseParams response = new PsiResponseParams();
            if (this.fakeLocationType == MLPResponse.MLPResultType.OK) {
                try {
                    response.setMcc(fakeMcc);
                    response.setMcc(fakeMnc);
                    int fakeLac = 271;
                    response.setLac(fakeLac);
                    response.setCi(fakeCellId);
                } catch (Exception e) {
                    logger.severe("MAP Exception while assigning SRI response values for MLP fake location:" + e);
                }

                this.handlePsiResponse(MLPResponse.MLPResultType.OK, response, requestingMSISDN, null);

            } else {
                this.handlePsiResponse(this.fakeLocationType, null, requestingMSISDN, this.fakeLocationAdditionalInfoErrorString);
            }
        }
    }

    /**
     * Handle generating the appropriate SLh-SLg Diameter commands generated by the HTTP request
     *
     * @param targetImsi            Target IMSI
     * @param targetMsisdn          Target MSISDN
     * @param locationRequestParams Parameters to set in RIR and PLR gathered from HTTP request
     */
    public void getLocationViaLTELocationServices(String targetImsi, String targetMsisdn, LocationRequestParams locationRequestParams) {

        boolean mlpTriggeredReportingService = false;
        MLPLocationRequest.ReportingService reportingService = locationRequestParams.getReportingService();
        if (reportingService == MLPLocationRequest.ReportingService.Triggered)
            mlpTriggeredReportingService = true;

        try {
            DiameterIdentity destHost = new DiameterIdentity(locationRequestParams.hssDiameterHost != null ? locationRequestParams.hssDiameterHost : gmlcPropertiesManagement.getDiameterDestHost());
            DiameterIdentity destRealm = new DiameterIdentity(locationRequestParams.hssDiameterRealm != null ? locationRequestParams.hssDiameterRealm : gmlcPropertiesManagement.getDiameterDestRealm());
            SLhClientSessionActivity slhClientSessionActivity = this.slhProvider.createSLhClientSessionActivity(destHost, destRealm);
            // Keep ACI across Diameter session for RIR/RIA
            ActivityContextInterface slhACIF = slhAcif.getActivityContextInterface(slhClientSessionActivity);
            slhACIF.attach(this.sbbContext.getSbbLocalObject());

            RequestValuesForPLR requestValuesForPLR = new RequestValuesForPLR();
            requestValuesForPLR.plrUserName = targetImsi;
            requestValuesForPLR.plrMsisdn = targetMsisdn;
            requestValuesForPLR.plrSlgLocationType = locationRequestParams.plrSlgLocationType;
            requestValuesForPLR.plrLcsNameString = locationRequestParams.plrLcsNameString;
            requestValuesForPLR.plrLcsFormatInd = locationRequestParams.plrLcsFormatInd;
            requestValuesForPLR.plrLcsClientType = locationRequestParams.plrLcsClientType;
            requestValuesForPLR.plrLcsRequestorIdString = locationRequestParams.plrLcsRequestorIdString;
            requestValuesForPLR.plrLcsRequestorFormatIndicator = locationRequestParams.plrLcsRequestorFormatIndicator;
            requestValuesForPLR.plrQoSClass = locationRequestParams.plrQoSClass;
            requestValuesForPLR.plrHorizontalAccuracy = locationRequestParams.plrHorizontalAccuracy;
            requestValuesForPLR.plrVerticalAccuracy = locationRequestParams.plrVerticalAccuracy;
            requestValuesForPLR.plrVerticalRequested = locationRequestParams.plrVerticalRequested;
            requestValuesForPLR.plrResponseTime = locationRequestParams.plrResponseTime;
            requestValuesForPLR.plrVelocityRequested = locationRequestParams.plrVelocityRequested;
            requestValuesForPLR.plrPrivacyCheckNonSession = locationRequestParams.plrPrivacyCheckNonSession;
            requestValuesForPLR.plrPrivacyCheckSession = locationRequestParams.plrPrivacyCheckSession;
            requestValuesForPLR.plrAreaType = locationRequestParams.plrAreaType;
            requestValuesForPLR.plrAreaIdentification = locationRequestParams.plrAreaIdentification;
            requestValuesForPLR.plrAdditionalAreaType = locationRequestParams.plrAdditionalAreaType;
            requestValuesForPLR.plrAdditionalAreaIdentification = locationRequestParams.plrAdditionalAreaIdentification;
            requestValuesForPLR.plrAreaEventOccurrenceInfo = locationRequestParams.plrAreaEventOccurrenceInfo;
            requestValuesForPLR.plrAreaEventIntervalTime = locationRequestParams.plrAreaEventIntervalTime;
            requestValuesForPLR.plrAreaEventMaxInterval = locationRequestParams.plrAreaEventMaxInterval;
            requestValuesForPLR.plrAreaEventSamplingInterval = locationRequestParams.plrAreaEventSamplingInterval;
            requestValuesForPLR.plrAreaEventReportingDuration = locationRequestParams.plrAreaEventReportingDuration;
            requestValuesForPLR.plrAreaEventRepLocRequirements = locationRequestParams.plrAreaEventRepLocRequirements;
            requestValuesForPLR.plrPeriodicLDRReportingAmount = locationRequestParams.plrPeriodicLDRReportingAmount;
            requestValuesForPLR.plrPeriodicLDRReportingInterval = locationRequestParams.plrPeriodicLDRReportingInterval;
            requestValuesForPLR.plrVisitedPLMNIdList = locationRequestParams.plrVisitedPLMNIdList;
            requestValuesForPLR.plrPeriodicLocationSupportIndicator = locationRequestParams.plrPeriodicLocationSupportIndicator;
            requestValuesForPLR.reportingPLMNListAvp = locationRequestParams.reportingPLMNListAvp;
            requestValuesForPLR.plrPrioritizedListIndicator = locationRequestParams.plrPrioritizedListIndicator;
            requestValuesForPLR.plrMotionEventOccurrenceInfo = locationRequestParams.plrMotionEventOccurrenceInfo;
            requestValuesForPLR.plrMotionEventLinearDistance = locationRequestParams.plrMotionEventLinearDistance;
            requestValuesForPLR.plrMotionEventSamplingInterval = locationRequestParams.plrMotionEventSamplingInterval;
            requestValuesForPLR.plrMotionEventIntervalTime = locationRequestParams.plrMotionEventIntervalTime;
            requestValuesForPLR.plrMotionEventMaximumInterval = locationRequestParams.plrMotionEventMaximumInterval;
            requestValuesForPLR.plrMotionEvenReportingDuration = locationRequestParams.plrMotionEvenReportingDuration;
            requestValuesForPLR.plrMotionEvenReportingLocationRequirements = locationRequestParams.plrMotionEvenReportingLocationRequirements;
            requestValuesForPLR.plrLcsReferenceNumber = locationRequestParams.plrLcsReferenceNumber;
            requestValuesForPLR.lrrCallbackUrl = locationRequestParams.lrrCallbackUrl;
            requestValuesForPLR.plrImei = locationRequestParams.targetingIMEI;
            requestValuesForPLR.plrLcsPriority = locationRequestParams.plrLcsPriority;
            requestValuesForPLR.plrLcsSupportedGadShapes = locationRequestParams.plrLcsSupportedGadShapes;
            requestValuesForPLR.plrLcsServiceTypeId = locationRequestParams.plrLcsServiceTypeId;
            requestValuesForPLR.plrLcsCodeword = locationRequestParams.plrLcsCodeword;
            requestValuesForPLR.plrServiceSelection = locationRequestParams.plrServiceSelection;
            requestValuesForPLR.plrDeferredLocationType = locationRequestParams.plrDeferredLocationType;
            requestValuesForPLR.plrFlags = locationRequestParams.plrFlags;

            Long transaction = mobileCoreNetworkTransactions.create();
            mobileCoreNetworkTransactions.setValue(transaction, "requestValuesForPLR", requestValuesForPLR);
            mobileCoreNetworkTransactions.setSession(transaction, slhClientSessionActivity.getSessionId());
            mobileCoreNetworkTransactions.setValue(transaction, "curlUser", locationRequestParams.curlUser);
            mobileCoreNetworkTransactions.setValue(transaction, "httpRequestType", locationRequestParams.httpRespType);
            DateTime transactionStart = DateTime.now();
            mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
            mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", mlpTriggeredReportingService);

            // set the new timer for the RIR/RIA cycle
            TimerID timerID = timerFacility.setTimer(slhACIF, null, System.currentTimeMillis() + DIAMETER_COMMAND_TIMEOUT, defaultTimerOptions);
            mobileCoreNetworkTransactions.setValue(transaction, "timerID", timerID);

            if (logger.isFineEnabled()) {
                this.logger.fine(String.format("Saving RIR values with session '%s' and transaction '%d'.", slhClientSessionActivity.getSessionId(), transaction));
            }
            LCSRoutingInfoRequest rir = slhClientSessionActivity.createLCSRoutingInfoRequest();

            if (targetImsi != null) {
                rir.setUserName(targetImsi);
            }

            if (targetMsisdn != null) {
                rir.setMSISDN(parseTBCD(targetMsisdn));
            }

            String gmlcNumber = gmlcPropertiesManagement.getDiameterGmlcNumber();
            if (gmlcNumber != null) {
                if (!gmlcNumber.equalsIgnoreCase("")) {
                    rir.setGMLCNumber(parseTBCD(gmlcNumber));
                }
            }

            AuthSessionStateType authSessionStateType = AuthSessionStateType.NO_STATE_MAINTAINED;
            rir.setAuthSessionState(authSessionStateType);

            slhClientSessionActivity.sendLCSRoutingInfoRequest(rir);
            if (logger.isFineEnabled()) {
                logger.fine("Sent SLh RIR: " + rir);
            }

        } catch (Exception e) {
            logger.severe("setupLteLocationServicesInterface(): exception while sending LCSRoutingInfoRequest: " + e.getMessage(), e);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception while setting up LCSRoutingInfoRequest",
                "RIR", targetMsisdn, targetImsi, locationRequestParams.targetingIMEI, locationRequestParams.plrLcsReferenceNumber, null, null,
                null, null, mlpTriggeredReportingService);
        }
    }

    /**
     * Handle generating the appropriate Sh User-Data-Request command generated by the HTTP request
     *
     * @param locationRequestParams Parameters to set in Diameter Sh UDR gathered from HTTP request
     */
    public void getLocationViaIMSSh(LocationRequestParams locationRequestParams) {

        String msisdn = locationRequestParams.udrMsisdn;
        String publicIdentity = locationRequestParams.udrImsPublicId;
        int dataReferenceType = DataReferenceType._LOCATION_INFORMATION;
        String domain = locationRequestParams.domainType;
        String active = locationRequestParams.activeLocation;
        String epsLocInfo = locationRequestParams.locationInfoEps;
        String nr5gLocationInfo = locationRequestParams.locationInfo5gs;
        String ratTypeRequested = locationRequestParams.ratTypeRequested;
        String curlUser = locationRequestParams.curlUser;
        String hssHost = locationRequestParams.hssDiameterHost;
        String hssRealm = locationRequestParams.hssDiameterRealm;

        try {
            DiameterIdentity destHost = new DiameterIdentity(hssHost != null ? hssHost : gmlcPropertiesManagement.getDiameterDestHost());
            DiameterIdentity destRealm = new DiameterIdentity(hssRealm != null ? hssRealm : gmlcPropertiesManagement.getDiameterDestRealm());
            ShClientActivity shClientActivity = this.shClientProvider.createShClientActivity();
            // Keep ACI across Diameter session for UDR/UDA
            ActivityContextInterface shACIF = shClientAcif.getActivityContextInterface(shClientActivity);
            shACIF.attach(this.sbbContext.getSbbLocalObject());

            UserDataRequest udr = shClientActivity.getClientMessageFactory().createUserDataRequest();

            UserIdentityAvp userIdentityAvp = new UserIdentityAvpImpl(DiameterShAvpCodes.USER_IDENTITY, TGPP_VENDOR_ID, 1, 0, new byte[]{});
            if (msisdn != null) {
                byte[] tbcdMsisdn = parseTBCD(msisdn);
                userIdentityAvp.setMsisdn(tbcdMsisdn);
            }
            if (publicIdentity != null)
                userIdentityAvp.setPublicIdentity(publicIdentity);
            udr.setUserIdentity(userIdentityAvp);

            DataReferenceType dataReference = DataReferenceType.fromInt(dataReferenceType);
            udr.setDataReference(dataReference);

            RequestedDomainType requestedDomainType = RequestedDomainType.PS_DOMAIN;
            if (domain != null) {
                if (domain.equalsIgnoreCase("cs")) {
                    requestedDomainType = RequestedDomainType.CS_DOMAIN;
                }
            }
            if (epsLocInfo == null || epsLocInfo.equalsIgnoreCase("true")) {
                if (ratTypeRequested != null) {
                    if (ratTypeRequested.equalsIgnoreCase("true")) {
                        udr.setUDRFlags(3L);
                    } else {
                        udr.setUDRFlags(1L);
                    }
                } else {
                    udr.setUDRFlags(1L);
                }
                if (nr5gLocationInfo != null) {
                    if (nr5gLocationInfo.equalsIgnoreCase("true")) {
                        udr.setRequestedNodes(8L);
                    } else {
                        udr.setRequestedNodes(3L);
                    }
                } else {
                    udr.setRequestedNodes(3L);
                }
            } else {
                if (ratTypeRequested != null) {
                    if (ratTypeRequested.equalsIgnoreCase("true"))
                        udr.setUDRFlags(2L);
                }
            }
            udr.setRequestedDomain(requestedDomainType);

            CurrentLocationType currentLocationType = CurrentLocationType.INITIATE_ACTIVE_LOCATION_RETRIEVAL;
            if (active != null) {
                if (active.equalsIgnoreCase("false"))
                    currentLocationType = CurrentLocationType.DO_NOT_NEED_INITIATE_ACTIVE_LOCATION_RETRIEVAL;
            }
            udr.setCurrentLocation(currentLocationType);

            udr.setDestinationHost(destHost);
            udr.setDestinationRealm(destRealm);

            AuthSessionStateType authSessionStateType = AuthSessionStateType.NO_STATE_MAINTAINED;
            udr.setAuthSessionState(authSessionStateType);

            Long transaction = mobileCoreNetworkTransactions.create();
            mobileCoreNetworkTransactions.setValue(transaction, "shUdrMsisdn", msisdn);
            mobileCoreNetworkTransactions.setSession(transaction, shClientActivity.getSessionId());
            mobileCoreNetworkTransactions.setValue(transaction, "curlUser", curlUser);
            DateTime transactionStart = DateTime.now();
            mobileCoreNetworkTransactions.setValue(transaction, "transactionStart", transactionStart);
            mobileCoreNetworkTransactions.setValue(transaction, "mlpTriggeredReportingService", false);

            // set the new timer for the UDR/UDA cycle
            TimerID timerID = timerFacility.setTimer(shACIF, null, System.currentTimeMillis() + DIAMETER_COMMAND_TIMEOUT, defaultTimerOptions);
            mobileCoreNetworkTransactions.setValue(transaction, "timerId", timerID);

            shClientActivity.sendUserDataRequest(udr);
            if (logger.isFineEnabled()) {
                logger.fine("Sent Sh UDR for MSISDN=" + Arrays.toString(udr.getUserIdentity().getMsisdn()) + " with session Id: " + udr.getSessionId() +
                    ", host '" + udr.getDestinationHost() + "', realm '" + udr.getDestinationRealm());
                logger.fine("Sh UDR details: " + udr);
            }

        } catch (Exception e) {
            logger.severe("getLocationViaIMSSh: exception while sending Sh UDR: " + e.getMessage(), e);
            this.reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Exception while setting up Sh UDR", "RIR",
                msisdn, null, null, null, null, null, null, null, false);
        }
    }

    /**
     * Handle generating the appropriate SUPL session generated by the HTTP request
     *
     * @param locationRequestParams MLP Request Parameters for SUPL
     */
    public void getLocationViaSUPL(LocationRequestParams locationRequestParams, Integer transactionId, ActivityContextInterface aci) {
        InetAddress localSocketAddress = null, remoteSocketAddress = null;
        int localSocketPort = -1, remoteSocketPort = -1;
        Boolean mlpTriggeredReportingService = false;
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
        if (networkInitiatedSuplLocation != null) {
            if (networkInitiatedSuplLocation.getServerSocket() != null) {
                localSocketAddress = networkInitiatedSuplLocation.getServerSocket().getInetAddress();
                localSocketPort = networkInitiatedSuplLocation.getServerSocket().getLocalPort();
                SocketAddress socketAddress = networkInitiatedSuplLocation.getServerSocket().getLocalSocketAddress();
                InetSocketAddress remoteInetSocketAddress = (InetSocketAddress) socketAddress;
                remoteSocketAddress = remoteInetSocketAddress.getAddress();
                remoteSocketPort = remoteInetSocketAddress.getPort();
            }
        }
        // CDR initialization
        GMLCCDRState gmlcCdrState = CDRCreationHelper.suplCdrInitializer(aci, this.getCDRInterface(), locationRequestParams,
                localSocketAddress, localSocketPort, remoteSocketAddress, remoteSocketPort);
        // Set timer last
        this.setTimer(aci);

        try {
            QoP qoP = null;
            if (locationRequestParams.suplHorizontalAccuracy != null) {
                qoP = new QoP(locationRequestParams.suplHorizontalAccuracy);
                if (locationRequestParams.suplVerticalAccuracy != null && locationRequestParams.suplMaximumLocationAge != null &&
                        locationRequestParams.suplDelay != null && locationRequestParams.suplResponseTime != null) {
                    qoP = new QoP(locationRequestParams.suplHorizontalAccuracy, locationRequestParams.suplVerticalAccuracy,
                        locationRequestParams.suplMaximumLocationAge, locationRequestParams.suplDelay, locationRequestParams.suplResponseTime);
                }
            }
            SuplResponseHelperForMLP suplResponseHelperForMLP;
            PosMethod posMethod = PosMethod.agpsSETassisted(); // FIXME hardcoded for now
            TriggerParams triggerParams = null;
            if (locationRequestParams.reportingService == MLPLocationRequest.ReportingService.Triggered) {
                // The Location Request arms triggers for reports on area events or periodically (originated by MLP TLRR)
                mlpTriggeredReportingService = true;
                triggerParams = new TriggerParams();
                if (locationRequestParams.suplAreaEventType != null) {
                    Integer areaType = locationRequestParams.suplAreaEventType;
                    AreaEventType areaEventType;
                    switch (areaType) {
                        case 0:
                            areaEventType = AreaEventType.enteringArea();
                            break;
                        case 1:
                            areaEventType = AreaEventType.insideArea();
                            break;
                        case 2:
                            areaEventType = AreaEventType.outsideArea();
                            break;
                        case 3:
                            areaEventType = AreaEventType.leavingArea();
                            break;
                        default:
                            areaEventType = AreaEventType.valueOf(-999);
                            break;
                    }
                    Asn1Boolean locationEstimateRequired = new Asn1Boolean(locationRequestParams.suplLocationEstimateRequested);
                    Asn1Integer minimumIntervalTime = new Asn1Integer(locationRequestParams.suplMinimumIntervalTime);
                    Asn1Integer maxNumberOfReports = new Asn1Integer(locationRequestParams.suplMaximumNumberOfReports);
                    RepeatedReportingParams repeatedReportingParams = new RepeatedReportingParams(minimumIntervalTime, maxNumberOfReports);
                    Asn1Integer startTime = null, stopTime = null;
                    if (locationRequestParams.suplStartTime != null)
                        startTime = new Asn1Integer(locationRequestParams.suplStartTime);
                    if (locationRequestParams.suplStopTime != null)
                        stopTime = new Asn1Integer(locationRequestParams.suplStopTime);
                    GeographicTargetArea geographicTargetArea = new GeographicTargetArea();
                    Coordinate_latitudeSign latitudeSign = Coordinate_latitudeSign.north();
                    if (locationRequestParams.suplAreaEventLatitude < 0) {
                        latitudeSign = Coordinate_latitudeSign.south();
                        locationRequestParams.suplAreaEventLatitude *= -1;
                    }
                    Asn1Integer asn1Latitude = new Asn1Integer(GADShapesUtils.encodeLatitude(locationRequestParams.suplAreaEventLatitude));
                    Asn1Integer asn1Longitude = new Asn1Integer(GADShapesUtils.encodeLongitude(locationRequestParams.suplAreaEventLongitude));
                    Coordinate areaCoordinates = new Coordinate(latitudeSign, asn1Latitude, asn1Longitude);
                    if (locationRequestParams.suplGeographicTargetArea.equals(SuplGeoTargetArea.CircularArea.name())) {
                        Asn1Integer radius = new Asn1Integer(locationRequestParams.suplAreaEventRadius);
                        CircularArea circularArea = new CircularArea(areaCoordinates, radius);
                        geographicTargetArea.isCircularArea();
                        geographicTargetArea.setCircularArea(circularArea);
                    }
                    if (locationRequestParams.suplGeographicTargetArea.equals(SuplGeoTargetArea.EllipticalArea.name())) {
                        Asn1Integer semiMajor = new Asn1Integer(GADShapesUtils.encodeUncertainty(locationRequestParams.suplAreaEventSemiMajor));
                        Asn1Integer semiMinor = new Asn1Integer(GADShapesUtils.encodeUncertainty(locationRequestParams.suplAreaEventSemiMinor));
                        Asn1Integer angle = new Asn1Integer((long) ((locationRequestParams.suplAreaEventAngle / 2) - 1));
                        EllipticalArea ellipticalArea = new EllipticalArea();
                        ellipticalArea.setCoordinate(areaCoordinates);
                        ellipticalArea.setSemiMajor(semiMajor);
                        ellipticalArea.setSemiMinor(semiMinor);
                        ellipticalArea.setAngle(angle);
                        geographicTargetArea.isEllipticalArea();
                        geographicTargetArea.setEllipticalArea(ellipticalArea);
                    }
                    GeographicTargetArea[] geoTargetAreaArray = new GeographicTargetArea[]{geographicTargetArea};
                    GeographicTargetAreaList geographicTargetAreaList = new GeographicTargetAreaList(geoTargetAreaArray);
                    // TODO fill AreaEventParams_areaIdLists according to locationRequestParams.suplAreaId
                    byte[] eci = new byte[]{(byte) 0xc6, 0x03, 0x30, 0x30}; // FIXME hardcoded for now
                    LTEAreaId lteAreaId = new LTEAreaId(748L, 1L, new Asn1BitString(29, eci)); // FIXME hardcoded for now
                    AreaId areaId = new AreaId();
                    areaId.setLTEAreaId(lteAreaId);
                    AreaId[] areaIds = new AreaId[]{areaId};
                    /*for (AreaId areaId : areaIds) { }*/
                    AreaIdSet areaIdSet = new AreaIdSet(areaIds);
                    AreaIdList areaIdList = new AreaIdList(areaIdSet);
                    AreaIdList[] areaIdListArray = new AreaIdList[]{areaIdList};
                    AreaEventParams_areaIdLists areaIdLists = new AreaEventParams_areaIdLists(areaIdListArray);
                    AreaEventParams areaEventParams = new AreaEventParams(areaEventType, locationEstimateRequired, repeatedReportingParams,
                            startTime, stopTime, geographicTargetAreaList, areaIdLists);
                    triggerParams.setAreaEventParams(areaEventParams);
                    suplResponseHelperForMLP = networkInitiatedSuplLocation.processNetworkInitiatedSuplBySmppAreaEventTriggeredService(qoP, SuplTriggerType.AreaEvent.getSuplTriggerType(), posMethod, SLPMode.nonProxy(), triggerParams, transactionId, locationRequestParams.getTargetingMSISDN());
                    String splId = new String(suplResponseHelperForMLP.getSessionID().getSlpSessionID().getSessionID().value);
                    exec.scheduleWithFixedDelay(new MlpResponseExecutor(exec, splId), SUPL_SESSION_SCHEDULE, SUPL_SESSION_SCHEDULE, TimeUnit.MILLISECONDS);
                    try {
                        logger.info("EGMLC Exec ->  ScheduledThreadPoolExecutor");
                        exec.awaitTermination(SUPL_SESSION_TIMEOUT,TimeUnit.MILLISECONDS);
                        exec.shutdown();
                        int sessionStatus = (int) suplTransaction.getValue(splId,"sessionStatus");
                        if (sessionStatus == SuplSessionStatus.SUPL_SESSION_END.getSuplSessionStatus()) {
                            suplResponseHelperForMLP = (SuplResponseHelperForMLP) suplTransaction.getValue(splId,"suplResponseHelperForMLP");
                            suplTransaction.destroy(splId);
                        } else {
                            suplTransaction.destroy(splId);
                            suplResponseHelperForMLP.setTimeOut(true);
                        }
                    } catch (InterruptedException e) {
                        logger.severe("Error on start SULP session with slpId -> " + splId);
                    }
                    if (suplResponseHelperForMLP != null) {
                        suplResponseHelperForMLP.setMsisdn(locationRequestParams.targetingMSISDN);
                        suplResponseHelperForMLP.setImsi(locationRequestParams.targetingIMSI);
                        httpSubscriberLocationReport = getHttpSubscriberLocationReport();
                        boolean isMlp = true;
                        suplResponseHelperForMLP.suplReportReferenceNumber = httpSubscriberLocationReport.Register(transactionId,
                                locationRequestParams.suplAgentCallbackUrl, null, isMlp, locationRequestParams.curlUser);
                        httpSubscriberLocationReport.closeMongo();
                    }
                } else {
                    // Is periodic Event
                    Asn1Integer numberOfFixes = new Asn1Integer(locationRequestParams.suplReportingAmount);
                    Asn1Integer intervalBetweenFixes = new Asn1Integer(locationRequestParams.suplReportingInterval);
                    Asn1Integer periodicStartTime = new Asn1Integer(locationRequestParams.suplStartTime);
                    PeriodicParams periodicParams = new PeriodicParams(numberOfFixes, intervalBetweenFixes, periodicStartTime);
                    triggerParams.setPeriodicParams(periodicParams);
                    suplResponseHelperForMLP = networkInitiatedSuplLocation.processNetworkInitiatedSuplBySmppAreaEventTriggeredService(qoP, SuplTriggerType.AreaEvent.getSuplTriggerType(), posMethod, SLPMode.nonProxy(), triggerParams, transactionId, locationRequestParams.getTargetingMSISDN());
                    String splId = new String(suplResponseHelperForMLP.getSessionID().getSlpSessionID().getSessionID().value);
                    exec.scheduleWithFixedDelay(new MlpResponseExecutor(exec, splId), SUPL_SESSION_SCHEDULE, SUPL_SESSION_SCHEDULE, TimeUnit.MILLISECONDS);
                    try {
                        logger.info("EGMLC Exec ->  ScheduledThreadPoolExecutor");
                        exec.awaitTermination(SUPL_SESSION_TIMEOUT,TimeUnit.MILLISECONDS);
                        exec.shutdown();
                        int sessionStatus = (int) suplTransaction.getValue(splId,"sessionStatus");
                        if (sessionStatus == SuplSessionStatus.SUPL_SESSION_END.getSuplSessionStatus()) {
                            suplResponseHelperForMLP = (SuplResponseHelperForMLP) suplTransaction.getValue(splId,"suplResponseHelperForMLP");
                            suplTransaction.destroy(splId);
                        } else {
                            suplTransaction.destroy(splId);
                            suplResponseHelperForMLP.setTimeOut(true);
                            // Removing from the concurrent hashmap
                            // TimeOut Error
                        }
                    } catch (InterruptedException e) {
                        logger.severe("Error on start SULP session with slpId -> " + splId);
                    }
                    if (suplResponseHelperForMLP != null) {
                        suplResponseHelperForMLP.setMsisdn(locationRequestParams.targetingMSISDN);
                        suplResponseHelperForMLP.setImsi(locationRequestParams.targetingIMSI);
                        httpSubscriberLocationReport = getHttpSubscriberLocationReport();
                        boolean isMlp = true;
                        suplResponseHelperForMLP.suplReportReferenceNumber = httpSubscriberLocationReport.Register(transactionId,
                                locationRequestParams.suplAgentCallbackUrl, null, isMlp, locationRequestParams.curlUser);
                        httpSubscriberLocationReport.closeMongo();
                    }
                }
            } else {
                logger.warning("EGMLC -> MSISDN " + locationRequestParams.getTargetingMSISDN());
                // Location Request it is standard (originated by MLP SLIR), does not arm triggers for periodic or area event reporting
                //TODO Check when the request contains IMSI
                suplResponseHelperForMLP = networkInitiatedSuplLocation.processNetworkInitiatedSuplBySmppStandardService(qoP, SLPMode.nonProxy(), transactionId, locationRequestParams.getTargetingMSISDN());
                String splId = new String(suplResponseHelperForMLP.getSessionID().getSlpSessionID().getSessionID().value);
                exec.scheduleWithFixedDelay(new MlpResponseExecutor(exec, splId), SUPL_SESSION_SCHEDULE, SUPL_SESSION_SCHEDULE, TimeUnit.MILLISECONDS);
                try {
                    logger.info("EGMLC Exec ->  ScheduledThreadPoolExecutor");
                    exec.awaitTermination(SUPL_SESSION_TIMEOUT,TimeUnit.MILLISECONDS);
                    exec.shutdown();
                    int sessionStatus = (int) suplTransaction.getValue(splId,"sessionStatus");
                    if (sessionStatus == SuplSessionStatus.SUPL_SESSION_END.getSuplSessionStatus()) {
                        suplResponseHelperForMLP = (SuplResponseHelperForMLP) suplTransaction.getValue(splId,"suplResponseHelperForMLP");
                        suplTransaction.destroy(splId);
                    } else {
                        suplTransaction.destroy(splId);
                        suplResponseHelperForMLP.setTimeOut(true);
                        // Removing from the concurrent hashmap
                        // TimeOut Error
                    }
                } catch (InterruptedException e) {
                    logger.severe("Error on start SUPL session with slpId -> " + splId);
                }

            }
            if (suplResponseHelperForMLP != null && !suplResponseHelperForMLP.getTimeOut()) {
                handleSUPLResponse(MLPResponse.MLPResultType.OK, "", locationRequestParams.targetingMSISDN,
                        locationRequestParams.targetingIMSI, transactionId, suplResponseHelperForMLP, mlpTriggeredReportingService, false);
                if (suplResponseHelperForMLP.isReport()) {
                    handleSuplReports(suplResponseHelperForMLP, aci, triggerParams, locationRequestParams, gmlcCdrState, transactionId, mlpTriggeredReportingService);
                } else {
                    if (gmlcCdrState.isInitialized()) {
                        if (triggerParams != null) {
                            if (triggerParams.getAreaEventParams() != null) {
                                CDRCreationHelper.updateSuplCdrState(gmlcCdrState, suplResponseHelperForMLP, networkInitiatedSuplLocation, transactionId);
                                this.createCDRRecord(RecordStatus.SUPL_NI_AREA_TRIGGERED_SUCCESS);
                            }
                            if (triggerParams.isPeriodicParams()) {
                                CDRCreationHelper.updateSuplCdrState(gmlcCdrState, suplResponseHelperForMLP, networkInitiatedSuplLocation, transactionId);
                                this.createCDRRecord(RecordStatus.SUPL_NI_PERIODIC_TRIGGERED_SUCCESS);
                            }
                        } else {
                            CDRCreationHelper.updateSuplCdrState(gmlcCdrState, suplResponseHelperForMLP, networkInitiatedSuplLocation, transactionId);
                            this.createCDRRecord(RecordStatus.SUPL_NI_STANDARD_SUCCESS);
                        }
                    } else {
                        if (gmlcCdrState.isInitialized()) {
                            this.createCDRRecord(RecordStatus.SUPL_SYSTEM_FAILURE);
                        }
                    }
                }
            } else {
                if (suplResponseHelperForMLP != null) {
                    if (suplResponseHelperForMLP.getTimeOut()) {
                        this.createCDRRecord(RecordStatus.SUPL_TIMEOUT);
                        reportLocationRequestError(MLPResponse.MLPResultType.TIMEOUT, "Network Initiated SUPL positioning timeout", "SUPL",
                                locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, null, null, null, null, null, null, mlpTriggeredReportingService);
                    } else {
                        if (gmlcCdrState.isInitialized()) {
                            if (triggerParams != null) {
                                if (locationRequestParams.suplAreaEventType != null) {
                                    this.createCDRRecord(RecordStatus.SUPL_NI_AREA_TRIGGERED_FAILURE);
                                }
                                if (locationRequestParams.suplReportingInterval != null && locationRequestParams.suplReportingAmount != null) {
                                    this.createCDRRecord(RecordStatus.SUPL_NI_PERIODIC_TRIGGERED_FAILURE);
                                }
                            } else {
                                this.createCDRRecord(RecordStatus.SUPL_NI_STANDARD_FAILURE);
                            }
                        } else {
                            this.createCDRRecord(RecordStatus.SUPL_SYSTEM_FAILURE);
                        }
                        reportLocationRequestError(MLPResponse.MLPResultType.POSITION_METHOD_FAILURE, "Network Initiated SUPL positioning failure", "SUPL",
                                locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, null, null, null, null, null, null, mlpTriggeredReportingService);

                    }
                }
            }

        } catch (Exception e) {
            logger.severe("Exception on getLocationViaSUPL ", e);
            if (gmlcCdrState.isInitialized()) {
                this.createCDRRecord(RecordStatus.SUPL_SYSTEM_FAILURE);
            }
            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Exception on SUPL request", "SUPL", locationRequestParams.targetingMSISDN,
                    locationRequestParams.targetingIMSI, null, null, null, null, null, null, mlpTriggeredReportingService);
        } finally {
            aci.detach(this.sbbContext.getSbbLocalObject());
        }
    }

    public void handleSuplReports(SuplResponseHelperForMLP suplResponseHelperForMLP, ActivityContextInterface aci, TriggerParams triggerParams,
                                  LocationRequestParams locationRequestParams, GMLCCDRState gmlcCdrState, int transactionId, boolean mlpTriggeredReportingService) {
        try {
            httpSubscriberLocationReport = getHttpSubscriberLocationReport();
            httpSubscriberLocationReport.Perform(HttpReport.HttpMethod.POST, suplResponseHelperForMLP.suplReportReferenceNumber,
                suplResponseHelperForMLP, false);
            httpSubscriberLocationReport.closeMongo();
            if (gmlcCdrState.isInitialized()) {
                if (triggerParams != null) {
                    if (triggerParams.getAreaEventParams() != null) {
                        CDRCreationHelper.updateSuplCdrState(gmlcCdrState, suplResponseHelperForMLP, null, transactionId);
                        this.createCDRRecord(RecordStatus.SUPL_NI_AREA_TRIGGERED_REPORT_SUCCESS);
                    }
                    if (triggerParams.isPeriodicParams()) {
                        CDRCreationHelper.updateSuplCdrState(gmlcCdrState, suplResponseHelperForMLP, null, transactionId);
                        this.createCDRRecord(RecordStatus.SUPL_NI_PERIODIC_TRIGGERED_REPORT_SUCCESS);
                    }
                }
            } else {
                this.createCDRRecord(RecordStatus.SUPL_SYSTEM_FAILURE);
            }
        } catch (Exception e) {
            logger.severe("Exception on handleSuplReports ", e);
            reportLocationRequestError(MLPResponse.MLPResultType.FORMAT_ERROR, "Exception on SUPL report processing", "SUPL",
                locationRequestParams.targetingMSISDN, locationRequestParams.targetingIMSI, null, null, null, null, null,
                null, mlpTriggeredReportingService);
            aci.detach(this.sbbContext.getSbbLocalObject());
            if (gmlcCdrState.isInitialized()) {
                this.createCDRRecord(RecordStatus.SUPL_SYSTEM_FAILURE);
            }
        }
    }

    /*
     * MAP Application Context creation for ATI
     */
    private MAPApplicationContext getMAPAtiApplicationContext() {
        if (this.anyTimeEnquiryContext == null) {
            this.anyTimeEnquiryContext = MAPApplicationContext.getInstance(
                MAPApplicationContextName.anyTimeEnquiryContext, MAPApplicationContextVersion.version3);
        }
        return this.anyTimeEnquiryContext;
    }

    /*
     * MAP Application Context creation for SRILCS
     */
    private MAPApplicationContext getMAPSriforLcsApplicationContext() {
        if (this.locationSvcGatewayContext == null) {
            this.locationSvcGatewayContext = MAPApplicationContext.getInstance(MAPApplicationContextName.locationSvcGatewayContext,
                MAPApplicationContextVersion.version3);
        }
        return this.locationSvcGatewayContext;
    }

    /*
     * MAP Application Context creation for PSL and SLR
     */
    private MAPApplicationContext getMAPPslSlrApplicationContext() {
        if (this.locationSvcEnquiryContext == null) {
            this.locationSvcEnquiryContext = MAPApplicationContext.getInstance(MAPApplicationContextName.locationSvcEnquiryContext,
                MAPApplicationContextVersion.version3);
        }
        return this.locationSvcEnquiryContext;
    }

    /*
     * MAP Application Context creation for SRISM
     */
    private MAPApplicationContext getMAPSRISMApplicationContext(boolean onDialogRejected) {
        if (!onDialogRejected)
            this.shortMsgGatewayContext = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgGatewayContext,
                MAPApplicationContextVersion.version3);
        else
            this.shortMsgGatewayContext = MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgGatewayContext,
                MAPApplicationContextVersion.version2);
        return this.shortMsgGatewayContext;
    }

    /*
     * MAP Application Context creation for SRI (call handling)
     */
    private MAPApplicationContext getMAPSRIApplicationContext(boolean onDialogRejected) {
        if (!onDialogRejected)
            this.locationInfoRetrievalContext = MAPApplicationContext.getInstance(MAPApplicationContextName.locationInfoRetrievalContext,
                MAPApplicationContextVersion.version3);
        else
            this.locationInfoRetrievalContext = MAPApplicationContext.getInstance(MAPApplicationContextName.locationInfoRetrievalContext,
                MAPApplicationContextVersion.version2);
        return this.locationInfoRetrievalContext;
    }

    /*
     * MAP Application Context creation for PSI
     */
    private MAPApplicationContext getMAPPsiApplicationContext() {
        if (this.subscriberInfoEnquiryContext == null) {
            this.subscriberInfoEnquiryContext = MAPApplicationContext.getInstance(
                MAPApplicationContextName.subscriberInfoEnquiryContext, MAPApplicationContextVersion.version3);
        }
        return this.subscriberInfoEnquiryContext;
    }

    /*
     * GMLC SCCP Address creation
     */
    protected SccpAddress getGmlcSccpAddress() {
        if (this.gmlcSCCPAddress == null) {
            int tt = 0; // Translation Type = 0 : Unknown
            GlobalTitle gt = sccpParameterFact.createGlobalTitle(gmlcPropertiesManagement.getGmlcGt(), tt, NumberingPlan.ISDN_TELEPHONY,
                null, NatureOfAddress.INTERNATIONAL);
            this.gmlcSCCPAddress = sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,
                gt, 0, gmlcPropertiesManagement.getGmlcSsn());
        }
        return this.gmlcSCCPAddress;
    }

    /*
     * HLR SCCP Address creation
     */
    private SccpAddress getSccpAddress(String address, NumberingPlan numberingPlan, Integer translationType) {
        int tt = 0; // Translation Type = 0 : Unknown (default value)
        if (translationType != null)
            tt = translationType; // Translation Type taken from HTTP request
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, tt, numberingPlan, null, NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getHlrSsn());
    }

    /*
     * HLR SCCP Address creation
     */
    private SccpAddress getHlrSccpAddress(String address, Integer translationType) {
        int tt = 0; // Translation Type = 0 : Unknown (default value)
        if (translationType != null)
            tt = translationType; // Translation Type taken from HTTP request
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, tt, NumberingPlan.ISDN_TELEPHONY, null, NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getHlrSsn());
    }

    /*
     * MSC SCCP Address creation
     */
    private SccpAddress getMscSccpAddress(String address) {
        int tt = 0; // Translation Type = 0 : Unknown (default value)
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, tt, NumberingPlan.ISDN_TELEPHONY, null, NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getMscSsn());
    }

    /*
     * VLR SCCP Address creation
     */
    private SccpAddress getVlrSccpAddress(String address) {
        int tt = 0; // Translation Type = 0 : Unknown (default value)
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, tt, NumberingPlan.ISDN_TELEPHONY, null, NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getVlrSsn());
    }

    /*
     * SGSN SCCP Address creation
     */
    private SccpAddress getSgsnSccpAddress(String address) {
        int tt = 0; // Translation Type = 0 : Unknown (default value)
        GlobalTitle gt = sccpParameterFact.createGlobalTitle(address, tt, NumberingPlan.ISDN_TELEPHONY, null, NatureOfAddress.INTERNATIONAL);
        return sccpParameterFact.createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gt, 0, gmlcPropertiesManagement.getSgsnSsn());
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType         OK or error type to return to client
     * @param atiResponseParams     ATIResponse on location attempt
     * @param mlpClientErrorMessage Error message to send to the client
     * @param dialogResultMessage   Operation result to send to the client
     * @param httpResponseType      JSON or plain text payload to send in the HTTP response to the client (doesn't apply to MLP)
     */
    protected void handleAtiLocationResponse(MLPResponse.MLPResultType mlpResultType, AtiResponseParams atiResponseParams, String atiMsisdnDigits,
                                             String atiImsi, String mlpClientErrorMessage, String dialogResultMessage, String httpResponseType) {

        try {
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            MapAtiResponseHelperForMLP atiHelper = new MapAtiResponseHelperForMLP();
            atiHelper.handleAtiResponseValues(atiResponseParams);

            switch (request.type) {
                case REST:
                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {

                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

                        if (httpResponseType != null && httpResponseType.equalsIgnoreCase("plain")) {

                            /***** Plain HTTP Response *****/
                            // for retro-compatibility with Restcomm Geolocation API and GMLC v1.0.0
                            String atiResponseSb = "mcc=" +
                                    atiHelper.getMcc() +
                                    ", mnc=" +
                                    atiHelper.getMnc() +
                                    ", lac=" +
                                    atiHelper.getLac() +
                                    ", cellid=" +
                                    atiHelper.getCi() +
                                    ", aol=" +
                                    atiHelper.getAgeOfLocationInfo() +
                                    ", vlrNumber=" +
                                    atiHelper.getVlrNumber() +
                                    ", subscriberState=" +
                                    atiHelper.getSubscriberState() +
                                    "\n";

                            this.sendHTTPResult(HttpServletResponse.SC_OK, atiResponseSb);

                        } else {
                            /***** JSON Response *****/
                            String jsonResponse = "";
                            try {
                                if (atiResponseParams != null)
                                    jsonResponse = AtiResponseJsonBuilder.buildJsonResponseForAti(atiResponseParams, atiMsisdnDigits, atiImsi, "SUCCESS");
                                else
                                    reportLocationRequestError(MLPResponse.MLPResultType.SYSTEM_FAILURE, dialogResultMessage,
                                        "ATI", atiMsisdnDigits, atiImsi, atiHelper.getImei(), null, atiHelper.getNnn(),
                                        null, null, null, false);
                            } catch (MAPException me) {
                                this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Failure: MAP exception\"}");
                                logger.severe(me.getMessage());
                            } catch (Exception e) {
                                logger.severe(e.getMessage());
                            }
                            this.sendHTTPResult(HttpServletResponse.SC_OK, jsonResponse);
                            break;
                        }
                    } else {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                    }
                    break;

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);

                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        MLPResponseParams atiResponse = new MLPResponseParams();
                        atiResponse.mlpTypeOfShape = atiHelper.getTypeOfShape();
                        atiResponse.x = atiHelper.getLatitude();
                        atiResponse.y = atiHelper.getLongitude();
                        atiResponse.radius = atiHelper.getRadius();
                        atiResponse.mlpMcc = atiHelper.getMcc();
                        atiResponse.mlpMnc = atiHelper.getMnc();
                        atiResponse.mlpLac = atiHelper.getLac();
                        atiResponse.mlpCi = atiHelper.getCi();
                        atiResponse.mlpSac = atiHelper.getSac();
                        atiResponse.mlpUci = null;
                        atiResponse.mlpEnbId = atiHelper.getEnbId();
                        atiResponse.mlpEci = atiHelper.getEci();
                        atiResponse.mlpRac = atiHelper.getRac();
                        atiResponse.mlpTac = atiHelper.getTac();
                        atiResponse.mlpNci = atiHelper.getNci();
                        atiResponse.mlpNrTac = atiHelper.getNrTac();
                        atiResponse.mlpMmeName = atiHelper.getMmeName();
                        atiResponse.mlpMmeRealm = null;
                        atiResponse.mlpSgsnName = atiHelper.getSgsnNumber();
                        atiResponse.mlpSgsnRealm = null;
                        atiResponse.mlpSgsnNumber = atiHelper.getSgsnNumber();
                        if (atiHelper.getAmfAddress() != null)
                            atiResponse.mlpAmfAddress = new String(atiHelper.getAmfAddress().getData(), StandardCharsets.UTF_8);
                        atiResponse.mlpMscNo = atiHelper.getMscNumber();
                        atiResponse.mlpVlrNo = atiHelper.getVlrNumber();
                        atiResponse.mlpMsisdn = atiMsisdnDigits;
                        atiResponse.mlpImei = atiHelper.getImei();
                        atiResponse.mlpImsi = atiImsi;
                        atiResponse.mlpAge = atiHelper.getAgeOfLocationInfo();
                        atiResponse.mlpLmsi = null;
                        atiResponse.mlpRatType = atiHelper.getRatType();
                        atiResponse.mlpCivicAddress = null;

                        svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML("ATI", atiResponse.mlpTypeOfShape, atiResponse.x, atiResponse.y,
                            atiResponse.radius, null, null, null, null, null, null,
                            null, null, null, null,
                            null, null, null, null, null, null,
                            atiResponse.mlpMcc, atiResponse.mlpMnc, atiResponse.mlpLac, atiResponse.mlpCi, atiResponse.mlpSac,
                            atiResponse.mlpUci, atiResponse.mlpEci, atiResponse.mlpEnbId,
                            atiResponse.mlpRac, atiResponse.mlpTac, atiResponse.mlpNci, atiResponse.mlpNrTac,
                            atiResponse.mlpMmeName, atiResponse.mlpMmeRealm, atiResponse.mlpSgsnName, atiResponse.mlpSgsnRealm,
                            atiResponse.mlpSgsnNumber, atiResponse.mlpAmfAddress, atiResponse.mlpMscNo, atiResponse.mlpVlrNo,
                            atiResponse.mlpMsisdn, atiResponse.mlpImei, atiResponse.mlpImsi, atiResponse.mlpAge, null,
                            null, null, atiResponse.mlpRatType, atiResponse.mlpCivicAddress,
                            mlpResultType, false, false);

                    } else if (MLPResponse.isSystemError(mlpResultType)) {
                        svcResultXml = mlpResponse.getSystemErrorResponseXML(mlpResultType, mlpClientErrorMessage, false);
                    } else {
                        String imei = null;
                        if (atiResponseParams.getImei() != null)
                            imei = atiResponseParams.getImei().getIMEI();

                        svcResultXml = mlpResponse.getPositionErrorResponseXML(atiMsisdnDigits, atiImsi, imei,
                            mlpResultType, mlpClientErrorMessage, false, null, null);
                    }

                    this.sendHTTPResult(HttpServletResponse.SC_OK, svcResultXml);
                    break;
            }
        } catch (Exception e) {
            logger.severe("Exception on handleAtiLocationResponse: ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType         OK or error type to return to client
     * @param sriLcs                SRIForLCS response on location attempt
     * @param psl                   PSL response on location attempt
     * @param mlpClientErrorMessage Error message to send to the client
     */
    protected void handleLsmLocationResponse(MLPResponse.MLPResultType mlpResultType, SriLcsResponseParams sriLcs, PslResponseParams psl,
                                             String pslImei, String mlpClientErrorMessage, Boolean mlpTriggeredReportingService) {

        try {
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            switch (request.type) {
                case REST:
                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {

                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

                        String jsonResponse = "";
                        try {
                            jsonResponse = PslResponseJsonBuilder.buildJsonResponseForPsl(sriLcs, psl, sriLcs.getPslMsisdn(), sriLcs.getPslImsi(), pslImei,
                                sriLcs.getPslLcsReferenceNumber(), sriLcs.getPslReferenceNumber());
                        } catch (MAPException me) {
                            this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Fail MAP params\"}");
                            logger.severe(me.getMessage());
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                        this.sendHTTPResult(HttpServletResponse.SC_OK, jsonResponse);
                        break;
                    }

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);
                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        MapLsmResponseHelperForMLP mapLsmHelper = new MapLsmResponseHelperForMLP();
                        String pslMsisdn, pslImsi;
                        Integer pslClientTransId = null, pslLcsRefNum = null;
                        MLPResponseParams mapLsmResponse = new MLPResponseParams();
                        if (sriLcs != null) {
                            pslMsisdn = sriLcs.getPslMsisdn();
                            pslImsi = sriLcs.getPslImsi();
                            mapLsmHelper.setLcsReferenceNumber(sriLcs.getPslReferenceNumber());
                            pslLcsRefNum = mapLsmHelper.getLcsReferenceNumber();
                            pslClientTransId = sriLcs.getPslLcsReferenceNumber();
                            mapLsmHelper.handleSriLcsResponseValue(sriLcs, pslMsisdn, pslImsi);
                            mapLsmResponse.mlpTransId = sriLcs.getPslLcsReferenceNumber();
                            mapLsmResponse.mlpLcsRefNumber = sriLcs.getPslReferenceNumber();
                        }
                        mapLsmHelper.handlePslResponseValues(psl);
                        mapLsmResponse.mlpMsisdn = mapLsmHelper.getMsisdn();
                        mapLsmResponse.mlpImsi = mapLsmHelper.getImsi();
                        mapLsmResponse.mlpImei = pslImei;
                        mapLsmResponse.x = mapLsmHelper.getLatitude();
                        mapLsmResponse.y = mapLsmHelper.getLongitude();
                        mapLsmResponse.mlpTypeOfShape = mapLsmHelper.getTypeOfShape();
                        mapLsmResponse.radius = mapLsmHelper.getRadius();
                        mapLsmResponse.mlpUncertaintySemiMajorAxis = mapLsmHelper.getUncertaintySemiMajorAxis();
                        mapLsmResponse.mlpUncertaintySemiMinorAxis = mapLsmHelper.getUncertaintySemiMinorAxis();
                        mapLsmResponse.mlpAngleOfMajorAxis = mapLsmHelper.getAngleOfMajorAxis();
                        mapLsmResponse.mlpOffsetAngle = mapLsmHelper.getOffsetAngle();
                        mapLsmResponse.mlpIncludedAngle = mapLsmHelper.getIncludedAngle();
                        mapLsmResponse.mlpAltitude = mapLsmHelper.getAltitude();
                        mapLsmResponse.mlpUncertaintyAltitude = mapLsmHelper.getUncertaintyAltitude();
                        mapLsmResponse.mlpPolygon = mapLsmHelper.getPolygon();
                        mapLsmResponse.mlpNumberOfPoints = mapLsmHelper.getNumberOfPoints();
                        mapLsmResponse.mlpPositioningMethod = mapLsmHelper.getPositioningMethod();
                        mapLsmResponse.mlpMcc = mapLsmHelper.getMcc();
                        mapLsmResponse.mlpMnc = mapLsmHelper.getMnc();
                        mapLsmResponse.mlpLac = mapLsmHelper.getLac();
                        mapLsmResponse.mlpCi = mapLsmHelper.getCi();
                        mapLsmResponse.mlpSac = mapLsmHelper.getSac();
                        mapLsmResponse.mlpUci = null;
                        mapLsmResponse.mlpEci = null;
                        mapLsmResponse.mlpEnbId = null;
                        mapLsmResponse.mlpRac = null;
                        mapLsmResponse.mlpTac = null;
                        mapLsmResponse.mlpNci = null;
                        mapLsmResponse.mlpNrTac = null;
                        mapLsmResponse.mlpMmeName = mapLsmHelper.getMmeName();
                        mapLsmResponse.mlpMmeRealm = null;
                        mapLsmResponse.mlpSgsnName = mapLsmHelper.getSgsnName();
                        mapLsmResponse.mlpSgsnRealm = null;
                        if (mapLsmHelper.getGprsNodeIndicator())
                            mapLsmResponse.mlpSgsnNumber = mapLsmHelper.getNnn();
                        else
                            mapLsmResponse.mlpMscNo = mapLsmHelper.getNnn();
                        mapLsmResponse.mlpAmfAddress = null;
                        mapLsmResponse.mlpVlrNo = null;
                        mapLsmResponse.mlpImei = mapLsmHelper.getImei();
                        mapLsmResponse.mlpLmsi = mapLsmHelper.getLmsi();
                        mapLsmResponse.mlpAge = mapLsmHelper.getAgeOfLocationEstimate();
                        mapLsmResponse.mlpTargetHorizontalSpeed = mapLsmHelper.getHorizontalSpeed();
                        mapLsmResponse.mlpBearing = mapLsmHelper.getBearing();
                        mapLsmResponse.mlpTargetVerticalSpeed = mapLsmHelper.getVerticalSpeed();
                        mapLsmResponse.mlpUncertaintyHorizontalSpeed = mapLsmHelper.getUncertaintyHorizontalSpeed();
                        mapLsmResponse.mlpUncertaintyVerticalSpeed = mapLsmHelper.getUncertaintyVerticalSpeed();
                        mapLsmResponse.mlpVelocityType = mapLsmHelper.getVelocityType();
                        mapLsmResponse.mlpTransId = pslClientTransId;
                        mapLsmResponse.mlpLcsRefNumber = pslLcsRefNum;
                        mapLsmResponse.mlpRatType = null;
                        mapLsmResponse.mlpCivicAddress = mapLsmHelper.getCivicAddress();

                        svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML("PSL", mapLsmResponse.mlpTypeOfShape,
                            mapLsmResponse.x, mapLsmResponse.y, mapLsmResponse.radius, mapLsmResponse.mlpUncertaintySemiMajorAxis,
                            mapLsmResponse.mlpUncertaintySemiMinorAxis, mapLsmResponse.mlpAngleOfMajorAxis, mapLsmResponse.mlpOffsetAngle,
                            mapLsmResponse.mlpIncludedAngle, mapLsmResponse.mlpAltitude, mapLsmResponse.mlpUncertaintyAltitude,
                            mapLsmResponse.mlpPolygon, mapLsmResponse.mlpNumberOfPoints,
                            mapLsmResponse.mlpPositioningMethod,
                            mapLsmResponse.mlpTargetHorizontalSpeed, mapLsmResponse.mlpBearing, mapLsmResponse.mlpTargetVerticalSpeed,
                            mapLsmResponse.mlpUncertaintyHorizontalSpeed, mapLsmResponse.mlpUncertaintyVerticalSpeed, mapLsmResponse.mlpVelocityType,
                            mapLsmResponse.mlpMcc, mapLsmResponse.mlpMnc, mapLsmResponse.mlpLac, mapLsmResponse.mlpCi, mapLsmResponse.mlpSac,
                            mapLsmResponse.mlpUci, mapLsmResponse.mlpEci, mapLsmResponse.mlpEnbId, mapLsmResponse.mlpRac, mapLsmResponse.mlpTac,
                            mapLsmResponse.mlpNci, mapLsmResponse.mlpNrTac,
                            mapLsmResponse.mlpMmeName, mapLsmResponse.mlpMmeRealm, mapLsmResponse.mlpSgsnName, mapLsmResponse.mlpSgsnRealm,
                            mapLsmResponse.mlpSgsnNumber, mapLsmResponse.mlpAmfAddress,
                            mapLsmResponse.mlpMscNo, mapLsmResponse.mlpVlrNo, mapLsmResponse.mlpMsisdn,
                            mapLsmResponse.mlpImei, mapLsmResponse.mlpImsi, mapLsmResponse.mlpAge, mapLsmResponse.mlpLmsi, mapLsmResponse.mlpTransId,
                            mapLsmResponse.mlpLcsRefNumber, mapLsmResponse.mlpRatType, mapLsmResponse.mlpCivicAddress,
                            mlpResultType, mlpTriggeredReportingService, false);

                    } else if (MLPResponse.isSystemError(mlpResultType)) {
                        svcResultXml = mlpResponse.getSystemErrorResponseXML(mlpResultType, mlpClientErrorMessage, mlpTriggeredReportingService);
                    } else {
                        svcResultXml = mlpResponse.getPositionErrorResponseXML(sriLcs.getPslMsisdn(), sriLcs.getPslImsi(), pslImei,
                            mlpResultType, mlpClientErrorMessage, mlpTriggeredReportingService,
                            sriLcs.getPslLcsReferenceNumber(), sriLcs.getPslReferenceNumber());
                    }

                    this.sendHTTPResult(HttpServletResponse.SC_OK, svcResultXml);
                    break;
            }
        } catch (Exception e) {
            logger.severe("Exception on handleLsmLocationResponse : ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType         OK or error type to return to client
     * @param ria                   SRIForLCS response on location attempt
     * @param pla                   PSL response on location attempt
     * @param mlpClientErrorMessage Error message to send to the client
     */
    protected void handleLTELocationServicesResponse(MLPResponse.MLPResultType mlpResultType, RequestValuesForPLR rirPlr, SLhRiaAvpValues ria,
                                                     SLgPlaAvpValues pla, String mlpClientErrorMessage, Boolean mlpTriggeredReportingService) {
        try {
            int statusCode = HttpServletResponse.SC_OK;
            long resultCode;
            if (ria.getResultCode() != null) {
                resultCode = ria.getResultCode();
                statusCode = DiameterBaseError.getHttpServletResponseEquivalentCode(resultCode);
            } else if (ria.getExperimentalResultAvp() != null) {
                resultCode = ria.getExperimentalResultAvp().getExperimentalResultCode();
                statusCode = SLhSpecificErrors.getHttpServletResponseEquivalentCode(resultCode);
            }
            if (pla.getResultCode() != null) {
                resultCode = pla.getResultCode();
                statusCode = DiameterBaseError.getHttpServletResponseEquivalentCode(resultCode);
            } else if (pla.getExperimentalResultAvp() != null) {
                resultCode = pla.getExperimentalResultAvp().getExperimentalResultCode();
                statusCode = SLgSpecificErrors.getHttpServletResponseEquivalentCode(resultCode);
            }
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            switch (request.type) {
                case REST:
                    if (mlpResultType != null && httpEventContext != null) {
                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(statusCode);

                        String jsonResponse = "";
                        try {
                            jsonResponse = PlrResponseJsonBuilder.buildJsonResponseForPlr(ria, pla, rirPlr.plrMsisdn, rirPlr.plrUserName, rirPlr.plrImei,
                                rirPlr.plrFlags, rirPlr.plrLcsReferenceNumber, ria.lteLcsReferenceNumber, mlpClientErrorMessage);
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                        this.sendHTTPResult(statusCode, jsonResponse);
                        break;

                    } else {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                    }
                    break;

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);

                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        DiameterLcsResponseHelperForMLP slgLcsHelper = new DiameterLcsResponseHelperForMLP();
                        String plrMsisdn = null, plrImsi = null;
                        Integer plrClientTransId = null, plrLcsRefNum = null;
                        if (rirPlr != null) {
                            plrMsisdn = rirPlr.plrMsisdn;
                            plrImsi = rirPlr.plrUserName;
                            slgLcsHelper.setLcsReferenceNumber(ria.lteLcsReferenceNumber);
                            plrLcsRefNum = slgLcsHelper.getLcsReferenceNumber();
                            plrClientTransId = rirPlr.plrLcsReferenceNumber;
                        }
                        slgLcsHelper.handleRirAnswerValues(ria, plrMsisdn, plrImsi);
                        slgLcsHelper.handlePlrAnswerValues(pla);
                        MLPResponseParams slgLcsResponse = new MLPResponseParams();
                        slgLcsResponse.mlpMsisdn = slgLcsHelper.getMsisdn();
                        slgLcsResponse.mlpImsi = slgLcsHelper.getImsi();
                        slgLcsResponse.x = slgLcsHelper.getLatitude();
                        slgLcsResponse.y = slgLcsHelper.getLongitude();
                        slgLcsResponse.mlpTypeOfShape = slgLcsHelper.getTypeOfShape();
                        slgLcsResponse.radius = slgLcsHelper.getRadius();
                        slgLcsResponse.mlpUncertaintySemiMajorAxis = slgLcsHelper.getUncertaintySemiMajorAxis();
                        slgLcsResponse.mlpUncertaintySemiMinorAxis = slgLcsHelper.getUncertaintySemiMinorAxis();
                        slgLcsResponse.mlpAngleOfMajorAxis = slgLcsHelper.getAngleOfMajorAxis();
                        slgLcsResponse.mlpOffsetAngle = slgLcsHelper.getOffsetAngle();
                        slgLcsResponse.mlpIncludedAngle = slgLcsHelper.getIncludedAngle();
                        slgLcsResponse.mlpAltitude = slgLcsHelper.getAltitude();
                        slgLcsResponse.mlpUncertaintyAltitude = slgLcsHelper.getUncertaintyAltitude();
                        slgLcsResponse.mlpPolygon = slgLcsHelper.getPolygon();
                        slgLcsResponse.mlpNumberOfPoints = slgLcsHelper.getNumberOfPoints();
                        slgLcsResponse.mlpPositioningMethod = slgLcsHelper.getPositioningMethod();
                        slgLcsResponse.mlpMcc = slgLcsHelper.getMcc();
                        slgLcsResponse.mlpMnc = slgLcsHelper.getMnc();
                        slgLcsResponse.mlpLac = slgLcsHelper.getLac();
                        slgLcsResponse.mlpCi = slgLcsHelper.getCi();
                        slgLcsResponse.mlpSac = slgLcsHelper.getSac();
                        slgLcsResponse.mlpUci = null;
                        slgLcsResponse.mlpEci = slgLcsHelper.getEci();
                        slgLcsResponse.mlpEnbId = slgLcsHelper.getENBId();
                        slgLcsResponse.mlpRac = null;
                        slgLcsResponse.mlpTac = null;
                        slgLcsResponse.mlpNci = null;
                        slgLcsResponse.mlpNrTac = null;
                        slgLcsResponse.mlpMmeName = slgLcsHelper.getMmeName();
                        slgLcsResponse.mlpMmeRealm = slgLcsHelper.getMmeRealm();
                        slgLcsResponse.mlpSgsnName = slgLcsHelper.getSgsnName();
                        slgLcsResponse.mlpSgsnRealm = slgLcsHelper.getSgsnRealm();
                        slgLcsResponse.mlpAmfAddress = null;
                        slgLcsResponse.mlpMscNo = slgLcsHelper.getMscNumber();
                        slgLcsResponse.mlpVlrNo = slgLcsHelper.getVlrNumber();
                        slgLcsResponse.mlpImei = slgLcsHelper.getImei();
                        slgLcsResponse.mlpLmsi = slgLcsHelper.getLmsi();
                        if (slgLcsHelper.getAgeOfLocationEstimate() != null)
                            slgLcsResponse.mlpAge = (int) (long) slgLcsHelper.getAgeOfLocationEstimate();
                        slgLcsResponse.mlpTargetHorizontalSpeed = slgLcsHelper.getHorizontalSpeed();
                        slgLcsResponse.mlpTargetVerticalSpeed = slgLcsHelper.getVerticalSpeed();
                        slgLcsResponse.mlpVelocityType = slgLcsHelper.getVelocityType();
                        slgLcsResponse.mlpUncertaintyHorizontalSpeed = slgLcsHelper.getUncertaintyHorizontalSpeed();
                        slgLcsResponse.mlpUncertaintyVerticalSpeed = slgLcsHelper.getUncertaintyVerticalSpeed();
                        slgLcsResponse.mlpBearing = slgLcsHelper.getBearing();
                        slgLcsResponse.mlpCivicAddress = slgLcsHelper.getCivicAddress();
                        slgLcsResponse.mlpBarometricPressure = slgLcsHelper.getBarometricPressure();
                        slgLcsResponse.mlpTransId = plrClientTransId;
                        slgLcsResponse.mlpLcsRefNumber = plrLcsRefNum;
                        slgLcsResponse.mlpRatType = null;
                        slgLcsResponse.mlpCivicAddress = slgLcsHelper.getCivicAddress();

                        svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML("PLR", slgLcsResponse.mlpTypeOfShape,
                            slgLcsResponse.x, slgLcsResponse.y, slgLcsResponse.radius, slgLcsResponse.mlpUncertaintySemiMajorAxis,
                            slgLcsResponse.mlpUncertaintySemiMinorAxis, slgLcsResponse.mlpAngleOfMajorAxis, slgLcsResponse.mlpOffsetAngle,
                            slgLcsResponse.mlpIncludedAngle, slgLcsResponse.mlpAltitude, slgLcsResponse.mlpUncertaintyAltitude,
                            slgLcsResponse.mlpPolygon, slgLcsResponse.mlpNumberOfPoints,
                            slgLcsResponse.mlpPositioningMethod,
                            slgLcsResponse.mlpTargetHorizontalSpeed, slgLcsResponse.mlpBearing, slgLcsResponse.mlpTargetVerticalSpeed,
                            slgLcsResponse.mlpUncertaintyHorizontalSpeed, slgLcsResponse.mlpUncertaintyVerticalSpeed, slgLcsResponse.mlpVelocityType,
                            slgLcsResponse.mlpMcc, slgLcsResponse.mlpMnc, slgLcsResponse.mlpLac, slgLcsResponse.mlpCi, slgLcsResponse.mlpSac,
                            slgLcsResponse.mlpUci, slgLcsResponse.mlpEci, slgLcsResponse.mlpEnbId, slgLcsResponse.mlpRac, slgLcsResponse.mlpTac,
                            slgLcsResponse.mlpNci, slgLcsResponse.mlpNrTac,
                            slgLcsResponse.mlpMmeName, slgLcsResponse.mlpMmeRealm, slgLcsResponse.mlpSgsnName, slgLcsResponse.mlpSgsnRealm,
                            slgLcsResponse.mlpSgsnNumber, slgLcsResponse.mlpAmfAddress, slgLcsResponse.mlpMscNo, slgLcsResponse.mlpVlrNo,
                            slgLcsResponse.mlpMsisdn, slgLcsResponse.mlpImei, slgLcsResponse.mlpImsi,
                            slgLcsResponse.mlpAge, slgLcsResponse.mlpLmsi, slgLcsResponse.mlpTransId, slgLcsResponse.mlpLcsRefNumber,
                            slgLcsResponse.mlpRatType, slgLcsResponse.mlpCivicAddress, mlpResultType, mlpTriggeredReportingService, false);

                    } else if (MLPResponse.isSystemError(mlpResultType)) {
                        svcResultXml = mlpResponse.getSystemErrorResponseXML(mlpResultType, mlpClientErrorMessage, mlpTriggeredReportingService);
                        statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                    } else {
                        svcResultXml = mlpResponse.getPositionErrorResponseXML(rirPlr.plrMsisdn, rirPlr.plrUserName, rirPlr.plrImei,
                            mlpResultType, mlpClientErrorMessage, mlpTriggeredReportingService, rirPlr.plrLcsReferenceNumber, ria.lteLcsReferenceNumber);
                    }

                    this.sendHTTPResult(statusCode, svcResultXml);
                    break;
            }

        } catch (Exception e) {
            logger.severe("Exception on handleLTELocationServicesResponse: ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType         OK or error type to return to client
     * @param uda                   UDA parameters on Sh User-Data-Request attempt
     * @param mlpClientErrorMessage Error message to send to the client
     */
    protected void handleShUserDataRequestResponse(MLPResponse.MLPResultType mlpResultType, ShUdaAvpValues uda, String mlpClientErrorMessage) {

        try {
            int statusCode = HttpServletResponse.SC_OK;
            long resultCode;
            if (uda.getResultCode() != null) {
                resultCode = uda.getResultCode();
                statusCode = DiameterBaseError.getHttpServletResponseEquivalentCode(resultCode);
            } else if (uda.getExperimentalResultAvp() != null) {
                resultCode = uda.getExperimentalResultAvp().getExperimentalResultCode();
                statusCode = ShSpecificErrors.getHttpServletResponseEquivalentCode(resultCode);
            }
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            switch (request.type) {
                case REST:
                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(statusCode);

                        String jsonResponse = "";
                        try {
                            jsonResponse = UdrResponseJsonBuilder.buildJsonResponseForUdr(uda);
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                        this.sendHTTPResult(statusCode, jsonResponse);
                        break;

                    } else {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                    }
                    break;

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);
                    DiameterShUdrResponseHelperForMLP shUdrHelper = new DiameterShUdrResponseHelperForMLP();
                    shUdrHelper.handleUdrAnswerValues(uda);

                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        MLPResponseParams udrResponse = new MLPResponseParams();
                        udrResponse.mlpTypeOfShape = shUdrHelper.getTypeOfShape();
                        udrResponse.x = shUdrHelper.getLatitude();
                        udrResponse.y = shUdrHelper.getLongitude();
                        udrResponse.radius = shUdrHelper.getRadius();
                        udrResponse.mlpMcc = shUdrHelper.getMcc();
                        udrResponse.mlpMnc = shUdrHelper.getMnc();
                        udrResponse.mlpLac = shUdrHelper.getLac();
                        udrResponse.mlpCi = shUdrHelper.getCi();
                        udrResponse.mlpSac = shUdrHelper.getSac();
                        udrResponse.mlpUci = null;
                        udrResponse.mlpEci = shUdrHelper.getEci();
                        udrResponse.mlpEnbId = shUdrHelper.getEnbId();
                        udrResponse.mlpRac = shUdrHelper.getRac();
                        udrResponse.mlpTac = shUdrHelper.getTac();
                        udrResponse.mlpNci = shUdrHelper.getNci();
                        udrResponse.mlpNrTac = null;
                        udrResponse.mlpMmeName = shUdrHelper.getMmeName();
                        udrResponse.mlpMmeRealm = null;
                        udrResponse.mlpSgsnName = null;
                        udrResponse.mlpSgsnNumber = shUdrHelper.getSgsnNumber();
                        udrResponse.mlpAmfAddress = shUdrHelper.getAmfAddress();
                        udrResponse.mlpMscNo = shUdrHelper.getMscNumber();
                        udrResponse.mlpVlrNo = shUdrHelper.getVlrNumber();
                        udrResponse.mlpMsisdn = shUdrHelper.getMsisdn();
                        udrResponse.mlpImei = shUdrHelper.getImei();
                        udrResponse.mlpImsi = null;
                        udrResponse.mlpAge = shUdrHelper.getAgeOfLocationInfo();
                        udrResponse.mlpLmsi = null;
                        udrResponse.mlpRatType = shUdrHelper.getRatType();
                        udrResponse.mlpCivicAddress = null;

                        svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML("UDR", udrResponse.mlpTypeOfShape, udrResponse.x, udrResponse.y,
                            udrResponse.radius, null, null, null, null, null,
                            null, null, null, null, null,
                            null, null, null, null, null, null,
                            udrResponse.mlpMcc, udrResponse.mlpMnc, udrResponse.mlpLac, udrResponse.mlpCi, udrResponse.mlpSac, udrResponse.mlpUci,
                            udrResponse.mlpEci, udrResponse.mlpEnbId, udrResponse.mlpRac, udrResponse.mlpTac, udrResponse.mlpNci, udrResponse.mlpNrTac,
                            udrResponse.mlpMmeName, udrResponse.mlpMmeRealm, udrResponse.mlpSgsnName, udrResponse.mlpSgsnRealm, udrResponse.mlpSgsnNumber,
                            udrResponse.mlpAmfAddress, udrResponse.mlpMscNo, udrResponse.mlpVlrNo,
                            udrResponse.mlpMsisdn, udrResponse.mlpImei, udrResponse.mlpImsi,
                            udrResponse.mlpAge, udrResponse.mlpLmsi, null, null, udrResponse.mlpRatType, udrResponse.mlpCivicAddress,
                            mlpResultType, false, false);

                    } else if (MLPResponse.isSystemError(mlpResultType)) {
                        svcResultXml = mlpResponse.getSystemErrorResponseXML(mlpResultType, mlpClientErrorMessage, false);
                    } else {
                        String msisdn = uda.getMsisdn();
                        svcResultXml = mlpResponse.getPositionErrorResponseXML(msisdn, null, shUdrHelper.getImei(),
                            mlpResultType, mlpClientErrorMessage, false, null, null);
                    }

                    this.sendHTTPResult(statusCode, svcResultXml);
                    break;
            }

        } catch (Exception e) {
            logger.severe("Exception on handleShUserDataRequestResponse: ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType           OK or error type to return to client
     * @param sriResponseValues       SRI response values
     * @param sriSmResponseParams     SRISM response values
     * @param sriLcsResponseParams    SRILCS response values
     * @param operation               MAP operation involved (SRI, SRISM or SRILCS)
     * @param msisdn                  MSISDN used on the operation attempt
     * @param imsi                    IMSI used on the operation attempt
     * @param mlpClientErrorMessage   Error message to send to the client
     */
    protected void handleSriResponseValue(MLPResponse.MLPResultType mlpResultType, SriResponseValues sriResponseValues,
                                          SriSmResponseParams sriSmResponseParams, SriLcsResponseParams sriLcsResponseParams,
                                          String operation, String msisdn, String imsi, String mlpClientErrorMessage) {

        try {
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            if (Objects.requireNonNull(request.type) == HttpRequestType.REST) {
                if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                    HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                    HttpServletResponse httpServletResponse = httpRequest.getResponse();
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    String jsonResponse = "";
                    try {
                        if (operation.equalsIgnoreCase("SRI"))
                            jsonResponse = SriResponseJsonBuilder.buildJsonResponseForSri(imsi, msisdn, "SRI", sriResponseValues, null, null);
                        if (operation.equalsIgnoreCase("SRISM"))
                            jsonResponse = SriResponseJsonBuilder.buildJsonResponseForSri(imsi, msisdn, "SRISM", null, sriSmResponseParams, null);
                        if (operation.equalsIgnoreCase("SRILCS"))
                            jsonResponse = SriResponseJsonBuilder.buildJsonResponseForSri(imsi, msisdn, "SRILCS", null, null, sriLcsResponseParams);
                    } catch (MAPException me) {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Fail MAP params\"}");
                        logger.severe(me.getMessage());
                    } catch (Exception e) {
                        logger.severe(e.getMessage());
                    }
                    this.sendHTTPResult(HttpServletResponse.SC_OK, jsonResponse);
                } else {
                    this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                }
            }
        } catch (Exception e) {
            logger.severe("Exception on handleSriResponseValue: ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType         OK or error type to return to client
     * @param psiResponseParams     PSI parameters on PSI attempt
     * @param mlpClientErrorMessage Error message to send to the client
     */
    protected void handlePsiResponse(MLPResponse.MLPResultType mlpResultType, PsiResponseParams psiResponseParams,
                                     String psiMsisdn, String mlpClientErrorMessage) {

        try {
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            String imsi = "";

            switch (request.type) {
                case REST:
                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

                        if (psiResponseParams != null) {
                            if (psiResponseParams.getPsiOnlyImsi() != null && psiResponseParams.getPsiOnlyNnn() != null) {
                                if (psiResponseParams.getPsiServiceType() != null) {
                                    if (psiResponseParams.getPsiServiceType().equalsIgnoreCase("psiFirst"))
                                        imsi = psiResponseParams.getPsiOnlyImsi();
                                }
                                logger.fine(String.format("psiOnlyImsi global value '%s' fixed to '%s' obtained from Transaction.", psiResponseParams.getPsiOnlyImsi(), imsi));
                            } else {
                                imsi = psiResponseParams.getSriForSMImsi();
                                logger.fine(String.format("sriForSMImsi global value '%s' fixed to '%s' obtained from Transaction.", psiResponseParams.getSriForSMImsi(), imsi));
                            }
                        }

                        String jsonResponse = "";
                        try {
                            if (psiResponseParams != null) {
                                if (psiResponseParams.getPsiOnlyImsi() != null && psiResponseParams.getPsiOnlyNnn() != null &&
                                        psiResponseParams.getPsiServiceType() != null && psiResponseParams.getPsiServiceType().equalsIgnoreCase("psiFirst")) {
                                    jsonResponse = PsiResponseJsonBuilder.buildJsonResponseForPsi(psiResponseParams, imsi, psiMsisdn, null);
                                } else if (psiResponseParams.getSriForSmResponse() != null) {
                                    SriSmResponseParams sriSmResponse = psiResponseParams.getSriForSmResponse();
                                    LMSI lmsi = null;
                                    if (sriSmResponse.getLocationInfoWithLMSI() != null)
                                        lmsi = sriSmResponse.getLocationInfoWithLMSI().getLMSI();
                                    jsonResponse = PsiResponseJsonBuilder.buildJsonResponseForPsi(psiResponseParams, imsi, psiMsisdn, lmsi);
                                } else if (psiResponseParams.getSriResponse() != null) {
                                    SriResponseValues sriResponse = psiResponseParams.getSriResponse();
                                    imsi = sriResponse.getImsi().getData();
                                    jsonResponse = PsiResponseJsonBuilder.buildJsonResponseForPsi(psiResponseParams, imsi, psiMsisdn, null);
                                }
                            }
                        } catch (MAPException me) {
                            this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Fail MAP params\"}");
                            logger.severe(me.getMessage());
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                        this.sendHTTPResult(HttpServletResponse.SC_OK, jsonResponse);
                        break;

                    } else {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                    }
                    break;

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);
                    MapSriPsiResponseHelperForMLP psiHelper = new MapSriPsiResponseHelperForMLP();
                    psiHelper.handlePsiResponseValues(psiResponseParams);
                    try {
                        if (psiResponseParams.getPsiOnlyImsi() != null) {
                            imsi = psiResponseParams.getPsiOnlyImsi();
                        } else if (psiResponseParams.getSriForSmResponse() != null) {
                            SriSmResponseParams sriSmResponse = psiResponseParams.getSriForSmResponse();
                            if (sriSmResponse.getImsi() != null)
                                imsi = sriSmResponse.getImsi().getData();
                        } else if (psiResponseParams.getSriResponse() != null) {
                            SriResponseValues sriResponse = psiResponseParams.getSriResponse();
                            if (sriResponse.getImsi() != null)
                                imsi = sriResponse.getImsi().getData();
                        }
                    } catch (Exception e) {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                        logger.severe(e.getMessage());
                    }

                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        MLPResponseParams psiResponse = new MLPResponseParams();
                        psiResponse.mlpTypeOfShape = psiHelper.getTypeOfShape();
                        psiResponse.x = psiHelper.getLatitude();
                        psiResponse.y = psiHelper.getLongitude();
                        psiResponse.radius = psiHelper.getRadius();
                        psiResponse.mlpMcc = psiHelper.getMcc();
                        psiResponse.mlpMnc = psiHelper.getMnc();
                        psiResponse.mlpLac = psiHelper.getLac();
                        psiResponse.mlpCi = psiHelper.getCi();
                        psiResponse.mlpSac = psiHelper.getSac();
                        psiResponse.mlpUci = null;
                        psiResponse.mlpEci = psiHelper.getEci();
                        psiResponse.mlpEnbId = psiHelper.getEnbId();
                        psiResponse.mlpRac = psiHelper.getRac();
                        psiResponse.mlpTac = psiHelper.getTac();
                        psiResponse.mlpNci = psiHelper.getNci();
                        psiResponse.mlpNrTac = psiHelper.getNrTac();
                        psiResponse.mlpMmeName = psiHelper.getMmeName();
                        psiResponse.mlpMmeRealm = null;
                        psiResponse.mlpSgsnName = null;
                        psiResponse.mlpSgsnRealm = null;
                        psiResponse.mlpSgsnNumber = psiHelper.getSgsnNumber();
                        if (psiHelper.getAmfAddress() != null)
                            psiResponse.mlpAmfAddress = new String(psiHelper.getAmfAddress().getData(), StandardCharsets.UTF_8);
                        psiResponse.mlpMscNo = psiHelper.getMscNumber();
                        psiResponse.mlpVlrNo = psiHelper.getVlrNumber();
                        psiResponse.mlpMsisdn = psiMsisdn;
                        psiResponse.mlpImei = psiHelper.getImei();
                        psiResponse.mlpImsi = imsi;
                        psiResponse.mlpAge = psiHelper.getAgeOfLocationInfo();
                        psiResponse.mlpLmsi = null;
                        psiResponse.mlpRatType = psiHelper.getRatType();
                        psiResponse.mlpCivicAddress = null;

                        svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML("PSI", psiResponse.mlpTypeOfShape, psiResponse.x, psiResponse.y,
                            psiResponse.radius, null, null, null, null, null, null,
                            null, null, null, null,
                            null, null, null, null, null, null,
                            psiResponse.mlpMcc, psiResponse.mlpMnc, psiResponse.mlpLac, psiResponse.mlpCi, psiResponse.mlpSac, psiResponse.mlpUci,
                            psiResponse.mlpEci, psiResponse.mlpEnbId, psiResponse.mlpRac, psiResponse.mlpTac, psiResponse.mlpNci, psiResponse.mlpNrTac,
                            psiResponse.mlpMmeName, psiResponse.mlpMmeRealm, psiResponse.mlpSgsnName, psiResponse.mlpSgsnRealm, psiResponse.mlpSgsnNumber,
                            psiResponse.mlpAmfAddress, psiResponse.mlpMscNo, psiResponse.mlpVlrNo,
                            psiResponse.mlpMsisdn, psiResponse.mlpImei, psiResponse.mlpImsi,
                            psiResponse.mlpAge, psiResponse.mlpLmsi, null, null, psiResponse.mlpRatType, psiResponse.mlpCivicAddress,
                            mlpResultType, false, false);

                    } else if (MLPResponse.isSystemError(mlpResultType)) {
                        svcResultXml = mlpResponse.getSystemErrorResponseXML(mlpResultType, mlpClientErrorMessage, false);
                    } else {
                        String imei = null;

                        if (psiResponseParams.getImei() != null)
                            imei = psiResponseParams.getImei().getIMEI();

                        svcResultXml = mlpResponse.getPositionErrorResponseXML(psiMsisdn, imsi, imei,
                            mlpResultType, mlpClientErrorMessage, false, null, null);
                    }

                    this.sendHTTPResult(HttpServletResponse.SC_OK, svcResultXml);
                    break;
            }
        } catch (Exception e) {
            logger.severe("Exception on handlePsiResponse: ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response out of the SUPL session
     *
     * @param mlpResultType                 OK or error type to return to client
     * @param mlpClientErrorMessage         Error message to send to the client
     * @param msisdn                        MSISDN of the SET
     * @param imsi                          IMSI of the SET
     * @param transId                       Transaction id of the location attempt sent by the SLP Client
     * @param suplResponseHelperForMLP      SUPL response instance needed for MLP request answer
     * @param mlpTriggeredReportingService  indicates if the location report has been triggered via MLP
     * @param isReport                      indicates if the SUPL response constitutes a location report
     */
    protected void handleSUPLResponse(MLPResponse.MLPResultType mlpResultType, String mlpClientErrorMessage, String msisdn,
                                      String imsi, Integer transId, SuplResponseHelperForMLP suplResponseHelperForMLP,
                                      Boolean mlpTriggeredReportingService, Boolean isReport) {

        try {
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();

            switch (request.type) {
                case REST:
                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

                        String jsonResponse = "";
                        try {
                            logger.info("On handleSUPLResponse, REST not implemented yet");
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                        this.sendHTTPResult(HttpServletResponse.SC_OK, jsonResponse);
                        break;

                    } else {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mlpClientErrorMessage);
                    }
                    break;

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);

                    if (mlpResultType == MLPResponse.MLPResultType.OK && httpEventContext != null) {
                        MLPResponseParams suplResponse = new MLPResponseParams();
                        suplResponse.mlpMsisdn = msisdn;
                        suplResponse.mlpImsi = imsi;
                        suplResponse.x = suplResponseHelperForMLP.getLatitude();
                        suplResponse.y = suplResponseHelperForMLP.getLongitude();
                        if (suplResponseHelperForMLP.getTypeOfShape() != null)
                            suplResponse.mlpTypeOfShape = suplResponseHelperForMLP.getTypeOfShape().name();
                        suplResponse.radius = suplResponseHelperForMLP.getRadius();
                        suplResponse.mlpUncertaintySemiMajorAxis = suplResponseHelperForMLP.getUncertaintySemiMajorAxis();
                        suplResponse.mlpUncertaintySemiMinorAxis = suplResponseHelperForMLP.getUncertaintySemiMinorAxis();
                        suplResponse.mlpAngleOfMajorAxis = suplResponseHelperForMLP.getOrientationMajorAxis();
                        suplResponse.mlpOffsetAngle = suplResponseHelperForMLP.getOffsetAngle();
                        suplResponse.mlpIncludedAngle = suplResponseHelperForMLP.getIncludedAngle();
                        suplResponse.mlpAltitude = suplResponseHelperForMLP.getAltitude();
                        suplResponse.mlpUncertaintyAltitude = suplResponseHelperForMLP.getUncertaintyAltitude();
                        suplResponse.mlpPolygon = null;
                        suplResponse.mlpNumberOfPoints = null;
                        suplResponse.mlpPositioningMethod = null; // FIXME
                        if (suplResponseHelperForMLP.getPosMethod().getAsn1TypeName() != null)
                            suplResponse.mlpPositioningMethod = suplResponseHelperForMLP.getPosMethod().getAsn1TypeName();
                        suplResponse.mlpMcc = suplResponseHelperForMLP.getMcc();
                        suplResponse.mlpMnc = suplResponseHelperForMLP.getMnc();
                        suplResponse.mlpLac = suplResponseHelperForMLP.getLac();
                        suplResponse.mlpCi = suplResponseHelperForMLP.getCi();
                        suplResponse.mlpSac = suplResponseHelperForMLP.getSac();
                        suplResponse.mlpUci = suplResponseHelperForMLP.getUci();
                        suplResponse.mlpEci = suplResponseHelperForMLP.getEci();
                        suplResponse.mlpEnbId = suplResponseHelperForMLP.getEnbId();
                        suplResponse.mlpRac = suplResponseHelperForMLP.getRac();
                        suplResponse.mlpTac = suplResponseHelperForMLP.getTac();
                        suplResponse.mlpNci = suplResponseHelperForMLP.getNrCi();
                        suplResponse.mlpNrTac = null;
                        suplResponse.mlpMmeName = null;
                        suplResponse.mlpMmeRealm = null;
                        suplResponse.mlpSgsnName = null;
                        suplResponse.mlpSgsnRealm = null;
                        suplResponse.mlpSgsnNumber = null;
                        suplResponse.mlpAmfAddress = null;
                        suplResponse.mlpMscNo = null;
                        suplResponse.mlpVlrNo = null;
                        suplResponse.mlpImei = null;
                        suplResponse.mlpLmsi = null;
                        suplResponse.mlpAge = null;
                        suplResponse.mlpTargetHorizontalSpeed = null;
                        suplResponse.mlpTargetVerticalSpeed = null;
                        suplResponse.mlpVelocityType = null;
                        suplResponse.mlpCivicAddress = null;
                        suplResponse.mlpBarometricPressure = null;
                        suplResponse.mlpTransId = transId;
                        if (suplResponseHelperForMLP.getTriggerParams() != null)
                            suplResponse.mlpLcsRefNumber = transId;
                        else
                            suplResponse.mlpLcsRefNumber = null;
                        suplResponse.mlpRatType = null;

                        svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML("SUPL", suplResponse.mlpTypeOfShape,
                            suplResponse.x, suplResponse.y, suplResponse.radius, suplResponse.mlpUncertaintySemiMajorAxis,
                            suplResponse.mlpUncertaintySemiMinorAxis, suplResponse.mlpAngleOfMajorAxis, suplResponse.mlpOffsetAngle,
                            suplResponse.mlpIncludedAngle, suplResponse.mlpAltitude, suplResponse.mlpUncertaintyAltitude,
                            suplResponse.mlpPolygon, suplResponse.mlpNumberOfPoints,
                            suplResponse.mlpPositioningMethod,
                            suplResponse.mlpTargetHorizontalSpeed, suplResponse.mlpBearing, suplResponse.mlpTargetVerticalSpeed,
                            suplResponse.mlpUncertaintyHorizontalSpeed, suplResponse.mlpUncertaintyVerticalSpeed, suplResponse.mlpVelocityType,
                            suplResponse.mlpMcc, suplResponse.mlpMnc, suplResponse.mlpLac, suplResponse.mlpCi, suplResponse.mlpSac, suplResponse.mlpUci,
                            suplResponse.mlpEci, suplResponse.mlpEnbId, suplResponse.mlpRac, suplResponse.mlpTac, suplResponse.mlpNci, suplResponse.mlpNrTac,
                            suplResponse.mlpMmeName, suplResponse.mlpMmeRealm, suplResponse.mlpSgsnName, suplResponse.mlpSgsnRealm,
                            suplResponse.mlpSgsnNumber, suplResponse.mlpAmfAddress, suplResponse.mlpMscNo, suplResponse.mlpVlrNo,
                            suplResponse.mlpMsisdn, suplResponse.mlpImei, suplResponse.mlpImsi,
                            suplResponse.mlpAge, suplResponse.mlpLmsi, suplResponse.mlpTransId, suplResponse.mlpLcsRefNumber,
                            suplResponse.mlpRatType, suplResponse.mlpCivicAddress, mlpResultType, mlpTriggeredReportingService, isReport);

                    } else if (MLPResponse.isSystemError(mlpResultType)) {
                        svcResultXml = mlpResponse.getSystemErrorResponseXML(mlpResultType, mlpClientErrorMessage, mlpTriggeredReportingService);
                    } else {
                        svcResultXml = mlpResponse.getPositionErrorResponseXML(msisdn, imsi, null,
                            mlpResultType, mlpClientErrorMessage, mlpTriggeredReportingService, transId, null);
                    }

                    this.sendHTTPResult(HttpServletResponse.SC_OK, svcResultXml);
                    break;
            }
        } catch (Exception e) {
            logger.severe("Exception on handleSUPLResponse: ", e);
        }
    }

    /**
     * Handle generating the appropriate HTTP response
     * We're making use of the MLPResponse class for both GET/POST requests for convenience and
     * because eventually the GET method will likely be removed
     *
     * @param mlpResultType                OK or error type to return to the GMLC client
     * @param dialogErrorMessage           Error message to send to the GMLC client
     * @param operation                    Location operation that caused the error to report to the GMLC client
     * @param targetMsisdn                 MSISDN of the target subscriber
     * @param targetImsi                   IMSI of the target subscriber
     * @param targetImei                   IMEI of the target subscriber
     * @param refNumber                    Location Services reference number of the operation that caused the error to report to the GMLC client
     * @param networkNodeNumber            Network Node Number to where the failed operation was conveyed
     * @param addNetworkNodeNumber         Network Node Number to where the failed operation was conveyed (additional)
     * @param diameterHost                 Diameter Host name to where the failed operation was conveyed
     * @param diameterRealm                Diameter Realm name to where the failed operation was conveyed
     * @param mlpTriggeredReportingService indicates if the location report has been triggered via MLP
     */
    protected void reportLocationRequestError(MLPResponse.MLPResultType mlpResultType, String dialogErrorMessage, String operation,
                                              String targetMsisdn, String targetImsi, String targetImei, Integer refNumber,
                                              String networkNodeNumber, String addNetworkNodeNumber, String diameterHost, String diameterRealm,
                                              Boolean mlpTriggeredReportingService) {
        try {
            HttpRequest request = getHttpRequest();
            EventContext httpEventContext = this.resumeHttpEventContext();
            String jsonResponse;

            HttpRequestType httpRequestType;
            if (request == null) {
                httpRequestType = HttpRequestType.MLP;
            } else {
                httpRequestType = request.type;
            }

            switch (httpRequestType) {
                case REST:
                    if (httpEventContext != null) {
                        HttpServletRequestEvent httpRequest = (HttpServletRequestEvent) httpEventContext.getEvent();
                        HttpServletResponse httpServletResponse = httpRequest.getResponse();
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                        jsonResponse = OnErrorResponseJsonBuilder.buildJsonResponseOnError(targetMsisdn, targetImsi, targetImei, operation, dialogErrorMessage, refNumber,
                            networkNodeNumber, addNetworkNodeNumber, diameterHost, diameterRealm);
                        this.sendHTTPResult(setHttpServletResponseStatusCode(mlpResultType), jsonResponse);
                    } else {
                        this.sendHTTPResult(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
                    }
                    break;

                case MLP:
                    String svcResultXml;
                    MLPResponse mlpResponse = new MLPResponse(this.logger);
                    svcResultXml = mlpResponse.getPositionErrorResponseXML(targetMsisdn, targetImsi, targetImei,
                        mlpResultType, dialogErrorMessage, mlpTriggeredReportingService, refNumber, null);
                    this.sendHTTPResult(setHttpServletResponseStatusCode(mlpResultType), svcResultXml);
                    break;
            }
        } catch (Exception e) {
            logger.severe("Exception on reportLocationRequestError: ", e);
        }
    }

    /**
     * Return the specified response data to the HTTP client
     *
     * @param statusCode   HTTP status code to be included
     * @param responseData Response data to send to client
     */
    protected void sendHTTPResult(int statusCode, String responseData) {
        try {
            EventContext ctx = this.getEventContext();
            if (ctx == null) {
                logger.warning("When responding to HTTP no pending HTTP request is found, responseData=" + responseData);
                return;
            }

            HttpServletRequestEvent event = (HttpServletRequestEvent) ctx.getEvent();
            HttpServletResponse response = event.getResponse();
            response.setStatus(statusCode);
            if (Objects.requireNonNull(getHttpRequest().type) == HttpRequestType.MLP) {
                response.addHeader("Content-Type", "application/xml; utf-8");
            } else {
                response.addHeader("Content-Type", "application/json; utf-8");
            }
            PrintWriter w;
            w = response.getWriter();
            w.print(responseData);
            w.flush();
            response.flushBuffer();

            if (ctx.isSuspended()) {
                ctx.resumeDelivery();
            }

            if (logger.isFineEnabled()) {
                logger.fine("HTTP Request received and response sent, responseData=" + responseData);
            }

            // getNullActivity().endActivity();
        } catch (Exception e) {
            logger.severe("Error while sending back HTTP response", e);
        }
    }

    /**
     * Resume the HTTP Event Context
     */
    private EventContext resumeHttpEventContext() {
        EventContext httpEventContext = getEventContextCMP();

        if (httpEventContext == null) {
            logger.severe("No HTTP event context, can not resume ");
            return null;
        }

        httpEventContext.resumeDelivery();
        return httpEventContext;
    }

    private HttpReport getHttpSubscriberLocationReport() {
        String mongoHost = gmlcPropertiesManagement.getMongoHost();
        if (mongoHost != null && !mongoHost.isEmpty()) {
            Integer mongoPort = gmlcPropertiesManagement.getMongoPort();
            String mongoDatabase = gmlcPropertiesManagement.getMongoDatabase();
            httpSubscriberLocationReport = new HttpReport(mongoHost, mongoPort, mongoDatabase);
        } else {
            httpSubscriberLocationReport = new HttpReport();
        }
        return httpSubscriberLocationReport;
    }

    // //////////////////
    // SBB LO methods //
    // ////////////////

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.gmlc.slee.cdr.CDRInterfaceParent#
     * recordGenerationSucceeded(org.mobicents.gmlc.slee.cdr.CDRInterfaceParent.RecordType)
     */
    @Override
    public void recordGenerationSucceeded() {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("Generated CDR for Status: " + getCDRInterface().getState());
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.gmlc.slee.cdr.CDRInterfaceParent#
     * recordGenerationFailed(java.lang.String)
     */
    @Override
    public void recordGenerationFailed(String message) {
        if (this.logger.isSevereEnabled()) {
            this.logger.severe("Failed to generate CDR! Message: '" + message + "'");
            this.logger.severe("Status: " + getCDRInterface().getState());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.gmlc.slee.cdr.CDRInterfaceParent#
     * recordGenerationFailed(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void recordGenerationFailed(String message, Throwable t) {
        if (this.logger.isSevereEnabled()) {
            this.logger.severe("Failed to generate CDR! Message: '" + message + "'", t);
            this.logger.severe("Status: " + getCDRInterface().getState());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mobicents.gmlc.slee.cdr.CDRInterfaceParent#initFailed(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void initFailed(String message, Throwable t) {
        if (this.logger.isSevereEnabled()) {
            this.logger.severe("Failed to initialize CDR Database! Message: '" + message + "'", t);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.mobicents.gmlc.slee.cdr.CDRInterfaceParent#initSuccessed()
     */
    @Override
    public void initSucceeded() {
        if (this.logger.isFineEnabled()) {
            this.logger.fine("CDR Database has been initialized!");
        }

    }

    //////////////////////
    //  CDR interface  //
    ////////////////////

    protected static final String CDR = "CDR";

    public abstract ChildRelationExt getCDRPlainInterfaceChildRelation();

    public CDRInterface getCDRInterface() {
        GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();
        ChildRelationExt childExt;
        if (gmlcPropertiesManagement.getCdrLoggingTo() == GmlcPropertiesManagement.CdrLoggedType.Textfile) {
            childExt = getCDRPlainInterfaceChildRelation();
        } else {
            //childExt = getCDRInterfaceChildRelation();
            childExt = null; // temporary
        }

      assert childExt != null;
      CDRInterface child = (CDRInterface) childExt.get(CDR);
        if (child == null) {
            try {
                child = (CDRInterface) childExt.create(CDR);
            } catch (TransactionRequiredLocalException e) {
                logger.severe("TransactionRequiredLocalException when creating CDR child", e);
            } catch (IllegalArgumentException e) {
                logger.severe("IllegalArgumentException when creating CDR child", e);
            } catch (NullPointerException e) {
                logger.severe("NullPointerException when creating CDR child", e);
            } catch (SLEEException e) {
                logger.severe("SLEEException when creating CDR child", e);
            } catch (CreateException e) {
                logger.severe("CreateException when creating CDR child", e);
            }
        }

        return child;
    }

    protected void createCDRRecord(RecordStatus recordStatus) {
        try {
            this.getCDRInterface().createRecord(recordStatus);
        } catch (Exception e) {
            logger.severe("Error while trying to create CDR Record", e);
        }
    }

    // ///////////////////////////////////////////////
    // protected child stuff, to be used in parent //
    // /////////////////////////////////////////////

    protected void detachFromMAPDialogMobility(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        MAPDialogMobility mapDialogMobility = (MAPDialogMobility) aci.getActivity();
        mapDialogMobility.release();
    }

    protected void detachFromMAPDialogSms(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        MAPDialogSms mapDialogSms = (MAPDialogSms) aci.getActivity();
        mapDialogSms.release();
    }

    protected void detachFromMAPDialogCallHandling(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        MAPDialogCallHandling mapDialogCallHandling = (MAPDialogCallHandling) aci.getActivity();
        mapDialogCallHandling.release();
    }

    protected void detachFromMAPDialogLsm(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        MAPDialogLsm mapDialogLsm = (MAPDialogLsm) aci.getActivity();
        mapDialogLsm.release();
    }

    protected void detachFromSLhClientActivity(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        SLhClientSessionActivity slhClientSessionActivity = (SLhClientSessionActivity) aci.getActivity();
        slhClientSessionActivity.endActivity();
    }

    protected void detachFromSLgClientActivity(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        SLgClientSessionActivity slgClientSessionActivity = (SLgClientSessionActivity) aci.getActivity();
        slgClientSessionActivity.endActivity();
    }

    protected void detachFromShClientActivity(ActivityContextInterface aci) {
        aci.detach(this.sbbContext.getSbbLocalObject());
        ShClientActivity slhClientActivity = (ShClientActivity) aci.getActivity();
        slhClientActivity.endActivity();
    }

    protected void cancelTimer() {
        try {
            TimerID timerID = this.getTimerID();
            if (timerID != null) {
                this.timerFacility.cancelTimer(timerID);
            }
        } catch (Exception e) {
            logger.severe("Could not cancel Timer", e);
        }
    }

    protected void setTimer(ActivityContextInterface aci) {
        TimerOptions options = new TimerOptions();
        long waitingTime = gmlcPropertiesManagement.getEventContextSuspendDeliveryTimeout();
        options.setTimeout(waitingTime);
        // Set the timer on ACI
        TimerID timerID = this.timerFacility.setTimer(aci, null, System.currentTimeMillis() + waitingTime, options);
        this.setTimerID(timerID);
    }

    public void onTimerEvent(TimerEvent event, ActivityContextInterface aci) {
        // method for firing eventual location requests/reports on timer events
        if (logger.isFineEnabled()) {
            long expiryTime = event.getExpiryTime();
            if (aci != null) {
                logger.warning("\nonTimerEvent, expiry time=" + expiryTime + ". ACI=" + aci.hashCode());
            } else {
                logger.warning("\nonTimerEvent, expiry time=" + expiryTime);
            }
        }
        if (aci != null)
            aci.detach(this.sbbContext.getSbbLocalObject());
    }

    public Tracer getTracer(){
        return this.logger;
    }
}
