package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class TricycleFrontAxleConfiguration implements AxleConfiguration {
  private final WheelConfiguration wheelConfiguration;

  public TricycleFrontAxleConfiguration(Scalar scalar) {
    wheelConfiguration = new WheelConfiguration(Tensors.of( //
        RimoAxleConstants.xAxleRtoF, //
        RimoAxleConstants.yTireFront.zero(), //
        CubicAngleMapping.instance().getAngleFromSCE(scalar)), //
        RimoTireConfiguration.FRONT);
  }

  @Override
  public WheelConfiguration wheel(int wheel) {
    return wheelConfiguration;
  }
}
