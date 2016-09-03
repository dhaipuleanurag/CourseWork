package nyu.cs.pqs.ps5.player;

import nyu.cs.pqs.ps5.grid.GridColor;

public interface Player {
  public void play();
  public PlayerType getPlayerType();
  public GridColor getPlayerColor();
}
