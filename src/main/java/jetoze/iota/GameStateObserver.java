package jetoze.iota;

import javax.annotation.Nullable;

public interface GameStateObserver {

	default void gameHasStarted(GameState initialState) {/**/}
	
	default void playerInTurnChanged(Player player) {/**/}
	
	default void selectedPlayerCardChanged(@Nullable Card selected, @Nullable Position position) {/**/}
	
	default void cardWasPlacedOnBoard(Card card, Position positionOnBoard) {/**/}
	
	// TODO: This is not enough. We want to return the card to the same location
	// on the player's grid that it originated from.
	default void cardWasRemovedFromBoard(Card card, Position positionOnBoard) {/**/}
	
	// Allows us to disable the Get New Cards button when the deck becomes empty.
	default void cardsWereDealtToPlayer(Deck deck) {/**/}
	
}
