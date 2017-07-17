package jetoze.iota.ui;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public final class Borders {

	public static final Border empty(int top, int left, int bottom, int right) {
		return BorderFactory.createEmptyBorder(top, left, bottom, right);
	}

	public static final Border titled(String title) {
		return new TitledBorder(title);
	}
	
	private Borders() {/**/}

}
