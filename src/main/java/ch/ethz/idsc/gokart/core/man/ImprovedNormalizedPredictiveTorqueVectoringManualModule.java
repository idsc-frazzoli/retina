// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.ImprovedNormalizedPredictiveTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class ImprovedNormalizedPredictiveTorqueVectoringManualModule extends TorqueVectoringManualModule {
  public ImprovedNormalizedPredictiveTorqueVectoringManualModule() {
    super(new ImprovedNormalizedPredictiveTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
