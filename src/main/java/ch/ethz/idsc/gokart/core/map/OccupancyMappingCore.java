// code by jph
package ch.ethz.idsc.gokart.core.map;

import java.nio.FloatBuffer;

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
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneSpacialProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ref.TensorListener;

/** requires:
 * gokart pose event
 * lidar ray packets */
public class OccupancyMappingCore implements GokartPoseListener, LidarRayBlockListener, TensorListener {
  public final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final ErodableMap erodableMap;
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate;
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

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
    velodyneDecoder.addRayListener(velodyneSpacialProvider);
    velodyneDecoder.addRayListener(lidarPacketCollector);
  }

  @Override // from GokartPoseListener
  public final void getEvent(GokartPoseEvent getEvent) {
    this.gokartPoseEvent = getEvent;
  }

  @Override // from LidarRayBlockListener
  public final void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (lidarRayBlockEvent.dimensions != 3)
      throw new RuntimeException("dim=" + lidarRayBlockEvent.dimensions);
    // ---
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    tensorReceived(Tensors.vector(i -> Tensors.of( //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get()), //
        DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size()));
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor points) {
    process(points);
  }

  public final void process(Tensor points) {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent;
    if (LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent)) {
      erodableMap.setReference(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      // TODO JPH lidar offset missing
      for (Tensor point : points) { // point x, y, z
        boolean isObstacle = spacialXZObstaclePredicate.isObstacle(point); // only x and z are used
        erodableMap.setPixel(point, isObstacle);
      }
    }
  }

  /** @param radius 0 means no erosion
   * @return */
  public final BufferedImageRegion erodedMap(int radius) {
    return erodableMap.erodedRegion(radius);
  }
}
