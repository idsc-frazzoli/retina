// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.Tensor;

// provides a single particle for the SLAM algorithm
// TODO Is this the correct interface
public class SlamParticle implements GokartPoseInterface{
  /* required fields:
   * position
   * orientation */
  SlamParticle() {
    // initialize particle with lidar pose
  }

  // ideally, we want to use wheel odometry to propagate the state
  public void propagateStateEstimate() {
    // ..
  }

  // this method needs the likelihood map
  public void propagateStateLikelihoods() {
    // ..
  }

  @Override
  public Tensor getPose() {
    // ..
    return null;
  }
}
