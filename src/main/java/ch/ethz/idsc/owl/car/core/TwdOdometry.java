// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Chop;

public class TwdOdometry {
  private final Scalar radius;
  private final Scalar yTireRear;

  public TwdOdometry(AxleConfiguration axleConfiguration) {
    WheelConfiguration wheelL = axleConfiguration.wheel(0);
    WheelConfiguration wheelR = axleConfiguration.wheel(1);
    Chop._10.requireClose(wheelL.tireConfiguration().radius(), wheelR.tireConfiguration().radius());
    yTireRear = wheelL.local().Get(1);
    radius = wheelL.tireConfiguration().radius();
  }

  public Scalar tangentSpeed(Tensor angularRate_Y_pair) {
    return radius.multiply(Mean.of(angularRate_Y_pair).Get());
  }

  public Scalar turningRate(Tensor angularRate_Y_pair) {
    // rad/s * m == (m / s) / m
    return Differences.of(angularRate_Y_pair).Get(0) //
        .multiply(RationalScalar.HALF).multiply(radius).divide(yTireRear);
  }

  /** @param angularRate_Y_pair
   * @return {vx[m*s^-1], vy[m*s^-1], omega[s^-1]} */
  public Tensor velocity(Tensor angularRate_Y_pair) {
    Scalar vx = tangentSpeed(angularRate_Y_pair);
    return Tensors.of( //
        tangentSpeed(angularRate_Y_pair), //
        vx.zero(), //
        turningRate(angularRate_Y_pair));
  }
}
