// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.tensor.Tensor;

public interface WaypointFilterInterface {
  /** filters the way point sequence by comparing the current way point with the last valid one
   * 
   * @param gokartWaypoints
   * @param validities */
  void filter(Tensor gokartWaypoints, boolean[] validities);
}
