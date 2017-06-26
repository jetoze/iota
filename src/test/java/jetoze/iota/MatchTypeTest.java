package jetoze.iota;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public class MatchTypeTest {

	@Test
	public void collectPossibleWildcardRepsForSAME() {
		List<Card> nonWildcards = Arrays.asList(
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
		Set<Card> actual = MatchType.SAME.collectPossibleWildcardRepresentations(nonWildcards);
		assertEquals(expected, actual);
	}

}
