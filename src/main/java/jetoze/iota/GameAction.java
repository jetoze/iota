package jetoze.iota;

@FunctionalInterface
public interface GameAction {

	public void perform(Player player, Grid grid, Deck deck);

	public static final GameAction PASS = (p, g, d) -> {/**/};
	
	
}
