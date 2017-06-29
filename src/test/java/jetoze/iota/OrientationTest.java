package jetoze.iota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public final class OrientationTest {

	@Test(expected = IllegalArgumentException.class)
	public void needAtLeastTwoPoints() {
		Orientation.of(Collections.singletonList(new Position(0, 0)));
	}
	
	@Test
	public void detectHorizontal() {
		List<Position> points = Lists.newArrayList(
				new Position(0, 0), new Position(0, 1));
		Orientation detected = Orientation.of(points);
		assertEquals(Orientation.HORIZONTAL, detected);
		
		points.add(new Position(0, 2));
		detected = Orientation.of(points);
		assertEquals(Orientation.HORIZONTAL, detected);
		
		points.add(new Position(0, -1));
		detected = Orientation.of(points);
		assertEquals(Orientation.HORIZONTAL, detected);
	}
	
	@Test
	public void detectVertical() {
		List<Position> points = Lists.newArrayList(
				new Position(0, 0), new Position(1, 0));
		Orientation detected = Orientation.of(points);
		assertEquals(Orientation.VERTICAL, detected);
		
		points.add(new Position(2, 0));
		detected = Orientation.of(points);
		assertEquals(Orientation.VERTICAL, detected);
		
		points.add(new Position(-1, 0));
		detected = Orientation.of(points);
		assertEquals(Orientation.VERTICAL, detected);
	}
	
	@Test
	public void gapsAreOk() {
		assertEquals(Orientation.HORIZONTAL, Orientation.of(Arrays.asList(
					new Position(0, 0),
					new Position(0, 1),
					new Position(0, 3))));
		assertEquals(Orientation.VERTICAL, Orientation.of(Arrays.asList(
					new Position(0, 0),
					new Position(1, 0),
					new Position(3, 0))));
	}
	
	@Test
	public void detectInvalidLine() {
		try {
			Orientation.of(Arrays.asList(
					new Position(0, 0),
					new Position(0, 1),
					new Position(1, 2)));
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue("expected", true);
		}
	}
	
}
