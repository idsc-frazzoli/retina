// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum GokartPoseHelper {
  ;
  /** @param state vector with units {x[m], y[m], angle[]}
   * @return */
  public static Tensor toSE2Matrix(Tensor state) {
    return Se2Utils.toSE2Matrix(toUnitless(state));
  }

  /** @param state of the form {x[m], y[m], angle}
   * @return {x, y, angle} */
  public static Tensor toUnitless(Tensor state) {
    return Tensors.of( //
        Magnitude.METER.apply(state.Get(0)), //
        Magnitude.METER.apply(state.Get(1)), //
        state.Get(2));
  }
}
