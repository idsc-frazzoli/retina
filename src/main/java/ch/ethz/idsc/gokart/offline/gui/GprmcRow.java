// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.retina.util.gps.Gprmc;
import ch.ethz.idsc.retina.util.gps.GprmcListener;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.LinearColorDataGradient;

/* package */ class GprmcRow extends GokartLogImageRow implements GprmcListener {
  private static final ColorDataGradient COLOR_DATA_GRADIENT = //
      LinearColorDataGradient.of(Tensors.fromString("{{0, 0, 0, 255}, {64, 64, 64, 255}, {96, 96, 0, 255}}"));
  // ---
  private Scalar scalar = RealScalar.ZERO;

  @Override // from GprmcListener
  public void gprmcReceived(Gprmc gprmc) {
    scalar = gprmc.isValid() //
        ? RealScalar.ONE
        : RationalScalar.HALF;
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
    return "gprmc";
  }
}
