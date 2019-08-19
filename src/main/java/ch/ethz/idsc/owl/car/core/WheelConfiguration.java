// code by jph
package ch.ethz.idsc.owl.car.core;

import java.io.Serializable;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Tensor;

public class WheelConfiguration implements Serializable {
  private final Tensor xya;
  private final TireConfiguration tireConfiguration;
  private final Se2GroupElement se2GroupElement_inverse;

  /** @param xya position of wheel in vehicle frame in the form {x[m], y[m], angle}
   * if the wheel moves along the x-axis, i.e. the wheel axis is fixed to the y-axis the angle == 0
   * @param tireConfiguration */
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
