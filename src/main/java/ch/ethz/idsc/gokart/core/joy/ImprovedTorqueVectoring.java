// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clip;

public class ImprovedSimpleTorqueVectoring {
  private static final Scalar MIN = RealScalar.of(-1);
  private static final Scalar MAX = RealScalar.of(+1);
  // ---
  private final TorqueVectoringConfig torqueVectoringConfig;

  public ImprovedSimpleTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  /** @param expectedRotationPerMeterDriven with unit m^-1
   * @param meanTangentSpeed with unit m*s^-1
   * @param angularSlip with unit s^-1
   * @param power unitless in the interval [-1, 1]
   * @param real rotation taken from gyro with unit s^-1
   * @return vector of the form {powerLeft, powerRight} where both
   * powerLeft and powerRight are guaranteed to be in the interval [-1, 1] */
  public Tensor powers(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar power, Scalar realRotation) {
    // compute differential torque (in Arms as we do not use the power function yet)
    Scalar dynamicComponent = angularSlip.multiply(torqueVectoringConfig.dynamicCorrection);
    // System.out.println("Dynamic component: " + dynamicComponent);
    Scalar lateralAcceleration = Times.of(expectedRotationPerMeterDriven, meanTangentSpeed, meanTangentSpeed);
    // System.out.println("lateral Acceleration: " + lateralAcceleration);
    Scalar staticComponent = lateralAcceleration.multiply(torqueVectoringConfig.staticCompensation);
    // System.out.println("Static component: " + staticComponent);
    Scalar wantedZTorque = dynamicComponent.add(staticComponent); // One
    // do we want to break the back axle lose?
    // if we want to stabilise an oversteering gokart, we should have no differential thrust
    // do we want to stabilise?
    if (Scalars.lessThan(realRotation.multiply(wantedZTorque), Quantity.of(0, SI.PER_SECOND))) {
      Scalar ks = Quantity.of(10, SI.SECOND);
      Scalar stabilizerFactor = Quantity.of(1, SI.ONE).subtract(Clip.absoluteOne().apply(realRotation.abs().multiply(ks)));
      wantedZTorque = stabilizerFactor.multiply(stabilizerFactor);
    }
    // System.out.println("ZTorque: " + wantedZTorque);
    // left and right power
    Scalar powerLeft = power.subtract(wantedZTorque); // One
    Scalar powerRight = power.add(wantedZTorque); // One
    // prefer power over Z-torque
    // powerRight = powerRight.add(Clip.absoluteOne().apply(powerLeft).subtract(powerLeft));
    if (Scalars.lessThan(MAX, powerRight)) {
      Scalar overpower = powerRight.subtract(MAX);
      powerRight = MAX;
      powerLeft = powerLeft.add(overpower);
    } else //
    if (Scalars.lessThan(MAX, powerLeft)) {
      Scalar overpower = powerLeft.subtract(MAX);
      powerLeft = MAX;
      powerRight = powerRight.add(overpower);
    } else //
    if (Scalars.lessThan(powerRight, MIN)) {
      Scalar underPower = powerRight.subtract(MIN);
      powerRight = MIN;
      powerLeft = powerLeft.add(underPower);
    } else //
    if (Scalars.lessThan(powerLeft, MIN)) {
      Scalar underPower = powerLeft.subtract(MIN);
      powerLeft = MIN;
      powerRight = powerRight.add(underPower);
    }
    return Tensors.of(powerLeft, powerRight).map(Clip.absoluteOne());
  }
}
