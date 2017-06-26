package jetoze.iota;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

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
	public void ensureSingleLinesOfMatchingCardsCanBeBuilt() {
		Grid grid = new Grid();
		grid.start(Card.newCard(Color.BLUE, Shape.SQUARE, 1));
		// Horizontal line of blue cards
		grid.addCard(Card.newCard(Color.BLUE, Shape.SQUARE, 1), 0, 1);
		grid.addCard(Card.newCard(Color.BLUE, Shape.CROSS, 1), 0, 2);
		grid.addCard(Card.newCard(Color.BLUE, Shape.CIRCLE, 2), 0, 3);
		// Vertical line of non-matching cards
		grid.addCard(Card.newCard(Color.GREEN, Shape.CROSS, 2), 1, 0);
		grid.addCard(Card.newCard(Color.RED, Shape.CIRCLE, 3), 2, 0);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.TRIANGLE, 4), 3, 0);
	}
	
	@Test
	public void ensureSingleLineCannotBeLongerThanMax() {
		Grid grid = new Grid();
		Card card = Card.newCard(Color.BLUE, Shape.SQUARE, 1);
		grid.start(card);
		for (int row = 1; row < Constants.MAX_LINE_LENGTH; ++row) {
			grid.addCard(card, row, 0);
		}
		assertFalse(grid.isCardAllowed(card, Constants.MAX_LINE_LENGTH, 0));
		try {
			grid.addCard(card, Constants.MAX_LINE_LENGTH, 0);
			fail();
		} catch (Exception e) {
			// expected
		}
		
		// Now repeat for a row
		for (int col = 1; col < Constants.MAX_LINE_LENGTH; ++col) {
			grid.addCard(card, 0, col);
		}
		assertFalse(grid.isCardAllowed(card, 0, Constants.MAX_LINE_LENGTH));
		try {
			grid.addCard(card, 0, Constants.MAX_LINE_LENGTH);
			fail();
		} catch (Exception e) {
			// expected
		}
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
		Set<Card> added = new HashSet<>();
		// Row 1: blue
		// Row 2: unique
		// Column 1: unique
		// Column 2: triangle
		grid.start(blueSquareOne);
		added.addAll(grid.addCard(blueTriangleThree, 0, 1));
		added.addAll(grid.addCard(yellowCircleTwo, 1, 0));
		added.addAll(grid.addCard(greenTriangleFour, 1, 1));
		assertEquals(Sets.newHashSet(blueSquareOne, blueTriangleThree, yellowCircleTwo, greenTriangleFour), added);
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
		grid.addCard(Card.wildcard(), 0, 1);
		grid.addCard(Card.newCard(Color.BLUE, Shape.CIRCLE, 4), 0, 2);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CROSS, 4), 1, 1);
		grid.addCard(Card.newCard(Color.RED, Shape.CROSS, 2), 2, 1);
		
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
		grid.addCard(Card.wildcard(), 0, 1);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CIRCLE, 3), 0, 2);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CROSS, 4), 1, 1);
		grid.addCard(Card.newCard(Color.RED, Shape.CROSS, 2), 2, 1);

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
		grid.addCard(Card.wildcard(), 0, 1);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CIRCLE, 3), 0, 2);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CIRCLE, 1), 1, 1);
		grid.addCard(Card.newCard(Color.RED, Shape.CROSS, 2), 2, 1);
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
		grid.addCard(Card.wildcard(), 0, 1);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CIRCLE, 3), 0, 2);
		grid.addCard(Card.newCard(Color.RED, Shape.CROSS, 4), 0, 3);
		grid.addCard(Card.newCard(Color.YELLOW, Shape.CIRCLE, 1), 1, 1);
		grid.addCard(Card.newCard(Color.RED, Shape.CROSS, 4), 2, 1);
		assertFalse(grid.isCardAllowed(Card.newCard(Color.BLUE, Shape.TRIANGLE, 3), 3, 1));
	}
	
}
