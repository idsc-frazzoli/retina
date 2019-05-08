// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class RimoFrontAxleConfiguration implements AxleConfiguration {
  private final WheelConfiguration[] wheelConfiguration;

  public RimoFrontAxleConfiguration(Scalar scalar) {
    wheelConfiguration = new WheelConfiguration[] { //
        new WheelConfiguration(Tensors.of( //
            ChassisGeometry.GLOBAL.xAxleRtoF, //
            ChassisGeometry.GLOBAL.yTireFront, //
            FrontWheelAngleMapping._LEFT.getAngleFromSCE(scalar)), //
            RimoTireConfiguration.FRONT), //
        new WheelConfiguration(Tensors.of( //
            ChassisGeometry.GLOBAL.xAxleRtoF, //
            ChassisGeometry.GLOBAL.yTireFront.negate(), //
            FrontWheelAngleMapping.RIGHT.getAngleFromSCE(scalar)), //
            RimoTireConfiguration.FRONT) };
  }

  @Override // from AxleConfiguration
  public WheelConfiguration wheel(int index) {
    return wheelConfiguration[index];
  }
}
