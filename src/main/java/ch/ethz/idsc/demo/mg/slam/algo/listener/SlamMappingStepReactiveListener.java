// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamMappingStepUtil;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

public class SlamMappingStepReactiveListener extends AbstractSlamMappingStep {
  private final double reactiveUpdateRate;
  private final double lookBehindDistance;
  // ---
  private Double lastReactiveUpdateTimeStamp = null;

  protected SlamMappingStepReactiveListener(SlamConfig slamConfig, SlamContainer slamContainer, SlamImageToGokart slamImageToGokart) {
    super(slamConfig, slamContainer, slamImageToGokart);
    reactiveUpdateRate = Magnitude.SECOND.toDouble(slamConfig.reactiveUpdateRate);
    lookBehindDistance = Magnitude.METER.toDouble(slamConfig.lookBehindDistance);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    updateOccurrenceMap();
    if (currentTimeStamp - lastReactiveUpdateTimeStamp > reactiveUpdateRate) {
      updateReactiveOccurrenceMap();
      lastReactiveUpdateTimeStamp = currentTimeStamp;
    }
  }

  private void initializeTimeStamps(double currentTimeStamp) {
    if (Objects.isNull(lastReactiveUpdateTimeStamp))
      lastReactiveUpdateTimeStamp = currentTimeStamp;
  }

  private void updateReactiveOccurrenceMap() {
    SlamMappingStepUtil.updateReactiveOccurrenceMap(slamContainer.getSlamEstimatedPose().getPoseUnitless(), //
        slamContainer.getOccurrenceMap(), lookBehindDistance);
  }
}
