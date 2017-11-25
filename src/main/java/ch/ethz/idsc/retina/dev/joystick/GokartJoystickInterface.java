// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

/** interface implemented by a {@link JoystickEvent}
 * to qualify for controlling a gokart like robot
 * 
 * two modes are supported:
 * 1) simple drive
 * 2) full control */
public interface GokartJoystickInterface {
  /** positive value is interpreted as ccw rotation
   * 
   * @return value in the interval [-1, 1] */
  double getSteerLeft();

  /***************************************************/
  /** @return double in the interval [0, 1] */
  double getBreakStrength(); // used in full control

  /***************************************************/
  /** @return double in the interval [0, 1] */
  double getBreakSecondary(); // used in simple drive

  /** @return double in the interval [-1, 1] */
  double getAheadAverage(); // used in simple drive

  /** @return in the interval [0, 1] */
  Tensor getAheadPair_Unit();

  Optional<Integer> getSpeedMultiplierOptional();
}
