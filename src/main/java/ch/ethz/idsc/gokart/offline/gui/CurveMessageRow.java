// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.ref.TensorListener;

/* package */ class CurveMessageRow extends GokartLogImageRow implements TensorListener {
  private Scalar scalar = RealScalar.ZERO;

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    scalar = RealScalar.ONE;
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = scalar;
    scalar = RealScalar.ZERO;
    return value;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.BONE;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "curve present";
  }
}
