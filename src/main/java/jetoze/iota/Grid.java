package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;

public final class Grid {

	private final Table<Integer, Integer, Card> grid = HashBasedTable.create();
	
	public void start(Card card) {
		checkState(grid.isEmpty());
		checkNotNull(card);
		grid.put(0, 0, card);
	}
	
	public boolean isCardAllowed(Card card, int row, int col) {
		checkNotNull(card);
		NewCardEffect e = new NewCardEffect(card, new Position(row, col));
		return e.isValid();
	}
	
	/**
	 * Adds a new card to the grid. Returns a set containing all cards in the lines
	 * that were appended to as a result.
	 */
	public ImmutableSet<Card> addCard(Card card, int row, int col) {
		NewCardEffect e = new NewCardEffect(card, new Position(row, col));
		checkArgument(e.isValid());
		e.apply();
		return e.getSetOfPointCards();
	}
	
	@Nullable
	private Line createHorizontalLine(Card newCard, Position p) {
		Position start = findStartOfRow(p);
		Position end = findEndOfRow(p);
		return createLine(newCard, p, start, end, Position::rightOf);
	}
	
	@Nullable
	private Line createVerticalLine(Card newCard, Position p) {
		Position start = findStartOfColumn(p);
		Position end = findEndOfColumn(p);
		return createLine(newCard, p, start, end, Position::below);
	}
	
	@Nullable
	private Line createLine(Card newCard,
							Position newCardPosition,
						 	Position start, 
						 	Position end, 
						 	Function<Position, Position> nextGenerator) {
		if (start.equals(end)) {
			return Line.singleCard(newCard, newCardPosition);
		}
		List<LineItem> items = new ArrayList<>();
		for (Position p = start; ; p = nextGenerator.apply(p)) {
			Card card = grid.get(p.row, p.col);
			if (card == null) {
				card = newCard;
			}
			items.add(new LineItem(card, p));
			if (p.equals(end)) {
				break;
			}
		}
		MatchType matchType = deduceMatchType(items.stream()
				.map(LineItem::getCard)
				.collect(Collectors.toList()));
		return (matchType != null)
				? new Line(items, matchType)
				: null;
	}
	
	@Nullable
	private static MatchType deduceMatchType(List<Card> line) {
		// TODO: If a wildcard is part of two lines, it must represent the same card
		// in both lines. How do we account for that here?
		// One approach would be the following: When validating a wildcard, collect
		// all the properties that the wildcard *can* have to make it a valid card
		// at that position. Then, once both lines have been validated, make sure that
		// the set of properties from the first validation and the set of properties
		// from the second validation has a matching Color-Shape-Facevalue combination.
		//
		// Revision: collect all the *concrete* cards that the wildcard could represent.`
		// Hmm, this will be tricky in the case where a line contains two wildcards. In that
		// case we must generate all combinations that make both cards match. Oh dear.

		if (line.size() < 2) {
			return MatchType.EITHER;
		} else if (line.size() > Constants.MAX_LINE_LENGTH) {
			return null;
		} else {
			Set<Object> matches = null;
			Set<Object> all = null;
			boolean allUnique = true;
			for (Card card : line) {
				if (matches == null) {
					// The first card in the line. If the first card is a WC
					// we move on to the next one, since a WC does not have any
					// inherent properties itself.
					if (!card.isWildcard()) {
						matches = card.getMatchProperties();
						all = new HashSet<>(matches);
					}
				} else {
					matches = card.match(matches);
					if (allUnique) {
						Set<Object> cardProperties = card.getMatchProperties();
						int expectedSizeOfAllProperties = all.size() + cardProperties.size();
						all.addAll(cardProperties);
						if (all.size() < expectedSizeOfAllProperties) {
							allUnique = false;
						}
					}
					all.addAll(card.getMatchProperties());
				}
			}
			if (matches == null) {
				// This is the case of a line consisting of wildcards only.
				return MatchType.EITHER;
			} else if (!matches.isEmpty()) {
				// All the cards share a common property
				return MatchType.SAME;
			} else if (allUnique) {
				// No matching property
				return MatchType.DIFFERENT;
			} else {
				// No match
				return null;
			}
		}
	}
	
	private boolean contains(Position p) {
		return grid.contains(p.row, p.col);
	}
	
	private Position findStartOfRow(Position p) {
		return findEndpoint(p, Position::leftOf);
	}
	
	private Position findEndOfRow(Position p) {
		return findEndpoint(p, Position::rightOf);
	}
	
	private Position findStartOfColumn(Position p) {
		return findEndpoint(p, Position::above);
	}
	
