package org.mobicents.gmlc.slee.primitives;

import com.google.common.collect.Multimap;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class EUTRANPositioningDataTester {

    private static final Logger logger = Logger.getLogger(EUTRANPositioningDataTester.class.getName());

    public static void main(String[] args) throws Exception {
        /* Positioning Data Set examples */
        // 00000 001 (0x01) Cell ID; Attempted successfully: results not used to generate location - not used
        // 00010 010 (0x12) E-CID; Attempted successfully: results used to verify but not generate location - not used
        // 00100 011 (0x23) OTDOA; Attempted successfully: results used to generate location.
        // 01000 100 (0x44) U-TDOA; Attempted successfully: case where UE supports multiple mobile based positioning methods and the actual method or methods used by the UE cannot be determined;
        // 00001 000 (0x08) Reserved; Attempted unsuccessfully due to failure or interruption - not used;
        byte[] data = new byte[] {0x44, 0x01, 0x12, 0x23, 0x44, 0x08};
        EUTRANPositioningData eutranPositioningData = new EUTRANPositioningDataImpl(data);

        HashMap<String, Integer> methodsAndUsage = eutranPositioningData.getPositioningDataMethodsAndUsage(eutranPositioningData.getPositioningDataSet());

        logger.info("Positioning Data Set");
        for (HashMap.Entry<String, Integer> entry : methodsAndUsage.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            logger.info("Method=" + key + ", Usage=" + value + ": " + eutranPositioningData.getPositioningDataSetUsage(value));
        }

        /* GNSS Positioning Data Set examples */
        // 01 000 011 (0x42) UE-Assisted; GPS; Attempted successfully: results used to generate location
        // 00 001 001 (0x09) UE-Based; Galileo; Attempted successfully: results used to verify but not generate location
        // 11 010 010 (0xd2) Reserved; SBAS; Attempted unsuccessfully due to failure or interruption
        // 00 011 001 (0x19) UE-Based; Modernized GPS; Attempted successfully: results not used to generate location
        // 11 100 000 (0xe0) Reserved; QZSS; Attempted unsuccessfully due to failure or interruption
        // 00 101 100 (0x2c) UE-Based; GLONASS; Attempted successfully: case where UE supports multiple mobile based positioning methods and the actual method or methods used by the UE cannot be determined.
        // 10 110 010 (0xb2) Conventional; BDS; Attempted successfully: results used to verify but not generate location
        // 10 111 010 (0xba) Conventional; NavIC; Attempted successfully: results used to verify but not generate location
        data = new byte[] {0x27, 0x42, 0x09, (byte) 0xd2, 0x19, (byte) 0xe0, 0x2c, (byte) 0xb2, (byte) 0xba};
        eutranPositioningData = new EUTRANPositioningDataImpl(data);

        Multimap<String, String> methodsAndGanssIds = eutranPositioningData.getGNSSPositioningMethodsAndGNSSIds(eutranPositioningData.getGNSSPositioningDataSet());

        logger.info("\nGNSS Positioning Data Set");
        int i = 0;
        for (Map.Entry<String, String> entry : methodsAndGanssIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            logger.info("Method=" + key + ", gNSSId=" + value + ": "
                    + eutranPositioningData.getUsage(eutranPositioningData.getGNSSPositioningDataSet(), i));
            i++;
        }

        /* Additional Positioning Data Set examples */
        // 10 000 001 (0x81) Standalone; Barometric Pressure; Attempted successfully: results not used to generate location
        // 00 001 011 (0x0b) UE-Based; WLAN; Attempted successfully: results used to generate location
        // 01 010 010 (0x52) UE-Assisted; Bluetooth; Attempted successfully: results used to verify but not generate location
        // 11 011 000 (0xd8) Reserved; MBS; Attempted unsuccessfully due to failure or interruption
        // 10 100 100 (0xa4) Standalone; Motion-Sensor(s); Attempted successfully: case where UE supports multiple mobile based positioning methods and the actual method or methods used by the UE cannot be determined.
        data = new byte[] {0x05, (byte) 0x81, 0x0b, 0x52, (byte) 0xd8, (byte) 0xa4};
        eutranPositioningData = new EUTRANPositioningDataImpl(data);

        Multimap<String, String> methodsAndAddPosIds = eutranPositioningData.getEUtranAdditionalPositioningMethodsAndIds(eutranPositioningData.getAdditionalPositioningDataSet());

        logger.info("\nAdditional Positioning Data Set");
        i = 0;
        for (Map.Entry<String, String> entry : methodsAndAddPosIds.entries()) {
            String key = entry.getKey();
            String value = entry.getValue();
            logger.info("Method=" + key + ", AddPosId=" + value + ": "
                    + eutranPositioningData.getUsage(eutranPositioningData.getAdditionalPositioningDataSet(), i));
            i++;
        }
    }
}
