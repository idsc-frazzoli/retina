// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;

// sets up the curve processing listener list
/* package */ enum SlamPrcHandlerUtil {
  ;
  public static final List<CurveListener> getListeners(SlamPrcContainer slamCurveContainer) {
    return Arrays.asList(//
        new SlamWaypointFilter(slamCurveContainer), //
        new SlamCurveInterpolate(slamCurveContainer), //
        new SlamCurveExtrapolate(slamCurveContainer));
  }
}
