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

import ch.ethz.idsc.demo.mg.pipeline.DavisSingleBlob;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

// provides a bufferedImage with the accumulated events and overlaid tracked objects
public class PipelineFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey
  private static final byte[] VALUE = { 0, (byte) 255 };
  // ---
  private final BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_INDEXED);
  private final Graphics2D graphics = bufferedImage.createGraphics();
  private final byte[] bytes;

  public PipelineFrame() {
    DataBufferByte dataBufferByte = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
    bytes = dataBufferByte.getData();
    clearImage();
  }
  // general todo list

  public BufferedImage getAccumulatedEvents() {
    return flipHorizontally(bufferedImage);
  }

  // overlays the active blobs on the image
  public BufferedImage trackOverlay(List<DavisSingleBlob> blobs, Color color) {
    AffineTransform old = graphics.getTransform();
    for (int i = 0; i < blobs.size(); i++) {
      rotatedEllipse(graphics, blobs.get(i), color);
      graphics.setTransform(old);
    }
    return flipHorizontally(bufferedImage);
  }

  // marks the event in the image plane as a dark or light pixel
  public void receiveEvent(DavisDvsEvent davisDvsEvent) {
    // int value = davisDvsEvent.brightToDark() ? 0 : 255;
    int index = davisDvsEvent.x + davisDvsEvent.y * 240;
    bytes[index] = VALUE[davisDvsEvent.i]; // (byte) value;
  }

  // resets all pixel to grey
  public void clearImage() {
    IntStream.range(0, bytes.length).forEach(i -> bytes[i] = CLEAR_BYTE);
  }

  // flips image along horizontal axis
  private static BufferedImage flipHorizontally(BufferedImage bufferedImage) {
    AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
    tx.translate(0, -bufferedImage.getHeight());
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    bufferedImage = op.filter(bufferedImage, null);
    return bufferedImage;
  }

  // use to Tensor library to correctly draw the ellipses
  private static void rotatedEllipse(Graphics2D graphics, DavisSingleBlob blob, Color color) {
    Tensor matrix = Tensors.matrixDouble(blob.getCovariance());
    // find eigenvector belonging to first eigenvalue
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor firstEigVec = eigensystem.vectors().get(0);
    // find rotation angle of that eigenvector
    // TODO mario, Math.atan2(y, x) vs. ArcTan.of(x, y)
    double rotAngle = Math.atan2(firstEigVec.Get(1).number().doubleValue(), firstEigVec.Get(0).number().doubleValue());
    Tensor semiAxes = Sqrt.of(eigensystem.values());
    float leftCornerX = blob.getPos()[0] - semiAxes.Get(0).number().floatValue();
    float leftCornerY = blob.getPos()[1] - semiAxes.Get(1).number().floatValue();
    // draw ellipse with first eigenvalue aligned with x axis
    Ellipse2D ellipse = new Ellipse2D.Float(leftCornerX, leftCornerY, //
        2 * semiAxes.Get(0).number().floatValue(), //
        2 * semiAxes.Get(1).number().floatValue());
    // rotate around blob mean by rotAngle which is the angle between first eigenvector and x axis
    graphics.rotate(rotAngle, blob.getPos()[0], blob.getPos()[1]);
    graphics.setColor(color);
    graphics.draw(ellipse);
  }
}
