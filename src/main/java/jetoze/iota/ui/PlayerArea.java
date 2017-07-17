package jetoze.iota.ui;

import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jetoze.iota.Card;
import jetoze.iota.PlacedCard;
import jetoze.iota.Player;
import jetoze.iota.PlayerObserver;
import jetoze.iota.Position;

public final class PlayerArea {

	private final Player player;
	
	private final JComponent canvas;
	
	private final GridUi cards = new GridUi(1, 4);
	
	private final JLabel points = new JLabel();
	
	private final UiUpdater uiUpdater = new UiUpdater();
	
	public PlayerArea(Player player) {
		this.player = player;
		this.points.setText(String.valueOf(player.getPoints()));
		this.canvas = Layouts.border()
			.north(wrap(cards))
			.south(wrap(nameLabel(player), new JLabel("Points: "), this.points))
			.container();
		int col = 0;
		for (Card card : player.getCards()) {
			cards.addCard(new CardUi(card), 0, col);
			++col;
		}
		this.player.addObserver(uiUpdater);
	}
	
	private static JLabel nameLabel(Player player) {
		JLabel lbl = Fonts.boldify(new JLabel());
		lbl.setText(player.getName());
		lbl.setBorder(Borders.empty(0, 0, 0, 20));
		return lbl;
	}
	
	private static JPanel wrap(JComponent...components) {
		JPanel p = new JPanel();
		for (JComponent c : components) {
			p.add(c);
		}
		return p;
	}

	public Player getPlayer() {
		return player;
	}
	
	public JComponent getUi() {
		return canvas;
	}
	
	public void setSelectedCard(@Nullable Card card) {
		this.cards.allCardUis().forEach(c -> {
			c.setSelected(c.getCard() == card);
		});
	}

	public void showCards() {
		this.cards.allCardUis().forEach(c -> c.setFaceUp(true));
		this.cards.setSuppressEvents(false);
	}

	public void hideCards() {
		this.cards.allCardUis().forEach(c -> {
			c.setFaceUp(false);
			c.setSelected(false);
		});
		this.cards.setSuppressEvents(true);
	}
	
	public void dispose() {
		this.player.removeObserver(uiUpdater);
	}
	
	public void addCardListener(GridUiListener lst) {
		this.cards.addListener(lst);
	}
	
	public void removeCardListener(GridUiListener lst) {
		this.cards.removeListener(lst);
	}
	
	
	private class UiUpdater implements PlayerObserver {

		@Override
		public void pointsChanged(Player player, int newPointTotal) {
			UiThread.run(() -> points.setText(String.valueOf(newPointTotal)));
		}

		@Override
		public void gotCard(Player player, Card card, int positionInHand) {
			UiThread.run(() -> {
				Position pos = new Position(0, positionInHand);
				cards.addCard(new CardUi(card), pos);
			});
		}

		@Override
		public void playedCard(Player player, PlacedCard placedCard) {
			UiThread.run(() -> {
				cards.removeCard(placedCard.getCard());
			});
		}
	}
	
}
