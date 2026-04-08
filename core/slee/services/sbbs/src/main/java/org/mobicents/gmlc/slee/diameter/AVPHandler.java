package org.mobicents.gmlc.slee.diameter;

import net.java.slee.resource.diameter.base.events.avp.Address;
import net.java.slee.resource.diameter.slg.events.avp.*;
import org.apache.log4j.Logger;
import org.mobicents.gmlc.slee.diameter.sh.ShUdaAvpValues;
import org.mobicents.gmlc.slee.diameter.sh.elements.*;
import org.mobicents.gmlc.slee.primitives.*;
import org.mobicents.gmlc.slee.primitives.LocationInformation5GS;
import org.mobicents.gmlc.slee.primitives.LocationInformation5GSImpl;
import org.mobicents.gmlc.slee.primitives.NRCellGlobalId;
import org.mobicents.gmlc.slee.primitives.NRCellGlobalIdImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.LocationNumberImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.LocationNumber;
import org.restcomm.protocols.ss7.map.api.MAPException;
import org.restcomm.protocols.ss7.map.api.primitives.*;
import org.restcomm.protocols.ss7.map.api.service.lsm.AccuracyFulfilmentIndicator;
import org.restcomm.protocols.ss7.map.api.service.lsm.LCSQoSClass;
import org.restcomm.protocols.ss7.map.api.service.lsm.ResponseTime;
import org.restcomm.protocols.ss7.map.api.service.lsm.*;
import org.restcomm.protocols.ss7.map.api.service.mobility.locationManagement.SupportedLCSCapabilitySets;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberInformation.*;
import org.restcomm.protocols.ss7.map.api.service.mobility.subscriberManagement.LSAIdentity;
import org.restcomm.protocols.ss7.map.primitives.*;
import org.restcomm.protocols.ss7.map.service.lsm.*;
import org.restcomm.protocols.ss7.map.service.mobility.locationManagement.SupportedLCSCapabilitySetsImpl;
import org.restcomm.protocols.ss7.map.service.mobility.subscriberInformation.*;

import java.nio.charset.StandardCharsets;

