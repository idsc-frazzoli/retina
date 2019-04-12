// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.sophus.group.Se2Adjoint;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class WheelConfiguration {
  private final Tensor xya;

  public WheelConfiguration(Tensor xya) {
    this.xya = xya;
  }

  /** @return {x[m], y[m], angle} */
  public Tensor local() {
    return xya.unmodifiable();
  }

  /** @return adjoint map that transforms speed vector from vehicle frame to wheel frame */
  public TensorUnaryOperator adjoint() {
    return Se2Adjoint.inverse(xya);
  }
}
