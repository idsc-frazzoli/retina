// code by mheim based on matlab code
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MotorFunctionV2 {
  ;
  private static float forwardacc(float fspd, float fpow) {
    float powerthresholdhigh = 1300f;
    float powerthresholdlow = -660f;
    float ptlramp = -10f;
    float lowpowerlimit = powerthresholdlow + ptlramp * fspd;
    if (fpow < lowpowerlimit)
      return RampFun.of(lowpowerlimit, fspd);
    if (fpow < powerthresholdhigh)
      return RampFun.of(fpow, fspd);
    return RampFun.of(powerthresholdhigh, fspd);
  }

  private static float backwardacc(float fspd, float fpow) {
    return -forwardacc(-fspd, -fpow);
  }

  private static float fullFunction(float fspd, float fpow) {
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

  /** @param power with unit "ARMS"
   * @param speed with unit velocity e.g. "m/s"
   * @return "m*s^-2" */
  public static Scalar getAccelerationEstimation(Scalar power, Scalar speed) {
    float fspd = Magnitude.VELOCITY.toFloat(speed);
    float fpow = Magnitude.ARMS.toFloat(power);
    return Quantity.of(fullFunction(fspd, fpow), SI.ACCELERATION);
  }
}
