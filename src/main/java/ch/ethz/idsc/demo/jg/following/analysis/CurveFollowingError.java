// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import ch.ethz.idsc.tensor.Tensor;

public class CurveFollowingError extends OfflineFollowingError {
  /** @param curve reference */
  public CurveFollowingError(Tensor curve) {
    super();
    setReference(curve);
  }
}
