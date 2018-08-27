// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.Mat;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.demo.mg.util.slam.SlamOpenCVUtil;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
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
   * @param gokartLidarPose provided by lidar
   * @return array of BufferedImages of length 3 */
  public static BufferedImage[] constructFrames(SlamMapFrame[] slamMapFrames, SlamContainer slamContainer, GokartPoseInterface gokartLidarPose) {
    paintRawMap(slamContainer.getOccurrenceMap(), slamMapFrames[0].getBytes());
    slamMapFrames[0].addGokartPose(gokartLidarPose.getPose(), Color.BLACK);
    slamMapFrames[0].addGokartPose(slamContainer.getPose(), Color.BLUE);
    slamMapFrames[1].drawWaypoints(slamContainer.getWaypoints());
    slamMapFrames[1].addGokartPose(slamContainer.getPose(), Color.BLUE);
    BufferedImage[] combinedFrames = new BufferedImage[3];
    for (int i = 0; i < 3; i++)
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

  /** draws ellipse representing the vehicle onto the graphics object
   * 
   * @param pose {x[m], y[m], angle[]}
   * @param color
   * @param graphics
   * @param cornerX interpreted as [m]
   * @param cornerY interpreted as [m]
   * @param cellDim interpreted as [m]
   * @param kartLength interpreted as [m] */
  public static void addGokartPose(Tensor pose, Color color, Graphics2D graphics, double cornerX, double cornerY, double cellDim, double kartLength) {
    double[] worldPos = { pose.Get(0).number().doubleValue(), pose.Get(1).number().doubleValue() };
    double rotAngle = pose.Get(2).number().doubleValue();
    double[] framePos = worldToFrame(worldPos, cornerX, cornerY, cellDim);
    Ellipse2D ellipse = new Ellipse2D.Double(framePos[0] - kartLength / 2, framePos[1] - kartLength / 4, kartLength, kartLength / 2);
    AffineTransform old = graphics.getTransform();
    graphics.rotate(rotAngle, framePos[0], framePos[1]);
    graphics.setColor(color);
    graphics.draw(ellipse);
    graphics.fill(ellipse);
    graphics.setTransform(old);
  }

  /** transforms world frame coordinates to frame coordinates
   * 
   * @param worldPos interpreted as [m]
   * @param cornerX interpreted as [m]
   * @param cornerY interpreted as [m]
   * @param cellDim interpreted as [m]
   * @return framePos interpreted as [pixel] */
  private static double[] worldToFrame(double[] worldPos, double cornerX, double cornerY, double cellDim) {
    return new double[] { //
        (worldPos[0] - cornerX) / cellDim, //
        (worldPos[1] - cornerY) / cellDim };
  }

  // /** overlays poses of particles with highest likelihood onto slamMapFrame */
  // private static void drawParticlePoses(SlamMapFrame[] slamMapFrames, SlamProvider slamProvider, int particleNumber) {
  // SlamParticle[] slamParticles = slamProvider.getParticles();
  // Stream.of(slamParticles) //
  // .parallel() //
  // .sorted(SlamParticleLikelihoodComparator.INSTANCE) //
  // .limit(particleNumber) //
  // .collect(Collectors.toList());
  // for (int i = 0; i < particleNumber; i++)
  // slamMapFrames[0].addGokartPose(slamParticles[i].getPose(), Color.RED);
  // }
  /** draws a Mat object
   * 
   * @param processedMat mat object containing e.g. labels of feature extraction
   * @param bytes representing frame content */
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

  /** draws a way point object onto the graphics object
   * 
   * @param graphics
   * @param waypoint
   * @param radius interpreted as [pixel]
   * @param cornerX interpreted as [m]
   * @param cornerY interpreted as [m]
   * @param cellDim interpreted as [m] */
  public static void drawWaypoint(Graphics2D graphics, SlamWaypoint waypoint, double radius, double cornerX, double cornerY, double cellDim) {
    double[] framePos = worldToFrame(waypoint.getWorldPosition(), cornerX, cornerY, cellDim);
    Ellipse2D ellipse = new Ellipse2D.Double( //
        framePos[0] - radius, //
        framePos[1] - radius, //
        2 * radius, 2 * radius);
    graphics.setColor(waypoint.getVisibility() ? Color.GREEN : Color.ORANGE);
    graphics.fill(ellipse);
  }
}
