// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum GokartPoseHelper {
  ;
  /** @param state vector with units {x[m], y[m], angle[]}
   * @return */
  public static Tensor toSE2Matrix(Tensor state) {
    // Se2Utils.toSE2Matrix(state.extract(0, 2).map(Magnitude.METER).append(state.Get(2)));
    return Se2Utils.toSE2Matrix(toUnitless(state));
  }

  /** @param state of the form {x[m], y[m], angle}
   * @return {x, y, angle} */
  public static Tensor toUnitless(Tensor state) {
    return Tensors.of( //
        Magnitude.METER.apply(state.Get(0)), //
        Magnitude.METER.apply(state.Get(1)), //
        Magnitude.ONE.apply(state.Get(2)));
  }

  /** @param vector {x, y, angle}
   * @return {x[m], y[m], angle} */
  public static Tensor attachUnits(Tensor vector) {
    return Tensors.of( //
        Quantity.of(vector.Get(0), SI.METER), //
        Quantity.of(vector.Get(1), SI.METER), //
        vector.Get(2));
  }
}
