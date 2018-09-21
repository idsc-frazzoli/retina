// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum SlamPrcContainerUtil {
  ;
  /** @param points in go kart frame
   * @param poseUnitless
   * @return points in world frame */
  public static Tensor local2World(Tensor points, Tensor poseUnitless) {
    TensorUnaryOperator local2World = new Se2Bijection(poseUnitless).forward();
    return Tensor.of(points.stream().map(local2World::apply));
  }

  /** @param points in world frame
   * @param poseUnitless
   * @return points in go kart frame */
  public static Tensor world2Local(Tensor points, Tensor poseUnitless) {
    TensorUnaryOperator world2Local = new Se2Bijection(poseUnitless).inverse();
    return Tensor.of(points.stream().map(world2Local::apply));
  }

  /** initializes the SlamWaypoints object with an ordered (by WaypointXComparator) list of way points in world frame
   * 
   * @param worldWaypoints world frame
   * @param poseUnitless
   * @param slamWaypoints */
  public static void setSlamWaypoints(Tensor worldWaypoints, Tensor poseUnitless, SlamWaypoints slamWaypoints) {
    Tensor gokartWaypoints = world2Local(worldWaypoints, poseUnitless);
    gokartWaypoints = Tensor.of(gokartWaypoints.stream().sorted(WaypointXComparator.INSTANCE));
    Tensor worldWaypointsOrdered = local2World(gokartWaypoints, poseUnitless);
    slamWaypoints.setGokartWaypoints(gokartWaypoints, worldWaypointsOrdered);
  }
}
