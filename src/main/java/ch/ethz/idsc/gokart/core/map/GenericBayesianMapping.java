// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.track.TrackReconConfig;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** class interprets sensor data from lidar */
public class GenericBayesianMapping extends AbstractMapping<BayesianOccupancyGrid> {
  public static GenericBayesianMapping createTrackMapping() {
    return new GenericBayesianMapping( //
        TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        200, MappingConfig.GLOBAL.createTrackFittingBayesianOccupancyGrid(), //
        -6);
  }

  public static GenericBayesianMapping createObstacleMapping() {
    return new GenericBayesianMapping( //
        SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        1000, MappingConfig.GLOBAL.createBayesianOccupancyGrid(), -1);
  }

  // ---
  // TODO GJOEL document parameters
  private GenericBayesianMapping( //
      SpacialXZObstaclePredicate spacialXZObstaclePredicate, //
      int waitMillis, BayesianOccupancyGrid bayesianOccupancyGrid, int max_alt) {
    super(spacialXZObstaclePredicate, waitMillis, bayesianOccupancyGrid);
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(LIDAR_SAMPLES, 3);
    VelodyneSpacialProvider velodyneSpacialProvider = //
        new Vlp16SegmentProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), max_alt);
    velodyneSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    velodyneSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(velodyneSpacialProvider);
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
      if (Objects.nonNull(points) && //
          LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
        points_ferry = null;
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
