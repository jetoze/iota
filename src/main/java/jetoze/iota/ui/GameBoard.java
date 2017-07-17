package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.google.common.collect.ImmutableList;

import jetoze.iota.Card;
import jetoze.iota.GameResult;
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
	
	private JComponent container;
	
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
		this.container = Layouts.border(0, 10)
				.center(gridUi.inScroll())
				.south(Layouts.border(10, 0)
						.west(pas.next().getUi())
						.center(controlPanel.layout())
						.east(pas.next().getUi()))
				.container();
		return this.container;
	}
	
	public void dispose() {
		gridUi.removeListener(gridListener);
		gameState.removeObserver(gameStateObserver);
		playerAreas.values().forEach(a -> a.removeCardListener(activePlayerAreaListener));
	}
	
	private void doGameOverCleanup() {
		dispose();
	}

	
	private class ActivePlayerAreaListener implements GridUiListener {

		// TODO: PlayerArea.setSelectedCard should be called by the UiMediator, who should be
		// notified through the GameStateObserver.
		
		@Override
		public void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {
			PlayerArea pa = playerAreas.get(gameState.getActivePlayer());
			assert pa != null;
			if (cardUi.isSelected()) {
				gameState.setSelectedPlayerCard(null);
				pa.setSelectedCard(null);
			} else {
				gameState.setSelectedPlayerCard(cardUi.getCard());
				pa.setSelectedCard(cardUi.getCard());
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

		@Override
		public void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {
			if (numberOfClicks == 2 && gameState.isPlacedCard(cardUi.getCard())) {
				gameState.returnPlacedCard(cardUi.getCard());
			}
		}
	}
	
	
	private class GameStateObserverImpl implements GameStateObserver {

		@Override
		public void gameHasStarted(GameState gameState, Card startCard) {
			gridUi.addCard(new CardUi(startCard), 0, 0);
		}

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
			gridUi.allCardUis().forEach(c -> c.setSelected(false));
		}

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {
			CardUi cardUi = new CardUi(card);
			gridUi.addCard(cardUi, positionOnBoard);
			cardUi.setSelected(true);
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {
			gridUi.removeCard(card);
		}
		
		@Override
		public void gameOver(GameResult result) {
			presentGameResult(result);
			doGameOverCleanup();
		}

		private void presentGameResult(GameResult result) {
			if (result.isWin()) {
				Player winner = result.getWinner();
				String message = String.format("Winner, with %d points: %s! :-D", winner.getPoints(), winner.getName());
				JOptionPane.showMessageDialog(container, message, "Game Over!", JOptionPane.INFORMATION_MESSAGE);
			} else {
				assert result.isTie();
				JOptionPane.showMessageDialog(container, "It's a tie :-/", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
}
