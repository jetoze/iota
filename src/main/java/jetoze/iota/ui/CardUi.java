package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

import jetoze.iota.Card;
import jetoze.iota.Card.ConcreteCard;
import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public class CardUi extends JComponent {

	private final Card card;
	
	public CardUi(Card card) {
		this.card = checkNotNull(card);
	}
	
	@Override
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
		if (card.isWildcard()) {
			paintWildcard((Graphics2D) g);
		} else {
			paintConcreteCard((Graphics2D) g);
		}
	}
	
	private void paintWildcard(Graphics2D g) {
		throw new RuntimeException("Not implemented yet");
	}
	
	private void paintConcreteCard(Graphics2D g) {
		ConcreteCard cc = (ConcreteCard) this.card;
		int faceValue = cc.getFaceValue();
		Color cardColor = cc.getColor();
		Shape cardShape = cc.getShape();
		
		// White background
		g.setColor(java.awt.Color.WHITE);
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
		
		// Black background
		g.setColor(java.awt.Color.BLACK);
		int outerMargin = 8;
		g.fillRect(outerMargin, outerMargin, getWidth() - 2 * outerMargin, getHeight() - 2 * outerMargin);
		
		drawShape(g, cardColor, cardShape, outerMargin);
		drawFaceValue(g, faceValue, cardShape);
	}

	private void drawShape(Graphics2D g, Color cardColor, Shape cardShape, int outerMargin) {
		UiConstants.applyCardColor(g, cardColor);
		switch (cardShape) {
		case CIRCLE: {
			int innerMargin = 6;
			g.fillOval(
					outerMargin + innerMargin,
					outerMargin + innerMargin,
					getWidth() - 2 * ((outerMargin + innerMargin)), 
					getHeight() - 2 * ((outerMargin + innerMargin)));
		}
			break;
		case SQUARE: {
			int innerMargin = 8;
			g.fillRect(
					outerMargin + innerMargin,
					outerMargin + innerMargin,
					getWidth() - 2 * (outerMargin + innerMargin), 
					getHeight() - 2 * (outerMargin + innerMargin));
		}
			break;
		case TRIANGLE: {
			int innerMarginH = 4;
			int innerMarginV = 8;
			fillTriangle(g,
					outerMargin + innerMarginH,
					outerMargin + innerMarginV,
					getWidth() - 2 * (outerMargin + innerMarginH),
					getHeight() - 2 * (outerMargin + innerMarginV));
			}
			break;
		case CROSS: {
			int innerMargin = 6;
			int protrusion = 20;
			fillCross(g, outerMargin + innerMargin,
					outerMargin + innerMargin,
					getWidth() - 2 * (outerMargin + innerMargin),
					protrusion);
		}
		break;
		default:
			throw new AssertionError("Unexpected shape: " + cardShape.name());
		}
	}
	
	private void fillTriangle(Graphics2D g, int x, int y, int width, int height) {
		int[] xPoints = new int[] {
				x,
				x + width / 2,
				x + width
		};
		int yPoints[] = new int[] {
				y + height,
				y,
				y + height
		};
		g.fillPolygon(xPoints, yPoints, 3);
	}
	
	private void fillCross(Graphics2D g, int x, int y, int size, int protrusion) {
		// Horizontal leg
		g.fillRect(
				x,
				y + protrusion,
				size, 
				size - 2 * protrusion);
		// Vertical leg
		g.fillRect(
				x + protrusion,
				y,
				size - 2 * protrusion,
				size);
	}
	
	
	private void drawFaceValue(Graphics2D g, int faceValue, Shape cardShape) {
		g.setColor(java.awt.Color.WHITE);
		switch (faceValue) {
		case 1: {
			drawFaceValueOne(g, cardShape);
		}
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			throw new AssertionError("Unexpected face value: " + faceValue);
		}
	}

	private void drawFaceValueOne(Graphics2D g, Shape cardShape) {
		switch (cardShape) {
		case CIRCLE: {
			int diameter = 8;
			int x = (getWidth() - diameter) / 2;
			int y = (getHeight() - diameter) / 2;
			g.fillOval(x, y, diameter, diameter);
			}
			break;
		case SQUARE: {
			int size = 8;
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			g.fillRect(x, y, size, size);
			}
			break;
		case TRIANGLE: {
			int width = 8;
			int height = 8;
			int x = (getWidth() - width) / 2;
			// Drawing the marker completely center looks wrong, so push it down a bit.
			int y = 6 + (getHeight() - height) / 2;
			fillTriangle(g, x, y, width, height);
			}
			break;
		case CROSS: {
			int size = 10;
			int x = (getWidth() - size) / 2;
			int y = (getHeight() - size) / 2;
			int protrusion = 3;
			fillCross(g, x, y, size, protrusion);
			}
			break;
		}
	}
	
}
