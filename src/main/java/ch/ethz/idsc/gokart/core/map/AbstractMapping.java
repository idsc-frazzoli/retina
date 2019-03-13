// code by ynager, mheim, gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Objects;

/** class interprets sensor data from lidar */
public abstract class AbstractMapping implements //
    StartAndStoppable, LidarRayBlockListener, GokartPoseListener, OccupancyGrid, Runnable, RenderInterface {
  // TODO check rationale behind constant 10000!
  private static final int LIDAR_SAMPLES = 10000;
  // ---
  private final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(LIDAR_SAMPLES, 3);
  private final double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
  private final Vlp16SegmentProvider lidarSpacialProvider;
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
  private final RayOccupancyGrid rayOccupancyGrid;
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder(); // TODO needed? also contained in vlp16LcmHandler
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final SpacialXZObstaclePredicate predicate;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent;
  /** tear down flag to stop thread */
  private boolean isLaunched = true;
  private final Thread thread = new Thread(this);
  /** points_ferry is null or a matrix with dimension Nx3
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  private Tensor points3d_ferry = null;
  private final int waitMillis;

  /* package */ AbstractMapping(RayOccupancyGrid rayOccupancyGrid, SpacialXZObstaclePredicate predicate, //
                                int max_alt, int waitMillis) {
    lidarSpacialProvider = new Vlp16SegmentProvider(offset, max_alt);
    this.predicate = predicate;
    this.waitMillis = waitMillis;
    this.rayOccupancyGrid = rayOccupancyGrid;
    // ---
    lidarSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    // ---
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    gokartPoseLcmClient.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  @Override // from StartAndStoppable
  public void start() {
    vlp16LcmHandler.startSubscriptions();
    gokartPoseLcmClient.startSubscriptions();
    thread.start();
  }

  @Override // from StartAndStoppable
  public void stop() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmHandler.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  public void prepareMap() {
    rayOccupancyGrid.genObstacleMap();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (lidarRayBlockEvent.dimensions != 3)
      throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
    // ---
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    points3d_ferry = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    thread.interrupt();
  }

  @Override
  public void run() {
    while (isLaunched) {
      Tensor points = points3d_ferry;
      if (Objects.nonNull(points) && Objects.nonNull(gokartPoseEvent)) {
        points3d_ferry = null;
        // TODO pose quality is not considered yet
        rayOccupancyGrid.setPose(gokartPoseEvent.getPose());
        for (Tensor point : points) { // point x, y, z
          boolean isObstacle = predicate.isObstacle(point); // only x and z are used
          rayOccupancyGrid.processObservation( //
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
    return rayOccupancyGrid.isMember(element);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    gokartPoseEvent = getEvent;
  }

  @Override //  from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    rayOccupancyGrid.render(geometricLayer, graphics);
  }

  @Override // from OccupancyGrid
  public Tensor getGridSize() {
    return rayOccupancyGrid.getGridSize();
  }

  @Override // from OccupancyGrid
  public boolean isCellOccupied(int x, int y) {
    return rayOccupancyGrid.isCellOccupied(x, y);
  }

  @Override // from OccupancyGrid
  public Tensor getTransform() {
    return rayOccupancyGrid.getTransform();
  }

  @Override // from OccupancyGrid
  public void clearStart(int startX, int startY, double orientation) {
    rayOccupancyGrid.clearStart(startX, startY, orientation);
  }
}
