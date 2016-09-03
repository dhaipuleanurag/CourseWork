package nyu.cs.pqs.ps5.model;

import java.util.ArrayList;

import nyu.cs.pqs.ps5.grid.Grid;
import nyu.cs.pqs.ps5.grid.Result;
import nyu.cs.pqs.ps5.grid.ResultValues;
import nyu.cs.pqs.ps5.grid.GridColor;
import nyu.cs.pqs.ps5.listener.Connect4Listener;
import nyu.cs.pqs.ps5.player.Player;

/***
 * This is the model for the game. All the core logic of the game resides in this place.
 * Any view that needs event update should register themselves using the API provided.
 * Following are the events that will be sent out: when a player wins, game draws, game ends,
 * coin added.
 * @author Anurag
 *
 */
public class Connect4Model {
  private Player currentTurn;
  private Grid grid;
  private ArrayList<Connect4Listener> listeners = new ArrayList<Connect4Listener>();
  private Player player1;
  private Player player2;
  private boolean gameStarted = false;
  
  public Connect4Model() {
    this.grid = Grid.getInstance();
  }
  
  /***
   * Gives the grid of the game.
   * @return
   */
  public Grid getGrid() {
    return grid;
  }
  
  /***
   * 
   * @return if game has started.
   */
  public boolean getIfGameStarted() {
    return gameStarted;
  }
  
  /***
   * Sets the players for the game. The color assigned to players should be different.
   * @param player1
   * @param player2
   */
  public void setPlayers(Player player1, Player player2) {
    if(player1 == null || player2 == null) {
      throw new NullPointerException();
    }
    if(player1.getPlayerColor() == player2.getPlayerColor()) {
      throw new IllegalArgumentException();
    }
    this.player1 = player1;
    this.player2 = player2;
    currentTurn = player1;
  }
  
  /***
   * Returns the player whose turn it is.
   * @return
   */
  public Player getCurrentTurn() {
    return currentTurn;
  }
  
  /***
   * API to register a view into model.
   * @param listener
   */
  public void addListener(Connect4Listener listener) {
    if(listener == null) {
      throw new NullPointerException();
    }
    listeners.add(listener);
  }
  
  /***
   * Function to start a game that also resets the game.
   */
  public void gameStart() {
    resetGame();
  }
  
  /***
   * Function to reset the game.
   */
  public void resetGame() {
    grid.reset();
    fireGameStartEvent();
    this.gameStarted = true;
  }
  
  private void fireGameStartEvent() {
    for (Connect4Listener listener: listeners) {
      listener.gameStart();
    }
  }
  
  /***
   * Function to insert Coin. When a coin is inserted by the user in the view side, the
   * view has to call this function to inform the model of user action.
   * @param column where coin is to be added.
   * @return
   */
  public Result insertCoin(int column) {
    if(!gameStarted) {
      fireGameEndEvent();
      return null;
    }
    //Result result = grid.addCoin(column, currentTurn.getPlayerColor());
    Result result = grid.addCoin(column, currentTurn.getPlayerColor());
    int resultRow = result.getRow();
    if(result.getResultValue() == ResultValues.WIN) {
      fireCoinAddedEvent(resultRow, column, currentTurn.getPlayerColor());
      firePlayerWonEvent(currentTurn.getPlayerColor());
      gameStarted = false;
    } else if(result.getResultValue() == ResultValues.DRAW) {
      fireCoinAddedEvent(resultRow, column, currentTurn.getPlayerColor());
      fireGameDrawEvent();
      gameStarted = false;
    } else if(result.getResultValue() == ResultValues.NORESULT) {
      fireCoinAddedEvent(resultRow, column, currentTurn.getPlayerColor());
      switchTurn();
      currentTurn.play();
    } else if(result.getResultValue() == ResultValues.INVALID) {
      fireInvalidMoveEvent();
      currentTurn.play();
    }
    return result;
  }
  
  private void fireInvalidMoveEvent() {
    for (Connect4Listener listener: listeners) {
      listener.invalidMove();
    }
  }
  
  private void fireGameDrawEvent() {
    for (Connect4Listener listener: listeners) {
      listener.gameDraw();
    }
  }
  
  private void fireGameEndEvent() {
    for (Connect4Listener listener: listeners) {
      listener.gameEnd();
    }
  }
  
  private void fireCoinAddedEvent(int row, int column, GridColor color) {
    for (Connect4Listener listener: listeners) {
      listener.coinAdded(row, column, color);
    }
  }
  
  private void firePlayerWonEvent(GridColor color) {
    for (Connect4Listener listener: listeners) {
      listener.playerWon(color);
    }
  }
  
  /***
   * Switches turn between players.
   */
  public void switchTurn() {
    if(currentTurn.equals(player1)) {
      currentTurn = player2;
    } else {
      currentTurn = player1;
    }
  }
}
