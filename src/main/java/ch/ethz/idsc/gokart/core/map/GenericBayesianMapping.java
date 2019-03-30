// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** class interprets sensor data from lidar */
public class GenericBayesianMapping extends AbstractMapping<BayesianOccupancyGrid> {
  public static GenericBayesianMapping createTrackMapping() {
    return new GenericBayesianMapping( //
        MappingConfig.GLOBAL.createTrackFittingBayesianOccupancyGrid(), //
        TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        200, -6);
  }

  public static GenericBayesianMapping createObstacleMapping() {
    return new GenericBayesianMapping( //
        MappingConfig.GLOBAL.createBayesianOccupancyGrid(), //
        SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        1000, -1);
  }

  // ---
  // TODO JG document parameters
  private GenericBayesianMapping( //
      BayesianOccupancyGrid bayesianOccupancyGrid, //
      SpacialXZObstaclePredicate spacialXZObstaclePredicate, //
      int waitMillis, int max_alt) {
    super(bayesianOccupancyGrid, spacialXZObstaclePredicate, waitMillis);
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(LIDAR_SAMPLES, 3);
    Vlp16SegmentProvider lidarSpacialProvider = new Vlp16SegmentProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), max_alt);
    lidarSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  // from AbstractMapping
  @Override
  public final void prepareMap() {
    occupancyGrid.genObstacleMap();
  }

  // from AbstractMapping
  @Override
  public final BayesianOccupancyGrid getMap() {
    return occupancyGrid;
  }

  @Override
  public final void run() {
    while (isLaunched) {
      Tensor points = points_ferry;
      if (Objects.nonNull(points) && Objects.nonNull(gokartPoseEvent)) {
        points_ferry = null;
        // TODO pose quality is not considered yet
        occupancyGrid.setPose(gokartPoseEvent.getPose());
        for (Tensor point : points) { // point x, y, z
          boolean isObstacle = spacialXZObstaclePredicate.isObstacle(point); // only x and z are used
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
