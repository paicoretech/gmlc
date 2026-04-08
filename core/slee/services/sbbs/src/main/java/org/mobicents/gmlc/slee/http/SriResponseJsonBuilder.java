package org.mobicents.gmlc.slee.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.mobicents.gmlc.slee.map.SriLcsResponseParams;
import org.mobicents.gmlc.slee.map.SriResponseValues;
import org.mobicents.gmlc.slee.map.SriSmResponseParams;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.mobicents.gmlc.slee.http.JsonWriter.bytesToHexString;
import static org.mobicents.gmlc.slee.http.JsonWriter.write3gppAaaServerName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeAdditionalVGmlcAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeGprsNodeIndicator;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeHGmlcAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeImsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeLmsi;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMmeName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMnpStatus;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeMsisdn;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetwork;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeNetworkNodeNumber;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperation;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeOperationResult;
import static org.mobicents.gmlc.slee.http.JsonWriter.writePprAddress;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeProtocol;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnName;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeSgsnRealm;
import static org.mobicents.gmlc.slee.http.JsonWriter.writeVGmlcAddress;
import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class SriResponseJsonBuilder {

    public SriResponseJsonBuilder() {
    }

    /**
     * Handle generating the appropriate HTTP response in JSON format
     *
     * @param msisdn        MSISDN value used on SRI/SRISM/SRILCS attempt
     * @param imsi          IMSI value used on SRI/SRISM/SRILCS attempt
     * @param sri           Subscriber Information values gathered from SRI response event
     * @param sriSm         Subscriber Information values gathered from SRISM response event
     * @param sriLcs        Subscriber Information values gathered from SRILCS response event
     */
    public static String buildJsonResponseForSri(String imsi, String msisdn, String operation,
                                                 SriResponseValues sri, SriSmResponseParams sriSm,
                                                 SriLcsResponseParams sriLcs) throws MAPException {

        final Logger logger = LoggerFactory.getLogger(SriResponseJsonBuilder.class.getName());
        int numberPortabilityStatusType = -1;
        boolean gprsNodeIndicator = false;
        String networkNodeNumber, additionalNumber, lmsi, mmeName, sgsnName, sgsnRealm, tgppAAAServerName, hGmlcAddress, vGmlcAddress, pprAddress, addVGmlcAddress;
        networkNodeNumber = additionalNumber = lmsi = mmeName = sgsnName = sgsnRealm = tgppAAAServerName = hGmlcAddress = vGmlcAddress = pprAddress = addVGmlcAddress = null;

        /***************************/
        /*** SRI response values ***/
        /***************************/
        if (sri != null) {
            if (sri.getImsi() != null)
                imsi = new String(sri.getImsi().getData().getBytes());

            if (sri.getMsisdn() != null)
                msisdn = sri.getMsisdn().getAddress();

            if (sri.getVmscAddress() != null)
                networkNodeNumber = sri.getVmscAddress().getAddress();

            if (sri.getNumberPortabilityStatus() != null) {
                numberPortabilityStatusType = sri.getNumberPortabilityStatus().getType();
            }
        }

        /*****************************/
        /*** SRISM response values ***/
        /*****************************/
        if (sriSm != null) {
            if (sriSm.getImsi() != null)
                imsi = new String(sriSm.getImsi().getData().getBytes());

            if (sriSm.getLocationInfoWithLMSI() != null) {
                if (sriSm.getLocationInfoWithLMSI().getNetworkNodeNumber() != null)
                    networkNodeNumber = sriSm.getLocationInfoWithLMSI().getNetworkNodeNumber().getAddress();

                if (sriSm.getLocationInfoWithLMSI().getAdditionalNumber() != null) {
                    if (sriSm.getLocationInfoWithLMSI().getAdditionalNumber().getMSCNumber() != null)
                        additionalNumber = sriSm.getLocationInfoWithLMSI().getAdditionalNumber().getMSCNumber().getAddress();
                    else
                        additionalNumber = sriSm.getLocationInfoWithLMSI().getAdditionalNumber().getSGSNNumber().getAddress();
                }

                if (sriSm.getLocationInfoWithLMSI().getLMSI() != null)
                    lmsi = bytesToHex(sriSm.getLocationInfoWithLMSI().getLMSI().getData());
            }
        }

        /******************************/
        /*** SRILCS response values ***/
        /******************************/
        if (sriLcs != null) {
            if (sriLcs.getMsisdn() != null)
                msisdn = sriLcs.getMsisdn().getAddress();

            if (sriLcs.getImsi() != null)
                imsi = new String(sriLcs.getImsi().getData().getBytes());

            if (sriLcs.getLmsi() != null)
                lmsi = bytesToHex(sriLcs.getLmsi().getData());

            if (sriLcs.getLcsLocationInfo() != null) {
                if (sriLcs.getLcsLocationInfo().getNetworkNodeNumber() != null)
                    networkNodeNumber = sriLcs.getLcsLocationInfo().getNetworkNodeNumber().getAddress();

                if (sriLcs.getLcsLocationInfo().getGprsNodeIndicator())
                    gprsNodeIndicator = sriLcs.getLcsLocationInfo().getGprsNodeIndicator();

                if (sriLcs.getLcsLocationInfo().getAdditionalNumber() != null) {
                    if (sriLcs.getLcsLocationInfo().getAdditionalNumber().getMSCNumber() != null)
                        additionalNumber = sriLcs.getLcsLocationInfo().getAdditionalNumber().getMSCNumber().getAddress();
                    else if (sriLcs.getLcsLocationInfo().getAdditionalNumber().getSGSNNumber() != null)
                        additionalNumber = sriLcs.getLcsLocationInfo().getAdditionalNumber().getSGSNNumber().getAddress();
                }

                if (sriLcs.getLcsLocationInfo().getMmeName() != null)
                    mmeName = new String(sriLcs.getLcsLocationInfo().getMmeName().getData());

                if (sriLcs.getLcsLocationInfo().getAaaServerName() != null) {
                    tgppAAAServerName = new String(sriLcs.getLcsLocationInfo().getAaaServerName().getData());
                }

                if (sriLcs.getLcsLocationInfo().getSgsnName() != null)
                    sgsnName = new String(sriLcs.getLcsLocationInfo().getSgsnName().getData());

                if (sriLcs.getLcsLocationInfo().getSgsnRealm() != null)
                    sgsnRealm = new String(sriLcs.getLcsLocationInfo().getSgsnRealm().getData());
            }

            if (sriLcs.getVGmlcAddress() != null) {
                vGmlcAddress = bytesToHexString(sriLcs.getVGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(vGmlcAddress));
                    vGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
            }

            if (sriLcs.getHGmlcAddress() != null) {
                hGmlcAddress = bytesToHexString(sriLcs.getHGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(hGmlcAddress));
                    hGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
            }

            if (sriLcs.getPprAddress() != null) {
                pprAddress = bytesToHexString(sriLcs.getPprAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(pprAddress));
                    pprAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
            }

            if (sriLcs.getAddVGmlcAddress() != null) {
                addVGmlcAddress = bytesToHexString(sriLcs.getAddVGmlcAddress().getGSNAddressData());
                try {
                    InetAddress address = InetAddress.getByAddress(DatatypeConverter.parseHexBinary(addVGmlcAddress));
                    addVGmlcAddress = address.getHostAddress();
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        JsonObject sriResponseJsonObject = new JsonObject();
        writeNetwork("GSM/UMTS", sriResponseJsonObject);
        writeProtocol("MAP", sriResponseJsonObject);
        writeOperationResult("SUCCESS", sriResponseJsonObject);
        writeOperation(operation, sriResponseJsonObject);
        writeMsisdn(msisdn, sriResponseJsonObject);
        writeImsi(imsi, sriResponseJsonObject);
        writeLmsi(lmsi, sriResponseJsonObject);
        writeNetworkNodeNumber(networkNodeNumber, sriResponseJsonObject);
        writeGprsNodeIndicator(gprsNodeIndicator, sriResponseJsonObject);
        writeAdditionalNetworkNodeNumber(additionalNumber, sriResponseJsonObject);
        writeMmeName(mmeName, sriResponseJsonObject);
        write3gppAaaServerName(tgppAAAServerName, sriResponseJsonObject);
        writeSgsnName(sgsnName, sriResponseJsonObject);
        writeSgsnRealm(sgsnRealm, sriResponseJsonObject);
        writeVGmlcAddress(vGmlcAddress, sriResponseJsonObject);
        writeHGmlcAddress(hGmlcAddress, sriResponseJsonObject);
        writePprAddress(pprAddress, sriResponseJsonObject);
        writeAdditionalVGmlcAddress(addVGmlcAddress, sriResponseJsonObject);
        writeMnpStatus(numberPortabilityStatusType, sriResponseJsonObject);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(sriResponseJsonObject);
    }
}
