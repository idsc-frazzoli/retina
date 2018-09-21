// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** sets validity of detected way points based on rectangular region of interest */
public class RegionOfInterestFilter implements WaypointFilterInterface {
  private final Scalar visibleBoxXMin = SlamPrcConfig.GLOBAL.visibleBoxXMin;
  private final Scalar visibleBoxXMax = SlamPrcConfig.GLOBAL.visibleBoxXMax;
  private final Scalar visibleBoxHalfWidth = SlamPrcConfig.GLOBAL.visibleBoxYHalfWidth;

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    for (int i = 0; i < gokartWaypoints.length(); ++i)
      validities[i] = Scalars.lessEquals(visibleBoxXMin, gokartWaypoints.get(i).Get(0)) //
          && Scalars.lessEquals(gokartWaypoints.get(i).Get(0), visibleBoxXMax) //
          && Scalars.lessEquals(gokartWaypoints.get(i).Get(1), visibleBoxHalfWidth) //
          && Scalars.lessEquals(visibleBoxHalfWidth.negate(), gokartWaypoints.get(i).Get(1));
  }
}
