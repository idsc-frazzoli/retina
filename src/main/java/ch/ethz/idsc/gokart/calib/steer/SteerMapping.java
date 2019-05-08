// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.tensor.Scalar;

public interface SteerMapping {
  /** @param steerColumnInterface
   * @return turning ratio for every meter driven with unit [m^-1]
   * @throws Exception if {@link SteerColumnInterface#isSteerColumnCalibrated()} returns false */
  Scalar getRatioFromSCE(SteerColumnInterface steerColumnInterface);

  /** @param scalar with unit SCE
   * @return turning ratio for every meter driven with unit [m^-1] */
  Scalar getRatioFromSCE(Scalar scalar);

  /** @param turning ratio for every meter driven with unit [m^-1]
   * @return steer column encoder value with unit "SCE" */
  Scalar getSCEfromRatio(Scalar ratio);
}
