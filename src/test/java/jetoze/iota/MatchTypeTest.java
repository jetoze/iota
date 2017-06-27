package jetoze.iota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public class MatchTypeTest {

	@Test
	public void collectNextCardCandidatesForSAME() {
		List<Card> line = Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.BLUE, Shape.CIRCLE, 4));
		// Expected result is any card that's blue, because that's the only
		// shared property.
		Set<Card> expected = new HashSet<>();
		for (Shape s : Shape.values()) {
			for (int fv = Constants.MIN_FACE_VALUE; fv <= Constants.MAX_FACE_VALUE; ++fv) {
				expected.add(Card.newCard(Color.BLUE, s, fv));
			}
		}
		Set<Card> actual = MatchType.SAME.collectNextCardCandidates(line);
		assertEquals(expected, actual);
		
		line = Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.BLUE, Shape.CIRCLE, 1));
		// Expected result is any card that's blue, or has the facevalue 1, because 
		// both those properties are common.
		expected = new HashSet<>();
		for (Color c : Color.values()) {
			for (Shape s : Shape.values()) {
				expected.add(Card.newCard(c, s, 1));
			}
		}
		for (Shape s : Shape.values()) {
			for (int fv = Constants.MIN_FACE_VALUE; fv <= Constants.MAX_FACE_VALUE; ++fv) {
				expected.add(Card.newCard(Color.BLUE, s, fv));
			}
		}
		actual = MatchType.SAME.collectNextCardCandidates(line);
		assertEquals(expected, actual);

		// Adding a wildcard to the line should not change the result.
		line = Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.BLUE, Shape.CIRCLE, 1),
				Card.wildcard());
		// Expected result is any card that's blue, or has the facevalue 1, because 
		// both those properties are common.
		actual = MatchType.SAME.collectNextCardCandidates(line);
		assertEquals(expected, actual);

		line = Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.BLUE, Shape.CIRCLE, 1),
				Card.newCard(Color.GREEN, Shape.CROSS, 2));
		assertTrue(MatchType.SAME.collectNextCardCandidates(line).isEmpty());
	
	}

}
