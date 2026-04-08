package org.mobicents.gmlc.slee;

import net.java.slee.resource.diameter.slg.events.avp.LCSClientType;
import net.java.slee.resource.diameter.slg.events.avp.LCSFormatIndicator;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLocationSupportIndicator;
import net.java.slee.resource.diameter.slg.events.avp.PrioritizedListIndicator;
import net.java.slee.resource.diameter.slg.events.avp.SLgLocationType;
import net.java.slee.resource.diameter.slg.events.avp.VelocityRequested;
import net.java.slee.resource.diameter.slg.events.avp.VerticalRequested;
import org.mobicents.gmlc.GmlcPropertiesManagement;
import org.mobicents.gmlc.slee.http.MongoGmlc;
import org.mobicents.gmlc.slee.mlp.MLPLocationRequest;
import org.mobicents.gmlc.slee.supl.SuplAreaEventType;
import org.mobicents.gmlc.slee.supl.SuplGeoTargetArea;
import org.mobicents.gmlc.slee.supl.SuplTriggerType;
import org.mobicents.gmlc.slee.utils.GADShapesUtils;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientInternalID;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationEstimateType;
import org.restcomm.protocols.ss7.map.api.service.lsm.OccurrenceInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;

import javax.servlet.http.HttpServletRequest;

import static org.mobicents.gmlc.slee.diameter.slg.SLgAreaEventInfoHelper.convertAreaTypeToString;
import static org.mobicents.gmlc.slee.utils.TBCDUtil.setAreaIdTbcd;

