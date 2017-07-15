package jetoze.iota.ui;

import java.awt.event.ActionEvent;

import jetoze.iota.GameState;

public final class RecallUiAction extends AbstractPlacedCardsAction {
	
	public RecallUiAction(GameState gameState) {
		super("Recall", gameState);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UiThread.runLater(() -> getGameState().returnAllCards());
	}

}
