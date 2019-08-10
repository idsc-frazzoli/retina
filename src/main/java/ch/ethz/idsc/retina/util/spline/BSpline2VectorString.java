// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/** function is defined for values in the interval [0, n - 2] */
/* package */ class BSpline2VectorString extends BSpline2Vector {
  private final Clip clip;

  public BSpline2VectorString(int n, int der) {
    super(n, der);
    clip = Clips.interval(0, n - 2);
  }

  @Override
  public Tensor apply(Scalar x) {
    clip.requireInside(x);
    return Tensors.vector(i -> getBasisElement(i, x), n);
  }

  private Scalar getBasisElement(int i, Scalar x) {
    Scalar value = x.add(RealScalar.of(2 - i));
    return bSpline2D.apply(value);
  }
}
