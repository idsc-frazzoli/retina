// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** class interprets sensor data from lidar */
public abstract class AbstractBayesianMapping extends AbstractMapping<BayesianOccupancyGrid> {
  private final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(LIDAR_SAMPLES, 3);
  private final double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
  private final Vlp16SegmentProvider lidarSpacialProvider;
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();

  /* package */ AbstractBayesianMapping(BayesianOccupancyGrid bayesianOccupancyGrid, //
      SpacialXZObstaclePredicate predicate, int max_alt, int waitMillis) {
    super(bayesianOccupancyGrid, predicate, waitMillis);
    lidarSpacialProvider = new Vlp16SegmentProvider(offset, max_alt);
    lidarSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  // from AbstractMapping
  public void prepareMap() {
    occupancyGrid.genObstacleMap();
  }

  // from AbstractMapping
  public BayesianOccupancyGrid getMap() {
    return occupancyGrid;
  }

  @Override
  public void run() {
    while (isLaunched) {
      Tensor points = points_ferry;
      if (Objects.nonNull(points) && Objects.nonNull(gokartPoseEvent)) {
        points_ferry = null;
        // TODO pose quality is not considered yet
        occupancyGrid.setPose(gokartPoseEvent.getPose());
        for (Tensor point : points) { // point x, y, z
          boolean isObstacle = predicate.isObstacle(point); // only x and z are used
          occupancyGrid.processObservation( //
              point, //
              isObstacle ? 1 : 0);
        }
      } else
        try {
          Thread.sleep(waitMillis);
        } catch (Exception exception) {
          // ---
        }
    }
  }
}
