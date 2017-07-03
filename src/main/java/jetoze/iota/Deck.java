package jetoze.iota;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public final class Deck {

	public static Deck shuffled() {
		Deck deck = new Deck();
		deck.shuffle();
		return deck;
	}
	
	private final Stack<Card> cards = new Stack<>();
	
	public Deck() {
		for (int n = 0; n < Constants.NUMBER_OF_WILDCARDS; ++n) {
			cards.push(Card.wildcard());
		}
		for (Card c : Card.createPossibleCards(Constants.collectAllCardProperties())){
			cards.push(c);
		}
	}
	
	public Deck(Collection<Card> cards) {
		cards.addAll(cards);
	}
	
	public void shuffle() {
		List<Card> list = new ArrayList<>(cards);
		Collections.shuffle(list);
		cards.removeAllElements();
		cards.addAll(list);
	}
	
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	public Card next() {
		return cards.pop();
	}
	
	
	public static void main(String[] args) {
		Deck deck = Deck.shuffled();
		while (!deck.isEmpty()) {
			Card c = deck.next();
			System.out.println(c);
		}
	}
	
}
