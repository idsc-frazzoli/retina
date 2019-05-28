// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

public abstract class AbstractImprovedTorqueVectoring extends AbstractTorqueVectoring {
  private static final PowerLookupTable POWER_LOOKUP_TABLE = PowerLookupTable.getInstance();

  public AbstractImprovedTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override // from TorqueVectoringInterface
  public final Tensor powers(AngularSlip angularSlip, Scalar wantedPower) {
    // wrapper for torque vectoring method
    Scalar wantedAcceleration = POWER_LOOKUP_TABLE.getNormalizedAccelerationTorqueCentered(wantedPower, angularSlip.tangentSpeed());
    return getMotorCurrentsFromAcceleration(angularSlip, wantedAcceleration).divide(ManualConfig.GLOBAL.torqueLimit);
  }

  @Override // from SimpleTorqueVectoring
  public final Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    if (Sign.isNegative(realRotation.multiply(wantedZTorque))) {
      Scalar scalar = Clips.unit().apply(realRotation.abs().multiply(torqueVectoringConfig.ks));
      Scalar stabilizerFactor = RealScalar.ONE.subtract(scalar);
      return wantedZTorque.multiply(stabilizerFactor);
    }
    return wantedZTorque;
  }

  /** get torque vectoring motor currents corresponding to the wanted rotation speed
   * (this can also be used externally!)
   * 
   * @param angularSlip
   * @param wantedAcceleration [m*s^-2]
   * @return the motor currents [ARMS] */
  public abstract Tensor getMotorCurrentsFromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration);
}
