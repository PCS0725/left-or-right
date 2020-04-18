/*
 * The GUI and driver program. 
 * It lays down the grid, handles mouse and keyboard events and runs the algorithm.
 * No state information is saved in this class, all the above data is saved in the object of pathfinder class.
 * This class sets the data fields and renders the values by fetching them whenever needed.
 */
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Controller extends JPanel implements ActionListener,
                              MouseListener, KeyListener, MouseMotionListener {
  //PathFinder class will store all the information like wall nodes, start node, final path, etc
  private PathFinder path;
  // added a Jpanel frame
  private JPanel pane;
  private JFrame frame;

  private Timer timer;

  private Node start;
  private Node end;

  private char keyPress;
  private boolean isOctile;
  private boolean isManhattan;

  private static final int WIDTH = 750;
  private static final int HEIGHT = 750;
  private static final int NODE_SIZE = 25;

  public Controller() {   

    isOctile = true;
    isManhattan = false;

    setLayout(null);           //No, we could not have worked with gridlayout :(
    pane = new JPanel();
    setFocusable(true);

    addMouseListener(this);
    addKeyListener(this);
    addMouseMotionListener(this);

    timer = new Timer(100, this);

    //set up frame
    frame = new JFrame();    
    frame.setContentPane(this);
    frame.setTitle("Left or Right");
    frame.getContentPane().setPreferredSize(new Dimension(WIDTH, HEIGHT));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);                      //This finally renders the frame.

    path = new PathFinder(this);

    //show changes to frame 
    this.revalidate();
    this.repaint();                             //anything on frame changes, call the paint() method again.

    timer.start();
  }
  
  @Override
  public void paintComponents(Graphics g) {
    super.paintComponent(g);
  }

  /*
	We need to override the paint method to create our own graphics
  */
  @Override
  public void paint(Graphics g) {
    //Draw grid on the screen( a matrix with rows = window_height/NODE_SIZE and columns = window_width/NODE_SIZE)
    g.setColor(Color.lightGray);
    for(int j = 0; j < this.getHeight(); j += NODE_SIZE) {          //
      for(int i = 0; i < this.getWidth(); i += NODE_SIZE) {

        g.setColor(new Color(40, 42, 54));
        g.fillRect(i, j, NODE_SIZE, NODE_SIZE);
        g.setColor(Color.black);
        g.drawRect(i, j, NODE_SIZE, NODE_SIZE);
      }
    }

    //draw the wall nodes by fetching from the pathFinder
    Set<Point> wallList = path.getWall();
    g.setColor(new Color(228, 31, 13));
    for(Point pt : wallList) {
      int xCoord = (int) pt.getX();
      int yCoord = (int) pt.getY();

      g.fillRect(xCoord + 1, yCoord + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw closed list
    Set<Point> closedList = path.getClosed();
    g.setColor(new Color(253, 90, 90));
    for(Point pt : closedList) {
      int xCoord = (int) pt.getX();
      int yCoord = (int) pt.getY();

      g.fillRect(xCoord + 1, yCoord + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw open list
    PriorityQueue<Node> openList = path.getOpen();
    g.setColor(new Color(80, 250, 123));
    for(Node e : openList) {
      g.fillRect(e.getX() + 1, e.getY() + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //Fetch the final path( check how we developed the final path in PathFinder class) and draw it
    ArrayList<Node> finalPath = path.getFinal();
    g.setColor(new Color(35, 228, 15));
    for(int i = 0; i < finalPath.size(); i++) {
      g.fillRect(finalPath.get(i).getX() + 1, finalPath.get(i).getY() + 1,
                 NODE_SIZE - 2, NODE_SIZE - 2);
    }

    //draw the start node
    if(start != null) {
      g.setColor(new Color(8, 228, 228));
      g.fillRect(start.getX() + 1, start.getY() + 1, NODE_SIZE - 2, NODE_SIZE -
          2);
    }

    //draw the end node
    if(end != null) {
      g.setColor(new Color(228, 35, 225));
      g.fillRect(end.getX() + 1, end.getY() + 1, NODE_SIZE - 2, NODE_SIZE - 2);
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    //get keyPress to know if we should make start, end, or delete 
    keyPress = e.getKeyChar();

    switch(keyPress){

      case KeyEvent.VK_SPACE:
        //start algorithm or stop it
        //set start and end node for path
        //on first run, set start and end; do again when finish algorithm
        if(!path.isComplete() && !path.isRun() && start != null && end != null){
          path.setisRun(true);
          path.setStart(start);
          path.setEnd(end);

        }

        //press space to pause the algorithm
        if(!path.isPause()) {
          path.setisPause(true);

        }else if(path.isPause()) {
          path.setisPause(false);

        }
        break;

      // backspace to grid delete
      case KeyEvent.VK_BACK_SPACE: 
        if(!path.isRun()) {
          path.deleteWalls(true);
          path.reset();  //this will clear all the lists and properties: closed, open, finalPath, start, end, etc.

          System.out.println("Grid Deletion Complete.\n");
        }
        break;

      case '1':
        if(!path.isRun()) {
          path.setisDijkstra(true);
          System.out.println("Begin Dijkstra.\n");

        }
        break;

      case '2':
        if(!path.isRun()) {
          path.setisDijkstra(false);
          System.out.println("Begin A-Star\n");
        }
        break;
      
      case 'c':
        //command to clear and reset
        path.reset();
        repaint();
        break;

      case 'm':
        if(!path.isRun()) {
          isManhattan = true;
          isOctile = false;

          System.out.println("Use MANHATTAN\n");
        }
        break;

      case 'o':
        if(!path.isRun()) {
          isOctile = true;
          isManhattan = false;

          System.out.println("Use OCTILE\n");
        }

        break;

      default:
    }

  }

  /*
   * Handler for mouse events
   */
  public void gridWork(MouseEvent e) {

      //if mouse click was left click
      if (e.getButton() == MouseEvent.BUTTON1) {

          //mouse clicks not exactly at node point of creation, so find remainder
          int xOver = e.getX() % NODE_SIZE;
          int yOver = e.getY() % NODE_SIZE;

          //s key and left mouse makes start node
          if (keyPress == 's') {

              int xTmp = e.getX() - xOver;
              int yTmp = e.getY() - yOver;

              //if start is null, the create a new start node.
              //check if the position already has a wall or end node presend there
              //Do not create start node is there is a wall at that place
              //override if there was an end node there.
              if (start == null) {

                  if (!path.isWall(new Point(xTmp, yTmp))) {
                      if (end == null) {
                          start = new Node(xTmp, yTmp);
                      } else {
                          if (!end.equals(new Node(xTmp, yTmp))) {
                              start = new Node(xTmp, yTmp);
                          }
                      }
                  }

                  //If there was a start already, change its coordinates.
              } else {

                  if (!path.isWall(new Point(xTmp, yTmp))) {
                      if (end == null) {
                          start.setXY(xTmp, yTmp);
                      } else {
                          if (!end.equals(new Node(xTmp, yTmp))) {
                              start.setXY(xTmp, yTmp);
                          }
                      }
                  }

              }

              repaint();

              //e key and left mouse makes end node
          } else if (keyPress == 'e') {

              int xTmp = e.getX() - xOver;
              int yTmp = e.getY() - yOver;

              //same as start node
              if (end == null) {

                  if (!path.isWall(new Point(xTmp, yTmp))) {
                      if (start == null) {
                          end = new Node(xTmp, yTmp);
                      } else {
                          if (!start.equals(new Node(xTmp, yTmp))) {
                              end = new Node(xTmp, yTmp);
                          }
                      }
                  }

                  //same as for start node
              } else {

                  if (!path.isWall(new Point(xTmp, yTmp))) {
                      if (start == null) {
                          end.setXY(xTmp, yTmp);
                      } else {
                          if (!start.equals(new Node(xTmp, yTmp))) {
                              end.setXY(xTmp, yTmp);
                          }
                      }
                  }

              }

              repaint();

              //d key and left mouse deletes nodes
          } else if (keyPress == 'd') {
              //check if the curr node is start, end or a wall and set it to null
              int nodeX = e.getX() - xOver;
              int nodeY = e.getY() - yOver;

              if (start != null && start.equals(new Node(nodeX, nodeY))) {
                  start = null;
              } else if (end != null && end.equals(new Node(nodeX, nodeY))) {
                  end = null;
              } else {
                  path.removeWall(new Point(nodeX, nodeY));
              }

              repaint();

              //Create walls by simply clicking on the nodes.
          } else {
              //create walls and add to wall list
              Node tmpWall = new Node(e.getX() - xOver, e.getY() - yOver);

              if (start == null && end == null) {
                  path.addWall(new Point(tmpWall.getX(), tmpWall.getY()));
              }

              if (!(tmpWall.equals(start)) && !(tmpWall.equals(end))) {
                  path.addWall(new Point(tmpWall.getX(), tmpWall.getY()));
              }

              repaint();
          }
      }
  }

      @Override
      public void mouseClicked (MouseEvent e){
          //all mouse clicks to change grid somehow
          //call the generic mouse EventHandler function on any kind of mouse click when the algorithm is not running
          if (!path.isRun()) {
              gridWork(e);
          }
      }

      @Override
      public void actionPerformed (ActionEvent e){
          // set a timer delay of 50
          timer.setDelay(50);

          if (path.isRun() && !path.isComplete() && !path.isPause()) {
              path.aStarPath();
          }
          repaint();
      }

      public boolean isOctile () {
          return isOctile;
      }

      @Override
      public void keyReleased (KeyEvent e){
          //keyPress = 0;
      }

      @Override
      public void mouseDragged (MouseEvent e){
          if (!path.isRun()) {
              gridWork(e);
          }
      }

      @Override
      public void keyTyped (KeyEvent e){
      }

      @Override
      public void mouseMoved (MouseEvent e){
      }

      @Override
      public void mouseEntered (MouseEvent e){
      }

      @Override
      public void mouseExited (MouseEvent e){
      }

      @Override
      public void mousePressed (MouseEvent e){
      }

      @Override
      public void mouseReleased (MouseEvent e){
      }
      public static void main (String[]args){
          new Controller();
      }
}
