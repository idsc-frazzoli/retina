// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class GokartPoseContainer implements MappedPoseInterface {
  private Tensor pose;

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return pose;
  }

  @Override // from MappedPoseInterface
  public void setPose(Tensor pose, Scalar quality) {
    this.pose = pose;
  }
}
