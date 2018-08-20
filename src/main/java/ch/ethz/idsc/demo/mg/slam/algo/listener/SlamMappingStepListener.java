// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamMappingStepUtil;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** executes the mapping step of the SLAM algorithm */
// TODO MG create abstract class and extend for localizationMode and lidarMappingMode
/* package */ class SlamMappingStepListener implements DavisDvsListener {
  private final SlamContainer slamContainer;
  private final SlamImageToGokart slamImageToGokart;
  private final int relevantParticles;

  public SlamMappingStepListener(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    this.slamContainer = slamContainer;
    this.slamImageToGokart = slamImageToGokart;
    relevantParticles = slamConfig.relevantParticles.number().intValue();
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    SlamMappingStepUtil.updateOccurrenceMap(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
        slamImageToGokart.getEventGokartFrame(), relevantParticles);
  }
}
