package org.mobicents.gmlc.slee.map;

import com.google.common.collect.Multimap;
import org.mobicents.gmlc.slee.primitives.EllipsoidPoint;
import org.mobicents.gmlc.slee.primitives.Polygon;
import org.mobicents.gmlc.slee.primitives.PolygonImpl;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdFixedLength;
import org.restcomm.protocols.ss7.map.api.primitives.CellGlobalIdOrServiceAreaIdOrLAI;
import org.restcomm.protocols.ss7.map.api.primitives.LAIFixedLength;
import org.restcomm.protocols.ss7.map.api.service.lsm.AddGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.lsm.ExtGeographicalInformation;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TypeOfShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import static org.mobicents.gmlc.slee.http.JsonWriter.bytesToHexString;
import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class MapLsmResponseHelperForMLP {

    protected final Logger logger = LoggerFactory.getLogger(MapLsmResponseHelperForMLP.class.getName());
    protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
    protected static final DecimalFormat radiusFormat = new DecimalFormat("#0.00");
    protected static final DecimalFormat angleFormat = new DecimalFormat("#0.00");
    protected static final DecimalFormat axisFormat = new DecimalFormat("#0.00");

    String msisdn, imsi, lmsi, imei;
    private Integer mcc, mnc, lac, ci, sac;
    private String vlrNumber, mscNumber, sgsnNumber;
    private boolean gprsNodeIndicator;
    private String nnn;
    private String mmeName, sgsnName, tgppAAAServerName, hGmlcAddress, vGmlcAddress, pprAddress;
    private String typeOfShape;
    private Double latitude;
    private Double longitude;
    private Double uncertainty , uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis,
        uncertaintyAltitude, uncertaintyInnerRadius, offsetAngle, includedAngle;
    private Integer confidence, altitude, innerRadius, numberOfPoints;
    private String positioningMethod;
    private Double radius = null;
    Polygon polygon = null;
    EllipsoidPoint[] polygonEllipsoidPoints;
    Double[][] polygonArray;
    private Integer ageOfLocationEstimate, accuracyFulfilmentIndicator;
    private Boolean deferredMTLRresponseIndicator, molrShortCircuitIndicator, saiPresent = false;
    private Integer horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed;
    private String velocityType;
    private Integer terminationCause, sequenceNumber, reportingAmount, reportingInterval, lcsEvent, lcsServiceTypeID, clientReferenceNumber;
    Integer lcsReferenceNumber;
    Boolean pseudonymIndicator, moLrShortCircuitIndicator, deferredEventLocationType, enteringArea, insideArea, leavingArea, msAvailable, periodicLDR,
        supportedLCSCapabilitySets, addSupportedLCSCapabilitySets,
        supportedLCSCapabilitySetRelease98_99, supportedLCSCapabilitySetRelease4, supportedLCSCapabilitySetRelease5, supportedLCSCapabilitySetRelease6,
        supportedLCSCapabilitySetRelease7, addSupportedLCSCapabilitySetRelease98_99, addSupportedLCSCapabilitySetRelease4,
        addSupportedLCSCapabilitySetRelease5, addSupportedLCSCapabilitySetRelease6, addSupportedLCSCapabilitySetRelease7;
    private Integer ciOrsac;
    private String civicAddress;

    public MapLsmResponseHelperForMLP() {
    }

    public void handleSriLcsResponseValue(SriLcsResponseParams sriLcs, String txMsisdn, String txIMSI) {

        if (sriLcs.getMsisdn() != null) {
            this.msisdn = sriLcs.getMsisdn().getAddress();
        } else {
            this.msisdn = txMsisdn;
        }

        if (sriLcs.getImsi() != null) {
            this.imsi = new String(sriLcs.getImsi().getData().getBytes());
        } else {
            this.imsi = txIMSI;
        }

        if (sriLcs.getLmsi() != null) {
            this.lmsi = bytesToHex(sriLcs.getLmsi().getData());
        }

        if (sriLcs.getLcsLocationInfo() != null) {
            if (sriLcs.getLcsLocationInfo().getNetworkNodeNumber() != null) {
                this.nnn = sriLcs.getLcsLocationInfo().getNetworkNodeNumber().getAddress();
            }

            if (sriLcs.getLcsLocationInfo().getGprsNodeIndicator()) {
                this.gprsNodeIndicator = sriLcs.getLcsLocationInfo().getGprsNodeIndicator();
            }

            if (sriLcs.getLcsLocationInfo().getMmeName() != null) {
                this.mmeName = new String(sriLcs.getLcsLocationInfo().getMmeName().getData());
            }

            if (sriLcs.getLcsLocationInfo().getSgsnName() != null) {
                this.sgsnName = new String(sriLcs.getLcsLocationInfo().getSgsnName().getData());
            }

            if (sriLcs.getLcsLocationInfo().getAaaServerName() != null) {
                this.tgppAAAServerName = new String(sriLcs.getLcsLocationInfo().getAaaServerName().getData());
            }
        }

        if (sriLcs.getHGmlcAddress() != null) {
            this.hGmlcAddress = bytesToHexString(sriLcs.getHGmlcAddress().getGSNAddressData());
            try {
                InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(hGmlcAddress));
                this.hGmlcAddress = address.getHostAddress();
            } catch (UnknownHostException e) {
                logger.error(e.getMessage());
            }
        }

        if (sriLcs.getVGmlcAddress() != null) {
            this.vGmlcAddress = bytesToHexString(sriLcs.getVGmlcAddress().getGSNAddressData());
            try {
                InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.vGmlcAddress));
                this.vGmlcAddress = address.getHostAddress();
            } catch (UnknownHostException e) {
                logger.error(e.getMessage());

            }
        }

        if (sriLcs.getPprAddress() != null) {
            this.pprAddress = bytesToHexString(sriLcs.getPprAddress().getGSNAddressData());
            try {
                InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.pprAddress));
                this.pprAddress = address.getHostAddress();
            } catch (UnknownHostException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void handlePslResponseValues(PslResponseParams psl) {
        if (psl != null) {
            // Location Estimate
            if (psl.getLocationEstimate() != null) {
                ExtGeographicalInformation locationEstimate = psl.getLocationEstimate();
                if (locationEstimate.getTypeOfShape() == TypeOfShape.Polygon) {
                    this.typeOfShape = locationEstimate.getTypeOfShape().name();
                } else {
                    this.typeOfShape = locationEstimate.getTypeOfShape().name();
                    this.latitude = locationEstimate.getLatitude();
                    this.longitude = locationEstimate.getLongitude();
                    this.uncertainty = locationEstimate.getUncertainty();
                    this.uncertaintySemiMajorAxis = locationEstimate.getUncertaintySemiMajorAxis();
                    this.uncertaintySemiMinorAxis = locationEstimate.getUncertaintySemiMinorAxis();
                    this.angleOfMajorAxis = locationEstimate.getAngleOfMajorAxis();
                    this.confidence = locationEstimate.getConfidence();
                    this.altitude = locationEstimate.getAltitude();
                    this.uncertaintyAltitude = locationEstimate.getUncertaintyAltitude();
                    this.innerRadius = locationEstimate.getInnerRadius();
                    this.uncertaintyInnerRadius = locationEstimate.getUncertaintyRadius();
                    this.offsetAngle = locationEstimate.getOffsetAngle();
                    this.includedAngle = locationEstimate.getIncludedAngle();
                }
            }

            // Additional Location Estimate
            if (psl.getAdditionalLocationEstimate() != null) {
                AddGeographicalInformation additionalLocationEstimate = psl.getAdditionalLocationEstimate();
                this.typeOfShape = additionalLocationEstimate.getTypeOfShape().name();
                if (additionalLocationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    this.latitude = additionalLocationEstimate.getLatitude();
                    this.longitude = additionalLocationEstimate.getLongitude();
                    this.uncertainty = additionalLocationEstimate.getUncertainty();
                    this.uncertaintySemiMajorAxis = additionalLocationEstimate.getUncertaintySemiMajorAxis();
                    this.uncertaintySemiMinorAxis = additionalLocationEstimate.getUncertaintySemiMinorAxis();
                    this.angleOfMajorAxis = additionalLocationEstimate.getAngleOfMajorAxis();
                    this.confidence = additionalLocationEstimate.getConfidence();
                    this.altitude = additionalLocationEstimate.getAltitude();
                    this.uncertaintyAltitude = additionalLocationEstimate.getUncertaintyAltitude();
                    this.innerRadius = additionalLocationEstimate.getInnerRadius();
                    this.uncertaintyInnerRadius = additionalLocationEstimate.getUncertaintyRadius();
                    this.offsetAngle = additionalLocationEstimate.getOffsetAngle();
                    this.includedAngle = additionalLocationEstimate.getIncludedAngle();
                } else {
                    // PSL Additional Location Estimate for TypeOfShape.Polygon
                    this.polygon = new PolygonImpl(additionalLocationEstimate.getData());
                    this.numberOfPoints = polygon.getNumberOfPoints();
                    this.polygonEllipsoidPoints = new EllipsoidPoint[numberOfPoints];
                    for (int point = 0; point < numberOfPoints; point++) {
                        this.polygonEllipsoidPoints[point] = polygon.getEllipsoidPoint(point);
                    }
                    try {
                        ((PolygonImpl) this.polygon).setData(polygonEllipsoidPoints);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (psl.getAgeOfLocationEstimate() != null) {
                this.ageOfLocationEstimate = psl.getAgeOfLocationEstimate();
            }

            if (psl.getAccuracyFulfilmentIndicator() != null) {
                this.accuracyFulfilmentIndicator = psl.getAccuracyFulfilmentIndicator().getIndicator();
            }

            if (psl.isDeferredMTLRResponseIndicator()) {
                this.deferredMTLRresponseIndicator = psl.isDeferredMTLRResponseIndicator();
            }

            if (psl.isMoLrShortCircuitIndicator()) {
                this.molrShortCircuitIndicator = psl.isMoLrShortCircuitIndicator();
            }

            if (psl.isSaiPresent()) {
                this.saiPresent = true;
            }

            if (psl.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                CellGlobalIdOrServiceAreaIdOrLAI cgiOrSaiOrLai = psl.getCellGlobalIdOrServiceAreaIdOrLAI();
                LAIFixedLength laiFixedLength = cgiOrSaiOrLai.getLAIFixedLength();
                CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength = cgiOrSaiOrLai.getCellGlobalIdOrServiceAreaIdFixedLength();
                if (laiFixedLength != null) {
                    try {
                        this.mcc = laiFixedLength.getMCC();
                        this.mnc = laiFixedLength.getMNC();
                        this.lac = laiFixedLength.getLac();
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }

                } else if (cellGlobalIdOrServiceAreaIdFixedLength != null) {
                    try {
                        this.mcc = cellGlobalIdOrServiceAreaIdFixedLength.getMCC();
                        this.mnc = cellGlobalIdOrServiceAreaIdFixedLength.getMNC();
                        this.lac = cellGlobalIdOrServiceAreaIdFixedLength.getLac();
                        ciOrsac = cellGlobalIdOrServiceAreaIdFixedLength.getCellIdOrServiceAreaCode();
                        if (this.saiPresent)
                            this.sac = ciOrsac;
                        else
                            this.ci = ciOrsac;
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (psl.getGeranPositioningDataInformation() != null || psl.getGeranGANSSpositioningData() != null) {
                try {
                    if (psl.getGeranPositioningDataInformation() != null) {
                        ArrayList<String> methods = psl.getGeranPositioningDataInformation().getLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                            this.positioningMethod = method;
                        }
                    } else if (psl.getGeranGANSSpositioningData() != null) {
                        Multimap<String, String> methodsAndGanssIds = psl.getGeranGANSSpositioningData().getGeranGANSSPositioningMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            } else if (psl.getUtranPositioningDataInfo() != null || psl.getUtranGANSSpositioningData() != null || psl.getUtranAdditionalPositioningData() != null) {
                try {
                    // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                    if (psl.getUtranPositioningDataInfo() != null) {
                        ArrayList<String> methods = psl.getUtranPositioningDataInfo().getUtranLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            this.positioningMethod = method;
                        }
                    } else if (psl.getUtranGANSSpositioningData() != null) {
                        Multimap<String, String> methodsAndGanssIds = psl.getUtranGANSSpositioningData().getLocationGeneratedMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    } else if (psl.getUtranAdditionalPositioningData() != null) {
                        Multimap<String, String> methodsAndPosId = psl.getUtranAdditionalPositioningData().getLocationGeneratedMethodsAndAddPosIds();
                        for (Map.Entry<String, String> entry : methodsAndPosId.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            }

            if (psl.getVelocityEstimate() != null) {
                if (psl.getVelocityEstimate().getHorizontalSpeed() > -1)
                    this.horizontalSpeed = psl.getVelocityEstimate().getHorizontalSpeed();
                if (psl.getVelocityEstimate().getBearing() > -1)
                    this.bearing = psl.getVelocityEstimate().getBearing();
                if (psl.getVelocityEstimate().getVerticalSpeed() > -1)
                    this.verticalSpeed = psl.getVelocityEstimate().getVerticalSpeed();
                if (psl.getVelocityEstimate().getUncertaintyHorizontalSpeed() > -1)
                    this.uncertaintyHorizontalSpeed = psl.getVelocityEstimate().getUncertaintyHorizontalSpeed();
                if (psl.getVelocityEstimate().getUncertaintyVerticalSpeed() > -1)
                    this.uncertaintyVerticalSpeed = psl.getVelocityEstimate().getUncertaintyVerticalSpeed();
                if (psl.getVelocityEstimate().getVelocityType() != null)
                    this.velocityType = psl.getVelocityEstimate().getVelocityType().name();
            }

            /*** UTRAN Civic Address ***/
            if (psl.getUtranCivicAddress() != null) {
                this.civicAddress = new String(psl.getUtranCivicAddress().getData(), StandardCharsets.UTF_8);
            }
        }
    }

    public void handleSlrRequestValues(SlrRequestParams slr, Integer clientReferenceNumber) {

        if (slr != null) {

            if (slr.getLcsReferenceNumber() != null) {
                if (slr.getLcsReferenceNumber() < 0)
                    this.lcsReferenceNumber = slr.getLcsReferenceNumber() + 256;
                else
                    this.lcsReferenceNumber = slr.getLcsReferenceNumber();
            }

            if (clientReferenceNumber != null) {
                this.clientReferenceNumber = clientReferenceNumber;
            }

            if (slr.getMsisdn() != null) {
                this.msisdn = slr.getMsisdn().getAddress();
            }

            if (slr.getImsi() != null) {
                this.imsi = new String(slr.getImsi().getData().getBytes());
            }

            if (slr.getImei() != null) {
                this.imei = slr.getImei().getIMEI();
            }

            if (slr.getLcsServiceTypeID() != null) {
                this.lcsServiceTypeID = slr.getLcsServiceTypeID();
            }

            // Location Estimate
            if (slr.getLocationEstimate() != null) {
                ExtGeographicalInformation locationEstimate = slr.getLocationEstimate();
                if (locationEstimate.getTypeOfShape() == TypeOfShape.Polygon) {
                    this.typeOfShape = locationEstimate.getTypeOfShape().name();
                } else {
                    this.typeOfShape = locationEstimate.getTypeOfShape().name();
                    this.latitude = locationEstimate.getLatitude();
                    this.longitude = locationEstimate.getLongitude();
                    this.uncertainty = locationEstimate.getUncertainty();
                    this.uncertaintySemiMajorAxis = locationEstimate.getUncertaintySemiMajorAxis();
                    this.uncertaintySemiMinorAxis = locationEstimate.getUncertaintySemiMinorAxis();
                    this.angleOfMajorAxis = locationEstimate.getAngleOfMajorAxis();
                    this.confidence = locationEstimate.getConfidence();
                    this.altitude = locationEstimate.getAltitude();
                    this.uncertaintyAltitude = locationEstimate.getUncertaintyAltitude();
                    this.innerRadius = locationEstimate.getInnerRadius();
                    this.uncertaintyInnerRadius = locationEstimate.getUncertaintyRadius();
                    this.offsetAngle = locationEstimate.getOffsetAngle();
                    this.includedAngle = locationEstimate.getIncludedAngle();
                }
            }

            // Additional Location Estimate
            if (slr.getAdditionalLocationEstimate() != null) {
                AddGeographicalInformation additionalLocationEstimate = slr.getAdditionalLocationEstimate();
                this.typeOfShape = additionalLocationEstimate.getTypeOfShape().name();
                if (additionalLocationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    this.latitude = additionalLocationEstimate.getLatitude();
                    this.longitude = additionalLocationEstimate.getLongitude();
                    this.uncertainty = additionalLocationEstimate.getUncertainty();
                    this.uncertaintySemiMajorAxis = additionalLocationEstimate.getUncertaintySemiMajorAxis();
                    this.uncertaintySemiMinorAxis = additionalLocationEstimate.getUncertaintySemiMinorAxis();
                    this.angleOfMajorAxis = additionalLocationEstimate.getAngleOfMajorAxis();
                    this.confidence = additionalLocationEstimate.getConfidence();
                    this.altitude = additionalLocationEstimate.getAltitude();
                    this.uncertaintyAltitude = additionalLocationEstimate.getUncertaintyAltitude();
                    this.innerRadius = additionalLocationEstimate.getInnerRadius();
                    this.uncertaintyInnerRadius = additionalLocationEstimate.getUncertaintyRadius();
                    this.offsetAngle = additionalLocationEstimate.getOffsetAngle();
                    this.includedAngle = additionalLocationEstimate.getIncludedAngle();
                } else {
                    // PSL Additional Location Estimate for TypeOfShape.Polygon
                    this.polygon = new PolygonImpl(additionalLocationEstimate.getData());
                    this.numberOfPoints = polygon.getNumberOfPoints();
                    this.polygonEllipsoidPoints = new EllipsoidPoint[numberOfPoints];
                    for (int point = 0; point < numberOfPoints; point++) {
                        this.polygonEllipsoidPoints[point] = polygon.getEllipsoidPoint(point);
                    }
                    try {
                        ((PolygonImpl) this.polygon).setData(polygonEllipsoidPoints);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (slr.getAgeOfLocationEstimate() != null) {
                this.ageOfLocationEstimate = slr.getAgeOfLocationEstimate();
            }

            if (slr.getAccuracyFulfilmentIndicator() != null) {
                this.accuracyFulfilmentIndicator = slr.getAccuracyFulfilmentIndicator().getIndicator();
            }

            if (slr.getSaiPresent())
                this.saiPresent = true;

            if (slr.getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                /*** LAI fixed length ***/
                if (slr.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                    try {
                        this.mcc = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC();
                        this.mnc = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC();
                        this.lac = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac();
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }

                }
                /*** CGI or SAI fixed length ***/
                if (slr.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                    try {
                        this.mcc = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                        this.mnc = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                        this.lac = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                        ciOrsac = slr.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                        if (this.saiPresent)
                            this.sac = ciOrsac;
                        else
                            this.ci = ciOrsac;
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            /*if (slr.getLcsClientID() != null) {
                if (slr.getLcsClientID().getLCSClientType() != null && (slr.getLcsClientID().getLCSClientType().getType() > Integer.MIN_VALUE
                    && slr.getLcsClientID().getLCSClientType().getType() < Integer.MAX_VALUE)) {
                    lcsClientIDType = slr.getLcsClientID().getLCSClientType().getType();
                }
                if (slr.getLcsClientID().getLCSClientExternalID() != null) {
                    if (slr.getLcsClientID().getLCSClientExternalID().getExternalAddress() != null)
                        lcsClientIDExternalID = slr.getLcsClientID().getLCSClientExternalID().getExternalAddress().getAddress();
                }
                if (slr.getLcsClientID().getLCSClientInternalID() != null
                    && (slr.getLcsClientID().getLCSClientInternalID().getId() > Integer.MIN_VALUE
                    && slr.getLcsClientID().getLCSClientInternalID().getId() < Integer.MAX_VALUE)) {
                    lcsClientIDInternalID = slr.getLcsClientID().getLCSClientInternalID().getId();
                }
                if (slr.getLcsClientID().getLCSClientName() != null) {
                    if (slr.getLcsClientID().getLCSClientName().getNameString() != null) {
                        lcsClientName = new String(slr.getLcsClientID().getLCSClientName().getNameString().getEncodedString());
                    }
                    if (slr.getLcsClientID().getLCSClientName().getDataCodingScheme() != null) {
                        lcsClientDataCodingScheme = slr.getLcsClientID().getLCSClientName().getDataCodingScheme().getCode();
                    }
                    if (slr.getLcsClientID().getLCSClientName().getLCSFormatIndicator() != null) {
                        lcsClientFormatIndicator = slr.getLcsClientID().getLCSClientName().getLCSFormatIndicator().getIndicator();
                    }
                }
                if (slr.getLcsClientID().getLCSAPN() != null) {
                    lcsClientIDAPN = new String(slr.getLcsClientID().getLCSAPN().getApn().getBytes());
                }
                if (slr.getLcsClientID().getLCSRequestorID() != null) {
                    if (slr.getLcsClientID().getLCSRequestorID().getRequestorIDString() != null) {
                        requestorIDEncodedString = new String(slr.getLcsClientID().getLCSRequestorID().getRequestorIDString().getEncodedString());
                    }
                    if (slr.getLcsClientID().getLCSRequestorID().getDataCodingScheme() != null) {
                        requestorIDDataCodingScheme = slr.getLcsClientID().getLCSRequestorID().getDataCodingScheme().getCode();
                    }
                    if (slr.getLcsClientID().getLCSRequestorID().getLCSFormatIndicator() != null) {
                        requestorIDFormatIndicator = slr.getLcsClientID().getLCSRequestorID().getLCSFormatIndicator().getIndicator();
                    }
                }
                if (slr.getLcsClientID().getLCSClientDialedByMS() != null) {
                    lcsClientDialedByMS = slr.getLcsClientID().getLCSClientDialedByMS().getAddress();
                }
            }*/

            if (slr.getGeranPositioningDataInformation() != null || slr.getGeranGANSSpositioningData() != null) {
                try {
                    if (slr.getGeranPositioningDataInformation() != null) {
                        ArrayList<String> methods = slr.getGeranPositioningDataInformation().getLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                            this.positioningMethod = method;
                            if (logger.isDebugEnabled())
                                logger.debug(this.positioningMethod);
                        }
                    } else if (slr.getGeranGANSSpositioningData() != null) {
                        Multimap<String, String> methodsAndGanssIds = slr.getGeranGANSSpositioningData().getGeranGANSSPositioningMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                            this.positioningMethod = entry.getValue();
                            if (logger.isDebugEnabled())
                                logger.debug(this.positioningMethod);
                        }
                    }
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            } else if (slr.getUtranPositioningDataInfo() != null || slr.getUtranGANSSpositioningData() != null || slr.getUtranAdditionalPositioningData() != null) {
                try {
                    // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                    if (slr.getUtranPositioningDataInfo() != null) {
                        ArrayList<String> methods = slr.getUtranPositioningDataInfo().getUtranLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            this.positioningMethod = method;
                            if (logger.isDebugEnabled())
                                logger.debug(method);
                        }
                    } else if (slr.getUtranGANSSpositioningData() != null) {
                        Multimap<String, String> methodsAndGanssIds = slr.getUtranGANSSpositioningData().getLocationGeneratedMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                            if (logger.isDebugEnabled())
                                logger.debug(this.positioningMethod);
                        }
                    } else if (slr.getUtranAdditionalPositioningData() != null) {
                        Multimap<String, String> methodsAndPosId = slr.getUtranAdditionalPositioningData().getLocationGeneratedMethodsAndAddPosIds();
                        for (Map.Entry<String, String> entry : methodsAndPosId.entries()) {
                            this.positioningMethod = entry.getValue();
                            if (logger.isDebugEnabled())
                                logger.debug(this.positioningMethod);
                        }
                    }
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            }

            if (slr.getVelocityEstimate() != null) {
                this.horizontalSpeed = slr.getVelocityEstimate().getHorizontalSpeed();
                this.bearing = slr.getVelocityEstimate().getBearing();
                this.verticalSpeed = slr.getVelocityEstimate().getVerticalSpeed();
                this.uncertaintyHorizontalSpeed = slr.getVelocityEstimate().getUncertaintyHorizontalSpeed();
                this.uncertaintyVerticalSpeed = slr.getVelocityEstimate().getUncertaintyVerticalSpeed();
                if (slr.getVelocityEstimate().getVelocityType() != null)
                    this.velocityType = slr.getVelocityEstimate().getVelocityType().name();
            }

            if (slr.getPseudonymIndicator() != null) {
                this.pseudonymIndicator = slr.getPseudonymIndicator();
            }

            if (slr.getLcsEvent() != null) {
                this.lcsEvent = slr.getLcsEvent().getEvent();
            }

          this.moLrShortCircuitIndicator = slr.isMoLrShortCircuitIndicator();

            if (slr.getPeriodicLDRInfo() != null) {
                this.reportingAmount = slr.getPeriodicLDRInfo().getReportingAmount();
                this.reportingInterval = slr.getPeriodicLDRInfo().getReportingInterval();
            }

            if (slr.getSequenceNumber() != null) {
                this.sequenceNumber = slr.getSequenceNumber();
            }

            /*** Deferred MT LR Data ***/
            if (slr.getDeferredmtlrData() != null) {
                // Deferred Location Event Type
                if (slr.getDeferredmtlrData().getDeferredLocationEventType() != null) {
                    this.deferredEventLocationType = true;
                    this.enteringArea = slr.getDeferredmtlrData().getDeferredLocationEventType().getEnteringIntoArea();
                    this.insideArea = slr.getDeferredmtlrData().getDeferredLocationEventType().getBeingInsideArea();
                    this.leavingArea = slr.getDeferredmtlrData().getDeferredLocationEventType().getLeavingFromArea();
                    this.msAvailable = slr.getDeferredmtlrData().getDeferredLocationEventType().getMsAvailable();
                    this.periodicLDR = slr.getDeferredmtlrData().getDeferredLocationEventType().getPeriodicLDR();
                }
                // Termination Cause
                if (slr.getDeferredmtlrData().getTerminationCause() != null) {
                    this.terminationCause = slr.getDeferredmtlrData().getTerminationCause().getCause();
                }

                // LCS Location Info
                if (slr.getDeferredmtlrData().getLCSLocationInfo() != null) {

                    // GPRS Node Indicator
                    this.gprsNodeIndicator = slr.isGprsNodeIndicator();

                    // Network Node Number
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getNetworkNodeNumber() != null) {
                        this.nnn = slr.getDeferredmtlrData().getLCSLocationInfo().getNetworkNodeNumber().getAddress();
                    }

                    // LMSI
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getLMSI() != null) {
                        this.lmsi = bytesToHex(slr.getDeferredmtlrData().getLCSLocationInfo().getLMSI().getData());
                    }

                    // MME Name
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getMmeName() != null) {
                        this.mmeName = new String(slr.getDeferredmtlrData().getLCSLocationInfo().getMmeName().getData());
                    }

                    // AAA Server Name
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getAaaServerName() != null) {
                        this.tgppAAAServerName = new String(slr.getDeferredmtlrData().getLCSLocationInfo().getAaaServerName().getData());
                    }

                    // Additional Number
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber() != null) {
                        if (slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getMSCNumber() != null) {
                            this.mscNumber = slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getMSCNumber().getAddress();
                        }
                        if (slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getSGSNNumber() != null) {
                            this.sgsnNumber = slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress();
                        }
                    }

                    // Supported LCS Capability Sets
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets() != null) {
                        this.supportedLCSCapabilitySets = true;
                        this.supportedLCSCapabilitySetRelease98_99 = slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99();
                        this.supportedLCSCapabilitySetRelease4 = slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease4();
                        this.supportedLCSCapabilitySetRelease5 = slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease5();
                        this.supportedLCSCapabilitySetRelease6 = slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease6();
                        this.supportedLCSCapabilitySetRelease7 = slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease7();
                    }
                    // Additional Supported LCS Capability Sets
                    if (slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets() != null) {
                        this.addSupportedLCSCapabilitySets = true;
                        this.addSupportedLCSCapabilitySetRelease98_99 = slr.getDeferredmtlrData().getLCSLocationInfo().getSupportedLCSCapabilitySets().getCapabilitySetRelease98_99();
                        this.addSupportedLCSCapabilitySetRelease4 = slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease4();
                        this.addSupportedLCSCapabilitySetRelease5 = slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease5();
                        this.addSupportedLCSCapabilitySetRelease6 = slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease6();
                        this.addSupportedLCSCapabilitySetRelease7 = slr.getDeferredmtlrData().getLCSLocationInfo().getAdditionalLCSCapabilitySets().getCapabilitySetRelease7();
                    }
                }
            }

            /*** UTRAN Civic Address ***/
            if (slr.getUtranCivicAddress() != null) {
                this.civicAddress = new String(slr.getUtranCivicAddress().getData(), StandardCharsets.UTF_8);
            }
        }
    }

    //////////////////////////
    // Getters and Setters //
    ////////////////////////


    public Integer getMcc() {
        return mcc;
    }

    public void setMcc(Integer mcc) {
        this.mcc = mcc;
    }

    public Integer getMnc() {
        return mnc;
    }

    public void setMnc(Integer mnc) {
        this.mnc = mnc;
    }

    public Integer getLac() {
        return lac;
    }

    public void setLac(Integer lac) {
        this.lac = lac;
    }

    public Integer getCi() {
        return ci;
    }

    public void setCi(Integer ci) {
        this.ci = ci;
    }

    public Integer getSac() {
        return sac;
    }

    public void setSac(Integer sac) {
        this.sac = sac;
    }

    public String getVlrNumber() {
        return vlrNumber;
    }

    public void setVlrNumber(String vlrNumber) {
        this.vlrNumber = vlrNumber;
    }

    public String getMscNumber() {
        return mscNumber;
    }

    public void setMscNumber(String mscNumber) {
        this.mscNumber = mscNumber;
    }

    public String getSgsnNumber() {
        return sgsnNumber;
    }

    public void setSgsnNumber(String sgsnNumber) {
        this.sgsnNumber = sgsnNumber;
    }

    public boolean getGprsNodeIndicator() {
        return gprsNodeIndicator;
    }

    public void setGprsNodeIndicator(boolean gprsNodeIndicator) {
        this.gprsNodeIndicator = gprsNodeIndicator;
    }

    public String getNnn() {
        return nnn;
    }

    public void setNnn(String nnn) {
        this.nnn = nnn;
    }

    public String getMmeName() {
        return mmeName;
    }

    public void setMmeName(String mmeName) {
        this.mmeName = mmeName;
    }

    public String getSgsnName() {
        return sgsnName;
    }

    public void setSgsnName(String sgsnName) {
        this.sgsnName = sgsnName;
    }

    public String getTgppAAAServerName() {
        return tgppAAAServerName;
    }

    public void setTgppAAAServerName(String tgppAAAServerName) {
        this.tgppAAAServerName = tgppAAAServerName;
    }

    public String getHGmlcAddress() {
        return hGmlcAddress;
    }

    public void setHGmlcAddress(String hGmlcAddress) {
        this.hGmlcAddress = hGmlcAddress;
    }

    public String getVGmlcAddress() {
        return vGmlcAddress;
    }

    public void setVGmlcAddress(String vGmlcAddress) {
        this.vGmlcAddress = vGmlcAddress;
    }

    public String getPprAddress() {
        return pprAddress;
    }

    public void setPprAddress(String pprAddress) {
        this.pprAddress = pprAddress;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getLmsi() {
        return lmsi;
    }

    public void setLmsi(String lmsi) {
        this.lmsi = lmsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTypeOfShape() {
        return typeOfShape;
    }

    public void setTypeOfShape(String typeOfShape) {
        this.typeOfShape = typeOfShape;
    }

    public Double getLatitude() {
        if (this.latitude != null) {
            String formattedLatitude = coordinatesFormat.format(this.latitude);
            return Double.valueOf(formattedLatitude);
        }
        return null;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        if (this.longitude != null) {
            String formattedLongitude = coordinatesFormat.format(this.longitude);
            return Double.valueOf(formattedLongitude);
        }
        return null;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(Double uncertainty) {
        this.uncertainty = uncertainty;
    }

    public Double getRadius() {
        if (this.typeOfShape != null) {
            if (this.typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle") ||
                this.typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse") ||
                this.typeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
                if (this.uncertainty != null) {
                    this.radius = this.uncertainty;
                    String formattedRadius = radiusFormat.format(this.radius);
                    return Double.valueOf(formattedRadius);
                }
            } else if (this.typeOfShape.equalsIgnoreCase("EllipsoidArc")) {
                if (this.innerRadius != null) {
                    this.radius = Double.valueOf(this.innerRadius);
                    String formattedRadius = radiusFormat.format(this.radius);
                    return Double.valueOf(formattedRadius);
                }
            }
        }
        return null;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Double getUncertaintySemiMajorAxis() {
        if (this.uncertaintySemiMajorAxis != null) {
            String formattedMajorAxis = axisFormat.format(this.uncertaintySemiMajorAxis);
            return Double.valueOf(formattedMajorAxis);
        } else {
            return null;
        }
    }

    public void setUncertaintySemiMajorAxis(Double uncertaintySemiMajorAxis) {
        this.uncertaintySemiMajorAxis = uncertaintySemiMajorAxis;
    }

    public Double getUncertaintySemiMinorAxis() {
        if (this.uncertaintySemiMinorAxis != null) {
            String formattedMinorAxis = axisFormat.format(this.uncertaintySemiMinorAxis);
            return Double.valueOf(formattedMinorAxis);
        } else {
            return null;
        }
    }

    public void setUncertaintySemiMinorAxis(Double uncertaintySemiMinorAxis) {
        this.uncertaintySemiMinorAxis = uncertaintySemiMinorAxis;
    }

    public Double getAngleOfMajorAxis() {
        if (this.angleOfMajorAxis != null) {
            String formattedAngle = angleFormat.format(this.angleOfMajorAxis);
            return Double.valueOf(formattedAngle);
        } else {
            return null;
        }
    }

    public void setAngleOfMajorAxis(Double angleOfMajorAxis) {
        this.angleOfMajorAxis = angleOfMajorAxis;
    }

    public Double getOffsetAngle() {
        if (this.offsetAngle != null) {
            String formattedAngle = angleFormat.format(this.offsetAngle);
            return Double.valueOf(formattedAngle);
        } else {
            return null;
        }
    }

    public void setOffsetAngle(Double offsetAngle) {
        this.offsetAngle = offsetAngle;
    }

    public Double getIncludedAngle() {
        if (this.includedAngle != null) {
            String formattedAngle = angleFormat.format(this.includedAngle);
            return Double.valueOf(formattedAngle);
        } else {
            return null;
        }
    }

    public void setIncludedAngle(Double includedAngle) {
        this.includedAngle = includedAngle;
    }

    public Double getUncertaintyAltitude() {
        return uncertaintyAltitude;
    }

    public void setUncertaintyAltitude(Double uncertaintyAltitude) {
        this.uncertaintyAltitude = uncertaintyAltitude;
    }

    public Double getUncertaintyInnerRadius() {
        return uncertaintyInnerRadius;
    }

    public void setUncertaintyInnerRadius(Double uncertaintyInnerRadius) {
        this.uncertaintyInnerRadius = uncertaintyInnerRadius;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Integer getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(Integer innerRadius) {
        this.innerRadius = innerRadius;
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(Integer numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public EllipsoidPoint[] getPolygonEllipsoidPoints() {
        return polygonEllipsoidPoints;
    }

    public void setPolygonEllipsoidPoints(EllipsoidPoint[] polygonEllipsoidPoints) {
        this.polygonEllipsoidPoints = polygonEllipsoidPoints;
    }

    public Double[][] getPolygonArray() {
        return this.polygonArray;
    }

    public void setPolygonArray(Double[][] polygonArray) {
        this.polygonArray = polygonArray;
    }

    public String getPositioningMethod() {
        return this.positioningMethod;
    }

    public void setPositioningMethod(String positioningMethod) {
        this.positioningMethod = positioningMethod;
    }

    public Integer getAgeOfLocationEstimate() {
        return ageOfLocationEstimate;
    }

    public void setAgeOfLocationEstimate(Integer ageOfLocationEstimate) {
        this.ageOfLocationEstimate = ageOfLocationEstimate;
    }

    public Integer getAccuracyFulfilmentIndicator() {
        return accuracyFulfilmentIndicator;
    }

    public void setAccuracyFulfilmentIndicator(Integer accuracyFulfilmentIndicator) {
        this.accuracyFulfilmentIndicator = accuracyFulfilmentIndicator;
    }

    public Boolean getDeferredMTLRresponseIndicator() {
        return deferredMTLRresponseIndicator;
    }

    public void setDeferredMTLRresponseIndicator(Boolean deferredMTLRresponseIndicator) {
        this.deferredMTLRresponseIndicator = deferredMTLRresponseIndicator;
    }

    public Boolean getMolrShortCircuitIndicator() {
        return molrShortCircuitIndicator;
    }

    public void setMolrShortCircuitIndicator(Boolean molrShortCircuitIndicator) {
        this.molrShortCircuitIndicator = molrShortCircuitIndicator;
    }

    public Boolean getSaiPresent() {
        return saiPresent;
    }

    public void setSaiPresent(Boolean saiPresent) {
        this.saiPresent = saiPresent;
    }

    public Integer getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public void setHorizontalSpeed(Integer horizontalSpeed) {
        this.horizontalSpeed = horizontalSpeed;
    }

    public Integer getBearing() {
        return bearing;
    }

    public void setBearing(Integer bearing) {
        this.bearing = bearing;
    }

    public Integer getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(Integer verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }

    public Integer getUncertaintyHorizontalSpeed() {
        return uncertaintyHorizontalSpeed;
    }

    public void setUncertaintyHorizontalSpeed(Integer uncertaintyHorizontalSpeed) {
        this.uncertaintyHorizontalSpeed = uncertaintyHorizontalSpeed;
    }

    public Integer getUncertaintyVerticalSpeed() {
        return uncertaintyVerticalSpeed;
    }

    public void setUncertaintyVerticalSpeed(Integer uncertaintyVerticalSpeed) {
        this.uncertaintyVerticalSpeed = uncertaintyVerticalSpeed;
    }

    public String getVelocityType() {
        return velocityType;
    }

    public void setVelocityType(String velocityType) {
        this.velocityType = velocityType;
    }

    public Integer getLcsReferenceNumber() {
        return lcsReferenceNumber;
    }

    public void setLcsReferenceNumber(Integer lcsReferenceNumber) {
        this.lcsReferenceNumber = lcsReferenceNumber;
    }

    public Integer getTerminationCause() {
        return terminationCause;
    }

    public void setTerminationCause(Integer terminationCause) {
        this.terminationCause = terminationCause;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Integer getReportingAmount() {
        return reportingAmount;
    }

    public void setReportingAmount(Integer reportingAmount) {
        this.reportingAmount = reportingAmount;
    }

    public Integer getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(Integer reportingInterval) {
        this.reportingInterval = reportingInterval;
    }

    public Integer getLcsEvent() {
        return lcsEvent;
    }

    public void setLcsEvent(Integer lcsEvent) {
        this.lcsEvent = lcsEvent;
    }

    public Integer getLcsServiceTypeID() {
        return lcsServiceTypeID;
    }

    public void setLcsServiceTypeID(Integer lcsServiceTypeID) {
        this.lcsServiceTypeID = lcsServiceTypeID;
    }

    public Integer getClientReferenceNumber() {
        return clientReferenceNumber;
    }

    public void setClientReferenceNumber(Integer clientReferenceNumber) {
        this.clientReferenceNumber = clientReferenceNumber;
    }

    public Boolean getPseudonymIndicator() {
        return pseudonymIndicator;
    }

    public void setPseudonymIndicator(Boolean pseudonymIndicator) {
        this.pseudonymIndicator = pseudonymIndicator;
    }

    public Boolean getMoLrShortCircuitIndicator() {
        return moLrShortCircuitIndicator;
    }

    public void setMoLrShortCircuitIndicator(Boolean moLrShortCircuitIndicator) {
        this.moLrShortCircuitIndicator = moLrShortCircuitIndicator;
    }

    public Boolean getDeferredEventLocationType() {
        return deferredEventLocationType;
    }

    public void setDeferredEventLocationType(Boolean deferredEventLocationType) {
        this.deferredEventLocationType = deferredEventLocationType;
    }

    public Boolean getEnteringArea() {
        return enteringArea;
    }

    public void setEnteringArea(Boolean enteringArea) {
        this.enteringArea = enteringArea;
    }

    public Boolean getInsideArea() {
        return insideArea;
    }

    public void setInsideArea(Boolean insideArea) {
        this.insideArea = insideArea;
    }

    public Boolean getLeavingArea() {
        return leavingArea;
    }

    public void setLeavingArea(Boolean leavingArea) {
        this.leavingArea = leavingArea;
    }

    public Boolean getMsAvailable() {
        return msAvailable;
    }

    public void setMsAvailable(Boolean msAvailable) {
        this.msAvailable = msAvailable;
    }

    public Boolean getPeriodicLDR() {
        return periodicLDR;
    }

    public void setPeriodicLDR(Boolean periodicLDR) {
        this.periodicLDR = periodicLDR;
    }

    public Boolean getSupportedLCSCapabilitySets() {
        return supportedLCSCapabilitySets;
    }

    public void setSupportedLCSCapabilitySets(Boolean supportedLCSCapabilitySets) {
        this.supportedLCSCapabilitySets = supportedLCSCapabilitySets;
    }

    public Boolean getAddSupportedLCSCapabilitySets() {
        return addSupportedLCSCapabilitySets;
    }

    public void setAddSupportedLCSCapabilitySets(Boolean addSupportedLCSCapabilitySets) {
        this.addSupportedLCSCapabilitySets = addSupportedLCSCapabilitySets;
    }

    public Boolean getSupportedLCSCapabilitySetRelease98_99() {
        return supportedLCSCapabilitySetRelease98_99;
    }

    public void setSupportedLCSCapabilitySetRelease98_99(Boolean supportedLCSCapabilitySetRelease98_99) {
        this.supportedLCSCapabilitySetRelease98_99 = supportedLCSCapabilitySetRelease98_99;
    }

    public Boolean getSupportedLCSCapabilitySetRelease4() {
        return supportedLCSCapabilitySetRelease4;
    }

    public void setSupportedLCSCapabilitySetRelease4(Boolean supportedLCSCapabilitySetRelease4) {
        this.supportedLCSCapabilitySetRelease4 = supportedLCSCapabilitySetRelease4;
    }

    public Boolean getSupportedLCSCapabilitySetRelease5() {
        return supportedLCSCapabilitySetRelease5;
    }

    public void setSupportedLCSCapabilitySetRelease5(Boolean supportedLCSCapabilitySetRelease5) {
        this.supportedLCSCapabilitySetRelease5 = supportedLCSCapabilitySetRelease5;
    }

    public Boolean getSupportedLCSCapabilitySetRelease6() {
        return supportedLCSCapabilitySetRelease6;
    }

    public void setSupportedLCSCapabilitySetRelease6(Boolean supportedLCSCapabilitySetRelease6) {
        this.supportedLCSCapabilitySetRelease6 = supportedLCSCapabilitySetRelease6;
    }

    public Boolean getSupportedLCSCapabilitySetRelease7() {
        return supportedLCSCapabilitySetRelease7;
    }

    public void setSupportedLCSCapabilitySetRelease7(Boolean supportedLCSCapabilitySetRelease7) {
        this.supportedLCSCapabilitySetRelease7 = supportedLCSCapabilitySetRelease7;
    }

    public Boolean getAddSupportedLCSCapabilitySetRelease98_99() {
        return addSupportedLCSCapabilitySetRelease98_99;
    }

    public void setAddSupportedLCSCapabilitySetRelease98_99(Boolean addSupportedLCSCapabilitySetRelease98_99) {
        this.addSupportedLCSCapabilitySetRelease98_99 = addSupportedLCSCapabilitySetRelease98_99;
    }

    public Boolean getAddSupportedLCSCapabilitySetRelease4() {
        return addSupportedLCSCapabilitySetRelease4;
    }

    public void setAddSupportedLCSCapabilitySetRelease4(Boolean addSupportedLCSCapabilitySetRelease4) {
        this.addSupportedLCSCapabilitySetRelease4 = addSupportedLCSCapabilitySetRelease4;
    }

    public Boolean getAddSupportedLCSCapabilitySetRelease5() {
        return addSupportedLCSCapabilitySetRelease5;
    }

    public void setAddSupportedLCSCapabilitySetRelease5(Boolean addSupportedLCSCapabilitySetRelease5) {
        this.addSupportedLCSCapabilitySetRelease5 = addSupportedLCSCapabilitySetRelease5;
    }

    public Boolean getAddSupportedLCSCapabilitySetRelease6() {
        return addSupportedLCSCapabilitySetRelease6;
    }

    public void setAddSupportedLCSCapabilitySetRelease6(Boolean addSupportedLCSCapabilitySetRelease6) {
        this.addSupportedLCSCapabilitySetRelease6 = addSupportedLCSCapabilitySetRelease6;
    }

    public Boolean getAddSupportedLCSCapabilitySetRelease7() {
        return addSupportedLCSCapabilitySetRelease7;
    }

    public void setAddSupportedLCSCapabilitySetRelease7(Boolean addSupportedLCSCapabilitySetRelease7) {
        this.addSupportedLCSCapabilitySetRelease7 = addSupportedLCSCapabilitySetRelease7;
    }

    public String getCivicAddress() {
        return civicAddress;
    }

    public void setCivicAddress(String civicAddress) {
        this.civicAddress = civicAddress;
    }
}
