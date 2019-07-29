// code by mg
package ch.ethz.idsc.retina.app.slam.prc;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.app.slam.SlamPrcContainer;

// sets up the curve processing listener list
/* package */ enum SlamPrcHandlerUtil {
  ;
  public static final List<CurveListener> getListeners(SlamPrcContainer slamCurveContainer) {
    return Arrays.asList(//
        new SlamWaypointFilter(slamCurveContainer), //
        new SlamCurveFitting(slamCurveContainer), //
        new SlamCurveExtrapolate(slamCurveContainer));
  }
}
