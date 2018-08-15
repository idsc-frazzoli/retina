// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** SLAM algorithm uses a unitless pose representation */
/* package */ class SlamEstimatedPose implements GokartPoseInterface {
  private Tensor poseUnitless;

  public void setPoseUnitless(Tensor unitlessPose) {
    poseUnitless = unitlessPose;
  }

  public Tensor getPoseUnitless() {
    return poseUnitless;
  }

  /** sets pose with when input argument is not unitless
   * 
   * @param pose {x[m], y[m], heading[]} */
  public void setPose(Tensor pose) {
    this.poseUnitless = GokartPoseHelper.toUnitless(pose);
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of( //
        Quantity.of(poseUnitless.Get(0), SI.METER), //
        Quantity.of(poseUnitless.Get(1), SI.METER), //
        poseUnitless.Get(2));
  }
}
