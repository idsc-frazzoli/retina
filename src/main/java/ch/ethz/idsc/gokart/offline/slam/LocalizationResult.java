// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class LocalizationResult {
  public final Scalar time;
  public final Tensor pose_xyt; // {x, y, theta} without units
  public final Scalar quality;

  /** @param time
   * @param pose_xyt
   * @param quality */
  public LocalizationResult(Scalar time, Tensor pose_xyt, Scalar quality) {
    this.time = time;
    this.pose_xyt = pose_xyt.unmodifiable();
    this.quality = quality;
  }
}
