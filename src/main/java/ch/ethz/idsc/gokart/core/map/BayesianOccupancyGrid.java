// code by ynager
package ch.ethz.idsc.gokart.core.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.RadiusXY;
import ch.ethz.idsc.retina.util.math.Bresenham;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Sign;

/** all pixels have the same amount of weight or clearance radius attached
 * 
 * the cascade of affine transformation is
 * lidar2cell == grid2gcell * world2grid * gokart2world * lidar2gokart */
public class BayesianOccupancyGrid implements RenderInterface, OccupancyGrid {
  // TODO invert colors: black should be empty space
  private static final byte MASK_OCCUPIED = 0;
  private static final Color COLOR_OCCUPIED = Color.BLACK;
  // private static final Color COLOR_UNKNOWN = new Color(0xdd, 0xdd, 0xdd);
  private static final Color COLOR_UNKNOWN = Color.WHITE;

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

  // ---
  private final Tensor lidar2gokart = SensorsConfig.GLOBAL.vlp16Gokart(); // from lidar frame to gokart frame
  // ---
  // TODO assign all constants in constructor using a reference to a MappingConfig instance
  /** prior */
  private final double P_M = MappingConfig.GLOBAL.getP_M(); // prior
  private final double L_M_INV = StaticHelper.pToLogOdd(1 - P_M);
  /** inv sensor model p(m|z) */
  private final double P_M_HIT = MappingConfig.GLOBAL.getP_M_HIT();
  private final double P_M_PASS = MappingConfig.GLOBAL.getP_M_PASS();
  /** cells with p(m|z_1:t) > probThreshold are considered occupied */
  private final double P_THRESH = MappingConfig.GLOBAL.getP_THRESH();
  private final double L_THRESH = StaticHelper.pToLogOdd(P_THRESH);
  private final double[] PREDEFINED_P = { 1 - P_M_HIT, P_M_HIT, P_M_PASS };
  /** forgetting factor for previous classifications */
  private final double lambda = MappingConfig.GLOBAL.getLambda();
  private final boolean alongLine = MappingConfig.GLOBAL.alongLine;
  // ---
  private final Scalar cellDim; // [m] per cell
  private final Tensor cellDimHalfVec;
  private final Scalar cellDimInv; // cells per [m]
  private final Tensor gridSize; // grid size in pixels
  private final int dimx;
  private final int dimy;
  // ---
  private final GeometricLayer lidar2cellLayer;
  private final GeometricLayer world2cellLayer;
  /** maximum likelihood obstacle map */
  private final BufferedImage obstacleImage;
  private final byte[] imagePixels;
  private final Graphics2D imageGraphics;
  /** set of occupied cells */
  private final Set<Tensor> hset = new HashSet<>();
  // ---
  private final Scalar obsDilationRadius;
  private final Tensor scaling;
  private final double lFactor;
  // ---
  private Tensor lbounds;
  /** from gokart frame to world frame */
  private Tensor gokart2world = null;
  // ---
  /** array containing current log odds of each cell */
  private double[] logOdds;

