// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.pipeline.PhysicalBlob;

// provides a BufferedImage to visualize a list of PhysialBlob objects
public class PhysicalBlobFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private final BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_INDEXED);
  private final Graphics2D graphics = bufferedImage.createGraphics();
  private final byte[] bytes;

  public PhysicalBlobFrame() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    clearImage();
  }

  // resets all pixel to grey
  public void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }

  // simple fct to be replaced later
  public BufferedImage getFrame() {
    return bufferedImage;
  }

  // paint physical Blobs
  public BufferedImage overlayPhysicalBlobs(List<PhysicalBlob> physicalBlobs) {
    for (int i = 0; i < physicalBlobs.size(); i++) {
      drawPhysicalBlob(graphics, physicalBlobs.get(i), Color.BLACK);
    }
    return bufferedImage;
  }

  // draws an ellipse representing a PhysicalBlob object onto a Graphics2D object
  private void drawPhysicalBlob(Graphics2D graphics, PhysicalBlob blob, Color color) {
    double[] imageCoord = convertWorldToImageCoord(blob.getPos());
    // AffineTransform old = graphics.getTransform();
    Ellipse2D ellipse = new Ellipse2D.Double(imageCoord[0], imageCoord[1], 50, 50);
    graphics.setColor(color);
    graphics.draw(ellipse);
    // graphics.setTransform(old);
  }

  // this defines which part of the physical world is shown in the image
  private double[] convertWorldToImageCoord(double[] physicalPos) {
    double[] imageCoord = new double[2];
    imageCoord[0] = 100 * physicalPos[0] + 50;
    imageCoord[1] = 100 * physicalPos[1] + 50;
    return imageCoord;
  }
}
