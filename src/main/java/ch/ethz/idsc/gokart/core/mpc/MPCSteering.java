// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public abstract class MPCSteering extends MPCControlUpdateListener implements MPCStateProviderClient {
  public abstract Scalar getSteering(Scalar time);
  public abstract Scalar getDotSteering(Scalar time);
}
