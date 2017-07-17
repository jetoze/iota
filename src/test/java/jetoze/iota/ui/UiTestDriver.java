package jetoze.iota.ui;

import static jetoze.iota.Card.newCard;
import static jetoze.iota.Constants.Color.BLUE;
import static jetoze.iota.Constants.Color.GREEN;
import static jetoze.iota.Constants.Color.RED;
import static jetoze.iota.Constants.Shape.CIRCLE;
import static jetoze.iota.Constants.Shape.CROSS;
import static jetoze.iota.Constants.Shape.SQUARE;
import static jetoze.iota.Constants.Shape.TRIANGLE;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import jetoze.iota.Card;
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
	
	public void publish() {
		GridUi gridUi = GridUi.square(UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID);
		gridUi.setGameBoard(true);
		GameState gameState = new GameState(Arrays.asList(player1, player2), smallDeck());
		ControlPanel controlPanel = new ControlPanel(gameState);
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

	static Deck smallDeck() {
		List<Card> cards = Arrays.asList(
				newCard(BLUE, SQUARE, 1),
				newCard(BLUE, CIRCLE, 2),
				newCard(BLUE, TRIANGLE, 3),
				newCard(BLUE, CROSS, 4),
				newCard(GREEN, SQUARE, 1),
				newCard(GREEN, CIRCLE, 2),
				newCard(GREEN, TRIANGLE, 3),
				newCard(GREEN, CROSS, 4),
				newCard(RED, SQUARE, 1),
				newCard(RED, CIRCLE, 2),
				newCard(RED, TRIANGLE, 3),
				newCard(RED, CROSS, 4),
				Card.wildcard(),
				Card.wildcard()
		);
		Deck deck = new Deck(cards);
		deck.shuffle();
		return deck;
	}
	
}
