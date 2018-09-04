// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMax;

/* package */ enum SlamWaypointSelectionUtil {
  ;
  /** v1.1: choose visible way point that is farthest away */
  public static void selectWaypoint(SlamContainer slamContainer) {
    List<SlamWaypoint> visibleSlamWaypoints = slamContainer.getVisibleWaypoints();
    if (visibleSlamWaypoints.isEmpty()) {
      slamContainer.setSelectedSlamWaypoint(Optional.empty());
      return;
    }
    TensorUnaryOperator world2local = new Se2Bijection(slamContainer.getPoseUnitless()).inverse();
    Tensor distances = Tensor.of(visibleSlamWaypoints.stream() //
        .map(waypoint -> world2local.apply(Tensors.vectorDouble(waypoint.getWorldPosition())).Get(0)));
    slamContainer.setSelectedSlamWaypoint(Optional.of(visibleSlamWaypoints.get(ArgMax.of(distances))));
  }
}
