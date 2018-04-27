// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides a bufferedImage with the accumulated events and overlaid features drawn as ellipses.
// also contains static methods to be used by other visualization tools
public class AccumulatedEventFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private static final byte[] VALUE = { 0, (byte) 255 };
  // ---
  private final BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_INDEXED);
  private final Graphics2D graphics = bufferedImage.createGraphics();
  private final byte[] bytes;

  public AccumulatedEventFrame() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    clearImage();
  }

  public BufferedImage getAccumulatedEvents() {
    return rotate180Degrees(bufferedImage);
  }

  // overlays blobs and sets color according to ImageBlobSelector module
  public BufferedImage overlayActiveBlobs(List<ImageBlob> blobs) {
    for (int i = 0; i < blobs.size(); i++) {
      if (blobs.get(i).getIsRecognized()) {
        drawImageBlob(graphics, blobs.get(i), Color.GREEN);
      } else {
        drawImageBlob(graphics, blobs.get(i), Color.RED);
      }
    }
    return rotate180Degrees(bufferedImage);
  }

  public BufferedImage overlayHiddenBlobs(List<ImageBlob> blobs) {
    for (int i = 0; i < blobs.size(); i++) {
      drawImageBlob(graphics, blobs.get(i), Color.GRAY);
    }
    return rotate180Degrees(bufferedImage);
  }

  // marks the event in the image plane as a dark or light pixel
  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    int index = davisDvsEvent.x + davisDvsEvent.y * 240;
    bytes[index] = VALUE[davisDvsEvent.i];
  }

  // resets all pixel to grey
  public void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }

  // rotates BufferedImage by 180 degrees
  private static BufferedImage rotate180Degrees(BufferedImage bufferedImage) {
    AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
    tx.translate(-bufferedImage.getWidth(), -bufferedImage.getHeight());
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    bufferedImage = op.filter(bufferedImage, null);
    return bufferedImage;
  }

  // draws an ellipse representing an ImageBlob object onto a Graphics2D object
  public static void drawImageBlob(Graphics2D graphics, ImageBlob blob, Color color) {
    AffineTransform old = graphics.getTransform();
    double rotAngle = blob.getRotAngle();
    float[] semiAxes = blob.getStandardDeviation();
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
