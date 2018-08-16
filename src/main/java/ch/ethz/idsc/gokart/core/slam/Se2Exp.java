// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum Se2Exp {
  ;
  /** maps an element x = (vx, vy, be) of the Lie-algebra se2 in standard coordinates:
   * [0 -be vx]
   * [+be 0 vy]
   * [+0 +0 +0]
   * to the corresponding 3x3 matrix in SE2.
   * 
   * @param x vector of length 3
   * @return matrix with dimensions 3x3 */
  public static Tensor of(Tensor x) {
    return Se2Utils.toSE2Matrix(Se2CoveringExponential.INSTANCE.exp(x));
  }
}
