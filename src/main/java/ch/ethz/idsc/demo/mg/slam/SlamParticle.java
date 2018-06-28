// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// provides a single particle for the SLAM algorithm
// TODO instead of x,y,angle maybe just a Tensor field
public class SlamParticle implements GokartPoseInterface {
  private Tensor pose;
  private double particleLikelihood;
  private GeometricLayer gokartPoseLayer;

  public void initialize(Tensor pose, double particleLikelihood) {
    this.pose = pose;
    this.particleLikelihood = particleLikelihood;
    setGeometricLayer();
  }

  /** @param deltaPose of the form {vx, vy, vangle} multipled by time_delta */
  public void propagateStateEstimate(Tensor deltaPose) {
    setPoseUnitless(Se2Integrator.INSTANCE.spin(getPoseUnitless(), deltaPose));
    // different option below
    // Tensor xya = Se2Utils.fromSE2Matrix(Se2Utils.toSE2Matrix(getPoseUnitless()).dot(Se2Utils.toSE2Matrix(deltaPose)));
    // setPose(xya);
  }

  private void setGeometricLayer() {
    gokartPoseLayer = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(pose));
  }

  private Tensor getPoseUnitless() {
    return GokartPoseHelper.toUnitless(pose);
  }

  private void setPoseUnitless(Tensor unitlessPose) {
    double x = unitlessPose.Get(0).number().doubleValue();
    double y = unitlessPose.Get(1).number().doubleValue();
    double angle = unitlessPose.Get(2).number().doubleValue();
    pose = Tensors.of(Quantity.of(x, SI.METER), Quantity.of(y, SI.METER), DoubleScalar.of(angle));
    setGeometricLayer();
  }

  public void setPose(Tensor pose) {
    this.pose = pose;
    setGeometricLayer();
  }

  public void setParticleLikelihood(double particleLikelihood) {
    this.particleLikelihood = particleLikelihood;
  }

  public double getParticleLikelihood() {
    return particleLikelihood;
  }

  public GeometricLayer getGeometricLayer() {
    return gokartPoseLayer;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return pose;
  }
}
