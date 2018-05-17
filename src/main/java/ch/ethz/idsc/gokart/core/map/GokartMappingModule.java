package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;
import java.nio.FloatBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
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
import ch.ethz.idsc.retina.dev.lidar.app.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class GokartMappingModule implements Region<Tensor>, LidarRayBlockListener, GokartPoseListener, RenderInterface {
  final BayesianOccupancyGrid grid;
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private SpacialXZObstaclePredicate predicate = SimpleSpacialObstaclePredicate.createVlp16();
  private final Tensor gridRange = Tensors.vector(85, 85);
  private final Tensor lbounds = Tensors.vector(0, 0);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();

  public GokartMappingModule() {
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(10000, 3);
    double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    Vlp16SegmentProvider lidarSpacialProvider = new Vlp16SegmentProvider(offset, -1);
    Scalar obstacleRadius = Magnitude.METER.apply(MappingConfig.GLOBAL.obsRadius);
    lidarSpacialProvider.setLimitLo(obstacleRadius.number().doubleValue() + 0.5);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    gokartPoseLcmClient.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
    vlp16LcmHandler.startSubscriptions();
    gokartPoseLcmClient.startSubscriptions();
    // ---
    grid = BayesianOccupancyGrid.of(lbounds, gridRange, DoubleScalar.of(0.2));
    grid.setObstacleRadius(Magnitude.METER.apply(MappingConfig.GLOBAL.obsRadius));
  }

  public void prepareMap() {
    grid.genObstacleMap();
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    if (Objects.nonNull(grid))
      if (lidarRayBlockEvent.dimensions == 3)
        while (floatBuffer.hasRemaining()) {
          double x = floatBuffer.get();
          double y = floatBuffer.get();
          double z = floatBuffer.get();
          //
          boolean isObstacle = predicate.isObstacle(x, z);
          Tensor planarPoint = Tensors.vectorDouble(x, y, 1);
          int type = isObstacle ? 1 : 0;
          grid.processObservation(planarPoint, type);
        }
  }

  @Override //  from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    grid.render(geometricLayer, graphics);
  }

  @Override // from Region
  public boolean isMember(Tensor element) {
    return grid.isMember(element);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    grid.setPose(getEvent.getPose(), getEvent.getQuality());
  }
}
