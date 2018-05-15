// code by ynager
package ch.ethz.idsc.demo.yn;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.HashSet;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.subare.util.GlobalAssert;
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
import ch.ethz.idsc.tensor.sca.Log;
import ch.ethz.idsc.tensor.sca.Sign;

/** all pixels have the same amount of weight or clearance radius attached */
public class BayesianOccupancyGrid implements Region<Tensor>, RenderInterface {
  // private static final Set<Byte> VALUES = new HashSet<>();
  private static final short MASK_OCCUPIED = 0x00;
  private static final short MASK_EMPTY = 0xFF;
  private static final short MASK_UNKNOWN = 0xDD;
  private static final Color COLOR_OCCUPIED = new Color(MASK_OCCUPIED, MASK_OCCUPIED, MASK_OCCUPIED);
  // ---
  private Tensor lbounds;
  private final Scalar cellDim; // [m] per cell
  private final Tensor cellDimHalfVec;
  private final Scalar cellDimInv; // cells per [m]
  private final Tensor gridSize; // grid size in pixels
  private final Tensor gridRange;
  private final int dimx;
  private final int dimy;
  private final int arrayLength;
  // ---
  // lidar2grid = gworld2gpix * world2gworld * gokart2world * lidar2gokart
  private final Tensor grid2cell; // from grid frame to grid cell
  private Tensor world2grid = IdentityMatrix.of(3); // from world frame to grid frame
  private Tensor gokart2world = IdentityMatrix.of(3); // from gokart frame to world frame
  private final static Tensor LIDAR2GOKART = SensorsConfig.GLOBAL.vlp16Gokart(); // from lidar frame to gokart frame
  private final GeometricLayer geometricLayer;
  // ---
  private double[] logOdds; // array of current log odd of each cell
  private BufferedImage obstacleImage; // maximum likelihood obstacle map
  private byte[] imagePixels;
  private final Graphics2D imageGraphics;
  private HashSet<Tensor> hset = new HashSet<>();
  // ---
  private static final Scalar P_M = DoubleScalar.of(0.5); // prior
  private static final Scalar P_M_HIT = DoubleScalar.of(0.85); // inv sensor model p(m|z)
  private static final Scalar priorInvLogOdd = pToLogOdd(RealScalar.ONE.subtract(P_M));
  private static final Scalar probThreshold = DoubleScalar.of(0.5); // cells with p(m|z_1:t) > probThreshold are considered occupied
  private final Scalar lThreshold;
  // ---
  public static final int TYPE_HIT_FLOOR = 0; // TODO define somewhere in lidar module
  public static final int TYPE_HIT_OBSTACLE = 1;
  // ---
  private Scalar obsDilationRadius;
  private final Tensor scaling;
  private final Scalar[] PREDEFINED_P;

  /** Returns an instance of BayesianOccupancyGrid whose grid dimensions are ceiled to
   * fit a whole number of cells per dimension
   * @param lbounds vector of length 2
   * @param range effective size of grid in coordinate space
   * @param cellDim dimension of cell in [m]
   * @return BayesianOccupancyGrid */
  public static BayesianOccupancyGrid of(Tensor lbounds, Tensor range, Scalar cellDim) {
    Tensor sizeCeil = Ceiling.of(range.divide(Sign.requirePositive(cellDim)));
    Tensor rangeCeil = sizeCeil.multiply(cellDim);
    return new BayesianOccupancyGrid(lbounds, rangeCeil, sizeCeil);
  }

