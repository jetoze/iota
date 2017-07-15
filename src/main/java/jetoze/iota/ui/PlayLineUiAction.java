package jetoze.iota.ui;

import java.awt.event.ActionEvent;

import jetoze.iota.GameAction.Result;
import jetoze.iota.GameState;
import jetoze.iota.PlayLineAction;

public class PlayLineUiAction extends AbstractPlacedCardsAction {
	
	public PlayLineUiAction(GameState gameState) {
		super("Play Line", gameState);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		UiThread.runLater(this::playLine);
	}

	private void playLine() {
		getGameState().getPlayLineAction().ifPresent(this::invoke);
	}
	
	private void invoke (PlayLineAction action) {
		Result result = getGameState().completeTurn(action);
		if (!result.isSuccess()) {
			// TODO: Display error
			System.err.println(result.getError());
		}
	}
	
}
