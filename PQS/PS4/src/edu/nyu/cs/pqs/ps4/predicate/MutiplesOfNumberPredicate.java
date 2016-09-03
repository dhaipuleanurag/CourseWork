package edu.nyu.cs.pqs.ps4.predicate;

/***
 * Class that supports the condition of passing only multiples of a given number that is passed as argument
 * in the constructor.
 * @author Anurag
 *
 */
public class MutiplesOfNumberPredicate implements Predicate<Integer> {

  private int value;
  public MutiplesOfNumberPredicate(int value) {
    this.value = value;
  }
  
  @Override
  public boolean accept(Integer item) {
    return item % value == 0;  
  }
}
