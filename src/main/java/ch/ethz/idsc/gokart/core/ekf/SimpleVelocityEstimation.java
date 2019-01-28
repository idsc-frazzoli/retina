// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.time.IntervalClock;
import ch.ethz.idsc.sophus.planar.Cross2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO JPH possibly does not extend abstract module? but StartAndStoppable
public class SimpleVelocityEstimation extends AbstractModule implements VelocityEstimation {
  private final IntervalClock intervalClockLidar = new IntervalClock();
  private final IntervalClock intervalClockIMU = new IntervalClock();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final GokartPoseListener gokartPoseListener = new GokartPoseListener() {
    @Override
    public void getEvent(GokartPoseEvent getEvent) {
      measurePose(getEvent.getPose());
    }
  };
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Vmu931ImuFrameListener vmu931ImuFrameListener = new Vmu931ImuFrameListener() {
    @Override
    public void vmu931ImuFrame(Vmu931ImuFrame vmu931ImuFrame) {
      Tensor acc = vmu931ImuFrame.accXY();
      Scalar gyro = (Scalar) vmu931ImuFrame.gyroZ();
      int currentTime = vmu931ImuFrame.timestamp_ms();
      Scalar time = Quantity.of((currentTime - lastVmuTime) / 1000.0, SI.SECOND);
      lastVmuTime = currentTime;
      if (Scalars.lessThan(Quantity.of(0, SI.SECOND), time))
        measureAcceleration(acc, gyro, time);
    }
  };
  private Tensor lastPosition = null;
  private Scalar angularVelocity = Quantity.of(0, SI.ANGULAR_ACCELERATION);
  private int lastVmuTime = 0;
  Tensor velocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  // private long lastReset = 0;

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]} */
  public void measurePose(Tensor pose) {
    measurePose(pose, Quantity.of(intervalClockLidar.seconds(), SI.SECOND));
  }

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]}
   * @param deltaT [s] */
  public void measurePose(Tensor pose, Scalar deltaT) {
    Tensor position = Tensors.of(pose.Get(0), pose.Get(1));
    Scalar orientation = pose.Get(2);
    // TODO: how do we do this without null
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
  public void measureAcceleration(Tensor accelerations, Scalar angularVelocity) {
    measureAcceleration(accelerations, angularVelocity, Quantity.of(intervalClockIMU.seconds(), SI.SECOND));
  }

  /** take new acceleration measurement into account
   * 
   * @param accelerations {x[m/s^2], y[m/s^2]}
   * @param angularVelocity {x[1/s]}
   * @param deltaT [s] */
  public void measureAcceleration(Tensor accelerations, Scalar angularVelocity, Scalar deltaT) {
    this.angularVelocity = angularVelocity;
    Scalar rdt = angularVelocity.multiply(deltaT);
    // transform old system (compensate for rotation)
    Tensor vel = velocity.add(Cross2D.of(velocity).multiply(rdt).negate());
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

  /**
   * 
   */
  @Override
  public Tensor getVelocity() {
    return velocity.copy().append(angularVelocity);
  }

  @Override
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    vmu931ImuLcmClient.addListener(vmu931ImuFrameListener);
    vmu931ImuLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    gokartPoseLcmClient.stopSubscriptions();
    vmu931ImuLcmClient.stopSubscriptions();
  }
}
