//code by mheim based on matlab code
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

enum PowerHelpers {
  ;
  private static float sfneg(float fpow, float fspd) {
    float p00 = -0.321f;
    float p10 = 0.1285f;
    float p01 = 0.002162f;
    float p20 = -0.03076f;
    float p11 = -0.0002196f;
    float p02 = -3.999e-07f;
    float p30 = 0.002858f;
    float p21 = 5.106e-06f;
    float p12 = 7.048e-08f;
    float p03 = -5.126e-11f;
    float x = fspd;
    float y = fpow;
    return p00// constant
        + p10 * x + p01 * y// linear
        + p20 * x * x + p11 * x * y + p02 * y * y// quadratic
        + p30 * x * x * x + p21 * x * x * y + p12 * x * y * y + p03 * y * y * y;// cubic
  }

  private static float sfpos(float fspd, float fpow) {
    float p00 = -0.3738f;
    float p10 = -0.06382f;
    float p01 = 0.002075f;
    float p20 = 0.03953f;
    float p11 = 0.0001024f;
    float p02 = 1.371e-06f;
    float p30 = -0.004336f;
    float p21 = -3.495e-06f;
    float p12 = 2.634e-08f;
    float p03 = 2.899e-10f;
    float x = fspd;
    float y = fpow;
    return p00// constant
        + p10 * x + p01 * y// linear
        + p20 * x * x + p11 * x * y + p02 * y * y// quadratic
        + p30 * x * x * x + p21 * x * x * y + p12 * x * y * y + p03 * y * y * y;// cubic
  }

  private static float forwardacc(float fspd, float fpow) {
    float powerthreshold = 0.1f;
    if (fpow > powerthreshold)
      return sfpos(fspd, fpow);
    else if (fpow < -powerthreshold)
      return sfneg(fspd, fpow);
    else {
      final float posval = sfpos(fspd, powerthreshold);
      final float negval = sfneg(fspd, -powerthreshold);
      final float prog = (fpow - powerthreshold) / (2 * powerthreshold);
      return prog * posval + (1 - prog) * negval;
    }
  }

  private static float backwardacc(float fspd, float fpow) {
    return -forwardacc(-fspd, -fpow);
  }

  private static float fullFunction(float fspd, float fpow) {
    float speedthreshold = 0.5f;
    if (fspd > speedthreshold) {
      return forwardacc(fspd, fpow);
    } else if (fspd < -speedthreshold) {
      return backwardacc(fspd, fpow);
    } else {
      final float forwardValue = forwardacc(speedthreshold, fpow);
      final float backwardValue = backwardacc(-speedthreshold, fpow);
      float prog = (fspd + speedthreshold) / (2 * speedthreshold);
      return prog * forwardValue + (1 - prog) * backwardValue;
    }
  }

  /** @param power with unit "ARMS"
   * @param speed with unit velocity e.g. "m/s"
   * @return */
  public static Scalar getAccelerationEstimation(Scalar power, Scalar speed) {
    float fpow = Magnitude.ARMS.toFloat(power);
    float fspd = Magnitude.VELOCITY.toFloat(speed);
    return Quantity.of(fullFunction(fspd, fpow), SI.ACCELERATION);
  }
}
