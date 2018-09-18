// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

/** handles the list of way point filters used in SlamWaypointFilter */
public enum WaypointFilterUtil {
  ;
  public static List<WaypointFilterInterface> getWaypointFilters(Optional<Tensor> curve) {
    return Arrays.asList( //
        new RegionOfInterestFilter(), //
        new MergeWaypointsFilter(), //
        new SausageFilter(curve), new CurvatureFilter());
  }
}
