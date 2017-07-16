package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jetoze.iota.GameState;
import jetoze.iota.PassAction;

public final class PassUiAction extends AbstractAction {

	private final GameState gameState;
	
	public PassUiAction(GameState gameState) {
		super("Pass");
		this.gameState = checkNotNull(gameState);
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
	
}
