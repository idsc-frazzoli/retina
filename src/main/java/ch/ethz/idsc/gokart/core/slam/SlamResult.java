// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clips;

public class SlamResult implements Serializable {
  private final Tensor matrix;
  private final Scalar quality;

  /** @param matrix with size 3 x 3
   * @param quality in the interval [0, 1] */
  public SlamResult(Tensor matrix, Scalar quality) {
    this.matrix = matrix;
    this.quality = Clips.unit().requireInside(quality);
  }

  public Tensor getTransform() {
    return matrix;
  }

  /** @return in the interval [0, 1] */
  public Scalar getQuality() {
    return quality;
  }
}
