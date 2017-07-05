package jetoze.iota.ui;

import jetoze.iota.Position;

public interface GridUiListener {

	void emptyCellWasClickedOn(Position pos, int numberOfClicks);
	
	void cardWasClickedOn(CardUi cardUi, int numberOfClicks);
	
}
