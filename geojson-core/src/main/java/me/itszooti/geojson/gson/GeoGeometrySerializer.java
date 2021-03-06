package me.itszooti.geojson.gson;

import java.lang.reflect.Type;

import me.itszooti.geojson.GeoGeometry;
import me.itszooti.geojson.GeoLineString;
import me.itszooti.geojson.GeoMultiLineString;
import me.itszooti.geojson.GeoMultiPoint;
import me.itszooti.geojson.GeoMultiPolygon;
import me.itszooti.geojson.GeoPoint;
import me.itszooti.geojson.GeoPolygon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GeoGeometrySerializer implements JsonSerializer<GeoGeometry> {

	@Override
	public JsonElement serialize(GeoGeometry geom, Type type, JsonSerializationContext context) {
		// always an object with type
		JsonObject obj = new JsonObject();
		obj.addProperty("type", geom.getClass().getSimpleName().substring(3));
		
		// check type
		if (geom instanceof GeoPoint) {
			GeoPoint point = (GeoPoint)geom;
			obj.add("coordinates", context.serialize(point.getPosition()));
		} else if (geom instanceof GeoLineString) {
			GeoLineString lineString = (GeoLineString)geom;
			obj.add("coordinates", context.serialize(lineString.getPositions()));
		} else if (geom instanceof GeoPolygon) {
			GeoPolygon polygon = (GeoPolygon)geom;
			JsonArray coords = new JsonArray();
			coords.add(context.serialize(polygon.getExterior()));
			for (int i = 0; i < polygon.getNumInteriors(); i++) {
				coords.add(context.serialize(polygon.getInterior(i)));
			}
			obj.add("coordinates", coords);
		} else if (geom instanceof GeoMultiPoint) {
			GeoMultiPoint multiPoint = (GeoMultiPoint)geom;
			JsonArray coords = new JsonArray();
			for (int i = 0; i < multiPoint.getNumPoints(); i++) {
				coords.add(context.serialize(multiPoint.getPoint(i).getPosition()));
			}
			obj.add("coordinates", coords);
		} else if (geom instanceof GeoMultiLineString) {
			GeoMultiLineString multiLineString = (GeoMultiLineString)geom;
			JsonArray coords = new JsonArray();
			for (int i = 0; i < multiLineString.getNumLineStrings(); i++) {
				coords.add(context.serialize(multiLineString.getLineString(i).getPositions()));
			}
			obj.add("coordinates", coords);
		} else if (geom instanceof GeoMultiPolygon) {
			GeoMultiPolygon multiPolygon = (GeoMultiPolygon)geom;
			JsonArray coords = new JsonArray();
			for (int i = 0; i < multiPolygon.getNumPolygons(); i++) {
				GeoPolygon polygon = multiPolygon.getPolygon(i);
				JsonArray polyCoords = new JsonArray();
				polyCoords.add(context.serialize(polygon.getExterior()));
				for (int j = 0; j < polygon.getNumInteriors(); j++) {
					polyCoords.add(context.serialize(polygon.getInterior(j)));
				}
				coords.add(polyCoords);
			}
			obj.add("coordinates", coords);
		}
		
		return obj;
	}

}
