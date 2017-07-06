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
	public Result invoke(Player player, Grid grid, Deck deck) {
		if (deck.cardsLeft() < this.cardsToTrade.size()) {
			return Result.failed("There are not enough cards left in the deck.");
		}
		this.cardsToTrade.forEach(c -> changeCard(c, player, deck));
		return Result.SUCCESS;
	}

	private static void changeCard(Card c, Player p, Deck d) {
		p.removeCard(c);
		d.addToBottom(c);
		p.giveCard(d.next());
	}
}
