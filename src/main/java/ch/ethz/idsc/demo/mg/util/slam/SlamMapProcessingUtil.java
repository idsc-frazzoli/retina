// code by mg
package ch.ethz.idsc.demo.mg.util.slam;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

// static methods to facilitate map and waypoint processing
public class SlamMapProcessingUtil {
  private final static int arrayStep = Double.SIZE / Byte.SIZE;

  /** creates a binary map based on a threshold operation of rawMap
   * 
   * @param rawMap
   * @param thresholdMap
   * @param mapThreshold [-] between [0,1] actual threshold = maxValue*mapThreshold */
  public static void computeThresholdMap(MapProvider rawMap, MapProvider thresholdMap, double mapThreshold) {
    double[] mapCopy = rawMap.getMapArray();
    double maxValue = rawMap.getMaxValue();
    for (int i = 0; i < mapCopy.length; i++) {
      if (mapCopy[i] > maxValue * mapThreshold) {
        thresholdMap.setValue(i, 1);
      } else {
        thresholdMap.setValue(i, 0);
      }
    }
  }

  /** @param thresholdMap input object containing binary map
   * @param processedMap
   * @param labels map with labelled connected components
   * @param frameWayPoints centroids of labelled connected components
   * @param dilateKernel parameter for dilate morphological operation
   * @param erodeKernel parameter for erode morphological operation */
  public static List<double[]> findWayPoints(MapProvider thresholdMap, Mat labels, Mat dilateKernel, Mat erodeKernel) {
    Mat processedMap = SlamOpenCVUtil.mapProviderToMat(thresholdMap);
    // opening
    opencv_imgproc.dilate(processedMap, processedMap, dilateKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    opencv_imgproc.erode(processedMap, processedMap, erodeKernel, new Point(-1, -1), 1, opencv_core.BORDER_CONSTANT, null);
    // connected components labeling and centroid extraction
    Mat centroid = new Mat(opencv_core.CV_64F);
    Mat stats = new Mat();
    opencv_imgproc.connectedComponentsWithStats(processedMap, labels, stats, centroid, 8, opencv_core.CV_16UC1);
    // start at 1 because 0 is background label
    List<double[]> frameWayPoints = new ArrayList<>(centroid.rows() - 1);
    for (int i = 1; i < centroid.rows(); i++) {
      double[] newWayPoint = { centroid.row(i).arrayData().getDouble(0), centroid.row(i).arrayData().getDouble(arrayStep) };
      frameWayPoints.add(i - 1, newWayPoint);
    }
    return frameWayPoints;
  }

  /** @param worldWayPoints
   * @param frameWayPoints
   * @param cornerX
   * @param cornerY
   * @param cellDim */
  public static List<double[]> updateWorldWayPoints(List<double[]> frameWayPoints, double cornerX, double cornerY, double cellDim) {
    List<double[]> worldWayPoints = new ArrayList<>(frameWayPoints.size());
    for (int i = 0; i < frameWayPoints.size(); i++) {
      double[] worldWayPoint = frameToWorld(frameWayPoints.get(i), cornerX, cornerY, cellDim);
      worldWayPoints.add(worldWayPoint);
    }
    return worldWayPoints;
  }

  private static double[] frameToWorld(double[] framePos, double cornerX, double cornerY, double cellDim) {
    double[] physicalPos = new double[2];
    physicalPos[0] = cornerX + framePos[0] * cellDim;
    physicalPos[1] = cornerY + framePos[1] * cellDim;
    return physicalPos;
  }

  /** sets waypoint objects according to world frame waypoint positions
   * 
   * @param worldWayPoints [m] in world frame
   * @param gokartWayPoints
   * @param currentPose */
  public static void setGokartWayPoints(List<double[]> worldWayPoints, List<WayPoint> gokartWayPoints, Tensor currentPose) {
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(GokartPoseHelper.toSE2Matrix(currentPose)));
    for (int i = 0; i < worldWayPoints.size(); i++) {
      double[] worldPosition = worldWayPoints.get(i);
      WayPoint slamWayPoint = new WayPoint(worldPosition);
      Tensor gokartPosition = worldToGokartLayer.toVector(worldWayPoints.get(i)[0], worldWayPoints.get(i)[1]);
      slamWayPoint.setGokartPosition(gokartPosition);
      gokartWayPoints.add(i, slamWayPoint);
    }
  }

  /** sets visibility field of waypoints
   * 
   * @param gokartWayPoints
   * @param visibleGokartWayPoints
   * @param visibleBoxXMin [m] in go kart frame
   * @param visibleBoxXMax [m] in go kart frame
   * @param visibleBoxHalfWidth [m] in go kart frame */
  public static void checkVisibility(List<WayPoint> gokartWayPoints, List<WayPoint> visibleGokartWayPoints, double visibleBoxXMin, double visibleBoxXMax,
      double visibleBoxHalfWidth) {
    for (int i = 0; i < gokartWayPoints.size(); i++) {
      double[] gokartPosition = gokartWayPoints.get(i).getGokartPosition();
      if (gokartPosition[0] > visibleBoxXMin && gokartPosition[1] < visibleBoxXMax && gokartPosition[1] > -visibleBoxHalfWidth
          && gokartPosition[1] < visibleBoxHalfWidth) {
        gokartWayPoints.get(i).setVisibility(true);
        visibleGokartWayPoints.add(gokartWayPoints.get(i));
      }
    }
  }

  /** finds visible waypoint that is furthest away
   * 
   * @param visibleGokartWayPoints
   * @param purePursuitIndex index of element in visibleGokartWayPoints that is furthest away */
  public static void choosePurePursuitPoint(List<WayPoint> visibleGokartWayPoints, int purePursuitIndex) {
    double maxDistance = 0;
    purePursuitIndex = -1;
    for (int i = 0; i < visibleGokartWayPoints.size(); i++) {
      if (visibleGokartWayPoints.get(i).getGokartPosition()[0] > maxDistance) {
        maxDistance = visibleGokartWayPoints.get(i).getGokartPosition()[0];
        purePursuitIndex = i;
      }
    }
  }
  // idea: attention module that guesses position of next waypoint
}
