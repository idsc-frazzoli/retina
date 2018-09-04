// code by mg
package ch.ethz.idsc.demo.mg.util.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.tensor.io.Primitives;

/** blob tracking algorithm visualization static methods */
public enum VisBlobTrackUtil {
  ;
  /** draw ellipses for image based on list of blobs for the image.
   *
   * @param graphics
   * @param blobs */
  public static void drawEllipsesOnImage(Graphics2D graphics, List<ImageBlob> blobs) {
    blobs.forEach(blob -> drawImageBlob(graphics, blob, Color.WHITE));
  }

  /** draws an ellipse representing a ImageBlob object onto a Graphics2D object
   *
   * @param graphics object to be drawn onto
   * @param blob ImageBlob to be drawn
   * @param color desired */
  public static void drawImageBlob(Graphics2D graphics, ImageBlob blob, Color color) {
    AffineTransform old = graphics.getTransform();
    double rotAngle = blob.getRotAngle();
    float[] semiAxes = Primitives.toFloatArray(blob.getStandardDeviation());
    float leftCornerX = blob.getPos()[0] - semiAxes[0];
    float leftCornerY = blob.getPos()[1] - semiAxes[1];
    // draw ellipse with first eigenvalue aligned with x axis
    Ellipse2D ellipse = new Ellipse2D.Float(leftCornerX, leftCornerY, 2 * semiAxes[0], 2 * semiAxes[1]);
    // rotate around blob pos by rotAngle
    graphics.rotate(rotAngle, blob.getPos()[0], blob.getPos()[1]);
    graphics.setColor(color);
    graphics.draw(ellipse);
    graphics.setTransform(old);
  }
}
