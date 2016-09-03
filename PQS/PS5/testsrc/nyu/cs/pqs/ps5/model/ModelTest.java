package nyu.cs.pqs.ps5.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nyu.cs.pqs.ps5.grid.Grid;
import nyu.cs.pqs.ps5.grid.GridColor;
import nyu.cs.pqs.ps5.grid.Result;
import nyu.cs.pqs.ps5.grid.ResultValues;
import nyu.cs.pqs.ps5.player.Player;
import nyu.cs.pqs.ps5.player.PlayerFactory;
import nyu.cs.pqs.ps5.player.PlayerType;
import nyu.cs.pqs.ps5.view.Connect4View;

public class ModelTest {
  
  Grid grid;
  Player player1;
  Player player2;
  PlayerFactory playerFactory;
  Connect4Model model;
  
  @Before
  public void SetUp() {
    model =  new Connect4Model();
    playerFactory = new PlayerFactory();
    
    player1 = playerFactory.getPlayer(model, PlayerType.Computer, GridColor.RED);
    player2 = playerFactory.getPlayer(model, PlayerType.Human, GridColor.BLUE);
    grid = model.getGrid();
    grid.reset();
    model.setPlayers(player1, player2);
  }
  
  @Test
  public void switchPlayerTest() {
    assertEquals(model.getCurrentTurn(), player1);
    model.switchTurn();
    assertEquals(model.getCurrentTurn(), player2);
    model.switchTurn();
    assertEquals(model.getCurrentTurn(), player1);
  }
  
  @Test(expected=NullPointerException.class)
  public void setFirstPlayerNullTest() {
    model.setPlayers(null, player2);
  }
  
  @Test(expected=NullPointerException.class)
  public void setSecondPlayerNullTest() {
    model.setPlayers(player1, null);
  }
  
  @Test(expected= IllegalArgumentException.class)
  public void setPlayersWithSameColorTest() {
    Player player3 = playerFactory.getPlayer(model, PlayerType.Human, GridColor.BLUE);
    model.setPlayers(player3, player2);
  }
  
  @Test(expected=NullPointerException.class)
  public void addNullListenerTest() {
    model.addListener(null);
  }
  
  @Test
  public void gameStartedTest() {
   assertEquals(false, model.getIfGameStarted());
   model.gameStart();
   assertEquals(true, model.getIfGameStarted());
  }
  
  @Test
  public void insertCoinGameNotStartedTest() {
    assertEquals(null, model.insertCoin(1));
  }
  
  @Test
  public void insertCoin_GameWonResult() {
    model.gameStart();
    for(int i = 0; i < 3; i++) {
      assertEquals(ResultValues.NORESULT, model.insertCoin(1).getResultValue());
      model.switchTurn();
    }
    assertEquals(ResultValues.WIN, model.insertCoin(1).getResultValue());
  }
  
  @Test
  public void insertCoin_GameInvalidResult() {
    model.gameStart();
    for(int i = 0; i < grid.getColumn(); i++) {
      for(int j = 0; i < grid.getRows(); j++)
        model.insertCoin(i);
    }
    assertEquals(ResultValues.INVALID, model.insertCoin(1).getResultValue());
  }
}
