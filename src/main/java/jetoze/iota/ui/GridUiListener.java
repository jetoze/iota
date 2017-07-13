package jetoze.iota.ui;

import jetoze.iota.Position;

public interface GridUiListener {

	default void emptyCellWasClickedOn(Position pos, int numberOfClicks) {/**/}
	
	default void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {/**/}
	
}
