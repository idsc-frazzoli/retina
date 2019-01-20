// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.tvec.ImprovedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

// TODO JPH/MH obsolete?
public final class ImprovedTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public ImprovedTorqueVectoringJoystickModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
