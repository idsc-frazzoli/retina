// code by jph
package ch.ethz.idsc.retina.joystick;

import java.awt.event.MouseListener;

/** design inspired by {@link MouseListener} */
public interface JoystickListener {
  /** callback function for a joystick event
   * 
   * @param joystickEvent */
  void joystick(JoystickEvent joystickEvent);
}
