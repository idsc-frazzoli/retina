// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ImprovedNormalizedTorqueVectoring extends ImprovedTorqueVectoring {
  private static final PowerLookupTable POWER_LOOKUP_TABLE = PowerLookupTable.getInstance();

  // ---
  public ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override // from TorqueVectoringInterface
  public final Tensor powers(AngularSlip angularSlip, Scalar wantedPower) {
    // wrapper for torque vectoring method
    Scalar wantedAcceleration = POWER_LOOKUP_TABLE.getNormalizedAccelerationTorqueCentered(wantedPower, angularSlip.tangentSpeed());
    Tensor motorCurrents = getMotorCurrentsFromAcceleration(angularSlip, wantedAcceleration);
    return motorCurrents.divide(ManualConfig.GLOBAL.torqueLimit);
  }

  /** get torque vectoring motor currents corresponding to the wanted rotation speed
   * (this can also be used externally!)
   * 
   * @param angularSlip
   * @param wantedAcceleration [m/s^2]
   * @return the motor currents [ARMS] */
  public Tensor getMotorCurrentsFromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration) {
    Scalar dynamicComponent = getDynamicComponent(angularSlip.angularSlip());
    Scalar staticComponent = getStaticComponent(angularSlip.rotationPerMeterDriven(), angularSlip.tangentSpeed());
    // ---
    Scalar wantedZTorque = wantedZTorque(dynamicComponent.add(staticComponent), angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, angularSlip.tangentSpeed());
  }

  /** @param wantedAcceleration [m*s^-2]
   * @param wantedZTorque [ONE]
   * @param velocity [m/s]
   * @return vector of length 2 with required motor currents [ARMS] */
  // TODO JPH/MH write tests specifically for method getAdvancedMotorCurrents
  /* package */ static Tensor getAdvancedMotorCurrents(Scalar wantedAcceleration, Scalar wantedZTorque, Scalar velocity) {
    Tensor minMax = POWER_LOOKUP_TABLE.getMinMaxAcceleration(velocity);
    // get clipped individual accelerations
    PowerClip powerClip = new PowerClip(minMax.Get(0), minMax.Get(1));
    Tensor wantedAccelerations = //
        TorqueVectoringClip.from(powerClip.relative(wantedAcceleration), wantedZTorque) //
            .map(powerClip::absolute);
    return Tensors.of( //
        POWER_LOOKUP_TABLE.getNeededCurrent(wantedAccelerations.Get(0), velocity), //
        POWER_LOOKUP_TABLE.getNeededCurrent(wantedAccelerations.Get(1), velocity));
  }
}
