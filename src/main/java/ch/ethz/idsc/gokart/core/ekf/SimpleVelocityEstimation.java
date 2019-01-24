package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.time.IntervalClock;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;

public class SimpleVelocityEstimation extends AbstractModule {
  Tensor velocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  Tensor lastPosition = null;
  Scalar AngularVelocity = Quantity.of(0, SI.ANGULAR_ACCELERATION);
  private final IntervalClock intervalClockLidar = new IntervalClock();
  private final IntervalClock intervalClockIMU = new IntervalClock();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final GokartPoseListener gokartPoseListener = new GokartPoseListener() {
    @Override
    public void getEvent(GokartPoseEvent getEvent) {
      measurePosition(getEvent.getPose());
    }
  };
  
  //TODO: listen to accelerometer

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]} */
  public void measurePosition(Tensor pose) {
    measurePosition(pose, Quantity.of(intervalClockLidar.seconds(), SI.SECOND));
  }

  /** take new lidar pose into account
   * @param pose measured lidar pose: {x[m], y[m], angle[]}
   * @param deltaT [s] */
  public void measurePosition(Tensor pose, Scalar deltaT) {
    Tensor position = Tensors.of(pose.Get(0), pose.Get(1));
    Scalar orientation = pose.Get(2);
    // TODO: how do we do this without null
    if (lastPosition != null) {
      Tensor differenceToLast = position//
          .subtract(lastPosition);
      Tensor lidarSpeed = getCompensationRotationMatrix(orientation)//
          .dot(differenceToLast)//
          .divide(Quantity.of(deltaT, SI.SECOND));
      Scalar newFactor = VelocityEstimationConfig.GLOBAL.correctionFactor;
      Scalar oldFactor = RealScalar.ONE.subtract(newFactor);
      velocity = lidarSpeed.multiply(newFactor).add(velocity.multiply(oldFactor));
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
    this.AngularVelocity = angularVelocity;
    Scalar rdt = angularVelocity.multiply(deltaT);
    // transform old system (compensate for rotation)
    Scalar vx = velocity.Get(0).add(velocity.Get(1).multiply(rdt));
    Scalar vy = velocity.Get(1).subtract(velocity.Get(0).multiply(rdt));
    Tensor vel = Tensors.of(vx, vy);
    this.velocity = vel.add(accelerations.multiply(deltaT));
  }

  private Tensor getCompensationRotationMatrix(Scalar orientation) {
    return RotationMatrix.of(orientation.negate());
  }

  @Override
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    gokartPoseLcmClient.stopSubscriptions();
  }
}
