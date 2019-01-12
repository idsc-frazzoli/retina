// code by jph
package ch.ethz.idsc.retina.joystick;

/* package */ final class PCPS3AndroidJoystick extends JoystickEvent {
  @Override
  public JoystickType type() {
    return null; // JoystickType.PC_PS3_ANDROID;
  }

  public boolean isButtonPressedA() {
    return isButtonPressed(0);
  }

  public boolean isButtonPressedB() {
    return isButtonPressed(1);
  }

  public boolean isButtonPressedSelect() {
    return isButtonPressed(2);
  }

  public boolean isButtonPressedX() {
    return isButtonPressed(3);
  }

  public boolean isButtonPressedY() {
    return isButtonPressed(4);
  }

  public boolean isButtonPressedOptions() {
    return isButtonPressed(5);
  }

  public boolean isButtonPressedBack() {
    return isButtonPressed(6);
  }

  public boolean isButtonPressedStart() {
    return isButtonPressed(7);
  }

  public boolean isButtonPressedLeftKnob() {
    return isButtonPressed(8);
  }

  public boolean isButtonPressedRightKnob() {
    return isButtonPressed(9);
  }

  public boolean isButtonPressedL1() {
    return isButtonPressed(10);
  }

  public boolean isButtonPressedL2() {
    return isButtonPressed(11);
  }

  public boolean isButtonPressedR1() {
    return isButtonPressed(12);
  }

  public boolean isButtonPressedR2() {
    return isButtonPressed(13);
  }

  public boolean isButtonPressedHome() {
    return isButtonPressed(14);
  }

  /** value is 1.0 if left knob is held to the far right value is -1.0 if left knob
   * is held to the far left
   * 
   * @return values in the unit interval [0, 1] */
  public double getLeftKnobDirectionRight() {
    return getAxisValue(0);
  }

  /** value is 1.0 if left knob is pulled towards user value is -1.0 if left knob
   * is pushed away from user
   * 
   * @return values in the unit interval [0, 1] */
  public double getLeftKnobDirectionDown() {
    return getAxisValue(1);
  }

  /** value is 1.0 if left knob is pushed away from user value is -1.0 if left knob
   * is pulled towards user
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
}
