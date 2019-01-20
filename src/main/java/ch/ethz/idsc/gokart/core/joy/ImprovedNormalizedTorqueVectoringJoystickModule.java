// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class ImprovedNormalizedTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedNormalizedTorqueVectoringJoystickModule() {
    super(new ImprovedNormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
