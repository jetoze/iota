package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.Nullable;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;

public class Player {

	private final String name;
	
	private int points;

	private final List<Integer> pointsPerTurn = new ArrayList<>();
	
	private final Card[] cards = new Card[Constants.NUMBER_OF_CARDS_PER_PLAYER];
	
	private final List<PlayerObserver> observers = new CopyOnWriteArrayList<>();
	
	public Player(String name) {
		this.name = checkNotNull(name);
	}

	public String getName() {
		return name;
	}
	
	public void completeTurn(int points) {
		checkArgument(points >= 0); // points == 0 when passing
		this.pointsPerTurn.add(points);
		this.points += points;
		if (points > 0) {
			this.observers.forEach(o -> o.pointsChanged(Player.this, Player.this.points));
		}
	}
	
	public int getPoints() {
		return points;
	}

	public void giveCard(Card card) {
		checkNotNull(card);
		for (int n = 0; n < cards.length; ++n) {
			if (cards[n] == null) {
				cards[n] = card;
				int positionInHand = n;
				this.observers.forEach(o -> o.gotCard(Player.this, card, positionInHand));
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
		ImmutableList.Builder<Card> builder = ImmutableList.builder();
		for (Card c : this.cards) {
			if (c != null) {
				builder.add(c);
			}
		}
		return builder.build();
	}
	
	public void returnCard(Card card, Position positionInHand) {
		checkNotNull(card);
		checkArgument(positionInHand.row == 0, "Not a valid position");
		checkArgument(this.cards[positionInHand.col] == null, "Already a card at this position");
		this.cards[positionInHand.col] = card;
		this.observers.forEach(o -> o.gotCard(this, card, positionInHand.col));
	}

	public void addObserver(PlayerObserver o) {
		checkNotNull(o);
		this.observers.add(o);
	}

	public void removeObserver(PlayerObserver o) {
		checkNotNull(o);
		this.observers.remove(o);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj == this) {
			return true;
		}
		return (obj instanceof Player) && this.name.equals(((Player) obj).name);
	}

	@Override
	public String toString() {
		return name;
	}
}
