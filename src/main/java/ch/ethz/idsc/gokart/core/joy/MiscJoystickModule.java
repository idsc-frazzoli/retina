// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.GetListener;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;

/** conversion of joystick event to brake command */
// FIXME class does not need to be a put provider
public class MiscJoystickModule extends JoystickModule<MiscPutEvent> implements GetListener<MiscGetEvent> {
  private MiscGetEvent miscGetEvent = null;

  @Override // from AbstractModule
  void protected_first() {
    MiscSocket.INSTANCE.addGetListener(this);
    MiscSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    MiscSocket.INSTANCE.removePutProvider(this);
    MiscSocket.INSTANCE.removeGetListener(this);
  }

  /***************************************************/
  @Override // from JoystickModule
  Optional<MiscPutEvent> translate(GokartJoystickInterface joystick) {
    if (Objects.nonNull(miscGetEvent) && miscGetEvent.isCommTimeout())
      if (joystick.isResetPressed() && MiscIgnitionProvider.INSTANCE.isIdle())
        MiscIgnitionProvider.INSTANCE.schedule();
    return Optional.empty();
  }

  @Override
  public void getEvent(MiscGetEvent getEvent) {
    miscGetEvent = getEvent;
  }
}
