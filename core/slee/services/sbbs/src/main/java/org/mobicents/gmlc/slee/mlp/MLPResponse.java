package org.mobicents.gmlc.slee.mlp;

import org.mobicents.gmlc.slee.mlp.v3_4.Cgi;
import org.mobicents.gmlc.slee.mlp.v3_4.CircularArcArea;
import org.mobicents.gmlc.slee.mlp.v3_4.CircularArea;
import org.mobicents.gmlc.slee.mlp.v3_4.Civicloc;
import org.mobicents.gmlc.slee.mlp.v3_4.CiviclocElement;
import org.mobicents.gmlc.slee.mlp.v3_4.Coord;
import org.mobicents.gmlc.slee.mlp.v3_4.DiameterIdentity;
import org.mobicents.gmlc.slee.mlp.v3_4.ECgi;
import org.mobicents.gmlc.slee.mlp.v3_4.EllipticalArea;
import org.mobicents.gmlc.slee.mlp.v3_4.ExtensionParams;
import org.mobicents.gmlc.slee.mlp.v3_4.GsmNetParam;
import org.mobicents.gmlc.slee.mlp.v3_4.LinearRing;
import org.mobicents.gmlc.slee.mlp.v3_4.Msid;
import org.mobicents.gmlc.slee.mlp.v3_4.MsisdsExt;
import org.mobicents.gmlc.slee.mlp.v3_4.Neid;
import org.mobicents.gmlc.slee.mlp.v3_4.NrCgi;
import org.mobicents.gmlc.slee.mlp.v3_4.NrTai;
import org.mobicents.gmlc.slee.mlp.v3_4.OuterBoundaryIs;
import org.mobicents.gmlc.slee.mlp.v3_4.Pd;
import org.mobicents.gmlc.slee.mlp.v3_4.Point;
import org.mobicents.gmlc.slee.mlp.v3_4.Polygon;
import org.mobicents.gmlc.slee.mlp.v3_4.Pos;
import org.mobicents.gmlc.slee.mlp.v3_4.Rai;
import org.mobicents.gmlc.slee.mlp.v3_4.Result;
import org.mobicents.gmlc.slee.mlp.v3_4.Sai;
import org.mobicents.gmlc.slee.mlp.v3_4.ServingNode;
import org.mobicents.gmlc.slee.mlp.v3_4.Sgsnid;
import org.mobicents.gmlc.slee.mlp.v3_4.Shape;
import org.mobicents.gmlc.slee.mlp.v3_4.Slia;
import org.mobicents.gmlc.slee.mlp.v3_4.Slrep;
import org.mobicents.gmlc.slee.mlp.v3_4.SvcResult;
import org.mobicents.gmlc.slee.mlp.v3_4.Tai;
import org.mobicents.gmlc.slee.mlp.v3_4.Time;
import org.mobicents.gmlc.slee.mlp.v3_4.Tlra;
import org.mobicents.gmlc.slee.mlp.v3_4.Tlrep;
import org.mobicents.gmlc.slee.mlp.v3_4.TrlPos;
import org.mobicents.gmlc.slee.mlp.v3_4.UtranCgi;
import org.mobicents.gmlc.slee.mlp.v3_4.VelocityEstimate;
import org.mobicents.gmlc.slee.mlp.v3_4.Vlrid;
import org.mobicents.gmlc.slee.mlp.v3_4.Vmscid;
import org.mobicents.gmlc.slee.primitives.CivicAddressElements;
import org.mobicents.gmlc.slee.primitives.CivicAddressXmlReader;

import javax.slee.facilities.Tracer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is a helper for generating MLP XML responses to send to an MLP client.
 * It uses the JAXB generated XML bound classes in org.oma.protocols.mlp
 * It exists to generate consistent XML output using the JAXB marshaller
 * It supports generating a result only for a single MSISDN that has a single Point (X/Y - lat/lon) result
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 * @author <a href="mailto:eross@locatrix.com"> Andrew Eross </a>
 */
public class MLPResponse {

  public enum MLPResultType {
    OK,
    SYSTEM_FAILURE,
    UNSPECIFIED_ERROR,
    UNAUTHORIZED_APPLICATION,
    UNKNOWN_SUBSCRIBER,
    ABSENT_SUBSCRIBER,
    POSITION_METHOD_FAILURE,
    TIMEOUT,
    CONGESTION_IN_LOCATION_SERVER,
    UNSUPPORTED_VERSION,
    TOO_MANY_POSITION_ITEMS,
    FORMAT_ERROR,
    SYNTAX_ERROR,
    PROTOCOL_ELEMENT_NOT_SUPPORTED,
    SERVICE_NOT_SUPPORTED,
    PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED,
    INVALID_PROTOCOL_ELEMENT_VALUE,
    INVALID_PROTOCOL_ELEMENT_ATTRIBUTE_VALUE,
    PROTOCOL_ELEMENT_VALUE_NOT_SUPPORTED,
    PROTOCOL_ELEMENT_ATTRIBUTE_VALUE_NOT_SUPPORTED,
    CANCELLATION_OF_TRIGGERED_LOCATION_REQUEST,
    INVALID_MSID_IN_TLRSR,
    TLRSR_FOR_INDIVIDUAL_TARGET_NOT_SUPPORTED,
    QOP_NOT_ATTAINABLE,
    POSITIONING_NOT_ALLOWED,
    CONGESTION_IN_MOBILE_NETWORK,
    DISALLOWED_BY_LOCAL_REGULATIONS,
    MISCONFIGURATION_OF_LOCATION_SERVER,
    TARGET_MOVED_TO_NEW_MSC_SGSN,
    STANDARD_LOCATION_REPORT_SERVICE_NOT_SUPPORTED,
    MLS_CLIENT_ERROR,
    STANDARD_LOCATION_REPORT_SERVICE_NOT_ACCEPTED,
    SUBSCRIBER_IN_STANDARD_LOCATION_REPORT_SERVICE_NOT_VALID,
    INVALID_SERVICE_ID_IN_STANDARD_LOCATION_REPORT_SERVICE
  }

  /**
   * Logger from the calling SBB
   */
  private final Tracer logger;

  protected static final DecimalFormat coordinatesFormat = new DecimalFormat("#0.000000");
  private String exceptionError = "";

  public MLPResponse(Tracer logger) {
    this.logger = logger;
  }

  // If there's an internal exception or other error, we have to fall back to some "worst case scenario"
  // static XML return data
  private final String genericStandardLocationRequestErrorXML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "  <!DOCTYPE svc_result SYSTEM \"MLP_v3_4.dtd\">" +
          "  <svc_result xmlns=\"MLP_v3_4.dtd\" ver=\"3.4.0\">\n" +
          "  <slia ver=\"3.4.0\">\n" +
          "    <result resid=\"1\">SYSTEM FAILURE</result>\n" +
          "    <add_info>" + exceptionError + "</add_info>\n" +
          "  </slia>\n" +
          "</svc_result>";

  private final String genericTriggeredLocationRequestErrorXML =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
          "  <!DOCTYPE svc_result SYSTEM \"MLP_v3_4.dtd\">" +
          "  <svc_result xmlns=\"MLP_v3_4.dtd\" ver=\"3.4.0\">\n" +
          "  <tlra ver=\"3.4.0\">\n" +
          "    <result resid=\"1\">SYSTEM FAILURE</result>\n" +
          "    <add_info>" + exceptionError + "</add_info>\n" +
          "  </tlra>\n" +
          "</svc_result>";

