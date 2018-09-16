// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import java.util.Arrays;
import java.util.List;

/** handles the list of way point filters used in SlamWaypointFilter */
public enum WaypointFilterUtil {
  ;
  public static List<WaypointCompareInterface> getWaypointFilters() {
    return Arrays.asList( //
        new AbsPosDiffFilter(), //
        new YPosDiffFilter());
  }
}
