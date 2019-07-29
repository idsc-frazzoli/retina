// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.MotorCurrentsInterface;
import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class CalibratedTorqueVectoring implements TorqueVectoringInterface {
  private static final PowerLookupTable POWER_LOOKUP_TABLE = PowerLookupTable.getInstance();
  // ---
  private final MotorCurrentsInterface motorCurrentsInterface;

  public CalibratedTorqueVectoring(MotorCurrentsInterface motorCurrentsInterface) {
    this.motorCurrentsInterface = motorCurrentsInterface;
  }

  @Override // from TorqueVectoringInterface
  public final Tensor powers(AngularSlip angularSlip, Scalar wantedPower) {
    return motorCurrentsInterface.fromAcceleration( //
        angularSlip, //
        POWER_LOOKUP_TABLE.getNormalizedAccelerationTorqueCentered(wantedPower, angularSlip.tangentSpeed())) //
        .divide(ManualConfig.GLOBAL.torqueLimit);
  }
}
