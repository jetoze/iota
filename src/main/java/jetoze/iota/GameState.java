package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.ImmutableList;

import jetoze.iota.GameAction.Result;

public final class GameState {
	
	private final ImmutableList<Player> players;
	
	private final Deck deck = Deck.shuffled();
	
	private final Grid grid = new Grid();
	
	private Player playerInTurn;

	private final Set<Card> selectedPlayerCards = new HashSet<>();
	
	private final Map<Card, PlacedCard> placedCards = new HashMap<>();
	
	private final List<GameStateObserver> observers = new CopyOnWriteArrayList<>();
	
	public GameState() {
		this(ImmutableList.of(new Player("Alice"), new Player("John")));
	}
	
	public GameState(List<Player> players) {
		checkState(players.size() >= 2, "Must have at least two players");
		this.players = ImmutableList.copyOf(players);
	}

	public void start() {
		giveCardsToPlayers();
		placeFirstCard();
		setPlayerInTurn(0);
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
	
	public Optional<PlayLineAction> getPlayLineAction() {
		if (placedCards.isEmpty()) {
			return Optional.empty();
		}
		List<LineItem> lineItems = placedCards.values().stream()
				.map(PlacedCard::asLineItemForBoard)
				.collect(toList());
		return Optional.of(new PlayLineAction(lineItems));
	}
	
	public Result completeTurn(GameAction action) {
		Result result = action.invoke(playerInTurn, grid, deck);
		if (result.isSuccess()) {
			switchPlayer();
		}
		return result;
	}
	
	public void addSelectedPlayerCard(Card card) {
		checkNotNull(card);
		this.selectedPlayerCards.add(card);
	}
	
	public void removeSelectedPlayerCard(Card card) {
		checkNotNull(card);
		this.selectedPlayerCards.remove(card);
	}
	
	public int getNumberOfSelectedPlayerCards() {
		return this.selectedPlayerCards.size();
	}
	
	public Optional<Card> getOnlySelectedPlayerCard() {
		return this.selectedPlayerCards.size() == 1
				? Optional.of(this.selectedPlayerCards.iterator().next())
				: Optional.empty();
	}
	
	public int getNumberOfPlacedCards() {
		return placedCards.size();
	}
	
	public void placeSelectedCard(Position positionOnBoard) {
		getOnlySelectedPlayerCard().ifPresent(card -> {
			PlacedCard placedCard = playerInTurn.placeOnBoard(card, positionOnBoard);
			addPlacedCard(placedCard);
			selectedPlayerCards.remove(card);
		});
	}
	
	private void addPlacedCard(PlacedCard pc) {
		checkNotNull(pc);
		checkArgument(!this.placedCards.containsKey(pc.getCard()));
		this.placedCards.put(pc.getCard(), pc);
		int value = getValueOfCurrentlyPlacedCards();
		this.observers.forEach(o -> o.cardWasPlacedOnBoard(pc.getCard(), pc.getPositionOnBoard(), value));
	}
	
	private int getValueOfCurrentlyPlacedCards() {
		if (placedCards.isEmpty()) {
			return 0;
		}
		try {
			Grid clone = grid.clone();
			int value = clone.addLine(placedCards.values().stream()
					.map(PlacedCard::asLineItemForBoard)
					.collect(toList()));
			return value;
		} catch (InvalidLineException e) {
			return 0;
		}
	}

	public boolean isPlacedCard(Card card) {
		checkNotNull(card);
		return this.placedCards.containsKey(card);
	}
	
	public void returnPlacedCard(Card card) {
		PlacedCard pc = this.placedCards.remove(card);
		checkArgument(pc != null, "Not a placed card");
		pc.returnToHand();
		int value = getValueOfCurrentlyPlacedCards();
		this.observers.forEach(o -> o.cardWasRemovedFromBoard(card, pc.getPositionOnBoard(), value));
	}
	
	public void returnAllCards() {
		Set<PlacedCard> pcs = new HashSet<>(placedCards.values());
		pcs.stream().map(PlacedCard::getCard).forEach(this::returnPlacedCard);
	}
	
	private void switchPlayer() {
		doPostTurnCleanup();
		int nextPlayerIndex = (this.players.indexOf(this.playerInTurn) + 1) % this.players.size();
		setPlayerInTurn(nextPlayerIndex);
	}

	private void setPlayerInTurn(int index) {
		this.playerInTurn = this.players.get(index);
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
	
	public Player getActivePlayer() {
		return playerInTurn;
	}
	
	public ImmutableList<Player> getPlayers() {
		return players;
	}
	
	public Deck getDeck() {
		return deck;
	}
	
}
