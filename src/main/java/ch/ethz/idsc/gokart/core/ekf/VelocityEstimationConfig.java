// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class VelocityEstimationConfig {
  public static final VelocityEstimationConfig GLOBAL = AppResources.load(new VelocityEstimationConfig());
  // ---
  /** How much does the computed velocity from lidar correct the integrated velocity */
  public Scalar velocityCorrectionFactor = RealScalar.of(0.01);
  /** How much does the computed velocity from lidar correct the integrated velocity */
  public Scalar poseCorrectionFactor = RealScalar.of(1.0);
  /** Rotation filtering */
  public Scalar rotFilter = RealScalar.of(0.5);
}
