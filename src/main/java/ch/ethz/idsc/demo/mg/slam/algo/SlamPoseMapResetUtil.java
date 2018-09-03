// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamPoseMapResetUtil {
  ;
  /** checks if vehicle pose is too close to boarders of map domain
   * 
   * @param pose with units
   * @param corner [m] lower left corner of map
   * @param cornerHigh [m] upper right corner of map
   * @param padding interpreted as [m] padding around map boarders
   * @return true if vehicle pose is outside map domain */
  public static boolean checkBoarders(Tensor pose, Tensor corner, Tensor cornerHigh, double padding) {
    if (pose.Get(0).subtract(corner.Get(0)).number().doubleValue() < padding)
      return true;
    if (pose.Get(1).subtract(corner.Get(1)).number().doubleValue() < padding)
      return true;
    if (pose.Get(0).subtract(cornerHigh.Get(0)).number().doubleValue() > -padding)
      return true;
    if (pose.Get(1).subtract(cornerHigh.Get(1)).number().doubleValue() > -padding)
      return true;
    return false;
  }

  /** subtracts the same poseDifference vector from all SLAM particles
   * 
   * @param slamParticles
   * @param poseDifference unitless pose vector to be subtracted */
  public static void resetPose(SlamParticle[] slamParticles, Tensor poseDifference) {
    for (int i = 0; i < slamParticles.length; i++) {
      slamParticles[i].subtractPose(poseDifference);
    }
  }

  /** resets the map according to the moved vehicle pose
   * 
   * @param occurrenceMap
   * @param poseDifference difference between current and desired pose after resetting */
  // TODO for loop could be done in parallel
  public static void resetMap(MapProvider occurrenceMap, Tensor poseDifference) {
    int numberOfCells = occurrenceMap.getNumberOfCells();
    double[] mapArray = new double[numberOfCells];
    for (int i = 0; i < numberOfCells; i++) {
      double[] newCoord = occurrenceMap.getCellCoord(i);
      double[] oldCoord = new double[] { newCoord[0] + poseDifference.Get(0).number().doubleValue(), //
          newCoord[1] + poseDifference.Get(1).number().doubleValue() };
      mapArray[i] = occurrenceMap.getValue(oldCoord[0], oldCoord[1]);
    }
    occurrenceMap.setMapArray(mapArray);
  }
}