  /**
   * Get the MLP result code as per OMA MLP section 5.4.1
   *
   * @param mlpResultType MLPResultType internal enum value for use in the MLP classes
   * @return MLP result code to return to the client
   */
  public static String getResultCodeForType(MLPResultType mlpResultType) {

    /*
    0 - 99 Location server specific errors
    0	OK	                        No error occurred while processing the request.
    1	SYSTEM FAILURE	            The request can not be handled because of a general problem in the location server.
    2	UNSPECIFIED ERROR	        An unspecified error used in case none of the other errors apply. This can also be used in case privacy issues prevent certain errors from being presented.
    3	UNAUTHORIZED APPLICATION	The requesting location-based application is not allowed to access the location server or a wrong password has been supplied.
    4	UNKNOWN SUBSCRIBER	        The user is unknown, i.e. no such subscription exists.
    5	ABSENT SUBSCRIBER	        The user is currently not reachable.
    6	POSITION METHOD FAILURE	    The location service fails to obtain the user's position via the positioning method used.
                                    The exact cause may be indicated in ADD_INFO by the inclusion of an event code (A, B, C etc.)
                                    from the list below:
                                    A: Target does not support SUPL.
                                    B: SUPL Positioning Failure - Target does not support requested service. For example a SUPL 1.0 device doesn't support periodic trigger service.
                                    C: SUPL Positioning Failure - Target fails to deliver Cell Info.
                                    D: SUPL Positioning Failure - both Cell ID location and GNSS positioning fail.
    7	TIMEOUT	                    Timer expiry for the requested event trigger

    100 - 199 Request specific errors
    101	CONGESTION IN LOCATION SERVER	The request can not be handled due to congestion in the location server.
    103	UNSUPPORTED VERSION	            The Location server does not support the indicated protocol version.
    104	TOO MANY POSITION ITEMS	        Too many position items have been specified in the request.
    105	FORMAT ERROR	                A protocol element in the request has invalid format. The invalid element is indicated in ADD_INFO.
    106	SYNTAX ERROR	                The position request has invalid syntax. Details may be indicated in ADD_INFO.
    107	PROTOCOL ELEMENT NOT SUPPORTED	A protocol element specified in the position request is not supported by the Location Server, or the position result is not supported by the LCS Client. The element is indicated in ADD_INFO.
    108	SERVICE NOT SUPPORTED	        The requested service is not supported in the Location Server. The service is indicated in ADD_INFO.
    109	PROTOCOL ELEMENT ATTRIBUTE NOT SUPPORTED	A protocol element attribute is not supported in the Location Server. The attribute is indicated in ADD_INFO.
    110	INVALID PROTOCOL ELEMENT VALUE	            A protocol element in the request has an invalid value. The element is indicated in ADD_INFO.
    111	INVALID PROTOCOL ELEMENT ATTRIBUTE VALUE	A protocol element attribute in the request has a wrong value. The element is indicated in ADD_INFO.
    112	PROTOCOL ELEMENT VALUE NOT SUPPORTED	    A specific value of a protocol element is not supported in the Location Server. The element and value are indicated in ADD_INFO.
    113	PROTOCOL ELEMENT ATTRIBUTE VALUE NOT SUPPORTED	A specific value of a protocol element attribute is not supported in the Location Server. The attribute and value are indicated in ADD_INFO.
    114	CANCELLATION OF TRIGGERED LOCATION REQUEST	The requested triggered location report is cancelled.
    115	INVALID MSID IN TLRSR	                    One or more msid elements in the Triggered Location Reporting Stop Request are not valid to the Location Server.
    116	TLRSR FOR INDIVIDUAL TARGET NOT SUPPORTED	The function of stopping triggered location reporting for individual target(s) is not supported in Location Server.

    200 - 299 Network specific errors
    201	QOP NOT ATTAINABLE	            The requested QoP cannot be provided. The exact QoP parameter which cannot be provided, i.e. accuracy, response time or max_loc_age, and value are indicated in ADD_INFO.
    202	POSITIONING NOT ALLOWED	        The subscriber does not allow the application to position him/her for whatever reason (privacy settings in location server, LCS privacy class).
    203	CONGESTION IN MOBILE NETWORK	The request can not be handled due to congestion in the mobile network.
    204	DISALLOWED BY LOCAL REGULATIONS	The location request is disallowed by local regulatory requirements.
    207	MISCONFIGURATION OF LOCATION SERVER	The location server is not completely configured to be able to calculate a position.
    208	TARGET MOVED TO NEW MSC/SGSN	The triggered Location Request has been aborted due to that target has moved to another MSC/SGSN. This result code shall only be used towards The Home Location Server. Restrictions:
                                        - This code SHALL only be used in RLP.
                                        - This result code shall only be used towards The Home Location Server.

    300 - 499 Reserved for future use

    500 - 599 Vendor specific errors

    600 - 699 MLS Client specific errors
    601	STANDARD LOCATION REPORT SERVICE NOT SUPPORTED	The MLS Client does not support the standard location report service.
    602	MLS CLIENT ERROR	                            An error occurred in the MLS Client.
    603	STANDARD LOCATION REPORT SERVICE NOT ACCEPTED	The standard location report was not accepted by the MLS Client.
    604	SUBSCRIBER IN STANDARD LOCATION REPORT SERVICE NOT VALID  The subscriber in the Standard Location Report is not valid to the MLS Client.
    605	INVALID SERVICE ID IN STANDARD LOCATION REPORT SERVICE	  The service identity in the Standard Location Report is not valid to the MLS Client.
     */

    switch (mlpResultType) {
      case OK:
        return "0";
      case UNSPECIFIED_ERROR:
        return "2";
      case UNAUTHORIZED_APPLICATION:
        return "3";
      case UNKNOWN_SUBSCRIBER:
        return "4";
      case ABSENT_SUBSCRIBER:
        return "5";
      case POSITION_METHOD_FAILURE:
        return "6";
      case TIMEOUT:
        return "7";
      case CONGESTION_IN_LOCATION_SERVER:
        return "101";
      case UNSUPPORTED_VERSION:
        return "103";
      case TOO_MANY_POSITION_ITEMS:
        return "104";
      case FORMAT_ERROR:
        return "105";
      case SYNTAX_ERROR:
        return "106";
      case PROTOCOL_ELEMENT_NOT_SUPPORTED:
        return "107";
      case SERVICE_NOT_SUPPORTED:
        return "108";
      case PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED:
        return "109";
      case INVALID_PROTOCOL_ELEMENT_VALUE:
        return "110";
      case INVALID_PROTOCOL_ELEMENT_ATTRIBUTE_VALUE:
        return "111";
      case PROTOCOL_ELEMENT_VALUE_NOT_SUPPORTED:
        return "112";
      case PROTOCOL_ELEMENT_ATTRIBUTE_VALUE_NOT_SUPPORTED:
        return "113";
      case CANCELLATION_OF_TRIGGERED_LOCATION_REQUEST:
        return "114";
      case INVALID_MSID_IN_TLRSR:
        return "115";
      case TLRSR_FOR_INDIVIDUAL_TARGET_NOT_SUPPORTED:
        return "116";
      case QOP_NOT_ATTAINABLE:
        return "201";
      case POSITIONING_NOT_ALLOWED:
        return "202";
      case CONGESTION_IN_MOBILE_NETWORK:
        return "203";
      case DISALLOWED_BY_LOCAL_REGULATIONS:
        return "204";
      case MISCONFIGURATION_OF_LOCATION_SERVER:
        return "207";
      case TARGET_MOVED_TO_NEW_MSC_SGSN:
        return "208";
      case STANDARD_LOCATION_REPORT_SERVICE_NOT_SUPPORTED:
        return "601";
      case MLS_CLIENT_ERROR:
        return "602";
      case STANDARD_LOCATION_REPORT_SERVICE_NOT_ACCEPTED:
        return "603";
      case SUBSCRIBER_IN_STANDARD_LOCATION_REPORT_SERVICE_NOT_VALID:
        return "604";
      case INVALID_SERVICE_ID_IN_STANDARD_LOCATION_REPORT_SERVICE:
        return "605";
      default:
        return "1"; // contains SYSTEM_FAILURE case
    }
  }

