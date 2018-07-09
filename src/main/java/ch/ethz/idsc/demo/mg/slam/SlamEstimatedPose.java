// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.Tensor;

/** estimated pose can be passed to other modules with this class */
class SlamEstimatedPose implements GokartPoseInterface {
  private Tensor pose;

  // pose is set in the SLAM algorithm
  public void setPose(Tensor pose) {
    this.pose = pose;
  }

  @Override
  public Tensor getPose() {
    return pose;
  }
}
