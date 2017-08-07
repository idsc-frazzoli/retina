// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import ch.ethz.idsc.retina.util.gui.Hue;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** [2048 x 32] hue color images visualizing distance and intensity
 * with better contrast than {@link GrayscalePanorama}
 * 
 * size of each image is 4 * 65536 == 262144 bytes */
public class HuePanorama implements Hdl32ePanorama {
  public static final double DISTANCE_WRAP = 0.002 / 10; // wrap every 10[m]
  public static final double INTENSITY_WRAP = 0.00976563;
  // ---
  public final Tensor angle = Tensors.empty();
  // ---
  private final BufferedImage distancesImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_INT_ARGB);
  private final int[] distances = ((DataBufferInt) distancesImage.getRaster().getDataBuffer()).getData();
  // ---
  private final BufferedImage intensityImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_INT_ARGB);
  private final int[] intensity = ((DataBufferInt) intensityImage.getRaster().getDataBuffer()).getData();
  // ---

  /** @param x
   * @param y
   * @param distance 256 == 0.512[m] */
  @Override
  public void setReading(int x, int y, int distance, byte _intensity) {
    int offset = y << BIT_WIDTH;
    // int offset = y * MAX_WIDTH;
    distances[offset + x] = new Hue(distance * DISTANCE_WRAP, 1, 1, 1).color.getRGB();
    intensity[offset + x] = new Hue(_intensity * INTENSITY_WRAP + 0.5, 1, 1, 1).color.getRGB();
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
