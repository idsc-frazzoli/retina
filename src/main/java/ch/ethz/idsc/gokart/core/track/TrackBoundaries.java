// code by jph
package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.tensor.Tensor;

public interface TrackBoundaries {
  /** @return matrix of dimensions n x 2 */
  Tensor getLineCenter();

  /** @return matrix of dimensions n x 2 */
  Tensor getLineRight();

  /** @return matrix of dimensions n x 2 */
  Tensor getLineLeft();
}
