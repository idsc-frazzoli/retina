// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
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
    // somehow set the initial pose from LIDAR
  }

  // ideally, we want to use wheel odometry to propagate the state
  public void propagateStateEstimate() {
    // ..
  }

  // for testing to use instead of propagateStateEstimate
  public void setPose(Tensor pose) {
    x = pose.Get(0).number().doubleValue();
    y = pose.Get(1).number().doubleValue();
    angle = pose.Get(2).number().doubleValue();
  }

  public void setParticleLikelihood(double particleLikelihood) {
    this.particleLikelihood = particleLikelihood;
  }

  public Tensor getWorldCoord(double[] gokartFramePos) {
    Tensor gokart2World = GokartPoseHelper.toSE2Matrix(this.getPose());
    Tensor worldCoord = gokart2World.dot(Tensors.vector(gokartFramePos[0], gokartFramePos[1], 0));
    return worldCoord;
  }
  
  public double getParticleLikelihood() {
    return particleLikelihood;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of(Quantity.of(x, SI.METER), Quantity.of(y, SI.METER), DoubleScalar.of(angle));
  }
}
