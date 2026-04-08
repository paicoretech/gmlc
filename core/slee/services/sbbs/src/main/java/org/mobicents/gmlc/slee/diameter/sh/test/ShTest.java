package org.mobicents.gmlc.slee.diameter.sh.test;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformation;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformationExtension;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformationExtension2;
import org.mobicents.gmlc.slee.diameter.sh.CSLocationInformationExtension3;
import org.mobicents.gmlc.slee.diameter.sh.EPSLocationInformation;
import org.mobicents.gmlc.slee.diameter.sh.EPSLocationInformationExtension;
import org.mobicents.gmlc.slee.diameter.sh.EPSLocationInformationExtension2;
import org.mobicents.gmlc.slee.diameter.sh.Extension;
import org.mobicents.gmlc.slee.diameter.sh.LocalTimeZone;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformation;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformationExtension;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformationExtension2;
import org.mobicents.gmlc.slee.diameter.sh.PSLocationInformationExtension3;
import org.mobicents.gmlc.slee.diameter.sh.PublicIds;
import org.mobicents.gmlc.slee.diameter.sh.Sh5GSLocationInformation;
import org.mobicents.gmlc.slee.diameter.sh.ShDataReader;
import org.mobicents.gmlc.slee.diameter.sh.ShUdaAvpValues;
import org.mobicents.gmlc.slee.diameter.sh.UserCSGInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShCellGlobalId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShEUTRANCellGlobalId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShGeodeticInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShGeographicalInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShLocationAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShLocationNumber;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShNRCellGlobalId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShRoutingAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShServiceAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShTrackingAreaId;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShUserCSGInformation;
import org.mobicents.gmlc.slee.diameter.sh.elements.ShVisitedPLMNId;

import java.io.File;
import java.io.IOException;

public class ShTest {

  private static final Logger logger = Logger.getLogger(ShTest.class.getName());

