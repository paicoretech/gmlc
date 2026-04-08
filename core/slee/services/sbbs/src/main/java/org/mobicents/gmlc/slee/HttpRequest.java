package org.mobicents.gmlc.slee;

import net.java.slee.resource.diameter.slg.events.avp.AdditionalAreaAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaDefinitionAvp;
import net.java.slee.resource.diameter.slg.events.avp.AreaEventInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.LCSQoSAvp;
import net.java.slee.resource.diameter.slg.events.avp.MotionEventInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.PeriodicLDRInfoAvp;
import net.java.slee.resource.diameter.slg.events.avp.VerticalRequested;
import org.jdiameter.api.Avp;
import org.mobicents.slee.resource.diameter.slg.events.avp.AdditionalAreaAvpImpl;
import org.mobicents.slee.resource.diameter.slg.events.avp.AreaAvpImpl;
import org.mobicents.slee.resource.diameter.slg.events.avp.AreaDefinitionAvpImpl;
import org.mobicents.slee.resource.diameter.slg.events.avp.AreaEventInfoAvpImpl;
import org.mobicents.slee.resource.diameter.slg.events.avp.LCSQoSAvpImpl;
import org.mobicents.slee.resource.diameter.slg.events.avp.MotionEventInfoAvpImpl;
import org.mobicents.slee.resource.diameter.slg.events.avp.PeriodicLDRInfoAvpImpl;
import org.restcomm.protocols.ss7.map.api.service.lsm.Area;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaDefinition;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaEventInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaIdentification;
import org.restcomm.protocols.ss7.map.api.service.lsm.AreaType;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSPriority;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoS;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass;
import org.restcomm.protocols.ss7.map.api.service.lsm.OccurrenceInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.PeriodicLDRInfo;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTime;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTimeCategory;
import org.restcomm.protocols.ss7.map.service.lsm.AreaDefinitionImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaEventInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaIdentificationImpl;
import org.restcomm.protocols.ss7.map.service.lsm.AreaImpl;
import org.restcomm.protocols.ss7.map.service.lsm.LCSQoSImpl;
import org.restcomm.protocols.ss7.map.service.lsm.PeriodicLDRInfoImpl;
import org.restcomm.protocols.ss7.map.service.lsm.ResponseTimeImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

/**
 * HTTP Request
 */
public class HttpRequest implements Serializable {
    private static final Logger logger = Logger.getLogger(HttpRequest.class.getName());
    public HttpRequestType type;
    public String msisdn;
    public String imsi;
    public String imsPublicIdentity;
    public String coreNetworkOperation;
    public String domainT;
    public Integer lcsReferenceNumber = (int) (new Date().getTime() / 1000);
    public Integer tt;

    /**
     * SS7 LCS parameters
     **/
    public Integer lcsServiceTypeID;
    public LCSPriority lcsPriority;
    public Integer horizontalAccuracy;
    public Integer verticalAccuracy;
    public Boolean verticalCoordinateRequest;
    public ResponseTimeCategory responseTimeCategory;
    public ResponseTime responseTime;
    public Boolean velocityRequest;
    public LCSQoS lcsQoS;
    public AreaDefinition areaDefinition;
    public OccurrenceInfo occurrenceInfo;
    public AreaEventInfo areaEventInfo;
    public PeriodicLDRInfo periodicLDRInfo;
    public String slrCallbackUrl;
    public String psi;
    public String httpResponseType;
    public String locationInfoEPS;
    public String locationInfo5GS;
    public String activeLocationRetrieval;
    public String ratType;

