// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** abstract base class for modules that convert joystick events into actuation */
/* package */ abstract class ManualModule<PE> extends AbstractModule implements PutProvider<PE> {
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.getProvider();

  /***************************************************/
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from PutProvider
  public final Optional<PE> putEvent() {
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    return optional.isPresent() //
        ? translate(optional.get())
        : Optional.empty();
  }

  /** @param manualControlInterface
   * @return put event for actuator controlled by this joystick module */
  abstract Optional<PE> translate(ManualControlInterface manualControlInterface);
}
