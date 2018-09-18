// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.demo.mg.slam.prc.SlamCurveUtil;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

// filters way points when the curvature is too large
class CurvatureFilter implements WaypointFilterInterface {
  private final Scalar curvatureThreshold = SlamPrcConfig.GLOBAL.curvatureThreshold;

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    if (gokartWaypoints.length() >= 3) {
      Tensor localCurvature = SlamCurveUtil.localCurvature(gokartWaypoints);
      for (int i = 1; i < localCurvature.length() - 1; ++i) {
        if (Scalars.lessEquals(curvatureThreshold, localCurvature.Get(i).abs()))
          validities[i] = false;
      }
    }
  }
}
