package nyu.cs.pqs.ps5.player;

import nyu.cs.pqs.ps5.grid.GridColor;
import nyu.cs.pqs.ps5.model.Connect4Model;

public class HumanPlayer implements Player {
  private PlayerType playerType;
  private Connect4Model model;
  private GridColor color;
  
  HumanPlayer(Connect4Model model, PlayerType playerType, GridColor color) {
    this.playerType = PlayerType.Human;
    this.model = model;
    this.color = color;
  }
  
  @Override
  public void play() {
    // No need to do anything here since the user has to take action on the UI.
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
    HumanPlayer other = (HumanPlayer) obj;
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
