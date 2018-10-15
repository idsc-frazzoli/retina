package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public final class PowerHelpers {
  private static final float sfpos(float fpow, float fspd) {
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

  private static final float sfneg(float fspd, float fpow) {
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

  private static final float forwardacc(float fspd, float fpow) {
    float powerthreshold = 0.1f;
    if (fpow > powerthreshold)
      return sfpos(fspd, fpow);
    else if (fpow < -powerthreshold)
      return sfneg(fspd, fpow);
    else {
      float posval = sfpos(fspd, powerthreshold);
      float negval = sfneg(fspd, -powerthreshold);
      float prog = (fpow - powerthreshold) / (2 * powerthreshold);
      return prog * posval + (1 - prog) * negval;
    }
  }

  public static final Scalar getAccelerationEstimation(Scalar power, Scalar speed) {
    float fpow = Magnitude.ARMS.toFloat(power);
    float fspd = Magnitude.VELOCITY.toFloat(speed);
    return RealScalar.ZERO;
  }
}
