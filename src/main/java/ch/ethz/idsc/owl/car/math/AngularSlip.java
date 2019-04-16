// code by mh, jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Tan;

// TODO JPH lot's of improvements possible: see where AngularSlip is used currently
public enum AngularSlip {
  ;
  /** @param theta steering angle of imaginary front wheel
   * @param xAxleRtoF with unit m
   * @param gyroZ with unit s^-1
   * @param tangentSpeed with unit m*s^-1
   * @return */
  public static Scalar of(Scalar theta, Scalar xAxleRtoF, Scalar gyroZ, Scalar tangentSpeed) {
    // theta has interpretation in rad/m but is encoded in true SI units: "m^-1"
    Scalar rotationPerMeterDriven = Tan.FUNCTION.apply(theta).divide(xAxleRtoF); // m^-1
    // compute wanted motor torques / no-slip behavior (sorry jan for corrective factor)
    Scalar wantedRotationRate = rotationPerMeterDriven.multiply(tangentSpeed); // unit s^-1
    // compute (negative) angular slip
    return wantedRotationRate.subtract(gyroZ);
  }
}
