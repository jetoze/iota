package jetoze.iota;

public final class GameState {

	private final Player player1 = new Player("John");
	
	private final Player player2 = new Player("Alice");
	
	private final Deck deck = Deck.shuffled();
	
	private final Grid grid = new Grid();
	
	private Player playerInTurn = player1;
	
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

	public void switchPlayer() {
		this.playerInTurn = (this.playerInTurn == this.player1)
				? this.player2
				: this.player1;
	}
	
}
