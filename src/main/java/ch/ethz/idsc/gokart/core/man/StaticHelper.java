// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;

/* package */ enum StaticHelper {
  ;
  public static Tensor incrSteps(int n) {
    Tensor tensor = Tensors.empty();
    for (Tensor _s : Subdivide.of(.03, .5, n)) {
      Scalar s = (Scalar) _s;
      tensor.append(UnitVector.of(3, 0).multiply(s));
      tensor.append(UnitVector.of(3, 0).multiply(s.negate()));
    }
    return Tensor.of(tensor.flatten(-1));
  }
}
