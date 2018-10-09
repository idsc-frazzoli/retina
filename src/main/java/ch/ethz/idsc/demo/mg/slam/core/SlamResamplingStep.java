// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** resamples particles of SLAM algorithm */
/* package */ class SlamResamplingStep extends PeriodicSlamStep {
  private final SlamResamplingStepUtil slamResamplingStepUtil;

  SlamResamplingStep(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.resampleRate);
    slamResamplingStepUtil = new SlamResamplingStepUtil( //
        Magnitude.ACCELERATION.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.rougheningLinAccelStd), //
        Magnitude.ANGULAR_ACCELERATION.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.rougheningAngAccelStd));
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    slamResamplingStepUtil.resampleParticles(slamCoreContainer.getSlamParticles(), dT);
  }
}
