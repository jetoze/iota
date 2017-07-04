package jetoze.iota;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

public final class PlayLineAction implements GameAction {

	private final ImmutableList<LineItem> cardsToPlay;
	
	public PlayLineAction(Collection<LineItem> cardsToPlay) {
		this.cardsToPlay = ImmutableList.copyOf(cardsToPlay);
	}

	@Override
	public void perform(Player player, Grid grid, Deck deck) {
		int points = grid.addLine(cardsToPlay);
		cardsToPlay.stream()
			.map(LineItem::getCard)
			.forEach(player::removeCard);
		while (player.needsCards() && !deck.isEmpty()) {
			player.giveCard(deck.next());
		}
		player.addPoints(points);
	}

}
