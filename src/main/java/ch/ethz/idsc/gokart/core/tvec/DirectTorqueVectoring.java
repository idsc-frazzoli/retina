// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.tensor.Scalar;

public final class DirectTorqueVectoring extends AbstractTorqueVectoring {
  public DirectTorqueVectoring(TorqueVectoringConfig torqueVectoringConfig) {
    super(torqueVectoringConfig);
  }

  @Override
  public Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation) {
    return wantedZTorque;
  }
}
