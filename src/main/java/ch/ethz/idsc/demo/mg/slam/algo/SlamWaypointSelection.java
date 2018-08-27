// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.tensor.Scalar;

/** selects the way point that should be followed by the pure pursuit algorithm */
/* package */ class SlamWaypointSelection extends PeriodicSlamStep {
  protected SlamWaypointSelection(SlamContainer slamContainer, Scalar updatePeriod) {
    super(slamContainer, updatePeriod);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamWaypointSelectionUtil.selectWaypoint(slamContainer);
  }
}
