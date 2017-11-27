// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

final class MadCatzIncMadCatzFightpadProPs3 extends JoystickEvent implements GokartJoystickInterface {
  @Override
  public JoystickType type() {
    return null; // JoystickType.MAD_CATZ_FIGHTPAD_PRO_PS3;
  }

  @Override
  public double getSteerLeft() {
    return 0;
  }

  @Override
  public double getBreakStrength() {
    return 0;
  }

  @Override
  public double getAheadAverage() {
    return 0;
  }

  @Override
  public Tensor getAheadPair_Unit() {
    return Array.zeros(2);
  }
}
