package edu.nyu.pqs.stopwatch.impl;

import java.util.ArrayList;
import java.util.List;

import edu.nyu.pqs.stopwatch.api.Stopwatch;

/**
 * This class implements Stopwatch. It is thread-safe, since all methods that are reading or writing
 * mutable variables are synchronized.
 *
 * @author Xi Xiao<xx565@nyu.edu>
 *
 */
public class StopwatchCreator implements Stopwatch {
  private boolean running = false;
  private final String StopwatchId;
  private List<Long> lapTime = new ArrayList<Long>();
  private long pastTime = 0;
  private long startTime = 0;
  private long endTime = 0;

  // package-access constructor
  // since we checked if id is null or empty in method getStopwatch, we could just assign id to
  // StopwatchId here.
  StopwatchCreator(String id) {
    StopwatchId = id;
  }

  /**
   * Returns the Id of this stopwatch
   *
   * @return the Id of this stopwatch. Will never be empty or null.
   */
  public String getId() {
    return StopwatchId;
  }

  /**
   * Starts the stopwatch.
   *
   * @throws IllegalStateException thrown when the stopwatch is already running
   */
  public synchronized void start() throws IllegalStateException {
    if (running == true) {
      throw new IllegalStateException();
    }
    startTime = System.currentTimeMillis();
    running = true;
    if (lapTime.isEmpty()) {
      pastTime = 0;
    } else {
      pastTime = lapTime.get(lapTime.size() - 1);
      lapTime.remove(lapTime.size() - 1);
    }
  }

  /**
   * Stores the time elapsed since the last time lap() was called or since start() was called if
   * this is the first lap.
   *
   * @throws IllegalStateException thrown when the stopwatch isn't running
   */
  public synchronized void lap() throws IllegalStateException {
    if (running == false) {
      throw new IllegalStateException();
    }
    endTime = System.currentTimeMillis();
    lapTime.add(endTime - startTime + pastTime);
    startTime = endTime;
    pastTime = 0;
  }

  /**
   * Stops the stopwatch (and records one final lap).
   *
   * @throws IllegalStateException thrown when the stopwatch isn't running
   */
  public synchronized void stop() throws IllegalStateException {
    if (running == false) {
      throw new IllegalStateException();
    }
    long time = System.currentTimeMillis() - startTime;
    pastTime += time;
    lapTime.add(pastTime);
    running = false;
  }

  /**
   * Resets the stopwatch. If the stopwatch is running, this method stops the watch and resets it.
   * This also clears all recorded laps.
   */
  public synchronized void reset() {
    lapTime.clear();
    running = false;
    pastTime = 0;
  }

  /**
   * Returns a list of lap times (in milliseconds). This method can be called at any time and will
   * not throw an exception.
   *
   * @return a list of recorded lap times or an empty list.
   */
  @SuppressWarnings("unchecked")
  public synchronized List<Long> getLapTimes() {
    if (running == true) {
      endTime = System.currentTimeMillis();
      long time = endTime - startTime + pastTime;
      ArrayList<Long> result = new ArrayList<Long>(lapTime);
      result.add(time);
      return result;
    } else {
      return (List<Long>) ((ArrayList<Long>) lapTime).clone();
    }
  }

  @Override
  public synchronized String toString() {
    return "Stopwatch " + StopwatchId + " : " + lapTime.toString();
  }

  @Override
  public synchronized boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof StopwatchCreator)) {
      return false;
    }
    StopwatchCreator other = (StopwatchCreator) obj;
    if (StopwatchId == null) {
      if (other.StopwatchId != null) {
        return false;
      }
    } else if (!StopwatchId.equals(other.StopwatchId)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((StopwatchId == null) ? 0 : StopwatchId.hashCode());
    return result;
  }
}
