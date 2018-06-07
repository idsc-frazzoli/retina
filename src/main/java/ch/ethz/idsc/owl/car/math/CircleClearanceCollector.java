// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;

public class CircleClearanceCollector extends CircleClearanceTracker {
  private final Collection<Tensor> collection = new LinkedList<>();

  /** @param half width along y-axis
   * @param angle steering
   * @param xya reference frame of sensor as 3-vector {px, py, angle}
   * @param clearanceFront */
  public CircleClearanceCollector(Scalar half, Scalar angle, Tensor xya, Clip clip_X) {
    super(half, angle, xya, clip_X);
  }

  @Override // from CircleClearanceTracker
  protected void notifyHit(Tensor point) {
    collection.add(point);
  }

  /** @return unmodifiable collection with points that were determined to be in path */
  public Collection<Tensor> getPointsInViolation() {
    return Collections.unmodifiableCollection(collection);
  }
}
