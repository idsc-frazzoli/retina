// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

public class ImprovedTorqueVectoring extends SimpleTorqueVectoring {
  public ImprovedTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override // from SimpleTorqueVectoring
  final Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    if (Sign.isNegative(realRotation.multiply(wantedZTorque))) {
      Scalar scalar = Clip.unit().apply(realRotation.abs().multiply(torqueVectoringConfig.ks));
      Scalar stabilizerFactor = RealScalar.ONE.subtract(scalar);
      return wantedZTorque.multiply(stabilizerFactor);
    }
    return wantedZTorque;
  }
}
