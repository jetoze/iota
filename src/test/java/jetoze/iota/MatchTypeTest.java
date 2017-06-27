package jetoze.iota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public class MatchTypeTest {

	@Test
	public void collectCandidatesForNextCardForSAME() {
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
		Set<Card> actual = MatchType.SAME.collectCandidatesForNextCard(line);
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
		actual = MatchType.SAME.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);

		// Adding a wildcard to the line should not change the result.
		line = Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.BLUE, Shape.CIRCLE, 1),
				Card.wildcard());
		// Expected result is any card that's blue, or has the facevalue 1, because 
		// both those properties are common.
		actual = MatchType.SAME.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);

		line = Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.BLUE, Shape.CIRCLE, 1),
				Card.newCard(Color.GREEN, Shape.CROSS, 2));
		assertTrue(MatchType.SAME.collectCandidatesForNextCard(line).isEmpty());
	}

	@Test
	public void collectCandidatesForNextCardForDIFFERENT() {
		List<Card> line = Lists.newArrayList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.GREEN, Shape.CIRCLE, 4));
		// Expected result is any card that's red or yellow, is a triangle or
		// cross, and has face value 2 or 3.
		Set<Card> expected = new HashSet<>();
		for (Color c : EnumSet.of(Color.RED, Color.YELLOW)) {
			for (Shape s : EnumSet.of(Shape.TRIANGLE, Shape.CROSS)) {
				expected.add(Card.newCard(c, s, 2));
				expected.add(Card.newCard(c, s, 3));
			}
		}
		Set<Card> actual = MatchType.DIFFERENT.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);
		
		// Adding a wildcard to the line should not change the result.
		line.add(Card.wildcard());
		// Expected result is any card that's blue, or has the facevalue 1, because 
		// both those properties are common.
		actual = MatchType.DIFFERENT.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);

		// Collapse the number of choices to a single card
		line.add(Card.newCard(Color.RED, Shape.CROSS, 2));
		expected = Collections.singleton(Card.newCard(Color.YELLOW, Shape.TRIANGLE, 3));
		actual = MatchType.DIFFERENT.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);
		
		line.add(Card.newCard(Color.YELLOW, Shape.TRIANGLE, 3));
		assertTrue(MatchType.DIFFERENT.collectCandidatesForNextCard(line).isEmpty());
	}


	@Test
	public void collectCandidatesForNextCardForEITHER() {
		List<Card> line = Lists.newArrayList(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		// Expected result is any card.
		Set<Card> expected = Card.createPossibleCards(Constants.collectAllCardProperties());
		Set<Card> actual = MatchType.EITHER.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);
		
		// Adding a wildcard to the line should not change the result.
		line.add(Card.wildcard());
		// Expected result is any card that's blue, or has the facevalue 1, because 
		// both those properties are common.
		actual = MatchType.EITHER.collectCandidatesForNextCard(line);
		assertEquals(expected, actual);
	}
}
