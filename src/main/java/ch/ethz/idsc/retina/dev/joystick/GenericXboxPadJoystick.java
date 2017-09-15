// code by jph
package ch.ethz.idsc.retina.dev.joystick;

public final class GenericXboxPadJoystick extends JoystickEvent {
  @Override
  public JoystickType type() {
    return JoystickType.GENERIC_XBOX_PAD;
  }

  public boolean isButtonPressedA() {
    return isButtonPressed(0);
  }

  public boolean isButtonPressedB() {
    return isButtonPressed(1);
  }

  public boolean isButtonPressedBlack() {
    return isButtonPressed(2);
  }

  public boolean isButtonPressedX() {
    return isButtonPressed(3);
  }

  public boolean isButtonPressedY() {
    return isButtonPressed(4);
  }

  public boolean isButtonPressedWhite() {
    return isButtonPressed(5);
  }

  public boolean isButtonPressedBack() {
    return isButtonPressed(6);
  }

  public boolean isButtonPressedStart() {
    return isButtonPressed(7);
  }

  public boolean isButtonPressedAxisLeft() {
    return isButtonPressed(8);
  }

  public boolean isButtonPressedAxisRight() {
    return isButtonPressed(9);
  }

  /** value is 1.0 if left knob is held to the far right
   * value is -1.0 if left knob is held to the far left
   * 
   * @return values in the unit interval [0, 1] */
  public double getLeftKnobDirectionRight() {
    return getAxisValue(0);
  }

  /** value is 1.0 if left knob is pulled towards user
   * value is -1.0 if left knob is pushed away from user
   * 
   * @return values in the unit interval [0, 1] */
  public double getLeftKnobDirectionDown() {
    return getAxisValue(1);
  }

  /** value is 1.0 if left knob is pushed away from user
   * value is -1.0 if left knob is pulled towards user
   * 
   * @return values in the unit interval [0, 1] */
  public double getLeftKnobDirectionUp() {
    return -getLeftKnobDirectionDown();
  }

  public double getRightKnobDirectionRight() {
    return getAxisValue(3);
  }

  public double getRightKnobDirectionDown() {
    return getAxisValue(4);
  }

  public double getRightKnobDirectionUp() {
    return -getRightKnobDirectionDown();
  }

  /** value is 0.0 if slider is passive, and
   * 1.0 if slider is pressed inwards all the way
   * 
   * @return value in the unit interval [0,1] */
  public double getLeftSliderUnitValue() {
    double axis = getAxisValue(2);
    return (axis + 1) * 0.5;
  }

  /** value is 0.0 if slider is passive, and
   * 1.0 if slider is pressed inwards all the way
   * 
   * @return value in the unit interval [0,1] */
  public double getRightSliderUnitValue() {
    double axis = getAxisValue(5);
    return (axis + 1) * 0.5;
  }
}
