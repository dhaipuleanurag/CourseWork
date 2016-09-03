package edu.nyu.cs.pqs.ps4.predicate;

/***
 * Class that supports predicate operation by ANDing two passed predicate as parameter.
 * @author Anurag
 *
 * @param <T> Generic type on which the predicate functions.
 */
public class AndPredicate<T> implements Predicate<T> {

  private Predicate predicate1;
  private Predicate predicate2;
  
  public AndPredicate(Predicate predicate1, Predicate predicate2){
    if(predicate1 == null || predicate2 == null){
      throw new NullPointerException();
    }
    this.predicate1 = predicate1;
    this.predicate2 = predicate2;
  }
  
  @Override
  public boolean accept(T item) {
    return predicate1.accept(item) && predicate2.accept(item); 
  }
}
