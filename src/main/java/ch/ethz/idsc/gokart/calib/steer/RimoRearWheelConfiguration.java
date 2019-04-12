// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum RimoRearWheelConfiguration implements TwoWheelConfiguration {
  INSTANCE;
  // ---
  private final WheelConfiguration wheelConfigurationL = new WheelConfiguration(Tensors.of( //
      Quantity.of(0, SI.METER), //
      ChassisGeometry.GLOBAL.yTireRear, //
      RealScalar.ZERO));
  private final WheelConfiguration wheelConfigurationR = new WheelConfiguration(Tensors.of( //
      Quantity.of(0, SI.METER), //
      ChassisGeometry.GLOBAL.yTireRear.negate(), //
      RealScalar.ZERO));

  @Override // from AxleConfiguration
  public WheelConfiguration _left() {
    return wheelConfigurationL;
  }

  @Override // from AxleConfiguration
  public WheelConfiguration right() {
    return wheelConfigurationR;
  }
}
