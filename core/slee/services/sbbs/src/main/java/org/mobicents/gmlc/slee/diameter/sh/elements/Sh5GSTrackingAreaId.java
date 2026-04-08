package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.primitives.TrackingAreaId5GS;
import org.mobicents.gmlc.slee.primitives.TrackingAreaId5GSImpl;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class Sh5GSTrackingAreaId {

    private static final Logger logger = Logger.getLogger(Sh5GSTrackingAreaId.class.getName());

    private static final String MCC = "mcc";
    private static final String MNC = "mnc";
    private static final String TAC = "tac";

    private String trackingAreaId5GSStr;
    private TrackingAreaId5GS trackingAreaId5GS;

    public Sh5GSTrackingAreaId(String trackingAreaId5GSStr, TrackingAreaId5GS trackingAreaId5GS) {
        this.trackingAreaId5GSStr = trackingAreaId5GSStr;
        this.trackingAreaId5GS = trackingAreaId5GS;
    }

    public Sh5GSTrackingAreaId(String trackingAreaId5GSStr) {
        this.trackingAreaId5GSStr = trackingAreaId5GSStr;
    }

    public Sh5GSTrackingAreaId(TrackingAreaId5GS trackingAreaId5GS) {
        this.trackingAreaId5GS = trackingAreaId5GS;
    }

    public Sh5GSTrackingAreaId() {
    }

    public TrackingAreaId5GS get5GSTrackingAreaId() {
        return trackingAreaId5GS;
    }

    public void set5GSTrackingAreaId(TrackingAreaId5GS trackingAreaId5GS) {
        this.trackingAreaId5GS = trackingAreaId5GS;
    }

    public String get5GSTrackingAreaIdStr() {
        return trackingAreaId5GSStr;
    }

    public void set5GSTrackingAreaIdStr(String trackingAreaId5GSStr) {
        if (trackingAreaId5GSStr != null) {
            byte[] raIdBytes = get5GSTrackingAreaIdentityBytes(trackingAreaId5GSStr);
            this.trackingAreaId5GS = decode5GSTrackingAreaId(raIdBytes);
            this.trackingAreaId5GSStr = this.trackingAreaId5GS.toString();
        }
    }

    public byte[] get5GSTrackingAreaIdentityBytes(String trackingAreaIdentity5GS) {
        return Base64.getDecoder().decode(trackingAreaIdentity5GS);
    }

    public TrackingAreaId5GS decode5GSTrackingAreaId(byte[] raIdBytes) {
        return new TrackingAreaId5GSImpl(raIdBytes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TAI");
        sb.append(" [");

        if (trackingAreaId5GS != null) {

            try {
                sb.append(MCC+"=");
                sb.append(this.trackingAreaId5GS.getMCC());

                sb.append(", "+MNC+"=");
                sb.append(this.trackingAreaId5GS.getMNC());

                sb.append(", "+TAC+"=");
                sb.append(this.trackingAreaId5GS.get5GSTAC());

            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        sb.append("]");

        return sb.toString();
    }
}
