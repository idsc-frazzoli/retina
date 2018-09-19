// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.prc.filt.WaypointFilterInterface;
import ch.ethz.idsc.demo.mg.slam.prc.filt.WaypointFilterUtil;
import ch.ethz.idsc.tensor.Tensor;

/** filters the way points according to a list of way point filters */
/* package */ class SlamWaypointFilter extends AbstractSlamCurveStep {
  private final List<WaypointFilterInterface> waypointFilters;

  SlamWaypointFilter(SlamPrcContainer slamPrcContainer) {
    super(slamPrcContainer);
    waypointFilters = WaypointFilterUtil.getWaypointFilters(slamPrcContainer);
  }

  @Override // from CurveListener
  public void process() {
    Tensor gokartWaypoints = slamPrcContainer.getGokartWaypoints();
    boolean[] validities = slamPrcContainer.getValidities();
    for (WaypointFilterInterface waypointFilter : waypointFilters)
      waypointFilter.filter(gokartWaypoints, validities);
    slamPrcContainer.setValidities(validities);
  }
}
