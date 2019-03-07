// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
/* package */ final class Vlp16ActiveSlowing extends Vlp16ClearanceModule {
  private static final Scalar SPEED_ZERO = Quantity.of(0, "rad*s^-1");
  /** RimoRateControllerWrap has to be instance of {@link RimoRateControllerUno}
   * as the steering angle is not provided */
  private final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();

  @Override // from Vlp16ClearanceModule
  void protected_first() {
    RimoSocket.INSTANCE.addGetListener(rimoRateControllerWrap);
  }

  @Override // from Vlp16ClearanceModule
  void protected_last() {
    RimoSocket.INSTANCE.removeGetListener(rimoRateControllerWrap);
  }

  @Override // from Vlp16ClearanceModule
  Optional<RimoPutEvent> penaltyAction() {
    // steering angle not used in RimoRateControllerUno
    return Optional.of(rimoRateControllerWrap.iterate(SPEED_ZERO, null).orElse(RimoPutEvent.PASSIVE));
  }
}