/**
 * Helper class to handle HTTP Request values and populate MAP and Diameter location request parameters
 *
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class HttpServletRequestParams {

    private static final GmlcPropertiesManagement gmlcPropertiesManagement = GmlcPropertiesManagement.getInstance();

    public HttpServletRequestParams() {
    }

    public LocationRequestParams createGlobalLocationRequestParamsFromHttpRequest(HttpServletRequest httpServletRequest, LocationRequestParams locationRequestParams) throws IllegalArgumentException {

        String numberFormatException, requestUriOperation;

        locationRequestParams.httpRespType = httpServletRequest.getParameter("httpRespType");
        if (locationRequestParams.httpRespType != null && locationRequestParams.httpRespType.equalsIgnoreCase("json")) {
            locationRequestParams.httpRespType = "json";
        }

        String requestURI = httpServletRequest.getRequestURI(); // (e.g., /restcomm/gmlc/rest/plr)
        if (requestURI != null && !requestURI.isEmpty()) {
            String[] pathSegments = requestURI.split("/");
            if (pathSegments.length > 3) {
                requestUriOperation = pathSegments[4]; // (e.g., plr)
                locationRequestParams.operation = requestUriOperation;
            }
        }
        if (locationRequestParams.operation == null) {
            throw new IllegalArgumentException("Invalid operation parameter, can not be null");
        } else if (!locationRequestParams.operation.equalsIgnoreCase("ATI")
            && !locationRequestParams.operation.equalsIgnoreCase("PSI")
            && !locationRequestParams.operation.equalsIgnoreCase("PSL")
            && !locationRequestParams.operation.equalsIgnoreCase("PLR")
            && !locationRequestParams.operation.equalsIgnoreCase("UDR")
            && !locationRequestParams.operation.equalsIgnoreCase("SRI")
            && !locationRequestParams.operation.equalsIgnoreCase("SRISM")
            && !locationRequestParams.operation.equalsIgnoreCase("SRILCS")) {
            throw new IllegalArgumentException("Invalid operation parameter, must be one of ATI, PSI, PSL, SRI, SRISM or SRILCS for SS7 networks, " +
                "PLR for LTE location services or UDR for the IMS");
        }

        locationRequestParams.curlToken = httpServletRequest.getParameter("token");
        if (locationRequestParams.curlToken == null) {
            throw new IllegalArgumentException("Invalid token parameter, can not be null");
        } else if (locationRequestParams.curlToken.equals(gmlcPropertiesManagement.getCurlToken())) {
            locationRequestParams.curlUser = gmlcPropertiesManagement.getCurlUser();
        } else {
            // if token does not equal the one defined in gmlcPropertiesManagement configuration, then query MongoDB
            try {
                MongoGmlc mongoGmlc = new MongoGmlc(gmlcPropertiesManagement.getMongoHost(), gmlcPropertiesManagement.getMongoPort(), gmlcPropertiesManagement.getMongoDatabase());
                String user = mongoGmlc.queryCurlUser(locationRequestParams.curlToken);
                mongoGmlc.closeMongo();
                if (user != null) {
                    locationRequestParams.curlUser = user;
                } else {
                    throw new IllegalArgumentException("Authentication error, invalid token");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Authentication error, invalid token");
            }
        }

        // Translation Type of SS7 SCCP Called Party Address (used for routing purposes in certain cases such as ported subscribers)
        if (httpServletRequest.getParameter("tt") != null) {
            try {
                locationRequestParams.translationType = Integer.parseInt(httpServletRequest.getParameter("tt"));
                if (locationRequestParams.translationType < 0 || locationRequestParams.translationType > 255) {
                    throw new IllegalArgumentException("Incorrect tt argument, must be a positive integer number between 0 and 255");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect tt argument, must be a positive integer number between 0 and 255";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        return locationRequestParams;
    }


    /*********************************************************************/
    /*** SS7 MAP AnyTimeInterrogation and ProvideSubscriberInformation ***/
    /*********************************************************************/
    public LocationRequestParams createLocationRequestParamsForMapAtiOrPsi(HttpServletRequest httpServletRequest, LocationRequestParams locationRequestParams)
        throws IllegalArgumentException {

        String numberFormatException;

        if (locationRequestParams.getOperation().equalsIgnoreCase("ATI")) {
            if (locationRequestParams.targetingMSISDN == null && locationRequestParams.targetingIMSI == null) {
                throw new IllegalArgumentException("One of MSISDN or IMSI is mandatory in MAP ATI, parameters msisdn and imsi can not be both null)");
            }
        }

        locationRequestParams.domainType = httpServletRequest.getParameter("domain");
        if (locationRequestParams.domainType == null) {
            if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                locationRequestParams.domainType = "ps";
            else
                locationRequestParams.domainType = "cs";
        } else if (!locationRequestParams.domainType.equalsIgnoreCase("CS")
            && !locationRequestParams.domainType.equalsIgnoreCase("PS")) {
            throw new IllegalArgumentException("Incorrect domain argument, must be one of CS or PS");
        }

        locationRequestParams.locationInfoEps = httpServletRequest.getParameter("locationInfoEps");
        if (locationRequestParams.locationInfoEps == null) {
            locationRequestParams.locationInfoEps = "true";
        } else if (!locationRequestParams.locationInfoEps.equals("true") && !locationRequestParams.locationInfoEps.equals("false")) {
            throw new IllegalArgumentException("Incorrect locationInfoEps argument, must be one of true or false");
        }

        locationRequestParams.activeLocation = httpServletRequest.getParameter("activeLocation");
        if (locationRequestParams.activeLocation == null) {
            locationRequestParams.activeLocation = "true";
        } else if (!locationRequestParams.activeLocation.equals("true") && !locationRequestParams.activeLocation.equals("false")) {
            throw new IllegalArgumentException("Incorrect activeLocation argument, must be one of true or false");
        }

        locationRequestParams.locationInfo5gs = httpServletRequest.getParameter("locationInfo5gs");
        if (locationRequestParams.locationInfo5gs == null) {
            locationRequestParams.locationInfo5gs = "false";
        } else if (!locationRequestParams.locationInfo5gs.equals("true") && !locationRequestParams.locationInfo5gs.equals("false")) {
            throw new IllegalArgumentException("Incorrect locationInfo5gs argument, must be one of true or false");
        }

        locationRequestParams.ratTypeRequested = httpServletRequest.getParameter("ratTypeRequested");
        if (locationRequestParams.ratTypeRequested == null) {
            locationRequestParams.ratTypeRequested = "false";
        } else if (!locationRequestParams.ratTypeRequested.equals("true") && !locationRequestParams.ratTypeRequested.equals("false")) {
            throw new IllegalArgumentException("Incorrect ratTypeRequested argument, must be one of true or false");
        }

        locationRequestParams.atiExtraInfoRequested = httpServletRequest.getParameter("extraRequestedInfo");
        if (locationRequestParams.atiExtraInfoRequested == null) {
            locationRequestParams.atiExtraInfoRequested = "true";
        } else if (!locationRequestParams.atiExtraInfoRequested.equals("true") && !locationRequestParams.atiExtraInfoRequested.equals("false")) {
            throw new IllegalArgumentException("Incorrect locationInfoEps argument, must be one of true or false");
        }

        locationRequestParams.psiServiceType = httpServletRequest.getParameter("psiServiceType");
        if (locationRequestParams.psiServiceType == null) {
            locationRequestParams.psiServiceType = "useSriSm";
        } else if (!locationRequestParams.operation.equalsIgnoreCase("PSI")) {
            throw new IllegalArgumentException("Incorrect use of psiServiceType argument, it only applies for PSI operation");
        } else if (!locationRequestParams.psiServiceType.equalsIgnoreCase("psiFirst") &&
            !locationRequestParams.psiServiceType.equalsIgnoreCase("useSri") &&
            !locationRequestParams.psiServiceType.equalsIgnoreCase("useSriSm")) {
            throw new IllegalArgumentException("Incorrect psiServiceType argument, must be one of useSri, useSriSm or psiFirst");
        }

        if (httpServletRequest.getParameter("psiImsi") != null) {
            if (!locationRequestParams.operation.equalsIgnoreCase("PSI")) {
                throw new IllegalArgumentException("Incorrect use of psiImsi argument, it only applies for PSI operation");
            }
            try {
                locationRequestParams.psiOnlyImsi = httpServletRequest.getParameter("psiImsi");
                long imsiValue = Long.parseLong(locationRequestParams.psiOnlyImsi);
                if (locationRequestParams.psiOnlyImsi.length() > 15 || imsiValue <= 0) {
                    throw new IllegalArgumentException("Incorrect psiImsi argument entered (" + imsiValue + "), must be valid IMSI value according to ITU-T E.212, " +
                        "a number with an amount of digits not greater than 15");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect psiImsi argument, must be valid IMSI value according to ITU-T E.212, " +
                    "a number with an amount of digits not greater than 15";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        if (httpServletRequest.getParameter("psiNnn") != null) {
            if (!locationRequestParams.operation.equalsIgnoreCase("PSI")) {
                throw new IllegalArgumentException("Incorrect use of psiNnn argument, it only applies for PSI operation");
            }
            try {
                locationRequestParams.psiOnlyNnn = httpServletRequest.getParameter("psiNnn");
                long nnnDigits = Long.parseLong(locationRequestParams.psiOnlyNnn);
                if (nnnDigits <= 0) {
                    throw new IllegalArgumentException("Incorrect psiNnn argument entered (" + nnnDigits + "), must be valid numeric value according to ITU-T E.214");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect psiNnn argument, must be valid numeric value according to ITU-T E.214";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        if (locationRequestParams.psiServiceType.equalsIgnoreCase("psiFirst")) {
            if (locationRequestParams.psiOnlyImsi == null || locationRequestParams.psiOnlyNnn == null)
                locationRequestParams.psiServiceType = "useSriSm";
        }

        return locationRequestParams;
    }


    /***************************************************************************/
    /*** SS7 MAP SendRoutingInfoForLCS, ProvideSubscriberLocation parameters ***/
    /***************************************************************************/
    public LocationRequestParams createLocationRequestParamsForMapPsl(HttpServletRequest httpServletRequest, LocationRequestParams locationRequestParams)
        throws IllegalArgumentException {

        String numberFormatException;

        if (locationRequestParams.targetingMSISDN == null && locationRequestParams.targetingIMSI == null) {
            throw new IllegalArgumentException("One of MSISDN or IMSI is mandatory in SRILCS/PSL, parameters msisdn and imsi can not be both null)");
        }

        /**** Location Type mandatory parameter for PSL ****/
        // Location Estimate Type
        locationRequestParams.pslLocationEstimateType = httpServletRequest.getParameter("lcsLocationType");
        if (locationRequestParams.pslLocationEstimateType != null) {
            if (!locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.currentOrLastKnownLocation)) &&
                !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.initialLocation)) &&
                !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.currentLocation)) &&
                !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) &&
                !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation)) &&
                !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.notificationVerificationOnly))) {
                throw new IllegalArgumentException("Incorrect lcsLocationType argument for PSL, must be one of currentLocation, currentOrLastKnownLocation, " +
                    "initialLocation, activateDeferredLocation, cancelDeferredLocation or notificationVerificationOnly");
            }
        } else {
            throw new IllegalArgumentException("lcsLocationType argument cannot be null for PSL, must be one of currentLocation, currentOrLastKnownLocation, " +
                "initialLocation, activateDeferredLocation or cancelDeferredLocation");
        }
        // Deferred Location Event Type
        locationRequestParams.pslDeferredLocationEventType = httpServletRequest.getParameter("lcsDeferredLocationType");
        if (locationRequestParams.pslDeferredLocationEventType != null) {
            if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("available") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("periodicLDR")) {
                    throw new IllegalArgumentException("Incorrect lcsDeferredLocationType argument for PSL, must be one of available, inside, entering, or " +
                        "leaving (Area Event Info) or periodicLDR");
                }
            } else {
                throw new IllegalArgumentException("lcsDeferredLocationType argument is conditional to lcsLocationType being equal to " +
                    "activateDeferredLocation or cancelDeferredLocation for PSL");
            }
        } else {
            if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                throw new IllegalArgumentException("lcsDeferredLocationType argument cannot be null if lcsLocationType equals " +
                    "activateDeferredLocation or cancelDeferredLocation");
            }
        }


        /*** LCS Client ID mandatory parameter for PSL ***/
        // LCS Client Type
        if (httpServletRequest.getParameter("lcsClientType") != null) {
            try {
                locationRequestParams.pslLcsClientType = Integer.valueOf(httpServletRequest.getParameter("lcsClientType"));
                if (locationRequestParams.pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.emergencyServices.getType() &&
                    locationRequestParams.pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.valueAddedServices.getType() &&
                    locationRequestParams.pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.plmnOperatorServices.getType() &&
                    locationRequestParams.pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.lawfulInterceptServices.getType()) {
                    throw new IllegalArgumentException("Incorrect lcsClientType argument, must be one of 0 (emergency services), 1 (value-added services), " +
                        "2 (PLMN operator services) or 3 (lawful interception)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsClientType argument, must be one of 0 (emergency services), 1 (value-added services), " +
                    "2 (PLMN operator services) or 3 (lawful interception)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }

            if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.emergencyServices.getType()) {
                // lcsClientType = emergencyServices :
                // lcsClientExternalID (O), lcsClientInternalID (NA), lcsClientName (NA), lcsClientDialedByMS (NA), lcsRequestorID (NA)
                if (httpServletRequest.getParameter("lcsClientExternalID") != null) {
                    try {
                        if (httpServletRequest.getParameter("lcsClientExternalID").length() > 16) {
                            throw new IllegalArgumentException("lcsClientExternalID argument represents an ISDN address whose length must not exceed 16 digits");
                        } else {
                            locationRequestParams.pslClientExternalID = httpServletRequest.getParameter("lcsClientExternalID");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "lcsClientExternalID argument represents an ISDN address whose length must not exceed 16 digits";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("lcsClientExternalID argument cannot be null when lcsClientType is 0 (emergency services)");
                }
                // LCSClientExternalID lcsClientExternalID = new LCSClientExternalIDImpl(final ISDNAddressString externalAddress, final MAPExtensionContainer extensionContainer)

            } else if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.valueAddedServices.getType()) {
                // lcsClientType = valueAddedServices :
                // lcsClientExternalID (M), lcsClientInternalID (NA), lcsClientName (M), lcsClientDialedByMS (O*), lcsRequestorID (O)
                // lcsClientDialedByMS (O*) : This component shall be present if the MT-LR is associated to either CS call or PS session.
                // If the MT-LR is associated with the CS call, the number dialled by UE is used.
                // Otherwise, if the MT-LR is associated with the PS session, the APN-NI is used
                if (httpServletRequest.getParameter("lcsClientExternalID") != null) {
                    try {
                        if (httpServletRequest.getParameter("lcsClientExternalID").length() > 16) {
                            throw new IllegalArgumentException("lcsClientExternalID argument represents an ISDN address whose length must not exceed 16 digits");
                        } else {
                            locationRequestParams.pslClientExternalID = httpServletRequest.getParameter("lcsClientExternalID");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "lcsClientExternalID argument represents an ISDN address whose length must not exceed 16 digits";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("lcsClientExternalID argument cannot be null when lcsClientType is 1 (value-added services)");
                }
                if (httpServletRequest.getParameter("lcsClientName") != null) {
                    locationRequestParams.pslClientName = httpServletRequest.getParameter("lcsClientName");
                    if (httpServletRequest.getParameter("lcsClientFormatIndicator") != null) {
                        try {
                            locationRequestParams.pslClientFormatIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsClientFormatIndicator"));
                            if (locationRequestParams.pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.logicalName.getIndicator() &&
                                locationRequestParams.pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.emailAddress.getIndicator() &&
                                locationRequestParams.pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.msisdn.getIndicator() &&
                                locationRequestParams.pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.url.getIndicator() &&
                                locationRequestParams.pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.sipUrl.getIndicator()) {
                                throw new IllegalArgumentException("Incorrect lcsClientFormatIndicator argument, must be one of 0 (logicalName), 1 (emailAddress), " +
                                    "2 (MSISDN), 3 (URL) or 4 (SIP URL)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsClientFormatIndicator argument, must be one of 0 (logicalName), 1 (emailAddress),  " +
                                "2 (MSISDN), 3 (URL) or 4 (SIP URL)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        throw new IllegalArgumentException("lcsClientFormatIndicator argument value cannot be null when lcsClientName is provided");
                    }
                } else {
                    throw new IllegalArgumentException("lcsClientName argument cannot be null when lcsClientType is 1 (value-added services)");
                }
                if (httpServletRequest.getParameter("lcsRequestorId") != null) {
                    locationRequestParams.pslRequestorIdString = httpServletRequest.getParameter("lcsRequestorId");
                    if (httpServletRequest.getParameter("lcsRequestorFormatIndicator") != null) {
                        try {
                            locationRequestParams.pslRequestorFormatIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsRequestorFormatIndicator"));
                            if (locationRequestParams.pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.logicalName.getIndicator() &&
                                locationRequestParams.pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.emailAddress.getIndicator() &&
                                locationRequestParams.pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.msisdn.getIndicator() &&
                                locationRequestParams.pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.url.getIndicator() &&
                                locationRequestParams.pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.sipUrl.getIndicator()) {
                                throw new IllegalArgumentException("Incorrect lcsRequestorFormatIndicator argument, must be one of 0 (logicalName), 1 (emailAddress), " +
                                    "2 (MSISDN), 3 (URL) or 4 (SIP URL)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsRequestorFormatIndicator argument, must be one of 0 (logicalName), 1 (emailAddress),  " +
                                "2 (MSISDN), 3 (URL) or 4 (SIP URL)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        throw new IllegalArgumentException("lcsRequestorFormatIndicator argument value cannot be null when lcsRequestorId is provided");
                    }
                }
                // String lcsClientDialedByMS = httpServletRequest.getParameter("lcsClientDialedByMS");

            } else if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.plmnOperatorServices.getType()) {
                //lcsClientType = plmnOperatorServices :
                //lcsClientExternalID (NA), lcsClientInternalID (M), lcsClientName (NA), lcsClientDialedByMS (NA), lcsRequestorID (NA)
                if (httpServletRequest.getParameter("lcsClientInternalID") != null) {
                    try {
                        locationRequestParams.pslClientInternalID = Integer.valueOf(httpServletRequest.getParameter("lcsClientInternalID"));
                        if (locationRequestParams.pslClientInternalID != LCSClientInternalID.broadcastService.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.oandMHPLMN.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.oandMVPLMN.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.anonymousLocation.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.targetMSsubscribedService.getId()) {
                            throw new IllegalArgumentException("Incorrect lcsClientInternalID argument, must be one of 0 (broadcastService), 1 (oandMHPLMN), " +
                                "2 (oandMVPLMN), 3 (anonymousLocation) or 4 (targetMSsubscribedServiceSIP)");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "Incorrect lcsClientInternalID argument, must be one of 0 (broadcastService), 1 (oandMHPLMN), " +
                            "2 (oandMVPLMN), 3 (anonymousLocation) or 4 (targetMSsubscribedServiceSIP)";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("lcsClientInternalID argument cannot be null when lcsClientType is 2 (PLMN operator services), must be one of 0 (broadcastService), 1 (oandMHPLMN), " +
                        "2 (oandMVPLMN), 3 (anonymousLocation) or 4 (targetMSsubscribedServiceSIP)");
                }
            }
        } else {
            throw new IllegalArgumentException("lcsClientType argument cannot be null for PSL, must be one of 0 (emergency services), 1 (value-added services), " +
                "2 (PLMN operator services) or 3 (lawful interception)");
        }


        /*** clientReferenceNumber indirectly related to optional LCSReference-Number ***/
        if (httpServletRequest.getParameter("clientReferenceNumber") != null) {
            try {
                locationRequestParams.pslLcsReferenceNumber = Integer.parseInt(httpServletRequest.getParameter("clientReferenceNumber"));
                if (locationRequestParams.pslLcsReferenceNumber < 0) {
                    throw new IllegalArgumentException("clientReferenceNumber argument must be a valid integer value");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect clientReferenceNumber argument, must be an integer number";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            throw new IllegalArgumentException("clientReferenceNumber argument must not be null");
        }

        /*** LCS Service Type ID optional parameter for PSL ***/
        if (httpServletRequest.getParameter("lcsServiceTypeId") != null) {
            try {
                locationRequestParams.pslLcsServiceTypeID = Integer.valueOf(httpServletRequest.getParameter("lcsServiceTypeId"));
                if (locationRequestParams.pslLcsServiceTypeID > 127 || locationRequestParams.pslLcsServiceTypeID < 0) {
                    throw new IllegalArgumentException("Incorrect lcsServiceTypeId argument, must be a positive integer value equal or higher than 0 and lower than 128");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsServiceTypeId argument, must be an integer value equal or higher than 0 and lower than 128";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        /**** LCS QoS parameters for PSL ****/
        // Horizontal Accuracy
        if (httpServletRequest.getParameter("horizontalAccuracy") != null) {
            try {
                int horizontalAccuracy = Integer.parseInt(httpServletRequest.getParameter("horizontalAccuracy"));
                if (horizontalAccuracy < 0) {
                    throw new IllegalArgumentException("Incorrect horizontalAccuracy argument, must be a positive number " +
                        "corresponding to the desired horizontal accuracy in metres");
                } else {
                    locationRequestParams.pslLcsHorizontalAccuracy = GADShapesUtils.encodeUncertainty(horizontalAccuracy);
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect horizontalAccuracy argument, must be a positive number corresponding " +
                    "to the desired horizontal accuracy in metres";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }
        // Vertical Accuracy
        if (httpServletRequest.getParameter("verticalAccuracy") != null) {
            try {
                int verticalAccuracy = Integer.parseInt(httpServletRequest.getParameter("verticalAccuracy"));
                if (verticalAccuracy < 0) {
                    throw new IllegalArgumentException("Incorrect verticalAccuracy argument, must be a positive number " +
                        "corresponding to the desired vertical accuracy in metres");
                } else {
                    locationRequestParams.pslLcsVerticalAccuracy = GADShapesUtils.encodeUncertainty(verticalAccuracy);
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect verticalAccuracy argument, must be a positive number corresponding " +
                    "to the desired vertical accuracy in metres";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }
        // Vertical Coordinate Request
        if (httpServletRequest.getParameter("verticalCoordinateRequest") != null) {
            locationRequestParams.pslVerticalCoordinateRequest = httpServletRequest.getParameter("verticalCoordinateRequest");
            if (!locationRequestParams.pslVerticalCoordinateRequest.equalsIgnoreCase("true")
                && !locationRequestParams.pslVerticalCoordinateRequest.equalsIgnoreCase("false")) {
                throw new IllegalArgumentException("Incorrect verticalCoordinateRequest argument, must be true or false");
            }
        }
        // Response Time Category
        if (httpServletRequest.getParameter("responseTime") != null) {
            locationRequestParams.pslResponseTimeCategory = httpServletRequest.getParameter("responseTime");
            if (!locationRequestParams.pslResponseTimeCategory.equalsIgnoreCase(ResponseTimeCategory.delaytolerant.name())
                && !locationRequestParams.pslResponseTimeCategory.equalsIgnoreCase(ResponseTimeCategory.lowdelay.name())) {
                throw new IllegalArgumentException("Incorrect responseTime argument, must be delaytolerant or lowdelay");
            }
        }
        // Velocity Request
        if (httpServletRequest.getParameter("velocityRequested") != null) {
            String pslVelocityRequestedStr = httpServletRequest.getParameter("velocityRequested");
            if (!pslVelocityRequestedStr.equals("true") && !pslVelocityRequestedStr.equals("false")) {
                throw new IllegalArgumentException("Incorrect velocityRequested argument, must be one of true or false");
            } else {
                locationRequestParams.pslVelocityRequest = Boolean.valueOf(httpServletRequest.getParameter("velocityRequested"));
            }
        }
        //
        if (httpServletRequest.getParameter("pslQosClass") != null) {
            try {
                locationRequestParams.pslQoSClass = Integer.valueOf(httpServletRequest.getParameter("pslQosClass"));
                if (locationRequestParams.pslQoSClass != org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass.bestEffort.getCode() &&
                        locationRequestParams.pslQoSClass != org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass.assured.getCode()) {
                    throw new IllegalArgumentException("Incorrect lcsQoSClass argument, must be one of 0 (bestEffort) or 1 (assured)");
                } else {
                    if (locationRequestParams.pslQoSClass == 0)
                        locationRequestParams.pslQoSClass = org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass.bestEffort.getCode();
                    else if (locationRequestParams.pslQoSClass == 1)
                        locationRequestParams.pslQoSClass = org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass.assured.getCode();
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsQoSClass argument, must be one of 0 (bestEffort) or 1 (assured)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }


        /**** Area Event Info parameters for PSL ****/
        // Area Type
        locationRequestParams.pslAreaType = httpServletRequest.getParameter("lcsAreaType");
        if (locationRequestParams.pslAreaType != null) {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving")) {
                    throw new IllegalArgumentException("lcsAreaType argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                        "entering or leaving");
                }
            } else {
                throw new IllegalArgumentException("lcsAreaType argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                    "entering or leaving or null)");
            }
            if (!locationRequestParams.pslAreaType.equalsIgnoreCase("countryCode") &&
                !locationRequestParams.pslAreaType.equalsIgnoreCase("plmnId") &&
                !locationRequestParams.pslAreaType.equalsIgnoreCase("locationAreaId") &&
                !locationRequestParams.pslAreaType.equalsIgnoreCase("routingAreaId") &&
                !locationRequestParams.pslAreaType.equalsIgnoreCase("cellGlobalId") &&
                !locationRequestParams.pslAreaType.equalsIgnoreCase("utranCellId")) {
                //AreaType ::= ENUMERATED { countryCode (0), plmnId (1), locationAreaId (2), routingAreaId (3), cellGlobalId (4), utranCellId (5) }
                throw new IllegalArgumentException("Incorrect lcsAreaType argument, must be one of countryCode, plmnId, routingAreaId, locationAreaId, cellGlobalId or utranCellId");
            }
        } else {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                    locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                    if (locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") ||
                        locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") ||
                        locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving")) {
                        throw new IllegalArgumentException("lcsAreaType argument can not be null when lcsLocationType argument is provided " +
                            "for an area deferred location event type (inside, entering or leaving)");
                    }
                }
            }
        }
        // Area Id
        locationRequestParams.pslAreaId = httpServletRequest.getParameter("lcsAreaId");
        if (httpServletRequest.getParameter("lcsAreaId") != null) {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving")) {
                    throw new IllegalArgumentException("lcsAreaId argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                        "entering or leaving");
                }
            } else {
                throw new IllegalArgumentException("lcsAreaId argument is not allowed when lcsDeferredLocationType argument is not one inside, " +
                    "entering or leaving or null)");
            }
            if (locationRequestParams.pslAreaType == null) {
                throw new IllegalArgumentException("Incorrect input, lcsAreaId argument needs a valid lcsAreaType value");
            } else {
                String pslAreaIdTbcd = setAreaIdTbcd(locationRequestParams.pslAreaId.split("-"), locationRequestParams.pslAreaType);
                if (pslAreaIdTbcd.equalsIgnoreCase("Invalid")) {
                    throw new IllegalArgumentException("Invalid lcsAreaId argument");
                }
            }
        } else {
            if (locationRequestParams.pslAreaType != null) {
                throw new IllegalArgumentException("lcsAreaId argument can not be null when a valid lcsAreaType value is provided");
            }
        }
        // Occurrence Info
        // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
        locationRequestParams.pslOccurrenceInfo = httpServletRequest.getParameter("lcsAreaEventOccurrenceInfo");
        if (locationRequestParams.pslOccurrenceInfo != null) {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving")) {
                    throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                        "entering or leaving");
                }
            } else {
                throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                    "entering or leaving or null");
            }
            if (locationRequestParams.pslAreaType == null || locationRequestParams.pslAreaId == null) {
                throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument need both lcsAreaType and lcsAreaId arguments to be provided");
            } else if (!locationRequestParams.pslOccurrenceInfo.equalsIgnoreCase("oneTimeEvent") &&
                !locationRequestParams.pslOccurrenceInfo.equalsIgnoreCase("multipleTimeEvent")) {
                throw new IllegalArgumentException("Incorrect lcsAreaEventOccurrenceInfo argument, must be oneTimeEvent or multipleTimeEvent");
            }
        } else {
            if (locationRequestParams.pslAreaType != null && locationRequestParams.pslAreaId != null) {
                // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
                locationRequestParams.pslOccurrenceInfo = "oneTimeEvent";
            }
        }
        // IntervalTime (minimum interval time between area reports in seconds) is only applicable
        // when the OccurrenceInfo is set to "MULTIPLE_TIME_EVENT" (1).
        if (httpServletRequest.getParameter("lcsAreaEventIntervalTime") != null) {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") &&
                    !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving")) {
                    throw new IllegalArgumentException("lcsAreaEventIntervalTime argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                        "entering or leaving");
                }
            } else {
                throw new IllegalArgumentException("lcsAreaEventIntervalTime argument is not allowed when lcsDeferredLocationType argument is not one of inside, " +
                    "entering or leaving or null");
            }
            if (locationRequestParams.pslOccurrenceInfo == null) {
                throw new IllegalArgumentException("lcsAreaEventIntervalTime argument needs a valid lcsAreaEventOccurrenceInfo value");
            } else if (!locationRequestParams.pslOccurrenceInfo.equalsIgnoreCase("multipleTimeEvent")) {
                throw new IllegalArgumentException("lcsAreaEventIntervalTime argument is only applicable when lcsAreaEventOccurrenceInfo " +
                    "is set to multipleTimeEvent");
            }
            try {
                locationRequestParams.pslIntervalTime = Integer.valueOf(httpServletRequest.getParameter("lcsAreaEventIntervalTime"));
                if (locationRequestParams.pslIntervalTime > 32767 || locationRequestParams.pslIntervalTime < 0) {
                    throw new IllegalArgumentException("Incorrect lcsAreaEventIntervalTime argument, must be a positive integer value lower than 32767 (seconds)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsAreaEventIntervalTime argument, must be a positive integer value lower than 32767 (seconds)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            if (locationRequestParams.pslOccurrenceInfo != null) {
                // IntervalTime is only applicable when the OccurrenceInfo is set to "MULTIPLE_TIME_EVENT" (1).
                if (locationRequestParams.pslOccurrenceInfo.equalsIgnoreCase("multipleTimeEvent")) {
                    // If not included, the default value of Interval-Time shall be considered as one
                    locationRequestParams.pslIntervalTime = 1;
                }
            }
        }

        /**** Periodic LDR Info parameters for PSL ****/
        // Reporting Amount
        if (httpServletRequest.getParameter("lcsPeriodicReportingAmount") != null) {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("periodicLDR")) {
                    throw new IllegalArgumentException("lcsPeriodicReportingAmount argument is not allowed when lcsDeferredLocationType argument is not " +
                        "periodicLDR");
                }
            } else {
                throw new IllegalArgumentException("lcsPeriodicReportingAmount argument is not allowed when lcsDeferredLocationType argument is null");
            }
            try {
                locationRequestParams.pslReportingAmount = Integer.valueOf(httpServletRequest.getParameter("lcsPeriodicReportingAmount"));
                if (locationRequestParams.pslReportingAmount > 8639999 || locationRequestParams.pslReportingAmount < 0) {
                    throw new IllegalArgumentException("Incorrect lcsPeriodicReportingAmount argument, must be a positive integer value indicating the reporting frequency " +
                        "between 1 than 8639999");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPeriodicReportingInterval parameter, must be a positive integer value indicating the reporting frequency " +
                    "between 1 than 8639999";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                    locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                    if (locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("periodicLDR")) {
                        throw new IllegalArgumentException("lcsPeriodicReportingAmount argument can not be null when lcsLocationType argument is provided " +
                            "for a periodicLDR deferred location event type");
                    }
                }
            }
        }
        // Reporting Interval
        if (httpServletRequest.getParameter("lcsPeriodicReportingInterval") != null) {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("periodicLDR")) {
                    throw new IllegalArgumentException("lcsPeriodicReportingInterval argument is not allowed when lcsDeferredLocationType argument is not " +
                        "periodicLDR");
                }
            } else {
                throw new IllegalArgumentException("lcsPeriodicReportingInterval argument is not allowed when lcsDeferredLocationType argument is null");
            }
            if (locationRequestParams.pslReportingAmount == null) {
                throw new IllegalArgumentException("lcsPeriodicReportingInterval argument needs a valid lcsPeriodicReportingAmount argument value");
            }
            try {
                locationRequestParams.pslReportingInterval = Integer.valueOf(httpServletRequest.getParameter("lcsPeriodicReportingInterval"));
                if (locationRequestParams.pslReportingInterval > 8639999 || locationRequestParams.pslReportingInterval < 0) {
                    throw new IllegalArgumentException("Incorrect lcsPeriodicReportingInterval parameter, must be a positive integer value indicating the reporting interval " +
                        "between 1 than 8639999 (seconds)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPeriodicReportingInterval argument, must be a positive integer value";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            if (locationRequestParams.pslDeferredLocationEventType != null) {
                if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                    locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                    if (locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("periodicLDR")) {
                        throw new IllegalArgumentException("lcsPeriodicReportingInterval argument can not be null when lcsLocationType argument is provided " +
                            "for a periodicLDR deferred location event type");
                    }
                }
            }
        }


        /*** Reporting PLMN List for PSL ***/
        // 3GPP TS 29.002 v15.5.0
        // Reporting PLMN List
        // This parameter indicates a list of PLMNs in which subsequent periodic MO-LR TTTP requests will be made.
        // ReportingPLMNList::= SEQUENCE { plmn-ListPrioritized [0] NULL OPTIONAL, plmn-List [1] PLMNList, ...}
        // PLMNList
        if (httpServletRequest.getParameter("lcsVisitedPLMNId") != null) {
            locationRequestParams.pslPLMNIdList = httpServletRequest.getParameter("lcsVisitedPLMNId");
            String plrVisitedPLMNIdTbcd = setAreaIdTbcd(locationRequestParams.pslPLMNIdList.split("-"), "plmnId");
            if (plrVisitedPLMNIdTbcd.equalsIgnoreCase("Invalid")) {
                throw new IllegalArgumentException("Invalid lcsVisitedPLMNId argument");
            }
        }
        // renTechnology (param of ReportingPLMNImpl)
        if (httpServletRequest.getParameter("lcsVisitedPLMNIdRAN") != null) {
            if (locationRequestParams.pslPLMNIdList == null) {
                throw new IllegalArgumentException("lcsVisitedPLMNIdRAN argument does not apply when lcsVisitedPLMNId argument is not provided");
            }
            try {
                locationRequestParams.pslVisitedPLMNIdRAN = Integer.valueOf(httpServletRequest.getParameter("lcsVisitedPLMNIdRAN"));
                if (locationRequestParams.pslVisitedPLMNIdRAN != 0 && locationRequestParams.pslVisitedPLMNIdRAN != 1) {
                    throw new IllegalArgumentException("Incorrect lcsVisitedPLMNIdRAN argument, must be 0 (GSM) or 1 (UMTS)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsVisitedPLMNIdRAN argument, must be 0 (GSM) or 1 (UMTS)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            if (locationRequestParams.pslPLMNIdList != null) {
                throw new IllegalArgumentException("lcsVisitedPLMNIdRAN argument can not be null when a valid lcsVisitedPLMNId argument is provided");
            }
        }
        // ranPeriodicLocationSupport (param of ReportingPLMNImpl)
        if (httpServletRequest.getParameter("lcsPeriodicLocationSupportIndicator") != null) {
            if (locationRequestParams.pslPLMNIdList == null) {
                throw new IllegalArgumentException("lcsPeriodicLocationSupportIndicator argument does not apply when lcsVisitedPLMNId argument is not provided");
            }
            try {
                locationRequestParams.pslPeriodicLocationSupportIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsPeriodicLocationSupportIndicator"));
                if (locationRequestParams.pslPeriodicLocationSupportIndicator != 0 &&
                    locationRequestParams.pslPeriodicLocationSupportIndicator != 1) {
                    throw new IllegalArgumentException("Incorrect lcsPeriodicLocationSupportIndicator argument, must be 0 (NOT_SUPPORTED) or 1 (SUPPORTED)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPeriodicLocationSupportIndicator argument, must be 0 (NOT_SUPPORTED) or 1 (SUPPORTED)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            if (locationRequestParams.pslPLMNIdList != null) {
                throw new IllegalArgumentException("lcsPeriodicLocationSupportIndicator argument can not be null when a valid lcsVisitedPLMNId argument is provided");
            }
        }
        // plmn-ListPrioritized
        if (httpServletRequest.getParameter("lcsPrioritizedListIndicator") != null) {
            if (locationRequestParams.pslPLMNIdList == null) {
                throw new IllegalArgumentException("lcsPrioritizedListIndicator argument does not apply when lcsVisitedPLMNId argument is not provided");
            }
            try {
                locationRequestParams.pslPrioritizedListIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsPrioritizedListIndicator"));
                if (locationRequestParams.pslPrioritizedListIndicator != 0 && locationRequestParams.pslPrioritizedListIndicator != 1) {
                    throw new IllegalArgumentException("Incorrect lcsPrioritizedListIndicator argument, must be 0 (NOT_PRIORITIZED) or 1 (PRIORITIZED)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPrioritizedListIndicator argument must be 0 (NOT_PRIORITIZED) or 1 (PRIORITIZED)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        /**** Other non-mandatory parameters for PSL ****/

        /*** IMEI for PSL ***/
        if (httpServletRequest.getParameter("imei") != null) {
            locationRequestParams.targetingIMEI = httpServletRequest.getParameter("imei");
            if (locationRequestParams.targetingIMEI.length() > 15 || locationRequestParams.targetingIMEI.length() < 14) {
                throw new IllegalArgumentException("Incorrect imei length");
            }
        }

        /*** LCS Priority for PSL ***/
        if (httpServletRequest.getParameter("lcsPriority") != null) {
            locationRequestParams.pslLcsPriority = httpServletRequest.getParameter("lcsPriority");
            if (!locationRequestParams.pslLcsPriority.equalsIgnoreCase("normalPriority")
                && !locationRequestParams.pslLcsPriority.equalsIgnoreCase("highestPriority")) {
                throw new IllegalArgumentException("Incorrect lcsPriority argument, must be normalPriority or highestPriority");
            }
        }

        /*** LCS Codeword for PSL ***/
        if (httpServletRequest.getParameter("lcsCodeword") != null) {
            locationRequestParams.pslLcsCodeword = httpServletRequest.getParameter("lcsCodeword");
        }

        if (httpServletRequest.getParameter("lcsCallbackUrl") != null) {
            locationRequestParams.slrCallbackUrl = httpServletRequest.getParameter("lcsCallbackUrl");
        } else {
            locationRequestParams.slrCallbackUrl = GmlcPropertiesManagement.getInstance().getLcsUrlCallback();
        }

        return locationRequestParams;
    }


    /*****************************************************************************************/
    /*** Diameter SLh-SLg Routing-Information-Request, Provide-Location-Request parameters ***/
    /*****************************************************************************************/
    public LocationRequestParams createLocationRequestParamsForDiameterPLR(HttpServletRequest httpServletRequest, LocationRequestParams locationRequestParams)
        throws IllegalArgumentException {

        String numberFormatException;

        /**** Mandatory parameters for SLh/SLg RIR/PLR ****/

        if (locationRequestParams.targetingMSISDN == null && locationRequestParams.targetingIMSI == null) {
            throw new IllegalArgumentException("One of MSISDN or User-Name AVPs is mandatory in PLR, parameters msisdn and imsi can not be both null)");
        }

        /*** Diameter Destination Host and Realm where to send SLh RIR and Sh UDR ***/
        if (httpServletRequest.getParameter("hssDiameterHost") != null) {
            locationRequestParams.hssDiameterHost = httpServletRequest.getParameter("hssDiameterHost");
        }
        if (httpServletRequest.getParameter("hssDiameterRealm") != null) {
            locationRequestParams.hssDiameterRealm = httpServletRequest.getParameter("hssDiameterRealm");
        }

        /*** PLR mandatory AVP: { SLg-Location-Type } ***/
        // The SLg-Location-Type AVP is of type Enumerated.
        // The following values are defined: CURRENT_LOCATION (0), CURRENT_OR_LAST_KNOWN_LOCATION (1),
        // INITIAL_LOCATION (2), ACTIVATE_DEFERRED_LOCATION (3), CANCEL_DEFERRED_LOCATION (4), NOTIFICATION_VERIFICATION_ONLY (5)
        if (httpServletRequest.getParameter("lcsLocationType") != null) {
            try {
                locationRequestParams.plrSlgLocationType = Integer.valueOf(httpServletRequest.getParameter("lcsLocationType"));
                if (locationRequestParams.plrSlgLocationType != SLgLocationType._CURRENT_LOCATION &&
                    locationRequestParams.plrSlgLocationType != SLgLocationType._CURRENT_OR_LAST_KNOWN_LOCATION &&
                    locationRequestParams.plrSlgLocationType != SLgLocationType._INITIAL_LOCATION &&
                    locationRequestParams.plrSlgLocationType != SLgLocationType._ACTIVATE_DEFERRED_LOCATION &&
                    locationRequestParams.plrSlgLocationType != SLgLocationType._CANCEL_DEFERRED_LOCATION &&
                    locationRequestParams.plrSlgLocationType != SLgLocationType._NOTIFICATION_VERIFICATION_ONLY) {
                    throw new IllegalArgumentException("SLg-Location-Type AVP is mandatory in PLR, " +
                        "lcsLocationType argument must be one of 0 (CURRENT_LOCATION), 1 (CURRENT_OR_LAST_KNOWN_LOCATION), " +
                        "2 (INITIAL_LOCATION), 3 (ACTIVATE_DEFERRED_LOCATION), 4 (CANCEL_DEFERRED_LOCATION) or 5 (NOTIFICATION_VERIFICATION_ONLY)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "SLg-Location-Type AVP is mandatory in PLR, " +
                    "lcsLocationType argument must be one of 0 (CURRENT_LOCATION), 1 (CURRENT_OR_LAST_KNOWN_LOCATION), " +
                    "2 (INITIAL_LOCATION), 3 (ACTIVATE_DEFERRED_LOCATION), 4 (CANCEL_DEFERRED_LOCATION) or 5 (NOTIFICATION_VERIFICATION_ONLY)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            throw new IllegalArgumentException("SLg-Location-Type AVP is mandatory in PLR, " +
                "lcsLocationType argument must be one of 0 (CURRENT_LOCATION), 1 (CURRENT_OR_LAST_KNOWN_LOCATION), " +
                "2 (INITIAL_LOCATION), 3 (ACTIVATE_DEFERRED_LOCATION), 4 (CANCEL_DEFERRED_LOCATION) or 5 (NOTIFICATION_VERIFICATION_ONLY)");
        }

        /*** PLR mandatory AVP: { LCS-EPS-Client-Name } ***/
        // LCS-EPS-Client-Name ::= <AVP header: 2501 10415>
        //      [ LCS-Name-String ]
        //      [ LCS-Format-Indicator ]
        // [ LCS-Name-String ] AVP of mandatory grouped { LCS-EPS-Client-Name } AVP
        locationRequestParams.plrLcsNameString = httpServletRequest.getParameter("lcsClientName");
        if (locationRequestParams.plrLcsNameString == null) {
            throw new IllegalArgumentException("LCS-EPS-Client-Name grouped AVP is mandatory in PLR, lcsClientName cannot be null");
        }
        // [ LCS-Format-Indicator ] AVP of mandatory grouped { LCS-EPS-Client-Name } AVP
        // The LCS-Format-Indicator AVP is of type Enumerated
        // and contains the format of the LCS Client name.
        // It can be one of the following values: 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), 2 (MSISDN),
        // 3 URL, 4 SIP_URL
        if (httpServletRequest.getParameter("lcsClientFormatIndicator") != null) {
            try {
                locationRequestParams.plrLcsFormatInd = Integer.valueOf(httpServletRequest.getParameter("lcsClientFormatIndicator"));
                if (locationRequestParams.plrLcsFormatInd != LCSFormatIndicator._LOGICAL_NAME &&
                    locationRequestParams.plrLcsFormatInd != LCSFormatIndicator._EMAIL_ADDRESS &&
                    locationRequestParams.plrLcsFormatInd != LCSFormatIndicator._MSISDN &&
                    locationRequestParams.plrLcsFormatInd != LCSFormatIndicator._URL &&
                    locationRequestParams.plrLcsFormatInd != LCSFormatIndicator._SIP_URL) {
                    throw new IllegalArgumentException("LCS-EPS-Client-Name grouped AVP is mandatory in PLR, " +
                        "lcsClientFormatIndicator must be one of 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), 2 (MSISDN), 3 (URL) or 4 (SIP_URL)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "LCS-EPS-Client-Name AVP is mandatory in PLR, " +
                    "lcsClientFormatIndicator must be one of 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), 2 (MSISDN), 3 (URL) or 4 (SIP_URL)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            throw new IllegalArgumentException("LCS-EPS-Client-Name AVP is mandatory in PLR, " +
                "lcsClientFormatIndicator must be one of 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), 2 (MSISDN), 3 (URL) or 4 (SIP_URL)");
        }

        /*** PLR mandatory AVP: { LCS-Client-Type } ***/
        // The LCS-Client-Type AVP (AVP code 1241) is of type Enumerated and contains the type of services requested by the LCS Client.
        // It can be one of the following values: 0 EMERGENCY_SERVICES, 1 VALUE_ADDED_SERVICES, 2 PLMN_OPERATOR_SERVICES, 3 LAWFUL_INTERCEPT_SERVICES
        if (httpServletRequest.getParameter("lcsClientType") != null) {
            try {
                locationRequestParams.plrLcsClientType = Integer.valueOf(httpServletRequest.getParameter("lcsClientType"));
                if (locationRequestParams.plrLcsClientType != LCSClientType._EMERGENCY_SERVICES &&
                    locationRequestParams.plrLcsClientType != LCSClientType._VALUE_ADDED_SERVICES &&
                    locationRequestParams.plrLcsClientType != LCSClientType._PLMN_OPERATOR_SERVICES &&
                    locationRequestParams.plrLcsClientType != LCSClientType._LAWFUL_INTERCEPT_SERVICES) {
                    throw new IllegalArgumentException("lcsClientType argument must be one of 0 (EMERGENCY_SERVICES), 1 (VALUE_ADDED_SERVICES), " +
                        "2 (PLMN_OPERATOR_SERVICES) or 3 (LAWFUL_INTERCEPT_SERVICES)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "lcsClientType argument must be one of 0 (EMERGENCY_SERVICES), 1 (VALUE_ADDED_SERVICES), " +
                    "2 (PLMN_OPERATOR_SERVICES) or 3 (LAWFUL_INTERCEPT_SERVICES)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            throw new IllegalArgumentException("LCS-Client-Type AVP is mandatory in PLR, lcsClientType must be one of 0 (EMERGENCY_SERVICES), 1 (VALUE_ADDED_SERVICES), " +
                "2 (PLMN_OPERATOR_SERVICES) or 3 (LAWFUL_INTERCEPT_SERVICES)");
        }

        // clientReferenceNumber indirectly related to optional [ LCS-Reference-Number ] AVP
        if (httpServletRequest.getParameter("clientReferenceNumber") != null) {
            try {
                locationRequestParams.plrLcsReferenceNumber = Integer.valueOf(httpServletRequest.getParameter("clientReferenceNumber"));
                if (locationRequestParams.plrLcsReferenceNumber < 0) {
                    throw new IllegalArgumentException("clientReferenceNumber argument must be a valid integer value");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "clientReferenceNumber argument must be a valid integer value";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            throw new IllegalArgumentException("clientReferenceNumber argument can not be null, must be a valid integer value");
        }


        /*** PLR optional AVP: [ LCS-Requestor-Name ] ***/
        // LCS-Requestor-Name ::= <AVP header: 2502 10415>
        //    [ LCS-Requestor-Id-String ]
        //    [ LCS-Format-Indicator ]
        // [ LCS-Requestor-Id-String ] AVP of optional grouped [ LCS-Requestor-Name ] AVP
        locationRequestParams.plrLcsRequestorIdString = httpServletRequest.getParameter("lcsRequestorId");
        // [ LCS-Format-Indicator ] AVP of optional grouped [ LCS-Requestor-Name ] AVP
        if (httpServletRequest.getParameter("lcsRequestorFormatIndicator") != null) {
            try {
                locationRequestParams.plrLcsRequestorFormatIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsRequestorFormatIndicator"));
                if (locationRequestParams.plrLcsRequestorFormatIndicator != LCSFormatIndicator._LOGICAL_NAME &&
                    locationRequestParams.plrLcsRequestorFormatIndicator != LCSFormatIndicator._EMAIL_ADDRESS &&
                    locationRequestParams.plrLcsRequestorFormatIndicator != LCSFormatIndicator._MSISDN &&
                    locationRequestParams.plrLcsRequestorFormatIndicator != LCSFormatIndicator._URL &&
                    locationRequestParams.plrLcsRequestorFormatIndicator != LCSFormatIndicator._SIP_URL) {
                    throw new IllegalArgumentException("Incorrect lcsRequestorFormatIndicator argument, must be one of 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), " +
                        "2 (MSISDN), 3 (URL) or 4 (SIP_URL)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsRequestorFormatIndicator argument, must be one of 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS),  " +
                    "2 (MSISDN), 3 (URL) or 4 (SIP_URL)";
                locationRequestParams.numberFormatException = numberFormatException;
            }
        } else {
            if (locationRequestParams.plrLcsRequestorIdString != null) {
                throw new IllegalArgumentException("lcsRequestorFormatIndicator argument cannot be null when a valid lcsRequestorId argument is provided");
            }
        }


        /*** PLR optional AVP: [ LCS-Priority ] ***/
        if (httpServletRequest.getParameter("lcsPriority") != null) {
            try {
                locationRequestParams.plrLcsPriority = Long.valueOf(httpServletRequest.getParameter("lcsPriority"));
                if (locationRequestParams.plrLcsPriority != 0 && locationRequestParams.plrLcsPriority != 1) {
                    throw new IllegalArgumentException("Incorrect lcsPriority argument, must be one of 0 (highest priority) or 1 (normal priority)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPriority argument, must be one of 0 (highest priority) or 1 (normal priority)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }


        /*** PLR optional AVP: [ LCS-QoS ] ***/
        // LCS-QoS grouped AVP
        // LCS-QoS ::= <AVP header: 2504 10415>
        //      [ LCS-QoS-Class ]
        //      [ Horizontal-Accuracy ]
        //      [ Vertical-Accuracy ]
        //      [ Vertical-Requested ]
        //      [ Response-Time]

        // [ LCS-QoS-Class ] AVP of optional grouped [ LCS-QoS ] AVP
        // [ LCS-QoS-Class ]
        // The LCS-QoS-Class AVP is of the type Enumerated. The following values are defined: ASSURED (0), BEST EFFORT (1)
        if (httpServletRequest.getParameter("lcsQoSClass") != null) {
            try {
                locationRequestParams.plrQoSClass = Integer.valueOf(httpServletRequest.getParameter("lcsQoSClass"));
                if (locationRequestParams.plrQoSClass != LCSQoSClass._ASSURED &&
                    locationRequestParams.plrQoSClass != LCSQoSClass._BEST_EFFORT) {
                    throw new IllegalArgumentException("Incorrect lcsQoSClass argument, must be one of 0 (ASSURED) or 1 (BEST EFFORT)");
                } else {
                    if (locationRequestParams.plrQoSClass == 0)
                        locationRequestParams.plrQoSClass = LCSQoSClass._ASSURED;
                    else
                        locationRequestParams.plrQoSClass = LCSQoSClass._BEST_EFFORT;
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsQoSClass argument, must be one of 0 (ASSURED) or 1 (BEST EFFORT)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }
        // [ Horizontal-Accuracy ] AVP of optional grouped [ LCS-QoS ] AVP
        // The Horizontal-Accuracy AVP is of type Unsigned32.
        // Bits 6-0 corresponds to Uncertainty Code defined in 3GPP TS 23.032 [3].
        // The horizontal location error should be less than the error indicated by the uncertainty code with 67% confidence.
        // Bits 7 to 31 shall be ignored.
        if (httpServletRequest.getParameter("horizontalAccuracy") != null) {
            try {
                long horizontalAccuracy = Long.parseLong(httpServletRequest.getParameter("horizontalAccuracy"));
                if (horizontalAccuracy < 0) {
                    throw new IllegalArgumentException("Incorrect horizontalAccuracy argument, must be a positive number " +
                        "corresponding to the desired horizontal accuracy in metres");
                } else {
                    locationRequestParams.plrHorizontalAccuracy = (long) GADShapesUtils.encodeUncertainty(horizontalAccuracy);
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect horizontalAccuracy argument, must be a positive number corresponding " +
                    "to the desired horizontal accuracy in metres";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }
        // [ Vertical-Accuracy ] AVP of optional grouped [ LCS-QoS ] AVP
        // The Vertical-Accuracy AVP is of type Unsigned32.
        // Bits 6-0 corresponds to Uncertainty Code defined in 3GPP TS 23.032 [3].
        // The vertical location error should be less than the error indicated by the uncertainty code with 67% confidence.
        // Bits 7 to 31 shall be ignored
        if (httpServletRequest.getParameter("verticalAccuracy") != null) {
            try {
                long verticalAccuracy = Long.parseLong(httpServletRequest.getParameter("verticalAccuracy"));
                if (verticalAccuracy < 0) {
                    throw new IllegalArgumentException("Incorrect verticalAccuracy argument, must be a positive number " +
                        "corresponding to the desired vertical accuracy in metres");
                } else {
                    locationRequestParams.plrVerticalAccuracy = (long) GADShapesUtils.encodeUncertainty(verticalAccuracy);
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect verticalAccuracy argument, must be a positive number corresponding " +
                    "to the desired vertical accuracy in metres";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        // [ Vertical-Requested ] AVP of optional grouped [ LCS-QoS ] AVP
        // The Vertical-Requested AVP is of type Enumerated. The following values are defined:
        //  VERTICAL_COORDINATE_IS_NOT REQUESTED (0)
        //  VERTICAL_COORDINATE_IS_REQUESTED (1)
        // Default value if AVP is not present is: VERTICAL_COORDINATE_IS_NOT_REQUESTED (0).
        if (httpServletRequest.getParameter("verticalCoordinateRequest") != null) {
            try {
                locationRequestParams.plrVerticalRequested = Integer.valueOf(httpServletRequest.getParameter("verticalCoordinateRequest"));
                if (locationRequestParams.plrVerticalRequested != VerticalRequested._VERTICAL_COORDINATE_IS_NOT_REQUESTED &&
                    locationRequestParams.plrVerticalRequested != VerticalRequested._VERTICAL_COORDINATE_IS_REQUESTED) {
                    throw new IllegalArgumentException("Incorrect verticalCoordinateRequest argument, must be 0 (VERTICAL_COORDINATE_IS_NOT_REQUESTED) " +
                        "or 1 (VERTICAL_COORDINATE_IS_REQUESTED)");
                } else {
                    if (locationRequestParams.plrVerticalRequested == 0)
                        locationRequestParams.plrVerticalRequested = VerticalRequested._VERTICAL_COORDINATE_IS_NOT_REQUESTED;
                    if (locationRequestParams.plrVerticalRequested == 1)
                        locationRequestParams.plrVerticalRequested = VerticalRequested._VERTICAL_COORDINATE_IS_REQUESTED;
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect verticalCoordinateRequest argument, must be 0 or 1 for VERTICAL_COORDINATE_IS_NOT_REQUESTED or VERTICAL_COORDINATE_IS_REQUESTED respectively";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        // [ Response-Time] AVP of optional grouped [ LCS-QoS ] AVP
        // The Response-Time AVP is of type Enumerated. The following values are defined: LOW_DELAY (0), DELAY_TOLERANT (1)
        if (httpServletRequest.getParameter("responseTime") != null) {
            try {
                locationRequestParams.plrResponseTime = Integer.valueOf(httpServletRequest.getParameter("responseTime"));
                if (locationRequestParams.plrResponseTime != net.java.slee.resource.diameter.slg.events.avp.ResponseTime._LOW_DELAY &&
                    locationRequestParams.plrResponseTime != net.java.slee.resource.diameter.slg.events.avp.ResponseTime._DELAY_TOLERANT) {
                    throw new IllegalArgumentException("Incorrect responseTime argument, must be 0 or 1 for LOW_DELAY or DELAY_TOLERANT respectively");
                } else {
                    if (locationRequestParams.plrResponseTime == 0)
                        locationRequestParams.plrResponseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime._LOW_DELAY;
                    if (locationRequestParams.plrResponseTime == 1)
                        locationRequestParams.plrResponseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime._DELAY_TOLERANT;
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect responseTime argument, must be 0 or 1 for LOW_DELAY or DELAY_TOLERANT respectively";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        /*** PLR optional AVP: [ Velocity-Requested ] ***/
        // The Velocity-Requested AVP is of type Enumerated. The following values are defined:
        //  VELOCITY_IS_NOT_REQUESTED (0)
        //  VELOCITY_IS_REQUESTED (1)
        //  Default value if AVP is not present is: VELOCITY_IS_NOT_REQUESTED (0).
        if (httpServletRequest.getParameter("velocityRequested") != null) {
            try {
                locationRequestParams.plrVelocityRequested = Integer.valueOf(httpServletRequest.getParameter("velocityRequested"));
                if (locationRequestParams.plrVelocityRequested != VelocityRequested._VELOCITY_IS_NOT_REQUESTED &&
                    locationRequestParams.plrVelocityRequested != VelocityRequested._VELOCITY_IS_REQUESTED) {
                    throw new IllegalArgumentException("Incorrect velocityRequested argument, must be 0 (VELOCITY_IS_NOT_REQUEST) or 1 (VELOCITY_IS_REQUESTED)");
                } else {
                    if (locationRequestParams.plrVelocityRequested == 0)
                        locationRequestParams.plrVelocityRequested = VelocityRequested._VELOCITY_IS_NOT_REQUESTED;
                    if (locationRequestParams.plrVelocityRequested == 1)
                        locationRequestParams.plrVelocityRequested = VelocityRequested._VELOCITY_IS_REQUESTED;
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect velocityRequested argument, must be 0 (VELOCITY_IS_NOT_REQUEST) or 1 (VELOCITY_IS_REQUESTED) respectively";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }


        /*** PLR optional AVP: [ LCS-Supported-GAD-Shapes ] ***/
        // The Supported-GAD-Shapes AVP is of type Unsigned32 and it shall contain a bitmask.
        //  A node shall mark in the BIT STRING all Shapes defined in 3GPP TS 23.032 [3] it supports.
        //  Bits 8-0 in shall indicate the supported Shapes defined in 3GPP TS 23.032 [3]. Bits 9 to 31 shall be ignored.
        //  ellipsoidPoint (0)
        //  ellipsoidPointWithUncertaintyCircle (1)
        //  ellipsoidPointWithUncertaintyEllipse (2)
        //  polygon (3)
        //  ellipsoidPointWithAltitude (4)
        //  ellipsoidPointWithAltitudeAndUncertaintyEllipsoid (5)
        //  ellipsoidArc (6)
        //  highAccuracyEllipsoidPointWithUncertaintyEllipse (7)
        //  highAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid (8)
        if (httpServletRequest.getParameter("lcsSupportedGADShapes") != null) {
            try {
                locationRequestParams.plrLcsSupportedGadShapes = Long.valueOf(httpServletRequest.getParameter("lcsSupportedGADShapes"));
                if (locationRequestParams.plrLcsSupportedGadShapes < 0 || locationRequestParams.plrLcsSupportedGadShapes > 255) {
                    throw new IllegalArgumentException("Incorrect lcsSupportedGADShapes argument, must be an integer between 1 and 255 indicating " +
                        "support for ellipsoidPoint (bit 0)\n" +
                        "ellipsoidPointWithUncertaintyCircle (bit 1)\n" +
                        "ellipsoidPointWithUncertaintyEllipse (bit 2)\n" +
                        "polygon (bit 3)\n" +
                        "ellipsoidPointWithAltitude (bit 4)\n" +
                        "ellipsoidPointWithAltitudeAndUncertaintyEllipsoid (bit 5)\n" +
                        "ellipsoidArc (bit 6)\n" +
                        "highAccuracyEllipsoidPointWithUncertaintyEllipse (bit 7)\n" +
                        "highAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid (bit 8)\n");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsSupportedGADShapes argument, must be an integer between 1 and 255 indicating " +
                    "support for ellipsoidPoint (bit 0)\n" +
                    "ellipsoidPointWithUncertaintyCircle (bit 1)\n" +
                    "ellipsoidPointWithUncertaintyEllipse (bit 2)\n" +
                    "polygon (bit 3)\n" +
                    "ellipsoidPointWithAltitude (bit 4)\n" +
                    "ellipsoidPointWithAltitudeAndUncertaintyEllipsoid (bit 5)\n" +
                    "ellipsoidArc (bit 6)\n" +
                    "highAccuracyEllipsoidPointWithUncertaintyEllipse (bit 7)\n" +
                    "highAccuracyEllipsoidPointWithAltitudeAndUncertaintyEllipsoid (bit 8)\n";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }


        /*** PLR optional AVP: [ LCS-Service-Type-ID ] ***/
        if (httpServletRequest.getParameter("lcsServiceTypeId") != null) {
            try {
                locationRequestParams.plrLcsServiceTypeId = Long.valueOf(httpServletRequest.getParameter("lcsServiceTypeId"));
                if (locationRequestParams.plrLcsServiceTypeId > 127 || locationRequestParams.plrLcsServiceTypeId < 0) {
                    throw new IllegalArgumentException("Incorrect lcsServiceTypeId argument, must be a positive integer value equal or higher than 0 and lower than 128");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsServiceTypeId argument, must be a positive integer value equal or higher than 0 and lower than 128";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        /*** Deferred Location Requests ***/
            /* 3GPP TS 23.271
                4.4.2 Deferred Location Request
                Request for location contingent on some current or future events where the response from the LCS Server to the LCS Client
                may occur some time after the request was sent.
            */

        /*** PLR optional AVP: [ Deferred-Location-Type ] ***/
            /* The Deferred-Location-Type AVP is of type Unsigned32 and it shall contain a bit mask.
               Each bit indicates a type of event, until when the location estimation is deferred.
               For details, please refer to 3GPP TS 23.271 [3] clause 4.4.2.
               The meaning of the bits shall be as defined in table 7.4.36/1:
                Table 7.4.36/1: Deferred-Location-Type
                Bit	    Event Type	            Description
                0	    UE-Available	        Any event in which the SGSN has established a contact with the UE.
                1	    Entering-Into-Area	    An event where the UE enters a pre-defined geographical area.
                2	    Leaving-From-Area	    An event where the UE leaves a pre-defined geographical area.
                3	    Being-Inside-Area	    An event where the UE is currently within the pre-defined geographical area.
                4	    Periodic-LDR	        An event where a defined periodic timer expires in the UE and activates a location report
                                                or a location request.
                5	    Motion-Event	        An event where the UE moves by more than a minimum linear distance.
                                                This event is applicable to a deferred EPC-MT-LR only.
                6	    LDR-Activated	        An event where deferred location reporting has been activated in the UE.
                                                This event is applicable to a deferred EPC-MT-LR only.
                7	    Maximum-Interval-Expiration	    An event where the maximum reporting interval has expired.
                                                        This event is applicable to a deferred EPC-MT-LR only.
             */
        if (httpServletRequest.getParameter("lcsDeferredLocationType") != null) {
            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                try {
                    locationRequestParams.plrDeferredLocationType = Long.valueOf(httpServletRequest.getParameter("lcsDeferredLocationType"));
                    if (locationRequestParams.plrDeferredLocationType != 1 && locationRequestParams.plrDeferredLocationType != 2 &&
                        locationRequestParams.plrDeferredLocationType != 4 && locationRequestParams.plrDeferredLocationType != 8 &&
                        locationRequestParams.plrDeferredLocationType != 16 && locationRequestParams.plrDeferredLocationType != 32 &&
                        locationRequestParams.plrDeferredLocationType != 64 && locationRequestParams.plrDeferredLocationType != 128) {
                        throw new IllegalArgumentException("Incorrect lcsDeferredLocationType argument, valid values are 1 (UE-Available), 2 (Entering-Into-Area), " +
                            "4 (Leaving-From-Area), 8 (Being-Inside-Area), 16 (Periodic-LDR), 32 (Motion-Event), 64 (LDR-Activated) or 128 (Maximum-Interval-Expiration)");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect lcsDeferredLocationType argument, valid values are 1 (UE-Available), 2 (Entering-Into-Area), " +
                        "4 (Leaving-From-Area), 8 (Being-Inside-Area), 16 (Periodic-LDR), 32 (Motion-Event), 64 (LDR-Activated) or 128 (Maximum-Interval-Expiration)";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("lcsDeferredLocationType argument is conditional to lcsLocationType argument " +
                    "being equal to 3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
            }
        }


        /*** Area Events ***/
            /* 3GPP TS 23.271
                 4.4.2.1 Types of event
                b) Change of Area: An event where the UE enters or leaves a pre-defined geographical area
                or if the UE is currently within the pre-defined geographical area.
                Only one type of area event may be defined (i.e. entering, leaving or remaining within the area).
                The LCS client defines the target area as a geographical area, as an E.164 country code for a geographic area [35a],
                as a PLMN identity or as a geopolitical name of the area.
                The LCS server may translate and define the target area as the identities of one or more radio cells, location areas,
                routing areas, tracking areas, country code or PLMN identity.
                The target UE must not give the target UE user access to the area definitions and network identities.
                The change of area event may be reported one time only, or several times.
                The area event report must not be repeated more often than allowed by the LCS client.
                The change of area event report shall contain an indication of the event occurrence.
                The location estimate may be included in the report.
                If an area event is detected by the UE but an event report cannot be sent
                (e.g. because the UE cannot access the network or due to a minimum reporting interval),
                a report shall be sent later when possible irrespective of whether the area event still applies for the current UE location.
                For E-UTRAN access, area event reporting is controlled by a minimum and a maximum reporting time.
                The minimum reporting time defines the minimum allowed time between successive area events.
                The maximum reporting time defines the maximum time between successive reports.
                When a UE sends a report due to expiration of the maximum reporting time,
                the UE indicates expiration of the maximum reporting time as the trigger event.
                The maximum reporting time enables the LCS client, R-GMLC and H-GMLC to remain aware of continuing support by the UE
                for the area event (e.g. to detect if area event reporting may have been aborted due to UE power off).
             */

        /*** PLR optional AVP: [ Area-Event-Info ] ***/
        // Area-Event-Info grouped AVP
        // Area-Event-Info ::= <AVP header: 2533 10415>
        //      { Area-Definition }
        //      [ Occurrence-Info ]
        //      [ Interval-Time ]
        //      [ Maximum-Interval ]
        //      [ Sampling-Interval ]
        //      [ Reporting-Duration ]
        //      [ Reporting-Location-Requirements ]

        // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
        // Interval-Time and Maximum-Interval AVPs are only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
        // If not included, the default value of Interval-Time shall be considered as one
        // and the default values of Maximum-Interval, Sampling-Interval and Reporting-Duration shall each be considered as the maximum value.

        // Area-Definition grouped AVP
        // Area-Definition ::= <AVP header: 2534 10415>
        //        1*10{ Area }
        //        *240[ Additional-Area ]

        // Area grouped AVP
        // Area ::= <AVP header: 2535 10415>
        //      { Area-Type }
        //      { Area-Identification }
        if (locationRequestParams.plrDeferredLocationType != null) {
            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                if (locationRequestParams.plrDeferredLocationType == 2 || locationRequestParams.plrDeferredLocationType == 4 ||
                    locationRequestParams.plrDeferredLocationType == 8) {

                    // { Area-Type } AVP of optional grouped [ Area-Event-Info ] AVP
                        /*
                           The Area-Type AVP is of type Unsigned32. The following values are defined:
                            "Country Code" 0, "PLMN ID", "Location Area ID 2, "Routing Area ID"	3, "Cell Global ID"	4, "UTRAN Cell ID"	5,
                            "Tracking Area ID" 6 and "E-UTRAN Cell Global ID" 7
                        */
                    if (httpServletRequest.getParameter("lcsAreaType") != null) {
                        try {
                            locationRequestParams.plrAreaType = Long.valueOf(httpServletRequest.getParameter("lcsAreaType"));
                            if (locationRequestParams.plrAreaType < 0 || locationRequestParams.plrAreaType > 7) {
                                throw new IllegalArgumentException("Incorrect lcsAreaType argument, must be 0 (Country Code), 1 (PLMN ID), 2 (Location Area ID), 3 (Routing Area ID), " +
                                    "4 (Cell Global ID), 5 (UTRAN Cell ID), 6 (Tracking Area ID) or 7 (E-UTRAN Cell Global ID)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaType argument, must be 0 (Country Code), 1 (PLMN ID), 2 (Location Area ID), 3 (Routing Area ID), " +
                                "4 (Cell Global ID), 5 (UTRAN Cell ID), 6 (Tracking Area ID) or 7 (E-UTRAN Cell Global ID)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        throw new IllegalArgumentException("lcsAreaType argument can not be null when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }

                    // { Area-Identification } AVP of optional grouped [ Area-Event-Info ] AVP
                    // The Area-Identification AVP is of type OctetString and shall contain the identification
                    // of the area applicable for the change of area event based deferred location reporting.
                    locationRequestParams.plrAreaIdentification = httpServletRequest.getParameter("lcsAreaId");
                    if (locationRequestParams.plrAreaIdentification != null) {
                        if (locationRequestParams.plrAreaType == null) {
                            throw new IllegalArgumentException("lcsAreaId argument needs a valid lcsAreaType argument value");
                        } else {
                            String lcsAreaType = convertAreaTypeToString(locationRequestParams.plrAreaType);
                            String plrAreaIdTbcd = setAreaIdTbcd(locationRequestParams.plrAreaIdentification.split("-"), lcsAreaType);
                            if (plrAreaIdTbcd.equalsIgnoreCase("Invalid")) {
                                throw new IllegalArgumentException("Invalid lcsAreaId argument");
                            }
                        }
                    } else {
                        if (locationRequestParams.plrAreaType != null) {
                            throw new IllegalArgumentException("lcsAreaId argument can not be null when a valid lcsAreaType argument value is provided");
                        }
                    }

                    // [ Occurrence-Info ] AVP of optional grouped [ Area-Event-Info ] AVP
                    if (httpServletRequest.getParameter("lcsAreaEventOccurrenceInfo") != null) {
                        try {
                            if (locationRequestParams.plrAreaType == null && locationRequestParams.plrAreaIdentification == null) {
                                throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument needs valid lcsAreaType and lcsAreaId values");
                            } else {
                                locationRequestParams.plrAreaEventOccurrenceInfo = Integer.valueOf(httpServletRequest.getParameter("lcsAreaEventOccurrenceInfo"));
                                if (locationRequestParams.plrAreaEventOccurrenceInfo < 0 || locationRequestParams.plrAreaEventOccurrenceInfo > 1) {
                                    throw new IllegalArgumentException("Incorrect lcsAreaEventOccurrenceInfo argument, must be 0 or 1 for ONE_TIME_EVENT or MULTIPLE_TIME_EVENT respectively");
                                } else {
                                    if (locationRequestParams.plrAreaEventOccurrenceInfo == 0)
                                        locationRequestParams.plrAreaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT;
                                    if (locationRequestParams.plrAreaEventOccurrenceInfo == 1)
                                        locationRequestParams.plrAreaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT;
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaEventOccurrenceInfo argument, must be 0 or 1 for ONE_TIME_EVENT or MULTIPLE_TIME_EVENT respectively";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
                        if (locationRequestParams.plrAreaType != null && locationRequestParams.plrAreaIdentification != null) {
                            locationRequestParams.plrAreaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT;
                        }
                    }

                    // [ Interval-Time ] AVP of optional grouped [ Area-Event-Info ] AVP
                    // The Interval-Time AVP (Unsigned32) contains the minimum time interval
                    // between area reports or motion reports, in seconds.
                    // The minimum value shall be 1 second and the maximum value 32767 seconds.
                    if (httpServletRequest.getParameter("lcsAreaEventIntervalTime") != null) {
                        try {
                            if (locationRequestParams.plrAreaType == null && locationRequestParams.plrAreaIdentification == null) {
                                throw new IllegalArgumentException("lcsAreaEventIntervalTime argument needs valid lcsAreaType and lcsAreaId values");
                            } else {
                                // Interval-Time AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                                if (locationRequestParams.plrAreaEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                    locationRequestParams.plrAreaEventIntervalTime = Long.valueOf(httpServletRequest.getParameter("lcsAreaEventIntervalTime"));
                                    if (locationRequestParams.plrAreaEventIntervalTime < 1 ||
                                        locationRequestParams.plrAreaEventIntervalTime > 32767) {
                                        throw new IllegalArgumentException("Incorrect lcsAreaEventIntervalTime argument, must be an integer value between 1 and 32767 (seconds)");
                                    }
                                } else {
                                    throw new IllegalArgumentException("lcsAreaEventIntervalTime argument only applies when lcsAreaEventOccurrenceInfo argument" +
                                        " equals 1 (MULTIPLE_TIME_EVENT)");
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaEventIntervalTime argument, must be an integer value between 1 and 32767 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // Interval-Time AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                        if (locationRequestParams.plrAreaEventOccurrenceInfo != null) {
                            if (locationRequestParams.plrAreaEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                // If not included, the default value of Interval-Time shall be considered as one
                                locationRequestParams.plrAreaEventIntervalTime = 1L;
                            }
                        }
                    }

                    // [ Maximum-Interval ] AVP of optional grouped [ Area-Event-Info ] AVP
                    // The Maximum-Interval AVP (Unsigned32) contains the maximum time interval between consecutive event reports,
                    // in seconds.
                    // The minimum value shall be 1 second and the maximum value 86400 seconds.
                    if (httpServletRequest.getParameter("lcsAreaEventMaxInterval") != null) {
                        try {
                            if (locationRequestParams.plrAreaType == null && locationRequestParams.plrAreaIdentification == null) {
                                throw new IllegalArgumentException("lcsAreaEventIntervalTime argument needs valid lcsAreaType and lcsAreaId values");
                            } else {
                                // Maximum-Interval AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                                if (locationRequestParams.plrAreaEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                    locationRequestParams.plrAreaEventMaxInterval = Long.valueOf(httpServletRequest.getParameter("lcsAreaEventMaxInterval"));
                                    if (locationRequestParams.plrAreaEventMaxInterval < 1 || locationRequestParams.plrAreaEventMaxInterval > 86400) {
                                        throw new IllegalArgumentException("Incorrect lcsAreaEventMaxInterval argument, must be an integer value between 1 and 86400 (seconds)");
                                    }
                                } else {
                                    throw new IllegalArgumentException("lcsAreaEventMaxInterval argument only applies when lcsAreaEventOccurrenceInfo argument" +
                                        " equals 1 (MULTIPLE_TIME_EVENT)");
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaEventMaxInterval argument, must be an integer value between 1 and 86400 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // Maximum-Interval AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                        if (locationRequestParams.plrAreaEventOccurrenceInfo != null) {
                            if (locationRequestParams.plrAreaEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT)
                                // If not included, the default Maximum-Interval shall be considered as the maximum value.
                                locationRequestParams.plrAreaEventMaxInterval = 86400L;
                        }
                    }

                    // [ Sampling-Interval ] AVP of optional grouped [ Area-Event-Info ] AVP
                    // The Sampling-Interval AVP (Unsigned32) contains the maximum time interval
                    // between consecutive evaluations by a UE
                    // of an area event or motion event, in seconds.
                    // The minimum value shall be 1 second and the maximum value 3600 seconds.
                    if (httpServletRequest.getParameter("lcsAreaEventSamplingInterval") != null) {
                        try {
                            if (locationRequestParams.plrAreaType == null && locationRequestParams.plrAreaIdentification == null) {
                                throw new IllegalArgumentException("lcsAreaEventSamplingInterval argument needs valid lcsAreaType and lcsAreaId values");
                            } else {
                                locationRequestParams.plrAreaEventSamplingInterval = Long.valueOf(httpServletRequest.getParameter("lcsAreaEventSamplingInterval"));
                                if (locationRequestParams.plrAreaEventSamplingInterval < 1 ||
                                    locationRequestParams.plrAreaEventSamplingInterval > 3600) {
                                    throw new IllegalArgumentException("Incorrect lcsAreaEventSamplingInterval argument, must be an integer value between 1 and 3600 (seconds)");
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaEventSamplingInterval argument, must be an integer value between 1 and 3600 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // If not included, the default value of Sampling-Interval shall be considered as the maximum value.
                        if (locationRequestParams.plrAreaEventOccurrenceInfo != null)
                            locationRequestParams.plrAreaEventSamplingInterval = 3600L;
                    }

                    // [ Reporting-Duration ] AVP of optional grouped [ Area-Event-Info ] AVP
                    if (httpServletRequest.getParameter("lcsAreaEventReportingDuration") != null) {
                        try {
                            if (locationRequestParams.plrAreaType == null && locationRequestParams.plrAreaIdentification == null) {
                                throw new IllegalArgumentException("lcsAreaEventReportingDuration argument needs valid lcsAreaType and lcsAreaId values");
                            } else {
                                locationRequestParams.plrAreaEventReportingDuration = Long.valueOf(httpServletRequest.getParameter("lcsAreaEventReportingDuration"));
                                if (locationRequestParams.plrAreaEventReportingDuration < 1 ||
                                    locationRequestParams.plrAreaEventReportingDuration > 8640000) {
                                    throw new IllegalArgumentException("Incorrect lcsAreaEventReportingDuration argument, must be an integer value between 1 and 8640000 (seconds)");
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaEventReportingDuration argument, must be an integer value between 1 and 8640000 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // If not included, the default value of Reporting-Duration shall be considered as the maximum value.
                        if (locationRequestParams.plrAreaEventOccurrenceInfo != null)
                            locationRequestParams.plrAreaEventReportingDuration = 8640000L;
                    }

                    // [ Reporting-Location-Requirements ] AVP of optional grouped [ Area-Event-Info ] AVP
                    if (httpServletRequest.getParameter("lcsAreaEventReportLocationReqs") != null) {
                        // The Reporting-Location-Requirements AVP is of type Unsigned32 and it shall contain a bit string
                        // indicating requirements on location provision for a deferred EPC-MT-LR.
                        // When a bit is set to one, the corresponding requirement is present.
                        // Bit 0 - A location estimate is required for each area event,
                        // motion event report or expiration of the maximum time interval between event reports.
                        try {
                            if (locationRequestParams.plrAreaType == null && locationRequestParams.plrAreaIdentification == null) {
                                throw new IllegalArgumentException("lcsAreaEventReportLocationReqs argument needs valid lcsAreaType and lcsAreaId values");
                            } else {
                                locationRequestParams.plrAreaEventRepLocRequirements = Long.valueOf(httpServletRequest.getParameter("lcsAreaEventReportLocationReqs"));
                                if (locationRequestParams.plrAreaEventRepLocRequirements < 0 || locationRequestParams.plrAreaEventRepLocRequirements > 31) {
                                    throw new IllegalArgumentException("Incorrect lcsAreaEventReportLocationReqs argument for a deferred EPC-MT-LR. Must be an integer value between 0 and 31");
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsAreaEventReportLocationReqs argument for a deferred EPC-MT-LR. Must be an integer value between 0 and 31";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // When a bit is set to zero or when the AVP is omitted, the corresponding requirement is not present.
                        // For support of backward compatibility, a receiver shall ignore any bits that are set to one but are not supported
                        if (locationRequestParams.plrAreaEventOccurrenceInfo != null)
                            locationRequestParams.plrAreaEventRepLocRequirements = 1L;
                    }

                    // Additional-Area ::= <AVP header: 2565 10415>
                    //          { Area-Type }
                    //          { Area-Identification }
                    // { Area-Type } AVP (additional area) of optional grouped [ Area-Event-Info ] AVP
                    if (httpServletRequest.getParameter("lcsAdditionalAreaType") != null) {
                        if (locationRequestParams.plrDeferredLocationType == 2 || locationRequestParams.plrDeferredLocationType == 4 ||
                            locationRequestParams.plrDeferredLocationType == 8) {
                            try {
                                locationRequestParams.plrAdditionalAreaType = Long.valueOf(httpServletRequest.getParameter("lcsAdditionalAreaType"));
                                if (locationRequestParams.plrAdditionalAreaType < 0 || locationRequestParams.plrAdditionalAreaType > 7) {
                                    throw new IllegalArgumentException("Incorrect lcsAdditionalAreaType argument, must be 0 (Country Code), 1 (PLMN ID), 2 (Location Area ID), 3 (Routing Area ID), " +
                                        "4 (Cell Global ID), 5 (UTRAN Cell ID), 6 (Tracking Area ID) or 7 (E-UTRAN Cell Global ID)");
                                }
                            } catch (NumberFormatException nfe) {
                                numberFormatException = "Incorrect lcsAdditionalAreaType argument, must be 0 (Country Code), 1 (PLMN ID), 2 (Location Area ID), 3 (Routing Area ID), " +
                                    "4 (Cell Global ID), 5 (UTRAN Cell ID), 6 (Tracking Area ID) or 7 (E-UTRAN Cell Global ID)";
                                locationRequestParams.numberFormatException = numberFormatException;
                                return locationRequestParams;
                            }
                        } else {
                            throw new IllegalArgumentException("lcsAdditionalAreaType argument only applies when lcsDeferredLocationType argument value equals 0 (UE-Available), " +
                                "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                        }
                    }

                    // { Area-Identification } AVP (additional area) of optional grouped [ Area-Event-Info ] AVP
                    locationRequestParams.plrAdditionalAreaIdentification = httpServletRequest.getParameter("lcsAdditionalAreaId");
                    if (locationRequestParams.plrAdditionalAreaIdentification != null) {
                        if (locationRequestParams.plrAdditionalAreaType == null) {
                            throw new IllegalArgumentException("Incorrect input, lcsAreaId argument needs a valid lcsAdditionalAreaType argument value");
                        } else {
                            String lcsAdditionalAreaType = convertAreaTypeToString(locationRequestParams.plrAdditionalAreaType);
                            String plrAdditionalAreaIdTbcd = setAreaIdTbcd(locationRequestParams.plrAdditionalAreaIdentification.split("-"), lcsAdditionalAreaType);
                            if (plrAdditionalAreaIdTbcd.equalsIgnoreCase("Invalid")) {
                                throw new IllegalArgumentException("Invalid lcsAdditionalAreaId argument");
                            }
                        }
                    } else {
                        if (locationRequestParams.plrAdditionalAreaType != null) {
                            throw new IllegalArgumentException("lcsAdditionalAreaId argument can not be null when a valid lcsAdditionalAreaType argument value is provided");
                        }
                    }


                } else {
                    // Check if Area Event Info parameters were set when they do not apply as for the Deferred Location Type value
                    if (httpServletRequest.getParameter("lcsAreaType") != null) {
                        throw new IllegalArgumentException("lcsAreaType argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaId") != null) {
                        throw new IllegalArgumentException("lcsAreaId argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaEventOccurrenceInfo") != null) {
                        throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaEventIntervalTime") != null) {
                        throw new IllegalArgumentException("lcsAreaEventIntervalTime argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaEventMaxInterval") != null) {
                        throw new IllegalArgumentException("lcsAreaEventMaxInterval argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaEventSamplingInterval") != null) {
                        throw new IllegalArgumentException("lcsAreaEventSamplingInterval argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaEventReportingDuration") != null) {
                        throw new IllegalArgumentException("lcsAreaEventReportingDuration argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAreaEventReportLocationReqs") != null) {
                        throw new IllegalArgumentException("lcsAreaEventReportLocationReqs argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAdditionalAreaType") != null) {
                        throw new IllegalArgumentException("lcsAdditionalAreaType argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                    if (httpServletRequest.getParameter("lcsAdditionalAreaId") != null) {
                        throw new IllegalArgumentException("lcsAdditionalAreaId argument only applies when lcsDeferredLocationType argument value equals " +
                            "2 (Entering-Into-Area), 4 (Leaving-From-Area), 8 (Being-Inside-Area)");
                    }
                }
            } else {
                // Check if Area Event Info parameters were set when they do not apply as for the SLg Location Type value
                if (httpServletRequest.getParameter("lcsAreaType") != null) {
                    throw new IllegalArgumentException("lcsAreaType argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaId") != null) {
                    throw new IllegalArgumentException("lcsAreaId argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaEventOccurrenceInfo") != null) {
                    throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaEventIntervalTime") != null) {
                    throw new IllegalArgumentException("lcsAreaEventIntervalTime argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaEventMaxInterval") != null) {
                    throw new IllegalArgumentException("lcsAreaEventMaxInterval argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaEventSamplingInterval") != null) {
                    throw new IllegalArgumentException("lcsAreaEventSamplingInterval argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaEventReportingDuration") != null) {
                    throw new IllegalArgumentException("lcsAreaEventReportingDuration argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAreaEventReportLocationReqs") != null) {
                    throw new IllegalArgumentException("lcsAreaEventReportLocationReqs argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAdditionalAreaType") != null) {
                    throw new IllegalArgumentException("lcsAdditionalAreaType argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsAdditionalAreaId") != null) {
                    throw new IllegalArgumentException("lcsAdditionalAreaId argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
            }
        }


        /*** Motion Events ***/
            /*
             /* 3GPP TS 23.271
                 4.4.2.1 Types of event
                d) Motion: An event where the UE moves by more than some predefined linear distance from a previous location.
                    The motion event may be reported one time only, or several times.
                    The motion event report shall contain an indication of the event occurrence.
                    A location estimate may be included in the report if requested by the LCS client.
                    For successive motion event reports, motion is determined relative to the UE location corresponding to the immediately
                    preceding event report (including an event report triggered by expiration of the maximum reporting time).
                    If a motion event is detected by the UE but an event report is deferred
                    (e.g. because the UE cannot access the network temporarily), a report shall be sent later when possible
                    irrespective of whether the motion event still applies to the current UE location.
                    Motion reporting is controlled by a minimum and a maximum reporting time.
                    The minimum reporting time defines the minimum allowed time between successive event reports.
                    The maximum reporting time defines the maximum time between successive reports.
                    When a UE sends a report due to expiration of the maximum reporting time, the UE indicates expiration of the maximum reporting time
                    as the trigger event.
                    The maximum reporting time enables the LCS client, R-GMLC and HGMLC to remain aware of continuing support by the UE
                    for the motion event (e.g. to detect if motion event reporting may have been aborted due to UE power off).
             */

        /*** PLR optional AVP: [ Motion-Event-Info ] ***/
        // Motion-Event-Info ::= <AVP header: 2559 10415>
        //      { Linear-Distance }
        //      [ Occurrence-Info ]
        //      [ Interval-Time ]
        //      [ Maximum-Interval ]
        //      [ Sampling-Interval ]
        //      [ Reporting-Duration ]
        //      [ Reporting-Location-Requirements ]

        // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
        // Interval-Time and Maximum-Interval AVPs are only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
        // If not included, the default value of Interval-Time shall be considered as one and the default values of
        // Maximum-Interval, Sampling-Interval and Reporting-Duration shall each be considered as the maximum value.
        // The Motion-Event-Info AVP is only applicable to a deferred EPC-MT-LR.
        if (locationRequestParams.plrDeferredLocationType != null) {
            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                if (locationRequestParams.plrDeferredLocationType == 32) {

                    // { Linear-Distance } AVP of optional grouped [ Motion-Event-Info ] AVP
                    // The Linear-Distance AVP is of type Unsigned32 and it contains the minimum linear (straight line) distance
                    // for motion event reports, in meters. The minimum value shall be 1 and maximum value shall be 10,000.
                    if (httpServletRequest.getParameter("lcsMotionEventLinearDistance") != null) {
                        try {
                            locationRequestParams.plrMotionEventLinearDistance = Long.valueOf(httpServletRequest.getParameter("lcsMotionEventLinearDistance"));
                            if (locationRequestParams.plrMotionEventLinearDistance < 0 || locationRequestParams.plrMotionEventLinearDistance > 10000) {
                                throw new IllegalArgumentException("Incorrect lcsMotionEventLinearDistance argument, must be an integer value between 1 and 10000 (meters)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventLinearDistance argument, must be an integer value between 1 and 10000 (meters)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        throw new IllegalArgumentException("lcsMotionEventLinearDistance can not be null when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event) and lcsLocationType argument value equals 3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                    }

                    // [ Occurrence-Info ] AVP of optional grouped [ Motion-Event-Info ] AVP
                    if (httpServletRequest.getParameter("lcsMotionEventOccurrenceInfo") != null) {
                        try {
                            locationRequestParams.plrMotionEventOccurrenceInfo = Integer.valueOf(httpServletRequest.getParameter("lcsMotionEventOccurrenceInfo"));
                            if (locationRequestParams.plrMotionEventOccurrenceInfo != net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT &&
                                locationRequestParams.plrMotionEventOccurrenceInfo != net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                throw new IllegalArgumentException("Incorrect lcsMotionEventOccurrenceInfo argument, must be 0 or 1 for ONE_TIME_EVENT or MULTIPLE_TIME_EVENT respectively");
                            } else {
                                if (locationRequestParams.plrMotionEventOccurrenceInfo == 0)
                                    locationRequestParams.plrMotionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT;
                                if (locationRequestParams.plrMotionEventOccurrenceInfo == 1)
                                    locationRequestParams.plrMotionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT;
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventOccurrenceInfo argument, must be 0 or 1 for ONE_TIME_EVENT or MULTIPLE_TIME_EVENT respectively";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // If not included, the default value of Occurrence-Info shall be considered as "ONE_TIME_EVENT" (0).
                        if (locationRequestParams.plrMotionEventLinearDistance != null) {
                            locationRequestParams.plrMotionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT;
                        }
                    }

                    // [ Interval-Time ] AVP of optional grouped [ Motion-Event-Info ] AVP
                    // The Interval-Time AVP (Unsigned32) contains the minimum time interval
                    // between area reports or motion reports, in seconds.
                    // The minimum value shall be 1 second and the maximum value 32767 seconds.
                    if (httpServletRequest.getParameter("lcsMotionEventIntervalTime") != null) {
                        try {
                            locationRequestParams.plrMotionEventIntervalTime = Long.valueOf(httpServletRequest.getParameter("lcsMotionEventIntervalTime"));
                            // Interval-Time AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (locationRequestParams.plrMotionEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                if (locationRequestParams.plrMotionEventIntervalTime < 1 || locationRequestParams.plrMotionEventIntervalTime > 32767) {
                                    throw new IllegalArgumentException("Incorrect lcsMotionEventIntervalTime argument, must be an integer value between 1 and 32767 (seconds)");
                                }
                            } else {
                                throw new IllegalArgumentException("lcsMotionEventIntervalTime argument is only applicable when lcsMotionEventOccurrenceInfo is 1 (MULTIPLE_TIME_EVENT)");
                            }

                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventIntervalTime argument, must be an integer value between 1 and 32767 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // Interval-Time AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                        if (locationRequestParams.plrMotionEventOccurrenceInfo != null) {
                            if (locationRequestParams.plrMotionEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                // If not included, the default value of Interval-Time shall be considered as one.
                                locationRequestParams.plrMotionEventIntervalTime = 1L;
                            }
                        }
                    }

                    // [ Maximum-Interval ] AVP of optional grouped [ Motion-Event-Info ] AVP
                    // The Maximum-Interval AVP (Unsigned32) contains the maximum time interval between consecutive event reports,
                    // in seconds.
                    // The minimum value shall be 1 second and the maximum value 86400 seconds.
                    if (httpServletRequest.getParameter("lcsMotionEventMaxInterval") != null) {
                        try {
                            locationRequestParams.plrMotionEventMaximumInterval = Long.valueOf(httpServletRequest.getParameter("lcsMotionEventMaxInterval"));
                            // Maximum-Interval AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (locationRequestParams.plrMotionEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                if (locationRequestParams.plrMotionEventMaximumInterval < 1 ||
                                    locationRequestParams.plrMotionEventMaximumInterval > 86400) {
                                    throw new IllegalArgumentException("Incorrect lcsMotionEventMaxInterval argument, must be an integer value between 1 and 86400 (seconds)");
                                }
                            } else {
                                throw new IllegalArgumentException("lcsMotionEventMaxInterval argument is only applicable when lcsMotionEventOccurrenceInfo is 1 (MULTIPLE_TIME_EVENT)");
                            }

                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventMaxInterval argument, must be an integer value between 1 and 86400 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        if (locationRequestParams.plrMotionEventOccurrenceInfo != null) {
                            // Maximum-Interval AVP is only applicable when the Occurrence-Info is set to "MULTIPLE_TIME_EVENT" (1).
                            if (locationRequestParams.plrMotionEventOccurrenceInfo == net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT) {
                                // If not included, the default Maximum-Interval shall be considered as the maximum value.
                                locationRequestParams.plrMotionEventMaximumInterval = 86400L;
                            }
                        }
                    }

                    // [ Sampling-Interval ] AVP of optional grouped [ Motion-Event-Info ] AVP
                    // The Sampling-Interval AVP (Unsigned32) contains the maximum time interval
                    // between consecutive evaluations by a UE of an area event or motion event, in seconds.
                    // The minimum value shall be 1 second and the maximum value 3600 seconds.
                    if (httpServletRequest.getParameter("lcsMotionEventSamplingInterval") != null) {
                        try {
                            locationRequestParams.plrMotionEventSamplingInterval = Long.valueOf(httpServletRequest.getParameter("lcsMotionEventSamplingInterval"));
                            if (locationRequestParams.plrMotionEventSamplingInterval < 1 ||
                                locationRequestParams.plrMotionEventSamplingInterval > 3600) {
                                throw new IllegalArgumentException("Incorrect lcsMotionEventSamplingInterval argument, muest be an integer value between 1 and 3600 (seconds)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventSamplingInterval argument, muest be an integer value between 1 and 32767 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // If not included, the default Sampling-Interval shall be considered as the maximum value.
                        if (locationRequestParams.plrMotionEventOccurrenceInfo != null)
                            locationRequestParams.plrMotionEventSamplingInterval = 3600L;
                    }

                    // [ Reporting-Duration ] AVP of optional grouped [ Motion-Event-Info ] AVP
                    // The Reporting-Duration AVP (Unsigned32) contains the maximum duration of event reporting, in seconds.
                    // Its minimum value shall be 1 and maximum value shall be 8640000.
                    if (httpServletRequest.getParameter("lcsMotionEventReportingDuration") != null) {
                        try {
                            locationRequestParams.plrMotionEvenReportingDuration = Long.valueOf(httpServletRequest.getParameter("lcsMotionEventReportingDuration"));
                            if (locationRequestParams.plrMotionEvenReportingDuration < 1 ||
                                locationRequestParams.plrMotionEvenReportingDuration > 8640000) {
                                throw new IllegalArgumentException("Incorrect lcsMotionEventReportingDuration argument, must be an integer value between 1 and 8640000 (seconds)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventReportingDuration argument, must be an integer value between 1 and 8640000 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // If not included, the default Reporting-Duration shall be considered as the maximum value.
                        if (locationRequestParams.plrMotionEventOccurrenceInfo != null)
                            locationRequestParams.plrMotionEvenReportingDuration = 8640000L;
                    }

                    // [ Reporting-Location-Requirements ] AVP of optional grouped [ Motion-Event-Info ] AVP
                    // The Reporting-Location-Requirements AVP is of type Unsigned32 and it shall contain a bit string
                    // indicating requirements on location provision for a deferred EPC-MT-LR.
                    // When a bit is set to one, the corresponding requirement is present.
                    // Bit 0 - A location estimate is required for each area event,
                    // motion event report or expiration of the maximum time interval between event reports.
                    if (httpServletRequest.getParameter("lcsMotionEventReportLocationReqs") != null) {
                        try {
                            locationRequestParams.plrMotionEvenReportingLocationRequirements = Long.valueOf(httpServletRequest.getParameter("lcsMotionEventReportLocationReqs"));
                            if (locationRequestParams.plrMotionEvenReportingLocationRequirements < 0 || locationRequestParams.plrMotionEvenReportingLocationRequirements > 31) {
                                throw new IllegalArgumentException("Incorrect lcsMotionEventReportLocationReqs argument for a deferred EPC-MT-LR, must be an integer value between 0 and 31");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsMotionEventReportLocationReqs argument for a deferred EPC-MT-LR, must be an integer value between 0 and 31";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        // When a bit is set to zero or when the AVP is omitted, the corresponding requirement is not present.
                        // For support of backward compatibility, a receiver shall ignore any bits that are set to one but are not supported
                        // Bit 0 - A location estimate is required for each area event,
                        // motion event report or expiration of the maximum time interval between event reports.
                        if (locationRequestParams.plrMotionEventOccurrenceInfo != null)
                            locationRequestParams.plrMotionEvenReportingLocationRequirements = 1L;
                    }

                } else {
                    // Check if Motion Event Info parameters were set when they do not apply as for the Deferred Location Type value
                    if (httpServletRequest.getParameter("lcsMotionEventLinearDistance") != null) {
                        throw new IllegalArgumentException("lcsMotionEventLinearDistance argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                    if (httpServletRequest.getParameter("lcsMotionEventOccurrenceInfo") != null) {
                        throw new IllegalArgumentException("lcsMotionEventOccurrenceInfo argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                    if (httpServletRequest.getParameter("lcsMotionEventIntervalTime") != null) {
                        throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                    if (httpServletRequest.getParameter("lcsMotionEventMaxInterval") != null) {
                        throw new IllegalArgumentException("lcsMotionEventMaxInterval argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                    if (httpServletRequest.getParameter("lcsMotionEventSamplingInterval") != null) {
                        throw new IllegalArgumentException("lcsMotionEventSamplingInterval argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                    if (httpServletRequest.getParameter("lcsMotionEventReportingDuration") != null) {
                        throw new IllegalArgumentException("lcsMotionEventReportingDuration argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                    if (httpServletRequest.getParameter("lcsMotionEventReportLocationReqs") != null) {
                        throw new IllegalArgumentException("lcsMotionEventReportLocationReqs argument only applies when lcsDeferredLocationType argument value equals " +
                            "32 (Motion-Event)");
                    }
                }
            } else {
                // Check if Motion Event Info parameters were set when they do not apply as for the SLg Location Type value
                if (httpServletRequest.getParameter("lcsMotionEventLinearDistance") != null) {
                    throw new IllegalArgumentException("lcsMotionEventLinearDistance argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsMotionEventOccurrenceInfo") != null) {
                    throw new IllegalArgumentException("lcsMotionEventOccurrenceInfo argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsMotionEventIntervalTime") != null) {
                    throw new IllegalArgumentException("lcsAreaEventOccurrenceInfo argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsMotionEventMaxInterval") != null) {
                    throw new IllegalArgumentException("lcsMotionEventMaxInterval argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsMotionEventSamplingInterval") != null) {
                    throw new IllegalArgumentException("lcsMotionEventSamplingInterval argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsMotionEventReportingDuration") != null) {
                    throw new IllegalArgumentException("lcsMotionEventReportingDuration argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
                if (httpServletRequest.getParameter("lcsMotionEventReportLocationReqs") != null) {
                    throw new IllegalArgumentException("lcsMotionEventReportLocationReqs argument only applies when lcsLocationType argument value equals " +
                        "3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
            }
        }


        /*** PLR optional AVP: [ Periodic-LDR-Information ] ***/
        // Periodic-LDR-Info grouped AVP
        // Periodic-LDR-Info ::= <AVP header: 2540 10415>
        //    { Reporting-Amount }
        //    { Reporting-Interval }
        if (locationRequestParams.plrDeferredLocationType != null) {
            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                if (locationRequestParams.plrDeferredLocationType == 16) {

                    // { Reporting-Amount } AVP of optional grouped [ Periodic-LDR-Information ] AVP
                    // The Reporting-Amount AVP is of type Unsigned32 and it contains reporting frequency.
                    // Its minimum value shall be 1 and maximum value shall be 8639999
                    if (httpServletRequest.getParameter("lcsPeriodicReportingAmount") != null) {
                        try {
                            locationRequestParams.plrPeriodicLDRReportingAmount = Long.valueOf(httpServletRequest.getParameter("lcsPeriodicReportingAmount"));
                            if (locationRequestParams.plrPeriodicLDRReportingAmount < 1 ||
                                locationRequestParams.plrPeriodicLDRReportingAmount > 8639999) {
                                throw new IllegalArgumentException("Incorrect lcsPeriodicReportingAmount argument, must be an integer between 1 and 8639999 " +
                                    "indicating the reporting frequency");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsPeriodicReportingAmount argument, must be an integer between 1 and 8639999 " +
                                "indicating the reporting frequency";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        if (locationRequestParams.plrDeferredLocationType != null) {
                            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                                if (locationRequestParams.plrDeferredLocationType == 16) {
                                    throw new IllegalArgumentException("lcsPeriodicReportingAmount argument can not be null when " +
                                        "lcsLocationType is provided for a periodic LDR (lcsDeferredLocationType equals 16)");
                                }
                            }
                        }
                    }

                    // { Reporting-Interval } AVP of optional grouped [ Periodic-LDR-Information ] AVP
                    // The Interval-Time AVP is of type Unsigned32 and it contains reporting interval in seconds.
                    // Its minimum value shall be 1 and maximum value shall be 8639999.
                    if (httpServletRequest.getParameter("lcsPeriodicReportingInterval") != null) {
                        try {
                            locationRequestParams.plrPeriodicLDRReportingInterval = Long.valueOf(httpServletRequest.getParameter("lcsPeriodicReportingInterval"));
                            if (locationRequestParams.plrPeriodicLDRReportingInterval < 1 ||
                                locationRequestParams.plrPeriodicLDRReportingInterval > 8639999) {
                                throw new IllegalArgumentException("Incorrect lcsPeriodicReportingInterval argument, must be an integer indicating the reporting interval " +
                                    "between 1 and 8639999 (seconds)");
                            }
                            if (locationRequestParams.plrPeriodicLDRReportingAmount == null) {
                                throw new IllegalArgumentException("lcsPeriodicReportingAmount argument can not be null when lcsPeriodicReportingInterval argument is valid and " +
                                    "lcsDeferredLocationType argument value equals 4 (Periodic-LDR)");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "Incorrect lcsPeriodicReportingInterval argument, must be an integer indicating the reporting interval " +
                                "between 1 and 8639999 (seconds)";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    } else {
                        if (locationRequestParams.plrDeferredLocationType != null) {
                            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                                if (locationRequestParams.plrDeferredLocationType == 16) {
                                    throw new IllegalArgumentException("lcsPeriodicReportingInterval argument can not be null when " +
                                        "lcsDeferredLocationType argument value equals 16 (Periodic-LDR)");
                                }
                            }
                        }
                    }
                } else {
                    if (httpServletRequest.getParameter("lcsPeriodicReportingAmount") != null) {
                        throw new IllegalArgumentException("lcsPeriodicReportingAmount argument only applies when lcsDeferredLocationType argument " +
                            "value equals 16 (Periodic-LDR)");
                    }
                    if (httpServletRequest.getParameter("lcsPeriodicReportingInterval") != null) {
                        throw new IllegalArgumentException("lcsPeriodicReportingInterval argument only applies when lcsDeferredLocationType argument " +
                            "value equals 16 (Periodic-LDR)");
                    }
                }
            }
        }

        // Reporting-PLMN-List AVP
        // Reporting-PLMN-List ::= <AVP header: 2543 10415>
        //                    1*20{ PLMN-ID-List }
        //                    [ Prioritized-List-Indicator ]

        // PLMN-ID-List ::= <AVP header: 2544 10415>
        //          { Visited-PLMN-Id }
        //          [ Periodic-Location-Support-Indicator ]

        // { Visited-PLMN-Id } AVP of optional grouped [ Reporting-PLMN-List ] AVP
        if (httpServletRequest.getParameter("lcsVisitedPLMNId") != null) {
            locationRequestParams.plrVisitedPLMNIdList = httpServletRequest.getParameter("lcsVisitedPLMNId");
            String plrVisitedPLMNIdTbcd = setAreaIdTbcd(locationRequestParams.plrVisitedPLMNIdList.split("-"), "plmnId");
            if (plrVisitedPLMNIdTbcd.equalsIgnoreCase("Invalid")) {
                throw new IllegalArgumentException("Invalid lcsVisitedPLMNId argument");
            }
        }
        // [ Periodic-Location-Support-Indicator ] AVP of optional grouped [ Reporting-PLMN-List ] AVP
        if (httpServletRequest.getParameter("lcsPeriodicLocationSupportIndicator") != null) {
            try {
                locationRequestParams.plrPeriodicLocationSupportIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsPeriodicLocationSupportIndicator"));
                if (locationRequestParams.plrPeriodicLocationSupportIndicator != PeriodicLocationSupportIndicator._NOT_SUPPORTED &&
                    locationRequestParams.plrPeriodicLocationSupportIndicator != PeriodicLocationSupportIndicator._SUPPORTED) {
                    throw new IllegalArgumentException("Incorrect lcsPeriodicLocationSupportIndicator argument, must be one of 0 (NOT_SUPPORTED) or 1 (SUPPORTED)");
                } else {
                    if (locationRequestParams.plrPeriodicLocationSupportIndicator == 0)
                        locationRequestParams.plrPeriodicLocationSupportIndicator = PeriodicLocationSupportIndicator._NOT_SUPPORTED;
                    if (locationRequestParams.plrPeriodicLocationSupportIndicator == 1)
                        locationRequestParams.plrPeriodicLocationSupportIndicator = PeriodicLocationSupportIndicator._SUPPORTED;
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPeriodicLocationSupportIndicator argument, must be one of 0 (NOT_SUPPORTED) or 1 (SUPPORTED)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        } else {
            if (locationRequestParams.plrVisitedPLMNIdList != null) {
                throw new IllegalArgumentException("lcsPeriodicLocationSupportIndicator argument can not be null when a valid lcsVisitedPLMNId argument is provided");
            }
        }
        // [ Prioritized-List-Indicator ] AVP of optional grouped [ Reporting-PLMN-List ] AVP
        if (httpServletRequest.getParameter("lcsPrioritizedListIndicator") != null) {
            try {
                locationRequestParams.plrPrioritizedListIndicator = Integer.valueOf(httpServletRequest.getParameter("lcsPrioritizedListIndicator"));
                if (locationRequestParams.plrPrioritizedListIndicator != PrioritizedListIndicator._NOT_PRIORITIZED &&
                    locationRequestParams.plrPrioritizedListIndicator != PrioritizedListIndicator._PRIORITIZED) {
                    throw new IllegalArgumentException("Incorrect lcsPrioritizedListIndicator argument, must be one of 0 (NOT_PRIORITIZED) or 1 (PRIORITIZED)");
                } else {
                    if (locationRequestParams.plrPrioritizedListIndicator == 0)
                        locationRequestParams.plrPrioritizedListIndicator = PrioritizedListIndicator._NOT_PRIORITIZED;
                    if (locationRequestParams.plrPrioritizedListIndicator == 1)
                        locationRequestParams.plrPrioritizedListIndicator = PrioritizedListIndicator._PRIORITIZED;
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPrioritizedListIndicator argument must be one of 0 (NOT_PRIORITIZED) or 1 (PRIORITIZED)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        // Check of deferred location type parameters consistency with SLg Location Type
        if (locationRequestParams.plrSlgLocationType != null) {
            if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                if (locationRequestParams.plrDeferredLocationType != null) {
                    if ((locationRequestParams.plrAreaType != null && locationRequestParams.plrAreaIdentification != null) &&
                        (locationRequestParams.plrMotionEventLinearDistance != null && locationRequestParams.plrMotionEventOccurrenceInfo != null &&
                            locationRequestParams.plrMotionEventIntervalTime != null && locationRequestParams.plrMotionEventMaximumInterval != null &&
                            locationRequestParams.plrMotionEventSamplingInterval != null) && (locationRequestParams.plrPeriodicLDRReportingAmount != null)) {
                        throw new IllegalArgumentException("Area/Motion event info or periodicLDR parameters can not be null when " +
                            "lcsLocationType parameter equals to 3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                    }
                } else {
                    throw new IllegalArgumentException("lcsDeferredLocationType parameter can not be null when " +
                        "lcsLocationType parameter equals to 3 (ACTIVATE_DEFERRED_LOCATION) or 4 (CANCEL_DEFERRED_LOCATION)");
                }
            }
        }

        /*** PLR optional AVP: [ LCS-Codeword ] ***/
        if (httpServletRequest.getParameter("lcsCodeword") != null) {
            locationRequestParams.plrLcsCodeword = httpServletRequest.getParameter("lcsCodeword");
        }

        /*** PLR optional AVP: [ LCS-Privacy-Check-Non-Session ] ***/
        if (httpServletRequest.getParameter("lcsPrivacyCheckNonSession") != null) {
            try {
                locationRequestParams.plrPrivacyCheckNonSession = Integer.valueOf(httpServletRequest.getParameter("lcsPrivacyCheckNonSession"));
                if (locationRequestParams.plrPrivacyCheckNonSession < 0 || locationRequestParams.plrPrivacyCheckNonSession > 4) {
                    throw new IllegalArgumentException("Incorrect lcsPrivacyCheckNonSession argument, must be 0, 1, 2, 3 or 4 for ALLOWED_WITHOUT_NOTIFICATION or ALLOWED_WITH_NOTIFICATION" +
                        "or ALLOWED_IF_NO_RESPONSE or RESTRICTED_IF_NO_RESPONSE or NOT_ALLOWED respectively");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPrivacyCheckNonSession argument, must be 0, 1, 2, 3 or 4 for ALLOWED_WITHOUT_NOTIFICATION or ALLOWED_WITH_NOTIFICATION" +
                    "or ALLOWED_IF_NO_RESPONSE or RESTRICTED_IF_NO_RESPONSE or NOT_ALLOWED respectively";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        /*** PLR optional AVP: [ LCS-Privacy-Check-Session ] ***/
        if (httpServletRequest.getParameter("lcsPrivacyCheckSession") != null) {
            try {
                locationRequestParams.plrPrivacyCheckSession = Integer.valueOf(httpServletRequest.getParameter("lcsPrivacyCheckSession"));
                if (locationRequestParams.plrPrivacyCheckSession < 0 || locationRequestParams.plrPrivacyCheckSession > 4) {
                    throw new IllegalArgumentException("Incorrect lcsPrivacyCheckSession argument, must be 0, 1, 2, 3 or 4 for ALLOWED_WITHOUT_NOTIFICATION, ALLOWED_WITH_NOTIFICATION" +
                        ", ALLOWED_IF_NO_RESPONSE, RESTRICTED_IF_NO_RESPONSE or NOT_ALLOWED respectively");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect lcsPrivacyCheckSession argument, must be 0, 1, 2, 3 or 4 for ALLOWED_WITHOUT_NOTIFICATION, ALLOWED_WITH_NOTIFICATION" +
                    ", ALLOWED_IF_NO_RESPONSE, RESTRICTED_IF_NO_RESPONSE or NOT_ALLOWED respectively";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        /*** PLR optional AVP: [ Service-Selection ] ***/
        if (httpServletRequest.getParameter("lcsApn") != null) {
            locationRequestParams.plrServiceSelection = httpServletRequest.getParameter("lcsApn");
        }

        /*** PLR conditional AVP: [ IMEI ] ***/
        if (httpServletRequest.getParameter("imei") != null) {
            locationRequestParams.targetingIMEI = httpServletRequest.getParameter("imei");
            if (locationRequestParams.targetingIMEI.length() > 15 || locationRequestParams.targetingIMEI.length() < 14) {
                throw new IllegalArgumentException("Incorrect imei length");
            }
        }

        /*** PLR optional AVP: [ PLR-Flags ] ***/
        if (httpServletRequest.getParameter("plrFlags") != null) {
            try {
                locationRequestParams.plrFlags = Long.valueOf(httpServletRequest.getParameter("plrFlags"));
                if (locationRequestParams.plrFlags < 0 || locationRequestParams.plrFlags > 7) {
                    throw new IllegalArgumentException("plrFlags argument must be an integer value for a bit mask for bit 0 (MO-LR-ShortCircuit-Indicator), bit 1 (Optimized-LCS-Proc-Req) and bit 2 (Delayed-Location-Reporting-Support-Indicator)");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "plrFlags argument must be an integer value for a bit mask for bit 0 (MO-LR-ShortCircuit-Indicator), bit 1 (Optimized-LCS-Proc-Req) and bit 2 (Delayed-Location-Reporting-Support-Indicator)";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        if (httpServletRequest.getParameter("lcsCallbackUrl") != null) {
            locationRequestParams.lrrCallbackUrl = httpServletRequest.getParameter("lcsCallbackUrl");
        } else {
            locationRequestParams.lrrCallbackUrl = GmlcPropertiesManagement.getInstance().getLcsUrlCallback();
        }

        return locationRequestParams;
    }


    /***********************************************************************/
    /*** MLP Request for either SS7 MAP or Diameter SLh/SLg/Sh or SUPL  ***/
    /*********************************************************************/

    public LocationRequestParams createLocationRequestParamsFromMLP(LocationRequestParams locationRequestParams, MLPLocationRequest mlpLocationRequest)
        throws IllegalArgumentException {
        String numberFormatException;

        locationRequestParams.targetingMSISDN = mlpLocationRequest.getMsisdn();

        locationRequestParams.targetingIMSI = mlpLocationRequest.getImsi();

        locationRequestParams.targetingIMEI = mlpLocationRequest.getImei();

        locationRequestParams.operation = mlpLocationRequest.getOperation();
        if (locationRequestParams.operation == null) {
            throw new IllegalArgumentException("Invalid operation parameter, can not be null");
        } else if (!locationRequestParams.operation.equalsIgnoreCase("ATI")
            && !locationRequestParams.operation.equalsIgnoreCase("PSI")
            && !locationRequestParams.operation.equalsIgnoreCase("PSL")
            && !locationRequestParams.operation.equalsIgnoreCase("PLR")
            && !locationRequestParams.operation.equalsIgnoreCase("UDR")
            && !locationRequestParams.operation.equalsIgnoreCase("SUPL")) {
            throw new IllegalArgumentException("Invalid operation parameter, must be one of ATI, PSI, PSL for SS7 networks, " +
                "PLR for LTE location services or UDR for the IMS, or SUPL for User Plane Location");
        }

        locationRequestParams.curlToken = mlpLocationRequest.getCurlToken();
        if (locationRequestParams.curlToken == null) {
            throw new IllegalArgumentException("Invalid token parameter, can not be null");
        } else if (locationRequestParams.curlToken.equals(gmlcPropertiesManagement.getCurlToken())) {
            locationRequestParams.curlUser = gmlcPropertiesManagement.getCurlUser();
        } else {
            // if token does not equal the one defined in gmlcPropertiesManagement configuration, then query MongoDB
            try {
                MongoGmlc mongoGmlc = new MongoGmlc(gmlcPropertiesManagement.getMongoHost(), gmlcPropertiesManagement.getMongoPort(), gmlcPropertiesManagement.getMongoDatabase());
                String user = mongoGmlc.queryCurlUser(locationRequestParams.curlToken);
                mongoGmlc.closeMongo();
                if (user != null) {
                    locationRequestParams.curlUser = user;
                } else {
                    throw new IllegalArgumentException("Authentication error, invalid token");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Authentication error, invalid token");
            }
        }

        // Domain Type, only applies for SS7 MAP ATI and PSI or Diameter UDR
        locationRequestParams.domainType = mlpLocationRequest.getDomain();
        if (locationRequestParams.domainType == null) {
            if (locationRequestParams.operation.equalsIgnoreCase("UDR"))
                locationRequestParams.domainType = "ps";
            else
                locationRequestParams.domainType = "cs";
        } else if (!locationRequestParams.domainType.equalsIgnoreCase("CS")
            && !locationRequestParams.domainType.equalsIgnoreCase("PS")) {
            throw new IllegalArgumentException("Incorrect domain argument, must be one of CS or PS");
        }

        // Translation Type, only applies for SS7 operations
        locationRequestParams.translationType = mlpLocationRequest.getTranslationType();
        if (locationRequestParams.translationType != null) {
            try {
                if (locationRequestParams.translationType < 0 || locationRequestParams.translationType > 255) {
                    throw new IllegalArgumentException("Incorrect tt argument, must be a positive integer number between 0 and 255");
                }
            } catch (NumberFormatException nfe) {
                numberFormatException = "Incorrect translation_type value, must be a positive integer number between 0 and 255";
                locationRequestParams.numberFormatException = numberFormatException;
                return locationRequestParams;
            }
        }

        if (mlpLocationRequest.getReportingService() == MLPLocationRequest.ReportingService.Triggered)
            locationRequestParams.reportingService = MLPLocationRequest.ReportingService.Triggered;
        else
            locationRequestParams.reportingService = MLPLocationRequest.ReportingService.Immediate;

        // Only applies for SS7 MAP ATI or PSI operations (flag determining if EPS Location Information is requested/supported)
        locationRequestParams.locationInfoEps = mlpLocationRequest.getLocationInfoEps();
        if (locationRequestParams.locationInfoEps == null) {
            locationRequestParams.locationInfoEps = "true";
        } else if (!locationRequestParams.locationInfoEps.equals("true") && !locationRequestParams.locationInfoEps.equals("false")) {
            throw new IllegalArgumentException("Incorrect locationInfoEps argument, must be one of true or false");
        }

        locationRequestParams.atiExtraInfoRequested = mlpLocationRequest.getAtiExtraRequestedInfo();
        if (locationRequestParams.atiExtraInfoRequested == null) {
            locationRequestParams.atiExtraInfoRequested = "true";
        } else if (!locationRequestParams.atiExtraInfoRequested.equals("true") && !locationRequestParams.atiExtraInfoRequested.equals("false")) {
            throw new IllegalArgumentException("Incorrect locationInfoEps argument, must be one of true or false");
        }

        // MAP SRI-PSI
        locationRequestParams.psiServiceType = mlpLocationRequest.getPsiServiceType();
        if (locationRequestParams.psiServiceType == null) {
            locationRequestParams.psiServiceType = "useSriSm";
        } else if (!locationRequestParams.operation.equalsIgnoreCase("PSI")) {
            throw new IllegalArgumentException("Incorrect use of psiServiceType argument, it only applies for PSI operation");
        } else if (!locationRequestParams.psiServiceType.equalsIgnoreCase("useSri") &&
            !locationRequestParams.psiServiceType.equalsIgnoreCase("useSriSm")) {
            throw new IllegalArgumentException("Incorrect psiServiceType argument, must be one of useSriSm or useSri");
        }

        // targeting MSISDN or IMSI for MAP PSL or Diameter PLR or Diameter UDR
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (locationRequestParams.targetingMSISDN == null && locationRequestParams.targetingIMSI == null) {
                throw new IllegalArgumentException("One of MSISDN or IMSI is mandatory in msid type value for MAP SRILCS-PSL");
            }
        } // targeting MSISDN or IMSI for Diameter PLR or Diameter UDR
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            if (locationRequestParams.targetingMSISDN == null && locationRequestParams.targetingIMSI == null) {
                throw new IllegalArgumentException("One of MSISDN or IMSI is mandatory in msid type value for Diameter RIR-PLR");
            }
        } // targeting MSISDN Diameter UDR
        else if (locationRequestParams.operation.equalsIgnoreCase("UDR")) {
            locationRequestParams.udrMsisdn = locationRequestParams.targetingMSISDN;
            if (locationRequestParams.udrMsisdn == null) {
                throw new IllegalArgumentException("MSISDN is mandatory msid type value for Sh UDR)");
            }
        }

        // LCS Client Type (M) for MAP PSL or Diameter PLR
        // lcsClientType = valueAddedServices :
        // lcsClientExternalID (M), lcsClientInternalID (NA), lcsClientName (M), lcsClientDialedByMS (O*), lcsRequestorID (O)
        // lcsClientDialedByMS (O*) : This component shall be present if the MT-LR is associated to either CS call or PS session.
        // If the MT-LR is associated with the CS call, the number dialled by UE is used.
        // Otherwise if the MT-LR is associated with the PS session, the APN-NI is used
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getLcsClientType() != null) {
                locationRequestParams.pslLcsClientType = mlpLocationRequest.getLcsClientType().getType();
                try {
                    int pslLcsClientType = locationRequestParams.pslLcsClientType;
                    if (pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.emergencyServices.getType() &&
                        pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.valueAddedServices.getType() &&
                        pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.plmnOperatorServices.getType() &&
                        pslLcsClientType != org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.lawfulInterceptServices.getType()) {
                        throw new IllegalArgumentException("Incorrect lcs_client_type value, must be one of EMERGENCY_SERVICES, VALUE_ADDED_SERVICES, " +
                            "PLMN_OPERATOR_SERVICES or LAWFUL_INTERCEPT_SERVICES for MAP PSL");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect lcs_client_type value, must be one of EMERGENCY_SERVICES, VALUE_ADDED_SERVICES, " +
                        "PLMN_OPERATOR_SERVICES or LAWFUL_INTERCEPT_SERVICES for MAP PSL";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("Incorrect lcs_client_type value, must be one of EMERGENCY_SERVICES, VALUE_ADDED_SERVICES, " +
                    "PLMN_OPERATOR_SERVICES or LAWFUL_INTERCEPT_SERVICES for MAP PSL");
            }
        } else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            /*** PLR mandatory AVP: { LCS-Client-Type } ***/
            // The LCS-Client-Type AVP (AVP code 1241) is of type Enumerated and contains the type of services requested by the LCS Client.
            // It can be one of the following values: 0 EMERGENCY_SERVICES, 1 VALUE_ADDED_SERVICES, 2 PLMN_OPERATOR_SERVICES, 3 LAWFUL_INTERCEPT_SERVICES
            if (mlpLocationRequest.getLcsClientType() != null) {
                locationRequestParams.plrLcsClientType = mlpLocationRequest.getLcsClientType().getType();
                try {
                    int plrLcsClientType = locationRequestParams.plrLcsClientType;
                    if (plrLcsClientType != LCSClientType._EMERGENCY_SERVICES &&
                        plrLcsClientType != LCSClientType._VALUE_ADDED_SERVICES &&
                        plrLcsClientType != LCSClientType._PLMN_OPERATOR_SERVICES &&
                        plrLcsClientType != LCSClientType._LAWFUL_INTERCEPT_SERVICES) {
                        throw new IllegalArgumentException("LCS-Client-Type AVP is mandatory in PLR, lcs_client_type must be one of EMERGENCY_SERVICES, " +
                            "VALUE_ADDED_SERVICES, PLMN_OPERATOR_SERVICES or LAWFUL_INTERCEPT_SERVICES");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "LCS-Client-Type AVP is mandatory in PLR, lcs_client_type must be one of EMERGENCY_SERVICES, " +
                        "VALUE_ADDED_SERVICES, PLMN_OPERATOR_SERVICES or LAWFUL_INTERCEPT_SERVICES";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("LCS-Client-Type AVP is mandatory in PLR, lcs_client_type must be one of EMERGENCY_SERVICES, " +
                    "VALUE_ADDED_SERVICES, PLMN_OPERATOR_SERVICES or LAWFUL_INTERCEPT_SERVICES");
            }
        }

        // LCS Location Estimate Type for MAP PSL or Diameter PLR
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getLocationEstimateType() != null) {
                locationRequestParams.pslLocationEstimateType = String.valueOf(mlpLocationRequest.getLocationEstimateType());
                if (!locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.currentOrLastKnownLocation)) &&
                    !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.initialLocation)) &&
                    !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.currentLocation)) &&
                    !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) &&
                    !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation)) &&
                    !locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.notificationVerificationOnly))) {
                    throw new IllegalArgumentException("Incorrect loc_type type value for PSL, must be one of CURRENT, CURRENT_OR_LAST, LAST_OR_CURRENT, " +
                        "INITIAL, LAST or CURRENT_AND_INTERMEDIATE");
                }
            } else {
                throw new IllegalArgumentException("Incorrect loc_type type value for PSL, must be one of CURRENT, CURRENT_OR_LAST, LAST_OR_CURRENT, " +
                    "INITIAL, LAST or CURRENT_AND_INTERMEDIATE");
            }
        } // LCS Location Estimate Type for Diameter PLR
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            if (mlpLocationRequest.getLocationEstimateType() != null) {
                locationRequestParams.plrSlgLocationType = mlpLocationRequest.getLocationEstimateType().getType();
                try {
                    long plrSlgLocationType = locationRequestParams.plrSlgLocationType;
                    if (plrSlgLocationType != SLgLocationType._CURRENT_LOCATION &&
                        plrSlgLocationType != SLgLocationType._CURRENT_OR_LAST_KNOWN_LOCATION &&
                        plrSlgLocationType != SLgLocationType._INITIAL_LOCATION &&
                        plrSlgLocationType != SLgLocationType._ACTIVATE_DEFERRED_LOCATION &&
                        plrSlgLocationType != SLgLocationType._CANCEL_DEFERRED_LOCATION &&
                        plrSlgLocationType != SLgLocationType._NOTIFICATION_VERIFICATION_ONLY) {
                        throw new IllegalArgumentException("Incorrect loc_type type value for PSL, must be one of CURRENT, " +
                                "CURRENT_OR_LAST, LAST_OR_CURRENT, " + "INITIAL, LAST or CURRENT_AND_INTERMEDIATE");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect loc_type type value for PSL, must be one of CURRENT, CURRENT_OR_LAST, LAST_OR_CURRENT, " +
                        "INITIAL, LAST or CURRENT_AND_INTERMEDIATE";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("Incorrect loc_type type value for PLR, must be one of CURRENT, CURRENT_OR_LAST, LAST_OR_CURRENT, " +
                    "INITIAL, LAST or CURRENT_AND_INTERMEDIATE");
            }
        }

        // GMLC Client Reference Number for MAP PSL
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            locationRequestParams.pslLcsReferenceNumber = mlpLocationRequest.getClientReferenceNumber();
            if (locationRequestParams.pslLcsReferenceNumber != null) {
                try {
                    int clientReferenceNumber = locationRequestParams.pslLcsReferenceNumber;
                    if (clientReferenceNumber < 0) {
                        throw new IllegalArgumentException("trans_id value, must be a valid integer value");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect trans_id value, must be an integer number";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("trans_id value must not be null");
            }
        } // GMLC Client Reference Number for Diameter PLR
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            locationRequestParams.plrLcsReferenceNumber = mlpLocationRequest.getClientReferenceNumber();
            if (locationRequestParams.plrLcsReferenceNumber != null) {
                try {
                    int clientReferenceNumber = locationRequestParams.plrLcsReferenceNumber;
                    if (clientReferenceNumber < 0) {
                        throw new IllegalArgumentException("trans_id value must be a valid integer value");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "trans_id argument must be a valid integer value";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("trans_id value can not be null, must be a valid integer value");
            }
        }
        else if (locationRequestParams.operation.equalsIgnoreCase("SUPL")) {
            locationRequestParams.suplTransactionId = mlpLocationRequest.getClientReferenceNumber();
            if (locationRequestParams.suplTransactionId != null) {
                try {
                    int transactionId = locationRequestParams.suplTransactionId;
                    if (transactionId < 0) {
                        throw new IllegalArgumentException("trans_id value must be a valid integer value");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "trans_id argument must be a valid integer value";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("trans_id value can not be null, must be a valid integer value");
            }
        }

        // Client Name for MAP PSL
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            locationRequestParams.pslClientName = mlpLocationRequest.getLcsClientName();
            locationRequestParams.pslClientFormatIndicator = mlpLocationRequest.getLcsClientNameFormatIndicator();
            if (locationRequestParams.pslClientName != null) {
                if (locationRequestParams.pslClientFormatIndicator != null) {
                    try {
                        int pslClientFormatIndicator = locationRequestParams.pslClientFormatIndicator;
                        if (pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.logicalName.getIndicator() &&
                            pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.emailAddress.getIndicator() &&
                            pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.msisdn.getIndicator() &&
                            pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.url.getIndicator() &&
                            pslClientFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.sipUrl.getIndicator()) {
                            throw new IllegalArgumentException("Incorrect lcs_client_name_fi value, must be one of NAME, E-MAIL, MSISDN, URL or SIPURL");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "Incorrect lcs_client_name_fi value, must be one of NAME, E-MAIL, MSISDN, URL or SIPURL";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("lcs_client_name_fi value cannot be null when lcs_client_name is provided");
                }
            } else if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.valueAddedServices.getType()) {
                throw new IllegalArgumentException("lcs_client_name cannot be null when lcs_client_type equals (value-added services)");
            }
        }
        /*** PLR mandatory AVP: { LCS-EPS-Client-Name } ***/
        // LCS-EPS-Client-Name ::= <AVP header: 2501 10415>
        //      [ LCS-Name-String ]
        //      [ LCS-Format-Indicator ]
        // [ LCS-Name-String ] AVP of mandatory grouped { LCS-EPS-Client-Name } AVP
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            locationRequestParams.plrLcsNameString = mlpLocationRequest.getLcsClientName();
            locationRequestParams.plrLcsFormatInd = mlpLocationRequest.getLcsClientNameFormatIndicator();
            if (locationRequestParams.plrLcsNameString == null) {
                throw new IllegalArgumentException("lcs_client_name cannot be null");
            }
            // [ LCS-Format-Indicator ] AVP of mandatory grouped { LCS-EPS-Client-Name } AVP
            // It can be one of the following values: 0 (LOGICAL_NAME), 1 (EMAIL_ADDRESS), 2 (MSISDN), 3 URL, 4 SIP_URL
            if (locationRequestParams.plrLcsFormatInd != null) {
                try {
                    int plrLcsFormatInd = locationRequestParams.plrLcsFormatInd;
                    if (plrLcsFormatInd != LCSFormatIndicator._LOGICAL_NAME &&
                        plrLcsFormatInd != LCSFormatIndicator._EMAIL_ADDRESS &&
                        plrLcsFormatInd != LCSFormatIndicator._MSISDN &&
                        plrLcsFormatInd != LCSFormatIndicator._URL &&
                        plrLcsFormatInd != LCSFormatIndicator._SIP_URL) {
                        throw new IllegalArgumentException("Incorrect lcs_client_name_fi value, must be one of NAME, E-MAIL, MSISDN, URL or SIPURL");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect lcs_client_name_fi value, must be one of NAME, E-MAIL, MSISDN, URL or SIPURL";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            } else {
                throw new IllegalArgumentException("lcs_client_name cannot be null when lcs_client_name is provided, must be one of NAME, E-MAIL, MSISDN, URL or SIPURL");
            }
        }

        // PSL Client External or Internal IDs
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getLcsClientExternalId() != null) {
                locationRequestParams.pslClientExternalID = mlpLocationRequest.getLcsClientExternalId();
                if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.valueAddedServices.getType() ||
                    locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.emergencyServices.getType()) {
                    // lcsClientType = valueAddedServices :
                    // lcsClientExternalID (M), lcsClientInternalID (NA), lcsClientName (M), lcsClientDialedByMS (O*), lcsRequestorID (O)
                    if (locationRequestParams.pslClientExternalID != null) {
                        try {
                            Long.valueOf(locationRequestParams.pslClientExternalID);
                            if (locationRequestParams.pslClientExternalID.length() > 16) {
                                throw new IllegalArgumentException("lcs_client_external_id represents an ISDN address whose length must not exceed 16 digits");
                            }
                        } catch (NumberFormatException nfe) {
                            numberFormatException = "lcs_client_external_id represents an ISDN address whose length must not exceed 16 digits";
                            locationRequestParams.numberFormatException = numberFormatException;
                            return locationRequestParams;
                        }
                    }
                } else {
                    throw new IllegalArgumentException("lcs_client_external_id value is only valid when lcs_client_type is is VALUE_ADDED_SERVICES or EMERGENCY_SERVICES");
                }
            } else if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.valueAddedServices.getType() ||
                locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.emergencyServices.getType()) {
                throw new IllegalArgumentException("lcs_client_external_id cannot be null when lcsClientType is VALUE_ADDED_SERVICES or EMERGENCY_SERVICES");
            }
            if (mlpLocationRequest.getLcsClientInternalId() != null) {
                if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.plmnOperatorServices.getType()) {
                    try {
                        //lcsClientType = plmnOperatorServices :
                        //lcsClientExternalID (NA), lcsClientInternalID (M), lcsClientName (NA), lcsClientDialedByMS (NA), lcsRequestorID (NA)
                        locationRequestParams.pslClientInternalID = Integer.valueOf(mlpLocationRequest.getLcsClientInternalId());
                        if (locationRequestParams.pslClientInternalID != LCSClientInternalID.broadcastService.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.oandMHPLMN.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.oandMVPLMN.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.anonymousLocation.getId() &&
                            locationRequestParams.pslClientInternalID != LCSClientInternalID.targetMSsubscribedService.getId()) {
                            throw new IllegalArgumentException("Incorrect lcs_client_internal_id value, must be one of 0 (broadcastService), 1 (oandMHPLMN), " +
                                "2 (oandMVPLMN), 3 (anonymousLocation) or 4 (targetMSsubscribedServiceSIP)");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "Incorrect lcs_client_internal_id value, must be one of 0 (broadcastService), 1 (oandMHPLMN), " +
                            "2 (oandMVPLMN), 3 (anonymousLocation) or 4 (targetMSsubscribedServiceSIP)";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("lcs_client_internal_id value is only valid when lcs_client_type is PLMN_OPERATOR_SERVICES");
                }
            } else if (locationRequestParams.pslLcsClientType == org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType.plmnOperatorServices.getType()) {
                throw new IllegalArgumentException("lcs_client_internal_id value cannot be null when lcs_client_type is PLMN_OPERATOR_SERVICES, must be one of 0 (broadcastService), 1 (oandMHPLMN), " +
                    "2 (oandMVPLMN), 3 (anonymousLocation) or 4 (targetMSsubscribedServiceSIP)");
            }
        }

        // Requestor for MAP PSL
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            locationRequestParams.pslRequestorIdString = mlpLocationRequest.getLcsRequestorId();
            locationRequestParams.pslRequestorFormatIndicator = mlpLocationRequest.getLcsRequestorFormatIndicator();
            if (locationRequestParams.pslRequestorIdString != null) {
                if (locationRequestParams.pslRequestorFormatIndicator != null) {
                    try {
                        int pslRequestorFormatIndicator = locationRequestParams.pslRequestorFormatIndicator;
                        if (pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.logicalName.getIndicator() &&
                            pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.emailAddress.getIndicator() &&
                            pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.msisdn.getIndicator() &&
                            pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.url.getIndicator() &&
                            pslRequestorFormatIndicator != org.restcomm.protocols.ss7.map.api.service.lsm.LCSFormatIndicator.sipUrl.getIndicator()) {
                            throw new IllegalArgumentException("Incorrect requestor type, must be one of NAME, E-MAIL, MSISDN, URL or SIP-URL");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "Incorrect requestor type, must be one of NAME, E-MAIL, MSISDN, URL or SIP-URL";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("requestor type cannot be null when requestor is provided");
                }
            }
        } // Requestor for Diameter PLR
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            locationRequestParams.plrLcsRequestorIdString = mlpLocationRequest.getLcsRequestorId();
            locationRequestParams.plrLcsRequestorFormatIndicator = mlpLocationRequest.getLcsRequestorFormatIndicator();
            /*** PLR optional AVP: [ LCS-Requestor-Name ] ***/
            // LCS-Requestor-Name ::= <AVP header: 2502 10415>
            //    [ LCS-Requestor-Id-String ]
            //    [ LCS-Format-Indicator ]
            // [ LCS-Requestor-Id-String ] AVP of optional grouped [ LCS-Requestor-Name ] AVP
            // [ LCS-Format-Indicator ] AVP of optional grouped [ LCS-Requestor-Name ] AVP
            if (locationRequestParams.plrLcsRequestorIdString != null) {
                if (locationRequestParams.plrLcsRequestorFormatIndicator != null) {
                    try {
                        int plrLcsRequestorFormatIndicator = locationRequestParams.plrLcsRequestorFormatIndicator;
                        if (plrLcsRequestorFormatIndicator != LCSFormatIndicator._LOGICAL_NAME &&
                            plrLcsRequestorFormatIndicator != LCSFormatIndicator._EMAIL_ADDRESS &&
                            plrLcsRequestorFormatIndicator != LCSFormatIndicator._MSISDN &&
                            plrLcsRequestorFormatIndicator != LCSFormatIndicator._URL &&
                            plrLcsRequestorFormatIndicator != LCSFormatIndicator._SIP_URL) {
                            throw new IllegalArgumentException("Incorrect requestor type, must be one of NAME, E-MAIL, MSISDN, URL or SIP-URL");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "Incorrect requestor type, must be one of NAME, E-MAIL, MSISDN, URL or SIP-URL";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("requestor type cannot be null when a valid requestor argument is provided");
                }
            }
        }

        // LCS Priority MAP PSL
        if (mlpLocationRequest.getLcsPriority() != null) {
            if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
                locationRequestParams.pslLcsPriority = mlpLocationRequest.getLcsPriority().toString();
                if (!locationRequestParams.pslLcsPriority.equalsIgnoreCase("normalPriority")
                    && !locationRequestParams.pslLcsPriority.equalsIgnoreCase("highestPriority")) {
                    throw new IllegalArgumentException("Incorrect prio type, must be NORMAL or HIGH");
                }
            } // LCS Priority for Diameter PLR
            else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
                locationRequestParams.plrLcsPriority = (long) mlpLocationRequest.getLcsPriority().getCode();
                try {
                    long plrLcsPriority = locationRequestParams.plrLcsPriority;
                    if (plrLcsPriority != 0 && plrLcsPriority != 1) {
                        throw new IllegalArgumentException("Incorrect prio type, must be NORMAL or HIGH");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect prio type, must be NORMAL or HIGH";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
        }

        // LCS QoS for MAP PSL
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getLcsQoS() != null) {
                try {
                    int horizontalAccuracy = mlpLocationRequest.getLcsQoS().getHorizontalAccuracy();
                    if (horizontalAccuracy < 0) {
                        throw new IllegalArgumentException("Incorrect hor_acc value, must be a positive number " +
                            "corresponding to the desired horizontal accuracy in metres");
                    } else {
                        locationRequestParams.pslLcsHorizontalAccuracy = GADShapesUtils.encodeUncertainty(horizontalAccuracy);
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect hor_acc value, must be a positive number " +
                        "corresponding to the desired horizontal accuracy in metres";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
                try {
                    int verticalAccuracy = mlpLocationRequest.getLcsQoS().getVerticalAccuracy();
                    if (verticalAccuracy < 0) {
                        throw new IllegalArgumentException("Incorrect ver_acc value, must be a positive number corresponding " +
                            "to the desired vertical accuracy in metres");
                    } else {
                        locationRequestParams.pslLcsVerticalAccuracy = GADShapesUtils.encodeUncertainty(verticalAccuracy);
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect ver_acc value, must be a positive number corresponding " +
                        "to the desired vertical accuracy in metres";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
                if (mlpLocationRequest.getLcsQoS().getVerticalCoordinateRequest())
                    locationRequestParams.pslVerticalCoordinateRequest = "true";
                else
                    locationRequestParams.pslVerticalCoordinateRequest = "false";

            }
            if (mlpLocationRequest.getResponseTimeCategory() != null) {
                locationRequestParams.pslResponseTimeCategory = String.valueOf(mlpLocationRequest.getResponseTimeCategory());
                if (!locationRequestParams.pslResponseTimeCategory.equalsIgnoreCase(ResponseTimeCategory.delaytolerant.name())
                    && !locationRequestParams.pslResponseTimeCategory.equalsIgnoreCase(ResponseTimeCategory.lowdelay.name())) {
                    throw new IllegalArgumentException("Incorrect responseTime argument, must be delaytolerant or lowdelay");
                }
            }
        } // LCS QoS for Diameter PLR
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            if (mlpLocationRequest.getLcsQoS() != null) {
                try {
                    long horizontalAccuracy = mlpLocationRequest.getLcsQoS().getHorizontalAccuracy();
                    if (horizontalAccuracy < 0) {
                        throw new IllegalArgumentException("Incorrect hor_acc value, must be a positive number " +
                            "corresponding to the desired horizontal accuracy in metres");
                    } else {
                        locationRequestParams.plrHorizontalAccuracy = (long) GADShapesUtils.encodeUncertainty(horizontalAccuracy);
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect hor_acc value, must be a positive number " +
                        "corresponding to the desired horizontal accuracy in metres";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
                try {
                    long verticalAccuracy = mlpLocationRequest.getLcsQoS().getVerticalAccuracy();
                    if (verticalAccuracy < 0) {
                        throw new IllegalArgumentException("Incorrect ver_acc value, must be a positive number corresponding " +
                            "to the desired vertical accuracy in metres");
                    } else {
                        locationRequestParams.plrVerticalAccuracy = (long) GADShapesUtils.encodeUncertainty(verticalAccuracy);
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect ver_acc value, must be a positive number corresponding " +
                        "to the desired vertical accuracy in metres";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
                if (mlpLocationRequest.getLcsQoS().getVerticalCoordinateRequest())
                    locationRequestParams.plrVerticalRequested = VerticalRequested._VERTICAL_COORDINATE_IS_REQUESTED;
                else
                    locationRequestParams.plrVerticalRequested = VerticalRequested._VERTICAL_COORDINATE_IS_NOT_REQUESTED;
            }
            if (mlpLocationRequest.getResponseTimeCategory() != null) {
                locationRequestParams.plrResponseTime = mlpLocationRequest.getResponseTimeCategory().getCategory();
                try {
                    int plrResponseTime = locationRequestParams.plrResponseTime;
                    if (plrResponseTime != net.java.slee.resource.diameter.slg.events.avp.ResponseTime._LOW_DELAY &&
                        plrResponseTime != net.java.slee.resource.diameter.slg.events.avp.ResponseTime._DELAY_TOLERANT) {
                        throw new IllegalArgumentException("Incorrect resp_req type value, must be NO_DELAY, LOW_DELAY or DELAY_TOL");
                    } else {
                        if (plrResponseTime == 0) {
                            locationRequestParams.plrResponseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime._LOW_DELAY;
                        }
                        if (plrResponseTime == 1) {
                            locationRequestParams.plrResponseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime._DELAY_TOLERANT;
                        }
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect resp_req type value, must be NO_DELAY, LOW_DELAY or DELAY_TOL";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
            if (mlpLocationRequest.getLcsQoSClass() != null) {
                locationRequestParams.plrQoSClass = mlpLocationRequest.getLcsQoSClass().getValue();
                try {
                    int plrQoSClass = locationRequestParams.plrQoSClass;
                    if (plrQoSClass != LCSQoSClass._ASSURED &&
                        plrQoSClass != LCSQoSClass._BEST_EFFORT) {
                        throw new IllegalArgumentException("Incorrect qos_class value, must be one of ASSURED) or BEST_EFFORT");
                    } else {
                        if (plrQoSClass == 0) {
                            locationRequestParams.plrQoSClass = LCSQoSClass._ASSURED;
                        } else {
                            locationRequestParams.plrQoSClass = LCSQoSClass._BEST_EFFORT;
                        }
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect qos_class value, must be one of ASSURED) or BEST_EFFORT";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
        } // QoP for SUPL
        else if (locationRequestParams.operation.equalsIgnoreCase("SUPL")) {
            if (mlpLocationRequest.getLcsQoS() != null) {
                try {
                    long horizontalAccuracy = (long) mlpLocationRequest.getLcsQoS().getHorizontalAccuracy();
                    if (horizontalAccuracy < 0) {
                        throw new IllegalArgumentException("Incorrect hor_acc value, must be a positive number " +
                            "corresponding to the desired horizontal accuracy in metres");
                    } else {
                        locationRequestParams.suplHorizontalAccuracy = (long) GADShapesUtils.encodeUncertainty(horizontalAccuracy);
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect hor_acc value, must be a positive number " +
                        "corresponding to the desired horizontal accuracy in metres";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
                try {
                    long verticalAccuracy = (long) mlpLocationRequest.getLcsQoS().getVerticalAccuracy();
                    if (verticalAccuracy < 0) {
                        throw new IllegalArgumentException("Incorrect ver_acc value, must be a positive number corresponding " +
                            "to the desired vertical accuracy in metres");
                    } else {
                        locationRequestParams.suplVerticalAccuracy = (long) GADShapesUtils.encodeUncertainty(verticalAccuracy);
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect ver_acc value, must be a positive number corresponding " +
                        "to the desired vertical accuracy in metres";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
            if (mlpLocationRequest.getResponseTimeCategory() != null) {
                try {
                    long plrResponseTime = mlpLocationRequest.getResponseTimeCategory().getCategory();
                    if (plrResponseTime != 0 && plrResponseTime != 1) {
                        throw new IllegalArgumentException("Incorrect resp_req type value, must be NO_DELAY, LOW_DELAY or DELAY_TOL");
                    } else {
                        if (plrResponseTime == 0) {
                            locationRequestParams.suplResponseTime = 1L;
                        }
                        if (plrResponseTime == 1) {
                            locationRequestParams.suplResponseTime = 128L;
                        }
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect resp_req type value, must be NO_DELAY, LOW_DELAY or DELAY_TOL";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
            if (mlpLocationRequest.getLcsQoSClass() != null) {
                try {
                    int plrQoSClass = mlpLocationRequest.getLcsQoSClass().getValue();
                    if (plrQoSClass != LCSQoSClass._ASSURED &&
                        plrQoSClass != LCSQoSClass._BEST_EFFORT) {
                        throw new IllegalArgumentException("Incorrect qos_class value, must be one of ASSURED) or BEST_EFFORT");
                    } else {
                        if (plrQoSClass == 0) {
                            locationRequestParams.suplMaximumLocationAge = 60L;
                        } else {
                            locationRequestParams.suplMaximumLocationAge = 65535L;
                        }
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect qos_class value, must be one of ASSURED) or BEST_EFFORT";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
        }

        // Service Type Id for PSL (Optional)
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getLcsServiceTypeId() != null) {
                try {
                    locationRequestParams.pslLcsServiceTypeID = Integer.valueOf(mlpLocationRequest.getLcsServiceTypeId());
                    if (locationRequestParams.pslLcsServiceTypeID > 127 || locationRequestParams.pslLcsServiceTypeID < 0) {
                        throw new IllegalArgumentException("Incorrect lcsServiceTypeId argument, must be a positive integer value equal or higher than 0 and lower than 128");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect lcs_service_type_id value, must be an integer value equal or higher than 0 and lower than 128";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
        } // Service Type Id for PLR (Optional)
        else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            if (mlpLocationRequest.getLcsServiceTypeId() != null) {
                try {
                    locationRequestParams.plrLcsServiceTypeId = Long.valueOf(mlpLocationRequest.getLcsServiceTypeId());
                    if (locationRequestParams.plrLcsServiceTypeId > 127 || locationRequestParams.plrLcsServiceTypeId < 0) {
                        throw new IllegalArgumentException("Incorrect lcs_service_type_id value, must be a positive integer value equal or higher than 0 and lower than 128");
                    }
                } catch (NumberFormatException nfe) {
                    numberFormatException = "Incorrect lcs_service_type_id value, must be a positive integer value equal or higher than 0 and lower than 128";
                    locationRequestParams.numberFormatException = numberFormatException;
                    return locationRequestParams;
                }
            }
        }

        // Deferred Location Event Type for PSL or PLR
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getDeferredLocationEventType() != null) {
                if (mlpLocationRequest.getDeferredLocationEventType() == 1L)
                    locationRequestParams.pslDeferredLocationEventType = "available";
                else if (mlpLocationRequest.getDeferredLocationEventType() == 2L)
                    locationRequestParams.pslDeferredLocationEventType = "entering";
                else if (mlpLocationRequest.getDeferredLocationEventType() == 4L)
                    locationRequestParams.pslDeferredLocationEventType = "leaving";
                else if (mlpLocationRequest.getDeferredLocationEventType() == 8L)
                    locationRequestParams.pslDeferredLocationEventType = "inside";
                else if (mlpLocationRequest.getDeferredLocationEventType() == 16L)
                    locationRequestParams.pslDeferredLocationEventType = "periodicLDR";

                if (locationRequestParams.pslDeferredLocationEventType != null) {
                    if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                        locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                        if (!locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("inside") &&
                            !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("entering") &&
                            !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("leaving") &&
                            !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("available") &&
                            !locationRequestParams.pslDeferredLocationEventType.equalsIgnoreCase("periodicLDR")) {
                            throw new IllegalArgumentException("Incorrect ms_action or change_area type value for PSL, must be one of MS_AVAIL (ms_action), MS_ENTERING, MS_OUTSIDE_AREA, " +
                                "MS_LEAVING, MS_WITHIN_AREA (change_area) or PERIODIC");
                        }
                    } else {
                        throw new IllegalArgumentException("Incorrect ms_action or change_area type value for PSL, must be one of MS_AVAIL (ms_action), MS_ENTERING, MS_OUTSIDE_AREA, " +
                            "MS_LEAVING, MS_WITHIN_AREA (change_area) or PERIODIC");
                    }
                } else {
                    if (locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.activateDeferredLocation)) ||
                        locationRequestParams.pslLocationEstimateType.equalsIgnoreCase(String.valueOf(LocationEstimateType.cancelDeferredLocation))) {
                        throw new IllegalArgumentException("ms_action or change_area type value cannot be null if loc_type type equals " +
                            "CURRENT_AND_INTERMEDIATE");
                    }
                }
            }
        } else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            locationRequestParams.plrDeferredLocationType = mlpLocationRequest.getDeferredLocationEventType();
            if (locationRequestParams.plrDeferredLocationType != null) {
                if (locationRequestParams.plrSlgLocationType == 3 || locationRequestParams.plrSlgLocationType == 4) {
                    try {
                        long plrDeferredLocationType = locationRequestParams.plrDeferredLocationType;
                        if (plrDeferredLocationType != 1 && plrDeferredLocationType != 2 &&
                            plrDeferredLocationType != 4 && plrDeferredLocationType != 8 &&
                            plrDeferredLocationType != 16 && plrDeferredLocationType != 32 &&
                            plrDeferredLocationType != 64 && plrDeferredLocationType != 128) {
                            throw new IllegalArgumentException("Incorrect ms_action or change_area type value for PLR, must be one of MS_AVAIL (ms_action), MS_ENTERING, MS_OUTSIDE_AREA, " +
                                "MS_LEAVING, MS_WITHIN_AREA (change_area) or PERIODIC, or an equidistant_event must exist");
                        }
                    } catch (NumberFormatException nfe) {
                        numberFormatException = "Incorrect ms_action or change_area type value for PLR, must be one of MS_AVAIL (ms_action), MS_ENTERING, MS_OUTSIDE_AREA, " +
                            "MS_LEAVING, MS_WITHIN_AREA (change_area) or PERIODIC, or an equidistant_event must exist";
                        locationRequestParams.numberFormatException = numberFormatException;
                        return locationRequestParams;
                    }
                } else {
                    throw new IllegalArgumentException("An equidistant_event must exist or ms_action or change_area type value cannot be null if loc_type type " +
                        "equals CURRENT_AND_INTERMEDIATE");
                }
            }
        }

        // Deferred Location Event for MAP PSL, Diameter PLR (Area, Motion or Periodic-LDR) or SUPL (Area or Periodic)
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            if (mlpLocationRequest.getAreaType() != null) {
                locationRequestParams.pslAreaType = mlpLocationRequest.getAreaType();
                locationRequestParams.pslAreaId = mlpLocationRequest.getAreaId();
                if (locationRequestParams.pslAreaId != null) {
                    String pslAreaIdTbcd = setAreaIdTbcd(locationRequestParams.pslAreaId.split("-"), locationRequestParams.pslAreaType);
                    if (pslAreaIdTbcd.equalsIgnoreCase("Invalid")) {
                        throw new IllegalArgumentException("Invalid target_area arguments for the provided change_area type");
                    }
                } else {
                    if (locationRequestParams.pslAreaType == null) {
                        throw new IllegalArgumentException("change_area type argument cannot be null when a valid target_area is provided");
                    }
                }
                if (mlpLocationRequest.getOccurrenceInfo() == OccurrenceInfo.oneTimeEvent)
                    locationRequestParams.pslOccurrenceInfo = "oneTimeEvent";
                else if (mlpLocationRequest.getOccurrenceInfo() == OccurrenceInfo.multipleTimeEvent) {
                    locationRequestParams.pslOccurrenceInfo = "multipleTimeEvent";
                    locationRequestParams.pslIntervalTime = mlpLocationRequest.getIntervalTime();
                }
            } else if (mlpLocationRequest.getReportingInterval() != null && mlpLocationRequest.getReportingAmount() != null) {
                locationRequestParams.pslReportingInterval = mlpLocationRequest.getReportingInterval();
                locationRequestParams.pslReportingAmount = mlpLocationRequest.getReportingAmount();
            }
        } else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            if (mlpLocationRequest.getAreaType() != null) {
                if (mlpLocationRequest.getAreaType().equalsIgnoreCase("countryCode"))
                    locationRequestParams.plrAreaType = 0L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("plmnId"))
                    locationRequestParams.plrAreaType = 1L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("locationAreaId"))
                    locationRequestParams.plrAreaType = 2L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("routingAreaId"))
                    locationRequestParams.plrAreaType = 3L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("cellGlobalId"))
                    locationRequestParams.plrAreaType = 4L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("utranCellId"))
                    locationRequestParams.plrAreaType = 5L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("trackingAreaId"))
                    locationRequestParams.plrAreaType = 6L;
                else if (mlpLocationRequest.getAreaType().equalsIgnoreCase("eUtranCellId"))
                    locationRequestParams.plrAreaType = 7L;
                locationRequestParams.plrAreaIdentification = mlpLocationRequest.getAreaId();
                if (locationRequestParams.plrAreaIdentification != null) {
                    String lcsAreaType = convertAreaTypeToString(locationRequestParams.plrAreaType);
                    String plrAreaIdTbcd = setAreaIdTbcd(locationRequestParams.plrAreaIdentification.split("-"), lcsAreaType);
                    if (plrAreaIdTbcd.equalsIgnoreCase("Invalid")) {
                        throw new IllegalArgumentException("Invalid target_area arguments for the provided change_area type");
                    }
                } else {
                    if (locationRequestParams.plrAreaType == null) {
                        throw new IllegalArgumentException("change_area type argument cannot be null when a valid target_area is provided");
                    }
                }
                if (mlpLocationRequest.getOccurrenceInfo() == OccurrenceInfo.oneTimeEvent)
                    locationRequestParams.plrAreaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT;
                else if (mlpLocationRequest.getOccurrenceInfo() == OccurrenceInfo.multipleTimeEvent) {
                    locationRequestParams.plrAreaEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT;
                    if (mlpLocationRequest.getIntervalTime() != null)
                        locationRequestParams.plrAreaEventIntervalTime = (long) mlpLocationRequest.getIntervalTime();
                    // If not included, the default Maximum-Interval shall be considered as the maximum value.
                    locationRequestParams.plrAreaEventMaxInterval = 86400L;
                    // If not included, the default value of Sampling-Interval shall be considered as the maximum value.
                    locationRequestParams.plrAreaEventSamplingInterval = 3600L;
                }
                if (mlpLocationRequest.getReportingDuration() != null)
                    locationRequestParams.plrAreaEventReportingDuration = (long) mlpLocationRequest.getReportingDuration();
                if (mlpLocationRequest.getReportingLocationReqs() != null)
                    locationRequestParams.plrAreaEventRepLocRequirements = (long) mlpLocationRequest.getReportingLocationReqs();
            } else if (mlpLocationRequest.getMotionEventDistance() != null) {
                locationRequestParams.plrMotionEventLinearDistance = mlpLocationRequest.getMotionEventDistance();
                if (mlpLocationRequest.getOccurrenceInfo() == OccurrenceInfo.oneTimeEvent)
                    locationRequestParams.plrMotionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT;
                else if (mlpLocationRequest.getOccurrenceInfo() == OccurrenceInfo.multipleTimeEvent) {
                    locationRequestParams.plrMotionEventOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT;
                    if (mlpLocationRequest.getIntervalTime() != null)
                        locationRequestParams.plrMotionEventIntervalTime = (long) mlpLocationRequest.getIntervalTime();
                    // If not included, the default Maximum-Interval shall be considered as the maximum value.
                    locationRequestParams.plrMotionEventMaximumInterval = 86400L;
                    // If not included, the default value of Sampling-Interval shall be considered as the maximum value.
                    locationRequestParams.plrMotionEventSamplingInterval = 3600L;
                }
                if (mlpLocationRequest.getReportingDuration() != null)
                    locationRequestParams.plrMotionEvenReportingDuration = (long) mlpLocationRequest.getReportingDuration();
                if (mlpLocationRequest.getReportingLocationReqs() != null)
                    locationRequestParams.plrMotionEvenReportingLocationRequirements = (long) mlpLocationRequest.getReportingLocationReqs();
            } else {
                if (mlpLocationRequest.getReportingInterval() != null && mlpLocationRequest.getReportingAmount() != null) {
                    locationRequestParams.plrPeriodicLDRReportingInterval = (long) mlpLocationRequest.getReportingInterval();
                    locationRequestParams.plrPeriodicLDRReportingAmount = (long) mlpLocationRequest.getReportingAmount();
                }
            }
        } else if (locationRequestParams.operation.equalsIgnoreCase("SUPL")) {
            if (mlpLocationRequest.getReportingService() == MLPLocationRequest.ReportingService.Triggered)
                locationRequestParams.reportingService = MLPLocationRequest.ReportingService.Triggered;
            else
                locationRequestParams.reportingService = MLPLocationRequest.ReportingService.Immediate;
            if (mlpLocationRequest.getSuplTriggerType() != null) {
                if (mlpLocationRequest.getSuplTriggerType() == SuplTriggerType.AreaEvent) {
                    if (mlpLocationRequest.getSuplAreaEventType() != null) {
                        if (mlpLocationRequest.getSuplAreaEventType() == SuplAreaEventType.ENTERING_AREA ||
                            mlpLocationRequest.getSuplAreaEventType() == SuplAreaEventType.INSIDE_AREA ||
                            mlpLocationRequest.getSuplAreaEventType() == SuplAreaEventType.OUTSIDE_AREA ||
                            mlpLocationRequest.getSuplAreaEventType() == SuplAreaEventType.LEAVING_AREA ||
                            mlpLocationRequest.getSuplAreaEventType() == SuplAreaEventType.UNDEFINED) {
                            locationRequestParams.suplAreaEventType = mlpLocationRequest.getSuplAreaEventType().getSuplAreaEventType();
                        } else {
                            throw new IllegalArgumentException("Wrong change_area type argument, must be one of MS_ENTERING, MS_OUTSIDE_AREA, MS_LEAVING, " +
                                "MS_WITHIN_AREA or UNDEFINED");
                        }
                    } else {
                        throw new IllegalArgumentException("change_area type argument cannot be null when valid change_area parameters are provided");
                    }

                    if (mlpLocationRequest.getSuplLocationEstimateRequested() != null) {
                        if (mlpLocationRequest.getSuplLocationEstimateRequested()) {
                            locationRequestParams.suplLocationEstimateRequested = true;
                        }
                    } else {
                        throw new IllegalArgumentException("loc_estimates value cannot be null when valid change_area parameters are provided for SUPL, " +
                            "it must be TRUE or FALSE");
                    }

                    if (mlpLocationRequest.getSuplAreaEventRepeatedReporting()) {
                        locationRequestParams.suplAreaEventRepeatedReporting = true;
                    }

                    if (mlpLocationRequest.getSuplMinimumIntervalTime() != null) {
                        locationRequestParams.suplMinimumIntervalTime = mlpLocationRequest.getSuplMinimumIntervalTime();
                    } else {
                        throw new IllegalArgumentException("minimumIntervalTime argument cannot be null when valid change_area parameters are provided");
                    }

                    if (mlpLocationRequest.getSuplMaximumNumberOfReports() != null) {
                        locationRequestParams.suplMaximumNumberOfReports = mlpLocationRequest.getSuplMaximumNumberOfReports();
                    } else {
                        throw new IllegalArgumentException("no_of_reports argument cannot be null when valid change_area parameters are provided");
                    }

                    if (mlpLocationRequest.getSuplGeographicTargetArea() != null) {
                        if (mlpLocationRequest.getSuplGeographicTargetArea() == SuplGeoTargetArea.CircularArea) {
                            locationRequestParams.suplGeographicTargetArea = SuplGeoTargetArea.CircularArea.name();
                            locationRequestParams.suplAreaEventLatitude = mlpLocationRequest.getSuplAreaEventLatitude();
                            locationRequestParams.suplAreaEventLongitude = mlpLocationRequest.getSuplAreaEventLongitude();
                            locationRequestParams.suplAreaEventRadius = mlpLocationRequest.getSuplAreaEventRadius();
                        } else if (mlpLocationRequest.getSuplGeographicTargetArea() == SuplGeoTargetArea.EllipticalArea) {
                            locationRequestParams.suplGeographicTargetArea = SuplGeoTargetArea.EllipticalArea.name();
                            locationRequestParams.suplAreaEventLatitude = mlpLocationRequest.getSuplAreaEventLatitude();
                            locationRequestParams.suplAreaEventLongitude = mlpLocationRequest.getSuplAreaEventLongitude();
                            locationRequestParams.suplAreaEventSemiMajor = mlpLocationRequest.getSuplAreaEventSemiMajor();
                            locationRequestParams.suplAreaEventSemiMinor = mlpLocationRequest.getSuplAreaEventSemiMinor();
                            locationRequestParams.suplAreaEventAngle = mlpLocationRequest.getSuplAreaEventAngle();
                        } else if (mlpLocationRequest.getSuplGeographicTargetArea() == SuplGeoTargetArea.PolygonArea) {
                            locationRequestParams.suplGeographicTargetArea = SuplGeoTargetArea.PolygonArea.name();
                            // TODO populate each point coordinates
                        }
                    } else {
                        throw new IllegalArgumentException("shape argument cannot be null for SUPL when valid change_area nd target_area parameters " +
                            "are provided");
                    }

                    if (mlpLocationRequest.getSuplStartTime() != null) {
                        locationRequestParams.suplStartTime = mlpLocationRequest.getSuplStartTime();
                        if (mlpLocationRequest.getSuplStopTime() != null) {
                            locationRequestParams.suplStopTime = mlpLocationRequest.getSuplStopTime();
                        }
                    }

                } else if (mlpLocationRequest.getSuplTriggerType() == SuplTriggerType.Periodic) {
                    if (mlpLocationRequest.getSuplStartTime() != null && mlpLocationRequest.getReportingInterval() != null
                        && mlpLocationRequest.getReportingAmount() != null) {
                        locationRequestParams.suplStartTime = mlpLocationRequest.getSuplStartTime();
                        locationRequestParams.suplReportingInterval = mlpLocationRequest.getReportingInterval().longValue();
                        locationRequestParams.suplReportingAmount = mlpLocationRequest.getReportingAmount().longValue();
                    } else {
                        throw new IllegalArgumentException("Periodic params (suplStartTime, suplReportingInterval, suplReportingAmount) " +
                            "cannot be null for SUPL when SuplTriggerType is Periodic");
                    }
                }
            }
        }

        // Callback URL for MAP PSL or Diameter PLR or SUPL
        if (locationRequestParams.operation.equalsIgnoreCase("PSL")) {
            locationRequestParams.slrCallbackUrl = mlpLocationRequest.getLocationReportCallbackUrl();
        } else if (locationRequestParams.operation.equalsIgnoreCase("PLR")) {
            locationRequestParams.lrrCallbackUrl = mlpLocationRequest.getLocationReportCallbackUrl();
        } else if (locationRequestParams.operation.equalsIgnoreCase("SUPL")) {
            locationRequestParams.suplAgentCallbackUrl = mlpLocationRequest.getLocationReportCallbackUrl();
        }

        return locationRequestParams;
    }

}
