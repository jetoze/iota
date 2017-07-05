package jetoze.iota;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public final class GameState {

	private final Player player1 = new Player("John");
	
	private final Player player2 = new Player("Alice");
	
	private final Deck deck = Deck.shuffled();
	
	private final Grid grid = new Grid();
	
	private Player playerInTurn = player1;
	
	@Nullable
	private Card selectedPlayerCard;
	
	private final List<LineItem> placedCards = new ArrayList<>();
	
	public GameState() {
		giveCardsToPlayers();
		placeFirstCard();
	}

	private void giveCardsToPlayers() {
		for (int n = 0; n < Constants.NUMBER_OF_CARDS_PER_PLAYER; ++n) {
			player1.giveCard(deck.next());
			player2.giveCard(deck.next());
		}
	}
	
	private void placeFirstCard() {
		Card card = deck.next();
		grid.start(card);
	}
	
	public void completeTurn(GameAction action) {
		action.perform(playerInTurn, grid, deck);
		switchPlayer();
	}

	private void switchPlayer() {
		this.playerInTurn = (this.playerInTurn == this.player1)
				? this.player2
				: this.player1;
	}
	
}
