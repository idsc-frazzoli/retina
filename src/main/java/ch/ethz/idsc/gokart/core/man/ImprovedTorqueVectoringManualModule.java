// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.ImprovedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

// TODO JPH/MH obsolete?
public final class ImprovedTorqueVectoringManualModule extends TorqueVectoringManualModule {
  public ImprovedTorqueVectoringManualModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
