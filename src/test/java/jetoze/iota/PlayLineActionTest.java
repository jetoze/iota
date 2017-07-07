package jetoze.iota;

import static jetoze.iota.Constants.Color.BLUE;
import static jetoze.iota.Constants.Color.GREEN;
import static jetoze.iota.Constants.Color.RED;
import static jetoze.iota.Constants.Color.YELLOW;
import static jetoze.iota.Constants.Shape.CIRCLE;
import static jetoze.iota.Constants.Shape.CROSS;
import static jetoze.iota.Constants.Shape.SQUARE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import jetoze.iota.Constants.Shape;
import jetoze.iota.GameAction.Result;

public final class PlayLineActionTest {

	private Player player;
	
	private Grid grid;
	
	@Before
	public void setup() {
		player = new Player("Wallace");
		grid = new Grid();
	}

	@Test
	public void addLineAndCollectPoints() {
		grid.start(Card.newCard(BLUE, SQUARE, 1));
		Card blueCircle2 = Card.newCard(BLUE, CIRCLE, 2);
		Card greenCircle2 = Card.newCard(GREEN, CIRCLE, 2);
		Card redCross4 = Card.newCard(RED, CROSS, 4);
		Card blueTriangle1 = Card.newCard(BLUE, Shape.TRIANGLE, 1);
		// Play greenCircle2 and redCross4 for a unique line. The other two cards 
		// remains on the players hand.
		player.giveCards(blueCircle2, blueTriangle1);
		
		Card wc = Card.wildcard();
		Card yellowCross3 = Card.newCard(YELLOW, CROSS, 3);
		Card greenSquare2 = Card.newCard(GREEN, SQUARE, 2);
		Deck deck = Deck.of(wc, yellowCross3, greenSquare2);
		
		PlayLineAction action = new PlayLineAction(Arrays.asList(
				new LineItem(greenCircle2, 0, 1), new LineItem(redCross4, 0, 2)));
		Result result = action.invoke(player, grid, deck);
		assertTrue(result.isSuccess());
		assertEquals(1 + 2 + 4, player.getPoints());
		assertEquals(4, player.getCards().size());
		assertEquals(ImmutableSet.of(blueCircle2, blueTriangle1, wc, yellowCross3),
				ImmutableSet.copyOf(player.getCards()));
		assertEquals(1, deck.cardsLeft());
		assertSame(greenSquare2, deck.peek());
		
		// Play another line, vertically, with blue cards.
		player.removeCard(blueCircle2);
		player.removeCard(blueTriangle1);
		action = new PlayLineAction(Arrays.asList(
				new LineItem(blueCircle2, 1, 0), new LineItem(blueTriangle1, 2, 0)));
		result = action.invoke(player, grid, deck);
		assertTrue(result.isSuccess());
		assertEquals((1 + 2 + 4) + (1 + 1 + 2), player.getPoints());
		// The deck only had one card left, so the player should have three cards on
		// hand now.
		assertEquals(3, player.getCards().size());
		assertEquals(ImmutableSet.of(wc, yellowCross3, greenSquare2),
				ImmutableSet.copyOf(player.getCards()));
		assertTrue(deck.isEmpty());
	}

	@Test
	public void invalidLineShouldNotChangeState() {
		grid.start(Card.newCard(BLUE, SQUARE, 1));
		Card blueCircle2 = Card.newCard(BLUE, CIRCLE, 2);
		Card greenCircle2 = Card.newCard(GREEN, CIRCLE, 2);
		Card redCross4 = Card.newCard(RED, CROSS, 4);
		Card blueTriangle1 = Card.newCard(BLUE, Shape.TRIANGLE, 1);
		// Try to play blueCircle2 and redCross4. The other two cards remains on the 
		// players hand.
		player.giveCards(greenCircle2, blueTriangle1);

		Card wc = Card.wildcard();
		Card yellowCross3 = Card.newCard(YELLOW, CROSS, 3);
		Card greenSquare2 = Card.newCard(GREEN, SQUARE, 2);
		Deck deck = Deck.of(wc, yellowCross3, greenSquare2);

		PlayLineAction action = new PlayLineAction(Arrays.asList(
				new LineItem(blueCircle2, 0, 1), new LineItem(redCross4, 0, 2)));
		Result result = action.invoke(player, grid, deck);
		assertFalse(result.isSuccess());
		assertEquals(0, player.getPoints());
		// Note that the cards are not returned to the player's hand in this case.
		// The card remains on the board, in case the player just placed a card on the
		// wrong cell, or placed one wrong card. (The cards will be returned to the
		// player's hand at the end of the turn if the player opts to Pass instead.)
		assertEquals(2, player.getCards().size());
		assertEquals(ImmutableSet.of(greenCircle2, blueTriangle1),
				ImmutableSet.copyOf(player.getCards()));
		assertEquals(3, deck.cardsLeft());
		assertSame(wc, deck.peek());
	}
	

}
