// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO JPH OWL V027
public enum Extract2D implements TensorUnaryOperator {
  FUNCTION;
  // ---
  /** @param tensor
   * @return first two entries of given tensor
   * @throws Exception if given tensor does not contain at least two elements */
  @Override
  public Tensor apply(Tensor tensor) {
    return tensor.extract(0, 2);
  }
}
