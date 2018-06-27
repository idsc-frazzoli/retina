// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;

// provides a single particle for the SLAM algorithm
// TODO instead of x,y,angle maybe just a Tensor field
public class SlamParticle implements GokartPoseInterface {
  private double x;
  private double y;
  private double angle;
  private double particleLikelihood;
  private GeometricLayer gokartPose;

  SlamParticle(double initParticleLikelihood) {
    particleLikelihood = initParticleLikelihood;
    gokartPose = GeometricLayer.of(IdentityMatrix.of(3));
  }

  /** @param deltaPose of the form {vx, vy, vangle} multiply time_delta */
  public void propagateStateEstimate(Tensor deltaPose) {
    setPose(Se2Integrator.INSTANCE.spin(getPoseUnitless(), deltaPose));
    // different option below
    // Tensor xya = Se2Utils.fromSE2Matrix(Se2Utils.toSE2Matrix(getPoseUnitless()).dot(Se2Utils.toSE2Matrix(deltaPose)));
    // setPose(xya);
  }

  // TODO is this best solution?
  private void setGeometricLayer() {
    gokartPose = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(getPose()));
  }

  // for testing to use instead of propagateStateEstimate
  public void setPose(Tensor pose) {
    x = pose.Get(0).number().doubleValue();
    y = pose.Get(1).number().doubleValue();
    angle = pose.Get(2).number().doubleValue();
    this.setGeometricLayer();
  }

  public void setParticleLikelihood(double particleLikelihood) {
    this.particleLikelihood = particleLikelihood;
  }

  public double getParticleLikelihood() {
    return particleLikelihood;
  }

  public GeometricLayer getGeometricLayer() {
    return gokartPose;
  }

  public Tensor getPoseUnitless() {
    return Tensors.vector(x, y, angle);
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of(Quantity.of(x, SI.METER), Quantity.of(y, SI.METER), DoubleScalar.of(angle));
  }
}
