package edu.nyu.pqs.stopwatch.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.nyu.pqs.stopwatch.api.Stopwatch;

/**
 * The StopwatchFactory is a thread-safe factory class for Stopwatch objects. It maintains
 * references to all created Stopwatch objects and provides a convenient method for getting a list
 * of those objects.
 *
 * @author Xi Xiao<xx565@nyu.edu>
 */
public class StopwatchFactory {

  // using list rather than arraylist or linked list to follow the code to an interface rule.
  // using CopyOnWriteArrayList to avoid explicit synchronization.
  private static List<Stopwatch> stopwatches = new CopyOnWriteArrayList<Stopwatch>();

  /**
   * Creates and returns a new Stopwatch object
   *
   * @param id The identifier of the new object
   * @return The new Stopwatch object
   * @throws IllegalArgumentException if <code>id</code> is empty, null, or already taken.
   */
  public synchronized static Stopwatch getStopwatch(String id) throws IllegalArgumentException {
    if (id == null || id.length() == 0) {
      throw new IllegalArgumentException();
    }
    for (Stopwatch sw : stopwatches) {
      if (sw.getId().equals(id)) {
        throw new IllegalArgumentException();
        // if asked to return the same id stopwatch instead of throwing an
        // IllegalArgumentException, here should be return sw;
      }
    }
    Stopwatch stopwatchObject = new StopwatchCreator(id);
    stopwatches.add(stopwatchObject);
    return stopwatchObject;
  }

  /**
   * Returns a list of all created stopwatches
   *
   * @return a List of all created Stopwatches objects. Returns an empty list if no Stopwatch have
   *         been created.
   */
  @SuppressWarnings("unchecked")
  public static List<Stopwatch> getStopwatches() {
    return (List<Stopwatch>) ((CopyOnWriteArrayList<Stopwatch>) stopwatches).clone();
  }
}
