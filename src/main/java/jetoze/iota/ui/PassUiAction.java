package jetoze.iota.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jetoze.iota.GameResult;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.PassAction;

public final class PassUiAction extends AbstractAction {

	private final GameState gameState;
	
	private final GameStateObserver stateListener = new GameStateObserver() {

		@Override
		public void gameOver(GameResult result) {
			UiThread.supply(false, PassUiAction.this::setEnabled);
		}
	};
	
	public PassUiAction(GameState gameState) {
		super("Pass");
		this.gameState = gameState;
		this.gameState.addObserver(stateListener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		UiThread.runLater(this::pass);
	}

	private void pass() {
		// TODO: Allow the user to exchange cards.
		PassAction action = new PassAction();
		gameState.completeTurn(action);
	}

	public void dispose() {
		this.gameState.removeObserver(stateListener);
	}
	
}
