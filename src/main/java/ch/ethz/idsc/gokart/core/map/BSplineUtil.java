// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/** uses UniformBSpline2 to map control points */
/* package */ enum BSplineUtil {
  ;
  private static final TensorUnaryOperator NORMALIZE = NormalizeUnlessZero.with(Norm._2);

  public static Tensor getSidewardsUnitVectors(Tensor controlpointsXY, Tensor basisMatrix1Der) {
    Tensor forwardXY = basisMatrix1Der.dot(controlpointsXY);
    return Tensor.of(forwardXY.stream() //
        .map(Tensor::negate) // TODO MH/JPH the negate is not elegant
        .map(Cross::of) //
        .map(NORMALIZE));
  }
}