	private Position findEndOfColumn(Position p) {
		return findEndpoint(p, Position::below);
	}

	private Position findEndpoint(Position start, Function<Position, Position> nextPositionGenerator) {
		Position p0 = start;
		Position p = nextPositionGenerator.apply(p0);
		while (contains(p)) {
			p0 = p;
			p = nextPositionGenerator.apply(p);
		}
		return p0;
	}
	
	
	private class NewCardEffect {
		
		private final Card newCard;
		
		private final Position position;
		
		@Nullable
		private final Line horizontalLine;
		
		@Nullable
		private final Line verticalLine;

		public NewCardEffect(Card newCard, Position position) {
			this.newCard = newCard;
			this.position = position;
			this.horizontalLine = createHorizontalLine(newCard, position);
			this.verticalLine = createVerticalLine(newCard, position);
		}

		public boolean isValid() {
			if ((this.horizontalLine == null) || (this.verticalLine == null)) {
				return false;
			}
			if (grid.isEmpty()) {
				// First card is by definition placed in origo.
				assert horizontalLine.length() == 1;
				assert verticalLine.length() == 1;
				return position.row == 0 && position.col == 0;
			} else {
				if (contains(position)) {
					return false;
				}
				// TODO: Wildcard validation goes here. Pseudo-code:
				// for each wc in hLine:
				//   is wc also in vLine:
				//     collect possible card properties from hLine
				//     collect possible card properties from vLine
				//     look for matching set of properties
				// The collecting of possible card properties will use the MatchType
				// to figure out which properties are allowed.
				for (LineItem wcItem : horizontalLine.getWildcardItems()) {
					if (verticalLine.contains(wcItem.getPosition())) {
						// TODO: Implement this. But write the tests first ;-)
					}
				}
				
				// At least one of the lines must contain more than one card.
				// (This ensures all cards are connected in the grid.)
				return horizontalLine.length() > 1 || verticalLine.length() > 1;
			}
		}
		
		public void apply() {
			grid.put(position.row, position.col, newCard);
		}
		
		public ImmutableSet<Card> getSetOfPointCards() {
			checkState(this.horizontalLine != null && this.verticalLine != null);
			return ImmutableSet.<Card>builder()
					.addAll(this.horizontalLine.getCards())
					.addAll(this.verticalLine.getCards())
					.build();
		}
	}
	
	
	private static final class Position {
		
		public final int row;
		
		public final int col;
		
		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}
		
		public Position leftOf() {
			return new Position(row, col - 1);
		}
		
		public Position rightOf() {
			return new Position(row, col + 1);
		}
		
		public Position above() {
			return new Position(row - 1, col);
		}
		
		public Position below() {
			return new Position(row + 1, col);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true; 
			}
			if (o instanceof Position) {
				Position that = (Position) o;
				return this.row == that.row && this.col == that.col;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(row, col);
		}
		
		@Override
		public String toString() {
			return String.format("[%d, %d]", row, col);
		}
	}

	
	private static class Line {
		
		private final ImmutableList<LineItem> items;
		
		private final MatchType matchType;
		
		public Line(List<LineItem> items, MatchType matchType) {
			this.items = ImmutableList.copyOf(items);
			this.matchType = matchType;
		}
		
		public static Line singleCard(Card card, Position pos) {
			return new Line(ImmutableList.of(new LineItem(card, pos)), MatchType.EITHER);
		}
		
		public int length() {
			return items.size();
		}
		
		public List<Card> getCards() {
			return items.stream().map(LineItem::getCard).collect(Collectors.toList());
		}
		
		public List<LineItem> getWildcardItems() {
			return items.stream()
					.filter(i -> i.getCard().isWildcard())
					.collect(Collectors.toList());
		}
		
		public boolean contains(Position pos) {
			// TODO: Consider using some sort of map as storage, so that we can 
			// do this as a lookup rather than iterate over the list. OTOH, the
			// line will be at most 4 elements long, so it's not like this is
			// a terrible performance overhead.
			return items.stream()
					.anyMatch(i -> i.getPosition().equals(pos));
		}
		
		public MatchType getMatchType() {
			return matchType;
		}
	}
	
	
	private static class LineItem {
		
		private final Card card;
		
		private final Position position;

		public LineItem(Card card, Position position) {
			this.card = card;
			this.position = position;
		}

		public Card getCard() {
			return card;
		}

		public Position getPosition() {
			return position;
		}
	}
	
	
	private static enum MatchType {

		SAME,
		
		DIFFERENT,
		
		EITHER
		
	}
	
}
