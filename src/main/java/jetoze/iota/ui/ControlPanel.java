package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jetoze.iota.Card;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Position;

public final class ControlPanel {

	// TODO: Four actions here:
	//   * Play Line
	//   * Recall
	//   * Replace Cards
	//   * Pass
	
	private final GameState gameState;
	
	private final PlayLineUiAction playLineUiAction;
	
	private final RecallUiAction recallUiAction;
	
	private final JLabel valueLabel = new JLabel("Value:");
	
	private final ValueLabelUpdater valueLabelUpdater = new ValueLabelUpdater();
	
	public ControlPanel(GameState gameState) {
		this.gameState = checkNotNull(gameState);
		this.playLineUiAction = new PlayLineUiAction(gameState);
		this.recallUiAction = new RecallUiAction(gameState);
		gameState.addObserver(valueLabelUpdater);
	}
	
	public JComponent layout() {
		return Layouts.grid(5, 1, 0, 10)
				.add(valueLabel)
				.add(playLineUiAction)
				.add(recallUiAction)
				.add(new JButton("Get New Cards"))
				.add(new JButton("Pass"))
				.container();
	}

	public void dispose() {
		gameState.removeObserver(valueLabelUpdater);
		playLineUiAction.dispose();
		recallUiAction.dispose();
	}
	
	
	private class ValueLabelUpdater implements GameStateObserver {

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {
			updateValue(value);
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {
			updateValue(value);
		}
		
		private void updateValue(int value) {
			String text = "Value:";
			if (value > 0) {
				text += " " + value;
			}
			UiThread.provide(text, valueLabel::setText); 
		}
	}
	
}
