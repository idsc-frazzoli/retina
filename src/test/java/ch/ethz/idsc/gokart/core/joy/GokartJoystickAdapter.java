// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ class GokartJoystickAdapter implements GokartJoystickInterface {
  private final Scalar steerLeft;
  private final Scalar breakStrength;
  private final Scalar ahead;
  private final Tensor pair;
  private final boolean isAutonomousPressed;
  public boolean isResetPressed;

  /** see {@link GokartJoystickInterface} for valid range of arguments
   * 
   * @param steerLeft in the interval [-1, 1]
   * @param breakStrength in the unit interval [0, 1]
   * @param ahead real scalar in the interval [-1, 1]
   * @param pair vector of length 2 with entries in the unit interval [0, 1]
   * @param isAutonomousPressed
   * @throws Exception if any argument is not in the valid range */
  public GokartJoystickAdapter(Scalar steerLeft, Scalar breakStrength, Scalar ahead, Tensor pair, boolean isAutonomousPressed) {
    Clip.absoluteOne().requireInside(steerLeft);
    Clip.unit().requireInside(breakStrength);
    Clip.absoluteOne().requireInside(ahead);
    if (!pair.map(Clip.unit()).equals(pair))
      throw TensorRuntimeException.of(pair);
    // ---
    this.steerLeft = steerLeft;
    this.breakStrength = breakStrength;
    this.ahead = ahead;
    this.pair = pair.copy();
    this.isAutonomousPressed = isAutonomousPressed;
  }

  @Override // from GokartJoystickInterface
  public Scalar getSteerLeft() {
    return steerLeft;
  }

  @Override // from GokartJoystickInterface
  public Scalar getBreakStrength() {
    return breakStrength;
  }

  @Override // from GokartJoystickInterface
  public Scalar getAheadAverage() {
    return ahead;
  }

  @Override // from GokartJoystickInterface
  public Tensor getAheadPair_Unit() {
    return pair;
  }

  @Override // from GokartJoystickInterface
  public boolean isPassive() {
    return Scalars.isZero(steerLeft) //
        && Scalars.isZero(breakStrength) //
        && Scalars.isZero(ahead) //
        && Chop.NONE.allZero(pair);
  }

  @Override
  public boolean isAutonomousPressed() {
    return isAutonomousPressed;
  }

  @Override
  public boolean isResetPressed() {
    return isResetPressed;
  }
}
