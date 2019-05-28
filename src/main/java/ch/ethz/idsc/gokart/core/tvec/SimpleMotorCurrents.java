// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public final class SimpleMotorCurrents extends AbstractMotorCurrents {
  public SimpleMotorCurrents(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override // from MotorCurrentsInterface
  public Tensor fromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration) {
    Scalar wantedZTorque = wantedZTorque(torqueVectoringConfig.getDynamicAndStatic(angularSlip), angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return StaticHelper.getAdvancedMotorCurrents(wantedAcceleration, wantedZTorque, angularSlip.tangentSpeed());
  }
}
