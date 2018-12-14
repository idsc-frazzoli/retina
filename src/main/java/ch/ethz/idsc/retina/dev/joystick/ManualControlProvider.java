// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.Optional;

import ch.ethz.idsc.retina.util.StartAndStoppable;

public interface ManualControlProvider extends StartAndStoppable {
  // TODO rename function
  /** @return */
  Optional<GokartJoystickInterface> getJoystick();
}
