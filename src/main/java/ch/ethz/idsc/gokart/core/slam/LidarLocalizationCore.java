// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseVelocityInterface;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;

/** localization algorithm 3rd gen. fusing of two sources of information:
 * 1) inertial measurements acc and gyro from VMU931 sensor at 1000[Hz], and
 * 2) lidar-based pose and velocity estimates at 20[Hz]
 * 
 * PoseVelocityInterface provides fused (and therefore filtered) pose and velocity */
public class LidarLocalizationCore implements //
    LidarRayBlockListener, Vmu931ImuFrameListener, Runnable, PoseVelocityInterface {
  /** the constant 0.1 was established in post-processing
   * with mh and jph to filter out spikes in the gyroZ signal */
  private static final Scalar IIR1_FILTER_GYROZ = RealScalar.of(0.1);
  private static final Scalar QUALITY_DECR = RealScalar.of(0.05);
  private static final Scalar BLEND_POSE = RealScalar.of(0.25);
  private static final Scalar BLEND_VELOCITY = RealScalar.of(0.04);
  // ---
  private static final Scalar _1 = DoubleScalar.of(1);
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  private static final LidarGyroLocalization LIDAR_GYRO_LOCALIZATION = //
      LidarGyroLocalization.of(LocalizationConfig.getPredefinedMap());
  // ---
  public final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  public final LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
  private final LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
  private final LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
  private final Vmu931Odometry vmu931Odometry = new Vmu931Odometry(SensorsConfig.getPlanarVmu931Imu());
  // ---
  private boolean tracking = false;
  private boolean flagSnap = false;
  /** tear down flag to stop thread */
  boolean isLaunched = false;
  /** points_ferry is null or a matrix with dimension Nx2
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  private Tensor points2d_ferry = null;
  /* package for testing */ Scalar quality = RealScalar.ZERO;
  private GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, IIR1_FILTER_GYROZ);
  private Scalar gyroZ_vmu931 = Quantity.of(0.0, SI.PER_SECOND);
  private Scalar gyroZ_filtered = Quantity.of(0.0, SI.PER_SECOND);
  final Thread thread = new Thread(this);

  public LidarLocalizationCore() {
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
  }

  /** @return */
  boolean isTracking() {
    return tracking;
  }

  /** @param tracking */
  public void setTracking(boolean tracking) {
    this.tracking = tracking;
  }

  /** flag snap */
  void flagSnap() {
    flagSnap = true;
  }

  @Override // from LidarRayBlockListener
  public synchronized void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) { // receive 2D block event
    if (flagSnap || tracking) {
      FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      points2d_ferry = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
      thread.interrupt();
    }
    if (!tracking) {
      vmu931Odometry.resetVelocity();
      quality = RealScalar.ZERO; // pose quality is zero when tracking is off
    }
  }

  /***************************************************/
  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    vmu931Odometry.vmu931ImuFrame(vmu931ImuFrame);
    gyroZ_vmu931 = vmu931Odometry.getGyroZ();
    gyroZ_filtered = geodesicIIR1Filter.apply(gyroZ_vmu931).Get();
  }

  /***************************************************/
  @Override // from Runnable
  public void run() {
    do {
      Tensor points = points2d_ferry; // only updated when tracking == true
      if (Objects.nonNull(points)) {
        points2d_ferry = null;
        fit(points);
      } else
        try {
          Thread.sleep(1000); // sleep is interrupted once data arrives
          // 1[s] of no lidar data indicates sensor failure
          quality = RealScalar.ZERO;
        } catch (Exception exception) {
          // ---
        }
    } while (isLaunched);
  }

  private GokartPoseEvent prevResult = null;

  private void fit(Tensor points) {
    Optional<GokartPoseEvent> optional = LIDAR_GYRO_LOCALIZATION.handle(getPose(), getVelocity(), points);
    if (optional.isPresent()) {
      GokartPoseEvent slamResult = optional.get();
      quality = slamResult.getQuality();
      boolean matchOk = LocalizationConfig.GLOBAL.isQualityOk(quality);
      if (matchOk || flagSnap) {
        // blend pose
        Scalar blend = flagSnap ? _1 : BLEND_POSE;
        vmu931Odometry.blendPose(slamResult.getPose(), blend);
        if (Objects.nonNull(prevResult)) {
          // blend velocity
          Tensor velXY = LIE_DIFFERENCES.pair( //
              prevResult.getPose(), //
              slamResult.getPose()) //
              .extract(0, 2).multiply(SensorsConfig.GLOBAL.vlp16_rate);
          vmu931Odometry.blendVelocity(velXY, BLEND_VELOCITY);
        }
        prevResult = slamResult;
        flagSnap = false;
      } else
        prevResult = null;
    } else {
      quality = Clips.unit().apply(quality.subtract(QUALITY_DECR));
      prevResult = null;
    }
  }

  /***************************************************/
  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return vmu931Odometry.getPose();
  }

  @Override // from PoseVelocityInterface
  public Tensor getVelocity() {
    return vmu931Odometry.velocityXY().append(gyroZ_filtered);
  }

  @Override // from PoseVelocityInterface
  public Scalar getGyroZ() {
    return gyroZ_filtered;
  }

  /** @return instance of GokartPoseEvent with current pose, quality and velocity */
  public GokartPoseEvent createPoseEvent() {
    return GokartPoseEvents.create(getPose(), quality, getVelocity());
  }

  /** function called when operator initializes pose
   * 
   * @param pose */
  public void resetPose(Tensor pose) {
    // System.out.println("reset pose=" + pose.map(Round._5));
    vmu931Odometry.resetPose(pose);
    prevResult = null;
  }

  /***************************************************/
  /** Hint: only use function in post-processing.
   * DO NOT use function during operation of the gokart
   * 
   * @return unfiltered gyroZ value with unit "s^-1" */
  public Scalar getGyroZ_vmu931() {
    return gyroZ_vmu931;
  }
}
