// code by mh, jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/* package */ class Regularization {
  private final GeodesicInterface geodesicInterface;
  private final Scalar factor;

  public Regularization(GeodesicInterface geodesicInterface, Scalar factor) {
    this.geodesicInterface = geodesicInterface;
    this.factor = factor;
  }

  /** @param tensor
   * @param factor for instance 0.01
   * @param cyclic
   * @return */
  public Tensor apply(Tensor tensor, boolean cyclic) {
    Tensor center = Tensors.empty();
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    if (!cyclic) {
      center.append(tensor.get(0));
      for (int i = 1; i < length - 1; ++i)
        center.append(geodesicInterface.split(tensor.get(i - 1), tensor.get(i + 1), RationalScalar.HALF));
      center.append(Last.of(tensor));
    } else {
      center.append(geodesicInterface.split(tensor.get(length - 1), tensor.get(1), RationalScalar.HALF));
      for (int i = 1; i < length - 1; ++i)
        center.append(geodesicInterface.split(tensor.get(i - 1), tensor.get(i + 1), RationalScalar.HALF));
      center.append(geodesicInterface.split(tensor.get(length - 2), tensor.get(0), RationalScalar.HALF));
    }
    // TODO JPH the following is illegal for anything other than RnGeodesic
    return geodesicInterface.split(tensor, center, factor);
  }
}
