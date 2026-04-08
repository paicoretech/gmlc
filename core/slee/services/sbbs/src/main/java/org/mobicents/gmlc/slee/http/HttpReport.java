package org.mobicents.gmlc.slee.http;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.mobicents.gmlc.slee.diameter.DiameterLcsResponseHelperForMLP;
import org.mobicents.gmlc.slee.diameter.slg.SLgLrrAvpValues;
import org.mobicents.gmlc.slee.http.report.ReportParameters;
import org.mobicents.gmlc.slee.http.report.ReportRegister;
import org.mobicents.gmlc.slee.map.MapLsmResponseHelperForMLP;
import org.mobicents.gmlc.slee.map.SlrRequestParams;
import org.mobicents.gmlc.slee.mlp.MLPResponse;
import org.mobicents.gmlc.slee.primitives.Polygon;
import org.mobicents.gmlc.slee.supl.SuplResponseHelperForMLP;
import org.restcomm.protocols.ss7.map.api.MAPException;

import javax.slee.facilities.Tracer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class HttpReport {

    private final Logger logger = Logger.getLogger("main");
    private Tracer tracer;

    public enum HttpMethod { POST, GET }

    // Registration is made on internal id storing referenceNumber for callbackUrl and reportParameters passed
    ReportRegister reportRegister;

    // Mongo DB Object
    DB mongoDB = null;

    public HttpReport() {
        if (this.logger.isDebugEnabled()) {
            logger.debug("HttpReport Initialized");
        }
        reportRegister = new ReportRegister();
    }

    public HttpReport(String mongoHost, int mongoPort, String mongoDatabase) {
        try {
            if (this.logger.isDebugEnabled()) {
                logger.debug("HttpReport Initialized for MongoDB {"  + mongoHost + ":" + mongoPort + "/" + mongoDatabase + " }");
            }
            MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
            mongoDB = mongoClient.getDB(mongoDatabase);
        } catch (Exception e) {
            logger.error("Exception on HttpReport(String mongoHost, int mongoPort, String mongoDatabase) constructor : ", e);
        }
    }

    public Integer Register(Integer clientReferenceNumber, String callbackUrl, ReportParameters reportParameters, Boolean mlp, String requestor) {
        Integer referenceNumber;
        if (mongoDB == null) {
            // add reference number passed
            if (reportParameters == null)
                reportParameters = new ReportParameters();
            reportParameters.put("transactionNumber", clientReferenceNumber);
            reportParameters.put("callbackUrl", callbackUrl);

            // return registered id for later retrieval and perform http method callback
            referenceNumber = reportRegister.add(reportParameters);
        } else {
            // get next serial number
            DBObject nextReferenceNumberObject =
                mongoDB.getCollection("http-report-counter").findAndModify(
                    new BasicDBObject("name", "serialNumber"),
                    new BasicDBObject("$inc",
                        new BasicDBObject("counter", 1)));

            // set as transactionNumber
            referenceNumber = (int) Double.parseDouble(nextReferenceNumberObject.get("counter").toString());

            // update transaction
            WriteResult writeResult =
                mongoDB.getCollection("http-report").update(
                    new BasicDBObject("referenceNumber", referenceNumber),
                    new BasicDBObject("referenceNumber", referenceNumber)
                        .append("transactionNumber", clientReferenceNumber)
                        .append("callbackUrl", callbackUrl)
                        .append("reportParameters", reportParameters)
                        .append("mlp", mlp)
                        .append("requestor", requestor),
                    true,
                    false);
        }

        if (this.logger.isDebugEnabled()) {
            logger.debug("Registered { clientReferenceNumber: " + clientReferenceNumber +
                ", callbackUrl: " + callbackUrl +
                ", reportParameters: { " + (reportParameters == null ? "" : reportParameters.toString()) +
                " } , referenceNumber: " + referenceNumber + " }" +
                " } , mlp: " + mlp + " }");
        }

        return referenceNumber;
    }

    public void Cancel(Integer referenceNumber) {
        if (mongoDB == null) {
            reportRegister.remove(referenceNumber);
        } else {
            mongoDB.getCollection("http-report").findAndRemove(
                new BasicDBObject("referenceNumber", referenceNumber));
        }
    }

    public void Perform(HttpMethod httpMethod, Integer lcsReferenceNumber, Object objectReportParameters, Boolean ss7) throws IOException, MAPException {
        String callbackUrl = GmlcPropertiesManagement.getInstance().getLcsUrlCallback();
        ReportParameters transactionReportParameters = null;
        String reportCommandParameterString = null;
        Integer clientReferenceNumber = null;
        Integer ss7LcsReferenceNumber = null;
        Boolean mlp = false;
        String requestor = null;
        if (ss7 && lcsReferenceNumber != null)
            if (lcsReferenceNumber < 0)
                ss7LcsReferenceNumber = lcsReferenceNumber + 256;
            else
                ss7LcsReferenceNumber = lcsReferenceNumber;

        if (lcsReferenceNumber != null) {
            if (mongoDB != null) {
                // get the callback from mongo db
                DBObject findOne;
                if (ss7)
                    findOne = mongoDB.getCollection("http-report").findOne(new BasicDBObject("referenceNumber", ss7LcsReferenceNumber));
                else
                    findOne = mongoDB.getCollection("http-report").findOne(new BasicDBObject("referenceNumber", lcsReferenceNumber));
                if (findOne != null) {
                    callbackUrl = (String) findOne.get("callbackUrl");
                    clientReferenceNumber = (Integer) findOne.get("transactionNumber");
                    transactionReportParameters = (ReportParameters) findOne.get("reportParameters");
                    mlp = (Boolean) findOne.get("mlp");
                    requestor = (String) findOne.get("requestor");
                }
            } else {
                // get the report element parameters previously registered
                transactionReportParameters = reportRegister.get(lcsReferenceNumber);
                if (transactionReportParameters != null) {
                    callbackUrl = (String) transactionReportParameters.get("callbackUrl");
                }
            }
        } else {
            String lcsReportApi = GmlcPropertiesManagement.getInstance().getLcsNonTriggeredReportOption();
            mlp = lcsReportApi.equalsIgnoreCase("MLP");
        }

        try {
            switch(objectReportParameters.getClass().getSimpleName()) {
                case "List<String>":
                    reportCommandParameterString = SlrRequestJsonBuilder.buildJsonReportForSlrFromStringList((List<String>) objectReportParameters);
                    performJsonReportToCallbackUrl(httpMethod, transactionReportParameters, reportCommandParameterString, callbackUrl);
                    break;
                case "SlrRequestParams":
                    if (!mlp) {
                        reportCommandParameterString = SlrRequestJsonBuilder.buildJsonReportForSLR((SlrRequestParams) objectReportParameters, clientReferenceNumber);
                        performJsonReportToCallbackUrl(httpMethod, transactionReportParameters, reportCommandParameterString, callbackUrl);
                    } else {
                        performMLPReportToCallbackUrl((SlrRequestParams) objectReportParameters, null, null, clientReferenceNumber, lcsReferenceNumber,
                            callbackUrl, httpMethod);
                    }
                    break;
                case "SLgLrrAvpValues":
                    if (!mlp) {
                        reportCommandParameterString = LrrRequestJsonBuilder.buildJsonReportForLRR((SLgLrrAvpValues) objectReportParameters, clientReferenceNumber);
                        performJsonReportToCallbackUrl(httpMethod, transactionReportParameters, reportCommandParameterString, callbackUrl);
                    } else {
                        performMLPReportToCallbackUrl(null, (SLgLrrAvpValues) objectReportParameters, null, clientReferenceNumber, lcsReferenceNumber,
                            callbackUrl, httpMethod);
                    }
                    break;
                case "SuplResponseHelperForMLP":
                    performMLPReportToCallbackUrl(null, null, (SuplResponseHelperForMLP) objectReportParameters, clientReferenceNumber, lcsReferenceNumber, callbackUrl, httpMethod);
                    break;
                default:
                    logger.warn("Perform [default] { type: " + objectReportParameters.getClass().getSimpleName() + " }");
                    performJsonReportToCallbackUrl(httpMethod, transactionReportParameters, reportCommandParameterString, callbackUrl);
                    break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.warn("Perform { httpMethod: " + httpMethod.toString() + ", lcsReferenceNumber: " + (ss7 ? ss7LcsReferenceNumber : lcsReferenceNumber) +
                ", transactionReportParameters: { " + (transactionReportParameters == null ? "" : transactionReportParameters.toString()) +
                " }, reportCommandParameterString: " + reportCommandParameterString + ", callbackUrl: " + callbackUrl +
                ", callbackUrl: " + callbackUrl + " }");
        }
    }

    private void performJsonReportToCallbackUrl(HttpMethod httpMethod, ReportParameters reportParameters,
                                                String reportCommandParameterString, String callbackUrl) {
        try {
            URL urlCallback = new URL(callbackUrl);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlCallback.openConnection();
            httpUrlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpUrlConnection.setRequestMethod(httpMethod.toString());
            httpUrlConnection.setDoOutput(true);
            OutputStream httpOutputStream = httpUrlConnection.getOutputStream();

            httpOutputStream.write("{ ".getBytes());

            if (reportParameters != null) {
                httpOutputStream.write(reportParameters.toJsonString(false).getBytes());
            }

            if (reportCommandParameterString != null && !reportCommandParameterString.isEmpty()) {
                if (reportParameters != null)
                    httpOutputStream.write(", ".getBytes());
                httpOutputStream.write(reportCommandParameterString.replaceAll("^\\{|}$", "").getBytes());
            }

            httpOutputStream.write(" }".getBytes());

            httpOutputStream.flush();
            httpOutputStream.close();

            // obtain HTTP response for log
            httpUrlConnection.getResponseCode();
        } catch (IOException ioe) {
            logger.warn(String.format("Cannot perform report callback to provided URL, stack trace is '%s'", ioe));
        }
    }

    private void performMLPReportToCallbackUrl(SlrRequestParams slr, SLgLrrAvpValues lrr, SuplResponseHelperForMLP suplResponseHelperForMLP,
                                               Integer clientReferenceNumber, Integer lcsReferenceNumber,
                                               String callbackUrl, HttpMethod httpMethod) {
        String svcResultXml, operation;
        MLPResponse mlpResponse = new MLPResponse(this.tracer);

        if (slr != null || lrr != null || suplResponseHelperForMLP != null) {
            MLPResponse.MLPResultType mlpResultType = MLPResponse.MLPResultType.OK;
            MLPResponseParams mlpResponseParams = new MLPResponseParams();
            if (slr != null) {
                operation = "SLR";
                MapLsmResponseHelperForMLP mapLsmHelper = new MapLsmResponseHelperForMLP();
                mapLsmHelper.handleSlrRequestValues(slr, clientReferenceNumber);
                mlpResponseParams.mlpMsisdn = mapLsmHelper.getMsisdn();
                mlpResponseParams.mlpImsi = mapLsmHelper.getImsi();
                mlpResponseParams.x = mapLsmHelper.getLatitude();
                mlpResponseParams.y = mapLsmHelper.getLongitude();
                mlpResponseParams.mlpTypeOfShape = mapLsmHelper.getTypeOfShape();
                mlpResponseParams.radius = mapLsmHelper.getRadius();
                mlpResponseParams.mlpUncertaintySemiMajorAxis = mapLsmHelper.getUncertaintySemiMajorAxis();
                mlpResponseParams.mlpUncertaintySemiMinorAxis = mapLsmHelper.getUncertaintySemiMinorAxis();
                mlpResponseParams.mlpAngleOfMajorAxis = mapLsmHelper.getAngleOfMajorAxis();
                mlpResponseParams.mlpOffsetAngle = mapLsmHelper.getOffsetAngle();
                mlpResponseParams.mlpIncludedAngle = mapLsmHelper.getIncludedAngle();
                mlpResponseParams.mlpAltitude = mapLsmHelper.getAltitude();
                mlpResponseParams.mlpUncertaintyAltitude = mapLsmHelper.getUncertaintyAltitude();
                mlpResponseParams.mlpPolygon = mapLsmHelper.getPolygon();
                mlpResponseParams.mlpNumberOfPoints = mapLsmHelper.getNumberOfPoints();
                mlpResponseParams.mlpPositioningMethod = mapLsmHelper.getPositioningMethod();
                mlpResponseParams.mlpTargetHorizontalSpeed = mapLsmHelper.getHorizontalSpeed();
                mlpResponseParams.mlpBearing = mapLsmHelper.getBearing();
                mlpResponseParams.mlpTargetVerticalSpeed = mapLsmHelper.getVerticalSpeed();
                mlpResponseParams.mlpUncertaintyHorizontalSpeed = mapLsmHelper.getUncertaintyHorizontalSpeed();
                mlpResponseParams.mlpUncertaintyVerticalSpeed = mapLsmHelper.getUncertaintyVerticalSpeed();
                mlpResponseParams.mlpVelocityType = mapLsmHelper.getVelocityType();
                mlpResponseParams.mlpMcc = mapLsmHelper.getMcc();
                mlpResponseParams.mlpMnc = mapLsmHelper.getMnc();
                mlpResponseParams.mlpLac = mapLsmHelper.getLac();
                mlpResponseParams.mlpCi = mapLsmHelper.getCi();
                mlpResponseParams.mlpSac = mapLsmHelper.getSac();
                mlpResponseParams.mlpEci = null;
                mlpResponseParams.mlpEnbId = null;
                mlpResponseParams.mlpTac = null;
                mlpResponseParams.mlpNci = null;
                mlpResponseParams.mlpNrTac = null;
                mlpResponseParams.mlpMmeName = mapLsmHelper.getMmeName();
                mlpResponseParams.mlpMmeRealm = null;
                mlpResponseParams.mlpSgsnName = mapLsmHelper.getSgsnName();
                mlpResponseParams.mlpSgsnRealm = null;
                mlpResponseParams.mlpSgsnNumber = mapLsmHelper.getSgsnNumber();
                mlpResponseParams.mlpAmfAddress = null;
                mlpResponseParams.mlpMscNo = mapLsmHelper.getMscNumber();
                mlpResponseParams.mlpVlrNo = mapLsmHelper.getVlrNumber();
                mlpResponseParams.mlpImei = mapLsmHelper.getImei();
                mlpResponseParams.mlpLmsi = mapLsmHelper.getLmsi();
                mlpResponseParams.mlpAge = mapLsmHelper.getAgeOfLocationEstimate();
                mlpResponseParams.mlpTransId = clientReferenceNumber;
                mlpResponseParams.mlpLcsRefNumber = lcsReferenceNumber;
                mlpResponseParams.mlpRatType = null;
                mlpResponseParams.mlpCivicAddress = mapLsmHelper.getCivicAddress();
            } else if (lrr != null) {
                operation = "LRR";
                DiameterLcsResponseHelperForMLP diameterLcsHelper = new DiameterLcsResponseHelperForMLP();
                diameterLcsHelper.handleLrrResponseValues(lrr, clientReferenceNumber);
                mlpResponseParams.mlpMsisdn = diameterLcsHelper.getMsisdn();
                mlpResponseParams.mlpImsi = diameterLcsHelper.getImsi();
                mlpResponseParams.x = diameterLcsHelper.getLatitude();
                mlpResponseParams.y = diameterLcsHelper.getLongitude();
                mlpResponseParams.mlpTypeOfShape = diameterLcsHelper.getTypeOfShape();
                mlpResponseParams.radius = diameterLcsHelper.getRadius();
                mlpResponseParams.mlpUncertaintySemiMajorAxis = diameterLcsHelper.getUncertaintySemiMajorAxis();
                mlpResponseParams.mlpUncertaintySemiMinorAxis = diameterLcsHelper.getUncertaintySemiMinorAxis();
                mlpResponseParams.mlpAngleOfMajorAxis = diameterLcsHelper.getAngleOfMajorAxis();
                mlpResponseParams.mlpOffsetAngle = diameterLcsHelper.getOffsetAngle();
                mlpResponseParams.mlpIncludedAngle = diameterLcsHelper.getIncludedAngle();
                mlpResponseParams.mlpAltitude = diameterLcsHelper.getAltitude();
                mlpResponseParams.mlpUncertaintyAltitude = diameterLcsHelper.getUncertaintyAltitude();
                mlpResponseParams.mlpPolygon = diameterLcsHelper.getPolygon();
                mlpResponseParams.mlpNumberOfPoints = diameterLcsHelper.getNumberOfPoints();
                mlpResponseParams.mlpPositioningMethod = diameterLcsHelper.getPositioningMethod();
                mlpResponseParams.mlpTargetHorizontalSpeed = diameterLcsHelper.getHorizontalSpeed();
                mlpResponseParams.mlpBearing = diameterLcsHelper.getBearing();
                mlpResponseParams.mlpTargetVerticalSpeed = diameterLcsHelper.getVerticalSpeed();
                mlpResponseParams.mlpUncertaintyHorizontalSpeed = diameterLcsHelper.getUncertaintyHorizontalSpeed();
                mlpResponseParams.mlpUncertaintyVerticalSpeed = diameterLcsHelper.getUncertaintyVerticalSpeed();
                mlpResponseParams.mlpVelocityType = diameterLcsHelper.getVelocityType();
                mlpResponseParams.mlpMcc = diameterLcsHelper.getMcc();
                mlpResponseParams.mlpMnc = diameterLcsHelper.getMnc();
                mlpResponseParams.mlpLac = diameterLcsHelper.getLac();
                mlpResponseParams.mlpCi = diameterLcsHelper.getCi();
                mlpResponseParams.mlpSac = diameterLcsHelper.getSac();
                mlpResponseParams.mlpEci = diameterLcsHelper.getEci();
                mlpResponseParams.mlpEnbId = diameterLcsHelper.getENBId();
                mlpResponseParams.mlpRac = null;
                mlpResponseParams.mlpTac = null;
                mlpResponseParams.mlpNci = null;
                mlpResponseParams.mlpNrTac = null;
                mlpResponseParams.mlpMmeName = diameterLcsHelper.getMmeName();
                mlpResponseParams.mlpMmeRealm = diameterLcsHelper.getMmeRealm();
                mlpResponseParams.mlpSgsnName = diameterLcsHelper.getSgsnName();
                mlpResponseParams.mlpSgsnRealm = diameterLcsHelper.getSgsnRealm();
                mlpResponseParams.mlpSgsnNumber = diameterLcsHelper.getSgsnNumber();
                mlpResponseParams.mlpAmfAddress = null;
                mlpResponseParams.mlpMscNo = diameterLcsHelper.getMscNumber();
                mlpResponseParams.mlpVlrNo = diameterLcsHelper.getVlrNumber();
                mlpResponseParams.mlpImei = diameterLcsHelper.getImei();
                mlpResponseParams.mlpLmsi = diameterLcsHelper.getLmsi();
                mlpResponseParams.mlpAge = (int) (long) diameterLcsHelper.getAgeOfLocationEstimate();
                mlpResponseParams.mlpTransId = clientReferenceNumber;
                mlpResponseParams.mlpLcsRefNumber = lcsReferenceNumber;
                mlpResponseParams.mlpRatType = null;
                mlpResponseParams.mlpCivicAddress = diameterLcsHelper.getCivicAddress();
            } else {
                operation = "SUPL";
                mlpResponseParams.mlpMsisdn = suplResponseHelperForMLP.getMsisdn();
                mlpResponseParams.mlpImsi = suplResponseHelperForMLP.getImsi();
                mlpResponseParams.x = suplResponseHelperForMLP.getLatitude();
                mlpResponseParams.y = suplResponseHelperForMLP.getLongitude();
                if (suplResponseHelperForMLP.getTypeOfShape() != null)
                    mlpResponseParams.mlpTypeOfShape = suplResponseHelperForMLP.getTypeOfShape().name();
                mlpResponseParams.radius = suplResponseHelperForMLP.getRadius();
                mlpResponseParams.mlpUncertaintySemiMajorAxis = suplResponseHelperForMLP.getUncertaintySemiMajorAxis();
                mlpResponseParams.mlpUncertaintySemiMinorAxis = suplResponseHelperForMLP.getUncertaintySemiMinorAxis();
                mlpResponseParams.mlpAngleOfMajorAxis = suplResponseHelperForMLP.getAngleOfMajorAxis();
                mlpResponseParams.mlpOffsetAngle = suplResponseHelperForMLP.getOffsetAngle();
                mlpResponseParams.mlpIncludedAngle = suplResponseHelperForMLP.getIncludedAngle();
                mlpResponseParams.mlpAltitude = suplResponseHelperForMLP.getAltitude();
                mlpResponseParams.mlpUncertaintyAltitude = suplResponseHelperForMLP.getUncertaintyAltitude();
                mlpResponseParams.mlpPolygon = null;  // FIXME
                mlpResponseParams.mlpNumberOfPoints = null; // FIXME
                if (suplResponseHelperForMLP.getPosMethod().getAsn1TypeName() != null)
                    mlpResponseParams.mlpPositioningMethod = suplResponseHelperForMLP.getPosMethod().getAsn1TypeName();
                mlpResponseParams.mlpTargetHorizontalSpeed = suplResponseHelperForMLP.getHorizontalSpeed();
                mlpResponseParams.mlpBearing = suplResponseHelperForMLP.getBearing();
                mlpResponseParams.mlpTargetVerticalSpeed = suplResponseHelperForMLP.getVerticalSpeed();
                mlpResponseParams.mlpUncertaintyHorizontalSpeed = suplResponseHelperForMLP.getUncertaintyHorizontalSpeed();
                mlpResponseParams.mlpUncertaintyVerticalSpeed = suplResponseHelperForMLP.getUncertaintyVerticalSpeed();
                mlpResponseParams.mlpVelocityType = suplResponseHelperForMLP.getVelocityType();
                mlpResponseParams.mlpMcc = suplResponseHelperForMLP.getMcc();
                mlpResponseParams.mlpMnc = suplResponseHelperForMLP.getMnc();
                mlpResponseParams.mlpLac = suplResponseHelperForMLP.getLac();
                mlpResponseParams.mlpCi = suplResponseHelperForMLP.getCi();
                mlpResponseParams.mlpSac = suplResponseHelperForMLP.getSac();
                mlpResponseParams.mlpUci = suplResponseHelperForMLP.getUci();
                mlpResponseParams.mlpEci = suplResponseHelperForMLP.getEci();
                mlpResponseParams.mlpEnbId  = suplResponseHelperForMLP.getEnbId();
                mlpResponseParams.mlpRac = suplResponseHelperForMLP.getRac();
                mlpResponseParams.mlpTac = suplResponseHelperForMLP.getTac();
                mlpResponseParams.mlpNci = suplResponseHelperForMLP.getNrCi();
                mlpResponseParams.mlpNrTac = null;
                mlpResponseParams.mlpMmeName = null;
                mlpResponseParams.mlpMmeRealm = null;
                mlpResponseParams.mlpSgsnName = null;
                mlpResponseParams.mlpSgsnRealm = null;
                mlpResponseParams.mlpSgsnNumber = null;
                mlpResponseParams.mlpAmfAddress = null;
                mlpResponseParams.mlpMscNo = null;
                mlpResponseParams.mlpVlrNo = null;
                mlpResponseParams.mlpImei = null;
                mlpResponseParams.mlpLmsi = null;
                mlpResponseParams.mlpAge = null;
                mlpResponseParams.mlpTargetHorizontalSpeed = null;
                mlpResponseParams.mlpTargetVerticalSpeed = null;
                mlpResponseParams.mlpVelocityType = null;
                mlpResponseParams.mlpTransId = clientReferenceNumber;
                mlpResponseParams.mlpLcsRefNumber = lcsReferenceNumber;
                mlpResponseParams.mlpRatType = null;
                mlpResponseParams.mlpCivicAddress = null;
            }

            svcResultXml = mlpResponse.getCoreNetworkSinglePositionXML(operation, mlpResponseParams.mlpTypeOfShape,
                mlpResponseParams.x, mlpResponseParams.y, mlpResponseParams.radius, mlpResponseParams.mlpUncertaintySemiMajorAxis,
                mlpResponseParams.mlpUncertaintySemiMinorAxis, mlpResponseParams.mlpAngleOfMajorAxis, mlpResponseParams.mlpOffsetAngle,
                mlpResponseParams.mlpIncludedAngle, mlpResponseParams.mlpAltitude, mlpResponseParams.mlpUncertaintyAltitude,
                mlpResponseParams.mlpPolygon, mlpResponseParams.mlpNumberOfPoints,
                mlpResponseParams.mlpPositioningMethod,
                mlpResponseParams.mlpTargetHorizontalSpeed, mlpResponseParams.mlpBearing, mlpResponseParams.mlpTargetVerticalSpeed,
                mlpResponseParams.mlpUncertaintyHorizontalSpeed, mlpResponseParams.mlpUncertaintyVerticalSpeed, mlpResponseParams.mlpVelocityType,
                mlpResponseParams.mlpMcc, mlpResponseParams.mlpMnc, mlpResponseParams.mlpLac, mlpResponseParams.mlpCi, mlpResponseParams.mlpSac,
                mlpResponseParams.mlpUci, mlpResponseParams.mlpEci, mlpResponseParams.mlpEnbId, mlpResponseParams.mlpRac, mlpResponseParams.mlpTac,
                mlpResponseParams.mlpNci, mlpResponseParams.mlpNrTac,
                mlpResponseParams.mlpMmeName, mlpResponseParams.mlpMmeRealm, mlpResponseParams.mlpSgsnName, mlpResponseParams.mlpSgsnRealm,
                mlpResponseParams.mlpSgsnNumber, mlpResponseParams.mlpAmfAddress, mlpResponseParams.mlpMscNo, mlpResponseParams.mlpVlrNo,
                mlpResponseParams.mlpMsisdn, mlpResponseParams.mlpImei, mlpResponseParams.mlpImsi, mlpResponseParams.mlpAge,
                mlpResponseParams.mlpLmsi, mlpResponseParams.mlpTransId, mlpResponseParams.mlpLcsRefNumber,
                mlpResponseParams.mlpRatType, mlpResponseParams.mlpCivicAddress,
                mlpResultType, lcsReferenceNumber != null, true);

        } else {
            svcResultXml = mlpResponse.getSystemErrorResponseXML(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Location report is null",
                    lcsReferenceNumber != null);
        }

        try {
            URL urlCallback = new URL(callbackUrl);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlCallback.openConnection();
            httpUrlConnection.setRequestProperty("Content-Type", "application/xml; utf-8");
            httpUrlConnection.setRequestMethod(httpMethod.toString());
            httpUrlConnection.setDoOutput(true);
            OutputStream httpOutputStream = httpUrlConnection.getOutputStream();

            if (svcResultXml != null) {
                httpOutputStream.write(svcResultXml.getBytes(StandardCharsets.UTF_8));
            }

            httpOutputStream.flush();
            httpOutputStream.close();

            // obtain HTTP response for log
            httpUrlConnection.getResponseCode();
        } catch (IOException e) {
            logger.warn(String.format("Cannot perform report callback to provided URL, stack trace is '%s'", e.getMessage()));
        }
    }

    public void closeMongo() {
        try {
            mongoDB.getMongo().close();
        } catch (Exception e) {
            logger.error("Exception when closing connection to MongoDB");
        }
    }

    /**
     * Inner class for MLP response parameters
     */
    private static class MLPResponseParams implements Serializable {

        /*********************/
        /*** MLP Response ***/
        /*******************/
        String mlpMsisdn;
        Integer mlpMcc, mlpMnc, mlpLac, mlpCi, mlpSac, mlpTac, mlpRac, mlpNrTac;
        Long mlpUci, mlpEci, mlpEnbId, mlpNci;
        String mlpVlrNo, mlpMscNo, mlpMmeName, mlpMmeRealm, mlpSgsnName, mlpSgsnRealm, mlpSgsnNumber, mlpAmfAddress;
        String mlpState;
        Integer mlpAge;
        Double x;
        Double y;
        String mlpTypeOfShape;
        Double radius;
        Polygon mlpPolygon;
        Integer mlpNumberOfPoints;
        String mlpPositioningMethod;
        Double mlpUncertainty, mlpUncertaintySemiMajorAxis, mlpUncertaintySemiMinorAxis, mlpAngleOfMajorAxis,
            mlpUncertaintyAltitude, mlpUncertaintyInnerRadius, mlpOffsetAngle, mlpIncludedAngle;
        Integer mlpConfidence, mlpAltitude, mlpInnerRadius, numberOfPoints;
        Integer mlpAgeOfLocationEstimate, mlpAccuracyFulfilmentIndicator;
        Integer mlpTargetHorizontalSpeed, mlpBearing, mlpTargetVerticalSpeed, mlpUncertaintyHorizontalSpeed, mlpUncertaintyVerticalSpeed;
        String mlpVelocityType;
        String mlpImei, mlpImsi, mlpLmsi;
        String mlpCivicAddress;
        Long mlpBarometricPressure;
        Integer mlpTransId;
        Integer mlpLcsRefNumber;
        Integer mlpRatType;
    }
}
