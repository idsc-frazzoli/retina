// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** operator returns RealScalar.ZERO or RealScalar.ONE */
public class VectorSignal implements ScalarUnaryOperator {
  private final Interpolation interpolation;

  /** @param signal
   * @param width of single bit
   * @param amplitude */
  public VectorSignal(Tensor signal, Scalar width) {
    Mod MOD = Mod.function(signal.length());
    interpolation = MappedInterpolation.of(signal, tensor -> MOD.of(tensor.divide(width)));
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar scalar) {
    return interpolation.At(scalar);
  }
}
