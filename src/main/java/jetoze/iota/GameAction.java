package jetoze.iota;

@FunctionalInterface
public interface GameAction {

	// TODO: Return a Result object that indicates success or failure.
	// Examples of failures: Invalid Line, or Not Enough Cards in Deck.
	public void perform(Player player, Grid grid, Deck deck);
	
	
}
