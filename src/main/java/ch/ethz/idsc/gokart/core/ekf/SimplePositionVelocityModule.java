// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
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
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Mod;

// TODO MH cleanup comments/unused code
// TODO JPH refactor
public class SimplePositionVelocityModule extends AbstractModule implements //
    Vmu931ImuFrameListener, GokartPoseListener, PositionVelocityEstimation {
  private static final Scalar MIN_DRIFT_VELOCITY = Quantity.of(1, SI.VELOCITY);
  private static final Clip CLIP_TIME = Clips.interval(Quantity.of(0, SI.SECOND), Quantity.of(0.1, SI.SECOND));
  // ---
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final IntervalClock intervalClockLidar = new IntervalClock();
  private Tensor lastPosition = null;
  private Scalar angularVelocity = Quantity.of(0, SI.PER_SECOND);
  private int lastVmuTime = 0;
  /* package for testing */
  Tensor filteredVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  Tensor filteredPose = GokartPoseHelper.attachUnits(Tensors.vector(0, 0, 0));
  // private long lastReset = 0;
  private GokartPoseEvent lidar_prev = null;

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    Tensor acc = SensorsConfig.GLOBAL.vmu931AccXY(vmu931ImuFrame);
    Scalar gyro = SensorsConfig.GLOBAL.vmu931GyroZ(vmu931ImuFrame);
    int currentTime = vmu931ImuFrame.timestamp_ms();
    Scalar time = Quantity.of((currentTime - lastVmuTime) * 1e-3, SI.SECOND);
    lastVmuTime = currentTime;
    measureAcceleration(acc, gyro, CLIP_TIME.apply(time));
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    Scalar delta_time = Min.of( //
        Quantity.of(intervalClockLidar.seconds(), SI.SECOND), //
        Quantity.of(0.03, SI.SECOND)); // 1/50 == 0.02 is nominal
    if (LidarLocalizationModule.TRACKING) {
      if (Objects.nonNull(lidar_prev)) {
        lastPosition = lidar_prev.getPose().extract(0, 2);
        filteredPose = gokartPoseEvent.getPose();
        measurePose(gokartPoseEvent, delta_time);
      }
    } else
      filteredVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
    // ---
    lidar_prev = LidarLocalizationModule.TRACKING // TODO magic const
        && Scalars.lessThan(RealScalar.of(.2), gokartPoseEvent.getQuality()) //
            ? gokartPoseEvent
            : null;
  }

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
      filteredVelocity = RnGeodesic.INSTANCE.split(filteredVelocity, lidarSpeed, VelocityEstimationConfig.GLOBAL.velocityCorrectionFactor);
      // System.out.println("new factor: "+newFactor+" delta T: "+deltaT);
      // System.out.println("pose: "+pose+" Velocity: "+ velocity);
    }
    lastPosition = position;
    // correct filtered Pose
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
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());

  /** take new acceleration measurement into account
   * 
   * @param accelerations {x[m/s^2], y[m/s^2]}
   * @param angularVelocity {x[1/s]}
   * @param deltaT [s] */
  /* package for testing */
  void measureAcceleration(Tensor accelerations, Scalar angularVelocity, Scalar deltaT) {
    this.angularVelocity = (Scalar) RnGeodesic.INSTANCE.split(this.angularVelocity, angularVelocity, VelocityEstimationConfig.GLOBAL.rotFilter);
    Scalar rdt = angularVelocity.multiply(deltaT);
    // transform old system (compensate for rotation)
    Tensor vel = filteredVelocity.add(Cross.of(filteredVelocity).multiply(rdt.negate()));
    // Tensors.of(vx, vy);
    // System.out.println("Acc: "+accelerations);
    this.filteredVelocity = vel.add(accelerations.multiply(deltaT));
    // integrate pose
    Tensor currentVelocity = getVelocity();
    filteredPose = Se2CoveringIntegrator.INSTANCE.spin(filteredPose, currentVelocity.multiply(deltaT));
    filteredPose.set(MOD_DISTANCE, 2);
  }

  @Override // from VelocityEstimation
  public Tensor getVelocity() {
    return filteredVelocity.copy().append(angularVelocity);
  }

  public Tensor getXYVelocity() {
    return filteredVelocity.copy();
  }

  public Scalar getDrift() {
    if (Scalars.lessThan(filteredVelocity.Get(0), MIN_DRIFT_VELOCITY))
      return RealScalar.ZERO;
    return filteredVelocity.Get(1).divide(filteredVelocity.Get(0));
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
