// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.Tan;

public enum TurningGeometry {
  ;
  /** 0.01[rad] will result in ~100[m] offset for x_front == 1[m] */
  static final Scalar ANGLE_THRESHOLD = RealScalar.of(0.01);

  /** inverse function of {@link ChassisGeometry#steerAngleForTurningRatio(Scalar)}
   * 
   * @param x_front distance from rear to front axle
   * @param angle of steering
   * @return center of circle of rotation of a vehicle on the y-axis
   * except when the vehicle is headed straight, in which case
   * Optional.empty() is returned */
  public static Optional<Scalar> offset_y(Scalar x_front, Scalar angle) {
    if (Scalars.lessEquals(ANGLE_THRESHOLD, angle.abs()))
      return Optional.of(x_front.divide(Tan.FUNCTION.apply(angle)));
    return Optional.empty();
  }
}
