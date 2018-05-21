// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** operator returns RealScalar.ZERO or RealScalar.ONE */
public enum PRBS7SignedSignal {
  ;
  private static final Scalar[] VALUE = new Scalar[] { RealScalar.of(-1), RealScalar.of(+1) };
  private static final Mod MOD = Mod.function(127);

  /** @param width of single bit */
  public static ScalarUnaryOperator of(Scalar width) {
    Tensor vector = PRBS7.sequence().map(PRBS7SignedSignal::zeroToMinusOne);
    return MappedInterpolation.of(vector, tensor -> MOD.of(tensor.divide(width)))::At;
  }

  private static Scalar zeroToMinusOne(Scalar bit) {
    return VALUE[bit.number().intValue()]; // -1 or 1
  }
}
