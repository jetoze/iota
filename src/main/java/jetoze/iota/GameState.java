package jetoze.iota;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.ImmutableList;

import jetoze.iota.GameAction.Result;

public final class GameState {
	
	private final ImmutableList<Player> players;
	
	private final Deck deck = Deck.shuffled();
	
	private final Grid grid = new Grid();
	
	private Player playerInTurn;

	private final List<LineItem> selectedPlayerCards = new ArrayList<>();
	
	private final List<LineItem> placedCards = new ArrayList<>();
	
	private final List<GameStateObserver> observers = new CopyOnWriteArrayList<>();
	
	public GameState() {
		this(ImmutableList.of(new Player("Alice"), new Player("John")));
	}
	
	public GameState(List<Player> players) {
		checkState(players.size() >= 2, "Must have at least two players");
		this.players = ImmutableList.copyOf(players);
	}

	public void start() {
		this.playerInTurn = this.players.get(0);
		giveCardsToPlayers();
		placeFirstCard();
	}
	
	private void giveCardsToPlayers() {
		for (int n = 0; n < Constants.NUMBER_OF_CARDS_PER_PLAYER; ++n) {
			players.forEach(p -> p.giveCard(deck.next()));
		}
	}
	
	private void placeFirstCard() {
		Card card = deck.next();
		grid.start(card);
	}
	
	public Result completeTurn(GameAction action) {
		Result result = action.invoke(playerInTurn, grid, deck);
		if (result.isSuccess()) {
			switchPlayer();
		}
		return result;
	}

	private void switchPlayer() {
		doPostTurnCleanup();
		int nextPlayerIndex = (this.players.indexOf(this.playerInTurn) + 1) % this.players.size();
		this.playerInTurn = this.players.get(nextPlayerIndex);
		this.observers.forEach(o -> o.playerInTurnChanged(this.playerInTurn));
	}
	
	private void doPostTurnCleanup() {
		// TODO: If we support the following scenario:
		//   1. Player places some cards on the board.
		//   2. With the cards still on the board, the player optes to pass.
		// we must do extra cleanup here, by returning the placed cards to the players hand.
		// We will not support this, at least not initially - the player must return the
		// cards to his hands first, then play Pass. This is probably the better user
		// experience anyway, especially since the player must be allowed to select 
		// cards to exchange.
		this.selectedPlayerCards.clear();
		this.placedCards.clear();
	}
	
	public boolean isGameOver() {
		return deck.isEmpty() && players.stream().anyMatch(Player::noCardsLeft);
	}

	public void addObserver(GameStateObserver o) {
		checkNotNull(o);
		this.observers.add(o);
	}
	
	public void removeObserver(GameStateObserver o) {
		checkNotNull(o);
		this.observers.remove(o);
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	public ImmutableList<Player> getPlayers() {
		return players;
	}
	
	public Deck getDeck() {
		return deck;
	}
	
}
