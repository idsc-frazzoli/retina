// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.Tensor;

/** SLAM algorithm uses a unitless pose representation */
public class SlamEstimatedPose implements GokartPoseInterface {
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
    return GokartPoseHelper.attachUnits(poseUnitless);
  }
}
