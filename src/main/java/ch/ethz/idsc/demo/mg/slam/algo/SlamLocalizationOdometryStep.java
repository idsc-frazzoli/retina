// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.owl.math.map.Se2CoveringIntegrator;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** propagates the estimated pose of the SLAM algorithm when an external velocity estimate is available, e.g. through odometry.
 * note that this module can be combined with SlamMapPoseReset to account for pose errors that drift away */
// TODO MG reorganize SlamLocalizationStep and this class
/* package */ class SlamLocalizationOdometryStep extends PeriodicSlamStep {
  private final GokartPoseOdometryDemo gokartPoseOdometry;

  protected SlamLocalizationOdometryStep(SlamContainer slamContainer, SlamConfig slamConfig, GokartPoseOdometryDemo gokartPoseOdometry) {
    super(slamContainer, slamConfig.localizationUpdateRate);
    this.gokartPoseOdometry = gokartPoseOdometry;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    Tensor newPose = propagatePose(slamContainer.getPoseUnitless(), gokartPoseOdometry.getVelocity(), dT);
    slamContainer.setPoseUnitless(newPose);
  }

  /** @param oldPose unitless
   * @param velocity with units
   * @param dT interpreted as [s]
   * @return */
  private Tensor propagatePose(Tensor oldPose, Tensor velocity, double dT) {
    Tensor unitlessVelocity = Tensors.of( //
        Magnitude.VELOCITY.apply(velocity.Get(0)), //
        Magnitude.VELOCITY.apply(velocity.Get(1)), //
        Magnitude.PER_SECOND.apply(velocity.Get(2)));
    Tensor deltaPose = unitlessVelocity.multiply(RealScalar.of(dT));
    return Se2CoveringIntegrator.INSTANCE.spin(oldPose, deltaPose);
  }
}
