// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;

/** conversion of joystick event to brake command */
public class LinmotJoystickModule extends JoystickModule<LinmotPutEvent> {
  @Override // from AbstractModule
  void protected_first() {
    LinmotSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    LinmotSocket.INSTANCE.removePutProvider(this);
  }

  /***************************************************/
  @Override // from JoystickModule
  Optional<LinmotPutEvent> translate(GokartJoystickInterface joystick) {
    return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(joystick.getBreakStrength()));
  }
}
