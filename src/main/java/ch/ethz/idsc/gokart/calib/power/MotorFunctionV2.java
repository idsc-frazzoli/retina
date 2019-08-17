// code by mheim based on matlab code
package ch.ethz.idsc.gokart.calib.power;

/** linear */
/* package */ class MotorFunctionV2 extends MotorFunctionBase {
  static final MotorFunctionBase INSTANCE = new MotorFunctionV2();
  // ---
  static final float P0 = -0.3223f;
  static final float PPOWER = 0.001855f;
  static final float PVEL = -0.0107f;

  static float rampfun(float fpow, float fvel) {
    return P0 + PPOWER * fpow + PVEL * fvel;
  }

  // ---
  static final float POWER_THRESHOLD_HI = 1300f;
  static final float POWER_THRESHOLD_LO = -660f;
  static final float PTLRAMP = -10f;

  private MotorFunctionV2() {
    // ---
  }

  @Override // from MotorFunctionBase
  float forwardacc(float fspd, float fpow) {
    float lowpowerlimit = POWER_THRESHOLD_LO + PTLRAMP * fspd;
    if (fpow < lowpowerlimit)
      return rampfun(lowpowerlimit, fspd);
    if (fpow < POWER_THRESHOLD_HI)
      return rampfun(fpow, fspd);
    return rampfun(POWER_THRESHOLD_HI, fspd);
  }
}
