// code by mheim based on matlab code
package ch.ethz.idsc.gokart.calib.power;

/** non-linear */
/* package */ class MotorFunctionV1 extends MotorFunctionBase {
  static final MotorFunctionBase INSTANCE = new MotorFunctionV1();
  // ---
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

  // ---
  private MotorFunctionV1() {
    // ---
  }

  @Override // from MotorFunctionBase
  float forwardacc(float fspd, float fpow) {
    float powerthreshold = 100f;
    if (fpow > powerthreshold)
      return sfpos(fspd, fpow);
    if (fpow < -powerthreshold)
      return sfneg(fspd, fpow);
    float posval = sfpos(fspd, powerthreshold);
    float negval = sfneg(fspd, -powerthreshold);
    float prog = (fpow + powerthreshold) / (2 * powerthreshold);
    return prog * posval + (1 - prog) * negval;
  }
}
