// code by mg
package ch.ethz.idsc.retina.app.slam.core;

import java.util.Objects;

import ch.ethz.idsc.retina.app.slam.SlamCoreContainer;

/** update of occurrence map using the particles */
/* package */ class SlamOccurrenceMapStep extends EventActionSlamStep {
  private final int relevantParticles;

  protected SlamOccurrenceMapStep(SlamCoreContainer slamCoreContainer, int relevantParticles) {
    super(slamCoreContainer);
    this.relevantParticles = relevantParticles;
  }

  @Override // from EventActionSlamStep
  void davisDvsAction() {
    if (Objects.nonNull(slamCoreContainer.getEventGokartFrame()))
      SlamOccurrenceMapStepUtil.updateOccurrenceMap( //
          slamCoreContainer.getSlamParticles(), //
          slamCoreContainer.getOccurrenceMap(), //
          slamCoreContainer.getEventGokartFrame(), //
          relevantParticles);
  }
}
