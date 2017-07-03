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
		
		// Shape
		drawShape(g, cardColor, cardShape, outerMargin);
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
			int[] xValues = new int[] { 
					getWidth() / 2, 
					outerMargin + innerMarginH, 
					getWidth() - (outerMargin + innerMarginH) };
			int[] yValues = new int[] { 
					outerMargin + innerMarginV, 
					getHeight() - (outerMargin + innerMarginV),
					getHeight() - (outerMargin + innerMarginV) };
			g.fillPolygon(xValues, yValues, 3);
			}
			break;
		case CROSS: {
			int innerMargin = 6;
			int protrusion = 20;
			int thickness = getWidth() - 2 * (outerMargin + innerMargin + protrusion);
			// Horizontal leg
			g.fillRect(
					outerMargin + innerMargin,
					outerMargin + innerMargin + protrusion,
					getWidth() - 2 * (outerMargin + innerMargin), 
					thickness);
			// Vertical leg
			g.fillRect(
					outerMargin + innerMargin + protrusion,
					outerMargin + innerMargin,
					thickness,
					getHeight() - 2 * (outerMargin + innerMargin)
					);
		}
		break;
		default:
			throw new AssertionError(cardShape.name());
		}
	}
	
}
