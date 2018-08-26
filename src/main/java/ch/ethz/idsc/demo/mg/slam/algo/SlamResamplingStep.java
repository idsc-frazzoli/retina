// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** resamples particles of SLAM algorithm */
/* package */ class SlamResamplingStep extends AbstractSlamStep {
  private final double resampleRate;
  private final SlamResamplingStepUtil slamResamplingStepUtil;
  // ---
  private Double lastResampleTimeStamp = null;

  public SlamResamplingStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    resampleRate = Magnitude.SECOND.toDouble(slamConfig.resampleRate);
    slamResamplingStepUtil = new SlamResamplingStepUtil( //
        Magnitude.ACCELERATION.toDouble(slamConfig.rougheningLinAccelStd), //
        Magnitude.ANGULAR_ACCELERATION.toDouble(slamConfig.rougheningAngAccelStd));
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    // TODO use int for checking
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    if (currentTimeStamp - lastResampleTimeStamp > resampleRate) {
      resampleParticles(currentTimeStamp, lastResampleTimeStamp);
      lastResampleTimeStamp = currentTimeStamp;
    }
  }

  private void initializeTimeStamps(double currentTimeStamp) {
    if (Objects.isNull(lastResampleTimeStamp))
      lastResampleTimeStamp = currentTimeStamp;
  }

  private void resampleParticles(double currentTimeStamp, Double lastResampleTimeStamp2) {
    double dT = currentTimeStamp - lastResampleTimeStamp;
    slamResamplingStepUtil.resampleParticles(slamContainer.getSlamParticles(), dT);
  }
}
