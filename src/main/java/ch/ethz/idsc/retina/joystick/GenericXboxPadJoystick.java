// code by jph
package ch.ethz.idsc.retina.joystick;

import ch.ethz.idsc.retina.util.math.Clipzone;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ final class GenericXboxPadJoystick extends JoystickEvent implements ManualControlInterface {
  // function for testing
  static GenericXboxPadJoystick createDefault() {
    return new GenericXboxPadJoystick(9.5);
  }

  // ---
  private final Clipzone clipzone;

  /** @param deadzone number between [0, 127] */
  public GenericXboxPadJoystick(double deadzone) {
    clipzone = new Clipzone(Clip.function(deadzone / 127, 1.0));
  }

  // ---
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

  public boolean isButtonPressedLeftKnob() {
    return isButtonPressed(8);
  }

  public boolean isButtonPressedRightKnob() {
    return isButtonPressed(9);
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
   * @return values in the interval [-1, 1] */
  public double getLeftKnobDirectionDown() {
    return getAxisValue(1);
  }

  /** value is 1.0 if left knob is pushed away from user value is -1.0 if left knob
   * is pulled towards user
   * 
   * @return values in the interval [-1, 1] */
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
    double axis = getAxisValue(2);
    return (axis + 1) * 0.5;
  }

  /** value is 0.0 if slider is passive, and 1.0 if slider is pressed inwards all
   * the way
   * 
   * @return value in the unit interval [0, 1] */
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

  /***************************************************/
  @Override // from GokartJoystickInterface
  public Scalar getSteerLeft() {
    return DoubleScalar.of(-getRightKnobDirectionRight());
  }

  @Override // from GokartJoystickInterface
  public Scalar getBreakStrength() {
    return DoubleScalar.of(Math.max(0, getRightKnobDirectionDown()));
  }

  @Override // from GokartJoystickInterface
  public Scalar getAheadAverage() {
    return clipzone.apply(DoubleScalar.of(getLeftKnobDirectionUp()));
  }

  @Override // from GokartJoystickInterface
  public Tensor getAheadPair_Unit() {
    return Tensors.vectorDouble( //
        getLeftSliderUnitValue(), //
        getRightSliderUnitValue());
  }

  @Override // from GokartJoystickInterface
  public boolean isAutonomousPressed() {
    return isButtonPressedA();
  }

  @Override // from GokartJoystickInterface
  public boolean isResetPressed() {
    return isButtonPressedY();
  }
}
