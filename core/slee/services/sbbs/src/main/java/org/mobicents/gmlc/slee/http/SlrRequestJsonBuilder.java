package org.mobicents.gmlc.slee.http;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.map.SlrRequestParams;
import org.mobicents.gmlc.slee.primitives.CivicAddressElements;
import org.mobicents.gmlc.slee.primitives.CivicAddressXmlReader;
import org.mobicents.gmlc.slee.primitives.EllipsoidPoint;
import org.mobicents.gmlc.slee.primitives.PolygonImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
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
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAaaServerName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAccuracyFulfilmentIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalLCSCapabilitySets;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAgeOfLocationEstimate;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAltitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAngleOfMajorAxis;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeBarometricPressure;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeBearing;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCivicAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeClientReferenceNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeConfidence;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeDeferredLocationEventType;
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
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientAPN;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientDataCodingScheme;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientDataFormatIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientDialedByMS;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientExternalID;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientIDType;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientInternalID;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSClientName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSEvent;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSServiceTypeID;
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
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNaESRD;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNaESRK;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNaESRKRequest;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetwork;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNumberOfPoints;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOffsetAngle;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperation;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeProtocol;
import static org.mobicents.gmlc.slee.http.JsonWriter.writePseudonymIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeReportingAmount;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeReportingInterval;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeRequestorIdDataCodingScheme;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeRequestorIdDataFormatIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeRequestorIdEncodedString;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSLRTerminationCause;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSequenceNumber;
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
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVelocityType;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVerticalSpeed;
import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SlrRequestJsonBuilder {

    protected static final Logger logger = Logger.getLogger(SlrRequestJsonBuilder.class);

    public SlrRequestJsonBuilder() {
    }

    public static String buildJsonReportForSlrFromStringList(List<String> slrReportParameters) {
        String reportParameterString = "";

        if (slrReportParameters != null && !slrReportParameters.isEmpty()) {
            StringBuilder reportParameterStringBuilder = new StringBuilder();

            for (String slrReportParameter : slrReportParameters) {
                reportParameterStringBuilder.append(Arrays.toString((slrReportParameter + ", ").getBytes()));
            }

            // remove trailing comma and space
            reportParameterString = reportParameterStringBuilder.substring(0, reportParameterStringBuilder.length() - 2);
        }

        return reportParameterString;
    }

    /**
     * Handle generating the appropriate HTTP response in JSON format
     *
     * @param slrReq                SLR Request values gathered from SLR request event
     * @param clientReferenceNumber Reference number gathered from the originating HTTP request sent by the GMLC Client
     */
    public static String buildJsonReportForSLR(SlrRequestParams slrReq, Integer clientReferenceNumber) throws MAPException {

        int lcsReferenceNumber, lcsClientDataCodingScheme, lcsClientFormatIndicator, requestorIDFormatIndicator, requestorIDDataCodingScheme,
                numberOfPoints, confidence, altitude, innerRadius, addConfidence, addAltitude, addInnerRadius,
                mcc, mnc, lac, ciOrSac;
        numberOfPoints = confidence = altitude = innerRadius = addConfidence = addAltitude = addInnerRadius = mcc = mnc = lac = ciOrSac = -1;
        String lcsClientIDExternalID, lcsClientName, lcsClientIDAPN, requestorIDEncodedString, lcsClientDialedByMS, typeOfShape, additionalTypeOfShape;
        lcsClientIDExternalID = additionalTypeOfShape = null;
        Double latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, uncertaintyAltitude, uncertaintyInnerRadius,
            angleOfMajorAxis, offsetAngle, includedAngle;
        latitude = longitude = uncertainty = uncertaintySemiMajorAxis = uncertaintySemiMinorAxis = uncertaintyAltitude = uncertaintyInnerRadius =
            angleOfMajorAxis = offsetAngle = includedAngle = null;
        Double addLatitude, addLongitude, addUncertainty, addUncertaintySemiMajorAxis, addUncertaintySemiMinorAxis, addUncertaintyAltitude,
            addUncertaintyInnerRadius, addAngleOfMajorAxis, addOffsetAngle, addIncludedAngle;
        addLatitude = addLongitude = addUncertainty = addUncertaintySemiMajorAxis = addUncertaintySemiMinorAxis = addUncertaintyAltitude =
            addUncertaintyInnerRadius = addAngleOfMajorAxis = addOffsetAngle = addIncludedAngle = null;
        PolygonImpl polygon = null;
        EllipsoidPoint[] polygonEllipsoidPoints;
        Double[][] polygonArray;

        JsonObject slrJsonObject = new JsonObject();
        writeNetwork("GSM/UMTS", slrJsonObject);
        writeProtocol("MAP", slrJsonObject);
        writeOperation("SLR", slrJsonObject);
        writeOperationResult("SUCCESS", slrJsonObject);

        if (slrReq != null) {

            if (clientReferenceNumber != null) {
                writeClientReferenceNumber(clientReferenceNumber, slrJsonObject);
            }

            /*** lcs-Event LCS-Event ***/
            if (slrReq.getLcsEvent() != null) {
                writeLCSEvent(slrReq.getLcsEvent().getEvent(), slrJsonObject);
            }

            /*** lcs-ClientID LCS-ClientID ***/
            if (slrReq.getLcsClientID() != null) {
                JsonObject lcsClientIDJsonObject = new JsonObject();
                if (slrReq.getLcsClientID().getLCSClientType() != null && (slrReq.getLcsClientID().getLCSClientType().getType() > Integer.MIN_VALUE
                        && slrReq.getLcsClientID().getLCSClientType().getType() < Integer.MAX_VALUE)) {
                    writeLCSClientIDType(slrReq.getLcsClientID().getLCSClientType().getType(), lcsClientIDJsonObject);
                }
                if (slrReq.getLcsClientID().getLCSClientExternalID() != null) {
                    if (slrReq.getLcsClientID().getLCSClientExternalID().getExternalAddress() != null)
                        lcsClientIDExternalID = slrReq.getLcsClientID().getLCSClientExternalID().getExternalAddress().getAddress();
                    writeLCSClientExternalID(lcsClientIDExternalID, lcsClientIDJsonObject);
                }
                if (slrReq.getLcsClientID().getLCSClientInternalID() != null
                        && (slrReq.getLcsClientID().getLCSClientInternalID().getId() > Integer.MIN_VALUE
                        && slrReq.getLcsClientID().getLCSClientInternalID().getId() < Integer.MAX_VALUE)) {
                    writeLCSClientInternalID(slrReq.getLcsClientID().getLCSClientInternalID().getId(), lcsClientIDJsonObject);
                }
                if (slrReq.getLcsClientID().getLCSClientName() != null) {
                    JsonObject lcsClientNameJsonObject = new JsonObject();
                    if (slrReq.getLcsClientID().getLCSClientName().getNameString() != null) {
                        lcsClientName = slrReq.getLcsClientID().getLCSClientName().getNameString().getString(Charset.defaultCharset());
                        writeLCSClientName(lcsClientName, lcsClientNameJsonObject);
                    }
                    if (slrReq.getLcsClientID().getLCSClientName().getDataCodingScheme() != null) {
                        lcsClientDataCodingScheme = slrReq.getLcsClientID().getLCSClientName().getDataCodingScheme().getCode();
                        writeLCSClientDataCodingScheme(lcsClientDataCodingScheme, lcsClientNameJsonObject);
                    }
                    if (slrReq.getLcsClientID().getLCSClientName().getLCSFormatIndicator() != null) {
                        lcsClientFormatIndicator = slrReq.getLcsClientID().getLCSClientName().getLCSFormatIndicator().getIndicator();
                        writeLCSClientDataFormatIndicator(lcsClientFormatIndicator, lcsClientNameJsonObject);
                    }
                    lcsClientIDJsonObject.add("lcsClientIDName", lcsClientNameJsonObject);
                }
                if (slrReq.getLcsClientID().getLCSAPN() != null) {
                    lcsClientIDAPN = new String(slrReq.getLcsClientID().getLCSAPN().getApn().getBytes());
                    writeLCSClientAPN(lcsClientIDAPN, lcsClientIDJsonObject);
                }
                if (slrReq.getLcsClientID().getLCSRequestorID() != null) {
                    JsonObject lcsClientRequestorIDJsonObject = new JsonObject();
                    if (slrReq.getLcsClientID().getLCSRequestorID().getRequestorIDString() != null) {
                        requestorIDEncodedString = slrReq.getLcsClientID().getLCSRequestorID().getRequestorIDString().getString(Charset.defaultCharset());
                        writeRequestorIdEncodedString(requestorIDEncodedString, lcsClientRequestorIDJsonObject);
                    }
                    if (slrReq.getLcsClientID().getLCSRequestorID().getDataCodingScheme() != null) {
                        requestorIDDataCodingScheme = slrReq.getLcsClientID().getLCSRequestorID().getDataCodingScheme().getCode();
                        writeRequestorIdDataCodingScheme(requestorIDDataCodingScheme, lcsClientRequestorIDJsonObject);
                    }
                    if (slrReq.getLcsClientID().getLCSRequestorID().getLCSFormatIndicator() != null) {
                        requestorIDFormatIndicator = slrReq.getLcsClientID().getLCSRequestorID().getLCSFormatIndicator().getIndicator();
                        writeRequestorIdDataFormatIndicator(requestorIDFormatIndicator, lcsClientRequestorIDJsonObject);
                    }
                    lcsClientIDJsonObject.add("lcsClientRequestorID", lcsClientRequestorIDJsonObject);
                }
                if (slrReq.getLcsClientID().getLCSClientDialedByMS() != null) {
                    lcsClientDialedByMS = slrReq.getLcsClientID().getLCSClientDialedByMS().getAddress();
                    writeLCSClientDialedByMS(lcsClientDialedByMS, lcsClientIDJsonObject);
                }
                slrJsonObject.add("LCSClientId", lcsClientIDJsonObject);
            }

            /*** lcsLocationInfo LCSLocationInfo ***/
            if (slrReq.getLcsLocationInfo() != null) {
                JsonObject lcsLocationInfoJsonObject = new JsonObject();
                if (slrReq.getLcsLocationInfo().getNetworkNodeNumber() != null) {
                    String networkNodeNumber = slrReq.getLcsLocationInfo().getNetworkNodeNumber().getAddress();
                    writeNetworkNodeNumber(networkNodeNumber, lcsLocationInfoJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getGprsNodeIndicator()) {
                    writeGprsNodeIndicator(slrReq.getLcsLocationInfo().getGprsNodeIndicator(), lcsLocationInfoJsonObject);
                }
                if (slrReq.getLmsi() != null) {
                    String lmsi = bytesToHex(slrReq.getLmsi().getData());
                    writeLmsi(lmsi, lcsLocationInfoJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getAdditionalNumber() != null) {
                    String additionalNumber = null;
                    if (slrReq.getLcsLocationInfo().getAdditionalNumber().getMSCNumber() != null)
                        additionalNumber = slrReq.getLcsLocationInfo().getAdditionalNumber().getMSCNumber().getAddress();
                    else if (slrReq.getLcsLocationInfo().getAdditionalNumber().getSGSNNumber() != null)
                        additionalNumber = slrReq.getLcsLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress();
                    writeAdditionalNetworkNodeNumber(additionalNumber, lcsLocationInfoJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getSupportedLCSCapabilitySets() != null) {
                    JsonObject supportedLCSCapSetsJsonObject = new JsonObject();
                    boolean supportedLCSCapabilitySetRelease98_99 = slrReq.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99();
                    boolean supportedLCSCapabilitySetRelease4 = slrReq.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease4();
                    boolean supportedLCSCapabilitySetRelease5 = slrReq.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease5();
                    boolean supportedLCSCapabilitySetRelease6 = slrReq.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease6();
                    boolean supportedLCSCapabilitySetRelease7 = slrReq.getLcsLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease7();
                    writeSupportedLCSCapabilitySets(true, supportedLCSCapabilitySetRelease98_99, supportedLCSCapabilitySetRelease4, supportedLCSCapabilitySetRelease5,
                            supportedLCSCapabilitySetRelease6, supportedLCSCapabilitySetRelease7, supportedLCSCapSetsJsonObject);
                    lcsLocationInfoJsonObject.add("SupportedLCSCapabilitySets", supportedLCSCapSetsJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                    JsonObject additionalLCSCapSetsJsonObject = new JsonObject();
                    boolean addSupportedLCSCapabilitySetRelease98_99 = slrReq.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease98_99();
                    boolean addSupportedLCSCapabilitySetRelease4 = slrReq.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease4();
                    boolean addSupportedLCSCapabilitySetRelease5 = slrReq.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease5();
                    boolean addSupportedLCSCapabilitySetRelease6 = slrReq.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease6();
                    boolean addSupportedLCSCapabilitySetRelease7 = slrReq.getLcsLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease7();
                    writeAdditionalLCSCapabilitySets(true, addSupportedLCSCapabilitySetRelease98_99, addSupportedLCSCapabilitySetRelease4,
                            addSupportedLCSCapabilitySetRelease5, addSupportedLCSCapabilitySetRelease6, addSupportedLCSCapabilitySetRelease7, additionalLCSCapSetsJsonObject);
                    lcsLocationInfoJsonObject.add("AdditionalLCSCapabilitySets", additionalLCSCapSetsJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getMmeName() != null) {
                    String mmeName = new String(slrReq.getLcsLocationInfo().getMmeName().getData());
                    writeMmeName(mmeName, lcsLocationInfoJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getAaaServerName() != null) {
                    String tgppAAAServerName = new String(slrReq.getLcsLocationInfo().getAaaServerName().getData());
                    write3gppAaaServerName(tgppAAAServerName, lcsLocationInfoJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getSgsnName() != null) {
                    String sgsnName = new String(slrReq.getLcsLocationInfo().getSgsnName().getData());
                    writeSgsnName(sgsnName, lcsLocationInfoJsonObject);
                }
                if (slrReq.getLcsLocationInfo().getSgsnRealm() != null) {
                    String sgsnRealm = new String(slrReq.getLcsLocationInfo().getSgsnRealm().getData());
                    writeSgsnRealm(sgsnRealm, lcsLocationInfoJsonObject);
                }
                slrJsonObject.add("LCSLocationInfo", lcsLocationInfoJsonObject);
            }

            /*** msisdn [0] ISDN-AddressString OPTIONAL ***/
            if (slrReq.getMsisdn() != null) {
                writeMsisdn(slrReq.getMsisdn().getAddress(), slrJsonObject);
            }

            /*** imsi [1] IMSI  OPTIONAL ***/
            if (slrReq.getImsi() != null) {
                String imsi = new String(slrReq.getImsi().getData().getBytes());
                writeImsi(imsi, slrJsonObject);
            }

            /*** imei [2] IMEI  OPTIONAL ***/
            if (slrReq.getImei() != null) {
                String imei = slrReq.getImei().getIMEI();
                writeImei(imei, slrJsonObject);
            }

            /*** na-ESRD [3] ISDN-AddressString OPTIONAL ***/
            if (slrReq.getNaESRD() != null) {
                writeNaESRD(slrReq.getNaESRD().getAddress(), slrJsonObject);
            }

            /*** na-ESRK [4] ISDN-AddressString OPTIONAL ***/
            if (slrReq.getNaESRK() != null) {
                writeNaESRK(slrReq.getNaESRK().getAddress(), slrJsonObject);
            }

            /*** locationEstimate [5] Ext-GeographicalInformation OPTIONAL ***/
            if (slrReq.getLocationEstimate() != null) {
                JsonObject slrLocationEstimateJsonObject = new JsonObject();
                ExtGeographicalInformation locationEstimate = slrReq.getLocationEstimate();
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
                // Write Location Estimate values from SLR
                writeTypeOfShape(typeOfShape, slrLocationEstimateJsonObject);
                if (typeOfShape.equalsIgnoreCase("EllipsoidPoint")) {
                    writeLatitude(latitude, slrLocationEstimateJsonObject);
                    writeLongitude(longitude, slrLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle")) {
                    writeLatitude(latitude, slrLocationEstimateJsonObject);
                    writeLongitude(longitude, slrLocationEstimateJsonObject);
                    writeUncertainty(uncertainty, slrLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse")) {
                    writeLatitude(latitude, slrLocationEstimateJsonObject);
                    writeLongitude(longitude, slrLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(uncertaintySemiMajorAxis, slrLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(uncertaintySemiMinorAxis, slrLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(angleOfMajorAxis, slrLocationEstimateJsonObject);
                    writeConfidence(confidence, slrLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
                    writeLatitude(latitude, slrLocationEstimateJsonObject);
                    writeLongitude(longitude, slrLocationEstimateJsonObject);
                    writeAltitude(altitude, slrLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(uncertaintySemiMajorAxis, slrLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(uncertaintySemiMinorAxis, slrLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(angleOfMajorAxis, slrLocationEstimateJsonObject);
                    writeUncertaintyAltitude(uncertaintyAltitude, slrLocationEstimateJsonObject);
                    writeConfidence(confidence, slrLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidArc")) {
                    writeLatitude(latitude, slrLocationEstimateJsonObject);
                    writeLongitude(longitude, slrLocationEstimateJsonObject);
                    writeInnerRadius(innerRadius, slrLocationEstimateJsonObject);
                    writeUncertaintyInnerRadius(uncertaintyInnerRadius, slrLocationEstimateJsonObject);
                    writeOffsetAngle(offsetAngle, slrLocationEstimateJsonObject);
                    writeIncludedAngle(includedAngle, slrLocationEstimateJsonObject);
                    writeConfidence(confidence, slrLocationEstimateJsonObject);
                }

                if (slrReq.getAdditionalLocationEstimate() != null) {
                    if (slrReq.getAdditionalLocationEstimate().getTypeOfShape() != null)
                        additionalTypeOfShape = slrReq.getAdditionalLocationEstimate().getTypeOfShape().name();
                    if (additionalTypeOfShape != null) {
                        if (additionalTypeOfShape.equalsIgnoreCase("Polygon")) {
                            typeOfShape = "Polygon";
                            writeTypeOfShape(typeOfShape, slrLocationEstimateJsonObject);
                        }
                    }
                }
                slrJsonObject.add("LocationEstimate", slrLocationEstimateJsonObject);
            }

            /*** ageOfLocationEstimate [6] AgeOfLocationInformation OPTIONAL ***/
            if (slrReq.getAgeOfLocationEstimate() != null) {
                writeAgeOfLocationEstimate(slrReq.getAgeOfLocationEstimate(), slrJsonObject);
            }

            /*** slr-ArgExtensionContainer [7] SLR-ArgExtensionContainer OPTIONAL ***/
            if (slrReq.getSlrArgExtensionContainer() != null) {
                JsonObject slrArgExtContainerJsonObject = new JsonObject();
                boolean naEsrkRequest;
                if (slrReq.getSlrArgExtensionContainer().getSlrArgPcsExtensions() != null) {
                    naEsrkRequest = slrReq.getSlrArgExtensionContainer().getSlrArgPcsExtensions().getNaEsrkRequest();
                    writeNaESRKRequest(naEsrkRequest, slrArgExtContainerJsonObject);
                }
                if (slrReq.getSlrArgExtensionContainer().getPrivateExtensionList() != null) {
                    slrReq.getSlrArgExtensionContainer().getPrivateExtensionList();// TODO ??
                }
                slrJsonObject.add("SLRArgExtensionContainer", slrArgExtContainerJsonObject);
            }

            /*** add-LocationEstimate [8] Add-GeographicalInformation OPTIONAL ***/
            if (slrReq.getAdditionalLocationEstimate() != null) {
                JsonObject slrAdditionalLocationEstimateJsonObject = new JsonObject();
                AddGeographicalInformation additionalLocationEstimate = slrReq.getAdditionalLocationEstimate();
                additionalTypeOfShape = additionalLocationEstimate.getTypeOfShape().name();
                if (additionalLocationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    additionalTypeOfShape = additionalLocationEstimate.getTypeOfShape().name();
                    addLatitude = additionalLocationEstimate.getLatitude();
                    addLongitude = additionalLocationEstimate.getLongitude();
                    addUncertainty = additionalLocationEstimate.getUncertainty();
                    addConfidence = additionalLocationEstimate.getConfidence();
                    addUncertaintySemiMajorAxis = additionalLocationEstimate.getUncertaintySemiMajorAxis();
                    addUncertaintySemiMinorAxis = additionalLocationEstimate.getUncertaintySemiMinorAxis();
                    addAltitude = additionalLocationEstimate.getAltitude();
                    addUncertaintyAltitude = additionalLocationEstimate.getUncertaintyAltitude();
                    addInnerRadius = additionalLocationEstimate.getInnerRadius();
                    addUncertaintyInnerRadius = additionalLocationEstimate.getUncertaintyRadius();
                    addOffsetAngle = additionalLocationEstimate.getOffsetAngle();
                    addIncludedAngle = additionalLocationEstimate.getIncludedAngle();
                    addAngleOfMajorAxis = additionalLocationEstimate.getAngleOfMajorAxis();
                } else {
                    // SLR Additional Location Estimate Additional Location Estimate for TypeOfShape.Polygon
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
                // Write Additional Location Estimate values from SLR
                writeTypeOfShape(additionalTypeOfShape, slrAdditionalLocationEstimateJsonObject);
                if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPoint")) {
                    writeLatitude(addLatitude, slrAdditionalLocationEstimateJsonObject);
                    writeLongitude(addLongitude, slrAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle")) {
                    writeLatitude(addLatitude, slrAdditionalLocationEstimateJsonObject);
                    writeLongitude(addLongitude, slrAdditionalLocationEstimateJsonObject);
                    writeUncertainty(addUncertainty, slrAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse")) {
                    writeLatitude(addLatitude, slrAdditionalLocationEstimateJsonObject);
                    writeLongitude(addLongitude, slrAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(addUncertaintySemiMajorAxis, slrAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(addUncertaintySemiMinorAxis, slrAdditionalLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(addAngleOfMajorAxis, slrAdditionalLocationEstimateJsonObject);
                    writeConfidence(addConfidence, slrAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("Polygon")) {
                    JsonObject slrAdditionalLocationEstimatePolygonPointsJsonObject = new JsonObject();
                    JsonObject slrAdditionalLocationEstimatePolygonCentroidObject = new JsonObject();
                    polygonArray = new Double[numberOfPoints][numberOfPoints];
                    Double lat, lon;
                    if (numberOfPoints > 2 && numberOfPoints <= 15) {
                        writeNumberOfPoints(numberOfPoints, slrAdditionalLocationEstimateJsonObject);
                        for (int index=0; index<numberOfPoints; index++) {

                            lat = polygon.getEllipsoidPoint(index).getLatitude();
                            lon = polygon.getEllipsoidPoint(index).getLongitude();
                            polygonArray[index][0] = lat;
                            polygonArray[index][1] = lon;
                            String additionalPolygonPoint = "polygonPoint"+(index+1);
                            writeLatitude(lat, slrAdditionalLocationEstimatePolygonPointsJsonObject);
                            writeLongitude(lon, slrAdditionalLocationEstimatePolygonPointsJsonObject);
                            slrAdditionalLocationEstimateJsonObject.add(additionalPolygonPoint, slrAdditionalLocationEstimatePolygonPointsJsonObject);
                            slrAdditionalLocationEstimatePolygonPointsJsonObject = new JsonObject();
                        }
                        List<Point2D> listOfPoints = new ArrayList<>();
                        Point2D[] point2D = new Point2D.Double[polygonArray.length];
                        Point2D polygonPoint;
                        for (int point = 0; point < polygonArray.length; point++) {
                            lat = polygonArray[point][0];
                            lon = polygonArray[point][1];
                            polygonPoint = new Point2D.Double(lat,lon);
                            listOfPoints.add(polygonPoint);
                            point2D[point] = listOfPoints.get(point);
                        }
                        polygonCentroid(point2D);
                        writeLatitude(polygonCentroid(point2D).getX(), slrAdditionalLocationEstimatePolygonCentroidObject);
                        writeLongitude(polygonCentroid(point2D).getY(), slrAdditionalLocationEstimatePolygonCentroidObject);
                        slrAdditionalLocationEstimateJsonObject.add("polygonCentroid", slrAdditionalLocationEstimatePolygonCentroidObject);
                    }
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitude")) {
                    writeLatitude(addLatitude, slrAdditionalLocationEstimateJsonObject);
                    writeLongitude(addLongitude, slrAdditionalLocationEstimateJsonObject);
                    writeAltitude(addAltitude, slrAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
                    writeLatitude(addLatitude, slrAdditionalLocationEstimateJsonObject);
                    writeLongitude(addLongitude, slrAdditionalLocationEstimateJsonObject);
                    writeAltitude(addAltitude, slrAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(addUncertaintySemiMajorAxis, slrAdditionalLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(addUncertaintySemiMinorAxis, slrAdditionalLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(addAngleOfMajorAxis, slrAdditionalLocationEstimateJsonObject);
                    writeUncertaintyAltitude(addUncertaintyAltitude, slrAdditionalLocationEstimateJsonObject);
                    writeConfidence(addConfidence, slrAdditionalLocationEstimateJsonObject);
                } else if (additionalTypeOfShape.equalsIgnoreCase("EllipsoidArc")) {
                    writeLatitude(addLatitude, slrAdditionalLocationEstimateJsonObject);
                    writeLongitude(addLongitude, slrAdditionalLocationEstimateJsonObject);
                    writeInnerRadius(addInnerRadius, slrAdditionalLocationEstimateJsonObject);
                    writeUncertaintyInnerRadius(addUncertaintyInnerRadius, slrAdditionalLocationEstimateJsonObject);
                    writeOffsetAngle(addOffsetAngle, slrAdditionalLocationEstimateJsonObject);
                    writeIncludedAngle(addIncludedAngle, slrAdditionalLocationEstimateJsonObject);
                    writeConfidence(addConfidence, slrAdditionalLocationEstimateJsonObject);
                }
                slrJsonObject.add("AdditionalLocationEstimate", slrAdditionalLocationEstimateJsonObject);
            }

            /*** deferredmt-lrData [9] Deferredmt-lrData OPTIONAL ***/
            if (slrReq.getDeferredmtlrData() != null) {
                JsonObject deferredMTLRDataJsonObject = new JsonObject();
                // Deferred Location Event Type
                if (slrReq.getDeferredmtlrData().getDeferredLocationEventType() != null) {
                    boolean enteringArea = slrReq.getDeferredmtlrData().getDeferredLocationEventType().getEnteringIntoArea();
                    boolean insideArea = slrReq.getDeferredmtlrData().getDeferredLocationEventType().getBeingInsideArea();
                    boolean leavingArea = slrReq.getDeferredmtlrData().getDeferredLocationEventType().getLeavingFromArea();
                    boolean msAvailable = slrReq.getDeferredmtlrData().getDeferredLocationEventType().getMsAvailable();
                    boolean periodicLDR = slrReq.getDeferredmtlrData().getDeferredLocationEventType().getPeriodicLDR();
                    writeDeferredLocationEventType(true, enteringArea, insideArea, leavingArea, msAvailable, periodicLDR, deferredMTLRDataJsonObject);
                }
                // Termination Cause
                if (slrReq.getDeferredmtlrData().getTerminationCause() != null) {
                    int terminationCause = slrReq.getDeferredmtlrData().getTerminationCause().getCause();
                    writeSLRTerminationCause(terminationCause, deferredMTLRDataJsonObject);
                }
                // LCS Location Info (Serving Node)
                if (slrReq.getDeferredmtlrData().getLCSLocationInfo() != null) {
                    JsonObject deferredMTLRDataLCSLocationInfoJsonObject = new JsonObject();
                    // GPRS Node Indicator
                    writeGprsNodeIndicator(slrReq.isGprsNodeIndicator(), deferredMTLRDataLCSLocationInfoJsonObject);
                    // Network Node Number
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getNetworkNodeNumber() != null) {
                        String networkNodeNumber = slrReq.getDeferredmtlrData().getLCSLocationInfo().getNetworkNodeNumber().getAddress();
                        writeNetworkNodeNumber(networkNodeNumber, deferredMTLRDataLCSLocationInfoJsonObject);
                    }
                    // LMSI
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getLMSI() != null) {
                        String lmsi = bytesToHex(slrReq.getDeferredmtlrData().getLCSLocationInfo().getLMSI().getData());
                        writeLmsi(lmsi, deferredMTLRDataLCSLocationInfoJsonObject);
                    }
                    // MME Name
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getMmeName() != null) {
                        String mmeName = new String(slrReq.getDeferredmtlrData().getLCSLocationInfo().getMmeName().getData());
                        writeMmeName(mmeName, deferredMTLRDataLCSLocationInfoJsonObject);
                    }
                    // AAA Server Name
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getAaaServerName() != null) {
                        String aaaServerName = new String(slrReq.getDeferredmtlrData().getLCSLocationInfo().getAaaServerName().getData());
                        writeAaaServerName(aaaServerName, deferredMTLRDataLCSLocationInfoJsonObject);
                    }
                    // Additional Number
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber() != null) {
                        if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getMSCNumber() != null) {
                            String mscNumber = slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getMSCNumber().getAddress();
                            writeMscNumber(mscNumber, deferredMTLRDataLCSLocationInfoJsonObject);
                        }
                        if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getSGSNNumber() != null) {
                            String sgsnNumber = slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress();
                            writeSgsnNumber(sgsnNumber, deferredMTLRDataLCSLocationInfoJsonObject);
                        }
                    }
                    // Supported LCS Capability Sets
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets() != null) {
                        JsonObject deferredSupportedLCSCapSetsJsonObject = new JsonObject();
                        boolean supportedLCSCapabilitySetRelease98_99 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99();
                        boolean supportedLCSCapabilitySetRelease4 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease4();
                        boolean supportedLCSCapabilitySetRelease5 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease5();
                        boolean supportedLCSCapabilitySetRelease6 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease6();
                        boolean supportedLCSCapabilitySetRelease7 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease7();
                        writeSupportedLCSCapabilitySets(true, supportedLCSCapabilitySetRelease98_99, supportedLCSCapabilitySetRelease4, supportedLCSCapabilitySetRelease5,
                                supportedLCSCapabilitySetRelease6, supportedLCSCapabilitySetRelease7, deferredSupportedLCSCapSetsJsonObject);
                        deferredMTLRDataLCSLocationInfoJsonObject.add("SupportedLCSCapabilitySets", deferredSupportedLCSCapSetsJsonObject);
                    }
                    // Additional Supported LCS Capability Sets
                    if (slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                        JsonObject deferredAddSupportedLCSCapSetsJsonObject = new JsonObject();
                        boolean addSupportedLCSCapabilitySetRelease98_99 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99();
                        boolean addSupportedLCSCapabilitySetRelease4 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease4();
                        boolean addSupportedLCSCapabilitySetRelease5 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease5();
                        boolean addSupportedLCSCapabilitySetRelease6 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease6();
                        boolean addSupportedLCSCapabilitySetRelease7 = slrReq.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease7();
                        writeAdditionalLCSCapabilitySets(true, addSupportedLCSCapabilitySetRelease98_99, addSupportedLCSCapabilitySetRelease4, addSupportedLCSCapabilitySetRelease5,
                                addSupportedLCSCapabilitySetRelease6, addSupportedLCSCapabilitySetRelease7, deferredAddSupportedLCSCapSetsJsonObject);
                        deferredMTLRDataLCSLocationInfoJsonObject.add("AdditionalLCSCapabilitySets", deferredAddSupportedLCSCapSetsJsonObject);
                    }
                    deferredMTLRDataJsonObject.add("LCSLocationInfo", deferredMTLRDataLCSLocationInfoJsonObject);
                }
                slrJsonObject.add("DeferredMTLRData", deferredMTLRDataJsonObject);
            }

            /*** lcs-ReferenceNumber [10] LCS-ReferenceNumber OPTIONAL ***/
            if (slrReq.getLcsReferenceNumber() != null) {
                if (slrReq.getLcsReferenceNumber() < 0)
                    lcsReferenceNumber = slrReq.getLcsReferenceNumber() + 256;
                else
                    lcsReferenceNumber = slrReq.getLcsReferenceNumber();
                writeLcsReferenceNumber(lcsReferenceNumber, slrJsonObject);
            }

            /*** geranPositioningData [11] PositioningDataInformation OPTIONAL ***/
            if (slrReq.getGeranPositioningDataInformation() != null) {
                JsonObject slrGeranPosInfoDataSetJsonObject = new JsonObject();
                JsonObject slrGeranPosInfoJsonObject = new JsonObject();
                HashMap<String, Integer> methodsAndUsage = slrReq.getGeranPositioningDataInformation().getPositioningDataSet();
                JsonObject[] slrGeranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                int itemIndex = 0;
                for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                    String property = "Item-" + itemIndex;
                    String method = item.getKey();
                    Integer usage = item.getValue();
                    slrGeranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                    slrGeranPosInfoDataSetJsonObject.add(property, slrGeranPosInfoMethodAndUsage[itemIndex]);
                    writeGeranPositioningMethod(method, slrGeranPosInfoMethodAndUsage[itemIndex]);
                    writeGeranPositioningUsage(usage, slrGeranPosInfoMethodAndUsage[itemIndex]);
                    itemIndex++;
                }
                slrGeranPosInfoJsonObject.add("PositioningDataSet", slrGeranPosInfoDataSetJsonObject);
                // Write GERAN Positioning Info values from SLR
                slrJsonObject.add("GeranPositioningData", slrGeranPosInfoJsonObject);
            }

            /*** utranPositioningData [12] UtranPositioningDataInfo OPTIONAL ***/
            if (slrReq.getUtranPositioningDataInfo() != null) {
                JsonObject slrUtranPosInfoDataSetJsonObject = new JsonObject();
                JsonObject slrUtranPosInfoJsonObject = new JsonObject();
                HashMap<String, Integer> methodsAndUsage = slrReq.getUtranPositioningDataInfo().getUtranPositioningDataSet();
                JsonObject[] slrUtranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                int itemIndex = 0;
                for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                    String property = "Item-" + itemIndex;
                    String method = item.getKey();
                    Integer usage = item.getValue();
                    slrUtranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                    slrUtranPosInfoDataSetJsonObject.add(property, slrUtranPosInfoMethodAndUsage[itemIndex]);
                    writeUtranPositioningMethod(method, slrUtranPosInfoMethodAndUsage[itemIndex]);
                    writeUtranPositioningUsage(usage, slrUtranPosInfoMethodAndUsage[itemIndex]);
                    itemIndex++;
                }
                slrUtranPosInfoJsonObject.add("PositioningDataSet", slrUtranPosInfoDataSetJsonObject);
                // Write GERAN Positioning Info values from SLR
                slrJsonObject.add("UtranPositioningData", slrUtranPosInfoJsonObject);
            }

            /*** cellIdOrSai [13] CellGlobalIdOrServiceAreaIdOrLAI OPTIONAL ***/
            if (slrReq.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                JsonObject slrCGIorSAIorLAIJsonObject = new JsonObject();
                /*** LAI fixed length ***/
                if (slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                    mcc = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC();
                    mnc = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC();
                    lac = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac();
                }
                /*** CGI or SAI fixed length ***/
                if (slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                    mcc = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                    mnc = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                    lac = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                    ciOrSac = slrReq.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                }
                writeMcc(mcc, slrCGIorSAIorLAIJsonObject);
                writeMnc(mnc, slrCGIorSAIorLAIJsonObject);
                writeLac(lac, slrCGIorSAIorLAIJsonObject);
                if (!slrReq.getSaiPresent()) {
                    if (ciOrSac >= -1) {
                        writeCellId(ciOrSac, slrCGIorSAIorLAIJsonObject);
                        slrJsonObject.add("CGI", slrCGIorSAIorLAIJsonObject);
                    } else {
                        if (lac >= -1)
                            slrJsonObject.add("LAI", slrCGIorSAIorLAIJsonObject);
                    }
                } else {
                    if (ciOrSac >= -1) {
                        writeServiceAreaCode(ciOrSac, slrCGIorSAIorLAIJsonObject);
                        slrJsonObject.add("SAI", slrCGIorSAIorLAIJsonObject);
                    } else {
                        if (lac >= -1)
                            slrJsonObject.add("LAI", slrCGIorSAIorLAIJsonObject);
                    }
                }
            }

            /*** h-gmlc-Address [14] GSN-Address OPTIONAL ***/
            if (slrReq.gethGmlcAddress() != null) {
                String hGmlcAddress = bytesToHexString(slrReq.gethGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(hGmlcAddress));
                    hGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writeHGmlcAddress(hGmlcAddress, slrJsonObject);
            }

            /*** lcsServiceTypeID [15] LCSServiceTypeID OPTIONAL ***/
            if (slrReq.getLcsServiceTypeID() != null) {
                writeLCSServiceTypeID(slrReq.getLcsServiceTypeID(), slrJsonObject);
            }

            /*** sai-Present [17] NULL OPTIONAL ***/
            // implicit in cellIdOrSai

            /*** pseudonymIndicator [18] NULL  OPTIONAL ***/
            if (slrReq.getPseudonymIndicator() != null) {
                writePseudonymIndicator(slrReq.getPseudonymIndicator(), slrJsonObject);
            }

            /*** accuracyFulfilmentIndicator [19] AccuracyFulfilmentIndicator OPTIONAL ***/
            if (slrReq.getAccuracyFulfilmentIndicator() != null) {
                writeAccuracyFulfilmentIndicator(slrReq.getAccuracyFulfilmentIndicator().getIndicator(), slrJsonObject);
            }

            /*** velocityEstimate [20] VelocityEstimate OPTIONAL ***/
            if (slrReq.getVelocityEstimate() != null) {
                JsonObject slrVelocityEstimateJsonObject = new JsonObject();
                int horizontalSpeed = slrReq.getVelocityEstimate().getHorizontalSpeed();
                int bearing = slrReq.getVelocityEstimate().getBearing();
                int verticalSpeed = slrReq.getVelocityEstimate().getVerticalSpeed();
                int uncertaintyHorizontalSpeed = slrReq.getVelocityEstimate().getUncertaintyHorizontalSpeed();
                int uncertaintyVerticalSpeed = slrReq.getVelocityEstimate().getUncertaintyVerticalSpeed();
                String velocityType = null;
                if (slrReq.getVelocityEstimate().getVelocityType() != null)
                    velocityType = slrReq.getVelocityEstimate().getVelocityType().name();
                writeHorizontalSpeed(horizontalSpeed, slrVelocityEstimateJsonObject);
                writeBearing(bearing, slrVelocityEstimateJsonObject);
                writeVerticalSpeed(verticalSpeed, slrVelocityEstimateJsonObject);
                writeUncertaintyHorizontalSpeed(uncertaintyHorizontalSpeed, slrVelocityEstimateJsonObject);
                writeUncertaintyVerticalSpeed(uncertaintyVerticalSpeed, slrVelocityEstimateJsonObject);
                writeVelocityType(velocityType, slrVelocityEstimateJsonObject);
                slrJsonObject.add("VelocityEstimate", slrVelocityEstimateJsonObject);
            }

            /*** sequenceNumber [21] SequenceNumber OPTIONAL ***/
            if (slrReq.getSequenceNumber() != null) {
                writeSequenceNumber(slrReq.getSequenceNumber(), slrJsonObject);
            }

            /*** periodicLDRInfo [22] PeriodicLDRInfo OPTIONAL ***/
            if (slrReq.getPeriodicLDRInfo() != null) {
                JsonObject slrPeriodicLDRInfoJsonObject = new JsonObject();
                int reportingAmount = slrReq.getPeriodicLDRInfo().getReportingAmount();
                int reportingInterval = slrReq.getPeriodicLDRInfo().getReportingInterval();
                writeReportingAmount(reportingAmount, slrPeriodicLDRInfoJsonObject);
                writeReportingInterval(reportingInterval, slrPeriodicLDRInfoJsonObject);
                slrJsonObject.add("PeriodicLDRInfo", slrPeriodicLDRInfoJsonObject);
            }

            /*** mo-lrShortCircuitIndicator [23] NULL  OPTIONAL ***/
            if (slrReq.isMoLrShortCircuitIndicator())
                writeMoLrShortCircuitIndicator(slrReq.isMoLrShortCircuitIndicator(), slrJsonObject);

            /*** geranGANSSpositioningData [24] GeranGANSSpositioningData OPTIONAL ***/
            if (slrReq.getGeranGANSSpositioningData() != null) {
                JsonObject slrGeranGanssPosInfoDataSetJsonObject = new JsonObject();
                JsonObject slrGeranGanssInfoJsonObject = new JsonObject();
                Multimap<String, String> methodsAndGanssIds = slrReq.getGeranGANSSpositioningData().getGeranGANSSPositioningMethodsAndGANSSIds();
                JsonObject[] slrGeranGanssPosInfoMethodIdUsage = new JsonObject[methodsAndGanssIds.size()];
                String method, id;
                int itemIndex = 0, usage;
                for (HashMap.Entry<String, String> item : methodsAndGanssIds.entries()) {
                    method = item.getKey();
                    id = item.getValue();
                    usage = slrReq.getGeranGANSSpositioningData().getUsageCode(slrReq.getGeranGANSSpositioningData().getData(), itemIndex+1);
                    String property = "Item-" + itemIndex;
                    slrGeranGanssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                    slrGeranGanssPosInfoDataSetJsonObject.add(property, slrGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeGeranGanssPositioningMethod(method, slrGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeGeranGanssPositioningGanssId(id, slrGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeGeranGanssPositioningUsage(usage, slrGeranGanssPosInfoMethodIdUsage[itemIndex]);
                    itemIndex++;
                }
                slrGeranGanssInfoJsonObject.add("GanssPositioningDataSet", slrGeranGanssPosInfoDataSetJsonObject);
                // Write GERAN GANSS Positioning Info values from SLR
                slrJsonObject.add("GeranGANSSPositioningData", slrGeranGanssInfoJsonObject);
            }

            /*** utranGANSSpositioningData [25] UtranGANSSpositioningData OPTIONAL ***/
            if (slrReq.getUtranGANSSpositioningData() != null) {
                JsonObject slrUtranGanssPosInfoDataSetJsonObject = new JsonObject();
                JsonObject slrUtranGanssInfoJsonObject = new JsonObject();
                Multimap<String, String> methodsAndGanssIds = slrReq.getUtranGANSSpositioningData().getUtranGANSSPositioningMethodsAndGANSSIds();
                JsonObject[] slrUtranGanssPosInfoMethodIdUsage  = new JsonObject[methodsAndGanssIds.size()];
                String method, id;
                int itemIndex = 0, usage;
                for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                    method = item.getKey();
                    id = item.getValue();
                    usage = slrReq.getUtranGANSSpositioningData().getUsageCode(slrReq.getUtranGANSSpositioningData().getData(), itemIndex);
                    String property = "Item-" + itemIndex;
                    slrUtranGanssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                    slrUtranGanssPosInfoDataSetJsonObject.add(property, slrUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeUtranGanssPositioningMethod(method, slrUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeUtranGanssPositioningGanssId(id, slrUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    writeUtranGanssPositioningUsage(usage, slrUtranGanssPosInfoMethodIdUsage[itemIndex]);
                    itemIndex++;
                }
                slrUtranGanssInfoJsonObject.add("GanssPositioningDataSet", slrUtranGanssPosInfoDataSetJsonObject);
                // Write UTRAN GANSS Positioning Info values from SLR
                slrJsonObject.add("UtranGANSSPositioningData", slrUtranGanssInfoJsonObject);
            }

            /*** targetServingNodeForHandover [26] ServingNodeAddress OPTIONAL ***/
            if (slrReq.getTargetServingNodeForHandover() != null) {
                JsonObject slrServingNodeForHoJsonObject = new JsonObject();
                if (slrReq.getTargetServingNodeForHandover().getMmeNumber() != null) {
                    String mmeNumber = Arrays.toString(slrReq.getTargetServingNodeForHandover().getMmeNumber().getData());
                    writeMmeNumber(mmeNumber, slrServingNodeForHoJsonObject);
                }
                if (slrReq.getTargetServingNodeForHandover().getMscNumber() != null) {
                    String mscNumber = slrReq.getTargetServingNodeForHandover().getMscNumber().getAddress();
                    writeMscNumber(mscNumber, slrServingNodeForHoJsonObject);
                }
                if (slrReq.getTargetServingNodeForHandover().getSgsnNumber() != null) {
                    String sgsnNumber = slrReq.getTargetServingNodeForHandover().getSgsnNumber().getAddress();
                    writeSgsnNumber(sgsnNumber, slrServingNodeForHoJsonObject);
                }
                // Write Target Serving Node for Handover address
                slrJsonObject.add("TargetServingNodeForHandover", slrServingNodeForHoJsonObject);
            }

            /*** utranAdditionalPositioningData  [27] UtranAdditionalPositioningData OPTIONAL ***/
            if (slrReq.getUtranAdditionalPositioningData() != null) {
                JsonObject slrUtranAddPosInfoDataSetJsonObject = new JsonObject();
                JsonObject slrUtranAddInfoJsonObject = new JsonObject();
                Multimap<String, String> methodsAndAddPosIds = slrReq.getUtranAdditionalPositioningData().getUtranAdditionalPositioningMethodsAndIds();
                JsonObject[] slrUtranAddPosInfoMethodAndId = new JsonObject[methodsAndAddPosIds.size()];
                String method, id;
                int itemIndex = 0, usage;
                for (Map.Entry<String, String> item : methodsAndAddPosIds.entries()) {
                    method = item.getKey();
                    id = item.getValue();
                    usage = slrReq.getUtranAdditionalPositioningData().getUsageCode(slrReq.getUtranAdditionalPositioningData().getData(), itemIndex);
                    String property = "Item-" + itemIndex;
                    slrUtranAddPosInfoMethodAndId[itemIndex] = new JsonObject();
                    slrUtranAddPosInfoDataSetJsonObject.add(property, slrUtranAddPosInfoMethodAndId[itemIndex]);
                    writeUtranAddPositioningMethod(method, slrUtranAddPosInfoMethodAndId[itemIndex]);
                    writeUtranAddPositioningPosId(id, slrUtranAddPosInfoMethodAndId[itemIndex]);
                    writeUtranAddPositioningUsage(usage, slrUtranAddPosInfoMethodAndId[itemIndex]);
                    itemIndex++;
                }
                slrUtranAddInfoJsonObject.add("AdditionalPositioningDataSet", slrUtranAddPosInfoDataSetJsonObject);
                // Write UTRAN Positioning Info values from PSL
                slrJsonObject.add("UtranAdditionalPositioningData", slrUtranAddInfoJsonObject);
            }

            /*** utranBaroPressureMeas [28] UtranBaroPressureMeas OPTIONAL ***/
            if (slrReq.getUtranBaroPressureMeas() != null) {
                JsonObject slrUtranBaroPressureMeasJsonObject = new JsonObject();
                Long utranBarometricPressureMeas = Long.valueOf(slrReq.getUtranBaroPressureMeas());
                writeBarometricPressure(utranBarometricPressureMeas, slrUtranBaroPressureMeasJsonObject);
                slrJsonObject.add("BarometricPressure", slrUtranBaroPressureMeasJsonObject);
            }

            /*** utranCivicAddress [29] UtranCivicAddress OPTIONAL ***/
            if (slrReq.getUtranCivicAddress() != null) {
                JsonObject slrUtranCivicAddressJsonObject = new JsonObject();
                Charset charset = StandardCharsets.UTF_8;
                String utranCivicAddressStr = new String(slrReq.getUtranCivicAddress().getData(), charset);
                CivicAddressXmlReader reader = new CivicAddressXmlReader();
                reader.civicAddressXMLReader(utranCivicAddressStr);
                CivicAddressElements utranCivicAddress = reader.getCivicAddressElements();
                writeCivicAddress(utranCivicAddress, slrUtranCivicAddressJsonObject);
                slrJsonObject.add("CivicAddress", slrUtranCivicAddressJsonObject);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(slrJsonObject);
    }
}
