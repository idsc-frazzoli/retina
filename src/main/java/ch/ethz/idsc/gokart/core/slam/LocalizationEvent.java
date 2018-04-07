// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;

/** class is intentionally slam result
 * because slam result has a different interpretation as
 * localization event so mixing is no good */
public class LocalizationEvent {
  private final Tensor tensor;
  private final Scalar ratio;

  /** @param tensor
   * @param ratio in the interval [0, 1] */
  public LocalizationEvent(Tensor tensor, Scalar ratio) {
    this.tensor = tensor.unmodifiable();
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
