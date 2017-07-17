package jetoze.iota;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The result of the game. Either a win for one of the player, or a tie between two or more players.
 */
public abstract class GameResult {

	public static GameResult wonBy(Player winner) {
		return new Win(winner);
	}
	
	public static GameResult tie() {
		return new Tie();
	}
	
	/**
	 * Checks if the outcome of the game was that one player won.
	 * @return
	 */
	public abstract boolean isWin();
	
	/**
	 * Checks if the outcome of the game was that two or more players ended with
	 * the same number of points.
	 * @return
	 */
	public final boolean isTie() {
		return !isWin();
	}
	
	/**
	 * Returns the winner of the game.
	 * <p>
	 * It is a programming error to call this method if {@link #isWin()} returns {@code false}.
	 */
	public Player getWinner() {
		throw new UnsupportedOperationException();
	}
	
	
	private static class Win extends GameResult {
		
		private final Player winner;
		
		public Win(Player winner) {
			this.winner = checkNotNull(winner);
		}
		
		@Override
		public boolean isWin() {
			return true;
		}
		
		@Override
		public Player getWinner() {
			return winner;
		}
		
		@Override
		public String toString() {
			return String.format("Winner: %s (%d points)", winner.getName(), winner.getPoints());
		}
	}

	
	private static class Tie extends GameResult {
		
		@Override
		public boolean isWin() {
			return false;
		}
		
		@Override
		public String toString() {
			return "Tie";
		}
	}
	
}
