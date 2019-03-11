// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class VelocityEstimationConfig {
  public static final VelocityEstimationConfig GLOBAL = AppResources.load(new VelocityEstimationConfig());
  // ---
  /** How much does the computed velocity from lidar correct the integrated velocity */
  public Scalar correctionFactor = RealScalar.of(0.01);
  /** Rotation filtering */
  public Scalar rotFilter = RealScalar.of(0.5);
}
