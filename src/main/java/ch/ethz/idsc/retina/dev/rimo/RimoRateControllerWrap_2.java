// code by jph & ej
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** the controller has to be subscribed to rimo get events */
// TODO suggest better naming for this
public class RimoRateControllerWrap_2 implements RimoGetListener {
  private final RimoRateController pi = new RimoRateController();
  private RimoGetEvent rimoGetEvent = null;

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  /** @param pair vector of length == 2 with desired angular rates on the left and right rear wheel
   * @return */
  public Optional<RimoPutEvent> iterate(Scalar vel_target) {
    if (Objects.nonNull(rimoGetEvent))
      try {
        short armsL_raw = 0;
        short armsR_raw = 0;
        {
          Scalar vel_L = rimoGetEvent.getTireL.getAngularRate_Y();
          Scalar vel_R = rimoGetEvent.getTireR.getAngularRate_Y();
          Scalar vel_avg = vel_L.add(vel_R).multiply(RealScalar.of(0.5));
          Scalar vel_error = vel_target.subtract(vel_avg);
          Scalar torque = pi.iterate(vel_error);
          short value_Yaxis = RimoPutTire.MAGNITUDE_ARMS.apply(torque).number().shortValue();
          armsL_raw = (short) -value_Yaxis;
          armsR_raw = (short) +value_Yaxis;
        }
        // System.out.println(pair + " -> " + armsL_raw + " " + armsR_raw);
        return Optional.of(new RimoPutEvent( //
            new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
            new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
        ));
      } catch (Exception exception) {
        System.err.println("RRCW:" + exception.getMessage()); // message may be null
      }
    return Optional.empty();
  }
}
