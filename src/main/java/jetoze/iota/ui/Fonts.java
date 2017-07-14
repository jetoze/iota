package jetoze.iota.ui;

import java.awt.Font;

import javax.swing.JLabel;

public final class Fonts {

	/**
	 * Converts the label's current font to bold, keeping all other font
	 * properties intact, and returns the label itself.
	 * 
	 * @param label
	 *            the label the font of which to make bold
	 * @return the label itself
	 */
	public static JLabel boldify(JLabel label) {
		Font bf = bold(label.getFont());
		label.setFont(bf);
		return label;
	}
	
	public static final Font bold(Font f) {
		return f.deriveFont(Font.BOLD);
	}
	
	private Fonts() {/**/}

}
