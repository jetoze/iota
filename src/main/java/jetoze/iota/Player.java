package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.ImmutableList;

public class Player {

	private final String name;
	
	private int points;
	
	private final List<Card> cards = new ArrayList<>();
	
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
		checkState(this.needsCards());
		this.cards.add(card);
		this.observers.forEach(o -> o.gotCard(Player.this, card));
	}
	
	public void giveCards(Card... cards) {
		for (Card c : cards) {
			giveCard(c);
		}
	}
	
	public void removeCard(Card card) {
		checkNotNull(card);
		if (this.cards.remove(card)) {
			this.observers.forEach(o -> o.playedCard(Player.this, card));
		}
	}
	
	public boolean needsCards() {
		return this.cards.size() < Constants.NUMBER_OF_CARDS_PER_PLAYER;
	}
	
	public boolean noCardsLeft() {
		return this.cards.isEmpty();
	}
	
	public ImmutableList<Card> getCards() {
		return ImmutableList.copyOf(this.cards);
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
