package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.primitives.TrackingAreaId;
import org.mobicents.gmlc.slee.primitives.TrackingAreaIdImpl;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShTrackingAreaId {

  private static final Logger logger = Logger.getLogger(ShTrackingAreaId.class.getName());

  private static final String MCC = "mcc";
  private static final String MNC = "mnc";
  private static final String TAC = "tac";

  private String trackingAreaIdStr;
  private TrackingAreaId trackingAreaId;

  public ShTrackingAreaId(String trackingAreaIdStr, TrackingAreaId trackingAreaId) {
    this.trackingAreaIdStr = trackingAreaIdStr;
    this.trackingAreaId = trackingAreaId;
  }

  public ShTrackingAreaId(String trackingAreaIdStr) {
    this.trackingAreaIdStr = trackingAreaIdStr;
  }

  public ShTrackingAreaId(TrackingAreaId trackingAreaId) {
    this.trackingAreaId = trackingAreaId;
  }

  public ShTrackingAreaId() {
  }

  public TrackingAreaId getTrackingAreaId() {
    return trackingAreaId;
  }

  public void setTrackingAreaId(TrackingAreaId trackingAreaId) {
    this.trackingAreaId = trackingAreaId;
  }

  public String getTrackingAreaIdStr() {
    return trackingAreaIdStr;
  }

  public void setTrackingAreaIdStr(String trackingAreaIdentityStr) {
    if (trackingAreaIdentityStr != null) {
      byte[] raIdBytes = getTrackingAreaIdentityBytes(trackingAreaIdentityStr);
      this.trackingAreaId = decodeTrackingAreaIdentity(raIdBytes);
      this.trackingAreaIdStr = this.trackingAreaId.toString();
    }
  }

  public byte[] getTrackingAreaIdentityBytes(String trackingAreaIdentity) {
    return Base64.getDecoder().decode(trackingAreaIdentity);
  }

  public TrackingAreaId decodeTrackingAreaIdentity(byte[] raIdBytes) {
    return new TrackingAreaIdImpl(raIdBytes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("TAI");
    sb.append(" [");

    if (trackingAreaId != null) {

      try {
        sb.append(MCC+"=");
        sb.append(this.trackingAreaId.getMCC());

        sb.append(", "+MNC+"=");
        sb.append(this.trackingAreaId.getMNC());

        sb.append(", "+TAC+"=");
        sb.append(this.trackingAreaId.getTAC());

      } catch (Exception e) {
        logger.error(e.getMessage());
      }

    }

    sb.append("]");

    return sb.toString();
  }

}
