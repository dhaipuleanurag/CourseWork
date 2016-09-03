package edu.nyu.cs.pqs.ps4.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

/***
 * Graph class stores all data related to an undirected graph. It also provides methods for adding, removing nodes 
 * and edges.
 * Two types of iterators that perform traversals are provided, Breadth for Search and Depth for Search iterator.
 * Note that the graph is made of Node and Edge, which are classes within this class. 
 * The graph is a generic type and hence can store any type as member within a node. 
 * @author Anurag
 *
 * @param <T> Generic type member.
 */
public class Graph<T> {

  private List<Node> nodes = new ArrayList<Node>();
  private List<Edge> edges = new ArrayList<Edge>();
  private List<Iterator> BFSIterators = new ArrayList<Iterator>();
  private List<Iterator> DFSIterators = new ArrayList<Iterator>();
  private Node root;
  
  /***
   * Method to get DFSIterator that traverses the graph in depth for search approach. If a graph is unconnected then
   * only the part of the graph that is connected to the 'startNode' is traversed.
   * @param startNode root node for traversal.
   * @return
   */
  public Iterator<T> DFSIterator(Node startNode){
    Iterator iterator = new DepthForSearchIterator(startNode);
    DFSIterators.add(iterator);
    return iterator;
  }
  
  /***
   * Method to get BFSIterator that traverses the graph in breadth for search approach. If a graph is unconnected then
   * only the part of the graph that is connected to the 'startNode' is traversed.
   * @param startNode root node for traversal.
   * @return
   */
  public Iterator<T> BFSIterator(Node startNode){
    Iterator iterator = new BreadthForSearchIterator(startNode);
    BFSIterators.add(iterator);
    return iterator;
  }
  
  private boolean checkIfNodeExists(Node node){
    if(node == null){
      throw new NullPointerException();
    }
    for(Node currentNode: nodes){
      if(currentNode.equals(node)){
        return true;
      }
    }
    return false;
  }
  
  private void makeAllIteratorsInvalid() {
    for(Iterator iterator: BFSIterators) {
      BreadthForSearchIterator BFSIterator = (BreadthForSearchIterator)iterator;
      BFSIterator.makeThisIteartorInvalid();
    }
    for(Iterator iterator: DFSIterators) {
      DepthForSearchIterator DFSIterator = (DepthForSearchIterator)iterator;
      DFSIterator.makeThisIteartorInvalid();
    }
  }
  
  /***
   * Methods to get count of total nodes in graph.
   * @return
   */
  public int getNodeCount() {
    return nodes.size();
  }
  
  /***
   * Method to get count of total edges in graph.
   * @return
   */
  public int getEdgeCount() {
    return edges.size();
  }
  
  /***
   * Adds edges to a graph between the two nodes that are provided as arguments.
   * @param existingNode1 This node should be part of graph. If not exception is thrown.
   * @param existingNode2 This node should be part of graph. If not exception is thrown.
   */
  public void addEdge(Node existingNode1, Node existingNode2){
    addEdge(existingNode1, existingNode2, "");
  }
  
  /***
   * Adds edges to a graph between the two nodes that are provided as arguments.
   * @param existingNode1 This node should be part of graph. If not exception is thrown.
   * @param existingNode2 This node should be part of graph. If not exception is thrown.
   * @param edgeWeight This gives a name/weight/label to the edge that is added in the graph.
   */
  public void addEdge(Node existingNode1, Node existingNode2, String edgeWeight){
    if(existingNode1 == null || existingNode2 == null || edgeWeight == null){
      throw new NullPointerException();
    }
    existingNode1.addToAdjacencyList(existingNode2);
    existingNode2.addToAdjacencyList(existingNode1);
    edges.add(new Edge(existingNode1, existingNode2, edgeWeight));
    makeAllIteratorsInvalid();
  }
  
  /***
   * Adds a node to the graph. This method also sets the root node of the graph.
   * @param node Node that is considered the first node in graph.
   */
  public void addNode(Node node){
    if(node == null){
      throw new NullPointerException();
    }
    if(root == null){
      root = node;
    }
    nodes.add(node);
    makeAllIteratorsInvalid();
  }
  
