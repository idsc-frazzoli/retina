// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamLocalizationStepUtil;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.util.math.Magnitude;

/* package */ abstract class AbstractSlamLocalizationStep implements DavisDvsListener {
  protected final SlamContainer slamContainer;
  protected final SlamImageToGokart slamImageToGokart;
  protected final double resampleRate;
  protected final double statePropagationRate;
  protected final double rougheningLinAccelStd;
  protected final double rougheningAngAccelStd;
  protected final double alpha;
  // ---
  protected double lastResampleTimeStamp;
  protected double lastPropagationTimeStamp;

  protected AbstractSlamLocalizationStep(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    this.slamContainer = slamContainer;
    this.slamImageToGokart = slamImageToGokart;
    resampleRate = Magnitude.SECOND.toDouble(slamConfig.resampleRate);
    statePropagationRate = Magnitude.SECOND.toDouble(slamConfig.statePropagationRate);
    rougheningLinAccelStd = Magnitude.ACCELERATION.toDouble(slamConfig.rougheningLinAccelStd);
    rougheningAngAccelStd = Magnitude.ANGULAR_ACCELERATION.toDouble(slamConfig.rougheningAngAccelStd);
    alpha = slamConfig.alpha.number().doubleValue();
  }

  protected void updateLikelihoods() {
    if (Objects.nonNull(slamImageToGokart.getEventGokartFrame()))
      SlamLocalizationStepUtil.updateLikelihoods(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamImageToGokart.getEventGokartFrame(), alpha);
  }

  protected void resampleParticles(double currentTimeStamp, double lastResampleTimeStamp) {
    if (currentTimeStamp - lastResampleTimeStamp > resampleRate) {
      double dT = currentTimeStamp - lastResampleTimeStamp;
      SlamLocalizationStepUtil.resampleParticles(slamContainer.getSlamParticles(), dT, rougheningLinAccelStd, rougheningAngAccelStd);
      lastResampleTimeStamp = currentTimeStamp;
    }
  }
}
