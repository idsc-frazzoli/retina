// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.Optional;

import ch.ethz.idsc.retina.util.StartAndStoppable;

/** only events aged less equals than a timeout, e.g. 200[ms]
 * are provided to the application layer */
public interface ManualControlProvider extends StartAndStoppable {
  // TODO JAN rename function getManualControl
  /** @return */
  Optional<GokartJoystickInterface> getJoystick();
}