  /** @param lbounds vector of length 2
   * @param rangeCeil effective size of grid in coordinate space of the form {value, value}
   * @param dimension of grid in cell space */
  private BayesianOccupancyGrid(Tensor lbounds, Tensor rangeCeil, Dimension dimension, Scalar obstacleRadius, boolean fillMap) {
    VectorQ.requireLength(rangeCeil, 2);
    System.out.print("Grid range: " + rangeCeil + "\n");
    System.out.print("Grid size: " + dimension + "\n");
    this.lbounds = VectorQ.requireLength(lbounds, 2);
    gridSize = Tensors.vector(dimension.width, dimension.height).unmodifiable();
    dimx = dimension.width;
    dimy = dimension.height;
    cellDim = RadiusXY.requireSame(rangeCeil).divide(gridSize.Get(0));
    cellDimInv = cellDim.reciprocal();
    cellDimHalfVec = Tensors.of(cellDim, cellDim).divide(RealScalar.of(2)).unmodifiable();
    scaling = DiagonalMatrix.of(cellDim, cellDim, RealScalar.ONE).unmodifiable();
    // ---
    obstacleImage = new BufferedImage(dimx, dimy, BufferedImage.TYPE_BYTE_GRAY);
    WritableRaster writableRaster = obstacleImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    imagePixels = dataBufferByte.getData();
    imageGraphics = obstacleImage.createGraphics();
    obsDilationRadius = Magnitude.METER.apply(obstacleRadius);
    genObstacleMap();
    Scalar ratio = MappingConfig.GLOBAL.minObsHeight.divide(SensorsConfig.GLOBAL.vlp16Height);
    lFactor = RealScalar.ONE.subtract(ratio).number().doubleValue();
    // ---
    // PREDEFINED_P
    logOdds = new double[dimx * dimy];
    if (!fillMap)
      Arrays.fill(logOdds, StaticHelper.pToLogOdd(P_M));
    else {
      Arrays.fill(logOdds, StaticHelper.pToLogOdd(0.99));
      setHset();
    }
    // ---
    Tensor grid2cell = DiagonalMatrix.of(cellDimInv, cellDimInv, RealScalar.ONE);
    Tensor world2grid = getWorld2grid();
    //  ---
    lidar2cellLayer = GeometricLayer.of(grid2cell); // grid 2 cell
    lidar2cellLayer.pushMatrix(world2grid); // world to grid
    lidar2cellLayer.pushMatrix(IdentityMatrix.of(3)); // placeholder gokart2world
    lidar2cellLayer.pushMatrix(lidar2gokart); // lidar to gokart
    // ---
    world2cellLayer = GeometricLayer.of(grid2cell);
    world2cellLayer.pushMatrix(world2grid);
  }

  /** @return matrix */
  private Tensor getWorld2grid() {
    return Se2Utils.toSE2Matrix(lbounds.negate().append(RealScalar.ZERO));
  }

