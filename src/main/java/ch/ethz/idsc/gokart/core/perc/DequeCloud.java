// code by vc
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ConvexHull;

public class DequeCloud {
  private final Tensor points;
  private final Tensor hull;

  public DequeCloud(Tensor points) {
    this.points = points;
    hull = ConvexHull.of(points);
  }

  public Tensor points() {
    return points;
  }

  public Tensor hull() {
    return hull;
  }
}