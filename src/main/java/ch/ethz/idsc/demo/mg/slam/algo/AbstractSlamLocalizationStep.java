// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/* package */ abstract class AbstractSlamLocalizationStep extends AbstractSlamStep {
  protected final double resampleRate;
  protected final double statePropagationRate;
  protected final double rougheningLinAccelStd;
  protected final double rougheningAngAccelStd;
  protected final double alpha;
  // ---
  protected Double lastPropagationTimeStamp = null;
  protected Double lastResampleTimeStamp = null;

  protected AbstractSlamLocalizationStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    resampleRate = Magnitude.SECOND.toDouble(slamConfig.resampleRate);
    statePropagationRate = Magnitude.SECOND.toDouble(slamConfig.statePropagationRate);
    rougheningLinAccelStd = Magnitude.ACCELERATION.toDouble(slamConfig.rougheningLinAccelStd);
    rougheningAngAccelStd = Magnitude.ANGULAR_ACCELERATION.toDouble(slamConfig.rougheningAngAccelStd);
    alpha = slamConfig.alpha.number().doubleValue();
  }

  protected void initializeTimeStamps(double currentTimeStamp) {
    if (Objects.isNull(lastPropagationTimeStamp))
      lastPropagationTimeStamp = currentTimeStamp;
    if (Objects.isNull(lastResampleTimeStamp))
      lastResampleTimeStamp = currentTimeStamp;
  }

  protected void updateLikelihoods() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamLocalizationStepUtil.updateLikelihoods(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamContainer.getEventGokartFrame(), alpha);
  }

  protected void resampleParticles(double currentTimeStamp, double lastResampleTimeStamp) {
    double dT = currentTimeStamp - lastResampleTimeStamp;
    SlamLocalizationStepUtil.resampleParticles(slamContainer.getSlamParticles(), dT, rougheningLinAccelStd, rougheningAngAccelStd);
  }
}
