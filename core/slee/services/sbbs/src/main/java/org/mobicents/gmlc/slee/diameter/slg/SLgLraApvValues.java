package org.mobicents.gmlc.slee.diameter.slg;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.ExperimentalResultAvp;
import net.java.slee.resource.diameter.slg.SLgAVPFactory;
import net.java.slee.resource.diameter.slg.events.avp.ELPAVPCodes;
import net.java.slee.resource.diameter.slg.events.avp.PLMNIDListAvp;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLocationSupportIndicator;
import net.java.slee.resource.diameter.slg.events.avp.PrioritizedListIndicator;
import net.java.slee.resource.diameter.slg.events.avp.ReportingPLMNListAvp;
import org.apache.log4j.Logger;

import java.io.Serializable;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SLgLraApvValues implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final int DIAMETER_SLg_VENDOR_ID = 10415;

  private static final Logger logger = Logger.getLogger(SLgLraApvValues.class.getName());

  /*
    3GPP TS 29.172 v15.0.0 reference
      7.3.4	Location-Report-Answer (LRA) Command
      The Location-Report-Answer (LRA) command, indicated by the Command-Code field set to 8388621 and the 'R' bit cleared in the Command Flags field,
      is sent by the GMLC to the MME or SGSN in response to the Location-Report-Request command.

    Message Format

    < Location-Report-Answer > ::= < Diameter Header: 8388621, PXY, 16777255>
                                  < Session-Id >
                                  [ DRMP ]
                                  [ Vendor-Specific-Application-Id ]
                                  [ Result-Code ]
                                  [ Experimental-Result ]
                                  { Auth-Session-State }
                                  { Origin-Host }
                                  { Origin-Realm }
                                  [ GMLC-Address ]
                                  [ LRA-Flags ]
                                  [ Reporting-PLMN-List ]
                                  [ LCS-Reference-Number ]
                                  *[ Supported-Features ]
                                  *[ AVP ]
                                  [ Failed-AVP ]
                                  *[ Proxy-Info ]
                                  *[ Route-Record ]
  */

  private Long resultCode;
  private ExperimentalResultAvp experimentalResultAvp;
  private Address gmlcAddress;
  private Long lraFlags;
  private ReportingPLMNListAvp reportingPLMNList;
  private PrioritizedListIndicator prioritizedListIndicator;
  private PLMNIDListAvp plmnIdList;
  private byte[] visitedPLMNId;
  private PeriodicLocationSupportIndicator periodicLocationSupportIndicator;
  private byte[] lcsReferenceNumber;

  public SLgLraApvValues() {
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

  public Address getGmlcAddress() {
    return gmlcAddress;
  }

  public void setGmlcAddress(Address gmlcAddress) {
    this.gmlcAddress = gmlcAddress;
  }

  public Long getLraFlags() {
    return lraFlags;
  }

  public void setLraFlags(Long lraFlags) {
    this.lraFlags = lraFlags;
  }

  public ReportingPLMNListAvp getReportingPLMNList() {
    return reportingPLMNList;
  }

  public void setReportingPLMNList(ReportingPLMNListAvp reportingPLMNList) {
    this.reportingPLMNList = reportingPLMNList;
  }

  public PrioritizedListIndicator getPrioritizedListIndicator() {
    return prioritizedListIndicator;
  }

  public void setPrioritizedListIndicator(PrioritizedListIndicator prioritizedListIndicator) {
    this.prioritizedListIndicator = prioritizedListIndicator;
  }

  public PLMNIDListAvp getPlmnIdList() {
    return plmnIdList;
  }

  public void setPlmnIdList(PLMNIDListAvp plmnIdList) {
    this.plmnIdList = plmnIdList;
  }

  public byte[] getVisitedPLMNId() {
    return visitedPLMNId;
  }

  public void setVisitedPLMNId(byte[] visitedPLMNId) {
    this.visitedPLMNId = visitedPLMNId;
  }

  public PeriodicLocationSupportIndicator getPeriodicLocationSupportIndicator() {
    return periodicLocationSupportIndicator;
  }

  public void setPeriodicLocationSupportIndicator(PeriodicLocationSupportIndicator periodicLocationSupportIndicator) {
    this.periodicLocationSupportIndicator = periodicLocationSupportIndicator;
  }

  public byte[] getLcsReferenceNumber() {
    return lcsReferenceNumber;
  }

  public void setLcsReferenceNumber(byte[] lcsReferenceNumber) {
    this.lcsReferenceNumber = lcsReferenceNumber;
  }

  /** PLR AVPs building method **/
  public void createPLRAvps(SLgAVPFactory slgAVPFactory) throws Exception {

    try {

      // AVP name="GMLC-Address" code="2405" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Address"
      DiameterAvp gmlcAddressAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.GMLC_ADDRESS);
      setGmlcAddress((Address) gmlcAddressAvp);

      // AVP name="LRA-Flags" code="2549" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Unsigned32"
      DiameterAvp lraFlagsAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LRA_FLAGS, this.lraFlags);
      setLraFlags(lraFlagsAvp.longValue());

      // AVP ame="Prioritized-List-Indicator" code="2551" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp prioritizedListIndicatorAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.PRIORITIZED_LIST_INDICATOR, this.prioritizedListIndicator);
      PrioritizedListIndicator prioritizedListIndicatorCode = PrioritizedListIndicator.fromInt(prioritizedListIndicatorAvp.getCode());
      setPrioritizedListIndicator(prioritizedListIndicatorCode);
      // AVP name="Periodic-Location-Support-Indicator" code="2550" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="Enumerated"
      DiameterAvp periodicLocSupportIndicatorAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.PERIODIC_LOCATION_SUPPORT_INDICATOR, this.periodicLocationSupportIndicator);
      PeriodicLocationSupportIndicator periodicLocationSupportIndicatorCode = PeriodicLocationSupportIndicator.fromInt(periodicLocSupportIndicatorAvp.getCode());
      setPeriodicLocationSupportIndicator(periodicLocationSupportIndicatorCode);
      // APN name="Visited-PLMN-Id" code="1407" vendor-id="TGPP" mandatory="must" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="OctetString"
      DiameterAvp visitedPlmnIdAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.VISITED_PLMN_ID, this.visitedPLMNId);
      setVisitedPLMNId(visitedPlmnIdAvp.byteArrayValue());
      // AVP name="PLMN-ID-List" code="2544" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" >
      //		<grouped>
      //			<avp name="Visited-PLMN-Id" multiplicity="1" />
      //			<avp name="Periodic-Location-Support-Indicator" multiplicity="0-1" />
      //		</grouped>
      plmnIdList = slgAVPFactory.createPLMNIDList();
      plmnIdList.setVisitedPLMNId(getVisitedPLMNId());
      plmnIdList.setPeriodicLocationSupportIndicator(getPeriodicLocationSupportIndicator());
      // AVP name="Reporting-PLMN-List" code="2543" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" >
      //		<grouped>
      //			<avp name="PLMN-ID-List" multiplicity="1+" /> <!-- 1-20 -->
      //			<avp name="Prioritized-List-Indicator" multiplicity="0-1" />
      //		</grouped>
      reportingPLMNList = slgAVPFactory.createReportingPLMNList();
      reportingPLMNList.setPLMNIDList(getPlmnIdList());
      reportingPLMNList.setPrioritizedListIndicator(getPrioritizedListIndicator());

      // AVP ame="LCS-Reference-Number" code="2531" vendor-id="TGPP" mandatory="mustnot" protected="mustnot" may-encrypt="no" vendor-bit="must" type-name="OctetString"
      DiameterAvp lcsReferenceNumberAvp = slgAVPFactory.createAvp(DIAMETER_SLg_VENDOR_ID, ELPAVPCodes.LCS_REFERENCE_NUMBER, this.lcsReferenceNumber);
      setLcsReferenceNumber(lcsReferenceNumberAvp.byteArrayValue());

    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }
}
