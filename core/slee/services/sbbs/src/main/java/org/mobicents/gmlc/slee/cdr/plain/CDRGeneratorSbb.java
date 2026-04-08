package org.mobicents.gmlc.slee.cdr.plain;

import com.google.common.collect.Multimap;
import net.java.slee.resource.diameter.slg.events.avp.LCSFormatIndicator;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import net.java.slee.resource.diameter.slg.events.avp.LocationEvent;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.mobicents.gmlc.slee.MobileCoreNetworkInterfaceSbb;
import org.mobicents.gmlc.slee.cdr.CDRInterface;
import org.mobicents.gmlc.slee.cdr.GMLCCDRState;
import org.mobicents.gmlc.slee.cdr.RecordStatus;
import org.mobicents.gmlc.slee.cdr.model.CDRModel;
import org.mobicents.gmlc.slee.cdr.tasks.TaskCDR;
import org.mobicents.gmlc.slee.cdr.tasks.TaskManager;
import org.mobicents.gmlc.slee.diameter.sh.LocalTimeZone;
import org.mobicents.gmlc.slee.primitives.EUTRANCGI;
import org.mobicents.gmlc.slee.primitives.EUTRANCGIImpl;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningData;
import org.mobicents.gmlc.slee.primitives.LocationInformation5GS;
import org.mobicents.gmlc.slee.primitives.Polygon;
import org.mobicents.gmlc.slee.primitives.RoutingAreaId;
import org.mobicents.gmlc.slee.primitives.RoutingAreaIdImpl;
import org.mobicents.gmlc.slee.primitives.TrackingAreaId;
import org.mobicents.gmlc.slee.primitives.TrackingAreaIdImpl;
import org.mobicents.gmlc.slee.primitives.CivicAddressElements;
import org.mobicents.gmlc.slee.primitives.CivicAddressXmlReader;
import org.restcomm.protocols.ss7.indicator.AddressIndicator;

import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.DiameterIdentity;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.primitives.IMEI;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;

import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSEvent;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoS;
import org.restcomm.protocols.ss7.map.api.service.lsm.DeferredmtlrData;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ServingNodeAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranAdditionalPositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranCivicAddress;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;
import org.restcomm.protocols.ss7.map.api.service.lsm.AccuracyFulfilmentIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.GeranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.PositioningDataInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranPositioningDataInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.AdditionalNumber;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientID;
import org.restcomm.protocols.ss7.map.api.service.lsm.ReportingPLMNList;

import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.GPRSMSClass;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationEPS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformationGPRS;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MNPInfoRes;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.MSClassmark2;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.PSSubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberInfo;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.SubscriberState;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.EUtranCgiImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.TAIdImpl;
import org.restcomm.protocols.ss7.sccp.parameter.GlobalTitle;
import org.restcomm.protocols.ss7.sccp.parameter.SccpAddress;

import javax.slee.ActivityContextInterface;
import javax.slee.CreateException;
import javax.slee.SbbContext;
import javax.slee.serviceactivity.ServiceStartedEvent;
import javax.xml.bind.DatatypeConverter;
import java.awt.geom.Point2D;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mobicents.gmlc.slee.gis.GeographicHelper.polygonCentroid;
import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;

