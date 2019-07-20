// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.Tensor;

public interface TrackInterface {
  /** @param resolution
   * @return matrix of dimensions n x 2 */
  Tensor getLineMiddle(int resolution);

  /** @param resolution
   * @return */
  TrackBoundaries getTrackBoundaries(int resolution);

  Tensor getNearestPosition(Tensor position);

  boolean isClosed();

  /** test if the position is inside the track limits
   * 
   * @param position in [m]
   * @return true if within track limits */
  boolean isInTrack(Tensor position);
}
