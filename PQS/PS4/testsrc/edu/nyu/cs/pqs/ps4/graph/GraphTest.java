package edu.nyu.cs.pqs.ps4.graph;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import edu.nyu.cs.pqs.ps4.graph.Graph.Node;

public class GraphTest {

  private Graph<Integer> graph; 
  
  @Before
  public void setUp() {
   graph = new Graph<Integer>();
  }
  
  @Test
  public void addSingleNodeTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    assertEquals(1, graph.getNodeCount());
  }
  
  @Test
  public void addMultipleNodeTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    graph.addNode(node1);
    graph.addNode(node2);
    assertEquals(2, graph.getNodeCount());
  }
  
  @Test(expected = NullPointerException.class)
  public void addNullNodeTest() {
    graph.addNode(null);
  }
  
  @Test(expected = NullPointerException.class)
  public void addExistingNullNodeTest() {
    graph.addNode(null, null);
  }
  
  @Test
  public void addNodesWithSingleEdge() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    graph.addNode(node1);
    graph.addNode(node1, node2);
    assertEquals(1, graph.getEdgeCount());
    assertEquals(2, graph.getNodeCount());
    assertEquals(1, node1.getadjacentNodesCount());
    assertEquals(1, node2.getadjacentNodesCount());
  }
  
  @Test
  public void addMultipleNodesWithMultipleEdges() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node1, node3);
    graph.addNode(node3, node4);
    graph.addNode(node3, node5);
    assertEquals(5, graph.getNodeCount());
    assertEquals(4, graph.getEdgeCount());
    assertEquals(2, node1.getadjacentNodesCount());
    assertEquals(3, node3.getadjacentNodesCount());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addDoesntExistNodeTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    
    graph.addNode(node1);
    graph.addNode(node2, node3);
  }
  
  @Test
  public void addExistingNodeTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    graph.addNode(node1);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void addExistingNodeWithEdgeTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    graph.addNode(node1, node1);
  }
  
  @Test
  public void addEdgeTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    graph.addNode(node1);
    graph.addNode(node2);
    assertEquals(0, graph.getEdgeCount());
    assertEquals(0, node1.getadjacentNodesCount());
    assertEquals(0, node2.getadjacentNodesCount());
    graph.addEdge(node1, node2);
    assertEquals(1, graph.getEdgeCount());
    assertEquals(1, node1.getadjacentNodesCount());
    assertEquals(1, node2.getadjacentNodesCount());
  }
  
  @Test(expected = NullPointerException.class)
  public void addNullEdge() {
    graph.addEdge(null, null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void removeNodeThatDoesntExistTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    graph.addNode(node1);
    graph.removeNode(node2);
  }
  
  @Test
  public void removeNodesTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    graph.addNode(node1);
    graph.addNode(node1, node2);
    assertEquals(1, graph.getEdgeCount());
    assertEquals(2, graph.getNodeCount());
    assertEquals(1, node1.getadjacentNodesCount());
    assertEquals(1, node2.getadjacentNodesCount());
    graph.removeNode(node2);
    assertEquals(0, graph.getEdgeCount());
    assertEquals(1, graph.getNodeCount());
    assertEquals(0, node1.getadjacentNodesCount());
  }
  
  @Test
  public void removeEdgeInGraphWithMultipleNodesAndEdges() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node1, node3);
    graph.addNode(node3, node4);
    graph.addNode(node3, node5);
    assertEquals(5, graph.getNodeCount());
    assertEquals(4, graph.getEdgeCount());
    assertEquals(2, node1.getadjacentNodesCount());
    assertEquals(3, node3.getadjacentNodesCount());
    graph.removeNode(node3);
    assertEquals(4, graph.getNodeCount());
    assertEquals(1, graph.getEdgeCount());
    assertEquals(1, node1.getadjacentNodesCount());
  }
  
  @Test
  public void singleNodeDFSIteratorTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    int nextValue = iterator.next();
    assertEquals(1, nextValue);
  }
  
  @Test
  public void DFSIteratorOnMultipleNodesAndEdgesTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node1, node3);
    graph.addNode(node3, node4);
    graph.addNode(node3, node5);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    int[] expectedValues= {5, 4, 3, 2, 1};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test
  public void BFSIteratorOnMultipleNodesAndEdgesTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node1, node3);
    graph.addNode(node3, node4);
    graph.addNode(node3, node5);
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    int[] expectedValues= {1, 2, 3, 4, 5};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test
  public void DFSIteratorTestOnGraphWithOneNodeChild() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    graph.addNode(node4, node5);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    int[] expectedValues= {5, 4, 3, 2, 1};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test
  public void BFSIteratorTestOnGraphWithOneNodeChild() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    graph.addNode(node4, node5);
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    int[] expectedValues= {1, 2, 3, 4, 5};
    int i = 0;
    while(iterator.hasNext()) {
      int nextValue = iterator.next();
      assertEquals(expectedValues[i], nextValue);
      i++;
    }
  }
  
  @Test
  public void DFSIteartorTestForHasNextFail() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    assertTrue(iterator.hasNext());
    int nextValue = iterator.next();
    assertFalse(iterator.hasNext());
  }
  
  @Test
  public void BFSIteartorTestForHasNextFail() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    assertTrue(iterator.hasNext());
    int nextValue = iterator.next();
    assertFalse(iterator.hasNext());
  }
  
  @Test(expected = NoSuchElementException.class)
  public void DFSIteratorTestForNextWhen_hasNextReturnsFalse() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    assertTrue(iterator.hasNext());
    int nextValue = iterator.next();
    iterator.next();
  }
  
  @Test(expected = NoSuchElementException.class)
  public void BFSIteratorTestForNextWhen_hasNextReturnsFalse() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    graph.addNode(node1);
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    assertTrue(iterator.hasNext());
    int nextValue = iterator.next();
    iterator.next();
  }
  
  @Test
  public void multipleDFSIteratorTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    graph.addNode(node4, node5);
    Iterator<Integer> iterator1 = graph.DFSIterator(node1);
    Iterator<Integer> iterator2 = graph.DFSIterator(node1);
    int[] expectedValues= {5, 4, 3, 2, 1};
    int i = 0;
    iterator1.next();
    int value = iterator1.next();
    assertEquals(value, 4);
    iterator1.next();
    value = iterator2.next();
    assertEquals(value, 5);
    iterator1.next();
    value = iterator1.next();
    assertEquals(value, 1);
  }
  
  @Test
  public void multipleBFSIteratorTest() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    graph.addNode(node4, node5);
    Iterator<Integer> iterator1 = graph.BFSIterator(node1);
    Iterator<Integer> iterator2 = graph.BFSIterator(node1);
    int[] expectedValues= {1, 2, 3, 4, 5};
    int i = 0;
    iterator1.next();
    int value = iterator1.next();
    assertEquals(value, 2);
    iterator1.next();
    value = iterator2.next();
    assertEquals(value, 1);
    iterator1.next();
    value = iterator1.next();
    assertEquals(value, 5);
  }
  
  @Test(expected = IllegalStateException.class)
  public void invalidStateTestForBFSIterator_hasNext() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    iterator.hasNext();
    graph.addNode(node4, node5);
    iterator.hasNext();
  }
  
  @Test(expected = IllegalStateException.class)
  public void invalidStateTestForDFSIterator_hasNext() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    iterator.hasNext();
    graph.addNode(node4, node5);
    iterator.hasNext();
  }
  
  @Test(expected = IllegalStateException.class)
  public void invalidStateTestForDFSIterator_next() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    Iterator<Integer> iterator = graph.DFSIterator(node1);
    iterator.next();
    graph.addNode(node4, node5);
    iterator.next();
  }
  
  @Test(expected = IllegalStateException.class)
  public void invalidStateTestForBFSIterator_next() {
    Graph<Integer>.Node node1 = graph.new Node(1);
    Graph<Integer>.Node node2 = graph.new Node(2);
    Graph<Integer>.Node node3 = graph.new Node(3);
    Graph<Integer>.Node node4 = graph.new Node(4);
    Graph<Integer>.Node node5 = graph.new Node(5);
    graph.addNode(node1);
    graph.addNode(node1, node2, "edgeWeight1");
    graph.addNode(node2, node3);
    graph.addNode(node3, node4);
    Iterator<Integer> iterator = graph.BFSIterator(node1);
    iterator.next();
    graph.addNode(node4, node5);
    iterator.next();
  }
}