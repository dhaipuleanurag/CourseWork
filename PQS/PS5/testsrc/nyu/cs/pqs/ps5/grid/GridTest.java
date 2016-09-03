package nyu.cs.pqs.ps5.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class GridTest {
  
  Grid grid;
  int rows;
  int columns;
  
  @Before
  public void setUp() {
    grid = Grid.getInstance();
    rows = 6;
    columns = 7;
    grid.reset();
  }
  
  @Test
  public void checkReset() {
    grid.addCoin(1, GridColor.BLUE);
    grid.reset();
    GridColor[][] matrix = grid.getMatrix(); 
    for(int i = 0; i < grid.getRows(); i++) {
      for (int j = 0; j < grid.getColumn(); j++) {
        assertEquals(matrix[i][j], GridColor.EMPTY);
      }
    }
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addCoinNegativeColumnSizeTest() {
    grid.addCoin(-1, GridColor.BLUE);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addCoinHigherColumnSizeTest() {
    grid.addCoin(10, GridColor.BLUE);
  }
  
  @Test
  public void invalidAddCoinTest() {
    for(int i = 0; i < rows; i++) {
    grid.addCoin(0, GridColor.BLUE);
    }
    Result result = grid.addCoin(0, GridColor.BLUE);
    assertEquals(result.getResultValue(), ResultValues.INVALID);
  }
  
  @Test
  public void blueWinCoinEntryTest() {
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(0, GridColor.BLUE);
      assertEquals(result.getResultValue(), ResultValues.NORESULT);
    }
    Result result = grid.addCoin(0, GridColor.BLUE);
    assertEquals(result.getResultValue(), ResultValues.WIN);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void getFirstFreeSlotNegativeColumnTest() {
    grid.getFirstFreeSlotInColumn(-1);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void getFirstFreeSlotLargerColumnTest() {
    grid.getFirstFreeSlotInColumn(columns);
  }
  
  @Test
  public void noFreeSlotFoundTest() {
    for(int i = 0; i < rows; i++) {
      Result result = grid.addCoin(0, GridColor.BLUE);
    }
    assertEquals(-1, grid.getFirstFreeSlotInColumn(0));
  }
  
  @Test
  public void checkColumnContinuityTest() {
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(0, GridColor.BLUE);
    }
    assertEquals(true,grid.checkColumn(3, 0, GridColor.BLUE, 3));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkColumnNegativeRowTest() {
    grid.checkColumn(-1, 0, GridColor.BLUE, 3);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkColumnNegativeColumnTest() {
    grid.checkColumn(0, -1, GridColor.BLUE, 3);
  }
  
  @Test
  public void checkColumnNotContinuousTest() {
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(0, GridColor.BLUE);
    }
    assertEquals(false,grid.checkColumn(3, 1, GridColor.BLUE, 3));
  }
  
  @Test
  public void checkRowContinuityTest() {
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(i, GridColor.BLUE);
    }
    assertEquals(true,grid.checkRow(0, 3, GridColor.BLUE, 3));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkRowNegativeRowTest() {
    grid.checkRow(-1, 1, GridColor.BLUE, 3);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkRowNegativeColumnTest() {
    grid.checkRow(0, -1, GridColor.BLUE, 3);
  }
  
  @Test
  public void checkRowNotContinuousTest() {
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(i, GridColor.BLUE);
    }
    assertEquals(false, grid.checkRow(1, 3, GridColor.BLUE, 3));
  }
  
  @Test
  public void checkLeftDiagonalContinuityTest() {
    for(int i = 0; i < 1; i++) {
      Result result = grid.addCoin(0, GridColor.BLUE);
    }
    for(int i = 0; i < 2; i++) {
      Result result = grid.addCoin(1, GridColor.BLUE);
    }
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(2, GridColor.BLUE);
    }
    assertEquals(true,grid.checkLeftDiagonal(3, 3, GridColor.BLUE, 3));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkLeftDiagonalNegativeRowTest() {
    grid.checkLeftDiagonal(-1, 0, GridColor.BLUE, 3);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkLeftDiagonalNegativeColumnTest() {
    grid.checkLeftDiagonal(0, -1, GridColor.BLUE, 3);
  }
  
  @Test
  public void checkRightDiagonalContinuityTest() {
    for(int i = 0; i < 3; i++) {
      Result result = grid.addCoin(1, GridColor.BLUE);
    }
    for(int i = 0; i < 2; i++) {
      Result result = grid.addCoin(2, GridColor.BLUE);
    }
    for(int i = 0; i < 1; i++) {
      Result result = grid.addCoin(3, GridColor.BLUE);
    }
    assertEquals(true,grid.checkRightDiagonal(3, 0, GridColor.BLUE, 3));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkRightDiagonalNegativeRowTest() {
    grid.checkRightDiagonal(-1, 0, GridColor.BLUE, 3);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void checkRightDiagonalNegativeColumnTest() {
    grid.checkRightDiagonal(0, -1, GridColor.BLUE, 3);
  }
}