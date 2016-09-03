package edu.nyu.cs.pqs.ps4.predicate;

/***
 * Class that supports predicate operation by ORing two passed predicate as parameter.
 * @author Anurag
 *
 * @param <T> Generic type on which the predicate functions.
 */
public class OrPredicate<T> implements Predicate<T> {

  Predicate predicate1;
  Predicate predicate2;
  
  public OrPredicate(Predicate predicate1, Predicate predicate2){
    if(predicate1 == null || predicate2 == null){
      throw new NullPointerException();
    }
    this.predicate1 = predicate1;
    this.predicate2 = predicate2;
  }
  
  @Override
  public boolean accept(T item) {
    return predicate1.accept(item) || predicate2.accept(item); 
  }
}
