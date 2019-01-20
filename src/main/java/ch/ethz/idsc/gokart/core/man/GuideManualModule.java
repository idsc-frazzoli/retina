// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;

/** base class for joystick modules that require access to the SteerColumnInterface
 * of the gokart. Examples are for control of steering and acceleration. */
abstract class GuideManualModule<PE> extends ManualModule<PE> {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();

  @Override // from JoystickModule
  final Optional<PE> translate(ManualControlInterface manualControlInterface) {
    return private_translate(steerColumnInterface, manualControlInterface);
  }

  // function non-private for testing only
  final Optional<PE> private_translate(SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    if (steerColumnInterface.isSteerColumnCalibrated())
      return control(steerColumnInterface, manualControlInterface);
    return Optional.empty(); // steering position evaluates to NaN
  }

  /** @param steerColumnInterface guaranteed to be calibrated
   * @param manualControlInterface
   * @return */
  abstract Optional<PE> control(SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface);
}
