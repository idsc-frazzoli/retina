// code by jph
package ch.ethz.idsc.owl.car.core;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Chop;

public class TwdOdometry implements Serializable {
  private final Scalar radius;
  private final Scalar factor;

  public TwdOdometry(AxleConfiguration axleConfiguration) {
    WheelConfiguration wheelL = axleConfiguration.wheel(0);
    WheelConfiguration wheelR = axleConfiguration.wheel(1);
    Chop._10.requireClose(wheelL.tireConfiguration().radius(), wheelR.tireConfiguration().radius());
    Scalar yTireRear = wheelL.local().Get(1);
    radius = wheelL.tireConfiguration().radius();
    factor = radius.divide(yTireRear.add(yTireRear));
  }

  /** @param angularRate_Y_pair vector of the form {omegaL[s^-1], omegaR[s^-1]}
   * @return */
  public Scalar tangentSpeed(Tensor angularRate_Y_pair) {
    return Mean.of(angularRate_Y_pair).Get().multiply(radius);
  }

  /** @param angularRate_Y_pair vector of the form {omegaL[s^-1], omegaR[s^-1]}
   * @return */
  public Scalar turningRate(Tensor angularRate_Y_pair) {
    // rad/s * m == (m / s) / m
    return Differences.of(angularRate_Y_pair).Get(0).multiply(factor);
  }

  /** @param angularRate_Y_pair vector of the form {omegaL[s^-1], omegaR[s^-1]}
   * @return {vx[m*s^-1], vy[m*s^-1] == 0, omega[s^-1]} */
  public Tensor velocity(Tensor angularRate_Y_pair) {
    Scalar vx = tangentSpeed(angularRate_Y_pair);
    return Tensors.of( //
        vx, //
        vx.zero(), //
        turningRate(angularRate_Y_pair));
  }
}
