package org.mobicents.gmlc.slee.http;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.map.PslResponseParams;
import org.mobicents.gmlc.slee.map.SriLcsResponseParams;
import org.mobicents.gmlc.slee.primitives.CivicAddressElements;
import org.mobicents.gmlc.slee.primitives.CivicAddressXmlReader;
import org.mobicents.gmlc.slee.primitives.EllipsoidPoint;
import org.mobicents.gmlc.slee.primitives.PolygonImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.LAIFixedLength;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;

import javax.xml.bind.DatatypeConverter;
import java.awt.geom.Point2D;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mobicents.gmlc.slee.gis.GeographicHelper.polygonCentroid;
import static org.mobicents.gmlc.slee.http.JsonWriter.bytesToHexString;
import static org.mobicents.gmlc.slee.http.JsonWriter.write3gppAaaServerName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAccuracyFulfilmentIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalLCSCapabilitySets;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalVGmlcAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAgeOfLocationEstimate;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAltitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAngleOfMajorAxis;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeBarometricPressure;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeBearing;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCivicAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeClientReferenceNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeConfidence;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeDeferredMTlrResponseIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranGanssPositioningGanssId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranGanssPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranGanssPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGprsNodeIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeHGmlcAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeHorizontalSpeed;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImei;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeIncludedAngle;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeInnerRadius;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLac;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLatitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLcsReferenceNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLmsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLongitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMcc;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMmeName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMmeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnc;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMoLrShortCircuitIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMscNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMsisdn;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetwork;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNumberOfPoints;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOffsetAngle;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperation;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writePprAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeProtocol;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeServiceAreaCode;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnRealm;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSupportedLCSCapabilitySets;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeTypeOfShape;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertainty;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertaintyAltitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertaintyHorizontalSpeed;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertaintyInnerRadius;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertaintySemiMajorAxis;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertaintySemiMinorAxis;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertaintyVerticalSpeed;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranAddPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranAddPositioningPosId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranAddPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranGanssPositioningGanssId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranGanssPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranGanssPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUtranPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVGmlcAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVelocityType;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVerticalSpeed;
import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class PslResponseJsonBuilder {

    protected static final Logger logger = Logger.getLogger(PslResponseJsonBuilder.class);

    public PslResponseJsonBuilder() {
    }

    /**
     * Handle generating the appropriate HTTP response in JSON format
     *
     * @param sriLcs                Subscriber Information values gathered from SRILCS response event
     * @param psl                   Subscriber Information values gathered from PSL response event
     * @param sriPslMsisdn          Subscriber's MSISDN
     * @param sriPslImsi            Subscriber's IMSI
     * @param pslImei               Subscriber's IMEI
     * @param clientReferenceNumber Reference Number gathered from the originating HTTP request sent by the GMLC Client
     * @param lcsReferenceNumber    LCS-ReferenceNumber exchanged between the GMLC and the UMTS core network
     */
    public static String buildJsonResponseForPsl(SriLcsResponseParams sriLcs, PslResponseParams psl, String sriPslMsisdn, String sriPslImsi,
                                                 String pslImei, Integer clientReferenceNumber, Integer lcsReferenceNumber) throws MAPException {

        String typeOfShape, additionalTypeOfShape = null;
        Double latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, uncertaintyAltitude, uncertaintyInnerRadius,
            angleOfMajorAxis, offsetAngle, includedAngle;
        latitude = longitude = uncertainty = uncertaintySemiMajorAxis = uncertaintySemiMinorAxis = uncertaintyAltitude = uncertaintyInnerRadius =
            angleOfMajorAxis = offsetAngle = includedAngle = null;
        Double additionalLatitude, additionalLongitude, additionalUncertainty, additionalUncertaintySemiMajorAxis, additionalUncertaintySemiMinorAxis,
            additionalUncertaintyAltitude, additionalUncertaintyInnerRadius, additionalAngleOfMajorAxis, additionalOffsetAngle, additionalIncludedAngle;
        additionalLatitude = additionalLongitude = additionalUncertainty = additionalUncertaintySemiMajorAxis = additionalUncertaintySemiMinorAxis =
            additionalUncertaintyAltitude = additionalUncertaintyInnerRadius = additionalAngleOfMajorAxis = additionalOffsetAngle =
                additionalIncludedAngle = null;
        int mcc, mnc, lac, ciOrSac, aol, numberOfPoints, confidence, additionalConfidence, altitude, additionalAltitude,
            innerRadius, additionalInnerRadius, accuracyFulfilmentIndicator, horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed,
            uncertaintyVerticalSpeed;
        mcc = mnc = lac = ciOrSac = numberOfPoints = confidence = additionalConfidence = altitude = additionalAltitude =
            innerRadius = additionalInnerRadius = horizontalSpeed = bearing = verticalSpeed = uncertaintyHorizontalSpeed =
                uncertaintyVerticalSpeed = -1;
        PolygonImpl polygon = null;
        EllipsoidPoint[] polygonEllipsoidPoints;
        Double[][] polygonArray;

        JsonObject sriForLcsJsonObject = null;
        JsonObject pslJsonObject = null;
        JsonObject sriPslJsonObject = new JsonObject();
        writeNetwork("GSM/UMTS", sriPslJsonObject);
        writeProtocol("MAP", sriPslJsonObject);
        writeOperation("SRILCS-PSL", sriPslJsonObject);
        writeOperationResult("SUCCESS", sriPslJsonObject);
        if (pslImei != null)
            writeImei(pslImei, sriPslJsonObject);
        writeClientReferenceNumber(clientReferenceNumber, sriPslJsonObject);
        writeLcsReferenceNumber(lcsReferenceNumber, sriPslJsonObject);

        /**************************/
        /*** MAP SRILCS values ***/
        /*************************/
        if (sriLcs != null) {
            sriForLcsJsonObject = new JsonObject();
            // Get SRILCS response values

            /*** targetMS [0] SubscriberIdentity ***/
            if (sriLcs.getMsisdn() != null) {
                writeMsisdn(sriLcs.getMsisdn().getAddress(), sriForLcsJsonObject);
            } else {
                writeMsisdn(sriPslMsisdn, sriForLcsJsonObject);
            }
            if (sriLcs.getImsi() != null) {
                String imsi = new String(sriLcs.getImsi().getData().getBytes());
                writeImsi(imsi, sriForLcsJsonObject);
            } else {
                writeImsi(sriPslImsi, sriForLcsJsonObject);
            }

            /*** lmsi [0] LMSI OPTIONAL ***/
            if (sriLcs.getLmsi() != null) {
                String lmsi = bytesToHex(sriLcs.getLmsi().getData());
                writeLmsi(lmsi, sriForLcsJsonObject);
            }

            /*** lcsLocationInfo [1] LCSLocationInfo ***/
            if (sriLcs.getLcsLocationInfo() != null) {
                JsonObject sriLcsLocationInfo = new JsonObject();
                /*** networkNode-Number ISDN-AddressString ***/
                if (sriLcs.getLcsLocationInfo().getNetworkNodeNumber() != null) {
                    writeNetworkNodeNumber(sriLcs.getLcsLocationInfo().getNetworkNodeNumber().getAddress(), sriLcsLocationInfo);
                }
                /*** gprsNodeIndicator [2] NULL OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getGprsNodeIndicator()) {
                    // gprsNodeIndicator is set only if the SGSN number is sent as the Network Node Number
                    writeGprsNodeIndicator(sriLcs.getLcsLocationInfo().getGprsNodeIndicator(), sriLcsLocationInfo);
                }
                /*** additional-Number [3] Additional-Number OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getAdditionalNumber() != null) {
                    String additionalNumber = null;
                    if (sriLcs.getLcsLocationInfo().getAdditionalNumber().getMSCNumber() != null)
                        additionalNumber = sriLcs.getLcsLocationInfo().getAdditionalNumber().getMSCNumber().getAddress();
                    else if (sriLcs.getLcsLocationInfo().getAdditionalNumber().getSGSNNumber() != null)
                        additionalNumber = sriLcs.getLcsLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress();
                    writeAdditionalNetworkNodeNumber(additionalNumber, sriLcsLocationInfo);
                }
                /*** supportedLCS-CapabilitySets [4] SupportedLCS-CapabilitySets OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getSupportedLCSCapabilitySets() != null) {
                    JsonObject supportedLCSCapSetsJsonObject = new JsonObject();
                    boolean supportedLCSCapabilitySetRelease98_99 = sriLcs.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99();
                    boolean supportedLCSCapabilitySetRelease4 = sriLcs.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease4();
                    boolean supportedLCSCapabilitySetRelease5 = sriLcs.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease5();
                    boolean supportedLCSCapabilitySetRelease6 = sriLcs.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease6();
                    boolean supportedLCSCapabilitySetRelease7 = sriLcs.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease7();
                    writeSupportedLCSCapabilitySets(true, supportedLCSCapabilitySetRelease98_99, supportedLCSCapabilitySetRelease4,
                        supportedLCSCapabilitySetRelease5, supportedLCSCapabilitySetRelease6, supportedLCSCapabilitySetRelease7, supportedLCSCapSetsJsonObject);
                    sriLcsLocationInfo.add("SupportedLCSCapabilitySets", supportedLCSCapSetsJsonObject);
                }
                /*** additional-LCS-CapabilitySets [5] SupportedLCS-CapabilitySets OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                    JsonObject additionalLCSCapSetsJsonObject = new JsonObject();
                    boolean addSupportedLCSCapabilitySetRelease98_99 = sriLcs.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease98_99();
                    boolean addSupportedLCSCapabilitySetRelease4 = sriLcs.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease4();
                    boolean addSupportedLCSCapabilitySetRelease5 = sriLcs.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease5();
                    boolean addSupportedLCSCapabilitySetRelease6 = sriLcs.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease6();
                    boolean addSupportedLCSCapabilitySetRelease7 = sriLcs.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease7();
                    writeAdditionalLCSCapabilitySets(true, addSupportedLCSCapabilitySetRelease98_99, addSupportedLCSCapabilitySetRelease4, addSupportedLCSCapabilitySetRelease5,
                        addSupportedLCSCapabilitySetRelease6, addSupportedLCSCapabilitySetRelease7, additionalLCSCapSetsJsonObject);
                    sriLcsLocationInfo.add("AdditionalLCSCapabilitySets", additionalLCSCapSetsJsonObject);
                }
                /*** mme-Name [6] DiameterIdentity OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getMmeName() != null) {
                    String mmeName = new String(sriLcs.getLcsLocationInfo().getMmeName().getData());
                    writeMmeName(mmeName, sriLcsLocationInfo);
                }
                /*** aaa-Server-Name [8] DiameterIdentity OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getAaaServerName() != null) {
                    String tgppAAAServerName = new String(sriLcs.getLcsLocationInfo().getAaaServerName().getData());
                    write3gppAaaServerName(tgppAAAServerName, sriLcsLocationInfo);
                }
                /*** sgsn-Name [9] DiameterIdentity OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getSgsnName() != null) {
                    String sgsnName = new String(sriLcs.getLcsLocationInfo().getSgsnName().getData());
                    writeSgsnName(sgsnName, sriLcsLocationInfo);
                }

                /*** lcsLocationInfo [1] LCSLocationInfo ***/
                /*** sgsn-Realm [10] DiameterIdentity OPTIONAL ***/
                if (sriLcs.getLcsLocationInfo().getSgsnRealm() != null) {
                    String sgsnRealm = new String(sriLcs.getLcsLocationInfo().getSgsnRealm().getData());
                    writeSgsnRealm(sgsnRealm, sriLcsLocationInfo);
                }
                sriForLcsJsonObject.add("LCSLocationInfo", sriLcsLocationInfo);
            }

            /*** v-gmlc-Address [3] GSN-Address OPTIONAL ***/
            if (sriLcs.getVGmlcAddress() != null) {
                String vGmlcAddress = bytesToHexString(sriLcs.getVGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(vGmlcAddress));
                    vGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writeVGmlcAddress(vGmlcAddress, sriForLcsJsonObject);
            }

            /*** h-gmlc-Address [4] GSN-Address OPTIONAL ***/
            if (sriLcs.getHGmlcAddress() != null) {
                String hGmlcAddress = bytesToHexString(sriLcs.getHGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(hGmlcAddress));
                    hGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writeHGmlcAddress(hGmlcAddress, sriForLcsJsonObject);
            }

            /*** ppr-Address [5] GSN-Address OPTIONAL ***/
            if (sriLcs.getPprAddress() != null) {
                String pprAddress = bytesToHexString(sriLcs.getPprAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(pprAddress));
                    pprAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writePprAddress(pprAddress, sriForLcsJsonObject);
            }

            /*** additional-v-gmlc-Address [6] GSN-Address OPTIONAL ***/
            if (sriLcs.getAddVGmlcAddress() != null) {
                String addVGmlcAddress = bytesToHexString(sriLcs.getAddVGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(addVGmlcAddress));
                    addVGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writeAdditionalVGmlcAddress(addVGmlcAddress, sriForLcsJsonObject);
            }
        }

        /***********************/
        /*** MAP PSL values ***/
        /**********************/
        if (psl != null) {

            pslJsonObject = new JsonObject();

            // Get MAP PSL response values

            /*** locationEstimate Ext-GeographicalInformation, ***/
            if (psl.getLocationEstimate() != null) {
                JsonObject pslLocationEstimateJsonObject = new JsonObject();
                ExtGeographicalInformation locationEstimate = psl.getLocationEstimate();
                typeOfShape = locationEstimate.getTypeOfShape().name();
                if (locationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    latitude = locationEstimate.getLatitude();
                    longitude = locationEstimate.getLongitude();
                    uncertainty = locationEstimate.getUncertainty();
                    uncertaintySemiMajorAxis = locationEstimate.getUncertaintySemiMajorAxis();
                    uncertaintySemiMinorAxis = locationEstimate.getUncertaintySemiMinorAxis();
                    angleOfMajorAxis = locationEstimate.getAngleOfMajorAxis();
                    confidence = locationEstimate.getConfidence();
                    altitude = locationEstimate.getAltitude();
                    uncertaintyAltitude = locationEstimate.getUncertaintyAltitude();
                    innerRadius = locationEstimate.getInnerRadius();
                    uncertaintyInnerRadius = locationEstimate.getUncertaintyRadius();
                    offsetAngle = locationEstimate.getOffsetAngle();
                    includedAngle = locationEstimate.getIncludedAngle();
                }
                // Write Location Estimate values from PSL
                writeTypeOfShape(typeOfShape, pslLocationEstimateJsonObject);
                if (typeOfShape.equalsIgnoreCase("EllipsoidPoint")) {
                    writeLatitude(latitude, pslLocationEstimateJsonObject);
                    writeLongitude(longitude, pslLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle")) {
                    writeLatitude(latitude, pslLocationEstimateJsonObject);
                    writeLongitude(longitude, pslLocationEstimateJsonObject);
                    writeUncertainty(uncertainty, pslLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse")) {
                    writeLatitude(latitude, pslLocationEstimateJsonObject);
                    writeLongitude(longitude, pslLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(uncertaintySemiMajorAxis, pslLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(uncertaintySemiMinorAxis, pslLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(angleOfMajorAxis, pslLocationEstimateJsonObject);
                    writeConfidence(confidence, pslLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
                    writeLatitude(latitude, pslLocationEstimateJsonObject);
                    writeLongitude(longitude, pslLocationEstimateJsonObject);
                    writeAltitude(altitude, pslLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(uncertaintySemiMajorAxis, pslLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(uncertaintySemiMinorAxis, pslLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(angleOfMajorAxis, pslLocationEstimateJsonObject);
                    writeUncertaintyAltitude(uncertaintyAltitude, pslLocationEstimateJsonObject);
                    writeConfidence(confidence, pslLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidArc")) {
                    writeLatitude(latitude, pslLocationEstimateJsonObject);
                    writeLongitude(longitude, pslLocationEstimateJsonObject);
                    writeInnerRadius(innerRadius, pslLocationEstimateJsonObject);
                    writeUncertaintyInnerRadius(uncertaintyInnerRadius, pslLocationEstimateJsonObject);
                    writeOffsetAngle(offsetAngle, pslLocationEstimateJsonObject);
                    writeIncludedAngle(includedAngle, pslLocationEstimateJsonObject);
                    writeConfidence(confidence, pslLocationEstimateJsonObject);
                }
                if (psl.getAdditionalLocationEstimate() != null) {
                    if (psl.getAdditionalLocationEstimate().getTypeOfShape() != null)
                        additionalTypeOfShape = psl.getAdditionalLocationEstimate().getTypeOfShape().name();
                    if (additionalTypeOfShape != null) {
                        if (additionalTypeOfShape.equalsIgnoreCase("Polygon")) {
                            typeOfShape = "Polygon";
                            writeTypeOfShape(typeOfShape, pslLocationEstimateJsonObject);
                        }
                    }
                }
                pslJsonObject.add("LocationEstimate", pslLocationEstimateJsonObject);
            }

            /*** ageOfLocationEstimate [0] AgeOfLocationInformation OPTIONAL ***/
            if (psl.getAgeOfLocationEstimate() != null) {
                aol = psl.getAgeOfLocationEstimate();
                writeAgeOfLocationEstimate(aol, pslJsonObject);
            }

            /*** add-LocationEstimate [2] Add-GeographicalInformation OPTIONAL ***/
            // Additional Location Estimate
            if (psl.getAdditionalLocationEstimate() != null) {
                JsonObject pslAdditionalLocationEstimateJsonObject = new JsonObject();
                AddGeographicalInformation additionalLocationEstimate = psl.getAdditionalLocationEstimate();
                additionalTypeOfShape = additionalLocationEstimate.getTypeOfShape().name();
                if (additionalLocationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    additionalLatitude = additionalLocationEstimate.getLatitude();
                    additionalLongitude = additionalLocationEstimate.getLongitude();
                    additionalUncertainty = additionalLocationEstimate.getUncertainty();
                    additionalUncertaintySemiMajorAxis = additionalLocationEstimate.getUncertaintySemiMajorAxis();
                    additionalUncertaintySemiMinorAxis = additionalLocationEstimate.getUncertaintySemiMinorAxis();
                    additionalAngleOfMajorAxis = additionalLocationEstimate.getAngleOfMajorAxis();
                    additionalConfidence = additionalLocationEstimate.getConfidence();
                    additionalAltitude = additionalLocationEstimate.getAltitude();
                    additionalUncertaintyAltitude = additionalLocationEstimate.getUncertaintyAltitude();
                    additionalInnerRadius = additionalLocationEstimate.getInnerRadius();
                    additionalUncertaintyInnerRadius = additionalLocationEstimate.getUncertaintyRadius();
                    additionalOffsetAngle = additionalLocationEstimate.getOffsetAngle();
                    additionalIncludedAngle = additionalLocationEstimate.getIncludedAngle();
                } else {
                    // PSL Additional Location Estimate for TypeOfShape.Polygon
                    polygon = new PolygonImpl(additionalLocationEstimate.getData());
                    numberOfPoints = polygon.getNumberOfPoints();
                    polygonEllipsoidPoints = new EllipsoidPoint[numberOfPoints];
                    for (int point = 0; point < numberOfPoints; point++) {
                        polygonEllipsoidPoints[point] = polygon.getEllipsoidPoint(point);
                    }
                    try {
                        polygon.setData(polygonEllipsoidPoints);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
                // Write Additional Location Estimate values from PSL
                writeTypeOfShape(additionalTypeOfShape, pslAdditionalLocationEstimateJsonObject);
                if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPoint")) {
                    writeLatitude(additionalLatitude, pslAdditionalLocationEstimateJsonObject);
                    writeLongitude(additionalLongitude, pslAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle")) {
                    writeLatitude(additionalLatitude, pslAdditionalLocationEstimateJsonObject);
                    writeLongitude(additionalLongitude, pslAdditionalLocationEstimateJsonObject);
                    writeUncertainty(additionalUncertainty, pslAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse")) {
                    writeLatitude(additionalLatitude, pslAdditionalLocationEstimateJsonObject);
                    writeLongitude(additionalLongitude, pslAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(additionalUncertaintySemiMajorAxis, pslAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(additionalUncertaintySemiMinorAxis, pslAdditionalLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(additionalAngleOfMajorAxis, pslAdditionalLocationEstimateJsonObject);
                    writeConfidence(additionalConfidence, pslAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("Polygon")) {
                    JsonObject pslAdditionalLocationEstimatePolygonPointsJsonObject = new JsonObject();
                    JsonObject pslAdditionalLocationEstimatePolygonCentroidObject = new JsonObject();
                    polygonArray = new Double[numberOfPoints][numberOfPoints];
                    Double lat, lon;
                    if (numberOfPoints > 2 && numberOfPoints <= 15) {
                        writeNumberOfPoints(numberOfPoints, pslAdditionalLocationEstimateJsonObject);
                        for (int index = 0; index < numberOfPoints; index++) {
                            lat = polygon.getEllipsoidPoint(index).getLatitude();
                            lon = polygon.getEllipsoidPoint(index).getLongitude();
                            polygonArray[index][0] = lat;
                            polygonArray[index][1] = lon;
                            String additionalPolygonPoint = "polygonPoint" + (index + 1);
                            writeLatitude(lat, pslAdditionalLocationEstimatePolygonPointsJsonObject);
                            writeLongitude(lon, pslAdditionalLocationEstimatePolygonPointsJsonObject);
                            pslAdditionalLocationEstimateJsonObject.add(additionalPolygonPoint, pslAdditionalLocationEstimatePolygonPointsJsonObject);
                            pslAdditionalLocationEstimatePolygonPointsJsonObject = new JsonObject();
                        }
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
                        polygonCentroid(point2D);
                        writeLatitude(polygonCentroid(point2D).getX(), pslAdditionalLocationEstimatePolygonCentroidObject);
                        writeLongitude(polygonCentroid(point2D).getY(), pslAdditionalLocationEstimatePolygonCentroidObject);
                        pslAdditionalLocationEstimateJsonObject.add("polygonCentroid", pslAdditionalLocationEstimatePolygonCentroidObject);
                    }
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitude")) {
                    writeLatitude(additionalLatitude, pslAdditionalLocationEstimateJsonObject);
                    writeLongitude(additionalLongitude, pslAdditionalLocationEstimateJsonObject);
                    writeAltitude(additionalAltitude, pslAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
                    writeLatitude(additionalLatitude, pslAdditionalLocationEstimateJsonObject);
                    writeLongitude(additionalLongitude, pslAdditionalLocationEstimateJsonObject);
                    writeAltitude(additionalAltitude, pslAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(additionalUncertaintySemiMajorAxis, pslAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(additionalUncertaintySemiMinorAxis, pslAdditionalLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(additionalAngleOfMajorAxis, pslAdditionalLocationEstimateJsonObject);
                    writeUncertaintyAltitude(additionalUncertaintyAltitude, pslAdditionalLocationEstimateJsonObject);
                    writeConfidence(additionalConfidence, pslAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidArc")) {
                    writeLatitude(additionalLatitude, pslAdditionalLocationEstimateJsonObject);
                    writeLongitude(additionalLongitude, pslAdditionalLocationEstimateJsonObject);
                    writeInnerRadius(additionalInnerRadius, pslAdditionalLocationEstimateJsonObject);
                    writeUncertaintyInnerRadius(additionalUncertaintyInnerRadius, pslAdditionalLocationEstimateJsonObject);
                    writeOffsetAngle(additionalOffsetAngle, pslAdditionalLocationEstimateJsonObject);
                    writeIncludedAngle(additionalIncludedAngle, pslAdditionalLocationEstimateJsonObject);
                    writeConfidence(additionalConfidence, pslAdditionalLocationEstimateJsonObject);
                }
                pslJsonObject.add("AdditionalLocationEstimate", pslAdditionalLocationEstimateJsonObject);
            }

            /*** deferredmt-lrResponseIndicator [3] NULL OPTIONAL ***/
            if (psl.isDeferredMTLRResponseIndicator()) {
                writeDeferredMTlrResponseIndicator(psl.isDeferredMTLRResponseIndicator(), pslJsonObject);
            }

            /*** geranPositioningData [4] PositioningDataInformation OPTIONAL ***/
            if (psl.getGeranPositioningDataInformation() != null) {
                JsonObject pslGeranPosInfoDataSetJsonObject = new JsonObject();
                JsonObject pslGeranPosInfoJsonObject = new JsonObject();
                HashMap<String, Integer> methodsAndUsage = psl.getGeranPositioningDataInformation().getPositioningDataSet();
                JsonObject[] pslGeranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                int itemIndex = 0;
                for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                    String property = "Item-" + itemIndex;
                    String method = item.getKey();
                    Integer usage = item.getValue();
                    pslGeranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                    pslGeranPosInfoDataSetJsonObject.add(property, pslGeranPosInfoMethodAndUsage[itemIndex]);
                    writeGeranPositioningMethod(method, pslGeranPosInfoMethodAndUsage[itemIndex]);
                    writeGeranPositioningUsage(usage, pslGeranPosInfoMethodAndUsage[itemIndex]);
                    itemIndex++;
                }
                pslGeranPosInfoJsonObject.add("PositioningDataSet", pslGeranPosInfoDataSetJsonObject);
                // Write GERAN Positioning Info values from PSL
                pslJsonObject.add("GeranPositioningData", pslGeranPosInfoJsonObject);
            }

            /*** utranPositioningData [5] UtranPositioningDataInfo OPTIONAL ***/
            if (psl.getUtranPositioningDataInfo() != null) {
                JsonObject pslUtranPosInfoDataSetJsonObject = new JsonObject();
                JsonObject pslUtranPosInfoJsonObject = new JsonObject();
                HashMap<String, Integer> methodsAndUsage = psl.getUtranPositioningDataInfo().getUtranPositioningDataSet();
                JsonObject[] pslUtranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                int itemIndex = 0;
                for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                    String property = "Item-" + itemIndex;
                    String method = item.getKey();
                    Integer usage = item.getValue();
                    pslUtranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                    pslUtranPosInfoDataSetJsonObject.add(property, pslUtranPosInfoMethodAndUsage[itemIndex]);
                    writeUtranPositioningMethod(method, pslUtranPosInfoMethodAndUsage[itemIndex]);
                    writeUtranPositioningUsage(usage, pslUtranPosInfoMethodAndUsage[itemIndex]);
                    itemIndex++;
                }
                pslUtranPosInfoJsonObject.add("PositioningDataSet", pslUtranPosInfoDataSetJsonObject);
                // Write GERAN Positioning Info values from PSL
                pslJsonObject.add("UtranPositioningData", pslUtranPosInfoJsonObject);
            }

            /*** cellIdOrSai [6] CellGlobalIdOrServiceAreaIdOrLAI OPTIONAL ***/
            if (psl.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                JsonObject pslCgiOrSaiOrLaiJsonObject = new JsonObject();
                CellGlobalIdOrServiceAreaIdOrLAI cgiOrSaiOrLai = psl.getCellGlobalIdOrServiceAreaIdOrLAI();
                LAIFixedLength laiFixedLength = cgiOrSaiOrLai.getLAIFixedLength();
                CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = cgiOrSaiOrLai.getCellGlobalIdOrServiceAreaIdFixedLength();
                if (laiFixedLength != null) {
                    mcc = laiFixedLength.getMCC();
                    mnc = laiFixedLength.getMNC();
                    lac = laiFixedLength.getLac();
                } else if (cellGlobalIdOrServiceAreaIdFixedLength != null) {
                    mcc = cellGlobalIdOrServiceAreaIdFixedLength.getMCC();
                    mnc = cellGlobalIdOrServiceAreaIdFixedLength.getMNC();
                    lac = cellGlobalIdOrServiceAreaIdFixedLength.getLac();
                    ciOrSac = cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode();
                }
                // Write CGI or SAI values from PSL
                writeMcc(mcc, pslCgiOrSaiOrLaiJsonObject);
                writeMnc(mnc, pslCgiOrSaiOrLaiJsonObject);
                writeLac(lac, pslCgiOrSaiOrLaiJsonObject);
                /*** sai-Present [7] NULL OPTIONAL ***/
                if (!psl.isSaiPresent()) {
                    if (ciOrSac >= 0) {
                        writeCellId(ciOrSac, pslCgiOrSaiOrLaiJsonObject);
                        pslJsonObject.add("CGI", pslCgiOrSaiOrLaiJsonObject);
                    } else {
                        if (lac >= 0)
                            pslJsonObject.add("LAI", pslCgiOrSaiOrLaiJsonObject);
                    }
                } else {
                    if (ciOrSac >= 0) {
                        writeServiceAreaCode(ciOrSac, pslCgiOrSaiOrLaiJsonObject);
                        pslJsonObject.add("SAI", pslCgiOrSaiOrLaiJsonObject);
                    } else {
                        if (lac >= 0)
                            pslJsonObject.add("LAI", pslCgiOrSaiOrLaiJsonObject);
                    }
                }
            }

            /*** accuracyFulfilmentIndicator [8] AccuracyFulfilmentIndicator OPTIONAL ***/
            if (psl.getAccuracyFulfilmentIndicator() != null) {
                accuracyFulfilmentIndicator = psl.getAccuracyFulfilmentIndicator().getIndicator();
                writeAccuracyFulfilmentIndicator(accuracyFulfilmentIndicator, pslJsonObject);
            }

            /*** velocityEstimate [9] VelocityEstimate OPTIONAL ***/
            if (psl.getVelocityEstimate() != null) {
                JsonObject pslVelocityEstimateJsonObject = new JsonObject();
                if (psl.getVelocityEstimate().getHorizontalSpeed() > -1)
                    horizontalSpeed = psl.getVelocityEstimate().getHorizontalSpeed();
                if (psl.getVelocityEstimate().getBearing() > -1)
                    bearing = psl.getVelocityEstimate().getBearing();
                if (psl.getVelocityEstimate().getVerticalSpeed() > -1)
                    verticalSpeed = psl.getVelocityEstimate().getVerticalSpeed();
                if (psl.getVelocityEstimate().getUncertaintyHorizontalSpeed() > -1)
                    uncertaintyHorizontalSpeed = psl.getVelocityEstimate().getUncertaintyHorizontalSpeed();
                if (psl.getVelocityEstimate().getUncertaintyVerticalSpeed() > -1)
                    uncertaintyVerticalSpeed = psl.getVelocityEstimate().getUncertaintyVerticalSpeed();
                String velocityType = null;
                if (psl.getVelocityEstimate().getVelocityType() != null)
                    velocityType = psl.getVelocityEstimate().getVelocityType().name();
                // Write Velocity Estimate values from PSL
                writeHorizontalSpeed(horizontalSpeed, pslVelocityEstimateJsonObject);
                writeBearing(bearing, pslVelocityEstimateJsonObject);
                writeVerticalSpeed(verticalSpeed, pslVelocityEstimateJsonObject);
                writeUncertaintyHorizontalSpeed(uncertaintyHorizontalSpeed, pslVelocityEstimateJsonObject);
                writeUncertaintyVerticalSpeed(uncertaintyVerticalSpeed, pslVelocityEstimateJsonObject);
                writeVelocityType(velocityType, pslVelocityEstimateJsonObject);
                pslJsonObject.add("VelocityEstimate", pslVelocityEstimateJsonObject);
            }

            /*** mo-lrShortCircuitIndicator [10] NULL OPTIONAL ***/
            if (psl.isMoLrShortCircuitIndicator()) {
                writeMoLrShortCircuitIndicator(psl.isMoLrShortCircuitIndicator(), pslJsonObject);
            }

            /*** geranGANSSpositioningData [11] GeranGANSSpositioningData OPTIONAL ***/
            if (psl.getGeranGANSSpositioningData() != null) {
                JsonObject pslGeranGanssPosInfoDataSetJsonObject = new JsonObject();
                JsonObject pslGeranGanssInfoJsonObject = new JsonObject();
                Multimap<String, String> methodsAndGanssIds = psl.getGeranGANSSpositioningData().getGeranGANSSPositioningMethodsAndGANSSIds();
                JsonObject[] pslGeranGanssPosInfoMethodIdUsage = new JsonObject[methodsAndGanssIds.size()];
                String method, id;
                int itemIndex = 0, usage;
                for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                    method = item.getKey();
                    id = item.getValue();
                    usage = psl.getGeranGANSSpositioningData().getUsageCode(psl.getGeranGANSSpositioningData().getData(), itemIndex+1);
                    String property = "Item-" + itemIndex;
                    pslGeranGanssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                    pslGeranGanssPosInfoDataSetJsonObject.add(property, pslGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeGeranGanssPositioningMethod(method, pslGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeGeranGanssPositioningGanssId(id, pslGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeGeranGanssPositioningUsage(usage, pslGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    itemIndex++;
                }
                pslGeranGanssInfoJsonObject.add("GanssPositioningDataSet", pslGeranGanssPosInfoDataSetJsonObject);
                // Write GERAN GANSS Positioning Info values from PSL
                pslJsonObject.add("GeranGANSSPositioningData", pslGeranGanssInfoJsonObject);
            }

            /*** utranGANSSpositioningData [12] UtranGANSSpositioningData OPTIONAL ***/
            if (psl.getUtranGANSSpositioningData() != null) {
                JsonObject pslUtranGanssPosInfoDataSetJsonObject = new JsonObject();
                JsonObject pslUtranGanssInfoJsonObject = new JsonObject();
                Multimap<String, String> methodsAndGanssIds = psl.getUtranGANSSpositioningData().getUtranGANSSPositioningMethodsAndGANSSIds();
                JsonObject[] pslUtranGanssPosInfoMethodIdUsage  = new JsonObject[methodsAndGanssIds.size()];
                String method, id;
                int itemIndex = 0, usage;
                for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                    method = item.getKey();
                    id = item.getValue();
                    usage = psl.getUtranGANSSpositioningData().getUsageCode(psl.getUtranGANSSpositioningData().getData(), itemIndex);
                    String property = "Item-" + itemIndex;
                    pslUtranGanssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                    pslUtranGanssPosInfoDataSetJsonObject.add(property, pslUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeUtranGanssPositioningMethod(method, pslUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeUtranGanssPositioningGanssId(id, pslUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeUtranGanssPositioningUsage(usage, pslUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    itemIndex++;
                }
                pslUtranGanssInfoJsonObject.add("GanssPositioningDataSet", pslUtranGanssPosInfoDataSetJsonObject);
                // Write UTRAN GANSS Positioning Info values from PSL
                pslJsonObject.add("UtranGANSSPositioningData", pslUtranGanssInfoJsonObject);
            }

            /*** targetServingNodeForHandover [13] ServingNodeAddress OPTIONAL ***/
            if (psl.getTargetServingNodeForHandover() != null) {
                JsonObject pslServingNodeForHoJsonObject = new JsonObject();
                if (psl.getTargetServingNodeForHandover().getMmeNumber() != null) {
                    String mmeNumber = Arrays.toString(psl.getTargetServingNodeForHandover().getMmeNumber().getData());
                    writeMmeNumber(mmeNumber, pslServingNodeForHoJsonObject);
                }
                if (psl.getTargetServingNodeForHandover().getMscNumber() != null) {
                    String mscNumber = psl.getTargetServingNodeForHandover().getMscNumber().getAddress();
                    writeMscNumber(mscNumber, pslServingNodeForHoJsonObject);
                }
                if (psl.getTargetServingNodeForHandover().getSgsnNumber() != null) {
                    String sgsnNumber = psl.getTargetServingNodeForHandover().getSgsnNumber().getAddress();
                    writeSgsnNumber(sgsnNumber, pslServingNodeForHoJsonObject);
                }
                // Write Target Serving Node for Handover address
                pslJsonObject.add("TargetServingNodeForHandover", pslServingNodeForHoJsonObject);
            }

            /*** utranAdditionalPositioningData [14] UtranAdditionalPositioningData OPTIONAL ***/
            if (psl.getUtranAdditionalPositioningData() != null) {
                JsonObject pslUtranAddPosInfoDataSetJsonObject = new JsonObject();
                JsonObject pslUtranAddInfoJsonObject = new JsonObject();
                Multimap<String, String> methodsAndAddPosIds = psl.getUtranAdditionalPositioningData().getUtranAdditionalPositioningMethodsAndIds();
                JsonObject[] pslUtranAddPosInfoMethodAndId = new JsonObject[methodsAndAddPosIds.size()];
                String method, id;
                int itemIndex = 0, usage;
                for (Map.Entry<String, String> item : methodsAndAddPosIds.entries()) {
                    method = item.getKey();
                    id = item.getValue();
                    usage = psl.getUtranAdditionalPositioningData().getUsageCode(psl.getUtranAdditionalPositioningData().getData(), itemIndex);
                    String property = "Item-" + itemIndex;
                    pslUtranAddPosInfoMethodAndId[itemIndex] = new JsonObject();
                    pslUtranAddPosInfoDataSetJsonObject.add(property, pslUtranAddPosInfoMethodAndId[itemIndex]);
                    writeUtranAddPositioningMethod(method, pslUtranAddPosInfoMethodAndId[itemIndex]);
                    writeUtranAddPositioningPosId(id, pslUtranAddPosInfoMethodAndId[itemIndex]);
                    writeUtranAddPositioningUsage(usage, pslUtranAddPosInfoMethodAndId[itemIndex]);
                    itemIndex++;
                }
                pslUtranAddInfoJsonObject.add("AdditionalPositioningDataSet", pslUtranAddPosInfoDataSetJsonObject);
                // Write UTRAN Positioning Info values from PSL
                pslJsonObject.add("UtranAdditionalPositioningData", pslUtranAddInfoJsonObject);
            }

            /*** utranBaroPressureMeas [15] UtranBaroPressureMeas OPTIONAL ***/
            if (psl.getUtranBaroPressureMeas() != null) {
                Long utranBarometricPressureMeas = Long.valueOf(psl.getUtranBaroPressureMeas());
                // Write UTRAN Barometric Pressure Measurement from PSL
                JsonObject pslUtranBaroPressureMeasJsonObject = new JsonObject();
                writeBarometricPressure(utranBarometricPressureMeas, pslUtranBaroPressureMeasJsonObject);
                pslJsonObject.add("BarometricPressure", pslUtranBaroPressureMeasJsonObject);
            }

            /*** utranCivicAddress [16] UtranCivicAddress OPTIONAL ***/
            if (psl.getUtranCivicAddress() != null) {
                Charset charset = StandardCharsets.UTF_8;
                String utranCivicAddressStr = new String(psl.getUtranCivicAddress().getData(), charset);
                CivicAddressXmlReader reader = new CivicAddressXmlReader();
                reader.civicAddressXMLReader(utranCivicAddressStr);
                CivicAddressElements utranCivicAddress = reader.getCivicAddressElements();
                // Write UTRAN Civic Address from PSL
                JsonObject pslUtranCivicAddressJsonObject = new JsonObject();
                writeCivicAddress(utranCivicAddress, pslUtranCivicAddressJsonObject);
                pslJsonObject.add("CivicAddress", pslUtranCivicAddressJsonObject);
            }
        }

        // Write values retrieved from SRILCS
        if (sriForLcsJsonObject != null)
            sriPslJsonObject.add("SRILCS", sriForLcsJsonObject);

        // Write values retrieved from PSL
        if (pslJsonObject != null)
            sriPslJsonObject.add("PSL", pslJsonObject);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(sriPslJsonObject);
    }
}
