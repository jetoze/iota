package jetoze.iota;

public interface PlayerObserver {

	void pointsChanged(Player player, int newPointTotal);
	
	void gotCard(Player player, Card card);
	
	void playedCard(Player player, Card card);
	
}
