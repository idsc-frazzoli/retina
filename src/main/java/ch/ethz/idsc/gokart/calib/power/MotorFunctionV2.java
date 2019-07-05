// code by mheim based on matlab code
package ch.ethz.idsc.gokart.calib.power;

/** linear */
/* package */ class MotorFunctionV2 extends MotorFunctionBase {
  static final MotorFunctionBase INSTANCE = new MotorFunctionV2();

  private MotorFunctionV2() {
    // ---
  }

  @Override // from MotorFunctionBase
  float forwardacc(float fspd, float fpow) {
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
}
