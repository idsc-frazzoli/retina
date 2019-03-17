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
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;

/** match the most recent lidar scan to static geometry of a pre-recorded map
 * the module runs a separate thread. on a standard pc the matching takes 0.017[s] on average */
public class LidarLocalizationModule extends AbstractModule implements //
    LidarRayBlockListener, DavisImuFrameListener, Runnable, GokartPoseInterface, //
    PositionVelocityEstimation {
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Vmu931Odometry vmu931Odometry = new Vmu931Odometry(SensorsConfig.getPlanarVmu931Imu());
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final LidarGyroLocalization lidarGyroLocalization = LocalizationConfig.getLidarGyroLocalization();
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
    davisImuLcmClient.addListener(this);
    davisImuLcmClient.startSubscriptions();
    // ---
    vmu931ImuLcmClient.addListener(vmu931Odometry);
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
    davisImuLcmClient.stopSubscriptions();
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
  private GeodesicIIR1Filter davis_gyroZ_filter = new GeodesicIIR1Filter(RnGeodesic.INSTANCE, RealScalar.of(0.03));
  private Scalar davis_gyroZ = Quantity.of(0.0, SI.PER_SECOND);

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    davis_gyroZ = davis_gyroZ_filter.apply(SensorsConfig.GLOBAL.davisGyroZ(davisImuFrame)).Get();
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

  private void fit(Tensor points) {
    lidarGyroLocalization.setState(getPose());
    Optional<SlamResult> optional = lidarGyroLocalization.handle(davis_gyroZ, points);
    if (optional.isPresent()) {
      SlamResult slamResult = optional.get();
      quality = slamResult.getMatchRatio();
      Scalar rescale = Clips.interval(0.5, 0.8).rescale(quality);
      // System.out.println(rescale);
      vmu931Odometry.inertialOdometry.blendPose(slamResult.getTransform(), rescale);
      // TODO blend velocity
    } else {
      quality = Clips.unit().apply(quality.subtract(RealScalar.of(0.05)));
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
    System.out.println("reset pose=" + pose.map(Round._5));
    vmu931Odometry.inertialOdometry.resetPose(pose);
    quality = RealScalar.ONE;
  }

  @Override
  public Tensor getVelocity() {
    return vmu931Odometry.inertialOdometry.getVelocity();
  }
}
