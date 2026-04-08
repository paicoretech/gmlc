package org.mobicents.gmlc.slee.map;

import org.restcomm.protocols.ss7.map.api.primitives.IMSI;
import org.restcomm.protocols.ss7.map.api.service.sms.IpSmGwGuidance;
import org.restcomm.protocols.ss7.map.api.service.sms.LocationInfoWithLMSI;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 * @author <a href="mailto:aferreiraguido@gmail.com"> Alejandro Ferreira Guido </a>
 */
public class SriSmResponseParams {

    /**
     * RoutingInfoForSM-Res ::= SEQUENCE {
     *   imsi                    IMSI,
     *   locationInfoWithLMSI    [0] LocationInfoWithLMSI,
     *   extensionContainer      [4] ExtensionContainer OPTIONAL,
     *   ...,
     *   ip-sm-gwGuidance        [5] IP-SM-GW-Guidance OPTIONAL
     * }

     * LocationInfoWithLMSI ::= SEQUENCE {
     *  networkNode-Number                         [1] ISDN-AddressString,
     *  lmsi                                       LMSI                           OPTIONAL,
     *  extensionContainer                         ExtensionContainer             OPTIONAL,
     *  ...,
     *  gprsNodeIndicator                          [5] NULL                       OPTIONAL,
     *  -- gprsNodeIndicator is set only if the SGSN number is sent as the Network Node Number
     *  additional-Number                          [6] Additional-Number          OPTIONAL,
     *  networkNodeDiameterAddress                 [7] NetworkNodeDiameterAddress OPTIONAL,
     *  additionalNetworkNodeDiameterAddress       [8] NetworkNodeDiameterAddress OPTIONAL,
     *  thirdNumber                                [9] Additional-Number          OPTIONAL,
     *  thirdNetworkNodeDiameterAddress           [10] NetworkNodeDiameterAddress OPTIONAL,
     *  imsNodeIndicator                          [11] NULL                       OPTIONAL,
     *  -- gprsNodeIndicator and imsNodeIndicator shall not both be present.
     *  -- additionalNumber and thirdNumber shall not both contain the same type of number.
     *  smsf-3gpp-Number                          [12] ISDN-AddressString         OPTIONAL,
     *  smsf-3gpp-DiameterAddress                 [13] NetworkNodeDiameterAddress OPTIONAL,
     *  smsf-non-3gpp-Number                      [14] ISDN-AddressString         OPTIONAL,
     *  smsf-non-3gpp-DiameterAddress             [15] NetworkNodeDiameterAddress OPTIONAL,
     *  smsf-3gpp-address-indicator               [16] NULL                       OPTIONAL,
     *  smsf-non-3gpp-address-indicator           [17] NULL                       OPTIONAL
     *  --
     *  -- If smsf-supportIndicator was not included in the request, in RoutingInfoForSM-Arg,
     *  -- then smsf-3gpp Number/DiameterAddress, smsf-non-3gpp Number/DiameterAddress and
     *  -- smsf-address-indicator and smsf-non-3gpp-address-indicator shall be absent.
     *  --
     *  -- If smsf-3gpp-address-indicator is present, it indicates that the networkNode-Number
     *  -- (and networkNodeDiameterAddress, if present) contains the address of an SMSF for
     *  -- 3GPP access.
     *  --
     *  -- If smsf-non-3gpp-address-indicator is present, it indicates that the
     *  -- networkNode-Number (and networkNodeDiameterAddress, if present) contains the
     *  -- address of an SMSF for non 3GPP access.
     *  --
     *  -- At most one of gprsNodeIndicator, imsNodeIndicator, smsf-3gpp-address-indicator
     *  -- and smsf-non-3gpp-address-indicator shall be present. Absence of all these
     *  -- indicators indicate that the networkNode-Number (and networkNodeDiameterAddress,
     *  -- if present) contains the address of an MSC/MME.
     * }

     * IP-SM-GW-Guidance ::= SEQUENCE {
     *   minimumDeliveryTimeValue              SM-DeliveryTimerValue,
     *   recommendedDeliveryTimeValue          SM-DeliveryTimerValue,
     *   extensionContainer                    ExtensionContainer OPTIONAL,
     *   ...
     * }
     * SM-DeliveryTimerValue ::= INTEGER (30..600)
     */

    private IMSI imsi;
    private LocationInfoWithLMSI locationInfoWithLMSI;
    private IpSmGwGuidance ipSmGwGuidance;

    public SriSmResponseParams() {
    }

    public IMSI getImsi() {
        return imsi;
    }

    public void setImsi(IMSI imsi) {
        this.imsi = imsi;
    }

    public LocationInfoWithLMSI getLocationInfoWithLMSI() {
        return locationInfoWithLMSI;
    }

    public void setLocationInfoWithLMSI(LocationInfoWithLMSI locationInfoWithLMSI) {
        this.locationInfoWithLMSI = locationInfoWithLMSI;
    }

    public IpSmGwGuidance getIpSmGwGuidance() {
        return ipSmGwGuidance;
    }

    public void setIpSmGwGuidance(IpSmGwGuidance ipSmGwGuidance) {
        this.ipSmGwGuidance = ipSmGwGuidance;
    }

    @Override
    public String toString() {
        return "SriSmResponseParams{" +
                "imsi=" + imsi +
                ", locationInfoWithLMSI=" + locationInfoWithLMSI +
                ", ipSmGwGuidance=" + ipSmGwGuidance +
                '}';
    }
}
