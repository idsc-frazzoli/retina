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
enum SlamLookAheadComputation {
  ;
  public static void selectLookAhead(SlamContainer slamContainer, List<double[]> visibleWaypoints, double offset) {
    if (visibleWaypoints.isEmpty()) {
      // TODO could introduce some recovery mode here
      slamContainer.setLookAhead(Optional.empty());
      return;
    }
    double[] farthestWaypoint = selectFartestWaypoint(visibleWaypoints);
    double[] lookAheadGokartFrame = setOffset(farthestWaypoint, offset);
    setLookAheadWorldFrame(slamContainer, lookAheadGokartFrame);
  }

  /** way point with largest x coordinate (go kart frame) is chosen as lookAhead
   * 
   * @param visibleWaypoints
   * @return lookAheadGokartFrame in go kart frame coordinates */
  private static double[] selectFartestWaypoint(List<double[]> visibleWaypoints) {
    Tensor distances = Tensor.of((visibleWaypoints.stream()//
        .map(waypoint -> Tensors.vector(waypoint[0]).Get(0))));
    return visibleWaypoints.get(ArgMax.of(distances));
  }

  /** sets lookAheadGokartFrame at an offset in y coordinate of farthestWayPoint
   * 
   * @param farthestWaypoint way point with largest x coordinate (go kart frame)
   * @param offset in go kart frame coordinates
   * @return lookAheadGokartFrame in go kart coordinates */
  private static double[] setOffset(double[] farthestWaypoint, double offset) {
    double[] lookAheadGokartFrame = farthestWaypoint;
    lookAheadGokartFrame[1] += offset;
    return lookAheadGokartFrame;
  }

  /** transform the lookAhead coordinates from go kart frame coordinates to world frame coordinates and
   * set field in slamContainer
   * 
   * @param slamContainer
   * @param lookAheadGokartFrame in go kart frame coordinates */
  private static void setLookAheadWorldFrame(SlamContainer slamContainer, double[] lookAheadGokartFrame) {
    TensorUnaryOperator local2world = new Se2Bijection(slamContainer.getPoseUnitless()).forward();
    Optional<double[]> lookAheadWorldFrame = Optional.of(Primitives.toDoubleArray( //
        local2world.apply(Tensors.vectorDouble(lookAheadGokartFrame))));
    slamContainer.setLookAhead(lookAheadWorldFrame);
  }
}
