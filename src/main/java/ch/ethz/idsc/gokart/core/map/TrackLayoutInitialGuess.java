// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.UniformBSpline2;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.mat.PseudoInverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;

public class TrackLayoutInitialGuess implements RenderInterface {
  private class Cell {
    private final int x;
    private final int y;
    private Scalar cost;
    private boolean inQ = false;
    private boolean processed = false;
    private Cell lastCell = null;
    // TODO JPH/MH create type Cell+Cost and use only 1 list
    private List<Cell> neighBors = null;
    private List<Scalar> neighBorCost = null;

    public Cell(int x, int y) {
      this.x = x;
      this.y = y;
      this.cost = DoubleScalar.POSITIVE_INFINITY;
    }

    public Tensor getPos() {
      // TODO can use AffineFrame2D
      return occupancyGrid.getTransform().dot(Tensors.vector(x, y, 1));
    }

    @Override
    public String toString() {
      return x + " / " + y + " : " + cost + (inQ ? " in Q" : "") + (processed ? " is processed" : "");
    }

    public LinkedList<Cell> getRoute() {
      LinkedList<Cell> result;
      if (Objects.isNull(lastCell))
        result = new LinkedList<>();
      else
        result = lastCell.getRoute();
      result.add(this);
      return result;
    }

    public void findNeighbors() {
      if (Objects.isNull(neighBors)) {
        neighBors = new ArrayList<>();
        neighBorCost = new ArrayList<>();
        for (Neighbor neighbor : possibleNeighbors) {
          Cell newNeighBor = neighbor.getFrom(this);
          if (Objects.nonNull(newNeighBor)) {
            neighBors.add(newNeighBor);
            neighBorCost.add(neighbor.cost);
          }
        }
      }
    }
  }

  private class Neighbor {
    private final int dx;
    private final int dy;
    private final Scalar cost;

    public Neighbor(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
      cost = RealScalar.of(Math.hypot(dx, dy));
    }

    public Cell getFrom(Cell cell) {
      int nx = cell.x + dx;
      int ny = cell.y + dy;
      if (!occupancyGrid.isCellOccupied(nx, ny))
        return cellGrid[nx][ny];
      return null;
    }
  }

  private final OccupancyGrid occupancyGrid;

  public TrackLayoutInitialGuess(OccupancyGrid occupancyGrid) {
    this.occupancyGrid = occupancyGrid;
  }

  // private function for dijkstra
  private Cell startingPoint;
  private PriorityQueue<Cell> priorityQueue;
  private int m;
  private int n;
  // starting point for Dijkstra
  private Cell dijkstraStart;
  // goal for dijkstra
  private Cell dijkstraGokartBack;
  private Cell dijkstraTarget;
  // actual goal
  private Cell actualTarget;
  private Cell[][] cellGrid;
  private List<Neighbor> possibleNeighbors;
  /** has to be linked list to invoke specific member functions */
  private LinkedList<Cell> route;
  // TODO MH not used
  private LinkedList<Cell> forwardRoute;
  private Tensor routePolygon;
  private boolean closed = false;
  private List<Tensor> positionalSupports = new LinkedList<>();
  private Tensor controlPoints = Tensors.empty();

  // this is potentially slow
  Cell getFarthestCell() {
    Cell farthest = dijkstraStart;
    for (int i = 0; i < m; ++i)
      for (int ii = 0; ii < n; ++ii)
        if (Objects.nonNull(cellGrid[i][ii]) && //
            Objects.nonNull(cellGrid[i][ii].lastCell) && //
            Scalars.lessThan(farthest.cost, cellGrid[i][ii].cost))
          farthest = cellGrid[i][ii];
    return farthest;
  }

  public boolean isClosed() {
    return closed;
  }

  private static final Comparator<Cell> COMPARATOR = new Comparator<Cell>() {
    @Override
    public int compare(Cell o1, Cell o2) {
      return Scalars.compare(o1.cost, o2.cost);
    }
  };

