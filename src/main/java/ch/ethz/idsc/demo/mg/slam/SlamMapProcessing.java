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

/** extracts way points from a map using threshold operation, morphological processing
 * and connected component labeling */
class SlamMapProcessing {
  private final static int arrayStep = Double.SIZE / Byte.SIZE;
  private final MapProvider thresholdMap;
  private final Mat dilateKernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, //
      new Size(8, 8));
  private final Mat erodeKernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, //
      new Size(3, 3));
  private final double wayPointUpdateRate;
  private final double mapThreshold;
  private final double cornerX;
  private final double cornerY;
  private final double cellDim;
  private List<double[]> frameWayPoints; // in frame coordinates
  private List<double[]> worldWayPoints; // in physical coordinates
  private double lastComputationTimeStamp;
  private Mat processedMap;
  private Mat labels;

  SlamMapProcessing(SlamConfig slamConfig) {
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

  // first version: threshold operation to remove noise
  private void computeThresholdMap(MapProvider map) {
    double[] mapCopy = map.getMapArray();
    double maxValue = map.getMaxValue();
    for (int i = 0; i < mapCopy.length; i++) {
      // TODO maybe respective to "local" maxValue?
      if (mapCopy[i] > maxValue * mapThreshold) {
        thresholdMap.setValue(i, 1);
      } else {
        thresholdMap.setValue(i, 0);
      }
    }
  }

  // through image processing, extract way points in the threshold map
  private void findWayPoints() {
    processedMap = CVUtil.mapProviderToMat(thresholdMap);
    // opening
    opencv_imgproc.dilate(processedMap, processedMap, dilateKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    opencv_imgproc.erode(processedMap, processedMap, erodeKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    // connected components labeling and centroid extraction
    Mat centroid = new Mat(opencv_core.CV_64F);
    Mat stats = new Mat();
    opencv_imgproc.connectedComponentsWithStats(processedMap, labels, stats, centroid, 8, opencv_core.CV_16UC1);
    // start at 1 because 0 is background label
    frameWayPoints = new ArrayList<>(centroid.rows() - 1);
    for (int i = 1; i < centroid.rows(); i++) {
      double[] newWayPoint = { centroid.row(i).arrayData().getDouble(0), centroid.row(i).arrayData().getDouble(arrayStep) };
      frameWayPoints.add(i - 1, newWayPoint);
    }
  }

  private void updateWorldWayPoints() {
    worldWayPoints = new ArrayList<>(frameWayPoints.size());
    for (int i = 0; i < frameWayPoints.size(); i++) {
      double[] worldWayPoint = frameToWorld(frameWayPoints.get(i));
      worldWayPoints.add(worldWayPoint);
    }
  }

  // TODO maybe move to static utility class
  private double[] frameToWorld(double[] framePos) {
    double[] physicalPos = new double[2];
    physicalPos[0] = cornerX + framePos[0] * cellDim;
    physicalPos[1] = cornerY + framePos[1] * cellDim;
    return physicalPos;
  }

  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }

  public List<double[]> getWorldWayPoints() {
    return worldWayPoints;
  }
}
