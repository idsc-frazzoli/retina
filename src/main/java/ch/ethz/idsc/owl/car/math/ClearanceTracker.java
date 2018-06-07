// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

public interface ClearanceTracker {
  /** @param local coordinates {x, y} of obstacle in sensor reference frame
   * @return whether given point is an obstruction */
  boolean isObstructed(Tensor local);
  
  /** @return closest of all obstructing points, or empty */
  Optional<Tensor> violation();
}
