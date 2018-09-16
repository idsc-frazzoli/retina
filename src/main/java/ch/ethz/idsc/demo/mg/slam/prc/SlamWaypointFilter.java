// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.prc.filt.RegionOfInterestFilter;
import ch.ethz.idsc.demo.mg.slam.prc.filt.WaypointCompareInterface;
import ch.ethz.idsc.demo.mg.slam.prc.filt.WaypointFilterUtil;
import ch.ethz.idsc.tensor.Tensor;

/** filters the way points according to a list of way point filters */
/* package */ class SlamWaypointFilter extends AbstractSlamCurveStep {
  private final List<WaypointCompareInterface> waypointFilters;

  SlamWaypointFilter(SlamPrcContainer slamPrcContainer) {
    super(slamPrcContainer);
    waypointFilters = WaypointFilterUtil.getWaypointFilters();
  }

  @Override // from CurveListener
  public void process() {
    Tensor gokartWaypoints = slamPrcContainer.getGokartWaypoints();
    boolean[] validities = slamPrcContainer.getValidities();
    RegionOfInterestFilter.filter(gokartWaypoints, validities);
    int firstValidIndex = findFirstValidIndex(validities);
    for (WaypointCompareInterface waypointFilter : waypointFilters)
      filterWaypoints(gokartWaypoints, validities, firstValidIndex, waypointFilter);
    slamPrcContainer.setValidities(validities);
  }

  /** filters the ordered list of way points by iterating through it and comparing the current way point with the last valid one
   * 
   * @param gokartWaypoints go kart frame
   * @param validities same length as gokartWaypoints
   * @param firstValidIndex
   * @param waypointFilter filter implementation */
  private void filterWaypoints(Tensor gokartWaypoints, boolean[] validities, int firstValidIndex, WaypointCompareInterface waypointFilter) {
    int previousValidIndex = firstValidIndex;
    for (int i = firstValidIndex + 1; i < gokartWaypoints.length(); ++i) {
      if (validities[i]) {
        if (waypointFilter.filter(gokartWaypoints.get(i), gokartWaypoints.get(previousValidIndex)))
          previousValidIndex = i;
        else
          validities[i] = false;
      }
    }
  }

  /** @param validities
   * @return in case no point is valid, validities.length is returned */
  private int findFirstValidIndex(boolean[] validities) {
    for (int i = 0; i < validities.length; ++i) {
      if (validities[i])
        return i;
    }
    return validities.length;
  }
}
