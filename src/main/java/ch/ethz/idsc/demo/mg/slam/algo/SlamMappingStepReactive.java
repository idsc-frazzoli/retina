// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** executes the mapping step of the SLAM algorithm in reactiveMapMode */
/* package */ class SlamMappingStepReactive extends AbstractSlamMappingStep {
  private final double reactiveUpdateRate;
  private final double lookBehindDistance;
  private final int relevantParticles;
  // ---
  private Double lastReactiveUpdateTimeStamp = null;

  protected SlamMappingStepReactive(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    reactiveUpdateRate = Magnitude.SECOND.toDouble(slamConfig.reactiveUpdateRate);
    lookBehindDistance = Magnitude.METER.toDouble(slamConfig.lookBehindDistance);
    relevantParticles = slamConfig.relevantParticles.number().intValue();
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    updateOccurrenceMap();
    if (currentTimeStamp - lastReactiveUpdateTimeStamp > reactiveUpdateRate) {
      clearNonvisibleOccurrenceMap();
      lastReactiveUpdateTimeStamp = currentTimeStamp;
    }
  }

  @Override // from AbstractSlamMappingStep
  protected void updateOccurrenceMap() {
    if (Objects.nonNull(slamContainer.getEventGokartFrame()))
      SlamMappingStepUtil.updateOccurrenceMap(slamContainer.getSlamParticles(), slamContainer.getOccurrenceMap(), //
          slamContainer.getEventGokartFrame(), relevantParticles);
  }

  private void initializeTimeStamps(double currentTimeStamp) {
    if (Objects.isNull(lastReactiveUpdateTimeStamp))
      lastReactiveUpdateTimeStamp = currentTimeStamp;
  }

  private void clearNonvisibleOccurrenceMap() {
    SlamMappingStepUtil.clearNonvisibleOccurrenceMap(slamContainer.getSlamEstimatedPose().getPoseUnitless(), //
        slamContainer.getOccurrenceMap(), lookBehindDistance);
  }
}
