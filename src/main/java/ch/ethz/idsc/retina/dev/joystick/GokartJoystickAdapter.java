// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

public class GokartJoystickAdapter implements GokartJoystickInterface {
  public static final GokartJoystickInterface PASSIVE = new GokartJoystickAdapter( //
      RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), false, false);
  // ---
  private final Scalar steerLeft;
  private final Scalar breakStrength;
  private final Scalar aheadAverage;
  private final Tensor pair;
  private final boolean isAutonomousPressed;
  private final boolean isResetPressed;

  /** see {@link GokartJoystickInterface} for valid range of arguments
   * 
   * @param steerLeft in the interval [-1, 1]
   * @param breakStrength in the unit interval [0, 1]
   * @param aheadAverage real scalar in the interval [-1, 1]
   * @param pair vector of length 2 with entries in the unit interval [0, 1]
   * @param isAutonomousPressed
   * @throws Exception if any argument is not in the valid range */
  public GokartJoystickAdapter( //
      Scalar steerLeft, //
      Scalar breakStrength, //
      Scalar aheadAverage, //
      Tensor pair, //
      boolean isAutonomousPressed, //
      boolean isResetPressed) {
    Clip.absoluteOne().requireInside(steerLeft);
    Clip.unit().requireInside(breakStrength);
    Clip.absoluteOne().requireInside(aheadAverage);
    if (!pair.map(Clip.unit()).equals(pair))
      throw TensorRuntimeException.of(pair);
    // ---
    this.steerLeft = steerLeft;
    this.breakStrength = breakStrength;
    this.aheadAverage = aheadAverage;
    this.pair = pair.copy();
    this.isAutonomousPressed = isAutonomousPressed;
    this.isResetPressed = isResetPressed;
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
    return aheadAverage;
  }

  @Override // from GokartJoystickInterface
  public Tensor getAheadPair_Unit() {
    return pair;
  }

  @Override // from GokartJoystickInterface
  public boolean isPassive() {
    return Scalars.isZero(steerLeft) //
        && Scalars.isZero(breakStrength) //
        && Scalars.isZero(aheadAverage) //
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
