// code by jph
package ch.ethz.idsc.retina.joystick;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public final class LogitechGamepadF710 extends JoystickEvent implements ManualControlInterface {
  @Override
  public JoystickType type() {
    // return JoystickType.LOGITECH_GAMEPAD_F710;
    return null;
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

  public boolean isButtonPressedBack() {
    return isButtonPressed(6);
  }

  public boolean isButtonPressedStart() {
    return isButtonPressed(7);
  }

  public boolean isButtonPressedLeftKnob() {
    return isButtonPressed(9);
  }

  public boolean isButtonPressedRightKnob() {
    return isButtonPressed(10);
  }

  /** value is 1.0 if left knob is held to the far right value is -1.0 if left knob
   * is held to the far left
   *
   * @return values in the interval [-1, 1] */
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

  /** value is 0.0 if slider is passive, and 1.0 if slider is pressed inwards all
   * the way
   *
   * @return value in the unit interval [0, 1] */
  public double getLeftSliderUnitValue() {
    return getSliderValue(2);
  }

  /** value is 0.0 if slider is passive, and 1.0 if slider is pressed inwards all
   * the way
   *
   * @return value in the unit interval [0, 1] */
  public double getRightSliderUnitValue() {
    return getSliderValue(5);
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

  /***************************************************/
  @Override // from GokartJoystickInterface
  public Scalar getBreakStrength() {
    return DoubleScalar.of(Math.max(0, getRightKnobDirectionDown()));
  }

  @Override // from GokartJoystickInterface
  public Scalar getAheadAverage() {
    return RealScalar.of(getLeftKnobDirectionUp());
  }

  @Override // from GokartJoystickInterface
  public Scalar getSteerLeft() {
    return RealScalar.of(-getRightKnobDirectionRight());
  }

  @Override // from GokartJoystickInterface
  public Tensor getAheadPair_Unit() {
    return Tensors.vectorDouble( //
        getLeftSliderUnitValue(), //
        getRightSliderUnitValue());
  }

  @Override
  public boolean isAutonomousPressed() {
    throw new RuntimeException();
  }

  @Override
  public boolean isResetPressed() {
    throw new RuntimeException();
  }
}
