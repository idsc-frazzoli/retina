package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class TrackLayoutInitialGuess implements RenderInterface {
  private class Cell {
    final int x;
    final int y;
    Scalar cost;
    Boolean inQ = false;
    Cell lastCell = null;
    ArrayList<Cell> neighBors;
    //ArrayList<Scalar> neighBorCost;
    public Cell(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public void findNeighbors()
    {
      for(Neighbor n: possibleNeighbors) {
        Cell newNeighBor = n.getFrom(this);
        if(newNeighBor!=null) {
          this.neighBors.add(newNeighBor);
          //this.neighBorCost.add(Sqrt.of(RealScalar.of(n.dx^2+n.dy^2)));
        }
      }
    }
  }

  private class Neighbor {
    final int dx;
    final int dy;

    public Neighbor(int dx, int dy) {
      this.dx = dx;
      this.dy = dy;
    }

    public Cell getFrom(Cell cell) {
      int nx = cell.x+dx;
      int ny = cell.y+dy;
      if(!occupancyGrid.isCellOccupied(new Point(nx, ny)))
        return cellGrid[nx][ny];
      else
        return null;
    }
  }

  private final BayesianOccupancyGrid occupancyGrid;

  public TrackLayoutInitialGuess(BayesianOccupancyGrid occupancyGrid) {
    this.occupancyGrid = occupancyGrid;
  }

  // private function for dijkstra
  Cell startingPoint;
  PriorityQueue<Cell> Q;
  Cell[][] cellGrid;
  ArrayList<Neighbor> possibleNeighbors;

  void prepareCells(Tensor gridsize, int startx, int starty, double startorientation) {
    Comparator<Cell> comparator = new Comparator<TrackLayoutInitialGuess.Cell>() {
      @Override
      public int compare(Cell o1, Cell o2) {
        if (Scalars.lessThan(o1.cost, o2.cost))
          return -1;
        else if (o1.cost.equals(o2.cost))
          return 0;
        else
          return 1;
      }
    };
    //prepare grid
    for(int i = 0;i<gridsize.Get(0).number().intValue();i++) {
      for(int ii = 0;ii<gridsize.Get(1).number().intValue();ii++) {
        Cell newCell= new Cell(i, ii);
        if(!occupancyGrid.isCellOccupied(//
            new Point(newCell.x, newCell.y)))
            cellGrid[i][ii]=newCell;
      }
    }
    //prepare
    
    //add to Q
    Q = new PriorityQueue<>(100000, comparator);
    for(int i = 0;i<gridsize.Get(0).number().intValue();i++) {
      for(int ii = 0;ii<gridsize.Get(1).number().intValue();ii++) {
         if(cellGrid[i][ii]!=null) {
           Q.add(cellGrid[i][ii]);
           cellGrid[i][ii].inQ = true;
         }
      }
    }
    //add neighbors
    for(Cell c: Q) {
      c.findNeighbors();
    }
  }

  void initialise(int x, int y) {
    Tensor gridSize = occupancyGrid.getGridSize();
    possibleNeighbors = new ArrayList<>();
    possibleNeighbors.add(new Neighbor(-1, -1));
    possibleNeighbors.add(new Neighbor(-1, 0));
    possibleNeighbors.add(new Neighbor(-1, 1));
    possibleNeighbors.add(new Neighbor(0, -1));
    possibleNeighbors.add(new Neighbor(0, 1));
    possibleNeighbors.add(new Neighbor(1, -1));
    possibleNeighbors.add(new Neighbor(1, 0));
    possibleNeighbors.add(new Neighbor(1, 1));
    prepareCells(gridSize);
    cellGrid[x][y].cost = RealScalar.ZERO;
  }
  
  public List<Cell> getWayTo(Cell target) {
    LinkedList<Cell> solution = new LinkedList<>();
    solution.add(target);
    while(!solution.getFirst().equals(startingPoint)) {
      solution.addFirst(solution.getFirst().lastCell);
    }
    return solution;
  }

  public void update(int startX,int startY, double startorientation) {
    initialise(x,y);
    while(!Q.isEmpty()) {
      Cell currentCell = Q.poll();
      currentCell.inQ = false;
      for(Cell n: currentCell.neighBors) {
        if(n.inQ) {
          //TODO: maybe use neighborcost (not that important)
          Scalar alternativ = currentCell.cost.add(RealScalar.ONE);
          if(Scalars.lessThan(alternativ, n.cost))
          {
            //this could potentially be too slow
            Q.remove(n);
            n.cost = alternativ;
            n.lastCell = currentCell;
            Q.add(n);
          }
        }
      }
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }
}
