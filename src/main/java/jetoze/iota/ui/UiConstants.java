package jetoze.iota.ui;

import java.awt.Graphics2D;
import java.util.EnumMap;

import jetoze.iota.Constants.Color;

public final class UiConstants {

	private static final EnumMap<Color, java.awt.Color> CARD_COLORS = new EnumMap<>(Color.class);
	static {
		CARD_COLORS.put(Color.RED, java.awt.Color.RED);
		CARD_COLORS.put(Color.GREEN, java.awt.Color.GREEN.darker());
		CARD_COLORS.put(Color.BLUE, new java.awt.Color(30, 144, 255));
		CARD_COLORS.put(Color.YELLOW, java.awt.Color.ORANGE);
	}
	
	public static void applyCardColor(Graphics2D g, Color cardColor) {
		java.awt.Color awt = CARD_COLORS.get(cardColor);
		g.setColor(awt);
	}
	
	private UiConstants() {/**/}
	
}
