// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMax;

// TODO MG work in progress, refactor and implement lookAhead with offset
/* package */ enum SlamWaypointSelectionUtil {
  ;
  /** creates SlamWaypoint objects based on worldWaypoints. sets visibility field of slamWaypoints
   * 
   * @param worldWaypoints
   * @param pose unitless representation */
  public static void getWaypoints(List<double[]> worldWaypoints, SlamContainer slamContainer, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    List<double[]> gokartWaypoints = computeGokartCoordinates(worldWaypoints, slamContainer.getPoseUnitless());
    List<Boolean> visibilities = computeVisibility(gokartWaypoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    List<double[]> visibleWaypoints = getVisibleWaypoints(gokartWaypoints, visibilities);
    setWaypoints(slamContainer, worldWaypoints, visibilities);
    selectWaypoints(slamContainer, visibleWaypoints);
  }

  private static void selectWaypoints(SlamContainer slamContainer, List<double[]> visibleWaypoints) {
    if (visibleWaypoints.isEmpty()) {
      slamContainer.setLookAhead(Optional.empty());
      return;
    }
    Tensor distances = Tensor.of((visibleWaypoints.stream()//
        .map(waypoint -> Tensors.vector(waypoint[0]).Get(0))));
    double[] lookAheadGokartFrame = visibleWaypoints.get(ArgMax.of(distances));
    setLookAheadWorldFrame(slamContainer, lookAheadGokartFrame);
  }

  private static void setLookAheadWorldFrame(SlamContainer slamContainer, double[] lookAheadGokartFrame) {
    TensorUnaryOperator local2world = new Se2Bijection(slamContainer.getPoseUnitless()).forward();
    Optional<double[]> lookAheadWorldFrame = Optional.of(Primitives.toDoubleArray( //
        local2world.apply(Tensors.vectorDouble(lookAheadGokartFrame))));
    slamContainer.setLookAhead(lookAheadWorldFrame);
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

  private static List<double[]> getVisibleWaypoints(List<double[]> gokartWaypoints, List<Boolean> visibilities) {
    List<double[]> visibleWaypoints = new ArrayList<>();
    for (int i = 0; i < gokartWaypoints.size(); i++) {
      if (visibilities.get(i))
        visibleWaypoints.add(gokartWaypoints.get(i));
    }
    return visibleWaypoints;
  }

  /** sets the slamWaypoints field in the slamContainer based on world frame coordinates and current visibilities */
  private static void setWaypoints(SlamContainer slamContainer, List<double[]> worldWaypoints, List<Boolean> visibilities) {
    List<SlamWaypoint> slamWaypoints = new ArrayList<>();
    for (int i = 0; i < worldWaypoints.size(); i++) {
      slamWaypoints.add(new SlamWaypoint(worldWaypoints.get(i), visibilities.get(i)));
    }
    slamContainer.setWaypoints(slamWaypoints);
  }
}
