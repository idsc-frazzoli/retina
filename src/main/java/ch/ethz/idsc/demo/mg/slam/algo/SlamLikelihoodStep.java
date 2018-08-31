// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;

/** updates the particle's likelihoods. When using the particle filter, the localization step of
 * the SLAM algorithm usually consists of likelihood update, state propagation and particle resampling */
/* package */ class SlamLikelihoodStep extends EventActionSlamStep {
  private final double alpha;

  protected SlamLikelihoodStep(SlamContainer slamContainer, Scalar alpha) {
    super(slamContainer);
    this.alpha = Magnitude.ONE.toDouble(alpha);
  }

  @Override // from EventActionSlamStep
  void davisDvsAction() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamLikelihoodStepUtil.updateLikelihoods( //
          slamContainer.getSlamParticles(), //
          slamContainer.getOccurrenceMap(), //
          slamContainer.getEventGokartFrame(), //
          alpha);
  }
}
