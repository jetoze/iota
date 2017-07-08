package jetoze.iota;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents a card that has been moved from a Player's hand to the board.
 */
public final class PlacedCard {

	private final Player player;
	
	private final Card card;
	
	private final Position positionInHand;
	
	private final Position positionOnBoard;

	public PlacedCard(Player player, Card card, Position positionInHand, Position positionOnBoard) {
		this.player = checkNotNull(player);
		this.card = checkNotNull(card);
		this.positionInHand = checkNotNull(positionInHand);
		this.positionOnBoard = checkNotNull(positionOnBoard);
	}

	public Card getCard() {
		return card;
	}
	
	public LineItem asLineItemForBoard() {
		return new LineItem(card, positionOnBoard);
	}
	
	public void returnToHand() {
		player.returnCard(card, positionInHand);
	}
	
}
