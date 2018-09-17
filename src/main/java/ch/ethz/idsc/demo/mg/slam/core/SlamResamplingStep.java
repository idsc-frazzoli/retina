// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** resamples particles of SLAM algorithm */
/* package */ class SlamResamplingStep extends PeriodicSlamStep {
  private final SlamResamplingStepUtil slamResamplingStepUtil;

  SlamResamplingStep(SlamCoreContainer slamContainer, SlamCoreConfig slamConfig) {
    super(slamContainer, slamConfig.resampleRate);
    slamResamplingStepUtil = new SlamResamplingStepUtil( //
        Magnitude.ACCELERATION.toDouble(slamConfig.rougheningLinAccelStd), //
        Magnitude.ANGULAR_ACCELERATION.toDouble(slamConfig.rougheningAngAccelStd));
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    slamResamplingStepUtil.resampleParticles(slamCoreContainer.getSlamParticles(), dT);
  }
}
