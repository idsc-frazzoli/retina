// code by jph
package ch.ethz.idsc.retina.hdl32e;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import ch.ethz.idsc.retina.util.gui.Hue;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** [2304 x 32] hue color images visualizing distance and intensity
 * with better contrast than {@link GrayscalePanorama} */
public class HuePanorama implements Hdl32ePanorama {
  private static final double DISTANCE_WRAP = 0.002 / 10; // wrap every 10[m]
  private static final double INTENSITY_WRAP = 0.00976563;
  // ---
  private final Tensor angle = Tensors.empty();
  // ---
  private final BufferedImage distancesImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_INT_ARGB);
  private final int[] distances = ((DataBufferInt) distancesImage.getRaster().getDataBuffer()).getData();
  // ---
  private final BufferedImage intensityImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_INT_ARGB);
  private final int[] intensity = ((DataBufferInt) intensityImage.getRaster().getDataBuffer()).getData();
  // ---

  @Override
  public int getWidth() {
    return angle.length();
  }

  @Override
  public void setAngle(Scalar scalar) {
    angle.append(scalar);
  }

  /** @param x
   * @param y_abs
   * @param distance 256 == 0.512[m] */
  @Override
  public void setReading(int x, int y_abs, int distance, byte _intensity) {
    distances[y_abs + x] = Hue.of(distance * DISTANCE_WRAP, 1, 1, 1).getRGB();
    intensity[y_abs + x] = Hue.of(_intensity * INTENSITY_WRAP + 0.5, 1, 1, 1).getRGB();
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