  /**
   * Get the MLP result string as per OMA MLP section 5.4.1
   *
   * @param mlpResultType MLPResultType internal enum value for use in the MLP classes
   * @return MLP result string to return to the client
   */
  public static String getResultStringForType(MLPResultType mlpResultType) {
    switch (mlpResultType) {
      case OK:
        return "OK";
      case UNSPECIFIED_ERROR:
        return "UNSPECIFIED ERROR";
      case UNAUTHORIZED_APPLICATION:
        return "UNAUTHORIZED APPLICATION";
      case UNKNOWN_SUBSCRIBER:
        return "UNKNOWN SUBSCRIBER";
      case ABSENT_SUBSCRIBER:
        return "ABSENT SUBSCRIBER";
      case POSITION_METHOD_FAILURE:
        return "POSITION METHOD FAILURE";
      case TIMEOUT:
        return "TIMEOUT";
      case CONGESTION_IN_LOCATION_SERVER:
        return "CONGESTION IN LOCATION SERVER";
      case UNSUPPORTED_VERSION:
        return "UNSUPPORTED VERSION";
      case TOO_MANY_POSITION_ITEMS:
        return "TOO MANY POSITION ITEMS";
      case FORMAT_ERROR:
        return "FORMAT ERROR";
      case SYNTAX_ERROR:
        return "SYNTAX ERROR";
      case PROTOCOL_ELEMENT_NOT_SUPPORTED:
        return "PROTOCOL ELEMENT NOT SUPPORTED";
      case SERVICE_NOT_SUPPORTED:
        return "SERVICE NOT SUPPORTED";
      case PROTOCOL_ELEMENT_ATTRIBUTE_NOT_SUPPORTED:
        return "PROTOCOL ELEMENT ATTRIBUTE NOT SUPPORTED";
      case INVALID_PROTOCOL_ELEMENT_VALUE:
        return "INVALID PROTOCOL ELEMENT VALUE";
      case INVALID_PROTOCOL_ELEMENT_ATTRIBUTE_VALUE:
        return "INVALID PROTOCOL ELEMENT ATTRIBUTE VALUE";
      case PROTOCOL_ELEMENT_VALUE_NOT_SUPPORTED:
        return "PROTOCOL ELEMENT VALUE NOT SUPPORTED";
      case PROTOCOL_ELEMENT_ATTRIBUTE_VALUE_NOT_SUPPORTED:
        return "PROTOCOL ELEMENT ATTRIBUTE VALUE NOT SUPPORTED";
      case CANCELLATION_OF_TRIGGERED_LOCATION_REQUEST:
        return "CANCELLATION OF TRIGGERED LOCATION REQUEST";
      case INVALID_MSID_IN_TLRSR:
        return "INVALID MSID IN TLRSR";
      case TLRSR_FOR_INDIVIDUAL_TARGET_NOT_SUPPORTED:
        return "TLRSR FOR INDIVIDUAL TARGET NOT SUPPORTED";
      case QOP_NOT_ATTAINABLE:
        return "QOP NOT ATTAINABLE";
      case POSITIONING_NOT_ALLOWED:
        return "POSITIONING NOT ALLOWED";
      case CONGESTION_IN_MOBILE_NETWORK:
        return "CONGESTION IN MOBILE NETWORK";
      case DISALLOWED_BY_LOCAL_REGULATIONS:
        return "DISALLOWED BY LOCAL REGULATIONS";
      case MISCONFIGURATION_OF_LOCATION_SERVER:
        return "MISCONFIGURATION OF LOCATION SERVER";
      case TARGET_MOVED_TO_NEW_MSC_SGSN:
        return "TARGET MOVED TO NEW MSC/SGSN";
      case STANDARD_LOCATION_REPORT_SERVICE_NOT_SUPPORTED:
        return "STANDARD LOCATION REPORT SERVICE NOT SUPPORTED";
      case MLS_CLIENT_ERROR:
        return "MLS CLIENT ERROR";
      case STANDARD_LOCATION_REPORT_SERVICE_NOT_ACCEPTED:
        return "STANDARD LOCATION REPORT SERVICE NOT ACCEPTED";
      case SUBSCRIBER_IN_STANDARD_LOCATION_REPORT_SERVICE_NOT_VALID:
        return "SUBSCRIBER IN STANDARD LOCATION REPORT SERVICE NOT VALID";
      case INVALID_SERVICE_ID_IN_STANDARD_LOCATION_REPORT_SERVICE:
        return "INVALID SERVICE ID IN STANDARD LOCATION REPORT SERVICE";
      default:
        return "SYSTEM FAILURE"; // contains SYSTEM_FAILURE case
    }
  }

  /**
   * Is this error type a system error?
   *
   * @param mlpResultType MLPResultType internal enum value for use in the MLP classes
   * @return boolean true if it is a system error
   */
  public static boolean isSystemError(MLPResultType mlpResultType) {
    switch (mlpResultType) {
      case SYSTEM_FAILURE:
      case UNSPECIFIED_ERROR:
        return true;
      default:
        return false;
    }
  }

