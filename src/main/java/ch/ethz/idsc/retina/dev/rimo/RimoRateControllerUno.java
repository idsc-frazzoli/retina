// code by jph & ej
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** the controller has to be subscribed to rimo get events */
public class RimoRateControllerUno extends RimoRateControllerWrap {
  private final RimoRateController pi = new RimoRateController();

  @Override
  protected RimoPutEvent protected_getRimoPutEvent(Scalar vel_target, Scalar theta, RimoGetEvent rimoGetEvent) {
    Scalar vel_L = rimoGetEvent.getTireL.getAngularRate_Y();
    Scalar vel_R = rimoGetEvent.getTireR.getAngularRate_Y();
    Scalar vel_avg = vel_L.add(vel_R).multiply(RealScalar.of(0.5));
    Scalar vel_error = vel_target.subtract(vel_avg);
    Scalar torque = pi.iterate(vel_error);
    short value_Yaxis = RimoPutTire.MAGNITUDE_ARMS.apply(torque).number().shortValue();
    short armsL_raw = (short) -value_Yaxis;
    short armsR_raw = (short) +value_Yaxis;
    // System.out.println(pair + " -> " + armsL_raw + " " + armsR_raw);
    return new RimoPutEvent( //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
    );
  }
}
