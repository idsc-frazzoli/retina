// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum VelocityHelper {
  ;
  /** @param velocity of the form {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]}
   * @return {vx, vy, gyroZ} */
  public static Tensor toUnitless(Tensor velocity) {
    return Tensors.of( //
        Magnitude.VELOCITY.apply(velocity.Get(0)), //
        Magnitude.VELOCITY.apply(velocity.Get(1)), //
        Magnitude.PER_SECOND.apply(velocity.Get(2)));
  }
}
