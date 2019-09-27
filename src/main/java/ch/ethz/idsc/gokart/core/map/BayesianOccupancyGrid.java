// code by ynager, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.math.Bresenham;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

/** all pixels have the same amount of weight or clearance radius attached */
public class BayesianOccupancyGrid extends ImageGrid {
  /** @param lbounds vector of length 2
   * @param range effective size of grid in coordinate space
   * @param cellDim non-negative dimension of cell with unit SI.METER
   * @param obstacleRadius with unit SI.METER
   * cells within this radius of an occupied cell will also be labeled as occupied.
   * If not set or below cellDim, only the occupied cell is labeled as an obstacle
   * @return BayesianOccupancyGrid with grid dimensions ceil'ed to fit a whole number of cells per dimension */
  public static BayesianOccupancyGrid of(Tensor lbounds, Tensor range, Scalar cellDim, Scalar obstacleRadius) {
    return of(lbounds, range, cellDim, obstacleRadius, false);
  }

  /** @param lbounds vector of length 2
   * @param range effective size of grid in coordinate space
   * @param cellDim non-negative dimension of cell with unit SI.METER
   * @param obstacleRadius with unit SI.METER
   * @param fill whether the map should be considered occupied at the beginning
   * cells within this radius of an occupied cell will also be labeled as occupied.
   * If not set or below cellDim, only the occupied cell is labeled as an obstacle
   * @return BayesianOccupancyGrid with grid dimensions ceil'ed to fit a whole number of cells per dimension */
  public static BayesianOccupancyGrid of(Tensor lbounds, Tensor range, Scalar cellDim, Scalar obstacleRadius, boolean fill) {
    // sizeCeil is for instance {200[m^-1], 200[m^-1]}
    Tensor sizeCeil = Ceiling.of(range.divide(Sign.requirePositive(cellDim)));
    Tensor rangeCeil = sizeCeil.multiply(cellDim);
    Dimension dimension = new Dimension( //
        Magnitude.PER_METER.toInt(sizeCeil.Get(0)), //
        Magnitude.PER_METER.toInt(sizeCeil.Get(1)));
    return new BayesianOccupancyGrid(lbounds, rangeCeil, dimension, obstacleRadius, fill);
  }

  // TODO JPH assign all constants in constructor using a reference to a MappingConfig instance
  /** prior */
  private final double P_M = MappingConfig.GLOBAL.getP_M(); // prior
  private final double L_M_INV = BayesianOccupancyGrid.pToLogOdd(1 - P_M);
  /** inv sensor model p(m|z) */
  private final double P_M_HIT = MappingConfig.GLOBAL.getP_M_HIT();
  private final double P_M_PASS = MappingConfig.GLOBAL.getP_M_PASS();
  /** cells with p(m|z_1:t) > probThreshold are considered occupied */
  private final double P_THRESH = MappingConfig.GLOBAL.getP_THRESH();
  private final double L_THRESH = BayesianOccupancyGrid.pToLogOdd(P_THRESH);
  private final double[] PREDEFINED_P = { 1 - P_M_HIT, P_M_HIT, P_M_PASS };
  /** forgetting factor for previous classifications */
  private final double lambda = MappingConfig.GLOBAL.getLambda();
  private final boolean alongLine = MappingConfig.GLOBAL.alongLine;
  // ---
  private final Tensor cellDimHalfVec;
  // ---
  /** set of occupied cells */
  private final Set<Tensor> hset = new HashSet<>();
  // ---
  private final Scalar obsDilationRadius;
  private final double lFactor;
  // ---
  /** array containing current log odds of each cell */
  private double[] logOdds;

