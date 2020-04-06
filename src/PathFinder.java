/*
 * The PathFinder classes contain implementations of the PathFinding
 * algorithm and frequently communicates with the controller class.
 * Algorithm and read up on A* from: 
 * http://theory.stanford.edu/~amitp/GameProgramming/Heuristics.html#S7
 */

import java.util.*;
import java.awt.Point;

public class PathFinder {

  // variables used for Astar

  private static final int NODE_SIZE = 25;
  private static final int DIAGONAL_MOVE = (int) (Math.sqrt(1250));

  private Controller control;
  private Node start, end;
  private boolean deleteWalls, complete, isPause, run, isDijkstra;

  //data structures for A* pathfinding
  private PriorityQueue<Node> open;

  //list closed poitns and of all walls on the grid
  private Set<Point> closed, wall;

  // final path leading to the list 
  private ArrayList<Node> finalPath;

  /**
   * inner class used for Dijkstra's Algorithm
   */
  class DFSFinder {

    //list for finalPath creation and keeping track of walls
    private ArrayList<Node> finalPath;

    //data structures for DFS search
    private Stack<Node> open;

    private Node start, end;

    private Set<Point> wall, closed;

    private Controller control;

    public DFSFinder(Controller control) {
      this.control = control;

      finalPath = new ArrayList<Node>();
      wall = new HashSet<Point>();
      open = new Stack<Node>();
      closed = new HashSet<Point>();
    }
  }

  /**
   * inner class used for comaparing Nodes
   */
    
  class NodeComparator implements Comparator<Node> {
    public int compare(Node xCoord, Node yCoord) {
      if(xCoord.getF() > yCoord.getF()) {
        return 1;

      }else if(xCoord.getF() < yCoord.getF()) {
        return -1;

      }else{

        if(xCoord.getG() > yCoord.getG()) {
          return 1;

        }else if(xCoord.getG() < yCoord.getG()) {
          return -1;

        }

      }
      return 0;

    }
  }

  public PathFinder(Controller control) {
    this.control = control;

    run = false;
    isPause = true;

    finalPath = new ArrayList<Node>();
    wall = new HashSet<Point>();
    open = new PriorityQueue<Node>(new NodeComparator());
    closed = new HashSet<Point>();
  }

  /*
   * Checks to see if the list of walls contains a certain node.
   */
  public boolean isWall(Point point) {
    return wall.contains(point);
  }

  /*
   * Contains method to see if nodes are in the closed list.
   */
  public boolean closedContains(Point point) {
    return closed.contains(point);
  }

  public boolean closedRemove(Point point) {
    return closed.remove(point);
  }

  /*
   * Contains method to see if nodes are in the open list.
   */
  public boolean openContains(Node n) {
    return open.contains(n);
  }

  public boolean openRemove(Node n) {
    return open.remove(n);
  }

  public Node openFind(Node n) {
    for(Node x : open) {
      if(x.equals(n)) {
        return x;
      }
    }

    return null;
  }

  /*
   * Adds a wall to the wall list if a wall at the same location is not
   * already present.
   */
  public boolean addWall(Point point) {
    return wall.add(point);
  }

  /*
   * Removes a wall node from the list of walls.
   */
  public boolean removeWall(Point point) {
    return wall.remove(point);
  }

  public void deleteWalls(boolean check) {
    deleteWalls = check;
  }

  public void setisDijkstra(boolean check) {
    isDijkstra = check;
  }

  public void reset() {
    run = false;
    isPause = true;
    complete = false;

    if(deleteWalls) {
      wall.clear();
      deleteWalls = false;
    }

    closed.clear();
    open.clear();
    finalPath.clear();
  }

  /*
   * Various getter methods to get the various lists containing the nodes.
   */
  public Set<Point> getWall() {
    return wall;
  }

  public PriorityQueue<Node> getOpen() {
    return open;
  }

  public Set<Point> getClosed() {
    return closed;
  }

  public ArrayList<Node> getFinal() {
    return finalPath;
  }

  /*
   * Various setter methods to set the various lists containing the nodes.
   */
  public void setStart(Node start) {
    this.start = new Node(start.getX(), start.getY());
    open.add(this.start);
  }

  public void setEnd(Node end) {
    this.end = new Node(end.getX(), end.getY());
  }

  public void setisPause(boolean isPause) {
    this.isPause = isPause;
  }

  public void setisRun(boolean run) {
    this.run = run;
  }
  
  public boolean isRun() {
    return run;
  }

  public boolean isPause() {
    return isPause;
  }

  public boolean isComplete() {
    return complete;
  }

  /*
   * Constructs the final path from start to end node. Only called once a
   * valid path is found.
   */
  public void constructPath() {
    Node current = end;
    while(!(current.getParent().equals(start))) {
      finalPath.add(0, current.getParent());
      current = current.getParent();
    }

    finalPath.add(0, current);
  }

  /*
   * Method finds the cost associated with moving from the current node to
   * the neighbor node. Uses the formula for the distance between two points.
   */
  public double gCostMovement(Node parent, Node neighbor) {
    //distance from point to point in a grid
    int xCoord = neighbor.getX() - parent.getX();
    int yCoord = neighbor.getY() - parent.getY();

    return (int) (Math.sqrt(Math.pow(xCoord, 2) + Math.pow(yCoord, 2)));
  }

