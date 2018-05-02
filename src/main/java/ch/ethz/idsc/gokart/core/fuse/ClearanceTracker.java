// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.tensor.Tensor;

public interface ClearanceTracker {
  /** @param local coordinates {x, y} of obstacle in sensor reference frame
   * @return whether given point is an obstruction */
  boolean isObstructed(Tensor local);
}
