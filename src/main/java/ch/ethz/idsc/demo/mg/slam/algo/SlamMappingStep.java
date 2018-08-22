// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** executes the mapping step of the SLAM algorithm */
/* package */ class SlamMappingStep extends AbstractSlamMappingStep {
  private final int relevantParticles;

  protected SlamMappingStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    relevantParticles = slamConfig.relevantParticles.number().intValue();
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    updateOccurrenceMap();
  }

  @Override // from AbstractSlamMappingStep
  protected void updateOccurrenceMap() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamMappingStepUtil.updateOccurrenceMap(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamContainer.getEventGokartFrame(), relevantParticles);
  }
}
