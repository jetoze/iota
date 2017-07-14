package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import com.google.common.collect.ImmutableList;

import jetoze.iota.Card;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Player;
import jetoze.iota.Position;

public final class GameBoard {

	// TODO: At the moment this class listens to various UI events and updates the GameState
	// accordingly. Should this responsibility be moved to a dedicated mediator? As it stands,
	// this class mixes UI- and event-handling responsibilities.
	
	private final GameState gameState;
	
	private final GridUi gridUi;
	
	private final ControlPanel controlPanel;
	
	private final LinkedHashMap<Player, PlayerArea> playerAreas = new LinkedHashMap<>();
	
	private final ActivePlayerAreaListener activePlayerAreaListener = new ActivePlayerAreaListener();
	
	private final GameStateObserverImpl gameStateObserver = new GameStateObserverImpl();
	
	private final GridListener gridListener = new GridListener();
	
	public GameBoard(GameState gameState, GridUi gridUi, ControlPanel controlPanel, List<PlayerArea> playerAreas) {
		this.gameState = checkNotNull(gameState);
		this.gridUi = checkNotNull(gridUi);
		this.controlPanel = checkNotNull(controlPanel);
		playerAreas.forEach(pa -> GameBoard.this.playerAreas.put(pa.getPlayer(), pa));
		this.gameState.addObserver(gameStateObserver);
		this.gridUi.addListener(gridListener);
	}
	
	public ImmutableList<PlayerArea> getPlayerAreas() {
		return ImmutableList.copyOf(playerAreas.values());
	}
	
	public JComponent layout() {
		checkState(playerAreas.size() == 2, "Only two players supported at the moment.");
		Iterator<PlayerArea> pas = playerAreas.values().iterator();
		return Layouts.border(0, 10)
				.center(gridUi.inScroll())
				.south(Layouts.border(10, 0)
						.west(pas.next().getUi())
						.center(controlPanel.layout())
						.east(pas.next().getUi()))
				.container();
	}
	
	public void dispose() {
		gridUi.removeListener(gridListener);
		gameState.removeObserver(gameStateObserver);
		playerAreas.values().forEach(a -> a.removeCardListener(activePlayerAreaListener));
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

		@Override
		public void emptyCellWasClickedOn(Position pos, int numberOfClicks) {
			if (numberOfClicks == 1) {
				gameState.placeSelectedCard(pos);
			}
		}
	}
	
	
	private class GameStateObserverImpl implements GameStateObserver {

		@Override
		public void playerInTurnChanged(Player player) {
			for (Map.Entry<Player, PlayerArea> e : playerAreas.entrySet()) {
				PlayerArea pa = e.getValue();
				if (e.getKey() == player) {
					pa.addCardListener(activePlayerAreaListener);
					pa.showCards();
				} else {
					pa.removeCardListener(activePlayerAreaListener);
					pa.hideCards();
				}
			}
		}

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard) {
			gridUi.addCard(new CardUi(card), positionOnBoard);
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard) {
			gridUi.removeCard(card);
		}
	}
	
}
