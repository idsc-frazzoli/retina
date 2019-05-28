// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.NormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class ImprovedNormalizedTorqueVectoringManualModule extends TorqueVectoringManualModule {
  public ImprovedNormalizedTorqueVectoringManualModule() {
    super(new NormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
