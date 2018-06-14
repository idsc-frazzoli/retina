// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// provides a single particle for the SLAM algorithm
// TODO Is this the correct interface
public class SlamParticle implements GokartPoseInterface {
  private double x;
  private double y;
  private double angle;
  private double particleLikelihood;

  SlamParticle() {
    setInitialPose();
  }

  // somehow set the initial pose from LIDAR
  private void setInitialPose() {
    // ..
  }

  // ideally, we want to use wheel odometry to propagate the state
  public void propagateStateEstimate() {
    // ..
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of(Quantity.of(x, SI.METER), Quantity.of(y, SI.METER), DoubleScalar.of(angle));
  }

  public double getParticleLikelihood() {
    return particleLikelihood;
  }
}
