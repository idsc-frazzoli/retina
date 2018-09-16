// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.Arrays;
import java.util.List;

// sets up the curve processing listener list
enum SlamCurveProcessingUtil {
  ;
  public static final List<CurveListener> getListeners(SlamCurveContainer slamCurveContainer) {
    return Arrays.asList(new SlamWaypointSelection(slamCurveContainer), //
        new SlamCurveInterpolate(slamCurveContainer), //
        new SlamCurveExtrapolate(slamCurveContainer));
  }
}