import static org.mobicents.gmlc.slee.utils.ByteUtils.bytesToHex;
import static org.mobicents.gmlc.slee.utils.TBCDUtil.toTBCDString;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class AVPHandler {

    private static final Logger logger = Logger.getLogger(AVPHandler.class.getName());

    public AVPHandler() {
    }

    public static IMSI userName2Imsi(String userName) {
        IMSI imsi = null;
        if (userName != null) {
            imsi = new IMSIImpl(userName);
        }
        return imsi;
    }

    public static ISDNAddressString byte2IsdnAddressString(byte[] byteParam) {
        ISDNAddressString isdnAddressString = null;
        if (byteParam != null) {
            String byteValue = new String(byteParam, StandardCharsets.UTF_8);
            isdnAddressString = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, byteValue);
        }
        return isdnAddressString;
    }

    public static ISDNAddressString tbcd2IsdnAddressString(byte[] tbcd) {
        ISDNAddressString isdnAddressString = null;
        if (tbcd != null) {
            String tbcdByteValue = toTBCDString(tbcd);
            isdnAddressString = new ISDNAddressStringImpl(AddressNature.international_number,
                    org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, tbcdByteValue);
        }
        return isdnAddressString;
    }

    public static LMSI byte2Lmsi(byte[] byteParam) {
        LMSI lmsi = null;
        if (byteParam != null)
            lmsi = new LMSIImpl(byteParam);
        return lmsi;
    }

    public static String byte2String(byte[] byteParam) {
        String str = null;
        if (byteParam != null)
            str = new String(byteParam, StandardCharsets.UTF_8);
        return str;
    }

    public static Long byte2Long(byte[] byteParam) {
        Long longParam = null;
        if (byteParam != null) {
            String byteString = bytesToHex(byteParam);
            longParam = Long.parseLong(byteString, 16);
        }
        return longParam;
    }

    public static Integer byte2Int(byte[] byteParam) {
        Integer intParam = null;
        if (byteParam != null) {
            String byteString = bytesToHex(byteParam);
            intParam = Integer.parseInt(byteString, 16);
        }
        return intParam;
    }

    public static Integer long2Int(Long ageOfLocationEstimate) {
        Integer aole = null;
        if (ageOfLocationEstimate != null)
            aole = ageOfLocationEstimate.intValue();
        return aole;
    }

    public static DiameterIdentity diameterIdToMapDiameterId(net.java.slee.resource.diameter.base.events.avp.DiameterIdentity diameterIdentity) {
        DiameterIdentity diameterIdentityMap = null;
        if (diameterIdentity != null) {
            String diameterIdStr = diameterIdentity.toString();
            byte[] diameterIdByteArray = diameterIdStr.getBytes();
            diameterIdentityMap = new DiameterIdentityImpl(diameterIdByteArray);
        }
        return diameterIdentityMap;
    }

    public static GSNAddress address2GsnAddress(Address address) {
        GSNAddress gsnAddress = null;
        if (address != null) {
            byte[] addressByteArray = address.getAddress();
            gsnAddress = new GSNAddressImpl(addressByteArray);
        }
        return gsnAddress;
    }

    public static AccuracyFulfilmentIndicator diamAccFulInd2MapAccFulInd(net.java.slee.resource.diameter.slg.events.avp.AccuracyFulfilmentIndicator accuracyFulfilmentIndicator) {
        AccuracyFulfilmentIndicator mapAccuracyFulfilmentIndicator = AccuracyFulfilmentIndicator.requestedAccuracyNotFulfilled;
        if (accuracyFulfilmentIndicator != null) {
            int accuracyInd = accuracyFulfilmentIndicator.getValue();
            if (accuracyInd == 0) {
                mapAccuracyFulfilmentIndicator = AccuracyFulfilmentIndicator.requestedAccuracyFulfilled;
            }
        }
        return mapAccuracyFulfilmentIndicator;
    }

    public static ExtGeographicalInformation lteLocationEstimate2ExtGeographicalInformation(byte[] locationEstimate) {
        ExtGeographicalInformation extGeographicalInformation = null;
        if (locationEstimate != null)
            extGeographicalInformation = new ExtGeographicalInformationImpl(locationEstimate);
        return extGeographicalInformation;
    }

    public static AddGeographicalInformation lteLocationEstimate2AddGeographicalInformation(byte[] locationEstimate) {
        AddGeographicalInformation addGeographicalInformation = null;
        if (locationEstimate != null)
            addGeographicalInformation = new AddGeographicalInformationImpl(locationEstimate);
        return addGeographicalInformation;
    }

    public static VelocityEstimate lteVelocityEstimate2MapVelocityEstimate(byte[] velocityEstimate) {
        VelocityEstimate mapVelocityEstimate = null;
        if (velocityEstimate != null)
            mapVelocityEstimate = new VelocityEstimateImpl(velocityEstimate);
        return mapVelocityEstimate;
    }

    public static PositioningDataInformation lteGeranPosDataInfo2MapGeranPosDataInfo(byte[] geranPosDataInfo) {
        PositioningDataInformation positioningDataInformation = null;
        if (geranPosDataInfo != null)
            positioningDataInformation = new PositioningDataInformationImpl(geranPosDataInfo);
        return positioningDataInformation;
    }

    public static GeranGANSSpositioningData lteGeranGanssPosDataInfo2MapGeranGanssPosDataInfo(byte[] geranGanssPosDataInfo) {
        GeranGANSSpositioningData geranGANSSpositioningData = null;
        if (geranGanssPosDataInfo != null)
            geranGANSSpositioningData = new GeranGANSSpositioningDataImpl(geranGanssPosDataInfo);
        return geranGANSSpositioningData;
    }

    public static UtranPositioningDataInfo lteUtranPosData2MapUtranPosDataInfo(byte[] utranPositioningData) {
        UtranPositioningDataInfo utranPositioningDataInfo = null;
        if (utranPositioningData != null)
            utranPositioningDataInfo = new UtranPositioningDataInfoImpl(utranPositioningData);
        return utranPositioningDataInfo;
    }

    public static UtranGANSSpositioningData lteUtranGanssPosData2MapUtranGanssPosDataInfo(byte[] utranGanssPositioningData) {
        UtranGANSSpositioningData utranGanssPositioningDataInfo = null;
        if (utranGanssPositioningData != null)
            utranGanssPositioningDataInfo = new UtranGANSSpositioningDataImpl(utranGanssPositioningData);
        return utranGanssPositioningDataInfo;
    }

    public static UtranAdditionalPositioningData lteUtranAddPosData2MapUtranAdditionalPositioningdata(byte[] utranAddPosData) {
        UtranAdditionalPositioningData utranAdditionalPositioningData = null;
        if (utranAddPosData != null)
            utranAdditionalPositioningData = new UtranAdditionalPositioningDataImpl(utranAddPosData);
        return utranAdditionalPositioningData;
    }

    public static EUTRANCGI lteEcgi2MapEutranCgi(byte[] ecgi) {
        EUTRANCGI eUtranCGI = null;
        if (ecgi != null)
            eUtranCGI = new EUTRANCGIImpl(ecgi);
        return eUtranCGI;
    }

    public static IMEI string2MapImei(String imeiStr) {
        IMEI imei = null;
        if (imeiStr != null)
            imei = new IMEIImpl(imeiStr);
        return imei;
    }

    public static boolean ltePseudonymInd2Boolean(PseudonymIndicator pseudonymIndicator) {
        return pseudonymIndicator != null && pseudonymIndicator.getValue() == 1;
    }

    public static LCSQoS lteLcsQos2MapLcsQoS(LCSQoSAvp lcsQoSAvp) {
        LCSQoS lcsQoS = null;
        if (lcsQoSAvp != null) {
            int horizontalAccuracy = (int) lcsQoSAvp.getHorizontalAccuracy();
            int verticalAccuracy = (int) lcsQoSAvp.getVerticalAccuracy();
            ResponseTimeCategory responseTimeCategory = ResponseTimeCategory.delaytolerant;
            net.java.slee.resource.diameter.slg.events.avp.ResponseTime lteResponseTime = lcsQoSAvp.getResponseTime();
            if (lteResponseTime != null) {
                if (lteResponseTime.getValue() == 0)
                    responseTimeCategory = ResponseTimeCategory.lowdelay;
            }
            boolean verticalRequested = false;
            VerticalRequested lteVerticalRequested = lcsQoSAvp.getVerticalRequested();
            if (lteVerticalRequested != null) {
                if (lteVerticalRequested.getValue() == 1)
                    verticalRequested = true;
            }
            ResponseTime responseTime = new ResponseTimeImpl(responseTimeCategory);
            boolean velocityRequest = false; // FIXME with newer version of LCSQoSAvp?
            LCSQoSClass qoSClass = null;
            if (lcsQoSAvp.getLCSQoSClass() != null) {
                qoSClass = LCSQoSClass.bestEffort;
                if (lcsQoSAvp.getLCSQoSClass().getValue() == net.java.slee.resource.diameter.slg.events.avp.LCSQoSClass._ASSURED)
                    qoSClass = LCSQoSClass.assured;
            }
            lcsQoS = new LCSQoSImpl(horizontalAccuracy, verticalAccuracy, verticalRequested, responseTime, null, velocityRequest, qoSClass);
        }
        return lcsQoS;
    }

    public static DeferredmtlrData lteDeferredMtlrData2MapDeferredmtlrData(DeferredMTLRDataAvp deferredMTLRDataAvp) {
        DeferredmtlrData deferredmtlrData = null;
        if (deferredMTLRDataAvp != null) {
            long deferredLocationType = deferredMTLRDataAvp.getDeferredLocationType();
            DeferredLocationEventType deferredLocationEventType;
            boolean msAvailable, enteringIntoArea, leavingFromArea, beingInsideArea, periodicLDR;
            msAvailable = enteringIntoArea = leavingFromArea = beingInsideArea = periodicLDR = false;
            if (deferredLocationType == 0) {
                msAvailable = true;
            }
            if (deferredLocationType == 1) {
                enteringIntoArea = true;
            }
            if (deferredLocationType == 2) {
                leavingFromArea = true;
            }
            if (deferredLocationType == 3) {
                beingInsideArea = true;
            }
            if (deferredLocationType == 4) {
                periodicLDR = true;
            }
            deferredLocationEventType = new DeferredLocationEventTypeImpl(msAvailable, enteringIntoArea, leavingFromArea, beingInsideArea, periodicLDR);
            long lteTerminationCause = deferredMTLRDataAvp.getTerminationCause();
            TerminationCause terminationCause = TerminationCause.getTerminationCause((int) lteTerminationCause);
            LCSLocationInfo lcsLocationInfo = null;
            if (deferredMTLRDataAvp.getServingNode() != null) {
                net.java.slee.resource.diameter.base.events.avp.DiameterIdentity lteSgsnName = deferredMTLRDataAvp.getServingNode().getSGSNName();
                DiameterIdentity sgsnName = diameterIdToMapDiameterId(lteSgsnName);
                byte[] sgsnNumber = deferredMTLRDataAvp.getServingNode().getSGSNNumber();
                net.java.slee.resource.diameter.base.events.avp.DiameterIdentity lteSgsnRealm = deferredMTLRDataAvp.getServingNode().getSGSNRealm();
                DiameterIdentity sgsnRealm = diameterIdToMapDiameterId(lteSgsnRealm);
                net.java.slee.resource.diameter.base.events.avp.DiameterIdentity lteMmeName = deferredMTLRDataAvp.getServingNode().getMMEName();
                DiameterIdentity mmeName = diameterIdToMapDiameterId(lteMmeName);
                net.java.slee.resource.diameter.base.events.avp.DiameterIdentity mmeRealm = deferredMTLRDataAvp.getServingNode().getMMERealm();
                byte[] mscNumber = deferredMTLRDataAvp.getServingNode().getMSCNumber();
                net.java.slee.resource.diameter.base.events.avp.DiameterIdentity lte3gppAAAServerName = deferredMTLRDataAvp.getServingNode().get3GPPAAAServerName();
                DiameterIdentity tgppAAAServerName = diameterIdToMapDiameterId(lte3gppAAAServerName);
                Address gmlcAddress = deferredMTLRDataAvp.getServingNode().getGMLCAddress();
                long lcsCapabilitiesSets = deferredMTLRDataAvp.getServingNode().getLcsCapabilitiesSets();
                boolean lcsCapabilitySetRelease98_99, lcsCapabilitySetRelease4, lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7;
                lcsCapabilitySetRelease98_99 = lcsCapabilitySetRelease4 = lcsCapabilitySetRelease5 = lcsCapabilitySetRelease6 = lcsCapabilitySetRelease7 = false;
                if (lcsCapabilitiesSets == 0)
                    lcsCapabilitySetRelease98_99 = true;
                if (lcsCapabilitiesSets == 1)
                    lcsCapabilitySetRelease4 = true;
                if (lcsCapabilitiesSets == 2)
                    lcsCapabilitySetRelease5 = true;
                if (lcsCapabilitiesSets == 3)
                    lcsCapabilitySetRelease6 = true;
                if (lcsCapabilitiesSets == 4)
                    lcsCapabilitySetRelease7 = true;
                SupportedLCSCapabilitySets supportedLCSCapabilitySets = new SupportedLCSCapabilitySetsImpl(lcsCapabilitySetRelease98_99,
                        lcsCapabilitySetRelease4, lcsCapabilitySetRelease5, lcsCapabilitySetRelease6, lcsCapabilitySetRelease7);
                ISDNAddressString networkNodeNumber = null;
                boolean gprsNodeIndicator = false;
                AdditionalNumber additionalNumber = null;
                if (mscNumber != null) {
                    String mscNumberAddress = toTBCDString(mscNumber);
                    networkNodeNumber = new ISDNAddressStringImpl(AddressNature.international_number,
                            org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, mscNumberAddress);
                    if (sgsnNumber != null) {
                        String sgsnNumberAddress = toTBCDString(sgsnNumber);
                        additionalNumber = new AdditionalNumberImpl(null, new ISDNAddressStringImpl(AddressNature.international_number,
                                org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, sgsnNumberAddress));
                    }
                } else if (sgsnNumber != null) {
                    String sgsnNumberAddress = toTBCDString(sgsnNumber);
                    networkNodeNumber =  new ISDNAddressStringImpl(AddressNature.international_number,
                            org.restcomm.protocols.ss7.map.api.primitives.NumberingPlan.ISDN, sgsnNumberAddress);
                    gprsNodeIndicator = true;
                }
                lcsLocationInfo = new LCSLocationInfoImpl(networkNodeNumber, null, null, gprsNodeIndicator, additionalNumber,
                        supportedLCSCapabilitySets, null, mmeName, tgppAAAServerName, sgsnName, sgsnRealm);
            }
            deferredmtlrData = new DeferredmtlrDataImpl(deferredLocationEventType, terminationCause, lcsLocationInfo);
        }
        return deferredmtlrData;
    }

    public static PeriodicLDRInfo ltePeriodicLDRInfo2MapPeriodicLDRInfo(PeriodicLDRInfoAvp periodicLDRInfoAvp) {
        PeriodicLDRInfo periodicLDRInfo = null;
        if (periodicLDRInfoAvp != null) {
            int reportingAmount = (int) periodicLDRInfoAvp.getReportingAmount();
            int reportingInterval = (int) periodicLDRInfoAvp.getReportingInterval();
            ReportingOptionMilliseconds reportingOptionMilliseconds = null; // FIXME
            periodicLDRInfo = new PeriodicLDRInfoImpl(reportingAmount, reportingInterval, reportingOptionMilliseconds);
        }
        return periodicLDRInfo;
    }

    public static LocationInformation shLocationInfo2MapLocationInformation(ShUdaAvpValues shUdaAvpValues) {
        LocationInformation locationInformation = null;
        LocationInformationEPS locationInformationEPS;
        LocationNumberMap locationNumberMap = null;
        CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = null;
        GeographicalInformation geographicalInformation = null;
        GeodeticInformation geodeticInformation = null;
        ISDNAddressString mscNumber, vlrNumber;
        mscNumber = vlrNumber = null;
        DiameterIdentity mmeName = null;
        boolean csCurrentLocationRetrieved = false;
        Integer csAgeOfLocationInformation;
        EUtranCgi eUtranCellGlobalIdentity = null;
        TAId trackingAreaIdentity = null;
        GeographicalInformation epsGeographicalInfo = null;
        GeodeticInformation epsGeodeticInfo = null;
        boolean epsCurrentLocationRetrieved = false;
        boolean saiPresent = false;
        MAPExtensionContainer extensionContainer = null;
        LSAIdentity lsaIdentity = null;
        UserCSGInformation userCSGInformation = null;
        if (shUdaAvpValues != null) {
            // Collecting from CSLocationInformation
            // LocationNumber
            ShLocationNumber shLocationNumber = shUdaAvpValues.getLocationNumber();
            if (shLocationNumber != null) {
                int natureOfAddressIndicator = shLocationNumber.getLocationNumber().getNatureOfAddressIndicator();
                String locationNumberAddressDigits = shLocationNumber.getLocationNumber().getAddress();
                int numberingPlanIndicator = shLocationNumber.getLocationNumber().getNumberingPlanIndicator();
                int internalNetworkNumberIndicator = shLocationNumber.getLocationNumber().getInternalNetworkNumberIndicator();
                int addressRepresentationRestrictedIndicator = shLocationNumber.getLocationNumber().getAddressRepresentationRestrictedIndicator();
                int screeningIndicator = shLocationNumber.getLocationNumber().getScreeningIndicator();
                LocationNumber locationNumber = new LocationNumberImpl(natureOfAddressIndicator, locationNumberAddressDigits, numberingPlanIndicator,
                        internalNetworkNumberIndicator, addressRepresentationRestrictedIndicator, screeningIndicator);
                try {
                    locationNumberMap = new LocationNumberMapImpl(locationNumber);
                } catch (MAPException e) {
                    logger.error(e.getMessage());
                }
            }
            // CS CellGlobalId
            ShCellGlobalId cellGlobalId = shUdaAvpValues.getCsCellGlobalId();
            if (cellGlobalId != null) {
                CellGlobalIdOrServiceAreaIdFixedLength cgi = cellGlobalId.getCellGlobalIdOrServiceAreaIdFixedLength();
                cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(cgi);
            }
            // CS ServiceAreaId
            ShServiceAreaId serviceAreaId = shUdaAvpValues.getCsServiceAreaId();
            if (serviceAreaId != null) {
                CellGlobalIdOrServiceAreaIdFixedLength sai = serviceAreaId.getCellGlobalIdOrServiceAreaIdFixedLength();
                cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(sai);
            }
            // CS LocationAreaId
            ShLocationAreaId locationAreaId = shUdaAvpValues.getCsLocationAreaId();
            if (locationAreaId != null) {
                LAIFixedLength lai = locationAreaId.getLaiFixedLength();
                cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(lai);
            }
            // CS GeographicalInformation
            ShGeographicalInformation csGeographicalInformation = shUdaAvpValues.getCsGeographicalInformation();
            if (csGeographicalInformation != null)
                geographicalInformation = csGeographicalInformation.getGeographicalInformation();
            // CS GeodeticInformation
            ShGeodeticInformation csGeodeticInformation = shUdaAvpValues.getCsGeodeticInformation();
            if (csGeodeticInformation != null)
                geodeticInformation = csGeodeticInformation.getGeodeticInformation();
            // MSC Number
            if (shUdaAvpValues.getMscNumber() != null) {
                String mscAddress = shUdaAvpValues.getMscNumber().getAddress();
                mscNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, mscAddress);
            }
            // VLR Number
            if (shUdaAvpValues.getVlrNumber() != null) {
                String vlrAddress = shUdaAvpValues.getVlrNumber().getAddress();
                vlrNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, vlrAddress);
            }
            // CS Current Location Retrieved and Age Of Location Information
            if (shUdaAvpValues.getCsCurrentLocationInfoRetrieved() != null) {
                csCurrentLocationRetrieved = shUdaAvpValues.getCsCurrentLocationInfoRetrieved().equalsIgnoreCase("0") ||
                        shUdaAvpValues.getCsCurrentLocationInfoRetrieved().equalsIgnoreCase("true");
            }
            csAgeOfLocationInformation = shUdaAvpValues.getCsAgeOfLocationInfo();
            if (csAgeOfLocationInformation != null) {
                if (csAgeOfLocationInformation != 0)
                    csCurrentLocationRetrieved = false;
            }

            // Collecting from EPSLocationInformation
            // EUTRANCellGlobalId
            ShEUTRANCellGlobalId eutranCellGlobalId = shUdaAvpValues.getEutrancgi();
            if (eutranCellGlobalId != null)
                eUtranCellGlobalIdentity = new EUtranCgiImpl(eutranCellGlobalId.getEutranCgi().getData());
            // EPS TAI
            ShTrackingAreaId shTrackingAreaId = shUdaAvpValues.getTrackingAreaId();
            if (shTrackingAreaId != null)
                trackingAreaIdentity = new TAIdImpl(shTrackingAreaId.getTrackingAreaId().getData());
            // EPS GeographicalInformation
            ShGeographicalInformation epsGeographicalInformation = shUdaAvpValues.getEpsGeographicalInformation();
            if (epsGeographicalInformation != null)
                epsGeographicalInfo = epsGeographicalInformation.getGeographicalInformation();
            // EPS GeodeticInformation
            ShGeodeticInformation epsGeodeticInformation = shUdaAvpValues.getEpsGeodeticInformation();
            if (epsGeodeticInformation != null)
                epsGeodeticInfo = epsGeodeticInformation.getGeodeticInformation();
            // EPS MME Name
            String shMmeName = shUdaAvpValues.getMmeName();
            if (shMmeName != null)
                mmeName = new DiameterIdentityImpl(shMmeName.getBytes());
            // EPS Current Location Retrieved and Age Of Location Information
            if (shUdaAvpValues.getEpsCurrentLocationInfoRetrieved() != null) {
                epsCurrentLocationRetrieved = shUdaAvpValues.getEpsCurrentLocationInfoRetrieved().equalsIgnoreCase("0") ||
                        shUdaAvpValues.getEpsCurrentLocationInfoRetrieved().equalsIgnoreCase("true");
            }
            Integer epsAgeOfLocationInformation = shUdaAvpValues.getEpsAgeOfLocationInfo();
            if (epsAgeOfLocationInformation != null) {
                if (epsAgeOfLocationInformation != 0)
                    epsCurrentLocationRetrieved = false;
            }

            locationInformationEPS = new LocationInformationEPSImpl(eUtranCellGlobalIdentity, trackingAreaIdentity, extensionContainer, epsGeographicalInfo,
                    epsGeodeticInfo, epsCurrentLocationRetrieved, epsAgeOfLocationInformation, mmeName);

            locationInformation = new LocationInformationImpl(csAgeOfLocationInformation, geographicalInformation, vlrNumber, locationNumberMap,
                    cellGlobalIdOrServiceAreaIdOrLAI, extensionContainer, lsaIdentity, mscNumber, geodeticInformation, csCurrentLocationRetrieved,
                    saiPresent, locationInformationEPS, userCSGInformation);
        }
        return locationInformation;
    }

    public static LocationInformationGPRS shPSLocationInfo2MapLocationInformationGPRS(ShUdaAvpValues shUdaAvpValues) {
        LocationInformationGPRS locationInformationGPRS = null;
        CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = null;
        RAIdentity routingAreaIdentity = null;
        GeographicalInformation psGeographicalInfo = null;
        GeodeticInformation psGeodeticInfo = null;
        ISDNAddressString sgsnNumber = null;
        boolean psCurrentLocationRetrieved = false;
        LSAIdentity lsaIdentity = null;
        MAPExtensionContainer extensionContainer = null;
        boolean saiPresent = false;
        if (shUdaAvpValues != null) {
            // PS CellGlobalId
            ShCellGlobalId cellGlobalId = shUdaAvpValues.getPsCellGlobalId();
            if (cellGlobalId != null) {
                CellGlobalIdOrServiceAreaIdFixedLength cgi = cellGlobalId.getCellGlobalIdOrServiceAreaIdFixedLength();
                cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(cgi);
            }
            // PS ServiceAreaId
            ShServiceAreaId serviceAreaId = shUdaAvpValues.getPsServiceAreaId();
            if (serviceAreaId != null) {
                CellGlobalIdOrServiceAreaIdFixedLength sai = serviceAreaId.getCellGlobalIdOrServiceAreaIdFixedLength();
                cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(sai);
            }
            // PS LocationAreaId
            ShLocationAreaId locationAreaId = shUdaAvpValues.getPsLocationAreaId();
            if (locationAreaId != null) {
                LAIFixedLength lai = locationAreaId.getLaiFixedLength();
                cellGlobalIdOrServiceAreaIdOrLAI = new CellGlobalIdOrServiceAreaIdOrLAIImpl(lai);
            }
            // PS RoutingAreaId
            ShRoutingAreaId routingAreaId = shUdaAvpValues.getRoutingAreaId();
            if (routingAreaId != null)
                routingAreaIdentity = new RAIdentityImpl(routingAreaId.getRoutingAreaIdentity().getData());
            // PS GeographicalInformation
            ShGeographicalInformation psGeographicalInformation = shUdaAvpValues.getPsGeographicalInformation();
            if (psGeographicalInformation != null)
                psGeographicalInfo = psGeographicalInformation.getGeographicalInformation();
            // PS GeodeticInformation
            ShGeodeticInformation psGeodeticInformation = shUdaAvpValues.getPsGeodeticInformation();
            if (psGeodeticInformation != null)
                psGeodeticInfo = psGeodeticInformation.getGeodeticInformation();
            // PS SGSN Address
            if (shUdaAvpValues.getSgsnNumber() != null) {
                String sgsnAddress = shUdaAvpValues.getSgsnNumber().getAddress();
                sgsnNumber = new ISDNAddressStringImpl(AddressNature.international_number, NumberingPlan.ISDN, sgsnAddress);
            }
            // PS Current Location Retrieved and Age Of Location Information
            if (shUdaAvpValues.getPsCurrentLocationInfoRetrieved() != null) {
                psCurrentLocationRetrieved = shUdaAvpValues.getPsCurrentLocationInfoRetrieved().equalsIgnoreCase("0") ||
                        shUdaAvpValues.getPsCurrentLocationInfoRetrieved().equalsIgnoreCase("true");
            }
            Integer psAgeOfLocationInformation = shUdaAvpValues.getPsAgeOfLocationInfo();
            if (psAgeOfLocationInformation != null) {
                if (psAgeOfLocationInformation != 0)
                    psCurrentLocationRetrieved = false;
            }

            locationInformationGPRS = new LocationInformationGPRSImpl(cellGlobalIdOrServiceAreaIdOrLAI, routingAreaIdentity, psGeographicalInfo,
                    sgsnNumber, lsaIdentity, extensionContainer, saiPresent, psGeodeticInfo, psCurrentLocationRetrieved, psAgeOfLocationInformation);
        }
        return locationInformationGPRS;
    }

    public static LocationInformation5GS sh5GSLocationInfo2LocationInformation5GS(ShUdaAvpValues shUdaAvpValues) {
        LocationInformation5GS locationInformation5GS = null;
        NRCellGlobalId nrCellGlobalIdentity = null;
        TrackingAreaId5GS trackingAreaId5GS = null;
        EUTRANCGI eUtranCellGlobalIdentity = null;
        TAId trackingAreaIdentity = null;
        GeographicalInformation sh5gsGeographicalInfo = null;
        String amfAddress;
        String smsfAddress;
        boolean sh5gsCurrentLocationRetrieved = false;
        Integer sh5gsAgeOfLocationInformation;
        PlmnId visitedPlmnId = null;
        String timeZone = null;
        Integer daylightSavingTime = null;
        Integer ratType;
        if (shUdaAvpValues != null) {
            // NRCellGlobalId
            ShNRCellGlobalId shNRCellGlobalId = shUdaAvpValues.getShNRCellGlobalId();
            if (shNRCellGlobalId != null)
                nrCellGlobalIdentity = new NRCellGlobalIdImpl(shUdaAvpValues.getShNRCellGlobalId().getNRCellGlobalId().getData());
            Sh5GSTrackingAreaId sh5GSTrackingAreaId = shUdaAvpValues.getSh5GSTrackingAreaId();
            if (sh5GSTrackingAreaId != null)
                trackingAreaId5GS = new TrackingAreaId5GSImpl(shUdaAvpValues.getSh5GSTrackingAreaId().get5GSTrackingAreaId().getData());
            // EUTRANCellGlobalId
            ShEUTRANCellGlobalId eUtranCellGlobalId = shUdaAvpValues.getEutrancgi();
            if (eUtranCellGlobalId != null)
                eUtranCellGlobalIdentity = new EUTRANCGIImpl(eUtranCellGlobalId.getEutranCgi().getData());
            // EPS TAI
            ShTrackingAreaId shTrackingAreaId = shUdaAvpValues.getTrackingAreaId();
            if (shTrackingAreaId != null)
                trackingAreaIdentity = new TAIdImpl(shTrackingAreaId.getTrackingAreaId().getData());
            // EPS GeographicalInformation
            ShGeographicalInformation sh5gsGeographicalInformation = shUdaAvpValues.getSh5GSGeographicalInformation();
            if (sh5gsGeographicalInformation != null)
                sh5gsGeographicalInfo = sh5gsGeographicalInformation.getGeographicalInformation();
            // AMF Address
            amfAddress = shUdaAvpValues.getAmfAddress();
            // SMSF Address
            smsfAddress = shUdaAvpValues.getSmsfAddress();
            // 5GS Current Location Retrieved and Age Of Location Information
            if (shUdaAvpValues.getSh5GSCurrentLocationInfoRetrieved() != null) {
                sh5gsCurrentLocationRetrieved = shUdaAvpValues.getSh5GSCurrentLocationInfoRetrieved().equalsIgnoreCase("0") ||
                        shUdaAvpValues.getSh5GSCurrentLocationInfoRetrieved().equalsIgnoreCase("true");
            }
            sh5gsAgeOfLocationInformation = shUdaAvpValues.getSh5GSAgeOfLocationInfo();
            if (sh5gsAgeOfLocationInformation != null) {
                if (sh5gsAgeOfLocationInformation != 0)
                    sh5gsCurrentLocationRetrieved = false;
            }
            // Visited PLMN Id
            ShVisitedPLMNId shVisitedPLMNId = shUdaAvpValues.getSh5gsVisitedPLMNId();
            if (shVisitedPLMNId != null)
                visitedPlmnId = new PlmnIdImpl(shVisitedPLMNId.getVisitedPlmnId().getData());
            // Local Time Zone
            if (shUdaAvpValues.getSh5gsLocalTimeZone() != null) {
                timeZone = shUdaAvpValues.getSh5gsLocalTimeZone().getTimeZone();
                daylightSavingTime = shUdaAvpValues.getSh5gsLocalTimeZone().getDaylightSavingTime();
            }
            // RAT Type
            ratType = shUdaAvpValues.getSh5gsRatType();

            locationInformation5GS = new LocationInformation5GSImpl(nrCellGlobalIdentity, trackingAreaId5GS, eUtranCellGlobalIdentity, trackingAreaIdentity, sh5gsGeographicalInfo,
                    amfAddress, smsfAddress, sh5gsCurrentLocationRetrieved, sh5gsAgeOfLocationInformation, visitedPlmnId, timeZone, daylightSavingTime, ratType);
        }
        return locationInformation5GS;
    }

    public static TypeOfShape int2TypeOfShape(int lteTypeOfShape) {
        TypeOfShape typeOfShape = null;
        if (lteTypeOfShape == 0)
            typeOfShape = TypeOfShape.EllipsoidPoint;
        if (lteTypeOfShape == 1)
            typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyCircle;
        if (lteTypeOfShape == 3)
            typeOfShape = TypeOfShape.EllipsoidPointWithUncertaintyEllipse;
        if (lteTypeOfShape == 5)
            typeOfShape = TypeOfShape.Polygon;
        if (lteTypeOfShape == 8)
            typeOfShape = TypeOfShape.EllipsoidPointWithAltitude;
        if (lteTypeOfShape == 9)
            typeOfShape = TypeOfShape.EllipsoidPointWithAltitudeAndUncertaintyEllipsoid;
        if (lteTypeOfShape == 10)
            typeOfShape = TypeOfShape.EllipsoidArc;
        return typeOfShape;
    }

}

