// code by mcp
package ch.ethz.idsc.owl.bot.se2.pid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ enum RnUnitCircle {
  ;
  private static final ScalarUnaryOperator MOD = Mod.function(Pi.TWO, Pi.VALUE.negate());

  public static Scalar convert(Scalar angleOut) {
    return MOD.apply(angleOut);
  }
}
