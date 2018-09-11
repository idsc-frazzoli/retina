// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

// methods to determine the two boundaries of the race track
/* package */ enum SlamBoundaryLogic {
  ;
  private final static double leftThreshold = 0.5;
  private final static double rightThreshold = -0.5;

  // determines if a detected way point belongs to the left or right boundary
  public static void classifyWaypoints(List<double[]> visibleWaypoints, List<double[]> leftBoundary, List<double[]> rightBoundary) {
    for (int i = 0; i < visibleWaypoints.size(); ++i) {
      if (visibleWaypoints.get(i)[1] > leftThreshold)
        leftBoundary.add(visibleWaypoints.get(i));
      if (visibleWaypoints.get(i)[1] < rightThreshold)
        rightBoundary.add(visibleWaypoints.get(i));
    }
  }
}