  /** @param lbounds vector of length 2
   * @param rangeCeil effective size of grid in coordinate space of the form {value, value}
   * @param dimension of grid in cell space */
  private BayesianOccupancyGrid(Tensor lbounds, Tensor rangeCeil, Dimension dimension, Scalar obstacleRadius, boolean fillMap) {
    super(lbounds, rangeCeil, dimension);
    cellDimHalfVec = Tensors.of(cellDim, cellDim).divide(RealScalar.of(2)).unmodifiable();
    // ---
    obsDilationRadius = Magnitude.METER.apply(obstacleRadius);
    genObstacleMap();
    Scalar ratio = MappingConfig.GLOBAL.minObsHeight.divide(SensorsConfig.GLOBAL.vlp16Height);
    lFactor = RealScalar.ONE.subtract(ratio).number().doubleValue();
    // ---
    // PREDEFINED_P
    logOdds = new double[dimX() * dimY()];
    if (fillMap) {
      double logOdd = BayesianOccupancyGrid.pToLogOdd(0.99);
      Arrays.fill(logOdds, logOdd);
      if (logOdd > L_THRESH)
        // updateHset();
        IntStream.range(0, dimX()).boxed().flatMap(i -> IntStream.range(0, dimY()).mapToObj(j -> Tensors.vector(i, j))).forEach(hset::add);
    } else
      Arrays.fill(logOdds, BayesianOccupancyGrid.pToLogOdd(P_M));
  }

  /** process a new lidar observation and update the occupancy map
   *
   * @param pos 2D position of new lidar observation in gokart coordinates
   * @param type of observation either 0, or 1 */
  public void processObservation(Tensor pos, int type) {
    if (Objects.nonNull(gokart2world)) {
      Tensor cell = lidarToCell(pos);
      int pix = cell.Get(0).number().intValue();
      int piy = cell.Get(1).number().intValue();
      if (isCellInGrid(pix, piy)) {
        int idx = cellToIdx(pix, piy);
        double logOddPrev = logOdds[idx];
        updateCellLogOdd(pix, piy, PREDEFINED_P[type]);
        double logOdd = logOdds[idx];
        // Max likelihood estimation
        synchronized (hset) {
          if (L_THRESH < logOdd && logOddPrev <= L_THRESH)
            hset.add(cell);
          else //
          if (logOdd < L_THRESH && L_THRESH <= logOddPrev)
            hset.remove(cell);
        }
        // ---
        if (type == 0 && alongLine) {
          Tensor pos0 = pos.multiply(DoubleScalar.of(lFactor));
          Tensor cell0 = lidarToCell(pos0);
          List<Point> line = Bresenham.line( //
              cell0.Get(0).number().intValue(), //
              cell0.Get(1).number().intValue(), //
              pix, piy);
          for (Point point : line.subList(0, line.size() - 1)) {
            idx = cellToIdx(point.x, point.y);
            logOddPrev = logOdds[idx];
            updateCellLogOdd(point.x, point.y, PREDEFINED_P[2]);
            logOdd = logOdds[idx];
            // Max likelihood estimation
            synchronized (hset) {
              if (L_THRESH < logOdd && logOddPrev <= L_THRESH)
                hset.add(cell);
              else //
              if (logOdd < L_THRESH && L_THRESH <= logOddPrev)
                hset.remove(cell);
            }
          }
        }
      }
    }
    // else {
    // System.err.println("Observation not processed - no pose received");
    // }
  }

  /***************************************************/
  /** clears current obstacle image and redraws all known obstacles */
  // TODO LHF this function should return, or update a region object created here, or provided from the outside!
  public synchronized void genObstacleMap() {
    imageGraphics.setColor(COLOR_UNKNOWN);
    imageGraphics.fillRect(0, 0, obstacleImage.getWidth(), obstacleImage.getHeight());
    synchronized (hset) {
      if (Scalars.lessEquals(obsDilationRadius, cellDim))
        for (Tensor cell : hset)
          drawCell(cell, MASK_OCCUPIED);
      else {
        imageGraphics.setColor(COLOR_OCCUPIED);
        for (Tensor cell : hset)
          drawSphere(cell, obsDilationRadius);
      }
    }
  }

  private void drawCell(Tensor cell, byte grayScale) {
    imagePixels[cellToIdx(cell)] = grayScale;
  }

  private void drawSphere(Tensor cell, Scalar radius) {
    Tensor pos = toPos(cell);
    Scalar radiusScaled = radius.multiply(cellDimInv);
    double dim = radiusScaled.number().doubleValue();
    Ellipse2D sphere = new Ellipse2D.Double( //
        pos.Get(0).multiply(cellDimInv).subtract(radiusScaled).number().doubleValue(), //
        pos.Get(1).multiply(cellDimInv).subtract(radiusScaled).number().doubleValue(), //
        2 * dim, 2 * dim);
    imageGraphics.fill(sphere);
  }

