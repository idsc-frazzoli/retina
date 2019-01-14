// code by jph
package ch.ethz.idsc.retina.joystick;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** interface implemented by a {@link JoystickEvent}
 * to qualify for controlling a gokart like robot
 * 
 * two modes are supported:
 * 1) simple drive
 * 2) full control */
public interface ManualControlInterface {
  /** positive value is interpreted as ccw rotation
   * 
   * @return real scalar in the interval [-1, 1] */
  Scalar getSteerLeft();

  /** @return real scalar in the interval [0, 1] */
  Scalar getBreakStrength();

  /** @return real scalar in the interval [-1, 1]. when joystick is not being manipulated,
   * the return value equals zero. */
  Scalar getAheadAverage();

  /** @return vector of length 2 with each entry in unit interval [0, 1] */
  Tensor getAheadPair_Unit();

  /** @return true when operator authorizes autonomous mode */
  boolean isAutonomousPressed();

  /** @return true when operator authorizes calibration, or reset */
  boolean isResetPressed();
}
