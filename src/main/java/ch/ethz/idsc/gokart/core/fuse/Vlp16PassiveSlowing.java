// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.sys.SafetyCritical;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
@SafetyCritical
public final class Vlp16PassiveSlowing extends Vlp16ClearanceModule {
  @Override // from Vlp16ClearanceModule
  Optional<RimoPutEvent> penaltyAction() {
    return StaticHelper.OPTIONAL_RIMO_PASSIVE;
  }
}
