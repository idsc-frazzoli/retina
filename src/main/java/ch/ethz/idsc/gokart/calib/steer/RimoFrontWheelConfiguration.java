// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class RimoFrontWheelConfiguration implements TwoWheelConfiguration {
  public static TwoWheelConfiguration fromSCE(Scalar scalar) {
    return new RimoFrontWheelConfiguration(scalar);
  }

  // ---
  private final WheelConfiguration wheelConfigurationL;
  private final WheelConfiguration wheelConfigurationR;

  private RimoFrontWheelConfiguration(Scalar scalar) {
    wheelConfigurationL = new WheelConfiguration(Tensors.of( //
        ChassisGeometry.GLOBAL.xAxleRtoF, //
        ChassisGeometry.GLOBAL.yTireFront, //
        FrontWheelSteerMapping._LEFT.getAngleFromSCE(scalar)));
    wheelConfigurationR = new WheelConfiguration(Tensors.of( //
        ChassisGeometry.GLOBAL.xAxleRtoF, //
        ChassisGeometry.GLOBAL.yTireFront.negate(), //
        FrontWheelSteerMapping.RIGHT.getAngleFromSCE(scalar)));
  }

  @Override // from AxleConfiguration
  public WheelConfiguration _left() {
    return wheelConfigurationL;
  }

  @Override // from AxleConfiguration
  public WheelConfiguration right() {
    return wheelConfigurationR;
  }
}
