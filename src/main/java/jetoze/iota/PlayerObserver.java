package jetoze.iota;

public interface PlayerObserver {

	void pointsChanged(Player player, int newPointTotal);
	
	void gotCard(Player player, Card card, int positionInHand);
	
	void playedCard(Player player, PlacedCard placedCard);
	
}
