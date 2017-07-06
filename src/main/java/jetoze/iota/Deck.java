package jetoze.iota;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Deck {

	public static Deck shuffled() {
		Deck deck = new Deck();
		deck.shuffle();
		return deck;
	}
	
	public static Deck of(Card... cards) {
		return new Deck(Arrays.asList(cards));
	}
	
	private final List<Card> cards = new ArrayList<>();
	
	public Deck() {
		for (int n = 0; n < Constants.NUMBER_OF_WILDCARDS; ++n) {
			cards.add(Card.wildcard());
		}
		cards.addAll(Card.createPossibleCards(Constants.collectAllCardProperties()));
	}
	
	public Deck(Collection<Card> cards) {
		this.cards.addAll(cards);
		Collections.reverse(this.cards);
	}
	
	public void shuffle() {
		Collections.shuffle(this.cards);
	}
	
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	public int cardsLeft() {
		return cards.size();
	}
	
	public Card next() {
		Card next = cards.remove(cards.size() - 1);
		return next;
	}
	
	public void addToBottom(Card... cards) {
		addToBottom(Arrays.asList(cards));
	}
	
	public void addToBottom(Collection<Card> toAdd) {
		toAdd.forEach(c -> cards.add(0, c));
	}
	
}
