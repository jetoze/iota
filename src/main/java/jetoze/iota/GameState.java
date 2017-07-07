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
	
	public void completeTurn(GameAction action) {
		Result result = action.invoke(playerInTurn, grid, deck);
		if (result.isSuccess()) {
			switchPlayer();
		} else {
			// TODO: Display error.
		}
	}

	private void switchPlayer() {
		doPostTurnCleanup();
		int nextPlayerIndex = (this.players.indexOf(this.playerInTurn) + 1) % this.players.size();
		this.playerInTurn = this.players.get(nextPlayerIndex);
		this.observers.forEach(o -> o.playerInTurnChanged(this.playerInTurn));
	}
	
	private void doPostTurnCleanup() {
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
	
}
