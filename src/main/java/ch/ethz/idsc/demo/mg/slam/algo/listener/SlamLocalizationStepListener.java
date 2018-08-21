// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamLocalizationStepUtil;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** localization step of slam algorithm using standard state propagation */
/* package */ class SlamLocalizationStepListener extends AbstractSlamLocalizationStep {
  SlamLocalizationStepListener(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamConfig, slamContainer, slamImageToGokart);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    updateLikelihoods();
    if (currentTimeStamp - lastPropagationTimeStamp > statePropagationRate) {
      propagateStateEstimate(currentTimeStamp, lastPropagationTimeStamp);
      lastPropagationTimeStamp = currentTimeStamp;
    }
    if (currentTimeStamp - lastResampleTimeStamp > resampleRate) {
      resampleParticles(currentTimeStamp, lastResampleTimeStamp);
      lastResampleTimeStamp = currentTimeStamp;
    }
  }

  private void propagateStateEstimate(double currentTimeStamp, double lastPropagationTimeStamp) {
    double dT = currentTimeStamp - lastPropagationTimeStamp;
    SlamLocalizationStepUtil.propagateStateEstimate(slamContainer.getSlamParticles(), dT);
    slamContainer.getSlamEstimatedPose().setPoseUnitless(SlamLocalizationStepUtil.getAveragePose(slamContainer.getSlamParticles(), 1));
  }
}
