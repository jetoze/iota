package jetoze.iota.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Arrays;

import javax.swing.JFrame;

import jetoze.iota.Deck;
import jetoze.iota.GameState;
import jetoze.iota.Player;
import jetoze.iota.Position;

public final class UiTestDriver {

	public static void main(String[] args) {
		UiTestDriver driver = new UiTestDriver();
		EventQueue.invokeLater(driver::publish);
	}
	
	private final Player player1 = new Player("John");
	
	private final Player player2 = new Player("Alice");
	
	private final Deck deck = Deck.shuffled();
	
	public void publish() {
		GridUi gridUi = GridUi.square(UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID);
		gridUi.setGameBoard(true);
		gridUi.addCard(new CardUi(deck.next()), 0, 0);
		GameState gameState = new GameState(Arrays.asList(player1, player2));
		ControlPanel controlPanel = new ControlPanel();
		GameBoard gameBoard = new GameBoard(gameState, gridUi, controlPanel, Arrays.asList(
				new PlayerArea(player1), new PlayerArea(player2)));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(gameBoard.layout(), BorderLayout.CENTER);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		gridUi.scrollToVisible(new Position(0, 0), new Position(8, 8));
		gameState.start();
	}

}
