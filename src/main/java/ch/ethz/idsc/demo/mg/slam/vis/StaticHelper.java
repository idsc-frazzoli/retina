// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum StaticHelper {
  ;
  private static final byte CLEAR_BYTE = -1; // white
  private static final byte ORANGE = (byte) -52;
  private static final byte GREEN = (byte) 30;
  private static final byte BLUE = (byte) 5;
  private static final byte[] LOOKUP = { ORANGE, GREEN, BLUE };

  /** sets all frames for the visualization
   * 
   * @param slamMapFrames
   * @param slamContainer
   * @param gokartLidarPose pose with units provided by lidar
   * @return array of BufferedImages of length 2 */
  public static BufferedImage[] constructFrames(SlamMapFrame[] slamMapFrames, SlamContainer slamContainer, Tensor gokartLidarPose) {
    paintRawMap(slamContainer.getOccurrenceMap(), slamMapFrames[0].getBytes());
    slamMapFrames[0].addGokartPose(gokartLidarPose, Color.BLACK);
    slamMapFrames[0].addGokartPose(slamContainer.getPose(), Color.BLUE);
    slamMapFrames[1].drawSlamWaypoints(slamContainer.getSlamWaypoints());
    if (slamContainer.getSelectedSlamWaypoint().isPresent())
      slamMapFrames[1].drawSelectedSlamWaypoint(slamContainer.getSelectedSlamWaypoint().get());
    slamMapFrames[1].addGokartPose(slamContainer.getPose(), Color.BLUE);
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
    if (maxValue == 0)
      VisGeneralUtil.clearFrame(bytes);
    else
      for (int i = 0; i < bytes.length; i++)
        bytes[i] = (byte) (216 + 39 * (1 - mapArray[i] / maxValue));
  }

  /** draws a Mat object
   * 
   * @param processedMat mat object containing e.g. labels of feature extraction
   * @param bytes representing frame content */
  // to be used to visualize the raw processed Mat object from SlamMapProcessing
  @SuppressWarnings("unused")
  private static void setProcessedMat(Mat processedMat, byte[] bytes) {
    byte[] processedByteArray = SlamOpenCVUtil.matToByteArray(processedMat);
    for (int i = 0; i < bytes.length; i++) {
      if (processedByteArray[i] == 0)
        bytes[i] = CLEAR_BYTE;
      else {
        int labelID = processedByteArray[i] % 3;
        bytes[i] = LOOKUP[labelID];
      }
    }
  }
}
