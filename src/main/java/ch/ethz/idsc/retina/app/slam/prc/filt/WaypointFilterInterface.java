// code by mg
package ch.ethz.idsc.retina.app.slam.prc.filt;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface WaypointFilterInterface {
  /** @param gokartWaypoints
   * @param validities */
  void filter(Tensor gokartWaypoints, boolean[] validities);
}
