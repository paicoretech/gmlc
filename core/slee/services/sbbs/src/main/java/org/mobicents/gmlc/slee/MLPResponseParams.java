package org.mobicents.gmlc.slee;

import org.mobicents.gmlc.slee.primitives.Polygon;

import java.io.Serializable;

/**
 * Inner class for MLP response parameters
 */
class MLPResponseParams implements Serializable {

   /*********************/
   /*** MLP Response ***/
   /*******************/
   String mlpMsisdn;
   Integer mlpMcc, mlpMnc, mlpLac, mlpCi, mlpSac, mlpTac, mlpRac, mlpNrTac;
   Long mlpUci, mlpEci, mlpEnbId, mlpNci;
   String mlpVlrNo, mlpMscNo, mlpMmeName, mlpMmeRealm, mlpSgsnName, mlpSgsnRealm, mlpSgsnNumber, mlpAmfAddress;
   String mlpState;
   Integer mlpAge;
   Double x;
   Double y;
   String mlpTypeOfShape;
   Double radius;
   Polygon mlpPolygon;
   Integer mlpNumberOfPoints;
   Double mlpUncertainty, mlpUncertaintySemiMajorAxis, mlpUncertaintySemiMinorAxis, mlpAngleOfMajorAxis,
           mlpUncertaintyAltitude, mlpUncertaintyInnerRadius, mlpOffsetAngle, mlpIncludedAngle;
   Integer mlpConfidence, mlpAltitude, mlpInnerRadius, numberOfPoints;
   Integer mlpAgeOfLocationEstimate, mlpAccuracyFulfilmentIndicator;
   String mlpPositioningMethod;
   Integer mlpTargetHorizontalSpeed, mlpTargetVerticalSpeed, mlpUncertaintyHorizontalSpeed, mlpUncertaintyVerticalSpeed, mlpBearing;
   String mlpVelocityType;
   String mlpImei, mlpImsi, mlpLmsi;
   String mlpCivicAddress;
   Long mlpBarometricPressure;
   Integer mlpTransId;
   Integer mlpLcsRefNumber;
   Integer mlpRatType;
}
