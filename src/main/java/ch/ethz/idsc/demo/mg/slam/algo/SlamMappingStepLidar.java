// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/** executes the mapping step of the SLAM algorithm with the provided lidar pose */
/* package */ class SlamMappingStepLidar extends AbstractSlamMappingStep {
  protected SlamMappingStepLidar(SlamContainer slamContainer) {
    super(slamContainer);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    updateOccurrenceMap();
  }

  @Override // from AbstractSlamMappingStep
  protected void updateOccurrenceMap() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamMappingStepUtil.updateOccurrenceMapLidar(slamContainer.getSlamEstimatedPose().getPoseUnitless(), //
          slamContainer.getOccurrenceMap(), slamContainer.getEventGokartFrame());
  }
}
