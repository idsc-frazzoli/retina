// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.ekf.PositionVelocityEstimation;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;

/** match the most recent lidar scan to static geometry of a pre-recorded map
 * the module runs a separate thread. on a standard pc the matching takes 0.017[s] on average */
// TODO JPH split class in two classes
public class LidarLocalizationModule extends AbstractModule implements //
    LidarRayBlockListener, Vmu931ImuFrameListener, //
    Runnable, GokartPoseInterface, PositionVelocityEstimation, RimoGetListener {
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  private static final LidarGyroLocalization LIDAR_GYRO_LOCALIZATION = //
      LidarGyroLocalization.of(LocalizationConfig.getPredefinedMap());
  // ---
  // private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Vmu931Odometry vmu931Odometry = new Vmu931Odometry(SensorsConfig.getPlanarVmu931Imu());
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final Thread thread = new Thread(this);
  // ---
  private boolean tracking = false;
  private boolean flagSnap = false;
  /** tear down flag to stop thread */
  private boolean isLaunched = true;
  /** points_ferry is null or a matrix with dimension Nx2
   * containing the cross-section of the static geometry
   * with the horizontal plane at height of the lidar */
  private Tensor points2d_ferry = null;
  private Scalar quality = RealScalar.ZERO;

  /** @return */
  public boolean isTracking() {
    return tracking;
  }

  /** @param tracking */
  public void setTracking(boolean tracking) {
    this.tracking = tracking;
  }

  /** flag snap */
  public void flagSnap() {
    flagSnap = true;
  }

  @Override // from AbstractModule
  protected void first() {
    // davisImuLcmClient.addListener(this);
    // davisImuLcmClient.startSubscriptions();
    // ---
    vmu931ImuLcmClient.addListener(this);
    vmu931ImuLcmClient.startSubscriptions();
    // ---
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    lidarAngularFiringCollector.addListener(this);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarSpacialProvider);
    vlp16LcmHandler.velodyneDecoder.addRayListener(lidarRotationProvider);
    // ---
    vlp16LcmHandler.startSubscriptions();
    thread.start();
  }

  @Override // from AbstractModule
  protected void last() {
    isLaunched = false;
    thread.interrupt();
    vlp16LcmHandler.stopSubscriptions();
    // davisImuLcmClient.stopSubscriptions();
    vmu931ImuLcmClient.stopSubscriptions();
  }

  @Override // from LidarRayBlockListener
  public synchronized void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) { // receive 2D block event
    if (flagSnap || tracking) {
      flagSnap = false;
      FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
      points2d_ferry = Tensors.vector(i -> Tensors.of( //
          DoubleScalar.of(floatBuffer.get()), //
          DoubleScalar.of(floatBuffer.get())), lidarRayBlockEvent.size());
      thread.interrupt();
    }
  }

  /***************************************************/
  // TODO JPH magic const (1 - 0.03)^50 == 0.218065
  private GeodesicIIR1Filter gyroZ_filter = new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(0.1));
  private Scalar gyroZ_filtered = Quantity.of(0.0, SI.PER_SECOND);
  private final Tensor gyroZ_array = Array.of(l -> Quantity.of(0.0, SI.PER_SECOND), 50);
  private int index = 0;

  // @Override // from DavisImuFrameListener
  // public void imuFrame(DavisImuFrame davisImuFrame) {
  // davis_gyroZ = davis_gyroZ_filter.apply(SensorsConfig.GLOBAL.davisGyroZ(davisImuFrame)).Get();
  // gyroZ.set(SensorsConfig.GLOBAL.davisGyroZ(davisImuFrame), index);
  // ++index;
  // index %= gyroZ.length();
  // }
  /** @return */
  Scalar getGyroZ() {
    return gyroZ_filtered;
    // return Mean.of(gyroZ_array).Get();
  }

  // DelayedQueue<Vmu931ImuFrame> delayedQueue = new DelayedQueue<>(0);
  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    // Optional<Vmu931ImuFrame> optional = delayedQueue.push(vmu931ImuFrame);
    // if (optional.isPresent()) {
    // vmu931Odometry.vmu931ImuFrame(optional.get());
    // } else
    // System.out.println("skip");
    vmu931Odometry.vmu931ImuFrame(vmu931ImuFrame);
    Scalar vmu931_gyroZ = vmu931Odometry.inertialOdometry.getVelocity().Get(2);
    gyroZ_filtered = gyroZ_filter.apply(vmu931_gyroZ).Get();
    gyroZ_array.set(vmu931_gyroZ, index);
    ++index;
    index %= gyroZ_array.length();
  }

  /***************************************************/
  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    Scalar odometryTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
  }

  /***************************************************/
  @Override // from Runnable
  public void run() {
    while (isLaunched) {
      Tensor points = points2d_ferry;
      if (Objects.nonNull(points)) {
        points2d_ferry = null;
        fit(points);
      } else
        try {
          Thread.sleep(2000); // is interrupted once data arrives
        } catch (Exception exception) {
          // ---
        }
    }
  }

  private SlamResult prevResult = null;

  public void fit(Tensor points) {
    Optional<SlamResult> optional = LIDAR_GYRO_LOCALIZATION.handle(getPose(), getGyroZ(), points);
    if (optional.isPresent()) {
      SlamResult slamResult = optional.get();
      quality = slamResult.getMatchRatio();
      boolean matchOk = Scalars.lessThan(RealScalar.of(.7), quality);
      if (matchOk) {
        // blend pose
        Scalar blend = RealScalar.of(0.1);
        vmu931Odometry.inertialOdometry.blendPose(slamResult.getTransform(), blend);
        if (Objects.nonNull(prevResult)) {
          // blend velocity
          Tensor velXY = LIE_DIFFERENCES.pair( //
              prevResult.getTransform(), //
              slamResult.getTransform()) //
              .extract(0, 2).multiply(SensorsConfig.GLOBAL.vlp16_rate);
          // System.out.println("---");
          // System.out.println(vmu931Odometry.inertialOdometry.getVelocity().map(Round._4));
          // System.out.println(velXY.map(Round._4));
          // TODO JPH/MH magic const
          vmu931Odometry.inertialOdometry.blendVelocity(velXY, RealScalar.of(0.01));
        }
        prevResult = slamResult;
      } else
        prevResult = null;
    } else {
      quality = Clips.unit().apply(quality.subtract(RealScalar.of(0.05)));
      prevResult = null;
    }
  }

  /***************************************************/
  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return vmu931Odometry.inertialOdometry.getPose();
  }

  public GokartPoseEvent createPoseEvent() {
    return GokartPoseEvents.getPoseEvent(getPose(), quality);
  }

  /** function called when operator initializes pose
   * 
   * @param pose */
  public void resetPose(Tensor pose) {
    // System.out.println("reset pose=" + pose.map(Round._5));
    vmu931Odometry.inertialOdometry.resetPose(pose);
    quality = RealScalar.ONE;
    prevResult = null;
  }

  @Override
  public Tensor getVelocity() {
    return vmu931Odometry.inertialOdometry.getVelocity();
  }

  /** @return with unit s^-1 */
  public Scalar getGyroZFiltered() {
    return gyroZ_filtered;
  }
}
