// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamLocalizationStepUtil;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** localization step of slam algorithm using odometry data */
// TODO MG include GokartPoseOdometryDemo field for the odometry data
/* package */ class SlamLocalizationStepOdometryListener extends AbstractSlamLocalizationStep {
  protected SlamLocalizationStepOdometryListener(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamConfig, slamContainer, slamImageToGokart);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    updateLikelihoods();
    propagateStateEstimateOdometry(currentTimeStamp, lastPropagationTimeStamp);
    resampleParticles(currentTimeStamp, lastResampleTimeStamp);
  }

  private void propagateStateEstimateOdometry(double currentTimeStamp, double lastPropagationTimeStamp) {
    if (currentTimeStamp - lastPropagationTimeStamp > statePropagationRate) {
      double dT = currentTimeStamp - lastPropagationTimeStamp;
      SlamLocalizationStepUtil.propagateStateEstimate(slamContainer.getSlamParticles(), dT);
      slamContainer.getSlamEstimatedPose().setPoseUnitless(SlamLocalizationStepUtil.getAveragePose(slamContainer.getSlamParticles(), 1));
      lastPropagationTimeStamp = currentTimeStamp;
    }
  }
}
