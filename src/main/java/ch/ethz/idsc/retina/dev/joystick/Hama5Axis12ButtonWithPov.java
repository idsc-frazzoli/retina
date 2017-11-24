// code by jph
package ch.ethz.idsc.retina.dev.joystick;

/** axis 0 = stick right
 * axis 1 = stick down
 * axis 2 = handbrake flat == +1 (up -> -1)
 * axis 3 = stick twist cw direction, i.e. right
 * axis 4 == NO ID */
/* package */ class Hama5Axis12ButtonWithPov extends JoystickEvent implements GokartJoystickInterface {
  @Override
  public double getLeftKnobDirectionDown() {
    return getAxisValue(1);
  }

  @Override
  public double getLeftKnobDirectionUp() {
    // TODO implement this generally
    return -getAxisValue(1);
  }

  @Override
  public double getLeftKnobDirectionRight() {
    return 0.0;
  }

  @Override
  public double getRightKnobDirectionRight() {
    return getAxisValue(0);
  }

  @Override
  public double getLeftSliderUnitValue() {
    return getAxisValue(2);
  }

  @Override
  public double getRightSliderUnitValue() {
    return getAxisValue(2);
  }

  @Override
  public boolean isButtonPressedBack() {
    return false; // TODO button is not supported
  }

  @Override
  public boolean isButtonPressedStart() {
    return false;
  }

  @Override
  public JoystickType type() {
    return JoystickType.HAMA_5AXIS12BUTTON_WITH_POV;
  }
}
