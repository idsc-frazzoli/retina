// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

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
  public static Tensor selectWaypoints(List<double[]> worldWaypoints, SlamContainer slamContainer, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    Tensor worldWaypts = Tensor.of(worldWaypoints.stream().map(Tensors::vectorDouble));
    Tensor gokartWaypoints = computeGokartCoordinates(worldWaypts, slamContainer.getPoseUnitless());
    List<Boolean> visibilities = computeVisibility(gokartWaypoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    setWorldWaypoints(slamContainer, worldWaypoints, visibilities);
    return getVisibleWaypoints(gokartWaypoints, visibilities);
  }

  /** computes the go kart frame coordinates of the detected world frame way points
   * 
   * @param worldWaypoints
   * @param poseUnitless
   * @return go kart frame coordinates */
  // TODO MG reuse method in SlamContainerUtil
  private static Tensor computeGokartCoordinates(Tensor worldWaypoints, Tensor poseUnitless) {
    TensorUnaryOperator world2local = new Se2Bijection(poseUnitless).inverse();
    return Tensor.of(worldWaypoints.stream().map(world2local::apply));
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
  // TODO MG switch to Tensor field for pos in SlamWaypoint
  private static void setWorldWaypoints(SlamContainer slamContainer, List<double[]> worldWaypoints, List<Boolean> visibilities) {
    List<SlamWaypoint> slamWaypoints = new ArrayList<>();
    for (int i = 0; i < worldWaypoints.size(); i++)
      slamWaypoints.add(new SlamWaypoint(worldWaypoints.get(i), visibilities.get(i)));
    slamContainer.setWaypoints(slamWaypoints);
  }
}
