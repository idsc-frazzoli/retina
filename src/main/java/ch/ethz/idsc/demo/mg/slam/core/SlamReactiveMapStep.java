// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** clears parts of occurrence map that is not visible by current vehicle pose */
/* package */ class SlamReactiveMapStep extends PeriodicSlamStep {
  private final double lookBehindDistance = Magnitude.METER.toDouble(SlamCoreConfig.GLOBAL.lookBehindDistance);

  SlamReactiveMapStep(SlamCoreContainer slamCoreContainer) {
    super(slamCoreContainer, SlamCoreConfig.GLOBAL.reactiveUpdateRate);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamReactiveMapStepUtil.clearNonvisibleOccurrenceMap(slamCoreContainer.getPoseUnitless(), //
        slamCoreContainer.getOccurrenceMap(), lookBehindDistance);
  }
}
