package nyu.cs.pqs.ps5.grid;

/***
 * Class to store result of addition of a coin to grid.
 * @author Anurag
 *
 */
public class Result {
  private int row;
  private int column;
  private ResultValues resultValue;
  
  Result(int row, int column, ResultValues resultValue) {
    this.row = row;
    this.column = column;
    this.resultValue = resultValue;
  }
  
  /***
   * Gets the count of rows.
   * @return
   */
  public int getRow() {
    return row;
  }
  
  /***
   * Gets the count of columns.
   * @return
   */
  public int getColumn() {
    return column;
  }
  
  /***
   * Gets the result value from result.
   * @return
   */
  public ResultValues getResultValue() {
    return resultValue;
  }
}
