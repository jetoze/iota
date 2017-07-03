package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import jetoze.iota.Position;

public final class GridUi extends JPanel /* or should I also extend JComponent?*/ {

	// TODO: Switch to using composition rather than inheritance.
	
	private final Map<Position, CardUi> cards = new HashMap<>();
	
	public GridUi() {
		int width = UiConstants.GRID_CELL_MARGIN + 
				UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
		int height = width;
		setSize(width, height);
		setLayout(null);
	}
	
	public void addCard(CardUi card, int row, int col) {
		addCard(card, new Position(row, col));
	}
	
	public void addCard(CardUi card, Position pos) {
		checkNotNull(card);
		Position internalPos = toInternalPosition(pos);
		CardUi oldCard = cards.get(internalPos);
		if (oldCard != null) {
			remove(oldCard);
		}
		Point pt = locationOf(internalPos);
		card.setLocation(pt);
		add(card);
		cards.put(internalPos, card);
	}
	
	private static Point locationOf(Position pos) {
		int x = UiConstants.GRID_CELL_MARGIN + pos.col * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN)
				+ UiConstants.GRID_CELL_MARGIN / 2;
		int y = UiConstants.GRID_CELL_MARGIN + pos.row * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN)
				+ UiConstants.GRID_CELL_MARGIN / 2;
		return new Point(x, y);
	}
	
	/**
	 * The external position (0, 0) is in the center location of the grid. This method translates external
	 * positions to the internal positions maintained by this grid. For example, if the grid has 50 cells
	 * per side, the external position (0, 0) is internally represented by (24, 24).
	 */
	private static Position toInternalPosition(Position external) {
		int shift = UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID / 2 - 1;
		int row = external.row + shift;
		checkArgument(row < UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID, "Row out of bounds: " + external.row);
		int col = external.col + shift;
		checkArgument(col < UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID, "Column out of bounds: " + external.col);
		return new Position(row, col);
	}
	
	public void scrollToVisible(Position upperLeft, Position lowerRight) {
		Point ptUpperLeft = locationOf(toInternalPosition(upperLeft));
		Point otLowerRight = locationOf(toInternalPosition(lowerRight));
		otLowerRight.x += UiConstants.CARD_SIZE;
		otLowerRight.y += UiConstants.CARD_SIZE;
		Rectangle r = new Rectangle(ptUpperLeft.x, ptUpperLeft.y, otLowerRight.x - ptUpperLeft.x, otLowerRight.y - ptUpperLeft.y);
		scrollRectToVisible(r);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color saved = g.getColor();
		g.setColor(UiConstants.GRID_COLOR);
		for (int row = 1; row < UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID; ++row) {
			int y = UiConstants.GRID_CELL_MARGIN + row * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
			g.drawLine(
					UiConstants.GRID_CELL_MARGIN, 
					y, 
					getWidth() - UiConstants.GRID_CELL_MARGIN, 
					y);
		}
		for (int col = 1; col < UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID; ++col) {
			int x = UiConstants.GRID_CELL_MARGIN + col * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
			g.drawLine(x, UiConstants.GRID_CELL_MARGIN, x, getHeight() - UiConstants.GRID_CELL_MARGIN);
		}
		g.setColor(saved);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return getSize();
	}

}
