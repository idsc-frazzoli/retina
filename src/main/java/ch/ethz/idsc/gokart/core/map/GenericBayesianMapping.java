// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Objects;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** class interprets sensor data from lidar */
/* package */ class GenericBayesianMapping extends AbstractMapping<BayesianOccupancyGrid> {
  // TODO document parameters
  public GenericBayesianMapping( //
      SpacialXZObstaclePredicate spacialXZObstaclePredicate, //
      int waitMillis, //
      BayesianOccupancyGrid bayesianOccupancyGrid, //
      int max_alt) {
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

  @Override // from AbstractMapping
  public final void prepareMap() {
    imageGrid.genObstacleMap();
  }

  @Override // from AbstractMapping
  public final BayesianOccupancyGrid getMap() {
    return imageGrid;
  }

  @Override // from Runnable
  public final void run() {
    while (isLaunched) {
      Tensor points = points_ferry;
      if (Objects.nonNull(points) && //
          LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
        points_ferry = null;
        imageGrid.setPose(gokartPoseEvent.getPose());
        for (Tensor point : points) { // point x, y, z
          boolean isObstacle = spacialXZObstaclePredicate.isObstacle(point); // only x and z are used
          imageGrid.processObservation( //
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

  @Override
  public Region<Tensor> getErodedRegion() {
    throw new UnsupportedOperationException();
  }
}
