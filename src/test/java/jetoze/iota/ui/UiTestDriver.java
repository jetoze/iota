package jetoze.iota.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jetoze.iota.Card;
import jetoze.iota.Constants;
import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;
import jetoze.iota.Position;

public final class UiTestDriver {

	public static void main(String[] args) {
		EventQueue.invokeLater(UiTestDriver::publish);
	}
	
	public static void publish() {
		GridUi gridUi = GridUi.square(UiConstants.NUMBER_OF_CELLS_PER_SIDE_IN_GRID);
		
		int row = 0;
		int col = 0;
		for (Color c : Color.values()) {
			for (Shape s : Shape.values()) {
				for (int fv = Constants.MIN_FACE_VALUE; fv <= Constants.MAX_FACE_VALUE; ++fv) {
					CardUi card = new CardUi(Card.newCard(c, s, fv));
					gridUi.addCard(card, row, col);
					col++;
					if (col == 8) {
						col = 0;
						++row;
					}
				}
			}
		}
		gridUi.addCard(new CardUi(Card.wildcard()), -1, 0);
		gridUi.addCard(new CardUi(Card.wildcard()), -1, 1);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane scrollPane = new JScrollPane(gridUi);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(UiConstants.CARD_SIZE / 2);
		scrollPane.getVerticalScrollBar().setUnitIncrement(UiConstants.CARD_SIZE / 2);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(layoutPlayerAreas(), BorderLayout.SOUTH);
		frame.setSize(1000, 1000);
		frame.setVisible(true);
		gridUi.scrollToVisible(new Position(0, 0), new Position(8, 8));
	}
	
	private static JComponent layoutPlayerAreas() {
		JPanel p = new JPanel(new BorderLayout());
		p.add(new PlayerArea().getUi(), BorderLayout.WEST);
		p.add(new PlayerArea().getUi(), BorderLayout.EAST);
		return p;
	}

}
