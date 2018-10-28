// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Tensor;

public interface TrackInterface {
  Tensor getMiddleLine(int resolution);

  Tensor getLeftLine(int resolution);

  Tensor getRightLine(int resolution);

  Tensor getNearestPosition(Tensor position);

  /** test if the position is inside the track limits
   * 
   * @param position in [m]
   * @return true if within track limits */
  boolean isInTrack(Tensor position);
}
