// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_imgproc;

import ch.ethz.idsc.demo.mg.slam.MapProvider;

enum SlamMapProcessingUtil {
  ;
  private static final Point POINT = new Point(-1, -1);

  /** finds waypoints through threshold operation, morphological processing and connected component labeling
   * 
   * calls
   * opencv_imgproc#connectedComponentsWithStats
   * 
   * @param thresholdMap input object containing binary map
   * @param labels map with labelled connected components
   * @param dilateKernel parameter for dilate morphological operation
   * @param erodeKernel parameter for erode morphological operation
   * @param cornerX [m]
   * @param cornerY [m]
   * @param cellDim [m]
   * @return worldWayPoints [m] detected waypoints in world frame */
  public static List<double[]> findWayPoints( //
      MapProvider thresholdMap, Mat labels, Mat dilateKernel, Mat erodeKernel, double cornerX, double cornerY, double cellDim) {
    Mat processedMap = mapProviderToMat(thresholdMap);
    // opening
    opencv_imgproc.dilate(processedMap, processedMap, dilateKernel, POINT, 1, opencv_core.BORDER_CONSTANT, null);
    opencv_imgproc.erode(processedMap, processedMap, erodeKernel, POINT, 1, opencv_core.BORDER_CONSTANT, null);
    // connected components labeling and centroid extraction
    Mat centroid = new Mat(opencv_core.CV_64F);
    Mat stats = new Mat();
    opencv_imgproc.connectedComponentsWithStats(processedMap, labels, stats, centroid, 8, opencv_core.CV_16UC1);
    List<double[]> worldWayPoints = new ArrayList<>(centroid.rows() - 1);
    // start at 1 because 0 is background label
    for (int index = 1; index < centroid.rows(); ++index) {
      double[] newWayPoint = { //
          centroid.row(index).arrayData().getDouble(0), //
          centroid.row(index).arrayData().getDouble(Double.BYTES) };
      worldWayPoints.add(index - 1, frameToWorld(newWayPoint, cornerX, cornerY, cellDim));
    }
    processedMap.release(); // probably obsolete because underlying array was created in java
    centroid.release();
    stats.release();
    return worldWayPoints;
  }

  // TODO MG computeThresholdMap is obsolete. mapProviderToMat directly can map occupancyMap -> byte array via thresholding
  /** creates a binary map based on a threshold operation of rawMap
   * 
   * @param rawMap
   * @param thresholdMap
   * @param mapThreshold [-] between [0,1]: actual threshold = maxValue * mapThreshold */
  public static void computeThresholdMap(MapProvider rawMap, double mapThreshold, MapProvider thresholdMap) {
    double[] mapCopy = rawMap.getMapArray();
    double maxValue_scaled = rawMap.getMaxValue() * mapThreshold;
    for (int index = 0; index < mapCopy.length; ++index)
      thresholdMap.setValue(index, maxValue_scaled < mapCopy[index] ? 1 : 0);
  }

  /** convert binary image to a mat object
   * 
   * @param mapProvider
   * @return binary Mat object */
  private static Mat mapProviderToMat(MapProvider mapProvider) {
    double[] mapArray = mapProvider.getMapArray();
    byte[] byteArray = new byte[mapArray.length];
    Mat mat = new Mat(mapProvider.getWidth(), mapProvider.getHeight(), opencv_core.CV_8UC1);
    for (int index = 0; index < byteArray.length; ++index)
      byteArray[index] = mapArray[index] == 0 ? 0 : (byte) 1;
    mat.data().put(byteArray);
    return mat;
  }

  /** coordinate transformation
   * 
   * @param framePos [pixel] waypoint position in frame
   * @param cornerX [m]
   * @param cornerY [m]
   * @param cellDim [m]
   * @return worldPos [m] waypoint position in world coordinate system */
  private static double[] frameToWorld(double[] framePos, double cornerX, double cornerY, double cellDim) {
    return new double[] { //
        cornerX + framePos[0] * cellDim, //
        cornerY + framePos[1] * cellDim };
  }

  /** @param inputMap
   * @param resizeFactor [-]
   * @return outputMap same type as inputMap */
  // TODO MG remove if not needed
  private static Mat resizeMat(Mat inputMap, double resizeFactor) {
    int newHeight = (int) (inputMap.rows() / resizeFactor);
    int newWidth = (int) (inputMap.cols() / resizeFactor);
    Mat outputMap = new Mat(newHeight, newWidth, inputMap.type());
    opencv_imgproc.resize(inputMap, outputMap, outputMap.size());
    return outputMap;
  }
}
