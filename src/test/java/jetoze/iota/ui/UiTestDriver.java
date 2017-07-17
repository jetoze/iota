package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import jetoze.iota.Card;
import jetoze.iota.Constants;
import jetoze.iota.Deck;
import jetoze.iota.GameState;
import jetoze.iota.Player;
import jetoze.iota.Position;

public final class UiTestDriver {

	public static void main(String[] args) {
		UiTestDriver driver = new UiTestDriver();
		EventQueue.invokeLater(driver::publish);
	}
	
	public void publish() {
		GridUi gridUi = GridUi.square(UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID);
		gridUi.setGameBoard(true);
		List<Player> players = Arrays.asList(
				new Player("John"), new Player("Alice"), new Player("Tarzan"));
		GameState gameState = new GameState(players);
		ControlPanel controlPanel = new ControlPanel(gameState);
		List<PlayerArea> playerAreas = players.stream()
				.map(PlayerArea::new)
				.collect(toList());
		GameBoard gameBoard = new GameBoard(gameState, gridUi, controlPanel, playerAreas);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(gameBoard.layout(), BorderLayout.CENTER);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		gridUi.scrollToVisible(new Position(0, 0), new Position(8, 8));
		gameState.start();
	}

	static Deck smallDeck(int size) {
		checkArgument(size >= 9, "At least 9 cards is needed for a two person game.");
		List<Card> allCards = new ArrayList<>(Card.createPossibleCards(Constants.collectAllCardProperties()));
		Collections.shuffle(allCards);
		return new Deck(allCards.subList(0, size));
	}
	
}
