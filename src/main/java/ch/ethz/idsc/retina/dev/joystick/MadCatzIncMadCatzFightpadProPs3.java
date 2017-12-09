// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

final class MadCatzIncMadCatzFightpadProPs3 extends JoystickEvent implements GokartJoystickInterface {
  @Override
  public JoystickType type() {
    return null; // JoystickType.MAD_CATZ_FIGHTPAD_PRO_PS3;
  }

  @Override
  public Scalar getSteerLeft() {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar getBreakStrength() {
    return RealScalar.ZERO;
  }

  @Override
  public Scalar getAheadAverage() {
    return RealScalar.ZERO;
  }

  @Override
  public Tensor getAheadPair_Unit() {
    return Array.zeros(2);
  }
}
