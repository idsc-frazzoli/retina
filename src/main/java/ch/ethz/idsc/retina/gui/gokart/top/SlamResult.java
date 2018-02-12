// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class SlamResult {
  private final Tensor tensor;
  private final Scalar ratio;

  public SlamResult(Tensor tensor, Scalar ratio) {
    this.tensor = tensor;
    this.ratio = ratio;
  }

  public Tensor getTransform() {
    return tensor;
  }

  /** @return sum of all grayscale color values of the pixels in the map
   * that coincide with a lidar sample. the maximum possible value is the
   * number of samples multiplied by 255 */
  public Scalar getMatchRatio() {
    return ratio;
  }
}
