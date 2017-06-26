package jetoze.iota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public class CardTest {

	@Test
	public void matchProperties() {
		Card blueSquareOne = Card.newCard(Color.BLUE, Shape.SQUARE, 1);
		Set<Object> s0 = blueSquareOne.getMatchProperties();
		assertEquals(s0, blueSquareOne.match(s0));
		Set<Object> s1 = Card.newCard(Color.BLUE, Shape.TRIANGLE, 1).match(s0);
		assertTrue(!s1.isEmpty());
		Set<Object> s2 = Card.newCard(Color.GREEN, Shape.TRIANGLE, 1).match(s1);
		assertTrue(!s1.isEmpty());
		Set<Object> s3 = Card.newCard(Color.GREEN, Shape.CROSS, 2).match(s2);
		assertTrue(s3.isEmpty());
	}
	
	@Test
	public void matchPropertiesForWildcard() {
		Card blueSquareOne = Card.newCard(Color.BLUE, Shape.SQUARE, 1);
		Set<Object> s0 = blueSquareOne.getMatchProperties();
		Card wc = Card.wildcard();
		assertEquals(s0, wc.match(s0));
	}
	
}
