// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class SlamMappingStepLidar extends AbstractSlamMappingStep {
  protected SlamMappingStepLidar(SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamContainer, slamImageToGokart);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    updateOccurrenceMap();
  }

  @Override // from AbstractSlamMappingStep
  protected void updateOccurrenceMap() {
    if (Objects.nonNull(slamImageToGokart.getEventGokartFrame()))
      SlamMappingStepUtil.updateOccurrenceMapLidar(slamContainer.getSlamEstimatedPose().getPoseUnitless(), //
          slamContainer.getOccurrenceMap(), slamImageToGokart.getEventGokartFrame());
  }
}
