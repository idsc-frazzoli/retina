// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamMapFrameUtil {
  ;
  /** draws ellipse representing the vehicle onto the graphics object
   * 
   * @param pose with or without units
   * @param color
   * @param graphics
   * @param cornerX interpreted as [m]
   * @param cornerY interpreted as [m]
   * @param cellDim interpreted as [m]
   * @param kartLength interpreted as [pixel] */
  public static void addGokartPose(Tensor pose, Color color, Graphics2D graphics, double cornerX, //
      double cornerY, double cellDim, double kartLength) {
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

  /** draws a way point object onto the graphics object
   * 
   * @param graphics
   * @param waypoint
   * @param color used for the way point
   * @param radius interpreted as [pixel]
   * @param cornerX interpreted as [m]
   * @param cornerY interpreted as [m]
   * @param cellDim interpreted as [m] */
  public static void drawWaypoint(Graphics2D graphics, SlamWaypoint waypoint, Color color, double radius, //
      double cornerX, double cornerY, double cellDim) {
    double[] framePos = worldToFrame(waypoint.getWorldPosition(), cornerX, cornerY, cellDim);
    Ellipse2D circle = new Ellipse2D.Double( //
        framePos[0] - radius, //
        framePos[1] - radius, //
        2 * radius, 2 * radius);
    graphics.setColor(color);
    graphics.fill(circle);
  }
}
