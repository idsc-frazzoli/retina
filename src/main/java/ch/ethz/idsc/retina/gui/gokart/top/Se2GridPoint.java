// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Se2GridPoint {
  private final int x;
  private final int y;
  private final int t;
  private final Tensor coord;

  public Se2GridPoint(Scalar shift, Scalar angle, int x, int y, int t) {
    this.x = x;
    this.y = y;
    this.t = t;
    coord = Se2Exp.of(Tensors.of( //
        shift.multiply(DoubleScalar.of(x)), //
        shift.multiply(DoubleScalar.of(y)), //
        angle.multiply(DoubleScalar.of(t))));
  }

  /** @return affine transformation matrix from SE2 with dimensions 3 x 3 */
  public Tensor matrix() {
    return coord;
  }

  public Tensor index() {
    return Tensors.vector(x, y, t);
  }
}
