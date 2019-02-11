// code by mh
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class SelfCalibratingBrakeFunction extends AbstractBrakeFunction {
  private Scalar curveCorrectionFactor = RealScalar.ONE;

  @Override
  public final Scalar getDeceleration(Scalar brakingPosition) {
    return getDeceleration(brakingPosition, curveCorrectionFactor);
  }

  @Override
  public Scalar getNeededBrakeActuation(Scalar wantedDeceleration) {
    return super.getNeededBrakeActuation(wantedDeceleration, curveCorrectionFactor);
  }

  public Scalar getBrakeFadeFactor() {
    return curveCorrectionFactor;
  }

  /** do correction step
   * only call this when you are actively using
   * @param expectedBrakingDeceleration
   * @param realBrakingDeceleration
   * @param gokartSpeed
   * @param wheelSpeed */
  public void correctBraking(//
      Scalar expectedBrakingDeceleration, //
      Scalar realBrakingDeceleration, //
      Scalar gokartSpeed, //
      Scalar wheelSpeed) {
    Scalar slipRatio = wheelSpeed.divide(wheelSpeed);
    boolean lockedUp = Scalars.lessThan(//
        slipRatio, //
        BrakeFunctionConfig.GLOBAL.lockupRatio);
    boolean tooSlow = Scalars.lessThan(//
        gokartSpeed, //
        BrakeFunctionConfig.GLOBAL.speedThreshold);
    boolean notEnoughBraking = Scalars.lessThan(//
        expectedBrakingDeceleration, //
        BrakeFunctionConfig.GLOBAL.decelerationThreshold);
    if (!lockedUp && !tooSlow && !notEnoughBraking) {
      Scalar newCurveCorrectionFactor = realBrakingDeceleration.divide(expectedBrakingDeceleration).multiply(curveCorrectionFactor);
      // curveCorrectionFactor = (Scalar) geodesicIIR1Filter.apply(newCurveCorrectionFactor);
      // geodesic filter is not modifiable
      // TODO JPH/MH
      Scalar alpha = BrakeFunctionConfig.GLOBAL.geodesicFilterAlpha;
      curveCorrectionFactor = RnGeodesic.INSTANCE.split(curveCorrectionFactor, newCurveCorrectionFactor, alpha).Get();
      // curveCorrectionFactor = RealScalar.ONE.subtract(alpha).multiply(curveCorrectionFactor).add(alpha.multiply(newCurveCorrectionFactor));
      System.out.println(curveCorrectionFactor);
    }
  }
}
