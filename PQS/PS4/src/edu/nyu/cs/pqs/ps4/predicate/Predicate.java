package edu.nyu.cs.pqs.ps4.predicate;

/***
 * A predicate that has just one method to be overridden that returns true or false based on whether the passed generic 
 * type to the function passes some condtion. 
 * @author Anurag
 *
 * @param <T> Generic paramter type.
 */
public interface Predicate<T> {
    boolean accept(T item);
}
