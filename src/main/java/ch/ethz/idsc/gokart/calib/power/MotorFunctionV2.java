// code by mheim based on matlab code
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MotorFunctionV2 {
  ;
  private static final CubicBiPolynomial SF_POS = new CubicBiPolynomial( //
      -0.321f, //
      0.1285f, 0.002162f, //
      -0.03076f, -0.0002196f, -3.999e-07f, //
      0.002858f, 5.106e-06f, 7.048e-08f, -5.126e-11f);
  private static final CubicBiPolynomial SF_NEG = new CubicBiPolynomial( //
      -0.3738f, //
      -0.06382f, 0.002075f, //
      0.03953f, 0.0001024f, 1.371e-06f, //
      -0.004336f, -3.495e-06f, 2.634e-08f, 2.899e-10f);

  /* package */ static float sfpos(float fspd, float fpow) {
    return SF_POS.evaluate(fspd, fpow);
  }

  /* package */ static float sfneg(float fspd, float fpow) {
    return SF_NEG.evaluate(fspd, fpow);
  }

  static float rampfun(float fpow, float fvel) {
    float p0 = -0.3223f;
    float ppower = 0.001855f;
    float pvel = -0.0107f;
    return p0 + ppower * fpow + pvel * fvel;
  }

  private static float forwardacc(float fspd, float fpow) {
    float powerthresholdhigh = 1300f;
    float powerthresholdlow = -660f;
    float ptlramp = -10f;
    float lowpowerlimit = powerthresholdlow + ptlramp * fspd;
    if (fpow < lowpowerlimit)
      return rampfun(lowpowerlimit, fspd);
    else if (fpow < powerthresholdhigh)
      return rampfun(fpow, fspd);
    else
      return rampfun(powerthresholdhigh, fspd);
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
