// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class MPCPower extends MPCControlUpdateListener implements MPCStateProviderClient {
  public abstract Tensor getPower(Scalar time);
}
