// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
public final class Vlp16PassiveSlowing extends Vlp16ClearanceModule {
  @Override // from Vlp16ClearanceModule
  RimoPutEvent penaltyAction() {
    return RimoPutEvent.PASSIVE;
  }
}