  boolean prepareCells(Tensor gridsize, int startx, int starty, double startorientation, Tensor currPos, boolean searchFromGokart) {
    m = gridsize.Get(0).number().intValue();
    n = gridsize.Get(1).number().intValue();
    cellGrid = new Cell[m][n];
    double dirx = Math.cos(startorientation);
    double diry = Math.sin(startorientation);
    int sfx = (int) Math.round(startx + 2 * dirx);
    int sfy = (int) Math.round(starty + 2 * diry);
    priorityQueue = new PriorityQueue<>(COMPARATOR);
    // prepare gridif (true)
    for (int i = 0; i < gridsize.Get(0).number().intValue(); ++i)
      for (int ii = 0; ii < gridsize.Get(1).number().intValue(); ++ii) {
        Cell newCell = new Cell(i, ii);
        if ((i == sfx && ii == sfy) || //
            !occupancyGrid.isCellOccupied(newCell.x, newCell.y))
          cellGrid[i][ii] = newCell;
      }
    // add limit at start
    addStartingLine(startx, starty, startorientation);
    // add to Q
    /* Q = new PriorityQueue<>(100000, comparator);
     * for (int i = 0; i < gridsize.Get(0).number().intValue(); i++) {
     * for (int ii = 0; ii < gridsize.Get(1).number().intValue(); ii++) {
     * if (cellGrid[i][ii] != null) {
     * Q.add(cellGrid[i][ii]);
     * cellGrid[i][ii].inQ = true;
     * }
     * }
     * } */
    // gokart immediate target
    if (Objects.nonNull(currPos))
      dijkstraGokartBack = cellGrid[currPos.Get(0).number().intValue()][currPos.Get(1).number().intValue()];
    // add start to Q
    if (!searchFromGokart)
      dijkstraStart = cellGrid[sfx][sfy];
    else
      dijkstraStart = dijkstraGokartBack;
    if (Objects.isNull(dijkstraStart))
      return false;
    dijkstraStart.cost = RealScalar.ZERO;
    dijkstraStart.inQ = true;
    priorityQueue.add(dijkstraStart);
    // add target
    dijkstraTarget = cellGrid[(int) Math.round(startx - 3 * dirx)][(int) Math.round(starty - 3 * diry)];
    // add neighbors
    /* for (int i = 0; i < gridsize.Get(0).number().intValue(); i++) {
     * System.out.println("row: "+i);
     * for (int ii = 0; ii < gridsize.Get(1).number().intValue(); ii++) {
     * if (cellGrid[i][ii] != null)
     * cellGrid[i][ii].findNeighbors();
     * }
     * } */
    return true;
  }

  void addStartingLine(int startx, int starty, double startorientation) {
    double xforward = Math.cos(startorientation);
    double yforward = Math.sin(startorientation);
    double xsideward = yforward;
    double ysideward = -xforward;
    // find right end
    double currentx = startx;
    double currenty = starty;
    while (currentx >= 0 && currentx < m //
        && currenty >= 0 && currenty < n //
        && Objects.nonNull(cellGrid[(int) currentx][(int) currenty])) {
      currentx += xsideward;
      currenty += ysideward;
    }
    int rightx = (int) currentx;
    int righty = (int) currenty;
    // find left end
    currentx = startx;
    currenty = starty;
    while (currentx >= 0 && currentx < m //
        && currenty >= 0 && currenty < n //
        && Objects.nonNull(cellGrid[(int) currentx][(int) currenty])) {
      currentx -= xsideward;
      currenty -= ysideward;
    }
    int leftx = (int) currentx;
    int lefty = (int) currenty;
    // delete all cells on line
    int steps = (int) (Math.sqrt(1.0 * (rightx - leftx) * (rightx - leftx) + 1.0 * (righty - lefty) * (righty - lefty)) + 1.0);
    for (int i = 0; i < steps; ++i) {
      int posx = (int) Math.round(leftx + (rightx - leftx) * (1.0 * i / (steps - 1.0)));
      int posy = (int) Math.round(lefty + (righty - lefty) * (1.0 * i / (steps - 1.0)));
      if (posx > 0 && posx < m - 1 && posy > 0 && posy < n - 1) {
        // set the neightbors to zero
        cellGrid[posx - 1][posy - 1] = null;
        cellGrid[posx - 1][posy] = null;
        cellGrid[posx - 1][posy + 1] = null;
        cellGrid[posx][posy - 1] = null;
        cellGrid[posx][posy] = null;
        cellGrid[posx][posy + 1] = null;
        cellGrid[posx + 1][posy - 1] = null;
        cellGrid[posx + 1][posy] = null;
        cellGrid[posx + 1][posy + 1] = null;
      }
    }
  }

