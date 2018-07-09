// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.util.CVUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/** extracts way points from a map
 * as an input, we receive a MapProvider object and estimated go kart pose
 * we compute which part of the map is currently seen by the go kart
 * inside this part of the map, we extract the way points. First idea: threshold
 * the way points can be connected. We can check is minimum curvature etc is fullfilled */
class SlamWayPointExtraction {
  private final GokartPoseInterface gokartPose;
  private final MapProvider thresholdMap;
  private final double wayPointUpdateRate;
  private final double mapThreshold;
  private List<double[]> wayPoints;
  private double lastComputationTimeStamp;
  private Mat processedMap;

  SlamWayPointExtraction(SlamConfig slamConfig, GokartPoseInterface gokartPose) {
    this.gokartPose = gokartPose;
    thresholdMap = new MapProvider(slamConfig);
    wayPointUpdateRate = slamConfig.wayPointUpdateRate.number().doubleValue();
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
  }

  public void initialize(double initTimeStamp) {
    lastComputationTimeStamp = initTimeStamp;
  }

  public void mapPostProcessing(MapProvider occurrenceMap, double currentTimeStamp) {
    if ((currentTimeStamp - lastComputationTimeStamp) > wayPointUpdateRate) {
      computeThresholdMap(occurrenceMap);
      findWayPoints();
      createTrajectory();
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  // first version: threshold operation to remove noise
  public void computeThresholdMap(MapProvider map) {
    double[] mapCopy = map.getMapArray();
    double maxValue = map.getMaxValue();
    for (int i = 0; i < mapCopy.length; i++) {
      // TODO maybe respective to "local" maxValue?
      if (mapCopy[i] > maxValue * mapThreshold) {
        thresholdMap.setValue(i, 1);
      }
    }
  }

  // through image processing, extract way points in the threshold map
  private void findWayPoints() {
    // convert to Mat object
    processedMap = CVUtil.mapProviderToMat(thresholdMap);
    // do all kinds of crazy image processing here
    // try opening/closing/dilation
  }

  // generate trajectory by connecting line segments
  private void createTrajectory() {
    // ..
  }

  public Mat getProcessedMat() {
    return processedMap;
  }
}
