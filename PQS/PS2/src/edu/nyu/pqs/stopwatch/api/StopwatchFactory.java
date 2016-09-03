package edu.nyu.pqs.stopwatch.api;

import java.util.*;;
/**
 * The StopwatchFactory is a thread-safe factory class for Stopwatch objects.
 * It maintains references to all created Stopwatch objects and provides a
 * convenient method for getting a list of those objects.
 */
public class StopwatchFactory {
  private static ArrayList<Stopwatch> stopwatches = new ArrayList<Stopwatch>();
  /**
   * Creates and returns a new Stopwatch object
   * @param id The identifier of the new object
   * @return The new Stopwatch object
   * @throws IllegalArgumentException if <code>id</code> is empty, null, or
   *     already taken.
   */
  public static Stopwatch getStopwatch(String id) {
    // replace this return statement with correct code
    for(Stopwatch stopwatch: stopwatches){
      if(stopwatch.getId().equals(id)) {
        throw new IllegalArgumentException("ID " + id + " exists!");
      }
    }
    Stopwatch stopwatch= new StopwatchNextGen(id);
    stopwatches.add(stopwatch);
    return stopwatch;
  }
    
  /**
   * Returns a list of all created stopwatches
   * @return a List of al creates Stopwatch objects.  Returns an empty
   * list if no Stopwatches have been created.
   */
  public static List<Stopwatch> getStopwatches() {
    ArrayList<Stopwatch> stopwatchesTemp = new ArrayList<Stopwatch>();
    for(Stopwatch stopwatch: stopwatches) {
      stopwatchesTemp.add(stopwatch);
    }
    return stopwatchesTemp;
  }
}