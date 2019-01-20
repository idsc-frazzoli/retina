// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedPredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class ImprovedNormalizedPredictiveTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedNormalizedPredictiveTorqueVectoringJoystickModule() {
    super(new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
