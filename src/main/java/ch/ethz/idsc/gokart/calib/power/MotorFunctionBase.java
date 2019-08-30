// code by mh, jph
package ch.ethz.idsc.gokart.calib.power;

import java.io.Serializable;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ abstract class MotorFunctionBase implements MotorFunction, Serializable {
  @Override // from MotorFunction
  public final Scalar getAccelerationEstimation(Scalar power, Scalar speed) {
    float fspd = Magnitude.VELOCITY.toFloat(speed);
    float fpow = Magnitude.ARMS.toFloat(power);
    return Quantity.of(fullFunction(fspd, fpow), SI.ACCELERATION);
  }

  private final float backwardacc(float fspd, float fpow) {
    return -forwardacc(-fspd, -fpow);
  }

  private final float fullFunction(float fspd, float fpow) {
    float speedthreshold = 0.5f;
    if (fspd > speedthreshold)
      return forwardacc(fspd, fpow);
    if (fspd < -speedthreshold)
      return backwardacc(fspd, fpow);
    float forwardValue = forwardacc(speedthreshold, fpow);
    float backwardValue = backwardacc(-speedthreshold, fpow);
    float prog = (fspd + speedthreshold) / (2 * speedthreshold);
    return (prog * forwardValue + (1 - prog) * backwardValue);
  }

  /** @param fspd
   * @param fpow
   * @return */
  abstract float forwardacc(float fspd, float fpow);
}
