// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Imu;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.Refactor;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Mod;

// TODO MH cleanup comments/unused code
// TODO JPH refactor
@Refactor
public class SimplePositionVelocityModule extends AbstractModule implements //
    Vmu931ImuFrameListener, GokartPoseListener, PositionVelocityEstimation {
  private static final Scalar MIN_DRIFT_VELOCITY = Quantity.of(1, SI.VELOCITY);
  private static final Clip CLIP_TIME = Clips.interval(Quantity.of(0, SI.SECOND), Quantity.of(0.1, SI.SECOND));
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  // ---
  private final PlanarVmu931Imu planarVmu931Imu = SensorsConfig.getPlanarVmu931Imu();
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClock = new IntervalClock();
  private Tensor lastPosition = null;
  private Scalar angularVelocity = Quantity.of(0, SI.PER_SECOND);
  private int lastVmuTime = 0;
  /* package for testing */
  Tensor local_filteredVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  Tensor filteredPose = GokartPoseHelper.attachUnits(Tensors.vector(0, 0, 0));
  // private long lastReset = 0;
  private GokartPoseEvent lidar_prev = null;

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    Tensor local_acc = planarVmu931Imu.vmu931AccXY(vmu931ImuFrame);
    Scalar gyro = planarVmu931Imu.vmu931GyroZ(vmu931ImuFrame);
    int currentTime = vmu931ImuFrame.timestamp_ms();
    Scalar time = Quantity.of((currentTime - lastVmuTime) * 1e-3, SI.SECOND);
    lastVmuTime = currentTime;
    integrateImu(local_acc, gyro, CLIP_TIME.apply(time));
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    Scalar delta_time = Min.of( //
        Quantity.of(intervalClock.seconds(), SI.SECOND), //
        Quantity.of(0.03, SI.SECOND)); // 1/50 == 0.02 is nominal
    // FIXME JPH LOCAL
    // if (LidarLocalizationModule.TRACKING) {
    // // if (Scalars.lessThan(RealScalar.of(0.6), gokartPoseEvent.getQuality()))
    // // filteredPose = gokartPoseEvent.getPose();
    // if (Objects.nonNull(lidar_prev)) {
    // lastPosition = lidar_prev.getPose().extract(0, 2);
    // measurePose(gokartPoseEvent, delta_time);
    // }
    // } else
    local_filteredVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
    // ---
    lidar_prev = // FIXME JPH LOCAL
        // LidarLocalizationModule.TRACKING // TODO magic const
        // && Scalars.lessThan(RealScalar.of(.3), gokartPoseEvent.getQuality()) //
        // ? gokartPoseEvent
        // :
        null;
  }

  // private static LieDifferences lieDifferences = new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  int countPrint = 0;

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]}
   * @param deltaT [s] */
  public void measurePose(GokartPoseEvent gokartPoseEvent, Scalar deltaT) {
    Tensor newPose = gokartPoseEvent.getPose();
    Tensor position = newPose.extract(0, 2);
    Scalar orientation = newPose.Get(2);
    // TODO JPH how do we do this without null
    if (lastPosition != null) {
      Tensor differenceToLast = position.subtract(lastPosition);
      Tensor lidarSpeed = getCompensationRotationMatrix(orientation) //
          .dot(differenceToLast) //
          .divide(deltaT);
      local_filteredVelocity = RnGeodesic.INSTANCE.split(local_filteredVelocity, lidarSpeed, VelocityEstimationConfig.GLOBAL.velocityCorrectionFactor);
      // System.out.println("new factor: "+newFactor+" delta T: "+deltaT);
      // System.out.println("pose: "+pose+" Velocity: "+ velocity);
    }
    lastPosition = position;
    // correct filtered Pose
    if (++countPrint % 50 == 0) {
      // Tensor pair = lieDifferences.pair(filteredPose, newPose);
      // System.out.println("d=" + pair.map(Round._4));
    }
    filteredPose = Se2Geodesic.INSTANCE.split(filteredPose, newPose, VelocityEstimationConfig.GLOBAL.poseCorrectionFactor);
  }

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]} */
  private static Tensor getCompensationRotationMatrix(Scalar orientation) {
    return RotationMatrix.of(orientation.negate());
  }
  /** take new acceleration measurement into account
   * 
   * @param accelerations {x[m/s^2], y[m/s^2]}
   * @param angularVelocity {x[1/s]} */
  // void measureAcceleration(Tensor accelerations, Scalar angularVelocity) {
  // measureAcceleration(accelerations, angularVelocity, Quantity.of(intervalClockIMU.seconds(), SI.SECOND));
  // }

  /** take new acceleration measurement into account
   * 
   * @param local_acc {x[m*s^-2], y[m*s^-2]}
   * @param gyro with unit [s^-1]
   * @param deltaT [s] */
  /* package */ void integrateImu(Tensor local_acc, Scalar gyro, Scalar deltaT) {
    this.angularVelocity = (Scalar) RnGeodesic.INSTANCE.split(angularVelocity, gyro, VelocityEstimationConfig.GLOBAL.rotFilter);
    // transform old system (compensate for rotation)
    local_filteredVelocity = RotationMatrix.of(gyro.multiply(deltaT).negate()).dot(local_filteredVelocity).add(local_acc.multiply(deltaT));
    // integrate pose
    filteredPose = Se2CoveringIntegrator.INSTANCE.spin(filteredPose, getVelocity().multiply(deltaT));
    filteredPose.set(MOD_DISTANCE, 2);
  }

  @Override // from PositionVelocityEstimation
  public Tensor getVelocity() {
    return local_filteredVelocity.copy().append(angularVelocity);
  }

  public Tensor getXYVelocity() {
    return local_filteredVelocity.copy();
  }

  public Scalar getDrift() {
    if (Scalars.lessThan(local_filteredVelocity.Get(0), MIN_DRIFT_VELOCITY))
      return RealScalar.ZERO;
    return local_filteredVelocity.Get(1).divide(local_filteredVelocity.Get(0));
  }

  /** @return "s^-1" */
  public Scalar getGyroVelocity() {
    return angularVelocity;
  }

  @Override
  protected void first() {
    vmu931ImuLcmClient.addListener(this);
    gokartPoseLcmClient.addListener(this);
    // ---
    vmu931ImuLcmClient.startSubscriptions();
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    vmu931ImuLcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override
  public Tensor getPose() {
    return filteredPose.copy();
  }
}
