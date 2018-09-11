// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.ArgMax;

/** methods to determine lookAhead based on currently visible way points */
/* package */ enum SlamLookAheadComputation {
  ;
  /** @param slamContainer
   * @param visibleWaypoints go kart frame
   * @param offset go kart frame */
  public static void selectLookAhead(SlamContainer slamContainer, final List<double[]> visibleWaypoints, double offset) {
    if (visibleWaypoints.isEmpty()) {
      // TODO could introduce some recovery mode here
      slamContainer.setLookAhead(Optional.empty());
      return;
    }
    double[] farthestWaypoint = new double[2];
    double[] lookAheadGokartFrame = new double[2];
    selectFartestWaypoint(visibleWaypoints, farthestWaypoint);
    setOffset(farthestWaypoint, lookAheadGokartFrame, offset);
    setLookAheadWorldFrame(slamContainer, lookAheadGokartFrame);
  }

  /** way point with largest x coordinate (go kart frame) is chosen as lookAhead
   * 
   * @param visibleWaypoints go kart frame
   * @param farthestWaypoint go kart frame */
  private static void selectFartestWaypoint(List<double[]> visibleWaypoints, double[] farthestWaypoint) {
    Tensor distances = Tensor.of((visibleWaypoints.stream()//
        .map(waypoint -> Tensors.vector(waypoint[0]).Get(0))));
    farthestWaypoint[0] = visibleWaypoints.get(ArgMax.of(distances))[0];
    farthestWaypoint[1] = visibleWaypoints.get(ArgMax.of(distances))[1];
  }

  /** sets lookAheadGokartFrame at an offset in y coordinate of farthestWayPoint
   * 
   * @param farthestWaypoint way point with largest x coordinate (go kart frame)
   * @param offset in go kart frame
   * @return lookAheadGokartFrame in go kart */
  private static void setOffset(double[] farthestWaypoint, double[] lookAheadGokartFrame, double offset) {
    lookAheadGokartFrame[0] = farthestWaypoint[0];
    lookAheadGokartFrame[1] = farthestWaypoint[1] + offset;
  }

  /** transform the lookAhead coordinates from go kart frame coordinates to world frame coordinates and
   * set field in slamContainer
   * 
   * @param slamContainer
   * @param lookAheadGokartFrame in go kart frame */
  private static void setLookAheadWorldFrame(SlamContainer slamContainer, double[] lookAheadGokartFrame) {
    TensorUnaryOperator local2world = new Se2Bijection(slamContainer.getPoseUnitless()).forward();
    Optional<double[]> lookAheadWorldFrame = Optional.of(Primitives.toDoubleArray( //
        local2world.apply(Tensors.vectorDouble(lookAheadGokartFrame))));
    slamContainer.setLookAhead(lookAheadWorldFrame);
  }
}
