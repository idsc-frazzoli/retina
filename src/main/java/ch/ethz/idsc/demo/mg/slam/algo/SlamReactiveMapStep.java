// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** clears parts of occurrence map that is not visible by current vehicle pose */
/* package */ class SlamReactiveMapStep extends PeriodicSlamStep {
  private final double lookBehindDistance;

  SlamReactiveMapStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer, slamConfig.reactiveUpdateRate);
    lookBehindDistance = Magnitude.METER.toDouble(slamConfig.lookBehindDistance);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamReactiveMapStepUtil.clearNonvisibleOccurrenceMap(slamContainer.getPoseUnitless(), //
        slamContainer.getOccurrenceMap(), lookBehindDistance);
  }
}
