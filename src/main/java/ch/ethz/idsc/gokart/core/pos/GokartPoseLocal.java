// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum GokartPoseLocal implements GokartPoseInterface {
  INSTANCE;
  // ---
  private static final Tensor IDENTITY = Tensors.fromString("{0.0[m], 0.0[m], 0.0}");

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return IDENTITY.copy();
  }
}