  /*
   * Use JAXB marshalling to generate the MLP XML result data for a single successful position look-up
   *
   *
   * @return String XML result to return to client
   * Example usage:
   * svcResultXml = MLPResponse.getSinglePositionSuccessXML("27 28 25.00S", "153 01 43.00E", "+1000", "20140507082957", "61307341370");
   * Example result based on above usage:
   * <?xml version="1.0" encoding="UTF-8"?>
   * <!DOCTYPE svc_result SYSTEM "MLP_SVC_RESULT_320.DTD">
   * <svc_result xmlns="MLP_SVC_RESULT_320.DTD" ver="3.2.0">
   * <slia ver="3.4.0">
   *         <pos>
   *             <msid>59899077937</msid>
   *             <imei>011714004661057</imei>
   *             <pd>
   *                 <time utc_off="-0300">20160828181421</time>
   *                 <plmn>
   *                     <mcc>748</mcc>
   *                     <mnc>21</mnc>
   *                 </plmn>
   *                 <gsm_net_param>
   *                     <cgi>
   *                         <mcc>748</mcc>
   *                         <mnc>21</mnc>
   *                         <lac>32000</lac>
   *                         <cellid>38221</cellid>
   *                     </cgi>
   *                     <neid>
   *                         <vlrid>
   *                             <vlrno>59899000231</vlrno>
   *                         </vlrid>
   *                     </neid>
   *                 </gsm_net_param>
   *                 <geo_info>
   *                     <CoordinateReferenceSystem>
   *                 	    <Identifier>
   *                 		    <code>4326</code>
   *                 			<codeSpace>EPSG</codeSpace>
   *                 			<edition>6.1</edition>
   *                 		</Identifier>
   *                 	</CoordinateReferenceSystem>
   *                 	<shape>
   *                 		<CircularArea>
   *                 		    <coord>
   *                 			    <X>44 43 15.66S</X>
   *                 			    <Y>105 59 36.28E</Y>
   *                 			</coord>
   *                 		    <radius>76</radius>
   *                 		</CircularArea>
   *                 	</shape>
   *                 </geo_info>
   *             </pd>
   *         </pos>
   *     </slia>
   * </svc_result>
   */
  public String getCoreNetworkSinglePositionXML(String operation, String typeOfShape, Double x, Double y, Double radius, Double semiMajor,
                                                Double semMinor, Double angle, Double arcStartAngle, Double arcStopAngle, Integer altitude, Double uncertaintyAltitude,
                                                org.mobicents.gmlc.slee.primitives.Polygon polygon, Integer polygonPointsAmount, String posMethod,
                                                Integer horizontalSpeed, Integer bearing, Integer verticalSpeed,
                                                Integer uncertaintyHorizontalSpeed, Integer uncertaintyVerticalSpeed, String velocityType,
                                                Integer mcc, Integer mnc, Integer lac, Integer ci, Integer sac, Long uci,
                                                Long eci, Long enbId, Integer rac, Integer tac, Long nci, Integer nrTac,
                                                String mmeName, String mmeRealm, String sgsnName, String sgsnRealm, String sgsnNumber, String amfAddress,
                                                String mscNumber, String vlrNumber, String msid, String imei, String imsi, Integer age, String lmsi,
                                                Integer clientTransId, Integer lcsRefNumber, Integer ratType, String civicAddress,
                                                MLPResultType mlpResultType, Boolean mlpTriggeredReportingService, Boolean isReport) {

    // Generate XML response
    String svcResultXml;

    try {
      // Eventually this timestamp should be replaced by the actual network position time
      Date requestTime = new Date();
      String date = new SimpleDateFormat("yyyyMMddHHmmss").format(requestTime);
      String utcOffset = new SimpleDateFormat("Z").format(requestTime);

      // Generate the response XML
      svcResultXml = this.generateSinglePositionSuccessXML(operation, typeOfShape,
          x, y, radius, semiMajor, semMinor, angle, arcStartAngle, arcStopAngle, altitude, uncertaintyAltitude,
          polygon, polygonPointsAmount, posMethod,
          horizontalSpeed, bearing, verticalSpeed, uncertaintyHorizontalSpeed, uncertaintyVerticalSpeed, velocityType,
          utcOffset, date, msid,
          mcc, mnc, lac, ci, sac, uci, eci, enbId, rac, tac, nci, nrTac,
          mmeName, mmeRealm, sgsnName, sgsnRealm, sgsnNumber, amfAddress, mscNumber, vlrNumber,
          imei, imsi, age, lmsi, clientTransId, lcsRefNumber, ratType, civicAddress,
          mlpResultType, mlpTriggeredReportingService, isReport);

    } catch (IllegalArgumentException e) {
      // Generate the error XML
      svcResultXml = this.getSystemErrorResponseXML(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Failed to create request timestamp", mlpTriggeredReportingService);
    } catch (IOException e) {
      // Generate the error XML
      svcResultXml = this.getSystemErrorResponseXML(MLPResponse.MLPResultType.SYSTEM_FAILURE, "IO failure generating XML", mlpTriggeredReportingService);
    } catch (JAXBException e) {
      // Generate the error XML
      svcResultXml = this.getSystemErrorResponseXML(MLPResponse.MLPResultType.SYSTEM_FAILURE, "Failed to generate XML response from internal objects", mlpTriggeredReportingService);

    }

    return svcResultXml;
  }

  /**
   * Internal XML generation support function for above getSs7SinglePositionSuccess7XML() when operation is ATI or PSI
   *
   * @param x                   X coordinate in WGS84 DMS format
   * @param y                   Y coordinate in WGS84 DMS format
   * @param radius              Position radius in meters (e.g. 5000 for 5km of accuracy)
   * @param semiMajor           Length of semi-major axis, oriented at angle A (0 to 180º) measured clockwise from North
   * @param semiMinor           Length of semi-minor axis, oriented at angle A (0 to 180º) measured clockwise from North
   * @param angle               Angle A (0 to 180°) measured clockwise from north and a semi-minor axis
   * @param arcStartAngle       Angle (in angularUnit) between North and the first defined radius
   * @param arcStopAngle        Angle (in angularUnit) between the first and second defined radius
   * @param altitude            Altitude of the location estimate
   * @param uncertaintyAltitude Uncertainty of the altitude of the location estimate
   * @param polygon             Array containing location estimate coordinates for each point of the polygon
   * @param polygonPointsAmount Amount of points of the polygon
   * @param positioningMethod   The positioning method used to obtain the Position
   * @param horizontalSpeed     The estimated horizontal speed
   * @param bearing             The bearing of the velocity estimate
   * @param verticalSpeed       The estimated vertical speed
   * @param uncertaintyHorSpeed The uncertainty of the estimated horizontal speed
   * @param uncertaintyVerSpeed The uncertainty of the estimated vertical speed
   * @param velocityType        The velocity estimate type
   * @param utcOffSet           UTC offset for location timestamp in "[+/-]HHmm" format
   * @param date                Location timestamp at above UTC offset in "yyyyMMddHHmmss" format
   * @param msisdn              MSISDN
   * @param mcc                 Mobile Country Code
   * @param mnc                 Mobile Network Code
   * @param lac                 Location Area Code
   * @param ci                  Cell Id
   * @param sac                 Service Area Code
   * @param uci                 UTRAN Cell Id
   * @param eci                 LTE Cell Id
   * @param tac                 LTE Tracking Area Code
   * @param nci                 5GS NR Cell Id
   * @param nrTac               5GS NR Tracking Area Code
   * @param mmeName             Mobility Management Entity (MME) Diameter host name
   * @param mmeRealm            Mobility Management Entity (MME) Diameter realm
   * @param sgsnName            Serving GPRS Support Node (SGSN) Diameter host name
   * @param sgsnRealm           Serving GPRS Support Node (SGSN) Diameter realm
   * @param sgsnNumber          Serving GPRS Support Node (SGSN) number (E.164/E.214 digits, Global Title)
   * @param amfAddress          Access and Mobility Management Function (AMF) fully qualified domain name
   * @param mscNumber           Mobile Switching Center number (E.164/E.214 digits, Global Title)
   * @param vlrNumber           Visitor Location Register number (E.164/E.214 digits, Global Title)
   * @param imei                International Mobile Equipment Identity
   * @param imsi                International Mobile Subscriber Identity
   * @param age                 Age of Location
   * @param lmsi                Location Mobile Subscriber Identity
   * @param clientTransId       Transaction Identity of the GMLC client
   * @param lcsReferenceNumber  LCS Reference Number parameter of the PSL invoke
   * @param civicAddress        The civic address gathered by the location request
   * @param mlpResultType       MLPResultType internal enum value for use in the MLP classes
   * @return                    String XML result to return to client
   * @throws IOException        IO error occurred while generating the XML result
   * @throws JAXBException      JAXB error occurred while generating the XML result
   */
  private String generateSinglePositionSuccessXML(String operation, String typeOfShape , Double x, Double y, Double radius, Double semiMajor,
                                                  Double semiMinor, Double angle, Double arcStartAngle, Double arcStopAngle, Integer altitude, Double uncertaintyAltitude,
                                                  org.mobicents.gmlc.slee.primitives.Polygon polygon, Integer polygonPointsAmount, String positioningMethod,
                                                  Integer horizontalSpeed, Integer bearing, Integer verticalSpeed,
                                                  Integer uncertaintyHorSpeed, Integer uncertaintyVerSpeed, String velocityType,
                                                  String utcOffSet, String date, String msisdn, Integer mcc, Integer mnc, Integer lac,
                                                  Integer ci, Integer sac, Long uci, Long eci, Long enbId, Integer rac, Integer tac, Long nci, Integer nrTac,
                                                  String mmeName, String mmeRealm, String sgsnName, String sgsnRealm, String sgsnNumber, String amfAddress,
                                                  String mscNumber, String vlrNumber, String imei, String imsi, Integer age, String lmsi,
                                                  Integer clientTransId, Integer lcsReferenceNumber, Integer ratType, String civicAddress,
                                                  MLPResultType mlpResultType, Boolean mlpTriggeredReportingService, Boolean isReport) throws  IOException, JAXBException {

    String lXml;
    String ver = "3.4.0";

    // Create the objects we'll surely use to generate our svc_result XML
    SvcResult mlpSvcResult = new SvcResult();
    Slia mlpSlia = new Slia();
    Tlra mlpTlra = new Tlra();
    Tlrep mlpTlrep = new Tlrep();
    Slrep mlpSlrep = new Slrep();
    Pos mlpPos = new Pos();
    Msid mlpMsisdn = new Msid();
    Msid mlpImsi = new Msid();
    Msid mlpImei = new Msid();
    Pd mlpPd = new Pd();
    List<Pos> posList = new ArrayList<>();
    GsmNetParam mlpGsmNetParam = new GsmNetParam();
    Cgi mlpCgi = new Cgi();
    Neid mlpNeid = new Neid();
    Vmscid mlpVmscId = new Vmscid();
    Vlrid mlpVlrId = new Vlrid();
    ExtensionParams mlpExtensionParams = new ExtensionParams();
    Result mlpResult = new Result();

    try {
      // Setup all objects for this single position
      /** Msid object for Pos **/
      if (msisdn != null) {
        mlpMsisdn.setType("MSISDN");
        mlpMsisdn.setContent(msisdn);
        mlpPos.setMsid(mlpMsisdn);
      } else if (imsi != null) {
        mlpImsi.setType("IMSI");
        mlpImsi.setContent(imsi);
        mlpPos.setMsid(mlpImsi);
      } else if (imei != null) {
        mlpImei.setType("IMEI");
        mlpImei.setContent(imei);
        mlpPos.setMsid(mlpImei);
      }
      /** Pd object for Pos **/
      // Time object for Pd
      if (utcOffSet != null || date != null) {
        Time mlpTime = new Time();
        mlpTime.setUtcOff(utcOffSet);
        mlpTime.setContent(date);
        mlpPd.setTime(mlpTime);
      }
      /** Shape object for Pd **/
      if (typeOfShape != null) {
        Shape mlpShape = new Shape();
        Coord mlpCoord = new Coord();
        if (typeOfShape.equalsIgnoreCase("EllipsoidPoint")) {
          Point mlpPoint = new Point();
          mlpCoord.setX(String.valueOf(x));
          mlpCoord.setY(String.valueOf(y));
          mlpPoint.setCoord(mlpCoord);
          mlpShape.setPoint(mlpPoint);
        } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyCircle")) {
          CircularArea mlpCircularArea = new CircularArea();
          mlpCoord.setX(String.valueOf(x));
          mlpCoord.setY(String.valueOf(y));
          mlpCircularArea.setCoord(mlpCoord);
          if (radius != null) {
            mlpCircularArea.setRadius(String.valueOf(radius));
          }
          mlpShape.setCircularArea(mlpCircularArea);
          if (operation.equalsIgnoreCase("ATI") || operation.equalsIgnoreCase("PSI")) {
            // This is a strange condition that sometimes happens in live networks for MAP ATI and MAP PSI,
            // where both latitude and longitude are given as 0.0 which is obviously a fake coordinate
            // For such case, we set the shape to NULL
            if (x != null && y != null) {
              if (x == 0.0 && y == 0.0) {
                logger.warning("Received x == 0.0 && y == 0.0 for EllipsoidPointWithUncertaintyCircle type from SS7 MAP " + operation +
                    " while setting the shape for MLP SLIA");
                mlpShape = null;
              }
            }
          }
        } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithUncertaintyEllipse")) {
          EllipticalArea mlpEllipticalArea = new EllipticalArea();
          mlpCoord.setX(String.valueOf(x));
          mlpCoord.setY(String.valueOf(y));
          mlpEllipticalArea.setCoord(mlpCoord);
          mlpEllipticalArea.setSemiMajor(String.valueOf(semiMajor));
          mlpEllipticalArea.setSemiMinor(String.valueOf(semiMinor));
          mlpEllipticalArea.setAngle(String.valueOf(angle));
          mlpShape.setEllipticalArea(mlpEllipticalArea);
          // confidence not contemplated in MLP 3.4 EllipticalArea
        } else if (typeOfShape.equalsIgnoreCase("EllipsoidPointWithAltitudeAndUncertaintyEllipsoid")) {
          EllipticalArea mlpEllipticalArea = new EllipticalArea();
          mlpCoord.setX(String.valueOf(x));
          mlpCoord.setY(String.valueOf(y));
          mlpEllipticalArea.setCoord(mlpCoord);
          mlpEllipticalArea.setSemiMajor(String.valueOf(semiMajor));
          mlpEllipticalArea.setSemiMinor(String.valueOf(semiMinor));
          mlpEllipticalArea.setAngle(String.valueOf(angle));
          mlpShape.setEllipticalArea(mlpEllipticalArea);
          // altitude and confidence not contemplated in MLP 3.4 EllipticalArea
        } else if (typeOfShape.equalsIgnoreCase("EllipsoidArc")) {
          CircularArcArea mlpCircularArcArea = new CircularArcArea();
          mlpCoord.setX(String.valueOf(x));
          mlpCoord.setY(String.valueOf(y));
          mlpCircularArcArea.setCoord(mlpCoord);
          mlpCircularArcArea.setInRadius(String.valueOf(radius));
          mlpCircularArcArea.setStartAngle(String.valueOf(arcStartAngle));
          mlpCircularArcArea.setStopAngle(String.valueOf(arcStopAngle));
          mlpShape.setCircularArcArea(mlpCircularArcArea);
          // confidence not contemplated in MLP 3.4 EllipticalArea
        } else if (typeOfShape.equalsIgnoreCase("Polygon")) {
          Polygon mlpPolygon = new Polygon();
          LinearRing linearRing = new LinearRing();
          OuterBoundaryIs outerBoundaryIs = new OuterBoundaryIs();
          double lat, pointLatitude, lon, pointLongitude;
          String formattedLatitude, formattedLongitude;
          Double[][] polygonArray = new Double[polygonPointsAmount][polygonPointsAmount];
          if (polygonPointsAmount > 2 && polygonPointsAmount <= 15) {
            for (int index = 0; index < polygonPointsAmount; index++) {
              lat = polygon.getEllipsoidPoint(index).getLatitude();
              lon = polygon.getEllipsoidPoint(index).getLongitude();
              polygonArray[index][0] = lat;
              polygonArray[index][1] = lon;
            }
            for (int point = 0; point < polygonArray.length; point++) {
              Coord polygonCoord = new Coord();
              pointLatitude = polygonArray[point][0];
              pointLongitude = polygonArray[point][1];
              formattedLatitude = coordinatesFormat.format(pointLatitude);
              formattedLongitude = coordinatesFormat.format(pointLongitude);
              polygonCoord.setX(formattedLatitude);
              polygonCoord.setY(formattedLongitude);
              linearRing.getContent().add(polygonCoord);
            }
            outerBoundaryIs.setLinearRing(linearRing);
            mlpPolygon.setOuterBoundaryIs(outerBoundaryIs);
          }
          mlpShape.setPolygon(mlpPolygon);
        }
        mlpPd.setShape(mlpShape);
        if (altitude != null) {
          mlpPd.setAlt(String.valueOf(altitude));
          if (uncertaintyAltitude != null) {
            mlpPd.setAltUnc(String.valueOf(uncertaintyAltitude));
          }
        }
        if (horizontalSpeed != null) {
          mlpPd.setSpeed(String.valueOf(horizontalSpeed));
          if (uncertaintyHorSpeed != null) {
            mlpPd.setAltUnc(String.valueOf(uncertaintyHorSpeed));
          }
        }
      }
      /** CivicLoc for Pd **/
      if (civicAddress != null) {
        Civicloc mlpCivicloc = new Civicloc();
        CiviclocElement mlpCiviclocElement;
        CivicAddressXmlReader reader = new CivicAddressXmlReader();
        reader.civicAddressXMLReader(civicAddress);
        CivicAddressElements civicAddressElements = reader.getCivicAddressElements();
        if (civicAddressElements != null) {
          if (civicAddressElements.getCountry() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getCountry());
            mlpCiviclocElement.setElementType("COUNTRY");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getA1() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getA1());
            mlpCiviclocElement.setElementType("A1");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getA2() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getA2());
            mlpCiviclocElement.setElementType("A2");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getA3() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getA3());
            mlpCiviclocElement.setElementType("A3");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getA4() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getA4());
            mlpCiviclocElement.setElementType("A4");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getA5() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getA5());
            mlpCiviclocElement.setElementType("A5");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getA6() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getA6());
            mlpCiviclocElement.setElementType("A6");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPrm() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPrm());
            mlpCiviclocElement.setElementType("PRM");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPrd() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPrd());
            mlpCiviclocElement.setElementType("PRD");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getRd() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getRd());
            mlpCiviclocElement.setElementType("RD");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getSts() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getSts());
            mlpCiviclocElement.setElementType("STS");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPrd() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPrd());
            mlpCiviclocElement.setElementType("PRD");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPom() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPom());
            mlpCiviclocElement.setElementType("POM");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getRdsec() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getRdsec());
            mlpCiviclocElement.setElementType("RDSEC");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getRdbr() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getRdbr());
            mlpCiviclocElement.setElementType("RDBR");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getRdsubbr() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getRdsubbr());
            mlpCiviclocElement.setElementType("RDSUBBR");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getHno() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getHno());
            mlpCiviclocElement.setElementType("HNO");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getHns() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getHns());
            mlpCiviclocElement.setElementType("HNS");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getLmk() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getLmk());
            mlpCiviclocElement.setElementType("LMK");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getLoc() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getLoc());
            mlpCiviclocElement.setElementType("LOC");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getFlr() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getFlr());
            mlpCiviclocElement.setElementType("FLR");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getNam() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getNam());
            mlpCiviclocElement.setElementType("NAM");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPc() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPc());
            mlpCiviclocElement.setElementType("PC");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getBld() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getBld());
            mlpCiviclocElement.setElementType("BLD");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getUnit() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getUnit());
            mlpCiviclocElement.setElementType("UNIT");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getRoom() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getRoom());
            mlpCiviclocElement.setElementType("ROOM");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getSeat() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getSeat());
            mlpCiviclocElement.setElementType("SEAT");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPlc() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPlc());
            mlpCiviclocElement.setElementType("PLC");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPcn() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPcn());
            mlpCiviclocElement.setElementType("PCN");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPobox() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPobox());
            mlpCiviclocElement.setElementType("POBOX");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getPn() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getPn());
            mlpCiviclocElement.setElementType("PN");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getMp() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getMp());
            mlpCiviclocElement.setElementType("MP");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getStp() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getStp());
            mlpCiviclocElement.setElementType("STP");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
          if (civicAddressElements.getHnp() != null) {
            mlpCiviclocElement = new CiviclocElement();
            mlpCiviclocElement.setContent(civicAddressElements.getHnp());
            mlpCiviclocElement.setElementType("HNP");
            mlpCivicloc.getCiviclocElement().add(mlpCiviclocElement);
          }
        }
        mlpPd.setCivicloc(mlpCivicloc);
      }

      /** GsmNetParam setting for object Pos and Cgi **/
      if (lac != null && sac == null) {
        mlpCgi.setMcc(String.valueOf(mcc));
        mlpCgi.setMnc(String.valueOf(mnc));
        mlpCgi.setLac(String.valueOf(lac));
        if (ci != null) {
          mlpCgi.setCellid(String.valueOf(ci));
        }
        mlpGsmNetParam.setCgi(mlpCgi);
      }
      if (mscNumber != null || vlrNumber != null) {
        if (mscNumber != null) {
          mlpVmscId.setVmscno(mscNumber);
          mlpNeid.getContent().add(mlpVmscId);
        }
        if (vlrNumber != null) {
          mlpVlrId.setVlrno(vlrNumber);
          mlpNeid.getContent().add(mlpVlrId);
        }
        mlpGsmNetParam.setNeid(mlpNeid);
      }
      if (imsi != null) {
        if (!imsi.isEmpty()) {
          mlpGsmNetParam.setImsi(imsi);
        }
      }
      if (lmsi != null) {
        if (!lmsi.isEmpty()) {
          mlpGsmNetParam.setLmsi(lmsi);
        }
      }
      if (!isReport) {
        if (mlpCgi.getLac() != null || mscNumber != null || vlrNumber != null || imsi != null || lmsi != null) {
          // Add the gsmNetParams to Pos for SLIA
          mlpPos.setGsmNetParam(mlpGsmNetParam);
        }
      } else {
        if (mlpTriggeredReportingService) {
          if (mlpCgi.getLac() != null || mscNumber != null || vlrNumber != null) {
            // Add the Neid to ExtensionParams for TLREP and to GsmNetParams for Pos in SLREP
            mlpExtensionParams.setCgi(mlpCgi);
            if (mscNumber != null || vlrNumber != null) {
              if (mscNumber != null) {
                mlpVmscId.setVmscno(mscNumber);
                mlpNeid.getContent().add(mlpVmscId);
              }
              if (vlrNumber != null) {
                mlpVlrId.setVlrno(vlrNumber);
                mlpNeid.getContent().add(mlpVlrId);
              }
              mlpGsmNetParam.setNeid(mlpNeid);
              mlpPos.setGsmNetParam(mlpGsmNetParam);
            }
          }
        } else {
          if (mlpCgi.getLac() != null || mscNumber != null || vlrNumber != null || imsi != null || lmsi != null) {
            // Add the gsmNetParams to Pos for SLREP
            mlpPos.setGsmNetParam(mlpGsmNetParam);
          }
        }
      }

      // Extension params
      if (msisdn != null || imsi != null || imei != null) {
        MsisdsExt msids = new MsisdsExt();
        List<Msid> msidList = new ArrayList<>();
        if (msisdn != null) {
          mlpMsisdn.setType("MSISDN");
          mlpMsisdn.setContent(msisdn);
          msidList.add(mlpMsisdn);
        }
        if (imsi != null) {
          mlpImsi.setType("IMSI");
          mlpImsi.setContent(imsi);
          msidList.add(mlpImsi);
        }
        if (imei != null) {
          mlpImei.setType("IMEI");
          mlpImei.setContent(imei);
          msidList.add(mlpImei);
        }
        msids.setMsidList(msidList);
        mlpExtensionParams.setMsidsExt(msids);
      }
      if (horizontalSpeed != null || verticalSpeed != null) {
        VelocityEstimate velocityEstimate = new VelocityEstimate();
        if (horizontalSpeed != null) {
          velocityEstimate.setHorizontalSpeed(String.valueOf(horizontalSpeed));
          if (uncertaintyHorSpeed != null) {
            velocityEstimate.setUncertaintyHorizontalSpeed(String.valueOf(uncertaintyHorSpeed));
          }
          if (verticalSpeed != null) {
            velocityEstimate.setVerticalSpeed(String.valueOf(verticalSpeed));
            if (uncertaintyVerSpeed != null) {
              velocityEstimate.setUncertaintyVerticalSpeed(String.valueOf(uncertaintyVerSpeed));
            }
          }
        }
        if (velocityType != null) {
          velocityEstimate.setVelocityType(String.valueOf(velocityType));
        }
        if (bearing != null) {
          velocityEstimate.setBearing(String.valueOf(bearing));
        }
        mlpExtensionParams.setVelocityEstimate(velocityEstimate);
      }
      if (lac != null && sac == null) {
        if (mlpGsmNetParam.getCgi() != null) {
          if (mlpGsmNetParam.getCgi().getLac().isEmpty()) {
            mlpCgi.setMcc(String.valueOf(mcc));
            mlpCgi.setMnc(String.valueOf(mnc));
            mlpCgi.setLac(String.valueOf(lac));
            if (ci != null) {
              mlpCgi.setCellid(String.valueOf(ci));
            }
            mlpExtensionParams.setCgi(mlpCgi);
          }
        }
      } else if (sac != null) {
        Sai mlpSai = new Sai();
        mlpSai.setMcc(String.valueOf(mcc));
        mlpSai.setMnc(String.valueOf(mnc));
        mlpSai.setLac(String.valueOf(lac));
        mlpSai.setSac(String.valueOf(sac));
        mlpExtensionParams.setSai(mlpSai);
      }
      if (rac != null) {
        Rai rai = new Rai();
        rai.setMcc(String.valueOf(mcc));
        rai.setMnc(String.valueOf(mnc));
        rai.setRac(String.valueOf(rac));
        mlpExtensionParams.setRai(rai);
      }
      // Cgi and Sai already set before for ExtensionParams in this method
      if (uci != null){
        UtranCgi ucgi = new UtranCgi();
        ucgi.setMcc(String.valueOf(mcc));
        ucgi.setMnc(String.valueOf(mnc));
        ucgi.setUci(String.valueOf(uci));
        mlpExtensionParams.setUcgi(ucgi);
      }
      if (tac != null) {
        Tai tai = new Tai();
        tai.setMcc(String.valueOf(mcc));
        tai.setMnc(String.valueOf(mnc));
        tai.setTac(String.valueOf(tac));
        mlpExtensionParams.setTai(tai);
      }
      if (eci != null) {
        ECgi ecgi = new ECgi();
        ecgi.setMcc(String.valueOf(mcc));
        ecgi.setMnc(String.valueOf(mnc));
        ecgi.setEci(String.valueOf(eci));
        ecgi.setEnbid(String.valueOf(enbId));
        ecgi.setCi(String.valueOf(ci));
        mlpExtensionParams.setEcgi(ecgi);
      }
      if (nrTac != null) {
        NrTai nrTai = new NrTai();
        nrTai.setMcc(String.valueOf(mcc));
        nrTai.setMnc(String.valueOf(mnc));
        nrTai.setNrTac(String.valueOf(nrTac));
        mlpExtensionParams.setNrTai(nrTai);
      }
      if (nci != null) {
        NrCgi nrCgi = new NrCgi();
        nrCgi.setMcc(String.valueOf(mcc));
        nrCgi.setMnc(String.valueOf(mnc));
        nrCgi.setNci(String.valueOf(nci));
        mlpExtensionParams.setNrCgi(nrCgi);
      }
      if (sgsnName != null || sgsnRealm != null || sgsnNumber != null || mmeName != null || mmeRealm != null || amfAddress != null
          || mscNumber != null || vlrNumber != null) {
        ServingNode mlpServingNode = new ServingNode();
        if (mlpGsmNetParam.getNeid() == null) {
          if (mscNumber != null || vlrNumber != null) {
            if (mscNumber != null) {
              mlpVmscId.setVmscno(mscNumber);
              mlpNeid.getContent().add(mlpVmscId);
            }
            if (vlrNumber != null) {
              mlpVlrId.setVlrno(vlrNumber);
              mlpNeid.getContent().add(mlpVlrId);
            }
            mlpServingNode.setNeid(mlpNeid);
          }
        }
        if (mmeName != null) {
          DiameterIdentity mlpDiameterIdentity = new DiameterIdentity();
          if (!mmeName.isEmpty()) {
            mlpDiameterIdentity.setMmeName(mmeName);
          }
          if (mmeRealm != null) {
            if (!mmeRealm.isEmpty()) {
              mlpDiameterIdentity.setMmeRealm(mmeRealm);
            }
          }
          mlpServingNode.setDiameterIdentity(mlpDiameterIdentity);
        } else if (sgsnName != null) {
          DiameterIdentity mlpDiameterIdentity = new DiameterIdentity();
          if (!sgsnName.isEmpty()) {
            mlpDiameterIdentity.setSgsnName(sgsnName);
          }
          if (sgsnRealm != null) {
            if (!sgsnRealm.isEmpty()) {
              mlpDiameterIdentity.setSgsnRealm(sgsnRealm);
            }
          }
          mlpServingNode.setDiameterIdentity(mlpDiameterIdentity);
        }
        if (sgsnNumber != null) {
          if (!sgsnNumber.isEmpty()) {
            Sgsnid mlpSgsnid = new Sgsnid();
            mlpSgsnid.setSgsnno(sgsnNumber);
            mlpServingNode.setSgsnId(mlpSgsnid);
          }
        }
        if (amfAddress != null) {
          if (!amfAddress.isEmpty()) {
            mlpServingNode.setAmfAddress(amfAddress);
          }
        }
        if (mlpServingNode.getNeid() != null || mlpServingNode.getDiameterIdentity() != null
            || mlpServingNode.getSgsnId() != null || mlpServingNode.getAmfAddress() != null) {
          mlpExtensionParams.setServingNode(mlpServingNode);
        }
      }
      mlpSlia.setExtensionParams(mlpExtensionParams);
      mlpTlra.setExtensionParams(mlpExtensionParams);
      mlpSlrep.setExtensionParams(mlpExtensionParams);
      mlpTlrep.setExtensionParams(mlpExtensionParams);

      // Set transId in Pos (LCS Reference Number)
      if (operation.equalsIgnoreCase("PSL") || operation.equalsIgnoreCase("PLR")) {
        mlpTlra.setLcsRef(String.valueOf(lcsReferenceNumber));
        mlpPos.setResultType("FINAL");
      } else if (isReport) {
        mlpTlrep.setLcsRef(String.valueOf(lcsReferenceNumber));
      }
      // addInfo ?
      // posMethod
      if (positioningMethod != null)
        mlpPos.setPosMethod(positioningMethod.toUpperCase());
      // resultType
      mlpPos.setResultType("FINAL");
      if (!isReport) {
        // Set Pd object in Pos
        mlpPos.setPd(mlpPd);
        // Add the position list to the SLIA or TLRA
        posList.add(mlpPos);
        for (Pos pos : posList) {
          if (mlpTriggeredReportingService) {
              mlpTlra.setExtensionParams(mlpExtensionParams);
              mlpTlra.getExtensionParams().getPos().add(pos);
          } else {
            mlpSlia.getPos().add(pos);
          }
        }
      } else {
        if (mlpTriggeredReportingService) {
          TrlPos mlpTrlPos = new TrlPos();
          // Add the position list to the TLREP
          List<TrlPos> trlPosList = new ArrayList<>();
          trlPosList.add(mlpTrlPos);
          for (TrlPos tlrPos : trlPosList) {
            // Set the Msid in TrlPos for TLREP
            Msid tlrPosMsid = new Msid();
            if (imsi != null) {
              tlrPosMsid.setType("IMSI");
              tlrPosMsid.setContent(imsi);
              tlrPos.setMsid(tlrPosMsid);
            } else if (msisdn != null) {
              tlrPosMsid.setType("MSISDN");
              tlrPosMsid.setContent(msisdn);
              tlrPos.setMsid(tlrPosMsid);
            } else if (imei != null) {
              tlrPosMsid.setType("IMEI");
              tlrPosMsid.setContent(imei);
              tlrPos.setMsid(tlrPosMsid);
            }
            // Set Pd object in TrlPos for TLREP
            tlrPos.setPd(mlpPd);
            // Set the pos_method
            if (positioningMethod != null)
              tlrPos.setPosMethod(positioningMethod.toUpperCase());
            mlpTlrep.getTrlPos().add(tlrPos);
          }
        } else {
          // Set Pd object in Pos
          mlpPos.setPd(mlpPd);
          // Add the position list to the SLREP
          posList.add(mlpPos);
          for (Pos pos : posList) {
            mlpSlrep.getPos().add(pos);
          }
        }
      }
      // Set req_id to SLIA or TLRA/TLREP (Client Reference Number)
      if (clientTransId != null) {
        mlpSlia.setReqId(String.valueOf(clientTransId));
        mlpTlra.setReqId(String.valueOf(clientTransId));
        if (isReport)
          mlpTlrep.setReqId(String.valueOf(clientTransId));
      }
      // Result
      mlpResult.setContent(MLPResponse.getResultStringForType(mlpResultType));
      mlpResult.setResid(MLPResponse.getResultCodeForType(mlpResultType));
      if (mlpTriggeredReportingService) {
        if (!isReport) {
          if (operation.equalsIgnoreCase("PSL") || operation.equalsIgnoreCase("PLR")
              || operation.equalsIgnoreCase("SUPL")) {
            // Result for TLRA
            mlpTlra.setResult(mlpResult);
            // Version for TLRA
            mlpTlra.setVer(ver);
            // Set SvcResult with MLP TLRA
            mlpSvcResult.setTlra(mlpTlra);
          }
        } else {
          // Version for TLREP
          mlpTlrep.setVer(ver);
          // Set SvcResult with MLP TLREP
          mlpSvcResult.setTlrep(mlpTlrep);
        }
      } else {
        if (operation.equalsIgnoreCase("SLR") || operation.equalsIgnoreCase("LRR")) {
          // Version for SLREP
          mlpSlrep.setVer(ver);
          // Set SvcResult with MLP SLREP
          mlpSvcResult.setSlrep(mlpSlrep);
        } else {
          // Result for SLIA
          mlpSlia.setResult(mlpResult);
          // Version for SLIA
          mlpSlia.setVer(ver);
          // Set SvcResult with MLP SLIA
          mlpSvcResult.setSlia(mlpSlia);
        }
      }

      mlpSvcResult.setVer(ver);

      lXml = marshalMlpResult(mlpSvcResult, mlpTriggeredReportingService);
      // Return our XML string result
      return lXml;

    } catch (IllegalArgumentException illegalArgumentException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IllegalArgumentException while marshalling XML response data";
      this.logger.severe(this.exceptionError + " : " + illegalArgumentException);
      if (mlpTriggeredReportingService)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (IOException ioException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IOException while marshalling XML response data";
      this.logger.severe(this.exceptionError + " : " + ioException);
      if (mlpTriggeredReportingService)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }   catch (JAXBException jaxbException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "JAXBException while marshalling XML response data";
      this.logger.severe(this.exceptionError + " : " + jaxbException);
      if (mlpTriggeredReportingService)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (Exception e) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "Exception while marshalling XML response data";
      this.logger.severe(this.exceptionError + " : " + e);
      if (mlpTriggeredReportingService)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }
  }

  /**
   * Generate a MLP system error response
   *
   * @param mlpClientErrorType    Error type to return to client
   * @param mlpClientErrorMessage Error message to send to client
   * @return String XML result to return to client
   */
  public String getSystemErrorResponseXML(MLPResultType mlpClientErrorType, String mlpClientErrorMessage, boolean isTriggered) {
    // Generate XML response
    String svcResultXml;
    try {
      // Generate the error XML
      svcResultXml = this.generateSystemErrorXML(mlpClientErrorType, mlpClientErrorMessage, isTriggered);
      return svcResultXml;
    } catch (IOException e) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IOException while marshalling XML response data";
      this.logger.severe(this.exceptionError + " : " + e);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (JAXBException e) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "Exception while marshalling XML response data";
      this.logger.severe(this.exceptionError + " : " + e);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }
  }

  /**
   * Internal XML generation support function for above getSystemErrorResponseXML()
   *
   * @param mlpClientErrorType                Error type to return to client
   * @param mlpClientErrorMessage             Error message to send to client
   * @param isTriggered                       Indication if the the failed operation is of type triggered
   * @return                                  String XML result to return to client
   * @throws IOException                      IO error occurred while generating the XML result
   * @throws JAXBException                    JAXB error occurred while generating the XML result
   */
  private String generateSystemErrorXML(MLPResultType mlpClientErrorType, String mlpClientErrorMessage, boolean isTriggered) throws  IOException, JAXBException {

    String lXml;
    String ver = "3.4.0";

    try {
      // Create all the objects we'll use to generate our svc_result XML
      SvcResult mlpSvcResult = new SvcResult();
      Slia mlpSlia = new Slia();
      Tlra mlpTlra = new Tlra();
      Result mlpResult = new Result();

      // Set the additional data error message if one is available
      if (mlpClientErrorMessage != null) {
        if (isTriggered)
          mlpTlra.setAddInfo(mlpClientErrorMessage);
        else
          mlpSlia.setAddInfo(mlpClientErrorMessage);
      }

      mlpResult.setContent(MLPResponse.getResultStringForType(mlpClientErrorType));
      mlpResult.setResid(MLPResponse.getResultCodeForType(mlpClientErrorType));

      if (isTriggered) {
        mlpTlra.setResult(mlpResult);
        mlpTlra.setVer(ver);
        mlpSvcResult.setTlra(mlpTlra);
      } else {
        mlpSlia.setResult(mlpResult);
        mlpSlia.setVer(ver);
        mlpSvcResult.setSlia(mlpSlia);
      }
      mlpSvcResult.setVer(ver);

      JAXBContext jc = JAXBContext.newInstance(SvcResult.class);
      Marshaller marshaller  = jc.createMarshaller();

      ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();

      // Generate the XML
      marshaller.marshal(mlpSvcResult,lOutputStream);

      // Convert the stream to a string
      lXml = lOutputStream.toString(StandardCharsets.UTF_8);

      // Return our XML string result
      return lXml;

    } catch (IllegalArgumentException illegalArgumentException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IllegalArgumentException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + illegalArgumentException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (JAXBException jaxbException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "JAXBException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + jaxbException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (Exception e) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "Exception while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + e);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }
  }

  /**
   * Generate a MLP error response for a position
   *
   * @param msisdn                Device MSISDN
   * @param imsi                  Device IMSI
   * @param imei                  Device IMEI
   * @param mlpClientErrorType    Error type to return to client
   * @param mlpClientErrorMessage Error message to send to client
   * @param isTriggered           Indication if the the failed operation is of type triggered
   * @param clientTransId         The client's transaction id
   * @param lcsRefNumber          The LCS Reference Number generated for MAP PSL/SLR or Diameter SLg PLR/LRR
   * @return String XML result to return to client
   */
  public String getPositionErrorResponseXML(String msisdn, String imsi, String imei, MLPResultType mlpClientErrorType, String mlpClientErrorMessage,
                                            boolean isTriggered, Integer clientTransId, Integer lcsRefNumber) {
    // Generate XML response
    String svcResultXml;

    try {
      // Generate the error XML
      this.logger.info("Creating error XML response for type: " + MLPResponse.getResultCodeForType(mlpClientErrorType) + " message: " + mlpClientErrorMessage);
      svcResultXml = this.generatePositionErrorXML(msisdn, imsi, imei, mlpClientErrorType, mlpClientErrorMessage,
          isTriggered, clientTransId, lcsRefNumber);
      return svcResultXml;
    } catch (IllegalArgumentException illegalArgumentException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IllegalArgumentException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + illegalArgumentException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (IOException ioException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IOException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + ioException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }   catch (JAXBException jaxbException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "JAXBException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + jaxbException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (Exception e) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "Exception while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + e);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }
  }

  /**
   * Internal XML generation support function for above getSystemErrorResponseXML()
   *
   * @param msisdn                Device MSISDN
   * @param imsi                  Device IMSI
   * @param imei                  Device IMEI
   * @param mlpClientErrorType    Error type to return to client
   * @param mlpClientErrorMessage Error message to send to client
   * @param isTriggered           Indication if the the failed operation is of type triggered
   * @param clientTransId         The client's transaction id
   * @param lcsRefNumber          The LCS Reference Number generated for MAP PSL/SLR or Diameter SLg PLR/LRR
   * @return                      String XML result to return to client
   * @throws IOException          IO error occurred while generating the XML result
   * @throws JAXBException        JAXB error occurred while generating the XML result
   */
  private String generatePositionErrorXML(String msisdn, String imsi, String imei,
                                          MLPResultType mlpClientErrorType, String mlpClientErrorMessage,
                                          boolean isTriggered, Integer clientTransId, Integer lcsRefNumber)
      throws  IOException, JAXBException {

    String lXml;
    String ver = "3.4.0";

    try {
      // Create all the objects we'll use to generate our svc_result XML
      SvcResult mlpSvcResult = new SvcResult();
      Slia mlpSlia = new Slia();
      Tlra mlpTlra = new Tlra();
      Msid mlpMsisdn = new Msid();
      Msid mlpImsi = new Msid();
      Msid mlpImei = new Msid();
      ExtensionParams mlpExtensionParams = new ExtensionParams();
      Result mlpResult = new Result();

      // Set the data
      mlpResult.setContent(MLPResponse.getResultStringForType(mlpClientErrorType));
      mlpResult.setResid(MLPResponse.getResultCodeForType(mlpClientErrorType));
      // Extension params
      MsisdsExt msids = new MsisdsExt();
      List<Msid> msidList = new ArrayList<>();
      if (msisdn != null) {
        mlpMsisdn.setType("MSISDN");
        mlpMsisdn.setContent(msisdn);
        msidList.add(mlpMsisdn);
      }
      if (imsi != null) {
        mlpImsi.setType("IMSI");
        mlpImsi.setContent(imsi);
        msidList.add(mlpImsi);
      }
      if (imei != null) {
        mlpImei.setType("IMEI");
        mlpImei.setContent(imei);
        msidList.add(mlpImei);
      }
      msids.setMsidList(msidList);
      mlpExtensionParams.setMsidsExt(msids);

      if (isTriggered) {
        if (clientTransId != null) {
          mlpTlra.setReqId(String.valueOf(clientTransId));
        }
        if  (lcsRefNumber != null) {
          mlpTlra.setLcsRef(String.valueOf(lcsRefNumber));
        }
        mlpTlra.setVer(ver);
        mlpTlra.setResult(mlpResult);
        mlpTlra.setAddInfo(mlpClientErrorMessage);
        mlpTlra.setExtensionParams(mlpExtensionParams);
        mlpSvcResult.setTlra(mlpTlra);
        mlpSvcResult.setVer(ver);
      } else {
        if (clientTransId != null) {
          mlpSlia.setReqId(String.valueOf(clientTransId));
        }
        mlpSlia.setVer(ver);
        mlpSlia.setResult(mlpResult);
        mlpSlia.setAddInfo(mlpClientErrorMessage);
        mlpSlia.setExtensionParams(mlpExtensionParams);
        mlpSvcResult.setSlia(mlpSlia);
        mlpSvcResult.setVer(ver);
      }

      lXml = marshalMlpResult(mlpSvcResult, isTriggered);

      // Return our XML string result
      return lXml;
    } catch (IllegalArgumentException illegalArgumentException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IllegalArgumentException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + illegalArgumentException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (IOException ioException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "IOException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + ioException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (JAXBException jaxbException) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "JAXBException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + jaxbException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (Exception e) {
      // Return generic XML error response because we couldn't generate the correct response
      this.exceptionError = "Exception while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + e);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }
  }

  /**
   * Create the svc_result XML result for any type of result (error or success)
   *
   * @param mlpSvcResult    Fully filled in SvcResult object to marshal (convert to XML)
   * @return                String of XML result to send to client
   * @throws IOException    IO error occurred while generating the XML result
   * @throws JAXBException  JAXB error occurred while generating the XML result
   */
  private String marshalMlpResult(SvcResult mlpSvcResult, boolean isTriggered) throws IOException, JAXBException {
    String lXml;

    JAXBContext jc = JAXBContext.newInstance(SvcResult.class);
    Marshaller marshaller = jc.createMarshaller();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    DOMResult domResult = new DOMResult();

    // Generate the XML
    marshaller.marshal(mlpSvcResult, domResult);

    try {
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", 4);
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      transformer.transform(new DOMSource(domResult.getNode()), new StreamResult(outputStream));
    } catch (TransformerConfigurationException transformerConfigurationException) {
      this.exceptionError = "TransformerConfigurationException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + transformerConfigurationException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (TransformerException transformerException) {
      this.exceptionError = "TransformerException while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + transformerException);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    } catch (Exception e) {
      this.exceptionError = "Exception while marshalling XML response data";
      this.logger.info(this.exceptionError + " : " + e);
      if (isTriggered)
        return genericTriggeredLocationRequestErrorXML;
      else
        return genericStandardLocationRequestErrorXML;
    }

    // Convert the stream to a string
    lXml = outputStream.toString(StandardCharsets.UTF_8);
    // Return our XML string result
    return lXml;
  }
}