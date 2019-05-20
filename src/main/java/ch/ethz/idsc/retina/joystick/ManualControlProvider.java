// code by jph
package ch.ethz.idsc.retina.joystick;

import java.util.Optional;

/** only events aged less equals than a timeout, e.g. 200[ms]
 * are provided to the application layer */
@FunctionalInterface
public interface ManualControlProvider {
  /** @return */
  Optional<ManualControlInterface> getManualControl();
}
