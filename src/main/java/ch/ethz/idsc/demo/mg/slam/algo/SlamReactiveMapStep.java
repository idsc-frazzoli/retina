// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** clears parts of occurrence map that is not visible by current vehicle pose */
/* package */ class SlamReactiveMapStep extends AbstractSlamStep {
  private final double reactiveUpdateRate;
  private final double lookBehindDistance;
  // ---
  private Double lastReactiveUpdateTimeStamp = null;

  protected SlamReactiveMapStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    reactiveUpdateRate = Magnitude.SECOND.toDouble(slamConfig.reactiveUpdateRate);
    lookBehindDistance = Magnitude.METER.toDouble(slamConfig.lookBehindDistance);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    if (currentTimeStamp - lastReactiveUpdateTimeStamp > reactiveUpdateRate) {
      clearNonvisibleOccurrenceMap();
      lastReactiveUpdateTimeStamp = currentTimeStamp;
    }
  }

  private void initializeTimeStamps(double currentTimeStamp) {
    if (Objects.isNull(lastReactiveUpdateTimeStamp))
      lastReactiveUpdateTimeStamp = currentTimeStamp;
  }

  private void clearNonvisibleOccurrenceMap() {
    SlamReactiveMapStepUtil.clearNonvisibleOccurrenceMap(slamContainer.getSlamEstimatedPose().getPoseUnitless(), //
        slamContainer.getOccurrenceMap(), lookBehindDistance);
  }
}
