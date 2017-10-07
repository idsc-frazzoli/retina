// code by jph
package ch.ethz.idsc.retina.dev.joystick;

/** interface implemented by a {@link JoystickEvent}
 * to qualify for controlling a gokart like robot */
public interface GokartJoystickInterface {
  /** value is 1.0 if left knob is pulled towards user value is -1.0 if left knob
   * is pushed away from user
   * 
   * @return values in the unit interval [0, 1] */
  double getLeftKnobDirectionDown();

  /** value is 1.0 if left knob is pushed away from user value is -1.0 if left knob
   * is pulled towards user
   * 
   * @return values in the unit interval [0, 1] */
  double getLeftKnobDirectionUp();

  /** value is 1.0 if left knob is held to the far right value is -1.0 if left knob
   * is held to the far left
   * 
   * @return values in the unit interval [0, 1] */
  double getLeftKnobDirectionRight();

  double getRightKnobDirectionRight();

  /** value is 0.0 if slider is passive, and 1.0 if slider is pressed inwards all
   * the way
   * 
   * @return value in the unit interval [0,1] */
  double getLeftSliderUnitValue();

  /** value is 0.0 if slider is passive, and 1.0 if slider is pressed inwards all
   * the way
   * 
   * @return value in the unit interval [0,1] */
  double getRightSliderUnitValue();

  boolean isButtonPressedBack();

  boolean isButtonPressedStart();
}
