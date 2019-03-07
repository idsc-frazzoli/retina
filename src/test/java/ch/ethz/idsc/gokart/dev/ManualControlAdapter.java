// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;

/** immutable */
public class ManualControlAdapter implements ManualControlInterface {
  public static final ManualControlInterface PASSIVE = new ManualControlAdapter( //
      RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), false, false);
  // ---
  private final Scalar steerLeft;
  private final Scalar breakStrength;
  private final Scalar aheadAverage;
  private final Tensor pair;
  private final boolean isAutonomousPressed;
  private final boolean isResetPressed;

  /** see {@link ManualControlInterface} for valid range of arguments
   * 
   * @param steerLeft in the interval [-1, 1]
   * @param breakStrength in the unit interval [0, 1]
   * @param aheadAverage real scalar in the interval [-1, 1]
   * @param pair vector of length 2 with entries in the unit interval [0, 1]
   * @param isAutonomousPressed
   * @param isResetPressed
   * @throws Exception if any argument is not in the valid range */
  public ManualControlAdapter( //
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
    this.pair = VectorQ.requireLength(pair, 2).unmodifiable();
    this.isAutonomousPressed = isAutonomousPressed;
    this.isResetPressed = isResetPressed;
  }

  @Override // from ManualControlInterface
  public Scalar getSteerLeft() {
    return steerLeft;
  }

  @Override // from ManualControlInterface
  public Scalar getBreakStrength() {
    return breakStrength;
  }

  @Override // from ManualControlInterface
  public Scalar getAheadAverage() {
    return aheadAverage;
  }

  @Override // from ManualControlInterface
  public Tensor getAheadPair_Unit() {
    return pair;
  }

  @Override // from ManualControlInterface
  public boolean isAutonomousPressed() {
    return isAutonomousPressed;
  }

  @Override // from ManualControlInterface
  public boolean isResetPressed() {
    return isResetPressed;
  }

  @Override // from Object
  public String toString() {
    return Tensors.of( //
        steerLeft, //
        breakStrength, //
        aheadAverage, //
        pair, //
        Boole.of(isAutonomousPressed), //
        Boole.of(isResetPressed) //
    ).map(Round._2).toString();
  }
}
