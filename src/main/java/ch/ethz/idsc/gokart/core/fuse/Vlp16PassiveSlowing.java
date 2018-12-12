// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.sys.SafetyCritical;
import ch.ethz.idsc.retina.util.data.PenaltyTimeout;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
@SafetyCritical
public final class Vlp16PassiveSlowing extends Vlp16ClearanceModule {
  private final PenaltyTimeout penaltyTimeout = new PenaltyTimeout(0.1);

  @Override // from Vlp16ClearanceModule
  Optional<RimoPutEvent> penaltyAction() {
    return penaltyTimeout.isPenalty() //
        ? Optional.empty()
        : StaticHelper.OPTIONAL_RIMO_PASSIVE;
  }

  /** function may be called during manual operation of the
   * gokart in order to test hi-performance maneuvers */
  public void bypassSafety() {
    penaltyTimeout.flagPenalty();
  }
}
