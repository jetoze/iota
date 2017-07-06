package jetoze.iota.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public final class ControlPanel {

	// TODO: Four actions here:
	//   * Play Line
	//   * Recall
	//   * Replace Cards
	//   * Pass
	
	public JComponent layout() {
		return Layouts.grid(4, 1, 0, 10)
				.add(new JButton("Play Line"))
				.add(new JButton("Recall"))
				.add(new JButton("Get New Cards"))
				.add(new JButton("Pass"))
				.container();
	}
	
}
