// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.demo.mg.util.CVUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

/** extracts way points from a map */
class SlamWayPointExtraction {
  private final GokartPoseInterface gokartPose;
  private final MapProvider thresholdMap;
  private final Mat rectKernel1 = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, //
      new Size(12, 12));
  private final Mat rectKernel2 = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, //
      new Size(3, 3));
  private final List<double[]> frameWayPoints; // in frame coordinates
  private final List<double[]> worldWayPoints; // in physical coordinates
  private final double wayPointUpdateRate;
  private final double mapThreshold;
  private final double cornerX;
  private final double cornerY;
  private final double cellDim;
  private double lastComputationTimeStamp;
  private Mat processedMap;
  private Mat labels;

  SlamWayPointExtraction(SlamConfig slamConfig, GokartPoseInterface gokartPose) {
    this.gokartPose = gokartPose;
    thresholdMap = new MapProvider(slamConfig);
    processedMap = new Mat(thresholdMap.getWidth(), thresholdMap.getHeight(), opencv_core.CV_8U);
    labels = new Mat(thresholdMap.getWidth(), thresholdMap.getHeight(), opencv_core.CV_8U);
    wayPointUpdateRate = slamConfig.wayPointUpdateRate.number().doubleValue();
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = slamConfig.corner.Get(0).number().doubleValue();
    cornerY = slamConfig.corner.Get(1).number().doubleValue();
    cellDim = slamConfig.cellDim.number().doubleValue();
    frameWayPoints = new ArrayList<>();
    worldWayPoints = new ArrayList<>();
  }

  public void initialize(double initTimeStamp) {
    lastComputationTimeStamp = initTimeStamp;
  }

  public void mapPostProcessing(MapProvider occurrenceMap, double currentTimeStamp) {
    if ((currentTimeStamp - lastComputationTimeStamp) > wayPointUpdateRate) {
      computeThresholdMap(occurrenceMap);
      findWayPoints();
      updateWorldWayPoints();
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  private void updateWorldWayPoints() {
    for (int i = 0; i < frameWayPoints.size(); i++) {
      double[] worldWayPoint = frameToWorld(frameWayPoints.get(i));
      if (i < worldWayPoints.size()) {
        worldWayPoints.set(i, worldWayPoint);
      } else {
        worldWayPoints.add(worldWayPoint);
      }
    }
  }

  private double[] frameToWorld(double[] framePos) {
    double[] physicalPos = new double[2];
    physicalPos[0] = cornerX + framePos[0] * cellDim;
    physicalPos[1] = cornerY + framePos[1] * cellDim;
    return physicalPos;
  }

  // first version: threshold operation to remove noise
  private void computeThresholdMap(MapProvider map) {
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
    // opening
    opencv_imgproc.dilate(processedMap, processedMap, rectKernel1, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    opencv_imgproc.erode(processedMap, processedMap, rectKernel2, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    // connected components labeling and centroid extraction
    Mat centroid = new Mat(opencv_core.CV_64F);
    Mat stats = new Mat();
    opencv_imgproc.connectedComponentsWithStats(processedMap, labels, stats, centroid, 8, opencv_core.CV_16UC1);
    // start at one because 0 is background
    for (int i = 1; i < centroid.rows(); i++) {
      double[] newWayPoint = { centroid.row(i).arrayData().getDouble(0), centroid.row(i).arrayData().getDouble(8) };
      if (i - 1 < frameWayPoints.size()) {
        frameWayPoints.set(i - 1, newWayPoint);
      } else {
        frameWayPoints.add(i - 1, newWayPoint);
      }
    }
  }

  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }

  public List<double[]> getFrameWayPoints() {
    return frameWayPoints;
  }

  public List<double[]> getWorldWayPoints() {
    return worldWayPoints;
  }
}
