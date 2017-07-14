package jetoze.iota.ui;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public final class Borders {

	public static final Border empty(int top, int left, int bottom, int right) {
		return BorderFactory.createEmptyBorder(top, left, bottom, right);
	}
	
	private Borders() {/**/}

}
