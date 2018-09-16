// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Norm2Squared;

// utils to filter the way points detected by the SLAM algorithm
/* package */ class SlamWaypointFilter extends AbstractSlamCurveStep {
  private final Scalar deltaYDistance;
  private final Scalar deltaPos;

  SlamWaypointFilter(SlamCurveContainer slamCurveContainer) {
    super(slamCurveContainer);
    deltaYDistance = SlamPrcConfig.GLOBAL.deltaYDistance;
    deltaPos = SlamPrcConfig.GLOBAL.deltaPos;
  }

  /** @param waypoints sorted by x distance
   * @return */
  private Tensor filterWaypoints(Tensor waypoints) {
    Tensor filteredWaypoints = Tensors.of(waypoints.get(0));
    for (int i = 1; i < waypoints.length(); ++i) {
      Scalar deltaY = waypoints.get(i).Get(1).subtract(waypoints.get(i - 1).Get(1)).abs();
      if (Scalars.lessEquals(deltaY, deltaYDistance))
        filteredWaypoints.append(waypoints.get(i));
    }
    filteredWaypoints = mergeClosePoints(filteredWaypoints);
    return filteredWaypoints;
  }

  private Tensor mergeClosePoints(Tensor waypoints) {
    Tensor mergedPoints = Tensors.of(waypoints.get(0));
    int lastIndex = 0;
    if (waypoints.length() > 1) {
      for (int i = 1; i < waypoints.length(); ++i) {
        Scalar posDist = Norm2Squared.ofVector(waypoints.get(i).subtract(mergedPoints.get(lastIndex)));
        if (Scalars.lessEquals(deltaPos.multiply(deltaPos), posDist)) {
          mergedPoints.append(waypoints.get(i));
          ++lastIndex;
        }
      }
    }
    if (lastIndex + 1 != waypoints.length())
      System.out.println("filter");
    return mergedPoints;
  }

  @Override // from CurveListener
  public void process() {
    // TODO Auto-generated method stub
  }
}
