package org.mobicents.gmlc.slee.http;

import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.diameter.AVPHandler;
import org.mobicents.gmlc.slee.diameter.slg.SLgPlaAvpValues;
import org.mobicents.gmlc.slee.diameter.slh.SLhRiaAvpValues;
import org.mobicents.gmlc.slee.primitives.CivicAddressElements;
import org.mobicents.gmlc.slee.primitives.CivicAddressXmlReader;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningData;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningDataImpl;
import org.mobicents.gmlc.slee.primitives.EllipsoidPoint;
import org.mobicents.gmlc.slee.primitives.PolygonImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.GeranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.PositioningDataInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranAdditionalPositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranGANSSpositioningData;
import org.restcomm.protocols.ss7.map.api.service.lsm.UtranPositioningDataInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.VelocityEstimate;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.restcomm.protocols.ss7.map.primitives.CellGlobalIdOrServiceAreaIdFixedLengthImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.EUtranCgiImpl;

import javax.xml.bind.DatatypeConverter;
import java.awt.geom.Point2D;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mobicents.gmlc.slee.gis.GeographicHelper.polygonCentroid;
import static org.mobicents.gmlc.slee.http.JsonWriter.bytesToHexString;
import static org.mobicents.gmlc.slee.http.JsonWriter.write3gppAaaServerName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAccuracyFulfilmentIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAgeOfLocationEstimate;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAltitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAngleOfMajorAxis;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeBarometricPressure;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeBearing;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCellPortionId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCivicAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeClientReferenceNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeConfidence;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeDiameterResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeENBId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranAddPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranEci;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranGnssPositioningGnssId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranGnssPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranGnssPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranGanssPositioningGanssId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranGanssPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranGanssPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranPositioningMethod;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGeranPositioningUsage;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGmlcAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeHorizontalSpeed;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImei;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeIncludedAngle;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeInnerRadius;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLCSCapabilitySets;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLac;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLatitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLcsReferenceNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLmsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLongitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMcc;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMmeName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMmeRealm;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnc;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMscNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMsisdn;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetwork;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNumberOfPoints;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOffsetAngle;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperation;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writePlaFlags;
import static org.mobicents.gmlc.slee.http.JsonWriter.writePlrFlags;
import static org.mobicents.gmlc.slee.http.JsonWriter.writePprAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeProtocol;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeRiaFlags;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeServiceAreaCode;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnRealm;
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
import static org.mobicents.gmlc.slee.utils.TBCDUtil.toTBCDString;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class PlrResponseJsonBuilder {

    protected static final Logger logger = Logger.getLogger(PlrResponseJsonBuilder.class);

    public PlrResponseJsonBuilder() {
    }

    /**
     * Handle generating the appropriate HTTP response in JSON format
     *
     * @param ria                   Subscriber Information values gathered from RIA response event
     * @param pla                   Subscriber Information values gathered from PSL response event
     * @param plrMsisdn             Subscriber's MSISDN
     * @param plrImsi               Subscriber's IMSI
     * @param clientReferenceNumber Reference Number gathered from the originating HTTP request sent by the GMLC Client
     * @param lcsReferenceNumber    LCS-Reference-Number exchanged between the GMLC and the LTE EPC network
     */
    public static String buildJsonResponseForPlr(SLhRiaAvpValues ria, SLgPlaAvpValues pla, String plrMsisdn, String plrImsi, String plrImei,
                                                 Long plrFlags, Integer clientReferenceNumber, Integer lcsReferenceNumber, String diameterResultMessage) {

        String msisdn, imsi, lmsi, mmeName, mmeRealm, mscNumber, sgsnName, sgsnRealm, sgsnNumber, aaaServerName, gmlcAddress,
                typeOfShape, velocityType, civicAddress;
        mmeName = mmeRealm = sgsnName = sgsnRealm = sgsnNumber = aaaServerName = mscNumber = velocityType = gmlcAddress = null;
        Double latitude, longitude, uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, uncertaintyAltitude, uncertaintyInnerRadius,
            angleOfMajorAxis, offsetAngle, includedAngle;
        latitude = longitude = uncertainty = uncertaintySemiMajorAxis = uncertaintySemiMinorAxis = uncertaintyAltitude = uncertaintyInnerRadius =
            angleOfMajorAxis = offsetAngle = includedAngle = null;
        int cgiMcc, cgiMnc, cgiLac, cgiCi, saiMcc, saiMnc, saiLac, sac, ecgiMcc, ecgiMnc, ecgiCi, ageOfLocationEstimate, numberOfPoints, confidence, altitude,
            innerRadius, accuracyFulfilmentIndicator, horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed;
        cgiMcc = cgiMnc = cgiLac = cgiCi = saiMcc = saiMnc = saiLac = sac = ecgiMcc = ecgiMnc = ecgiCi = numberOfPoints = confidence =
            altitude = innerRadius = horizontalSpeed = bearing = verticalSpeed = uncertaintyHorizontalSpeed = uncertaintyVerticalSpeed = -1;
        Long eci, eNBId, cellPortionId, barometricPressure;
        eci = eNBId = cellPortionId = null;
        long lcsCapabilitySets = -1;
        PolygonImpl polygon = null;
        EllipsoidPoint[] polygonEllipsoidPoints;
        Double[][] polygonArray;

        JsonObject riaPlrPlaJsonObject = new JsonObject();
        writeNetwork("LTE", riaPlrPlaJsonObject);
        writeProtocol("Diameter SLh-SLg(ELP)", riaPlrPlaJsonObject);
        writeOperation("RIR/RIA PLR/PLA", riaPlrPlaJsonObject);
        writeOperationResult("SUCCESS", riaPlrPlaJsonObject);
        if (plrImei != null)
            writeImei(plrImei, riaPlrPlaJsonObject);
        writeClientReferenceNumber(clientReferenceNumber, riaPlrPlaJsonObject);
        writeLcsReferenceNumber(lcsReferenceNumber, riaPlrPlaJsonObject);
        writePlrFlags(plrFlags, riaPlrPlaJsonObject);
        JsonObject riaJsonObject = null;
        JsonObject plaJsonObject = null;

        /**************************************************/
        /*** Get SLh Routing-Information-Answer values ***/
        /*************************************************/
        if (ria != null) {

            riaJsonObject = new JsonObject();

            /*** MSISDN AVP ***/
            if (ria.getMsisdn() != null) {
                msisdn = AVPHandler.tbcd2IsdnAddressString(ria.getMsisdn()).getAddress();
                writeMsisdn(msisdn, riaJsonObject);
            } else {
                msisdn = plrMsisdn;
                writeMsisdn(msisdn, riaJsonObject);
            }

            /*** User-Name AVP ***/
            if (ria.getUserName() != null) {
                imsi = AVPHandler.userName2Imsi(ria.getUserName()).getData();
                writeImsi(imsi, riaJsonObject);
            } else {
                imsi = plrImsi;
                writeImsi(imsi, riaJsonObject);
            }

            /*** LMSI AVP ***/
            if (ria.getLmsi() != null) {
                lmsi = bytesToHex(AVPHandler.byte2Lmsi(ria.getLmsi()).getData());
                writeLmsi(lmsi, riaJsonObject);
            }

            /*** Serving-Node AVP ***/
            if (ria.getServingNodeAvp() != null) {
                JsonObject riaServingNodeJsonObject = new JsonObject();
                if (ria.getServingNodeAvp().getMMEName() != null) {
                    writeMmeName(new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getMMEName()).getData()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().getMMERealm() != null) {
                    writeMmeRealm(new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getMMERealm()).getData()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().getSGSNNumber() != null) {
                    writeSgsnNumber(toTBCDString(ria.getServingNodeAvp().getSGSNNumber()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().getSGSNName() != null) {
                    writeSgsnName(new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getSGSNName()).getData()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().getSGSNRealm() != null) {
                    writeSgsnRealm(new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getSGSNRealm()).getData()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().getMSCNumber() != null) {
                    writeMscNumber(toTBCDString(ria.getServingNodeAvp().getMSCNumber()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().get3GPPAAAServerName() != null) {
                    write3gppAaaServerName(new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().get3GPPAAAServerName()).getData()), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().hasLcsCapabilitiesSets()) {
                    writeLCSCapabilitySets(ria.getServingNodeAvp().getLcsCapabilitiesSets(), riaServingNodeJsonObject);
                }
                if (ria.getServingNodeAvp().getGMLCAddress() != null) {
                    String riaServingNodeGmlcAddress = bytesToHexString(ria.getServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(riaServingNodeGmlcAddress));
                        riaServingNodeGmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                    writeGmlcAddress(riaServingNodeGmlcAddress, riaServingNodeJsonObject);
                }
                riaJsonObject.add("ServingNode", riaServingNodeJsonObject);
            }

            /*** Additional-Serving-Node AVP ***/
            if (ria.getAdditionalServingNodeAvp() != null) {
                JsonObject riaAdditionalServingNodeJsonObject = new JsonObject();
                if (ria.getAdditionalServingNodeAvp().getMMEName() != null) {
                    writeMmeName(new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getMMEName()).getData()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().getMMERealm() != null) {
                    writeMmeRealm(new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getMMERealm()).getData()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().getSGSNNumber() != null) {
                    writeSgsnNumber(toTBCDString(ria.getAdditionalServingNodeAvp().getSGSNNumber()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().getSGSNName() != null) {
                    writeSgsnName(new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getSGSNName()).getData()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().getSGSNRealm() != null) {
                    writeSgsnRealm(new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getSGSNRealm()).getData()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().getMSCNumber() != null) {
                    writeMscNumber(toTBCDString(ria.getAdditionalServingNodeAvp().getMSCNumber()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().get3GPPAAAServerName() != null) {
                    write3gppAaaServerName(new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().get3GPPAAAServerName()).getData()), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().hasLcsCapabilitiesSets()) {
                    writeLCSCapabilitySets(ria.getAdditionalServingNodeAvp().getLcsCapabilitiesSets(), riaAdditionalServingNodeJsonObject);
                }
                if (ria.getAdditionalServingNodeAvp().getGMLCAddress() != null) {
                    String riaAddiServingNodeGmlcAddress = bytesToHexString(ria.getAdditionalServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(riaAddiServingNodeGmlcAddress));
                        riaAddiServingNodeGmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                    writeGmlcAddress(riaAddiServingNodeGmlcAddress, riaAdditionalServingNodeJsonObject);
                }
                riaJsonObject.add("AdditionalServingNode", riaAdditionalServingNodeJsonObject);
            }

            /*** GMLC-Address AVP ***/
            if (ria.getGmlcAddress() != null) {
                String riaGmlcAddress = bytesToHexString(ria.getGmlcAddress().getAddress());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(riaGmlcAddress));
                    riaGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writeGmlcAddress(riaGmlcAddress, riaJsonObject);
            }

            /*** PPR-Address AVP ***/
            if (ria.getPprAddress() != null) {
                String pprAddress = bytesToHexString(ria.getPprAddress().getAddress());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(pprAddress));
                    pprAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
                writePprAddress(pprAddress, riaJsonObject);
            }

            /*** RIA-Flags AVP ***/
            if (ria.getRiaFLags() != null) {
                writeRiaFlags(ria.getRiaFLags(), riaJsonObject);
            }
        }

        /***********************************************/
        /*** Get SLg Provide-Location-Answer values ***/
        /**********************************************/
        if (pla != null) {

            plaJsonObject = new JsonObject();

            writeDiameterResult(diameterResultMessage, plaJsonObject);

            /*** Location-Estimate AVP ***/
            if (pla.getLocationEstimate() != null) {
                ExtGeographicalInformation lteLocationEstimate = AVPHandler.lteLocationEstimate2ExtGeographicalInformation(pla.getLocationEstimate());
                typeOfShape = lteLocationEstimate.getTypeOfShape().name();
                if (lteLocationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    latitude = lteLocationEstimate.getLatitude();
                    longitude = lteLocationEstimate.getLongitude();
                    uncertainty = lteLocationEstimate.getUncertainty();
                    uncertaintySemiMajorAxis = lteLocationEstimate.getUncertaintySemiMajorAxis();
                    uncertaintySemiMinorAxis = lteLocationEstimate.getUncertaintySemiMinorAxis();
                    angleOfMajorAxis = lteLocationEstimate.getAngleOfMajorAxis();
                    confidence = lteLocationEstimate.getConfidence();
                    altitude = lteLocationEstimate.getAltitude();
                    uncertaintyAltitude = lteLocationEstimate.getUncertaintyAltitude();
                    innerRadius = lteLocationEstimate.getInnerRadius();
                    uncertaintyInnerRadius = lteLocationEstimate.getUncertaintyRadius();
                    offsetAngle = lteLocationEstimate.getOffsetAngle();
                    includedAngle = lteLocationEstimate.getIncludedAngle();
                } else {
                    polygon = new PolygonImpl(pla.getLocationEstimate());
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
                // Write Location-Estimate AVP values from SLg PLA
                JsonObject plaLocationEstimateJsonObject = new JsonObject();
                writeTypeOfShape(typeOfShape, plaLocationEstimateJsonObject);
                if (typeOfShape.equalsIgnoreCase("EllipsoidPoint")) {
                    writeLatitude(latitude, plaLocationEstimateJsonObject);
                    writeLongitude(longitude, plaLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle")) {
                    writeLatitude(latitude, plaLocationEstimateJsonObject);
                    writeLongitude(longitude, plaLocationEstimateJsonObject);
                    writeUncertainty(uncertainty, plaLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse")) {
                    writeLatitude(latitude, plaLocationEstimateJsonObject);
                    writeLongitude(longitude, plaLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(uncertaintySemiMajorAxis, plaLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(uncertaintySemiMinorAxis, plaLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(angleOfMajorAxis, plaLocationEstimateJsonObject);
                    writeConfidence(confidence, plaLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("Polygon")) {
                    JsonObject plaLocationEstimatePolygonPointsJsonObject = new JsonObject();
                    polygonArray = new Double[numberOfPoints][numberOfPoints];
                    Double lat, lon;
                    writeNumberOfPoints(numberOfPoints, plaLocationEstimateJsonObject);
                    if (numberOfPoints > 2 && numberOfPoints <= 15) {
                        for (int index=0; index<numberOfPoints; index++) {
                            lat = polygon.getEllipsoidPoint(index).getLatitude();
                            lon = polygon.getEllipsoidPoint(index).getLongitude();
                            polygonArray[index][0] = lat;
                            polygonArray[index][1] = lon;
                            String polygonPoint = "polygonPoint"+(index+1);
                            writeLatitude(lat, plaLocationEstimatePolygonPointsJsonObject);
                            writeLongitude(lon, plaLocationEstimatePolygonPointsJsonObject);
                            plaLocationEstimateJsonObject.add(polygonPoint, plaLocationEstimatePolygonPointsJsonObject);
                            plaLocationEstimatePolygonPointsJsonObject = new JsonObject();
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
                        JsonObject plaLocationEstimatePolygonCentroidObject = new JsonObject();
                        writeLatitude(polygonCentroid(point2D).getX(), plaLocationEstimatePolygonCentroidObject);
                        writeLongitude(polygonCentroid(point2D).getY(), plaLocationEstimatePolygonCentroidObject);
                        plaLocationEstimateJsonObject.add("polygonCentroid", plaLocationEstimatePolygonCentroidObject);
                    }
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitude")) {
                    writeLatitude(latitude, plaLocationEstimateJsonObject);
                    writeLongitude(longitude, plaLocationEstimateJsonObject);
                    writeAltitude(altitude, plaLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
                    writeLatitude(latitude, plaLocationEstimateJsonObject);
                    writeLongitude(longitude, plaLocationEstimateJsonObject);
                    writeAltitude(altitude, plaLocationEstimateJsonObject);
                    writeUncertaintySemiMajorAxis(uncertaintySemiMajorAxis, plaLocationEstimateJsonObject);
                    writeUncertaintySemiMinorAxis(uncertaintySemiMinorAxis, plaLocationEstimateJsonObject);
                    writeAngleOfMajorAxis(angleOfMajorAxis, plaLocationEstimateJsonObject);
                    writeUncertaintyAltitude(uncertaintyAltitude, plaLocationEstimateJsonObject);
                    writeConfidence(confidence, plaLocationEstimateJsonObject);
                } else if (typeOfShape.equalsIgnoreCase("EllipsoidArc")) {
                    writeLatitude(latitude, plaLocationEstimateJsonObject);
                    writeLongitude(longitude, plaLocationEstimateJsonObject);
                    writeInnerRadius(innerRadius, plaLocationEstimateJsonObject);
                    writeUncertaintyInnerRadius(uncertaintyInnerRadius, plaLocationEstimateJsonObject);
                    writeOffsetAngle(offsetAngle, plaLocationEstimateJsonObject);
                    writeIncludedAngle(includedAngle, plaLocationEstimateJsonObject);
                    writeConfidence(confidence, plaLocationEstimateJsonObject);
                }
                plaJsonObject.add("LocationEstimate", plaLocationEstimateJsonObject);
            }

            /*** Accuracy-Fulfilment-Indicator AVP ***/
            if (pla.getAccuracyFulfilmentIndicator() != null) {
                accuracyFulfilmentIndicator = AVPHandler.diamAccFulInd2MapAccFulInd(pla.getAccuracyFulfilmentIndicator()).getIndicator();
                writeAccuracyFulfilmentIndicator(accuracyFulfilmentIndicator, plaJsonObject);
            }

            /*** Age-Of-Location-Estimate AVP ***/
            if (pla.getAgeOfLocationEstimate() != null) {
                if (pla.getAgeOfLocationEstimate() <= Integer.MAX_VALUE && pla.getAgeOfLocationEstimate() >= Integer.MIN_VALUE) {
                    ageOfLocationEstimate = AVPHandler.long2Int(pla.getAgeOfLocationEstimate());
                    writeAgeOfLocationEstimate(ageOfLocationEstimate, plaJsonObject);
                }
            }

            /*** Velocity-Estimate AVP ***/
            if (pla.getVelocityEstimate() != null) {
                VelocityEstimate lteVelocityEstimate = AVPHandler.lteVelocityEstimate2MapVelocityEstimate(pla.getVelocityEstimate());
                if (lteVelocityEstimate.getHorizontalSpeed() > -1)
                    horizontalSpeed = lteVelocityEstimate.getHorizontalSpeed();
                if (lteVelocityEstimate.getBearing() > -1)
                    bearing = lteVelocityEstimate.getBearing();
                if (lteVelocityEstimate.getVerticalSpeed() > -1)
                    verticalSpeed = lteVelocityEstimate.getVerticalSpeed();
                if (lteVelocityEstimate.getUncertaintyHorizontalSpeed() > -1)
                    uncertaintyHorizontalSpeed = lteVelocityEstimate.getUncertaintyHorizontalSpeed();
                if (lteVelocityEstimate.getUncertaintyVerticalSpeed() > -1)
                    uncertaintyVerticalSpeed = lteVelocityEstimate.getUncertaintyVerticalSpeed();
                if (lteVelocityEstimate.getVelocityType() != null)
                    velocityType = lteVelocityEstimate.getVelocityType().name();
                // Write Velocity Estimate values from SLg PLA
                JsonObject plaVelocityEstimateJsonObject = new JsonObject();
                writeHorizontalSpeed(horizontalSpeed, plaVelocityEstimateJsonObject);
                writeBearing(bearing, plaVelocityEstimateJsonObject);
                writeVerticalSpeed(verticalSpeed, plaVelocityEstimateJsonObject);
                writeUncertaintyHorizontalSpeed(uncertaintyHorizontalSpeed, plaVelocityEstimateJsonObject);
                writeUncertaintyVerticalSpeed(uncertaintyVerticalSpeed, plaVelocityEstimateJsonObject);
                writeVelocityType(velocityType, plaVelocityEstimateJsonObject);
                plaJsonObject.add("VelocityEstimate", plaVelocityEstimateJsonObject);
            }

            /*** EUTRAN-Positioning-Data AVP ***/
            if (pla.getEUtranPositioningData() != null) {
                try {
                    EUTRANPositioningData eutranPositioningData = new EUTRANPositioningDataImpl(pla.getEUtranPositioningData());
                    if (eutranPositioningData.getPositioningDataSet() != null) {
                        HashMap<String, Integer> methodsAndUsage = eutranPositioningData.getPositioningDataMethodsAndUsage(eutranPositioningData.getPositioningDataSet());
                        JsonObject plaEUtranPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaEUtranPosInfoJsonObject = new JsonObject();
                        JsonObject[] plaEUtranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                        int itemIndex = 0;
                        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                            String property = "Item-" + itemIndex;
                            String method = item.getKey();
                            Integer usage = item.getValue();
                            plaEUtranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                            plaEUtranPosInfoDataSetJsonObject.add(property, plaEUtranPosInfoMethodAndUsage[itemIndex]);
                            writeEUtranPositioningMethod(method, plaEUtranPosInfoMethodAndUsage[itemIndex]);
                            writeEUtranPositioningUsage(usage, plaEUtranPosInfoMethodAndUsage[itemIndex]);
                            itemIndex++;
                        }
                        plaEUtranPosInfoJsonObject.add("PositioningDataSet", plaEUtranPosInfoDataSetJsonObject);
                        // Write EUTRAN-Positioning-Data AVP Positioning Data Set values from SLg PLA
                        plaJsonObject.add("EUtranPositioningData", plaEUtranPosInfoJsonObject);
                    }
                    if (eutranPositioningData.getGNSSPositioningDataSet() != null) {
                        Multimap<String, String> methodsAndGanssIds = eutranPositioningData.getGNSSPositioningMethodsAndGNSSIds(eutranPositioningData.getGNSSPositioningDataSet());
                        JsonObject plaEUtranGnssPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaEUtranGnssInfoJsonObject = new JsonObject();
                        JsonObject[] plaEUtranGnssPosInfoMethodIdUsage  = new JsonObject[methodsAndGanssIds.size()];
                        String method, id;
                        int itemIndex = 0, usage;
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            method = entry.getKey();
                            id = entry.getValue();
                            usage = eutranPositioningData.getUsageCode(eutranPositioningData.getGNSSPositioningDataSet(), itemIndex);
                            String property = "Item-" + itemIndex;
                            plaEUtranGnssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                            plaEUtranGnssPosInfoDataSetJsonObject.add(property, plaEUtranGnssPosInfoMethodIdUsage[itemIndex]);
                            writeEUtranGnssPositioningMethod(method, plaEUtranGnssPosInfoMethodIdUsage[itemIndex]);
                            writeEUtranGnssPositioningGnssId(id, plaEUtranGnssPosInfoMethodIdUsage[itemIndex]);
                            writeEUtranGnssPositioningUsage(usage, plaEUtranGnssPosInfoMethodIdUsage[itemIndex]);
                            itemIndex++;
                        }
                        plaEUtranGnssInfoJsonObject.add("GnssPositioningDataSet", plaEUtranGnssPosInfoDataSetJsonObject);
                        // Write EUTRAN-Positioning-Data AVP GNSS Positioning Data Set values from SLg PLA
                        plaJsonObject.add("EUtranPositioningData", plaEUtranGnssInfoJsonObject);
                    }
                    if (eutranPositioningData.getAdditionalPositioningDataSet() != null) {
                        Multimap<String, String> methodsAndAddPosIds = eutranPositioningData.getEUtranAdditionalPositioningMethodsAndIds(eutranPositioningData.getAdditionalPositioningDataSet());
                        JsonObject plaEUtranAddPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaEUtranAddInfoJsonObject = new JsonObject();
                        JsonObject[] plaEUtranAddPosInfoMethodAndId = new JsonObject[methodsAndAddPosIds.size()];
                        String method, id;
                        int itemIndex = 0, usage;
                        for (Map.Entry<String, String> entry : methodsAndAddPosIds.entries()) {
                            method = entry.getKey();
                            id = entry.getValue();
                            usage = eutranPositioningData.getUsageCode(eutranPositioningData.getAdditionalPositioningDataSet(), itemIndex);
                            String property = "Item-" + itemIndex;
                            plaEUtranAddPosInfoMethodAndId[itemIndex] = new JsonObject();
                            plaEUtranAddPosInfoDataSetJsonObject.add(property, plaEUtranAddPosInfoMethodAndId[itemIndex]);
                            writeEUtranAddPositioningMethod(method, plaEUtranAddPosInfoMethodAndId[itemIndex]);
                            writeUtranAddPositioningPosId(id, plaEUtranAddPosInfoMethodAndId[itemIndex]);
                            writeUtranAddPositioningUsage(usage, plaEUtranAddPosInfoMethodAndId[itemIndex]);
                            itemIndex++;
                        }
                        plaEUtranAddInfoJsonObject.add("AdditionalPositioningDataSet", plaEUtranAddPosInfoDataSetJsonObject);
                        // Write EUTRAN-Positioning-Data AVP Additional Positioning Data Set values from SLg PLA
                        plaJsonObject.add("EUtranPositioningData", plaEUtranAddInfoJsonObject);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            /*** ECGI AVP ***/
            if (pla.getEcgi() != null) {
                try {
                    EUtranCgi eutranCgi = new EUtranCgiImpl(pla.getEcgi());
                    ecgiMcc = eutranCgi.getMCC();
                    ecgiMnc = eutranCgi.getMNC();
                    eci = eutranCgi.getEci();
                    eNBId = eutranCgi.getENodeBId();
                    ecgiCi = eutranCgi.getCi();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
                // Write ECGI values from SLg PLA
                JsonObject ecgiJsonObject = new JsonObject();
                writeMcc(ecgiMcc, ecgiJsonObject);
                writeMnc(ecgiMnc, ecgiJsonObject);
                writeEUtranEci(eci, ecgiJsonObject);
                writeENBId(eNBId, ecgiJsonObject);
                writeEUtranCellId(ecgiCi, ecgiJsonObject);
                plaJsonObject.add("ECGI", ecgiJsonObject);
            }

            /*** GERAN-Positioning-Info AVP ***/
            if (pla.getGeranPositioningInfoAvp() != null) {
                if (pla.getGeranPositioningInfoAvp().getGERANPositioningData() != null) {
                    try {
                        PositioningDataInformation geranPositioningDataInformation = AVPHandler.lteGeranPosDataInfo2MapGeranPosDataInfo(pla.getGeranPositioningInfoAvp().getGERANPositioningData());
                        HashMap<String, Integer> methodsAndUsage = geranPositioningDataInformation.getPositioningDataSet();
                        JsonObject plaGeranPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaGeranPosInfoJsonObject = new JsonObject();
                        JsonObject[] plaGeranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                        int itemIndex = 0;
                        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                            String property = "Item-" + itemIndex;
                            String method = item.getKey();
                            Integer usage = item.getValue();
                            plaGeranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                            plaGeranPosInfoDataSetJsonObject.add(property, plaGeranPosInfoMethodAndUsage[itemIndex]);
                            writeGeranPositioningMethod(method, plaGeranPosInfoMethodAndUsage[itemIndex]);
                            writeGeranPositioningUsage(usage, plaGeranPosInfoMethodAndUsage[itemIndex]);
                            itemIndex++;
                        }
                        plaGeranPosInfoJsonObject.add("PositioningDataSet", plaGeranPosInfoDataSetJsonObject);
                        // Write GERAN Positioning Info values from SLg PLA
                        plaJsonObject.add("GeranPositioningData", plaGeranPosInfoJsonObject);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
                if (pla.getGeranPositioningInfoAvp().getGERANGANSSPositioningData() != null) {
                    try {
                        GeranGANSSpositioningData geranGANSSpositioningData = AVPHandler.lteGeranGanssPosDataInfo2MapGeranGanssPosDataInfo(pla.getGeranPositioningInfoAvp().getGERANGANSSPositioningData());
                        Multimap<String, String> methodsAndGanssIds = geranGANSSpositioningData.getGeranGANSSPositioningMethodsAndGANSSIds();
                        JsonObject plaGeranGanssPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaGeranGanssInfoJsonObject = new JsonObject();
                        JsonObject[] plaGeranGanssPosInfoMethodIdUsage = new JsonObject[methodsAndGanssIds.size()];
                        String method, id;
                        int itemIndex = 0, usage;
                        for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                            method = item.getKey();
                            id = item.getValue();
                            usage = geranGANSSpositioningData.getUsageCode(geranGANSSpositioningData.getData(), itemIndex+1);
                            String property = "Item-" + itemIndex;
                            plaGeranGanssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                            plaGeranGanssPosInfoDataSetJsonObject.add(property, plaGeranGanssPosInfoMethodIdUsage[itemIndex]);
                            writeGeranGanssPositioningMethod(method, plaGeranGanssPosInfoMethodIdUsage[itemIndex]);
                            writeGeranGanssPositioningGanssId(id, plaGeranGanssPosInfoMethodIdUsage[itemIndex]);
                            writeGeranGanssPositioningUsage(usage, plaGeranGanssPosInfoMethodIdUsage[itemIndex]);
                            itemIndex++;
                        }
                        plaGeranGanssInfoJsonObject.add("GanssPositioningDataSet", plaGeranGanssPosInfoDataSetJsonObject);
                        // Write GERAN GANSS Positioning Info values from SLg PLA
                        plaJsonObject.add("GeranGANSSPositioningData", plaGeranGanssInfoJsonObject);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            /*** Cell-Global-Identity AVP ***/
            if (pla.getCellGlobalIdentity() != null) {
                CellGlobalIdOrServiceAreaIdFixedLength cellGlobalId = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(pla.getCellGlobalIdentity());
                try {
                    cgiMcc = cellGlobalId.getMCC();
                    cgiMnc = cellGlobalId.getMNC();
                    cgiLac = cellGlobalId.getLac();
                    cgiCi = cellGlobalId.getCellIdOrServiceAreaCode();
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
                // Write CGI values from SLg PLA
                JsonObject cgiJsonObject = new JsonObject();
                writeMcc(cgiMcc, cgiJsonObject);
                writeMnc(cgiMnc, cgiJsonObject);
                writeLac(cgiLac, cgiJsonObject);
                writeCellId(cgiCi, cgiJsonObject);
                plaJsonObject.add("CGI", cgiJsonObject);
            }

            /*** UTRAN-Positioning-Info AVP ***/
            if (pla.getUtranPositioningInfoAvp() != null) {
                if (pla.getUtranPositioningInfoAvp().getUTRANPositioningData() != null) {
                    try {
                        UtranPositioningDataInfo utranPositioningDataInfo = AVPHandler.lteUtranPosData2MapUtranPosDataInfo(pla.getUtranPositioningInfoAvp().getUTRANPositioningData());
                        HashMap<String, Integer> methodsAndUsage = utranPositioningDataInfo.getUtranPositioningDataSet();
                        JsonObject plaUtranPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaUtranPosInfoJsonObject = new JsonObject();
                        JsonObject[] plaUtranPosInfoMethodAndUsage = new JsonObject[methodsAndUsage.size()];
                        int itemIndex = 0;
                        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                            String property = "Item-" + itemIndex;
                            String method = item.getKey();
                            Integer usage = item.getValue();
                            plaUtranPosInfoMethodAndUsage[itemIndex] = new JsonObject();
                            plaUtranPosInfoDataSetJsonObject.add(property, plaUtranPosInfoMethodAndUsage[itemIndex]);
                            writeUtranPositioningMethod(method, plaUtranPosInfoMethodAndUsage[itemIndex]);
                            writeUtranPositioningUsage(usage, plaUtranPosInfoMethodAndUsage[itemIndex]);
                            itemIndex++;
                        }
                        plaUtranPosInfoJsonObject.add("PositioningDataSet", plaUtranPosInfoDataSetJsonObject);
                        // Write GERAN Positioning Info values from SLg PLA
                        plaJsonObject.add("UtranPositioningData", plaUtranPosInfoJsonObject);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
                if (pla.getUtranPositioningInfoAvp().getUTRANGANSSPositioningData() != null) {
                    try {
                        UtranGANSSpositioningData utranGANSSpositioningData = AVPHandler.lteUtranGanssPosData2MapUtranGanssPosDataInfo(pla.getUtranPositioningInfoAvp().getUTRANGANSSPositioningData());
                        Multimap<String, String> methodsAndGanssIds = utranGANSSpositioningData.getUtranGANSSPositioningMethodsAndGANSSIds();
                        JsonObject plaUtranGanssPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaUtranGanssInfoJsonObject = new JsonObject();
                        JsonObject[] plaUtranGanssPosInfoMethodIdUsage  = new JsonObject[methodsAndGanssIds.size()];
                        String method, id;
                        int itemIndex = 0, usage;
                        for (Map.Entry<String, String> item : methodsAndGanssIds.entries()) {
                            method = item.getKey();
                            id = item.getValue();
                            usage = utranGANSSpositioningData.getUsageCode(utranGANSSpositioningData.getData(), itemIndex);
                            String property = "Item-" + itemIndex;
                            plaUtranGanssPosInfoMethodIdUsage[itemIndex] = new JsonObject();
                            plaUtranGanssPosInfoDataSetJsonObject.add(property, plaUtranGanssPosInfoMethodIdUsage[itemIndex]);
                            writeUtranGanssPositioningMethod(method, plaUtranGanssPosInfoMethodIdUsage[itemIndex]);
                            writeUtranGanssPositioningGanssId(id, plaUtranGanssPosInfoMethodIdUsage[itemIndex]);
                            writeUtranGanssPositioningUsage(usage, plaUtranGanssPosInfoMethodIdUsage[itemIndex]);
                            itemIndex++;
                        }
                        plaUtranGanssInfoJsonObject.add("GanssPositioningDataSet", plaUtranGanssPosInfoDataSetJsonObject);
                        // Write UTRAN GANSS Positioning Info values from SLg PLA
                        plaJsonObject.add("UtranGANSSPositioningData", plaUtranGanssInfoJsonObject);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
                if (pla.getUtranPositioningInfoAvp().getUTRANAdditionalPositioningData() != null) {
                    try {
                        UtranAdditionalPositioningData utranAdditionalPositioningData = AVPHandler.lteUtranAddPosData2MapUtranAdditionalPositioningdata(pla.getUtranPositioningInfoAvp().getUTRANAdditionalPositioningData());
                        Multimap<String, String> methodsAndAddPosIds = utranAdditionalPositioningData.getUtranAdditionalPositioningMethodsAndIds();
                        JsonObject plaUtranAddPosInfoDataSetJsonObject = new JsonObject();
                        JsonObject plaUtranAddInfoJsonObject = new JsonObject();
                        JsonObject[] plaUtranAddPosInfoMethodAndId = new JsonObject[methodsAndAddPosIds.size()];
                        String method, id;
                        int itemIndex = 0, usage;
                        for (Map.Entry<String, String> item : methodsAndAddPosIds.entries()) {
                            method = item.getKey();
                            id = item.getValue();
                            usage = utranAdditionalPositioningData.getUsageCode(utranAdditionalPositioningData.getData(), itemIndex);
                            String property = "Item-" + itemIndex;
                            plaUtranAddPosInfoMethodAndId[itemIndex] = new JsonObject();
                            plaUtranAddPosInfoDataSetJsonObject.add(property, plaUtranAddPosInfoMethodAndId[itemIndex]);
                            writeUtranAddPositioningMethod(method, plaUtranAddPosInfoMethodAndId[itemIndex]);
                            writeUtranAddPositioningPosId(id, plaUtranAddPosInfoMethodAndId[itemIndex]);
                            writeUtranAddPositioningUsage(usage, plaUtranAddPosInfoMethodAndId[itemIndex]);
                            itemIndex++;
                        }
                        plaUtranAddInfoJsonObject.add("AdditionalPositioningDataSet", plaUtranAddPosInfoDataSetJsonObject);
                        // Write UTRAN Positioning Info values from SLg PLA
                        plaJsonObject.add("UtranAdditionalPositioningData", plaUtranAddInfoJsonObject);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            /*** Service-Area-Identity AVP ***/
            if (pla.getServiceAreaIdentity() != null) {
                CellGlobalIdOrServiceAreaIdFixedLength serviceAreaId = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(pla.getServiceAreaIdentity());
                try {
                    saiMcc = serviceAreaId.getMCC();
                    saiMnc = serviceAreaId.getMNC();
                    saiLac = serviceAreaId.getLac();
                    sac = serviceAreaId.getCellIdOrServiceAreaCode();
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
                // Write SAI values from SLg PLA
                JsonObject saiJsonObject = new JsonObject();
                writeMcc(saiMcc, saiJsonObject);
                writeMnc(saiMnc, saiJsonObject);
                writeLac(saiLac, saiJsonObject);
                writeServiceAreaCode(sac, saiJsonObject);
                plaJsonObject.add("SAI", saiJsonObject);
            }

            /*** Serving-Node AVP ***/
            if (pla.getServingNodeAvp() != null) {
                if (pla.getServingNodeAvp().getMMEName() != null)
                    mmeName = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getMMEName()).getData());
                if (pla.getServingNodeAvp().getMMERealm() != null)
                    mmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getMMERealm()).getData());
                if (pla.getServingNodeAvp().getSGSNName() != null)
                    sgsnName = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getSGSNName()).getData());
                if (pla.getServingNodeAvp().getSGSNRealm() != null)
                    sgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getSGSNRealm()).getData());
                if (pla.getServingNodeAvp().getSGSNNumber() != null)
                    sgsnNumber = toTBCDString(pla.getServingNodeAvp().getSGSNNumber());
                if (pla.getServingNodeAvp().get3GPPAAAServerName() != null)
                    aaaServerName = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().get3GPPAAAServerName()).getData());
                if (pla.getServingNodeAvp().getMSCNumber() != null)
                    mscNumber = toTBCDString(pla.getServingNodeAvp().getMSCNumber());
                if (pla.getServingNodeAvp().hasLcsCapabilitiesSets())
                    lcsCapabilitySets = pla.getServingNodeAvp().getLcsCapabilitiesSets();
                if (pla.getServingNodeAvp().hasGMLCAddress()) {
                    gmlcAddress = bytesToHexString(pla.getServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(gmlcAddress));
                        gmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                }
                // Write Serving Node values from SLg PLA
                JsonObject targetServingNodeJsonObject = new JsonObject();
                writeMmeName(mmeName, targetServingNodeJsonObject);
                writeMmeRealm(mmeRealm, targetServingNodeJsonObject);
                writeSgsnName(sgsnName, targetServingNodeJsonObject);
                writeSgsnRealm(sgsnRealm, targetServingNodeJsonObject);
                writeSgsnNumber(sgsnNumber, targetServingNodeJsonObject);
                write3gppAaaServerName(aaaServerName, targetServingNodeJsonObject);
                writeMscNumber(mscNumber, targetServingNodeJsonObject);
                writeLCSCapabilitySets(lcsCapabilitySets, targetServingNodeJsonObject);
                writeGmlcAddress(gmlcAddress, targetServingNodeJsonObject);
                plaJsonObject.add("TargetServingNodeForHandover", targetServingNodeJsonObject);
            }

            /*** PLA-Flags ***/
            if (pla.getPlaFlags() != null) {
                writePlaFlags(pla.getPlaFlags(), plaJsonObject);
            }

            /*** ESMLC-Cell-Info AVP ***/
            if (pla.getEsmlcCellInfoAvp() != null) {
                if (pla.getEsmlcCellInfoAvp().getECGI() != null) {
                    try {
                        EUtranCgi eutranCgi = new EUtranCgiImpl(pla.getEsmlcCellInfoAvp().getECGI());
                        ecgiMcc = eutranCgi.getMCC();
                        ecgiMnc = eutranCgi.getMNC();
                        eci = eutranCgi.getEci();
                        eNBId = eutranCgi.getENodeBId();
                        ecgiCi = eutranCgi.getCi();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                if (pla.getEsmlcCellInfoAvp().getCellPortionID() > -1)
                    cellPortionId = pla.getEsmlcCellInfoAvp().getCellPortionID();
                // Write ESMLC Cell Info values from SLg PLA
                JsonObject ecgiJsonObject = new JsonObject();
                writeMcc(ecgiMcc, ecgiJsonObject);
                writeMnc(ecgiMnc, ecgiJsonObject);
                writeEUtranEci(eci, ecgiJsonObject);
                writeENBId(eNBId, ecgiJsonObject);
                writeEUtranCellId(ecgiCi, ecgiJsonObject);
                writeCellPortionId(cellPortionId, ecgiJsonObject);
                plaJsonObject.add("ESMLCCellInfo", ecgiJsonObject);
            }

            /*** Civic-Address AVP ***/
            if (pla.getCivicAddress() != null) {
                civicAddress = pla.getCivicAddress();
                CivicAddressXmlReader reader = new CivicAddressXmlReader();
                reader.civicAddressXMLReader(civicAddress);
                CivicAddressElements civicAddressElements = reader.getCivicAddressElements();
                JsonObject plaCivicAddressJsonObject = new JsonObject();
                // Write Civic Address from SLg PLA
                writeCivicAddress(civicAddressElements, plaCivicAddressJsonObject);
                plaJsonObject.add("CivicAddress", plaCivicAddressJsonObject);
            }

            /*** Barometric-Pressure AVP ***/
            if (pla.getBarometricPressure() != null) {
                JsonObject plrBarometricPressureJsonObject = new JsonObject();
                barometricPressure = pla.getBarometricPressure();
                // Write Barometric Pressure from SLg PLA
                writeBarometricPressure(barometricPressure, plrBarometricPressureJsonObject);
                plaJsonObject.add("BarometricPressure", plrBarometricPressureJsonObject);
            }
        }

        // Write values retrieved from SLh RIA
        if (riaJsonObject != null)
            riaPlrPlaJsonObject.add("Routing-Info-Answer", riaJsonObject);

        // Write values retrieved from SLg PLA
        if (plaJsonObject != null)
            riaPlrPlaJsonObject.add("Provide-Location-Answer", plaJsonObject);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(riaPlrPlaJsonObject);
    }
}
