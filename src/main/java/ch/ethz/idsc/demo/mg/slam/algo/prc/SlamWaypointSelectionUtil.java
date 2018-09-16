// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainerUtil;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** compute visible way points based on detected way points in world frame coordinates and estimated vehicle pose */
/* package */ enum SlamWaypointSelectionUtil {
  ;
  /** selects visible way points and sets way point field in slamContainer
   * 
   * @param worldWaypoints in world frame
   * @param slamContainer
   * @param visibleBoxXMin
   * @param visibleBoxXMax
   * @param visibleBoxHalfWidth
   * @return visibleWaypoints in go kart frame */
  public static Tensor selectWaypoints(Tensor worldWaypoints, SlamCurveContainer slamCurveContainer, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    Tensor gokartWaypoints = SlamContainerUtil.world2Local(worldWaypoints, slamCurveContainer.getPoseUnitless());
    // computeGokartCoordinates(worldWaypts, slamContainer.getPoseUnitless());
    List<Boolean> visibilities = computeVisibility(gokartWaypoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    setWorldWaypoints(slamCurveContainer, worldWaypoints, visibilities);
    return getVisibleWaypoints(gokartWaypoints, visibilities);
  }

  /** based on the field of view parameters, determines the current visibility of the way points
   * 
   * @param gokartWaypoints
   * @param visibleBoxXMin
   * @param visibleBoxXMax
   * @param visibleBoxHalfWidth
   * @return visibilities of the way points */
  private static List<Boolean> computeVisibility(Tensor gokartWaypoints, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    List<Boolean> visibilities = new ArrayList<>();
    for (int i = 0; i < gokartWaypoints.length(); ++i) {
      boolean visibility = visibleBoxXMin < gokartWaypoints.get(i).Get(0).number().doubleValue() //
          && gokartWaypoints.get(i).Get(0).number().doubleValue() < visibleBoxXMax //
          && -visibleBoxHalfWidth < gokartWaypoints.get(i).Get(1).number().doubleValue() //
          && gokartWaypoints.get(i).Get(1).number().doubleValue() < visibleBoxHalfWidth;
      visibilities.add(visibility);
    }
    return visibilities;
  }

  /** creates visibleWaypoints list ordered by distance
   * 
   * @param gokartWaypoints in go kart frame
   * @param visibilities
   * @return visibleWaypoints in go kart frame */
  private static Tensor getVisibleWaypoints(Tensor gokartWaypoints, List<Boolean> visibilities) {
    Tensor visibleWaypoints = Tensors.empty();
    for (int i = 0; i < gokartWaypoints.length(); i++)
      if (visibilities.get(i))
        visibleWaypoints.append(gokartWaypoints.get(i));
    visibleWaypoints = Tensor.of(visibleWaypoints.stream().sorted(WaypointXComparator.INSTANCE));
    return visibleWaypoints;
  }

  /** sets the slamWaypoints field in the slamContainer based on world frame coordinates and current visibilities */
  private static void setWorldWaypoints(SlamCurveContainer slamCurveContainer, Tensor worldWaypoints, List<Boolean> visibilities) {
    List<SlamWaypoint> slamWaypoints = new ArrayList<>();
    for (int i = 0; i < worldWaypoints.length(); i++)
      slamWaypoints.add(new SlamWaypoint(worldWaypoints.get(i), visibilities.get(i)));
    slamCurveContainer.setWaypoints(slamWaypoints);
  }
}
