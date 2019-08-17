// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.ImprovedTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class ImprovedTorqueVectoringModule extends TorqueVectoringModule {
  public ImprovedTorqueVectoringModule() {
    super(new ImprovedTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
