// code by mg
package ch.ethz.idsc.retina.app.slam.core;

import java.util.Objects;

import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** updates the particle's likelihoods. When using the particle filter, the localization step of
 * the SLAM algorithm usually consists of likelihood update, state propagation and particle resampling */
/* package */ class SlamLikelihoodStep extends EventActionSlamStep {
  private final double alpha = Magnitude.ONE.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.alpha);

  protected SlamLikelihoodStep(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
  }

  @Override // from EventActionSlamStep
  void davisDvsAction() {
    if (Objects.nonNull(slamCoreContainer.getEventGokartFrame()))
      SlamLikelihoodStepUtil.updateLikelihoods( //
          slamCoreContainer.getSlamParticles(), //
          slamCoreContainer.getOccurrenceMap(), //
          slamCoreContainer.getEventGokartFrame(), //
          alpha);
  }
}
