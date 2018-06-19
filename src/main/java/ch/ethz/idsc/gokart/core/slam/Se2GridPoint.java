// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class Se2GridPoint {
  private final int x;
  private final int y;
  private final int t;
  private final Tensor tangent;
  private final Tensor coord;

  public Se2GridPoint(Scalar shift, Scalar angle, int x, int y, int t) {
    this.x = x;
    this.y = y;
    this.t = t;
    tangent = Tensors.of(shift, shift, angle).pmul(index());
    coord = Se2Exp.of(tangent);
  }

  /** @return affine transformation matrix from SE2 with dimensions 3 x 3 */
  public Tensor matrix() {
    return coord;
  }

  public Tensor tangent() {
    return tangent;
  }

  public Tensor index() {
    return Tensors.vector(x, y, t);
  }
}