  /** @param lbounds vector of length 2
   * @param range effective size of grid in coordinate space
   * @param size size of grid in cell space */
  public BayesianOccupancyGrid(Tensor lbounds, Tensor range, Tensor size) {
    GlobalAssert.that(VectorQ.ofLength(lbounds, 2));
    GlobalAssert.that(VectorQ.ofLength(range, 2));
    GlobalAssert.that(VectorQ.ofLength(size, 2));
    System.out.print("Grid range: " + range + "\n");
    System.out.print("Grid size: " + size + "\n");
    PREDEFINED_P = new Scalar[] { //
        RealScalar.ONE.subtract(P_M_HIT), P_M_HIT };
    this.lbounds = lbounds;
    this.gridRange = range;
    this.gridSize = size;
    this.dimx = size.Get(0).number().intValue();
    this.dimy = size.Get(1).number().intValue();
    this.cellDim = range.Get(0).divide(gridSize.Get(0)); // TODO just 1st dim is checked
    cellDimInv = cellDim.reciprocal();
    obstacleImage = new BufferedImage( //
        gridSize.Get(0).number().intValue(), //
        gridSize.Get(1).number().intValue(), //
        BufferedImage.TYPE_BYTE_GRAY);
    cellDimHalfVec = Tensors.of(cellDim.divide(RealScalar.of(2)), cellDim.divide(RealScalar.of(2)));
    WritableRaster writableRaster = obstacleImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    imagePixels = dataBufferByte.getData();
    // ---
    imageGraphics = obstacleImage.createGraphics();
    obsDilationRadius = cellDim.divide(RealScalar.of(2));
    // ---
    arrayLength = gridSize.Get(0).multiply(gridSize.Get(1)).number().intValue();
    logOdds = new double[arrayLength];
    Arrays.fill(logOdds, pToLogOdd(P_M).number().doubleValue()); // fill with prior P_M
    lThreshold = pToLogOdd(probThreshold); // convert prob threshold to logOdd threshold
    // ---
    Graphics graphics = obstacleImage.getGraphics();
    graphics.setColor(new Color(MASK_UNKNOWN, MASK_UNKNOWN, MASK_UNKNOWN));
    graphics.fillRect(0, 0, obstacleImage.getWidth(), obstacleImage.getHeight());
    // ---
    scaling = DiagonalMatrix.of( //
        cellDim.number().doubleValue(), //
        cellDim.number().doubleValue(), 1);
    // Transformation Matrices
    double cellDimInvD = cellDimInv.number().doubleValue();
    double lboundX = lbounds.Get(0).number().doubleValue();
    double lboundY = lbounds.Get(1).number().doubleValue();
    grid2cell = Tensors.matrixDouble(new double[][] { { cellDimInvD, 0, 0 }, { 0, cellDimInvD, 0 }, { 0, 0, 1 } });
    world2grid = Tensors.matrixDouble(new double[][] { { 1, 0, -lboundX }, { 0, 1, -lboundY }, { 0, 0, 1 } });
    //  ---
    geometricLayer = GeometricLayer.of(grid2cell); // grid 2 cell
    geometricLayer.pushMatrix(world2grid); // world to grid
    geometricLayer.pushMatrix(gokart2world); // gokart to world
    geometricLayer.pushMatrix(LIDAR2GOKART); // lidar to gokart
  }

  /** process a new lidar observation and update the occupancy map
   * @param pos 2D position of new lidar observation in gokart coordinates
   * @param type of observation */
  public void processObservation(Tensor pos, int type) {
    if (pos.length() != 2)
      pos = pos.extract(0, 2);
    Tensor cell = toCell(pos);
    int pix = cell.Get(0).number().intValue();
    if (0 <= pix && pix < dimx) {
      int piy = cell.Get(1).number().intValue();
      if (0 <= piy && piy < dimy) {
        Scalar p_m_z = PREDEFINED_P[type];
        int idx = cellToIdx(pix, piy);
        Scalar logOddPrev = DoubleScalar.of(logOdds[idx]);
        updateCellLogOdd(idx, p_m_z);
        Scalar logOdd = DoubleScalar.of(logOdds[idx]);
        // Max likelihood estimation
        if (Scalars.lessThan(lThreshold, logOdd) && Scalars.lessEquals(logOddPrev, lThreshold))
          hset.add(cell);
        // drawSphere(pos, obsDilationRadius, MASK_OCCUPIED);
        else if (Scalars.lessThan(logOdd, lThreshold) && Scalars.lessEquals(lThreshold, logOddPrev))
          hset.remove(cell);
        // drawCell(cell, MASK_EMPTY);
        // else if (Scalars.isZero(logOdd.subtract(lThreshold)))
        // drawCell(cell, MASK_UNKNOWN);
      }
    }
  }

  /** set vehicle pose w.r.t world frame */
  public void setPose(Tensor pose) {
    GlobalAssert.that(pose.length() == 3);
    gokart2world = GokartPoseHelper.toSE2Matrix(pose);
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
    geometricLayer.pushMatrix(gokart2world);
    geometricLayer.pushMatrix(LIDAR2GOKART);
  }

  public void genObstacleMap() {
    Graphics graphics = obstacleImage.getGraphics();
    graphics.setColor(new Color(MASK_UNKNOWN, MASK_UNKNOWN, MASK_UNKNOWN));
    graphics.fillRect(0, 0, obstacleImage.getWidth(), obstacleImage.getHeight());
    for (Tensor cell : hset) {
      drawSphere(cell, obsDilationRadius, MASK_OCCUPIED);
      // drawCell(cell, MASK_OCCUPIED);
    }
    System.out.println(hset.size());
  }

  /** cells within this radius of an occupied cell will also be labeled as occupied.
   * If not set or below cellDim, only the occupied cell is labeled as an obstacle
   * @param radius */
  public void setObstacleRadius(Scalar radius) {
    obsDilationRadius = radius;
  }

