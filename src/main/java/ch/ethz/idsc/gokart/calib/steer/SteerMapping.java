// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;

public interface SteerMapping {
  /** @param steerColumnInterface
   * @return angle of imaginary center front wheel without unit but with interpretation in radian
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  Scalar getAngleFromSCE(SteerColumnInterface steerColumnInterface);

  /** @param scalar with unit SCE
   * @return angle of imaginary center front wheel without unit but with interpretation in radian */
  Scalar getAngleFromSCE(Scalar scalar);

  /** @param angle of imaginary center front wheel unitless with interpretation in radian
   * @return steer column encoder value with unit "SCE" */
  // FIXME JPH "fromAngle" in fact receives turning ratio m^-1 !?
  Scalar getSCEfromAngle(Scalar angle);
}
