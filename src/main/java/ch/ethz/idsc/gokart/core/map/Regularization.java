// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;

// TODO JPH/MH make this into a 'library' function
/* package */ enum Regularization {
  ;
  /** @param vector
   * @param factor for instance 0.01
   * @param closed
   * @return */
  static Tensor of(Tensor vector, Scalar factor, boolean closed) {
    Tensor regVec = Tensors.empty();
    if (!closed) {
      // do we have convolution?
      // TODO MH yes: ListConvolve or ListCorrelate
      regVec.append(Quantity.of(0, SI.METER));
      for (int i = 1; i < vector.length() - 1; i++) {
        regVec.append(Mean.of( //
            Tensors.of(vector.Get(i - 1), vector.Get(i + 1))));
      }
      regVec.append(Quantity.of(0, SI.METER));
    } else {
      // do we have convolution?
      // TODO MH yes: ListConvolve or ListCorrelate
      regVec.append(Mean.of( //
          Tensors.of(vector.Get(vector.length() - 1), vector.Get(1))));
      for (int i = 1; i < vector.length() - 1; i++) {
        regVec.append(Mean.of( //
            Tensors.of(vector.Get(i - 1), vector.Get(i + 1))));
      }
      regVec.append(Mean.of( //
          Tensors.of(vector.Get(vector.length() - 2), vector.Get(0))));
    }
    return regVec.subtract(vector).multiply(factor);
  }
}
