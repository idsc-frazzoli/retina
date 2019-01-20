// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;

public class ImprovedNormalizedTorqueVectoring extends ImprovedTorqueVectoring {
  private static final PowerLookupTable POWER_LOOKUP_TABLE = PowerLookupTable.getInstance();

  // ---
  public ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override // from TorqueVectoringInterface
  public final Tensor powers( //
      Scalar expectedRotationPerMeterDriven, //
      Scalar meanTangentSpeed, //
      Scalar angularSlip, //
      Scalar wantedPower, //
      Scalar realRotation) {
    // wrapper for torque vectoring method
    Scalar wantedAcceleration = POWER_LOOKUP_TABLE.getNormalizedAccelerationTorqueCentered(wantedPower, meanTangentSpeed);
    Tensor motorCurrents = getMotorCurrentsFromAcceleration( //
        expectedRotationPerMeterDriven, //
        meanTangentSpeed, //
        angularSlip, //
        wantedAcceleration, //
        realRotation);
    return motorCurrents.divide(ManualConfig.GLOBAL.torqueLimit);
  }

  /** get torque vectoring motor currents corresponding to the wanted rotation speed
   * (this can also be used externally!)
   * @param expectedRotationPerMeterDriven [1/s] TODO JPH/MH unit
   * @param meanTangentSpeed [m/s]
   * @param angularSlip [1/s]
   * @param wantedAcceleration [m/s^2]
   * @param realRotation [1/s]
   * @return the motor currents [ARMS] */
  public Tensor getMotorCurrentsFromAcceleration( //
      Scalar expectedRotationPerMeterDriven, //
      Scalar meanTangentSpeed, //
      Scalar angularSlip, //
      Scalar wantedAcceleration, //
      Scalar realRotation) {
    Scalar dynamicComponent = getDynamicComponent(angularSlip);
    Scalar staticComponent = getStaticComponent(expectedRotationPerMeterDriven, meanTangentSpeed);
    // ---
    Scalar wantedZTorque = wantedZTorque( //
        dynamicComponent.add(staticComponent), // One
        realRotation);
    // left and right power prefer power over Z-torque
    return getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, meanTangentSpeed);
  }

  /** @param wantedAcceleration [m*s^-2]
   * @param wantedZTorque [ONE] TODO MH currently dimensionless. Should we change that?
   * @param velocity [m/s]
   * @return the required motor currents [ARMS] */
  // TODO JPH/MH write tests specifically for method getAdvancedMotorCurrents
  public static Tensor getAdvancedMotorCurrents(Scalar wantedAcceleration, Scalar wantedZTorque, Scalar velocity) {
    Tensor minMax = POWER_LOOKUP_TABLE.getMinMaxAcceleration(velocity);
    Scalar min = minMax.Get(0);
    Scalar max = minMax.Get(1);
    Scalar halfRange = max.subtract(min).divide(RealScalar.of(2.0));
    Scalar mid = (Scalar) Mean.of(minMax);
    // get acceleration remapped to [-1,1]
    // TODO JPH/MH find handy Tensor function
    Scalar remappedMeanAcceleration = //
        wantedAcceleration.subtract(mid).divide(halfRange);//
    // get clipped individual accelerations
    Tensor remappedAccelerations = TorqueVectoringClip.of( //
        remappedMeanAcceleration.subtract(wantedZTorque), //
        remappedMeanAcceleration.add(wantedZTorque));
    // remap again to acceleration space
    Tensor wantedAccelerations = remappedAccelerations.multiply(halfRange).map(mid::add);
    return Tensors.of( //
        POWER_LOOKUP_TABLE.getNeededCurrent(wantedAccelerations.Get(0), velocity), //
        POWER_LOOKUP_TABLE.getNeededCurrent(wantedAccelerations.Get(1), velocity));
  }
}
