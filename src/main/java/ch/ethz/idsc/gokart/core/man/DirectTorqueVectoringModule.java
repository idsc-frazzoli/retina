// code by mh
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.core.tvec.DirectTorqueVectoring;
import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;

public final class DirectTorqueVectoringModule extends TorqueVectoringModule {
  public DirectTorqueVectoringModule() {
    super(new DirectTorqueVectoring(TorqueVectoringConfig.GLOBAL));
  }
}
