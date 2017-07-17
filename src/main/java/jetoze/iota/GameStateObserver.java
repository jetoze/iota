package jetoze.iota;

import com.google.common.collect.ImmutableSet;

public interface GameStateObserver {

	default void gameHasStarted(GameState gameState, Card startCard) {/**/}
	
	default void playerInTurnChanged(Player player) {/**/}
	
	default void selectedPlayerCardChanged(ImmutableSet<Card> selectedCards) {/**/}
	
	default void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {/**/}
	
	default void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {/**/}
	
	default void gameOver(GameResult result) {/**/}
	
}
