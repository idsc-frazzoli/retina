// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Optional;

import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum StaticHelper {
  ;
  private static final byte CLEAR_BYTE = -1; // white for type TYPE_BYTE_INDEXED
  private static final double RADIUS = 0.1; // [m]

  /** sets all frames for the visualization
   * 
   * @param slamMapFrames
   * @param slamCoreContainer
   * @param gokartLidarPose pose with units provided by lidar
   * @return array of BufferedImages of length 2 */
  public static BufferedImage[] constructFrames(SlamMapFrame[] slamMapFrames, SlamCoreContainer slamCoreContainer, //
      SlamPrcContainer slamPrcContainer, Tensor gokartLidarPose) {
    SlamMapFrame.setCorners(//
        slamCoreContainer.getOccurrenceMap().getCornerX(), //
        slamCoreContainer.getOccurrenceMap().getCornerY());
    paintRawMap(slamCoreContainer.getOccurrenceMap(), slamMapFrames[0].getBytes());
    Tensor pose = slamCoreContainer.getPoseUnitless().copy();
    Arrays.fill(slamMapFrames[1].getBytes(), CLEAR_BYTE);
    // setProcessedMat(slamCoreContainer.getLabels(), slamMapFrames[1].getBytes());
    {
      Optional<Tensor> optional = slamPrcContainer.getCurve();
      if (optional.isPresent())
        // drawInterpolate(slamMapFrames[1], slamCoreContainer.getPoseUnitless(), slamPrcContainer.getFittedCurve());
        drawInterpolate(slamMapFrames[1], slamCoreContainer.getPoseUnitless(), optional.get());
    }
    slamMapFrames[1].drawSlamWaypoints(slamPrcContainer.getWorldWaypoints(), slamPrcContainer.getValidities());
    // slamMapFrames[0].addGokartPose(gokartLidarPose, Color.BLACK);
    // slamMapFrames[0].addGokartPose(pose, Color.BLUE);
    slamMapFrames[1].addGokartPose(pose, Color.BLUE);
    BufferedImage[] combinedFrames = new BufferedImage[2];
    for (int i = 0; i < 2; i++)
      combinedFrames[i] = slamMapFrames[i].getFrame();
    return combinedFrames;
  }

  /** paints a MapProvider object
   * 
   * @param map object to be drawn
   * @param bytes representing BufferedImage of type TYPE_BYTE_INDEXED */
  private static void paintRawMap(MapProvider map, byte[] bytes) {
    double[] mapArray = map.getMapArray();
    double maxValue = map.getMaxValue();
    for (int i = 0; i < bytes.length; i++)
      bytes[i] = (byte) (216 + 39 * (1 - mapArray[i] / maxValue));
  }

  /** draws the curve estimated by the SLAM algorithm
   * 
   * @param slamMapFrame
   * @param pose of vehicle
   * @param cure in go kart frame */
  private static void drawInterpolate(SlamMapFrame slamMapFrame, Tensor poseUnitless, Tensor curve) {
    // transform to world frame coordinates for visualization
    TensorUnaryOperator local2World = new Se2Bijection(poseUnitless).forward();
    Tensor globalCurve = Tensor.of(curve.stream().map(local2World));
    for (int i = 0; i < globalCurve.length(); ++i) {
      double[] point = Primitives.toDoubleArray(globalCurve.get(i));
      slamMapFrame.drawPoint(point, Color.BLACK, RADIUS);
    }
  }

  /** draws a Mat object
   * 
   * @param processedMat mat object containing e.g. labels of feature extraction
   * @param bytes representing frame content */
  // to be used to visualize the raw processed Mat object from SlamMapProcessing
  @SuppressWarnings("unused")
  private static void setProcessedMat(Mat processedMat, byte[] bytes) {
    byte[] processedByteArray = SlamOpenCVUtil.matToByteArray(processedMat);
    for (int index = 0; index < bytes.length; ++index)
      bytes[index] = processedByteArray[index] == 0 ? CLEAR_BYTE : (byte) 0;
  }
}
