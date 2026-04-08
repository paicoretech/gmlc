package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.primitives.RoutingAreaId;
import org.mobicents.gmlc.slee.primitives.RoutingAreaIdImpl;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShRoutingAreaId {

  private static final Logger logger = Logger.getLogger(ShRoutingAreaId.class.getName());

  private static final String MCC = "mcc";
  private static final String MNC = "mnc";
  private static final String LAC = "lac";
  private static final String RAC = "rac";

  private String routingAreaIdentityStr;
  private RoutingAreaId routingAreaIdentity;

  public ShRoutingAreaId(String routingAreaIdentityStr, RoutingAreaId routingAreaIdentity) {
    this.routingAreaIdentityStr = routingAreaIdentityStr;
    this.routingAreaIdentity = routingAreaIdentity;
  }

  public ShRoutingAreaId(String routingAreaIdentityStr) {
    this.routingAreaIdentityStr = routingAreaIdentityStr;
  }

  public ShRoutingAreaId(RoutingAreaId routingAreaIdentity) {
    this.routingAreaIdentity = routingAreaIdentity;
  }

  public ShRoutingAreaId() {
  }

  public RoutingAreaId getRoutingAreaIdentity() {
    return routingAreaIdentity;
  }

  public void setRoutingAreaIdentity(RoutingAreaId routingAreaIdentity) {
    this.routingAreaIdentity = routingAreaIdentity;
  }

  public String getRoutingAreaIdentityStr() {
    return routingAreaIdentityStr;
  }

  public void setRoutingAreaIdentityStr(String routingAreaIdentityStr) {
    if (routingAreaIdentityStr != null) {
      byte[] raIdBytes = getRoutingAreaIdentityBytes(routingAreaIdentityStr);
      this.routingAreaIdentity = decodeRoutingAreaIdentity(raIdBytes);
      this.routingAreaIdentityStr = this.routingAreaIdentity.toString();
    }
  }

  public byte[] getRoutingAreaIdentityBytes(String routingAreaIdentity) {
    return Base64.getDecoder().decode(routingAreaIdentity);
  }

  public RoutingAreaId decodeRoutingAreaIdentity(byte[] raIdBytes) {
    return new RoutingAreaIdImpl(raIdBytes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("RAI");
    sb.append(" [");

    if (routingAreaIdentity != null) {

      try {

        sb.append(MCC+"=");
        sb.append(this.routingAreaIdentity.getMCC());

        sb.append(", "+MNC+"=");
        sb.append(this.routingAreaIdentity.getMNC());

        sb.append(", "+LAC+"=");
        sb.append(this.routingAreaIdentity.getLAC());

        sb.append(", "+RAC+"=");
        sb.append(this.routingAreaIdentity.getRAC());

      } catch (Exception e) {
        logger.error(e.getMessage());
      }

    }

    sb.append("]");

    return sb.toString();
  }

}
