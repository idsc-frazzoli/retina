// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.retina.util.gps.Gprmc;
import ch.ethz.idsc.retina.util.gps.GprmcListener;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ class GprmcRow extends GokartLogImageRow implements GprmcListener {
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
    return ColorDataGradients.PASTEL;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "gprmc";
  }
}
