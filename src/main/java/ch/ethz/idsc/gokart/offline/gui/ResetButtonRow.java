// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class ResetButtonRow extends GokartLogImageRow implements ManualControlListener {
  private Scalar scalar = RealScalar.ZERO;

  @Override // from GokartLogImageRow
  public void manualControl(ManualControlInterface manualControlInterface) {
    scalar = Max.of(scalar, Boole.of(manualControlInterface.isResetPressed()));
  }

  @Override // from GokartLogImageRow
  public Scalar getScalar() {
    Scalar value = scalar;
    scalar = RealScalar.ZERO;
    return value;
  }

  @Override // from GokartLogImageRow
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.AVOCADO;
  }

  @Override // from GokartLogImageRow
  public String getName() {
    return "reset button";
  }
}
