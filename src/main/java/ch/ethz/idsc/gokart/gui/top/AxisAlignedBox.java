// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class AxisAlignedBox {
  private static final Scalar HALF = RealScalar.of(0.5);
  // ---
  private final Scalar pos;
  private final Scalar neg;

  public AxisAlignedBox(Scalar width) {
    pos = width.multiply(HALF);
    neg = pos.negate();
  }

  public Tensor alongX(Scalar value) {
    return Tensors.matrix(new Scalar[][] { //
        { RealScalar.ZERO, neg }, //
        { value, neg }, //
        { value, pos }, //
        { RealScalar.ZERO, pos } });
  }

  public Tensor alongY(Scalar value) {
    return Tensors.matrix(new Scalar[][] { //
        { neg, RealScalar.ZERO }, //
        { neg, value }, //
        { pos, value }, //
        { pos, RealScalar.ZERO } });
  }
}
