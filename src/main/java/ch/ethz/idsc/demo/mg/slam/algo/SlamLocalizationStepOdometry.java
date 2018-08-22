// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** localization step of slam algorithm using odometry data */
/* package */ class SlamLocalizationStepOdometry extends AbstractSlamLocalizationStep {
  private final GokartPoseOdometryDemo gokartPoseOdometry;

  protected SlamLocalizationStepOdometry(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart, //
      GokartPoseOdometryDemo gokartPoseOdometry) {
    super(slamConfig, slamContainer, slamImageToGokart);
    this.gokartPoseOdometry = gokartPoseOdometry;
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    updateLikelihoods();
    if (currentTimeStamp - lastPropagationTimeStamp > statePropagationRate) {
      propagateStateEstimateOdometry(currentTimeStamp, lastPropagationTimeStamp);
      lastPropagationTimeStamp = currentTimeStamp;
    }
    if (currentTimeStamp - lastResampleTimeStamp > resampleRate) {
      resampleParticles(currentTimeStamp, lastResampleTimeStamp);
      lastResampleTimeStamp = currentTimeStamp;
    }
  }

  private void propagateStateEstimateOdometry(double currentTimeStamp, double lastPropagationTimeStamp) {
    double dT = currentTimeStamp - lastPropagationTimeStamp;
    SlamLocalizationStepUtil.propagateStateEstimateOdometry(slamContainer.getSlamParticles(), gokartPoseOdometry.getVelocity(), dT);
    slamContainer.getSlamEstimatedPose().setPoseUnitless(SlamLocalizationStepUtil.getAveragePose(slamContainer.getSlamParticles(), 1));
  }
}
