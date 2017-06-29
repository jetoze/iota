package jetoze.iota;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public final class Grid {

	private final Table<Integer, Integer, Card> grid = HashBasedTable.create();
	
	public void start(Card card) {
		checkState(grid.isEmpty());
		checkNotNull(card);
		grid.put(0, 0, card);
	}
	
	public int getNumberOfCards() {
		return grid.size();
	}
	
	/**
	 * Checks if the given card can be placed in the given position on the grid.
	 */
	public boolean isCardAllowed(Card card, int row, int col) {
		checkNotNull(card);
		NewCardEffect e = new NewCardEffect(card, new Position(row, col));
		return e.isValid();
	}
	
	/**
	 * Adds a new line to the grid, and returns the number of points it
	 * generated.
	 * 
	 * @param cards
	 *            the cards to add.
	 * @return the number of points. This is the sum of the face values of call
	 *         cards in each line either created or extended by this operation.
	 * @throws InvalidLineException
	 *             if the line is not valid.
	 */
	public int addLine(LineItem... cards) throws InvalidLineException {
		checkPreReqs(cards);
		List<NewCardEffect> effects = new ArrayList<>();
		List<LineItem> remainingCards = Lists.newArrayList(cards);
		AffectedLines pointGeneratingLines = new AffectedLines();
		while (!remainingCards.isEmpty()) {
			Iterator<LineItem> it = remainingCards.iterator();
			boolean cardWasAdded = false;
			while (it.hasNext() && !cardWasAdded) {
				LineItem card = it.next();
				NewCardEffect e = new NewCardEffect(card);
				if (e.isValid()) {
					e.apply();
					pointGeneratingLines = pointGeneratingLines.add(e.getAffectedLines());
					effects.add(e);
					it.remove();
					cardWasAdded = true;
				}
			}
			if (!cardWasAdded) {
				// At least one card in the line cannot be added, i.e. the line is not valid.
				// Rollback any changes that's been made to the grid, and throw an exception.
				for (NewCardEffect e : effects) {
					e.rollback();
				}
				throw new InvalidLineException();
			}
		}
		return pointGeneratingLines.getPoints();
	}

	private static void checkPreReqs(LineItem... cards) throws InvalidLineException {
		try {
			checkArgument(cards.length > 0 && cards.length <= Constants.MAX_LINE_LENGTH);
			Orientation.validatePoints(cards);
		} catch (IllegalArgumentException e) {
			throw new InvalidLineException(e.getMessage(), e);
		}
	}
	
	@Nullable
	private Line createHorizontalLine(Card newCard, Position p) {
		Position start = findStartOfRow(p);
		Position end = findEndOfRow(p);
		return createLine(newCard, p, Orientation.HORIZONTAL, start, end, Position::rightOf);
	}
	
	@Nullable
	private Line createVerticalLine(Card newCard, Position p) {
		Position start = findStartOfColumn(p);
		Position end = findEndOfColumn(p);
		return createLine(newCard, p, Orientation.VERTICAL, start, end, Position::below);
	}
	
	@Nullable
	private Line createLine(Card newCard,
							Position newCardPosition,
							Orientation orientation,
						 	Position start, 
						 	Position end, 
						 	Function<Position, Position> nextGenerator) {
		if (start.equals(end)) {
			return Line.singleCard(newCard, newCardPosition, orientation);
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
				.collect(toList()));
		return (matchType != null)
				? new Line(items, orientation, matchType)
				: null;
	}
	
	@Nullable
	private static MatchType deduceMatchType(List<Card> line) {
		// Three different match types:
		// SAME == All cards must share the same property. Requires at least 
		//   two non-wildcards in the line.
		// DIFFERENT == No two cards can share a property. Requires at least 
		//   two non-wildcards in the line.
		// EITHER == We don't know yet. This will be the case if the line contains
		//   at most one concrete card.
		if (line.size() > Constants.MAX_LINE_LENGTH) {
			// The line is too long.
			return null;
		}
		long numberOfConcreteCards = line.stream()
				.filter(c -> !c.isWildcard())
				.count();
		if (numberOfConcreteCards <= 1) {
			return MatchType.EITHER;
		}
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

		public NewCardEffect(LineItem item) {
			this(item.getCard(), item.getPosition());
		}
		
		public NewCardEffect(Card newCard, Position position) {
			this.newCard = checkNotNull(newCard);
			this.position = checkNotNull(position);
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
				if (horizontalLine.length() == 1 && verticalLine.length() == 1) {
					// At least one of the lines must contain more than one card.
					// (This ensures all cards are connected in the grid.)
					return false;
				}
				return validateWildcards();
			}
		}

		private boolean validateWildcards() {
			// Wildcard validation - ensure that a wildcard that appears in two lines
			// represent the same card in both lines. Pseudo-code:
			// for each wc in this.hLine:
			//   if wc also in a vLine (not necessarily this.vLine)
			//     collect possible card properties from this.hLine
			//     collect possible card properties from vLine
			//     look for matching set of properties
			// for each wc in this.vLine:
			//   if wc also in an hLine (not necessarily this.hLine)
			//     collect possible card properties from this.vLine
			//     collect possible card properties from hLine
			//     look for matching set of properties
			for (LineItem wcItem : this.horizontalLine.getWildcardItems()) {
				Line vLine = createVerticalLine(wcItem.getCard(), wcItem.getPosition());
				if (vLine.length() == 1) {
					continue;
				}
				Set<Card> hLineCandidates = this.horizontalLine.collectCandidatesForNextCard();
				Set<Card> vLineCandidates = vLine.collectCandidatesForNextCard();
				Set<Card> candidates = hLineCandidates;
				candidates.retainAll(vLineCandidates);
				if (candidates.isEmpty()) {
					return false;
				}
			}
			for (LineItem wcItem : this.verticalLine.getWildcardItems()) {
				Line hLine = createHorizontalLine(wcItem.getCard(), wcItem.getPosition());
				if (hLine.length() == 1) {
					continue;
				}
				Set<Card> vLineCandidates = this.verticalLine.collectCandidatesForNextCard();
				Set<Card> hLineCandidates = hLine.collectCandidatesForNextCard();
				Set<Card> candidates = vLineCandidates;
				candidates.retainAll(hLineCandidates);
				if (candidates.isEmpty()) {
					return false;
				}
			}
			// Hooray, we have a valid line!
			return true;
		}
		
		public void apply() {
			grid.put(position.row, position.col, newCard);
		}
		
		public void rollback() {
			grid.remove(position.row, position.col);
		}
		
		public AffectedLines getAffectedLines() {
			checkState(this.horizontalLine != null && this.verticalLine != null);
			return new AffectedLines(this.horizontalLine, this.verticalLine);
		}
	}
	
	
	private static class AffectedLines {
		
		private final ImmutableMultimap<Orientation, Line> lines;
		
		public AffectedLines() {
			lines = ImmutableMultimap.<Orientation, Line>builder().build();
		}
		
		public AffectedLines(Line horizontal, Line vertical) {
			this(Arrays.asList(horizontal, vertical));
		}
		
		public AffectedLines(List<Line> lines) {
			ImmutableMultimap.Builder<Orientation, Line> builder = ImmutableMultimap.builder();
			for (Line line : lines) {
				// Do not include a card that belongs to a single-item line,
				// since that card will be counted in the other line. For example,
				// when adding a third card to a horizontal line, this.verticalLine
				// will be a single-card line containing the new card.
				// Only if a card appears in two multi-card lines should it be 
				// counted twice.
				if (line.length() > 1) {
					builder.put(line.getOrientation(), line);
				}
			}
			this.lines = builder.build();
		}
		
		public int getPoints() {
			return lines.values().stream()
					.mapToInt(Line::getFaceValue)
					.sum();
		}

		public AffectedLines add(AffectedLines other) {
			List<Line> horizontal = this.get(Orientation.HORIZONTAL);
			horizontal.addAll(other.get(Orientation.HORIZONTAL));
			removeOverlappingLines(horizontal);
			List<Line> vertical = this.get(Orientation.VERTICAL);
			vertical.addAll(other.get(Orientation.VERTICAL));
			removeOverlappingLines(vertical);
			horizontal.addAll(vertical);
			return new AffectedLines(horizontal);
		}
		
		private void removeOverlappingLines(List<Line> lines) {
			lines.sort(Comparator.comparing(Line::length).reversed());
			Set<Line> toRemove = new HashSet<>();
			for (int n = 0; n < lines.size() - 1; ++n) {
				Line longer = lines.get(n);
				for (int m = n + 1; m < lines.size(); ++m) {
					Line shorter = lines.get(m);
					// No need to compare lines of equal length, because they will always be unique.
					// This is due to the nature of the problem.
					if (longer.length() > shorter.length() && longer.isOverlappingWith(shorter)) {
						toRemove.add(shorter);
					}
				}
			}
			lines.removeAll(toRemove);
		}
		
		private List<Line> get(Orientation o) {
			return new ArrayList<>(this.lines.get(o));
		}
	}
		
}
