package nyu.cs.pqs.ps5.grid;

/***
 * A singleton class stores all information about the grid. It has a matrix that stores
 * which coins are in what position.
 * All necessary operations like when a player has won, when a update needs to be done
 * are stores in this class. In general, any API related to the matrix are stores here.
 * @author Anurag
 *
 */
public class Grid {
  
  private final int rows;
  private final int columns;
  private GridColor[][] matrix; 
  private static Grid grid = null;
  private final int allMoves;
  private int totalMovesAsOfNow = 0;
  private int continuityCountNeeded;
  private Grid() {
    rows = 6;
    columns = 7;
    allMoves = rows * columns;
    continuityCountNeeded = 4;
    matrix = new GridColor[rows][columns];
  }
  
  /***
   * This is a singleton class and hence this API gives the instance of 
   * this class.
   * @return
   */
  public static Grid getInstance() {
    if (grid == null) {
      grid = new Grid();
    }
    return grid;
  }
  
  /***
   * This functions returns a copy of the matrix.
   * @return
   */
  public GridColor[][] getMatrix() {
    GridColor[][] temp = new GridColor[rows][columns];
    for(int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        temp[i][j] = matrix[i][j];
      }
    }
    return temp;
  }
  
  /***
   * Total number of columns in the grid.
   * @return
   */
  public int getColumn() {
    return columns;
  }
  
  /***
   * Total number of rows in the grid.
   * @return
   */
  public int getRows() {
    return rows;
  }

  /***
   * Resets the boards. All the slots in the matrix are made empty and game is
   * started.
   */
  public void reset() {
    for(int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        matrix[i][j] = GridColor.EMPTY;
      }
    }
  }
  
  /***
   * This functions adds a coin to a particular column in the board.
   * @param column The column where coin is to be added.
   * @param color Color of the coin to be added.
   * @return Result of coin addition.
   */
  public Result addCoin(int column, GridColor color) {
    if(column < 0 || column >= columns) {
      throw new IllegalArgumentException();
    }
    int freeSlotInColumn = getFirstFreeSlotInColumn(column);
    if(freeSlotInColumn == -1) {
      return new Result(-1, -1, ResultValues.INVALID);
    } else {
      matrix[freeSlotInColumn][column] = color;
      return checkForResult(freeSlotInColumn, column, color, continuityCountNeeded -1);
    }
  }
  
  /***
   * This API determines the first free row in a given column.
   * @param column The column where we need to find free row.
   * @return
   */
  public int getFirstFreeSlotInColumn(int column) {
    if(column < 0 || column >= columns) {
      throw new IllegalArgumentException();
    }
    for(int i = 0; i < rows; i++) {
      if(matrix[i][column] == GridColor.EMPTY) {
        return i;
      }
    }
    return -1;
  }
  
  private Result checkForResult(int currentCoinRow, int currentCoinColumn, GridColor color, int continuityCount) {
    if(checkRow(currentCoinRow, currentCoinColumn, color, continuityCount) ||
        checkColumn(currentCoinRow, currentCoinColumn, color, continuityCount) ||
        checkLeftDiagonal(currentCoinRow, currentCoinColumn, color, continuityCount) || 
        checkRightDiagonal(currentCoinRow, currentCoinColumn, color, continuityCount)) {
     return new Result(currentCoinRow, currentCoinColumn, ResultValues.WIN);
    }
    totalMovesAsOfNow++;
    if(totalMovesAsOfNow == allMoves) {
      return new Result(currentCoinRow, currentCoinColumn, ResultValues.DRAW);
    }
    return new Result(currentCoinRow, currentCoinColumn, ResultValues.NORESULT);
  }
  
  /***
   * Determines if a coin were to be added in a particular position would it create 
   * a continuous row connect of coins.
   * @param currentCoinRow Row where coin is to be added
   * @param currentCoinColumn Column where coin is to added.
   * @param color Color of coin to be added
   * @param continuityCount how long the continous coin length should be excluding the 
   * coin to be added.
   * @return
   */
  public boolean checkRow(int currentCoinRow, int currentCoinColumn, GridColor color, int continuityCount) {
    if(currentCoinColumn < 0 || currentCoinColumn >= columns ||
        currentCoinRow < 0 || currentCoinRow >= rows ||
        continuityCount < 0) {
      throw new IllegalArgumentException();
    }
    int leftCount = 0;
    for(int i = currentCoinColumn-1; i >=0; i--) {
      if(matrix[currentCoinRow][i] == color ) {
        leftCount++;
      } else {
        break;
      }
    }
    int rightCount = 0;
    for(int i = currentCoinColumn+1; i < columns; i++) {
      if(matrix[currentCoinRow][i] == color ) {
        rightCount++;
      } else {
        break;
      }
    }
    if(leftCount + rightCount >= continuityCount) {
      return true;
    }
    return false;
  }
  
  /***
   * Determines if a coin were to be added in a particular position would it create 
   * a continuous column connect of coins.
   * @param currentCoinRow Row where coin is to be added
   * @param currentCoinColumn Column where coin is to added.
   * @param color Color of coin to be added
   * @param continuityCount how long the continous coin length should be excluding the 
   * coin to be added.
   * @return
   */
  public boolean checkColumn(int currentCoinRow, int currentCoinColumn, GridColor color, int continuityCount) {
    if(currentCoinColumn < 0 || currentCoinColumn >= columns ||
        currentCoinRow < 0 || currentCoinRow >= rows ||
        continuityCount < 0) {
      throw new IllegalArgumentException();
    }
    int count = 0;
    for(int i = currentCoinRow -1; i >=0; i--) {
      if(matrix[i][currentCoinColumn] == color ) {
        count++;
      } else {
        break;
      }
    }
    if(count >= continuityCount) {
      return true;
    }
    return false;
  }
  
  /***
   * Determines if a coin were to be added in a particular position would it create 
   * a continuous left diagonal connect of coins.
   * @param currentCoinRow Row where coin is to be added
   * @param currentCoinColumn Column where coin is to added.
   * @param color Color of coin to be added
   * @param continuityCount how long the continous coin length should be excluding the 
   * coin to be added.
   * @return
   */
  public boolean checkLeftDiagonal(int currentCoinRow, int currentCoinColumn, GridColor color, int continuityCount) {
    if(currentCoinColumn < 0 || currentCoinColumn >= columns ||
        currentCoinRow < 0 || currentCoinRow >= rows ||
        continuityCount < 0) {
      throw new IllegalArgumentException();
    }
    int count = 0;
    for(int i = currentCoinRow-1, j = currentCoinColumn-1; i >=0 && j >=0; i--, j--) {
      if(matrix[i][j] == color ) {
        count++;
      } else {
        break;
      }
    }
    if(count >= continuityCount) {
      return true;
    }
    return false;
  }
  
  /***
   * Determines if a coin were to be added in a particular position would it create 
   * a continuous right diagonal connect of coins.
   * @param currentCoinRow Row where coin is to be added
   * @param currentCoinColumn Column where coin is to added.
   * @param color Color of coin to be added
   * @param continuityCount how long the continous coin length should be excluding the 
   * coin to be added.
   * @return
   */
  public boolean checkRightDiagonal(int currentCoinRow, int currentCoinColumn, GridColor color, int continuityCount) {
    if(currentCoinColumn < 0 || currentCoinColumn >= columns ||
        currentCoinRow < 0 || currentCoinRow >= rows ||
        continuityCount < 0) {
      throw new IllegalArgumentException();
    }
    int count = 0;
    for(int i = currentCoinRow-1, j = currentCoinColumn+1; i >=0 && j < columns; i--, j++) {
      if(matrix[i][j] == color ) {
        count++;
      } else {
        break;
      }
    }
    if(count >= continuityCount) {
      return true;
    }
    return false;
  }
}