  /***
   * Adds a node to the graph by connecting the new node to the existing node in the graph and hence an edge is added.
   * @param existingNode This node should be part of graph. If not exception is thrown.
   * @param node New node that needs to be added to graph.
   */
  public void addNode(Node existingNode, Node node){
    addNode(existingNode, node, "");
  }
  
  
  /***
   * Adds a node to the graph by connecting the new node to the existing node in the graph and hence an edge is added.
   * @param existingNode This node should be part of graph. If not exception is thrown.
   * @param node New node that needs to be added to graph.
   * @param edgeWeight This gives a name/weight/label to the edge that is added in the graph.
   */
  public void addNode(Node existingNode, Node node, String edgeWeight){
    if(existingNode == null || node == null || edgeWeight == null){
      throw new NullPointerException();
    }
    
    if(checkIfNodeExists(node)){
      throw new IllegalArgumentException("The given node exists in the graph.");
    }
    
    if(!checkIfNodeExists(existingNode)){
      throw new IllegalArgumentException("The given node doesn't exist in the graph.");
    }    
    
    nodes.add(node);
    edges.add(new Edge(existingNode, node, edgeWeight));
    
    existingNode.addToAdjacencyList(node);
    node.addToAdjacencyList(existingNode);
    makeAllIteratorsInvalid();
  }
  
  /***
   * Removes node from the graph. Exception is thrown if a node that is not presented in graph is being deleted.
   * @param node Node to be deleted.
   */
  public void removeNode(Node node){
    if(!checkIfNodeExists(node)){
      throw new IllegalArgumentException("The given node exists in the graph.");
    }
    for(int i = 0; i < nodes.size(); i++){
      nodes.get(i).removeFromAdjacencyList(node);
    }
    nodes.remove(node);
    for(int i = edges.size()-1; i >= 0; i--){
      if(edges.get(i).connects(node)){
        edges.remove(i);
      }
    }
    makeAllIteratorsInvalid();
  }
  
  
  
  @Override
  public String toString() {
    return "Graph [nodes=" + nodes + ", edges=" + edges + "]";
  }

  /***
   * Class to store data related to node and all necessary operations related to node are supported.
   * @author Anurag
   *
   */
  public class Node{
    private final T element;
    private List<Node> adjacencyList;
    
    
    public Node(T element) {
      this.element = element;
      this.adjacencyList = new ArrayList<Node>();
    }
    
    /***
     * Returns the value part of the node.
     * @return
     */
    public T getElement() {
      return element;
    }
    
    /***
     * Adds a node to the list of its adjacent nodes.
     * @param node Node to be added.
     * @return success of the opeartion.
     */
    public boolean addToAdjacencyList(Node node) {
      return adjacencyList.add(node);
    }
    
    /***
     * Removes a node from the list of its adjacent nodes.
     * @param node Node to be removed.
     * @return
     */
    public boolean removeFromAdjacencyList(Node node) {
      return adjacencyList.remove(node);
    }
    
    /***
     * Returns the adjacency list of a node. List of all nodes adjacent to current node.
     * @return
     */
    public List<Node> getAdjacencyList() {
      return adjacencyList;
    }
    
    /***
     * Get count of adjacent nodes.
     * @return
     */
    public int getadjacentNodesCount() {
      return adjacencyList.size();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((element == null) ? 0 : element.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Node other = (Node) obj;
      if (element == null) {
        if (other.element != null)
          return false;
      } else if (!element.equals(other.element))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "Node [element=" + element + ", adjacencyList=" + adjacencyList + "]";
    }
  }

  /***
   * 
   * Class to store all data related to Edge.  All necessary operations related to a edge are supported.
   * @author Anurag
   * 
   * @param <T>
   */
  public class Edge<T>{
    private final Node node1;
    private final Node node2;
    private final String weight;
    
    public Edge(Node node1, Node node2, String weight){
      this.node1 = node1;
      this.node2 = node2;
      this.weight = weight;
    }
    
    /***
     * Methods that returns true or false based on whether a given node connects to the current edge.
     * @param node
     * @return
     */
    private boolean connects(Node node){
      if(node == null){
        throw new NullPointerException();
      }
      if(node1.equals(node) || node2.equals(node)){
        return true;
      }
      return false;
    }

    @Override
    public String toString() {
      return "Edge [node1=" + node1 + ", node2=" + node2 + ", weight=" + weight + "]";
    }
    
  }
  
