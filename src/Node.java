/*
 * The Node Class is used to create the indivdual nodes that is to be searched
 */

public class Node {

  private int x;
  // varaibles used for x
  private int y;
  private double f, g, h;
  private Node parent;

  /**
   * The node constructor that creates the initial positions of the nodes.
   */
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
   * Extra methods that is used to compare Nodes and print the location
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
