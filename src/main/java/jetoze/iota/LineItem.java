package jetoze.iota;

import static com.google.common.base.Preconditions.*;

public final class LineItem {
	
	private final Card card;
	
	private final Position position;

	public LineItem(Card card, int row, int col) {
		this(card, new Position(row, col));
	}
	
	public LineItem(Card card, Position position) {
		this.card = checkNotNull(card);
		this.position = position;
	}

	public Card getCard() {
		return card;
	}

	public Position getPosition() {
		return position;
	}
	
	public int getFaceValue() {
		return card.getFaceValue();
	}
	
	@Override
	public String toString() {
		return String.format("%s@%s", card, position);
	}
	
}