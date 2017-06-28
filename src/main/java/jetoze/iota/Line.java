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
	
	public Line(List<LineItem> items, Orientation orientation, MatchType matchType) {
		this.items = ImmutableList.copyOf(items);
		this.orientation = checkNotNull(orientation);
		this.matchType = checkNotNull(matchType);
	}
	
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