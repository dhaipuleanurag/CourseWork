package nyu.cs.pqs.ps5.player;

import java.util.Random;

import nyu.cs.pqs.ps5.grid.Grid;
import nyu.cs.pqs.ps5.grid.GridColor;
import nyu.cs.pqs.ps5.model.Connect4Model;

public class ComputerPlayer implements Player {
  private PlayerType playerType;
  private Connect4Model model;
  private GridColor color;
  private Random RandomNumberGenerator;
  private int continuityCount;
  ComputerPlayer(Connect4Model model, PlayerType playerType, GridColor color) {
    this.playerType = PlayerType.Computer;
    this.model = model;
    this.color = color;
    RandomNumberGenerator = new Random();
    continuityCount = 4;
  }
  
  @Override
  public void play() {
    Grid grid = model.getGrid();
    int columnChoosen = 0; 
    boolean foundConnect = false;
    for(int i = 0; i < grid.getColumn(); i++) {
      int freeSlot = grid.getFirstFreeSlotInColumn(i);
      if(freeSlot == -1) {
        continue;
      }
      if(grid.checkRow(freeSlot, i, color, continuityCount -1) ||
          grid.checkColumn(freeSlot, i, color, continuityCount -1) ||
          grid.checkLeftDiagonal(freeSlot, i, color, continuityCount -1) ||
          grid.checkRightDiagonal(freeSlot, i, color, continuityCount -1)) {
        columnChoosen = i;
        foundConnect = true;
        break;
      }
    }
    if(!foundConnect) {
      columnChoosen = RandomNumberGenerator.nextInt(grid.getColumn());
    }
    model.insertCoin(columnChoosen);
  }

  @Override
  public PlayerType getPlayerType() {
    return playerType;
  }
  
  @Override
  public GridColor getPlayerColor() {
    return color;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((model == null) ? 0 : model.hashCode());
    result = prime * result + ((playerType == null) ? 0 : playerType.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ComputerPlayer other = (ComputerPlayer) obj;
    if (color != other.color)
      return false;
    if (model == null) {
      if (other.model != null)
        return false;
    } else if (!model.equals(other.model))
      return false;
    if (playerType != other.playerType)
      return false;
    return true;
  }
}
