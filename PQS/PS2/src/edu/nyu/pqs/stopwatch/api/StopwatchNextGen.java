package edu.nyu.pqs.stopwatch.api;

import java.util.*;

/**
 * @author Anurag
 * StopwatchNextGen is a thread safe class for Stopwatch. It has functions like start, lap, stop, reset and getAllLaps.
 * Lap functions records the time for that lap. Stop functions pauses the timer and continues when start is called again.
 * Reset clears all the lap records and stops the timer.
 * This is a thread safe stop watch which means many threads if have access to same stopwatch then the behavior is as if
 * the stopwatch is passed between different users/threads.
 */
public class StopwatchNextGen implements Stopwatch {
  private long currentLapStartTime;
  private boolean timerStarted;
  private long lastLapTime;
  private String id;
  private ArrayList<Long> lapTimes = new ArrayList<>();
  //Start method can be independent because until and unless stopwatch has started, other functions don't 
  //make sense. Hence a lock for start.
  private Object lockForStart = new Object();
  //Lap, Stop and Reset are dependent since they all modify shared variable and hence one single lock for all three.
  private Object lockForLapStopAndReset = new Object();
  protected StopwatchNextGen(String Id) {
    id = Id;
  }
    
  /* (non-Javadoc)
   * @see edu.nyu.pqs.stopwatch.api.Stopwatch#getId()
   */
  @Override
  public String getId() {
    return id;
  }
    
  /***
   * Gets the current system time in milliseconds.
   * @return System time in milliseconds.
   */
  private long getCurrentSystemTime() {
    //Converts nanoseconds to milliseconds.
    return System.nanoTime()/1000000;
  }
    
  /* (non-Javadoc)
   * @see edu.nyu.pqs.stopwatch.api.Stopwatch#start()
   */
  @Override
  public void start() {
    synchronized (lockForStart) {
      if(!timerStarted) {
        timerStarted = true;
        int totalLaps = lapTimes.size();
        currentLapStartTime = getCurrentSystemTime();
        // This check is to indicate that whether start is being done after stop(in which case there will already entries in laps) or
        //first start.
        if(totalLaps != 0) {
          int lastIndex = totalLaps - 1;
          lastLapTime = lastLapTime + lapTimes.remove(lastIndex);
        }
      } else {
        throw new IllegalStateException("Stopwatch with ID" + id.toString() + "is already running.");
      }
    }
  }
    
  /* (non-Javadoc)
   * @see edu.nyu.pqs.stopwatch.api.Stopwatch#lap()
   */
  @Override
  public void lap() {
    synchronized (lockForLapStopAndReset) {
      if(timerStarted) {
        long currentTime = getCurrentSystemTime();
        lapTimes.add(currentTime - currentLapStartTime + lastLapTime); 
        currentLapStartTime = currentTime;
        lastLapTime = 0;
      } else {
        throw new IllegalStateException("Stopwatch with ID" + id.toString() + "isn't running.");
      }
    }
  }
    
  /* (non-Javadoc)
   * @see edu.nyu.pqs.stopwatch.api.Stopwatch#stop()
   */
  @Override
  public void stop() {
    synchronized (lockForLapStopAndReset) {
      if(timerStarted) {
        lap();
        timerStarted = false;
      } else {
        throw new IllegalStateException("Stopwatch with ID" + id.toString() + "isn't running.");
      }
    }
  }
    
  /* (non-Javadoc)
   * @see edu.nyu.pqs.stopwatch.api.Stopwatch#reset()
   */
  @Override
  public void reset() {
    synchronized (lockForLapStopAndReset) {
      timerStarted = false;
      lastLapTime = 0;
      lapTimes.clear();
    }
  }
    
  /* (non-Javadoc)
   * @see edu.nyu.pqs.stopwatch.api.Stopwatch#getLapTimes()
   */
  @Override
  public List<Long> getLapTimes() {
    ArrayList<Long> lapTimesTemp = new ArrayList<Long>();
    // This is to ensure private copy is not exposed to public.
    for(Long lapTime: lapTimes) {
      lapTimesTemp.add(lapTime);
    }
    return lapTimesTemp; 
  }
     
  @Override
  public String toString() {
    return "StopwatchNextGen [id=" + id + ", lapTimes=" + lapTimes + "]";
  }
    
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }
    
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
	}  
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StopwatchNextGen other = (StopwatchNextGen) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id)) {
        return false;
    }
    return true;
  }
}