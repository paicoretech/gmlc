package org.mobicents.gmlc.slee.diameter.sh.elements;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.isup.ParameterException;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.LocationNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;

import java.util.Base64;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class ShLocationNumber {

  private static final Logger logger = Logger.getLogger(ShLocationNumber.class.getName());

  private static final String NUMBERING_PLAN_INDICATOR = "numberingPlanIndicator";
  private static final String INTERNAL_NETWORK_NUMBER_INDICATOR = "internalNetworkNumberIndicator";
  private static final String ADDRESS_REPRESENTATION_RESTRICTED_INDICATOR = "addressRepresentationRestrictedIndicator";
  private static final String SCREENING_INDICATOR = "screeningIndicator";
  private static final String NATURE_OF_ADDRESS_INDICATOR = "natureOfAddressIndicator";
  private static final String ODD_FLAG = "oddFlag";
  private static final String ADDRESS = "address";

  private static final int DEFAULT_NUMBERING_PLAN_INDICATOR = 0;
  private static final int DEFAULT_INTERNAL_NETWORK_NUMBER_INDICATOR = 0;
  private static final int DEFAULT_ADDRESS_REPRESENTATION_RESTRICTED_INDICATOR = 0;
  private static final int DEFAULT_SCREENING_INDICATOR = 0;
  private static final int DEFAULT_NATURE_OF_ADDRESS_INDICATOR = 0;
  private static final int DEFAULT_ODD_FLAG = 0;
  private static final int DEFAULT_ADDRESS = 0;
  private static final String LOCATION_NUMBER = "locationNumber";

  private String locationNumberStr;
  private LocationNumber locationNumber;

  public ShLocationNumber(String locationNumberStr, LocationNumber locationNumber) {
    this.locationNumberStr = locationNumberStr;
    this.locationNumber = locationNumber;
  }

  public ShLocationNumber(String locationNumberStr) {
    this.locationNumberStr = locationNumberStr;
  }

  public ShLocationNumber(LocationNumber locationNumber) {
    this.locationNumber = locationNumber;
  }

  public ShLocationNumber() {
  }

  public LocationNumber getLocationNumber() {
    return locationNumber;
  }

  public void setLocationNumber(LocationNumber locationNumber) {
    this.locationNumber = locationNumber;
  }

  public String getLocationNumberStr() {
    return locationNumberStr;
  }

  public void setLocationNumberStr(String locationNumberStr) {
    if (locationNumberStr != null) {
      byte[] locationNumberBytes = getLocationNumberBytes(locationNumberStr);
      this.locationNumber = decodeLocationNumber(locationNumberBytes);
      this.locationNumberStr = this.locationNumber.toString();
    }
  }

  public byte[] getLocationNumberBytes(String locationNumber) {
    return Base64.getDecoder().decode(locationNumber);
  }

  public LocationNumber decodeLocationNumber(byte[] lnBytes) {
    LocationNumber locationNumber = null;
    try {
      locationNumber = new LocationNumberImpl(lnBytes);
    } catch (ParameterException e) {
      logger.error(e.getMessage());
    }
    return locationNumber;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LocationNumber");
    sb.append(" [");

    if (locationNumber != null) {

      sb.append(ODD_FLAG+"=");
      sb.append(this.locationNumber.isOddFlag() ? "1" : "0");

      sb.append(", "+NATURE_OF_ADDRESS_INDICATOR+"=");
      sb.append(this.locationNumber.getNatureOfAddressIndicator());

      sb.append(", "+INTERNAL_NETWORK_NUMBER_INDICATOR+"=");
      sb.append(this.locationNumber.getInternalNetworkNumberIndicator());

      sb.append(", "+NUMBERING_PLAN_INDICATOR+"=");
      sb.append(this.locationNumber.getNumberingPlanIndicator());

      sb.append(", "+ADDRESS_REPRESENTATION_RESTRICTED_INDICATOR+"=");
      sb.append(this.locationNumber.getAddressRepresentationRestrictedIndicator());

      sb.append(", "+SCREENING_INDICATOR+"=");
      sb.append(this.locationNumber.getScreeningIndicator());

      sb.append(", "+ADDRESS+"=");
      sb.append(this.locationNumber.getAddress());

    }

    sb.append("]");

    return sb.toString();
  }
}
