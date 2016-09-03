package nyu.cs.pqs.ps5.player;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nyu.cs.pqs.ps5.grid.Grid;
import nyu.cs.pqs.ps5.grid.GridColor;
import nyu.cs.pqs.ps5.grid.Result;
import nyu.cs.pqs.ps5.model.Connect4Model;
import nyu.cs.pqs.ps5.view.Connect4View;

public class PlayerTest {
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
  public void ComputerPlayTest() {
    model.gameStart();
    for(int i = 0; i < 4; i++) {
      Result result = grid.addCoin(i, GridColor.RED);
    }
    assertEquals(GridColor.EMPTY, grid.getMatrix()[0][4]);
    player1.play();
    assertEquals(GridColor.RED, grid.getMatrix()[0][4]);
  }
  
  @Test
  public void PlayerTypeHumanTest() {
    assertEquals(PlayerType.Human, player2.getPlayerType());
    assertEquals(GridColor.BLUE, player2.getPlayerColor());
  }
  
  @Test
  public void PlayerTypeComputerTest() {
    assertEquals(PlayerType.Computer, player1.getPlayerType());
    assertEquals(GridColor.RED, player1.getPlayerColor());
  }
  
  
  @Test(expected = NullPointerException.class) 
  public void factoryTestWithNullModel() {
    playerFactory.getPlayer(null, PlayerType.Computer, GridColor.BLUE);
  }
}
