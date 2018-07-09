// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Integrator;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** provides a single particle for the SLAM algorithm */
public class SlamParticle implements GokartPoseInterface {
  private Tensor pose;
  private Scalar linVel; // in direction of go kart x axis
  private Scalar angVel; // around go kart z axis
  private double particleLikelihood;
  private GeometricLayer gokartPoseLayer;

  public void initialize(Tensor initPose, Scalar initLinVel, Scalar initAngVel, double initParticleLikelihood) {
    pose = initPose;
    linVel = initLinVel;
    angVel = initAngVel;
    particleLikelihood = initParticleLikelihood;
    setGeometricLayer();
  }

  public void propagateStateEstimate(double dT) {
    Tensor deltaPose = Tensors.of(linVel, RealScalar.of(0), angVel).multiply(RealScalar.of(dT));
    setPoseUnitless(Se2Integrator.INSTANCE.spin(getPoseUnitless(), deltaPose));
    // different option below
    // Tensor xya = Se2Utils.fromSE2Matrix(Se2Utils.toSE2Matrix(getPoseUnitless()).dot(Se2Utils.toSE2Matrix(deltaPose)));
    // setPose(xya);
  }

  private void setPoseUnitless(Tensor unitlessPose) {
    double x = unitlessPose.Get(0).number().doubleValue();
    double y = unitlessPose.Get(1).number().doubleValue();
    double angle = unitlessPose.Get(2).number().doubleValue();
    pose = Tensors.of(Quantity.of(x, SI.METER), Quantity.of(y, SI.METER), DoubleScalar.of(angle));
    setGeometricLayer();
  }

  public void setStateFromParticle(SlamParticle particle, double updatedLikelihood) {
    pose = particle.getPose();
    linVel = particle.getLinVel();
    angVel = particle.getAngVel();
    particleLikelihood = updatedLikelihood;
    setGeometricLayer();
  }

  private void setGeometricLayer() {
    gokartPoseLayer = GeometricLayer.of(GokartPoseHelper.toSE2Matrix(pose));
  }

  // for mapping with lidar pose
  public void setPose(Tensor pose) {
    this.pose = pose;
  }

  public void setLinVel(Scalar linVel) {
    this.linVel = linVel;
  }

  public void setAngVel(Scalar angVel) {
    this.angVel = angVel;
  }

  public void setParticleLikelihood(double particleLikelihood) {
    this.particleLikelihood = particleLikelihood;
  }

  public Scalar getLinVel() {
    return linVel;
  }

  public Scalar getAngVel() {
    return angVel;
  }

  public double getLinVelDouble() {
    return linVel.number().doubleValue();
  }

  public double getAngVelDouble() {
    return angVel.number().doubleValue();
  }

  public double getParticleLikelihood() {
    return particleLikelihood;
  }

  public GeometricLayer getGeometricLayer() {
    return gokartPoseLayer;
  }

  private Tensor getPoseUnitless() {
    return GokartPoseHelper.toUnitless(pose);
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return pose;
  }
}
