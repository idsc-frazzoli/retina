// code by mg
package ch.ethz.idsc.retina.app.slam.core;

import ch.ethz.idsc.retina.app.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.sophus.lie.se2.Se2Integrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** propagates the estimated pose of the SLAM algorithm when odometry is available.
 * note that this module can be combined with SlamMapPoseReset to account for pose errors that drift away */
/* package */ class SlamPoseOdometryStep extends PeriodicSlamStep {
  private final GokartPoseOdometryDemo gokartPoseOdometryDemo;

  protected SlamPoseOdometryStep(SlamCoreContainer slamCoreContainer, GokartPoseOdometryDemo gokartPoseOdometry) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.localizationUpdateRate);
    this.gokartPoseOdometryDemo = gokartPoseOdometry;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    Tensor newPose = propagatePose(slamCoreContainer.getPoseUnitless(), gokartPoseOdometryDemo.getVelocityUnitless(), dT);
    slamCoreContainer.setPoseUnitless(newPose);
  }

  /** @param oldPose unitless
   * @param velocity unitless
   * @param dT interpreted as [s]
   * @return propagated pose */
  private static Tensor propagatePose(Tensor oldPose, Tensor velocity, double dT) {
    Tensor deltaPose = velocity.multiply(RealScalar.of(dT));
    return Se2Integrator.INSTANCE.spin(oldPose, deltaPose);
  }
}
