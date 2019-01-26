// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;

/* package */ abstract class MPCBraking extends MPCControlUpdateListener implements MPCStateProviderClient {
  /** @param time
   * @return relative brake position in the interval [0, 1] */
  public abstract Scalar getBraking(Scalar time);
}
