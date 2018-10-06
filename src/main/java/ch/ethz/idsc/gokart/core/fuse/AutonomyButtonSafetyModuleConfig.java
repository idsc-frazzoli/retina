package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class AutonomyButtonSafetyModuleConfig {
  public static final AutonomyButtonSafetyModuleConfig GLOBAL = AppResources.load(new AutonomyButtonSafetyModuleConfig());
  public Scalar brakingAmount = Quantity.of(0.3, SI.ONE);
}
