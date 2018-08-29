// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMax;

/* package */ enum SlamWaypointSelectionUtil {
  ;
  /** v1.0: choose way point that is farthest away */
  public static void selectWaypoint(SlamContainer slamContainer) {
    List<SlamWaypoint> slamWaypoints = slamContainer.getSlamWaypoints();
    if (slamWaypoints.isEmpty())
      return;
    TensorUnaryOperator world2local = new Se2Bijection(slamContainer.getPoseUnitless()).inverse();
    Tensor distances = Tensor.of(slamWaypoints.stream() //
        .map(waypoint -> world2local.apply(Tensors.vectorDouble(waypoint.getWorldPosition())).Get(0)));
    slamContainer.setSelectedSlamWaypoint(slamWaypoints.get(ArgMax.of(distances)));
  }
}
