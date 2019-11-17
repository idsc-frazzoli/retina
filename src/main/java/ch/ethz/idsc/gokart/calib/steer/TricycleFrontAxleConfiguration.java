// code by ta, jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.Serializable;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public class TricycleFrontAxleConfiguration implements AxleConfiguration, Serializable {
  private final WheelConfiguration wheelConfiguration;

  /** @param scalar with unit SCE */
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
