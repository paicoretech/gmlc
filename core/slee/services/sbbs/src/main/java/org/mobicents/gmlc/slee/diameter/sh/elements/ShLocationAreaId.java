package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.LAIFixedLength;
import org.restcomm.protocols.ss7.map.primitives.LAIFixedLengthImpl;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShLocationAreaId {

    private static final Logger logger = Logger.getLogger(ShLocationAreaId.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";
    private static final String LAC = "lac";

    private String locationAreaIdStr;
    private LAIFixedLength laiFixedLength;

    public ShLocationAreaId(String locationAreaIdStr, LAIFixedLength laiFixedLength) {
        this.locationAreaIdStr = locationAreaIdStr;
        this.laiFixedLength = laiFixedLength;
    }

    public ShLocationAreaId(String locationAreaIdStr) {
        this.locationAreaIdStr = locationAreaIdStr;
    }

    public ShLocationAreaId(LAIFixedLength laiFixedLength) {
        this.laiFixedLength = laiFixedLength;
    }

    public LAIFixedLength getLaiFixedLength() {
        return laiFixedLength;
    }

    public void setLaiFixedLength(LAIFixedLength laiFixedLength) {
        this.laiFixedLength = laiFixedLength;
    }

    public ShLocationAreaId() {
    }

    public String getLocationAreIdStr() {
        return locationAreaIdStr;
    }

    public void setLocationAreIdStr(String locationAreaIdStr) {
        if (locationAreaIdStr != null) {
            byte[] laiBytes = getLAIFixedLengthBytes(locationAreaIdStr);
            this.laiFixedLength = decodeLAIFixedLengthBytes(laiBytes);
            this.locationAreaIdStr = this.laiFixedLength.toString();
        }
    }

    public byte[] getLAIFixedLengthBytes(String laiInfo) {
        return Base64.getDecoder().decode(laiInfo);
    }

    public LAIFixedLength decodeLAIFixedLengthBytes(byte[] laiBytes) {
        return new LAIFixedLengthImpl(laiBytes);
        
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LAI");
        sb.append(" [");

        if (laiFixedLength != null) {

            try {
                sb.append(MCC+"=");
                sb.append(this.laiFixedLength.getMCC());

                sb.append(", "+MNC+"=");
                sb.append(this.laiFixedLength.getMNC());

                sb.append(", "+LAC+"=");
                sb.append(this.laiFixedLength.getLac());

            } catch (MAPException e) {
                logger.error(e.getMessage());
            }

        }

        sb.append("]");

        return sb.toString();
    }
}
