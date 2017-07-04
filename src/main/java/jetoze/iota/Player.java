package jetoze.iota;

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
		this.points += points;
	}
	
	public int getPoints() {
		return points;
	}

	public void giveCard(Card card) {
		checkNotNull(card);
		checkState(this.cards.size() < Constants.NUMBER_OF_CARDS_PER_PLAYER);
		this.cards.add(card);
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
