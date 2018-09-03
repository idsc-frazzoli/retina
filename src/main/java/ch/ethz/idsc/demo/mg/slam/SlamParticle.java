// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.math.map.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** single particle for the SLAM algorithm */
public class SlamParticle implements GokartPoseInterface {
  private Tensor pose; // unitless representation
  private Scalar linVel; // in direction of go kart x axis
  private Scalar angVel; // around go kart z axis
  private double particleLikelihood;

  /** initializes particle pose and velocity
   * 
   * @param initPose {[m],[m],[-]} initial pose with units
   * @param initLinVel interpreted as [m/s]
   * @param initAngVel interpreted as [rad/s]
   * @param initParticleLikelihood [-] */
  public void initialize(Tensor initPose, Scalar initLinVel, Scalar initAngVel, double initParticleLikelihood) {
    setPose(initPose);
    linVel = initLinVel;
    angVel = initAngVel;
    particleLikelihood = initParticleLikelihood;
  }

  public void propagateStateEstimate(double dT) {
    Tensor deltaPose = Tensors.of(linVel, RealScalar.of(0), angVel).multiply(RealScalar.of(dT));
    setPoseUnitless(Se2CoveringIntegrator.INSTANCE.spin(getPoseUnitless(), deltaPose));
  }

  /** subtracts pose vector from pose
   * 
   * @param subtractPose unitless representation */
  public void subtractPose(Tensor subtractPose) {
    pose = pose.subtract(subtractPose);
  }

  public void setStateFromParticle(SlamParticle particle, double updatedLikelihood) {
    setPoseUnitless(particle.getPoseUnitless());
    linVel = particle.getLinVel();
    angVel = particle.getAngVel();
    particleLikelihood = updatedLikelihood;
  }

  /** @param pose {x,y,heading} without units */
  public void setPoseUnitless(Tensor unitlessPose) {
    pose = unitlessPose;
  }

  public Tensor getPoseUnitless() {
    return pose;
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

  /** sets pose when input argument is not unitless
   * 
   * @param pose {x[m], y[m], heading[]} */
  public void setPose(Tensor pose) {
    setPoseUnitless(GokartPoseHelper.toUnitless(pose));
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return GokartPoseHelper.attachUnits(pose);
  }
}
