package jetoze.iota.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jetoze.iota.Constants;
import jetoze.iota.Deck;
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
	
	public UiTestDriver() {
		for (int n = 0; n < Constants.NUMBER_OF_CARDS_PER_PLAYER; ++n) {
			player1.giveCard(deck.next());
			player2.giveCard(deck.next());
		}
	}
	
	public void publish() {
		GridUi gridUi = GridUi.square(UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID);
		gridUi.setGameBoard(true);
		gridUi.addCard(new CardUi(deck.next()), 0, 0);
		gridUi.addListener(new GridUiListener() {
			
			@Override
			public void emptyCellWasClickedOn(Position pos, int numberOfClicks) {
				System.out.println("Clicked on cell " + pos + ". Click count: " + numberOfClicks);
			}
			
			@Override
			public void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {
				cardUi.toggleSelection();
			}
		});
		ControlPanel controlPanel = new ControlPanel();
		GameBoard gameBoard = new GameBoard(gridUi, controlPanel, Arrays.asList(
				new PlayerArea(player1), new PlayerArea(player2)));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(gameBoard.layout(), BorderLayout.CENTER);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		gridUi.scrollToVisible(new Position(0, 0), new Position(8, 8));
	}

}
