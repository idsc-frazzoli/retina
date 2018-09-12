// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;
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
  public static List<double[]> selectWaypoints(List<double[]> worldWaypoints, SlamContainer slamContainer, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    List<double[]> gokartWaypoints = computeGokartCoordinates(worldWaypoints, slamContainer.getPoseUnitless());
    List<Boolean> visibilities = computeVisibility(gokartWaypoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    setWorldWaypoints(slamContainer, worldWaypoints, visibilities);
    return getVisibleWaypoints(gokartWaypoints, visibilities);
  }

  /** computes the go kart frame coordinates of the detected world frame way points
   * 
   * @param worldWaypoints
   * @param poseUnitless
   * @return go kart frame coordinates */
  private static List<double[]> computeGokartCoordinates(List<double[]> worldWaypoints, Tensor poseUnitless) {
    List<double[]> gokartWaypoints = new ArrayList<>();
    TensorUnaryOperator world2local = new Se2Bijection(poseUnitless).inverse();
    for (double[] worldWaypoint : worldWaypoints) {
      gokartWaypoints.add(Primitives.toDoubleArray( //
          world2local.apply(Tensors.vectorDouble(worldWaypoint))));
    }
    return gokartWaypoints;
  }

  /** based on the field of view parameters, determines the current visibility of the way points
   * 
   * @param gokartWaypoints
   * @param visibleBoxXMin
   * @param visibleBoxXMax
   * @param visibleBoxHalfWidth
   * @return visibilities of the way points */
  private static List<Boolean> computeVisibility(List<double[]> gokartWaypoints, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    List<Boolean> visibilities = new ArrayList<>();
    for (double[] gokartWaypoint : gokartWaypoints) {
      boolean visibility = visibleBoxXMin < gokartWaypoint[0] && gokartWaypoint[0] < visibleBoxXMax //
          && -visibleBoxHalfWidth < gokartWaypoint[1] && gokartWaypoint[1] < visibleBoxHalfWidth;
      visibilities.add(visibility);
    }
    return visibilities;
  }

  /** creates visibleWaypoints list ordered by distance and adds a way point at [0,0]
   * 
   * @param gokartWaypoints in go kart frame
   * @param visibilities
   * @return visibleWaypoints in go kart frame */
  private static List<double[]> getVisibleWaypoints(List<double[]> gokartWaypoints, List<Boolean> visibilities) {
    List<double[]> visibleWaypoints = new ArrayList<>();
    for (int i = 0; i < gokartWaypoints.size(); i++)
      if (visibilities.get(i))
        visibleWaypoints.add(gokartWaypoints.get(i));
    visibleWaypoints.add(new double[] { 0, 0 });
    Collections.sort(visibleWaypoints, WaypointXComparator.INSTANCE);
    return visibleWaypoints;
  }

  /** sets the slamWaypoints field in the slamContainer based on world frame coordinates and current visibilities */
  private static void setWorldWaypoints(SlamContainer slamContainer, List<double[]> worldWaypoints, List<Boolean> visibilities) {
    List<SlamWaypoint> slamWaypoints = new ArrayList<>();
    for (int i = 0; i < worldWaypoints.size(); i++)
      slamWaypoints.add(new SlamWaypoint(worldWaypoints.get(i), visibilities.get(i)));
    slamContainer.setWaypoints(slamWaypoints);
  }
}
