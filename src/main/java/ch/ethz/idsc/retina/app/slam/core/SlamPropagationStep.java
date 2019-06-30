// code by mg
package ch.ethz.idsc.retina.app.slam.core;

import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;

/** particle's state propagation which should be followed by a resampling step */
/* package */ class SlamPropagationStep extends PeriodicSlamStep {
  private final int particleRange;

  SlamPropagationStep(SlamCoreContainer slamCoreContainer, int particleRange) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.statePropagationRate);
    this.particleRange = particleRange;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    SlamPropagationStepUtil.propagateStateEstimate(slamCoreContainer.getSlamParticles(), dT);
    slamCoreContainer.setPoseUnitless(SlamPropagationStepUtil.getAveragePose(slamCoreContainer.getSlamParticles(), particleRange));
  }
}
