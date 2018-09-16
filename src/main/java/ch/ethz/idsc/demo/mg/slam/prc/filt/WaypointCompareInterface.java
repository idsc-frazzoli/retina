// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.tensor.Tensor;

public interface WaypointCompareInterface {
  /** filters the way point sequence by comparing the current way point with the last valid one
   * 
   * @param currentPoint
   * @param previousValidPoint
   * @return true if currentPoint is valid */
  public boolean filter(Tensor currentPoint, Tensor previousValidPoint);
}
