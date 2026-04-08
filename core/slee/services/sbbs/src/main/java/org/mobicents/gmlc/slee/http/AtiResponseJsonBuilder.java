package org.mobicents.gmlc.slee.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.map.AtiResponseParams;
import org.mobicents.gmlc.slee.primitives.RoutingAreaId;
import org.mobicents.gmlc.slee.primitives.RoutingAreaIdImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.PlmnId;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.UsedRATType;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.EUtranCgi;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRCellGlobalId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.NRTAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.TAId;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.FQDN;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.EUtranCgiImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.NRTAIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.TAIdImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberManagement.LSAIdentityImpl;

import java.nio.charset.StandardCharsets;

import static org.mobicents.gmlc.slee.http.JsonWriter.bytesToHexString;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAddressPresentationRestrictedIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAmfAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAol;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeConfidence;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeCurrentLocationRetrieved;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeENBId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeEUtranEci;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImei;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeInternalNetworkNumberIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLac;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLastRatType;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLatitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLocationNumberAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLongitude;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLsaId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLsaLSB;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMSClassmark;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMSNetworkCapability;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMSRadioAccessCapability;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMcc;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMmeName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnc;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnpImsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnpMsisdn;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnpRouteingNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnpStatus;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMscNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMsisdn;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNatureOfAddressIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetwork;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNotReachableReason;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNrCellId;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNumberingPlanIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOddFlag;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperation;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeProtocol;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeRoutingAreaCode;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSaiPresent;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeScreeningAndPresentationIndicators;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeScreeningIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeServiceAreaCode;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSubscriberState;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeTime;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeTrackingAreaCode;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeTypeOfShape;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeUncertainty;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVlrNumber;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class AtiResponseJsonBuilder {

    protected static final Logger logger = Logger.getLogger(AtiResponseJsonBuilder.class);

    public AtiResponseJsonBuilder() {
    }

    /**
     * Handle generating the appropriate HTTP response in JSON format
     *
     * @param atiResponseParams values gathered from MAP ATI response event
     */
    public static String buildJsonResponseForAti(AtiResponseParams atiResponseParams, String atiMsisdnDigits, String atiImsi, String dialogResultMessage) throws MAPException {

        int csMcc, csMnc, csLac, csCiOrSac, psMcc, psMnc, psLac, psCiOrSac, ecgiMcc, ecgiMnc, ecgiCi, taiMcc, taiMnc, tac, raiMcc, raiMnc, raiLac, rac,
                nrCgiMcc, nrCgiMnc, nrTaiMcc, nrTaiMnc, nrTaiTac, vPlmnIdMcc, vPlmnIdMnc, ageOfLocationInfo, natureOfAddressIndicator, internalNetworkNumberIndicator, numberingPlanIndicator, addressPresentationRestrictedIndicator,
                screeningIndicator, geodeticConfidence, geodeticScreeningAndPresentationIndicators, epsGeodeticConfidence, epsGeodeticScreeningAndPresentationIndicators,
                gprsGeodeticConfidence, gprsGeodeticScreeningAndPresentationIndicators, nrGeodeticConfidence, nrGeodeticScreeningAndPresentationIndicators,
                year, month, day, hour, minute, second;
        csMcc = csMnc = csLac = csCiOrSac = psMcc = psMnc = psLac = psCiOrSac = ecgiMcc = ecgiMnc = ecgiCi = taiMcc = taiMnc = tac = raiMcc = raiMnc = raiLac = rac =
                nrCgiMcc = nrCgiMnc = nrTaiMcc = nrTaiMnc = nrTaiTac = vPlmnIdMcc = vPlmnIdMnc = -1;
        Integer mnpInfoResultNumberPortabilityStatus = null;
        String vlrNumber, mscNumber, csSubscriberState, psSubscriberState, notReachableReason, sgsnNumber, locationNumberAddressDigits, geographicalTypeOfShape, geodeticTypeOfShape,
            gprsGeographicalTypeOfShape, gprsGeodeticTypeOfShape, epsGeographicalTypeOfShape, epsGeodeticTypeOfShape, nrGeographicalTypeOfShape, nrGeodeticTypeOfShape, mnpInfoResultMSISDN, mnpInfoResultIMSI,
            mnpInfoResultRouteingNumber, mmeName, lsaId, msClassmark, msNetCap, msRASCap, imei, lastUeActivityTime, lastRatType, epsSubscriberState, amfAddressString;
        csSubscriberState = psSubscriberState = epsSubscriberState = notReachableReason = mnpInfoResultMSISDN = mnpInfoResultIMSI =
                mnpInfoResultRouteingNumber = msClassmark = msNetCap = msRASCap = imei = lastUeActivityTime = lastRatType = null;
        double geographicalLatitude, geographicalLongitude, geographicalUncertainty, geodeticLatitude, geodeticLongitude, geodeticUncertainty;
        double epsGeographicalLatitude, epsGeographicalLongitude, epsGeographicalUncertainty, epsGeodeticLatitude, epsGeodeticLongitude, epsGeodeticUncertainty;
        double gprsGeographicalLatitude, gprsGeographicalLongitude, gprsGeographicalUncertainty, gprsGeodeticLatitude, gprsGeodeticLongitude, gprsGeodeticUncertainty;
        double nrGeographicalLatitude, nrGeographicalLongitude, nrGeographicalUncertainty, nrGeodeticLatitude, nrGeodeticLongitude, nrGeodeticUncertainty;
        Long ecgiEci = null, ecgiENBId = null, nrCgiCi = null;
        boolean oddFlag, lsaUniversal, saiPresent;
        saiPresent = false;

        JsonObject atiResponseJsonObject = new JsonObject();
        writeNetwork("GSM/UMTS", atiResponseJsonObject);
        writeProtocol("MAP", atiResponseJsonObject);
        writeOperation("ATI", atiResponseJsonObject);
        writeOperationResult(dialogResultMessage, atiResponseJsonObject);
        JsonObject atiCSLocationInformationJsonObject = null;
        JsonObject atiCsEPSLocationInformationJsonObject;
        JsonObject atiPSLocationInformationJsonObject = null;
        JsonObject atiEPSLocationInformationJsonObject = null;
        JsonObject ati5GSLocationInformationJsonObject = null;

        if (atiResponseParams != null) {

            if (atiResponseParams.getLocationInformation() != null) {
                atiCSLocationInformationJsonObject = new JsonObject();

                if (atiResponseParams.getLocationInformation().getSaiPresent())
                    saiPresent = true;

                if (atiResponseParams.getLocationInformation().getLocationNumber() != null) {
                    if (atiResponseParams.getLocationInformation().getLocationNumber().getLocationNumber() != null) {
                        JsonObject locationNumberJsonObject = new JsonObject();
                        LocationNumber locationNumber = atiResponseParams.getLocationInformation().getLocationNumber().getLocationNumber();
                        oddFlag = locationNumber.isOddFlag();
                        natureOfAddressIndicator = locationNumber.getNatureOfAddressIndicator();
                        internalNetworkNumberIndicator = locationNumber.getInternalNetworkNumberIndicator();
                        numberingPlanIndicator = locationNumber.getNumberingPlanIndicator();
                        addressPresentationRestrictedIndicator = locationNumber.getAddressRepresentationRestrictedIndicator();
                        screeningIndicator = locationNumber.getScreeningIndicator();
                        locationNumberAddressDigits = locationNumber.getAddress();
                        writeOddFlag(oddFlag, locationNumberJsonObject);
                        writeNatureOfAddressIndicator(natureOfAddressIndicator, locationNumberJsonObject);
                        writeInternalNetworkNumberIndicator(internalNetworkNumberIndicator, locationNumberJsonObject);
                        writeNumberingPlanIndicator(numberingPlanIndicator, locationNumberJsonObject);
                        writeAddressPresentationRestrictedIndicator(addressPresentationRestrictedIndicator, locationNumberJsonObject);
                        writeScreeningIndicator(screeningIndicator, locationNumberJsonObject);
                        writeLocationNumberAddress(locationNumberAddressDigits, locationNumberJsonObject);
                        atiCSLocationInformationJsonObject.add("LocationNumber", locationNumberJsonObject);
                    }
                }

                if (atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                    JsonObject csCgiOrLaiOrSaiJsonObject = new JsonObject();
                    if (atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                        csMcc = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC();
                        csMnc = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC();
                        csLac = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac();
                    } else if (atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                        csMcc = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                        csMnc = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                        csLac = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                        csCiOrSac = atiResponseParams.getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                    }
                    writeMcc(csMcc, csCgiOrLaiOrSaiJsonObject);
                    writeMnc(csMnc, csCgiOrLaiOrSaiJsonObject);
                    writeLac(csLac, csCgiOrLaiOrSaiJsonObject);
                    if (!saiPresent) {
                        if (csCiOrSac != -1) {
                            writeCellId(csCiOrSac, csCgiOrLaiOrSaiJsonObject);
                            atiCSLocationInformationJsonObject.add("CGI", csCgiOrLaiOrSaiJsonObject);
                        } else {
                            if (csLac != -1)
                                atiCSLocationInformationJsonObject.add("LAI", csCgiOrLaiOrSaiJsonObject);
                        }
                    } else {
                        if (csCiOrSac != -1) {
                            writeServiceAreaCode(csCiOrSac, csCgiOrLaiOrSaiJsonObject);
                            atiCSLocationInformationJsonObject.add("SAI", csCgiOrLaiOrSaiJsonObject);
                        } else {
                            if (csLac != -1)
                                atiCSLocationInformationJsonObject.add("LAI", csCgiOrLaiOrSaiJsonObject);
                        }
                    }
                }
                if (csCiOrSac != -1)
                    writeSaiPresent(saiPresent, atiCSLocationInformationJsonObject);

                if (atiResponseParams.getLocationInformation().getGeographicalInformation() != null) {
                    JsonObject atiCsGeographicalInformationJsonObject = new JsonObject();
                    geographicalLatitude = atiResponseParams.getLocationInformation().getGeographicalInformation().getLatitude();
                    geographicalLongitude = atiResponseParams.getLocationInformation().getGeographicalInformation().getLongitude();
                    geographicalTypeOfShape = atiResponseParams.getLocationInformation().getGeographicalInformation().getTypeOfShape().name();
                    geographicalUncertainty = atiResponseParams.getLocationInformation().getGeographicalInformation().getUncertainty();
                    writeTypeOfShape(geographicalTypeOfShape, atiCsGeographicalInformationJsonObject);
                    writeLatitude(geographicalLatitude, atiCsGeographicalInformationJsonObject);
                    writeLongitude(geographicalLongitude, atiCsGeographicalInformationJsonObject);
                    writeUncertainty(geographicalUncertainty, geographicalLatitude, geographicalLongitude, atiCsGeographicalInformationJsonObject);
                    atiCSLocationInformationJsonObject.add("GeographicalInformation", atiCsGeographicalInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation().getGeodeticInformation() != null) {
                    JsonObject atiCsGeodeticInformationJsonObject = new JsonObject();
                    geodeticLatitude = atiResponseParams.getLocationInformation().getGeodeticInformation().getLatitude();
                    geodeticLongitude = atiResponseParams.getLocationInformation().getGeodeticInformation().getLongitude();
                    geodeticTypeOfShape = atiResponseParams.getLocationInformation().getGeodeticInformation().getTypeOfShape().name();
                    geodeticUncertainty = atiResponseParams.getLocationInformation().getGeodeticInformation().getUncertainty();
                    geodeticConfidence = atiResponseParams.getLocationInformation().getGeodeticInformation().getConfidence();
                    geodeticScreeningAndPresentationIndicators = atiResponseParams.getLocationInformation().getGeodeticInformation().getScreeningAndPresentationIndicators();
                    writeTypeOfShape(geodeticTypeOfShape, atiCsGeodeticInformationJsonObject);
                    writeLatitude(geodeticLatitude, atiCsGeodeticInformationJsonObject);
                    writeLongitude(geodeticLongitude, atiCsGeodeticInformationJsonObject);
                    writeUncertainty(geodeticUncertainty, geodeticLatitude, geodeticLongitude, atiCsGeodeticInformationJsonObject);
                    writeConfidence(geodeticConfidence, geodeticLatitude, geodeticLongitude, atiCsGeodeticInformationJsonObject);
                    writeScreeningAndPresentationIndicators(geodeticScreeningAndPresentationIndicators, geodeticLatitude, geodeticLongitude, atiCsGeodeticInformationJsonObject);
                    atiCSLocationInformationJsonObject.add("GeodeticInformation", atiCsGeodeticInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation().getAgeOfLocationInformation() != null) {
                    ageOfLocationInfo = atiResponseParams.getLocationInformation().getAgeOfLocationInformation();
                    writeAol(ageOfLocationInfo, atiCSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation().getCurrentLocationRetrieved()) {
                    writeCurrentLocationRetrieved(true, atiCSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation().getVlrNumber() != null) {
                    vlrNumber = atiResponseParams.getLocationInformation().getVlrNumber().getAddress();
                    writeVlrNumber(vlrNumber, atiCSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation().getMscNumber() != null) {
                    mscNumber = atiResponseParams.getLocationInformation().getMscNumber().getAddress();
                    writeMscNumber(mscNumber, atiCSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation().getLocationInformationEPS() != null) {
                    atiCsEPSLocationInformationJsonObject = new JsonObject();

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                        JsonObject csLocInfoEutranCgiJsonObject = new JsonObject();
                        EUtranCgi eUtranCgi = new EUtranCgiImpl(atiResponseParams.getLocationInformation().getLocationInformationEPS().getEUtranCellGlobalIdentity().getData());
                        try {
                            ecgiMcc = eUtranCgi.getMCC();
                            ecgiMnc = eUtranCgi.getMNC();
                            ecgiEci = eUtranCgi.getEci();
                            ecgiENBId = eUtranCgi.getENodeBId();
                            ecgiCi = eUtranCgi.getCi();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                        writeMcc(ecgiMcc, csLocInfoEutranCgiJsonObject);
                        writeMnc(ecgiMnc, csLocInfoEutranCgiJsonObject);
                        writeEUtranEci(ecgiEci, csLocInfoEutranCgiJsonObject);
                        writeENBId(ecgiENBId, csLocInfoEutranCgiJsonObject);
                        writeEUtranCellId(ecgiCi, csLocInfoEutranCgiJsonObject);
                        atiCsEPSLocationInformationJsonObject.add("ECGI", csLocInfoEutranCgiJsonObject);
                    }

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                        JsonObject taiJsonObject = new JsonObject();
                        TAId tai = new TAIdImpl(atiResponseParams.getLocationInformation().getLocationInformationEPS().getTrackingAreaIdentity().getData());
                        try {
                            taiMcc = tai.getMCC();
                            taiMnc = tai.getMNC();
                            tac = tai.getTAC();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                        writeMcc(taiMcc, taiJsonObject);
                        writeMnc(taiMnc, taiJsonObject);
                        writeTrackingAreaCode(tac, taiJsonObject);
                        atiCsEPSLocationInformationJsonObject.add("TAI", taiJsonObject);
                    }

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeographicalInformation() != null) {
                        JsonObject atiEpsGeographicalInformationJsonObject = new JsonObject();
                        epsGeographicalLatitude = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getLatitude();
                        epsGeographicalLongitude = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getLongitude();
                        epsGeographicalTypeOfShape = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getTypeOfShape().name();
                        epsGeographicalUncertainty = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeographicalInformation().getUncertainty();
                        writeTypeOfShape(epsGeographicalTypeOfShape, atiEpsGeographicalInformationJsonObject);
                        writeLatitude(epsGeographicalLatitude, atiEpsGeographicalInformationJsonObject);
                        writeLongitude(epsGeographicalLongitude, atiEpsGeographicalInformationJsonObject);
                        writeUncertainty(epsGeographicalUncertainty, epsGeographicalLatitude, epsGeographicalLongitude, atiEpsGeographicalInformationJsonObject);
                        atiCsEPSLocationInformationJsonObject.add("GeographicalInformation", atiEpsGeographicalInformationJsonObject);
                    }

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation() != null) {
                        JsonObject atiEpsGeodeticInformationJsonObject = new JsonObject();
                        epsGeodeticLatitude = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getLatitude();
                        epsGeodeticLongitude = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getLongitude();
                        epsGeodeticTypeOfShape = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getTypeOfShape().name();
                        epsGeodeticUncertainty = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getUncertainty();
                        epsGeodeticConfidence = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getConfidence();
                        epsGeodeticScreeningAndPresentationIndicators = atiResponseParams.getLocationInformation().getLocationInformationEPS().getGeodeticInformation().getScreeningAndPresentationIndicators();
                        writeTypeOfShape(epsGeodeticTypeOfShape, atiEpsGeodeticInformationJsonObject);
                        writeLatitude(epsGeodeticLatitude, atiEpsGeodeticInformationJsonObject);
                        writeLongitude(epsGeodeticLongitude, atiEpsGeodeticInformationJsonObject);
                        writeUncertainty(epsGeodeticUncertainty, epsGeodeticLatitude, epsGeodeticLongitude, atiEpsGeodeticInformationJsonObject);
                        writeConfidence(epsGeodeticConfidence, epsGeodeticLatitude, epsGeodeticLongitude, atiEpsGeodeticInformationJsonObject);
                        writeScreeningAndPresentationIndicators(epsGeodeticScreeningAndPresentationIndicators, epsGeodeticLatitude, epsGeodeticLongitude, atiEpsGeodeticInformationJsonObject);
                        atiCsEPSLocationInformationJsonObject.add("GeodeticInformation", atiEpsGeodeticInformationJsonObject);
                    }

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getAgeOfLocationInformation() != null) {
                        ageOfLocationInfo = atiResponseParams.getLocationInformation().getLocationInformationEPS().getAgeOfLocationInformation();
                        writeAol(ageOfLocationInfo, atiCsEPSLocationInformationJsonObject);
                    }

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getCurrentLocationRetrieved()) {
                        writeCurrentLocationRetrieved(true, atiCsEPSLocationInformationJsonObject);
                    }

                    if (atiResponseParams.getLocationInformation().getLocationInformationEPS().getMmeName() != null) {
                        mmeName = new String(atiResponseParams.getLocationInformation().getLocationInformationEPS().getMmeName().getData());
                        writeMmeName(mmeName, atiCsEPSLocationInformationJsonObject);
                    }

                    // Write EPS Location Information values
                    atiCSLocationInformationJsonObject.add("EPSLocationInformation", atiCsEPSLocationInformationJsonObject);
                }
            }

            if (atiResponseParams.getLocationInformationGPRS() != null) {
                atiPSLocationInformationJsonObject = new JsonObject();

                if (atiResponseParams.getLocationInformationGPRS().isSaiPresent())
                    saiPresent = true;

                if (atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI() != null) {
                    JsonObject psCgiOrLaiOrSaiJsonObject = new JsonObject();
                    if (atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength() != null) {
                        psMcc = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMCC();
                        psMnc = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getMNC();
                        psLac = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getLAIFixedLength().getLac();
                    } else if (atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength() != null) {
                        psMcc = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
                        psMnc = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
                        psLac = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
                        psCiOrSac = atiResponseParams.getLocationInformationGPRS().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();
                    }
                    writeMcc(psMcc, psCgiOrLaiOrSaiJsonObject);
                    writeMnc(psMnc, psCgiOrLaiOrSaiJsonObject);
                    writeLac(psLac, psCgiOrLaiOrSaiJsonObject);
                    if (!saiPresent) {
                        if (psCiOrSac != -1) {
                            writeCellId(psCiOrSac, psCgiOrLaiOrSaiJsonObject);
                            atiPSLocationInformationJsonObject.add("CGI", psCgiOrLaiOrSaiJsonObject);
                        } else {
                            if (psLac != -1)
                                atiPSLocationInformationJsonObject.add("LAI", psCgiOrLaiOrSaiJsonObject);
                        }
                    } else {
                        if (psCiOrSac != -1) {
                            writeServiceAreaCode(psCiOrSac, psCgiOrLaiOrSaiJsonObject);
                            atiPSLocationInformationJsonObject.add("SAI", psCgiOrLaiOrSaiJsonObject);
                        } else {
                            if (psLac != -1)
                                atiPSLocationInformationJsonObject.add("LAI", psCgiOrLaiOrSaiJsonObject);
                        }
                    }
                }
                if (psCiOrSac != -1)
                    writeSaiPresent(saiPresent, atiPSLocationInformationJsonObject);

                if (atiResponseParams.getLocationInformationGPRS().getRouteingAreaIdentity() != null) {
                    JsonObject raiJsonObject = new JsonObject();
                    RoutingAreaId rai = new RoutingAreaIdImpl(atiResponseParams.getLocationInformationGPRS().getRouteingAreaIdentity().getData());
                    try {
                        raiMcc = rai.getMCC();
                        raiMnc = rai.getMNC();
                        raiLac = rai.getLAC();
                        rac = rai.getRAC();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(raiMcc, raiJsonObject);
                    writeMnc(raiMnc, raiJsonObject);
                    writeLac(raiLac, raiJsonObject);
                    writeRoutingAreaCode(rac, raiJsonObject);
                    atiPSLocationInformationJsonObject.add("RAI", raiJsonObject);
                }

                if (atiResponseParams.getLocationInformationGPRS().getLSAIdentity() != null) {
                    JsonObject lsaJsonObject = new JsonObject();
                    LSAIdentity lsaIdentity = new LSAIdentityImpl(atiResponseParams.getLocationInformationGPRS().getLSAIdentity().getData());
                    lsaUniversal = lsaIdentity.isPlmnSignificantLSA(); // isPlmnSignificantLSA means the opposite in jSS7 implementation
                    lsaId = new String(atiResponseParams.getLocationInformationGPRS().getLSAIdentity().getData());
                    writeLsaLSB(lsaUniversal, lsaJsonObject);
                    writeLsaId(lsaId, lsaJsonObject);
                    atiPSLocationInformationJsonObject.add("LSA", lsaJsonObject);
                }

                if (atiResponseParams.getLocationInformationGPRS().getGeographicalInformation() != null) {
                    JsonObject atiGprsGeographicalInformationJsonObject = new JsonObject();
                    gprsGeographicalLatitude = atiResponseParams.getLocationInformationGPRS().getGeographicalInformation().getLatitude();
                    gprsGeographicalLongitude = atiResponseParams.getLocationInformationGPRS().getGeographicalInformation().getLongitude();
                    gprsGeographicalTypeOfShape = atiResponseParams.getLocationInformationGPRS().getGeographicalInformation().getTypeOfShape().name();
                    gprsGeographicalUncertainty = atiResponseParams.getLocationInformationGPRS().getGeographicalInformation().getUncertainty();
                    writeTypeOfShape(gprsGeographicalTypeOfShape, atiGprsGeographicalInformationJsonObject);
                    writeLatitude(gprsGeographicalLatitude, atiGprsGeographicalInformationJsonObject);
                    writeLongitude(gprsGeographicalLongitude, atiGprsGeographicalInformationJsonObject);
                    writeUncertainty(gprsGeographicalUncertainty, gprsGeographicalLatitude, gprsGeographicalLongitude, atiGprsGeographicalInformationJsonObject);
                    atiPSLocationInformationJsonObject.add("GeographicalInformation", atiGprsGeographicalInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationGPRS().getGeodeticInformation() != null) {
                    JsonObject atiGprsGeodeticInformationJsonObject = new JsonObject();
                    gprsGeodeticLatitude = atiResponseParams.getLocationInformationGPRS().getGeodeticInformation().getLatitude();
                    gprsGeodeticLongitude = atiResponseParams.getLocationInformationGPRS().getGeodeticInformation().getLongitude();
                    gprsGeodeticTypeOfShape = atiResponseParams.getLocationInformationGPRS().getGeodeticInformation().getTypeOfShape().name();
                    gprsGeodeticUncertainty = atiResponseParams.getLocationInformationGPRS().getGeodeticInformation().getUncertainty();
                    gprsGeodeticConfidence = atiResponseParams.getLocationInformationGPRS().getGeodeticInformation().getConfidence();
                    gprsGeodeticScreeningAndPresentationIndicators = atiResponseParams.getLocationInformationGPRS().getGeodeticInformation().getScreeningAndPresentationIndicators();
                    writeTypeOfShape(gprsGeodeticTypeOfShape, atiGprsGeodeticInformationJsonObject);
                    writeLatitude(gprsGeodeticLatitude, atiGprsGeodeticInformationJsonObject);
                    writeLongitude(gprsGeodeticLongitude, atiGprsGeodeticInformationJsonObject);
                    writeUncertainty(gprsGeodeticUncertainty, gprsGeodeticLatitude, gprsGeodeticLongitude, atiGprsGeodeticInformationJsonObject);
                    writeConfidence(gprsGeodeticConfidence, gprsGeodeticLatitude, gprsGeodeticLongitude, atiGprsGeodeticInformationJsonObject);
                    writeScreeningAndPresentationIndicators(gprsGeodeticScreeningAndPresentationIndicators, gprsGeodeticLatitude, gprsGeodeticLongitude, atiGprsGeodeticInformationJsonObject);
                    atiPSLocationInformationJsonObject.add("GeodeticInformation", atiGprsGeodeticInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationGPRS().getAgeOfLocationInformation() != null) {
                    ageOfLocationInfo = atiResponseParams.getLocationInformationGPRS().getAgeOfLocationInformation();
                    writeAol(ageOfLocationInfo, atiPSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationGPRS().isCurrentLocationRetrieved()) {
                    writeCurrentLocationRetrieved(true, atiPSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationGPRS().getSGSNNumber() != null) {
                    sgsnNumber = atiResponseParams.getLocationInformationGPRS().getSGSNNumber().getAddress();
                    writeSgsnNumber(sgsnNumber, atiPSLocationInformationJsonObject);
                }

            }

            if (atiResponseParams.getImei() != null)
                imei = atiResponseParams.getImei().getIMEI();

            if (atiResponseParams.getSubscriberState() != null) {
                csSubscriberState = atiResponseParams.getSubscriberState().getSubscriberStateChoice().toString();
                if (atiResponseParams.getSubscriberState().getNotReachableReason() != null)
                    notReachableReason = atiResponseParams.getSubscriberState().getNotReachableReason().name();
            }
            if (atiResponseParams.getPsSubscriberState() != null) {
                psSubscriberState = atiResponseParams.getPsSubscriberState().getChoice().toString();
                if (atiResponseParams.getPsSubscriberState().getNetDetNotReachable() != null)
                    notReachableReason = atiResponseParams.getPsSubscriberState().getNetDetNotReachable().name();
            }

            if (atiResponseParams.getMsClassmark2() != null) {
                msClassmark = bytesToHexString(atiResponseParams.getMsClassmark2().getData());
            }

            if (atiResponseParams.getGprsMSClass() != null) {
                msNetCap = bytesToHexString(atiResponseParams.getGprsMSClass().getMSNetworkCapability().getData());
                msRASCap = bytesToHexString(atiResponseParams.getGprsMSClass().getMSRadioAccessCapability().getData());
            }

            if (atiResponseParams.getMnpInfoRes() != null) {
                if (atiResponseParams.getMnpInfoRes().getNumberPortabilityStatus() != null) {
                    mnpInfoResultNumberPortabilityStatus = atiResponseParams.getMnpInfoRes().getNumberPortabilityStatus().getType();
                }
                if (atiResponseParams.getMnpInfoRes().getMSISDN() != null) {
                    mnpInfoResultMSISDN = atiResponseParams.getMnpInfoRes().getMSISDN().getAddress();
                }
                if (atiResponseParams.getMnpInfoRes().getIMSI() != null) {
                    mnpInfoResultIMSI = new String(atiResponseParams.getMnpInfoRes().getIMSI().getData().getBytes());
                }
                if (atiResponseParams.getMnpInfoRes().getRouteingNumber() != null) {
                    mnpInfoResultRouteingNumber = atiResponseParams.getMnpInfoRes().getRouteingNumber().getRouteingNumber();
                }
            }

            if (atiResponseParams.getLastUEActivityTime() != null) {
                year = atiResponseParams.getLastUEActivityTime().getYear();
                month = atiResponseParams.getLastUEActivityTime().getMonth();
                day = atiResponseParams.getLastUEActivityTime().getDay();
                hour = atiResponseParams.getLastUEActivityTime().getHour();
                minute = atiResponseParams.getLastUEActivityTime().getMinute();
                second = atiResponseParams.getLastUEActivityTime().getSecond();
                lastUeActivityTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            }

            if (atiResponseParams.getLastRATType() != null) {
                UsedRATType usedRATType = UsedRATType.getInstance(atiResponseParams.getLastRATType().getCode());
                lastRatType = usedRATType.name();
            }

            // TODO atiResponseParams.getTimeZone() & atiResponseParams.getDaylightSavingTime()

            if (atiResponseParams.getEpsSubscriberState() != null) {
                epsSubscriberState = atiResponseParams.getEpsSubscriberState().getChoice().toString();
                if (atiResponseParams.getEpsSubscriberState().getNetDetNotReachable() != null)
                    notReachableReason = atiResponseParams.getEpsSubscriberState().getNetDetNotReachable().name();
            }

            if (atiResponseParams.getLocationInformationEPS() != null) {
                JsonObject locInfoEpsEUtranCgiJsonObject = new JsonObject();
                atiEPSLocationInformationJsonObject = new JsonObject();
                if (atiResponseParams.getLocationInformationEPS().getEUtranCellGlobalIdentity() != null) {
                    EUtranCgi eUtranCgi = new EUtranCgiImpl(atiResponseParams.getLocationInformationEPS().getEUtranCellGlobalIdentity().getData());
                    try {
                        ecgiMcc = eUtranCgi.getMCC();
                        ecgiMnc = eUtranCgi.getMNC();
                        ecgiEci = eUtranCgi.getEci();
                        ecgiENBId = eUtranCgi.getENodeBId();
                        ecgiCi = eUtranCgi.getCi();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(ecgiMcc, locInfoEpsEUtranCgiJsonObject);
                    writeMnc(ecgiMnc, locInfoEpsEUtranCgiJsonObject);
                    writeEUtranEci(ecgiEci, locInfoEpsEUtranCgiJsonObject);
                    writeENBId(ecgiENBId, locInfoEpsEUtranCgiJsonObject);
                    writeEUtranCellId(ecgiCi, locInfoEpsEUtranCgiJsonObject);
                    atiEPSLocationInformationJsonObject.add("ECGI", locInfoEpsEUtranCgiJsonObject);
                }

                if (atiResponseParams.getLocationInformationEPS().getTrackingAreaIdentity() != null) {
                    JsonObject taiJsonObject = new JsonObject();
                    TAId tai = new TAIdImpl(atiResponseParams.getLocationInformationEPS().getTrackingAreaIdentity().getData());
                    try {
                        taiMcc = tai.getMCC();
                        taiMnc = tai.getMNC();
                        tac = tai.getTAC();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(taiMcc, taiJsonObject);
                    writeMnc(taiMnc, taiJsonObject);
                    writeTrackingAreaCode(tac, taiJsonObject);
                    atiEPSLocationInformationJsonObject.add("TAI", taiJsonObject);
                }

                if (atiResponseParams.getLocationInformationEPS().getGeographicalInformation() != null) {
                    JsonObject atiNonCsEpsGeographicalInformationJsonObject = new JsonObject();
                    epsGeographicalLatitude = atiResponseParams.getLocationInformationEPS().getGeographicalInformation().getLatitude();
                    epsGeographicalLongitude = atiResponseParams.getLocationInformationEPS().getGeographicalInformation().getLongitude();
                    epsGeographicalTypeOfShape = atiResponseParams.getLocationInformationEPS().getGeographicalInformation().getTypeOfShape().name();
                    epsGeographicalUncertainty = atiResponseParams.getLocationInformationEPS().getGeographicalInformation().getUncertainty();
                    writeTypeOfShape(epsGeographicalTypeOfShape, atiNonCsEpsGeographicalInformationJsonObject);
                    writeLatitude(epsGeographicalLatitude, atiNonCsEpsGeographicalInformationJsonObject);
                    writeLongitude(epsGeographicalLongitude, atiNonCsEpsGeographicalInformationJsonObject);
                    writeUncertainty(epsGeographicalUncertainty, epsGeographicalLatitude, epsGeographicalLongitude, atiNonCsEpsGeographicalInformationJsonObject);
                    atiEPSLocationInformationJsonObject.add("GeographicalInformation", atiNonCsEpsGeographicalInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationEPS().getGeodeticInformation() != null) {
                    JsonObject atiNonCsEpsGeodeticInformationJsonObject = new JsonObject();
                    epsGeodeticLatitude = atiResponseParams.getLocationInformationEPS().getGeodeticInformation().getLatitude();
                    epsGeodeticLongitude = atiResponseParams.getLocationInformationEPS().getGeodeticInformation().getLongitude();
                    epsGeodeticTypeOfShape = atiResponseParams.getLocationInformationEPS().getGeodeticInformation().getTypeOfShape().name();
                    epsGeodeticUncertainty = atiResponseParams.getLocationInformationEPS().getGeodeticInformation().getUncertainty();
                    epsGeodeticConfidence = atiResponseParams.getLocationInformationEPS().getGeodeticInformation().getConfidence();
                    epsGeodeticScreeningAndPresentationIndicators = atiResponseParams.getLocationInformationEPS().getGeodeticInformation().getScreeningAndPresentationIndicators();
                    writeTypeOfShape(epsGeodeticTypeOfShape, atiNonCsEpsGeodeticInformationJsonObject);
                    writeLatitude(epsGeodeticLatitude, atiNonCsEpsGeodeticInformationJsonObject);
                    writeLongitude(epsGeodeticLongitude, atiNonCsEpsGeodeticInformationJsonObject);
                    writeUncertainty(epsGeodeticUncertainty, epsGeodeticLatitude, epsGeodeticLongitude, atiNonCsEpsGeodeticInformationJsonObject);
                    writeConfidence(epsGeodeticConfidence, epsGeodeticLatitude, epsGeodeticLongitude, atiNonCsEpsGeodeticInformationJsonObject);
                    writeScreeningAndPresentationIndicators(epsGeodeticScreeningAndPresentationIndicators, epsGeodeticLatitude, epsGeodeticLongitude, atiNonCsEpsGeodeticInformationJsonObject);
                    atiEPSLocationInformationJsonObject.add("GeodeticInformation", atiNonCsEpsGeodeticInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationEPS().getAgeOfLocationInformation() != null) {
                    ageOfLocationInfo = atiResponseParams.getLocationInformationEPS().getAgeOfLocationInformation();
                    writeAol(ageOfLocationInfo, atiEPSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationEPS().getCurrentLocationRetrieved()) {
                    writeCurrentLocationRetrieved(true, atiEPSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformationEPS().getMmeName() != null) {
                    mmeName = new String(atiResponseParams.getLocationInformationEPS().getMmeName().getData());
                    writeMmeName(mmeName, atiEPSLocationInformationJsonObject);
                }
            }

            if (atiResponseParams.getLocationInformation5GS() != null) {
                ati5GSLocationInformationJsonObject = new JsonObject();
                if (atiResponseParams.getLocationInformation5GS().getNRCellGlobalId() != null) {
                    JsonObject nrCgiJsonObject = new JsonObject();
                    NRCellGlobalId nrCellGlobalId = atiResponseParams.getLocationInformation5GS().getNRCellGlobalId();
                    try {
                        nrCgiMcc = nrCellGlobalId.getMCC();
                        nrCgiMnc = nrCellGlobalId.getMNC();
                        nrCgiCi = nrCellGlobalId.getNCI();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(nrCgiMcc, nrCgiJsonObject);
                    writeMnc(nrCgiMnc, nrCgiJsonObject);
                    writeNrCellId(nrCgiCi, nrCgiJsonObject);
                    ati5GSLocationInformationJsonObject.add("NCGI", nrCgiJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getNRTAId() != null) {
                    JsonObject nrTaiJsonObject = new JsonObject();
                    NRTAId nrtaId = new NRTAIdImpl(atiResponseParams.getLocationInformation5GS().getNRTAId().getData());
                    try {
                        nrTaiMcc = nrtaId.getMCC();
                        nrTaiMnc = nrtaId.getMNC();
                        nrTaiTac = nrtaId.getNrTAC();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(nrTaiMcc, nrTaiJsonObject);
                    writeMnc(nrTaiMnc, nrTaiJsonObject);
                    writeTrackingAreaCode(nrTaiTac, nrTaiJsonObject);
                    ati5GSLocationInformationJsonObject.add("NR-TAI", nrTaiJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getEUtranCgi() != null) {
                    JsonObject locInfo5gsEUtranCgiJsonObject = new JsonObject();
                    EUtranCgi eUtranCgi = new EUtranCgiImpl(atiResponseParams.getLocationInformation5GS().getEUtranCgi().getData());
                    try {
                        ecgiMcc = eUtranCgi.getMCC();
                        ecgiMnc = eUtranCgi.getMNC();
                        ecgiEci = eUtranCgi.getEci();
                        ecgiENBId = eUtranCgi.getENodeBId();
                        ecgiCi = eUtranCgi.getCi();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(ecgiMcc, locInfo5gsEUtranCgiJsonObject);
                    writeMnc(ecgiMnc, locInfo5gsEUtranCgiJsonObject);
                    writeEUtranEci(ecgiEci, locInfo5gsEUtranCgiJsonObject);
                    writeENBId(ecgiENBId, locInfo5gsEUtranCgiJsonObject);
                    writeEUtranCellId(ecgiCi, locInfo5gsEUtranCgiJsonObject);
                    ati5GSLocationInformationJsonObject.add("ECGI", locInfo5gsEUtranCgiJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getTAId() != null) {
                    JsonObject locInfo5gsTaiJsonObject = new JsonObject();
                    TAId tai = new TAIdImpl(atiResponseParams.getLocationInformation5GS().getTAId().getData());
                    try {
                        taiMcc = tai.getMCC();
                        taiMnc = tai.getMNC();
                        tac = tai.getTAC();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                    writeMcc(taiMcc, locInfo5gsTaiJsonObject);
                    writeMnc(taiMnc, locInfo5gsTaiJsonObject);
                    writeTrackingAreaCode(tac, locInfo5gsTaiJsonObject);
                    ati5GSLocationInformationJsonObject.add("TAI", locInfo5gsTaiJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getGeographicalInformation() != null) {
                    JsonObject ati5gsGeographicalInformationJsonObject = new JsonObject();
                    nrGeographicalTypeOfShape = atiResponseParams.getLocationInformation5GS().getGeographicalInformation().getTypeOfShape().name();
                    nrGeographicalLatitude = atiResponseParams.getLocationInformation5GS().getGeographicalInformation().getLatitude();
                    nrGeographicalLongitude = atiResponseParams.getLocationInformation5GS().getGeographicalInformation().getLongitude();
                    nrGeographicalUncertainty = atiResponseParams.getLocationInformation5GS().getGeographicalInformation().getUncertainty();
                    writeTypeOfShape(nrGeographicalTypeOfShape, ati5gsGeographicalInformationJsonObject);
                    writeLatitude(nrGeographicalLatitude, ati5gsGeographicalInformationJsonObject);
                    writeLongitude(nrGeographicalLongitude, ati5gsGeographicalInformationJsonObject);
                    writeUncertainty(nrGeographicalUncertainty, nrGeographicalLatitude, nrGeographicalLongitude, ati5gsGeographicalInformationJsonObject);
                    ati5GSLocationInformationJsonObject.add("GeographicalInformation", ati5gsGeographicalInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getGeodeticInformation() != null) {
                    JsonObject ati5gsGeodeticInformationJsonObject = new JsonObject();
                    nrGeodeticTypeOfShape = atiResponseParams.getLocationInformation5GS().getGeodeticInformation().getTypeOfShape().name();
                    nrGeodeticLatitude = atiResponseParams.getLocationInformation5GS().getGeodeticInformation().getLatitude();
                    nrGeodeticLongitude = atiResponseParams.getLocationInformation5GS().getGeodeticInformation().getLongitude();
                    nrGeodeticUncertainty = atiResponseParams.getLocationInformation5GS().getGeodeticInformation().getUncertainty();
                    nrGeodeticConfidence = atiResponseParams.getLocationInformation5GS().getGeodeticInformation().getConfidence();
                    nrGeodeticScreeningAndPresentationIndicators = atiResponseParams.getLocationInformation5GS().getGeodeticInformation().getScreeningAndPresentationIndicators();
                    writeTypeOfShape(nrGeodeticTypeOfShape, ati5gsGeodeticInformationJsonObject);
                    writeLatitude(nrGeodeticLatitude, ati5gsGeodeticInformationJsonObject);
                    writeLongitude(nrGeodeticLongitude, ati5gsGeodeticInformationJsonObject);
                    writeUncertainty(nrGeodeticUncertainty, nrGeodeticLatitude, nrGeodeticLongitude, ati5gsGeodeticInformationJsonObject);
                    writeConfidence(nrGeodeticConfidence, nrGeodeticLatitude, nrGeodeticLongitude, ati5gsGeodeticInformationJsonObject);
                    writeScreeningAndPresentationIndicators(nrGeodeticScreeningAndPresentationIndicators, nrGeodeticLatitude, nrGeodeticLongitude, ati5gsGeodeticInformationJsonObject);
                    ati5GSLocationInformationJsonObject.add("GeodeticInformation", ati5gsGeodeticInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getAMFAddress() != null) {
                    FQDN amfAddress = atiResponseParams.getLocationInformation5GS().getAMFAddress();
                    amfAddressString = new String(amfAddress.getData(), StandardCharsets.UTF_8);
                    writeAmfAddress(amfAddressString, ati5GSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getAgeOfLocationInformation() != null) {
                    ageOfLocationInfo = atiResponseParams.getLocationInformation5GS().getAgeOfLocationInformation();
                    writeAol(ageOfLocationInfo, ati5GSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().isCurrentLocationRetrieved()) {
                    writeCurrentLocationRetrieved(true, ati5GSLocationInformationJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS() != null) {
                    JsonObject locInfo5gsVPlmnIdJsonObject = new JsonObject();
                    if (atiResponseParams.getLocationInformation5GS().getVPlmnId() != null) {
                        PlmnId vPlmnId = atiResponseParams.getLocationInformation5GS().getVPlmnId();
                        try {
                            vPlmnIdMcc = vPlmnId.getMcc();
                            vPlmnIdMnc = vPlmnId.getMnc();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                        writeMcc(vPlmnIdMcc, locInfo5gsVPlmnIdJsonObject);
                        writeMnc(vPlmnIdMnc, locInfo5gsVPlmnIdJsonObject);
                    }
                    ati5GSLocationInformationJsonObject.add("VisitedPLMNId", locInfo5gsVPlmnIdJsonObject);
                }

                if (atiResponseParams.getLocationInformation5GS().getUsedRATType() != null) {
                    JsonObject nrUsedRatTypeJsonObject = new JsonObject();
                    UsedRATType usedRATType = UsedRATType.getInstance(atiResponseParams.getLocationInformation5GS().getUsedRATType().getCode());
                    String ratType = usedRATType.name();
                    writeLastRatType(ratType, nrUsedRatTypeJsonObject);
                    ati5GSLocationInformationJsonObject.add("Used-RAT-Type", nrUsedRatTypeJsonObject);
                }
            }
        }

        // Write ATI values
        if (atiMsisdnDigits != null)
            writeMsisdn(atiMsisdnDigits, atiResponseJsonObject);

        if (atiImsi != null)
            writeImsi(atiImsi, atiResponseJsonObject);

        if (imei != null)
            writeImei(imei, atiResponseJsonObject);

        // Write CS Location Information values which might include EPS Location Information values
        if (atiCSLocationInformationJsonObject != null)
            atiResponseJsonObject.add("CSLocationInformation", atiCSLocationInformationJsonObject);

        // Write PS Location Information values
        if (atiPSLocationInformationJsonObject != null)
            atiResponseJsonObject.add("PSLocationInformation", atiPSLocationInformationJsonObject);

        // Write EPS Location Information values (not included in CS Location Information)
        if (atiEPSLocationInformationJsonObject != null)
            atiResponseJsonObject.add("EPSLocationInformation", atiEPSLocationInformationJsonObject);

        // Write 5GS Location Information values
        if (ati5GSLocationInformationJsonObject != null)
            atiResponseJsonObject.add("5GSLocationInformation", ati5GSLocationInformationJsonObject);

        // Write Subscriber State
        if (csSubscriberState != null)
            writeSubscriberState(csSubscriberState, atiResponseJsonObject);
        else if (psSubscriberState != null)
            writeSubscriberState(psSubscriberState, atiResponseJsonObject);
        else if (epsSubscriberState != null)
            writeSubscriberState(epsSubscriberState, atiResponseJsonObject);
        if (notReachableReason != null)
            writeNotReachableReason(notReachableReason, atiResponseJsonObject);

        // Write MNP Result Info values
        if (mnpInfoResultNumberPortabilityStatus != null || mnpInfoResultMSISDN != null || mnpInfoResultIMSI != null || mnpInfoResultRouteingNumber != null) {
            JsonObject atiMnpInfoResultJsonObject = new JsonObject();
            writeMnpStatus(mnpInfoResultNumberPortabilityStatus, atiMnpInfoResultJsonObject);
            writeMnpMsisdn(mnpInfoResultMSISDN, atiMnpInfoResultJsonObject);
            writeMnpImsi(mnpInfoResultIMSI, atiMnpInfoResultJsonObject);
            writeMnpRouteingNumber(mnpInfoResultRouteingNumber, atiMnpInfoResultJsonObject);
            atiResponseJsonObject.add("MNPInfoResult", atiMnpInfoResultJsonObject);
        }

        // Write MS Classmark
        if (msClassmark != null)
            writeMSClassmark(msClassmark, atiResponseJsonObject);

        // Write GPRS MS Class values
        if (msNetCap != null || msRASCap != null) {
            JsonObject atiGprsMsClassJsonObject = new JsonObject();
            writeMSNetworkCapability(msNetCap, atiGprsMsClassJsonObject); // TODO: fix decoding of this value
            writeMSRadioAccessCapability(msRASCap, atiGprsMsClassJsonObject); // TODO: fix decoding of this value
            atiResponseJsonObject.add("GPRSMSClass", atiGprsMsClassJsonObject);
        }

        // Write last UE activity time
        if (lastUeActivityTime != null) {
            JsonObject atiLastUeActivityTime = new JsonObject();
            writeTime(lastUeActivityTime, atiLastUeActivityTime);
            atiResponseJsonObject.add("LastUEActivityTime", atiLastUeActivityTime);
        }

        // Write Last RAT type
        if (lastRatType != null) {
            JsonObject atiLastRatType = new JsonObject();
            writeLastRatType(lastRatType, atiLastRatType);
            atiResponseJsonObject.add("LastRATType", atiLastRatType);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(atiResponseJsonObject);
    }

}
