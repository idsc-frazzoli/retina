// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum VelocityHelper {
  ;
  public static final Tensor ZERO = attachUnits(Tensors.vector(0.0, 0.0, 0.0)).unmodifiable();

  /** @param velocity of the form {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]}
   * @return {vx, vy, gyroZ} */
  public static Tensor toUnitless(Tensor velocity) {
    return Tensors.of( //
        Magnitude.VELOCITY.apply(velocity.Get(0)), //
        Magnitude.VELOCITY.apply(velocity.Get(1)), //
        Magnitude.PER_SECOND.apply(velocity.Get(2)));
  }

  /** @param vector {vx, vy, gyroZ}
   * @return {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]} */
  public static Tensor attachUnits(Tensor vector) {
    return Tensors.of( //
        Quantity.of(vector.Get(0), SI.VELOCITY), //
        Quantity.of(vector.Get(1), SI.VELOCITY), //
        Quantity.of(vector.Get(2), SI.PER_SECOND));
  }
}
