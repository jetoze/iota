package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import jetoze.iota.Card;
import jetoze.iota.Card.ConcreteCard;
import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public class CardUi extends JComponent {

	private final Card card;

	private Point anchorPoint;

	private final Cursor draggingCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

	public CardUi(Card card) {
		this.card = checkNotNull(card);
		setSize(UiConstants.CARD_SIZE, UiConstants.CARD_SIZE);
	}

	@Override
	public Dimension getPreferredSize() {
		return getSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Border:
		g2.setColor(UiConstants.CARD_BORDER_COLOR);
		g2.drawRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

		// Background:
		g2.setColor(UiConstants.CARD_BACKGROUND);
		g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 6, 6);
		if (card.isWildcard()) {
			paintWildcard(g2);
		} else {
			paintConcreteCard(g2);
		}
		g2.dispose();
	}

	private void paintConcreteCard(Graphics2D g) {
		ConcreteCard cc = (ConcreteCard) this.card;
		int faceValue = cc.getFaceValue();
		Color cardColor = cc.getColor();
		Shape cardShape = cc.getShape();

		// Black background
		g.setColor(java.awt.Color.BLACK);
		int outerMargin = UiConstants.OUTER_CARD_MARGIN;
		g.fillRect(outerMargin, outerMargin, getWidth() - 2 * outerMargin, getHeight() - 2 * outerMargin);

		drawShape(g, cardColor, cardShape, outerMargin);
		drawFaceValue(g, faceValue, cardShape);
	}

	private void drawShape(Graphics2D g, Color cardColor, Shape cardShape, int outerMargin) {
		UiConstants.applyCardColor(g, cardColor);
		switch (cardShape) {
		case CIRCLE: {
			int innerMargin = UiConstants.INNER_CARD_MARGIN;
			g.fillOval(outerMargin + innerMargin, outerMargin + innerMargin,
					getWidth() - 2 * ((outerMargin + innerMargin)), getHeight() - 2 * ((outerMargin + innerMargin)));
		}
			break;
		case SQUARE: {
			int innerMargin = UiConstants.INNER_CARD_MARGIN;
			g.fillRect(outerMargin + innerMargin, outerMargin + innerMargin,
					getWidth() - 2 * (outerMargin + innerMargin), getHeight() - 2 * (outerMargin + innerMargin));
		}
			break;
		case TRIANGLE: {
			int innerMarginH = UiConstants.INNER_CARD_MARGIN - 2;
			int innerMarginV = UiConstants.INNER_CARD_MARGIN + 2;
			fillTriangle(g, outerMargin + innerMarginH, outerMargin + innerMarginV,
					getWidth() - 2 * (outerMargin + innerMarginH), getHeight() - 2 * (outerMargin + innerMarginV));
		}
			break;
		case CROSS: {
			int innerMargin = UiConstants.INNER_CARD_MARGIN;
			int protrusion = UiConstants.CROSS_PROTRUSION;
			fillCross(g, outerMargin + innerMargin, outerMargin + innerMargin,
					getWidth() - 2 * (outerMargin + innerMargin), protrusion);
		}
			break;
		default:
			throw new AssertionError("Unexpected shape: " + cardShape.name());
		}
	}

	private void fillTriangle(Graphics2D g, int x, int y, int width, int height) {
		fillTriangle(g, x, y, width, height, Direction.UP);
	}

	private void fillTriangle(Graphics2D g, int x, int y, int width, int height, Direction direction) {
		switch (direction) {
		case UP: {
			int[] xPoints = new int[] { x, x + width / 2, x + width };
			int yPoints[] = new int[] { y + height, y, y + height };
			g.fillPolygon(xPoints, yPoints, 3);
		}
			break;
		case DOWN: {
			int[] xPoints = new int[] { x, x + width / 2, x + width };
			int yPoints[] = new int[] { y, y + height, y };
			g.fillPolygon(xPoints, yPoints, 3);
		}
			break;
		case LEFT: {
			int[] xPoints = new int[] { x, x + width, x + width };
			int yPoints[] = new int[] { y + height / 2, y, y + height };
			g.fillPolygon(xPoints, yPoints, 3);
		}
			break;
		case RIGHT: {
			int[] xPoints = new int[] { x, x, x + width };
			int yPoints[] = new int[] { y, y + height, y + height / 2 };
			g.fillPolygon(xPoints, yPoints, 3);
		}
			break;
		}
	}

	private void fillCross(Graphics2D g, int x, int y, int size, int protrusion) {
		// Horizontal leg
		g.fillRect(x, y + protrusion, size, size - 2 * protrusion);
		// Vertical leg
		g.fillRect(x + protrusion, y, size - 2 * protrusion, size);
	}

	private void drawFaceValue(Graphics2D g, int faceValue, Shape cardShape) {
		g.setColor(java.awt.Color.WHITE);
		switch (faceValue) {
		case 1:
			drawFaceValueOne(g, cardShape);
			break;
		case 2:
			drawFaceValueTwo(g, cardShape);
			break;
		case 3:
			drawFaceValueThree(g, cardShape);
			break;
		case 4:
			drawFaceValueFour(g, cardShape);
			break;
		default:
			throw new AssertionError("Unexpected face value: " + faceValue);
		}
	}

	private void drawFaceValueOne(Graphics2D g, Shape cardShape) {
		int size = UiConstants.getFaceValueMarkerSize(cardShape);
		switch (cardShape) {
		case CIRCLE: {
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			g.fillOval(x, y, size, size);
		}
			break;
		case SQUARE: {
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			g.fillRect(x, y, size, size);
		}
			break;
		case TRIANGLE: {
			int x = (getWidth() - size) / 2;
			// Drawing the marker completely center looks wrong, so push it down
			// a bit.
			int y = 6 + (getHeight() - size) / 2;
			fillTriangle(g, x, y, size, size);
		}
			break;
		case CROSS: {
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			int protrusion = 3;
			fillCross(g, x, y, size, protrusion);
		}
			break;
		}
	}

	private void drawFaceValueTwo(Graphics2D g, Shape cardShape) {
		int space = UiConstants.FACE_VALUE_MARKER_GAP;
		int size = UiConstants.getFaceValueMarkerSize(cardShape);
		switch (cardShape) {
		case CIRCLE: {
			int x = (getWidth() - size - space) / 2;
			int y = (getHeight() - size) / 2;
			g.fillOval(x, y, size, size);
			g.fillOval(x + space, y, size, size);
		}
			break;
		case SQUARE: {
			int x = (getWidth() - size - space) / 2;
			int y = (getHeight() - size) / 2;
			g.fillRect(x, y, size, size);
			g.fillRect(x + space, y, size, size);
		}
			break;
		case TRIANGLE: {
			int x = (getWidth() - size - space) / 2;
			// Drawing the marker completely center looks wrong, so push it down
			// a bit.
			int y = 6 + (getHeight() - size) / 2;
			fillTriangle(g, x, y, size, size);
			fillTriangle(g, x + space, y, size, size);
		}
			break;
		case CROSS: {
			int x = (getWidth() - size - space) / 2;
			int y = (getHeight() - size) / 2;
			int protrusion = 3;
			fillCross(g, x, y, size, protrusion);
			fillCross(g, x + space, y, size, protrusion);
		}
			break;
		}
	}

	private void drawFaceValueThree(Graphics2D g, Shape cardShape) {
		int space = UiConstants.FACE_VALUE_MARKER_GAP;
		int size = UiConstants.getFaceValueMarkerSize(cardShape);
		switch (cardShape) {
		case CIRCLE: {
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			g.fillOval(x - space / 2, y - space / 2, size, size);
			g.fillOval(x + space / 2, y - space / 2, size, size);
			g.fillOval(x, y + space / 2, size, size);
		}
			break;
		case SQUARE: {
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			g.fillRect(x, y - space / 2, size, size);
			g.fillRect(x - space / 2, y + space / 2, size, size);
			g.fillRect(x + space / 2, y + space / 2, size, size);
		}
			break;
		case TRIANGLE: {
			int x = (getWidth() - size) / 2;
			// Drawing the marker completely center looks wrong, so push it down
			// a bit.
			int y = 6 + (getHeight() - size) / 2;
			fillTriangle(g, x, y - space / 2, size, size);
			fillTriangle(g, x - space / 2, y + space / 2, size, size);
			fillTriangle(g, x + space / 2, y + space / 2, size, size);
		}
			break;
		case CROSS: {
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2 - 4;
			int protrusion = 3;
			fillCross(g, x, y - space / 2, size, protrusion);
			fillCross(g, x - space / 2, y + space / 2, size, protrusion);
			fillCross(g, x + space / 2, y + space / 2, size, protrusion);
		}
			break;
		}
	}

	private void drawFaceValueFour(Graphics2D g, Shape cardShape) {
		int space = UiConstants.FACE_VALUE_MARKER_GAP;
		int size = UiConstants.getFaceValueMarkerSize(cardShape);
		switch (cardShape) {
		case CIRCLE: {
			int x = getWidth() / 2 - space / 2 - size;
			int y = (getHeight() - size) / 2;
			g.fillOval(x, y, size, size);
			g.fillOval(x + (space + size) / 2, y - space / 2 - size / 2, size, size);
			g.fillOval(x + (space + size) / 2, y + space / 2 + size / 2, size, size);
			g.fillOval(x + space + size, y, size, size);
		}
			break;
		case SQUARE: {
			int x = getWidth() / 2 - space / 2 - size;
			int y = (getHeight() - size) / 2;
			g.fillRect(x, y, size, size);
			g.fillRect(x + (space + size) / 2, y - space / 2 - size / 2, size, size);
			g.fillRect(x + (space + size) / 2, y + space / 2 + size / 2, size, size);
			g.fillRect(x + space + size, y, size, size);
		}
			break;
		case TRIANGLE: {
			int x = getWidth() / 2 - space / 2 - size;
			int y = (getHeight() - size) / 2 + 7;
			fillTriangle(g, x, y, size, size, Direction.LEFT);
			fillTriangle(g, x + (space + size) / 2, y - space / 2 - size / 2, size, size, Direction.UP);
			fillTriangle(g, x + (space + size) / 2, y + space / 2 + size / 2, size, size, Direction.DOWN);
			fillTriangle(g, x + space + size, y, size, size, Direction.RIGHT);
		}
			break;
		case CROSS: {
			int x = getWidth() / 2 - space / 2 - size;
			int y = (getHeight() - size) / 2;
			int protrusion = 3;
			fillCross(g, x, y, size, protrusion);
			fillCross(g, x + (space + size) / 2, y - space / 2 - size / 2, size, protrusion);
			fillCross(g, x + (space + size) / 2, y + space / 2 + size / 2, size, protrusion);
			fillCross(g, x + space + size, y, size, protrusion);
		}
			break;
		}
	}

	private void paintWildcard(Graphics2D g) {
		// Four squares with the different colors. Each square has one of the 
		// shapes painted in black within it.
		int margin = UiConstants.OUTER_CARD_MARGIN;
		int innerMargin = UiConstants.INNER_WILDCARD_MARGIN;
		int squareSize = (getWidth() - 2 * margin) / 2;
		int innerShapeSize = squareSize - 2 * innerMargin;
		
		// Upper-left: Yellow / square
		UiConstants.applyCardColor(g, Color.YELLOW);
		g.fillRect(margin, margin, squareSize, squareSize);
		g.setColor(java.awt.Color.BLACK);
		g.fillRect(margin + innerMargin, margin + innerMargin, innerShapeSize, innerShapeSize);
		
		// Upper-right: Red / Circle
		UiConstants.applyCardColor(g, Color.RED);
		g.fillRect(margin + squareSize, margin, squareSize, squareSize);
		g.setColor(java.awt.Color.BLACK);		
		g.fillOval(margin + squareSize + innerMargin, margin + innerMargin, innerShapeSize, innerShapeSize);
		
		// Lower-left: Blue / Cross
		UiConstants.applyCardColor(g, Color.BLUE);
		g.fillRect(margin, margin + squareSize, squareSize, squareSize);
		g.setColor(java.awt.Color.BLACK);
		fillCross(g, margin + innerMargin, margin + squareSize + innerMargin, innerShapeSize, 5);
		
		// Lower-right: Green / Triangle
		UiConstants.applyCardColor(g, Color.GREEN);
		g.fillRect(margin + squareSize, margin + squareSize, squareSize, squareSize);
		g.setColor(java.awt.Color.BLACK);
		innerMargin -= 1;
		innerShapeSize += 2;
		fillTriangle(g, margin + squareSize + innerMargin, margin + squareSize + innerMargin, innerShapeSize, innerShapeSize);
	}

	private static enum Direction {

		UP, DOWN, LEFT, RIGHT

	}

}
