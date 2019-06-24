// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface WaypointFilterInterface {
  /** @param gokartWaypoints
   * @param validities */
  void filter(Tensor gokartWaypoints, boolean[] validities);
}
