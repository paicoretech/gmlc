package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.primitives.PlmnIdImpl;

import static org.mobicents.gmlc.slee.utils.ByteUtils.decodeHexString;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShVisitedPLMNId {

    private static final Logger logger = Logger.getLogger(ShVisitedPLMNId.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";

    private String visitedPlmnIdStr;
    private PlmnId visitedPlmnId;

    public ShVisitedPLMNId(String visitedPlmnIdStr, PlmnId visitedPlmnId) {
        this.visitedPlmnIdStr = visitedPlmnIdStr;
        this.visitedPlmnId = visitedPlmnId;
    }

    public ShVisitedPLMNId(PlmnId visitedPlmnId) {
        this.visitedPlmnId = visitedPlmnId;
    }

    public ShVisitedPLMNId() {
    }

    public PlmnId getVisitedPlmnId() {
        return visitedPlmnId;
    }

    public void setVisitedPlmnId(PlmnId visitedPlmnId) {
        this.visitedPlmnId = visitedPlmnId;
    }

    public void setVisitedPlmnIdStr(String visitedPlmnIdStr) {
        if (visitedPlmnIdStr == null || !(visitedPlmnIdStr.length() == 5 || visitedPlmnIdStr.length() == 6))
            return;

        String byte01, byte02, byte03;
        visitedPlmnIdStr = visitedPlmnIdStr.length() == 5 ? visitedPlmnIdStr + "F" : visitedPlmnIdStr;
        // First Byte = MCC digit 2 + MCC digit 1
        byte01 = visitedPlmnIdStr.charAt(1) + String.valueOf(visitedPlmnIdStr.charAt(0));
        // Second Byte = MNC digit 3 + MCC digit 3
        byte02 = visitedPlmnIdStr.charAt(5) + String.valueOf(visitedPlmnIdStr.charAt(2));
        // Third Byte = MNC digit 2 + MNC digit 1
        byte03 = visitedPlmnIdStr.charAt(4) + String.valueOf(visitedPlmnIdStr.charAt(3));

        byte[] visitedPlmnIdBytes = decodeHexString(byte01 + byte02 + byte03);
        this.visitedPlmnId = new PlmnIdImpl(visitedPlmnIdBytes);
        this.visitedPlmnIdStr = this.visitedPlmnId.toString();
    }

    public String getVisitedPlmnIdStr() {
        return visitedPlmnIdStr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VisitedPLMNId [");

        if (visitedPlmnId != null) {
            try {
                sb.append(MCC+"=");
                sb.append(this.visitedPlmnId.getMcc());
                sb.append(", "+MNC+"=");
                sb.append(this.visitedPlmnId.getMnc());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        sb.append("]");

        return sb.toString();
    }
}
