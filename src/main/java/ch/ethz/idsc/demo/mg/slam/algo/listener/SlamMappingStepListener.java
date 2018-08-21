// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** executes the mapping step of the SLAM algorithm */
/* package */ class SlamMappingStepListener extends AbstractSlamMappingStep {
  protected SlamMappingStepListener(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamConfig, slamContainer, slamImageToGokart);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    updateOccurrenceMap();
  }
}
