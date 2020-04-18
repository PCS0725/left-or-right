/*
 * This class defines individual cells of the grids as nodes in a graph.
 * We maintain additional information in the node to calculate final path, cost, etc.
 */

public class Node {

  private int x;
  // variables used for x
  private int y;
  private double f, g, h;   //cost calculation variables for A*
  private Node parent;    // the node through which we came to current node in the final path


  public Node(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Setter Functions to set the cost calculations, parents of the node, and
   * more.
   */

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public void setXY(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setF(double f) {
    this.f = f;
  }

  public void setG(double g) {
    this.g = g;
  }

  public void setH(double h) {
    this.h = h;
  }

  /**
   * Various getter functions to get the positions of the node, get the
   * heuristic/cost calculations, and the parent of this node.
   */

  public Node getParent() {
    return parent;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public double getG() {
    return g;
  }

  public double getF() {
    return f;
  }

  public double getH() {
    return h;
  }

  /**
   * Extra methods that are used to compare Nodes and print the location
   * of the nodes.
   */

  @Override
  public String toString() {
    return "F Cost: " + f + " G Cost: " + g + " H Cost: " + h + "\n";
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) {
      return false;
    }

    Node tmp = (Node) obj;
    if(this.x == tmp.getX() && this.y == tmp.getY()) {
      return true;
    }
    return false;
  }
}
