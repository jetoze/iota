package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

public class Player {

	private final String name;
	
	private int points;
	
	private final Card[] cards = new Card[Constants.NUMBER_OF_CARDS_PER_PLAYER];
	
	private final List<PlayerObserver> observers = new CopyOnWriteArrayList<>();
	
	public Player(String name) {
		this.name = checkNotNull(name);
	}

	public String getName() {
		return name;
	}
	
	public void addPoints(int points) {
		checkArgument(points > 0);
		this.points += points;
		this.observers.forEach(o -> o.pointsChanged(Player.this, Player.this.points));
	}
	
	public int getPoints() {
		return points;
	}

	public void giveCard(Card card) {
		checkNotNull(card);
		for (int n = 0; n < cards.length; ++n) {
			if (cards[n] == null) {
				cards[n] = card;
				this.observers.forEach(o -> o.gotCard(Player.this, card));
				return;
			}
		}
		checkState(false, "The hand was alread full");
	}
	
	public void giveCards(Card... cards) {
		for (Card c : cards) {
			giveCard(c);
		}
	}
	
	public void removeCard(Card card) {
		checkNotNull(card);
		for (int n = 0; n < cards.length; ++n) { 
			if (cards[n] == card) {
				cards[n] = null;
			}
		}
	}

	public PlacedCard placeOnBoard(Card card, Position positionOnBoard) {
		checkNotNull(card);
		checkNotNull(positionOnBoard);
		for (int n = 0; n < cards.length; ++n) {
			if (cards[n] == card) {
				cards[n] = null;
				PlacedCard placedCard = new PlacedCard(this, card, new Position(0, n), positionOnBoard);
				this.observers.forEach(o -> o.playedCard(Player.this, placedCard));
				return placedCard;
			}
		}
		throw new IllegalArgumentException("No such card");
	}
	
	public boolean needsCards() {
		return Arrays.stream(cards).anyMatch(Predicates.isNull());
	}
	
	public boolean noCardsLeft() {
		return Arrays.stream(cards).allMatch(Predicates.isNull());
	}
	
	public ImmutableList<Card> getCards() {
		return ImmutableList.copyOf(this.cards);
	}
	
	public void returnCard(Card card, Position positionInHand) {
		checkNotNull(card);
		checkArgument(positionInHand.row == 0, "Not a valid position");
		checkArgument(this.cards[positionInHand.col] == null, "Already a card at this position");
		this.cards[positionInHand.col] = card;
	}

	public void addObserver(PlayerObserver o) {
		checkNotNull(o);
		this.observers.add(o);
	}

	public void removeObserver(PlayerObserver o) {
		checkNotNull(o);
		this.observers.remove(o);
	}
}
