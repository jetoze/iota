package jetoze.iota.ui;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Player;

public final class LeaderBoard {

	private final GameState gameState;

	private final GameStateObserver observer = new GameStateObserver() {

		@Override
		public void playerInTurnChanged(Player player) {
			UiThread.supply(player, LeaderBoard.this::update);
		}
	};
	
	private final JComponent container;
	
	public LeaderBoard(GameState gameState) {
		this.gameState = gameState;
		gameState.addObserver(observer);
		this.container = Layouts.grid(gameState.getNumberOfPlayers(), 2)
				.withHGap(16)
				.withVGap(12)
				.withBorder(Borders.titled("Leader Board"))
				.container();
		update(gameState.getActivePlayer());
	}

	private void update(@Nullable Player activePlayer) {
		container.removeAll();
		gameState.getStandings().forEach(p -> {
			String name = (p == activePlayer)
					? p.getName() + " (*):"
					: p.getName() + ":";
			container.add(new JLabel(name));
			container.add(new JLabel(String.valueOf(p.getPoints())));
		});
	}

	public JComponent getUi() {
		return container;
	}
	
	public void dispose() {
		gameState.removeObserver(observer);
	}
	
}
