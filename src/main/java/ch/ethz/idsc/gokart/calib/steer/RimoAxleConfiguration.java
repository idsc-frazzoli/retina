// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.tensor.Scalar;

public enum RimoAxleConfiguration {
  ;
  /** @param scalar with unit "SCE"
   * @return */
  public static AxleConfiguration frontFromSCE(Scalar scalar) {
    return new RimoFrontAxleConfiguration(scalar);
  }

  /** @return */
  public static AxleConfiguration rear() {
    return RimoRearAxleConfiguration.INSTANCE;
  }
}
