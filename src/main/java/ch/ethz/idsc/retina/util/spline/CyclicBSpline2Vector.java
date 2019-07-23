// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Mod;

/** function is periodic. values are mapped to the interval [0, n) using modulo. */
/* package */ class CyclicBSpline2Vector extends BSpline2Vector {
  private final Mod mod;

  public CyclicBSpline2Vector(int n, int der) {
    super(n, der);
    mod = Mod.function(n);
  }

  @Override
  public Tensor apply(Scalar x) {
    return Tensors.vector(i -> getBasisElement(i, x), n);
  }

  private Scalar getBasisElement(int i, Scalar x) {
    Scalar value = x.add(RealScalar.of(2 - i));
    return bSpline2D.apply(mod.apply(value));
  }
}
