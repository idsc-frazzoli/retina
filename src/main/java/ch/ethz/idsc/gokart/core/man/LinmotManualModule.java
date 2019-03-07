// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;

/** conversion of joystick event to brake command */
public class LinmotManualModule extends ManualModule<LinmotPutEvent> {
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
  Optional<LinmotPutEvent> translate(ManualControlInterface manualControlInterface) {
    return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(manualControlInterface.getBreakStrength()));
  }
}
