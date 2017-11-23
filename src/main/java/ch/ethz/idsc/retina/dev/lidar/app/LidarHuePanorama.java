// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.Hue;

/** [2304 x 32] hue color images visualizing distance and intensity with better
 * contrast than {@link LidarGrayscalePanorama} */
public class LidarHuePanorama implements LidarPanorama {
  private static final double DISTANCE_WRAP = 0.1; // wrap every 10[m]
  private static final double INTENSITY_WRAP = 0.00976563;
  // ---
  private final Tensor angle = Tensors.empty();
  // ---
  private final BufferedImage distancesImage;
  private final int[] distances;
  // ---
  private final BufferedImage intensityImage;
  private final int[] intensity;

  // ---
  public LidarHuePanorama(int max_width, int height) {
    distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_INT_ARGB);
    distances = ((DataBufferInt) distancesImage.getRaster().getDataBuffer()).getData();
    intensityImage = new BufferedImage(max_width, height, BufferedImage.TYPE_INT_ARGB);
    intensity = ((DataBufferInt) intensityImage.getRaster().getDataBuffer()).getData();
  }

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
  public void setReading(int address, float distance, byte _intensity) {
    // TODO JAN not efficient, use lookup table!
    distances[address] = Hue.of(distance * DISTANCE_WRAP, 1, 1, 1).getRGB();
    intensity[address] = Hue.of(_intensity * INTENSITY_WRAP + 0.5, 1, 1, 1).getRGB();
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
