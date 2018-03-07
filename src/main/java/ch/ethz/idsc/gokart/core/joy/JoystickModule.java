// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** abstract base class for modules that convert joystick events into actuation */
/* package */ abstract class JoystickModule<PE> extends AbstractModule implements PutProvider<PE> {
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);

  @Override // from AbstractModule
  protected final void first() throws Exception {
    joystickLcmClient.startSubscriptions();
    protected_first();
  }

  @Override // from AbstractModule
  protected final void last() {
    protected_last();
    joystickLcmClient.stopSubscriptions();
  }

  /** function invoked upon start of the module */
  abstract void protected_first();

  /** function invoked upon termination of the module */
  abstract void protected_last();

  /***************************************************/
  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override // from PutProvider
  public final Optional<PE> putEvent() {
    Optional<JoystickEvent> optional = joystickLcmClient.getJoystick();
    return optional.isPresent() //
        ? translate((GokartJoystickInterface) optional.get())
        : Optional.empty();
  }

  /** @param joystick
   * @return put event for actuator controlled by this joystick module */
  abstract Optional<PE> translate(GokartJoystickInterface joystick);
}