    /**
     * LTE Diameter SLh and SLg parameters
     **/
    public Long lteLcsPriority;
    public Long lteHorizontalAccuracy;
    public Long lteVerticalAccuracy;
    public Integer lteVerticalCoordinateRequested;
    public Integer lteResponseTime;
    public Long lteServiceTypeId;
    public LCSQoSAvp lteLcsQoS = new LCSQoSAvpImpl(Avp.LCS_QOS, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
    public Integer lteAreaOccurrenceInfo;
    public AreaEventInfoAvp lteAreaEventInfo = new AreaEventInfoAvpImpl(Avp.AREA_EVENT_INFO, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
    public Integer lteMotionOccurrenceInfo;
    public MotionEventInfoAvp lteMotionEventInfo = new MotionEventInfoAvpImpl(Avp.MOTION_EVENT_INFO, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
    public PeriodicLDRInfoAvp ltePeriodicLDRInfo = new PeriodicLDRInfoAvpImpl(Avp.PERIODIC_LDR_INFORMATION, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
    public AreaAvp area = new AreaAvpImpl(Avp.AREA, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
    public AdditionalAreaAvp additionalArea = new AdditionalAreaAvpImpl(Avp.ADDITIONAL_AREA, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
    public String lteLrrCallbackUrl;

    public HttpRequest(HttpRequestType httpRequestType, String msisdnAddress, String imsiData, String imsPublicId, String operation, String domain,
                       String priority, Integer horAccuracy, Integer vertAccuracy, String verticalCoordinateReq, String responseTimeCategoryValue,
                       Boolean velocityRequested, Integer lcsReferenceNumberValue, Integer lcsServiceTypeId, String areaType, String areaId, String addAreaT,
                       String addAreaId, String areaOccurrenceInfo, Integer areaIntervalTime, Long areaMaxInterval, Long areaSamplingInterval,
                       Long areaReportingDuration, Long areaRepLocReqs, Integer motionOccurrenceInfo, Long motionLinearDistance, Long motionSamplingInterval,
                       Long motionIntervalTime, Long motionMaxInterval, Long motionReportingDuration, Long motionRepLocReqs, Integer periodicReportingAmount,
                       Integer periodicReportingInterval, String callbackUrl, Integer lcsQosClass, String httpRespType,
                       String epsLocationInfo, String activeLocation, String sh5GSLocationInfo, String ratTypeRequested, Integer translationType) {

        type = httpRequestType;
        msisdn = msisdnAddress;
        imsi = imsiData;
        coreNetworkOperation = "ATI";
        domainT = domain;
        locationInfoEPS = epsLocationInfo;
        activeLocationRetrieval = activeLocation;
        locationInfo5GS = sh5GSLocationInfo;
        ratType = ratTypeRequested;
        tt = translationType;
        velocityRequest = false;

        try {
            if (operation != null) {
                if (operation.equalsIgnoreCase("PSL")) {
                    coreNetworkOperation = "PSL";
                    lcsReferenceNumber = lcsReferenceNumberValue;
                    lcsServiceTypeID = lcsServiceTypeId;
                    if (priority != null) {
                        if (priority.equalsIgnoreCase("highestPriority")) {
                            lcsPriority = LCSPriority.highestPriority;
                        } else {
                            lcsPriority = LCSPriority.normalPriority;
                        }
                    }
                    horizontalAccuracy = horAccuracy;
                    verticalAccuracy = vertAccuracy;
                    if (verticalCoordinateReq != null) {
                        verticalCoordinateRequest = !verticalCoordinateReq.equalsIgnoreCase("false");
                    }
                    if (responseTimeCategoryValue != null) {
                        if (responseTimeCategoryValue.equalsIgnoreCase("tolerant")) {
                            responseTimeCategory = ResponseTimeCategory.delaytolerant;
                        } else {
                            responseTimeCategory = ResponseTimeCategory.lowdelay;
                        }
                        responseTime = new ResponseTimeImpl(responseTimeCategory);
                    }
                    if (velocityRequested != null)
                        velocityRequest = velocityRequested;
                    LCSQoSClass qosClass = null;
                    if (lcsQosClass != null) {
                        qosClass = LCSQoSClass.valueOf(String.valueOf(lcsQosClass.intValue()));
                    }
                    if (horizontalAccuracy != null && verticalAccuracy != null && verticalCoordinateRequest != null && responseTime != null)
                        lcsQoS = new LCSQoSImpl(horizontalAccuracy, verticalAccuracy, verticalCoordinateRequest, responseTime, null, velocityRequest, qosClass);
                    if (areaType != null) {
                        try {
                            ArrayList<Area> areaArrayList = new ArrayList<>();
                            AreaType areaT = AreaType.locationAreaId;
                            if (areaType.equalsIgnoreCase("routing")) {
                                areaT = AreaType.routingAreaId;
                            } else if (areaType.equalsIgnoreCase("cgi")) {
                                areaT = AreaType.cellGlobalId;
                            } else if (areaType.equalsIgnoreCase("cc")) {
                                areaT = AreaType.countryCode;
                            } else if (areaType.equalsIgnoreCase("utranCid")) {
                                areaT = AreaType.utranCellId;
                            } else if (areaType.equalsIgnoreCase("plmn")) {
                                areaT = AreaType.plmnId;
                            }
                            if (areaId != null) {
                                byte[] aId = areaId.getBytes();
                                AreaIdentification areaIdentification = new AreaIdentificationImpl(aId);
                                Area area = new AreaImpl(areaT, areaIdentification);
                                areaArrayList.add(area);
                                areaDefinition = new AreaDefinitionImpl(areaArrayList);
                            }
                            if (areaOccurrenceInfo != null) {
                                if (areaOccurrenceInfo.equalsIgnoreCase("multiple")) {
                                    occurrenceInfo = OccurrenceInfo.multipleTimeEvent;
                                } else {
                                    occurrenceInfo = OccurrenceInfo.oneTimeEvent;
                                }
                                if (areaIntervalTime != null)
                                    areaEventInfo = new AreaEventInfoImpl(areaDefinition, occurrenceInfo, areaIntervalTime);
                            }
                        } catch (Exception e) {
                            logger.warning(String.format("Error while creating AreaEventInfo from HttpRequest:" + e));
                        }
                    }
                    if (periodicReportingAmount != null && periodicReportingInterval != null) {
                        // FIXME with ReportingOptionMilliseconds
                        periodicLDRInfo = new PeriodicLDRInfoImpl(periodicReportingAmount, periodicReportingInterval, null);
                    }
                    slrCallbackUrl = callbackUrl;
                }

                if (operation.equalsIgnoreCase("PLR")) {
                    coreNetworkOperation = "PLR";
                    lcsReferenceNumber = lcsReferenceNumberValue;
                    if (lcsServiceTypeId != null)
                        lteServiceTypeId = Long.valueOf(lcsServiceTypeId);
                    if (priority != null) {
                        if (priority.equalsIgnoreCase("highestPriority")) {
                            lteLcsPriority = 0L;
                        } else {
                            lteLcsPriority = 1L;
                        }
                    }
                    if (horAccuracy != null)
                        lteHorizontalAccuracy = Long.valueOf(horAccuracy);
                    if (vertAccuracy != null)
                        lteVerticalAccuracy = Long.valueOf(vertAccuracy);
                    if (verticalCoordinateReq != null && verticalCoordinateReq.equalsIgnoreCase("false")) {
                        lteVerticalCoordinateRequested = VerticalRequested._VERTICAL_COORDINATE_IS_NOT_REQUESTED; // VERTICAL_COORDINATE_IS_NOT REQUESTED (0)
                    } else {
                        lteVerticalCoordinateRequested = VerticalRequested._VERTICAL_COORDINATE_IS_REQUESTED; // VERTICAL_COORDINATE_IS_REQUESTED (1)
                    }
                    if (responseTimeCategoryValue != null && responseTimeCategoryValue.equalsIgnoreCase("tolerant")) {
                        lteResponseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime._DELAY_TOLERANT; // DELAY_TOLERANT (1)
                    } else {
                        lteResponseTime = net.java.slee.resource.diameter.slg.events.avp.ResponseTime._LOW_DELAY; // LOW_DELAY (0)
                    }
                    lteLcsQoS.setResponseTime(net.java.slee.resource.diameter.slg.events.avp.ResponseTime.fromInt(lteResponseTime));
                    if (lteHorizontalAccuracy != null)
                        lteLcsQoS.setHorizontalAccuracy(lteHorizontalAccuracy);
                    if (lteVerticalAccuracy != null)
                        lteLcsQoS.setVerticalAccuracy(lteVerticalAccuracy);
                    if (lteVerticalCoordinateRequested != null)
                        lteLcsQoS.setVerticalRequested(VerticalRequested.fromInt(lteVerticalCoordinateRequested));
                    if (lcsQosClass != null)
                        lteLcsQoS.setLCSQoSClass(net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass.fromInt(lcsQosClass));
                    if (areaType != null) {
                        area.setAreaType(Long.parseLong(areaType));
                    }
                    if (areaId != null)
                        area.setAreaIdentification(areaId.getBytes());
                    if (addAreaT != null)
                        additionalArea.setAreaType(Long.parseLong(addAreaT));
                    if (addAreaId != null)
                        additionalArea.setAreaIdentification(addAreaId.getBytes());
                    AreaDefinitionAvp areaDefinition = new AreaDefinitionAvpImpl(Avp.AREA_DEFINITION, MobileCoreNetworkInterfaceSbb.TGPP_VENDOR_ID, 0, 0, new byte[]{});
                    areaDefinition.setArea(area);
                    areaDefinition.setAdditionalArea(additionalArea);
                    if (areaOccurrenceInfo != null && areaOccurrenceInfo.equalsIgnoreCase("multiple")) {
                        lteAreaOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT; // MULTIPLE_TIME_EVENT (1)
                    } else {
                        lteAreaOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT; // ONE_TIME_EVENT (0)
                    }
                    lteAreaEventInfo.setAreaDefinition(areaDefinition);
                    if (lteAreaOccurrenceInfo != null)
                        lteAreaEventInfo.setOccurrenceInfo(net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo.fromInt(lteAreaOccurrenceInfo));
                    if (areaIntervalTime != null)
                        lteAreaEventInfo.setIntervalTime(areaIntervalTime);
                    if (areaMaxInterval != null)
                        lteAreaEventInfo.setMaximumInterval(areaMaxInterval);
                    if (areaSamplingInterval != null)
                        lteAreaEventInfo.setSamplingInterval(areaSamplingInterval);
                    if (areaReportingDuration != null)
                        lteAreaEventInfo.setReportDuration(areaReportingDuration);
                    if (areaRepLocReqs != null)
                        lteAreaEventInfo.setReportingLocationRequirements(areaRepLocReqs);
                    if (motionOccurrenceInfo != null && motionOccurrenceInfo == 1) {
                        lteMotionOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._MULTIPLE_TIME_EVENT; // MULTIPLE_TIME_EVENT (1)
                    } else {
                        lteMotionOccurrenceInfo = net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo._ONE_TIME_EVENT; // ONE_TIME_EVENT (0)
                    }
                    lteMotionEventInfo.setOccurrenceInfo(net.java.slee.resource.diameter.slg.events.avp.OccurrenceInfo.fromInt(lteMotionOccurrenceInfo));
                    if (motionLinearDistance != null)
                        lteMotionEventInfo.setLinearDistance(motionLinearDistance);
                    if (motionSamplingInterval != null)
                        lteMotionEventInfo.setSamplingInterval(motionSamplingInterval);
                    if (motionIntervalTime != null)
                        lteMotionEventInfo.setIntervalTime(motionIntervalTime);
                    if (motionMaxInterval != null)
                        lteMotionEventInfo.setMaximumInterval(motionMaxInterval);
                    if (motionReportingDuration != null)
                        lteMotionEventInfo.setReportDuration(motionReportingDuration);
                    if (motionRepLocReqs != null)
                        lteMotionEventInfo.setReportingLocationRequirements(motionRepLocReqs);

                    if (periodicReportingAmount != null && periodicReportingInterval != null) {
                        ltePeriodicLDRInfo.setReportingAmount(periodicReportingAmount);
                        ltePeriodicLDRInfo.setReportingInterval(periodicReportingInterval);
                    }
                    lteLrrCallbackUrl = callbackUrl;
                }

                if (operation.equalsIgnoreCase("UDR")) {
                    coreNetworkOperation = "UDR";
                    imsPublicIdentity = imsPublicId;
                    domainT = domain;
                    activeLocationRetrieval = activeLocation;
                }
            }

            httpResponseType = httpRespType;

        } catch (Exception e) {
            logger.severe("Exception on HttpRequest class after evaluating parameters received from curl request:\n" + e.getMessage());
        }
    }

    public HttpRequest(HttpRequestType type) {
        this(type, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null);
    }
}
