// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class StringBSpline2Vector extends BSpline2Vector {
  private final Clip clip;

  public StringBSpline2Vector(int n, int der) {
    super(n, der);
    clip = Clips.interval(0, n - 2);
  }

  @Override
  public Tensor apply(Scalar x) {
    Scalar xx = clip.apply(x);
    return Tensors.vector(i -> getBasisElement(i, xx), n);
  }

  private Scalar getBasisElement(int i, Scalar x) {
    Scalar value = x.subtract(RealScalar.of(i)).add(_2);
    return bSpline2D.apply(value);
  }
}
