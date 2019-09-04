// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.util.Collection;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.track.TrackReconConfig;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.lidar.LidarPolarFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarSectorProvider;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16PolarProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** create an obstacle map based on lidar sight lines */
public class SightLinesMapping extends AbstractMapping<SightLineOccupancyGrid> {
  public static SightLinesMapping defaultObstacle() {
    return new SightLinesMapping( //
        SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        1000, //
        BlindSpots.defaultGokart());
  }

  public static SightLinesMapping defaultTrack() {
    return new SightLinesMapping( //
        TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate(), //
        200, //
        BlindSpots.defaultGokart());
  }

  // ---
  private final ErodedMap erodedMap = ErodedMap.of(imageGrid, MappingConfig.GLOBAL.obsRadius);
  private final BlindSpots blindSpots;

  private SightLinesMapping(SpacialXZObstaclePredicate predicate, int waitMillis, BlindSpots blindSpots) {
    super(predicate, waitMillis, MappingConfig.GLOBAL.createSightLineOccupancyGrid());
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

  @Override // from AbstractMapping
  public void prepareMap() {
    erodedMap.genObstacleMap();
  }

  @Override // from AbstractMapping
  public ImageGrid getMap() {
    return erodedMap;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    super.getEvent(gokartPoseEvent);
    imageGrid.setPose(gokartPoseEvent.getPose());
    erodedMap.setPose(gokartPoseEvent.getPose());
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched) {
      Collection<Tensor> points = SightLineHandler.getClosestPoints(points_ferry, spacialXZObstaclePredicate, blindSpots);
      if (!points.isEmpty()) {
        Tensor polygon = SightLineHandler.polygon(points);
        SightLineHandler.closeSector(polygon);
        imageGrid.updateMap(polygon);
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
