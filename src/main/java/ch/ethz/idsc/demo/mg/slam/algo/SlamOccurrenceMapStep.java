// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** update of occurrence map using the particles */
/* package */ class SlamOccurrenceMapStep extends AbstractSlamStep {
  private final int relevantParticles;

  protected SlamOccurrenceMapStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    relevantParticles = slamConfig.relevantParticles.number().intValue();
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    updateOccurrenceMap();
  }

  protected void updateOccurrenceMap() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamOccurrenceMapStepUtil.updateOccurrenceMap(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamContainer.getEventGokartFrame(), relevantParticles);
  }
}
