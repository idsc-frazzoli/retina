// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.data.IntervalClock;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

// TODO MH cleanup comments/unused code
public class SimpleVelocityEstimation implements VelocityEstimation, Vmu931ImuFrameListener, GokartPoseListener {
  private static final Clip CLIP_TIME = Clip.function(Quantity.of(0, SI.SECOND), Quantity.of(0.1, SI.SECOND));
  // ---
  private final IntervalClock intervalClockLidar = new IntervalClock();
  // private final IntervalClock intervalClockIMU = new IntervalClock();

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent getEvent) {
    measurePose(getEvent.getPose());
  }

  private Tensor lastPosition = null;
  private Scalar angularVelocity = Quantity.of(0, SI.ANGULAR_ACCELERATION);
  private int lastVmuTime = 0;
  Tensor velocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  // private long lastReset = 0;

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]} */
  void measurePose(Tensor pose) {
    measurePose(pose, Quantity.of(intervalClockLidar.seconds(), SI.SECOND));
  }

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]}
   * @param deltaT [s] */
  void measurePose(Tensor pose, Scalar deltaT) {
    Tensor position = Tensors.of(pose.Get(0), pose.Get(1));
    Scalar orientation = pose.Get(2);
    // TODO JPH how do we do this without null
    if (lastPosition != null) {
      Tensor differenceToLast = position.subtract(lastPosition);
      Tensor lidarSpeed = getCompensationRotationMatrix(orientation) //
          .dot(differenceToLast) //
          .divide(deltaT);
      Scalar newFactor = VelocityEstimationConfig.GLOBAL.correctionFactor;
      Scalar oldFactor = RealScalar.ONE.subtract(newFactor);
      velocity = lidarSpeed.multiply(newFactor).add(velocity.multiply(oldFactor));
      // System.out.println("new factor: "+newFactor+" delta T: "+deltaT);
      // System.out.println("pose: "+pose+" Velocity: "+ velocity);
    }
    lastPosition = position;
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
   * @param accelerations {x[m/s^2], y[m/s^2]}
   * @param angularVelocity {x[1/s]}
   * @param deltaT [s] */
  void measureAcceleration(Tensor accelerations, Scalar angularVelocity, Scalar deltaT) {
    this.angularVelocity = angularVelocity;
    Scalar rdt = angularVelocity.multiply(deltaT);
    // transform old system (compensate for rotation)
    Tensor vel = velocity.add(Cross.of(velocity).multiply(rdt.negate()));
    // Tensors.of(vx, vy);
    // System.out.println("Acc: "+accelerations);
    this.velocity = vel.add(accelerations.multiply(deltaT));
    // if(System.currentTimeMillis()-lastReset>10000)
    // {
    // lastReset = System.currentTimeMillis();
    // this.velocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
    // }
  }

  private static Tensor getCompensationRotationMatrix(Scalar orientation) {
    return RotationMatrix.of(orientation.negate());
  }

  @Override // from VelocityEstimation
  public Tensor getVelocity() {
    return velocity.copy().append(angularVelocity);
  }

  @Override // from Vmu931ImuFrameListener
  public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
    Tensor acc = SensorsConfig.GLOBAL.getXYacc(vmu931ImuFrame);
    Scalar gyro = SensorsConfig.GLOBAL.getGyroZ(vmu931ImuFrame);
    int currentTime = vmu931ImuFrame.timestamp_ms();
    Scalar time = Quantity.of((currentTime - lastVmuTime) * 1e-3, SI.SECOND);
    lastVmuTime = currentTime;
    measureAcceleration(acc, gyro, CLIP_TIME.apply(time));
  }
}
