// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** operator returns RealScalar.ZERO or RealScalar.ONE */
public class PRBS7SignedSignal implements ScalarUnaryOperator {
  private static final Scalar[] VALUE = new Scalar[] { RealScalar.of(-1), RealScalar.of(+1) };
  private static final Mod MOD = Mod.function(127);
  // ---
  private final Interpolation interpolation;

  /** @param width of single bit */
  public PRBS7SignedSignal(Scalar width) {
    Tensor vector = PRBS7.sequence().map(this::zeroToMinusOne);
    interpolation = MappedInterpolation.of(vector, tensor -> MOD.of(tensor.divide(width)));
  }

  @Override
  public Scalar apply(Scalar scalar) {
    return interpolation.get(Tensors.of(scalar)).Get();
  }

  private Scalar zeroToMinusOne(Scalar bit) {
    return VALUE[bit.number().intValue()]; // -1 or 1
  }
}
