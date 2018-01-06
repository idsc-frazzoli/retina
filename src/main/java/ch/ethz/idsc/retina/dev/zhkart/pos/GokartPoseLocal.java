// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;

public enum GokartPoseLocal implements MappedPoseInterface {
  INSTANCE;
  // ---
  private static final Tensor IDENTITY = Tensors.fromString("{0[m], 0[m], 0}").unmodifiable();

  @Override
  public Tensor getPose() {
    return IDENTITY;
  }

  @Override
  public void setPose(Tensor pose, Scalar quality) {
    throw TensorRuntimeException.of(pose, quality);
  }
}
