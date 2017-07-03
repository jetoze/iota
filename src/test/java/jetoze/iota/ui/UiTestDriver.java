package jetoze.iota.ui;

import static jetoze.iota.ui.UiConstants.*;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jetoze.iota.Card;
import jetoze.iota.Constants;
import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public final class UiTestDriver {

	public static void main(String[] args) {
		EventQueue.invokeLater(UiTestDriver::publish);
	}
	
	public static void publish() {
		JPanel canvas = new JPanel();
		canvas.setLayout(null);
		
		int row = 0;
		int col = 0;
		int margin = 10;
		for (Color c : Color.values()) {
			for (Shape s : Shape.values()) {
				for (int fv = Constants.MIN_FACE_VALUE; fv <= Constants.MAX_FACE_VALUE; ++fv) {
					CardUi card = new CardUi(Card.newCard(c, s, fv));
					canvas.add(card);
					card.setSize(CARD_SIZE, CARD_SIZE);
					card.setLocation(col * (CARD_SIZE + margin), row * (CARD_SIZE + margin));
					col++;
					if (col == 8) {
						col = 0;
						++row;
					}
				}
			}
		}

		int gridLength = 8 * (CARD_SIZE + margin) + margin;
		canvas.setPreferredSize(new Dimension(gridLength, gridLength));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new JScrollPane(canvas));
		frame.pack();
		frame.setVisible(true);
	}

}
