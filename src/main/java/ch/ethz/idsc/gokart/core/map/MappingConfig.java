// code by ynager
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.track.TrackReconConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for the mapping of the gokart surroundings and obstacles */
public class MappingConfig {
  public static final MappingConfig GLOBAL = AppResources.load(new MappingConfig());
  /***************************************************/
  /** Prior on occupancy probability of a single cell in grid */
  public Scalar P_M = DoubleScalar.of(0.5);
  /** Probability that a cell is occupied given that
   * the LIDAR has observed an obstacle in that cell. */
  public Scalar P_M_HIT = DoubleScalar.of(0.85);
  /** Probability that a cell is occupied given that
   * the LIDAR ray has passed through it and was below minObsHeight. */
  public Scalar P_M_PASS = DoubleScalar.of(0.2);
  /** Probability threshold for occupancy threshold. Grid cells
   * with occupancy probability larger than P_THRESH are classified
   * as occupied */
  public Scalar P_THRESH = DoubleScalar.of(0.5);
  /** Forgetting factor lambda in (0, 1]
   * Lambda == 1 results in past and current measurements being equally important.
   * Lambda == 0.4 was a choice in the past */
  public Scalar lambda = DoubleScalar.of(0.8);
  /** any obstacle closer than minDistance not mapped,
   * otherwise the driver is put in the map. */
  // TODO JPH param should be obsolete if the mapping is started when the driver is already seated
  public Scalar minDistance = Quantity.of(2, SI.METER);
  /** Occupied cells are dilated with this radius before generating the obstacle map.
   * the safety margin accounts not only for the half-width of the gokart
   * but also the corner cutting behavior of the pure pursuit */
  public Scalar obsRadius = Quantity.of(1.1, SI.METER);
  public final Scalar trackDrivingObsRadius = Quantity.of(0.9, SI.METER);
  /** Cell dimension of a single grid cell in [m] */
  public final Scalar cellDim = Quantity.of(0.2, SI.METER);
  public Boolean alongLine = false;
  /** Minimal obstacle height. Used for inverse sensor model
   * only relevant when alongLine == true */
  public final Scalar minObsHeight = Quantity.of(0, SI.METER);
  /** lower bounds coordinates with interpretation meter */
  public Tensor lBounds = Tensors.vector(16, 20);
  /** range of map with interpretation meter */
  public Tensor range = Tensors.vector(50, 50);

  /***************************************************/
  public double getP_M() {
    return P_M.number().doubleValue();
  }

  public double getP_M_HIT() {
    return P_M_HIT.number().doubleValue();
  }

  public double getP_M_PASS() {
    return P_M_PASS.number().doubleValue();
  }

  public double getP_THRESH() {
    return P_THRESH.number().doubleValue();
  }

  public double getLambda() {
    return lambda.number().doubleValue();
  }

  /***************************************************/
  /** @return Dubilab specific BayesianOccupancyGrid */
  public BayesianOccupancyGrid createBayesianOccupancyGrid() {
    return BayesianOccupancyGrid.of(lBounds, range, cellDim, obsRadius);
  }

  public BayesianOccupancyGrid createTrackFittingBayesianOccupancyGrid() {
    return BayesianOccupancyGrid.of(lBounds, range, cellDim, trackDrivingObsRadius, true);
  }

  /** @return dubilab specific BayesianOccupancyGrid */
  // TODO JPH only used for offline
  public BayesianOccupancyGrid createThinBayesianOccupancyGrid() {
    return BayesianOccupancyGrid.of(lBounds, range, cellDim, Quantity.of(0, SI.METER), true);
  }

  public SightLineOccupancyGrid createSightLineOccupancyGrid() {
    return SightLineOccupancyGrid.of(lBounds, range, cellDim);
  }

  public GenericBayesianMapping createObstacleMapping() {
    return new GenericBayesianMapping( //
        SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        1000, //
        createBayesianOccupancyGrid(), //
        -1);
  }

  public GenericBayesianMapping createTrackMapping() {
    return new GenericBayesianMapping( //
        TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        200, //
        createTrackFittingBayesianOccupancyGrid(), //
        -6);
  }
}
