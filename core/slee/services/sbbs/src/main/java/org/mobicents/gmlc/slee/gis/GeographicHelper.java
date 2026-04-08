package org.mobicents.gmlc.slee.gis;

import org.apache.log4j.Logger;

import java.awt.geom.Point2D;

/**
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class GeographicHelper {

    private static final Logger logger = Logger.getLogger(GeographicHelper.class.getName());

    /**
     * Function to calculate the area of a polygon, according to the algorithm
     * defined at <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">...</a>
     *
     * @param polygonPoints array of points in the polygon
     * @return area of the polygon defined by pgPoints
     */
    public static double polygonArea(Point2D[] polygonPoints) {
        int i, j, n = polygonPoints.length;
        double area = 0;

        for (i = 0; i < n; i++) {
            j = (i + 1) % n;
            area += polygonPoints[i].getX() * polygonPoints[j].getY();
            area -= polygonPoints[j].getX() * polygonPoints[i].getY();
        }
        area /= 2.0;
        return (area);
    }

    /**
     * Function to calculate the center of mass for a given polygon, according
     * to the algorithm defined at <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">...</a>
     *
     * @param polygonPoints array of points in the polygon
     * @return point that is the center of mass of the polygon (centroid)
     */
    public static Point2D polygonCentroid(Point2D[] polygonPoints) {
        double cX = 0, cY = 0;
        double area = polygonArea(polygonPoints);
        Point2D centroid = new Point2D.Double();
        int i, j, n = polygonPoints.length;

        double factor;
        for (i = 0; i < n; i++) {
            j = (i + 1) % n;
            factor = (polygonPoints[i].getX() * polygonPoints[j].getY() - polygonPoints[j].getX() * polygonPoints[i].getY());
            cX += (polygonPoints[i].getX() + polygonPoints[j].getX()) * factor;
            cY += (polygonPoints[i].getY() + polygonPoints[j].getY()) * factor;
        }
        area *= 6.0f;
        factor = 1 / area;
        cX *= factor;
        cY *= factor;
        centroid.setLocation(cX, cY);
        return centroid;
    }

    /**
     * Function to calculate the distance between two points X, Y, defined by their geographic coordinates
     *
     * @param latX          latitude of point X
     * @param longX         longitude of point X
     * @param latY          latitude of point Y
     * @param longY         longitude of point Y
     * @return              distance in metres between X and Y
     */
    public static Double calculateGeoDistance(double latX, double longX, double latY, double longY) {

        // Coordinates conversion from double to radians
        latX = Math.toRadians(latX);
        longX = Math.toRadians(longX);
        latY = Math.toRadians(latY);
        longY = Math.toRadians(longY);

        // Haversine formula
        double longitudeDistance = longY - longX;
        double latitudeDistance = latY - latX;
        double a = (Math.pow(Math.sin(latitudeDistance/2),2)) + (Math.cos(latX) * Math.cos(latY) * Math.pow(Math.sin(longitudeDistance/2),2));
        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in metres
        double r = 6371008.8;

        // return the distance in metres
        return(c * r);
    }

    /**
     * Function to validate if the coordinate reference system is WGS84
     *
     * @param coordinates   latitude or longitude coordinates
     * @return              is or not WGS84
     */
    public static Boolean validateWGS84CoordinatesReferenceSystem(String coordinates) {

        //String degrees = "\\u00b0";
        String degrees = "º";
        String minutes = "'";
        boolean isWGS84;
        Boolean pattern1 = coordinates.matches("[NWSE]{1}\\d{1,3}\\s\\d{1,2}\\s\\d{1,2}\\.\\d{1,2}$");
        Boolean pattern2 = coordinates.matches("\\d{1,3}\\s\\d{1,2}\\s\\d{1,2}\\.\\d{1,2}[NWSE]{1}$");
        Boolean pattern3 = coordinates.matches("\\d{1,3}[" + degrees + "]\\d{1,3}[" + minutes + "]\\d{1,2}\\.\\d{1,2}["
            + minutes + "][" + minutes + "][NWSE]{1}$");
        Boolean pattern4 = coordinates.matches("[NWSE]{1}\\d{1,3}[" + degrees + "]\\d{1,3}[" + minutes + "]\\d{1,2}\\.\\d{1,2}["
            + minutes + "][" + minutes + "]$");
        Boolean pattern5 = coordinates.matches("\\d{1,3}\\s\\d{1,2}\\s\\d{1,2}\\.\\d{1,2}$");
        Boolean pattern6 = coordinates.matches("-?\\d{1,3}\\s\\d{1,2}\\s\\d{1,2}\\.\\d{1,2}$");
        Boolean pattern7 = coordinates.matches("-?\\d+(\\.\\d+)?");

        if (pattern1 || pattern2 || pattern3 || pattern4 || pattern5 || pattern6 || pattern7) {
            isWGS84 = true;
            return isWGS84;
        } else {
            isWGS84 = false;
            return isWGS84;
        }
    }


    public static void main(String[] args) {
        logger.info("Geodetic distance between Montevideo and Punta del Este (Uruguay) = " + calculateGeoDistance(-34.9011, -56.1645, -34.9363, -54.9378) / 1000 + " kilometres");
        logger.info("Geodetic distance between New York (USA) and Paris (France) = " + calculateGeoDistance(40.7128, -74.0060, 48.8566, 2.3522) / 1000 + " kilometres");
        logger.info(validateWGS84CoordinatesReferenceSystem("34º54'23.73''S"));
        logger.info(validateWGS84CoordinatesReferenceSystem("30 27 45.3N"));
    }

}
