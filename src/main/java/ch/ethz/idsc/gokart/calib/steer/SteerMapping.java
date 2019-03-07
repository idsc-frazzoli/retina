// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;

public interface SteerMapping {
  /** @param steerColumnInterface
   * @return angle of imaginary center front wheel without unit but with interpretation in radians
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface);

  /** @param scalar with unit SCE
   * @return angle of imaginary center front wheel without unit but with interpretation in radians */
  Scalar getAngleFromSCE(Scalar scalar);

  /** @param angle of imaginary center front wheel with unit "rad"
   * @return steer column encoder value with unit "SCE" */
  Scalar getSCEfromAngle(Scalar angle);
}
