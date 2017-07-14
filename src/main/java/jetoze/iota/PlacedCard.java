package jetoze.iota;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.annotation.Nullable;

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
	
	public Position getPositionOnBoard() {
		return positionOnBoard;
	}
	
	public void returnToHand() {
		player.returnCard(card, positionInHand);
	}

	@Override
	public int hashCode() {
		return Objects.hash(player, card, positionInHand, positionOnBoard);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof PlacedCard) {
			PlacedCard that = (PlacedCard) obj;
			return this.player.equals(that.player) &&
					this.card.equals(that.card) &&
					this.positionInHand.equals(that.positionInHand) &&
					this.positionOnBoard.equals(that.positionOnBoard);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s %s %s", player, card, positionOnBoard);
	}
	
}
