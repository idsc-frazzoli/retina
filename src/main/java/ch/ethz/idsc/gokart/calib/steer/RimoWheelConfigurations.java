// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Scalar;

public enum RimoWheelConfigurations {
  ;
  /** @param scalar with unit "SCE"
   * @return list of wheels: front-left, front-right, rear-left, rear-right */
  public static List<WheelConfiguration> fromSCE(Scalar scalar) {
    AxleConfiguration fac = RimoAxleConfiguration.frontFromSCE(scalar);
    AxleConfiguration rac = RimoAxleConfiguration.rear();
    return Arrays.asList( //
        fac.wheel(0), //
        fac.wheel(1), //
        rac.wheel(0), //
        rac.wheel(1));
  }
}
