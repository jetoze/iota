package jetoze.iota;

import static jetoze.iota.Card.newCard;
import static jetoze.iota.Card.wildcard;
import static jetoze.iota.Constants.Color.BLUE;
import static jetoze.iota.Constants.Color.GREEN;
import static jetoze.iota.Constants.Color.RED;
import static jetoze.iota.Constants.Color.YELLOW;
import static jetoze.iota.Constants.Shape.CIRCLE;
import static jetoze.iota.Constants.Shape.CROSS;
import static jetoze.iota.Constants.Shape.SQUARE;
import static jetoze.iota.Constants.Shape.TRIANGLE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public final class PlayerTest {

	@Test
	public void addPoints() {
		Player p = new Player("Beth");
		p.addPoints(4);
		assertEquals(4, p.getPoints());
		PlayerObserver o = mock(PlayerObserver.class);
		p.addObserver(o);
		p.addPoints(6);
		assertEquals(10, p.getPoints());
		verify(o).pointsChanged(p, 10);
	}
	
	@Test
	public void noCardsInitially() {
		Player p = new Player("Nick");
		assertTrue(p.needsCards());
		assertTrue(p.noCardsLeft());
		assertTrue(p.getCards().isEmpty());
	}
	
	@Test
	public void giveCard() {
		Player p = new Player("Maggie");
		List<Card> expected = new ArrayList<>();
		Card blueSquare1 = newCard(BLUE, SQUARE, 1);
		p.giveCard(blueSquare1);
		expected.add(blueSquare1);
		assertEquals(expected, p.getCards());
		Card redCross4 = newCard(RED, CROSS, 4);
		p.giveCard(redCross4);
		expected.add(redCross4);
		assertEquals(expected, p.getCards());
	}
	
	@Test(expected=IllegalStateException.class)
	public void giveCardShouldFailWhenHandIsFull() {
		Player p = new Player("Joe");
		p.giveCards(wildcard(), wildcard(), wildcard(), wildcard());
		p.giveCard(newCard(GREEN, TRIANGLE, 3));
	}
	
	@Test
	public void placeOnBoardAndReturn() {
		Player p = new Player("Helen");
		Card blueSquare1 = newCard(BLUE, SQUARE, 1);
		Card redCircle2 = newCard(RED, CIRCLE, 2);
		Card greenTriangle3 = newCard(GREEN, TRIANGLE, 3);
		Card yellowCross4 = newCard(YELLOW, CROSS, 4);
		p.giveCards(blueSquare1, redCircle2, greenTriangle3, yellowCross4);
		assertEquals(Arrays.asList(blueSquare1, redCircle2, greenTriangle3, yellowCross4), p.getCards());
		
		PlacedCard pc1 = p.placeOnBoard(blueSquare1, new Position(0, 1));
		assertEquals(Arrays.asList(redCircle2, greenTriangle3, yellowCross4), p.getCards());
		
		PlacedCard pc2 = p.placeOnBoard(greenTriangle3, new Position(0, 2));
		assertEquals(Arrays.asList(redCircle2, yellowCross4), p.getCards());
		
		PlacedCard pc3 = p.placeOnBoard(redCircle2, new Position(0, 3));
		assertEquals(Arrays.asList(yellowCross4), p.getCards());
		
		pc2.returnToHand();
		assertEquals(Arrays.asList(greenTriangle3, yellowCross4), p.getCards());
		
		pc1.returnToHand();
		assertEquals(Arrays.asList(blueSquare1, greenTriangle3, yellowCross4), p.getCards());
		
		pc3.returnToHand();
		assertEquals(Arrays.asList(blueSquare1, redCircle2, greenTriangle3, yellowCross4), p.getCards());
		
		try {
			pc3.returnToHand();
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
	
}
