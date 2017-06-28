package jetoze.iota;

import static com.google.common.base.Preconditions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import static java.util.stream.Collectors.*;

final class Line {
	
	private final ImmutableList<LineItem> items;

	private final Orientation orientation;
	
	private final MatchType matchType;
	
	/**
	 * Creates a line of the given cards and positions, orientation, and match
	 * type.
	 * <p>
	 * While it may seem superfluous to specify the orientation - which could be
	 * deduced from the positions of the items if there are two or more items -
	 * we must also assign an specific orientation in the general case, which
	 * includes a single-card line which does not have an inherent orientation.
	 */
	public Line(List<LineItem> items, Orientation orientation, MatchType matchType) {
		this.items = ImmutableList.copyOf(items);
		this.orientation = checkNotNull(orientation);
		this.matchType = checkNotNull(matchType);
	}
	
	/**
	 * Creates a line of a single card.
	 * 
	 * @param card
	 *            the card
	 * @param pos
	 *            the position of the card
	 * @param orientation
	 *            the orientation of the line. This may seem arbitrary for a
	 *            single-line card, but we need a specific orientation in order
	 *            to implement the {@link #isOverlappingWith(Line)} method
	 *            correctly, which in turn is needed (at the moment) for
	 *            generating the correct point total when adding a new line to
	 *            the grid.
	 */
	public static Line singleCard(Card card, Position pos, Orientation orientation) {
		return new Line(ImmutableList.of(new LineItem(card, pos)), orientation, 
				MatchType.EITHER);
	}
	
	public int length() {
		return items.size();
	}
	
	public List<Card> getCards() {
		return items.stream()
				.map(LineItem::getCard)
				.collect(Collectors.toList());
	}
	
	public int getFaceValue() {
		return items.stream()
				.mapToInt(LineItem::getFaceValue)
				.sum();
	}
	
	public List<LineItem> getWildcardItems() {
		return items.stream()
				.filter(i -> i.getCard().isWildcard())
				.collect(Collectors.toList());
	}
	
	public Set<Card> collectCandidatesForNextCard() {
		List<Card> cards = getCards();
		return matchType.collectCandidatesForNextCard(cards);
	}
	
	public boolean isOverlappingWith(Line other) {
		if (this == other) {
			return true;
		}
		if (this.orientation != other.orientation) {
			return false;
		}
		Line shorter = (this.length() < other.length())
				? this
				: other;
		Line longer = (shorter == this)
				? other
				: this;
		Set<Position> pointsInLongerLine = longer.items.stream()
				.map(LineItem::getPosition)
				.collect(toSet());
		return shorter.items.stream()
				.map(LineItem::getPosition)
				.allMatch(pointsInLongerLine::contains);
	}
	
	@Override
	public String toString() {
		return items.stream()
				.map(i -> String.format("%s@%s", i.getCard(), i.getPosition()))
				.collect(joining(" - ", "<|", "|>"));
	}
	
}