// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

public abstract class MPCBraking extends MPCControlUpdateListener implements MPCStateProviderClient {
  public abstract Scalar getBraking(Scalar time);
}
