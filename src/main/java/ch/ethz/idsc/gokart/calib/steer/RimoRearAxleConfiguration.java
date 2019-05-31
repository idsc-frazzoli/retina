// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum RimoRearAxleConfiguration implements AxleConfiguration {
  INSTANCE;
  // ---
  private final WheelConfiguration[] wheelConfiguration = { //
      new WheelConfiguration(Tensors.of( //
          RimoAxleConstants.yTireRear.zero(), //
          RimoAxleConstants.yTireRear, RealScalar.ZERO), //
          RimoTireConfiguration._REAR), //
      new WheelConfiguration(Tensors.of( //
          RimoAxleConstants.yTireRear.zero(), //
          RimoAxleConstants.yTireRear.negate(), RealScalar.ZERO), //
          RimoTireConfiguration._REAR) };

  @Override // from AxleConfiguration
  public WheelConfiguration wheel(int index) {
    return wheelConfiguration[index];
  }
}
