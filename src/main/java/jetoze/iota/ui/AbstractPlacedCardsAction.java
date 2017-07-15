package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.AbstractAction;

import jetoze.iota.Card;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Position;

abstract class AbstractPlacedCardsAction extends AbstractAction {

	private final GameState gameState;
	
	private final StateListener stateListener = new StateListener();

	public AbstractPlacedCardsAction(String name, GameState gameState) {
		super(name);
		this.gameState = checkNotNull(gameState);
		updateEnabledState();
		gameState.addObserver(stateListener);
	}

	private void updateEnabledState() {
		boolean enabled = gameState.getNumberOfPlacedCards() > 0;
		UiThread.run(() -> setEnabled(enabled));
	}
	
	protected final GameState getGameState() {
		return gameState;
	}

	public final void dispose() {
		gameState.removeObserver(stateListener);
	}
	
	
	private class StateListener implements GameStateObserver {

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {
			updateEnabledState();
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {
			updateEnabledState();
		}
	}

}
