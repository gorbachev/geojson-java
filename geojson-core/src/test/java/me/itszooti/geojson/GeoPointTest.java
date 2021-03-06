package me.itszooti.geojson;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class GeoPointTest {

	private GeoPoint point;
	
	@Before
	public void before() {
		point = new GeoPoint(new GeoPosition(100.0, 0.0));
	}
	
	@Test
	public void getPosition() {
		GeoPosition position = point.getPosition();
		assertThat(position, notNullValue());
		assertThat(position, equalTo(new GeoPosition(100.0, 0.0)));
	}
	
	@Test
	public void isGeoGeometry() {
		assertThat(point, instanceOf(GeoGeometry.class));
	}
	
}
