// code by jph
package ch.ethz.idsc.retina.joystick;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

final class MadCatzIncMadCatzFightpadProPs3 extends JoystickEvent implements ManualControlInterface {
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

  @Override
  public boolean isAutonomousPressed() {
    throw new RuntimeException();
  }

  @Override
  public boolean isResetPressed() {
    throw new RuntimeException();
  }
}
