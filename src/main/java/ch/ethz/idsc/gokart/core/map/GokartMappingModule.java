//code by ynager
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.nio.FloatBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** class interprets sensor data from lidar */
// TODO since this class does not (yet) extend from AbstractModule, the class name is not good
public class GokartMappingModule implements //
    StartAndStoppable, Region<Tensor>, LidarRayBlockListener, GokartPoseListener, Runnable, RenderInterface {
  // TODO check rationale behind constant 10000!
  private static final int LIDAR_SAMPLES = 10000;
  /** ferry for visualizing grid in presenter lcm module */
  public static RenderInterface GRID_RENDER;
  // ---
  private final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(LIDAR_SAMPLES, 3);
  private final double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
  private final Vlp16SegmentProvider lidarSpacialProvider = new Vlp16SegmentProvider(offset, -1);
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
  private final BayesianOccupancyGrid bayesianOccupancyGrid = MappingConfig.GLOBAL.createBayesianOccupancyGrid();
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final SpacialXZObstaclePredicate predicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent;
  /** tear down flag to stop thread */
  private boolean isLaunched = true;
  private final Thread thread = new Thread(this);
  /** points_ferry is null or a matrix with dimension Nx3
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  private Tensor points3d_ferry = null;

  public GokartMappingModule() {
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
    // ---
    GRID_RENDER = bayesianOccupancyGrid;
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
    bayesianOccupancyGrid.genObstacleMap();
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
        bayesianOccupancyGrid.setPose(gokartPoseEvent.getPose());
        for (Tensor point : points) {
          boolean isObstacle = predicate.isObstacle(point); // only x and z are used
          bayesianOccupancyGrid.processObservation( //
              point.extract(0, 2), // planar point x y
              isObstacle ? 1 : 0);
        }
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
    }
  }

  @Override // from Region
  public boolean isMember(Tensor element) {
    return bayesianOccupancyGrid.isMember(element);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    gokartPoseEvent = getEvent;
  }

  @Override //  from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    bayesianOccupancyGrid.render(geometricLayer, graphics);
  }
}
