// code by jph
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** cyclic mapped interpolation of vector that is stretched over a domain of width
 * vector.length() * width */
/* package */ class VectorSignal implements ScalarUnaryOperator {
  private final Interpolation interpolation;

  /** @param vector
   * @param width of single bit
   * @param amplitude */
  public VectorSignal(Tensor vector, Scalar width) {
    VectorQ.require(vector);
    Mod mod = Mod.function(vector.length());
    interpolation = MappedInterpolation.of(vector, tensor -> mod.of(tensor.divide(width)));
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return interpolation.At(scalar);
  }
}
