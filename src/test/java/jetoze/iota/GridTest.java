package jetoze.iota;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;


public class GridTest {

	@Test
	public void ensureCardIsAllowedOnEmptyGrid() {
		Grid grid = new Grid();
		Card blueSquareOne = Card.newCard(Color.BLUE, Shape.SQUARE, 1);
		assertTrue(grid.isCardAllowed(blueSquareOne, 0, 0));
		assertFalse(grid.isCardAllowed(blueSquareOne, 1, 0));
		assertFalse(grid.isCardAllowed(blueSquareOne, 0, 1));
	}
	
	@Test
	public void ensureTwoCardsCannotOccupyTheSameSpace() {
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		assertFalse(grid.isCardAllowed(Card.newCard(Color.GREEN, Shape.CROSS, 3), 0, 0));
	}
	
	@Test
	public void ensureAllCardsAreConnected() {
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		assertFalse(grid.isCardAllowed(Card.newCard(Color.GREEN, Shape.CROSS, 3), 2, 4));
	}
	
	@Test
	public void ensureTwoWildcardIsValidLine() {
		Grid grid = new Grid();
		grid.start(Card.wildcard());
		assertTrue(grid.isCardAllowed(Card.wildcard(), 0, 1));
	}
	
	@Test
	public void ensureFourCardsCanBePlacedInTwoRowsAndColumns() {
		Grid grid = new Grid();
		Card blueSquareOne = Card.newCard(Color.BLUE, Shape.SQUARE, 1);
		Card blueTriangleThree = Card.newCard(Color.BLUE, Shape.TRIANGLE, 3);
		Card yellowCircleTwo = Card.newCard(Color.YELLOW, Shape.CIRCLE, 2);
		Card greenTriangleFour = Card.newCard(Color.GREEN, Shape.TRIANGLE, 4);
		// Row 1: blue
		// Row 2: unique
		// Column 1: unique
		// Column 2: triangle
		grid.start(blueSquareOne);
		grid.addLine(new LineItem(blueTriangleThree, 0, 1));
		grid.addLine(new LineItem(yellowCircleTwo, 1, 0),
				new LineItem(greenTriangleFour, 1, 1));
		int expectedNumberOfCards = 4;
		int actualNumberOfCards = grid.getNumberOfCards();
		assertEquals(expectedNumberOfCards, actualNumberOfCards);
	}
	
