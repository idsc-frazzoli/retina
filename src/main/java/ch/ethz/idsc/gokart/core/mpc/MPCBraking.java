// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.Scalar;

/* package */ abstract class MPCBraking extends MPCControlUpdateCapture implements //
    // MPCStateProviderClient,
    StartAndStoppable {
  /** @param time with unit "s"
   * @return relative brake position in the interval [0, 1] */
  abstract Scalar getBraking(Scalar time);
}
