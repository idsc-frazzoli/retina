// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class Se2GridPoint {
  private final Tensor mask;
  private final Tensor tangent;
  private final Tensor coord;

  /** @param shift
   * @param angle
   * @param mask */
  public Se2GridPoint(Tensor mask, Scalar shift, Scalar angle) {
    this.mask = mask;
    tangent = Tensors.of(shift, shift, angle).pmul(mask);
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
    return mask;
  }
}
