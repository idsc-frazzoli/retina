// code by jph
package ch.ethz.idsc.owly.car.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** class determines the no-slip velocity for tires at an offset from the center of
 * the rear axis depending on the steering angle of the tires at the front axis */
public class DifferentialSpeed {
  private final Scalar factor;

  /** @param x_front non-zero distance from rear to front axis where the steering is assumed to take place
   * @param y_offset distance from center of rear axis to tire */
  public DifferentialSpeed(Scalar x_front, Scalar y_offset) {
    if (Scalars.isZero(x_front))
      throw TensorRuntimeException.of(x_front, y_offset);
    factor = y_offset.divide(x_front);
  }

  /** @param v speed of vehicle at center of front axis along the direction of steering
   * @param beta turn angle at center of front axis, beta == 0 for driving straight
   * @return speed at y_offset from center of rear axis */
  public Scalar get(Scalar v, Scalar beta) {
    Scalar cos = Cos.FUNCTION.apply(beta);
    Scalar sin = Sin.FUNCTION.apply(beta);
    return v.multiply(cos.subtract(factor.multiply(sin)));
  }
}
