package org.mobicents.gmlc.slee.mlp;

import net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass;
import org.mobicents.gmlc.slee.mlp.v3_4.AltAcc;
import org.mobicents.gmlc.slee.mlp.v3_4.Applicationid;
import org.mobicents.gmlc.slee.mlp.v3_4.ChangeArea;
import org.mobicents.gmlc.slee.mlp.v3_4.Client;
import org.mobicents.gmlc.slee.mlp.v3_4.EmeLir;
import org.mobicents.gmlc.slee.mlp.v3_4.Eqop;
import org.mobicents.gmlc.slee.mlp.v3_4.Hdr;
import org.mobicents.gmlc.slee.mlp.v3_4.HorAcc;
import org.mobicents.gmlc.slee.mlp.v3_4.LlAcc;
import org.mobicents.gmlc.slee.mlp.v3_4.LocType;
import org.mobicents.gmlc.slee.mlp.v3_4.Msid;
import org.mobicents.gmlc.slee.mlp.v3_4.Msids;
import org.mobicents.gmlc.slee.mlp.v3_4.Prio;
import org.mobicents.gmlc.slee.mlp.v3_4.Qop;
import org.mobicents.gmlc.slee.mlp.v3_4.Requestmode;
import org.mobicents.gmlc.slee.mlp.v3_4.Requestor;
import org.mobicents.gmlc.slee.mlp.v3_4.RespReq;
import org.mobicents.gmlc.slee.mlp.v3_4.Shape;
import org.mobicents.gmlc.slee.mlp.v3_4.Slir;
import org.mobicents.gmlc.slee.mlp.v3_4.StartTime;
import org.mobicents.gmlc.slee.mlp.v3_4.StopTime;
import org.mobicents.gmlc.slee.mlp.v3_4.Subclient;
import org.mobicents.gmlc.slee.mlp.v3_4.SvcInit;
import org.mobicents.gmlc.slee.mlp.v3_4.TargetArea;
import org.mobicents.gmlc.slee.mlp.v3_4.Tlrr;
import org.mobicents.gmlc.slee.mlp.v3_4.TlrrEvent;
import org.mobicents.gmlc.slee.mlp.v3_4.Tlrsr;
import org.mobicents.gmlc.slee.supl.SuplAreaEventType;
import org.mobicents.gmlc.slee.supl.SuplGeoTargetArea;
import org.mobicents.gmlc.slee.supl.SuplTriggerType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSClientType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPriority;
import org.restcomm.protocols.ss7.map.api.service.lsm.LocationEstimateType;
import org.restcomm.protocols.ss7.map.api.service.lsm.OccurrenceInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;

import javax.slee.facilities.Tracer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.List;

/**
 * Helper class to handle an incoming MLP XML request
 *
 * @author  <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>*
 */
public class MLPRequest {

    /**
     * Logger from the calling SBB
     */
    private final Tracer logger;

    /**
     * Default constructor
     *
     * @param logger Logger from the calling SBB
     */
    public MLPRequest(Tracer logger) {
        this.logger = logger;
    }

