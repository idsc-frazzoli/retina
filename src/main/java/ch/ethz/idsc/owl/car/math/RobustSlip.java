// code by edo and jph
package ch.ethz.idsc.owl.car.math;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Hypot;
import ch.ethz.idsc.tensor.red.Norm;

/** robust computation of slip
 * 
 * Important: {@link Pacejka3} is not continuous for several input
 * Tensors.vector(0, 1);
 * Tensors.vector(0, 0); */
public class RobustSlip implements SlipInterface, Serializable {
  private static final TensorUnaryOperator NORMALIZE = NormalizeUnlessZero.with(Norm._2);
  private final Tensor mu;

  /** if U == {rtw, 0} that means no slip
   * 
   * @param pacejka3
   * @param U ground speed in coordinate system of tire
   * @param rtw == radius * rate of wheel */
  public RobustSlip(Pacejka3 pacejka3, Tensor U, Scalar rtw) {
    final Scalar ux = U.Get(0).subtract(rtw); // effective speed of tire (longitude)
    final Scalar uy = U.Get(1);
    final Scalar total = Scalars.isZero(rtw) //
        ? pacejka3.limit()
        : pacejka3.apply(Hypot.of(ux, uy).divide(rtw));
    mu = NORMALIZE.apply(Tensors.of(ux, uy)).multiply(total.negate());
  }

  @Override // from SlipInterface
  public Tensor slip() {
    return mu;
  }
}
