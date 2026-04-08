package org.mobicents.gmlc.slee.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import static org.mobicents.gmlc.slee.http.JsonWriter.writeClientReferenceNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImei;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMsisdn;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetwork;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperation;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationErrorReason;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOriginHostName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOriginHostRealm;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeProtocol;


/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class OnErrorResponseJsonBuilder {

    public OnErrorResponseJsonBuilder() {
    }

    public static String buildJsonResponseOnError(String msisdn, String imsi, String imei, String operation, String errorMessage, Integer clientRefNum,
                                                  String networkNodeNumber, String addNetworkNodeNumber, String host, String realm) {

        String network, protocol;
        network = protocol = null;
        if (operation != null) {
            if (operation.equalsIgnoreCase("ATI")) {
                network = "GSM/UMTS";
                protocol = "MAP";
            } else if (operation.equalsIgnoreCase("SRISM")) {
                network = "GSM/UMTS";
                protocol = "MAP";
            } else if (operation.equalsIgnoreCase("SRI")) {
                network = "GSM/UMTS";
                protocol = "MAP";
            } else if (operation.equalsIgnoreCase("PSI")) {
                network = "GSM/UMTS";
                protocol = "MAP";
            } else if (operation.equalsIgnoreCase("SRILCS")) {
                network = "GSM/UMTS";
                protocol = "MAP";
            } else if (operation.equalsIgnoreCase("PSL")) {
                network = "GSM/UMTS";
                protocol = "MAP";
            } else if (operation.equalsIgnoreCase("RIR")) {
                network = "LTE";
                protocol = "Diameter SLh";
            }  else if (operation.equalsIgnoreCase("PLR")) {
                network = "LTE";
                protocol = "Diameter SLg (ELP)";
            } else if (operation.equalsIgnoreCase("UDR")) {
                network = "IMS";
                protocol = "Diameter Sh";
            }
        }

        JsonObject errorResponseJsonObject = new JsonObject();
        writeNetwork(network, errorResponseJsonObject);
        writeProtocol(protocol, errorResponseJsonObject);
        writeOperation(operation, errorResponseJsonObject);
        writeMsisdn(msisdn, errorResponseJsonObject);
        writeImsi(imsi, errorResponseJsonObject);
        writeImei(imei, errorResponseJsonObject);
        writeNetworkNodeNumber(networkNodeNumber, errorResponseJsonObject);
        writeAdditionalNetworkNodeNumber(addNetworkNodeNumber, errorResponseJsonObject);
        writeOriginHostName(host,errorResponseJsonObject);
        writeOriginHostRealm(realm, errorResponseJsonObject);
        writeClientReferenceNumber(clientRefNum, errorResponseJsonObject);
        writeOperationResult("ERROR", errorResponseJsonObject);
        writeOperationErrorReason(errorMessage, errorResponseJsonObject);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        return gson.toJson(errorResponseJsonObject);
    }

}
