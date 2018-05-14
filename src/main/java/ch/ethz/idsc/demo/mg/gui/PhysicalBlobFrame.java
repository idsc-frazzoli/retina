// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.pipeline.PhysicalBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.pipeline.TransformUtil;

/** provides a BufferedImage to visualize a list of PhysialBlob objects */
public class PhysicalBlobFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private static int frameWidth;
  private static int frameHeight;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final byte[] bytes;
  // world coord to visualization mapping
  private final double[][] fieldOfView; // defines trapezoid in physical space that is mapped onto image plane
  private final double[] physicalBoarders; // lower right point and dimensions of rectangle that confines fieldOfView

  public PhysicalBlobFrame(PipelineConfig pipelineConfig) {
    frameWidth = pipelineConfig.frameWidth.number().intValue();
    frameHeight = pipelineConfig.frameHeight.number().intValue();
    bufferedImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_BYTE_INDEXED);
    graphics = bufferedImage.createGraphics();
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    // TODO fieldOfView and physicalBoarders could be saved in .csv calibration file and loaded from there
    double[] upperLeft = TransformUtil.imageToWorld(10, 10);
    double[] upperRight = TransformUtil.imageToWorld(230, 10);
    double[] lowerLeft = TransformUtil.imageToWorld(10, 170);
    double[] lowerRight = TransformUtil.imageToWorld(230, 170);
    fieldOfView = new double[][] { upperLeft, upperRight, lowerLeft, lowerRight };
    // add padding for visualization
    physicalBoarders = new double[] { lowerLeft[0] - 2, upperRight[1] - 1, upperLeft[0] - lowerLeft[0] + 3, upperLeft[1] - upperRight[1] + 2 };
    setBackground();
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
    if (physicalBlobs.size() == 0) {
      return bufferedImage;
    } else {
      setBackground();
      for (int i = 0; i < physicalBlobs.size(); i++) {
        drawPhysicalBlob(graphics, physicalBlobs.get(i).getPos(), Color.WHITE);
      }
      return bufferedImage;
    }
  }

  /** background with trapezoid */
  public void setBackground() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
    // draw (0,0) position to show gokart
    int shapeSize = 20;
    Double[] origin = worldToViz(new double[] { 0, 0 });
    int xCoord = origin[0].intValue() - shapeSize / 2;
    int yCoord = origin[1].intValue() - shapeSize / 2;
    graphics.setColor(Color.BLACK);
    graphics.fillRect(xCoord, yCoord, shapeSize, shapeSize);
    // draw red tapezoid based on fieldOfView
    Double[] upperLeft = worldToViz(fieldOfView[0]);
    Double[] upperRight = worldToViz(fieldOfView[1]);
    Double[] lowerLeft = worldToViz(fieldOfView[2]);
    Double[] lowerRight = worldToViz(fieldOfView[3]);
    graphics.setColor(Color.RED);
    graphics.drawLine(upperLeft[0].intValue(), upperLeft[1].intValue(), upperRight[0].intValue(), upperRight[1].intValue());
    graphics.drawLine(upperLeft[0].intValue(), upperLeft[1].intValue(), lowerLeft[0].intValue(), lowerLeft[1].intValue());
    graphics.drawLine(lowerLeft[0].intValue(), lowerLeft[1].intValue(), lowerRight[0].intValue(), lowerRight[1].intValue());
    graphics.drawLine(lowerRight[0].intValue(), lowerRight[1].intValue(), upperRight[0].intValue(), upperRight[1].intValue());
  }

  /** draws an ellipse representing a PhysicalBlob object onto a Graphics2D object
   * 
   * @param graphics
   * @param blob
   * @param color */
  private void drawPhysicalBlob(Graphics2D graphics, double[] pos, Color color) {
    Double[] imageCoord = worldToViz(pos);
    graphics.setColor(color);
    graphics.fillOval(imageCoord[0].intValue(), imageCoord[1].intValue(), 20, 20);
  }

  /** this defines which part of the physical world is shown in the image
   * 
   * @param physicalPos
   * @return */
  private Double[] worldToViz(double[] physicalPos) {
    // TODO make sure both axes use same scale
    Double[] imageCoord = new Double[2];
    imageCoord[1] = frameHeight - (frameHeight * (physicalPos[0] - physicalBoarders[0]) / physicalBoarders[2]);
    imageCoord[0] = frameWidth - (frameWidth * (physicalPos[1] - physicalBoarders[1]) / physicalBoarders[3]);
    return imageCoord;
  }
}
