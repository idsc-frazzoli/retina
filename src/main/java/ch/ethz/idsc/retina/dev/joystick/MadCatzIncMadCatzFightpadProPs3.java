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

  @Override // from GokartJoystickInterface
  public Scalar getSteerLeft() {
    return RealScalar.ZERO;
  }

  @Override // from GokartJoystickInterface
  public Scalar getBreakStrength() {
    return RealScalar.ZERO;
  }

  @Override // from GokartJoystickInterface
  public Scalar getAheadAverage() {
    return RealScalar.ZERO;
  }

  @Override // from GokartJoystickInterface
  public Tensor getAheadPair_Unit() {
    return Array.zeros(2);
  }

  @Override // from GokartJoystickInterface
  public boolean isPassive() {
    throw new RuntimeException();
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
