// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;

/** update of occurrence map using the particles */
/* package */ class SlamOccurrenceMapStep extends EventActionSlamStep {
  private final int relevantParticles = SlamCoreConfig.GLOBAL.relevantParticles.number().intValue();

  protected SlamOccurrenceMapStep(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer);
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
