// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.track.TrackReconConfig;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmClient;
import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.retina.app.map.ErodableMap;
import ch.ethz.idsc.retina.lidar.LidarPacketCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** free space module always runs in the background
 * 
 * other modules that require free space information subscribe
 * to the instance of {@link OccupancyMappingModule} to obtain
 * an eroded snapshot of the current obstacles. */
public class OccupancyMappingModule extends AbstractModule implements //
    GokartPoseListener, LidarRayBlockListener, Runnable {
  private final ErodableMap erodableMap;
  private final List<AbstractModule> abstractModules = new CopyOnWriteArrayList<>();
  private final Thread thread = new Thread(this);
  // ---
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate;
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  // ---
  private final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  private final Vlp16LcmClient vlp16LcmClient = SensorsConfig.GLOBAL.vlp16LcmClient(vlp16Decoder);
  private volatile boolean isLaunched = true;
  // ---
  private Tensor points_ferry = null;

  public OccupancyMappingModule() {
    // TODO JPH this is dubilab specific an will be moved to config area
    BufferedImage bufferedImage = new BufferedImage(160, 80, BufferedImage.TYPE_BYTE_GRAY);
    Tensor model2pixel = Dot.of( //
        Se2Matrix.of(Tensors.vector(32, 20, Math.PI / 4)), //
        DiagonalMatrix.of( //
            38.4 / bufferedImage.getWidth(), //
            19.2 / bufferedImage.getHeight(), 1), //
        Se2Matrix.flipY(bufferedImage.getHeight()));
    erodableMap = new ErodableMap(bufferedImage, model2pixel);
    // ---
    // TODO JPH make configurable
    spacialXZObstaclePredicate = TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate();
    // TODO JPH investigate constants
    LidarPacketCollector lidarPacketCollector = new LidarPacketCollector(10_000, 3);
    VelodyneSpacialProvider velodyneSpacialProvider = //
        new Vlp16SegmentProvider(SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue(), -3);
    velodyneSpacialProvider.setLimitLo(Magnitude.METER.toDouble(MappingConfig.GLOBAL.minDistance));
    velodyneSpacialProvider.addListener(lidarPacketCollector);
    lidarPacketCollector.addListener(this);
    vlp16Decoder.addRayListener(velodyneSpacialProvider);
    vlp16Decoder.addRayListener(lidarPacketCollector);
  }

  @Override
  protected void first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    vlp16LcmClient.startSubscriptions();
    thread.start();
  }

  @Override
  protected void last() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  public void subscribe(AbstractModule abstractModule) {
    abstractModules.add(abstractModule);
  }

  public void unsubscribe(AbstractModule abstractModule) {
    abstractModules.remove(abstractModule);
  }

  public BufferedImageRegion erodedMap(int radius) {
    return erodableMap.erodedRegion(radius);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    this.gokartPoseEvent = getEvent;
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (lidarRayBlockEvent.dimensions != 3)
      throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
    // ---
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    points_ferry = Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
    thread.interrupt();
  }

  @Override
  public void run() {
    while (isLaunched) {
      Tensor points = points_ferry;
      if (Objects.nonNull(points) && //
          LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
        points_ferry = null;
        erodableMap.setReference(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
        for (Tensor point : points) { // point x, y, z
          boolean isObstacle = spacialXZObstaclePredicate.isObstacle(point); // only x and z are used
          erodableMap.setPixel(point, isObstacle);
        }
      } else
        try {
          Thread.sleep(1_000);
        } catch (Exception exception) {
          // ---
        }
    }
  }
}
