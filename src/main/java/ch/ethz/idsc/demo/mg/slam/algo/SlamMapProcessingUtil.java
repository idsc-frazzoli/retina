package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum SlamMapProcessingUtil {
  ;
  /** creates SlamWaypoint objects based on worldWaypoints. sets visibility field of slamWaypoints
   * 
   * @param worldWaypoints
   * @param pose unitless representation
   * @return slamWaypoints List of SlamWaypoint objects */
  public static List<SlamWaypoint> getWaypoints(List<double[]> worldWaypoints, Tensor pose, double visibleBoxXMin, //
      double visibleBoxXMax, double visibleBoxHalfWidth) {
    List<SlamWaypoint> slamWaypoints = new ArrayList<>();
    TensorUnaryOperator world2local = new Se2Bijection(pose).inverse(); //
    for (double[] worldWaypoint : worldWaypoints) {
      double[] gokartWaypoint = Primitives.toDoubleArray( //
          world2local.apply(Tensors.vectorDouble(worldWaypoint)));
      boolean visibility = visibleBoxXMin < gokartWaypoint[0] && gokartWaypoint[0] < visibleBoxXMax //
          && -visibleBoxHalfWidth < gokartWaypoint[1] && gokartWaypoint[1] < visibleBoxHalfWidth;
      slamWaypoints.add(new SlamWaypoint(worldWaypoint, visibility));
    }
    return slamWaypoints;
  }
}