  /*
   * Method finds the heuristic cost from the neighbor node to the end node.
   * From the Stanford page: "Here we compute the number of steps you take if 
   * you can’t take a diagonal, then subtract the steps you save by using the 
   * diagonal. There are min(dx, dy) diagonal steps, and each one costs D2 but 
   * saves you 2⨉D non-diagonal steps."
   * 
   * The heuristic used is octile distance where the cost of an orthogonal move
   * is one and the cost of a diagonal is sqrt(2).
   */
  public double hCostMovement(Node neighbor) {
    int hXCost = Math.abs(end.getX() - neighbor.getX());
    int hYCost = Math.abs(end.getY() - neighbor.getY());
    double hCost = hXCost + hYCost;

    if(control.isOctile()) {
      if(hXCost > hYCost) {
        hCost = ((hXCost - hYCost) + Math.sqrt(2) * hYCost);
      } else {
        hCost = ((hYCost - hXCost) + Math.sqrt(2) * hXCost);
      }
    }

    return hCost;
  }

  /*
   * A* pathfinding algorithm. Tries to explore the fewest number of nodes to
   * reach the end node. Self corrects the path to the end node using the
   * heuristic cost function h.
   */
  public void aStarPath() {
    //get node with lowest F cost off PQ
    Node current = open.poll();

    //if no min node, then no path
    if(current == null) {
      System.out.println("No path");
      run = false;
      isPause = true;
      return;
    }

    //if min node is the end, then stop algorithm and build final path
    if(!isDijkstra && current.equals(end)) {
      end.setParent(current.getParent());
      run = false;
      isPause = true;
      complete = true;
      control.repaint();
      constructPath();
      System.out.println("Total Cost of Path: " + end.getParent().getG());
      System.out.println("Size of Open: " + open.size());
      System.out.println("Size of Closed: " + closed.size());
      System.out.println("Size of Path: " + finalPath.size() + "\n");
      return;
    }

    closed.add(new Point(current.getX(), current.getY()));

    //calculate costs for the 8 possible adjacent nodes to current
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {

        //skip the current node we are exploring
        if(i == 1 && j == 1) {
          continue;
        }
        
        int xCoord = (current.getX() - NODE_SIZE) + (NODE_SIZE * i);
        int yCoord = (current.getY() - NODE_SIZE) + (NODE_SIZE * j);
        Node neighbor = new Node(xCoord, yCoord);

        //for Dijkstra, once we encounter end node we have the shortest path
        if(isDijkstra && neighbor.equals(end)) {
          end.setParent(current);
          run = false;
          isPause = true;
          complete = true;
          control.repaint();
          constructPath();
          System.out.println("Total Cost of Path: " + end.getParent().getG());
          System.out.println("Size of Open: " + open.size());
          System.out.println("Size of Closed: " + closed.size());
          System.out.println("Size of Path: " + finalPath.size() + "\n");
          return;
        }

        //checks if node is within canvas boundary
        if(xCoord < 0 || yCoord < 0 || xCoord >= control.getWidth() || yCoord >=
          control.getHeight()) {
          continue;
        }

        //checks to see if the neighbor node is a wall, in the open/closed list
        if(isWall(new Point(neighbor.getX(), neighbor.getY()))) {
          continue;
        }

        int wallJumpX = current.getX() + (xCoord - current.getX());
        int wallJumpY = current.getY() + (yCoord - current.getY());

        //checks for border in adjacent pos, does not allow for a diagonal
        //jump across a border
        if(isWall(new Point(wallJumpX, current.getY())) || isWall(new
              Point(current.getX(), wallJumpY)) && ((j == 0 | j == 2) && i != 1)) {
          continue;
        }

        //calculate f, g, and h costs for this node
        double gCost = current.getG() + gCostMovement(current, neighbor);
        double hCost = hCostMovement(neighbor);
        double fCost = gCost + hCost;

        boolean inOpen = openContains(neighbor);
        boolean inClosed = closedContains(new Point(neighbor.getX(),
                            neighbor.getY()));
        Node found = openFind(neighbor);

        //if inOpen and inClosed cases just in case, should not happen
        //if node in open and we found lower gCost, no need to search neighbor
        if(inOpen && (gCost < found.getG())) {
          openRemove(found);
          neighbor.setG(gCost);
          neighbor.setF(gCost + found.getH());
          neighbor.setParent(current);
          open.add(neighbor);
          continue;
        }

        //if neighbor in closed and found lower gCost, visit again
        if(inClosed && (gCost < neighbor.getG())) {
          System.out.println("HEYCLOSED");
          continue;
        }

        //if neighbor not visited, then add to open list
        if(!inOpen && !inClosed) {

          if(isDijkstra) {
            neighbor.setG(gCost);
            neighbor.setF(gCost);
          } else {
            neighbor.setG(gCost);
            neighbor.setH(hCost);
            neighbor.setF(fCost);
          }
          
          neighbor.setParent(current);

          open.add(neighbor);
        }
      }
    }

  }

}
