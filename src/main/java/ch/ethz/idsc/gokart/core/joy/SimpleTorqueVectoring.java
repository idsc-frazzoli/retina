// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Clip;

public class SimpleTorqueVectoring implements TorqueVectoringInterface {
  final TorqueVectoringConfig torqueVectoringConfig;

  public SimpleTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  @Override
  public final Tensor powers(Scalar expectedRotationPerMeterDriven, Scalar meanTangentSpeed, Scalar angularSlip, Scalar power, Scalar realRotation) {
    // compute differential torque (in ARMS as we do not use the power function yet)
    Scalar dynamicComponent = angularSlip.multiply(torqueVectoringConfig.dynamicCorrection);
    Scalar lateralAcceleration = Times.of(expectedRotationPerMeterDriven, meanTangentSpeed, meanTangentSpeed);
    Scalar staticComponent = lateralAcceleration.multiply(torqueVectoringConfig.staticCompensation);
    // ---
    Scalar wantedZTorque = wantedZTorque( //
        dynamicComponent.add(staticComponent), // One
        realRotation);
    // left and right power prefer power over Z-torque
    return clip( //
        power.subtract(wantedZTorque), // unit one
        power.add(wantedZTorque) // unit one
    );
  }

  /** @param wantedZTorque
   * @param realRotation
   * @return */
  Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    return wantedZTorque; // simple implementation
  }

  private static final Scalar MAX = RealScalar.of(+1.0);
  private static final Scalar MIN = RealScalar.of(-1.0);

  /** @param powerLeft unitless
   * @param powerRight unitless
   * @return vector of length 2 with scalars in interval [-1, 1] */
  static Tensor clip(Scalar powerLeft, Scalar powerRight) {
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
