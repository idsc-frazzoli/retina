// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.qty.Boole;

/* package */ class AutonomousButtonRow extends GokartLogImageRow implements ManualControlListener {
  private Scalar scalar = RealScalar.ZERO;

  @Override
  public void manualControl(ManualControlInterface manualControlInterface) {
    scalar = Boole.of(manualControlInterface.isAutonomousPressed());
  }

  @Override
  public Scalar get() {
    return scalar;
  }

  @Override
  public ColorDataGradient getColorDataGradient() {
    return ColorDataGradients.AVOCADO;
  }

  @Override
  public String getName() {
    return "autonomous button";
  }
}
