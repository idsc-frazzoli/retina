// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** estimated pose can be passed to other modules with this class */
/* package */ class SlamEstimatedPose implements GokartPoseInterface {
  private Tensor pose; // unitless representation

  public void setPoseUnitless(Tensor unitlessPose) {
    pose = unitlessPose;
  }

  public Tensor getPoseUnitless() {
    return pose;
  }

  /** set pose
   * 
   * @param pose {x[m], y[m], heading[]} */
  public void setPose(Tensor pose) {
    this.pose = GokartPoseHelper.toUnitless(pose);
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of( //
        Quantity.of(pose.Get(0), SI.METER), //
        Quantity.of(pose.Get(1), SI.METER), //
        pose.Get(2));
  }
}
