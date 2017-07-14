package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import jetoze.iota.Card;
import jetoze.iota.Position;

public final class GridUi extends JPanel /* or should I also extend JComponent?*/ {

	// TODO: Switch to using composition rather than inheritance.

	public static GridUi square(int size) {
		return new GridUi(size, size);
	}
	
	private final int rows;
	
	private final int cols;
	
	private final Map<Position, CardUi> posToCardUi = new HashMap<>();
	
	private final Map<Card, Position> cardToPos = new HashMap<>();
	
	private boolean usesAbsolutePositions = true;
	
	private final List<GridUiListener> listeners = new CopyOnWriteArrayList<>();

	private final MouseClickRouter mouseClickRouter = new MouseClickRouter();
	
	public GridUi(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		int width = UiConstants.GRID_CELL_MARGIN + 
				cols * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
		int height = UiConstants.GRID_CELL_MARGIN + 
				rows * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
		setSize(width, height);
		setLayout(null);
		addMouseListener(mouseClickRouter);
	}
	
	public void setGameBoard(boolean value) {
		this.usesAbsolutePositions = !value;
	}
	
	public Optional<Position> firstAvailablePosition() {
		for (int row = 0; row < this.rows; ++row) {
			for (int col = 0; col < this.cols; ++col) {
				Position p = new Position(row, col);
				if (!posToCardUi.containsKey(p)) {
					return Optional.of(toExternal(p));
				}
			}
		}
		return Optional.empty();
	}
	
	public void addCard(CardUi card, int row, int col) {
		addCard(card, new Position(row, col));
	}
	
	public void addCard(CardUi card, Position pos) {
		checkNotNull(card);
		Position internalPos = toInternalPosition(pos);
		CardUi oldCard = this.posToCardUi.get(internalPos);
		if (oldCard != null) {
			removeCard(oldCard);
		}
		Point pt = positionToPoint(internalPos);
		card.setLocation(pt);
		add(card);
		card.addMouseListener(mouseClickRouter);
		this.posToCardUi.put(internalPos, card);
		this.cardToPos.put(card.getCard(), internalPos);
		repaint();
	}

	private void removeCard(CardUi card) {
		Position pos = this.cardToPos.remove(card.getCard());
		this.posToCardUi.remove(pos);
		card.removeMouseListener(mouseClickRouter);
		remove(card);
	}
	
	private static Point positionToPoint(Position pos) {
		int x = UiConstants.GRID_CELL_MARGIN + pos.col * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN)
				+ UiConstants.GRID_CELL_MARGIN / 2;
		int y = UiConstants.GRID_CELL_MARGIN + pos.row * (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN)
				+ UiConstants.GRID_CELL_MARGIN / 2;
		return new Point(x, y);
	}
	
	private static Position pointToPosition(Point p) {
		int x = p.x - UiConstants.GRID_CELL_MARGIN;
		int y = p.y - UiConstants.GRID_CELL_MARGIN;
		int row = y / (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
		int col = x / (UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN);
		return new Position(row, col);
	}
	
	/**
	 * The external position (0, 0) is in the center location of the grid. This method translates external
	 * positions to the internal positions maintained by this grid. For example, if the grid has 50 cells
	 * per side, the external position (0, 0) is internally represented by (24, 24).
	 */
	private Position toInternalPosition(Position external) {
		if (usesAbsolutePositions) {
			return external;
		} else {
			int rowShift = this.rows / 2 - 1;
			int row = external.row + rowShift;
			checkArgument(row < UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID, "Row out of bounds: " + external.row);
			int colShift = this.cols / 2 - 1;
			int col = external.col + colShift;
			checkArgument(col < UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID, "Column out of bounds: " + external.col);
			return new Position(row, col);
		}
	}
	
	private Position toExternal(Position internal) {
		if (usesAbsolutePositions) {
			return internal;
		} else {
			int rowShift = this.rows / 2 - 1;
			int row = internal.row - rowShift;
			int colShift = this.cols / 2 - 1;
			int col = internal.col - colShift;
			return new Position(row, col);
		}
	}
	
	public void scrollToVisible(Position upperLeft, Position lowerRight) {
		Point ptUpperLeft = positionToPoint(toInternalPosition(upperLeft));
		Point otLowerRight = positionToPoint(toInternalPosition(lowerRight));
		otLowerRight.x += UiConstants.CARD_SIZE;
		otLowerRight.y += UiConstants.CARD_SIZE;
		Rectangle r = new Rectangle(ptUpperLeft.x, ptUpperLeft.y, otLowerRight.x - ptUpperLeft.x, otLowerRight.y - ptUpperLeft.y);
		scrollRectToVisible(r);
	}
	
	public boolean removeCard(Card card) {
		checkNotNull(card);
		Position pos = this.cardToPos.remove(card);
		if (pos == null) {
			return false;
		}
		CardUi cardUi = this.posToCardUi.remove(pos);
		assert cardUi != null;
		remove(cardUi);
		repaint();
		return true;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Color saved = g.getColor();
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
	
	public JScrollPane inScroll() {
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(UiConstants.CARD_SIZE / 2);
		scrollPane.getVerticalScrollBar().setUnitIncrement(UiConstants.CARD_SIZE / 2);
		return scrollPane;
	}
	
	public Stream<CardUi> allCardUis() {
		return this.posToCardUi.values().stream();
	}
	
	public void addListener(GridUiListener lst) {
		checkNotNull(lst);
		this.listeners.add(lst);
	}
	
	public void removeListener(GridUiListener lst) {
		checkNotNull(lst);
		this.listeners.remove(lst);
	}

	
	private class MouseClickRouter extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			if (e.getSource() instanceof CardUi) {
				handleClickOnCard(e);
			} else {
				handleClickOnEmptyCell(e);
			}
		}

		private void handleClickOnCard(MouseEvent e) {
			CardUi cardUi = (CardUi) e.getSource();
			int clickCount = e.getClickCount();
			listeners.forEach(lst -> lst.cardWasClickedOn(cardUi, clickCount));
		}

		private void handleClickOnEmptyCell(MouseEvent e) {
			if (isInVicinityOfCellBorder(e)) {
				return;
			}
			Position internalPos = pointToPosition(e.getPoint());
			Position externalPos = toExternal(internalPos);
			int clickCount = e.getClickCount();
			listeners.forEach(lst -> lst.emptyCellWasClickedOn(externalPos, clickCount));
		}
		
		private boolean isInVicinityOfCellBorder(MouseEvent e) {
			return isInVicinityOfCellBorder(e.getX()) || isInVicinityOfCellBorder(e.getY());
		}
		
		private boolean isInVicinityOfCellBorder(int loc) {
			int cellSize = UiConstants.CARD_SIZE + UiConstants.GRID_CELL_MARGIN;
			int mod = (loc - UiConstants.GRID_CELL_MARGIN) % cellSize;
			return (mod <= 3) || ((cellSize - mod) <= 3);
		}
	}
	
}
