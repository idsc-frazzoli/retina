// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.vis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackConfig;
import ch.ethz.idsc.demo.mg.blobtrack.PhysicalBlob;
import ch.ethz.idsc.demo.mg.util.calibration.ImageToGokartUtil;

/** BufferedImage to visualize a list of PhysialBlob objects */
/* package */ class PhysicalBlobFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private static int frameWidth;
  private static int frameHeight;
  // ---
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final byte[] bytes;
  // world coord to visualization mapping
  private final ImageToGokartUtil transformUtil;
  private final double[][] fieldOfView; // contains image plane coordinates of trapezoid defining field of view
  private final double scaleFactor; // [pixel/m] how many pixels in the frame correspond to one meter in physical world
  private final int gokartSize; // [pixel]
  private final double objectSize; // size of physicalBlobs
  private final int[] originPos; // [pixel] image plane location of physical world origin
  private final Path2D trapezoid; // describes the field of view in physical space

  public PhysicalBlobFrame(BlobTrackConfig pipelineConfig) {
    frameWidth = pipelineConfig.frameWidth.number().intValue();
    frameHeight = pipelineConfig.frameHeight.number().intValue();
    bufferedImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_BYTE_INDEXED);
    graphics = bufferedImage.createGraphics();
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    transformUtil = pipelineConfig.davisConfig.createImageToGokartUtil();
    scaleFactor = pipelineConfig.scaleFactor.number().doubleValue();
    originPos = new int[] { pipelineConfig.originPosX.number().intValue(), pipelineConfig.originPosY.number().intValue() };
    objectSize = pipelineConfig.objectSize.number().doubleValue();
    gokartSize = pipelineConfig.gokartSize.number().intValue();
    // TODO MG physical boarder points could be loaded from .csv
    fieldOfView = new double[4][2];
    // upper corners
    fieldOfView[0] = worldToImgPlane(transformUtil.imageToGokart(10, 10));
    fieldOfView[1] = worldToImgPlane(transformUtil.imageToGokart(230, 10));
    // lower corners
    fieldOfView[2] = worldToImgPlane(transformUtil.imageToGokart(230, 180));
    fieldOfView[3] = worldToImgPlane(transformUtil.imageToGokart(10, 180));
    // generate path
    trapezoid = new Path2D.Double();
    trapezoid.moveTo(fieldOfView[0][0], fieldOfView[0][1]);
    trapezoid.lineTo(fieldOfView[1][0], fieldOfView[1][1]);
    trapezoid.lineTo(fieldOfView[2][0], fieldOfView[2][1]);
    trapezoid.lineTo(fieldOfView[3][0], fieldOfView[3][1]);
    trapezoid.closePath();
    setBackground();
  }

  /** paint list of physicalBlob objects
   * 
   * @param physicalBlobs List of physicalBlob objects
   * @return BufferedImage for visualization */
  public BufferedImage overlayPhysicalBlobs(List<PhysicalBlob> physicalBlobs) {
    // if no physicalBlobs are present, return old BufferedImage
    // if (physicalBlobs.size() == 0) {
    // return bufferedImage;
    // }
    setBackground();
    for (int i = 0; i < physicalBlobs.size(); i++) {
      double[] imageCoord = worldToImgPlane(physicalBlobs.get(i).getPos());
      physicalBlobs.get(i).setImageCoord(imageCoord);
      drawPhysicalBlob(graphics, physicalBlobs.get(i), Color.WHITE, objectSize);
    }
    return bufferedImage;
  }

  /** draws rectangle at origin representing the gokart and a trapezoid representing the field of view */
  public void setBackground() {
    clearImage();
    graphics.setColor(Color.BLACK);
    // line that is 1m long
    graphics.drawLine(10, frameHeight - 10, (int) (10 + scaleFactor * 1), frameHeight - 10);
    graphics.fillRect(originPos[0] - gokartSize / 2, originPos[1] - gokartSize / 2, gokartSize, gokartSize);
    graphics.setColor(Color.RED);
    graphics.draw(trapezoid);
  }

  /** draws a PhysicalBlob onto the provided Graphics2D object
   * 
   * @param graphics
   * @param physicalBlob
   * @param color
   * @param size */
  private static void drawPhysicalBlob(Graphics2D graphics, PhysicalBlob physicalBlob, Color color, double size) {
    double leftCornerX = physicalBlob.getImageCoord()[0] - size / 2;
    double leftCornerY = physicalBlob.getImageCoord()[1] - size / 2;
    Ellipse2D ellipse = new Ellipse2D.Double(leftCornerX, leftCornerY, size, size);
    graphics.setColor(color);
    graphics.fill(ellipse);
  }

  /** resets all pixel to grey */
  private void clearImage() {
    Arrays.fill(bytes, CLEAR_BYTE);
  }

  /** transforms physical coordinates in go kart reference frame to image plane coordinates
   * 
   * @param physicalPos [m] go kart reference frame
   * @return imagePlaneCoord [pixel] image plane coordinates */
  private double[] worldToImgPlane(double[] physicalPos) {
    // unit conversion from [m] to [pixel]
    double[] physicalPosPixel = new double[] { physicalPos[0] * scaleFactor, physicalPos[1] * scaleFactor };
    // shift origin from gokart to upper left corner and transform coordinate axes: x --> -y and y --> -x
    return new double[] { //
        originPos[0] - physicalPosPixel[1], //
        originPos[1] - physicalPosPixel[0] };
  }
}