  private class DepthForSearchIterator implements Iterator<T>{
    private HashMap<T, Integer> visitedMap;  
    private Stack<Node> stack;
    private boolean invalidIterator = false;
    Node parentNode = null;
    
    /***
     * Makes this iterator invalid which means all operations on this iterator will throw exception after 
     * making it invalid.
     */
    public void makeThisIteartorInvalid() {
      invalidIterator = true;
    }
    
    private DepthForSearchIterator(Node root){
      visitedMap = new HashMap<T, Integer>();
      stack = new Stack<Node>();
      stack.push(root);
      visitedMap.put((T) root.getElement(), 1);
      Node topOfStackNode = stack.peek();
      
      // This loops keeps adding nodes to stack until leaf is obtained.
      while(topOfStackNode.getadjacentNodesCount() != 0){
        // This is to ensure we stop when leaves are reached
        List<Node> adjacencyList =  topOfStackNode.getAdjacencyList();
        if(topOfStackNode.getadjacentNodesCount() == 1 && adjacencyList.get(0).equals(parentNode)) {
          break;
        }
        
        for(int i = 0; i < adjacencyList.size(); i++){
          Node nodeToBePushed = adjacencyList.get(i);
          if(!nodeToBePushed.equals(topOfStackNode) && visitedMap.get(nodeToBePushed.getElement()) == null){
            stack.push(nodeToBePushed);
            visitedMap.put((T) nodeToBePushed.getElement(), 1);
          }
        }
        parentNode = topOfStackNode;
        topOfStackNode = stack.peek();
      }
    }
    
    @Override
    public boolean hasNext() {
      if(invalidIterator) {
        throw new IllegalStateException();
      }
      return !stack.isEmpty();
    }

    @Override
    public T next() {
      if(invalidIterator) {
        throw new IllegalStateException();
      }
      if(stack.isEmpty()) {
        throw new NoSuchElementException();
      }
      Node topOfStackNode = stack.peek();
      while(topOfStackNode.getadjacentNodesCount() != 0){
        // This is to ensure we stop when leaves are reached
        List<Node> adjacencyList =  topOfStackNode.getAdjacencyList();
        if(topOfStackNode.getadjacentNodesCount() == 1 && adjacencyList.get(0).equals(parentNode)) {
          break;
        }
        for(int i = 0; i < adjacencyList.size(); i++){
          Node nodeToBePushed = adjacencyList.get(i);
          // Equal to null implies not visited.
          if(visitedMap.get(nodeToBePushed.getElement()) == null){
            stack.push(nodeToBePushed);
            visitedMap.put((T) nodeToBePushed.getElement(), 1);
          }
        }
        if(!parentNode.equals(topOfStackNode)) {
        parentNode = topOfStackNode;
        topOfStackNode = stack.peek();
        } else {
          break;
        }
      }
      Node nextElement = stack.pop();
      return (T) nextElement.getElement();
    }
  }

  private class BreadthForSearchIterator implements Iterator<T>{
    private HashMap<T, Integer> visitedMap;  
    private Queue<Node> queue;
    private boolean invalidIterator = false;
    
    private BreadthForSearchIterator(Node root){
      visitedMap = new HashMap<T, Integer>();
      queue = new LinkedList<Node>();
      queue.add(root);
      visitedMap.put((T) root.getElement(), 1);
    }
    

    /***
     * Makes this iterator invalid which means all operations on this iterator will throw exception after making it
     * invalid.
     */
    public void makeThisIteartorInvalid() {
      invalidIterator = true;
    }
    
    @Override
    public boolean hasNext() {
      if(invalidIterator) {
        throw new IllegalStateException();
      }
      return !queue.isEmpty();
    }

    @Override
    public T next() {
      if(invalidIterator) {
        throw new IllegalStateException();
      }
      if(queue.isEmpty()) {
        throw new NoSuchElementException();
      }
      Node nextNode = queue.poll();
      List<Node> adjacencyList = nextNode.getAdjacencyList();
      for(int i = 0; i < adjacencyList.size(); i++){
        Node nodeToBePushed = adjacencyList.get(i);
        // Equal to null implies not visited.
        if(visitedMap.get(nodeToBePushed.getElement()) == null){
          queue.add(nodeToBePushed);
          visitedMap.put((T) nodeToBePushed.getElement(), 1);
        }
      }
      return (T) nextNode.getElement();
    }
  }
}
