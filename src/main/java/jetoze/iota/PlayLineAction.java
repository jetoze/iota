package jetoze.iota;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

public final class PlayLineAction implements GameAction {

	private final ImmutableList<LineItem> cardsToPlay;
	
	public PlayLineAction(Collection<LineItem> cardsToPlay) {
		this.cardsToPlay = ImmutableList.copyOf(cardsToPlay);
	}

	@Override
	public Result invoke(Player player, Grid grid, Deck deck) {
		try {
			int points = grid.addLine(cardsToPlay);
			while (player.needsCards() && !deck.isEmpty()) {
				player.giveCard(deck.next());
			}
			player.addPoints(points);
			return Result.SUCCESS;
		} catch (InvalidLineException e) {
			return Result.failed("Not a valid line.");
		}
	}

}
