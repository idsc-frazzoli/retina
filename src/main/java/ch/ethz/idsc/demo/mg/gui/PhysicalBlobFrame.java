// code by mg
package ch.ethz.idsc.demo.mg.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.stream.IntStream;

// provides a buffered image to visualize the physialBlobs.
// TODO copy appropriate code from AccumulatedEventFrame, e.g. use grey background with overlaid PhysicalBlobs from EstimationAlgorithm.
public class PhysicalBlobFrame {
  private static final byte CLEAR_BYTE = (byte) 240; // grey (TYPE_BYTE_INDEXED)
  private static final byte[] VALUE = { 0, (byte) 255 };
  // ---
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
}
