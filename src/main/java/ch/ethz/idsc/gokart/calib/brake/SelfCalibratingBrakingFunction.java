package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.sophus.filter.GeodesicIIR1Filter;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

public class SelfCalibratingBrakingFunction extends BrakingFunction {
  private static SelfCalibratingBrakingFunction INSTANCE = new SelfCalibratingBrakingFunction();
  private final GeodesicIIR1Filter geodesicIIR1Filter = //
      new GeodesicIIR1Filter(RnGeodesic.INSTANCE, SelfCalibratingBrakingFunctionConfig.GLOBAL.geodesicFilterAlpha);

  public static SelfCalibratingBrakingFunction getInstance() {
    return INSTANCE;
  }

  private Scalar curveCorrectionFactor = RealScalar.ONE;

  private SelfCalibratingBrakingFunction() {
    super();
  }

  @Override
  public Scalar getAcceleration(Scalar brakingPosition) {
    return super.getAcceleration(brakingPosition, curveCorrectionFactor);
  }

  @Override
  Scalar getNeededBrakeActuation(Scalar wantedAcceleration) {
    return super.getNeededBrakeActuation(wantedAcceleration, curveCorrectionFactor);
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
        SelfCalibratingBrakingFunctionConfig.GLOBAL.lockupRatio);
    boolean tooSlow = Scalars.lessThan(//
        gokartSpeed, //
        SelfCalibratingBrakingFunctionConfig.GLOBAL.speedThreshold);
    boolean notEnoughBraking = Scalars.lessThan(//
        expectedBrakingDeceleration, //
        SelfCalibratingBrakingFunctionConfig.GLOBAL.decelerationThreshold);
    if (!lockedUp && !tooSlow && !notEnoughBraking) {
      Scalar newCurveCorrectionFactor = realBrakingDeceleration.divide(expectedBrakingDeceleration).multiply(curveCorrectionFactor);
      // curveCorrectionFactor = (Scalar) geodesicIIR1Filter.apply(newCurveCorrectionFactor);
      // geodesic filter is not modifiable
      Scalar alpha = SelfCalibratingBrakingFunctionConfig.GLOBAL.geodesicFilterAlpha;
      curveCorrectionFactor = RealScalar.ONE.subtract(alpha).multiply(curveCorrectionFactor).add(alpha.multiply(newCurveCorrectionFactor));
    }
  }
}