  /** Updates the grid center. Grid range and size remain unchanged.
   * Overlapping segments remain unchanged.
   * 
   * @param state center of new grid */
  public void setNewlBound(Tensor lbounds) {
    // FIXME WIP
    this.lbounds = lbounds;
    double lboundX = lbounds.Get(0).number().doubleValue();
    double lboundY = lbounds.Get(1).number().doubleValue();
    world2grid = Tensors.matrixDouble(new double[][] { { 1, 0, -lboundX }, { 0, 1, -lboundY }, { 0, 0, 1 } });
    // ---
    hset.clear();
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
    geometricLayer.popMatrix();
    geometricLayer.pushMatrix(world2grid); // updated world to grid
    double[] logOddsNew = new double[arrayLength];
    Arrays.fill(logOddsNew, pToLogOdd(P_M).number().doubleValue()); // fill with prior P_M
    double threshold = lThreshold.number().doubleValue();
    for (int i = 0; i < dimx; i++)
      for (int j = 0; j < dimy; j++) {
        Tensor cellOld = Tensors.vector(i, j);
        double logOdd = logOdds[cellToIdx(cellOld)];
        Tensor pos = toPos(cellOld);
        Tensor cellNew = toCell(pos);
        int pix = cellNew.Get(0).number().intValue();
        if (0 <= pix && pix < dimx) {
          int piy = cellNew.Get(1).number().intValue();
          if (0 <= piy && piy < dimy) {
            logOddsNew[cellToIdx(cellNew)] = logOdd;
            if (logOdd > threshold)
              hset.add(cellNew);
          }
        }
      }
    logOdds = logOddsNew;
    geometricLayer.pushMatrix(gokart2world); // gokart to world
    geometricLayer.pushMatrix(LIDAR2GOKART); // lidar to gokart
  }

  /** Update the log odds of a cell using the probability of occupation given a new observation.
   * l_t = l_{t-1} + log[ p(m|z_t) / (1 - p(m|z_t)) ] + log[ (1-p(m)) / p(m) ]
   * @param idx of cell to be updated
   * @param p_m_z probability in [0,1] that Cell is occupied given the current observation z */
  private void updateCellLogOdd(int idx, Scalar p_m_z) {
    Scalar logOddDelta = pToLogOdd(p_m_z).add(priorInvLogOdd);
    logOdds[idx] += logOddDelta.number().doubleValue();
    if (Double.isInfinite(logOdds[idx]))
      throw new ArithmeticException("Overflow");
  }

  private static Scalar pToLogOdd(Scalar p) {
    return Log.FUNCTION.apply(p.divide(RealScalar.ONE.subtract(p)));
  }

  private Tensor toCell(Tensor pos) {
    Point2D point2D = geometricLayer.toPoint2D(pos);
    Tensor point = Tensors.vector(point2D.getX(), point2D.getY());
    return Floor.of(point);
  }

  private Tensor toPos(Tensor cell) {
    // return cell.multiply(cellDim).add(lbounds).add(cellDimHalfVec);
    return cell.multiply(cellDim).add(cellDimHalfVec);
  }

  private int cellToIdx(Tensor cell) {
    return cell.Get(1).multiply(gridSize.Get(0)).add(cell.Get(0)).number().intValue();
  }

  private int cellToIdx(int pix, int piy) {
    return piy * dimx + pix;
  }

  private Tensor idxToCell(int idx) {
    return Tensors.vector((int) (idx / dimx), (int) (idx % dimx));
  }

  private void drawCell(Tensor cell, short grayScale) {
    int idx = cellToIdx(cell);
    if (idx < imagePixels.length)
      imagePixels[idx] = (byte) (grayScale & 0xFF);
  }

  private void drawSphere(Tensor cell, Scalar radius, short grayScale) {
    if (Scalars.lessEquals(obsDilationRadius, cellDim)) {
      drawCell(cell, grayScale);
      return;
    }
    Tensor pos = toPos(cell);
    Scalar radiusScaled = radius.multiply(cellDimInv);
    double dim = radiusScaled.number().doubleValue();
    Ellipse2D sphere = new Ellipse2D.Double( //
        pos.Get(0).multiply(cellDimInv).subtract(radiusScaled).number().doubleValue(), //
        pos.Get(1).multiply(cellDimInv).subtract(radiusScaled).number().doubleValue(), //
        2 * dim, 2 * dim);
    imageGraphics.setColor(new Color(grayScale, grayScale, grayScale));
    imageGraphics.fill(sphere);
  }

  @Override // from Region<Tensor>
  public boolean isMember(Tensor state) {
    if (state.length() != 2)
      state = state.extract(0, 2);
    Tensor cell = toCell(state);
    int pix = cell.Get(0).number().intValue();
    if (0 <= pix && pix < dimx) {
      int piy = cell.Get(1).number().intValue();
      if (0 <= piy && piy < dimy) {
        byte gs = imagePixels[cellToIdx(toCell(state))];
        return gs == MASK_OCCUPIED;
      }
    }
    return true;
  }

  @Override // from Renderinterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor model2pixel = geometricLayer.getMatrix();
    Tensor translate = IdentityMatrix.of(3);
    translate.set(lbounds.get(0).multiply(cellDimInv), 0, 2);
    translate.set(lbounds.get(1).multiply(cellDimInv), 1, 2);
    Tensor matrix = model2pixel.dot(scaling).dot(translate);
    graphics.drawImage(obstacleImage, AffineTransforms.toAffineTransform(matrix), null);
  }
}
