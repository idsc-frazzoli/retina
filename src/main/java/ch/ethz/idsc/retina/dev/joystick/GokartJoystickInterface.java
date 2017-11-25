// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.Optional;

/** interface implemented by a {@link JoystickEvent}
 * to qualify for controlling a gokart like robot */
// TODO duplicate interface and rename functions to functionalNames: "forwardSpeed", turnAngle()...
public interface GokartJoystickInterface {
  /** @return double in the interval [0, 1] */
  double getBreakStrength();

  double getBreakSecondary();

  /** @return double in the interval [-1, 1] */
  double getAheadAverage();

  /** positive value is interpreted as ccw rotation
   * 
   * @return value in the interval [-1, 1] */
  double getSteerLeft();

  /** @return in the interval [0, 1] */
  double getAheadTireLeft_Unit();

  double getAheadTireRight_Unit();

  Optional<Integer> getSpeedMultiplierOptional();
  // boolean isButtonPressedBack();
  //
  // boolean isButtonPressedStart();
}
