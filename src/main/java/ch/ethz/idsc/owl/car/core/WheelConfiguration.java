// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.gokart.offline.video.TireConfiguration;
import ch.ethz.idsc.sophus.group.Se2Adjoint;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class WheelConfiguration {
  private final Tensor xya;
  private final TireConfiguration tireConfiguration;

  public WheelConfiguration(Tensor xya, TireConfiguration tireConfiguration) {
    this.xya = xya;
    this.tireConfiguration = tireConfiguration;
  }

  /** @return {x[m], y[m], angle} */
  public Tensor local() {
    return xya.unmodifiable();
  }

  /** @return adjoint map that transforms speed vector from vehicle frame to wheel frame */
  public TensorUnaryOperator adjoint() {
    return Se2Adjoint.inverse(xya);
  }

  public TireConfiguration tireConfiguration() {
    return tireConfiguration;
  }
}
