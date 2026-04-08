package org.mobicents.gmlc.slee.diameter.slg;

import net.java.slee.resource.diameter.slg.events.avp.ReportingPLMNListAvp;

public class RequestValuesForPLR {

    public String plrUserName, plrMsisdn, plrImei, plrLcsNameString, plrLcsRequestorIdString, plrLcsCodeword, plrServiceSelection,
        plrAreaIdentification, plrAdditionalAreaIdentification, plrVisitedPLMNIdList, lrrCallbackUrl;

    public Long plrLcsPriority, plrHorizontalAccuracy, plrVerticalAccuracy, plrLcsSupportedGadShapes, plrLcsServiceTypeId, plrDeferredLocationType,
        plrFlags, plrAreaType, plrAdditionalAreaType, plrAreaEventIntervalTime, plrAreaEventSamplingInterval, plrAreaEventMaxInterval,
        plrAreaEventReportingDuration, plrAreaEventRepLocRequirements, plrPeriodicLDRReportingAmount, plrPeriodicLDRReportingInterval,
        plrMotionEventLinearDistance, plrMotionEventIntervalTime, plrMotionEventMaximumInterval, plrMotionEventSamplingInterval,
        plrMotionEvenReportingDuration, plrMotionEvenReportingLocationRequirements;

    public Integer plrSlgLocationType, plrLcsFormatInd, plrLcsClientType, plrLcsRequestorFormatIndicator, plrLcsReferenceNumber, plrQoSClass,
        plrVerticalRequested, plrResponseTime, plrVelocityRequested, plrPrivacyCheckNonSession, plrPrivacyCheckSession, plrAreaEventOccurrenceInfo,
        plrPeriodicLocationSupportIndicator, plrPrioritizedListIndicator, plrMotionEventOccurrenceInfo;

    public ReportingPLMNListAvp reportingPLMNListAvp;

    public RequestValuesForPLR() {
    }
}
