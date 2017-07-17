package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.JComponent;
import javax.swing.JLabel;

import jetoze.iota.Card;
import jetoze.iota.Deck;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Player;
import jetoze.iota.Position;
import jetoze.iota.ui.Layouts.LayoutBuilder;

public final class ControlPanel {

	// TODO: Four actions here:
	//   * Play Line
	//   * Recall
	//   * Replace Cards
	//   * Pass
	
	private final GameState gameState;
	
	private final PlayLineUiAction playLineUiAction;
	
	private final RecallUiAction recallUiAction;
	
	private final PassUiAction passUiAction;
	
	private final JLabel valueLabel = new JLabel("Value: ");
	
	private final JLabel cardsLeftLabel = new JLabel("Cards left: ");
	
	private final ValueAndCardsLeftLabelsUpdater valueLabelUpdater = new ValueAndCardsLeftLabelsUpdater();
	
	public ControlPanel(GameState gameState) {
		this.gameState = checkNotNull(gameState);
		this.playLineUiAction = new PlayLineUiAction(gameState);
		this.recallUiAction = new RecallUiAction(gameState);
		this.passUiAction = new PassUiAction(gameState);
		gameState.addObserver(valueLabelUpdater);
	}
	
	public JComponent layout() {
		LayoutBuilder labels = Layouts.grid(1, 2).withHGap(25)
				.addAll(valueLabel, cardsLeftLabel);
		LayoutBuilder buttons = Layouts.grid(2, 2, 12, 12)
				.addAll(playLineUiAction, recallUiAction, passUiAction, " ");
		return Layouts.border().withVGap(12)
				.north(labels)
				.center(buttons)
				.container();
	}

	public void dispose() {
		gameState.removeObserver(valueLabelUpdater);
		playLineUiAction.dispose();
		recallUiAction.dispose();
		passUiAction.dispose();
	}
	
	
	private class ValueAndCardsLeftLabelsUpdater implements GameStateObserver {

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {
			updateValue(value);
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {
			updateValue(value);
		}
		
		private void updateValue(int value) {
			String text = "Value: ";
			if (value > 0) {
				text += value;
			}
			UiThread.supply(text, valueLabel::setText); 
		}

		@Override
		public void gameHasStarted(GameState gameState, Card startCard) {
			updateCardsLeft();
		}

		@Override
		public void playerInTurnChanged(Player player) {
			updateCardsLeft();
			updateValue(0);
		}

		private void updateCardsLeft() {
			Deck deck = gameState.getDeck();
			int cardsLeft = deck.cardsLeft();
			String text = "Cards left: " + cardsLeft;
			UiThread.supply(text, cardsLeftLabel::setText);
		}
	}
	
}
