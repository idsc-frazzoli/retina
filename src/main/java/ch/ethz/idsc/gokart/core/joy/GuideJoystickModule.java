// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;

/** base class for joystick modules that require access to the SteerColumnInterface
 * of the gokart. Examples are for control of steering and acceleration. */
abstract class GuideJoystickModule<PE> extends JoystickModule<PE> {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();

  @Override // from JoystickModule
  final Optional<PE> translate(GokartJoystickInterface joystick) {
    return private_translate(steerColumnInterface, joystick);
  }

  // function non-private for testing only
  final Optional<PE> private_translate(SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    if (steerColumnInterface.isSteerColumnCalibrated())
      return control(steerColumnInterface, joystick);
    return Optional.empty();
  }

  /** @param steerColumnInterface guaranteed to be calibrated
   * @param joystick
   * @return */
  abstract Optional<PE> control(SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick);
}
