// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.Scalar;

public enum So2AlignmentError {
  ;

  public static Scalar of(Scalar a1, Scalar a2) {
    return So2.MOD.apply(a1.subtract(a2)).abs();
  }
}
