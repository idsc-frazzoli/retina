// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GokartJoystickAdapter implements GokartJoystickInterface {
  private final Scalar steerLeft;
  private final double breakStrength;
  private final Scalar ahead;
  private final Tensor pair;

  public GokartJoystickAdapter(Scalar steerLeft, double breakStrength, Scalar ahead, Tensor pair) {
    this.steerLeft = steerLeft;
    this.breakStrength = breakStrength;
    this.ahead = ahead;
    this.pair = pair.copy();
  }

  @Override
  public Scalar getSteerLeft() {
    return steerLeft;
  }

  @Override
  public double getBreakStrength() {
    return breakStrength;
  }

  @Override
  public Scalar getAheadAverage() {
    return ahead;
  }

  @Override
  public Tensor getAheadPair_Unit() {
    return pair;
  }
}
