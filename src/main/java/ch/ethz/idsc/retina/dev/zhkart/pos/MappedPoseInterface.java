// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Tensor;

public interface MappedPoseInterface extends GokartPoseInterface {
  void setPose(Tensor pose);
}
