// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** updates the particle's likelihoods. When using the particle filter, the localization step of
 * the SLAM algorithm usually consists of likelihood update, state propagation and particle resampling */
/* package */ class SlamLikelihoodStep extends AbstractSlamStep {
  private final double alpha;

  protected SlamLikelihoodStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    alpha = slamConfig.alpha.number().doubleValue();
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    updateLikelihoods();
  }

  private void updateLikelihoods() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamLikelihoodStepUtil.updateLikelihoods(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamContainer.getEventGokartFrame(), alpha);
  }
}
