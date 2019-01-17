// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;

/** prevents acceleration if something is in the way
 * for instance when a person is entering or leaving the gokart */
public final class Vlp16PassiveSlowing extends Vlp16ClearanceModule {
  /** 20181212: flag the vlp16 passive slowing module as disabled for 0.1[s] */
  private final Watchdog watchdog = SoftWatchdog.barking(0.1);

  @Override // from Vlp16ClearanceModule
  Optional<RimoPutEvent> penaltyAction() {
    return watchdog.isBarking() //
        ? StaticHelper.OPTIONAL_RIMO_PASSIVE
        : Optional.empty();
  }

  /** function may be called during manual operation of the
   * gokart in order to test hi-performance maneuvers */
  public void bypassSafety() {
    watchdog.notifyWatchdog();
  }
}
