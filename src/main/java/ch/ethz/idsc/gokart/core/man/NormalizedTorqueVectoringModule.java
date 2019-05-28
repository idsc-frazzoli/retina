// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.NormalizedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class NormalizedTorqueVectoringModule extends TorqueVectoringModule {
  public NormalizedTorqueVectoringModule() {
    super(new NormalizedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
