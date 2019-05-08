// code by mh, jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Scalar;

public enum BicycleAngularSlip {
  ;
  /** @param tangentSpeed with unit [m*s^-1]
   * @param ratio of turning desired [m^-1]
   * @param gyroZ with unit [s^-1]
   * @return */
  public static AngularSlip getAngularSlip(Scalar tangentSpeed, Scalar ratio, Scalar gyroZ) {
    return new AngularSlip(tangentSpeed, ratio, gyroZ);
  }
}
