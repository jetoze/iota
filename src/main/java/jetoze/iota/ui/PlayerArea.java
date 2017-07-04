package jetoze.iota.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public final class PlayerArea {

	private final JPanel canvas = new JPanel();
	
	private final GridUi cards = new GridUi(1, 4);
	
	private final JLabel points = new JLabel("0");
	
	public PlayerArea() {
		canvas.setLayout(new BorderLayout());
		canvas.add(wrap(cards), BorderLayout.NORTH);
		canvas.add(wrap(new JLabel("Points: "), points), BorderLayout.SOUTH);
	}
	
	private static JPanel wrap(JComponent...components) {
		JPanel p = new JPanel();
		for (JComponent c : components) {
			p.add(c);
		}
		return p;
	}

	public JComponent getUi() {
		return canvas;
	}

}
