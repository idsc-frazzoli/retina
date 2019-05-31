// code by mh
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ abstract class AbstractMotorCurrents implements MotorCurrentsInterface {
  final TorqueVectoringConfig torqueVectoringConfig;

  protected AbstractMotorCurrents(TorqueVectoringConfig torqueVectoringConfig) {
    this.torqueVectoringConfig = torqueVectoringConfig;
  }

  final Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    if (Sign.isNegative(realRotation.multiply(wantedZTorque))) {
      Scalar scalar = Clips.unit().apply(realRotation.abs().multiply(torqueVectoringConfig.ks));
      Scalar stabilizerFactor = RealScalar.ONE.subtract(scalar);
      return wantedZTorque.multiply(stabilizerFactor);
    }
    return wantedZTorque;
  }
}
