// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

import java.awt.*;
import java.util.Objects;

/** class interprets sensor data from lidar */
public abstract class AbstractMapping extends AbstractLidarMapping implements OccupancyGrid, RenderInterface {
  private final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(LIDAR_SAMPLES, 3);
  private final double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
  private final Vlp16SegmentProvider lidarSpacialProvider;
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
  private final BayesianOccupancyGrid bayesianOccupancyGrid;

  /* package */ AbstractMapping(BayesianOccupancyGrid bayesianOccupancyGrid, SpacialXZObstaclePredicate predicate, //
                                int max_alt, int waitMillis) {
    super(predicate, waitMillis);
    lidarSpacialProvider = new Vlp16SegmentProvider(offset, max_alt);
    this.bayesianOccupancyGrid = bayesianOccupancyGrid;
    // ---
    lidarSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    // ---
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  public void prepareMap() {
    bayesianOccupancyGrid.genObstacleMap();
  }

  @Override
  public void run() {
    while (isLaunched) {
      Tensor points = points_ferry;
      if (Objects.nonNull(points) && Objects.nonNull(gokartPoseEvent)) {
        points_ferry = null;
        // TODO pose quality is not considered yet
        bayesianOccupancyGrid.setPose(gokartPoseEvent.getPose());
        for (Tensor point : points) { // point x, y, z
          boolean isObstacle = predicate.isObstacle(point); // only x and z are used
          bayesianOccupancyGrid.processObservation( //
              point,
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

  @Override // from Region
  public boolean isMember(Tensor element) {
    return bayesianOccupancyGrid.isMember(element);
  }

  @Override //  from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    bayesianOccupancyGrid.render(geometricLayer, graphics);
  }

  @Override // from OccupancyGrid
  public Tensor getGridSize() {
    return bayesianOccupancyGrid.getGridSize();
  }

  @Override // from OccupancyGrid
  public boolean isCellOccupied(int x, int y) {
    return bayesianOccupancyGrid.isCellOccupied(x, y);
  }

  @Override // from OccupancyGrid
  public Tensor getTransform() {
    return bayesianOccupancyGrid.getTransform();
  }

  @Override // from OccupancyGrid
  public void clearStart(int startX, int startY, double orientation) {
    bayesianOccupancyGrid.clearStart(startX, startY, orientation);
  }
}
