// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** extracts the way points through thresholding and morphological processing for noise removal.
 * the centroids of the remaining connected components are detected as way points */
/* package */ class SlamWaypointDetection {
  private static final Mat KERNEL_DILATE = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(8, 8));
  private static final Mat KERNEL_ERODE = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(3, 3));
  private static final Point POINT = new Point(-1, -1);
  // ---
  private final double mapThreshold; // in [0,1]
  private final double cornerX; // [m]
  private final double cornerY; // [m]
  private final double cellDim; // [m]
  // ---
  private final Mat labels;

  public SlamWaypointDetection(SlamConfig slamConfig) {
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = Magnitude.METER.toDouble(slamConfig.corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig.corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig.cellDim);
    labels = new Mat(slamConfig.mapWidth(), slamConfig.mapHeight(), opencv_core.CV_8U);
  }

  /** finds way points through threshold operation, morphological processing and
   * connected component labeling.
   * calls opencv_imgproc#connectedComponentsWithStats
   * 
   * @param thresholdMap input object containing binary map
   * @return worldWaypoints [m] detected way points in world frame */
  public List<double[]> detectWaypoints(MapProvider thresholdMap) {
    Mat processedMap = mapProviderToBinaryMat(thresholdMap, mapThreshold);
    // opening
    opencv_imgproc.dilate(processedMap, processedMap, KERNEL_DILATE, POINT, 1, opencv_core.BORDER_CONSTANT, null);
    opencv_imgproc.erode(processedMap, processedMap, KERNEL_ERODE, POINT, 1, opencv_core.BORDER_CONSTANT, null);
    // connected components labeling and centroid extraction
    Mat centroid = new Mat(opencv_core.CV_64F);
    Mat stats = new Mat();
    opencv_imgproc.connectedComponentsWithStats(processedMap, labels, stats, centroid, 8, opencv_core.CV_16UC1);
    final int centroid_rows = centroid.rows();
    List<double[]> worldWaypoints = new ArrayList<>(centroid_rows - 1);
    // start at 1 because 0 is background label
    for (int index = 1; index < centroid_rows; ++index) {
      BytePointer arrayData = centroid.row(index).arrayData();
      double[] newWaypoint = { //
          arrayData.getDouble(0), //
          arrayData.getDouble(Double.BYTES) };
      worldWaypoints.add(index - 1, frameToWorld(newWaypoint));
    }
    processedMap.release(); // probably obsolete because underlying array was created in java
    centroid.release();
    stats.release();
    return worldWaypoints;
  }

  /** convert mapProvider to binary Mat object by invoking threshold operation.
   * 
   * @param mapProvider
   * @param mapThreshold value in [0,1] that indicates the threshold relative to maxValue of mapProvider
   * @return binary Mat object */
  private static Mat mapProviderToBinaryMat(MapProvider mapProvider, double mapThreshold) {
    double[] mapArray = mapProvider.getMapArray();
    byte[] byteArray = new byte[mapArray.length];
    double maxValueScaled = mapProvider.getMaxValue() * mapThreshold;
    Mat mat = new Mat(mapProvider.getMapWidth(), mapProvider.getMapHeight(), opencv_core.CV_8UC1);
    for (int index = 0; index < byteArray.length; ++index)
      byteArray[index] = mapArray[index] >= maxValueScaled ? (byte) 1 : 0;
    mat.data().put(byteArray);
    return mat;
  }

  /** coordinate transformation between pixel coordinates and world frame coordinates
   * 
   * @param framePos interpreted as [pixel] way point position in frame
   * @return worldPos interpreted as [m] way point position in world coordinate system */
  private double[] frameToWorld(double[] framePos) {
    return new double[] { //
        cornerX + framePos[0] * cellDim, //
        cornerY + framePos[1] * cellDim };
  }

  // could be used for visualization of the processed occurrence map
  public Mat getProcessedMat() {
    labels.convertTo(labels, opencv_core.CV_8UC1);
    return labels;
  }

  /** @param inputMap
   * @param resizeFactor [-]
   * @return outputMap same type as inputMap */
  // could be used in the future to reduce computational load
  private static Mat resizeMat(Mat inputMap, double resizeFactor) {
    int newHeight = (int) (inputMap.rows() / resizeFactor);
    int newWidth = (int) (inputMap.cols() / resizeFactor);
    Mat outputMap = new Mat(newHeight, newWidth, inputMap.type());
    opencv_imgproc.resize(inputMap, outputMap, outputMap.size());
    return outputMap;
  }
}
