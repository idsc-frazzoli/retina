// code by jph
package ch.ethz.idsc.retina.joystick;

import java.util.Optional;

import ch.ethz.idsc.retina.util.StartAndStoppable;

/** only events aged less equals than a timeout, e.g. 200[ms]
 * are provided to the application layer */
public interface ManualControlProvider extends StartAndStoppable {
  /** @return */
  Optional<ManualControlInterface> getManualControl();
}
