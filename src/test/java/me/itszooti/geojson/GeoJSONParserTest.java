package me.itszooti.geojson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Test;

public class GeoJSONParserTest {

	private GeoJSONParser parser;
	
	private GeoObject parseFile(String filename) {
		return parser.parse(this.getClass().getClassLoader().getResourceAsStream(filename));
	}
	
	@Before
	public void before() {
		parser = GeoJSONParser.create();
	}
	
	private void testPosition(GeoPosition position, double expectedX, double expectedY) {
		assertThat(position, notNullValue());
		assertThat(position, equalTo(new GeoPosition(expectedX, expectedY)));
	}
	
	@Test
	public void parsePoint() {
		GeoObject geo = parseFile("point.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoPoint.class));
		testPosition(((GeoPoint)geo).getPosition(), 100.0, 0.0);
	}
	
	@Test
	public void parseMultiPoint() {
		GeoObject geo = parseFile("multipoint.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoMultiPoint.class));
		GeoMultiPoint multiPoint = (GeoMultiPoint)geo;
		assertThat(multiPoint.getNumPoints(), equalTo(2));
		testPosition(multiPoint.getPoint(0).getPosition(), 100.0, 0.0);
		testPosition(multiPoint.getPoint(1).getPosition(), 101.0, 1.0);
	}
	
	private void testPositions(GeoPosition[] positions, double[][] expected) {
		assertThat(positions.length, equalTo(expected.length));
		for (int i = 0; i < positions.length; i++) {
			assertThat(positions[i], equalTo(new GeoPosition(expected[i][0], expected[i][1])));
		}
	}
	
	@Test
	public void parseLineString() {
		GeoObject geo = parseFile("linestring.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoLineString.class));
		GeoLineString lineString = (GeoLineString)geo;
		assertThat(lineString.getNumPositions(), equalTo(2));
		testPositions(lineString.getPositions(), new double[][] { new double[] { 100.0, 0.0 }, new double[] { 101.0, 1.0 } });
	}
	
	@Test
	public void parseMultiLineString() {
		GeoObject geo = parseFile("multilinestring.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoMultiLineString.class));
		GeoMultiLineString multiLineString = (GeoMultiLineString)geo;
		assertThat(multiLineString.getNumLineStrings(), equalTo(2));
		testPositions(multiLineString.getLineString(0).getPositions(), new double[][] {
			new double[] { 100.0, 0.0 }, new double[] { 101.0, 1.0 }
		});
		testPositions(multiLineString.getLineString(1).getPositions(), new double[][] {
			new double[] { 102.0, 2.0 }, new double[] { 103.0, 3.0 }
		});
	}
	
	@Test
	public void parsePolygonWithHoles() {
		GeoObject geo = parseFile("polygon-withholes.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoPolygon.class));
		GeoPolygon polygon = (GeoPolygon)geo;
	    testPositions(polygon.getExterior(), new double[][] {
	    	new double[] { 100.0, 0.0 },
	    	new double[] { 101.0, 0.0 },
	    	new double[] { 101.0, 1.0 },
	    	new double[] { 100.0, 1.0 },
	    	new double[] { 100.0, 0.0 }
	    });
	    assertThat(polygon.getNumInteriors(), equalTo(1));
	    testPositions(polygon.getInterior(0), new double[][] {
	    	new double[] { 100.2, 0.2 },
	    	new double[] { 100.8, 0.2 },
	    	new double[] { 100.8, 0.8 },
	    	new double[] { 100.2, 0.8 },
	    	new double[] { 100.2, 0.2 }
	    });
	}
	
	@Test
	public void parsePolygonNoHoles() {
		GeoObject geo = parseFile("polygon-noholes.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoPolygon.class));
		GeoPolygon polygon = (GeoPolygon)geo;
	    testPositions(polygon.getExterior(), new double[][] {
	    	new double[] { 100.0, 0.0 },
	    	new double[] { 101.0, 0.0 },
	    	new double[] { 101.0, 1.0 },
	    	new double[] { 100.0, 1.0 },
	    	new double[] { 100.0, 0.0 }
	    });
	    assertThat(polygon.getNumInteriors(), equalTo(0));
	}
	
	@Test
	public void parseMultiPolygon() {
		GeoObject geo = parseFile("multipolygon.json");
		assertThat(geo, notNullValue());
		assertThat(geo, instanceOf(GeoMultiPolygon.class));
		GeoMultiPolygon multiPolygon = (GeoMultiPolygon)geo;
		assertThat(multiPolygon.getNumPolygons(), equalTo(2));
		GeoPolygon polygonNoHoles = (GeoPolygon)multiPolygon.getPolygon(0);
	    testPositions(polygonNoHoles.getExterior(), new double[][] {
	    	new double[] { 102.0, 2.0 },
	    	new double[] { 103.0, 2.0 },
	    	new double[] { 103.0, 3.0 },
	    	new double[] { 102.0, 3.0 },
	    	new double[] { 102.0, 2.0 }
	    });
	    assertThat(polygonNoHoles.getNumInteriors(), equalTo(0));
		GeoPolygon polygonWithHoles = (GeoPolygon)multiPolygon.getPolygon(1);
	    testPositions(polygonWithHoles.getExterior(), new double[][] {
	    	new double[] { 100.0, 0.0 },
	    	new double[] { 101.0, 0.0 },
	    	new double[] { 101.0, 1.0 },
	    	new double[] { 100.0, 1.0 },
	    	new double[] { 100.0, 0.0 }
	    });
		assertThat(polygonWithHoles.getNumInteriors(), equalTo(1));
	    testPositions(polygonWithHoles.getInterior(0), new double[][] {
	    	new double[] { 100.2, 0.2 },
	    	new double[] { 100.8, 0.2 },
	    	new double[] { 100.8, 0.8 },
	    	new double[] { 100.2, 0.8 },
	    	new double[] { 100.2, 0.2 }
	    });
	}
//	
//	@Test
//	public void parseGeometryCollection() {
//		Geometry geom = parseFile("geometrycollection.json");
//		assertThat(geom, notNullValue());
//		assertThat(geom, instanceOf(GeometryCollection.class));
//	    GeometryCollection gc = (GeometryCollection)geom;
//	    assertThat(gc.getNumGeometries(), equalTo(2));
//	    assertThat(gc.getGeometryN(0), instanceOf(Point.class));
//	    Point p = (Point)gc.getGeometryN(0);
//	    assertThat(gc.getGeometryN(1), instanceOf(LineString.class));
//	    testPoint(p, 100.0, 0.0);
//	    LineString ls = (LineString)gc.getGeometryN(1);
//	    testLineString(ls, new double[][] { new double[] { 101.0, 0.0 }, new double[] { 102.0, 1.0 } });
//	}
//	
//	@Test
//	public void parseCRS() {
//		assertThat(true, equalTo(false));
//	}
//	
//	@Test
//	public void parseFeature() {
//		assertThat(true, equalTo(false));
//	}
//	
//	@Test
//	public void parseFeatureCollection() {
//		assertThat(true, equalTo(false));
//	}
	
}
