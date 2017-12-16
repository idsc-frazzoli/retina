// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartPoseInterface {
  /** @return {x[m], y[m], angle[]} */
  Tensor getPose();

  // TODO not permanent!!! function exists only temporary
  void setPose(Tensor pose);
}
