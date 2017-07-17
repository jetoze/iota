package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.google.common.collect.ImmutableList;

import jetoze.iota.Card;
import jetoze.iota.GameResult;
import jetoze.iota.GameState;
import jetoze.iota.Player;
import jetoze.iota.Position;

public final class GameBoard {
	
	private final GameState gameState;
	
	private final GridUi gridUi;
	
	private final ControlPanel controlPanel;
	
	private final LeaderBoard leaderBoard;
	
	private final LinkedHashMap<Player, PlayerArea> playerAreas = new LinkedHashMap<>();
	
	private final PlayerAreaContainer playerAreaContainer;
	
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
	}
	
	public ImmutableList<PlayerArea> getPlayerAreas() {
		return ImmutableList.copyOf(playerAreas.values());
	}
	
	public void start(Card card) {
		gridUi.addCard(new CardUi(card), 0, 0);
	}
	
	public void placeCard(Card card, Position position) {
		CardUi cardUi = new CardUi(card);
		gridUi.addCard(cardUi, position);
		cardUi.setSelected(true);
	}
	
	public void removeCard(Card card) {
		gridUi.removeCard(card);
	}
	
	public void setSelectedPlayerCard(@Nullable Card card) {
		PlayerArea pa = playerAreas.get(gameState.getActivePlayer());
		pa.setSelectedCard(card);
	}

	public void unselectAllPlacedCards() {
		gridUi.allCardUis().forEach(c -> c.setSelected(false));
	}
	
	public void setActivePlayer(Player player) {
		for (Map.Entry<Player, PlayerArea> e : playerAreas.entrySet()) {
			PlayerArea pa = e.getValue();
			if (e.getKey() == player) {
				pa.showCards();
			} else {
				pa.hideCards();
			}
		}
		playerAreaContainer.switchToPlayer(player);
	}

	public void presentGameResult(GameResult result) {
		if (result.isWin()) {
			Player winner = result.getWinner();
			String message = String.format("Winner, with %d points: %s! :-D", winner.getPoints(), winner.getName());
			JOptionPane.showMessageDialog(container, message, "Game Over!", JOptionPane.INFORMATION_MESSAGE);
		} else {
			assert result.isTie();
			JOptionPane.showMessageDialog(container, "It's a tie :-/", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public JComponent layout() {
		this.container = Layouts.border(0, 10)
				.center(gridUi.inScroll())
				.south(playerAreaContainer.layout(controlPanel, leaderBoard))
				.container();
		return this.container;
	}
	
	public void addGridListener(GridUiListener lst) {
		gridUi.addListener(lst);
	}
	
	public void removeGridListener(GridUiListener lst) {
		gridUi.removeListener(lst);
	}
	
	public void addPlayerAreaListener(GridUiListener lst) {
		playerAreas.values().forEach(pa -> pa.addCardListener(lst));
	}
	
	public void removePlayerAreaListener(GridUiListener lst) {
		playerAreas.values().forEach(pa -> pa.removeCardListener(lst));
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
