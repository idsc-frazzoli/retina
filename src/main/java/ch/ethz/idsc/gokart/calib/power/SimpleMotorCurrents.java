// code by jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class SimpleMotorCurrents implements MotorCurrentsInterface {
  private final TorqueVectoringConfig torqueVectoringConfig;

  public SimpleMotorCurrents(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  @Override // from MotorCurrentsInterface
  public Tensor fromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration) {
    Scalar wantedZTorque = torqueVectoringConfig.wantedZTorque( //
        torqueVectoringConfig.getDynamicAndStatic(angularSlip), //
        angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return StaticHelper.getMotorCurrents(wantedAcceleration, wantedZTorque, angularSlip.tangentSpeed());
  }
}
