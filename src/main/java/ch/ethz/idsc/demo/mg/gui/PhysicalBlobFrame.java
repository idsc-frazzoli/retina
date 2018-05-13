// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.pipeline.TransformUtil;
import ch.ethz.idsc.demo.mg.pipeline.PhysicalBlob;

/** provides a BufferedImage to visualize a list of PhysialBlob objects */
public class PhysicalBlobFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private static final int WIDTH = 180;
  private static final int HEIGHT = 180;
  private final BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_BYTE_INDEXED);
  private final Graphics2D graphics = bufferedImage.createGraphics();
  private final byte[] bytes;
  // world coord to visualization mapping
  private final double[][] fieldOfView; // defines trapezoid in physical space that is mapped onto image plane
  private final double[] physicalBoarders; // lower right point and dimensions of rectangle that confines fieldOfView

  public PhysicalBlobFrame() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    // TODO could save this values in .csv calibration file and then load through pipelineConfig
    double[] upperLeft = TransformUtil.imageToWorld(10, 10);
    double[] upperRight = TransformUtil.imageToWorld(230, 10);
    double[] lowerLeft = TransformUtil.imageToWorld(10, 170);
    double[] lowerRight = TransformUtil.imageToWorld(230, 170);
    fieldOfView = new double[][] { upperLeft, upperRight, lowerLeft, lowerRight };
    physicalBoarders = new double[] { lowerLeft[0] - 1, upperRight[1] - 1, upperLeft[0] + 2 - lowerLeft[0], upperLeft[1] + 2 - upperRight[1] };
    clearImage();
    physicalFrameSetup();
  }

  /** resets all pixel to grey */
  public void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }

  /** simple fct to be replaced later
   * 
   * @return */
  public BufferedImage getFrame() {
    return bufferedImage;
  }

  /** paint physical Blobs
   * 
   * @param physicalBlobs
   * @return */
  public BufferedImage overlayPhysicalBlobs(List<PhysicalBlob> physicalBlobs) {
    for (int i = 0; i < physicalBlobs.size(); i++) {
      // drawPhysicalBlob(graphics, physicalBlobs.get(i).getPos(), Color.BLACK);
    }
    return bufferedImage;
  }

  /** sets up initial physicalFrame */
  private void physicalFrameSetup() {
    // draw (0,0) position to show gokart
    // ..
    // draw red tapezoid based on fieldOfView
    double[] upperLeft = worldToViz(fieldOfView[0]);
    double[] upperRight = worldToViz(fieldOfView[1]);
    double[] lowerLeft = worldToViz(fieldOfView[2]);
    double[] lowerRight = worldToViz(fieldOfView[3]);
    graphics.setColor(Color.RED);
    graphics.drawLine((int) upperLeft[0], (int) upperLeft[1], (int) upperRight[0], (int) upperRight[1]);
    graphics.drawLine((int) upperLeft[0], (int) upperLeft[1], (int) lowerLeft[0], (int) lowerLeft[1]);
    graphics.drawLine((int) lowerLeft[0], (int) lowerLeft[1], (int) lowerRight[0], (int) lowerRight[1]);
    graphics.drawLine((int) lowerRight[0], (int) lowerRight[1], (int) upperRight[0], (int) upperRight[1]);
  }

  /** draws an ellipse representing a PhysicalBlob object onto a Graphics2D object
   * 
   * @param graphics
   * @param blob
   * @param color */
  private void drawPhysicalBlob(Graphics2D graphics, double[] pos, Color color) {
    double[] imageCoord = worldToViz(pos);
    // AffineTransform old = graphics.getTransform();
    Ellipse2D ellipse = new Ellipse2D.Double(imageCoord[0], imageCoord[1], 50, 50);
    graphics.setColor(color);
    graphics.draw(ellipse);
    // graphics.setTransform(old);
  }

  /** this defines which part of the physical world is shown in the image
   * 
   * @param physicalPos
   * @return */
  private double[] worldToViz(double[] physicalPos) {
    double[] imageCoord = new double[2];
    imageCoord[1] = HEIGHT - (HEIGHT * (physicalPos[0] - physicalBoarders[0]) / physicalBoarders[2]);
    imageCoord[0] = WIDTH - (WIDTH * (physicalPos[1] - physicalBoarders[1]) / physicalBoarders[3]);
    return imageCoord;
  }
}
