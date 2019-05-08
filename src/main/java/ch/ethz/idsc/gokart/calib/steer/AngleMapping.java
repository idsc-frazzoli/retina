// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;

public interface AngleMapping {
  /** @param steerColumnInterface
   * @return angle with interpretation radian
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface);

  /** @param scalar with unit SCE
   * @return angle with interpretation radian */
  Scalar getAngleFromSCE(Scalar scalar);

  /** @param angle with interpretation radian
   * @return steer column encoder value with unit "SCE" */
  Scalar getSCEfromAngle(Scalar angle);
}
