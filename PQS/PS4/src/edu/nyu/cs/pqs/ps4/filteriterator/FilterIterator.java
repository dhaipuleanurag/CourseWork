package edu.nyu.cs.pqs.ps4.filteriterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.nyu.cs.pqs.ps4.predicate.Predicate;

/***
 * Class that further adds condition on the iterator. The condition is passed as predicate along with the iterator.
 * @author Anurag
 *
 * @param <T> Generic parameter type on which the class performs its operation.
 */
public class FilterIterator<T> implements Iterator<T> {

  private Iterator<T> iterator;
  private Predicate predicate;
  private boolean nextIsValid;
  private T nextItem;
  
  FilterIterator(Iterator<T> iterator, Predicate predicate){
    this.iterator = iterator;
    this.predicate = predicate;
  }
  
  /***
   * Checks if there is next element in the iteration that also satisfies the condition of predicate.
   */
  @Override
  public boolean hasNext() {
    if (nextIsValid) {
      return true;
    }
    while (iterator.hasNext()) {
      nextItem = iterator.next();
      if (predicate.accept(nextItem)) {
        nextIsValid = true;
        return true;
      }
    }
    return false;
  }
   
  /***
   * Returns the next element in the iteartions. Before calling this method make sure, hasNext() has returned true.
   * This object returned satisfies the condition presented in the predicate.
   */
  @Override
  public T next() {
    if (nextIsValid) {
      nextIsValid = false;
      T val = nextItem;
      nextItem = null;
      return val;
    }
    if (hasNext()) {
      return next();
    }
    throw new NoSuchElementException();
  }

  @Override
  public String toString() {
    return "FilterIterator [iterator=" + iterator + ", predicate=" + predicate + "]";
  }
}
