package jetoze.iota.ui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import jetoze.iota.Card;
import jetoze.iota.Constants.Color;
import jetoze.iota.Constants.Shape;

public final class UiTestDriver {

	public static void main(String[] args) {
		EventQueue.invokeLater(UiTestDriver::publish);
	}
	
	public static void publish() {
		JPanel canvas = new JPanel();
		canvas.setLayout(null);
		
		CardUi square = new CardUi(Card.newCard(Color.BLUE, Shape.SQUARE, 3));
		canvas.add(square);
		square.setSize(100, 100);
		square.setLocation(20, 20);
		
		CardUi circle = new CardUi(Card.newCard(Color.GREEN, Shape.CIRCLE, 1));
		canvas.add(circle);
		circle.setSize(100, 100);
		circle.setLocation(140, 20);
		
		CardUi triangle = new CardUi(Card.newCard(Color.YELLOW, Shape.TRIANGLE, 2));
		canvas.add(triangle);
		triangle.setSize(100, 100);
		triangle.setLocation(20, 140);
		
		CardUi cross = new CardUi(Card.newCard(Color.RED, Shape.CROSS, 4));
		canvas.add(cross);
		cross.setSize(100, 100);
		cross.setLocation(140, 140);
		
		canvas.setPreferredSize(new Dimension(400, 400));
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);
	}

}
