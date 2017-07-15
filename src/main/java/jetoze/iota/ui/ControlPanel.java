package jetoze.iota.ui;

import javax.swing.JButton;
import javax.swing.JComponent;

import jetoze.iota.GameState;

public final class ControlPanel {

	// TODO: Four actions here:
	//   * Play Line
	//   * Recall
	//   * Replace Cards
	//   * Pass
	
	private final PlayLineUiAction playLineUiAction;
	
	private final RecallUiAction recallUiAction;
	
	public ControlPanel(GameState gameState) {
		this.playLineUiAction = new PlayLineUiAction(gameState);
		this.recallUiAction = new RecallUiAction(gameState);
	}
	
	public JComponent layout() {
		return Layouts.grid(4, 1, 0, 10)
				.add(playLineUiAction)
				.add(recallUiAction)
				.add(new JButton("Get New Cards"))
				.add(new JButton("Pass"))
				.container();
	}

	public void dispose() {
		playLineUiAction.dispose();
		recallUiAction.dispose();
	}
	
}
