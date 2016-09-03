package edu.nyu.cs.pqs.ps4.filteriterator;

import org.junit.Before;
import org.junit.Test;

import edu.nyu.cs.pqs.ps4.graph.Graph;
import edu.nyu.cs.pqs.ps4.graph.Graph.Node;
import edu.nyu.cs.pqs.ps4.predicate.AndPredicate;
import edu.nyu.cs.pqs.ps4.predicate.MutiplesOfNumberPredicate;
import edu.nyu.cs.pqs.ps4.predicate.NotPredicate;
import edu.nyu.cs.pqs.ps4.predicate.OrPredicate;
import edu.nyu.cs.pqs.ps4.predicate.Predicate;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterIteratorTest {
  Predicate<Integer> includeMultipleOf2;
  Predicate<Integer> includeMultipleOf3;
  Predicate<Integer> includeMultipleOf5;
  Graph<Integer> graph;
  Graph<Integer>.Node node1;

  @Before
  public void setUp() {
    includeMultipleOf2 = new MutiplesOfNumberPredicate(2);
    includeMultipleOf3 = new MutiplesOfNumberPredicate(3);
    includeMultipleOf5 = new MutiplesOfNumberPredicate(5);
    graph = new Graph<Integer>();
    node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    Graph<Integer>.Node node6 = graph.new Node(6);
    Graph<Integer>.Node node7 = graph.new Node(7);
    
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node1, node3);
    graph.addNode(node2, node4);
    graph.addNode(node2, node5);
    graph.addNode(node3, node6);
    graph.addNode(node3, node7);
  }

  @Test
  public void filterBFSIteratorWithMultiple2PredicateTest() {
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    iterator = new FilterIterator<Integer>(iterator, includeMultipleOf2);
    int[] expectedValues= {2, 4, 6};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }

  @Test
  public void filterBFSIteratorWithMultiple3PredicateTest() {
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    iterator = new FilterIterator<Integer>(iterator, includeMultipleOf3);
    int[] expectedValues= {3, 6};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test(expected = NoSuchElementException.class)
  public void filterBFSIteratorWithMultiple2Predicate_ExceptionTest() {
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    iterator = new FilterIterator<Integer>(iterator, includeMultipleOf2);
    int[] expectedValues= {2, 4, 6};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
    iterator.next();
  }
  
  @Test 
  public void filterIteratorWithAndPredicate() {
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    
    Predicate includeMultipleOf6 = new AndPredicate<Integer>(includeMultipleOf2, includeMultipleOf3);
    iterator = new FilterIterator<Integer>(iterator, includeMultipleOf6);
    
    int[] expectedValues= {6};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test(expected = NullPointerException.class) 
  public void nullTestForAndPredicate() {
    Predicate andPredicate = new AndPredicate<Integer>(null, includeMultipleOf3);
  }
  
  @Test 
  public void filterIteratorWithOrPredicate() {
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    
    Predicate includeMultipleOfBoth2And3 = new OrPredicate<Integer>(includeMultipleOf2, includeMultipleOf3);
    iterator = new FilterIterator<Integer>(iterator, includeMultipleOfBoth2And3);
    
    int[] expectedValues= {2, 3, 4, 6};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test(expected = NullPointerException.class) 
  public void nullTestForOrPredicate() {
    Predicate predicate = new OrPredicate<Integer>(null, includeMultipleOf3);
  }
  
  @Test 
  public void filterIteratorWithNotPredicate() {
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    
    Predicate doNotIncludeMultipleOf2 = new NotPredicate<Integer>(includeMultipleOf2);
    iterator = new FilterIterator<Integer>(iterator, doNotIncludeMultipleOf2);
    
    int[] expectedValues= {1, 3, 5, 7};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test(expected = NullPointerException.class) 
  public void nullTestForNotPredicate() {
    Predicate<Integer> predicate = new NotPredicate<Integer>(null);
  }

}