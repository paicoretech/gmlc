package org.mobicents.gmlc.slee.map;

import org.restcomm.protocols.ss7.map.api.primitives.GSNAddress;
import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.restcomm.protocols.ss7.map.api.primitives.LMSI;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSLocationInfo;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SriLcsResponseParams implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * RoutingInfoForLCS-Res ::= SEQUENCE {
   *   targetMS                  [0] SubscriberIdentity,
   *   lcsLocationInfo           [1] LCSLocationInfo,
   *   extensionContainer        [2] ExtensionContainer OPTIONAL,
   *   ...,
   *   v-gmlc-Address            [3] GSN-Address OPTIONAL,
   *   h-gmlc-Address            [4] GSN-Address OPTIONAL,
   *   ppr-Address               [5] GSN-Address OPTIONAL,
   *   additional-v-gmlc-Address [6] GSN-Address OPTIONAL }

   * LCSLocationInfo ::= SEQUENCE {
   *   networkNode-Number              ISDN-AddressString,
   *    -- NetworkNode-number can be msc-number, sgsn-number or a dummy value of "0"
   *   lmsi                           [0] LMSI OPTIONAL,
   *   extensionContainer             [1] ExtensionContainer OPTIONAL,
   *   ... ,
   *   gprsNodeIndicator              [2] NULL OPTIONAL,
   *    -- gprsNodeIndicator is set only if the SGSN number is sent as the Network Node Number
   *   additional-Number              [3] Additional-Number OPTIONAL,
   *   supportedLCS-CapabilitySets    [4] SupportedLCS-CapabilitySets OPTIONAL,
   *   additional-LCS-CapabilitySets  [5] SupportedLCS-CapabilitySets OPTIONAL,
   *   mme-Name                       [6] DiameterIdentity OPTIONAL,
   *   aaa-Server-Name                [8] DiameterIdentity OPTIONAL,
   *   sgsn-Name                      [9] DiameterIdentity OPTIONAL,
   *   sgsn-Realm                    [10] DiameterIdentity OPTIONAL
   *  }
   */
  private ISDNAddressString msisdn; // The MSISDN is provided to identify the target MS.
  private IMSI imsi; // International Mobile Subscriber Identity defined in 3GPP TS 23.003.
  private LMSI lmsi; // Local MS identity allocated by the VLR to a given subscriber for internal management of data in the VLR
  private LCSLocationInfo lcsLocationInfo;
  private GSNAddress hGmlcAddress, vGmlcAddress, pprAddress, addVGmlcAddress; // IP address of a H-GMLC, V-GMLC and PPR.

  private String pslMsisdn, pslImsi;
  private Integer pslLcsReferenceNumber, pslReferenceNumber;

  public SriLcsResponseParams() {
    super();
  }

  public ISDNAddressString getMsisdn() {
    return msisdn;
  }

  public void setMsisdn(ISDNAddressString msisdn) {
    this.msisdn = msisdn;
  }

  public IMSI getImsi() {
    return imsi;
  }

  public void setImsi(IMSI imsi) {
    this.imsi = imsi;
  }

  public LMSI getLmsi() {
    return lmsi;
  }

  public void setLmsi(LMSI lmsi) {
    this.lmsi = lmsi;
  }

  public LCSLocationInfo getLcsLocationInfo() {
    return lcsLocationInfo;
  }

  public void setLcsLocationInfo(LCSLocationInfo lcsLocationInfo) {
    this.lcsLocationInfo = lcsLocationInfo;
  }

  public GSNAddress getHGmlcAddress() {
    return hGmlcAddress;
  }

  public void setHGmlcAddress(GSNAddress hGmlcAddress) {
    this.hGmlcAddress = hGmlcAddress;
  }

  public GSNAddress getVGmlcAddress() {
    return vGmlcAddress;
  }

  public void setVGmlcAddress(GSNAddress vGmlcAddress) {
    this.vGmlcAddress = vGmlcAddress;
  }

  public GSNAddress getPprAddress() {
    return pprAddress;
  }

  public void setPprAddress(GSNAddress pprAddress) {
    this.pprAddress = pprAddress;
  }

  public GSNAddress getAddVGmlcAddress() {
    return addVGmlcAddress;
  }

  public void setAddVGmlcAddress(GSNAddress addVGmlcAddress) {
    this.addVGmlcAddress = addVGmlcAddress;
  }

  public String getPslMsisdn() { return this.pslMsisdn; }

  public void setPslMsisdn(String pslMsisdn) { this.pslMsisdn = pslMsisdn; }

  public String getPslImsi() { return this.pslImsi; }

  public void setPslImsi(String pslImsi) { this.pslImsi = pslImsi; }

  public Integer getPslLcsReferenceNumber() { return this.pslLcsReferenceNumber; }

  public void setPslLcsReferenceNumber(Integer pslLcsReferenceNumber) { this.pslLcsReferenceNumber = pslLcsReferenceNumber; }

  public Integer getPslReferenceNumber() { return this.pslReferenceNumber; }

  public void setPslReferenceNumber(Integer pslReferenceNumber) { this.pslReferenceNumber = pslReferenceNumber; }

  @Override
  public String toString() {
    return "SriLcsResponseParams{" +
        "msisdn=" + msisdn +
        ", imsi=" + imsi +
        ", lmsi=" + lmsi +
        ", lcsLocationInfo=" + lcsLocationInfo +
        ", hGmlcAddress=" + hGmlcAddress +
        ", vGmlcAddress=" + vGmlcAddress +
        ", pprAddress=" + pprAddress +
        ", addVGmlcAddress=" + addVGmlcAddress +
        '}';
  }
}

