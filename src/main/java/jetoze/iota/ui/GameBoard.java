package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Player;

public final class GameBoard {

	// TODO: At the moment this class listens to various UI events and updates the GameState
	// accordingly. Should this responsibility be moved to a dedicated mediator? As it stands,
	// this class mixes UI- and event-handling responsibilities.
	
	private final GameState gameState;
	
	private final GridUi grid;
	
	private final ControlPanel controlPanel;
	
	private final LinkedHashMap<Player, PlayerArea> playerAreas = new LinkedHashMap<>();
	
	private final ActivePlayerAreaListener activePlayerAreaListener = new ActivePlayerAreaListener();
	
	public GameBoard(GameState gameState, GridUi grid, ControlPanel controlPanel, List<PlayerArea> playerAreas) {
		this.gameState = checkNotNull(gameState);
		this.grid = checkNotNull(grid);
		this.controlPanel = checkNotNull(controlPanel);
		playerAreas.forEach(pa -> GameBoard.this.playerAreas.put(pa.getPlayer(), pa));
		this.gameState.addObserver(new GameStateObserverImpl());
	}
	
	public ImmutableList<PlayerArea> getPlayerAreas() {
		return ImmutableList.copyOf(playerAreas.values());
	}
	
	public JComponent layout() {
		checkState(playerAreas.size() == 2, "Only two players supported at the moment.");
		Iterator<PlayerArea> pas = playerAreas.values().iterator();
		return Layouts.border(0, 10)
				.center(grid.inScroll())
				.south(Layouts.border(10, 0)
						.west(pas.next().getUi())
						.center(controlPanel.layout())
						.east(pas.next().getUi()))
				.container();
	}

	
	private class ActivePlayerAreaListener implements GridUiListener {

		@Override
		public void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {
			if (cardUi.isSelected()) {
				cardUi.setSelected(false);
				gameState.removeSelectedPlayerCard(cardUi.getCard());
			} else {
				cardUi.setSelected(true);
				gameState.addSelectedPlayerCard(cardUi.getCard());
			}
		}
	}
	
	
	private class GridListener implements GridUiListener {
		
	}
	
	
	private class GameStateObserverImpl implements GameStateObserver {

		@Override
		public void playerInTurnChanged(Player player) {
			for (Map.Entry<Player, PlayerArea> e : playerAreas.entrySet()) {
				if (e.getKey() == player) {
					e.getValue().addCardListener(activePlayerAreaListener);
				} else {
					e.getValue().removeCardListener(activePlayerAreaListener);
				}
			}
		}
	}
	
}
