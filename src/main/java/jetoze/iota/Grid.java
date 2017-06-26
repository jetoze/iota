package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

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
		return createLine(newCard, start, end, Position::rightOf);
	}
	
	@Nullable
	private Line createVerticalLine(Card newCard, Position p) {
		Position start = findStartOfColumn(p);
		Position end = findEndOfColumn(p);
		return createLine(newCard, start, end, Position::below);
	}
	
	@Nullable
	private Line createLine(Card newCard,
						 	Position start, 
						 	Position end, 
						 	Function<Position, Position> nextGenerator) {
		if (start.equals(end)) {
			return Line.singleCard(newCard);
		}
		List<Card> cards = new ArrayList<>();
		for (Position p = start; ; p = nextGenerator.apply(p)) {
			Card card = grid.get(p.row, p.col);
			if (card == null) {
				card = newCard;
			}
			cards.add(card);
			if (p.equals(end)) {
				break;
			}
		}
		MatchType matchType = deduceMatchType(cards);
		return (matchType != null)
				? new Line(cards, matchType)
				: null;
	}
	
	@Nullable
	private static MatchType deduceMatchType(List<Card> line) {
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
					matches = card.getMatchProperties();
					all = matches;
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
			if (!matches.isEmpty()) {
				// All the cards share a common property
				return MatchType.SAME;
			} else if (allUnique) {
				return line.stream().allMatch(Card::isWildcard) 
						? MatchType.EITHER 
						: MatchType.DIFFERENT;
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
		
		private final ImmutableList<Card> cards;
		
		private final MatchType matchType;
		
		public Line(List<Card> cards, MatchType matchType) {
			this.cards = ImmutableList.copyOf(cards);
			this.matchType = matchType;
		}
		
		public static Line singleCard(Card card) {
			return new Line(ImmutableList.of(card), MatchType.EITHER);
		}
		
		public int length() {
			return cards.size();
		}
		
		public ImmutableList<Card> getCards() {
			return cards;
		}
		
		public MatchType getMatchType() {
			return matchType;
		}
	}
	
	
	private static enum MatchType {

		SAME,
		
		DIFFERENT,
		
		EITHER
		
	}
	
}
