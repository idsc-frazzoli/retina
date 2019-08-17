// code by jph
package ch.ethz.idsc.retina.joystick;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

public enum ManualControls {
  ;
  public static final ManualControlInterface PASSIVE = new ManualControlAdapter( //
      RealScalar.ZERO, RealScalar.ZERO, RealScalar.ZERO, Tensors.vector(0, 0), false, false);
}