  /***************************************************/
  /** Updates the grid lbounds. Grid range and size remain unchanged.
   * Overlapping segment is copied.
   *
   * @param lbounds */
  // TODO function not used yet
  public void setNewlBound(Tensor lbounds) {
    if (Objects.nonNull(gokart2world)) {
      this.lbounds = VectorQ.requireLength(lbounds, 2);
      // ---
      lidar2cellLayer.popMatrix();
      lidar2cellLayer.popMatrix();
      lidar2cellLayer.popMatrix();
      // ---
      lidar2cellLayer.pushMatrix(getWorld2grid()); // updated world to grid
      double[] logOddsNew = new double[dimX() * dimY()];
      Arrays.fill(logOddsNew, BayesianOccupancyGrid.pToLogOdd(P_M));
      synchronized (hset) {
        hset.clear();
        Tensor trans = lidarToCell(toPos(Tensors.vector(0, 0))); // calculate translation
        final int ofsx = trans.Get(0).number().intValue();
        final int ofsy = trans.Get(1).number().intValue();
        // ---
        for (int i = 0; i < dimX(); i++)
          for (int j = 0; j < dimY(); j++) {
            double logOdd = logOdds[cellToIdx(i, j)];
            Tensor cell = Tensors.vector(i + ofsx, j + ofsy);
            if (isCellInGrid(cell)) {
              logOddsNew[cellToIdx(cell)] = logOdd;
              if (logOdd > L_THRESH)
                hset.add(cell);
            }
          }
      }
      logOdds = logOddsNew;
      lidar2cellLayer.pushMatrix(gokart2world); // gokart to world
      lidar2cellLayer.pushMatrix(lidar2gokart); // lidar to gokart
    }
  }

  // private void updateHset() {
  //   synchronized (hset) {
  //     IntStream.range(0, dimX()).boxed().flatMap(i -> //
  //         IntStream.range(0, dimY()).filter(j -> //
  //             logOdds[cellToIdx(i, j)] > L_THRESH).mapToObj(j -> Tensors.vector(i, j))).forEach(hset::add);
  //   }
  // }

  /** Update the log odds of a cell using the probability of occupation given a new observation.
   * l_t = l_{t-1} + log[ p(m|z_t) / (1 - p(m|z_t)) ] + log[ (1-p(m)) / p(m) ]
   * @param pix of cell to be updated
   * @param piy of cell to be updated
   * @param p_m_z probability in [0, 1] that Cell is occupied given the current observation z */
  private void updateCellLogOdd(int pix, int piy, double p_m_z) {
    int idx = cellToIdx(pix, piy);
    double logOddDelta = BayesianOccupancyGrid.pToLogOdd(p_m_z) + L_M_INV;
    logOdds[idx] = lambda * logOdds[idx] + logOddDelta;
    if (Double.isInfinite(logOdds[idx]))
      throw new ArithmeticException("Overflow");
  }

  private Tensor toPos(Tensor cell) {
    return cell.multiply(cellDim).add(cellDimHalfVec);
  }

  @Override // from OccupancyGrid
  public void clearStart(int startX, int startY, double orientation) {
    Tensor rotation = RotationMatrix.of(orientation);
    List<Tensor> toBeRemoved = new ArrayList<>();
    int fromy = (int) (-cellDimInv.number().doubleValue() * 3 * 2.0f);
    int endy = -fromy;
    for (int ix = -1; ix < cellDimInv.number().doubleValue() * 12 * 2.0f; ix++)
      for (int iy = fromy; iy <= endy; iy++) {
        Tensor posVec = Tensors.vector(ix, iy);
        Tensor rotPos = rotation.dot(posVec);
        int posX = (int) (startX + rotPos.Get(0).number().intValue() / 2.0);
        int posY = (int) (startY + rotPos.Get(1).number().intValue() / 2.0);
        if (isCellInGrid(posX, posY)) {
          logOdds[cellToIdx(posX, posY)] = 0;
          toBeRemoved.add(Tensors.vector(posX, posY));
        }
      }
    // updateHset();
    synchronized (hset) {
      hset.removeAll(toBeRemoved);
    }
  }

  /** @param p from the open interval (0, 1)
   * @return */
  static double pToLogOdd(double p) {
    GlobalAssert.that(p < 1);
    return Math.log(p / (1 - p));
  }
}
