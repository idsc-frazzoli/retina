// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
public final class Vlp16PassiveSlowing extends Vlp16ClearanceModule {
  private final Optional<RimoPutEvent> optional = Optional.of(RimoPutEvent.PASSIVE);

  @Override // from Vlp16ClearanceModule
  Optional<RimoPutEvent> penaltyAction() {
    return optional;
  }
}
