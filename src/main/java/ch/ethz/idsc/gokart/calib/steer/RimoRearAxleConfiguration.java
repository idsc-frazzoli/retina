// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RimoRearAxleConfiguration implements AxleConfiguration {
  INSTANCE;
  // ---
  private final Scalar posY = Quantity.of(0.54, SI.METER);
  private final WheelConfiguration[] wheelConfiguration = { //
      new WheelConfiguration(Tensors.of(posY.zero(), posY, RealScalar.ZERO), //
          RimoTireConfiguration._REAR), //
      new WheelConfiguration(Tensors.of(posY.zero(), posY.negate(), RealScalar.ZERO), //
          RimoTireConfiguration._REAR) };

  @Override // from AxleConfiguration
  public WheelConfiguration wheel(int index) {
    return wheelConfiguration[index];
  }
}