  /** process a new lidar observation and update the occupancy map
   * 
   * @param pos 2D position of new lidar observation in gokart coordinates
   * @param type of observation either 0, or 1 */
  public void processObservation(Tensor pos, int type) {
    if (Objects.nonNull(gokart2world)) {
      Tensor cell = lidarToCell(pos);
      int pix = cell.Get(0).number().intValue();
      if (0 <= pix && pix < dimx) {
        int piy = cell.Get(1).number().intValue();
        if (0 <= piy && piy < dimy) {
          int idx = piy * dimx + pix;
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
              idx = point.y * dimx + point.x;
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
    }
    // else {
    // System.err.println("Observation not processed - no pose received");
    // }
  }

  /** set vehicle pose w.r.t world frame
   * 
   * @param pose vector of the form {px, py, heading}
   * @param quality */
  public void setPose(Tensor pose) {
    gokart2world = GokartPoseHelper.toSE2Matrix(pose);
    lidar2cellLayer.popMatrix();
    lidar2cellLayer.popMatrix();
    lidar2cellLayer.pushMatrix(gokart2world);
    lidar2cellLayer.pushMatrix(lidar2gokart);
  }

  /***************************************************/
  /** clears current obstacle image and redraws all known obstacles */
  // TODO LHF this function should return, or update a region object created here, or provided from the outside!
  public void genObstacleMap() {
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
      double[] logOddsNew = new double[dimx * dimy];
      Arrays.fill(logOddsNew, StaticHelper.pToLogOdd(P_M));
      double threshold = L_THRESH;
      synchronized (hset) {
        hset.clear();
        Tensor trans = lidarToCell(toPos(Tensors.vector(0, 0))); // calculate translation
        final int ofsx = trans.Get(0).number().intValue();
        final int ofsy = trans.Get(1).number().intValue();
        // ---
        for (int i = 0; i < dimx; i++)
          for (int j = 0; j < dimy; j++) {
            double logOdd = logOdds[j * dimx + i];
            int pix = i + ofsx;
            if (0 <= pix && pix < dimx) {
              int piy = j + ofsy;
              if (0 <= piy && piy < dimy) {
                logOddsNew[piy * dimx + pix] = logOdd;
                if (logOdd > threshold)
                  hset.add(Tensors.vector(pix, piy));
              }
            }
          }
      }
      logOdds = logOddsNew;
      lidar2cellLayer.pushMatrix(gokart2world); // gokart to world
      lidar2cellLayer.pushMatrix(lidar2gokart); // lidar to gokart
    }
  }

  private void setHset() {
    synchronized (hset) {
      hset.clear();
      for (int i = 0; i < dimx; i++)
        for (int j = 0; j < dimy; j++) {
          double logOdd = logOdds[j * dimx + i];
          if (logOdd > L_THRESH)
            hset.add(Tensors.vector(i, j));
        }
    }
  }

  /** Update the log odds of a cell using the probability of occupation given a new observation.
   * l_t = l_{t-1} + log[ p(m|z_t) / (1 - p(m|z_t)) ] + log[ (1-p(m)) / p(m) ]
   * @param idx of cell to be updated
   * @param p_m_z probability in [0,1] that Cell is occupied given the current observation z */
  private void updateCellLogOdd(int pix, int piy, double p_m_z) {
    int idx = piy * dimx + pix;
    double logOddDelta = StaticHelper.pToLogOdd(p_m_z) + L_M_INV;
    logOdds[idx] = lambda * logOdds[idx] + logOddDelta;
    if (Double.isInfinite(logOdds[idx]))
      throw new ArithmeticException("Overflow");
  }

  /** function is used as key
   * 
   * @param pos vector of the form {px, py, ...}; only the first two entries are considered
   * @return */
  private Tensor lidarToCell(Tensor pos) {
    // TODO investigate if class with 2 int's is an attractive replacement as key type
    return Floor.of(lidar2cellLayer.toVector(pos));
  }

  /** Remark: values in the open interval (-1, 0) are now incorrectly ceil'ed to 0.
   * however, the consequences are negligible
   * 
   * @param pos
   * @return */
  private Point worldToCell(Tensor pos) {
    Point2D point2D = world2cellLayer.toPoint2D(pos);
    return new Point( //
        (int) point2D.getX(), //
        (int) point2D.getY());
  }

  private Tensor toPos(Tensor cell) {
    return cell.multiply(cellDim).add(cellDimHalfVec);
  }

  private int cellToIdx(Tensor cell) {
    return cell.Get(1).multiply(gridSize.Get(0)).add(cell.Get(0)).number().intValue();
  }

  @Override // from Region<Tensor>
  public boolean isMember(Tensor state) {
    Point cell = worldToCell(state);
    int pix = cell.x;
    if (0 <= pix && pix < dimx) {
      int piy = cell.y;
      if (0 <= piy && piy < dimy)
        return imagePixels[piy * dimx + pix] == MASK_OCCUPIED;
    }
    return true;
  }

  /** @return the currently used gridsize */
  @Override
  public Tensor getGridSize() {
    return gridSize;
  }

  /** return if specific cell is occupied
   * @param cell
   * @return true if cell is occupied */
  @Override
  public boolean isCellOccupied(int pix, int piy) {
    if (0 <= pix && pix < dimx)
      if (0 <= piy && piy < dimy)
        return imagePixels[piy * dimx + pix] == MASK_OCCUPIED;
    return true;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor model2pixel = geometricLayer.getMatrix();
    Tensor translate = IdentityMatrix.of(3);
    translate.set(lbounds.get(0).multiply(cellDimInv), 0, 2);
    translate.set(lbounds.get(1).multiply(cellDimInv), 1, 2);
    Tensor matrix = model2pixel.dot(scaling).dot(translate);
    graphics.drawImage(obstacleImage, AffineTransforms.toAffineTransform(matrix), null);
  }

  @Override
  public Tensor getTransform() {
    Tensor translate = IdentityMatrix.of(3);
    translate.set(lbounds.get(0).multiply(cellDimInv), 0, 2);
    translate.set(lbounds.get(1).multiply(cellDimInv), 1, 2);
    return IdentityMatrix.of(3).dot(scaling).dot(translate);
  }
}
