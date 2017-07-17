package jetoze.iota;

import javax.annotation.Nullable;

public interface GameStateObserver {

	default void gameHasStarted(GameState gameState, Card startCard) {/**/}
	
	default void playerInTurnChanged(Player player) {/**/}
	
	default void selectedPlayerCardChanged(@Nullable Card selectedCard) {/**/}
	
	default void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {/**/}
	
	default void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {/**/}
	
	default void gameOver(GameResult result) {/**/}
	
}
