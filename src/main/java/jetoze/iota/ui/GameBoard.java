package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

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
	
	private final LeaderBoard leaderBoard;
	
	private final LinkedHashMap<Player, PlayerArea> playerAreas = new LinkedHashMap<>();
	
	private final PlayerAreaContainer playerAreaContainer;
	
	private final ActivePlayerAreaListener activePlayerAreaListener = new ActivePlayerAreaListener();
	
	private final GameStateObserverImpl gameStateObserver = new GameStateObserverImpl();
	
	private final GridListener gridListener = new GridListener();
	
	private JComponent container;
	
	public GameBoard(GameState gameState, 
					 GridUi gridUi, 
					 ControlPanel controlPanel, 
					 LeaderBoard leaderBoard, 
					 List<PlayerArea> playerAreas) {
		this.gameState = checkNotNull(gameState);
		this.gridUi = checkNotNull(gridUi);
		this.controlPanel = checkNotNull(controlPanel);
		this.leaderBoard = checkNotNull(leaderBoard);
		checkArgument(playerAreas.size() >= 2, "Requires at least two players");
		this.playerAreaContainer = playerAreas.size() == 2
				? new SideBySideTwoPlayersContainer(playerAreas)
				: new PlayerAreaTabs(playerAreas);
		playerAreas.forEach(pa -> GameBoard.this.playerAreas.put(pa.getPlayer(), pa));
		this.gameState.addObserver(gameStateObserver);
		this.gridUi.addListener(gridListener);
	}
	
	public ImmutableList<PlayerArea> getPlayerAreas() {
		return ImmutableList.copyOf(playerAreas.values());
	}
	
	public JComponent layout() {
		this.container = Layouts.border(0, 10)
				.center(gridUi.inScroll())
				.south(playerAreaContainer.layout(controlPanel, leaderBoard))
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
			playerAreaContainer.switchToPlayer(player);
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
	
	
	private static interface PlayerAreaContainer {
		
		public JComponent layout(ControlPanel controlPanel, LeaderBoard leaderBoard);
		
		public void switchToPlayer(Player p);
		
	}
	
	
	private static class SideBySideTwoPlayersContainer implements PlayerAreaContainer {

		private final ImmutableList<PlayerArea> playerAreas;
		
		public SideBySideTwoPlayersContainer(Collection<PlayerArea> playerAreas) {
			checkArgument(playerAreas.size() == 2);
			this.playerAreas = ImmutableList.copyOf(playerAreas);
		}
		
		@Override
		public JComponent layout(ControlPanel controlPanel, LeaderBoard leaderBoard) {
			// We don't bother displaying the leader board in this mode.
			return Layouts.border(10, 0)
					.west(playerAreas.get(0).getUi())
					.center(controlPanel.layout())
					.east(playerAreas.get(1).getUi())
					.container();
		}

		@Override
		public void switchToPlayer(Player p) {
			// nothing to do
		}
		
	}
	
	
	
	private static class PlayerAreaTabs implements PlayerAreaContainer {
		
		private final JTabbedPane tabs = new JTabbedPane();
		
		private final ImmutableList<Player> players;
		
		public PlayerAreaTabs(Collection<PlayerArea> playerAreas) {
			this.players = ImmutableList.copyOf(playerAreas.stream().map(PlayerArea::getPlayer).collect(toList()));
			playerAreas.forEach(pa -> tabs.add(pa.getPlayer().getName(), pa.getUi()));
		}

		@Override
		public JComponent layout(ControlPanel controlPanel, LeaderBoard leaderBoard) {
			return Layouts.border()
					.withHGap(10)
					.withBorder(Borders.empty(0, 12, 0, 12))
					.center(tabs)
					.east(Layouts.grid(1, 2).withHGap(16).addAll(controlPanel.layout(), leaderBoard.getUi()))
					.container();
		}

		@Override
		public void switchToPlayer(Player p) {
			int index = players.indexOf(p);
			tabs.setSelectedIndex(index);
		}
	}
	
}
