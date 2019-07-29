// code by mh
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum SidewardsUnitVectors {
  ;
  private static final TensorUnaryOperator NORMALIZE = NormalizeUnlessZero.with(Norm._2);

  /** @param points_xy {{p0x, p0y}, {p1x, p1y}, ...}
   * @return */
  public static Tensor of(Tensor points_xy) {
    return Tensor.of(points_xy.stream() //
        .map(Tensor::negate) // TODO JPH the negate is not elegant
        .map(Cross::of) //
        .map(NORMALIZE));
  }
}
