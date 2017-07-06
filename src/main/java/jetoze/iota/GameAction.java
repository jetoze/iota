package jetoze.iota;

import static com.google.common.base.Preconditions.*;
import javax.annotation.Nullable;

@FunctionalInterface
public interface GameAction {

	public Result invoke(Player player, Grid grid, Deck deck);
	
	public static final class Result {
		
		public static final Result SUCCESS = new Result(null);
		
		public static final Result failed(String reason) {
			checkNotNull(reason);
			return new Result(reason);
		}
		
		@Nullable
		private final String error;
		
		private Result(@Nullable String error) {
			this.error = error;
		}
		
		public boolean isSuccess() {
			return error == null;
		}
		
		public String getError() {
			checkState(error != null);
			return error;
		}
	}
}
