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
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;

// TODO MG file contains a lot of functionality => class deserves a better, more specific name
/* package */ class SlamMapProcessingUtil {
  private static final Mat KERNEL_DILATE = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(8, 8));
  private static final Mat KERNEL_ERODE = //
      opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_RECT, new Size(3, 3));
  private static final Point POINT = new Point(-1, -1);
  // ---
  private final double mapThreshold;
  private final double cornerX; // [m]
  private final double cornerY; // [m]
  private final double cellDim; // [m]
  private final double visibleBoxXMin; // [m]
  private final double visibleBoxXMax; // [m]
  private final double visibleBoxHalfWidth; // [m]

  public SlamMapProcessingUtil(SlamConfig slamConfig) {
    mapThreshold = slamConfig.mapThreshold.number().doubleValue();
    cornerX = Magnitude.METER.toDouble(slamConfig.corner.Get(0));
    cornerY = Magnitude.METER.toDouble(slamConfig.corner.Get(1));
    cellDim = Magnitude.METER.toDouble(slamConfig.cellDim);
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = (visibleBoxXMax - visibleBoxXMin) * 0.5;
  }

  /** finds way points through threshold operation, morphological processing and
   * connected component labeling calls
   * opencv_imgproc#connectedComponentsWithStats
   * 
   * @param thresholdMap input object containing binary map
   * @param labels map with labeled connected components
   * @return worldWaypoints [m] detected way points in world frame */
  public List<double[]> findWaypoints(MapProvider thresholdMap, Mat labels) {
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
   * @param framePos [pixel] way point position in frame
   * @return worldPos [m] way point position in world coordinate system */
  private double[] frameToWorld(double[] framePos) {
    return new double[] { //
        cornerX + framePos[0] * cellDim, //
        cornerY + framePos[1] * cellDim };
  }

  /** creates SlamWaypoint objects based on worldWaypoints
   * 
   * @param worldWaypoints
   * @param pose unitless representation */
  public List<SlamWaypoint> getWaypoints(List<double[]> worldWaypoints, Tensor pose) {
    List<SlamWaypoint> slamWaypoints = new ArrayList<>();
    for (double[] worldWaypoint : worldWaypoints) {
      double[] gokartWaypoint = computeGokartPosition(worldWaypoint, pose);
      boolean visibility = visibleBoxXMin < gokartWaypoint[0] && gokartWaypoint[0] < visibleBoxXMax //
          && -visibleBoxHalfWidth < gokartWaypoint[1] && gokartWaypoint[1] < visibleBoxHalfWidth;
      slamWaypoints.add(new SlamWaypoint(worldWaypoint, visibility));
    }
    return slamWaypoints;
  }

  /** transforms between world and go kart frame
   * 
   * @param worldPosition position of point in world frame
   * @param pose unitless representation go kart pose
   * @return position in go kart frame */
  private static double[] computeGokartPosition(double[] worldPosition, Tensor pose) {
    // TODO MG the waypoint previously existed in local gokart coordinates, right?
    // ... we can look for a way to preserve the local coordinates instead of inverting the transform.
    Tensor gokartPosition = new Se2Bijection(pose).inverse() //
        .apply(Tensors.vectorDouble(worldPosition));
    return Primitives.toDoubleArray(gokartPosition);
  }

  /** @param inputMap
   * @param resizeFactor [-]
   * @return outputMap same type as inputMap */
  // TODO could be used in the future to reduce computational load
  private static Mat resizeMat(Mat inputMap, double resizeFactor) {
    int newHeight = (int) (inputMap.rows() / resizeFactor);
    int newWidth = (int) (inputMap.cols() / resizeFactor);
    Mat outputMap = new Mat(newHeight, newWidth, inputMap.type());
    opencv_imgproc.resize(inputMap, outputMap, outputMap.size());
    return outputMap;
  }
}
