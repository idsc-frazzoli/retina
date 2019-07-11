// code by mheim based on matlab code
package ch.ethz.idsc.gokart.calib.power;

/** linear */
/* package */ class MotorFunctionV2 extends MotorFunctionBase {
  static final MotorFunctionBase INSTANCE = new MotorFunctionV2();
  // ---
  static final float p0 = -0.3223f;
  static final float ppower = 0.001855f;
  static final float pvel = -0.0107f;

  static float rampfun(float fpow, float fvel) {
    return p0 + ppower * fpow + pvel * fvel;
  }

  // ---
  static final float powerthreshold_hi = 1300f;
  static final float powerthreshold_lo = -660f;
  static final float ptlramp = -10f;

  private MotorFunctionV2() {
    // ---
  }

  @Override // from MotorFunctionBase
  float forwardacc(float fspd, float fpow) {
    float lowpowerlimit = powerthreshold_lo + ptlramp * fspd;
    if (fpow < lowpowerlimit)
      return rampfun(lowpowerlimit, fspd);
    if (fpow < powerthreshold_hi)
      return rampfun(fpow, fspd);
    return rampfun(powerthreshold_hi, fspd);
  }
}
