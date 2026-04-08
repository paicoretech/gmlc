package org.mobicents.gmlc.slee.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class JsonParseHelperForSDP {

    private static final Logger logger = Logger.getLogger(JsonParseHelperForSDP.class);

    public static void main(String[] args) throws Exception {

        try {
            String rirPlrResponseJson = "{\n" +
                "  \"network\": \"LTE\",\n" +
                "  \"protocol\": \"Diameter SLh-SLg(ELP)\",\n" +
                "  \"operation\": \"RIR/RIA PLR/PLA\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"clientReferenceNumber\": 101,\n" +
                "  \"plrFlags\": 4,\n" +
                "  \"Routing-Info-Answer\": {\n" +
                "    \"msisdn\": 59899077938,\n" +
                "    \"imsi\": 748026871012341,\n" +
                "    \"lmsi\": \"2449692709\",\n" +
                "    \"ServingNode\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"lcsCapabilitySets\": \"release4\",\n" +
                "      \"gmlcAddress\": \"200.123.44.109\"\n" +
                "    },\n" +
                "    \"AdditionalServingNode\": {\n" +
                "      \"sgsnNumber\": 5989900023,\n" +
                "      \"sgsnName\": \"dra.simulator\",\n" +
                "      \"sgsnRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"lcsCapabilitySets\": \"release6\"\n" +
                "    },\n" +
                "    \"gmlcAddress\": \"200.123.44.109\",\n" +
                "    \"pprAddress\": \"10.1.0.93\",\n" +
                "    \"riaFlags\": 1\n" +
                "  },\n" +
                "  \"Provide-Location-Answer\": {\n" +
                "    \"LocationEstimate\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.910345,\n" +
                "      \"longitude\": -56.149814,\n" +
                "      \"uncertainty\": 7.71561\n" +
                "    },\n" +
                "    \"accuracyFulfilmentIndicator\": \"REQUESTED_ACCURACY_FULFILLED\",\n" +
                "    \"ageOfLocationEstimate\": 0,\n" +
                "    \"VelocityEstimate\": {\n" +
                "      \"horizontalSpeed\": 104,\n" +
                "      \"bearing\": 0,\n" +
                "      \"verticalSpeed\": 0,\n" +
                "      \"uncertaintyHorizontalSpeed\": 0,\n" +
                "      \"uncertaintyVerticalSpeed\": 0,\n" +
                "      \"velocityType\": \"HorizontalVelocity\"\n" +
                "    },\n" +
                "    \"CGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"lac\": 119,\n" +
                "      \"ci\": 15336\n" +
                "    },\n" +
                "    \"UtranPositioningData\": {\n" +
                "      \"PositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"Mobile Assisted GPS\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"Mobile Based GPS\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"U-TDOA\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"IPDL\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"RTT\",\n" +
                "          \"usage\": 4\n" +
                "        },\n" +
                "        \"Item-5\": {\n" +
                "          \"method\": \"OTDOA\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-6\": {\n" +
                "          \"method\": \"Conventional GPS\",\n" +
                "          \"usage\": 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"UtranGANSSPositioningData\": {\n" +
                "      \"GanssPositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"MS-Based\",\n" +
                "          \"ganssId\": \"Galileo\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"MS-Assisted\",\n" +
                "          \"ganssId\": \"SBAS\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"Conventional\",\n" +
                "          \"ganssId\": \"Modernized GPS\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"MS-Based\",\n" +
                "          \"ganssId\": \"QZSS\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"Reserved\",\n" +
                "          \"ganssId\": \"BDS\",\n" +
                "          \"usage\": 4\n" +
                "        },\n" +
                "        \"Item-5\": {\n" +
                "          \"method\": \"MS-Assisted\",\n" +
                "          \"ganssId\": \"GLONASS\",\n" +
                "          \"usage\": 3\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"UtranAdditionalPositioningData\": {\n" +
                "      \"AdditionalPositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"Standalone\",\n" +
                "          \"posId\": \"Bluetooth\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"MS-Assisted\",\n" +
                "          \"posId\": \"WLAN\",\n" +
                "          \"usage\": 4\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"TargetServingNodeForHandover\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"lcsCapabilitySets\": \"release4\",\n" +
                "      \"gmlcAddress\": \"200.123.44.109\"\n" +
                "    },\n" +
                "    \"plaFlags\": 0,\n" +
                "    \"CivicAddress\": {\n" +
                "      \"country\": \"US\",\n" +
                "      \"A1\": \"CA\",\n" +
                "      \"A2\": \"Sacramento\",\n" +
                "      \"RD\": \"I5\"\n" +
                "    },\n" +
                "    \"BarometricPressure\": {\n" +
                "      \"measuredPa\": 1014\n" +
                "    }\n" +
                "  }\n" +
                "}";

            procesRiaPla(rirPlrResponseJson);

            rirPlrResponseJson = "{\n" +
                "  \"network\": \"LTE\",\n" +
                "  \"protocol\": \"Diameter SLh-SLg(ELP)\",\n" +
                "  \"operation\": \"RIR/RIA PLR/PLA\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"clientReferenceNumber\": 99,\n" +
                "  \"plrFlags\": 4,\n" +
                "  \"Routing-Info-Answer\": {\n" +
                "    \"msisdn\": 59899077937,\n" +
                "    \"imsi\": 748026871012345,\n" +
                "    \"lmsi\": \"2449692759\",\n" +
                "    \"ServingNode\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"gmlcAddress\": \"200.123.44.108\"\n" +
                "    },\n" +
                "    \"AdditionalServingNode\": {\n" +
                "      \"mscNumber\": 5989900008,\n" +
                "      \"lcsCapabilitySets\": \"release6\"\n" +
                "    },\n" +
                "    \"gmlcAddress\": \"200.123.44.108\",\n" +
                "    \"pprAddress\": \"10.1.0.92\",\n" +
                "    \"riaFlags\": 0\n" +
                "  },\n" +
                "  \"Provide-Location-Answer\": {\n" +
                "    \"LocationEstimate\": {\n" +
                "      \"typeOfShape\": \"EllipsoidArc\",\n" +
                "      \"latitude\": -34.909734,\n" +
                "      \"longitude\": -56.146317,\n" +
                "      \"innerRadius\": 32,\n" +
                "      \"uncertaintyInnerRadius\": 3.31,\n" +
                "      \"offsetAngle\": 12.0,\n" +
                "      \"includedAngle\": 21.0,\n" +
                "      \"confidence\": 20\n" +
                "    },\n" +
                "    \"accuracyFulfilmentIndicator\": \"REQUESTED_ACCURACY_FULFILLED\",\n" +
                "    \"ageOfLocationEstimate\": 0,\n" +
                "    \"VelocityEstimate\": {\n" +
                "      \"horizontalSpeed\": 20,\n" +
                "      \"bearing\": 0,\n" +
                "      \"verticalSpeed\": 0,\n" +
                "      \"uncertaintyHorizontalSpeed\": 0,\n" +
                "      \"uncertaintyVerticalSpeed\": 0,\n" +
                "      \"velocityType\": \"HorizontalVelocity\"\n" +
                "    },\n" +
                "    \"EUtranPositioningData\": {\n" +
                "      \"PositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"Cell ID\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"E-CID\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"OTDOA\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"U-TDOA\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"Reserved\",\n" +
                "          \"usage\": 4\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"eci\": 518658,\n" +
                "      \"eNBId\": 2026,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"TargetServingNodeForHandover\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"gmlcAddress\": \"200.123.44.108\"\n" +
                "    },\n" +
                "    \"plaFlags\": 0,\n" +
                "    \"CivicAddress\": {\n" +
                "      \"country\": \"AU\",\n" +
                "      \"A1\": \"NSW\",\n" +
                "      \"A3\": \"Wollongong\",\n" +
                "      \"A4\": \"North Wollongong\",\n" +
                "      \"RD\": \"Flinders\",\n" +
                "      \"STS\": \"Street\",\n" +
                "      \"RDBR\": \"Campbell Street\",\n" +
                "      \"LMK\": \"Gilligan\\u0027s Island\",\n" +
                "      \"LOC\": \"Corner\",\n" +
                "      \"NAM\": \"Video Rental Store\",\n" +
                "      \"PC\": \"2500\",\n" +
                "      \"ROOM\": \"Westerns and Classics\",\n" +
                "      \"PLC\": \"store\",\n" +
                "      \"POBOX\": \"Private Box 15\"\n" +
                "    },\n" +
                "    \"BarometricPressure\": {\n" +
                "      \"measuredPa\": 1013\n" +
                "    }\n" +
                "  }\n" +
                "}";

            procesRiaPla(rirPlrResponseJson);

            rirPlrResponseJson = "{\n" +
                "  \"network\": \"LTE\",\n" +
                "  \"protocol\": \"Diameter SLh-SLg(ELP)\",\n" +
                "  \"operation\": \"RIR/RIA PLR/PLA\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"clientReferenceNumber\": 103,\n" +
                "  \"plrFlags\": 4,\n" +
                "  \"Routing-Info-Answer\": {\n" +
                "    \"msisdn\": 573195897484,\n" +
                "    \"imsi\": 732101509580853,\n" +
                "    \"lmsi\": \"2731191775\",\n" +
                "    \"ServingNode\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"mscNumber\": 5730100003,\n" +
                "      \"3GPPAAAServerName\": \"aaa001\",\n" +
                "      \"lcsCapabilitySets\": \"release7\",\n" +
                "      \"gmlcAddress\": \"191.42.21.204\"\n" +
                "    },\n" +
                "    \"AdditionalServingNode\": {\n" +
                "      \"3GPPAAAServerName\": \"aaa4.mnc01.mcc552.3gppnetwork.org\",\n" +
                "      \"lcsCapabilitySets\": \"release4\"\n" +
                "    },\n" +
                "    \"gmlcAddress\": \"191.42.21.204\",\n" +
                "    \"pprAddress\": \"192.168.1.20\",\n" +
                "    \"riaFlags\": 0\n" +
                "  },\n" +
                "  \"Provide-Location-Answer\": {\n" +
                "    \"LocationEstimate\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithAltitudeAndUncertaintyEllipsoid\",\n" +
                "      \"latitude\": 6.195592,\n" +
                "      \"longitude\": -75.558107,\n" +
                "      \"altitude\": 200,\n" +
                "      \"uncertaintySemiMajorAxis\": 2.1,\n" +
                "      \"uncertaintySemiMinorAxis\": 2.1,\n" +
                "      \"angleOfMajorAxis\": 4.0,\n" +
                "      \"uncertaintyAltitude\": 11.198834,\n" +
                "      \"confidence\": 80\n" +
                "    },\n" +
                "    \"accuracyFulfilmentIndicator\": \"REQUESTED_ACCURACY_FULFILLED\",\n" +
                "    \"ageOfLocationEstimate\": 0,\n" +
                "    \"VelocityEstimate\": {\n" +
                "      \"horizontalSpeed\": 20,\n" +
                "      \"bearing\": 0,\n" +
                "      \"verticalSpeed\": 0,\n" +
                "      \"uncertaintyHorizontalSpeed\": 0,\n" +
                "      \"uncertaintyVerticalSpeed\": 0,\n" +
                "      \"velocityType\": \"HorizontalVelocity\"\n" +
                "    },\n" +
                "    \"EUtranPositioningData\": {\n" +
                "      \"GnssPositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"UE-Assisted\",\n" +
                "          \"ganssId\": \"GPS\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"UE-Based\",\n" +
                "          \"ganssId\": \"Galileo\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"Reserved\",\n" +
                "          \"ganssId\": \"SBAS\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"UE-Based\",\n" +
                "          \"ganssId\": \"Modernized GPS\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"Reserved\",\n" +
                "          \"ganssId\": \"QZSS\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-5\": {\n" +
                "          \"method\": \"UE-Based\",\n" +
                "          \"ganssId\": \"GLONASS\",\n" +
                "          \"usage\": 4\n" +
                "        },\n" +
                "        \"Item-6\": {\n" +
                "          \"method\": \"Conventional\",\n" +
                "          \"ganssId\": \"BDS\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-7\": {\n" +
                "          \"method\": \"Conventional\",\n" +
                "          \"ganssId\": \"NavIC\",\n" +
                "          \"usage\": 2\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 732,\n" +
                "      \"mnc\": 123,\n" +
                "      \"eci\": 2829314,\n" +
                "      \"eNBId\": 11052,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"UtranPositioningData\": {\n" +
                "      \"PositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"U-TDOA\",\n" +
                "          \"usage\": 3\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"UtranGANSSPositioningData\": {\n" +
                "      \"GanssPositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"MS-Assisted\",\n" +
                "          \"ganssId\": \"GLONASS\",\n" +
                "          \"usage\": 3\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"TargetServingNodeForHandover\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"3GPPAAAServerName\": \"aaa001\",\n" +
                "      \"mscNumber\": 5730100003,\n" +
                "      \"lcsCapabilitySets\": \"release7\",\n" +
                "      \"gmlcAddress\": \"191.42.21.204\"\n" +
                "    },\n" +
                "    \"plaFlags\": 0,\n" +
                "    \"CivicAddress\": {\n" +
                "      \"country\": \"AU\",\n" +
                "      \"A1\": \"NSW\",\n" +
                "      \"A3\": \"Wollongong\",\n" +
                "      \"A4\": \"North Wollongong\",\n" +
                "      \"RD\": \"Flinders\",\n" +
                "      \"STS\": \"Street\",\n" +
                "      \"RDBR\": \"Campbell Street\",\n" +
                "      \"LMK\": \"Gilligan\\u0027s Island\",\n" +
                "      \"LOC\": \"Corner\",\n" +
                "      \"NAM\": \"Video Rental Store\",\n" +
                "      \"PC\": \"2500\",\n" +
                "      \"ROOM\": \"Westerns and Classics\",\n" +
                "      \"PLC\": \"store\",\n" +
                "      \"POBOX\": \"Private Box 15\"\n" +
                "    },\n" +
                "    \"BarometricPressure\": {\n" +
                "      \"measuredPa\": 1012\n" +
                "    }\n" +
                "  }\n" +
                "}";

            procesRiaPla(rirPlrResponseJson);

            rirPlrResponseJson = "{\n" +
                "  \"network\": \"LTE\",\n" +
                "  \"protocol\": \"Diameter SLh-SLg(ELP)\",\n" +
                "  \"operation\": \"RIR/RIA PLR/PLA\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"clientReferenceNumber\": 1824578,\n" +
                "  \"plrFlags\": 4,\n" +
                "  \"Routing-Info-Answer\": {\n" +
                "    \"msisdn\": 60192235909,\n" +
                "    \"imsi\": 552016871012345,\n" +
                "    \"lmsi\": \"2449692786\",\n" +
                "    \"ServingNode\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"sgsnNumber\": 5520100021,\n" +
                "      \"sgsnName\": \"dra.simulator\",\n" +
                "      \"sgsnRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"3GPPAAAServerName\": \"aaa003\",\n" +
                "      \"lcsCapabilitySets\": \"release7\",\n" +
                "      \"gmlcAddress\": \"200.123.44.108\"\n" +
                "    },\n" +
                "    \"AdditionalServingNode\": {\n" +
                "      \"mscNumber\": 5520100003,\n" +
                "      \"lcsCapabilitySets\": \"release4\"\n" +
                "    },\n" +
                "    \"gmlcAddress\": \"200.123.44.108\",\n" +
                "    \"pprAddress\": \"10.1.0.92\",\n" +
                "    \"riaFlags\": 0\n" +
                "  },\n" +
                "  \"Provide-Location-Answer\": {\n" +
                "    \"LocationEstimate\": {\n" +
                "      \"typeOfShape\": \"EllipsoidArc\",\n" +
                "      \"latitude\": 3.137208,\n" +
                "      \"longitude\": 101.73048,\n" +
                "      \"innerRadius\": 32,\n" +
                "      \"uncertaintyInnerRadius\": 3.31,\n" +
                "      \"offsetAngle\": 12.0,\n" +
                "      \"includedAngle\": 21.0,\n" +
                "      \"confidence\": 20\n" +
                "    },\n" +
                "    \"accuracyFulfilmentIndicator\": \"REQUESTED_ACCURACY_FULFILLED\",\n" +
                "    \"ageOfLocationEstimate\": 0,\n" +
                "    \"VelocityEstimate\": {\n" +
                "      \"horizontalSpeed\": 10,\n" +
                "      \"bearing\": 2,\n" +
                "      \"verticalSpeed\": 0,\n" +
                "      \"uncertaintyHorizontalSpeed\": 0,\n" +
                "      \"uncertaintyVerticalSpeed\": 0,\n" +
                "      \"velocityType\": \"HorizontalVelocity\"\n" +
                "    },\n" +
                "    \"EUtranPositioningData\": {\n" +
                "      \"AdditionalPositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"Standalone\",\n" +
                "          \"posId\": \"Barometric Pressure\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"UE-Based\",\n" +
                "          \"posId\": \"WLAN\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"UE-Assisted\",\n" +
                "          \"posId\": \"Bluetooth\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"Reserved\",\n" +
                "          \"posId\": \"MBS\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"Standalone\",\n" +
                "          \"posId\": \"Motion-Sensor(s)\",\n" +
                "          \"usage\": 4\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 502,\n" +
                "      \"mnc\": 18,\n" +
                "      \"eci\": 207631207,\n" +
                "      \"eNBId\": 811059,\n" +
                "      \"ci\": 103\n" +
                "    },\n" +
                "    \"TargetServingNodeForHandover\": {\n" +
                "      \"mmeName\": \"dra.simulator\",\n" +
                "      \"mmeRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"sgsnName\": \"dra.simulator\",\n" +
                "      \"sgsnRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"sgsnNumber\": 5520100021,\n" +
                "      \"3GPPAAAServerName\": \"aaa003\",\n" +
                "      \"lcsCapabilitySets\": \"release7\",\n" +
                "      \"gmlcAddress\": \"200.123.44.108\"\n" +
                "    },\n" +
                "    \"plaFlags\": 0,\n" +
                "    \"ESMLCCellInfo\": {\n" +
                "      \"mcc\": 502,\n" +
                "      \"mnc\": 18,\n" +
                "      \"eci\": 207631207,\n" +
                "      \"eNBId\": 811059,\n" +
                "      \"ci\": 103,\n" +
                "      \"cellPortionId\": 3\n" +
                "    },\n" +
                "    \"BarometricPressure\": {\n" +
                "      \"measuredPa\": 1010\n" +
                "    }\n" +
                "  }\n" +
                "}";

            procesRiaPla(rirPlrResponseJson);

            rirPlrResponseJson = "{\n" +
                "  \"network\": \"LTE\",\n" +
                "  \"protocol\": \"Diameter SLh-SLg(ELP)\",\n" +
                "  \"operation\": \"RIR/RIA PLR/PLA\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"clientReferenceNumber\": 102,\n" +
                "  \"plrFlags\": 6,\n" +
                "  \"Routing-Info-Answer\": {\n" +
                "    \"msisdn\": 59899077939,\n" +
                "    \"imsi\": 748026871012351,\n" +
                "    \"lmsi\": \"2449692707\",\n" +
                "    \"ServingNode\": {\n" +
                "      \"mscNumber\": 5989900022,\n" +
                "      \"lcsCapabilitySets\": \"release4\"\n" +
                "    },\n" +
                "    \"AdditionalServingNode\": {\n" +
                "      \"sgsnNumber\": 5989900023,\n" +
                "      \"sgsnName\": \"dra.simulator\",\n" +
                "      \"sgsnRealm\": \"epc.mnc000.mcc000.3gppnetwork.org\",\n" +
                "      \"lcsCapabilitySets\": \"release6\",\n" +
                "      \"gmlcAddress\": \"189.0.29.201\"\n" +
                "    },\n" +
                "    \"gmlcAddress\": \"200.123.44.106\",\n" +
                "    \"pprAddress\": \"10.1.0.92\",\n" +
                "    \"riaFlags\": 1\n" +
                "  },\n" +
                "  \"Provide-Location-Answer\": {\n" +
                "    \"LocationEstimate\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyEllipse\",\n" +
                "      \"latitude\": -34.905614,\n" +
                "      \"longitude\": -55.042191,\n" +
                "      \"uncertaintySemiMajorAxis\": 18.531167,\n" +
                "      \"uncertaintySemiMinorAxis\": 9.487171,\n" +
                "      \"angleOfMajorAxis\": 30.0,\n" +
                "      \"confidence\": 1\n" +
                "    },\n" +
                "    \"accuracyFulfilmentIndicator\": \"REQUESTED_ACCURACY_NOT_FULFILLED\",\n" +
                "    \"ageOfLocationEstimate\": 0,\n" +
                "    \"VelocityEstimate\": {\n" +
                "      \"horizontalSpeed\": 104,\n" +
                "      \"bearing\": 0,\n" +
                "      \"verticalSpeed\": 0,\n" +
                "      \"uncertaintyHorizontalSpeed\": 0,\n" +
                "      \"uncertaintyVerticalSpeed\": 0,\n" +
                "      \"velocityType\": \"HorizontalVelocity\"\n" +
                "    },\n" +
                "    \"GeranPositioningData\": {\n" +
                "      \"PositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"Timing Advance\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"Mobile Assisted E-OTD\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"Mobile Based E-OTD\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"Mobile Assisted GPS\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"Conventional GPS\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-5\": {\n" +
                "          \"method\": \"Cell ID\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-6\": {\n" +
                "          \"method\": \"U-TDOA\",\n" +
                "          \"usage\": 4\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"GeranGANSSPositioningData\": {\n" +
                "      \"GanssPositioningDataSet\": {\n" +
                "        \"Item-0\": {\n" +
                "          \"method\": \"Conventional\",\n" +
                "          \"ganssId\": \"SBAS\",\n" +
                "          \"usage\": 4\n" +
                "        },\n" +
                "        \"Item-1\": {\n" +
                "          \"method\": \"MS-Based\",\n" +
                "          \"ganssId\": \"Galileo\",\n" +
                "          \"usage\": 2\n" +
                "        },\n" +
                "        \"Item-2\": {\n" +
                "          \"method\": \"MS-Based\",\n" +
                "          \"ganssId\": \"Modernized GPS\",\n" +
                "          \"usage\": 1\n" +
                "        },\n" +
                "        \"Item-3\": {\n" +
                "          \"method\": \"MS-Assisted\",\n" +
                "          \"ganssId\": \"QZSS\",\n" +
                "          \"usage\": 0\n" +
                "        },\n" +
                "        \"Item-4\": {\n" +
                "          \"method\": \"Reserved\",\n" +
                "          \"ganssId\": \"BDS\",\n" +
                "          \"usage\": 3\n" +
                "        },\n" +
                "        \"Item-5\": {\n" +
                "          \"method\": \"MS-Assisted\",\n" +
                "          \"ganssId\": \"GLONASS\",\n" +
                "          \"usage\": 4\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"SAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 10,\n" +
                "      \"lac\": 9501,\n" +
                "      \"sac\": 35100\n" +
                "    },\n" +
                "    \"TargetServingNodeForHandover\": {\n" +
                "      \"mscNumber\": 5989900022,\n" +
                "      \"lcsCapabilitySets\": \"release4\"\n" +
                "    },\n" +
                "    \"plaFlags\": 0,\n" +
                "    \"CivicAddress\": {\n" +
                "      \"country\": \"US\",\n" +
                "      \"A1\": \"CA\",\n" +
                "      \"A2\": \"Sacramento\",\n" +
                "      \"RD\": \"Colorado\",\n" +
                "      \"HNO\": \"223\"\n" +
                "    },\n" +
                "    \"BarometricPressure\": {\n" +
                "      \"measuredPa\": 1015\n" +
                "    }\n" +
                "  }\n" +
                "}";

            procesRiaPla(rirPlrResponseJson);

            String atiOrPsiJsonLine1 = "{\n" +
                "  \"network\": \"GSM/UMTS\",\n" +
                "  \"protocol\": \"MAP\",\n" +
                "  \"operation\": \"ATI\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"msisdn\": 59899077937,\n" +
                "  \"imei\": \"011714004661050\",\n" +
                "  \"EPSLocationInformation\": {\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"eci\": 614146,\n" +
                "      \"eNBId\": 2399,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"tac\": 109\n" +
                "    },\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.910345,\n" +
                "      \"longitude\": -56.149814,\n" +
                "      \"uncertainty\": 1.0,\n" +
                "      \"confidence\": 1,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"mmeName\": \"mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org\"\n" +
                "  },\n" +
                "  \"5GSLocationInformation\": {\n" +
                "    \"NCGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 2,\n" +
                "      \"nci\": 34359738376\n" +
                "    },\n" +
                "    \"NR-TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 2,\n" +
                "      \"tac\": 495570\n" +
                "    },\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 7,\n" +
                "      \"eci\": 4926468,\n" +
                "      \"eNBId\": 19244,\n" +
                "      \"ci\": 4\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 7,\n" +
                "      \"tac\": 7000\n" +
                "    },\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.910345,\n" +
                "      \"longitude\": -56.149814,\n" +
                "      \"uncertainty\": 1.0,\n" +
                "      \"confidence\": 1,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"amfAddress\": \"amf3.cluster2.net2.amf.5gc.mnc02.mcc748.3gppnetwork.org\",\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"VisitedPLMNId\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 2\n" +
                "    },\n" +
                "    \"Used-RAT-Type\": {\n" +
                "      \"ratType\": \"nbIoT\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"subscriberState\": \"camelBusy\",\n" +
                "  \"MNPInfoResult\": {\n" +
                "    \"mnpStatus\": \"ownNumberNotPortedOut\",\n" +
                "    \"mnpMsisdn\": 59899077937,\n" +
                "    \"mnpImsi\": 748026871012345,\n" +
                "    \"mnpRouteingNumber\": \"598123\"\n" +
                "  },\n" +
                "  \"msClassmark\": \"393A52\"\n" +
                "}";

            processAtiOrPsi(atiOrPsiJsonLine1);

            String atiOrPsiJsonLine2 = "{\n" +
                "  \"network\": \"GSM/UMTS\",\n" +
                "  \"protocol\": \"MAP\",\n" +
                "  \"operation\": \"ATI\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"msisdn\": 59899077937,\n" +
                "  \"imei\": \"011714004661050\",\n" +
                "  \"EPSLocationInformation\": {\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"eci\": 614146,\n" +
                "      \"eNBId\": 2399,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"tac\": 109\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.909734,\n" +
                "      \"longitude\": -56.146317,\n" +
                "      \"uncertainty\": 0.0\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"mmeName\": \"mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org\"\n" +
                "  },\n" +
                "  \"5GSLocationInformation\": {\n" +
                "    \"NCGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"nci\": 42949672954\n" +
                "    },\n" +
                "    \"NR-TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"tac\": 595578\n" +
                "    },\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"eci\": 614146,\n" +
                "      \"eNBId\": 2399,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"tac\": 109\n" +
                "    },\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.910345,\n" +
                "      \"longitude\": -56.149814,\n" +
                "      \"uncertainty\": 1.0,\n" +
                "      \"confidence\": 1,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"amfAddress\": \"amf1.cluster1.net2.amf.5gc.mnc01.mcc748.3gppnetwork.org\",\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"VisitedPLMNId\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1\n" +
                "    },\n" +
                "    \"Used-RAT-Type\": {\n" +
                "      \"ratType\": \"eUtran\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"subscriberState\": \"camelBusy\",\n" +
                "  \"MNPInfoResult\": {\n" +
                "    \"mnpStatus\": \"ownNumberNotPortedOut\",\n" +
                "    \"mnpMsisdn\": 59899077937,\n" +
                "    \"mnpImsi\": 748026871012345,\n" +
                "    \"mnpRouteingNumber\": \"598123\"\n" +
                "  },\n" +
                "  \"msClassmark\": \"393A52\"\n" +
                "}";

            processAtiOrPsi(atiOrPsiJsonLine2);

            String atiOrPsiJsonLine3 = "{\n" +
                "  \"network\": \"GSM/UMTS\",\n" +
                "  \"protocol\": \"MAP\",\n" +
                "  \"operation\": \"ATI\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"msisdn\": 59899077937,\n" +
                "  \"imei\": \"011714004661050\",\n" +
                "  \"CSLocationInformation\": {\n" +
                "    \"LocationNumber\": {\n" +
                "      \"oddFlag\": false,\n" +
                "      \"natureOfAddressIndicator\": 4,\n" +
                "      \"internalNetworkNumberIndicator\": 1,\n" +
                "      \"numberingPlanIndicator\": 1,\n" +
                "      \"addressPresentationRestrictedIndicator\": 1,\n" +
                "      \"screeningIndicator\": 3,\n" +
                "      \"address\": 819203961904\n" +
                "    },\n" +
                "    \"CGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 7,\n" +
                "      \"lac\": 8820,\n" +
                "      \"ci\": 8051\n" +
                "    },\n" +
                "    \"saiPresent\": false,\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.909734,\n" +
                "      \"longitude\": -56.146317,\n" +
                "      \"uncertainty\": 0.0\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"vlrNumber\": 59899000231,\n" +
                "    \"mscNumber\": 5982123007\n" +
                "  },\n" +
                "  \"EPSLocationInformation\": {\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"eci\": 614146,\n" +
                "      \"eNBId\": 2399,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"tac\": 109\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.909734,\n" +
                "      \"longitude\": -56.146317,\n" +
                "      \"uncertainty\": 0.0\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"mmeName\": \"mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org\"\n" +
                "  },\n" +
                "  \"subscriberState\": \"camelBusy\",\n" +
                "  \"MNPInfoResult\": {\n" +
                "    \"mnpStatus\": \"ownNumberNotPortedOut\",\n" +
                "    \"mnpMsisdn\": 59899077937,\n" +
                "    \"mnpImsi\": 748026871012345,\n" +
                "    \"mnpRouteingNumber\": \"598123\"\n" +
                "  },\n" +
                "  \"msClassmark\": \"393A52\"\n" +
                "}";

            processAtiOrPsi(atiOrPsiJsonLine3);

            String atiOrPsiJsonLine4 = "{\n" +
                "  \"network\": \"GSM/UMTS\",\n" +
                "  \"protocol\": \"MAP\",\n" +
                "  \"operation\": \"ATI\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"msisdn\": 59899077937,\n" +
                "  \"imei\": \"011714004661050\",\n" +
                "  \"CSLocationInformation\": {\n" +
                "    \"LocationNumber\": {\n" +
                "      \"oddFlag\": false,\n" +
                "      \"natureOfAddressIndicator\": 4,\n" +
                "      \"internalNetworkNumberIndicator\": 1,\n" +
                "      \"numberingPlanIndicator\": 1,\n" +
                "      \"addressPresentationRestrictedIndicator\": 1,\n" +
                "      \"screeningIndicator\": 3,\n" +
                "      \"address\": 819203961904\n" +
                "    },\n" +
                "    \"CGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 10,\n" +
                "      \"lac\": 9501,\n" +
                "      \"ci\": 35100\n" +
                "    },\n" +
                "    \"saiPresent\": false,\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.905614,\n" +
                "      \"longitude\": -55.042191,\n" +
                "      \"uncertainty\": 3.31,\n" +
                "      \"confidence\": 10,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"vlrNumber\": 59899000231,\n" +
                "    \"mscNumber\": 5982123007\n" +
                "  },\n" +
                "  \"subscriberState\": \"assumedIdle\",\n" +
                "  \"MNPInfoResult\": {\n" +
                "    \"mnpStatus\": \"ownNumberNotPortedOut\",\n" +
                "    \"mnpMsisdn\": 59899077937,\n" +
                "    \"mnpImsi\": 748026871012345,\n" +
                "    \"mnpRouteingNumber\": \"598123\"\n" +
                "  },\n" +
                "  \"msClassmark\": \"393A52\"\n" +
                "}";

            processAtiOrPsi(atiOrPsiJsonLine4);

            String atiOrPsiJsonLine5 = "{\n" +
                "  \"network\": \"GSM/UMTS\",\n" +
                "  \"protocol\": \"MAP\",\n" +
                "  \"operation\": \"ATI\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"msisdn\": 59899077937,\n" +
                "  \"imei\": \"011714004661050\",\n" +
                "  \"CSLocationInformation\": {\n" +
                "    \"LocationNumber\": {\n" +
                "      \"oddFlag\": false,\n" +
                "      \"natureOfAddressIndicator\": 4,\n" +
                "      \"internalNetworkNumberIndicator\": 1,\n" +
                "      \"numberingPlanIndicator\": 1,\n" +
                "      \"addressPresentationRestrictedIndicator\": 1,\n" +
                "      \"screeningIndicator\": 3,\n" +
                "      \"address\": 819203961904\n" +
                "    },\n" +
                "    \"SAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"lac\": 101,\n" +
                "      \"sac\": 10263\n" +
                "    },\n" +
                "    \"saiPresent\": true,\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.909734,\n" +
                "      \"longitude\": -56.146317,\n" +
                "      \"uncertainty\": 0.0\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"vlrNumber\": 59899000231,\n" +
                "    \"mscNumber\": 5982123007\n" +
                "  },\n" +
                "  \"EPSLocationInformation\": {\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"eci\": 614146,\n" +
                "      \"eNBId\": 2399,\n" +
                "      \"ci\": 2\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"tac\": 109\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -34.909734,\n" +
                "      \"longitude\": -56.146317,\n" +
                "      \"uncertainty\": 0.0\n" +
                "    },\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"mmeName\": \"mmec03.mmegi3000.mme.epc.mnc002.mcc748.3gppnetwork.org\"\n" +
                "  },\n" +
                "  \"subscriberState\": \"camelBusy\",\n" +
                "  \"MNPInfoResult\": {\n" +
                "    \"mnpStatus\": \"ownNumberNotPortedOut\",\n" +
                "    \"mnpMsisdn\": 59899077937,\n" +
                "    \"mnpImsi\": 748026871012345,\n" +
                "    \"mnpRouteingNumber\": \"598123\"\n" +
                "  },\n" +
                "  \"msClassmark\": \"393A52\"\n" +
                "}";

            processAtiOrPsi(atiOrPsiJsonLine5);

            String udrJsonLine = "{\n" +
                "  \"network\": \"IMS\",\n" +
                "  \"protocol\": \"Diameter Sh\",\n" +
                "  \"operation\": \"UDR-UDA\",\n" +
                "  \"result\": \"SUCCESS\",\n" +
                "  \"PublicIdentifiers\": {\n" +
                "    \"imsPublicIdentity\": \"sip:john.doe@hp.com\",\n" +
                "    \"msisdn\": 59898077937\n" +
                "  },\n" +
                "  \"CSLocationInformation\": {\n" +
                "    \"LocationNumber\": {\n" +
                "      \"oddFlag\": true,\n" +
                "      \"natureOfAddressIndicator\": 4,\n" +
                "      \"internalNetworkNumberIndicator\": 1,\n" +
                "      \"numberingPlanIndicator\": 1,\n" +
                "      \"addressPresentationRestrictedIndicator\": 1,\n" +
                "      \"screeningIndicator\": 3,\n" +
                "      \"address\": 56034254999\n" +
                "    },\n" +
                "    \"CGI\": {\n" +
                "      \"mcc\": 732,\n" +
                "      \"mnc\": 103,\n" +
                "      \"lac\": 1,\n" +
                "      \"ci\": 20042\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": 19.484425,\n" +
                "      \"longitude\": -99.239695,\n" +
                "      \"uncertainty\": 0.0\n" +
                "    },\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -24.010009,\n" +
                "      \"longitude\": 110.009859,\n" +
                "      \"uncertainty\": 98.347059,\n" +
                "      \"confidence\": 1,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"mscNumber\": 598978934,\n" +
                "    \"vlrNumber\": 598978935,\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"csgId\": \"8191\",\n" +
                "    \"EPSLocationInformation\": {\n" +
                "      \"ECGI\": {\n" +
                "        \"mcc\": 502,\n" +
                "        \"mnc\": 19,\n" +
                "        \"eci\": 38676245,\n" +
                "        \"eNBId\": 151079,\n" +
                "        \"ci\": 21\n" +
                "      },\n" +
                "      \"TAI\": {\n" +
                "        \"mcc\": 502,\n" +
                "        \"mnc\": 19,\n" +
                "        \"tac\": 774\n" +
                "      },\n" +
                "      \"LocalTimeZone\": {\n" +
                "        \"timeZone\": \"-5\",\n" +
                "        \"daylightSavingTime\": 0\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"PSLocationInformation\": {\n" +
                "    \"CGI\": {\n" +
                "      \"mcc\": 732,\n" +
                "      \"mnc\": 103,\n" +
                "      \"lac\": 1,\n" +
                "      \"ci\": 20042\n" +
                "    },\n" +
                "    \"RAI\": {\n" +
                "      \"mcc\": 748,\n" +
                "      \"mnc\": 1,\n" +
                "      \"lac\": 14645,\n" +
                "      \"rac\": 50\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -23.291026,\n" +
                "      \"longitude\": 109.977801,\n" +
                "      \"uncertainty\": 45.599173\n" +
                "    },\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -24.010009,\n" +
                "      \"longitude\": 110.009859,\n" +
                "      \"uncertainty\": 98.347059,\n" +
                "      \"confidence\": 0,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"sgsnNumber\": 598978936,\n" +
                "    \"currentLocationRetrieved\": false,\n" +
                "    \"ageOfLocationInformation\": 5,\n" +
                "    \"csgId\": \"8191\",\n" +
                "    \"VisitedPLMNId\": {\n" +
                "      \"mcc\": 951,\n" +
                "      \"mnc\": 820\n" +
                "    },\n" +
                "    \"LocalTimeZone\": {\n" +
                "      \"timeZone\": \"+1\",\n" +
                "      \"daylightSavingTime\": 2\n" +
                "    },\n" +
                "    \"ratType\": \"GERAN\"\n" +
                "  },\n" +
                "  \"EPSLocationInformation\": {\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 502,\n" +
                "      \"mnc\": 19,\n" +
                "      \"eci\": 38676245,\n" +
                "      \"eNBId\": 151079,\n" +
                "      \"ci\": 21\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 502,\n" +
                "      \"mnc\": 19,\n" +
                "      \"tac\": 774\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -23.291026,\n" +
                "      \"longitude\": 109.977801,\n" +
                "      \"uncertainty\": 45.599173\n" +
                "    },\n" +
                "    \"GeodeticInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -24.010009,\n" +
                "      \"longitude\": 110.009859,\n" +
                "      \"uncertainty\": 98.347059,\n" +
                "      \"confidence\": 0,\n" +
                "      \"screeningAndPresentationIndicators\": 3\n" +
                "    },\n" +
                "    \"mmeName\": \"MMEC18.MMEGI8001.MME.EPC.MNC019.MCC502.3GPPNETWORK.ORG\",\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"csgId\": \"8191\",\n" +
                "    \"VisitedPLMNId\": {\n" +
                "      \"mcc\": 951,\n" +
                "      \"mnc\": 830\n" +
                "    },\n" +
                "    \"LocalTimeZone\": {\n" +
                "      \"timeZone\": \"-9\",\n" +
                "      \"daylightSavingTime\": 0\n" +
                "    },\n" +
                "    \"ratType\": \"EUTRAN\"\n" +
                "  },\n" +
                "  \"5GSLocationInformation\": {\n" +
                "    \"NCGI\": {\n" +
                "      \"mcc\": 951,\n" +
                "      \"mnc\": 80,\n" +
                "      \"nci\": 2000246128\n" +
                "    },\n" +
                "    \"ECGI\": {\n" +
                "      \"mcc\": 502,\n" +
                "      \"mnc\": 19,\n" +
                "      \"eci\": 38676245,\n" +
                "      \"eNBId\": 151079,\n" +
                "      \"ci\": 21\n" +
                "    },\n" +
                "    \"TAI\": {\n" +
                "      \"mcc\": 502,\n" +
                "      \"mnc\": 19,\n" +
                "      \"tac\": 774\n" +
                "    },\n" +
                "    \"GeographicalInformation\": {\n" +
                "      \"typeOfShape\": \"EllipsoidPointWithUncertaintyCircle\",\n" +
                "      \"latitude\": -23.291026,\n" +
                "      \"longitude\": 109.977801,\n" +
                "      \"uncertainty\": 45.599173\n" +
                "    },\n" +
                "    \"amfAddress\": \"amf1.cluster1.net2.amf.5gc.mnc012.mcc345.3gppnetwork.org\",\n" +
                "    \"smsfAddress\": \"smset12.smsf01.5gc.mnc012.mcc345.3gppnetwork.org\",\n" +
                "    \"currentLocationRetrieved\": true,\n" +
                "    \"ageOfLocationInformation\": 0,\n" +
                "    \"VisitedPLMNId\": {\n" +
                "      \"mcc\": 951,\n" +
                "      \"mnc\": 800\n" +
                "    },\n" +
                "    \"LocalTimeZone\": {\n" +
                "      \"timeZone\": \"-3\",\n" +
                "      \"daylightSavingTime\": 0\n" +
                "    },\n" +
                "    \"ratType\": \"NR\"\n" +
                "  }\n" +
                "}";


        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    private static void procesRiaPla(String rirPlrRJsonLine) {
        HashMap<String, String> rirPlrResponse = new HashMap<>();
        JsonElement jsonElement = new JsonParser().parse(rirPlrRJsonLine);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        logger.info("\n\n*** Diameter RIR/RIA PLR/PLA ***");
        if (jsonObject.get("clientReferenceNumber") != null)
            rirPlrResponse.put("clientReferenceNumber", jsonObject.get("clientReferenceNumber").getAsString());

        if (jsonObject.get("lcsReferenceNumber") != null)
            rirPlrResponse.put("lcsReferenceNumber", jsonObject.get("lcsReferenceNumber").getAsString());

        if (jsonObject.getAsJsonObject("Routing-Info-Answer") != null) {
            if (jsonObject.getAsJsonObject("Routing-Info-Answer").get("msisdn") != null) {
                rirPlrResponse.put("msisdn", jsonObject.getAsJsonObject("Routing-Info-Answer").get("msisdn").getAsString());
                logger.info("msisdn: "+jsonObject.getAsJsonObject("Routing-Info-Answer").get("msisdn").getAsString());
            }
            if (jsonObject.getAsJsonObject("Routing-Info-Answer").get("imsi") != null)
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").get("imsi").getAsString() != null) {
                    rirPlrResponse.put("imsi", jsonObject.getAsJsonObject("Routing-Info-Answer").get("imsi").getAsString());
                    logger.info("imsi: "+jsonObject.getAsJsonObject("Routing-Info-Answer").get("imsi").getAsString());
                }
            if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode") != null) {
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("mmeName") != null) {
                    rirPlrResponse.put("mmeName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("mmeName").getAsString());
                    logger.info("mmeName: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("mmeName").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("mmeRealm") != null) {
                    rirPlrResponse.put("mmeRealm", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("mmeRealm").getAsString());
                    logger.info("mmeRealm: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("mmeRealm").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("mscNumber") != null) {
                    rirPlrResponse.put("mscNumber", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("mscNumber").getAsString());
                    logger.info("mscNumber: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("mscNumber").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("sgsnNumber") != null) {
                    rirPlrResponse.put("sgsnNumber", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("sgsnNumber").getAsString());
                    logger.info("sgsnNumber: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("sgsnNumber").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("sgsnName") != null) {
                    rirPlrResponse.put("sgsnName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("sgsnName").getAsString());
                    logger.info("sgsnName: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("sgsnName").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("sgsnRealm") != null) {
                    rirPlrResponse.put("sgsnRealm", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("sgsnRealm").getAsString());
                    logger.info("sgsnRealm: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("sgsnRealm").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("3GPPAAAServerName") != null) {
                    rirPlrResponse.put("tgppAAAServerName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("3GPPAAAServerName").getAsString());
                    logger.info("tgppAAAServerName: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("3GPPAAAServerName").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").get("gmlcAddress") != null) {
                    rirPlrResponse.put("gmlcAddress", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("gmlcAddress").getAsString());
                    logger.info("gmlcAddress: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("AdditionalServingNode").
                        get("gmlcAddress").getAsString());
                }
            }
            if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode") != null) {
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("mmeName") != null) {
                    rirPlrResponse.put("mmeName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("mmeName").getAsString());
                    logger.info("mmeName: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("mmeName").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("mmeRealm") != null) {
                    rirPlrResponse.put("mmeRealm", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("mmeRealm").getAsString());
                    logger.info("mmeRealm: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("mmeRealm").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("mscNumber") != null) {
                    rirPlrResponse.put("mscNumber", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("mscNumber").getAsString());
                    logger.info("mscNumber: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("mscNumber").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("sgsnNumber") != null) {
                    rirPlrResponse.put("sgsnNumber", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("sgsnNumber").getAsString());
                    logger.info("sgsnNumber: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("sgsnNumber").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("sgsnName") != null) {
                    rirPlrResponse.put("sgsnName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("sgsnName").getAsString());
                    logger.info("sgsnName: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("sgsnName").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("sgsnRealm") != null) {
                    rirPlrResponse.put("sgsnRealm", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("sgsnRealm").getAsString());
                    logger.info("sgsnRealm: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("sgsnRealm").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("3GPPAAAServerName") != null) {
                    rirPlrResponse.put("tgppAAAServerName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("3GPPAAAServerName").getAsString());
                    rirPlrResponse.put("tgppAAAServerName", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("3GPPAAAServerName").getAsString());
                }
                if (jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").get("gmlcAddress") != null) {
                    rirPlrResponse.put("gmlcAddress", jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("gmlcAddress").getAsString());
                    logger.info("gmlcAddress: "+jsonObject.getAsJsonObject("Routing-Info-Answer").getAsJsonObject("ServingNode").
                        get("gmlcAddress").getAsString());
                }
            }
            if (jsonObject.getAsJsonObject("Routing-Info-Answer").get("gmlcAddress") != null) {
                rirPlrResponse.put("gmlcAddress", jsonObject.getAsJsonObject("Routing-Info-Answer").get("gmlcAddress").getAsString());
                logger.info("gmlcAddress: "+jsonObject.getAsJsonObject("Routing-Info-Answer").get("gmlcAddress").getAsString());
            }
            if (jsonObject.getAsJsonObject("Routing-Info-Answer").get("pprAddress") != null) {
                rirPlrResponse.put("pprAddress", jsonObject.getAsJsonObject("Routing-Info-Answer").get("pprAddress").getAsString());
                logger.info("pprAddress: "+jsonObject.getAsJsonObject("Routing-Info-Answer").get("pprAddress").getAsString());
            }
        }

        if (jsonObject.getAsJsonObject("Provide-Location-Answer") != null) {

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("latitude") != null) {
                    rirPlrResponse.put("latitude", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("latitude").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("longitude") != null) {
                    rirPlrResponse.put("longitude", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("longitude").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("typeOfShape") != null) {
                    if (!jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("typeOfShape").getAsString().isEmpty()) {
                        rirPlrResponse.put("typeOfShape", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                            get("typeOfShape").getAsString());
                    }
                    if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("typeOfShape").getAsString().equalsIgnoreCase("Polygon")) {
                        if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("numberOfPoints") != null) {
                            int numberOfPoints = jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                                get("numberOfPoints").getAsInt();
                            rirPlrResponse.put("numberOfPoints", String.valueOf(numberOfPoints));
                        }
                        if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                            getAsJsonObject("polygonCentroid") != null) {
                            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                                getAsJsonObject("polygonCentroid").get("latitude") != null) {
                                rirPlrResponse.put("latitude", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                                    getAsJsonObject("polygonCentroid").get("latitude").getAsString());
                            }
                            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                                getAsJsonObject("polygonCentroid").get("longitude") != null) {
                                rirPlrResponse.put("longitude", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                                    getAsJsonObject("polygonCentroid").get("longitude").getAsString());
                            }
                        }
                    }
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("uncertainty") != null) {
                    rirPlrResponse.put("uncertainty", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("uncertainty").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("uncertaintySemiMajorAxis") != null) {
                    rirPlrResponse.put("uncertaintySemiMajorAxis", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("uncertaintySemiMajorAxis").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("uncertaintySemiMinorAxis") != null) {
                    rirPlrResponse.put("uncertaintySemiMinorAxis", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("uncertaintySemiMinorAxis").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("angleOfMajorAxis") != null) {
                    rirPlrResponse.put("angleOfMajorAxis", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("angleOfMajorAxis").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("confidence") != null) {
                    rirPlrResponse.put("confidence", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("confidence").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("altitude") != null) {
                    rirPlrResponse.put("altitude", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("altitude").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("uncertaintyAltitude") != null) {
                    rirPlrResponse.put("uncertaintyAltitude", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("uncertaintyAltitude").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("innerRadius") != null) {
                    rirPlrResponse.put("innerRadius", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("innerRadius").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("uncertaintyInnerRadius") != null) {
                    rirPlrResponse.put("uncertaintyInnerRadius", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("uncertaintyInnerRadius").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("offsetAngle") != null) {
                    rirPlrResponse.put("offsetAngle", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("offsetAngle").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").get("includedAngle") != null) {
                    rirPlrResponse.put("includedAngle", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("LocationEstimate").
                        get("includedAngle").getAsString());
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").get("accuracyFulfilmentIndicator") != null)
                rirPlrResponse.put("accuracyFulfilmentIndicator", jsonObject.getAsJsonObject("Provide-Location-Answer").
                    get("accuracyFulfilmentIndicator").getAsString());

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").get("ageOfLocationEstimate") != null)
                rirPlrResponse.put("ageOfLocationEstimate", jsonObject.getAsJsonObject("Provide-Location-Answer").get("ageOfLocationEstimate").getAsString());

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("mcc") != null)
                    rirPlrResponse.put("mcc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("mcc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("mnc") != null)
                    rirPlrResponse.put("mnc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("mnc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("lac") != null)
                    rirPlrResponse.put("lac", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("lac").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("ci") != null)
                    rirPlrResponse.put("ci", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CGI").get("ci").getAsString());
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("mcc") != null)
                    rirPlrResponse.put("mcc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("mcc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("mnc") != null)
                    rirPlrResponse.put("mnc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("mnc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("lac") != null)
                    rirPlrResponse.put("lac", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("lac").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("sac") != null)
                    rirPlrResponse.put("sac", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("SAI").get("sac").getAsString());
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("mcc") != null)
                    rirPlrResponse.put("mcc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("mcc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("mnc") != null)
                    rirPlrResponse.put("mnc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("mnc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("eci") != null)
                    rirPlrResponse.put("ei", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("eci").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("eNBId") != null)
                    rirPlrResponse.put("eNBId", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("eNBId").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("ci") != null)
                    rirPlrResponse.put("ci", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ECGI").get("ci").getAsString());
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").get("mcc") != null)
                    rirPlrResponse.put("mcc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").
                        get("mcc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").get("mnc") != null)
                    rirPlrResponse.put("mnc", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").
                        get("mnc").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").get("eci") != null)
                    rirPlrResponse.put("ei", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").
                        get("eci").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").get("eNBId") != null)
                    rirPlrResponse.put("eNBId", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").
                        get("eNBId").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").get("ci") != null)
                    rirPlrResponse.put("ci", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").
                        get("ci").getAsString());
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").get("cellPortionId") != null)
                    rirPlrResponse.put("cellPortionId", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("ESMLCCellInfo").
                        get("cellPortionId").getAsString());
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("GeranPositioningData") != null) {
                for (Map.Entry<String, JsonElement> positioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                    getAsJsonObject("GeranPositioningData").entrySet()) {
                    for (Map.Entry<String, JsonElement> positioningDataSetItem : positioningDataSet.getValue().getAsJsonObject().entrySet()) {
                        if (positioningDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                            rirPlrResponse.put("PositioningMethod", positioningDataSetItem.getValue().getAsJsonObject().get("method").getAsString());
                            rirPlrResponse.put("RadioAccessTechnology", "GERAN");
                            logger.info("GeranPositioningData: " +positioningDataSetItem.getValue().getAsJsonObject().get("method").getAsString());

                        }
                    }
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("GeranGANSSPositioningData") != null) {
                for (Map.Entry<String, JsonElement> ganssPositioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                    getAsJsonObject("GeranGANSSPositioningData").entrySet()) {
                    for (Map.Entry<String, JsonElement> ganssDataSetItem : ganssPositioningDataSet.getValue().getAsJsonObject().entrySet()) {
                        if (ganssDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                            rirPlrResponse.put("PositioningMethod",
                                ganssDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                    concat(ganssDataSetItem.getValue().getAsJsonObject().get("ganssId").getAsString()));
                            rirPlrResponse.put("RadioAccessTechnology", "GERAN");
                            logger.info("GeranGANSSPositioningData: " +ganssDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                concat(ganssDataSetItem.getValue().getAsJsonObject().get("ganssId").getAsString()));
                        }
                    }
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("UtranPositioningData") != null) {
                for (Map.Entry<String, JsonElement> positioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                    getAsJsonObject("UtranPositioningData").entrySet()) {
                    for (Map.Entry<String, JsonElement> positioningDataSetItem : positioningDataSet.getValue().getAsJsonObject().entrySet()) {
                        if (positioningDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                            rirPlrResponse.put("PositioningMethod", positioningDataSetItem.getValue().getAsJsonObject().get("method").getAsString());
                            rirPlrResponse.put("RadioAccessTechnology", "UTRAN");
                            logger.info("UtranPositioningData: " +positioningDataSetItem.getValue().getAsJsonObject().get("method").getAsString());
                        }
                    }
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("UtranGANSSPositioningData") != null) {
                for (Map.Entry<String, JsonElement> ganssPositioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                    getAsJsonObject("UtranGANSSPositioningData").entrySet()) {
                    for (Map.Entry<String, JsonElement> ganssDataSetItem : ganssPositioningDataSet.getValue().getAsJsonObject().entrySet()) {
                        if (ganssDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                            rirPlrResponse.put("PositioningMethod",
                                ganssDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                    concat(ganssDataSetItem.getValue().getAsJsonObject().get("ganssId").getAsString()));
                            rirPlrResponse.put("RadioAccessTechnology", "UTRAN");
                            logger.info("UtranGANSSPositioningData: " +ganssDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                concat(ganssDataSetItem.getValue().getAsJsonObject().get("ganssId").getAsString()));
                        }
                    }
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("UtranAdditionalPositioningData") != null) {
                for (Map.Entry<String, JsonElement> addPositioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                    getAsJsonObject("UtranAdditionalPositioningData").entrySet()) {
                    for (Map.Entry<String, JsonElement> addDataSetItem : addPositioningDataSet.getValue().getAsJsonObject().entrySet()) {
                        if (addDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                            rirPlrResponse.put("PositioningMethod", addDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                concat(addDataSetItem.getValue().getAsJsonObject().get("posId").getAsString()));
                            rirPlrResponse.put("RadioAccessTechnology", "UTRAN");
                            logger.info("UtranAdditionalPositioningData: " +addDataSetItem.getValue().getAsJsonObject().get("method").getAsString().
                                concat(" ").concat(addDataSetItem.getValue().getAsJsonObject().get("posId").getAsString()));
                        }
                    }
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("EUtranPositioningData") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("EUtranPositioningData").
                    getAsJsonObject("GnssPositioningDataSet") != null) {
                    for (Map.Entry<String, JsonElement> ganssPositioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                        getAsJsonObject("EUtranPositioningData").entrySet()) {
                        for (Map.Entry<String, JsonElement> ganssDataSetItem : ganssPositioningDataSet.getValue().getAsJsonObject().entrySet()) {
                            if (ganssDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                                rirPlrResponse.put("PositioningMethod", ganssDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                    concat(ganssDataSetItem.getValue().getAsJsonObject().get("ganssId").getAsString()));
                                rirPlrResponse.put("RadioAccessTechnology", "EUTRAN");
                                logger.info("EUtranPositioningData GnssPositioningDataSet: " +ganssDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                    concat(ganssDataSetItem.getValue().getAsJsonObject().get("ganssId").getAsString()));
                            }
                        }
                    }
                } else if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("EUtranPositioningData").
                    getAsJsonObject("AdditionalPositioningDataSet") != null) {
                    for (Map.Entry<String, JsonElement> addPositioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                        getAsJsonObject("EUtranPositioningData").entrySet()) {
                        for (Map.Entry<String, JsonElement> addDataSetItem : addPositioningDataSet.getValue().getAsJsonObject().entrySet()) {
                            if (addDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                                rirPlrResponse.put("PositioningMethod", addDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                    concat(addDataSetItem.getValue().getAsJsonObject().get("posId").getAsString()));
                                rirPlrResponse.put("RadioAccessTechnology", "EUTRAN");
                                logger.info("EUtranPositioningData AdditionalPositioningDataSet" +addDataSetItem.getValue().getAsJsonObject().get("method").getAsString().concat(" ").
                                    concat(addDataSetItem.getValue().getAsJsonObject().get("posId").getAsString()));
                            }
                        }
                    }
                } else if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("EUtranPositioningData") != null) {
                    for (Map.Entry<String, JsonElement> positioningDataSet : jsonObject.getAsJsonObject("Provide-Location-Answer").
                        getAsJsonObject("EUtranPositioningData").entrySet()) {
                        for (Map.Entry<String, JsonElement> positioningDataSetItem : positioningDataSet.getValue().getAsJsonObject().entrySet()) {
                            if (positioningDataSetItem.getValue().getAsJsonObject().get("usage").getAsString().equals("3")) {
                                rirPlrResponse.put("PositioningMethod", positioningDataSetItem.getValue().getAsJsonObject().get("method").getAsString());
                                rirPlrResponse.put("RadioAccessTechnology", "EUTRAN");
                                logger.info("EUtranPositioningData PositioningDataSet: " +positioningDataSetItem.getValue().getAsJsonObject().get("method").getAsString());
                            }
                        }
                    }
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate") != null) {
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").get("horizontalSpeed") != null) {
                    rirPlrResponse.put("horizontalSpeed", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").
                        get("horizontalSpeed").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").get("bearing") != null) {
                    rirPlrResponse.put("bearing", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").
                        get("bearing").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").get("verticalSpeed") != null) {
                    rirPlrResponse.put("verticalSpeed", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").
                        get("verticalSpeed").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").get("uncertaintyHorizontalSpeed") != null) {
                    rirPlrResponse.put("uncertaintyHorizontalSpeed", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").
                        get("uncertaintyHorizontalSpeed").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").get("uncertaintyVerticalSpeed") != null) {
                    rirPlrResponse.put("uncertaintyVerticalSpeed", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").
                        get("uncertaintyVerticalSpeed").getAsString());
                }
                if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").get("velocityType") != null) {
                    rirPlrResponse.put("velocityType", jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("VelocityEstimate").
                        get("velocityType").getAsString());
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CivicAddress") != null) {
                JsonObject ca = jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("CivicAddress");
                StringBuilder civicAddress = new StringBuilder();
                if (ca != null) {
                    if (ca.get("NAM") != null)
                        civicAddress.append(ca.get("NAM").getAsString()).append(", ");
                    if (ca.get("LMK") != null)
                        civicAddress.append(ca.get("LMK").getAsString()).append(", ");
                    if (ca.get("HNS") != null)
                        civicAddress.append(ca.get("HNS").getAsString()).append(", ");
                    if (ca.get("HNO") != null)
                        civicAddress.append(ca.get("HNO").getAsString()).append(", ");
                    if (ca.get("STS") != null)
                        civicAddress.append(ca.get("STS").getAsString()).append(", ");
                    if (ca.get("POD") != null)
                        civicAddress.append(ca.get("POD").getAsString()).append(", ");
                    if (ca.get("PRD") != null)
                        civicAddress.append(ca.get("PRD").getAsString()).append(", ");
                    if (ca.get("A6") != null)
                        civicAddress.append(ca.get("A6").getAsString()).append(", ");
                    if (ca.get("FLR") != null)
                        civicAddress.append(ca.get("FLR").getAsString()).append(", ");
                    if (ca.get("FLR") != null)
                        civicAddress.append(ca.get("FLR").getAsString()).append(", ");
                    if (ca.get("LOC") != null)
                        civicAddress.append(ca.get("LOC").getAsString()).append(", ");
                    if (ca.get("A5") != null)
                        civicAddress.append(ca.get("A5").getAsString()).append(", ");
                    if (ca.get("A4") != null)
                        civicAddress.append(ca.get("A4").getAsString()).append(", ");
                    if (ca.get("A3") != null)
                        civicAddress.append(ca.get("A3").getAsString()).append(", ");
                    if (ca.get("A2") != null)
                        civicAddress.append(ca.get("A2").getAsString()).append(", ");
                    if (ca.get("A1") != null)
                        civicAddress.append(ca.get("A1").getAsString()).append(", ");
                    if (ca.get("PC") != null)
                        civicAddress.append(ca.get("PC").getAsString()).append(", ");
                    if (ca.get("country") != null)
                        civicAddress.append(ca.get("country").getAsString());
                    rirPlrResponse.put("civicAddress", String.valueOf(civicAddress));
                    logger.info("CivicAddress : " + civicAddress);
                    //   +----------------------+----------------------+---------------------+
                    //   | Label                | Description          | Example             |
                    //   +----------------------+----------------------+---------------------+
                    //   | country              | The country is       | US                  |
                    //   |                      | identified by the    |                     |
                    //   |                      | two-letter ISO 3166  |                     |
                    //   |                      | code.                |                     |
                    //   |                      |                      |                     |
                    //   | A1                   | national             | New York            |
                    //   |                      | subdivisions (state, |                     |
                    //   |                      | region, province,    |                     |
                    //   |                      | prefecture)          |                     |
                    //   |                      |                      |                     |
                    //   | A2                   | county, parish, gun  | King's County       |
                    //   |                      | (JP), district (IN)  |                     |
                    //   |                      |                      |                     |
                    //   | A3                   | city, township, shi  | New York            |
                    //   |                      | (JP)                 |                     |
                    //   |                      |                      |                     |
                    //   | A4                   | city division,       | Manhattan           |
                    //   |                      | borough, city        |                     |
                    //   |                      | district, ward, chou |                     |
                    //   |                      | (JP)                 |                     |
                    //   |                      |                      |                     |
                    //   | A5                   | neighborhood, block  | Morningside Heights |
                    //   |                      |                      |                     |
                    //   | A6                   | street               | Broadway            |
                    //   |                      |                      |                     |
                    //   | PRD                  | Leading street       | N, W                |
                    //   |                      | direction            |                     |
                    //   |                      |                      |                     |
                    //   | POD                  | Trailing street      | SW                  |
                    //   |                      | suffix               |                     |
                    //   |                      |                      |                     |
                    //   | STS                  | Street suffix        | Avenue, Platz,      |
                    //   |                      |                      | Street              |
                    //   |                      |                      |                     |
                    //   | HNO                  | House number,        | 123                 |
                    //   |                      | numeric part only.   |                     |
                    //   |                      |                      |                     |
                    //   | HNS                  | House number suffix  | A, 1/2              |
                    //   |                      |                      |                     |
                    //   | LMK                  | Landmark or vanity   | Low Library         |
                    //   |                      | address              |                     |
                    //   |                      |                      |                     |
                    //   | LOC                  | Additional location  | Room 543            |
                    //   |                      | information          |                     |
                    //   |                      |                      |                     |
                    //   | FLR                  | Floor                | 5                   |
                    //   |                      |                      |                     |
                    //   | NAM                  | Name (residence,     | Joe's Barbershop    |
                    //   |                      | business or office   |                     |
                    //   |                      | occupant)            |                     |
                    //   |                      |                      |                     |
                    //   | PC                   | Postal code          | 10027-0401          |
                    //   +----------------------+----------------------+---------------------+
                }
            }

            if (jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("BarometricPressure") != null) {
                int pressure = jsonObject.getAsJsonObject("Provide-Location-Answer").getAsJsonObject("BarometricPressure").get("measuredPa").getAsInt();
                rirPlrResponse.put("barometricPressure", String.valueOf(pressure));
                logger.info("BarometricPressure: " +pressure);
            }
        }

        if (jsonObject.get("errorReason") != null)
            rirPlrResponse.put("errorReason", jsonObject.get("errorReason").getAsString());

    }


    private static void processAtiOrPsi(String atiOrPsiJsonLine) {
        HashMap<String, String> atiOrPsiResponse = new HashMap<>();
        JsonElement jsonElement = new JsonParser().parse(atiOrPsiJsonLine);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        logger.info("\n\n*** ATI or PSI ***");
        if (jsonObject.getAsJsonObject("CSLocationInformation") != null) {
            logger.info("CSLocationInformation");
            JsonObject csLocInfo = jsonObject.getAsJsonObject("CSLocationInformation");
            if (csLocInfo.getAsJsonObject("LocationNumber") != null) {
                logger.info("LocationNumber");
                if (csLocInfo.getAsJsonObject("LocationNumber").get("oddFlag") != null) {
                    atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("oddFlag").getAsString());
                    logger.info("oddFlag:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("oddFlag").getAsString()));

                }
                if (csLocInfo.getAsJsonObject("LocationNumber").get("natureOfAddressIndicator") != null) {
                    atiOrPsiResponse.put("nai", csLocInfo.getAsJsonObject("LocationNumber").
                        get("natureOfAddressIndicator").getAsString());
                    logger.info("natureOfAddressIndicator:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("natureOfAddressIndicator").getAsString()));
                }
                if (csLocInfo.getAsJsonObject("LocationNumber").get("internalNetworkNumberIndicator") != null) {
                    atiOrPsiResponse.put("inni", csLocInfo.getAsJsonObject("LocationNumber").
                        get("internalNetworkNumberIndicator").getAsString());
                    logger.info("internalNetworkNumberIndicator:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("internalNetworkNumberIndicator").getAsString()));
                }
                if (csLocInfo.getAsJsonObject("LocationNumber").get("numberingPlanIndicator") != null) {
                    atiOrPsiResponse.put("npi", csLocInfo.getAsJsonObject("LocationNumber").
                        get("numberingPlanIndicator").getAsString());
                    logger.info("numberingPlanIndicator:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("numberingPlanIndicator").getAsString()));
                }
                if (csLocInfo.getAsJsonObject("LocationNumber").get("addressRepresentationRestrictedIndicator") != null) {
                    atiOrPsiResponse.put("arpi", csLocInfo.getAsJsonObject("LocationNumber").
                        get("addressRepresentationRestrictedIndicator").getAsString());
                    logger.info("addressRepresentationRestrictedIndicator:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("addressRepresentationRestrictedIndicator").getAsString()));
                }
                if (csLocInfo.getAsJsonObject("LocationNumber").get("screeningIndicator") != null) {
                    atiOrPsiResponse.put("si", csLocInfo.getAsJsonObject("LocationNumber").
                        get("screeningIndicator").getAsString());
                    logger.info("screeningIndicator:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("screeningIndicator").getAsString()));
                }
                if (csLocInfo.getAsJsonObject("LocationNumber").get("address") != null) {
                    atiOrPsiResponse.put("address", csLocInfo.getAsJsonObject("LocationNumber").
                        get("address").getAsString());
                    logger.info("address:"+atiOrPsiResponse.put("oddFlag", csLocInfo.getAsJsonObject("LocationNumber").
                        get("address").getAsString()));
                }
            }
            if (csLocInfo.getAsJsonObject("CGI") != null) {
                if (csLocInfo.getAsJsonObject("CGI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", csLocInfo.getAsJsonObject("CGI").get("mcc").getAsString());
                if (csLocInfo.getAsJsonObject("CGI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", csLocInfo.getAsJsonObject("CGI").get("mnc").getAsString());
                if (csLocInfo.getAsJsonObject("CGI").get("lac") != null)
                    atiOrPsiResponse.put("lac", csLocInfo.getAsJsonObject("CGI").get("lac").getAsString());
                if (csLocInfo.getAsJsonObject("CGI").get("ci") != null)
                    atiOrPsiResponse.put("ci", csLocInfo.getAsJsonObject("CGI").get("ci").getAsString());
                logger.info("CSLocationInformation CGI mcc="+csLocInfo.getAsJsonObject("CGI").get("mcc").getAsString());
                logger.info("CSLocationInformation CGI mnc="+csLocInfo.getAsJsonObject("CGI").get("mnc").getAsString());
                logger.info("CSLocationInformation CGI lac="+csLocInfo.getAsJsonObject("CGI").get("lac").getAsString());
                logger.info("CSLocationInformation CGI lac="+csLocInfo.getAsJsonObject("CGI").get("ci").getAsString());

            }
            if (csLocInfo.getAsJsonObject("SAI") != null) {
                if (csLocInfo.getAsJsonObject("SAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", csLocInfo.getAsJsonObject("SAI").get("mcc").getAsString());
                if (csLocInfo.getAsJsonObject("SAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", csLocInfo.getAsJsonObject("SAI").get("mnc").getAsString());
                if (csLocInfo.getAsJsonObject("SAI").get("lac") != null)
                    atiOrPsiResponse.put("lac", csLocInfo.getAsJsonObject("SAI").get("lac").getAsString());
                if (csLocInfo.getAsJsonObject("SAI").get("sac") != null)
                    atiOrPsiResponse.put("sac", csLocInfo.getAsJsonObject("SAI").get("sac").getAsString());
                logger.info("CSLocationInformation SAI mcc="+csLocInfo.getAsJsonObject("SAI").get("mcc").getAsString());
                logger.info("CSLocationInformation SAI mnc="+csLocInfo.getAsJsonObject("SAI").get("mnc").getAsString());
                logger.info("CSLocationInformation SAI lac="+csLocInfo.getAsJsonObject("SAI").get("lac").getAsString());
                logger.info("CSLocationInformation SAI lac="+csLocInfo.getAsJsonObject("SAI").get("sac").getAsString());
            }
            if (csLocInfo.getAsJsonObject("LAI") != null) {
                if (csLocInfo.getAsJsonObject("LAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", csLocInfo.getAsJsonObject("LAI").get("mcc").getAsString());
                if (csLocInfo.getAsJsonObject("LAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", csLocInfo.getAsJsonObject("LAI").get("mnc").getAsString());
                if (csLocInfo.getAsJsonObject("LAI").get("lac") != null)
                    atiOrPsiResponse.put("lac", csLocInfo.getAsJsonObject("LAI").get("lac").getAsString());
                logger.info("CSLocationInformation LAI mcc="+csLocInfo.getAsJsonObject("LAI").get("mcc").getAsString());
                logger.info("CSLocationInformation LAI mnc="+csLocInfo.getAsJsonObject("LAI").get("mnc").getAsString());
                logger.info("CSLocationInformation LAI lac="+csLocInfo.getAsJsonObject("LAI").get("lac").getAsString());
            }
            if (csLocInfo.getAsJsonObject("GeographicalInformation") != null) {
                if (csLocInfo.getAsJsonObject("GeographicalInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", csLocInfo.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                if (csLocInfo.getAsJsonObject("GeographicalInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", csLocInfo.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                if (csLocInfo.getAsJsonObject("GeographicalInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", csLocInfo.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                if (csLocInfo.getAsJsonObject("GeographicalInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", csLocInfo.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
                logger.info("CSLocationInformation GeographicalInformation typeOfShape="+csLocInfo.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                logger.info("CSLocationInformation GeographicalInformation latitude="+csLocInfo.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                logger.info("CSLocationInformation GeographicalInformation latitude="+csLocInfo.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                logger.info("CSLocationInformation GeographicalInformation uncertainty="+csLocInfo.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
            }
            if (csLocInfo.getAsJsonObject("GeodeticInformation") != null) {
                if (csLocInfo.getAsJsonObject("GeodeticInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", csLocInfo.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                if (csLocInfo.getAsJsonObject("GeodeticInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", csLocInfo.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                if (csLocInfo.getAsJsonObject("GeodeticInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", csLocInfo.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                if (csLocInfo.getAsJsonObject("GeodeticInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", csLocInfo.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                if (csLocInfo.getAsJsonObject("GeodeticInformation").get("confidence") != null)
                    atiOrPsiResponse.put("confidence", csLocInfo.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                if (csLocInfo.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators") != null)
                    atiOrPsiResponse.put("screeningAndPresentationIndicators", csLocInfo.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                logger.info("CSLocationInformation GeodeticInformation typeOfShape="+csLocInfo.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                logger.info("CSLocationInformation GeodeticInformation latitude="+csLocInfo.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                logger.info("CSLocationInformation GeodeticInformation latitude="+csLocInfo.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                logger.info("CSLocationInformation GeodeticInformation uncertainty="+csLocInfo.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                logger.info("CSLocationInformation GeodeticInformation confidence="+csLocInfo.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                logger.info("CSLocationInformation GeodeticInformation screeningAndPresentationIndicators="+csLocInfo.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
            }
            if (csLocInfo.getAsJsonObject("EPSLocationInformation") != null) {
                logger.info("EPSLocationInformation");
                JsonObject epsLocInfo = csLocInfo.getAsJsonObject("EPSLocationInformation");
                if (epsLocInfo.getAsJsonObject("TAI") != null) {
                    if (epsLocInfo.getAsJsonObject("TAI").get("mcc") != null)
                        atiOrPsiResponse.put("mcc", epsLocInfo.getAsJsonObject("TAI").get("mcc").getAsString());
                    if (epsLocInfo.getAsJsonObject("TAI").get("mnc") != null)
                        atiOrPsiResponse.put("mnc", epsLocInfo.getAsJsonObject("TAI").get("mnc").getAsString());
                    if (epsLocInfo.getAsJsonObject("TAI").get("tac") != null)
                        atiOrPsiResponse.put("tac", epsLocInfo.getAsJsonObject("TAI").get("tac").getAsString());
                    logger.info("EPSLocationInformation TAI mcc="+epsLocInfo.getAsJsonObject("TAI").get("mcc").getAsString());
                    logger.info("EPSLocationInformation TAI mnc="+epsLocInfo.getAsJsonObject("TAI").get("mnc").getAsString());
                    logger.info("EPSLocationInformation TAI tac="+epsLocInfo.getAsJsonObject("TAI").get("tac").getAsString());
                }
                if (epsLocInfo.getAsJsonObject("ECGI") != null) {
                    if (epsLocInfo.getAsJsonObject("ECGI").get("mcc") != null)
                        atiOrPsiResponse.put("mcc", epsLocInfo.getAsJsonObject("ECGI").get("mcc").getAsString());
                    if (epsLocInfo.getAsJsonObject("ECGI").get("mnc") != null)
                        atiOrPsiResponse.put("mnc", epsLocInfo.getAsJsonObject("ECGI").get("mnc").getAsString());
                    if (epsLocInfo.getAsJsonObject("ECGI").get("eci") != null)
                        atiOrPsiResponse.put("eci", epsLocInfo.getAsJsonObject("ECGI").get("eci").getAsString());
                    if (epsLocInfo.getAsJsonObject("ECGI").get("eNBId") != null)
                        atiOrPsiResponse.put("eENBId", epsLocInfo.getAsJsonObject("ECGI").get("eNBId").getAsString());
                    if (epsLocInfo.getAsJsonObject("ECGI").get("ci") != null)
                        atiOrPsiResponse.put("ci", epsLocInfo.getAsJsonObject("ECGI").get("ci").getAsString());
                    logger.info("EPSLocationInformation CGI mcc="+epsLocInfo.getAsJsonObject("ECGI").get("mcc").getAsString());
                    logger.info("EPSLocationInformation CGI mnc="+epsLocInfo.getAsJsonObject("ECGI").get("mnc").getAsString());
                    logger.info("EPSLocationInformation CGI eci="+epsLocInfo.getAsJsonObject("ECGI").get("eci").getAsString());
                    logger.info("EPSLocationInformation CGI eNBId="+epsLocInfo.getAsJsonObject("ECGI").get("eNBId").getAsString());
                    logger.info("EPSLocationInformation CGI ci="+epsLocInfo.getAsJsonObject("ECGI").get("ci").getAsString());
                }
                if (epsLocInfo.getAsJsonObject("GeographicalInformation") != null) {
                    if (epsLocInfo.getAsJsonObject("GeographicalInformation").get("typeOfShape") != null)
                        atiOrPsiResponse.put("typeOfShape", epsLocInfo.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeographicalInformation").get("latitude") != null)
                        atiOrPsiResponse.put("latitude", epsLocInfo.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeographicalInformation").get("longitude") != null)
                        atiOrPsiResponse.put("longitude", epsLocInfo.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeographicalInformation").get("uncertainty") != null)
                        atiOrPsiResponse.put("nncertainty", epsLocInfo.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
                    logger.info("EPSLocationInformation GeographicalInformation typeOfShape="+epsLocInfo.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                    logger.info("EPSLocationInformation GeographicalInformation latitude="+epsLocInfo.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                    logger.info("EPSLocationInformation GeographicalInformation longitude="+epsLocInfo.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                    logger.info("EPSLocationInformation GeographicalInformation uncertainty="+epsLocInfo.getAsJsonObject("GeographicalInformation").get("uncertainty"));
                }
                if (epsLocInfo.getAsJsonObject("GeodeticInformation") != null) {
                    if (epsLocInfo.getAsJsonObject("GeodeticInformation").get("typeOfShape") != null)
                        atiOrPsiResponse.put("typeOfShape", epsLocInfo.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeodeticInformation").get("latitude") != null)
                        atiOrPsiResponse.put("latitude", epsLocInfo.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeodeticInformation").get("longitude") != null)
                        atiOrPsiResponse.put("longitude", epsLocInfo.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeodeticInformation").get("uncertainty") != null)
                        atiOrPsiResponse.put("uncertainty", epsLocInfo.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeodeticInformation").get("confidence") != null)
                        atiOrPsiResponse.put("confidence", epsLocInfo.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                    if (epsLocInfo.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators") != null)
                        atiOrPsiResponse.put("screeningAndPresentationIndicators", epsLocInfo.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators").getAsString());
                    logger.info("EPSLocationInformation GeodeticInformation typeOfShape="+epsLocInfo.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                    logger.info("EPSLocationInformation GeodeticInformation latitude="+epsLocInfo.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                    logger.info("EPSLocationInformation GeodeticInformation longitude="+epsLocInfo.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                    logger.info("EPSLocationInformation GeodeticInformation uncertainty="+epsLocInfo.getAsJsonObject("GeodeticInformation").get("uncertainty"));
                    logger.info("EPSLocationInformation GeodeticInformation confidence="+epsLocInfo.getAsJsonObject("GeodeticInformation").get("confidence"));
                    logger.info("EPSLocationInformation GeodeticInformation screeningAndPresentationIndicators="+epsLocInfo.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators"));
                }
                if (epsLocInfo.get("mmeName") != null) {
                    atiOrPsiResponse.put("mmeName", epsLocInfo.get("mmeName").getAsString());
                    logger.info("EPSLocationInformation mmeName="+epsLocInfo.get("mmeName").getAsString());
                }
                if (csLocInfo.get("ageOfLocationInformation") != null || epsLocInfo.get("ageOfLocationInformation") != null) {
                    atiOrPsiResponse.put("ageOfLocationInformation", epsLocInfo.get("ageOfLocationInformation") != null ?
                        epsLocInfo.get("ageOfLocationInformation").getAsString() :
                        csLocInfo.get("ageOfLocationInformation").getAsString());
                    if (epsLocInfo.get("ageOfLocationInformation") != null)
                        logger.info("EPSLocationInformation ageOfLocationInformation="+epsLocInfo.get("ageOfLocationInformation").getAsString());
                }
                if (csLocInfo.get("currentLocationRetrieved") != null || epsLocInfo.get("currentLocationRetrieved") != null) {
                    atiOrPsiResponse.put("currentLocationRetrieved", epsLocInfo.get("currentLocationRetrieved") != null ?
                        epsLocInfo.get("currentLocationRetrieved").getAsString() :
                        csLocInfo.get("currentLocationRetrieved").getAsString());
                    if (epsLocInfo.get("currentLocationRetrieved") != null)
                        logger.info("EPSLocationInformation currentLocationRetrieved="+epsLocInfo.get("currentLocationRetrieved").getAsString());
                }
            }
            if (csLocInfo.get("vlrNumber") != null) {
                atiOrPsiResponse.put("vlrNumber", csLocInfo.get("vlrNumber").getAsString());
                logger.info("CSLocationInformation vlrNumber="+csLocInfo.get("vlrNumber").getAsString());
            }
            if (csLocInfo.get("mscNumber") != null) {
                atiOrPsiResponse.put("mscNumber", csLocInfo.get("mscNumber").getAsString());
                logger.info("CSLocationInformation mscNumber=" + csLocInfo.get("mscNumber").getAsString());
            }
        }

        if (jsonObject.getAsJsonObject("PSLocationInformation") != null) {
            logger.info("PSLocationInformation");
            JsonObject psLocationInformation = jsonObject.getAsJsonObject("PSLocationInformation");
            if (psLocationInformation.getAsJsonObject("LSA") != null) {
                if (psLocationInformation.getAsJsonObject("LSA").get("lsaIdType") != null) {
                    atiOrPsiResponse.put("lsaIdType", psLocationInformation.getAsJsonObject("LSA").get("lsaIdType").getAsString());
                    logger.info("PSLocationInformation lsaIdType="+psLocationInformation.getAsJsonObject("LSA").get("lsaIdType").getAsString());
                }
                if (psLocationInformation.getAsJsonObject("LSA").get("lsaId") != null) {
                    atiOrPsiResponse.put("lsaId", psLocationInformation.getAsJsonObject("LSA").get("lsaId").getAsString());
                    logger.info("PSLocationInformation lsaId="+psLocationInformation.getAsJsonObject("LSA").get("lsaId").getAsString());
                }
            }
            if (psLocationInformation.getAsJsonObject("RAI") != null) {
                if (psLocationInformation.getAsJsonObject("RAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", psLocationInformation.getAsJsonObject("RAI").get("mcc").getAsString());
                if (psLocationInformation.getAsJsonObject("RAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", psLocationInformation.getAsJsonObject("RAI").get("mnc").getAsString());
                if (psLocationInformation.getAsJsonObject("RAI").get("lac") != null)
                    atiOrPsiResponse.put("lac", psLocationInformation.getAsJsonObject("RAI").get("lac").getAsString());
                if (psLocationInformation.getAsJsonObject("RAI").get("rac") != null)
                    atiOrPsiResponse.put("rac", psLocationInformation.getAsJsonObject("RAI").get("rac").getAsString());
                logger.info("PSLocationInformation RAI mcc="+psLocationInformation.getAsJsonObject("RAI").get("mcc").getAsString());
                logger.info("PSLocationInformation RAI mnc="+psLocationInformation.getAsJsonObject("RAI").get("mnc").getAsString());
                logger.info("PSLocationInformation RAI lac="+psLocationInformation.getAsJsonObject("RAI").get("lac").getAsString());
                logger.info("PSLocationInformation RAI rac="+psLocationInformation.getAsJsonObject("RAI").get("rac").getAsString());
            }
            if (psLocationInformation.getAsJsonObject("CGI") != null) {
                if (psLocationInformation.getAsJsonObject("CGI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", psLocationInformation.getAsJsonObject("CGI").get("mcc").getAsString());
                if (psLocationInformation.getAsJsonObject("CGI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", psLocationInformation.getAsJsonObject("CGI").get("mnc").getAsString());
                if (psLocationInformation.getAsJsonObject("CGI").get("lac") != null)
                    atiOrPsiResponse.put("lac", psLocationInformation.getAsJsonObject("CGI").get("lac").getAsString());
                if (psLocationInformation.getAsJsonObject("CGI").get("ci") != null)
                    atiOrPsiResponse.put("ci", psLocationInformation.getAsJsonObject("CGI").get("ci").getAsString());
                logger.info("PSLocationInformation CGI mcc="+psLocationInformation.getAsJsonObject("CGI").get("mcc").getAsString());
                logger.info("PSLocationInformation CGI mnc="+psLocationInformation.getAsJsonObject("CGI").get("mnc").getAsString());
                logger.info("PSLocationInformation CGI lac="+psLocationInformation.getAsJsonObject("CGI").get("lac").getAsString());
                logger.info("PSLocationInformation CGI ci="+psLocationInformation.getAsJsonObject("CGI").get("ci").getAsString());
            }
            if (psLocationInformation.getAsJsonObject("SAI") != null) {
                if (psLocationInformation.getAsJsonObject("SAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", psLocationInformation.getAsJsonObject("SAI").get("mcc").getAsString());
                if (psLocationInformation.getAsJsonObject("SAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", psLocationInformation.getAsJsonObject("SAI").get("mnc").getAsString());
                if (psLocationInformation.getAsJsonObject("SAI").get("lac") != null)
                    atiOrPsiResponse.put("lac", psLocationInformation.getAsJsonObject("SAI").get("lac").getAsString());
                if (psLocationInformation.getAsJsonObject("SAI").get("sac") != null)
                    atiOrPsiResponse.put("sac", psLocationInformation.getAsJsonObject("SAI").get("sac").getAsString());
                logger.info("PSLocationInformation SAI mcc="+psLocationInformation.getAsJsonObject("SAI").get("mcc").getAsString());
                logger.info("PSLocationInformation SAI mnc="+psLocationInformation.getAsJsonObject("SAI").get("mnc").getAsString());
                logger.info("PSLocationInformation SAI lac="+psLocationInformation.getAsJsonObject("SAI").get("lac").getAsString());
                logger.info("PSLocationInformation SAI sac="+psLocationInformation.getAsJsonObject("SAI").get("sac").getAsString());
            }
            if (psLocationInformation.getAsJsonObject("LAI") != null) {
                if (psLocationInformation.getAsJsonObject("LAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", psLocationInformation.getAsJsonObject("LAI").get("mcc").getAsString());
                if (psLocationInformation.getAsJsonObject("LAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", psLocationInformation.getAsJsonObject("LAI").get("mnc").getAsString());
                if (psLocationInformation.getAsJsonObject("LAI").get("lac") != null)
                    atiOrPsiResponse.put("lac", psLocationInformation.getAsJsonObject("LAI").get("lac").getAsString());
                logger.info("PSLocationInformation LAI mcc="+psLocationInformation.getAsJsonObject("LAI").get("mcc").getAsString());
                logger.info("PSLocationInformation LAI mnc="+psLocationInformation.getAsJsonObject("LAI").get("mnc").getAsString());
                logger.info("PSLocationInformation LAI lac="+psLocationInformation.getAsJsonObject("LAI").get("lac").getAsString());
            }
            if (psLocationInformation.getAsJsonObject("GeographicalInformation") != null) {
                if (psLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", psLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                if (psLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", psLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                if (psLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", psLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                if (psLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", psLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
                logger.info("PSLocationInformation GeographicalInformation typeOfShape="+psLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape"));
                logger.info("PSLocationInformation GeographicalInformation latitude="+psLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude"));
                logger.info("PSLocationInformation GeographicalInformation longitude="+psLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude"));
                logger.info("PSLocationInformation GeographicalInformation uncertainty="+psLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty"));
            }
            if (psLocationInformation.getAsJsonObject("GeodeticInformation") != null) {
                if (psLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", psLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                if (psLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", psLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                if (psLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", psLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                if (psLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("ucertainty", psLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                if (psLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence") != null)
                    atiOrPsiResponse.put("confidence", psLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                if (psLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators") != null)
                    atiOrPsiResponse.put("screeningAndPresentationIndicators", psLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators").getAsString());
                logger.info("PSLocationInformation GeodeticInformation typeOfShape="+psLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape"));
                logger.info("PSLocationInformation GeodeticInformation latitude="+psLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude"));
                logger.info("PSLocationInformation GeodeticInformation longitude="+psLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude"));
                logger.info("PSLocationInformation GeodeticInformation uncertainty="+psLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty"));
                logger.info("PSLocationInformation GeodeticInformation confidence="+psLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence"));
                logger.info("PSLocationInformation GeodeticInformation screeningAndPresentationIndicators="+psLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators"));
            }
            if (psLocationInformation.get("ageOfLocationInformation") != null)
                atiOrPsiResponse.put("ageOfLocationInformation", psLocationInformation.get("ageOfLocationInformation").getAsString());
            logger.info("PSLocationInformation  ageOfLocationInformation="+psLocationInformation.get("ageOfLocationInformation"));
            if (psLocationInformation.get("currentLocationRetrieved") != null)
                atiOrPsiResponse.put("currentLocationRetrieved", psLocationInformation.get("currentLocationRetrieved").getAsString());
            logger.info("PSLocationInformation currentLocationRetrieved="+psLocationInformation.get("currentLocationRetrieved"));
            if (psLocationInformation.get("sgsnNumber") != null)
                atiOrPsiResponse.put("sgsnNumber", psLocationInformation.get("sgsnNumber").getAsString());
            logger.info("PSLocationInformation sgsnNumber="+psLocationInformation.get("sgsnNumber"));
        }

        if (jsonObject.getAsJsonObject("EPSLocationInformation") != null) {
            logger.info("EPSLocationInformation out of CSLocationInformation");
            JsonObject epsLocationInformation = jsonObject.getAsJsonObject("EPSLocationInformation");
            if (epsLocationInformation.getAsJsonObject("TAI") != null) {
                if (epsLocationInformation.getAsJsonObject("TAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", epsLocationInformation.getAsJsonObject("TAI").get("mcc").getAsString());
                if (epsLocationInformation.getAsJsonObject("TAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", epsLocationInformation.getAsJsonObject("TAI").get("mnc").getAsString());
                if (epsLocationInformation.getAsJsonObject("TAI").get("tac") != null)
                    atiOrPsiResponse.put("tac", epsLocationInformation.getAsJsonObject("TAI").get("tac").getAsString());
                logger.info("EPSLocationInformation TAI mcc="+epsLocationInformation.getAsJsonObject("TAI").get("mcc"));
                logger.info("EPSLocationInformation TAI mnc="+epsLocationInformation.getAsJsonObject("TAI").get("mnc"));
                logger.info("EPSLocationInformation TAI tac="+epsLocationInformation.getAsJsonObject("TAI").get("tac"));
            }
            if (epsLocationInformation.getAsJsonObject("ECGI") != null) {
                if (epsLocationInformation.getAsJsonObject("ECGI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", epsLocationInformation.getAsJsonObject("ECGI").get("mcc").getAsString());
                if (epsLocationInformation.getAsJsonObject("ECGI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", epsLocationInformation.getAsJsonObject("ECGI").get("mnc").getAsString());
                if (epsLocationInformation.getAsJsonObject("ECGI").get("eci") != null)
                    atiOrPsiResponse.put("eci", epsLocationInformation.getAsJsonObject("ECGI").get("eci").getAsString());
                if (epsLocationInformation.getAsJsonObject("ECGI").get("eNBId") != null)
                    atiOrPsiResponse.put("eNBId", epsLocationInformation.getAsJsonObject("ECGI").get("eNBId").getAsString());
                if (epsLocationInformation.getAsJsonObject("ECGI").get("ci") != null)
                    atiOrPsiResponse.put("ci", epsLocationInformation.getAsJsonObject("ECGI").get("ci").getAsString());
                logger.info("EPSLocationInformation ECGI mcc="+epsLocationInformation.getAsJsonObject("ECGI").get("mcc"));
                logger.info("EPSLocationInformation ECGI mnc="+epsLocationInformation.getAsJsonObject("ECGI").get("mnc"));
                logger.info("EPSLocationInformation ECGI eci="+epsLocationInformation.getAsJsonObject("ECGI").get("eci"));
                logger.info("EPSLocationInformation ECGI eNBId="+epsLocationInformation.getAsJsonObject("ECGI").get("eNBId"));
                logger.info("EPSLocationInformation ECGI ci="+epsLocationInformation.getAsJsonObject("ECGI").get("ci"));
            }
            if (epsLocationInformation.getAsJsonObject("GeographicalInformation") != null) {
                if (epsLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", epsLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", epsLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", epsLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", epsLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
                logger.info("EPSLocationInformation GeographicalInformation typeOfShape="+epsLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape"));
                logger.info("EPSLocationInformation GeographicalInformation latitude="+epsLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude"));
                logger.info("EPSLocationInformation GeographicalInformation longitude="+epsLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude"));
                logger.info("EPSLocationInformation GeographicalInformation uncertainty="+epsLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty"));
            }
            if (epsLocationInformation.getAsJsonObject("GeodeticInformation") != null) {
                if (epsLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", epsLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", epsLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", epsLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", epsLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence") != null)
                    atiOrPsiResponse.put("confidence", epsLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                if (epsLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators") != null)
                    atiOrPsiResponse.put("screeningAndPresentationIndicators", epsLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators").getAsString());
                logger.info("EPSLocationInformation GeodeticInformation typeOfShape="+epsLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape"));
                logger.info("EPSLocationInformation GeodeticInformation latitude="+epsLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude"));
                logger.info("EPSLocationInformation GeodeticInformation longitude="+epsLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude"));
                logger.info("EPSLocationInformation GeodeticInformation uncertainty="+epsLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty"));
                logger.info("EPSLocationInformation GeodeticInformation confidence="+epsLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence"));
                logger.info("EPSLocationInformation GeodeticInformation screeningAndPresentationIndicators="+epsLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators"));
            }
            if (epsLocationInformation.get("mmeName") != null)
                atiOrPsiResponse.put("mmeName", epsLocationInformation.get("mmeName").getAsString());
            logger.info("EPSLocationInformation mmeName="+epsLocationInformation.get("mmeName").getAsString());
            if (epsLocationInformation.get("ratType") != null) {
                atiOrPsiResponse.put("atiOrPsiRatType", epsLocationInformation.get("ratType").getAsString());
                logger.info("EPSLocationInformation ratType=" + epsLocationInformation.get("ratType").getAsString());
            }
        }

        if (jsonObject.getAsJsonObject("5GSLocationInformation") != null) {
            logger.info("5GSLocationInformation");
            JsonObject atiOrPsi5gsLocationInformation = jsonObject.getAsJsonObject("5GSLocationInformation");
            if (atiOrPsi5gsLocationInformation.getAsJsonObject("TAI") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("mcc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("mnc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("tac") != null)
                    atiOrPsiResponse.put("tac", atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("tac").getAsString());
                logger.info("5GSLocationInformation TAI mcc="+atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("mcc"));
                logger.info("5GSLocationInformation TAI mnc="+atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("mnc"));
                logger.info("5GSLocationInformation TAI tac="+atiOrPsi5gsLocationInformation.getAsJsonObject("TAI").get("tac"));
            }
            if (atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("mcc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("mnc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("eci") != null)
                    atiOrPsiResponse.put("eci", atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("eci").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("eNBId") != null)
                    atiOrPsiResponse.put("eNBId", atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("eNBId").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("ci") != null)
                    atiOrPsiResponse.put("ci", atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("ci").getAsString());
                logger.info("5GSLocationInformation ECGI mcc="+atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("mcc").getAsString());
                logger.info("5GSLocationInformation ECGI mnc="+atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("mnc").getAsString());
                logger.info("5GSLocationInformation ECGI eci="+atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("eci").getAsString());
                logger.info("5GSLocationInformation ECGI eNBId="+atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("eNBId").getAsString());
                logger.info("5GSLocationInformation ECGI ci="+atiOrPsi5gsLocationInformation.getAsJsonObject("ECGI").get("ci").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("mcc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("mnc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("nci") != null)
                    atiOrPsiResponse.put("nci", atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("nci").getAsString());
                logger.info("5GSLocationInformation NCGI mcc="+atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("mcc").getAsString());
                logger.info("5GSLocationInformation NCGI mnc="+atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("mnc").getAsString());
                logger.info("5GSLocationInformation NCGI nci="+atiOrPsi5gsLocationInformation.getAsJsonObject("NCGI").get("nci").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("mcc") != null)
                    atiOrPsiResponse.put("mcc", atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("mcc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("mnc") != null)
                    atiOrPsiResponse.put("mnc", atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("mnc").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("tac") != null)
                    atiOrPsiResponse.put("tac", atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("tac").getAsString());
                logger.info("5GSLocationInformation NR-TAI mcc="+atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("mcc").getAsString());
                logger.info("5GSLocationInformation NR-TAI mnc="+atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("mnc").getAsString());
                logger.info("5GSLocationInformation NR-TAI tac="+atiOrPsi5gsLocationInformation.getAsJsonObject("NR-TAI").get("tac").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
                logger.info("5GSLocationInformation GeographicalInformation typeOfShape="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("typeOfShape").getAsString());
                logger.info("5GSLocationInformation GeographicalInformation latitude="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("latitude").getAsString());
                logger.info("5GSLocationInformation GeographicalInformation longitude="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("longitude").getAsString());
                logger.info("5GSLocationInformation GeographicalInformation uncertainty="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeographicalInformation").get("uncertainty").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape") != null)
                    atiOrPsiResponse.put("typeOfShape", atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude") != null)
                    atiOrPsiResponse.put("latitude", atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude") != null)
                    atiOrPsiResponse.put("longitude", atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty") != null)
                    atiOrPsiResponse.put("uncertainty", atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence") != null)
                    atiOrPsiResponse.put("confidence", atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators") != null)
                    atiOrPsiResponse.put("screeningAndPresentationIndicators", atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators").getAsString());
                logger.info("5GSLocationInformation GeodeticInformation typeOfShape="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("typeOfShape").getAsString());
                logger.info("5GSLocationInformation GeodeticInformation latitude="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("latitude").getAsString());
                logger.info("5GSLocationInformation GeodeticInformation longitude="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("longitude").getAsString());
                logger.info("5GSLocationInformation GeodeticInformation uncertainty="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("uncertainty").getAsString());
                logger.info("5GSLocationInformation GeodeticInformation confidence="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("confidence").getAsString());
                logger.info("5GSLocationInformation GeodeticInformation screeningAndPresentationIndicators="+atiOrPsi5gsLocationInformation.getAsJsonObject("GeodeticInformation").get("screeningAndPresentationIndicators").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.get("amfAddress") != null) {
                atiOrPsiResponse.put("amfAddress", atiOrPsi5gsLocationInformation.get("amfAddress").getAsString());
                logger.info("5GSLocationInformation amfAddress="+atiOrPsi5gsLocationInformation.get("amfAddress").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.get("ageOfLocationInformation") != null) {
                atiOrPsiResponse.put("ageOfLocationInformation", atiOrPsi5gsLocationInformation.get("ageOfLocationInformation").getAsString());
                logger.info("5GSLocationInformation ageOfLocationInformation="+atiOrPsi5gsLocationInformation.get("ageOfLocationInformation").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.get("currentLocationRetrieved") != null) {
                atiOrPsiResponse.put("currentLocationRetrieved", atiOrPsi5gsLocationInformation.get("currentLocationRetrieved").getAsString());
                logger.info("5GSLocationInformation currentLocationRetrieved="+atiOrPsi5gsLocationInformation.get("currentLocationRetrieved").getAsString());
            }
            if (atiOrPsi5gsLocationInformation.get("Used-RAT-Type") != null) {
                if (atiOrPsi5gsLocationInformation.getAsJsonObject("Used-RAT-Type").get("ratType") != null)
                    atiOrPsiResponse.put("atiOrPsiRatType", atiOrPsi5gsLocationInformation.getAsJsonObject("Used-RAT-Type").get("ratType").getAsString());
                logger.info("5GSLocationInformation ratType="+atiOrPsi5gsLocationInformation.getAsJsonObject("Used-RAT-Type").get("ratType").getAsString());
            }
        }

        if (jsonObject.getAsJsonObject("MNPInfoResult") != null) {
            if (jsonObject.getAsJsonObject("MNPInfoResult").get("mnpStatus") != null)
                atiOrPsiResponse.put("mnpStatus", jsonObject.getAsJsonObject("MNPInfoResult").get("mnpStatus").getAsString());
            if (jsonObject.getAsJsonObject("MNPInfoResult").get("mnpMsisdn") != null)
                atiOrPsiResponse.put("mnpMsisdn", jsonObject.getAsJsonObject("MNPInfoResult").get("mnpMsisdn").getAsString());
            if (jsonObject.getAsJsonObject("MNPInfoResult").get("mnpImsi") != null)
                atiOrPsiResponse.put("mnpImsi", jsonObject.getAsJsonObject("MNPInfoResult").get("mnpImsi").getAsString());
            if (jsonObject.getAsJsonObject("MNPInfoResult").get("mnpRouteingNumber") != null)
                atiOrPsiResponse.put("mnpRouteingNumber", jsonObject.getAsJsonObject("MNPInfoResult").get("mnpRouteingNumber").getAsString());
            logger.info("MNPInfoResult mnpStatus="+jsonObject.getAsJsonObject("MNPInfoResult").get("mnpStatus").getAsString());
            logger.info("MNPInfoResult mnpMsisdn="+jsonObject.getAsJsonObject("MNPInfoResult").get("mnpMsisdn").getAsString());
            logger.info("MNPInfoResult mnpImsi="+jsonObject.getAsJsonObject("MNPInfoResult").get("mnpImsi").getAsString());
            logger.info("MNPInfoResult mnpRouteingNumber="+jsonObject.getAsJsonObject("MNPInfoResult").get("mnpRouteingNumber").getAsString());
        }

        if (jsonObject.get("msisdn") != null) {
            atiOrPsiResponse.put("msisdn", jsonObject.get("msisdn").getAsString());
            logger.info("msisdn="+jsonObject.get("msisdn").getAsString());
        }

        if (jsonObject.get("imsi") != null) {
            atiOrPsiResponse.put("imsi", jsonObject.get("imsi").getAsString());
            logger.info("imsi="+jsonObject.get("imsi").getAsString());
        }

        if (jsonObject.get("imei") != null) {
            atiOrPsiResponse.put("imei", jsonObject.get("imei").getAsString());
            logger.info("imei="+jsonObject.get("imei").getAsString());
        }

        if (jsonObject.get("subscriberState") != null) {
            atiOrPsiResponse.put("subscriberState", jsonObject.get("subscriberState").getAsString());
            logger.info("subscriberState="+jsonObject.get("subscriberState").getAsString());
        }

        if (jsonObject.get("notReachableReason") != null) {
            atiOrPsiResponse.put("notReachableReason", jsonObject.get("notReachableReason").getAsString());
            logger.info("notReachableReason="+jsonObject.get("notReachableReason").getAsString());
        }

        if (jsonObject.get("LastRATType") != null) {
            if (jsonObject.getAsJsonObject("LastRATType").get("ratType") != null)
                atiOrPsiResponse.put("atiOrPsiRatType", jsonObject.getAsJsonObject("LastRATType").get("ratType").getAsString());
            logger.info("ratType="+jsonObject.getAsJsonObject("LastRATType").get("ratType").getAsString());
        }

        if (jsonObject.get("errorReason") != null) {
            atiOrPsiResponse.put("errorReason", jsonObject.get("errorReason").getAsString());
            logger.info("errorReason="+jsonObject.get("errorReason").getAsString());
        }

    }
}
