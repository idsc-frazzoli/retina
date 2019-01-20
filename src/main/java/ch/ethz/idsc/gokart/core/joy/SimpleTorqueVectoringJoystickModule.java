// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.core.tvec.SimpleTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

// TODO JPH/MH obsolete?
public final class SimpleTorqueVectoringJoystickModule extends TorqueVectoringJoystickModule {
  public SimpleTorqueVectoringJoystickModule() {
    super(new SimpleTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
