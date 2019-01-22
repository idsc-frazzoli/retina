// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

// TODO MH document definition of functions
/* package */ abstract class MPCSteering extends MPCControlUpdateListener implements MPCStateProviderClient {
  /** @param time
   * @return */
  public abstract Scalar getSteering(Scalar time);

  /** @param time
   * @return */
  public abstract Scalar getDotSteering(Scalar time);
}
