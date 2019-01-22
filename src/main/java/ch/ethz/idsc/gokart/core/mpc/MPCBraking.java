// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

/* package */ abstract class MPCBraking extends MPCControlUpdateListener implements MPCStateProviderClient {
  // TODO MH document function input and output definition
  public abstract Scalar getBraking(Scalar time);
}
