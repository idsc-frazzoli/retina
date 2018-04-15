// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** operator returns RealScalar.ZERO or RealScalar.ONE */
public class PRBS7Signal implements ScalarUnaryOperator {
  private static final Mod MOD = Mod.function(127);
  // ---
  private final Interpolation interpolation;

  /** @param width of single bit */
  public PRBS7Signal(Scalar width) {
    Tensor vector = PRBS7.sequence();
    interpolation = MappedInterpolation.of(vector, tensor -> MOD.of(tensor.divide(width)));
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return interpolation.get(Tensors.of(scalar)).Get();
  }
}