  private boolean initialise(int x, int y, double orientation, Tensor currPos, boolean searchFromGokart) {
    Tensor gridSize = occupancyGrid.getGridSize();
    possibleNeighbors = new ArrayList<>();
    for (int dx = -2; dx <= 2; ++dx)
      for (int dy = -2; dy <= 2; ++dy)
        if (dx != 0 || dy != 0)
          possibleNeighbors.add(new Neighbor(dx, dy));
    return prepareCells(gridSize, x, y, orientation, currPos, searchFromGokart);
  }

  public List<Cell> getWayTo(Cell target) {
    LinkedList<Cell> solution = new LinkedList<>();
    solution.add(target);
    while (!solution.getFirst().equals(startingPoint)) {
      solution.addFirst(solution.getFirst().lastCell);
    }
    return solution;
  }

  public void processDijkstra() {
    while (!priorityQueue.isEmpty()) {
      Cell currentCell = priorityQueue.poll();
      currentCell.findNeighbors();
      currentCell.processed = true;
      currentCell.inQ = false;
      int nCount = 0;
      for (Cell n : currentCell.neighBors) {
        if (!n.processed) {
          // TODO maybe use neighborcost (not that important)
          Scalar alternativ = currentCell.cost.add(currentCell.neighBorCost.get(nCount));
          if (Scalars.lessThan(alternativ, n.cost)) {
            // this could potentially be too slow
            priorityQueue.remove(n);
            n.cost = alternativ;
            n.lastCell = currentCell;
            n.inQ = true;
            priorityQueue.add(n);
          }
        }
        ++nCount;
      }
    }
  }

  public Tensor getPixelPosition(Tensor worldPosition) {
    Tensor transform = occupancyGrid.getTransform();
    // TODO JPH/MH try the following line:
    // Tensor wp = worldPosition.extract(0, 2).append(Quantity.of(1, SI.METER));
    Tensor wp = Tensors.empty();
    wp.append(worldPosition.Get(0));
    wp.append(worldPosition.Get(1));
    wp.append(Quantity.of(1, SI.METER));
    return LinearSolve.of(transform, wp);
  }

  public void update(int startX, int startY, double startorientation) {
    update(startX, startY, startorientation, null);
  }

  public void update(int startX, int startY, double startorientation, Tensor gokartPosition) {
    // position if map
    Tensor curPos = null;
    if (Objects.nonNull(gokartPosition))
      curPos = getPixelPosition(gokartPosition);
    if (initialise(startX, startY, startorientation, curPos, false)) {
      processDijkstra();
      // check if we can reach target
      if (reachable(dijkstraTarget)) // we can reach target;
      {
        System.out.println("target found");
        closed = true;
        route = dijkstraTarget.getRoute();
      } else {
        System.out.println("target not found");
        closed = false;
        actualTarget = getFarthestCell();
        LinkedList<Cell> routeFromStart = actualTarget.getRoute();
        route = routeFromStart;
        // can we reach gokart?
        /* if (reachable(dijkstraGokartBack)) {
         * System.out.println("start->gokart found. Expanding beyond gokart");
         * route = routeFromStart;
         * } else {
         * // search from gokart toward
         * System.out.println("searching from gokart towards starting line");
         * boolean targetAvailable = initialise(startX, startY, startorientation, curPos, true);
         * if (targetAvailable)
         * processDijkstra();
         * if (targetAvailable && reachable(dijkstraTarget)) {
         * // found way from gokart
         * route = dijkstraTarget.getRoute();
         * route.addAll(routeFromStart);
         * } else {
         * System.out.println("no route found to gokart");
         * route = routeFromStart;
         * }
         * } */
      }
    } else {
      System.out.println("Target not available.");
    }
    routePolygon = null;
  }

  private static boolean reachable(Cell target) {
    return Objects.nonNull(target) //
        && target.processed;
  }

