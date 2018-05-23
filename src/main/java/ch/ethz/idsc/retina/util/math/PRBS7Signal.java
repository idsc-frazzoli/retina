// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.MappedInterpolation;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** operator returns RealScalar.ZERO or RealScalar.ONE */
/* package */ enum PRBS7Signal {
  ;
  private static final Mod MOD = Mod.function(127);

  /** @param width of single bit */
  public static ScalarUnaryOperator create(Scalar width) {
    return MappedInterpolation.of(PRBS7.sequence(), tensor -> MOD.of(tensor.divide(width)))::At;
  }
}
