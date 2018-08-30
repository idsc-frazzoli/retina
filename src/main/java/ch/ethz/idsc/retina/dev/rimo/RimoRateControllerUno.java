// code by jph & ej
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.red.Mean;

/** the uno-rimo rate controller uses only a single PI-controller
 * that compares the average wheels rate with a target velocity.
 * the steering wheel angle is not used. */
public class RimoRateControllerUno extends RimoRateControllerWrap {
  private final RimoRateController pi = new SimpleRimoRateController(RimoConfig.GLOBAL);

  @Override // from RimoRateControllerWrap
  protected RimoPutEvent protected_getRimoPutEvent(Scalar rate_target, Scalar angle, RimoGetEvent rimoGetEvent) {
    Scalar vel_avg = Mean.of(rimoGetEvent.getAngularRate_Y_pair()).Get(); // average of wheel rates
    Scalar vel_error = rate_target.subtract(vel_avg);
    Scalar torque = pi.iterate(vel_error);
    short value_Yaxis = Magnitude.ARMS.toShort(torque);
    return RimoPutHelper.operationTorque( //
        (short) -value_Yaxis, // negative sign LEFT
        (short) +value_Yaxis // positive sign RIGHT
    );
  }
}
