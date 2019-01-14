// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.tensor.io.Primitives;

/** blob tracking algorithm visualization static methods */
public enum VisBlobTrackUtil {
  ;
  /** draws an ellipse representing a ImageBlob object onto a Graphics2D object
   *
   * @param graphics object to be drawn onto
   * @param imageBlob to draw
   * @param color desired */
  public static void drawImageBlob(Graphics2D graphics, ImageBlob imageBlob, Color color) {
    AffineTransform affineTransform = graphics.getTransform();
    double rotAngle = imageBlob.getRotAngle();
    float[] semiAxes = Primitives.toFloatArray(imageBlob.getStandardDeviation());
    float leftCornerX = imageBlob.getPos()[0] - semiAxes[0];
    float leftCornerY = imageBlob.getPos()[1] - semiAxes[1];
    // draw ellipse with first eigenvalue aligned with x axis
    Ellipse2D ellipse2D = new Ellipse2D.Float(leftCornerX, leftCornerY, 2 * semiAxes[0], 2 * semiAxes[1]);
    // rotate around blob pos by rotAngle
    graphics.rotate(rotAngle, imageBlob.getPos()[0], imageBlob.getPos()[1]);
    graphics.setColor(color);
    graphics.draw(ellipse2D);
    graphics.setTransform(affineTransform);
  }
}
