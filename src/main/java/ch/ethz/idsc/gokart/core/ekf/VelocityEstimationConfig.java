// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class VelocityEstimationConfig {
  public static final VelocityEstimationConfig GLOBAL = AppResources.load(new VelocityEstimationConfig());
  // ---
  public Tensor correction = Tensors.of(Quantity.of(0.46, SI.ACCELERATION), Quantity.of(-0.56, SI.ACCELERATION));
  /** How much does the computed velocity from lidar correct the integrated velocity */
  public Scalar correctionFactor = Quantity.of(0.01, SI.ONE);
}
