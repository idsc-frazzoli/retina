// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** particle's state propagation which should be followed by a resampling step */
/* package */ class SlamPropagationStep extends PeriodicSlamStep {
  private final int particleRange;

  SlamPropagationStep(SlamContainer slamContainer, SlamConfig slamConfig) {
    super(slamContainer, slamConfig.statePropagationRate);
    particleRange = Magnitude.ONE.toInt(slamConfig.particleRange);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    double dT = (currentTimeStamp - lastComputationTimeStamp) * 1E-6;
    SlamPropagationStepUtil.propagateStateEstimate(slamContainer.getSlamParticles(), dT);
    slamContainer.setPoseUnitless(SlamPropagationStepUtil.getAveragePose(slamContainer.getSlamParticles(), particleRange));
  }
}
