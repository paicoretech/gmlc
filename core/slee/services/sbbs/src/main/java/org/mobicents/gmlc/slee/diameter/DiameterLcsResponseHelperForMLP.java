package org.mobicents.gmlc.slee.diameter;

import com.google.common.collect.Multimap;
import org.mobicents.gmlc.slee.diameter.slg.SLgLrrAvpValues;
import org.mobicents.gmlc.slee.diameter.slg.SLgPlaAvpValues;
import org.mobicents.gmlc.slee.diameter.slh.SLhRiaAvpValues;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningData;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningDataImpl;
import org.mobicents.gmlc.slee.primitives.EllipsoidPoint;
import org.mobicents.gmlc.slee.primitives.Polygon;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mobicents.gmlc.slee.http.JsonWriter.bytesToHexString;
import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;
import static org.mobicents.gmlc.slee.utils.TBCDUtil.toTBCDString;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class DiameterLcsResponseHelperForMLP {

    protected final Logger logger = LoggerFactory.getLogger(DiameterLcsResponseHelperForMLP.class.getName());
    protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
    protected static final DecimalFormat radiusFormat = new DecimalFormat("#0.00");
    protected static final DecimalFormat angleFormat = new DecimalFormat("#0.00");
    protected static final DecimalFormat axisFormat = new DecimalFormat("#0.00");

    String msisdn, imsi, lmsi, imei;
    private Integer mcc, mnc, lac, ci, sac;
    private Long eci, eNBId, cellPortionId;
    private String vlrNumber, mscNumber, sgsnNumber;
    private Boolean gprsNodeIndicator;
    private String nnn, mmeName, mmeRealm, sgsnName, sgsnRealm, tgppAAAServerName, gmlcAddress;
    private String typeOfShape;
    private Double latitude;
    private Double longitude;
    private Double uncertainty, uncertaintySemiMajorAxis, uncertaintySemiMinorAxis, angleOfMajorAxis,
        uncertaintyAltitude, uncertaintyInnerRadius, offsetAngle, includedAngle;
    private Integer confidence, altitude, innerRadius, numberOfPoints;
    private Double radius = null;
    Polygon polygon = null;
    EllipsoidPoint[] polygonEllipsoidPoints;
    Double[][] polygonArray;
    private Long ageOfLocationEstimate;
    Integer accuracyFulfilmentIndicator;
    private Boolean deferredMTLRresponseIndicator, molrShortCircuitIndicator, saiPresent = false;
    private Integer horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed;
    private Integer lcsQoSClassValue;
    private String positioningMethod;
    private String lcsEPSClientName, velocityType, civicAddress,
        mtlrMmeName, mtlrMmeRealm, mtlrSgsnName, mtlrSgsnRealm, mtlrSgsnNumber, mtlr3gppAAAServerName, mtlrMscNumber, mtlrGmlcAddress,
        dlrMmeName, dlrMmeRealm, dlrSgsnName, dlrSgsnRealm, dlrSgsnNumber, dlr3gppAAAServerName, dlrMscNumber, dlrGmlcAddress;
    Long lcsServiceTypeId, reportingAmount, reportingInterval, deferredLocationType, dMtLrterminationCause, barometricPressure,
        lcsCapabilitySets, mtlrLcsCapabilitySets, dlrTerminationCause, dlrLcsCapabilitySets, sequenceNumber;
    Integer lcsReferenceNumber, clientReferenceNumber, lcsFormatIndicator, locationEvent, lcsPseudonymIndicator;

    public DiameterLcsResponseHelperForMLP() {
    }

    public void handleRirAnswerValues(SLhRiaAvpValues ria, String txMsisdn, String txIMSI) {

        if (ria != null) {
            // Get SLh Routing-Information-Answer values
            if (ria.getMsisdn() != null) {
                this.msisdn = AVPHandler.tbcd2IsdnAddressString(ria.getMsisdn()).getAddress();
            } else {
                this.msisdn = txMsisdn;
            }

            if (ria.getUserName() != null) {
                this.imsi = AVPHandler.userName2Imsi(ria.getUserName()).getData();
            } else {
                this.imsi = txIMSI;
            }

            if (ria.getLmsi() != null)
                this.lmsi = bytesToHex(AVPHandler.byte2Lmsi(ria.getLmsi()).getData());

            if (ria.getAdditionalServingNodeAvp() != null) {
                if (ria.getAdditionalServingNodeAvp().hasSGSNNumber())
                    this.sgsnNumber = toTBCDString(ria.getAdditionalServingNodeAvp().getSGSNNumber());
                if (ria.getAdditionalServingNodeAvp().hasSGSNName())
                    this.sgsnName = new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getSGSNName()).getData());
                if (ria.getAdditionalServingNodeAvp().hasSGSNRealm())
                    this.sgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getSGSNRealm()).getData());
                if (ria.getAdditionalServingNodeAvp().hasMMEName())
                    this.mmeName = new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getMMEName()).getData());
                if (ria.getAdditionalServingNodeAvp().hasMMERealm())
                    this.mmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().getMMERealm()).getData());
                if (ria.getAdditionalServingNodeAvp().hasMSCNumber())
                    this.mscNumber = toTBCDString(ria.getAdditionalServingNodeAvp().getMSCNumber());
                if (ria.getAdditionalServingNodeAvp().has3GPPAAAServerName())
                    this.tgppAAAServerName = new String(AVPHandler.diameterIdToMapDiameterId(ria.getAdditionalServingNodeAvp().get3GPPAAAServerName()).getData());
                if (ria.getAdditionalServingNodeAvp().hasLcsCapabilitiesSets())
                    this.lcsCapabilitySets = ria.getAdditionalServingNodeAvp().getLcsCapabilitiesSets();
                if (ria.getAdditionalServingNodeAvp().hasGMLCAddress()) {
                    this.gmlcAddress = bytesToHexString(ria.getAdditionalServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.gmlcAddress));
                        this.gmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (ria.getServingNodeAvp() != null) {
                if (ria.getServingNodeAvp().hasSGSNNumber())
                    this.sgsnNumber = toTBCDString(ria.getServingNodeAvp().getSGSNNumber());
                if (ria.getServingNodeAvp().hasSGSNName())
                    this.sgsnName = new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getSGSNName()).getData());
                if (ria.getServingNodeAvp().hasSGSNRealm())
                    this.sgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getSGSNRealm()).getData());
                if (ria.getServingNodeAvp().hasMMEName())
                    this.mmeName = new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getMMEName()).getData());
                if (ria.getServingNodeAvp().hasMMERealm())
                    this.mmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().getMMERealm()).getData());
                if (ria.getServingNodeAvp().hasMSCNumber())
                    this.mscNumber = toTBCDString(ria.getServingNodeAvp().getMSCNumber());
                if (ria.getServingNodeAvp().has3GPPAAAServerName())
                    this.tgppAAAServerName = new String(AVPHandler.diameterIdToMapDiameterId(ria.getServingNodeAvp().get3GPPAAAServerName()).getData());
                if (ria.getServingNodeAvp().hasLcsCapabilitiesSets())
                    this.lcsCapabilitySets = ria.getServingNodeAvp().getLcsCapabilitiesSets();
                if (ria.getServingNodeAvp().hasGMLCAddress()) {
                    this.gmlcAddress = bytesToHexString(ria.getServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.gmlcAddress));
                        this.gmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (ria.getGmlcAddress() != null) {
                this.gmlcAddress = bytesToHexString(ria.getGmlcAddress().getAddress());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.gmlcAddress));
                    this.gmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }


    public void handlePlrAnswerValues(SLgPlaAvpValues pla) {

        if (pla != null) {
            // Get SLg Provide-Location-Answer values
            if (pla.getLocationEstimate() != null) {
                ExtGeographicalInformation locationEstimate = AVPHandler.lteLocationEstimate2ExtGeographicalInformation(pla.getLocationEstimate());
                this.typeOfShape = locationEstimate.getTypeOfShape().name();
                if (locationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
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
                } else {
                    this.polygon = new PolygonImpl(pla.getLocationEstimate());
                    this.numberOfPoints = polygon.getNumberOfPoints();
                    this.polygonEllipsoidPoints = new EllipsoidPoint[numberOfPoints];
                    for (int point = 0; point < numberOfPoints; point++) {
                        polygonEllipsoidPoints[point] = polygon.getEllipsoidPoint(point);
                    }
                    try {
                        ((PolygonImpl) polygon).setData(polygonEllipsoidPoints);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (pla.getAgeOfLocationEstimate() != null)
                this.ageOfLocationEstimate = Long.valueOf(AVPHandler.long2Int(pla.getAgeOfLocationEstimate()));

            if (pla.getAccuracyFulfilmentIndicator() != null)
                this.accuracyFulfilmentIndicator = AVPHandler.diamAccFulInd2MapAccFulInd(pla.getAccuracyFulfilmentIndicator()).getIndicator();

            if (pla.getCellGlobalIdentity() != null) {
                CellGlobalIdOrServiceAreaIdFixedLength cellGlobalId = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(pla.getCellGlobalIdentity());
                try {
                    this.mcc = cellGlobalId.getMCC();
                    this.mnc = cellGlobalId.getMNC();
                    this.lac = cellGlobalId.getLac();
                    this.ci = cellGlobalId.getCellIdOrServiceAreaCode();
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            }

            if (pla.getServiceAreaIdentity() != null) {
                CellGlobalIdOrServiceAreaIdFixedLength serviceAreaId = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(pla.getServiceAreaIdentity());
                try {
                    this.mcc = serviceAreaId.getMCC();
                    this.mnc = serviceAreaId.getMNC();
                    this.lac = serviceAreaId.getLac();
                    this.sac = serviceAreaId.getCellIdOrServiceAreaCode();
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            }

            if (pla.getEcgi() != null) {
                EUtranCgi eUtranCgi = new EUtranCgiImpl(pla.getEcgi());
                try {
                    this.mcc = eUtranCgi.getMCC();
                    this.mnc = eUtranCgi.getMNC();
                    this.eci = eUtranCgi.getEci();
                    this.eNBId = eUtranCgi.getENodeBId();
                    this.ci = eUtranCgi.getCi();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (pla.getEsmlcCellInfoAvp() != null) {
                if (pla.getEsmlcCellInfoAvp().getECGI() != null) {
                    EUtranCgi eUtranCgi = new EUtranCgiImpl(pla.getEsmlcCellInfoAvp().getECGI());
                    try {
                        this.mcc = eUtranCgi.getMCC();
                        this.mnc = eUtranCgi.getMNC();
                        this.eci = eUtranCgi.getEci();
                        this.eNBId = eUtranCgi.getENodeBId();
                        this.ci = eUtranCgi.getCi();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
                if (pla.getEsmlcCellInfoAvp().getCellPortionID() > -1)
                    this.cellPortionId = pla.getEsmlcCellInfoAvp().getCellPortionID();
            }

            if (pla.getServingNodeAvp() != null) {
                if (pla.getServingNodeAvp().hasSGSNNumber())
                    this.sgsnNumber = toTBCDString(pla.getServingNodeAvp().getSGSNNumber());
                if (pla.getServingNodeAvp().hasSGSNName())
                    this.sgsnName = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getSGSNName()).getData());
                if (pla.getServingNodeAvp().hasSGSNRealm())
                    this.sgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getSGSNRealm()).getData());
                if (pla.getServingNodeAvp().hasMMEName())
                    this.mmeName = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getMMEName()).getData());
                if (pla.getServingNodeAvp().hasMMERealm())
                    this.mmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().getMMERealm()).getData());
                if (pla.getServingNodeAvp().hasMSCNumber())
                    this.mscNumber = toTBCDString(pla.getServingNodeAvp().getMSCNumber());
                if (pla.getServingNodeAvp().has3GPPAAAServerName())
                    this.tgppAAAServerName = new String(AVPHandler.diameterIdToMapDiameterId(pla.getServingNodeAvp().get3GPPAAAServerName()).getData());
                if (pla.getServingNodeAvp().hasLcsCapabilitiesSets())
                    this.lcsCapabilitySets = pla.getServingNodeAvp().getLcsCapabilitiesSets();
                if (pla.getServingNodeAvp().hasGMLCAddress()) {
                    this.gmlcAddress = bytesToHexString(pla.getServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.gmlcAddress));
                        this.gmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (pla.getGeranPositioningInfoAvp() != null) {
                // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                try {
                    if (pla.getGeranPositioningInfoAvp().getGERANPositioningData() != null) {
                        PositioningDataInformation geranPositioningDataInformation = AVPHandler.lteGeranPosDataInfo2MapGeranPosDataInfo(pla.getGeranPositioningInfoAvp().getGERANPositioningData());
                        ArrayList<String> methods = geranPositioningDataInformation.getLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            this.positioningMethod = method;
                            if (logger.isDebugEnabled()) {
                                // Needed to do this for previous this.positioningMethod not being grayed out ¿?
                                logger.debug(method);
                            }
                        }
                    } else if (pla.getGeranPositioningInfoAvp().getGERANGANSSPositioningData() != null) {
                        GeranGANSSpositioningData geranGANSSpositioningData = AVPHandler.lteGeranGanssPosDataInfo2MapGeranGanssPosDataInfo(pla.getGeranPositioningInfoAvp().getGERANGANSSPositioningData());
                        Multimap<String, String> methodsAndGanssIds = geranGANSSpositioningData.getGeranGANSSPositioningMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            } else if (pla.getUtranPositioningInfoAvp() != null) {
                // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                try {
                    if (pla.getUtranPositioningInfoAvp().getUTRANPositioningData() != null) {
                        UtranPositioningDataInfo utranPositioningDataInfo = AVPHandler.lteUtranPosData2MapUtranPosDataInfo(pla.getUtranPositioningInfoAvp().getUTRANPositioningData());
                        ArrayList<String> methods = utranPositioningDataInfo.getUtranLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            this.positioningMethod = method;
                        }
                    } else if (pla.getUtranPositioningInfoAvp().getUTRANGANSSPositioningData() != null) {
                        UtranGANSSpositioningData utranGANSSpositioningData = AVPHandler.lteUtranGanssPosData2MapUtranGanssPosDataInfo(pla.getUtranPositioningInfoAvp().getUTRANGANSSPositioningData());
                        Multimap<String, String> methodsAndGanssIds = utranGANSSpositioningData.getLocationGeneratedMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    } else if (pla.getUtranPositioningInfoAvp().getUTRANAdditionalPositioningData() != null) {
                        UtranAdditionalPositioningData utranAdditionalPositioningData = AVPHandler.lteUtranAddPosData2MapUtranAdditionalPositioningdata(pla.getUtranPositioningInfoAvp().getUTRANAdditionalPositioningData());
                        Multimap<String, String> methodsAndPosId = utranAdditionalPositioningData.getLocationGeneratedMethodsAndAddPosIds();
                        for (Map.Entry<String, String> entry : methodsAndPosId.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            } else if (pla.getEUtranPositioningData() != null) {
                // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                try {
                    EUTRANPositioningData eutranPositioningData = new EUTRANPositioningDataImpl(pla.getEUtranPositioningData());
                    if (eutranPositioningData.getPositioningDataSet() != null) {
                        HashMap<String, Integer> methodsAndUsage = eutranPositioningData.getPositioningDataMethodsAndUsage(eutranPositioningData.getPositioningDataSet());
                        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                            this.positioningMethod = item.getKey();
                        }
                    } else if (eutranPositioningData.getGNSSPositioningDataSet() != null) {
                        Multimap<String, String> methodsAndGanssIds = eutranPositioningData.getGNSSPositioningMethodsAndGNSSIds(eutranPositioningData.getGNSSPositioningDataSet());
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    } else if (eutranPositioningData.getAdditionalPositioningDataSet() != null) {
                        Multimap<String, String> methodsAndAddPosIds = eutranPositioningData.getEUtranAdditionalPositioningMethodsAndIds(eutranPositioningData.getAdditionalPositioningDataSet());
                        for (Map.Entry<String, String> entry : methodsAndAddPosIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (pla.getVelocityEstimate() != null) {
                VelocityEstimate lteVelocityEstimate = AVPHandler.lteVelocityEstimate2MapVelocityEstimate(pla.getVelocityEstimate());
                if (lteVelocityEstimate.getHorizontalSpeed() > -1)
                    this.horizontalSpeed = lteVelocityEstimate.getHorizontalSpeed();
                if (lteVelocityEstimate.getBearing() > -1)
                    this.bearing = lteVelocityEstimate.getBearing();
                if (lteVelocityEstimate.getVerticalSpeed() > -1)
                    this.verticalSpeed = lteVelocityEstimate.getVerticalSpeed();
                if (lteVelocityEstimate.getUncertaintyHorizontalSpeed() > -1)
                    this.uncertaintyHorizontalSpeed = lteVelocityEstimate.getUncertaintyHorizontalSpeed();
                if (lteVelocityEstimate.getUncertaintyVerticalSpeed() > -1)
                    this.uncertaintyVerticalSpeed = lteVelocityEstimate.getUncertaintyVerticalSpeed();
                if (lteVelocityEstimate.getVelocityType() != null)
                    this.velocityType = lteVelocityEstimate.getVelocityType().name();
            }

            if (pla.getCivicAddress() != null)
                this.civicAddress = new String(pla.getCivicAddress().getBytes(), StandardCharsets.UTF_8);

            if (pla.getBarometricPressure() != null)
                this.barometricPressure = pla.getBarometricPressure();
        }
    }


    public void handleLrrResponseValues(SLgLrrAvpValues lrr, Integer clientReferenceNumber) {

        if (lrr != null) {

            if (lrr.getMsisdn() != null)
                this.msisdn = AVPHandler.tbcd2IsdnAddressString(lrr.getMsisdn()).getAddress();

            if (lrr.getUserName() != null)
                this.imsi = new String(AVPHandler.userName2Imsi(lrr.getUserName()).getData().getBytes());

            if (lrr.getLcsReferenceNumber() != null)
                this.lcsReferenceNumber = AVPHandler.byte2Int(lrr.getLcsReferenceNumber());

            if (clientReferenceNumber != null)
                this.clientReferenceNumber = clientReferenceNumber;

            if (lrr.getImei() != null)
                this.imei = AVPHandler.string2MapImei(lrr.getImei()).getIMEI();

            if (lrr.getLcsServiceTypeId() != null)
                this.lcsServiceTypeId = lrr.getLcsServiceTypeId();

            if (lrr.getLocationEvent() != null)
                this.locationEvent = lrr.getLocationEvent().getValue();

            /*** LCS-EPS-Client-Name AVP ***/
            if (lrr.getLcsEPSClientName() != null) {
                if (lrr.getLcsEPSClientName().getLCSNameString() != null) {
                    this.lcsEPSClientName = lrr.getLcsEPSClientName().getLCSNameString();
                }
                if (lrr.getLcsEPSClientName().getLCSFormatIndicator() != null) {
                    this.lcsFormatIndicator = lrr.getLcsEPSClientName().getLCSFormatIndicator().getValue();
                }
            }

            /*** Location-Estimate AVP ***/
            if (lrr.getLocationEstimate() != null) {
                ExtGeographicalInformation lteLocationEstimate = AVPHandler.lteLocationEstimate2ExtGeographicalInformation(lrr.getLocationEstimate());
                this.typeOfShape = lteLocationEstimate.getTypeOfShape().name();
                if (lteLocationEstimate.getTypeOfShape() != TypeOfShape.Polygon) {
                    this.latitude = lteLocationEstimate.getLatitude();
                    this.longitude = lteLocationEstimate.getLongitude();
                    this.uncertainty = lteLocationEstimate.getUncertainty();
                    this.uncertaintySemiMajorAxis = lteLocationEstimate.getUncertaintySemiMajorAxis();
                    this.uncertaintySemiMinorAxis = lteLocationEstimate.getUncertaintySemiMinorAxis();
                    this.altitude = lteLocationEstimate.getAltitude();
                    this.uncertaintyAltitude = lteLocationEstimate.getUncertaintyAltitude();
                    this.innerRadius = lteLocationEstimate.getInnerRadius();
                    this.uncertaintyInnerRadius = lteLocationEstimate.getUncertaintyRadius();
                    this.offsetAngle = lteLocationEstimate.getOffsetAngle();
                    this.includedAngle = lteLocationEstimate.getIncludedAngle();
                    this.confidence = lteLocationEstimate.getConfidence();
                } else {
                    this.polygon = new PolygonImpl(lteLocationEstimate.getData());
                    this.numberOfPoints = polygon.getNumberOfPoints();
                    this.polygonEllipsoidPoints = new EllipsoidPoint[numberOfPoints];
                    for (int point = 0; point < numberOfPoints; point++) {
                        this.polygonEllipsoidPoints[point] = polygon.getEllipsoidPoint(point);
                    }
                    try {
                        ((PolygonImpl) polygon).setData(polygonEllipsoidPoints);
                    } catch (MAPException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            if (lrr.getAgeOfLocationEstimate() != null)
                this.ageOfLocationEstimate = lrr.getAgeOfLocationEstimate();

            if (lrr.getAccuracyFulfilmentIndicator() != null)
                this.accuracyFulfilmentIndicator = AVPHandler.diamAccFulInd2MapAccFulInd(lrr.getAccuracyFulfilmentIndicator()).getIndicator();

            if (lrr.getLcsQoSClass() != null)
                this.lcsQoSClassValue = lrr.getLcsQoSClass().getValue();

            /*** Serving Node AVP ***/
            if (lrr.getServingNodeAvp() != null) {
                if (lrr.getServingNodeAvp().hasSGSNNumber())
                    this.sgsnNumber = toTBCDString(lrr.getServingNodeAvp().getSGSNNumber());
                if (lrr.getServingNodeAvp().hasSGSNName())
                    this.sgsnName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getServingNodeAvp().getSGSNName()).getData());
                if (lrr.getServingNodeAvp().hasSGSNRealm())
                    this.sgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getServingNodeAvp().getSGSNRealm()).getData());
                if (lrr.getServingNodeAvp().hasMMEName())
                    this.mmeName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getServingNodeAvp().getMMEName()).getData());
                if (lrr.getServingNodeAvp().hasMMERealm())
                    this.mmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getServingNodeAvp().getMMERealm()).getData());
                if (lrr.getServingNodeAvp().hasMSCNumber())
                    mscNumber = toTBCDString(lrr.getServingNodeAvp().getMSCNumber());
                if (lrr.getServingNodeAvp().has3GPPAAAServerName())
                    this.tgppAAAServerName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getServingNodeAvp().get3GPPAAAServerName()).getData());
                if (lrr.getServingNodeAvp().hasLcsCapabilitiesSets())
                    this.lcsCapabilitySets = lrr.getServingNodeAvp().getLcsCapabilitiesSets();
                if (lrr.getServingNodeAvp().hasGMLCAddress()) {
                    this.gmlcAddress = bytesToHexString(lrr.getServingNodeAvp().getGMLCAddress().getAddress());
                    try {
                        InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.gmlcAddress));
                        this.gmlcAddress = address.getHostAddress();
                    } catch (UnknownHostException e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            /*** CGI or SAI or ESMLC-Cell-Info AVPs ***/
            if (lrr.getCellGlobalIdentity() != null) {
                CellGlobalIdOrServiceAreaIdFixedLength cellGlobalId = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(lrr.getCellGlobalIdentity());
                try {
                    this.mcc = cellGlobalId.getMCC();
                    this.mnc = cellGlobalId.getMNC();
                    this.lac = cellGlobalId.getLac();
                    this.ci = cellGlobalId.getCellIdOrServiceAreaCode();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (lrr.getServiceAreaIdentity() != null) {
                CellGlobalIdOrServiceAreaIdFixedLength serviceAreaId = new CellGlobalIdOrServiceAreaIdFixedLengthImpl(lrr.getServiceAreaIdentity());
                try {
                    this.mcc = serviceAreaId.getMCC();
                    this.mnc = serviceAreaId.getMNC();
                    this.lac = serviceAreaId.getLac();
                    sac = serviceAreaId.getCellIdOrServiceAreaCode();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (lrr.getEcgi() != null || lrr.getEsmlcCellInfoAvp() != null) {
                EUtranCgi eUtranCgi;
                if (lrr.getEcgi() != null)
                    eUtranCgi = new EUtranCgiImpl(lrr.getEcgi());
                else
                    eUtranCgi = new EUtranCgiImpl(lrr.getEsmlcCellInfoAvp().getECGI());
                try {
                    this.mcc = eUtranCgi.getMCC();
                    this.mnc = eUtranCgi.getMNC();
                    this.eci = eUtranCgi.getEci();
                    this.eNBId = eUtranCgi.getENodeBId();
                    this.ci = eUtranCgi.getCi();
                    if (lrr.getEsmlcCellInfoAvp() != null) {
                        if (lrr.getEsmlcCellInfoAvp().getCellPortionID() > -1) {
                            cellPortionId = lrr.getEsmlcCellInfoAvp().getCellPortionID();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            /*** EUTRAN-Positioning-Info AVP ***/
            if (lrr.getEUtranPositioningData() != null) {
                // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                try {
                    EUTRANPositioningData eutranPositioningData = new EUTRANPositioningDataImpl(lrr.getEUtranPositioningData());
                    if (eutranPositioningData.getPositioningDataSet() != null) {
                        HashMap<String, Integer> methodsAndUsage = eutranPositioningData.getPositioningDataMethodsAndUsage(eutranPositioningData.getPositioningDataSet());
                        for (HashMap.Entry<String, Integer> item : methodsAndUsage.entrySet()) {
                            this.positioningMethod = item.getKey();
                        }
                    } else if (eutranPositioningData.getGNSSPositioningDataSet() != null) {
                        Multimap<String, String> methodsAndGanssIds = eutranPositioningData.getGNSSPositioningMethodsAndGNSSIds(eutranPositioningData.getGNSSPositioningDataSet());
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    } else if (eutranPositioningData.getAdditionalPositioningDataSet() != null) {
                        Multimap<String, String> methodsAndAddPosIds = eutranPositioningData.getEUtranAdditionalPositioningMethodsAndIds(eutranPositioningData.getAdditionalPositioningDataSet());
                        for (Map.Entry<String, String> entry : methodsAndAddPosIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            } /*** UTRAN-Positioning-Info AVP ***/
            else if (lrr.getUtranPositioningInfoAvp() != null) {

                // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                try {
                    if (lrr.getUtranPositioningInfoAvp().getUTRANPositioningData() != null) {
                        UtranPositioningDataInfo utranPositioningDataInfo = AVPHandler.lteUtranPosData2MapUtranPosDataInfo(lrr.getUtranPositioningInfoAvp().getUTRANPositioningData());
                        ArrayList<String> methods = utranPositioningDataInfo.getUtranLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            this.positioningMethod = method;
                        }
                    } else if (lrr.getUtranPositioningInfoAvp().getUTRANGANSSPositioningData() != null) {
                        UtranGANSSpositioningData utranGANSSpositioningData = AVPHandler.lteUtranGanssPosData2MapUtranGanssPosDataInfo(lrr.getUtranPositioningInfoAvp().getUTRANGANSSPositioningData());
                        Multimap<String, String> methodsAndGanssIds = utranGANSSpositioningData.getLocationGeneratedMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    } else if (lrr.getUtranPositioningInfoAvp().getUTRANAdditionalPositioningData() != null) {
                        UtranAdditionalPositioningData utranAdditionalPositioningData = AVPHandler.lteUtranAddPosData2MapUtranAdditionalPositioningdata(lrr.getUtranPositioningInfoAvp().getUTRANAdditionalPositioningData());
                        Multimap<String, String> methodsAndPosId = utranAdditionalPositioningData.getLocationGeneratedMethodsAndAddPosIds();
                        for (Map.Entry<String, String> entry : methodsAndPosId.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            } /*** GERAN-Positioning-Info AVP ***/
            else if (lrr.getGeranPositioningInfoAvp() != null) {
                // only taking one value as MLP admits only one for pos_method + it's the almost certain scenario
                try {
                    if (lrr.getGeranPositioningInfoAvp().getGERANPositioningData() != null) {
                        PositioningDataInformation geranPositioningDataInformation = AVPHandler.lteGeranPosDataInfo2MapGeranPosDataInfo(lrr.getGeranPositioningInfoAvp().getGERANPositioningData());
                        ArrayList<String> methods = geranPositioningDataInformation.getLocationGeneratedPositioningMethods();
                        for (String method : methods) {
                            this.positioningMethod = method;
                            if (logger.isDebugEnabled()) {
                                // Needed to do this for previous this.positioningMethod not being grayed out ¿?
                                logger.debug(method);
                            }
                        }
                    } else if (lrr.getGeranPositioningInfoAvp().getGERANGANSSPositioningData() != null) {
                        GeranGANSSpositioningData geranGANSSpositioningData = AVPHandler.lteGeranGanssPosDataInfo2MapGeranGanssPosDataInfo(lrr.getGeranPositioningInfoAvp().getGERANGANSSPositioningData());
                        Multimap<String, String> methodsAndGanssIds = geranGANSSpositioningData.getGeranGANSSPositioningMethodsAndGANSSIds();
                        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
                            this.positioningMethod = entry.getValue();
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            /*** Velocity Estimate AVP ***/
            if (lrr.getVelocityEstimate() != null) {
                VelocityEstimate lteVelocityEstimate = AVPHandler.lteVelocityEstimate2MapVelocityEstimate(lrr.getVelocityEstimate());
                this.horizontalSpeed = lteVelocityEstimate.getHorizontalSpeed();
                this.bearing = lteVelocityEstimate.getBearing();
                this.verticalSpeed = lteVelocityEstimate.getVerticalSpeed();
                this.uncertaintyHorizontalSpeed = lteVelocityEstimate.getUncertaintyHorizontalSpeed();
                this.uncertaintyVerticalSpeed = lteVelocityEstimate.getUncertaintyVerticalSpeed();
                this.velocityType = lteVelocityEstimate.getVelocityType().name();
            }

            /*** Pseudonym-Indicator AVP ***/
            if (lrr.getPseudonymIndicator() != null) {
                this.lcsPseudonymIndicator = lrr.getPseudonymIndicator().getValue();
            }

            /*** Periodic-LDR-Info AVP ***/
            if (lrr.getPeriodicLDRInformation() != null) {
                this.reportingAmount = lrr.getPeriodicLDRInformation().getReportingAmount();
                this.reportingInterval = lrr.getPeriodicLDRInformation().getReportingInterval();
            }

            /*** Deferred-MT-LR-Data AVP ***/
            if (lrr.getDeferredMTLRDataAvp() != null) {

                if (lrr.getDeferredMTLRDataAvp().hasDeferredLocationType())
                    this.deferredLocationType = lrr.getDeferredMTLRDataAvp().getDeferredLocationType();

                if (lrr.getDeferredMTLRDataAvp().hasTerminationCause())
                    this.dMtLrterminationCause = lrr.getDeferredMTLRDataAvp().getTerminationCause();

                if (lrr.getDeferredMTLRDataAvp().hasServingNode()) {
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasSGSNNumber())
                        this.mtlrSgsnNumber = toTBCDString(lrr.getDeferredMTLRDataAvp().getServingNode().getSGSNNumber());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasSGSNName())
                        this.mtlrSgsnName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDeferredMTLRDataAvp().getServingNode().getSGSNName()).getData());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasSGSNRealm())
                        this.mtlrSgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDeferredMTLRDataAvp().getServingNode().getSGSNRealm()).getData());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasMMEName())
                        this.mtlrMmeName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDeferredMTLRDataAvp().getServingNode().getMMEName()).getData());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasMMERealm())
                        this.mtlrMmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDeferredMTLRDataAvp().getServingNode().getMMERealm()).getData());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasMSCNumber())
                        this.mtlrMscNumber = toTBCDString(lrr.getDeferredMTLRDataAvp().getServingNode().getMSCNumber());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().has3GPPAAAServerName())
                        this.mtlr3gppAAAServerName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDeferredMTLRDataAvp().getServingNode().get3GPPAAAServerName()).getData());
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasLcsCapabilitiesSets())
                        this.mtlrLcsCapabilitySets = lrr.getDeferredMTLRDataAvp().getServingNode().getLcsCapabilitiesSets();
                    if (lrr.getDeferredMTLRDataAvp().getServingNode().hasGMLCAddress()) {
                        this.mtlrGmlcAddress = bytesToHexString(lrr.getDeferredMTLRDataAvp().getServingNode().getGMLCAddress().getAddress());
                        try {
                            InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.mtlrGmlcAddress));
                            this.mtlrGmlcAddress = address.getHostAddress();
                        } catch (UnknownHostException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }

            /*** Delayed-Location-Reporting-Data AVP ***/
            if (lrr.getDelayedLocationReportingDataAvp() != null) {

                if (lrr.getDelayedLocationReportingDataAvp().hasTerminationCause())
                    this.dlrTerminationCause = lrr.getDelayedLocationReportingDataAvp().getTerminationCause();

                if (lrr.getDelayedLocationReportingDataAvp().hasServingNode()) {
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasSGSNNumber())
                        this.dlrSgsnNumber = toTBCDString(lrr.getDelayedLocationReportingDataAvp().getServingNode().getSGSNNumber());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasSGSNName())
                        this.dlrSgsnName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDelayedLocationReportingDataAvp().getServingNode().getSGSNName()).getData());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasSGSNRealm())
                        this.dlrSgsnRealm = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDelayedLocationReportingDataAvp().getServingNode().getSGSNRealm()).getData());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasMMEName())
                        this.dlrMmeName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDelayedLocationReportingDataAvp().getServingNode().getMMEName()).getData());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasMMERealm())
                        this.dlrMmeRealm = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDelayedLocationReportingDataAvp().getServingNode().getMMERealm()).getData());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasMSCNumber())
                        this.dlrMscNumber = toTBCDString(lrr.getDelayedLocationReportingDataAvp().getServingNode().getMSCNumber());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().has3GPPAAAServerName())
                        this.dlr3gppAAAServerName = new String(AVPHandler.diameterIdToMapDiameterId(lrr.getDelayedLocationReportingDataAvp().getServingNode().get3GPPAAAServerName()).getData());
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasLcsCapabilitiesSets())
                        this.dlrLcsCapabilitySets = lrr.getDelayedLocationReportingDataAvp().getServingNode().getLcsCapabilitiesSets();
                    if (lrr.getDelayedLocationReportingDataAvp().getServingNode().hasGMLCAddress()) {
                        this.dlrGmlcAddress = bytesToHexString(lrr.getDelayedLocationReportingDataAvp().getServingNode().getGMLCAddress().getAddress());
                        try {
                            InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(this.dlrGmlcAddress));
                            this.dlrGmlcAddress = address.getHostAddress();
                        } catch (UnknownHostException e) {
                            logger.error(e.getMessage());
                        }
                    }
                }
            }

            /*** Sequence Number (Reporting-Amount AVP) ***/
            if (lrr.getSequenceNumber() != null) {
                this.sequenceNumber = lrr.getSequenceNumber();
            }

            /*** Civic-Address AVP ***/
            if (lrr.getCivicAddress() != null) {
                this.civicAddress = new String(lrr.getCivicAddress().getBytes(), StandardCharsets.UTF_8);
            }

            /*** Barometric-Pressure AVP ***/
            if (lrr.getBarometricPressure() != null) {
                this.barometricPressure = lrr.getBarometricPressure();
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

    public Long getEci() {
        return eci;
    }

    public void setEci(Long eci) {
        this.eci = eci;
    }

    public Long getENBId() {
        return eNBId;
    }

    public void setENBId(Long eNBId) {
        this.eNBId = eNBId;
    }

    public Long getCellPortionId() {
        return cellPortionId;
    }

    public void setCellPortionId(Long cellPortionId) {
        this.cellPortionId = cellPortionId;
    }

    public String getMmeRealm() {
        return mmeRealm;
    }

    public void setMmeRealm(String mmeRealm) {
        this.mmeRealm = mmeRealm;
    }

    public String getSgsnRealm() {
        return sgsnRealm;
    }

    public void setSgsnRealm(String sgsnRealm) {
        this.sgsnRealm = sgsnRealm;
    }

    public String getCivicAddress() {
        return civicAddress;
    }

    public void setCivicAddress(String civicAddress) {
        this.civicAddress = civicAddress;
    }

    public Long getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(Long barometricPressure) {
        this.barometricPressure = barometricPressure;
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

    public Boolean getGprsNodeIndicator() {
        return gprsNodeIndicator;
    }

    public void setGprsNodeIndicator(Boolean gprsNodeIndicator) {
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

    public String getGmlcAddress() {
        return gmlcAddress;
    }

    public void setGmlcAddress(String gmlcAddress) {
        this.gmlcAddress = gmlcAddress;
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

    public Long getAgeOfLocationEstimate() {
        return this.ageOfLocationEstimate;
    }

    public void setAgeOfLocationEstimate(Long ageOfLocationEstimate) {
        this.ageOfLocationEstimate = ageOfLocationEstimate;
    }

    public String getPositioningMethod() {
        return this.positioningMethod;
    }

    public void setPositioningMethod(String positioningMethod) {
        this.positioningMethod = positioningMethod;
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

    public Integer getLcsQoSClassValue() {
        return lcsQoSClassValue;
    }

    public void setLcsQoSClassValue(Integer lcsQoSClassValue) {
        this.lcsQoSClassValue = lcsQoSClassValue;
    }

    public String getLcsEPSClientName() {
        return lcsEPSClientName;
    }

    public void setLcsEPSClientName(String lcsEPSClientName) {
        this.lcsEPSClientName = lcsEPSClientName;
    }

    public String getMtlrMmeName() {
        return mtlrMmeName;
    }

    public void setMtlrMmeName(String mtlrMmeName) {
        this.mtlrMmeName = mtlrMmeName;
    }

    public String getMtlrMmeRealm() {
        return mtlrMmeRealm;
    }

    public void setMtlrMmeRealm(String mtlrMmeRealm) {
        this.mtlrMmeRealm = mtlrMmeRealm;
    }

    public String getMtlrSgsnName() {
        return mtlrSgsnName;
    }

    public void setMtlrSgsnName(String mtlrSgsnName) {
        this.mtlrSgsnName = mtlrSgsnName;
    }

    public String getMtlrSgsnRealm() {
        return mtlrSgsnRealm;
    }

    public void setMtlrSgsnRealm(String mtlrSgsnRealm) {
        this.mtlrSgsnRealm = mtlrSgsnRealm;
    }

    public String getMtlrSgsnNumber() {
        return mtlrSgsnNumber;
    }

    public void setMtlrSgsnNumber(String mtlrSgsnNumber) {
        this.mtlrSgsnNumber = mtlrSgsnNumber;
    }

    public String getMtlr3gppAAAServerName() {
        return mtlr3gppAAAServerName;
    }

    public void setMtlr3gppAAAServerName(String mtlr3gppAAAServerName) {
        this.mtlr3gppAAAServerName = mtlr3gppAAAServerName;
    }

    public String getMtlrMscNumber() {
        return mtlrMscNumber;
    }

    public void setMtlrMscNumber(String mtlrMscNumber) {
        this.mtlrMscNumber = mtlrMscNumber;
    }

    public String getMtlrGmlcAddress() {
        return mtlrGmlcAddress;
    }

    public void setMtlrGmlcAddress(String mtlrGmlcAddress) {
        this.mtlrGmlcAddress = mtlrGmlcAddress;
    }

    public String getDlrMmeName() {
        return dlrMmeName;
    }

    public void setDlrMmeName(String dlrMmeName) {
        this.dlrMmeName = dlrMmeName;
    }

    public String getDlrMmeRealm() {
        return dlrMmeRealm;
    }

    public void setDlrMmeRealm(String dlrMmeRealm) {
        this.dlrMmeRealm = dlrMmeRealm;
    }

    public String getDlrSgsnName() {
        return dlrSgsnName;
    }

    public void setDlrSgsnName(String dlrSgsnName) {
        this.dlrSgsnName = dlrSgsnName;
    }

    public String getDlrSgsnRealm() {
        return dlrSgsnRealm;
    }

    public void setDlrSgsnRealm(String dlrSgsnRealm) {
        this.dlrSgsnRealm = dlrSgsnRealm;
    }

    public String getDlrSgsnNumber() {
        return dlrSgsnNumber;
    }

    public void setDlrSgsnNumber(String dlrSgsnNumber) {
        this.dlrSgsnNumber = dlrSgsnNumber;
    }

    public String getDlr3gppAAAServerName() {
        return dlr3gppAAAServerName;
    }

    public void setDlr3gppAAAServerName(String dlr3gppAAAServerName) {
        this.dlr3gppAAAServerName = dlr3gppAAAServerName;
    }

    public String getDlrMscNumber() {
        return dlrMscNumber;
    }

    public void setDlrMscNumber(String dlrMscNumber) {
        this.dlrMscNumber = dlrMscNumber;
    }

    public Long getLcsServiceTypeId() {
        return lcsServiceTypeId;
    }

    public String getDlrGmlcAddress() {
        return dlrGmlcAddress;
    }

    public void setDlrGmlcAddress(String dlrGmlcAddress) {
        this.dlrGmlcAddress = dlrGmlcAddress;
    }

    public void setLcsServiceTypeId(Long lcsServiceTypeId) {
        this.lcsServiceTypeId = lcsServiceTypeId;
    }

    public Long getReportingAmount() {
        return reportingAmount;
    }

    public void setReportingAmount(Long reportingAmount) {
        this.reportingAmount = reportingAmount;
    }

    public Long getReportingInterval() {
        return reportingInterval;
    }

    public void setReportingInterval(Long reportingInterval) {
        this.reportingInterval = reportingInterval;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Long getDeferredLocationType() {
        return deferredLocationType;
    }

    public void setDeferredLocationType(Long deferredLocationType) {
        this.deferredLocationType = deferredLocationType;
    }

    public Integer getClientReferenceNumber() {
        return clientReferenceNumber;
    }

    public void setClientReferenceNumber(Integer clientReferenceNumber) {
        this.clientReferenceNumber = clientReferenceNumber;
    }

    public Integer getLcsFormatIndicator() {
        return lcsFormatIndicator;
    }

    public void setLcsFormatIndicator(Integer lcsFormatIndicator) {
        this.lcsFormatIndicator = lcsFormatIndicator;
    }

    public Integer getLocationEvent() {
        return locationEvent;
    }

    public void setLocationEvent(Integer locationEvent) {
        this.locationEvent = locationEvent;
    }

    public Integer getLcsPseudonymIndicator() {
        return lcsPseudonymIndicator;
    }

    public void setLcsPseudonymIndicator(Integer lcsPseudonymIndicator) {
        this.lcsPseudonymIndicator = lcsPseudonymIndicator;
    }
}
