// code by gjoel
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.owl.math.planar.AssistedCurveIntersection;
import ch.ethz.idsc.owl.math.planar.Extract2D;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

// TODO remove as it comes to owl
public class SphereSe2CurveIntersection extends AssistedCurveIntersection {
  public SphereSe2CurveIntersection(Scalar radius) {
    super(radius);
  }

  @Override // from SimpleCurveIntersection
  protected Scalar distance(Tensor tensor) {
    return Norm._2.ofVector(Extract2D.FUNCTION.apply(tensor));
  }

  @Override // from SimpleCurveIntersection
  protected Tensor split(Tensor prev, Tensor next, Scalar scalar) {
    return Se2Geodesic.INSTANCE.split(prev, next, scalar);
  }
}