  public static void main(String[] args) {

    try {

      ShDataReader shDataReader = new ShDataReader();
      File XMLfile = new File("core/slee/services/sbbs/src/main/java/org/mobicents/gmlc/slee/diameter/sh/test/Sh-Data.xml");
      String xmlFileToStr = FileUtils.readFileToString(XMLfile, "UTF-8");
      shDataReader.ShXMLReader(xmlFileToStr);

      ShUdaAvpValues shUdaAvpValues = new ShUdaAvpValues();

      PublicIds shPublicIds = shDataReader.getShPublicIdentifiers();
      String msisdn = shPublicIds.getMsisdn();
      shUdaAvpValues.setMsisdn(msisdn);
      String imsPublicIdentity = shPublicIds.getImsPublicIdentity();
      shUdaAvpValues.setImsPublicIdentity(imsPublicIdentity);
      logger.info("MSISDN: "+shUdaAvpValues.getMsisdn());
      logger.info("IMSPublicIdentity: "+shUdaAvpValues.getImsPublicIdentity());

      CSLocationInformation shDataCsLocation = shDataReader.getShCSLocationInfo();
      shUdaAvpValues.setCsLocationInformation(shDataCsLocation);
      String cellGlobalId = shDataCsLocation.getCellGlobalId();
      String serviceAreaId = shDataCsLocation.getServiceAreaId();
      String locationAreaId = shDataCsLocation.getLocationAreaId();
      String locationNumber = shDataCsLocation.getLocationNumber();
      String geographicalInfo = shDataCsLocation.getGeographicalInformation();
      String geodeticInfo = shDataCsLocation.getGeodeticInformation();
      CSLocationInformationExtension csLocationExtension = shDataCsLocation.getCsLocationInformationExtension();
      String csCsgId = null, csEutranCellGlobalId = null, csTrackingAreaId = null;
      LocalTimeZone csLocalTimeZone = null;
      if (csLocationExtension != null) {
        UserCSGInformation userCSGInformationCS = csLocationExtension.getUserCSGInformation();
        csCsgId = userCSGInformationCS.getCsgid();
        CSLocationInformationExtension2 csLocationInformationExtension2 = csLocationExtension.getCsLocationInformationExtension2();
        if (csLocationInformationExtension2 != null) {
          csEutranCellGlobalId = csLocationInformationExtension2.geteUTRANCellGlobalId();
          csTrackingAreaId = csLocationInformationExtension2.getTrackingAreaId();
          CSLocationInformationExtension3 csLocationInformationExtension3 = csLocationInformationExtension2.getCsLocationInformationExtension3();
          if (csLocationInformationExtension3 != null) {
            csLocalTimeZone = csLocationInformationExtension3.getLocalTimeZone();
          }
        }
      }
      ShCellGlobalId csCellGlobalId = new ShCellGlobalId();
      ShServiceAreaId csServiceAreaId = new ShServiceAreaId();
      ShLocationAreaId csLocationAreaId = new ShLocationAreaId();
      ShLocationNumber csLocationNumber = new ShLocationNumber();
      ShGeographicalInformation csGeographicalInformation = new ShGeographicalInformation();
      ShGeodeticInformation csGeodeticInformation = new ShGeodeticInformation();
      ShUserCSGInformation shCsUserCSGInformation = new ShUserCSGInformation();
      ShEUTRANCellGlobalId shCsEUTRANCellGlobalId = new ShEUTRANCellGlobalId();
      ShTrackingAreaId shCsTrackingAreaId = new ShTrackingAreaId();
      csCellGlobalId.setCellGlobalIdStr(cellGlobalId);
      shUdaAvpValues.setCsCellGlobalId(csCellGlobalId);
      csServiceAreaId.setServiceAreaIdStr(serviceAreaId);
      shUdaAvpValues.setCsServiceAreaId(csServiceAreaId);
      csLocationAreaId.setLocationAreIdStr(locationAreaId);
      shUdaAvpValues.setCsLocationAreaId(csLocationAreaId);
      csLocationNumber.setLocationNumberStr(locationNumber);
      shUdaAvpValues.setLocationNumber(csLocationNumber);
      csGeographicalInformation.setGeographicalInfoStr(geographicalInfo);
      shUdaAvpValues.setCsGeographicalInformation(csGeographicalInformation);
      csGeodeticInformation.setGeodeticInfoStr(geodeticInfo);
      shUdaAvpValues.setCsGeodeticInformation(csGeodeticInformation);
      shUdaAvpValues.setMscNumber(shDataCsLocation.getMscNumber());
      shUdaAvpValues.setVlrNumber(shDataCsLocation.getVlrNumber());
      shUdaAvpValues.setCsCurrentLocationInfoRetrieved((shDataCsLocation.getCurrentLocationRetrieved()));
      shUdaAvpValues.setCsAgeOfLocationInfo(Integer.valueOf(shDataCsLocation.getAgeOfLocationInformation()));
      shCsUserCSGInformation.setUserCSGInformationStr(csCsgId);
      shUdaAvpValues.setUserCSGInformation(shCsUserCSGInformation);
      shCsEUTRANCellGlobalId.setECGIStr(csEutranCellGlobalId);
      shUdaAvpValues.setEutrancgi(shCsEUTRANCellGlobalId);
      shCsTrackingAreaId.setTrackingAreaIdStr(csTrackingAreaId);
      shUdaAvpValues.setTrackingAreaId(shCsTrackingAreaId);
      shUdaAvpValues.setCsLocalTimeZone(csLocalTimeZone);

      logger.info("**************************\n");
      logger.info("CSLocationInformation: \n");
      logger.info(shUdaAvpValues.getCsCellGlobalId().toString());
      logger.info(shUdaAvpValues.getCsServiceAreaId().toString());
      logger.info(shUdaAvpValues.getCsLocationAreaId().toString());
      logger.info(shUdaAvpValues.getLocationNumber().toString());
      logger.info(shUdaAvpValues.getCsGeographicalInformation().toString());
      logger.info(shUdaAvpValues.getCsGeodeticInformation().toString());
      logger.info("MSC Number [address = "+shUdaAvpValues.getMscNumber().getAddress()+"]");
      logger.info("VLR Number [address = "+shUdaAvpValues.getVlrNumber().getAddress()+"]");
      logger.info("CurrentLocationRetrieved = "+shUdaAvpValues.getCsCurrentLocationInfoRetrieved());
      logger.info("AgeOfLocationInformation = "+shUdaAvpValues.getCsAgeOfLocationInfo());
      logger.info("UserCSGInformation [ "+shUdaAvpValues.getUserCSGInformation().toString()+"]");
      logger.info(shUdaAvpValues.getEutrancgi().toString());
      logger.info(shUdaAvpValues.getTrackingAreaId().toString());
      logger.info("Local Time Zone = "+shUdaAvpValues.getCsLocalTimeZone());

      PSLocationInformation shDataPsLocation = shDataReader.getShPSLocationInfo();
      String cellGlobalIdPs = shDataPsLocation.getCellGlobalId();
      String serviceAreaIdPs = shDataPsLocation.getServiceAreaId();
      String locationAreaIdPs = shDataPsLocation.getLocationAreaId();
      String routingAreaIdPs = shDataPsLocation.getRoutingAreaId();
      String geographicalInfoPs = shDataPsLocation.getGeographicalInformation();
      String geodeticInfoPs = shDataPsLocation.getGeodeticInformation();
      ShCellGlobalId psCellGlobalId = new ShCellGlobalId();
      ShServiceAreaId psServiceAreaId = new ShServiceAreaId();
      ShLocationAreaId psLocationAreaId = new ShLocationAreaId();
      ShRoutingAreaId psRAI = new ShRoutingAreaId();
      ShGeographicalInformation psGeographicalInformation = new ShGeographicalInformation();
      ShGeodeticInformation psGeodeticInformation = new ShGeodeticInformation();
      ShUserCSGInformation shPsUserCSGInformation = new ShUserCSGInformation();
      ShVisitedPLMNId shPsVisitedPLMNId = new ShVisitedPLMNId();
      psCellGlobalId.setCellGlobalIdStr(cellGlobalIdPs);
      shUdaAvpValues.setPsCellGlobalId(psCellGlobalId);
      psServiceAreaId.setServiceAreaIdStr(serviceAreaIdPs);
      shUdaAvpValues.setPsServiceAreaId(psServiceAreaId);
      psLocationAreaId.setLocationAreIdStr(locationAreaIdPs);
      shUdaAvpValues.setPsLocationAreaId(psLocationAreaId);
      psRAI.setRoutingAreaIdentityStr(routingAreaIdPs);
      shUdaAvpValues.setRoutingAreaId(psRAI);
      psGeographicalInformation.setGeographicalInfoStr(geographicalInfoPs);
      shUdaAvpValues.setPsGeographicalInformation(psGeographicalInformation);
      psGeodeticInformation.setGeodeticInfoStr(geodeticInfoPs);
      shUdaAvpValues.setPsGeodeticInformation(psGeodeticInformation);
      shUdaAvpValues.setSgsnNumber(shDataPsLocation.getSgsnNumber());
      shUdaAvpValues.setPsCurrentLocationInfoRetrieved((shDataPsLocation.getCurrentLocationRetrieved()));
      shUdaAvpValues.setPsAgeOfLocationInfo(Integer.valueOf(shDataPsLocation.getAgeOfLocationInformation()));
      String psCsgId = null, psVisitedPlmnId = null, psRatType = null;
      LocalTimeZone psLocalTimeZone = null;
      PSLocationInformationExtension psLocationInformationExtension = shDataPsLocation.getPsLocationInformationExtension();
      if (psLocationInformationExtension != null) {
        UserCSGInformation userCSGInformationPS = psLocationInformationExtension.getUserCSGInformation();
        psCsgId = userCSGInformationPS.getCsgid();
        PSLocationInformationExtension2 psLocationInformationExtension2 = psLocationInformationExtension.getPsLocationInformationExtension2();
        if (psLocationInformationExtension2 != null) {
          psVisitedPlmnId = psLocationInformationExtension2.getVisitedPLMNId();
          psLocalTimeZone = psLocationInformationExtension2.getLocalTimeZone();
          PSLocationInformationExtension3 psLocationInformationExtension3 = psLocationInformationExtension2.getPsLocationInformationExtension3();
          if (psLocationInformationExtension3 != null) {
            psRatType = psLocationInformationExtension3.getRatType();
          }
        }
      }
      shPsUserCSGInformation.setUserCSGInformationStr(psCsgId);
      shUdaAvpValues.setUserCSGInformation(shPsUserCSGInformation);
      shPsVisitedPLMNId.setVisitedPlmnIdStr(psVisitedPlmnId);
      shUdaAvpValues.setPsVisitedPLMNId(shPsVisitedPLMNId);
      shUdaAvpValues.setPsLocalTimeZone(psLocalTimeZone);
      if (psRatType != null)
        shUdaAvpValues.setPsRatType(Integer.valueOf(psRatType));

      logger.info("\n**************************\n");
      logger.info("PSLocationInformation: \n");
      logger.info(shUdaAvpValues.getPsCellGlobalId().toString());
      logger.info(shUdaAvpValues.getPsServiceAreaId().toString());
      logger.info(shUdaAvpValues.getRoutingAreaId().toString());
      logger.info(shUdaAvpValues.getPsGeographicalInformation().toString());
      logger.info(shUdaAvpValues.getPsGeodeticInformation().toString());
      logger.info(shUdaAvpValues.getSgsnNumber().toString());
      logger.info("CurrentLocationRetrieved = "+shUdaAvpValues.getPsCurrentLocationInfoRetrieved());
      logger.info("AgeOfLocationInformation = "+shUdaAvpValues.getPsAgeOfLocationInfo().toString());
      logger.info("UserCSGInformation [ "+shUdaAvpValues.getUserCSGInformation().toString()+"]");
      logger.info("VisitedPLMNID = "+shUdaAvpValues.getPsVisitedPLMNId().toString());
      logger.info("Local Time Zone = "+shUdaAvpValues.getPsLocalTimeZone());
      logger.info("RAT Type = "+shUdaAvpValues.getPsRatType());

      Extension shDataEpsLocation = shDataReader.getShEPSLocationInfo();
      EPSLocationInformation epsLocationInformation = shDataEpsLocation.getExtension().getExtension().getExtension().getEpsLocationInformation();
      String eUtranCellGlobalId = epsLocationInformation.geteUTRANCellGlobalId();
      String trackingAreaId = epsLocationInformation.getTrackingAreaId();
      String geographicalInfoEps = epsLocationInformation.getGeographicalInformation();
      String geodeticInfoEps = epsLocationInformation.getGeodeticInformation();
      String mmeName = epsLocationInformation.getMmeName();
      String epsCurrentLocationRetrieved = epsLocationInformation.getCurrentLocationRetrieved();
      String epsAgeOfLocationInformation = epsLocationInformation.getAgeOfLocationInformation();
      String epsCsgId = epsLocationInformation.getUserCSGInformation().getCsgid();
      String epsVisitedPlmnId = null, epsRatType = null;
      LocalTimeZone epsLocalTimeZone = null;
      EPSLocationInformationExtension epsLocationInformationExtension = epsLocationInformation.getEpsLocationInformationExtension();
      if (epsLocationInformationExtension != null) {
        epsVisitedPlmnId = epsLocationInformationExtension.getVisitedPLMNId();
        epsLocalTimeZone = epsLocationInformationExtension.getLocalTimeZone();
        EPSLocationInformationExtension2 epsLocationInformationExtension2 = epsLocationInformationExtension.getEpsLocationInformationExtension2();
        if (epsLocationInformationExtension2 != null) {
          epsRatType = epsLocationInformationExtension2.getRatType();
        }
      }
      ShEUTRANCellGlobalId shEUTRANCellGlobalId = new ShEUTRANCellGlobalId();
      ShTrackingAreaId shTrackingAreaId = new ShTrackingAreaId();
      ShGeographicalInformation shGeographicalInformationEps = new ShGeographicalInformation();
      ShGeodeticInformation shGeodeticInformationEps = new ShGeodeticInformation();
      ShUserCSGInformation shUserCSGInformation = new ShUserCSGInformation();
      ShVisitedPLMNId shEpsVisitedPLMNId = new ShVisitedPLMNId();
      shEUTRANCellGlobalId.setECGIStr(eUtranCellGlobalId);
      shUdaAvpValues.setEutrancgi(shEUTRANCellGlobalId);
      shTrackingAreaId.setTrackingAreaIdStr(trackingAreaId);
      shUdaAvpValues.setTrackingAreaId(shTrackingAreaId);
      shGeographicalInformationEps.setGeographicalInfoStr(geographicalInfoEps);
      shUdaAvpValues.setEpsGeographicalInformation(shGeographicalInformationEps);
      shGeodeticInformationEps.setGeodeticInfoStr(geodeticInfoEps);
      shUdaAvpValues.setEpsGeodeticInformation(shGeodeticInformationEps);
      shUserCSGInformation.setUserCSGInformationStr(epsCsgId);
      shUdaAvpValues.setUserCSGInformation(shUserCSGInformation);
      shUdaAvpValues.setMmeName(mmeName);
      shUdaAvpValues.setEpsCurrentLocationInfoRetrieved((epsCurrentLocationRetrieved));
      shUdaAvpValues.setEpsAgeOfLocationInfo(Integer.valueOf(epsAgeOfLocationInformation));
      shEpsVisitedPLMNId.setVisitedPlmnIdStr(epsVisitedPlmnId);
      shUdaAvpValues.setEpsVisitedPLMNId(shEpsVisitedPLMNId);
      shUdaAvpValues.setEpsLocalTimeZone(epsLocalTimeZone);
      if (epsRatType != null)
        shUdaAvpValues.setEpsRatType(Integer.valueOf(epsRatType));

      logger.info("\n**************************\n");
      logger.info("EPSLocationInformation: \n");
      logger.info(shUdaAvpValues.getEutrancgi().toString());
      logger.info(shUdaAvpValues.getTrackingAreaId().toString());
      logger.info(shUdaAvpValues.getEpsGeographicalInformation().toString());
      logger.info(shUdaAvpValues.getEpsGeodeticInformation().toString());
      logger.info("MME name = "+shUdaAvpValues.getMmeName());
      logger.info("CurrentLocationRetrieved = "+shUdaAvpValues.getEpsCurrentLocationInfoRetrieved());
      logger.info("AgeOfLocationInformation = "+shUdaAvpValues.getEpsAgeOfLocationInfo());
      logger.info("UserCSGInformation [ "+shUdaAvpValues.getUserCSGInformation().toString()+"]");
      logger.info("VisitedPLMNID = "+shUdaAvpValues.getEpsVisitedPLMNId().toString());
      logger.info("Local Time Zone = "+shUdaAvpValues.getEpsLocalTimeZone());
      logger.info("RAT Type = "+shUdaAvpValues.getEpsRatType());

      Extension shData5GSLocation = shDataReader.getSh5GSLocationInfo();
      Sh5GSLocationInformation sh5GSLocationInfo = shData5GSLocation.getExtension().getExtension().getExtension().getExtension().getExtension().getExtension().getSh5GSLocationInformation();
      String nrCellGlobalId = sh5GSLocationInfo.getNRCellGlobalId();
      eUtranCellGlobalId  = sh5GSLocationInfo.getEUTRANCellGlobalId();
      trackingAreaId = sh5GSLocationInfo.getTrackingAreaId();
      String geographicalInfo5GS = sh5GSLocationInfo.getGeographicalInformation();
      String amfAddress = sh5GSLocationInfo.getAMFAddress();
      String smsfAddress = sh5GSLocationInfo.getSMSFAddress();
      String currentLocationRetrieved5GS = sh5GSLocationInfo.getCurrentLocationRetrieved();
      String ageOfLocationInformation5GS = sh5GSLocationInfo.getAgeOfLocationInformation();
      String visitedPlmnId = sh5GSLocationInfo.getVisitedPLMNId();
      LocalTimeZone sh5GSlocalTimeZone = sh5GSLocationInfo.getLocalTimeZone();
      String ratType = sh5GSLocationInfo.getRatType();
      ShNRCellGlobalId shNRCellGlobalId = new ShNRCellGlobalId();
      // Sh5GSTrackingAreaId sh5GSTrackingAreaId = new Sh5GSTrackingAreaId();
      ShVisitedPLMNId shVisitedPLMNId = new ShVisitedPLMNId();
      ShGeographicalInformation sh5GSGeographicalInformation = new ShGeographicalInformation();
      shNRCellGlobalId.setNRCellGlobalIdStr(nrCellGlobalId);
      shUdaAvpValues.setShNRCellGlobalId(shNRCellGlobalId);
      shEUTRANCellGlobalId.setECGIStr(eUtranCellGlobalId);
      shUdaAvpValues.setEutrancgi(shEUTRANCellGlobalId);
      shTrackingAreaId.setTrackingAreaIdStr(trackingAreaId);
      shUdaAvpValues.setTrackingAreaId(shTrackingAreaId);
      sh5GSGeographicalInformation.setGeographicalInfoStr(geographicalInfo5GS);
      shUdaAvpValues.setSh5GSGeographicalInformation(sh5GSGeographicalInformation);
      shUdaAvpValues.setAmfAddress(amfAddress);
      shUdaAvpValues.setSmsfAddress(smsfAddress);
      shUdaAvpValues.setSh5GSCurrentLocationInfoRetrieved(currentLocationRetrieved5GS);
      shUdaAvpValues.setSh5GSAgeOfLocationInfo(Integer.valueOf(ageOfLocationInformation5GS));
      shVisitedPLMNId.setVisitedPlmnIdStr(visitedPlmnId);
      shUdaAvpValues.setSh5gsVisitedPLMNId(shVisitedPLMNId);
      shUdaAvpValues.setSh5gsLocalTimeZone(sh5GSlocalTimeZone);
      shUdaAvpValues.setSh5gsRatType(Integer.valueOf(ratType));

      logger.info("\n**************************\n");
      logger.info("5GSLocationInformation: \n");
      logger.info(shUdaAvpValues.getShNRCellGlobalId().toString());
      logger.info(shUdaAvpValues.getTrackingAreaId().toString());
      logger.info(shUdaAvpValues.getSh5GSGeographicalInformation().toString());
      logger.info("AMF address = "+shUdaAvpValues.getAmfAddress());
      logger.info("SMSF address = "+shUdaAvpValues.getSmsfAddress());
      logger.info("CurrentLocationRetrieved = "+shUdaAvpValues.getSh5GSCurrentLocationInfoRetrieved());
      logger.info("AgeOfLocationInformation = "+shUdaAvpValues.getSh5GSAgeOfLocationInfo());
      logger.info("VisitedPLMNID = "+shUdaAvpValues.getSh5gsVisitedPLMNId().toString());
      logger.info("Local Time Zone = "+shUdaAvpValues.getSh5gsLocalTimeZone());
      logger.info("RAT Type = "+shUdaAvpValues.getSh5gsRatType());

    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

}
