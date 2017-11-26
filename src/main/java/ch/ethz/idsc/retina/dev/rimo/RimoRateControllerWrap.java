// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** the controller has to be subscribed to rimo get events */
public class RimoRateControllerWrap implements RimoGetListener {
  private static final ScalarUnaryOperator ARMS = QuantityMagnitude.singleton(RimoPutTire.UNIT_TORQUE);
  // ---
  private final RimoRateController piL = new RimoRateController();
  private final RimoRateController piR = new RimoRateController();
  private RimoGetEvent rimoGetEvent = null;

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  /** @param pair vector of length == 2 with desired angular rates on the left and right rear wheel
   * @return */
  public Optional<RimoPutEvent> iterate(Tensor pair) {
    if (Objects.nonNull(rimoGetEvent))
      try {
        short armsL_raw = 0;
        short armsR_raw = 0;
        {
          Scalar vel_targetL = pair.Get(0);
          Scalar vel_error = vel_targetL.subtract(rimoGetEvent.getTireL.getAngularRate_Y());
          Scalar torque = piL.iterate(vel_error);
          short valueL_Yaxis = ARMS.apply(torque).number().shortValue();
          armsL_raw = (short) -valueL_Yaxis; // negative sign LEFT
        }
        {
          Scalar vel_targetR = pair.Get(1);
          Scalar vel_error = vel_targetR.subtract(rimoGetEvent.getTireR.getAngularRate_Y());
          Scalar torque = piR.iterate(vel_error);
          short valueR_Yaxis = ARMS.apply(torque).number().shortValue();
          armsR_raw = (short) +valueR_Yaxis; // positive sign RIGHT
        }
        return Optional.of(new RimoPutEvent( //
            new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
            new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
        ));
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      }
    return Optional.empty();
  }
}
