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
    AxleConfiguration front = RimoAxleConfiguration.frontFromSCE(scalar);
    AxleConfiguration _rear = RimoAxleConfiguration.rear();
    return Arrays.asList( //
        front.wheel(0), //
        front.wheel(1), //
        _rear.wheel(0), //
        _rear.wheel(1));
  }
}
