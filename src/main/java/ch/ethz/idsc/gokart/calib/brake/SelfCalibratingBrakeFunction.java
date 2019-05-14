// code by mh
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class SelfCalibratingBrakeFunction extends AbstractBrakeFunction {
  private Scalar curveCorrectionFactor = RealScalar.of(1.0);

  @Override // from AbstractBrakeFunction
  Scalar getDeceleration(Scalar brakingPosition) {
    return getDeceleration(brakingPosition, curveCorrectionFactor);
  }

  @Override // from AbstractBrakeFunction
  Scalar getNeededBrakeActuation(Scalar wantedDeceleration) {
    return getNeededBrakeActuation(wantedDeceleration, curveCorrectionFactor);
  }

  public Scalar getBrakeFadeFactor() {
    return curveCorrectionFactor;
  }

  /** do correction step
   * only call function when in active use
   * 
   * @param expectedBrakingDeceleration with unit [m*s^-2]
   * @param realBrakingDeceleration with unit [m*s^-2]
   * @param gokartSpeed with unit [m*s^-1]
   * @param wheelSpeed with unit [m*s^-1] */
  public boolean correctBraking( //
      Scalar expectedBrakingDeceleration, //
      Scalar realBrakingDeceleration, //
      Scalar gokartSpeed, //
      Scalar wheelSpeed) {
    Scalar slipRatio = wheelSpeed.divide(gokartSpeed); // unitless
    boolean lockedUp = Scalars.lessThan( //
        slipRatio, //
        BrakeFunctionConfig.GLOBAL.lockupRatio);
    boolean tooSlow = Scalars.lessThan( //
        gokartSpeed, //
        BrakeFunctionConfig.GLOBAL.speedThreshold);
    boolean notEnoughBraking = Scalars.lessThan( //
        expectedBrakingDeceleration, //
        BrakeFunctionConfig.GLOBAL.decelerationThreshold);
    boolean correct = !lockedUp && !tooSlow && !notEnoughBraking;
    if (correct) {
      Scalar newCurveCorrectionFactor = //
          realBrakingDeceleration.divide(expectedBrakingDeceleration).multiply(curveCorrectionFactor);
      Scalar alpha = BrakeFunctionConfig.GLOBAL.geodesicFilterAlpha;
      curveCorrectionFactor = RnGeodesic.INSTANCE.split( //
          curveCorrectionFactor, newCurveCorrectionFactor, alpha).Get();
    }
    return correct;
  }
}
