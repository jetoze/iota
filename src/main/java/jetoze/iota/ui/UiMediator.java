package jetoze.iota.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import jetoze.iota.Card;
import jetoze.iota.GameResult;
import jetoze.iota.GameState;
import jetoze.iota.GameStateObserver;
import jetoze.iota.Player;
import jetoze.iota.Position;

/**
 * Listens to changes in the GameState and updates the UI accordingly, and listens to UI events and updates
 * the GameState accordingly.
 *
 */
public final class UiMediator {

	private final GameState gameState;
	
	private final GameBoard gameBoard;
	
	private final GameStateObserverImpl gameStateObserver = new GameStateObserverImpl();
	
	private final GridListener gridListener = new GridListener();

	private final ActivePlayerAreaListener playerAreaListener = new ActivePlayerAreaListener();
	
	public UiMediator(GameState gameState, GameBoard gameBoard) {
		this.gameState = checkNotNull(gameState);
		this.gameBoard = checkNotNull(gameBoard);
	}
	
	public void install() {
		gameState.addObserver(gameStateObserver);
		gameBoard.addGridListener(gridListener);
		gameBoard.addPlayerAreaListener(playerAreaListener);
	}
	
	public void dispose() {
		gameBoard.removePlayerAreaListener(playerAreaListener);
		gameBoard.removeGridListener(gridListener);
		gameState.removeObserver(gameStateObserver);
	}
	
	private void doGameOverCleanup() {
		dispose();
	}

	
	private class ActivePlayerAreaListener implements GridUiListener {
		
		@Override
		public void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {
			if (cardUi.isSelected()) {
				gameState.setSelectedPlayerCard(null);
				gameBoard.setSelectedPlayerCard(null);
			} else {
				gameState.setSelectedPlayerCard(cardUi.getCard());
				gameBoard.setSelectedPlayerCard(cardUi.getCard());
			}
		}
	}
	
	
	private class GridListener implements GridUiListener {

		@Override
		public void emptyCellWasClickedOn(Position pos, int numberOfClicks) {
			if (numberOfClicks == 1) {
				gameState.placeSelectedCard(pos);
			}
		}

		@Override
		public void cardWasClickedOn(CardUi cardUi, int numberOfClicks) {
			if (numberOfClicks == 2 && gameState.isPlacedCard(cardUi.getCard())) {
				gameState.returnPlacedCard(cardUi.getCard());
			}
		}
	}
	
	
	private class GameStateObserverImpl implements GameStateObserver {

		@Override
		public void gameHasStarted(GameState gameState, Card startCard) {
			gameBoard.start(startCard);
		}

		@Override
		public void playerInTurnChanged(Player player) {
			gameBoard.setActivePlayer(player);
			gameBoard.unselectAllPlacedCards();
		}

		@Override
		public void cardWasPlacedOnBoard(Card card, Position positionOnBoard, int value) {
			gameBoard.placeCard(card, positionOnBoard);
		}

		@Override
		public void cardWasRemovedFromBoard(Card card, Position positionOnBoard, int value) {
			gameBoard.removeCard(card);
		}
		
		@Override
		public void gameOver(GameResult result) {
			gameBoard.presentGameResult(result);
			doGameOverCleanup();
		}
	}
	
}
