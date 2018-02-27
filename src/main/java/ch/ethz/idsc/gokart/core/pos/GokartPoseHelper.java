// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum GokartPoseHelper {
  ;
  /** @param state vector with units {x[m], y[m], angle[]}
   * @return */
  public static Tensor toSE2Matrix(Tensor state) {
    Scalar x = Magnitude.METER.apply(state.Get(0));
    Scalar y = Magnitude.METER.apply(state.Get(1));
    Scalar angle = state.Get(2);
    return Se2Utils.toSE2Matrix(Tensors.of(x, y, angle));
  }
}
