// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** particle's state propagation which should be followed by a resampling step */
/* package */ class SlamPropagationStep extends PeriodicSlamStep {
  private final int particleRange = Magnitude.ONE.toInt(SlamCoreConfig.GLOBAL.particleRange);

  SlamPropagationStep(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer, SlamCoreConfig.GLOBAL.statePropagationRate);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    SlamPropagationStepUtil.propagateStateEstimate(slamCoreContainer.getSlamParticles(), dT);
    slamCoreContainer.setPoseUnitless(SlamPropagationStepUtil.getAveragePose(slamCoreContainer.getSlamParticles(), particleRange));
  }
}
