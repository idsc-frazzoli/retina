// code by mh, jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Tan;

// TODO JPH lot's of improvements possible: see where AngularSlip is used currently
public class BicycleAngularSlip {
  private final Scalar xAxleRtoF;

  public BicycleAngularSlip(Scalar xAxleRtoF) {
    if (Scalars.isZero(xAxleRtoF))
      throw TensorRuntimeException.of(xAxleRtoF);
    this.xAxleRtoF = xAxleRtoF;
  }

  /** @param theta steering angle of imaginary front wheel
   * @return scalar with unit m^-1 */
  public Scalar rotationPerMeterDriven(Scalar theta) {
    // theta has interpretation in rad/m but is encoded in true SI units: "m^-1"
    return Tan.FUNCTION.apply(theta).divide(xAxleRtoF); // m^-1
  }

  /** @param theta steering angle of imaginary front wheel
   * @param tangentSpeed with unit m*s^-1
   * @return scalar with unit s^-1 */
  public Scalar wantedRotationRate(Scalar theta, Scalar tangentSpeed) {
    // compute wanted motor torques / no-slip behavior (sorry jan for corrective factor)
    return rotationPerMeterDriven(theta).multiply(tangentSpeed); // unit s^-1
  }

  /** @param theta steering angle of imaginary front wheel
   * @param tangentSpeed with unit m*s^-1
   * @param gyroZ with unit s^-1
   * @return */
  public Scalar of(Scalar theta, Scalar tangentSpeed, Scalar gyroZ) {
    // compute (negative) angular slip
    return wantedRotationRate(theta, tangentSpeed).subtract(gyroZ);
  }
}
