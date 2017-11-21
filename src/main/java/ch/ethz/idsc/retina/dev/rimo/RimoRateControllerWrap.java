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

  public Optional<RimoPutEvent> iterate(Tensor pair) {
    if (Objects.nonNull(rimoGetEvent))
      try {
        short valueL = 0;
        short valueR = 0;
        {
          Scalar vel_targetL = pair.Get(0);
          Scalar vel_error = vel_targetL.subtract(rimoGetEvent.getTireL.getAngularRate());
          Scalar torque = piL.iterate(vel_error);
          valueL = ARMS.apply(torque).number().shortValue();
        }
        {
          Scalar vel_targetR = pair.Get(1);
          Scalar vel_error = vel_targetR.subtract(rimoGetEvent.getTireR.getAngularRate());
          Scalar torque = piR.iterate(vel_error);
          valueR = ARMS.apply(torque).number().shortValue();
        }
        return Optional.of(new RimoPutEvent( //
            new RimoPutTire(RimoPutTire.OPERATION, (short) 0, valueL), //
            new RimoPutTire(RimoPutTire.OPERATION, (short) 0, valueR)));
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      }
    return Optional.empty();
  }
}
