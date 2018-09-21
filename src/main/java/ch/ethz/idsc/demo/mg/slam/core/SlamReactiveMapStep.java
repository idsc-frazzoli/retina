// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** clears parts of occurrence map that is not visible by current vehicle pose */
/* package */ class SlamReactiveMapStep extends PeriodicSlamStep {
  private final double lookBehindDistance;

  SlamReactiveMapStep(SlamCoreConfig slamConfig, SlamCoreContainer slamContainer) {
    super(slamContainer, slamConfig.reactiveUpdateRate);
    lookBehindDistance = Magnitude.METER.toDouble(slamConfig.lookBehindDistance);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamReactiveMapStepUtil.clearNonvisibleOccurrenceMap(slamCoreContainer.getPoseUnitless(), //
        slamCoreContainer.getOccurrenceMap(), lookBehindDistance);
  }
}
