// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Mod;

public enum So2AlignmentError {
  ;

  private final static Mod MOD = Mod.function(Pi.TWO);

  public static Scalar of(Scalar a1, Scalar a2) {
    Scalar diff = MOD.apply(a1.subtract(a2));
    return Min.of(diff, Pi.TWO.subtract(diff));
  }
}
