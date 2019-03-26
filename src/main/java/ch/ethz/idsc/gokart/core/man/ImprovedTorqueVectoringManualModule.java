// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.ImprovedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class ImprovedTorqueVectoringManualModule extends TorqueVectoringManualModule {
  public ImprovedTorqueVectoringManualModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
