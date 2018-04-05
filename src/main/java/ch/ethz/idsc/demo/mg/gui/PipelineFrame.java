// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.mg.pipeline.DavisSingleBlob;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

// provides a bufferedImage with the accumulated events and overlaid tracked objects
public class PipelineFrame {
  private static final byte CLEAR_BYTE = (byte) 128; // grey
  private BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
  private Graphics2D graphics = bufferedImage.createGraphics();
  private byte[] bytes;

  public PipelineFrame() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    clearImage();
  }
  // general todo list
  //TODO: white and black events look nice in frame, however the polarity is neglected by the tracking algorithm

  // image gets flippe due to camera orientation
  public BufferedImage getAccumulatedEvents() {
    return flipHorizontally(bufferedImage);
  }

  // overlays the active blobs on the image
  public BufferedImage trackOverlay(List<DavisSingleBlob> activeBlobs) {
    AffineTransform old = graphics.getTransform();
    for (int i = 0; i < activeBlobs.size(); i++) {
      Ellipse2D e = new Ellipse2D.Float(activeBlobs.get(i).getPos()[0], activeBlobs.get(i).getPos()[1], activeBlobs.get(i).getSemiAxes()[1],
          activeBlobs.get(i).getSemiAxes()[0]);
      graphics.rotate(activeBlobs.get(i).getRotAngle(), activeBlobs.get(i).getPos()[0] + 0.5 * activeBlobs.get(i).getSemiAxes()[1],
          activeBlobs.get(i).getPos()[1] + 0.5 * activeBlobs.get(i).getSemiAxes()[0]);
      graphics.draw(e);
      graphics.setTransform(old);
    }
    return flipHorizontally(bufferedImage);
  }

  // marks the event in the image plane as a dark or light pixel
  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    int value = davisDvsEvent.brightToDark() ? 0 : 255;
    int index = davisDvsEvent.x + davisDvsEvent.y * 240;
    bytes[index] = (byte) value;
  }

  // resets all pixel to grey
  public void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }

  // flips image along horizontal axis
  private BufferedImage flipHorizontally(BufferedImage bufferedImage) {
    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
    tx.translate(0, -bufferedImage.getHeight());
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    bufferedImage = op.filter(bufferedImage, null);
    return bufferedImage;
  }
}
