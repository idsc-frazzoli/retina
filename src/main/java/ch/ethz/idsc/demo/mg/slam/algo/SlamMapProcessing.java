// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.util.slam.SlamMapProcessingUtil;

/** extracts way points from a map using threshold operation, morphological processing
 * and connected component labeling */
class SlamMapProcessing {
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
  private Mat labels;

  SlamMapProcessing(SlamConfig slamConfig) {
    thresholdMap = new MapProvider(slamConfig);
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
    if (currentTimeStamp - lastComputationTimeStamp > wayPointUpdateRate) {
      SlamMapProcessingUtil.computeThresholdMap(occurrenceMap, thresholdMap, mapThreshold);
      frameWayPoints = SlamMapProcessingUtil.findWayPoints(thresholdMap, labels, dilateKernel, erodeKernel);
      worldWayPoints = SlamMapProcessingUtil.updateWorldWayPoints(frameWayPoints, cornerX, cornerY, cellDim);
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }

  public List<double[]> getWorldWayPoints() {
    return worldWayPoints;
  }
}
