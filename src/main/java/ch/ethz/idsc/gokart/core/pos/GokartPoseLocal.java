// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

public enum GokartPoseLocal implements MappedPoseInterface {
  INSTANCE;
  // ---
  private static final Tensor IDENTITY = Tensors.fromString("{0[m], 0[m], 0}").unmodifiable();

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return IDENTITY;
  }

  @Override // from MappedPoseInterface
  public void setPose(Tensor pose, Scalar quality) {
    throw TensorRuntimeException.of(pose, quality);
  }
}
