// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clips;

public abstract class AbstractTorqueVectoring implements TorqueVectoringInterface {
  final TorqueVectoringConfig torqueVectoringConfig;

  protected AbstractTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  @Override // from TorqueVectoringInterface
  public final Tensor powers(AngularSlip angularSlip, Scalar wantedPower) {
    // compute differential torque (in ARMS as we do not use the power function yet)
    Scalar wantedZTorque = wantedZTorque(torqueVectoringConfig.getDynamicAndStatic(angularSlip), angularSlip.gyroZ());
    // left and right power prefer power over Z-torque
    return TorqueVectoringClip.from(Clips.absoluteOne().apply(wantedPower), wantedZTorque);
  }

  /** @param wantedZTorque unitless
   * @param realRotation s^-1
   * @return quantity with unit same as wantedZTorque */
  public abstract Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation);
}
