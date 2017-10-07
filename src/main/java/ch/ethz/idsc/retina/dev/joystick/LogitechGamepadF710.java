// code by jph
package ch.ethz.idsc.retina.dev.joystick;

public final class LogitechGamepadF710 extends JoystickEvent implements GokartJoystickInterface {
  @Override
  public JoystickType type() {
    return JoystickType.LOGITECH_GAMEPAD_F710;
  }

  public boolean isButtonPressedA() {
    return isButtonPressed(0);
  }

  public boolean isButtonPressedB() {
    return isButtonPressed(1);
  }

  public boolean isButtonPressedX() {
    return isButtonPressed(2);
  }

  public boolean isButtonPressedY() {
    return isButtonPressed(3);
  }

  public boolean isButtonPressedLB() {
    return isButtonPressed(4);
  }

  public boolean isButtonPressedRB() {
    return isButtonPressed(5);
  }

  @Override
  public boolean isButtonPressedBack() {
    return isButtonPressed(6);
  }

  @Override
  public boolean isButtonPressedStart() {
    return isButtonPressed(7);
  }

  public boolean isButtonPressedLeftKnob() {
    return isButtonPressed(9);
  }

  public boolean isButtonPressedRightKnob() {
    return isButtonPressed(10);
  }

  @Override
  public double getLeftKnobDirectionRight() {
    return getAxisValue(0);
  }

  @Override
  public double getLeftKnobDirectionDown() {
    return getAxisValue(1);
  }

  @Override
  public double getLeftKnobDirectionUp() {
    return -getLeftKnobDirectionDown();
  }

  @Override
  public double getRightKnobDirectionRight() {
    return getAxisValue(3);
  }

  public double getRightKnobDirectionDown() {
    return getAxisValue(4);
  }

  public double getRightKnobDirectionUp() {
    return -getRightKnobDirectionDown();
  }

  @Override
  public double getLeftSliderUnitValue() {
    double axis = getAxisValue(2);
    return (axis + 1) * 0.5;
  }

  @Override
  public double getRightSliderUnitValue() {
    double axis = getAxisValue(5);
    return (axis + 1) * 0.5;
  }

  public boolean isHatPressedUp() {
    return (getHat(0) & 1) != 0;
  }

  public boolean isHatPressedRight() {
    return (getHat(0) & 2) != 0;
  }

  public boolean isHatPressedDown() {
    return (getHat(0) & 4) != 0;
  }

  public boolean isHatPressedLeft() {
    return (getHat(0) & 8) != 0;
  }
}