    /**
     * Parse incoming XML request data via JAXB Unmarshaller and return only the MSISDN being requested
     *
     * @param requestStream InputStream (likely directly from the HTTP POST) of the XML input data
     * @return MLPLocationRequest of the device to locate
     * @throws MLPException exception when parsing incoming MLP request
     */
    public MLPLocationRequest parse(InputStream requestStream) throws MLPException {
        MLPLocationRequest mlpLocationRequest = null;
        // Process the request
        try {
            // Create the JAXB unmarshalling object
            JAXBContext jc = JAXBContext.newInstance(SvcInit.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            // Unmarshal directly from the POST input stream
            SvcInit svcInit = (SvcInit) unmarshaller.unmarshal(requestStream);

            // Standard Location Immediate Request
            Slir slir = svcInit.getSlir();
            if (slir != null) {
                mlpLocationRequest = new MLPLocationRequest(MLPLocationRequest.ReportingService.Immediate);

                Hdr hdr = svcInit.getHdr();
                if (retrieveValuesFromHdr(mlpLocationRequest, hdr) != null)
                    mlpLocationRequest = retrieveValuesFromHdr(mlpLocationRequest, hdr);

                if (retrieveLocationRequestValuesFromMsisds(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestValuesFromMsisds(mlpLocationRequest, svcInit);

                if (retrieveLocationRequestType(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestType(mlpLocationRequest, svcInit);

                if (retrieveLocationRequestPrio(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestPrio(mlpLocationRequest, svcInit);

                if (retrieveLocationRequestQuality(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestQuality(mlpLocationRequest, svcInit);

                if (slir.getPushaddr() != null) {
                    assert mlpLocationRequest != null;
                    mlpLocationRequest.setLocationReportCallbackUrl(slir.getPushaddr().getUrl());
                }

            }

            // Triggered Location Reporting Request
            Tlrr tlrr = svcInit.getTlrr();
            if (tlrr != null) {
                mlpLocationRequest = new MLPLocationRequest(MLPLocationRequest.ReportingService.Triggered);

                Hdr hdr = svcInit.getHdr();
                if (retrieveValuesFromHdr(mlpLocationRequest, hdr) != null)
                    mlpLocationRequest = retrieveValuesFromHdr(mlpLocationRequest, hdr);

                if (retrieveLocationRequestValuesFromMsisds(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestValuesFromMsisds(mlpLocationRequest, svcInit);

                if (retrieveLocationRequestType(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestType(mlpLocationRequest, svcInit);

                if (retrieveLocationRequestPrio(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestPrio(mlpLocationRequest, svcInit);

                if (retrieveLocationRequestQuality(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveLocationRequestQuality(mlpLocationRequest, svcInit);

                if (retrieveDeferredLocationEventType(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveDeferredLocationEventType(mlpLocationRequest, svcInit);

                if (retrieveEventParams(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveEventParams(mlpLocationRequest, svcInit);

                if (retrieveSuplPeriodicParams(mlpLocationRequest, svcInit) != null)
                    mlpLocationRequest = retrieveSuplPeriodicParams(mlpLocationRequest, svcInit);

                if (tlrr.getPushaddr() != null) {
                    assert mlpLocationRequest != null;
                    mlpLocationRequest.setLocationReportCallbackUrl(tlrr.getPushaddr().getUrl());
                }
            }

            // Emergency Location Immediate Request
            EmeLir elir = svcInit.getEmeLir();
            if (elir != null) {
                // TODO ?
                logger.warning("EmeLir is not null: " + elir);
            }

            // Triggered Location Reporting Stop Request
            Tlrsr tlrsr = svcInit.getTlrsr();
            if (tlrsr != null) {
                // TODO ?
                logger.warning("Tlrsr is not null");
            }

            return mlpLocationRequest;

        } catch (JAXBException e) {
            this.logger.severe("Exception while unmarshalling XML request data: " + e.getMessage());

            // Set a custom error message for delivering directly to the client
            // and throw a new exception
            MLPException mlpException = new MLPException(e.getMessage());
            mlpException.setMlpClientErrorMessage("Invalid XML received: " + e.getMessage());
            mlpException.setMlpClientErrorType(MLPResponse.MLPResultType.FORMAT_ERROR);
            throw mlpException;
        }
    }

    private MLPLocationRequest retrieveValuesFromHdr(MLPLocationRequest mlpLocationRequest, Hdr hdr) {
        if (hdr != null) {
            String id, pwd, serviceId = null;
            String lcsClientType, lcsClientName, lcsClientNameFormatIndicator, lcsClientExternalId, lcsClientInternalId, lcsServiceType;
            List<Object> hdrContent = hdr.getContent();
            for (Object object : hdrContent) {
                if (object.getClass() == Client.class) {
                    // Client id
                    id = ((Client) object).getId();
                    mlpLocationRequest.setSlpClientId(id);
                    // Client pwd
                    pwd = ((Client) object).getPwd();
                    mlpLocationRequest.setCurlToken(pwd);
                    mlpLocationRequest.setSlpClientPwd(pwd);
                    // Client serviceId
                    serviceId = ((Client) object).getServiceid();
                    if (serviceId != null) {
                        if (serviceId.equalsIgnoreCase("0100")) {
                            mlpLocationRequest.setOperation("ATI");
                            mlpLocationRequest.setDomain("cs");
                            mlpLocationRequest.setLocationInfoEps("true");
                            mlpLocationRequest.setAtiExtraRequestedInfo("true");
                        } else if (serviceId.equalsIgnoreCase("0101")) {
                            mlpLocationRequest.setOperation("PSI");
                            mlpLocationRequest.setPsiServiceType("useSriSm");
                            mlpLocationRequest.setDomain("cs");
                            mlpLocationRequest.setLocationInfoEps("true");
                        } else if (serviceId.equalsIgnoreCase("0104")) {
                            mlpLocationRequest.setOperation("PLR");
                        } else if (serviceId.equalsIgnoreCase("0105")) {
                            mlpLocationRequest.setOperation("PSL");
                        } else if (serviceId.equalsIgnoreCase("0106")) {
                            mlpLocationRequest.setOperation("UDR");
                            mlpLocationRequest.setDomain("ps");
                            mlpLocationRequest.setLocationInfoEps("true");
                        } else if (serviceId.equalsIgnoreCase("0201")) {
                            mlpLocationRequest.setOperation("SUPL");
                            mlpLocationRequest.setSlpClientServiceId(serviceId);
                        }
                    }
                    lcsClientType = ((Client) object).getLcsClientType();
                    if (lcsClientType != null) {
                        switch (lcsClientType) {
                            case "EMERGENCY_SERVICES":
                                mlpLocationRequest.setLcsClientType(LCSClientType.emergencyServices);
                                break;
                            case "VALUE_ADDED_SERVICES":
                                mlpLocationRequest.setLcsClientType(LCSClientType.valueAddedServices);
                                break;
                            case "PLMN_OPERATOR_SERVICES":
                                mlpLocationRequest.setLcsClientType(LCSClientType.plmnOperatorServices);
                                break;
                            case "LAWFUL_INTERCEPT_SERVICES":
                                mlpLocationRequest.setLcsClientType(LCSClientType.lawfulInterceptServices);
                                break;
                        }
                    }
                    lcsClientName = ((Client) object).getLcsClientName();
                    lcsClientNameFormatIndicator = ((Client) object).getLcsClientNameFormatIndicator();
                    if (lcsClientName != null && lcsClientNameFormatIndicator != null) {
                        mlpLocationRequest.setLcsClientName(lcsClientName);
                        switch (lcsClientNameFormatIndicator) {
                            case "NAME":
                                mlpLocationRequest.setLcsClientNameFormatIndicator(0);
                                break;
                            case "E-MAIL":
                                mlpLocationRequest.setLcsClientNameFormatIndicator(1);
                                break;
                            case "MSISDN":
                                mlpLocationRequest.setLcsClientNameFormatIndicator(2);
                                break;
                            case "URL":
                                mlpLocationRequest.setLcsClientNameFormatIndicator(3);
                                break;
                            case "SIPURL":
                                mlpLocationRequest.setLcsClientNameFormatIndicator(4);
                                break;
                        }
                    }
                    if (serviceId != null) {
                        if (serviceId.equalsIgnoreCase("0105")) {
                            lcsClientExternalId = ((Client) object).getLcsClientExternalId();
                            mlpLocationRequest.setLcsClientExternalId(lcsClientExternalId);
                            lcsClientInternalId = ((Client) object).getLcsClientInternalId();
                            mlpLocationRequest.setLcsClientInternalId(lcsClientInternalId);
                        }
                        if (serviceId.equalsIgnoreCase("0104") || serviceId.equalsIgnoreCase("0105")) {
                            lcsServiceType = ((Client) object).getLcsServiceTypeId();
                            mlpLocationRequest.setLcsServiceTypeId(lcsServiceType);
                        }
                    }
                    // Client RequestMode
                    Requestmode requestMode = ((Client) object).getRequestmode();
                    if (requestMode != null) {
                        mlpLocationRequest.setSlpClientRequestModeType(requestMode.getType());
                    }
                    // Client ApplicationId
                    Applicationid applicationid = ((Client) object).getApplicationid();
                    if (applicationid != null) {
                        try {
                            mlpLocationRequest.setSlpClientApplicationName(applicationid.getAppName());
                            mlpLocationRequest.setSlpClientApplicationProvider(applicationid.getAppProvider());
                            mlpLocationRequest.setSlpClientApplicationVersion(applicationid.getAppVersion());
                        } catch (Exception e) {
                            logger.warning("Exception when getting Client application name and provider values for MLP: " + e.getMessage());
                        }
                    }
                }
                if (object.getClass() == Subclient.class) {
                    try {
                        mlpLocationRequest.setSlpSubClientId(((Subclient) object).getId());
                        mlpLocationRequest.setSlpSubClientPwd(((Subclient) object).getPwd());
                        mlpLocationRequest.setSlpSubClientServiceId(((Subclient) object).getServiceid());
                        mlpLocationRequest.setSlpSubClientLastClient(((Subclient) object).getLastClient());
                    } catch (Exception e) {
                        logger.warning("Exception when getting Sub Client id and serviceID values for MLP: " + e.getMessage());
                    }
                }
                if (object.getClass() == Requestor.class) {
                    try {
                        String requestorId = ((Requestor) object).getId();
                        String requestorType = ((Requestor) object).getType();
                        if (requestorId != null && requestorType != null) {
                            mlpLocationRequest.setLcsRequestorId(requestorId);
                            if (serviceId != null) {
                                if (serviceId.equalsIgnoreCase("0104") || serviceId.equalsIgnoreCase("0105")) {
                                    switch (requestorType) {
                                        case "NAME":
                                            mlpLocationRequest.setLcsRequestorFormatIndicator(0);
                                            break;
                                        case "E-MAIL":
                                            mlpLocationRequest.setLcsRequestorFormatIndicator(1);
                                            break;
                                        case "MSISDN":
                                            mlpLocationRequest.setLcsRequestorFormatIndicator(2);
                                            break;
                                        case "URL":
                                            mlpLocationRequest.setLcsRequestorFormatIndicator(3);
                                            break;
                                        case "SIPURL":
                                            mlpLocationRequest.setLcsRequestorFormatIndicator(4);
                                            break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warning("Exception when getting Requestor id and type values for MLP: " + e.getMessage());
                    }
                }
            }
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveLocationRequestValuesFromMsisds(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        Msids msids;
        if (svcInit.getSlir() != null) {
            Slir slir = svcInit.getSlir();
            msids = slir.getMsids();
            if (msids == null)
                return null;
        } else if (svcInit.getTlrr() != null) {
            Tlrr tlrr = svcInit.getTlrr();
            msids = tlrr.getMsids();
            if (msids == null)
                return null;
        } else {
            return null;
        }
        List<Object> msidAndCodewordAndSession = msids.getMsidAndCodewordAndSession();
        for (Object msidAndCodewordAndSessionObject : msidAndCodewordAndSession) {
            if (msidAndCodewordAndSessionObject.getClass() == Msid.class) {
                Msid msid = (((Msid) msidAndCodewordAndSessionObject));
                if (msid.getType().equalsIgnoreCase("MSISDN")) {
                    mlpLocationRequest.setMsisdn(((Msid) msidAndCodewordAndSessionObject).getContent());
                } else if (msid.getType().equalsIgnoreCase("IMSI")) {
                    mlpLocationRequest.setImsi(((Msid) msidAndCodewordAndSessionObject).getContent());
                }
                if (msid.getType().equalsIgnoreCase("IMEI"))
                    mlpLocationRequest.setImei(((Msid) msidAndCodewordAndSessionObject).getContent());
            }
            if (msidAndCodewordAndSessionObject.getClass() == JAXBElement.class) {
                try {
                    if (((JAXBElement<?>) msidAndCodewordAndSessionObject).getName().toString().equalsIgnoreCase("trans_id")) {
                        Integer trans_id =  Integer.valueOf(((JAXBElement<?>) msidAndCodewordAndSessionObject).getValue().toString());
                        mlpLocationRequest.setClientReferenceNumber(trans_id);
                    }
                    if (((JAXBElement<?>) msidAndCodewordAndSessionObject).getName().toString().equalsIgnoreCase("codeword")) {
                        String codeword = ((JAXBElement<?>) msidAndCodewordAndSessionObject).getValue().toString();
                        logger.info("codeword: " + codeword);
                        // TODO define what to do with this value
                        //mlpLocationRequest.set?;
                    }
                } catch (NumberFormatException nfe) {
                    logger.warning("NumberFormatException when getting trans_id value for MLP SLIR: " + nfe.getMessage());
                } catch (Exception e) {
                    logger.warning("Exception when getting trans_id value for MLP SLIR: " + e.getMessage());
                }
            }
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveLocationRequestType(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        LocType locType;
        if (svcInit.getSlir() != null) {
            Slir slir = svcInit.getSlir();
            locType = slir.getLocType();
        } else if (svcInit.getTlrr() != null) {
            Tlrr tlrr = svcInit.getTlrr();
            locType = tlrr.getLocType();
        } else {
            return null;
        }
        if (locType != null) {
            switch (locType.getType()) {
                case "CURRENT":
                    mlpLocationRequest.setLocationEstimateType(LocationEstimateType.currentLocation);
                    break;
                case "INITIAL":
                    mlpLocationRequest.setLocationEstimateType(LocationEstimateType.initialLocation);
                    break;
                case "CURRENT_OR_LAST":
                case "LAST":
                    mlpLocationRequest.setLocationEstimateType(LocationEstimateType.currentOrLastKnownLocation);
                    break;
                case "CURRENT_AND_INTERMEDIATE":
                    // kind of a hack to enable triggering LocationEstimateType.activateDeferredLocation for PSL/PLR
                    mlpLocationRequest.setLocationEstimateType(LocationEstimateType.activateDeferredLocation);
                    break;
                default:
                    // kind of a hack to enable triggering LocationEstimateType.cancelDeferredLocation for PSL/PLR
                    mlpLocationRequest.setLocationEstimateType(LocationEstimateType.cancelDeferredLocation);
                    break;
            }
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveLocationRequestPrio(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        Prio prio;
        if (svcInit.getSlir() != null) {
            Slir slir = svcInit.getSlir();
            prio = slir.getPrio();
        } else if (svcInit.getTlrr() != null) {
            Tlrr tlrr = svcInit.getTlrr();
            prio = tlrr.getPrio();
        } else {
            return null;
        }
        if (prio != null) {
            switch (prio.getType()) {
                case "NORMAL":
                    mlpLocationRequest.setLcsPriority(LCSPriority.normalPriority);
                    break;
                case "HIGH":
                    mlpLocationRequest.setLcsPriority(LCSPriority.highestPriority);
                    break;
            }
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveLocationRequestQuality(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        Eqop eqop;
        Qop qop = null;
        HorAcc horAcc;
        AltAcc altAcc ;
        RespReq respReq = null;
        LlAcc llAcc;
        Integer horizontalAccuracy = null, verticalAccuracy = null;
        boolean verticalCoordinateRequest = false;
        ResponseTimeCategory responseTimeCategory = null;
        LCSQoSClass lcsQoSClass = LCSQoSClass.BEST_EFFORT;
        if (svcInit.getSlir() != null) {
            Slir slir = svcInit.getSlir();
            eqop = slir.getEqop();
            if (eqop != null) {
                horAcc = eqop.getHorAcc();
                altAcc = eqop.getAltAcc();
                respReq = eqop.getRespReq();
                llAcc = eqop.getLlAcc();
            } else {
                return null;
            }
        } else if (svcInit.getTlrr() != null) {
            Tlrr tlrr = svcInit.getTlrr();
            qop = tlrr.getQop();
            if (qop != null) {
                horAcc = qop.getHorAcc();
                altAcc = qop.getAltAcc();
                llAcc = qop.getLlAcc();
            } else {
                return null;
            }
        } else {
            return null;
        }
        if (horAcc != null) {
            horizontalAccuracy = Integer.parseInt(horAcc.getContent());
        }
        if (altAcc != null) {
            verticalAccuracy = Integer.parseInt(altAcc.getContent());
        }
        if (verticalAccuracy != null)
            verticalCoordinateRequest = true;
        if (respReq != null) {
            switch (respReq.getType()) {
                case "NO_DELAY":
                case "LOW_DELAY":
                    responseTimeCategory = ResponseTimeCategory.lowdelay;
                    mlpLocationRequest.setResponseTimeCategory(responseTimeCategory);
                    break;
                case "DELAY_TOL":
                default:
                    responseTimeCategory = ResponseTimeCategory.delaytolerant;
                    mlpLocationRequest.setResponseTimeCategory(responseTimeCategory);
                    break;
            }
        } else {
            if (qop != null) {
                responseTimeCategory = ResponseTimeCategory.delaytolerant;
                mlpLocationRequest.setResponseTimeCategory(responseTimeCategory);
            }
        }
        mlpLocationRequest.setLcsQoS(horizontalAccuracy, verticalAccuracy, verticalCoordinateRequest,
            responseTimeCategory != null ? responseTimeCategory.getCategory() : null);
        if (llAcc != null) {
            String qoSClass = llAcc.getQosClass();
            if (qoSClass.equalsIgnoreCase("ASSURED"))
                lcsQoSClass = LCSQoSClass.ASSURED;
            mlpLocationRequest.setLcsQoSClass(lcsQoSClass);
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveDeferredLocationEventType(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        TlrrEvent tlrrEvent;
        String deferredLocationEventType = null;
        int interval = -1, duration = -1;
        if (svcInit.getTlrr() != null) {
            Tlrr tlrr = svcInit.getTlrr();
            if (tlrr.getInterval() != null)
                interval = Integer.parseInt(tlrr.getInterval());
            if (tlrr.getDuration() != null)
                duration = Integer.parseInt(tlrr.getDuration());
            tlrrEvent = tlrr.getTlrrEvent();
            if (tlrrEvent != null) {
                if (tlrrEvent.getMsAction() != null)
                    deferredLocationEventType = tlrrEvent.getMsAction().getType();
                if (tlrrEvent.getChangeArea() != null) {
                    deferredLocationEventType = tlrrEvent.getChangeArea().getType();
                }
                if (tlrrEvent.getEquidistanceEvent() != null)
                    deferredLocationEventType = "LEAVING_DISTANCE";
            }

            if (deferredLocationEventType != null) {
                switch (deferredLocationEventType) {
                    case "MS_AVAIL":
                        mlpLocationRequest.setDeferredLocationEventType(1L);
                        break;
                    case "MS_ENTERING":
                        mlpLocationRequest.setDeferredLocationEventType(2L);
                        mlpLocationRequest.setSuplTriggerType(SuplTriggerType.AreaEvent);
                        mlpLocationRequest.suplAreaEventType = SuplAreaEventType.ENTERING_AREA;
                        break;
                    case "MS_OUTSIDE_AREA":
                        mlpLocationRequest.setSuplTriggerType(SuplTriggerType.AreaEvent);
                        mlpLocationRequest.suplAreaEventType = SuplAreaEventType.OUTSIDE_AREA;
                        break;
                    case "MS_LEAVING":
                        mlpLocationRequest.setDeferredLocationEventType(4L);
                        mlpLocationRequest.setSuplTriggerType(SuplTriggerType.AreaEvent);
                        mlpLocationRequest.suplAreaEventType = SuplAreaEventType.LEAVING_AREA;
                        break;
                    case "MS_WITHIN_AREA":
                        mlpLocationRequest.setDeferredLocationEventType(8L);
                        mlpLocationRequest.setSuplTriggerType(SuplTriggerType.AreaEvent);
                        mlpLocationRequest.suplAreaEventType = SuplAreaEventType.INSIDE_AREA;
                        break;
                    case "ENTERING_DISTANCE":
                    case "LEAVING_DISTANCE":
                    case "WITHIN_DISTANCE":
                    case "OUTSIDE_DISTANCE":
                        mlpLocationRequest.setDeferredLocationEventType(32L); // motion event
                        break;
                    case "PERIODIC": // not standard value in MLP 3.4
                        mlpLocationRequest.setDeferredLocationEventType(16L); // Periodic-LDR
                        if (interval >= 0 && duration >= 0) {
                            int reportingAmount = duration/interval;
                            mlpLocationRequest.setReportingAmount(reportingAmount);
                            mlpLocationRequest.setIntervalTime(interval);
                        }
                        break;
                    case "LDR-ACTIVATED": // not standard value in MLP 3.4
                        mlpLocationRequest.setDeferredLocationEventType(64L);
                        break;
                    case "MAX-INTERVAL-ACTIVATION": // not standard value in MLP 3.4
                        mlpLocationRequest.setDeferredLocationEventType(128L);
                        break;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveEventParams(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        if (svcInit.getTlrr() != null) {
            String interval, occurrence, duration, distance, minimumIntervalTime;
            Tlrr tlrr = svcInit.getTlrr();
            StartTime startTime = tlrr.getStartTime();
            if (startTime != null)
                mlpLocationRequest.suplStartTime = Long.valueOf(startTime.getContent());
            StopTime stopTime = tlrr.getStopTime();
            if (stopTime != null)
                mlpLocationRequest.suplStopTime = Long.valueOf(stopTime.getContent());
            interval = tlrr.getInterval();
            duration = tlrr.getDuration();

            TlrrEvent tlrrEvent = tlrr.getTlrrEvent();
            if (tlrrEvent != null) {
                if (mlpLocationRequest.getDeferredLocationEventType() == 2L || mlpLocationRequest.getDeferredLocationEventType() == 4L ||
                    mlpLocationRequest.getDeferredLocationEventType() == 8L || mlpLocationRequest.getSuplTriggerType() == SuplTriggerType.AreaEvent) {
                    // Area Event for PSL/PLR
                    if (tlrrEvent.getChangeArea() != null) {
                        TargetArea targetArea;
                        String areaId;
                        ChangeArea area = tlrrEvent.getChangeArea();
                        targetArea = area.getTargetArea();
                        String mcc, mnc;
                        if (targetArea != null) {
                            if (targetArea.getCc() != null) {
                                mlpLocationRequest.setAreaType("countryCode");
                                areaId = targetArea.getCc();
                                mlpLocationRequest.setAreaId(areaId);
                            } else if (targetArea.getPlmn() != null) {
                                mlpLocationRequest.setAreaType("plmnId");
                                mcc = targetArea.getPlmn().getMcc();
                                mnc = targetArea.getPlmn().getMnc();
                                areaId = mcc + "-" + mnc;
                                mlpLocationRequest.setAreaId(areaId);
                                if (targetArea.getLocationAreaCode() != null) {
                                    mlpLocationRequest.setAreaType("locationAreaId");
                                    areaId = areaId + "-" + targetArea.getLocationAreaCode();
                                    mlpLocationRequest.setAreaId(areaId);
                                    if (targetArea.getRoutingAreaCode() != null) {
                                        mlpLocationRequest.setAreaType("routingAreaId");
                                        areaId = areaId + "-" + targetArea.getRoutingAreaCode();
                                        mlpLocationRequest.setAreaId(areaId);
                                    }
                                } else if (targetArea.getTrackingAreaCode() != null) {
                                    mlpLocationRequest.setAreaType("trackingAreaId");
                                    areaId = mcc + "-" + mnc + "-" + targetArea.getTrackingAreaCode();
                                    mlpLocationRequest.setAreaId(areaId);
                                }
                            } else if (targetArea.getServingCell() != null) {
                                mcc = targetArea.getServingCell().getMcc();
                                mnc = targetArea.getServingCell().getMnc();
                                if (targetArea.getServingCell().getCgi() != null) {
                                    mcc = targetArea.getServingCell().getCgi().getMcc();
                                    mnc = targetArea.getServingCell().getCgi().getMnc();
                                    String lac = targetArea.getServingCell().getCgi().getLac();
                                    String ci = targetArea.getServingCell().getCgi().getCellid();
                                    mlpLocationRequest.setAreaType("cellGlobalId");
                                    areaId = mcc + "-" + mnc + "-" + lac + "-" + ci;
                                    mlpLocationRequest.setAreaId(areaId);
                                } /*else if (targetArea.getServingCell().getUtranCi() != null) { FIXME
                                    mlpLocationRequest.setAreaType("utranCellId");
                                    areaId = mcc + "-" + mnc + "-"  + targetArea.getServingCell().getUtranCi();
                                    mlpLocationRequest.setAreaId(areaId);
                                }*/ else if (targetArea.getServingCell().getLteCi() != null) {
                                    mlpLocationRequest.setAreaType("eUtranCellId");
                                    areaId = mcc + "-" + mnc + "-"  + targetArea.getServingCell().getLteCi();
                                    mlpLocationRequest.setAreaId(areaId);
                                }
                            } else if (targetArea.getShape() != null) {
                                Shape shape = targetArea.getShape();
                                if (shape.getCircularArea() != null) {
                                    mlpLocationRequest.suplGeographicTargetArea = SuplGeoTargetArea.CircularArea;
                                    mlpLocationRequest.suplAreaEventLatitude = Double.valueOf(shape.getCircularArea().getCoord().getX());
                                    mlpLocationRequest.suplAreaEventLongitude = Double.valueOf(shape.getCircularArea().getCoord().getY());
                                    mlpLocationRequest.suplAreaEventRadius = Integer.valueOf(shape.getCircularArea().getRadius());
                                } else if (shape.getEllipticalArea() != null) {
                                    mlpLocationRequest.suplGeographicTargetArea = SuplGeoTargetArea.EllipticalArea;
                                    mlpLocationRequest.suplAreaEventLatitude = Double.valueOf(shape.getEllipticalArea().getCoord().getX());
                                    mlpLocationRequest.suplAreaEventLongitude = Double.valueOf(shape.getEllipticalArea().getCoord().getY());
                                    mlpLocationRequest.suplAreaEventSemiMajor = Double.valueOf(shape.getEllipticalArea().getSemiMajor());
                                    mlpLocationRequest.suplAreaEventSemiMinor = Double.valueOf(shape.getEllipticalArea().getSemiMinor());
                                    mlpLocationRequest.suplAreaEventAngle = Double.valueOf(shape.getEllipticalArea().getAngle());
                                } else if (shape.getPolygon() != null) {
                                    mlpLocationRequest.suplGeographicTargetArea = SuplGeoTargetArea.PolygonArea;
                                    // TODO populate each point coordinates
                                }
                            }
                        }
                        // Occurrence for Area Event for PSL/PLR
                        occurrence = area.getNoOfReports();
                        if (occurrence != null) {
                            mlpLocationRequest.setSuplMaximumNumberOfReports(Integer.valueOf(area.getNoOfReports()));
                            // Interval for Area Event
                            minimumIntervalTime = area.getMinimumIntervalTime();
                            if (Integer.parseInt(occurrence) <= 1) {
                                mlpLocationRequest.setOccurrenceInfo(OccurrenceInfo.oneTimeEvent);
                                mlpLocationRequest.setSuplAreaEventRepeatedReporting(false);
                                if (minimumIntervalTime != null) {
                                    mlpLocationRequest.setSuplMinimumIntervalTime(Integer.valueOf(minimumIntervalTime));
                                }
                            } else {
                                mlpLocationRequest.setOccurrenceInfo(OccurrenceInfo.multipleTimeEvent);
                                mlpLocationRequest.setSuplAreaEventRepeatedReporting(true);
                                if (minimumIntervalTime != null) {
                                    mlpLocationRequest.setIntervalTime(Integer.valueOf(minimumIntervalTime)); // SS7 pr Diameter case
                                    mlpLocationRequest.setSuplMinimumIntervalTime(Integer.valueOf(minimumIntervalTime)); // SUPL
                                }
                            }
                        }
                        if (duration != null) {
                            mlpLocationRequest.setReportingDuration(Integer.valueOf(duration));
                        }
                        String locEstimates = area.getLocEstimates();
                        if (locEstimates.equalsIgnoreCase("TRUE")) {
                            mlpLocationRequest.setReportingLocationReqs(1);
                            mlpLocationRequest.suplLocationEstimateRequested = true;
                        }
                    }

                } else if (mlpLocationRequest.getDeferredLocationEventType() == 32L) {
                    // Motion Event for PSL/PLR
                    if (tlrrEvent.getEquidistanceEvent() != null) {
                        distance = tlrrEvent.getEquidistanceEvent().getTargetEquidistance();
                        mlpLocationRequest.setMotionEventDistance(Long.valueOf(distance));
                        occurrence = tlrrEvent.getEquidistanceEvent().getNoOfReports();
                        if (occurrence != null) {
                            if (Integer.parseInt(occurrence) == 1)
                                mlpLocationRequest.setOccurrenceInfo(OccurrenceInfo.oneTimeEvent);
                            else {
                                mlpLocationRequest.setOccurrenceInfo(OccurrenceInfo.multipleTimeEvent);
                                minimumIntervalTime = tlrrEvent.getEquidistanceEvent().getMinimumIntervalTime();
                                if (minimumIntervalTime != null)
                                    mlpLocationRequest.setIntervalTime(Integer.valueOf(minimumIntervalTime));
                            }
                        }
                        if (duration != null) {
                            mlpLocationRequest.setReportingDuration(Integer.valueOf(duration));
                        }
                        String locEstimates = tlrrEvent.getEquidistanceEvent().getLocEstimates();
                        if (locEstimates.equalsIgnoreCase("TRUE")) {
                            mlpLocationRequest.setReportingLocationReqs(1);
                        }
                    }

                } else if (mlpLocationRequest.getDeferredLocationEventType() == 16L) {
                    // Periodic-LDR for PSL/PLR
                    if (duration != null && interval != null) {
                        mlpLocationRequest.setReportingInterval(Integer.valueOf(interval));
                        mlpLocationRequest.setReportingAmount(Integer.parseInt(duration) / Integer.parseInt(interval));
                    }
                }
            }
        } else {
            return null;
        }
        return mlpLocationRequest;
    }

    private MLPLocationRequest retrieveSuplPeriodicParams(MLPLocationRequest mlpLocationRequest, SvcInit svcInit) {
        if (svcInit.getTlrr() != null) {
            String interval, duration;
            Tlrr tlrr = svcInit.getTlrr();
            StartTime startTime = tlrr.getStartTime();
            if (startTime != null)
                mlpLocationRequest.suplStartTime = Long.valueOf(startTime.getContent());
            StopTime stopTime = tlrr.getStopTime();
            if (stopTime != null)
                mlpLocationRequest.suplStopTime = Long.valueOf(stopTime.getContent());
            interval = tlrr.getInterval();
            duration = tlrr.getDuration();
            if (interval != null && tlrr.getTlrrEvent() == null) {
                // FIXME for now we are not accepting area and periodic events for SUPL at the same time
                mlpLocationRequest.suplTriggerType = SuplTriggerType.Periodic;
                mlpLocationRequest.setReportingInterval(Integer.valueOf(interval));
                if (duration != null) {
                    mlpLocationRequest.setReportingAmount(Integer.parseInt(duration) / Integer.parseInt(interval));
                }
            }
        } else {
            return null;
        }
        return mlpLocationRequest;
    }
}