// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Collection;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.lidar.LidarPolarFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarSectorProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** create an obstacle map based on lidar sight lines */
public class SightLineMapping extends AbstractMapping<SightLineOccupancyGrid> {
  private final ErodedMap map = ErodedMap.of(occupancyGrid, MappingConfig.GLOBAL.obsRadius);
  private final BlindSpots blindSpots;

  public static SightLineMapping defaultObstacle() {
    return new SightLineMapping(SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), BlindSpots.defaultGokart(), 1000);
  }

  public static SightLineMapping defaultTrack() {
    return new SightLineMapping(TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate(), BlindSpots.defaultGokart(), 200);
  }

  public SightLineMapping(SpacialXZObstaclePredicate predicate, BlindSpots blindSpots, int waitMillis) {
    super(MappingConfig.GLOBAL.createSightLineOccupancyGrid(), predicate, waitMillis);
    this.blindSpots = blindSpots;
    // ---
    LidarPolarFiringCollector lidarPolarFiringCollector = new LidarPolarFiringCollector(LIDAR_SAMPLES, 3);
    Vlp16PolarProvider lidarPolarProvider = new Vlp16PolarProvider();
    lidarPolarProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    lidarPolarProvider.addListener(lidarPolarFiringCollector);
    LidarSectorProvider lidarSectorProvider = new LidarSectorProvider(VelodyneStatics.AZIMUTH_RESOLUTION, SightLineHandler.SECTORS);
    lidarSectorProvider.addListener(lidarPolarFiringCollector);
    lidarPolarFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarPolarProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSectorProvider);
  }

  // from AbstractMapping
  @Override
  public void prepareMap() {
    map.genObstacleMap();
  }

  // from AbstractMapping
  @Override
  public ImageGrid getMap() {
    return map;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    super.getEvent(gokartPoseEvent);
    occupancyGrid.setPose(gokartPoseEvent.getPose());
    map.setPose(gokartPoseEvent.getPose());
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched) {
      Collection<Tensor> points = SightLineHandler.getClosestPoints(points_ferry, predicate, blindSpots);
      if (!points.isEmpty()) {
        Tensor polygon = SightLineHandler.polygon(points);
        SightLineHandler.closeSector(polygon);
        occupancyGrid.updateMap(polygon);
      } else {
        try {
          Thread.sleep(waitMillis);
        } catch (Exception e) {
          // ---
        }
      }
    }
  }
}
