// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.ref.TensorListener;

/* package */ class CurveMessageRow extends MappedLogImageRow implements TensorListener {
  private static final ColorDataGradient COLOR_DATA_GRADIENT = //
      LinearColorDataGradient.of(Tensors.fromString("{{0, 0, 0, 255}, {64, 192, 64, 255}}"));
  // ---
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
    return COLOR_DATA_GRADIENT;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "curve present";
  }

  @Override // from DiscreteLogImageRow
  public Map<Scalar, String> legend() {
    LinkedHashMap<Scalar, String> linkedHashMap = new LinkedHashMap<>();
    linkedHashMap.put(Boole.of(false), "no");
    linkedHashMap.put(Boole.of(true), "yes");
    return linkedHashMap;
  }
}
