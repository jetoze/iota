package jetoze.iota;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;
import jetoze.iota.GameAction.Result;

public class PassActionTest {

	private Player player;
	
	private final Grid grid = new Grid();
	
	@Before
	public void setup() {
		// Create a new player each time to start with an empty set of cards.
		player = new Player("Wilhelm");
	}
	
	@Test
	public void noCardsToChange() {
		PassAction pa = new PassAction();
		Result result = pa.invoke(player, grid, Deck.of(Card.wildcard(), Card.wildcard()));
		assertTrue(result.isSuccess());
	}
	
	@Test
	public void notEnoughCards() {
		Deck oneCardDeck = Deck.of(Card.wildcard());
		PassAction pa = new PassAction(Arrays.asList(
				Card.newCard(Color.BLUE, Shape.SQUARE, 1),
				Card.newCard(Color.GREEN, Shape.CROSS, 2)));
		Result result = pa.invoke(player, grid, oneCardDeck);
		assertFalse(result.isSuccess());
		assertFalse(result.getError().isEmpty());
	}
	
	@Test
	public void tradeOneCard() {
		Card blueSquare1 = Card.newCard(Color.BLUE, Shape.SQUARE, 1);
		Card greenCross2 = Card.newCard(Color.GREEN, Shape.CROSS, 2);
		Deck deck = Deck.of(blueSquare1, greenCross2);
		
		Card redCircle3 = Card.newCard(Color.RED, Shape.CROSS, 3);
		Card yellowTriangle4 = Card.newCard(Color.YELLOW, Shape.TRIANGLE, 4);
		player.giveCard(redCircle3);
		player.giveCard(yellowTriangle4);
		
		PassAction pa = new PassAction(Arrays.asList(redCircle3));
		Result result = pa.invoke(player, grid, deck);
		assertTrue(result.isSuccess());
		
		ImmutableSet<Card> expectedPlayerCards = ImmutableSet.of(blueSquare1, yellowTriangle4);
		ImmutableSet<Card> actualPlayerCards = ImmutableSet.copyOf(player.getCards());
		assertEquals(expectedPlayerCards, actualPlayerCards);
		
		assertEquals(2, deck.cardsLeft());
		assertSame(greenCross2, deck.next());
		assertSame(redCircle3, deck.next());
	}
	
}
