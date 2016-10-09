package org.traccar.helper;

import org.apache.log4j.Logger;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.traccar.model.Position;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * @author jgraham 
 * 
 * Takes a position and converts the lat/lng to an alternative coordinate reference system so it can easily be consumed by other applications
 */
public class GeomReferenceSystemConverterUtil {
	
	// 27700 is British National Grid
	private static final String TARGET_CRS = "EPSG:27700";
	// 4326 is the reference system for the Global Positioning System (GPS) and used by Google maps
	private static final String SOURCE_CRS = "EPSG:4326";

	public static void convertLatLng(Position position) {
		GeometryFactory geometryFactory = new GeometryFactory();

		Point point = geometryFactory.createPoint(new Coordinate(position.getLongitude(), position.getLatitude()));

		try {
			CoordinateReferenceSystem sourceCRS = CRS.decode(SOURCE_CRS);
			CoordinateReferenceSystem targetCRS = CRS.decode(TARGET_CRS);

			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			Point targetGeometry = (Point) JTS.transform(point, transform);

			position.setAlternativeLatitude(targetGeometry.getX());
			position.setAlternativeLongitude(targetGeometry.getY());
		} catch (Exception e) {
			Log.error("Failed to convert coordinate reference system on a point. Point: " + position);
		}
	}
}
