// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartPoseInterface {
  /** @return {x[m], y[m], angle[]} */
  Tensor getPose();
}
