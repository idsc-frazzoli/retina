// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.SimpleTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

// TODO JPH/MH obsolete?
public final class SimpleTorqueVectoringManualModule extends TorqueVectoringManualModule {
  public SimpleTorqueVectoringManualModule() {
    super(new SimpleTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
