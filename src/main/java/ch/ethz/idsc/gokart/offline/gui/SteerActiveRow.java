// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Boole;

/* package */ class SteerActiveRow extends GokartLogImageRow implements SteerGetListener {
  private Scalar scalar = RealScalar.ZERO;

  @Override // from SteerGetListener
  public void getEvent(SteerGetEvent steerGetEvent) {
    scalar = Boole.of(steerGetEvent.isActive());
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    return scalar;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.COPPER;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "steer active";
  }
}