	@Test
	public void ensureWildcardCanBeUsedInTwoLines() {
		// Build the following grid:
		//
		// [B-Sq-1] - [  WC  ] - [B - Ci - 4]
		//               |
		//            [Y-Cr-4]
		//               |
		//            [R-Cr-2]
		//
		// The wild card must be [B-Cr-AnyFaceValue] 
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		int expectedPoints = 1 + 0 + 4;
		int actualPoints = grid.addLine(
				new LineItem(Card.wildcard(), 0, 1),
				new LineItem(Card.newCard(Color.BLUE, Shape.CROSS, 4), 0, 2));
		assertEquals(expectedPoints, actualPoints);
		
		expectedPoints = 0 + 4 + 2;
		actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.YELLOW, Shape.CROSS, 4), 1, 1),
				new LineItem(Card.newCard(Color.RED, Shape.CROSS, 2), 2, 1));
		assertEquals(expectedPoints, actualPoints);
		
		// Next, a case where one of the lines uses all unique properties
		// Build the following grid:
		//
		// [B-Sq-1] - [  WC  ] - [Y-Ci-3]
		//               |
		//            [Y-Cr-4]
		//               |
		//            [R-Cr-2]
		//
		// The wild card must be [G/R-Cr-2/4] 
		grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));

		expectedPoints = 1 + 0 + 3;
		actualPoints = grid.addLine(
				new LineItem(Card.wildcard(), 0, 1),
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 3), 0, 2));
		assertEquals(expectedPoints, actualPoints);

		expectedPoints = 0 + 4 + 2;
		actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.YELLOW, Shape.CROSS, 4), 1, 1),
				new LineItem(Card.newCard(Color.RED, Shape.CROSS, 2), 2, 1));
		assertEquals(expectedPoints, actualPoints);

		// Next, a case where both lines uses all unique properties
		// Build the following grid:
		//
		// [B-Sq-1] - [  WC  ] - [Y-Ci-3]
		//               |
		//            [Y-Ci-1]
		//               |
		//            [R-Cr-2]
		//
		// The wild card must be [G-Tr-1] 
		grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		
		// Validated above
		grid.addLine(
				new LineItem(Card.wildcard(), 0, 1),
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 3), 0, 2));
		
		expectedPoints = 0 + 1 + 2;
		actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 1), 1, 1),
				new LineItem(Card.newCard(Color.RED, Shape.CROSS, 2), 2, 1));
		assertEquals(expectedPoints, actualPoints);
	}
	
	@Test
	public void ensureWilcardCanBeUsedInTwoLinesOnlyIfItsTheSameCardInBothLines() {
		// Build the following grid:
		//
		// [B-Sq-1] - [  WC  ] - [Y-Ci-3] - [R-Cr-4]
		//               |
		//            [Y-Ci-1]
		//               |
		//            [R-Cr-4]
		//               |
		//            [B-Tr-3]
		//
		// The horizontal line mandates that the card must be [G-Tr-2]. The vertical line
		// mandates that it must be [G-Sq-2]. This should not be allowed.
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		
		grid.addLine(
				new LineItem(Card.wildcard(), 0, 1),
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 3), 0, 2),
				new LineItem(Card.newCard(Color.RED, Shape.CROSS, 4), 0, 3));

		grid.addLine(
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 1), 1, 1),
				new LineItem(Card.newCard(Color.RED, Shape.CROSS, 4), 2, 1));
		
		assertFalse(grid.isCardAllowed(Card.newCard(Color.BLUE, Shape.TRIANGLE, 3), 3, 1));
	}
	
	@Test
	public void addSingleCardLine() {
		// [B-Sq-1]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));

		// [B-Sq-1] - *[B-Ci-4]*
		int expectedPoints = 5;
		int actualPoints = grid.addLine(new LineItem(
				Card.newCard(Color.BLUE, Shape.CIRCLE, 4), 0, 1));
		assertEquals(expectedPoints, actualPoints);
		
		// [B-Sq-1] - [B-Ci-4] - *[B-Cr-2]*
		expectedPoints = 7;
		actualPoints = grid.addLine(new LineItem(
				Card.newCard(Color.BLUE, Shape.CROSS, 2), 0, 2));
		assertEquals(expectedPoints, actualPoints);
		
		// *[B-Tr-3]* - [B-Sq-1] - [B-Ci-4] - [B-Cr-2]
		expectedPoints = (3 + 1 + 4 + 2) * 2 /*one lot*/;
		actualPoints = grid.addLine(new LineItem(
				Card.newCard(Color.BLUE, Shape.TRIANGLE, 3), 0, -1));
		assertEquals(expectedPoints, actualPoints);
		
		// *[G-Tr-1]*
		//     |
		//  [B-Tr-3] - [B-Sq-1] - [B-Ci-4] - [B-Cr-2]
		expectedPoints = 4;
		actualPoints = grid.addLine(new LineItem(
				Card.newCard(Color.GREEN, Shape.TRIANGLE, 1), -1, -1));
		assertEquals(expectedPoints, actualPoints);
		
		// [G-Tr-1] - *[R-Ci-2]*
		//    |           |
		// [B-Tr-3] -  [B-Sq-1] - [B-Ci-4] - [B-Cr-2]
		expectedPoints = 6;
		actualPoints = grid.addLine(new LineItem(
				Card.newCard(Color.RED, Shape.CIRCLE, 2), -1, 0));
		assertEquals(expectedPoints, actualPoints);
	}

	@Test
	public void addMultiCardLine() {
		// [B-Sq-1]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));

		// [B-Sq-1] - *[B-Ci-4]* - *[B-Cr-2]*
		int expectedPoints = 4 + 3;
		int actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.BLUE, Shape.CIRCLE, 4), 0, 1),
				new LineItem(Card.newCard(Color.BLUE, Shape.CROSS, 2), 0, 2));
		assertEquals(expectedPoints, actualPoints);
		
		//  [B-Sq-1] -  [B-Ci-4] - [B-Cr-2]
		//     |           |
		// *[B-Cr-3] - *[Y-Ci-4]*
		expectedPoints = (3 + 4) + (1 + 3) + (4 + 4);
		actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.BLUE, Shape.CROSS, 3), 1, 0),
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 4), 1, 1));
		assertEquals(expectedPoints, actualPoints);

		// [B-Sq-1] - [B-Ci-4] -  [B-Cr-2]
		//     |           |
		// [B-Cr-3] - [Y-Ci-4] - *[  WC  ]*
		//                           |
		//                       *[Y-Ci-2]*
        //                           |
		//                       *[R-Tr-2]*
		expectedPoints = ((2 + 2 + 2) + (3 + 4)) * 2 /*one lot*/;
		actualPoints = grid.addLine(
				new LineItem(Card.wildcard(), 1, 2),
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 2), 2, 2),
				new LineItem(Card.newCard(Color.RED, Shape.TRIANGLE, 2), 3, 2));
		assertEquals(expectedPoints, actualPoints);
	}
	
	@Test(expected = InvalidLineException.class)
	public void addedLineMustInFactBeALine() {
		// [B-Sq-1]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));

		// The following should not be allowed.
		// *[B-Ci-2]*
		//     |
		//  [B-Sq-1] - *[G-Sq-2]*
		grid.addLine(
				new LineItem(Card.newCard(Color.BLUE, Shape.CIRCLE, 2), -1, 0),
				new LineItem(Card.newCard(Color.GREEN, Shape.SQUARE, 2), 0, 1));
	}
	
	@Test
	public void orderShouldNotMatter() {
		// [B-Sq-1]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));

		// Create the following line, but pass in the third card first
		// [B-Sq-1] - *[B-Ci-4]* - *[B-Cr-2]*
		int expectedPoints = 1 + 4 + 2;
		int actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.BLUE, Shape.CROSS, 2), 0, 2),
				new LineItem(Card.newCard(Color.BLUE, Shape.CIRCLE, 4), 0, 1));
		assertEquals(expectedPoints, actualPoints);
		
		// Create the following line, but pass in the cards bottom up
		// [B-Sq-1] - [B-Ci-4] -  [B-Cr-2]
		//                           |
		//                       *[Y-Ci-2]*
        //                           |
		//                       *[R-Tr-2]*
        //                           |
		//                       *[R-Sq-2]*
		expectedPoints = (2 + 2 + 2 + 2) * 2 /*one lot*/;
		actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.RED, Shape.SQUARE, 2), 3, 2),
				new LineItem(Card.newCard(Color.RED, Shape.TRIANGLE, 2), 2, 2),
				new LineItem(Card.newCard(Color.YELLOW, Shape.CIRCLE, 2), 1, 2));
		assertEquals(expectedPoints, actualPoints);
	}
	
	@Test
	public void addingInvalidLineShouldLeaveTheGridUntouched() {
		// [B-Sq-1] - [B-Ci-2]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		grid.addLine(new LineItem(Card.newCard(Color.BLUE, Shape.CIRCLE, 2), 0, 1));
		// The following should not be allowed.
		// [B-Sq-1] - [B-Ci-2] - *[B-Cr-4]* - *[Y-TR-1]*
		try {
			grid.addLine(
					new LineItem(Card.newCard(Color.BLUE, Shape.CROSS, 4), 0, 2),
					new LineItem(Card.newCard(Color.YELLOW, Shape.TRIANGLE, 1), 0, 3));
			fail();
		} catch (InvalidLineException e) {
			int expectedNumberOfCards = 2;
			int actualNumberOfCards = grid.getNumberOfCards();
			assertEquals(expectedNumberOfCards, actualNumberOfCards);
		}
	}
	
	@Test
	public void lineCanBeAddedToFromBothSides() {
		// [B-Sq-1]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		
		// *[B-Cr-2]* - [B-Sq-1] - *[B-Tr-2]*
		int expectedPoints = 2 + 1 + 2;
		int actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.BLUE, Shape.CROSS, 2), 0, -1),
				new LineItem(Card.newCard(Color.BLUE, Shape.TRIANGLE, 2), 0, 1));
		assertEquals(expectedPoints, actualPoints);
	}
	
	@Test
	public void fourLineCardDoublesThePoints() {
		// [B-Sq-1] - [B-Ci-2]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		grid.addLine(new LineItem(Card.newCard(Color.BLUE, Shape.CIRCLE, 2), 0, 1));

		// [G-Sq-2] - [G-Ci-3] - [  WC  ] - [G-Cr-4]
		// [B-Sq-1] - [B-Ci-2]
		int expectedPoints = ((2 + 1) + (3 + 2) + (2 + 3 + 0 + 4)) * 2 /*one lot*/
				* 2 /*four-card line*/;
		int actualPoints = grid.addLine(
				new LineItem(Card.newCard(Color.GREEN, Shape.SQUARE, 2), -1, 0),
				new LineItem(Card.newCard(Color.GREEN, Shape.CIRCLE, 3), -1, 1),
				new LineItem(Card.wildcard(), -1, 2),
				new LineItem(Card.newCard(Color.GREEN, Shape.CROSS, 4), -1, 3));
		assertEquals(expectedPoints, actualPoints);
	}
	
	@Test
	public void eachLotDoublesThePoints() {
		// [B-Sq-1] - [B-Ci-2]
		// [G-Sq-2] - [G-Ci-3]
		// [R-Sq-3] - [R-Ci-1]
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		grid.addLine(new LineItem(Card.newCard(Color.BLUE, Shape.CIRCLE, 2), 0, 1));
		grid.addLine(LineBuilder.horizontal(
				Card.newCard(Color.GREEN, Shape.SQUARE, 2), 1, 0)
				.add(Color.GREEN, Shape.CIRCLE, 3)
				.build());
		grid.addLine(LineBuilder.horizontal(
				Card.newCard(Color.RED, Shape.SQUARE, 3), 2, 0)
				.add(Color.RED, Shape.CIRCLE, 1)
				.build());

		// [B-Sq-1] - [B-Ci-2]
		// [G-Sq-2] - [G-Ci-3]
		// [R-Sq-3] - [R-Ci-1]
		// [B-Sq-4] - [  WC  ] - [Y-CR-4]
		int expectedPoints = ((1 + 2 + 3 + 4) + (2 + 3 + 1) + (4 + 0 + 4)) * 2 * 2;
		int actualPoints = grid.addLine(LineBuilder.horizontal(
				Card.newCard(Color.BLUE, Shape.SQUARE, 4), 3, 0)
				.wildcard()
				.add(Color.YELLOW, Shape.CROSS, 4)
				.build());
		assertEquals(expectedPoints, actualPoints);
	}
	
	
	/**
	 * Utility class that builds a line left-to-right or top-to-bottom.
	 */
	private static class LineBuilder {
		
		private final Orientation orientation;
		
		private final List<LineItem> items = new ArrayList<>();
		
		private LineBuilder(Orientation orientation) {
			this.orientation = orientation;
		}
		
		public static LineBuilder horizontal(Card firstCard, int row, int col) {
			return start(Orientation.HORIZONTAL, firstCard, row, col);
		}
		
		public static LineBuilder start(Orientation orientation,
				   Card firstCard, int row, int col) {
			LineBuilder b = new LineBuilder(orientation);
			b.items.add(new LineItem(firstCard, row, col));
			return b;
		}
		
		public LineBuilder add(Color color, Shape shape, int faceValue) {
			return add(Card.newCard(color, shape, faceValue));
		}
		
		public LineBuilder wildcard() {
			return add(Card.wildcard());
		}
		
		public LineBuilder add(Card card) {
			checkState(!items.isEmpty());
			Position lastPos = items.get(items.size() - 1).getPosition();
			Position nextPos = (orientation == Orientation.HORIZONTAL)
					? lastPos.rightOf()
					: lastPos.below();
			items.add(new LineItem(card, nextPos));
			return this;
		}
		
		public List<LineItem> build() {
			return items;
		}
	}
	
}
