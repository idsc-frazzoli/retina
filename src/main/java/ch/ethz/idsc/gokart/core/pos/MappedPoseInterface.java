// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface MappedPoseInterface extends GokartPoseInterface {
  /** @param pose vector of length 3 for instance {37.85[m], 38.89[m], -0.5658221}
   * @param quality value in the interval [0, 1] */
  void setPose(Tensor pose, Scalar quality);

  // TODO document!
  GokartPoseEvent getPoseEvent();
}
