// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

class RimoFrontAxleConfiguration implements AxleConfiguration {
  private final WheelConfiguration[] wheelConfiguration = new WheelConfiguration[2];

  public RimoFrontAxleConfiguration(Scalar scalar) {
    wheelConfiguration[0] = new WheelConfiguration(Tensors.of( //
        ChassisGeometry.GLOBAL.xAxleRtoF, //
        ChassisGeometry.GLOBAL.yTireFront, //
        FrontWheelSteerMapping._LEFT.getAngleFromSCE(scalar)));
    wheelConfiguration[1] = new WheelConfiguration(Tensors.of( //
        ChassisGeometry.GLOBAL.xAxleRtoF, //
        ChassisGeometry.GLOBAL.yTireFront.negate(), //
        FrontWheelSteerMapping.RIGHT.getAngleFromSCE(scalar)));
  }

  @Override // from AxleConfiguration
  public WheelConfiguration wheel(int index) {
    return wheelConfiguration[index];
  }
}
