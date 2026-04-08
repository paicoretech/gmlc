package org.mobicents.gmlc.slee.diameter.slg;

import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.slg.events.avp.AccuracyFulfilmentIndicator;
import net.java.slee.resource.diameter.slg.events.avp.ESMLCCellInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.GERANPositioningInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.ServingNodeAvp;
import net.java.slee.resource.diameter.slg.events.avp.UTRANPositioningInfoAvp;
import org.mobicents.gmlc.slee.primitives.EUTRANPositioningDataImpl;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SLgPlaAvpValues implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final int DIAMETER_SLg_VENDOR_ID = 10415;

  /*
    3GPP TS 29.172 v18.1.0
      7.3.2	Provide-Location-Answer (PLA) Command
      The Provide-Location-Answer (PLA) command, indicated by the Command-Code field set to 8388620 and the 'R' bit cleared in the Command Flags field,
      is sent by the MME or SGSN to the GMLC in response to the Provide-Location-Request command.

      Message Format:

      < Provide-Location-Answer > ::=    < Diameter Header: 8388620, PXY, 16777255 >
                                    < Session-Id >
                                    [ DRMP ]
                                    [ Vendor-Specific-Application-Id ]
                                    [ Result-Code ]
                                    [ Experimental-Result ]
                                    { Auth-Session-State }
                                    { Origin-Host }
                                    { Origin-Realm }
                                    [ Location-Estimate ]
                                    [ Accuracy-Fulfilment-Indicator ]
                                    [ Age-Of-Location-Estimate]
                                    [ Velocity-Estimate ]
                                    [ EUTRAN-Positioning-Data ]
                                    [ ECGI ]
                                    [ GERAN-Positioning-Info ]
                                    [ Cell-Global-Identity ]
                                    [ UTRAN-Positioning-Info ]
                                    [ Service-Area-Identity ]
                                    [ Serving-Node ]
                                    [ PLA-Flags ]
                                    [ ESMLC-Cell-Info ]
                                    [ Civic-Address ]
                                    [ Barometric-Pressure ]
                                    *[ Supported-Features ]
                                    *[ AVP ]
                                    [ Failed-AVP ]
                                    *[ Proxy-Info ]
                                    *[ Route-Record ]
  */

  private Long resultCode;
  private ExperimentalResultAvp experimentalResultAvp;
  private byte[] locationEstimate;
  private AccuracyFulfilmentIndicator accuracyFulfilmentIndicator;
  private Long ageOfLocationEstimate;
  private byte[] velocityEstimate;
  private byte[] eUtranPositioningData;
  private EUTRANPositioningDataImpl eutranPositioningData;
  private byte[] ecgi;
  private GERANPositioningInfoAvp geranPositioningInfoAvp;
  private byte[] geranPositioningData;
  private byte[] geranGANSSPositioningData;
  private byte[] cellGlobalIdentity;
  private UTRANPositioningInfoAvp utranPositioningInfoAvp;
  private byte[] utranPositioningData;
  private byte[] utranGANSSPositioningData;
  private byte[] utranAdditionalPositioningData;
  private byte[] serviceAreaIdentity;
  private ServingNodeAvp servingNodeAvp;
  private Long plaFlags;
  private ESMLCCellInfoAvp esmlcCellInfoAvp;
  private Long cellPortionId;
  private String civicAddress;
  private Long barometricPressure;

  public SLgPlaAvpValues() {
    super();
  }

  public Long getResultCode() {
    return resultCode;
  }

  public void setResultCode(Long resultCode) {
    this.resultCode = resultCode;
  }

  public ExperimentalResultAvp getExperimentalResultAvp() {
    return experimentalResultAvp;
  }

  public void setExperimentalResultAvp(ExperimentalResultAvp experimentalResultAvp) {
    this.experimentalResultAvp = experimentalResultAvp;
  }

  public byte[] getLocationEstimate() {
    return locationEstimate;
  }

  public void setLocationEstimate(byte[] locationEstimate) {
    this.locationEstimate = locationEstimate;
  }

  public AccuracyFulfilmentIndicator getAccuracyFulfilmentIndicator() {
    return accuracyFulfilmentIndicator;
  }

  public void setAccuracyFulfilmentIndicator(AccuracyFulfilmentIndicator accuracyFulfilmentIndicator) {
    this.accuracyFulfilmentIndicator = accuracyFulfilmentIndicator;
  }

  public Long getAgeOfLocationEstimate() {
    return ageOfLocationEstimate;
  }

  public void setAgeOfLocationEstimate(Long ageOfLocationEstimate) {
    this.ageOfLocationEstimate = ageOfLocationEstimate;
  }

  public byte[] getVelocityEstimate() {
    return velocityEstimate;
  }

  public void setVelocityEstimate(byte[] velocityEstimate) {
    this.velocityEstimate = velocityEstimate;
  }

  public byte[] getEUtranPositioningData() {
    return eUtranPositioningData;
  }

  public void setEUtranPositioningData(byte[] eUtranPositioningData) {
    this.eUtranPositioningData = eUtranPositioningData;
  }

  public EUTRANPositioningDataImpl getEutranPositioningData() {
    return eutranPositioningData;
  }

  public void setEutranPositioningData(EUTRANPositioningDataImpl eutranPositioningData) {
    this.eutranPositioningData = eutranPositioningData;
  }

  public byte[] getEcgi() {
    return ecgi;
  }

  public void setEcgi(byte[] ecgi) {
    this.ecgi = ecgi;
  }

  public GERANPositioningInfoAvp getGeranPositioningInfoAvp() {
    return geranPositioningInfoAvp;
  }

  public void setGeranPositioningInfoAvp(GERANPositioningInfoAvp geranPositioningInfoAvp) {
    this.geranPositioningInfoAvp = geranPositioningInfoAvp;
  }

  public byte[] getGeranPositioningData() {
    return geranPositioningData;
  }

  public void setGeranPositioningData(byte[] geranPositioningData) {
    this.geranPositioningData = geranPositioningData;
  }

  public byte[] getGeranGANSSPositioningData() {
    return geranGANSSPositioningData;
  }

  public void setGeranGANSSPositioningData(byte[] geranGANSSPositioningData) {
    this.geranGANSSPositioningData = geranGANSSPositioningData;
  }

  public byte[] getCellGlobalIdentity() {
    return cellGlobalIdentity;
  }

  public void setCellGlobalIdentity(byte[] cellGlobalIdentity) {
    this.cellGlobalIdentity = cellGlobalIdentity;
  }

  public UTRANPositioningInfoAvp getUtranPositioningInfoAvp() {
    return utranPositioningInfoAvp;
  }

  public void setUtranPositioningInfoAvp(UTRANPositioningInfoAvp utranPositioningInfoAvp) {
    this.utranPositioningInfoAvp = utranPositioningInfoAvp;
  }

  public byte[] getUtranPositioningData() {
    return utranPositioningData;
  }

  public void setUtranPositioningData(byte[] utranPositioningData) {
    this.utranPositioningData = utranPositioningData;
  }

  public byte[] getUtranGANSSPositioningData() {
    return utranGANSSPositioningData;
  }

  public void setUtranGANSSPositioningData(byte[] utranGANSSPositioningData) {
    this.utranGANSSPositioningData = utranGANSSPositioningData;
  }

  public byte[] getUtranAdditionalPositioningData() {
    return utranAdditionalPositioningData;
  }

  public void setUtranAdditionalPositioningData(byte[] utranAdditionalPositioningData) {
    this.utranAdditionalPositioningData = utranAdditionalPositioningData;
  }

  public byte[] getServiceAreaIdentity() {
    return serviceAreaIdentity;
  }

  public void setServiceAreaIdentity(byte[] serviceAreaIdentity) {
    this.serviceAreaIdentity = serviceAreaIdentity;
  }

  public ServingNodeAvp getServingNodeAvp() {
    return servingNodeAvp;
  }

  public void setServingNodeAvp(ServingNodeAvp servingNodeAvp) {
    this.servingNodeAvp = servingNodeAvp;
  }
  public Long getPlaFlags() {
    return plaFlags;
  }

  public void setPlaFlags(Long plaFlags) {
    this.plaFlags = plaFlags;
  }

  public ESMLCCellInfoAvp getEsmlcCellInfoAvp() {
    return esmlcCellInfoAvp;
  }

  public void setEsmlcCellInfoAvp(ESMLCCellInfoAvp esmlcCellInfoAvp) {
    this.esmlcCellInfoAvp = esmlcCellInfoAvp;
  }

  public Long getCellPortionId() {
    return cellPortionId;
  }

  public void setCellPortionId(Long cellPortionId) {
    this.cellPortionId = cellPortionId;
  }

  public String getCivicAddress() {
    return civicAddress;
  }

  public void setCivicAddress(String civicAddress) {
    this.civicAddress = civicAddress;
  }

  public Long getBarometricPressure() {
    return barometricPressure;
  }

  public void setBarometricPressure(Long barometricPressure) {
    this.barometricPressure = barometricPressure;
  }

}
