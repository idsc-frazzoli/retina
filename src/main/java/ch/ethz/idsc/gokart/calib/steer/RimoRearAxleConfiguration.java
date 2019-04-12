// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RimoRearAxleConfiguration implements AxleConfiguration {
  INSTANCE;
  // ---
  private final WheelConfiguration[] wheelConfiguration = new WheelConfiguration[] { //
      new WheelConfiguration(Tensors.of( //
          Quantity.of(0, SI.METER), //
          ChassisGeometry.GLOBAL.yTireRear, //
          RealScalar.ZERO)), //
      new WheelConfiguration(Tensors.of( //
          Quantity.of(0, SI.METER), //
          ChassisGeometry.GLOBAL.yTireRear.negate(), //
          RealScalar.ZERO)) };

  @Override // from AxleConfiguration
  public WheelConfiguration wheel(int index) {
    return wheelConfiguration[index];
  }
}
