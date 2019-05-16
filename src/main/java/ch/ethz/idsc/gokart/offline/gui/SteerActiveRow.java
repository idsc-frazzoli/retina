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

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    scalar = Boole.of(steerGetEvent.isActive());
  }

  @Override
  public Scalar get() {
    return scalar;
  }

  @Override
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.COPPER;
  }
}
