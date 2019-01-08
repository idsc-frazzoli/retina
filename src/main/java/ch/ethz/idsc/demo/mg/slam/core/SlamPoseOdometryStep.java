// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** propagates the estimated pose of the SLAM algorithm when odometry is available.
 * note that this module can be combined with SlamMapPoseReset to account for pose errors that drift away */
// TODO can be simply refactored to accept any external pose provider as long as it provides a getVelocity() method.
// e.g. lidar pose does currently not provide that
/* package */ class SlamPoseOdometryStep extends PeriodicSlamStep {
  private final GokartPoseOdometryDemo gokartPoseOdometry;

  protected SlamPoseOdometryStep(SlamCoreContainer slamCoreContainer, GokartPoseOdometryDemo gokartPoseOdometry) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.localizationUpdateRate);
    this.gokartPoseOdometry = gokartPoseOdometry;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    Tensor newPose = propagatePose(slamCoreContainer.getPoseUnitless(), gokartPoseOdometry.getVelocityUnitless(), dT);
    slamCoreContainer.setPoseUnitless(newPose);
  }

  /** @param oldPose unitless
   * @param velocity unitless
   * @param dT interpreted as [s]
   * @return propagated pose */
  private static Tensor propagatePose(Tensor oldPose, Tensor velocity, double dT) {
    Tensor deltaPose = velocity.multiply(RealScalar.of(dT));
    // TODO document why Se2CoveringIntegrator vs. Se2Integrator ? is this really intended ?
    return Se2CoveringIntegrator.INSTANCE.spin(oldPose, deltaPose);
  }
}
