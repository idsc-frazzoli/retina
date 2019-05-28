// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.PredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class UltimateTorqueVectoringModule extends TorqueVectoringManualModule {
  public UltimateTorqueVectoringModule() {
    super(new PredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
