// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.sophus.planar.Cross2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// TODO OWL V027 obsolete
public enum Det2D {
  ;
  /** @param p {px, py}
   * @param q {qx, qy}
   * @return px * qy - py * qx */
  public static Scalar of(Tensor p, Tensor q) {
    return Cross2D.of(p).dot(q).Get();
  }
}