/**
 * @author <a href="mailto:bbaranow@redhat.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public abstract class CDRGeneratorSbb extends MobileCoreNetworkInterfaceSbb implements CDRInterface {

    private static final GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();

    private static final Logger cdrTracer = Logger.getLogger(CDRGeneratorSbb.class);

    private static final String CDR_GENERATED_TO = "Textfile";

    protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
    protected static final DecimalFormat uncertaintyFormat = new DecimalFormat("#0.000000");

    private final TaskManager taskManager = new TaskManager();

    private final boolean sendCdrToGlass = gmlcPropertiesManagement.getGlaasEnabled();

    public CDRGeneratorSbb() throws UnknownHostException {
        super();
    }

    // -------------------- SLEE Stuff -----------------------
    // --------------- CDRInterface methods ------------------
    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.gmlc.slee.cdr.CDRInterface#init(boolean)
     */
    @Override
    public void init(final boolean reset) {
        super.logger.info("Setting CDR_GENERATED_TO to " + CDR_GENERATED_TO);
    }

    /* (non-Javadoc)
     * @see org.mobicents.gmlc.slee.cdr.CDRInterface#createRecord(org.mobicents.gmlc.slee.cdr.Status)
     */
    @Override
    public void createRecord(RecordStatus outcome) {

        GMLCCDRState state = getState();

        if (state.isGenerated()) {
            super.logger.severe("");
        } else {
            if (super.logger.isFineEnabled()) {
                super.logger.fine("Generating record, status '" + outcome + "' for '" + state + "'");
            }
            DateTime startTime = state.getDialogStartTime();
            if (startTime != null) {
                DateTime endTime = DateTime.now();
                Long duration = endTime.getMillis() - startTime.getMillis();
                state.setDialogEndTime(endTime);
                state.setDialogDuration(duration);
            }
            state.setRecordStatus(outcome);
            state.setGenerated(true);
            this.setState(state);
            String data = this.toString(state);
            if (this.logger.isFineEnabled()) {
                this.logger.fine(data);
            } else {
                cdrTracer.debug(data);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.gmlc.slee.cdr.CDRInterface#setState(org.mobicents.gmlc.slee.cdr.GMLCCDRState)
     */
    @Override
    public void setState(GMLCCDRState state) {
        this.setGMLCCDRState(state);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mobicents.gmlc.slee.cdr.CDRInterface#getState()
     */
    @Override
    public GMLCCDRState getState() {
        return this.getGMLCCDRState();
    }

    // CMPs
    public abstract GMLCCDRState getGMLCCDRState();

    public abstract void setGMLCCDRState(GMLCCDRState state);

    public void onStartServiceEvent(ServiceStartedEvent event, ActivityContextInterface aci) {
        this.init(true);
    }

    // --------------- SBB callbacks ---------------

    /*
     * (non-Javadoc)
     *
     * @see javax.slee.Sbb#sbbCreate()
     */
    @Override
    public void sbbCreate() throws CreateException {
        this.setGMLCCDRState(new GMLCCDRState());
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.slee.Sbb#setSbbContext(javax.slee.SbbContext)
     */
    @Override
    public void setSbbContext(SbbContext ctx) {
        super.setSbbContext(ctx);
        super.logger = super.sbbContext.getTracer(TRACER_NAME);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.slee.Sbb#unsetSbbContext()
     */
    @Override
    public void unsetSbbContext() {
        super.unsetSbbContext();
    }

    // -------- helper methods
    private static final String SEPARATOR = "|";

    /**
     * @param gmlcCdrState GMLCCDRState object
     * @return String
     */
    protected String toString(GMLCCDRState gmlcCdrState) {

        CDRModel cdrModel = new CDRModel();

        final StringBuilder stringBuilder = new StringBuilder(); //StringBuilder is faster than StringBuffer

        final Timestamp time_stamp = new Timestamp(System.currentTimeMillis());
        String time_stamp_string = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time_stamp);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time_stamp.getTime()));
        String time = new SimpleDateFormat("HH:mm:ss").format(new Time(time_stamp.getTime()));

        if (sendCdrToGlass)
            taskManager.start();

        // TIMESTAMP
        cdrModel.setTimeStamp(time_stamp_string);
        stringBuilder.append(cdrModel.getTimeStamp()).append(SEPARATOR);

        cdrModel.setCdrDate(date);
        cdrModel.setCdrTime(time);

        // ID
        stringBuilder.append(gmlcCdrState.getId()).append(SEPARATOR);
        cdrModel.setGmlcId(gmlcCdrState.getId());

        // HTTP Request (cURL) Username
        if (gmlcCdrState.getCurlUser() != null) {
            cdrModel.setCurlUser(gmlcCdrState.getCurlUser());
            stringBuilder.append(cdrModel.getCurlUser()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
            cdrModel.setCurlUser("");
        }

        // RECORD STATUS
        if (gmlcCdrState.getRecordStatus() != null) {
            cdrModel.setRecordStatus(gmlcCdrState.getRecordStatus().toString());
            stringBuilder.append(gmlcCdrState.getRecordStatus().toString()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
            cdrModel.setRecordStatus("");
        }

        // STATUS CODE
        if (gmlcCdrState.getStatusCode() != null) {
            cdrModel.setStatusCode(gmlcCdrState.getStatusCode().toString());
            stringBuilder.append(gmlcCdrState.getStatusCode()).append(SEPARATOR);
        } else {
            cdrModel.setStatusCode("");
            stringBuilder.append(SEPARATOR);
        }

        // LOCAL DIALOG_ID
        if (gmlcCdrState.getLocalDialogId() != null) {
            cdrModel.setLocalDialogId(gmlcCdrState.getLocalDialogId().toString());
            stringBuilder.append(gmlcCdrState.getLocalDialogId()).append(SEPARATOR);
        } else {
            cdrModel.setLocalDialogId("");
            stringBuilder.append(SEPARATOR);
        }

        // REMOTE DIALOG_ID
        if (gmlcCdrState.getRemoteDialogId() != null) {
            cdrModel.setRemoteDialogId(gmlcCdrState.getRemoteDialogId().toString());
            stringBuilder.append(gmlcCdrState.getRemoteDialogId()).append(SEPARATOR);
        } else {
            cdrModel.setRemoteDialogId("");
            stringBuilder.append(SEPARATOR);
        }

        // DIALOG_DURATION
        Long dialogDuration = gmlcCdrState.getDialogDuration();
        if (dialogDuration != null) {
            cdrModel.setDialogDuration(dialogDuration.toString());
            stringBuilder.append(dialogDuration).append(SEPARATOR);
        } else {
            cdrModel.setDialogDuration("");
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LOCAL Address
         */
        SccpAddress localAddress = gmlcCdrState.getLocalAddress();
        if (localAddress != null) {
            AddressIndicator addressIndicator = localAddress.getAddressIndicator();

            // LOCAL SPC
            if (addressIndicator.isPCPresent()) {
                cdrModel.setLocalSPC(String.valueOf(localAddress.getSignalingPointCode()));
                stringBuilder.append(localAddress.getSignalingPointCode()).append(SEPARATOR);
            } else {
                cdrModel.setLocalSPC("");
                stringBuilder.append(SEPARATOR);
            }

            // LOCAL SSN
            if (addressIndicator.isSSNPresent()) {
                cdrModel.setLocalSSN(String.valueOf(localAddress.getSubsystemNumber()));
                stringBuilder.append(localAddress.getSubsystemNumber()).append(SEPARATOR);
            } else {
                cdrModel.setLocalSSN("");
                stringBuilder.append(SEPARATOR);
            }
            // LOCAL ROUTING INDICATOR
            if (addressIndicator.getRoutingIndicator() != null) {
                cdrModel.setLocalRoutingIndicator(String.valueOf((byte) addressIndicator.getRoutingIndicator().getValue()));
                stringBuilder.append((byte) addressIndicator.getRoutingIndicator().getValue()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
                cdrModel.setLocalRoutingIndicator("");
            }

            // Local GLOBAL TITLE
            GlobalTitle localAddressGlobalTitle = localAddress.getGlobalTitle();
            // Local GLOBAL TITLE INDICATOR
            if (localAddressGlobalTitle != null && localAddressGlobalTitle.getGlobalTitleIndicator() != null) {
                cdrModel.setLocalGlobalTitleIndicator(String.valueOf((byte) localAddressGlobalTitle.getGlobalTitleIndicator().getValue()));
                stringBuilder.append((byte) localAddressGlobalTitle.getGlobalTitleIndicator().getValue()).append(SEPARATOR);
            } else {
                cdrModel.setLocalGlobalTitleIndicator("");
                stringBuilder.append(SEPARATOR);
            }
            // Local GLOBAL TITLE DIGITS
            if (localAddressGlobalTitle != null && localAddressGlobalTitle.getDigits() != null) {
                cdrModel.setLocalGlobalTitleDigits(localAddressGlobalTitle.getDigits());
                stringBuilder.append(localAddressGlobalTitle.getDigits()).append(SEPARATOR);
            } else {
                cdrModel.setLocalGlobalTitleDigits("");
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }

        /**
         * REMOTE Address
         */
        SccpAddress remoteAddress = gmlcCdrState.getRemoteAddress();
        if (remoteAddress != null) {
            AddressIndicator addressIndicator = remoteAddress.getAddressIndicator();

            // REMOTE SPC
            if (addressIndicator.isPCPresent()) {
                cdrModel.setRemoteSPC(String.valueOf(remoteAddress.getSignalingPointCode()));
                stringBuilder.append(remoteAddress.getSignalingPointCode()).append(SEPARATOR);
            } else {
                cdrModel.setRemoteSPC("");
                stringBuilder.append(SEPARATOR);
            }

            // REMOTE SSN
            if (addressIndicator.isSSNPresent()) {
                cdrModel.setRemoteSSN(String.valueOf(remoteAddress.getSubsystemNumber()));
                stringBuilder.append(remoteAddress.getSubsystemNumber()).append(SEPARATOR);
            } else {
                cdrModel.setRemoteSSN("");
                stringBuilder.append(SEPARATOR);
            }

            // REMOTE ROUTING INDICATOR
            if (addressIndicator.getRoutingIndicator() != null) {
                cdrModel.setRemoteRoutingIndicator(String.valueOf((byte) addressIndicator.getRoutingIndicator().getValue()));
                stringBuilder.append((byte) addressIndicator.getRoutingIndicator().getValue()).append(SEPARATOR);
            } else {
                cdrModel.setRemoteRoutingIndicator("");
                stringBuilder.append(SEPARATOR);
            }

            // REMOTE GLOBAL TITLE
            GlobalTitle remoteAddressGlobalTitle = remoteAddress.getGlobalTitle();
            if (remoteAddressGlobalTitle != null && remoteAddressGlobalTitle.getGlobalTitleIndicator() != null) {
                // REMOTE GLOBAL TITLE INDICATOR
                cdrModel.setRemoteGlobalTitleIndicator(String.valueOf((byte) remoteAddressGlobalTitle.getGlobalTitleIndicator().getValue()));
                stringBuilder.append((byte) remoteAddressGlobalTitle.getGlobalTitleIndicator().getValue()).append(SEPARATOR);
            } else {
                cdrModel.setRemoteGlobalTitleIndicator("");
                stringBuilder.append(SEPARATOR);
            }
            // REMOTE GLOBAL TITLE DIGITS
            if (remoteAddressGlobalTitle != null && remoteAddressGlobalTitle.getDigits() != null) {
                cdrModel.setRemoteGlobalTitleDigits(remoteAddressGlobalTitle.getDigits());
                stringBuilder.append(remoteAddressGlobalTitle.getDigits()).append(SEPARATOR);
            } else {
                cdrModel.setRemoteGlobalTitleDigits("");
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }

        /**
         * ISDN Address
         */
        ISDNAddressString isdnAddressString = gmlcCdrState.getISDNAddressString();
        if (isdnAddressString != null) {
            // ISDN ADDRESS NATURE
            cdrModel.setIsdnAddressNature(String.valueOf((byte) isdnAddressString.getAddressNature().getIndicator()));
            stringBuilder.append((byte) isdnAddressString.getAddressNature().getIndicator()).append(SEPARATOR);
            // ISDN NUMBERING PLAN INDICATOR
            cdrModel.setIsdnNumberingPlanIndicator(String.valueOf((byte) isdnAddressString.getNumberingPlan().getIndicator()));
            stringBuilder.append((byte) isdnAddressString.getNumberingPlan().getIndicator()).append(SEPARATOR);
            // ISDN ADDRESS DIGITS
            cdrModel.setIsdnAddressDigits(isdnAddressString.getAddress());
            stringBuilder.append(isdnAddressString.getAddress()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }

        /*
         * Diameter Session Id
         */
        String diameterSessionId = gmlcCdrState.getDiameterSessionId();
        if (diameterSessionId != null) {
            cdrModel.setDiameterSessionId(diameterSessionId);
            stringBuilder.append(diameterSessionId).append(SEPARATOR); // DIAMETER SESSION ID
        } else {
            cdrModel.setDiameterSessionId("");
            stringBuilder.append(SEPARATOR);
        }

        /*
         * Diameter Command Origin Host and Realm
         */
        net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginHost = gmlcCdrState.getDiameterOriginHost();
        if (diameterOriginHost != null) {
            cdrModel.setDiameterOriginHost(diameterOriginHost.toString());
            stringBuilder.append(diameterOriginHost).append(SEPARATOR); // DIAMETER COMMAND ORIGIN HOST
        } else {
            cdrModel.setDiameterOriginHost("");
            stringBuilder.append(SEPARATOR);
        }
        net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterOriginRealm = gmlcCdrState.getDiameterOriginRealm();
        if (diameterOriginRealm != null) {
            cdrModel.setDiameterOriginRealm(diameterOriginRealm.toString());
            stringBuilder.append(diameterOriginRealm).append(SEPARATOR); // DIAMETER COMMAND ORIGIN REALM
        } else {
            cdrModel.setDiameterOriginRealm("");
            stringBuilder.append(SEPARATOR);
        }

        /*
         * Diameter Command Destination Host and Realm
         */
        net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestinationHost = gmlcCdrState.getDiameterDestHost();
        if (diameterDestinationHost != null) {
            cdrModel.setDiameterDestinationHost(diameterDestinationHost.toString());
            stringBuilder.append(diameterDestinationHost).append(SEPARATOR); // DIAMETER COMMAND DESTINATION HOST
        } else {
            cdrModel.setDiameterDestinationHost("");
            stringBuilder.append(SEPARATOR);
        }
        net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterDestinationRealm = gmlcCdrState.getDiameterDestRealm();
        if (diameterDestinationRealm != null) {
            cdrModel.setDiameterDestinationRealm(diameterDestinationRealm.toString());
            stringBuilder.append(diameterDestinationRealm).append(SEPARATOR); // DIAMETER COMMAND DESTINATION REALM
        } else {
            cdrModel.setDiameterDestinationRealm("");
            stringBuilder.append(SEPARATOR);
        }

        /*
         * SUPL TLS SLP Socket (GMLC)
         */
        InetAddress slpSocketAddress = gmlcCdrState.getSlpSocketAddress();
        if (slpSocketAddress != null) {
            cdrModel.setSlpSocketAddress(slpSocketAddress.getHostAddress());
            stringBuilder.append(slpSocketAddress.getHostAddress()).append(SEPARATOR);
        } else {
            cdrModel.setSlpSocketAddress("");
            stringBuilder.append(SEPARATOR);
        }
        int slpSocketPort = gmlcCdrState.getSlpSocketPort();
        if (slpSocketPort > -1) {
            cdrModel.setSlpSocketPort(String.valueOf(slpSocketPort));
            stringBuilder.append(slpSocketPort).append(SEPARATOR);
        } else {
            cdrModel.setSlpSocketPort("");
            stringBuilder.append(SEPARATOR);
        }
        /*
         * SUPL TLS SET Socket (GMLC)
         */
        InetAddress setSocketAddress = gmlcCdrState.getSetSocketAddress();
        if (setSocketAddress != null) {
            cdrModel.setSetSocketAddress(setSocketAddress.getHostAddress());
            stringBuilder.append(setSocketAddress.getHostAddress()).append(SEPARATOR);
        } else {
            cdrModel.setSetSocketAddress("");
            stringBuilder.append(SEPARATOR);
        }
        int setSocketPort = gmlcCdrState.getSlpSocketPort();
        if (setSocketPort > -1) {
            cdrModel.setSetSocketPort(String.valueOf(setSocketPort));
            stringBuilder.append(setSocketPort).append(SEPARATOR);
        } else {
            cdrModel.setSetSocketPort("");
            stringBuilder.append(SEPARATOR);
        }

        ISDNAddressString vlrNumber, mscNumber, sgsnNumber;
        vlrNumber = mscNumber = sgsnNumber = null;
        DiameterIdentity sgsnName, sgsnRealm, mmeName, mmeRealm, aaaServerName, servingNodeAddressMmeNumber;
        sgsnName = sgsnRealm = mmeName = mmeRealm = aaaServerName = servingNodeAddressMmeNumber = null;
        String typeOfShape, lsaIdPLMNSig, amfAddress, smsfAddress, state, notReachableReasonState, locationNumberAddress;
        typeOfShape = lsaIdPLMNSig = amfAddress = smsfAddress = state = notReachableReasonState = locationNumberAddress = null;
        Double latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, uncertaintyAltitude, uncertaintyRadius,
            angleOfMajorAxis, offsetAngle, includedAngle;
        latitude = longitude = uncertainty = uncertaintySemiMajorAxis = uncertaintySemiMinorAxis = uncertaintyAltitude = uncertaintyRadius =
            angleOfMajorAxis = offsetAngle = includedAngle = null;
        int cgiMcc, cgiMnc, cgiLac, cgiCiorSac, raiMcc, raiMnc, raiLac, raiRac, lsaId, ecgiMcc, ecgiMnc, ecgiCi, taiMcc, taiMnc, taiTac,
            nrCgiMcc, nrCgiMnc, nrTaiMcc, nrTaiMnc, nrTaiTac, geodeticConfidence, geodeticScreeningAndPresentationInd, innerRadius, estimateConfidence, altitude,
            polygonNumberOfPoints, locationNumberNAI, locationNumberNNI, locationNumberNPI, locationNumberAddressRepresentationRestrictedIndicator,
            locationNumberScreeningIndicator;
        cgiMcc = cgiMnc = cgiLac = cgiCiorSac = raiMcc = raiMnc = raiLac = raiRac = lsaId = ecgiMcc = ecgiMnc = ecgiCi = taiMcc = taiMnc = taiTac =
            nrCgiMcc = nrCgiMnc = nrTaiMcc = nrTaiMnc = nrTaiTac = geodeticConfidence = geodeticScreeningAndPresentationInd = innerRadius = estimateConfidence = altitude =
                polygonNumberOfPoints = locationNumberNAI = locationNumberNNI = locationNumberNPI =
                    locationNumberAddressRepresentationRestrictedIndicator = locationNumberScreeningIndicator = -1;
        Integer ratTypeCode = null;
        Long eci, eNBId, nci;
        eci = eNBId = nci = null;
        boolean saiPresent, geoInfo, locationNumber, locationNumberOddFlag;
        saiPresent = geoInfo = locationNumber = locationNumberOddFlag = false;
        Boolean deferredMTLRResponseIndicator;
        PlmnId visitedPlmnId = null;
        Polygon estimatePolygon = null;
        LocalTimeZone localTimeZone = null;
        MNPInfoRes mnpInfoResult = null;
        MSClassmark2 msClassmark = null;
        GPRSMSClass gprsMsClass = null;

        /**
         * CS Location Information (from Sh)
         */
        LocationInformation shLocationInformation = gmlcCdrState.getLocationInformation();

        /**
         * EPS Location Information (from Sh)
         */
        LocationInformationEPS shLocationInformationEPS = gmlcCdrState.getLocationInformationEPS();
        /**
         * CELL PORTION ID (from LTE LCS)
         */
        Long cellPortionId = gmlcCdrState.getCellPortionId();
        /**
         * Location Information GPRS (from Sh)
         */
        LocationInformationGPRS shLocationInformationPS = gmlcCdrState.getLocationInformationGPRS();
        /**
         * Sh 5GS Location Information (from Sh)
         */
        LocationInformation5GS shlocationInformation5GS = gmlcCdrState.getLocationInformation5GS();
        /**
         * Sh 5GS Location Information (from MAP)
         */
        org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.LocationInformation5GS locationInformation5GS =
                gmlcCdrState.getLocationInformation5GSFromMap();

        /**
         * Subscriber Information (from ATI or PSI or Sh UDR)
         */
        SubscriberInfo subscriberInfo = gmlcCdrState.getSubscriberInfo();
        if (subscriberInfo != null || shLocationInformation != null || shLocationInformationEPS != null
                || shLocationInformationPS != null || shlocationInformation5GS != null) {
            /**
             * CS Location Information
             */
            LocationInformation locationInformation = null;
            if (subscriberInfo != null) {
                locationInformation = subscriberInfo.getLocationInformation();
            } else if (shLocationInformation != null || shLocationInformationEPS != null || shlocationInformation5GS != null) {
                locationInformation = shLocationInformation;
            }
            if (locationInformation != null || shLocationInformationEPS != null || shlocationInformation5GS != null
                    || locationInformation5GS != null) {
                if (locationInformation != null) {
                    /**
                     * LOCATION NUMBER
                     */
                    if (locationInformation.getLocationNumber() != null) {
                        try {
                            if (locationInformation.getLocationNumber().getLocationNumber() != null) {
                                locationNumber = true;
                                // LOCATION NUMBER ODD FLAG
                                locationNumberOddFlag = locationInformation.getLocationNumber().getLocationNumber().isOddFlag();
                                cdrModel.setLocationNumberOddFlag(String.valueOf(locationNumberOddFlag));
                                // LOCATION NUMBER NAI
                                locationNumberNAI = locationInformation.getLocationNumber().getLocationNumber().getNatureOfAddressIndicator();
                                cdrModel.setLocationNumberNAI(String.valueOf(locationNumberNAI));
                                // LOCATION NUMBER NNI
                                locationNumberNNI = locationInformation.getLocationNumber().getLocationNumber().getInternalNetworkNumberIndicator();
                                cdrModel.setLocationNumberINNI(String.valueOf(locationNumberNNI));
                                // LOCATION NUMBER NPI
                                locationNumberNPI = locationInformation.getLocationNumber().getLocationNumber().getNumberingPlanIndicator();
                                cdrModel.setLocationNumberNPI(String.valueOf(locationNumberNPI));
                                // LOCATION NUMBER REPRESENTATION RESTRICTED INDICATOR
                                locationNumberAddressRepresentationRestrictedIndicator = locationInformation.getLocationNumber().getLocationNumber().getAddressRepresentationRestrictedIndicator();
                                cdrModel.setLocationNumberAPRI(String.valueOf(locationNumberAddressRepresentationRestrictedIndicator));
                                // LOCATION NUMBER SCREENING INDICATOR
                                locationNumberScreeningIndicator = locationInformation.getLocationNumber().getLocationNumber().getScreeningIndicator();
                                cdrModel.setLocationNumberSI(String.valueOf(locationNumberScreeningIndicator));
                                // LOCATION NUMBER ADDRESS
                                locationNumberAddress = locationInformation.getLocationNumber().getLocationNumber().getAddress();
                                cdrModel.setLocationNumberAddress(locationNumberAddress);
                            }

                        } catch (MAPException e) {
                            logger.severe(e.getMessage());
                        }
                    }
                    if (locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                        /**
                         * CGI
                         */
                        if (locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                            try {
                                // CGI MCC
                                cgiMcc = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                                cdrModel.setCellGlobalIdServiceAreaIdMCC(String.valueOf(cgiMcc));
                                // CGI MNC
                                cgiMnc = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                                cdrModel.setCellGlobalIdServiceAreaIdMNC(String.valueOf(cgiMnc));
                                // CGI LAC
                                cgiLac = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                                cdrModel.setCellGlobalIdServiceAreaIdLac(String.valueOf(cgiLac));
                                // CGI CI
                                cgiCiorSac = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                                cdrModel.setCellGlobalIdServiceAreaIdCI(String.valueOf(cgiCiorSac));
                            } catch (MAPException e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        if (locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                            try {
                                // CGI MCC
                                cgiMcc = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC();
                                cdrModel.setCellGlobalIdServiceAreaIdMCC(String.valueOf(cgiMcc));
                                // CGI MNC
                                cgiMnc = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC();
                                cdrModel.setCellGlobalIdServiceAreaIdMNC(String.valueOf(cgiMnc));
                                // CGI LAC
                                cgiCiorSac = locationInformation.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac();
                                cdrModel.setCellGlobalIdServiceAreaIdLac(String.valueOf(cgiCiorSac));
                                // CGI CI
                                cdrModel.setCellGlobalIdServiceAreaIdCI("");
                            } catch (MAPException e) {
                                logger.severe(e.getMessage());
                            }
                        }
                    }
                    /**
                     * SAI Present
                     */
                    if (locationInformation.getSaiPresent())
                        saiPresent = locationInformation.getSaiPresent();
                    /**
                     * VLR NUMBER
                     */
                    if (locationInformation.getVlrNumber() != null) {
                        vlrNumber = locationInformation.getVlrNumber();
                        cdrModel.setVlrNumber(locationInformation.getVlrNumber().getAddress());
                    } else {
                        cdrModel.setVlrNumber("");
                    }
                    /**
                     * MSC NUMBER
                     */
                    if (locationInformation.getMscNumber() != null) {
                        mscNumber = locationInformation.getMscNumber();
                        cdrModel.setMscNumber(locationInformation.getMscNumber().getAddress());
                    } else {
                        cdrModel.setMscNumber("");
                    }
                    /**
                     * AGE OF LOCATION INFORMATION
                     */
                    if (locationInformation.getAgeOfLocationInformation() != null) {
                        cdrModel.setAgeOfLocationInformation(locationInformation.getAgeOfLocationInformation().toString());
                    } else {
                        cdrModel.setAgeOfLocationInformation("");
                    }
                    /**
                     * GEOGRAPHICAL INFO
                     */
                    if (locationInformation.getGeographicalInformation() != null) {
                        geoInfo = true;
                        // GEOGRAPHICAL INFO TYPE OF SHAPE
                        typeOfShape = locationInformation.getGeographicalInformation().getTypeOfShape().toString();
                        cdrModel.setGeographicalInfoTypeOfShape(locationInformation.getGeographicalInformation().getTypeOfShape().toString());
                        // GEOGRAPHICAL INFO LATITUDE
                        if (locationInformation.getGeographicalInformation().getLatitude() != 0.0) {
                            String formattedGeographicalInformationLatitude;
                            formattedGeographicalInformationLatitude = coordinatesFormat.format(locationInformation.getGeographicalInformation().getLatitude());
                            latitude = Double.valueOf(formattedGeographicalInformationLatitude);
                            cdrModel.setGeographicalInfoLatitude(formattedGeographicalInformationLatitude);
                        } else {
                            cdrModel.setGeographicalInfoLatitude("");
                        }
                        // GEOGRAPHICAL INFO LONGITUDE
                        if (locationInformation.getGeographicalInformation().getLongitude() != 0.0) {
                            String formattedGeographicalInformationLongitude;
                            formattedGeographicalInformationLongitude = coordinatesFormat.format(locationInformation.getGeographicalInformation().getLongitude());
                            longitude = Double.valueOf(formattedGeographicalInformationLongitude);
                            cdrModel.setGeographicalInfoLongitude(formattedGeographicalInformationLongitude);
                        } else {
                            cdrModel.setGeographicalInfoLongitude("");
                        }

                        // GEOGRAPHICAL INFO UNCERTAINTY
                        if (locationInformation.getGeographicalInformation().getLatitude() != 0.0
                                && locationInformation.getGeographicalInformation().getLongitude() != 0.0) {
                            uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformation.getGeographicalInformation().getUncertainty()));
                            cdrModel.setGeographicalInfoUncertainty(uncertaintyFormat.format(locationInformation.getGeographicalInformation().getUncertainty()));
                        } else {
                            cdrModel.setGeographicalInfoUncertainty("");
                        }
                    }
                    /**
                     * GEODETIC INFO
                     */
                    if (locationInformation.getGeodeticInformation() != null) {
                        geoInfo = true;
                        // GEODETIC INFO TYPE OF SHAPE
                        if (locationInformation.getGeodeticInformation().getTypeOfShape() != null) {
                            typeOfShape = locationInformation.getGeodeticInformation().getTypeOfShape().toString();
                            cdrModel.setGeodeticInfoTypeOfShape(locationInformation.getGeodeticInformation().getTypeOfShape().toString());
                        }
                        // GEODETIC INFO LATITUDE
                        if (locationInformation.getGeodeticInformation().getLatitude() != 0.0) {
                            String formattedGeodeticInformationLatitude;
                            formattedGeodeticInformationLatitude = coordinatesFormat.format(locationInformation.getGeodeticInformation().getLatitude());
                            latitude = Double.valueOf(formattedGeodeticInformationLatitude);
                            cdrModel.setGeodeticInfoLatitude(formattedGeodeticInformationLatitude);
                        } else {
                            cdrModel.setGeodeticInfoLatitude("");
                        }

                        // GEODETIC INFO LONGITUDE
                        if (locationInformation.getGeodeticInformation().getLongitude() != 0.0) {
                            String formattedGeodeticInformationLongitude;
                            formattedGeodeticInformationLongitude = coordinatesFormat.format(locationInformation.getGeodeticInformation().getLongitude());
                            longitude = Double.valueOf(formattedGeodeticInformationLongitude);
                            cdrModel.setGeodeticInfoLongitude(formattedGeodeticInformationLongitude);
                        } else {
                            cdrModel.setGeodeticInfoLongitude("");
                        }
                        // GEODETIC INFO UNCERTAINTY
                        if (locationInformation.getGeodeticInformation().getLatitude() != 0.0 
                                && locationInformation.getGeodeticInformation().getLongitude() != 0.0) {
                            uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformation.getGeodeticInformation().getUncertainty()));
                            cdrModel.setGeodeticInfoUncertainty(uncertaintyFormat.format(locationInformation.getGeodeticInformation().getUncertainty()));
                        } else {
                            cdrModel.setGeodeticInfoUncertainty("");
                        }
                        // GEODETIC INFO CONFIDENCE
                        if (locationInformation.getGeodeticInformation().getLatitude() != 0.0 &&
                                locationInformation.getGeodeticInformation().getLongitude() != 0.0) {
                            geodeticConfidence = locationInformation.getGeodeticInformation().getConfidence();
                            cdrModel.setGeodeticInfoConfidence(String.valueOf(locationInformation.getGeodeticInformation().getConfidence()));
                        } else {
                            cdrModel.setGeodeticInfoConfidence("");
                        }
                        // GEODETIC INFO SCREENING AND PRESENTATION INDICATORS
                        if (locationInformation.getGeodeticInformation().getLatitude() != 0.0 &&
                                locationInformation.getGeodeticInformation().getLongitude() != 0.0) {
                            geodeticScreeningAndPresentationInd = locationInformation.getGeodeticInformation().getScreeningAndPresentationIndicators();
                            cdrModel.setGeodeticInfoScreeningAndPresentationIndicators(String.valueOf(locationInformation.getGeodeticInformation().getScreeningAndPresentationIndicators()));
                        } else {
                            cdrModel.setGeodeticInfoScreeningAndPresentationIndicators("");
                        }
                    }
                }
                /**
                 * EPS Location Information
                 */
                LocationInformationEPS locationInformationEPS = null;
                if (subscriberInfo != null) {
                    if (subscriberInfo.getLocationInformation() != null)
                        locationInformationEPS = subscriberInfo.getLocationInformation().getLocationInformationEPS();
                    else if (subscriberInfo.getLocationInformationEPS() != null)
                        locationInformationEPS = subscriberInfo.getLocationInformationEPS();
                } else if (shLocationInformationEPS != null) {
                    locationInformationEPS = shLocationInformationEPS;
                }

                if (locationInformationEPS != null || shlocationInformation5GS != null || locationInformation5GS != null) {

                    if (locationInformationEPS != null) {
                        /**
                         * E-UTRAN CGI from EPS location information from ATI, PSI or Sh
                         */
                        if (locationInformationEPS.getEUtranCellGlobalIdentity() != null) {
                            EUtranCgi eutrancgi = new EUtranCgiImpl(locationInformationEPS.getEUtranCellGlobalIdentity().getData());
                            try {
                                // ECGI MCC
                                ecgiMcc = eutrancgi.getMCC();
                                cdrModel.setEutranCellGlobalIdMCC(String.valueOf(ecgiMcc));
                                // ECGI MNC
                                ecgiMnc = eutrancgi.getMNC();
                                cdrModel.setEutranCellGlobalIdMNC(String.valueOf(ecgiMnc));
                                // ECGI ECI
                                eci = eutrancgi.getEci();
                                cdrModel.setEutranCellGlobalIdECI(String.valueOf(eci));
                                // ECGI ENBID
                                eNBId = eutrancgi.getENodeBId();
                                cdrModel.setEutranCellGlobalIdENBID(String.valueOf(eNBId));
                                // ECGI CI
                                ecgiCi = eutrancgi.getCi();
                                cdrModel.setEutranCellGlobalIdCI(String.valueOf(ecgiCi));
                            } catch (Exception e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        if (cellPortionId != null) {
                            cdrModel.setCellPortionId(String.valueOf(cellPortionId));
                        } else {
                            cdrModel.setCellPortionId("");
                        }
                        /*
                         * TRACKING AREA IDENTITY from EPS location information from ATI, PSI or Sh
                         */
                        if (locationInformationEPS.getTrackingAreaIdentity() != null) {
                            TrackingAreaId tai = new TrackingAreaIdImpl(locationInformationEPS.getTrackingAreaIdentity().getData());
                            try {
                                // TAI MCC
                                taiMcc = tai.getMCC();
                                cdrModel.setTaiMCC(String.valueOf(taiMcc));
                                // TAI MNC
                                taiMnc = tai.getMNC();
                                cdrModel.setTaiMNC(String.valueOf(taiMnc));
                                // TAI TAC
                                taiTac = tai.getTAC();
                                cdrModel.setTaiTAC(String.valueOf(taiTac));
                            } catch (MAPException e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        /**
                         * EPS GEOGRAPHICAL INFORMATION from EPS location information from ATI, PSI or Sh
                         */
                        if (locationInformationEPS.getGeographicalInformation() != null) {
                            geoInfo = true;
                            // EPS LOCATION INFO GEOGRAPHICAL TYPE OF SHAPE
                            typeOfShape = locationInformationEPS.getGeographicalInformation().getTypeOfShape().toString();
                            cdrModel.setEpsLocationInfoGeographicalTypeOfShape(locationInformationEPS.getGeographicalInformation().getTypeOfShape().toString());
                            // EPS LOCATION INFO GEOGRAPHICAL LATITUDE
                            if (locationInformationEPS.getGeographicalInformation().getLatitude() != 0.0) {
                                String formattedEPSGeographicalInformationLatitude;
                                formattedEPSGeographicalInformationLatitude = coordinatesFormat.format(locationInformationEPS.getGeographicalInformation().getLatitude());
                                latitude = Double.valueOf(formattedEPSGeographicalInformationLatitude);
                                cdrModel.setEpsLocationInfoGeographicalLatitude(formattedEPSGeographicalInformationLatitude);
                            } else {
                                cdrModel.setEpsLocationInfoGeographicalLatitude("");
                            }
                            // EPS LOCATION INFO GEOGRAPHICAL LONGITUDE
                            if (locationInformationEPS.getGeographicalInformation().getLongitude() != 0.0) {
                                String formattedEPSGeographicalInformationLongitude;
                                formattedEPSGeographicalInformationLongitude = coordinatesFormat.format(locationInformationEPS.getGeographicalInformation().getLongitude());
                                longitude = Double.valueOf(formattedEPSGeographicalInformationLongitude);
                                cdrModel.setEpsLocationInfoGeographicalLongitude(formattedEPSGeographicalInformationLongitude);
                            } else {
                                cdrModel.setEpsLocationInfoGeographicalLongitude("");
                            }
                            // EPS LOCATION INFO GEOGRAPHICAL UNCERTAINTY
                            if (locationInformationEPS.getGeographicalInformation().getLatitude() != 0.0 &&
                                locationInformationEPS.getGeographicalInformation().getLongitude() != 0.0) {
                                uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformationEPS.getGeographicalInformation().getUncertainty()));
                                cdrModel.setEpsLocationInfoGeographicalUncertainty(uncertaintyFormat.format(locationInformationEPS.getGeographicalInformation().getUncertainty()));
                            } else {
                                cdrModel.setEpsLocationInfoGeographicalUncertainty("");
                            }
                        }

                    } else if (shlocationInformation5GS != null) {
                        /**
                         * E-UTRAN CGI from 5GS location information from Sh
                         */
                        if (shlocationInformation5GS.getEUtranCellGlobalIdentity() != null) {
                            EUTRANCGI eutrancgi = new EUTRANCGIImpl(shlocationInformation5GS.getEUtranCellGlobalIdentity().getData());
                            try {
                                // ECGI MCC
                                ecgiMcc = eutrancgi.getMCC();
                                cdrModel.setEutranCellGlobalId5GsMCC(String.valueOf(ecgiMcc));
                                // ECGI MNC
                                ecgiMnc = eutrancgi.getMNC();
                                cdrModel.setEutranCellGlobalId5GsMNC(String.valueOf(ecgiMnc));
                                // ECGI ECI
                                eci = eutrancgi.getEci();
                                cdrModel.setEutranCellGlobalId5GsECI(String.valueOf(eci));
                                // ECGI ENBID
                                eNBId = eutrancgi.getENodeBId();
                                cdrModel.setEutranCellGlobalId5GsENBID(String.valueOf(eNBId));
                                // ECGI CI
                                ecgiCi = eutrancgi.getCi();
                                cdrModel.setEutranCellGlobalId5GsCI(String.valueOf(ecgiCi));
                            } catch (Exception e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        if (cellPortionId != null) {
                            cdrModel.setCellPortionId5Gs(String.valueOf(cellPortionId));
                        } else {
                            cdrModel.setCellPortionId5Gs("");
                        }
                        /**
                         * TRACKING AREA IDENTITY from 5GS location information from Sh
                         */
                        if (shlocationInformation5GS.getTrackingAreaIdentity() != null) {
                            TrackingAreaId trackingAreaId = new TrackingAreaIdImpl(shlocationInformation5GS.getTrackingAreaIdentity().getData());
                            try {
                                // TAI MCC
                                taiMcc = trackingAreaId.getMCC();
                                cdrModel.setTai5GsMCC(String.valueOf(trackingAreaId.getMCC()));
                                // TAI MNC
                                taiMnc = trackingAreaId.getMNC();
                                cdrModel.setTai5GsMNC(String.valueOf(trackingAreaId.getMNC()));
                                // TAI TAC
                                taiTac = trackingAreaId.getTAC();
                                cdrModel.setTai5GsTAC(String.valueOf(trackingAreaId.getTAC()));
                            } catch (Exception e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        /**
                         * GEOGRAPHICAL INFORMATION from 5GS location information from Sh
                         */
                        if (shlocationInformation5GS.getGeographicalInformation() != null) {
                            geoInfo = true;
                            // 5GS GEOGRAPHICAL INFO TYPE OF SHAPE
                            if (shlocationInformation5GS.getGeographicalInformation().getTypeOfShape() != null) {
                                typeOfShape = shlocationInformation5GS.getGeographicalInformation().getTypeOfShape().toString();
                                cdrModel.setGeographicalInfo5GsTypeOfShape(gmlcCdrState.getTypeOfShape().toString());
                            }
                            // 5GS GEOGRAPHICAL INFO LATITUDE
                            if (shlocationInformation5GS.getGeographicalInformation().getLatitude() != 0.0) {
                                String formatted5GSGeographicalInformationLatitude;
                                formatted5GSGeographicalInformationLatitude = coordinatesFormat.format(shlocationInformation5GS.getGeographicalInformation().getLatitude());
                                latitude = Double.valueOf(formatted5GSGeographicalInformationLatitude);
                                cdrModel.setGeographicalInfo5GsLatitude(formatted5GSGeographicalInformationLatitude);
                            } else {
                                cdrModel.setGeographicalInfo5GsLatitude("");
                            }
                            // 5GS GEOGRAPHICAL INFO LONGITUDE
                            if (shlocationInformation5GS.getGeographicalInformation().getLongitude() != 0.0) {
                                String formatted5GSGeographicalInformationLongitude;
                                formatted5GSGeographicalInformationLongitude = coordinatesFormat.format(shlocationInformation5GS.getGeographicalInformation().getLongitude());
                                longitude = Double.valueOf(formatted5GSGeographicalInformationLongitude);
                                cdrModel.setGeographicalInfo5GsLongitude(formatted5GSGeographicalInformationLongitude);
                            } else {
                                cdrModel.setGeographicalInfo5GsLongitude("");
                            }
                            // 5GS GEOGRAPHICAL INFO UNCERTAINTY
                            if (shlocationInformation5GS.getGeographicalInformation().getLatitude() != 0.0
                                && shlocationInformation5GS.getGeographicalInformation().getLongitude() != 0.0) {
                                uncertainty = Double.valueOf(uncertaintyFormat.format(shlocationInformation5GS.getGeographicalInformation().getUncertainty()));
                                cdrModel.setGeographicalInfo5GsUncertainty(uncertaintyFormat.format(uncertainty));
                            } else {
                                cdrModel.setGeographicalInfo5GsUncertainty("");
                            }
                        }
                    } else {
                        /**
                         * E-UTRAN CGI from 5GS location information from MAP ATI or PSI
                         */
                        if (locationInformation5GS.getEUtranCgi() != null) {
                            EUtranCgi eUtranCgi = new EUtranCgiImpl(locationInformation5GS.getEUtranCgi().getData());
                            try {
                                // ECGI MCC
                                ecgiMcc = eUtranCgi.getMCC();
                                cdrModel.setEutranCellGlobalId5GsMCC(String.valueOf(ecgiMcc));
                                // ECGI MNC
                                ecgiMnc = eUtranCgi.getMNC();
                                cdrModel.setEutranCellGlobalId5GsMNC(String.valueOf(ecgiMnc));
                                // ECGI ECI
                                eci = eUtranCgi.getEci();
                                cdrModel.setEutranCellGlobalId5GsECI(String.valueOf(eci));
                                // ECGI ENBID
                                eNBId = eUtranCgi.getENodeBId();
                                cdrModel.setEutranCellGlobalId5GsENBID(String.valueOf(eNBId));
                                // ECGI CI
                                ecgiCi = eUtranCgi.getCi();
                                cdrModel.setEutranCellGlobalId5GsCI(String.valueOf(ecgiCi));
                            } catch (Exception e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        if (cellPortionId != null) {
                            cdrModel.setCellPortionId5Gs(String.valueOf(cellPortionId));
                        } else {
                            cdrModel.setCellPortionId5Gs("");
                        }
                        /**
                         * TRACKING AREA IDENTITY from 5GS location information from MAP ATI or PSI
                         */
                        if (locationInformation5GS.getTAId() != null) {
                            TAId taId = new TAIdImpl(locationInformation5GS.getTAId().getData());
                            try {
                                // TAI MCC
                                taiMcc = taId.getMCC();
                                cdrModel.setTai5GsMCC(String.valueOf(taId.getMCC()));
                                // TAI MNC
                                taiMnc = taId.getMNC();
                                cdrModel.setTai5GsMNC(String.valueOf(taId.getMNC()));
                                // TAI TAC
                                taiTac = taId.getTAC();
                                cdrModel.setTai5GsTAC(String.valueOf(taId.getTAC()));
                            } catch (Exception e) {
                                logger.severe(e.getMessage());
                            }
                        }
                        /**
                         * GEOGRAPHICAL INFORMATION from 5GS location information from MAP ATI or PSI
                         */
                        if (locationInformation5GS.getGeographicalInformation() != null) {
                            geoInfo = true;
                            // 5GS GEOGRAPHICAL INFO TYPE OF SHAPE
                            if (locationInformation5GS.getGeographicalInformation().getTypeOfShape() != null) {
                                typeOfShape = locationInformation5GS.getGeographicalInformation().getTypeOfShape().toString();
                                cdrModel.setGeographicalInfo5GsTypeOfShape(gmlcCdrState.getTypeOfShape().toString());
                            }
                            // 5GS GEOGRAPHICAL INFO LATITUDE
                            if (locationInformation5GS.getGeographicalInformation().getLatitude() != 0.0) {
                                String formatted5GSGeographicalInformationLatitude;
                                formatted5GSGeographicalInformationLatitude = coordinatesFormat.format(locationInformation5GS.getGeographicalInformation().getLatitude());
                                latitude = Double.valueOf(formatted5GSGeographicalInformationLatitude);
                                cdrModel.setGeographicalInfo5GsLatitude(formatted5GSGeographicalInformationLatitude);
                            } else {
                                cdrModel.setGeographicalInfo5GsLatitude("");
                            }
                            // 5GS GEOGRAPHICAL INFO LONGITUDE
                            if (locationInformation5GS.getGeographicalInformation().getLongitude() != 0.0) {
                                String formatted5GSGeographicalInformationLongitude;
                                formatted5GSGeographicalInformationLongitude = coordinatesFormat.format(locationInformation5GS.getGeographicalInformation().getLongitude());
                                longitude = Double.valueOf(formatted5GSGeographicalInformationLongitude);
                                cdrModel.setGeographicalInfo5GsLongitude(formatted5GSGeographicalInformationLongitude);
                            } else {
                                cdrModel.setGeographicalInfo5GsLongitude("");
                            }
                            // 5GS GEOGRAPHICAL INFO UNCERTAINTY
                            if (locationInformation5GS.getGeographicalInformation().getLatitude() != 0.0 &&
                                locationInformation5GS.getGeographicalInformation().getLongitude() != 0.0) {
                                uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformation5GS.getGeographicalInformation().getUncertainty()));
                                cdrModel.setGeographicalInfo5GsUncertainty(uncertaintyFormat.format(uncertainty));
                            } else {
                                cdrModel.setGeographicalInfo5GsUncertainty("");
                            }
                        }
                    }

                    if (locationInformationEPS != null || locationInformation5GS != null) {

                        if (locationInformationEPS != null) {
                            /**
                             * EPS GEODETIC INFORMATION
                             */
                            if (locationInformationEPS.getGeodeticInformation() != null) {
                                geoInfo = true;
                                // EPS LOCATION INFO GEODETIC TYPE OF SHAPE
                                if (locationInformationEPS.getGeodeticInformation().getTypeOfShape() != null) {
                                    typeOfShape = locationInformationEPS.getGeodeticInformation().getTypeOfShape().toString();
                                    cdrModel.setEpsLocationInfoGeodeticTypeOfShape(locationInformationEPS.getGeodeticInformation().getTypeOfShape().toString());
                                }
                                // EPS LOCATION INFO GEODETIC LATITUDE
                                if (locationInformationEPS.getGeodeticInformation().getLatitude() != 0.0) {
                                    String formattedEPSGeodeticInformationLatitude;
                                    formattedEPSGeodeticInformationLatitude = coordinatesFormat.format(locationInformationEPS.getGeodeticInformation().getLatitude());
                                    latitude = Double.valueOf(formattedEPSGeodeticInformationLatitude);
                                    cdrModel.setEpsLocationInfoGeodeticLatitude(formattedEPSGeodeticInformationLatitude);
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticLatitude("");
                                }
                                // EPS LOCATION INFO GEODETIC LONGITUDE
                                if (locationInformationEPS.getGeodeticInformation().getLongitude() != 0.0) {
                                    String formattedEPSGeodeticInformationLongitude;
                                    formattedEPSGeodeticInformationLongitude = coordinatesFormat.format(locationInformationEPS.getGeodeticInformation().getLongitude());
                                    longitude = Double.valueOf(formattedEPSGeodeticInformationLongitude);
                                    cdrModel.setEpsLocationInfoGeodeticLongitude(formattedEPSGeodeticInformationLongitude);
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticLongitude("");
                                }
                                // EPS LOCATION INFO GEODETIC UNCERTAINTY
                                if (locationInformationEPS.getGeodeticInformation().getLatitude() != 0.0 &&
                                    locationInformationEPS.getGeodeticInformation().getLongitude() != 0.0) {
                                    uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformationEPS.getGeodeticInformation().getUncertainty()));
                                    cdrModel.setEpsLocationInfoGeodeticUncertainty(uncertaintyFormat.format(locationInformationEPS.getGeodeticInformation().getUncertainty()));
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticUncertainty("");
                                }
                                // EPS LOCATION INFO GEODETIC CONFIDENCE
                                if (locationInformationEPS.getGeodeticInformation().getLatitude() != 0.0 &&
                                        locationInformationEPS.getGeodeticInformation().getLongitude() != 0.0) {
                                    geodeticConfidence = locationInformationEPS.getGeodeticInformation().getConfidence();
                                    cdrModel.setEpsLocationInfoGeodeticConfidence(String.valueOf(locationInformationEPS.getGeodeticInformation().getConfidence()));
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticConfidence("");
                                }
                                // EPS LOCATION INFO GEODETIC SCREENING AND PRESENTATION INDICATORS
                                if (locationInformationEPS.getGeodeticInformation().getLatitude() != 0.0 &&
                                        locationInformationEPS.getGeodeticInformation().getLongitude() != 0.0) {
                                    geodeticScreeningAndPresentationInd = locationInformationEPS.getGeodeticInformation().getScreeningAndPresentationIndicators();
                                    cdrModel.setEpsLocationInfoGeodeticScreeningAndPresentationInd(String.valueOf(locationInformationEPS.getGeodeticInformation().getScreeningAndPresentationIndicators()));
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticScreeningAndPresentationInd("");
                                }
                            }
                            /**
                             * MME NAME
                             */
                            if (locationInformationEPS.getMmeName() != null) {
                                mmeName = locationInformationEPS.getMmeName();
                                cdrModel.setEpsLocationInfoGeodeticMMEName(new String(locationInformationEPS.getMmeName().getData()));
                            } else {
                                cdrModel.setEpsLocationInfoGeodeticMMEName("");
                            }
                        } else {
                            /**
                             * 5GS GEODETIC INFORMATION
                             */
                            if (locationInformation5GS.getGeodeticInformation() != null) {
                                geoInfo = true;
                                // 5GS LOCATION INFO GEODETIC TYPE OF SHAPE
                                if (locationInformation5GS.getGeodeticInformation().getTypeOfShape() != null) {
                                    typeOfShape = locationInformation5GS.getGeodeticInformation().getTypeOfShape().toString();
                                    cdrModel.setNrLocationInfoGeodeticTypeOfShape(locationInformation5GS.getGeodeticInformation().getTypeOfShape().toString());
                                }
                                // 5GS LOCATION INFO GEODETIC LATITUDE
                                if (locationInformation5GS.getGeodeticInformation().getLatitude() != 0.0) {
                                    String formatted5GSGeodeticInformationLatitude;
                                    formatted5GSGeodeticInformationLatitude = coordinatesFormat.format(locationInformation5GS.getGeodeticInformation().getLatitude());
                                    latitude = Double.valueOf(formatted5GSGeodeticInformationLatitude);
                                    cdrModel.setNrLocationInfoGeodeticLatitude(formatted5GSGeodeticInformationLatitude);
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticLatitude("");
                                }
                                // 5GS LOCATION INFO GEODETIC LONGITUDE
                                if (locationInformation5GS.getGeodeticInformation().getLongitude() != 0.0) {
                                    String formatted5GSGeodeticInformationLongitude;
                                    formatted5GSGeodeticInformationLongitude = coordinatesFormat.format(locationInformation5GS.getGeodeticInformation().getLongitude());
                                    longitude = Double.valueOf(formatted5GSGeodeticInformationLongitude);
                                    cdrModel.setNrLocationInfoGeodeticLongitude(formatted5GSGeodeticInformationLongitude);
                                } else {
                                    cdrModel.setEpsLocationInfoGeodeticLongitude("");
                                }
                                // 5GS LOCATION INFO GEODETIC UNCERTAINTY
                                if (locationInformation5GS.getGeodeticInformation().getLatitude() != 0.0 &&
                                    locationInformation5GS.getGeodeticInformation().getLongitude() != 0.0) {
                                    uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformation5GS.getGeodeticInformation().getUncertainty()));
                                    cdrModel.setNrLocationInfoGeodeticUncertainty(uncertaintyFormat.format(locationInformation5GS.getGeodeticInformation().getUncertainty()));
                                } else {
                                    cdrModel.setNrLocationInfoGeodeticUncertainty("");
                                }
                                // 5GS LOCATION INFO GEODETIC CONFIDENCE
                                if (locationInformation5GS.getGeodeticInformation().getLatitude() != 0.0 &&
                                        locationInformation5GS.getGeodeticInformation().getLongitude() != 0.0) {
                                    geodeticConfidence = locationInformation5GS.getGeodeticInformation().getConfidence();
                                    cdrModel.setNrLocationInfoGeodeticConfidence(String.valueOf(locationInformation5GS.getGeodeticInformation().getConfidence()));
                                } else {
                                    cdrModel.setNrLocationInfoGeodeticConfidence("");
                                }
                                // 5GS LOCATION INFO GEODETIC SCREENING AND PRESENTATION INDICATORS
                                if (locationInformation5GS.getGeodeticInformation().getLatitude() != 0.0 &&
                                        locationInformation5GS.getGeodeticInformation().getLongitude() != 0.0) {
                                    geodeticScreeningAndPresentationInd = locationInformation5GS.getGeodeticInformation().getScreeningAndPresentationIndicators();
                                    cdrModel.setNrLocationInfoGeodeticScreeningAndPresentationInd(String.valueOf(locationInformation5GS.getGeodeticInformation().getScreeningAndPresentationIndicators()));
                                } else {
                                    cdrModel.setNrLocationInfoGeodeticScreeningAndPresentationInd("");
                                }
                            }
                        }
                    }
                }
            }
            /**
             * Location Information GPRS
             */
            LocationInformationGPRS locationInformationGPRS = null;
            if (subscriberInfo != null) {
                if (subscriberInfo.getLocationInformationGPRS() != null)
                    locationInformationGPRS = subscriberInfo.getLocationInformationGPRS();
            } else if (shLocationInformationPS != null) {
                locationInformationGPRS = shLocationInformationPS;
            }
            if (locationInformationGPRS != null) {
                if (locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                    /**
                     * PS CGI
                     */
                    if (locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                        try {
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAIMCC(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC()));
                            cgiMcc = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAIMNC(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC()));
                            cgiMnc = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAILac(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac()));
                            cgiLac = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAICI(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode()));
                            cgiCiorSac = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                        } catch (MAPException e) {
                            logger.severe(e.getMessage());
                        }
                    }
                    if (locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                        try {
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAIMCC(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC()));
                            cgiMcc = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC();
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAIMNC(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC()));
                            cgiMnc = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC();
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAILac(String.valueOf(locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac()));
                            cgiLac = locationInformationGPRS.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac();
                            cdrModel.setPsCellGlobalIdServiceAreaIdOrLAICI("");
                        } catch (MAPException e) {
                            logger.severe(e.getMessage());
                        }
                    }
                }
                /**
                 * GPRS SAI PRESENT
                 */
                if (locationInformationGPRS.isSaiPresent())
                    saiPresent = locationInformationGPRS.isSaiPresent();
                /**
                 * ROUTEING AREA IDENTITY
                 */
                if (locationInformationGPRS.getRouteingAreaIdentity() != null) {
                    RoutingAreaId rai = new RoutingAreaIdImpl(locationInformationGPRS.getRouteingAreaIdentity().getData());
                    try {
                        // RAI MCC
                        raiMcc = rai.getMCC();
                        cdrModel.setRaiMCC(String.valueOf(raiMcc));
                        // RAI MNC
                        raiMnc = rai.getMNC();
                        cdrModel.setRaiMNC(String.valueOf(raiMnc));
                        // RAI LAC
                        raiLac = rai.getLAC();
                        cdrModel.setRaiLAC(String.valueOf(raiLac));
                        // RAI RAC
                        raiRac = rai.getRAC();
                        cdrModel.setRaiRAC(String.valueOf(raiRac));
                    } catch (MAPException e) {
                        logger.severe(e.getMessage());
                    }
                }
                /**
                 * PS AGE OF LOCATION INFORMATION
                 */
                if (locationInformationGPRS.getAgeOfLocationInformation() != null) {
                    cdrModel.setPsAgeOfLocationInformation(String.valueOf(locationInformationGPRS.getAgeOfLocationInformation().intValue()));
                }
                /**
                 * GPRS GEOGRAPHICAL INFORMATION
                 */
                if (locationInformationGPRS.getGeographicalInformation() != null) {
                    geoInfo = true;
                    // GPRS GEOGRAPHICAL INFO TYPE OF SHAPE
                    if (locationInformationGPRS.getGeographicalInformation().getTypeOfShape() != null) {
                        typeOfShape = locationInformationGPRS.getGeographicalInformation().getTypeOfShape().toString();
                        cdrModel.setPsGeographicalInfoTypeOfShape(String.valueOf(locationInformationGPRS.getGeographicalInformation().getTypeOfShape()));
                    }
                    // GPRS GEOGRAPHICAL INFO LATITUDE
                    if (locationInformationGPRS.getGeographicalInformation().getLatitude() != 0.0) {
                        String formattedPSGeographicalInformationLatitude;
                        formattedPSGeographicalInformationLatitude = coordinatesFormat.format(locationInformationGPRS.getGeographicalInformation().getLatitude());
                        latitude = Double.valueOf(formattedPSGeographicalInformationLatitude);
                        cdrModel.setPsGeographicalInfoLatitude(formattedPSGeographicalInformationLatitude);
                    }
                    // GPRS GEOGRAPHICAL INFO LONGITUDE
                    if (locationInformationGPRS.getGeographicalInformation().getLongitude() != 0.0) {
                        String formattedPSGeographicalInformationLongitude;
                        formattedPSGeographicalInformationLongitude = coordinatesFormat.format(locationInformationGPRS.getGeographicalInformation().getLongitude());
                        longitude = Double.valueOf(formattedPSGeographicalInformationLongitude);
                        cdrModel.setPsGeographicalInfoLongitude(formattedPSGeographicalInformationLongitude);
                    }
                    // GPRS GEOGRAPHICAL INFO UNCERTAINTY
                    if (locationInformationGPRS.getGeographicalInformation().getLatitude() != 0.0
                        && locationInformationGPRS.getGeographicalInformation().getLongitude() != 0.0) {
                        uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformationGPRS.getGeographicalInformation().getUncertainty()));
                        cdrModel.setPsGeographicalInfoUncertainty(uncertaintyFormat.format(locationInformationGPRS.getGeographicalInformation().getUncertainty()));
                    }
                }
                /**
                 * GPRS GEODETIC INFORMATION
                 */
                if (locationInformationGPRS.getGeodeticInformation() != null) {
                    geoInfo = true;
                    // GPRS GEODETIC INFO TYPE OF SHAPE
                    if (locationInformationGPRS.getGeodeticInformation().getTypeOfShape() != null) {
                        typeOfShape = locationInformationGPRS.getGeodeticInformation().getTypeOfShape().toString();
                        cdrModel.setPsGeodeticInfoTypeOfShape(String.valueOf(locationInformationGPRS.getGeodeticInformation().getTypeOfShape()));
                    }
                    // GPRS GEODETIC INFO LATITUDE
                    if (locationInformationGPRS.getGeodeticInformation().getLatitude() != 0.0) {
                        String formattedPSGeodeticInformationLatitude;
                        formattedPSGeodeticInformationLatitude = coordinatesFormat.format(locationInformationGPRS.getGeodeticInformation().getLatitude());
                        latitude = Double.valueOf(formattedPSGeodeticInformationLatitude);
                        cdrModel.setPsGeodeticInfoLatitude(formattedPSGeodeticInformationLatitude);
                    }
                    // GPRS GEODETIC INFO LONGITUDE
                    if (locationInformationGPRS.getGeodeticInformation().getLongitude() != 0.0) {
                        String formattedPSGeodeticInformationLongitude;
                        formattedPSGeodeticInformationLongitude = coordinatesFormat.format(locationInformationGPRS.getGeodeticInformation().getLongitude());
                        longitude = Double.valueOf(formattedPSGeodeticInformationLongitude);
                        cdrModel.setPsGeodeticInfoLongitude(formattedPSGeodeticInformationLongitude);
                    }
                    // GPRS GEODETIC INFO UNCERTAINTY
                    if (locationInformationGPRS.getGeodeticInformation().getLatitude() != 0.0 &&
                        locationInformationGPRS.getGeodeticInformation().getLongitude() != 0.0) {
                        uncertainty = Double.valueOf(uncertaintyFormat.format(locationInformationGPRS.getGeodeticInformation().getUncertainty()));
                        cdrModel.setPsGeodeticInfoUncertainty(uncertaintyFormat.format(locationInformationGPRS.getGeodeticInformation().getUncertainty()));
                    }
                    // GPRS GEODETIC INFO CONFIDENCE
                    if (locationInformationGPRS.getGeodeticInformation().getLatitude() != 0.0 &&
                            locationInformationGPRS.getGeodeticInformation().getLongitude() != 0.0) {
                        geodeticConfidence = locationInformationGPRS.getGeodeticInformation().getConfidence();
                        cdrModel.setPsGeodeticInfoConfidence(String.valueOf(locationInformationGPRS.getGeodeticInformation().getConfidence()));
                    }
                    // GPRS GEODETIC INFO SCREENING AND PRESENTATION INDICATORS
                    if (locationInformationGPRS.getGeodeticInformation().getLatitude() != 0.0 &&
                            locationInformationGPRS.getGeodeticInformation().getLongitude() != 0.0) {
                        geodeticScreeningAndPresentationInd = locationInformationGPRS.getGeodeticInformation().getScreeningAndPresentationIndicators();
                        cdrModel.setPsGeodeticInfoScreeningAndPresentationIndicators(String.valueOf(locationInformationGPRS.getGeodeticInformation().getScreeningAndPresentationIndicators()));
                    }
                }
                /**
                 * LSA ID
                 */
                if (locationInformationGPRS.getLSAIdentity() != null) {
                    // LSA ID
                    cdrModel.setLsaId(new String(locationInformationGPRS.getLSAIdentity().getData()));
                    // LSA ID PLMN SIGNIFICANT
                    if (locationInformationGPRS.getLSAIdentity().isPlmnSignificantLSA()) { // isPlmnSignificantLSA means the opposite in jSS7 implementation
                        cdrModel.setLsaIdPLMNSignificant("Universal");
                        lsaIdPLMNSig = "Universal";
                    } else {
                        cdrModel.setLsaIdPLMNSignificant("PLMN");
                        lsaIdPLMNSig = "PLMN";
                    }
                }
                /**
                 * SGSN NUMBER
                 */
                if (locationInformationGPRS.getSGSNNumber() != null) {
                    cdrModel.setSgsnNumber(locationInformationGPRS.getSGSNNumber().getAddress());
                }
            }
            /**
             * SAI Present
             */
            if (saiPresent) {
                cdrModel.setSaiPresent(String.valueOf(saiPresent));
            }

            if (subscriberInfo != null) {
                /**
                 * CS and PS Subscriber STATE
                 */
                SubscriberState subscriberState = subscriberInfo.getSubscriberState();
                PSSubscriberState psSubscriberState = subscriberInfo.getPSSubscriberState();
                if (subscriberState != null || psSubscriberState != null) {
                    if (subscriberState != null) {
                        if (subscriberState.getSubscriberStateChoice() != null)
                            cdrModel.setSubscriberState(subscriberState.getSubscriberStateChoice().toString());
                        if (subscriberState.getNotReachableReason() != null)
                            cdrModel.setNotReachableReasonState(subscriberState.getNotReachableReason().name());
                    }
                    if (psSubscriberState != null) {
                        if (psSubscriberState.getChoice() != null)
                            cdrModel.setSubscriberState(psSubscriberState.getChoice().toString());
                        if (psSubscriberState.getNetDetNotReachable() != null)
                            cdrModel.setNotReachableReasonState(psSubscriberState.getNetDetNotReachable().name());
                    }
                    state = cdrModel.getSubscriberState();
                    notReachableReasonState = cdrModel.getNotReachableReasonState();
                }

                /**
                 * MS CLASSMARK 2
                 */
                msClassmark = subscriberInfo.getMSClassmark2();
                if (msClassmark != null) {
                    cdrModel.setMsClassmark(bytesToHexString(msClassmark.getData()));
                }

                /**
                 * GPRS MS CLASS
                 */
                gprsMsClass = subscriberInfo.getGPRSMSClass();
                if (gprsMsClass != null) {
                    // GPRS MS CLASS MS RADIO ACCESS CAPABILITY
                    if (gprsMsClass.getMSRadioAccessCapability() != null) {
                        cdrModel.setGprsMSRadioAccessCapability(bytesToHexString(gprsMsClass.getMSRadioAccessCapability().getData()));
                    }
                    // GPRS MS CLASS MS NETWORK CAPABILITY
                    if (gprsMsClass.getMSNetworkCapability() != null) {
                        cdrModel.setGprsMSNetworkCapability(bytesToHexString(gprsMsClass.getMSNetworkCapability().getData()));
                    }
                }

                /**
                 * MNP INFO RESULT (from ATI or PSI)
                 */
                mnpInfoResult = subscriberInfo.getMNPInfoRes();
                if (mnpInfoResult != null) {
                    // MNP NUMBER PORTABILITY STATUS
                    if (mnpInfoResult.getNumberPortabilityStatus() != null)
                        cdrModel.setMnpNumberPortabilityStatus(String.valueOf(mnpInfoResult.getNumberPortabilityStatus().getType()));
                    // MNP IMSI
                    if (mnpInfoResult.getIMSI() != null)
                        cdrModel.setMnpIMSI(new String(mnpInfoResult.getIMSI().getData().getBytes()));
                    // MNP MSISDN
                    if (mnpInfoResult.getMSISDN() != null)
                        cdrModel.setMnpMSISDN(mnpInfoResult.getMSISDN().getAddress());
                    // MNP ROUTEING NUMBER
                    if (mnpInfoResult.getRouteingNumber() != null)
                        cdrModel.setMnpRouteingNumber(mnpInfoResult.getRouteingNumber().getRouteingNumber());
                }
            }

            /**
             * Sh 5GS Location Information only parameters
             */
            if (shlocationInformation5GS != null || locationInformation5GS != null) {
                if (shlocationInformation5GS != null) {
                    if (shlocationInformation5GS.getNRCellGlobalIdentity() != null) {
                        try {
                            // NRCGI MCC
                            nrCgiMcc = shlocationInformation5GS.getNRCellGlobalIdentity().getMCC();
                            cdrModel.setNrCellGlobalId5GsMCC(String.valueOf(shlocationInformation5GS.getNRCellGlobalIdentity().getMCC()));
                            // NRCGI MNC
                            nrCgiMcc = shlocationInformation5GS.getNRCellGlobalIdentity().getMNC();
                            cdrModel.setNrCellGlobalId5GsMNC(String.valueOf(shlocationInformation5GS.getNRCellGlobalIdentity().getMNC()));
                            // NRCGI NCI
                            nci = shlocationInformation5GS.getNRCellGlobalIdentity().getNCI();
                            cdrModel.setNrCellGlobalId5GsNCI(String.valueOf(shlocationInformation5GS.getNRCellGlobalIdentity().getNCI()));
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                    }
                    /*
                     * 5GS AMF Address
                     */
                    if (shlocationInformation5GS.getAMFAddress() != null) {
                        amfAddress = shlocationInformation5GS.getAMFAddress();
                        cdrModel.setAmfAddress5Gs(shlocationInformation5GS.getAMFAddress());
                    }
                    /*
                     * 5GS SMSF Address
                     */
                    if (shlocationInformation5GS.getSMSFAddress() != null) {
                        smsfAddress = shlocationInformation5GS.getSMSFAddress();
                        cdrModel.setSmsfAddress5gs(shlocationInformation5GS.getSMSFAddress());
                    }
                } else {
                    if (locationInformation5GS.getEUtranCgi() != null) {
                        try {
                            // NRCGI MCC
                            nrCgiMcc = locationInformation5GS.getNRCellGlobalId().getMCC();
                            cdrModel.setNrCellGlobalId5GsMCC(String.valueOf(locationInformation5GS.getNRCellGlobalId().getMCC()));
                            // NRCGI MNC
                            nrCgiMnc = locationInformation5GS.getNRCellGlobalId().getMNC();
                            cdrModel.setNrCellGlobalId5GsMNC(String.valueOf(locationInformation5GS.getNRCellGlobalId().getMNC()));
                            // NRCGI NCI
                            nci = locationInformation5GS.getNRCellGlobalId().getNCI();
                            cdrModel.setNrCellGlobalId5GsNCI(String.valueOf(locationInformation5GS.getNRCellGlobalId().getNCI()));
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                    }
                    /*
                     * 5GS AMF Address
                     */
                    if (locationInformation5GS.getAMFAddress() != null) {
                        FQDN amfAddressFqdn = locationInformation5GS.getAMFAddress();
                        amfAddress = new String(amfAddressFqdn.getData(), StandardCharsets.UTF_8);
                        cdrModel.setAmfAddress5Gs(amfAddress);
                    }
                    /*
                     * NR Tracking Area Identity
                     */
                    if (locationInformation5GS.getNRTAId() != null) {
                        try {
                            // NR-TAI MCC
                            nrTaiMcc = locationInformation5GS.getNRTAId().getMCC();
                            cdrModel.setTai5GsMCC(String.valueOf(locationInformation5GS.getNRTAId().getMCC()));
                            // NR-TAI MNC
                            nrTaiMnc = locationInformation5GS.getNRTAId().getMNC();
                            cdrModel.setTai5GsMNC(String.valueOf(locationInformation5GS.getNRTAId().getMNC()));
                            // NR-TAI TAC
                            nrTaiTac = locationInformation5GS.getNRTAId().getNrTAC();
                            cdrModel.setTai5GsTAC(String.valueOf(locationInformation5GS.getNRTAId().getNrTAC()));
                        } catch (Exception e) {
                            logger.severe(e.getMessage());
                        }
                    }
                }
            }

            /*
             * Visited PLMN ID from Sh UDR/UDA or ATI or PSI
             */
            if (gmlcCdrState.getVisitedPlmnId() != null) {
                visitedPlmnId = gmlcCdrState.getVisitedPlmnId();
                try {
                    cdrModel.setVisitedPlmnIdMCC(String.valueOf(visitedPlmnId.getMcc()));
                    cdrModel.setVisitedPlmnIdMNC(String.valueOf(visitedPlmnId.getMnc()));
                } catch (Exception e) {
                    logger.severe(e.getMessage());
                }
            }
            /*
             * Local Time Zone from Sh UDR/UDA
             */
            localTimeZone = gmlcCdrState.getLocalTimeZone();
            if (localTimeZone != null) {
                if (localTimeZone.getTimeZone() != null)
                    cdrModel.setLocalTimeZone(localTimeZone.getTimeZone());
                if (localTimeZone.getDaylightSavingTime() != null)
                    cdrModel.setDaylightSavingTime(String.valueOf(localTimeZone.getDaylightSavingTime()));
            }

            /*
             * RAT type from Sh UDR/UDA or ATI or PSI
             */
            ratTypeCode = gmlcCdrState.getRatType();
            if (ratTypeCode != null) {
                String ratType = getRatType(ratTypeCode);
                cdrModel.setRatType(ratType);
            }
        }

        /**
         * MAP LSM (SRILCS, PSL & SLR) and LTE LCS (RIR, PLR, LRR) gathered parameters
         */

        /**
         * ADDITIONAL NUMBER
         */
        AdditionalNumber additionalNumber = gmlcCdrState.getAdditionalNumber();
        if (additionalNumber != null) {
            if (additionalNumber.getMSCNumber() != null) {
                // MAP LSM ADDITIONAL NUMBER (MSC)
                mscNumber = additionalNumber.getMSCNumber();
                cdrModel.setMapMSCNumber(additionalNumber.getMSCNumber().getAddress());
            }
            if (additionalNumber.getSGSNNumber() != null) {
                // MAP LSM ADDITIONAL NUMBER (SGSN)
                sgsnNumber = additionalNumber.getSGSNNumber();
                cdrModel.setMapSGSNNumber(additionalNumber.getSGSNNumber().getAddress());
            }
        }

        /**
         * SGSN Number
         */
        if (gmlcCdrState.getSgsnNumber() != null) {
            sgsnNumber = gmlcCdrState.getSgsnNumber();
            cdrModel.setSgsnNumber(sgsnNumber.getAddress());
        }

        /**
         * SGSN NAME
         */
        if (gmlcCdrState.getSgsnName() != null) {
            sgsnName = gmlcCdrState.getSgsnName();
            cdrModel.setSgsnName(new String(sgsnName.getData(), StandardCharsets.UTF_8));
        }

        /**
         * SGSN REALM
         */
        if (gmlcCdrState.getSgsnRealm() != null) {
            sgsnRealm = gmlcCdrState.getSgsnRealm();
            cdrModel.setSgsnRealm(new String(sgsnRealm.getData(), StandardCharsets.UTF_8));
        }

        /**
         * MME NAME
         */
        if (gmlcCdrState.getMmeName() != null) {
            mmeName = gmlcCdrState.getMmeName();
            cdrModel.setMmeName(new String(mmeName.getData(), StandardCharsets.UTF_8));
        }

        /**
         * MME REALM
         */
        if (gmlcCdrState.getMmeRealm() != null) {
            mmeRealm = gmlcCdrState.getMmeRealm();
            cdrModel.setMmeRealm(new String(mmeRealm.getData(), StandardCharsets.UTF_8));
        }

        /**
         * MSC Number
         */
        if (gmlcCdrState.getMscNumber() != null) {
            mscNumber = gmlcCdrState.getMscNumber();
            cdrModel.setMscNumber(mscNumber.getAddress());
        }

        /**
         * 3GPP AAA Server Name
         */
        if (gmlcCdrState.getAaaServerName() != null) {
            aaaServerName = gmlcCdrState.getAaaServerName();
            cdrModel.setTgppAAAServerName(new String(aaaServerName.getData(), StandardCharsets.UTF_8));
        }

        /**
         * LOCATION ESTIMATE
         */
        ExtGeographicalInformation locationEstimate = gmlcCdrState.getLocationEstimate();
        if (locationEstimate != null) {
            // LOCATION ESTIMATE TYPE OF SHAPE
            if (locationEstimate.getTypeOfShape() != null) {
                typeOfShape = String.valueOf(locationEstimate.getTypeOfShape());
                cdrModel.setLocationEstimateTypeOfShape(String.valueOf(locationEstimate.getTypeOfShape()));
            }

            // LOCATION ESTIMATE LATITUDE
            locationEstimate.getLatitude();
            if (locationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                String formattedLatitude = coordinatesFormat.format(Double.valueOf(locationEstimate.getLatitude()));
                latitude = Double.valueOf(formattedLatitude);
                cdrModel.setLocationEstimateLatitude(formattedLatitude);
            }

            // LOCATION ESTIMATE LONGITUDE
            locationEstimate.getLongitude();
            if (locationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                String formattedLongitude = coordinatesFormat.format(Double.valueOf(locationEstimate.getLongitude()));
                longitude = Double.valueOf(formattedLongitude);
                cdrModel.setLocationEstimateLongitude(formattedLongitude);
            }

            // LOCATION ESTIMATE UNCERTAINTY
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyCircle) {
                uncertainty = Double.valueOf(uncertaintyFormat.format(locationEstimate.getUncertainty()));
                cdrModel.setLocationEstimateUncertainty(uncertaintyFormat.format(locationEstimate.getUncertainty()));
            }

            // LOCATION ESTIMATE UNCERTAINTY SEMI MAJOR AXIS
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                uncertaintySemiMajorAxis = Double.valueOf(uncertaintyFormat.format(locationEstimate.getUncertaintySemiMajorAxis()));
                cdrModel.setLocationEstimateUncertaintySemiMajorAxis(uncertaintyFormat.format(locationEstimate.getUncertaintySemiMajorAxis()));
            }

            // LOCATION ESTIMATE UNCERTAINTY SEMI MINOR AXIS
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                uncertaintySemiMinorAxis = Double.valueOf(uncertaintyFormat.format(locationEstimate.getUncertaintySemiMinorAxis()));
                cdrModel.setLocationEstimateUncertaintySemiMinorAxis(uncertaintyFormat.format(locationEstimate.getUncertaintySemiMinorAxis()));
            }

            // LOCATION ESTIMATE CONFIDENCE
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                estimateConfidence = locationEstimate.getConfidence();
                cdrModel.setLocationEstimateConfidence(String.valueOf(locationEstimate.getConfidence()));
            }

            // LOCATION ESTIMATE ANGLE OF MAJOR AXIS
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                angleOfMajorAxis = locationEstimate.getAngleOfMajorAxis();
                cdrModel.setLocationEstimateAngleOfMajorAxis(String.valueOf(locationEstimate.getAngleOfMajorAxis()));
            }

            // LOCATION ESTIMATE ALTITUDE
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid ||
                    locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                altitude = locationEstimate.getAltitude();
                cdrModel.setLocationEstimateAltitude(String.valueOf(locationEstimate.getAltitude()));
            }

            // LOCATION ESTIMATE UNCERTAINTY ALTITUDE
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                uncertaintyAltitude = Double.valueOf(uncertaintyFormat.format(locationEstimate.getUncertaintyAltitude()));
                cdrModel.setLocationEstimateUncertaintyAltitude(uncertaintyFormat.format(locationEstimate.getUncertaintyAltitude()));
            }

            // LOCATION ESTIMATE INNER RADIUS
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                innerRadius = locationEstimate.getInnerRadius();
                cdrModel.setLocationEstimateInnerRadius(String.valueOf(locationEstimate.getInnerRadius()));
            }

            // LOCATION ESTIMATE UNCERTAINTY RADIUS
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                uncertaintyRadius = Double.valueOf(uncertaintyFormat.format(locationEstimate.getUncertaintyRadius()));
                cdrModel.setLocationEstimateUncertaintyRadius(uncertaintyFormat.format(locationEstimate.getUncertaintyRadius()));
            }

            // LOCATION ESTIMATE OFFSET ANGLE
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                offsetAngle = locationEstimate.getOffsetAngle();
                cdrModel.setLocationEstimateOffSetAngle(String.valueOf(locationEstimate.getOffsetAngle()));
            }

            // LOCATION ESTIMATE INCLUDED ANGLE
            if (locationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                includedAngle = locationEstimate.getIncludedAngle();
                cdrModel.setLocationEstimateIncludedAngle(String.valueOf(locationEstimate.getIncludedAngle()));
            }

        }

        /**
         * ADDITIONAL LOCATION ESTIMATE
         */
        AddGeographicalInformation additionalLocationEstimate = gmlcCdrState.getAdditionalLocationEstimate();
        if (additionalLocationEstimate != null) {
            // ADDITIONAL LOCATION ESTIMATE TYPE OF SHAPE
            if (additionalLocationEstimate.getTypeOfShape() != null) {
                typeOfShape = String.valueOf(additionalLocationEstimate.getTypeOfShape());
                cdrModel.setAdditionalLocationEstimateTypeOfShape(String.valueOf(additionalLocationEstimate.getTypeOfShape()));
            }

            // ADDITIONAL LOCATION ESTIMATE TYPE OF SHAPE == Polygon
            if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.Polygon) {
                // ADDITIONAL LOCATION ESTIMATE POLYGON NUMBER OF POINTS
                polygonNumberOfPoints = gmlcCdrState.getAdditionalNumberOfPoints();
                if (polygonNumberOfPoints != -1) {
                    cdrModel.setAdditionalLocationEstimatePolygonNumberOfPoint(String.valueOf(polygonNumberOfPoints));
                }

                // ADDITIONAL LOCATION ESTIMATE LATITUDES & LONGITUDES of each POLYGON point
                estimatePolygon = gmlcCdrState.getAdditionalPolygon();

            } else {

                // ADDITIONAL LOCATION ESTIMATE LATITUDE
                latitude = additionalLocationEstimate.getLatitude();
                String formattedAdditionalLocationEstimateLatitude;
                formattedAdditionalLocationEstimateLatitude = coordinatesFormat.format(Double.valueOf(additionalLocationEstimate.getLatitude()));
                cdrModel.setAdditionalLocationEstimateLatitude(formattedAdditionalLocationEstimateLatitude);

                // ADDITIONAL LOCATION ESTIMATE LONGITUDE
                longitude = additionalLocationEstimate.getLongitude();
                String formattedAdditionalLocationEstimateLongitude;
                formattedAdditionalLocationEstimateLongitude = coordinatesFormat.format(Double.valueOf(additionalLocationEstimate.getLongitude()));
                cdrModel.setAdditionalLocationEstimateLongitude(formattedAdditionalLocationEstimateLongitude);

                // ADDITIONAL LOCATION ESTIMATE UNCERTAINTY
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyCircle) {
                    uncertainty = Double.valueOf(uncertaintyFormat.format(additionalLocationEstimate.getUncertainty()));
                    cdrModel.setAdditionalLocationEstimateUncertainty(uncertaintyFormat.format(Double.valueOf(additionalLocationEstimate.getUncertainty())));
                }
                // ADDITIONAL LOCATION ESTIMATE UNCERTAINTY SEMI MAJOR AXIS
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                    uncertaintySemiMajorAxis = Double.valueOf(uncertaintyFormat.format(additionalLocationEstimate.getUncertaintySemiMajorAxis()));
                    cdrModel.setAdditionalLocationEstimateUncertaintySemiMajorAxis(uncertaintyFormat.format(Double.valueOf(additionalLocationEstimate.getUncertaintySemiMajorAxis())));
                }

                // ADDITIONAL LOCATION ESTIMATE UNCERTAINTY SEMI MINOR AXIS
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                    uncertaintySemiMinorAxis = Double.valueOf(uncertaintyFormat.format(additionalLocationEstimate.getUncertaintySemiMinorAxis()));
                    cdrModel.setAdditionalLocationEstimateUncertaintySemiMinorAxis(uncertaintyFormat.format(Double.valueOf(additionalLocationEstimate.getUncertaintySemiMinorAxis())));
                }

                // ADDITIONAL LOCATION ESTIMATE ANGLE OF MAJOR AXIS
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                    angleOfMajorAxis = additionalLocationEstimate.getAngleOfMajorAxis();
                    cdrModel.setAdditionalLocationEstimateAngleOfMajorAxis(String.valueOf(additionalLocationEstimate.getAngleOfMajorAxis()));
                }

                // ADDITIONAL LOCATION ESTIMATE CONFIDENCE
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithUncertaintyEllipse ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                    estimateConfidence = additionalLocationEstimate.getConfidence();
                    cdrModel.setAdditionalLocationEstimateConfidence(String.valueOf(additionalLocationEstimate.getConfidence()));
                }

                // ADDITIONAL LOCATION ESTIMATE ALTITUDE
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitude ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitude ||
                        additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                    altitude = additionalLocationEstimate.getAltitude();
                    if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitude ||
                            additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitude ||
                            additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                        cdrModel.setAdditionalLocationEstimateAltitude(String.valueOf(additionalLocationEstimate.getAltitude()));
                    }
                }

                // ADDITIONAL LOCATION ESTIMATE UNCERTAINTY ALTITUDE
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid) {
                    uncertaintyAltitude = Double.valueOf(uncertaintyFormat.format(additionalLocationEstimate.getUncertaintyAltitude()));
                    cdrModel.setAdditionalLocationEstimateUncertaintyAltitude(uncertaintyFormat.format(additionalLocationEstimate.getUncertaintyAltitude()));
                }

                // ADDITIONAL LOCATION ESTIMATE INNER RADIUS
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                    innerRadius = additionalLocationEstimate.getInnerRadius();
                    cdrModel.setAdditionalLocationEstimateInnerRadius(String.valueOf(additionalLocationEstimate.getInnerRadius()));
                }

                // ADDITIONAL LOCATION ESTIMATE UNCERTAINTY RADIUS
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                    uncertaintyRadius = Double.valueOf(uncertaintyFormat.format(additionalLocationEstimate.getUncertaintyRadius()));
                    if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                        cdrModel.setAdditionalLocationEstimateUncertaintyRadius(uncertaintyFormat.format(Double.valueOf(additionalLocationEstimate.getUncertaintyRadius())));
                    }
                }

                // ADDITIONAL LOCATION ESTIMATE OFFSET ANGLE
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                    offsetAngle = additionalLocationEstimate.getOffsetAngle();
                    if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                        cdrModel.setAdditionalLocationEstimateOffSetAngle(String.valueOf(additionalLocationEstimate.getOffsetAngle()));
                    }
                }

                // ADDITIONAL LOCATION ESTIMATE INCLUDED ANGLE
                if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                    includedAngle = additionalLocationEstimate.getIncludedAngle();
                    if (additionalLocationEstimate.getTypeOfShape() == TypeOfShape.EllipsoidArc) {
                        cdrModel.setAdditionalLocationEstimateIncludedAngle(String.valueOf(additionalLocationEstimate.getIncludedAngle()));
                    }
                }

            }
        }

        /**
         * DEFERRED MT LR RESPONSE INDICATOR
         */
        deferredMTLRResponseIndicator = gmlcCdrState.isDeferredMTLRResponseIndicator();
        if (deferredMTLRResponseIndicator != null) {
            cdrModel.setDeferredMTLRResponseIndicator(String.valueOf(deferredMTLRResponseIndicator));
        }

        /**
         * CGI or SAI or LAI
         */
        CellGlobalIdOrServiceAreaIdOrLAI lcsCGIorSAIorLAI = gmlcCdrState.getCellGlobalIdOrServiceAreaIdOrLAI();
        if (lcsCGIorSAIorLAI != null) {
            if (lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                try {
                    // LCS CGI MCC
                    cgiMcc = lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                    cdrModel.setLcsCGIorSAIorLAIMCC(String.valueOf(lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getMCC()));
                    // LCS CGI MNC
                    cgiMnc = lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                    cdrModel.setLcsCGIorSAIorLAIMNC(String.valueOf(lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getMNC()));
                    // LCS CGI LAC
                    cgiLac = lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                    cdrModel.setLcsCGIorSAIorLAILAC(String.valueOf(lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getLac()));
                    // LCS CGI CI
                    cgiCiorSac = lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                    cdrModel.setLcsCGIorSAIorLAICI(String.valueOf(lcsCGIorSAIorLAI.getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode()));
                } catch (MAPException e) {
                    logger.severe(e.getMessage());
                }
            }
            if (lcsCGIorSAIorLAI.getLAIFixedLength() != null) {
                try {
                    // LCS CGI MCC
                    cgiMcc = lcsCGIorSAIorLAI.getLAIFixedLength().getMCC();
                    cdrModel.setLcsCGIorSAIorLAIMCC(String.valueOf(lcsCGIorSAIorLAI.getLAIFixedLength().getMCC()));
                    // LCS CGI MNC
                    cgiMnc = lcsCGIorSAIorLAI.getLAIFixedLength().getMNC();
                    cdrModel.setLcsCGIorSAIorLAIMNC(String.valueOf(lcsCGIorSAIorLAI.getLAIFixedLength().getMNC()));
                    // LCS CGI LAC
                    cgiLac = lcsCGIorSAIorLAI.getLAIFixedLength().getLac();
                    cdrModel.setLcsCGIorSAIorLAILAC(String.valueOf(lcsCGIorSAIorLAI.getLAIFixedLength().getLac()));
                } catch (MAPException e) {
                    logger.severe(e.getMessage());
                }
            }
        }

        /*
         * LTE ECGI (obtained from SLg)
         */
        EUTRANCGI lcsEutranCgi = gmlcCdrState.getEUtranCgi();
        if (lcsEutranCgi != null) {
            try {
                // ECGI MCC
                ecgiMcc = lcsEutranCgi.getMCC();
                cdrModel.setLcsEutranCgiMCC(String.valueOf(ecgiMcc));
                // ECGI MNC
                ecgiMnc = lcsEutranCgi.getMNC();
                cdrModel.setLcsEutranCgiMNC(String.valueOf(ecgiMnc));
                // ECGI ECI
                eci = lcsEutranCgi.getEci();
                cdrModel.setLcsEutranCgiECI(String.valueOf(eci));
                // ECGI ENBID
                eNBId = lcsEutranCgi.getENodeBId();
                cdrModel.setLcsEutranCgiENBID(String.valueOf(eNBId));
                // ECGI CI
                ecgiCi = lcsEutranCgi.getCi();
                cdrModel.setLcsEutranCgiCI(String.valueOf(ecgiCi));
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        }
        if (cellPortionId != null) {
            cdrModel.setLcsCellPortionId(String.valueOf(cellPortionId));
        }

        /**
         * SERVING NODE ADDRESS
         */
        ServingNodeAddress servingNodeAddress = gmlcCdrState.getServingNodeAddress();
        if (servingNodeAddress != null) {
            if (servingNodeAddress.getMscNumber() != null) {
                // SERVING NODE ADDRESS MSC NUMBER
                mscNumber = servingNodeAddress.getMscNumber();
                cdrModel.setServingNodeAddressMSCNumber(servingNodeAddress.getMscNumber().getAddress());
            }
            if (servingNodeAddress.getSgsnNumber() != null) {
                // SERVING NODE ADDRESS SGSN Number
                sgsnNumber = servingNodeAddress.getSgsnNumber();
                cdrModel.setServingNodeAddressSGSNNumber(servingNodeAddress.getSgsnNumber().getAddress());
            }
            if (servingNodeAddress.getMmeNumber() != null) {
                // SERVING NODE ADDRESS MME NUMBER
                servingNodeAddressMmeNumber = servingNodeAddress.getMmeNumber();
                String mmeNumStr = new String(servingNodeAddress.getMmeNumber().getData(), StandardCharsets.UTF_8);
                cdrModel.setServingNodeAddressMMENumber(mmeNumStr);
            }
        }

        if (sendCdrToGlass) {
            try {
                taskManager.addTask(new TaskCDR(cdrModel));
            } catch (Exception ex) {
                logger.severe("Error on try to send CDR to GLaaS " + ex.getMessage());
            }
        }


        /**
         * MSISDN
         */
        ISDNAddressString msisdn = gmlcCdrState.getMsisdn();
        if (msisdn != null) {
            cdrModel.setMsisdnAddress(msisdn.getAddress());
            stringBuilder.append(cdrModel.getMsisdnAddress()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * IMSI
         */
        IMSI imsi = gmlcCdrState.getImsi();
        if (imsi != null) {
            String imsiStr;
            if (imsi.getData() != null) {
                imsiStr = new String(imsi.getData().getBytes(), StandardCharsets.UTF_8);
                cdrModel.setImsi(imsiStr);
                stringBuilder.append(cdrModel.getImsi()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * IMEI
         */
        IMEI imei = gmlcCdrState.getImei();
        if (imei != null) {
            String imeiStr = new String(imei.getIMEI().getBytes());
            cdrModel.setImei(imeiStr);
            stringBuilder.append(cdrModel.getImei()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LMSI
         */
        LMSI lmsi = gmlcCdrState.getLmsi();
        if (lmsi != null) {
            String lmsiStr = bytesToHex(lmsi.getData());
            cdrModel.setLmsi(lmsiStr);
            stringBuilder.append(cdrModel.getLmsi()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * CGI
         */
        if (cgiMcc != -1) {
            stringBuilder.append(cgiMcc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (cgiMnc != -1) {
            stringBuilder.append(cgiMnc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (cgiLac != -1) {
            stringBuilder.append(cgiLac).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (cgiCiorSac != -1) {
            stringBuilder.append(cgiCiorSac).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (saiPresent) {
            stringBuilder.append(saiPresent).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * RAI
         */
        if (raiMcc != -1) {
            stringBuilder.append(raiMcc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (raiMnc != -1) {
            stringBuilder.append(raiMnc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (raiLac != -1) {
            stringBuilder.append(raiLac).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (raiRac != -1) {
            stringBuilder.append(raiRac).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LSA
         */
        if (lsaId != -1) {
            stringBuilder.append(lsaId).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (lsaIdPLMNSig != null) {
            stringBuilder.append(lsaIdPLMNSig).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * ECGI
         */
        if (ecgiMcc != -1) {
            stringBuilder.append(ecgiMcc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (ecgiMnc != -1) {
            stringBuilder.append(ecgiMnc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (eci != null) {
            stringBuilder.append(eci).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (eNBId != null) {
            stringBuilder.append(eNBId).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (ecgiCi != -1) {
            stringBuilder.append(ecgiCi).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (cellPortionId != null) {
            stringBuilder.append(cellPortionId).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * TAI
         */
        if (taiMcc != -1) {
            stringBuilder.append(taiMcc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (taiMnc != -1) {
            stringBuilder.append(taiMnc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (taiTac != -1) {
            stringBuilder.append(taiTac).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * 5GS NRCGI
         */
        if (nrCgiMcc != -1) {
            stringBuilder.append(nrCgiMcc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (nrCgiMnc != -1) {
            stringBuilder.append(nrCgiMnc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (nci != null) {
            stringBuilder.append(nci).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * 5GS TAI
         */
        if (nrTaiMcc != -1) {
            stringBuilder.append(nrTaiMcc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (nrTaiMnc != -1) {
            stringBuilder.append(nrTaiMnc).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        if (nrTaiTac != -1) {
            stringBuilder.append(nrTaiTac).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * NETWORK NODE NUMBER
         */
        ISDNAddressString networkNodeNumber = gmlcCdrState.getNetworkNodeNumber();
        if (networkNodeNumber != null) {
            cdrModel.setNetworkNodeNumber(networkNodeNumber.getAddress());
            stringBuilder.append(cdrModel.getNetworkNodeNumber()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * GPRS NODE INDICATOR
         */
        Boolean gprsNodeIndicator = gmlcCdrState.isGprsNodeIndicator();
        if (gprsNodeIndicator != null) {
            cdrModel.setGprsNodeIndicator(String.valueOf(gprsNodeIndicator));
            stringBuilder.append(cdrModel.getGprsNodeIndicator()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * VLR Number
         */
        if (vlrNumber != null) {
            stringBuilder.append(vlrNumber.getAddress()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SGSN Number
         */
        if (sgsnNumber != null) {
            stringBuilder.append(sgsnNumber.getAddress()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SGSN Name
         */
        if (sgsnName != null) {
            stringBuilder.append(new String(sgsnName.getData(), StandardCharsets.UTF_8)).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SGSN Realm
         */
        if (sgsnRealm != null) {
            stringBuilder.append(new String(sgsnRealm.getData(), StandardCharsets.UTF_8)).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * MME Name
         */
        if (mmeName != null || servingNodeAddressMmeNumber != null) {
            if (servingNodeAddressMmeNumber != null)
                stringBuilder.append(new String(servingNodeAddressMmeNumber.getData(), StandardCharsets.UTF_8)).append(SEPARATOR);
            else
                stringBuilder.append(new String(mmeName.getData(), StandardCharsets.UTF_8)).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * MME Realm
         */
        if (mmeRealm != null) {
            stringBuilder.append(new String(mmeRealm.getData(), StandardCharsets.UTF_8)).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * MSC Number
         */
        if (mscNumber != null) {
            stringBuilder.append(mscNumber.getAddress()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * 3GPP AAA Server Name
         */
        if (aaaServerName != null) {
            stringBuilder.append(new String(aaaServerName.getData(), StandardCharsets.UTF_8)).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LCS Capabilities Sets
         */
        SupportedLCSCapabilitySets supportedLCSCapabilitySets = gmlcCdrState.getLcsCapabilitySets();
        Long lcsCapabilitiesSets = gmlcCdrState.getLcsCapabilitiesSets();
        if (supportedLCSCapabilitySets != null || lcsCapabilitiesSets != null) {
            String lcsCapSets = null;
            if (supportedLCSCapabilitySets != null) {
                if (supportedLCSCapabilitySets.getCapabilitySetRelease98_99())
                    lcsCapSets = "release98_99";
                if (supportedLCSCapabilitySets.getCapabilitySetRelease4())
                    lcsCapSets = "release4";
                if (supportedLCSCapabilitySets.getCapabilitySetRelease5())
                    lcsCapSets = "release5";
                if (supportedLCSCapabilitySets.getCapabilitySetRelease6())
                    lcsCapSets = "release6";
                if (supportedLCSCapabilitySets.getCapabilitySetRelease7())
                    lcsCapSets = "release7";
            } else {
                switch ((int) (long) lcsCapabilitiesSets) {
                    case 0:
                        lcsCapSets = "release98_99";
                        break;
                    case 1:
                        lcsCapSets = "release4";
                        break;
                    case 2:
                        lcsCapSets = "release5";
                        break;
                    case 3:
                        lcsCapSets = "release6";
                        break;
                    case 4:
                        lcsCapSets = "release7";
                        break;
                }
            }
            cdrModel.setLcsCapabilitySets(lcsCapSets);
            stringBuilder.append(cdrModel.getLcsCapabilitySets()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * H-GMLC ADDRESS
         */
        GSNAddress hGmlcAddress = gmlcCdrState.gethGmlcAddress();
        if (hGmlcAddress != null) {
            // H-GMLC ADDRESS TYPE
            if (hGmlcAddress.getGSNAddressAddressType() != null) {
                cdrModel.setHgmlcAddressType(String.valueOf(hGmlcAddress.getGSNAddressAddressType().getCode()));
                stringBuilder.append(cdrModel.getHgmlcAddressType()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            // H-GMLC ADDRESS DATA
            if (hGmlcAddress.getGSNAddressData() != null) {
                String hGmlcAddressData = bytesToHexString(hGmlcAddress.getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(hGmlcAddressData));
                    hGmlcAddressData = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.severe(e.getMessage());
                }
                cdrModel.setHgmlcAddressData(hGmlcAddressData);
                stringBuilder.append(cdrModel.getHgmlcAddressData()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR).append(SEPARATOR);
        }

        /**
         * V-GMLC ADDRESS
         */
        GSNAddress vGmlcAddress = gmlcCdrState.getvGmlcAddress();
        if (vGmlcAddress != null) {
            // V-GMLC ADDRESS TYPE
            if (vGmlcAddress.getGSNAddressAddressType() != null) {
                cdrModel.setVgmlcAddressType(String.valueOf(vGmlcAddress.getGSNAddressAddressType().getCode()));
                stringBuilder.append(cdrModel.getVgmlcAddressType()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            // V-GMLC ADDRESS DATA
            if (vGmlcAddress.getGSNAddressData() != null) {
                String vGmlcAddressData = bytesToHexString(vGmlcAddress.getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(vGmlcAddressData));
                    vGmlcAddressData = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.severe(e.getMessage());
                }
                cdrModel.setVgmlcAddressData(vGmlcAddressData);
                stringBuilder.append(cdrModel.getVgmlcAddressData()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR).append(SEPARATOR);
        }

        /**
         * PPR ADDRESS
         */
        GSNAddress pprAddress = gmlcCdrState.getPprAddress();
        if (pprAddress != null) {
            // PPR ADDRESS TYPE
            if (pprAddress.getGSNAddressAddressType() != null) {
                cdrModel.setPprAddressType(String.valueOf(pprAddress.getGSNAddressAddressType().getCode()));
                stringBuilder.append(cdrModel.getPprAddressType()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            // PPR ADDRESS DATA
            if (pprAddress.getGSNAddressData() != null) {
                String pprAddressData = bytesToHexString(pprAddress.getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(pprAddressData));
                    pprAddressData = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.severe(e.getMessage());
                }
                cdrModel.setPprAddressData(pprAddressData);
                stringBuilder.append(cdrModel.getPprAddressData()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR).append(SEPARATOR);
        }

        /**
         * AMF Address
         */
        if (amfAddress != null) {
            stringBuilder.append(amfAddress).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SMSF Address
         */
        if (smsfAddress != null) {
            stringBuilder.append(smsfAddress).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * V-PLMN ID
         */
        if (visitedPlmnId != null) {
            stringBuilder.append(visitedPlmnId.getMcc()).append(SEPARATOR);
            stringBuilder.append(visitedPlmnId.getMnc()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR).append(SEPARATOR);
        }

        /**
         * LCSClientID
         */
        LCSClientID lcsClientID = gmlcCdrState.getLcsClientID();
        String lcsEpsClientName = gmlcCdrState.getLcsEpsClientName();
        LCSFormatIndicator lcsEpsClientFormatIndicator = gmlcCdrState.getLcsEpsClientFormatIndicator();
        if (lcsClientID != null) {
            try {
                // LCS CLIENT ID TYPE
                if (lcsClientID.getLCSClientType() != null && (lcsClientID.getLCSClientType().getType() > Integer.MIN_VALUE
                    && lcsClientID.getLCSClientType().getType() < Integer.MAX_VALUE)) {
                    cdrModel.setLcsClientType(String.valueOf(lcsClientID.getLCSClientType().getType()));
                    stringBuilder.append(cdrModel.getLcsClientType()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                if (lcsClientID.getLCSClientName() != null) {
                    if (lcsClientID.getLCSClientName().getNameString() != null) {
                        // LCS CLIENT ID NAME
                        cdrModel.setLcsClientName(lcsClientID.getLCSClientName().getNameString().getString(Charset.defaultCharset()));
                        stringBuilder.append(cdrModel.getLcsClientName()).append(SEPARATOR);
                    } else {
                        stringBuilder.append(SEPARATOR);
                    }
                    if (lcsClientID.getLCSClientName().getDataCodingScheme() != null) {
                        // LCS CLIENT ID NAME DCS
                        cdrModel.setLcsClientDCS(String.valueOf(lcsClientID.getLCSClientName().getDataCodingScheme().getCode()));
                        stringBuilder.append(cdrModel.getLcsClientDCS()).append(SEPARATOR);
                    } else {
                        stringBuilder.append(SEPARATOR);
                    }
                    if (lcsClientID.getLCSClientName().getLCSFormatIndicator() != null) {
                        // LCS CLIENT ID NAME FI
                        cdrModel.setLcsClientFI(String.valueOf(lcsClientID.getLCSClientName().getLCSFormatIndicator().getIndicator()));
                        stringBuilder.append(cdrModel.getLcsClientFI()).append(SEPARATOR);
                    } else {
                        stringBuilder.append(SEPARATOR);
                    }
                }
                if (lcsClientID.getLCSAPN() != null) {
                    // LCS CLIENT ID APN
                    if (lcsClientID.getLCSAPN().getApn() != null) {
                        cdrModel.setLcsClientAPN(new String(lcsClientID.getLCSAPN().getApn().getBytes(), StandardCharsets.UTF_8));
                        stringBuilder.append(cdrModel.getLcsClientAPN()).append(SEPARATOR);
                    }
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                // LCS CLIENT DIALED BY MS
                if (lcsClientID.getLCSClientDialedByMS() != null) {
                    cdrModel.setLcsClientDialedByMS(lcsClientID.getLCSClientDialedByMS().getAddress());
                    stringBuilder.append(cdrModel.getLcsClientDialedByMS()).append(SEPARATOR);
                } else
                    stringBuilder.append(SEPARATOR);
                // LCS CLIENT EXT ID
                if (lcsClientID.getLCSClientExternalID() != null) {
                    cdrModel.setLcsClientExternalID(lcsClientID.getLCSClientExternalID().getExternalAddress().getAddress());
                    stringBuilder.append(cdrModel.getLcsClientExternalID()).append(SEPARATOR);
                } else
                    stringBuilder.append(SEPARATOR);
                // LCS CLIENT INT ID
                if (lcsClientID.getLCSClientInternalID() != null && (lcsClientID.getLCSClientInternalID().getId() > Integer.MIN_VALUE
                    && lcsClientID.getLCSClientInternalID().getId() < Integer.MAX_VALUE)) {
                    cdrModel.setLcsClientInternalID(String.valueOf(lcsClientID.getLCSClientInternalID().getId()));
                    stringBuilder.append(cdrModel.getLcsClientInternalID()).append(SEPARATOR);
                } else
                    stringBuilder.append(SEPARATOR);
                if (lcsClientID.getLCSRequestorID() != null) {
                    // LCS CLIENT REQUESTOR DCS
                    cdrModel.setLcsClientRequestorDCS(String.valueOf(lcsClientID.getLCSRequestorID().getDataCodingScheme().getCode()));
                    stringBuilder.append(cdrModel.getLcsClientRequestorDCS()).append(SEPARATOR);
                    // LCS CLIENT REQUESTOR FI
                    cdrModel.setLcsClientRequestorFI(String.valueOf(lcsClientID.getLCSRequestorID().getLCSFormatIndicator().getIndicator()));
                    stringBuilder.append(cdrModel.getLcsClientRequestorFI()).append(SEPARATOR);
                    // LCS CLIENT REQUESTOR STRING
                    cdrModel.setLcsClientRequestorString(lcsClientID.getLCSRequestorID().getRequestorIDString().getString(Charset.defaultCharset()));
                    stringBuilder.append(cdrModel.getLcsClientRequestorString()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                }
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
        } else {

            if (lcsEpsClientName != null) {
                stringBuilder.append(lcsEpsClientName).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (lcsEpsClientFormatIndicator != null) {
                stringBuilder.append(lcsEpsClientFormatIndicator.getValue()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }

            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }

        /**
         * PSEUDONYM INDICATOR
         */
        Boolean pseudonymIndicator = gmlcCdrState.isPseudonymIndicator();
        if (pseudonymIndicator != null) {
            cdrModel.setPseudonymIndicator(String.valueOf(pseudonymIndicator));
            stringBuilder.append(cdrModel.getPseudonymIndicator()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * GEOGRAPHICAL INFO or GEODETIC INFO or LOCATION ESTIMATE
         */
        if (geoInfo) {
            /*
             * GEOGRAPHICAL INFO or GEODETIC INFO
             */
            if (typeOfShape != null) {
                stringBuilder.append(typeOfShape).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (latitude != null) {
                stringBuilder.append(latitude).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (longitude != null) {
                stringBuilder.append(longitude).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (uncertainty != null) {
                stringBuilder.append(uncertainty).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (geodeticConfidence != -1)  {
                stringBuilder.append(geodeticConfidence).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (geodeticScreeningAndPresentationInd != -1) {
                stringBuilder.append(geodeticScreeningAndPresentationInd).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            stringBuilder.append(SEPARATOR); // ESTIMATE UNCERTAINTY SEMI MAJOR AXIS
            stringBuilder.append(SEPARATOR); // ESTIMATE UNCERTAINTY SEMI MINOR AXIS
            stringBuilder.append(SEPARATOR); // ESTIMATE ANGLE OF MAJOR AXIS
            stringBuilder.append(SEPARATOR); // ESTIMATE ALTITUDE
            stringBuilder.append(SEPARATOR); // ESTIMATE UNCERTAINTY ALTITUDE
            stringBuilder.append(SEPARATOR); // ESTIMATE INNER RADIUS
            stringBuilder.append(SEPARATOR); // ESTIMATE UNCERTAINTY RADIUS
            stringBuilder.append(SEPARATOR); // ESTIMATE OFFSET ANGLE
            stringBuilder.append(SEPARATOR); // ESTIMATE INCLUDED ANGLE
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON NUMBER OF POINTS
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 1
            stringBuilder.append(SEPARATOR); // ADDITIONAL LOCATION POLYGON LONGITUDE POINT 1
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 2
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 2
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 3
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 3
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 4
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 4
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 5
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 5
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 6
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 6
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 7
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 7
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 8
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 8
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 9
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 9
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 10
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 10
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 11
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 11
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 12
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 12
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 13
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 13
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 14
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 14
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 15
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 15
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON CENTROID LATITUDE
            stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON CENTROID LONGITUDE

        } else {
            /*
             * LOCATION ESTIMATE
             */
            if (typeOfShape != null) {
                stringBuilder.append(typeOfShape).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (latitude != null && latitude != 0.0) {
                stringBuilder.append(latitude).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (longitude != null && longitude != 0.0) {
                stringBuilder.append(longitude).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (uncertainty != null) {
                stringBuilder.append(uncertainty).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (estimateConfidence != -1) {
                stringBuilder.append(estimateConfidence).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }

            stringBuilder.append(SEPARATOR); // place of geodetic screening and presentation indicators

            if (uncertaintySemiMajorAxis != null) {
                stringBuilder.append(uncertaintySemiMajorAxis).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (uncertaintySemiMinorAxis != null) {
                stringBuilder.append(uncertaintySemiMinorAxis).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (angleOfMajorAxis != null) {
                stringBuilder.append(angleOfMajorAxis).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (altitude != -1) {
                stringBuilder.append(altitude).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (uncertaintyAltitude != null) {
                stringBuilder.append(uncertaintyAltitude).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (innerRadius != -1) {
                stringBuilder.append(innerRadius).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (uncertaintyRadius != null) {
                stringBuilder.append(uncertaintyRadius).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (offsetAngle != null) {
                stringBuilder.append(offsetAngle).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (includedAngle != null) {
                stringBuilder.append(includedAngle).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (polygonNumberOfPoints != -1) {
                stringBuilder.append(polygonNumberOfPoints).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (estimatePolygon != null) {
                int polygonCoordinates;
                StringBuilder locationsPoints = new StringBuilder();
                if (estimatePolygon.getNumberOfPoints() > 2 && estimatePolygon.getNumberOfPoints() <= 15) {
                    Double[][] polygonArray = new Double[estimatePolygon.getNumberOfPoints()][estimatePolygon.getNumberOfPoints()];
                    Double lat, lon;
                    String formattedLatitude, formattedLongitude;
                    for (polygonCoordinates = 0; polygonCoordinates < estimatePolygon.getNumberOfPoints(); polygonCoordinates++) {
                        lat = estimatePolygon.getEllipsoidPoint(polygonCoordinates).getLatitude();
                        lon = estimatePolygon.getEllipsoidPoint(polygonCoordinates).getLongitude();
                        polygonArray[polygonCoordinates][0] = lat;
                        polygonArray[polygonCoordinates][1] = lon;
                        formattedLatitude = coordinatesFormat.format(lat);
                        formattedLongitude = coordinatesFormat.format(lon);
                        locationsPoints.append("-{").append(formattedLatitude).append(",").append(formattedLongitude).append("}");
                        stringBuilder.append(formattedLatitude).append(SEPARATOR);
                        stringBuilder.append(formattedLongitude).append(SEPARATOR);
                    }
                    while (polygonCoordinates < 15) {
                        stringBuilder.append(SEPARATOR);
                        stringBuilder.append(SEPARATOR);
                        locationsPoints.append("-{0").append(",").append("0}");
                        polygonCoordinates++;
                    }
                    cdrModel.setAdditionalLocationEstPolyListLatLongPoints(locationsPoints.toString());
                    List<Point2D> listOfPoints = new ArrayList<>();
                    Point2D[] point2D = new Point2D.Double[polygonArray.length];
                    Point2D polygonPoint;
                    for (int point = 0; point < polygonArray.length; point++) {
                        lat = polygonArray[point][0];
                        lon = polygonArray[point][1];
                        polygonPoint = new Point2D.Double(lat, lon);
                        listOfPoints.add(polygonPoint);
                        point2D[point] = listOfPoints.get(point);
                    }
                    formattedLatitude = coordinatesFormat.format(polygonCentroid(point2D).getX());
                    formattedLongitude = coordinatesFormat.format(polygonCentroid(point2D).getY());
                    cdrModel.setPolygonCentroidLatitude(formattedLatitude);
                    cdrModel.setPolygonCentroidLongitude(formattedLongitude);
                    stringBuilder.append(formattedLatitude).append(SEPARATOR);
                    stringBuilder.append(formattedLongitude).append(SEPARATOR);
                }
            } else {
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 1
                stringBuilder.append(SEPARATOR); // ADDITIONAL LOCATION POLYGON LONGITUDE POINT 1
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 2
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 2
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 3
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 3
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 4
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 4
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 5
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 5
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 6
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 6
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 7
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 7
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 8
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 8
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 9
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 9
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 10
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 10
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 11
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 11
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 12
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 12
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 13
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 13
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 14
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 14
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LATITUDE POINT 15
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON LONGITUDE POINT 15
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON CENTROID LATITUDE
                stringBuilder.append(SEPARATOR); // ESTIMATE POLYGON CENTROID LONGITUDE
            }
        }

        /**
         * AGE OF LOCATION ESTIMATE
         */
        Integer ageOfLocationEstimate = gmlcCdrState.getAgeOfLocationEstimate();
        if (ageOfLocationEstimate != null) {
            cdrModel.setAgeOfLocationEstimate(String.valueOf(ageOfLocationEstimate));
            stringBuilder.append(cdrModel.getAgeOfLocationEstimate()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * Current Location Retrieved
         */
        Boolean currentLocationRetrieved = gmlcCdrState.isCurrentLocationRetrieved();
        if (currentLocationRetrieved != null) {
            cdrModel.setCurrentLocationRetrieved(String.valueOf(currentLocationRetrieved));
            stringBuilder.append(cdrModel.getCurrentLocationRetrieved()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LCS QoS
         */
        // LCS QoS HORIZONTAL ACCURACY
        LCSQoS horizontalAccuracy = gmlcCdrState.getLcsQoS();
        if (horizontalAccuracy != null) {
            if (horizontalAccuracy.getHorizontalAccuracy() != null) {
                cdrModel.setLcsQoSHorizontalAccuracy(String.valueOf(horizontalAccuracy.getHorizontalAccuracy().intValue()));
                stringBuilder.append(cdrModel.getLcsQoSHorizontalAccuracy()).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // LCS QoS VERTICAL ACCURACY
        LCSQoS verticalAccuracy = gmlcCdrState.getLcsQoS();
        if (verticalAccuracy != null) {
            if (verticalAccuracy.getVerticalAccuracy() != null) {
                cdrModel.setLcsQoSverticalAccuracy(String.valueOf(verticalAccuracy.getVerticalAccuracy().intValue()));
                stringBuilder.append(cdrModel.getLcsQoSverticalAccuracy()).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // LCS QoS VERTICAL COORDINATE REQUESTED
        LCSQoS verticalCoordinateRequest = gmlcCdrState.getLcsQoS();
        if (verticalCoordinateRequest != null) {
            if (verticalCoordinateRequest.getVerticalCoordinateRequest() || !verticalCoordinateRequest.getVerticalCoordinateRequest()) {
                cdrModel.setLcsQoSVerticalCoordinateRequest(String.valueOf(verticalCoordinateRequest.getVerticalCoordinateRequest()));
                stringBuilder.append(cdrModel.getLcsQoSVerticalCoordinateRequest()).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // LCS QoS RESPONSE TIME
        LCSQoS responseTime = gmlcCdrState.getLcsQoS();
        if (responseTime != null) {
            if (responseTime.getResponseTime() != null) {
                cdrModel.setLcsQoSResponseTime(String.valueOf(responseTime.getResponseTime().getResponseTimeCategory()));
                stringBuilder.append(cdrModel.getLcsQoSResponseTime()).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // LCS QoS CLASS
        LCSQoSClass lcsQoSClass = gmlcCdrState.getLteLcsQoSClass();
        if (lcsQoSClass != null) {
            if (lcsQoSClass.getValue() == 0)
                cdrModel.setLcsQoSClass("LCS-QoS-ASSURED");
            if (lcsQoSClass.getValue() == 1)
                cdrModel.setLcsQoSClass("LCS-QoS-BESTEFFORT");

            stringBuilder.append(cdrModel.getLcsQoSClass()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * ACCURACY FULFILLMENT INDICATOR
         */
        AccuracyFulfilmentIndicator accuracyFulfilmentIndicator = gmlcCdrState.getAccuracyFulfilmentIndicator();
        if (accuracyFulfilmentIndicator != null) {
            cdrModel.setAccuracyFulfilmentIndicator(String.valueOf(accuracyFulfilmentIndicator.getIndicator()));
            stringBuilder.append(cdrModel.getAccuracyFulfilmentIndicator()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * GERAN POSITIONING DATA
         */
        PositioningDataInformation geranPositioningDataInformation = gmlcCdrState.getGeranPositioningDataInformation();
        if (geranPositioningDataInformation != null) {
            try {
                HashMap<String, Integer> methodsAndUsage = geranPositioningDataInformation.getPositioningDataSet();
                StringBuilder geranPositioningDataInfo = new StringBuilder();
                int itemCounter = 0;
                for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                    itemCounter++;
                    String method = item.getKey();
                    Integer usage = item.getValue();
                    geranPositioningDataInfo.append("method=").append(method).append(" usage=").append(usage);
                    if (methodsAndUsage.size() != itemCounter)
                        geranPositioningDataInfo.append(" -- ");
                }
                cdrModel.setGeranPositioningDataInformation(String.valueOf(geranPositioningDataInfo));
            } catch (MAPException e) {
                logger.severe(e.getMessage());
            }
            stringBuilder.append(cdrModel.getGeranPositioningDataInformation()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * GERAN GANSS POSITIONING DATA
         */
        GeranGANSSpositioningData geranGANSSPositioningDataInformation = gmlcCdrState.getGeranGANSSpositioningData();
        if (geranGANSSPositioningDataInformation != null) {
            try {
                Multimap<String, String> methodsAndGanssIds = geranGANSSPositioningDataInformation.getGeranGANSSPositioningMethodsAndGANSSIds();
                StringBuilder geranGANSSPosDataInfo = new StringBuilder();
                String method = null, ganssId = null;
                int i = 0, usage;
                for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                    if (method != null || ganssId != null)
                        geranGANSSPosDataInfo.append(" -- ");
                    method = item.getKey();
                    ganssId = item.getValue();
                    usage = geranGANSSPositioningDataInformation.getUsageCode(geranGANSSPositioningDataInformation.getData(), i+1);
                    geranGANSSPosDataInfo.append("method=").append(method).append(" ganssId=").append(ganssId).append(" usage=").append(usage);
                    i++;
                }
                cdrModel.setGeranGANSSPositioningDataInformation(String.valueOf(geranGANSSPosDataInfo));
            } catch (MAPException e) {
                logger.severe(e.getMessage());
            }
            stringBuilder.append(cdrModel.getGeranGANSSPositioningDataInformation()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * UTRAN POSITIONING DATA
         */
        UtranPositioningDataInfo utranPositioningDataInfo = gmlcCdrState.getUtranPositioningDataInfo();
        if (utranPositioningDataInfo != null) {
            try {
                HashMap<String, Integer> methodsAndUsage = utranPositioningDataInfo.getUtranPositioningDataSet();
                StringBuilder utranPosDataInfo = new StringBuilder();
                int itemCounter = 0;
                for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                    itemCounter++;
                    String method = item.getKey();
                    Integer usage = item.getValue();
                    utranPosDataInfo.append("method=").append(method).append(" usage=").append(usage);
                    if (methodsAndUsage.size() != itemCounter)
                        utranPosDataInfo.append(" -- ");
                }
                cdrModel.setUtranPositioningDataInfo(String.valueOf(utranPosDataInfo));
            } catch (MAPException e) {
                logger.severe(e.getMessage());
            }
            stringBuilder.append(cdrModel.getUtranPositioningDataInfo()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * UTRAN GANSS POSITIONING DATA
         */
        UtranGANSSpositioningData utranGANSSpositioningData = gmlcCdrState.getUtranGANSSpositioningData();
        if (utranGANSSpositioningData != null) {
            try {
                Multimap<String, String> methodsAndGanssIds = utranGANSSpositioningData.getUtranGANSSPositioningMethodsAndGANSSIds();
                StringBuilder utranGANSSPosDataInfo = new StringBuilder();
                String method = null, ganssId = null;
                int i = 0, usage;
                for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                    if (method != null || ganssId != null)
                        utranGANSSPosDataInfo.append(" -- ");
                    method = item.getKey();
                    ganssId = item.getValue();
                    usage = utranGANSSpositioningData.getUsageCode(utranGANSSpositioningData.getData(), i);
                    utranGANSSPosDataInfo.append("method=").append(method).append(" ganssId=").append(ganssId).append(" usage=").append(usage);
                    i++;
                }
                cdrModel.setUtranGANSSPositioningData(String.valueOf(utranGANSSPosDataInfo));
            } catch (MAPException e) {
                logger.severe(e.getMessage());
            }
            stringBuilder.append(cdrModel.getUtranGANSSPositioningData()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * UTRAN ADDITIONAL POSITIONING DATA
         */
        UtranAdditionalPositioningData utranAdditionalPositioningData = gmlcCdrState.getUtranAdditionalPositioningData();
        if (utranAdditionalPositioningData != null) {
            try {
                Multimap<String, String> methodsAndAddPosIds = utranAdditionalPositioningData.getUtranAdditionalPositioningMethodsAndIds();
                StringBuilder utranAddPositioningData = new StringBuilder();
                String method = null, id = null;
                int i = 0, usage;
                for (Map.Entry<String, String> item : methodsAndAddPosIds.entries()) {
                    if (method != null || id != null)
                        utranAddPositioningData.append(" -- ");
                    method = item.getKey();
                    id = item.getValue();
                    usage = utranAdditionalPositioningData.getUsageCode(utranAdditionalPositioningData.getData(), i);
                    utranAddPositioningData.append("method=").append(method).append(" addPosId=").append(id).append(" usage=").append(usage);
                    i++;
                }
                cdrModel.setUtranAdditionalPositioningDataInfo(String.valueOf(utranAddPositioningData));
            } catch (MAPException e) {
                logger.severe(e.getMessage());
            }
            stringBuilder.append(cdrModel.getUtranAdditionalPositioningDataInfo()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * EUTRAN POSITIONING DATA
         */
        EUTRANPositioningData eutranPositioningData = gmlcCdrState.getEUTRANPositioningData();
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
                    cdrModel.setEutranPositioningDataInfo(String.valueOf(positioningDataInfo));
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
                    cdrModel.setEutranPositioningDataInfo(String.valueOf(gnssPositioningDataInfo));
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
                    cdrModel.setEutranPositioningDataInfo(String.valueOf(eutranAddPositioningData));
                }
            } catch (Exception e) {
                logger.severe(e.getMessage());
            }
            stringBuilder.append(cdrModel.getEutranPositioningDataInfo()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * VELOCITY ESTIMATE
         */
        // HORIZONTAL VELOCITY ESTIMATE
        VelocityEstimate horizontalVelocityEstimate = gmlcCdrState.getVelocityEstimate();
        if (horizontalVelocityEstimate != null) {
            cdrModel.setHorizontalVelocityEstimate(String.valueOf(horizontalVelocityEstimate.getHorizontalSpeed()));
            stringBuilder.append(cdrModel.getHorizontalVelocityEstimate()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // VELOCITY ESTIMATE BEARING
        VelocityEstimate velocityEstimateBearing = gmlcCdrState.getVelocityEstimate();
        if (velocityEstimateBearing != null) {
            cdrModel.setVelocityEstimateBearing(String.valueOf(velocityEstimateBearing.getBearing()));
            stringBuilder.append(cdrModel.getVelocityEstimateBearing()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // VERTICAL VELOCITY ESTIMATE
        VelocityEstimate verticalVelocityEstimate = gmlcCdrState.getVelocityEstimate();
        if (verticalVelocityEstimate != null) {
            cdrModel.setVerticalVelocityEstimate(String.valueOf(verticalVelocityEstimate.getVerticalSpeed()));
            stringBuilder.append(cdrModel.getVerticalVelocityEstimate()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // VELOCITY ESTIMATE HORIZONTAL UNCERTAINTY
        VelocityEstimate velocityHorizontalUncertainty = gmlcCdrState.getVelocityEstimate();
        if (velocityHorizontalUncertainty != null) {
            cdrModel.setVelocityHorizontalUncertainty(String.valueOf(velocityHorizontalUncertainty.getUncertaintyHorizontalSpeed()));
            stringBuilder.append(cdrModel.getVelocityHorizontalUncertainty()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        // VELOCITY ESTIMATE VERTICAL UNCERTAINTY
        VelocityEstimate velocityVerticalUncertainty = gmlcCdrState.getVelocityEstimate();
        if (velocityVerticalUncertainty != null) {
            cdrModel.setVelocityVerticalUncertainty(String.valueOf(velocityVerticalUncertainty.getUncertaintyVerticalSpeed()));
            stringBuilder.append(cdrModel.getVelocityVerticalUncertainty()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // VELOCITY ESTIMATE TYPE
        VelocityEstimate velocityType = gmlcCdrState.getVelocityEstimate();
        if (velocityType != null) {
            cdrModel.setVelocityType(velocityType.getVelocityType().name());
            stringBuilder.append(cdrModel.getVelocityType()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LCS SERVICE TYPE ID
         */
        Integer lcsServiceTypeID = gmlcCdrState.getLcsServiceTypeID();
        if (lcsServiceTypeID != null) {
            cdrModel.setLcsServiceTypeID(String.valueOf(lcsServiceTypeID));
            stringBuilder.append(cdrModel.getLcsServiceTypeID()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * MO-LR SHORT-CIRCUIT INDICATOR
         */
        Boolean moLrShortCircuitIndicator = gmlcCdrState.isMoLrShortCircuitIndicator();
        if (moLrShortCircuitIndicator != null) {
            cdrModel.setMoLrShortCircuitIndicator(String.valueOf(moLrShortCircuitIndicator));
            stringBuilder.append(cdrModel.getMoLrShortCircuitIndicator()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * REPORTING PLMN LIST
         */
        ReportingPLMNList reportingPLMNList = gmlcCdrState.getReportingPLMNList();
        if (reportingPLMNList != null) {
            int plmnCounter = 0;
            String reportingPLMNListArray = "[ ";
            while (reportingPLMNList.getPlmnList().iterator().hasNext()) {
                reportingPLMNListArray = reportingPLMNListArray + reportingPLMNList.getPlmnList().get(plmnCounter);
                plmnCounter++;
                if (reportingPLMNList.getPlmnList().get(plmnCounter) != null) {
                    reportingPLMNListArray = reportingPLMNListArray + ", ";
                } else {
                    reportingPLMNListArray = reportingPLMNListArray + " ]";
                }
                cdrModel.setReportingPLMNList(reportingPLMNListArray);
                stringBuilder.append(cdrModel.getReportingPLMNList()).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * Client REFERENCE NUMBER
         */
        Integer clientReferenceNumber = gmlcCdrState.getClientReferenceNumber();
        if (clientReferenceNumber != null) {
            cdrModel.setClientReferenceNumber(String.valueOf(clientReferenceNumber));
            stringBuilder.append(cdrModel.getClientReferenceNumber()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LCS EVENT (MAP/LTE)
         */
        LCSEvent lcsEvent = gmlcCdrState.getLcsEvent();
        LocationEvent locationEvent = gmlcCdrState.getLocationEvent();
        if (lcsEvent != null) {
            // LCS EVENT (MAP)
            cdrModel.setLcsEvent(String.valueOf(lcsEvent.getEvent()));
            stringBuilder.append(getLocationEvent(lcsEvent.getEvent())).append(SEPARATOR);
        } else {
            if (locationEvent != null) {
                // LCS EVENT (LTE)
                cdrModel.setLcsEvent(String.valueOf(locationEvent.getValue()));
                stringBuilder.append(getLocationEvent(locationEvent.getValue())).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        }

        /**
         * DEFERRED MT LR RESPONSE INDICATOR
         */
        if (deferredMTLRResponseIndicator != null) {
            stringBuilder.append(deferredMTLRResponseIndicator).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * DEFERRED MT LR DATA
         */
        // LOCATION EVENT TYPE
        DeferredmtlrData deferredLocationEventType = gmlcCdrState.getDeferredmtlrData();
        if (deferredLocationEventType != null) {
            if (deferredLocationEventType.getDeferredLocationEventType() != null) {
                if (deferredLocationEventType.getDeferredLocationEventType().getEnteringIntoArea())
                    cdrModel.setDeferredLocationEventType("EnteringIntoArea");
                if (deferredLocationEventType.getDeferredLocationEventType().getBeingInsideArea())
                    cdrModel.setDeferredLocationEventType("InsideArea");
                if (deferredLocationEventType.getDeferredLocationEventType().getMsAvailable())
                    cdrModel.setDeferredLocationEventType("Available");
                if (deferredLocationEventType.getDeferredLocationEventType().getLeavingFromArea())
                    cdrModel.setDeferredLocationEventType("LeavingFromArea");

                stringBuilder.append(cdrModel.getDeferredLocationEventType()).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /*
         * DEFERRED MT-LR DATA LCS Location Info
         */
        DeferredmtlrData deferredLcsLocationInfo = gmlcCdrState.getDeferredmtlrData();
        if (deferredLcsLocationInfo != null) {
            if (deferredLcsLocationInfo.getLCSLocationInfo() != null) {
                if (deferredLcsLocationInfo.getLCSLocationInfo().getNetworkNodeNumber() != null) {
                    // DEFERRED MT-LR DATA NETWORK NODE NUMBER
                    cdrModel.setDeferredLcsLocationInfoNetworkNodeNumber(deferredLcsLocationInfo.getLCSLocationInfo().getNetworkNodeNumber().getAddress());
                    stringBuilder.append(cdrModel.getDeferredLcsLocationInfoNetworkNodeNumber()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getGprsNodeIndicator()) {
                    // DEFERRED MT-LR DATA GPRS NODE IND
                    cdrModel.setDeferredLcsLocationInfoGprsNodeIndicator(String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getGprsNodeIndicator()));
                    stringBuilder.append(cdrModel.getDeferredLcsLocationInfoGprsNodeIndicator()).append(SEPARATOR);
                } else {
                    stringBuilder.append(false).append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalNumber() != null) {
                    // DEFERRED MT-LR DATA ADDITIONAL NUMBER
                    if (deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalNumber().getMSCNumber() != null)
                        cdrModel.setDeferredLcsLocationInfoAdditionalNumber(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalNumber().getMSCNumber().getAddress());
                    else if (deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber() != null)
                        cdrModel.setDeferredLcsLocationInfoAdditionalNumber(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress());

                    stringBuilder.append(cdrModel.getDeferredLcsLocationInfoAdditionalNumber()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getLMSI() != null) {
                    // DEFERRED MT-LR DATA LMSI
                    String lmsiStr = bytesToHex(deferredLcsLocationInfo.getLCSLocationInfo().getLMSI().getData());
                    cdrModel.setDeferredLcsLocationInfoLMSI(lmsiStr);
                    stringBuilder.append(cdrModel.getDeferredLcsLocationInfoLMSI()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getMmeName() != null) {
                    // DEFERRED MT-LR DATA MME NAME
                    String mmeNameStr = new String(deferredLcsLocationInfo.getLCSLocationInfo().getMmeName().getData(), StandardCharsets.UTF_8);
                    cdrModel.setDeferredLcsLocationInfoMmeName(mmeNameStr);
                    stringBuilder.append(cdrModel.getDeferredLcsLocationInfoMmeName()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getAaaServerName() != null) {
                    // DEFERRED MT-LR DATA AAA SERVER NAME
                    String aaaServerNameStr = new String(deferredLcsLocationInfo.getLCSLocationInfo().getAaaServerName().getData(), StandardCharsets.UTF_8);
                    cdrModel.setDeferredLcsLocationInfoAaaServerName(aaaServerNameStr);
                    stringBuilder.append(cdrModel.getDeferredLcsLocationInfoAaaServerName()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getSupportedLCSCapabilitySets() != null) {
                    String LcsCsR98_99 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99());
                    String LcsCsR4 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease4());
                    String LcsCsR5 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease5());
                    String LcsCsR6 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease6());
                    String LcsCsR7 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease7());
                    // DEFERRED MT-LR DATA LCS CAPS R98_99
                    cdrModel.setDeferredLcsCsR98_99(LcsCsR98_99);
                    stringBuilder.append(cdrModel.getDeferredLcsCsR98_99()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA LCS CAPS R4
                    cdrModel.setDeferredLcsCsR4(LcsCsR4);
                    stringBuilder.append(cdrModel.getDeferredLcsCsR4()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA LCS CAPS R5
                    cdrModel.setDeferredLcsCsR5(LcsCsR5);
                    stringBuilder.append(cdrModel.getDeferredLcsCsR5()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA LCS CAPS R6
                    cdrModel.setDeferredLcsCsR6(LcsCsR6);
                    stringBuilder.append(cdrModel.getDeferredLcsCsR6()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA LCS CAPS R7
                    cdrModel.setDeferredLcsCsR7(LcsCsR7);
                    stringBuilder.append(cdrModel.getDeferredLcsCsR7()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                }
                if (deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                    String aLCSCSR98_99 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease98_99());
                    String aLCSCSR4 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease4());
                    String aLCSCSR5 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease5());
                    String aLCSCSR6 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease6());
                    String aLCSCSR7 = String.valueOf(deferredLcsLocationInfo.getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease7());
                    // DEFERRED MT-LR DATA ADD LCS CAPS R98_99
                    cdrModel.setDeferredALcsCsR98_99(aLCSCSR98_99);
                    stringBuilder.append(cdrModel.getDeferredALcsCsR98_99()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA ADD LCS CAPS R4
                    cdrModel.setDeferredALcsCsR4(aLCSCSR4);
                    stringBuilder.append(cdrModel.getDeferredALcsCsR4()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA ADD LCS CAPS R5
                    cdrModel.setDeferredALcsCsR5(aLCSCSR5);
                    stringBuilder.append(cdrModel.getDeferredALcsCsR5()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA ADD LCS CAPS R6
                    cdrModel.setDeferredALcsCsR6(aLCSCSR6);
                    stringBuilder.append(cdrModel.getDeferredALcsCsR6()).append(SEPARATOR);
                    // DEFERRED MT-LR DATA ADD LCS CAPS R7
                    cdrModel.setDeferredALcsCsR7(aLCSCSR7);
                    stringBuilder.append(cdrModel.getDeferredALcsCsR7()).append(SEPARATOR);
                } else {
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                    stringBuilder.append(SEPARATOR);
                }
            }
        } else {
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }
        // TERMINATION CAUSE
        DeferredmtlrData deferredTerminationCause = gmlcCdrState.getDeferredmtlrData();
        if (deferredTerminationCause != null) {
            cdrModel.setDeferredTerminationCause(String.valueOf(deferredTerminationCause.getTerminationCause()));
            stringBuilder.append(cdrModel.getDeferredTerminationCause()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * PERIODIC LDR INFO
         */
        // REPORTING AMOUNT
        PeriodicLDRInfo periodicReportingAmount = gmlcCdrState.getPeriodicLDRInfo();
        if (periodicReportingAmount != null) {
            cdrModel.setPeriodicReportingAmount(String.valueOf(periodicReportingAmount.getReportingAmount()));
            stringBuilder.append(cdrModel.getPeriodicReportingAmount()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        // REPORTING INTERVAL
        PeriodicLDRInfo periodicReportingInterval = gmlcCdrState.getPeriodicLDRInfo();
        if (periodicReportingInterval != null) {
            cdrModel.setPeriodicReportingInterval(String.valueOf(periodicReportingInterval.getReportingInterval()));
            stringBuilder.append(cdrModel.getPeriodicReportingInterval()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LCS REFERENCE NUMBER
         */
        Integer lcsReferenceNumber = gmlcCdrState.getLcsReferenceNumber();
        if (lcsReferenceNumber != null) {
            cdrModel.setLcsReferenceNumber(String.valueOf(lcsReferenceNumber));
            stringBuilder.append(cdrModel.getLcsReferenceNumber()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SEQUENCE NUMBER
         */
        Integer sequenceNumber = gmlcCdrState.getSequenceNumber();
        if (sequenceNumber != null) {
            cdrModel.setSequenceNumber(String.valueOf(sequenceNumber));
            stringBuilder.append(cdrModel.getSequenceNumber()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * BAROMETRIC PRESSURE
         */
        Long barometricPressureMeasurement = gmlcCdrState.getBarometricPressureMeasurement();
        Integer utranBarometricPressureMeasurement = gmlcCdrState.getUtranBaroPressureMeas();
        if (barometricPressureMeasurement != null || utranBarometricPressureMeasurement != null) {
            if (barometricPressureMeasurement != null) {
                cdrModel.setBarometricPressureMeasurement(String.valueOf(barometricPressureMeasurement));
                stringBuilder.append(cdrModel.getBarometricPressureMeasurement()).append(SEPARATOR);
            } else {
                cdrModel.setBarometricPressureMeasurement(String.valueOf(utranBarometricPressureMeasurement));
                stringBuilder.append(cdrModel.getBarometricPressureMeasurement()).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * CIVIC ADDRESS
         */
        String civicAddress = gmlcCdrState.getCivicAddress();
        UtranCivicAddress utranCivicAddress = gmlcCdrState.getUtranCivicAddress();
        if (civicAddress != null || utranCivicAddress != null) {
            if (civicAddress != null) {
                cdrModel.setCivicAddress(civicAddress);
                StringBuilder sb = getCivicAddress(civicAddress);
                stringBuilder.append(sb).append(SEPARATOR);
            } else {
                Charset charset = StandardCharsets.UTF_8;
                String utranCivicAddressStr = new String(utranCivicAddress.getData(), charset);
                StringBuilder sb = getCivicAddress(utranCivicAddressStr);
                stringBuilder.append(sb).append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * na-ESRK Request
         */
        Boolean naEsrkRequest = gmlcCdrState.getNaEsrkRequest();
        if (naEsrkRequest != null) {
            cdrModel.setNaEsrkRequest(String.valueOf(naEsrkRequest));
            stringBuilder.append(cdrModel.getNaEsrkRequest()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * na-ESRD
         */
        ISDNAddressString naESRD = gmlcCdrState.getNaESRD();
        if (naESRD != null) {
            cdrModel.setNaESRD(naESRD.getAddress());
            stringBuilder.append(cdrModel.getNaESRD()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * na-ESRK
         */
        ISDNAddressString naESRK = gmlcCdrState.getNaESRD();
        if (naESRK != null) {
            cdrModel.setNaESRK(naESRK.getAddress());
            stringBuilder.append(cdrModel.getNaESRK()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * 1xRTT-RCID
         */
        String oneXRTTRCID = gmlcCdrState.getOneXRTTRCID();
        if (oneXRTTRCID != null) {
            cdrModel.setOneXRTTRCID(oneXRTTRCID);
            stringBuilder.append(cdrModel.getOneXRTTRCID()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * AMF-Instance-Id
         */
        String amfInstanceId = gmlcCdrState.getAmfInstanceId();
        if (amfInstanceId != null) {
            cdrModel.setAmfInstanceId(amfInstanceId);
            stringBuilder.append(cdrModel.getAmfInstanceId()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SLh RIA-Flags
         */
        Long riaFlags = gmlcCdrState.getRiaFlags();
        if (riaFlags != null) {
            cdrModel.setRiaFlags(riaFlags);
            stringBuilder.append(cdrModel.getRiaFlags()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SLg PLR-Flags
         */
        Long plrFlags = gmlcCdrState.getPlrFlags();
        if (plrFlags != null) {
            cdrModel.setPlrFlags(plrFlags);
            stringBuilder.append(cdrModel.getPlrFlags()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SLg PLA-Flags
         */
        Long plaFlags = gmlcCdrState.getPlaFlags();
        if (plaFlags != null) {
            cdrModel.setPlaFlags(plaFlags);
            stringBuilder.append(cdrModel.getPlaFlags()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SLg LRR-Flags
         */
        Long lrrFlags = gmlcCdrState.getLrrFlags();
        if (lrrFlags != null) {
            cdrModel.setLrrFlags(lrrFlags);
            stringBuilder.append(cdrModel.getLrrFlags()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * SLg LRA-Flags
         */
        Long lraFlags = gmlcCdrState.getLraFlags();
        if (lraFlags != null) {
            cdrModel.setLraFlags(lraFlags);
            stringBuilder.append(cdrModel.getLraFlags()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * Subscriber Status from ATI or PSI
         */
        if (state != null) {
            stringBuilder.append(state).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }
        /**
         * Subscriber Not Reachable Reason from ATI or PSI
         */
        if (notReachableReasonState != null) {
            stringBuilder.append(notReachableReasonState).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * Local Time Zone from Sh UDR/UDA
         */
        if (localTimeZone != null) {
            stringBuilder.append(localTimeZone.getTimeZone()).append(SEPARATOR);
            stringBuilder.append(localTimeZone.getDaylightSavingTime()).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }
        /**
         * RAT type from Sh UDR/UDA or ATI or PSI
         */
        if (ratTypeCode != null) {
            stringBuilder.append(getRatType(ratTypeCode)).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * LOCATION NUMBER from ATI or PSI or Sh UDR
         */
        if (locationNumber) {
            if (locationNumberOddFlag) {
                stringBuilder.append(locationNumberOddFlag).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (locationNumberNAI != -1) {
                stringBuilder.append(locationNumberNAI).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (locationNumberNNI != -1) {
                stringBuilder.append(locationNumberNNI).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (locationNumberNPI != -1) {
                stringBuilder.append(locationNumberNPI).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (locationNumberAddressRepresentationRestrictedIndicator != -1) {
                stringBuilder.append(locationNumberAddressRepresentationRestrictedIndicator).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (locationNumberScreeningIndicator != -1) {
                stringBuilder.append(locationNumberScreeningIndicator).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            if (locationNumberAddress != null) {
                stringBuilder.append(locationNumberAddress).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR).append(SEPARATOR).append(SEPARATOR).append(SEPARATOR).append(SEPARATOR).append(SEPARATOR).append(SEPARATOR);
        }

        /**
         * MNP INFO RESULT (from ATI or PSI)
         */
        if (mnpInfoResult != null) {
            // MNP NUMBER PORTABILITY STATUS
            if (mnpInfoResult.getNumberPortabilityStatus() != null)
                stringBuilder.append(mnpInfoResult.getNumberPortabilityStatus().name()).append(SEPARATOR);
            else
                stringBuilder.append(SEPARATOR);
            // MNP IMSI
            if (mnpInfoResult.getIMSI() != null)
                stringBuilder.append(new String(mnpInfoResult.getIMSI().getData().getBytes())).append(SEPARATOR);
            else
                stringBuilder.append(SEPARATOR);
            // MNP MSISDN
            if (mnpInfoResult.getMSISDN() != null)
                stringBuilder.append(mnpInfoResult.getMSISDN().getAddress()).append(SEPARATOR);
            else
                stringBuilder.append(SEPARATOR);
            // MNP ROUTEING NUMBER
            if (mnpInfoResult.getRouteingNumber() != null)
                stringBuilder.append(mnpInfoResult.getRouteingNumber().getRouteingNumber()).append(SEPARATOR);
            else
                stringBuilder.append(SEPARATOR);
        } else {

            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }

        /**
         * MS CLASSMARK 2
         */
        if (msClassmark != null) {
            stringBuilder.append(bytesToHexString(msClassmark.getData())).append(SEPARATOR);
        } else {
            stringBuilder.append(SEPARATOR);
        }

        /**
         * GPRS MS CLASS
         */
        if (gprsMsClass != null) {
            // GPRS MS CLASS MS RADIO ACCESS CAPABILITY
            if (gprsMsClass.getMSRadioAccessCapability() != null) {
                stringBuilder.append(bytesToHexString(gprsMsClass.getMSRadioAccessCapability().getData())).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
            // GPRS MS CLASS MS NETWORK CAPABILITY
            if (gprsMsClass.getMSNetworkCapability() != null) {
                stringBuilder.append(bytesToHexString(gprsMsClass.getMSNetworkCapability().getData())).append(SEPARATOR);
            } else {
                stringBuilder.append(SEPARATOR);
            }
        } else {
            stringBuilder.append(SEPARATOR);
            stringBuilder.append(SEPARATOR);
        }

        return stringBuilder.toString();
    }

    private static String getLocationEvent(Integer lcsEvent) {
        String locationEvent = null;
        if (lcsEvent != null) {
            switch (lcsEvent) {
                case 0:
                    locationEvent = "EMERGENCY_CALL_ORIGINATION";
                    break;
                case 1:
                    locationEvent = "EMERGENCY_CALL_RELEASE";
                    break;
                case 2:
                    locationEvent = "MO_LR";
                    break;
                case 3:
                    locationEvent = "EMERGENCY_CALL_HANDOVER";
                    break;
                case 4:
                    locationEvent = "DEFERRED_MT_LR_RESPONSE";
                    break;
                case 5:
                    locationEvent = "DEFERRED_MO_LR_TTTP_INITIATION";
                    break;
                case 6:
                    locationEvent = "DELAYED_LOCATION_REPORTING";
                    break;
                case 7:
                    locationEvent = "HANDOVER_TO_5GC";
                    break;
                default:
                    break;
            }
        }
        return locationEvent;
    }

    private StringBuilder getCivicAddress(String civicAddressStr) {
        CivicAddressXmlReader reader = new CivicAddressXmlReader();
        reader.civicAddressXMLReader(civicAddressStr);
        CivicAddressElements civicAddressElements = reader.getCivicAddressElements();
        StringBuilder sb = new StringBuilder();
        if (civicAddressElements != null) {
            if (civicAddressElements.getCountry() != null)
                sb.append("Country=").append(civicAddressElements.getCountry());
            if (civicAddressElements.getA1() != null)
                sb.append(" A1=").append(civicAddressElements.getA1());
            if (civicAddressElements.getA2() != null)
                sb.append(" A2=").append(civicAddressElements.getA2());
            if (civicAddressElements.getA3() != null)
                sb.append(" A3=").append(civicAddressElements.getA3());
            if (civicAddressElements.getA4() != null)
                sb.append(" A4=").append(civicAddressElements.getA4());
            if (civicAddressElements.getA5() != null)
                sb.append(" A5=").append(civicAddressElements.getA5());
            if (civicAddressElements.getA6() != null)
                sb.append(" A6=").append(civicAddressElements.getA6());
            if (civicAddressElements.getPrm() != null)
                sb.append(" PRM=").append(civicAddressElements.getPrm());
            if (civicAddressElements.getPrd() != null)
                sb.append(" PRD=").append(civicAddressElements.getPrd());
            if (civicAddressElements.getRd() != null)
                sb.append(" RD=").append(civicAddressElements.getRd());
            if (civicAddressElements.getSts() != null)
                sb.append(" STS=").append(civicAddressElements.getSts());
            if (civicAddressElements.getPod() != null)
                sb.append(" POD").append(civicAddressElements.getPod());
            if (civicAddressElements.getPom() != null)
                sb.append(" POM").append(civicAddressElements.getPom());
            if (civicAddressElements.getRdsec() != null)
                sb.append(" RDSEC=").append(civicAddressElements.getRdsec());
            if (civicAddressElements.getRdbr() != null)
                sb.append(" RDBR=").append(civicAddressElements.getRdbr());
            if (civicAddressElements.getRdsubbr() != null)
                sb.append(" RDSUBBR=").append(civicAddressElements.getRdsubbr());
            if (civicAddressElements.getHno() != null)
                sb.append(" HNO=").append(civicAddressElements.getHno());
            if (civicAddressElements.getHns() != null)
                sb.append(" HNS=").append(civicAddressElements.getHns());
            if (civicAddressElements.getLmk() != null)
                sb.append(" LMK=").append(civicAddressElements.getLmk());
            if (civicAddressElements.getLoc() != null)
                sb.append(" LOC=").append(civicAddressElements.getLoc());
            if (civicAddressElements.getFlr() != null)
                sb.append(" FLR=").append(civicAddressElements.getFlr());
            if (civicAddressElements.getNam() != null)
                sb.append(" NAM=").append(civicAddressElements.getNam());
            if (civicAddressElements.getPc() != null)
                sb.append(" PC=").append(civicAddressElements.getPc());
            if (civicAddressElements.getBld() != null)
                sb.append(" BLD=").append(civicAddressElements.getBld());
            if (civicAddressElements.getUnit() != null)
                sb.append(" UNIT=").append(civicAddressElements.getUnit());
            if (civicAddressElements.getRoom() != null)
                sb.append(" ROOM=").append(civicAddressElements.getRoom());
            if (civicAddressElements.getSeat() != null)
                sb.append(" SEAT=").append(civicAddressElements.getSeat());
            if (civicAddressElements.getPlc() != null)
                sb.append(" PLC=").append(civicAddressElements.getPlc());
            if (civicAddressElements.getPcn() != null)
                sb.append(" PCN=").append(civicAddressElements.getPcn());
            if (civicAddressElements.getPobox() != null)
                sb.append(" POBOX=").append(civicAddressElements.getPobox());
            if (civicAddressElements.getPn() != null)
                sb.append(" PN=").append(civicAddressElements.getPn());
            if (civicAddressElements.getMp() != null)
                sb.append(" MP=").append(civicAddressElements.getMp());
            if (civicAddressElements.getStp() != null)
                sb.append(" STP=").append(civicAddressElements.getStp());
            if (civicAddressElements.getHnp() != null)
                sb.append(" HNP=").append(civicAddressElements.getHnp());
        }
        return sb;
    }

    private String getRatType(Integer ratTypeCode) {
        String ratType = null;
        if (ratTypeCode != null) {
            switch (ratTypeCode) {
                case 0:
                    ratType = "WLAN";
                    break;
                case 1:
                    ratType = "VIRTUAL";
                    break;
                case 1000:
                    ratType = "UTRAN";
                    break;
                case 1001:
                    ratType = "GERAN";
                    break;
                case 1002:
                    ratType = "GAN";
                    break;
                case 1003:
                    ratType = "HSPA_EVOLUTION";
                    break;
                case 1004:
                    ratType = "EUTRAN";
                    break;
                case 1005:
                    ratType = "EUTRAN-NB-IoT";
                    break;
                case 1006:
                    ratType = "NR";
                    break;
                case 1007:
                    ratType = "LTE-M";
                    break;
                case 2000:
                    ratType = "CDMA2000_1X";
                    break;
                case 2001:
                    ratType = "HRPD";
                    break;
                case 2002:
                    ratType = "UMB";
                    break;
                case 2003:
                    ratType = "EHRPD";
                    break;
                default:
                    break;
            }
        }
        return ratType;
    }

    private String bytesToHexString(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
