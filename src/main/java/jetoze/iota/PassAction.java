package jetoze.iota;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

public final class PassAction implements GameAction {

	private final ImmutableList<Card> cardsToTrade;
	
	public PassAction() {
		this.cardsToTrade = ImmutableList.of();
	}
	
	public PassAction(Collection<Card> cardsToTrade) {
		this.cardsToTrade = ImmutableList.copyOf(cardsToTrade);
	}

	@Override
	public void perform(Player player, Grid grid, Deck deck) {
		int numberOfCardsThatCanBeTraded = Math.min(this.cardsToTrade.size(), deck.cardsLeft());
		for (int n = 0; n < numberOfCardsThatCanBeTraded; ++n) {
			Card c = this.cardsToTrade.get(n);
			player.removeCard(c);
			deck.addToBottom(c);
			player.giveCard(deck.next());
		}
	}

}
