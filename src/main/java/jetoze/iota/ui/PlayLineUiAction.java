package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jetoze.iota.Card;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Position;

public class PlayLineUiAction extends AbstractAction {

	private final GameState gameState;
	
	private final StateListener stateListener = new StateListener();
	
	public PlayLineUiAction(GameState gameState) {
		super("Play Line");
		this.gameState = checkNotNull(gameState);
		updateEnabledState();
		gameState.addObserver(stateListener);
	}

	private void updateEnabledState() {
		boolean enabled = gameState.getNumberOfPlacedCards() > 0;
		UiThread.run(() -> setEnabled(enabled));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		UiThread.runLater(this::invoke);
	}

	private void invoke() {
		gameState.getPlayLineAction().ifPresent(gameState::completeTurn);
	}

	public void dispose() {
		gameState.removeObserver(stateListener);
	}
	
	
	private class StateListener implements GameStateObserver {

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard) {
			updateEnabledState();
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard) {
			updateEnabledState();
		}
	}

}
