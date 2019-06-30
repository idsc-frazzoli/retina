// code by mg
package ch.ethz.idsc.retina.app.slam.prc.filt;

import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** sets validity of detected way points based on rectangular region of interest */
public class RegionOfInterestFilter implements WaypointFilterInterface {
  private final Scalar visibleBoxXMin = Magnitude.METER.apply(SlamDvsConfig.eventCamera.slamPrcConfig.visibleBoxXMin);
  private final Scalar visibleBoxXMax = Magnitude.METER.apply(SlamDvsConfig.eventCamera.slamPrcConfig.visibleBoxXMax);
  private final Scalar visibleBoxYHalfWidth = Magnitude.METER.apply(SlamDvsConfig.eventCamera.slamPrcConfig.visibleBoxYHalfWidth);

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    int index = 0;
    for (Tensor gokartWaypoint : gokartWaypoints) {
      Scalar ix = gokartWaypoint.Get(0);
      validities[index] = Scalars.lessEquals(visibleBoxXMin, ix) //
          && Scalars.lessEquals(ix, visibleBoxXMax) //
          && Scalars.lessEquals(gokartWaypoint.Get(1).abs(), visibleBoxYHalfWidth);
      ++index;
    }
  }
}
