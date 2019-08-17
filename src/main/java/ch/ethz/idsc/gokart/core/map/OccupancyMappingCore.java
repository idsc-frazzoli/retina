// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.nio.FloatBuffer;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.track.TrackReconConfig;
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
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** requires:
 * gokart pose event
 * lidar ray packets */
public class OccupancyMappingCore implements GokartPoseListener, LidarRayBlockListener {
  private final ErodableMap erodableMap;
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate;
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  // ---
  public final Vlp16Decoder vlp16Decoder = new Vlp16Decoder();
  // ---
  Tensor points_ferry = null;

  public OccupancyMappingCore(OccupancyConfig occupancyConfig) {
    erodableMap = occupancyConfig.erodableMap();
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
    action();
  }

  public void action() {
    process();
  }

  /** @param radius 0 means no erosion
   * @return */
  public BufferedImageRegion erodedMap(int radius) {
    return erodableMap.erodedRegion(radius);
  }

  public boolean process() {
    Tensor points = points_ferry;
    if (Objects.nonNull(points) && //
        LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
      points_ferry = null;
      erodableMap.setReference(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      for (Tensor point : points) { // point x, y, z
        boolean isObstacle = spacialXZObstaclePredicate.isObstacle(point); // only x and z are used
        erodableMap.setPixel(point, isObstacle);
      }
      return true;
    }
    return false;
  }
}
