// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;

public class SlamResult {
  private final Tensor tensor;
  private final Scalar ratio;

  /** @param tensor
   * @param ratio in the interval [0, 1] */
  public SlamResult(Tensor tensor, Scalar ratio) {
    this.tensor = tensor;
    this.ratio = Clip.unit().requireInside(ratio);
  }

  public Tensor getTransform() {
    return tensor;
  }

  /** @return in the interval [0, 1] */
  public Scalar getMatchRatio() {
    return ratio;
  }
}
