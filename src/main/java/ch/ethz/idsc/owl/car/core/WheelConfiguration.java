// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.gokart.offline.video.TireConfiguration;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Tensor;

public class WheelConfiguration {
  private final Tensor xya;
  private final TireConfiguration tireConfiguration;
  private final Se2GroupElement se2GroupElement_inverse;

  public WheelConfiguration(Tensor xya, TireConfiguration tireConfiguration) {
    this.xya = xya;
    this.tireConfiguration = tireConfiguration;
    se2GroupElement_inverse = (Se2GroupElement) new Se2GroupElement(xya).inverse();
  }

  /** @return {x[m], y[m], angle} */
  public Tensor local() {
    return xya.unmodifiable();
  }

  /** @return adjoint map that transforms speed vector from vehicle frame to wheel frame */
  public Tensor adjoint(Tensor uvw) {
    return se2GroupElement_inverse.adjoint(uvw);
  }

  public TireConfiguration tireConfiguration() {
    return tireConfiguration;
  }
}
