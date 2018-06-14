//code by ynager
package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** class interprets sensor data from lidar information */
// TODO since this class does not (yet) extend from AbstractModule, the class name is not good
public class GokartMappingModule implements //
    StartAndStoppable, Region<Tensor>, LidarRayBlockListener, GokartPoseListener, RenderInterface {
  private final LidarAngularFiringCollector lidarAngularFiringCollector = //
      new LidarAngularFiringCollector(10000, 3);
  private final double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
  private final Vlp16SegmentProvider lidarSpacialProvider = new Vlp16SegmentProvider(offset, -1);
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
  public static BayesianOccupancyGrid grid;
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final SpacialXZObstaclePredicate predicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final Tensor gridRange = Tensors.vector(40, 40); // TODO comment on magic const 640/7.5
  private final Tensor lbounds = Tensors.vector(30, 30);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();

  public GokartMappingModule() {
    final Scalar obstacleRadius = Magnitude.METER.apply(MappingConfig.GLOBAL.obsRadius);
    lidarSpacialProvider.setLimitLo(obstacleRadius.number().doubleValue() + 0.5); // TODO comment on 0.5
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
    grid = BayesianOccupancyGrid.of(lbounds, gridRange, MappingConfig.GLOBAL.cellDim);
    grid.setObstacleRadius(obstacleRadius);
  }

  @Override // from StartAndStoppable
  public void start() {
    vlp16LcmHandler.startSubscriptions();
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from StartAndStoppable
  public void stop() {
    vlp16LcmHandler.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  public void prepareMap() {
    grid.genObstacleMap();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    if (lidarRayBlockEvent.dimensions != 3)
      throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
    while (floatBuffer.hasRemaining()) {
      float x = floatBuffer.get();
      float y = floatBuffer.get();
      float z = floatBuffer.get();
      //
      boolean isObstacle = predicate.isObstacle(x, z);
      grid.processObservation( //
          Tensors.vectorDouble(x, y), // planar point
          isObstacle ? 1 : 0);
    }
  }

  @Override // from Region
  public boolean isMember(Tensor element) {
    return grid.isMember(element);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    grid.setPose(getEvent.getPose(), getEvent.getQuality());
  }

  @Override //  from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    grid.render(geometricLayer, graphics);
  }
}