  public Tensor getRoutePolygon() {
    if (Objects.isNull(routePolygon)) {
      routePolygon = Tensors.empty();
      if (Objects.nonNull(route)) {
        Tensor grid2model = occupancyGrid.getTransform();
        for (Cell cell : route)
          routePolygon.append(grid2model.dot(Tensors.vector(cell.x, cell.y, 1)));
      }
    }
    return routePolygon;
  }

  /** @param spacing
   * @param controlPointResolution
   * @return matrix of dimension n x 2 */
  public Tensor getControlPointGuess(Scalar spacing, Scalar controlPointResolution) {
    Tensor wantedPositionsXY = Tensors.empty();
    // Tensor wantedPositionsY = Tensors.empty();
    Tensor lastPosition = route.getFirst().getPos();
    positionalSupports = new LinkedList<>();
    for (Cell cell : route) {
      Tensor pos = cell.getPos();
      Tensor dist = pos.subtract(lastPosition);
      if (Scalars.lessThan(spacing, Norm._2.of(dist))) {
        lastPosition = pos;
        wantedPositionsXY.append(Extract2D.FUNCTION.apply(pos));
        // wantedPositionsY.append(pos.Get(1));
        positionalSupports.add(pos.copy());
      }
    }
    if (wantedPositionsXY.length() > 3) {
      wantedPositionsXY.append(Extract2D.FUNCTION.apply(route.getLast().getPos()));
      // wantedPositionsY.append(route.getLast().getPos().Get(1));
      wantedPositionsXY = wantedPositionsXY.multiply(Quantity.of(1, SI.METER));
      // wantedPositionsY = wantedPositionsY.multiply(Quantity.of(1, SI.METER));
      // solve for bspline points
      // number of bspline query points
      int m = wantedPositionsXY.length();
      // number of control points
      int n = (int) (wantedPositionsXY.length() * controlPointResolution.number().doubleValue());
      // first possible value is 0
      // last possible value is n-2
      final Tensor queryPositions;
      if (closed) // we found closed solution
        queryPositions = Tensors.vector((i) -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
      else
        queryPositions = Tensors.vector((i) -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m);
      final Tensor splineMatrix = UniformBSpline2.getBasisMatrix(n, 0, closed, queryPositions);
      // solve for control points: x
      Tensor pinv = PseudoInverse.of(splineMatrix);
      // TODO JPH/MH can this be done smarter:
      Tensor controlpointsXY = pinv.dot(wantedPositionsXY);
      // Tensor controlpointsY = pinv.dot(wantedPositionsY);
      if (true) // TODO JPH/MH
        controlPoints = controlpointsXY;
      // Transpose.of(Tensors.of(controlpointsX, controlpointsY));
      return controlpointsXY.copy(); // Tensors.of(controlpointsX, controlpointsY);
    }
    System.out.println("no usable track!");
    return null;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor routePolygon = getRoutePolygon();
    Path2D path2d = geometricLayer.toPath2D(routePolygon);
    graphics.draw(path2d);
    /* for (Tensor t : freeLines) {
     * path2d = geometricLayer.toPath2D(t);
     * graphics.draw(path2d);
     * } */
  }

  public void renderHR(GeometricLayer geometricLayer, Graphics2D graphics) {
    float width = geometricLayer.getMatrix().get(0).Get(0).number().floatValue() / 7.5f;
    Stroke defaultStroke;
    BasicStroke thick = new BasicStroke(width);
    graphics.setColor(Color.RED);
    defaultStroke = graphics.getStroke();
    graphics.setStroke(thick);
    if (true) {
      Tensor routePolygon = getRoutePolygon();
      Path2D path2d = geometricLayer.toPath2D(routePolygon);
      graphics.draw(path2d);
    }
    if (true) {
      graphics.setColor(Color.ORANGE);
      for (Tensor t : positionalSupports) {
        Tensor pos = geometricLayer.toVector(t);
        int r = (int) (width * 2.5f);
        int X = pos.Get(0).number().intValue();
        int Y = pos.Get(1).number().intValue();
        graphics.drawOval(X - r, Y - r, 2 * r, 2 * r);
      }
    }
    if (false) { // TODO JPH/MH
      graphics.setColor(Color.BLUE);
      Path2D path2d = geometricLayer.toPath2D(controlPoints);
      graphics.draw(path2d);
    }
    graphics.setStroke(defaultStroke);
    graphics.setColor(Color.WHITE);
  }
}
