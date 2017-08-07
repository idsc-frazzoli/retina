// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** [2048 x 32] grayscale images visualizing distance and intensity
 * 
 * size of each image is 65536 bytes */
public class GrayscalePanorama implements Hdl32ePanorama {
  /** 2 ^ 11, power of two enables bitshift for computing pixel address */
  public static final int MAX_WIDTH = 2048; // TODO prove that sufficient for slow rotation rate +- tolerance...
  // ---
  public final Tensor angle = Tensors.empty();
  // ---
  private final BufferedImage distancesImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
  // ---
  private final BufferedImage intensityImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] intensity = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();

  @Override
  public void setReading(int x, int y, int value, byte ivalue) {
    int offset = y << 11;
    distances[offset + x] = (byte) (value >> 8);
    intensity[offset + x] = ivalue;
  }

  @Override
  public int getWidth() {
    return angle.length();
  }

  @Override
  public BufferedImage distances() {
    return distancesImage;
  }

  @Override
  public BufferedImage intensity() {
    return intensityImage;
  }
}
