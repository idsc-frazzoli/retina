// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface MappedPoseInterface extends GokartPoseInterface {
  /** @param pose vector of length 3
   * @param quality value in the interval [0, 1] */
  void setPose(Tensor pose, Scalar quality);
}
