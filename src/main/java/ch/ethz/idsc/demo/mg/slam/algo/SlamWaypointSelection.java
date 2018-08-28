// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;

/** selects the way point that should be followed by the pure pursuit algorithm */
// TODO instead of periodic, execute the task once map processing task is finished
/* package */ class SlamWaypointSelection extends PeriodicSlamStep {
  protected SlamWaypointSelection(SlamContainer slamContainer, SlamConfig slamConfig) {
    super(slamContainer, slamConfig.waypointSelectionUpdateRate);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamWaypointSelectionUtil.selectWaypoint(slamContainer);
  }
}
