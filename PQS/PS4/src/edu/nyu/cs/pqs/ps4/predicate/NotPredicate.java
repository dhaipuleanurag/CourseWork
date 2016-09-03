package edu.nyu.cs.pqs.ps4.predicate;

/***
 * Class that supports predicate operation by NOTing the passed predicate as parameter.
 * @author Anurag
 *
 * @param <T> Generic type on which the predicate functions.
 */
public class NotPredicate<T> implements Predicate<T> {

  Predicate predicate;
  
  public NotPredicate(Predicate predicate){
    if(predicate == null){
      throw new NullPointerException();
    }
    this.predicate = predicate;
  }
  
  @Override
  public boolean accept(T item) {
    return !predicate.accept(item); 
  }
}
