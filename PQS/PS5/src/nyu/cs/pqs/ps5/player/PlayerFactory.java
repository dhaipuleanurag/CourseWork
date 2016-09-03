package nyu.cs.pqs.ps5.player;

import nyu.cs.pqs.ps5.grid.GridColor;
import nyu.cs.pqs.ps5.model.Connect4Model;

public class PlayerFactory {
  
  public Player getPlayer(Connect4Model model, PlayerType playerType, GridColor color) {
    if(model == null) {
      throw new NullPointerException();
    }
    if(playerType == PlayerType.Computer) {
      return new ComputerPlayer(model, playerType, color);
    } else {
      return new HumanPlayer(model, playerType, color);
    }
  }
}
