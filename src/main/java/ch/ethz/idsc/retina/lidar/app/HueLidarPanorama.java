// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.tensor.img.Hue;

/** [2304 x 32] hue color images visualizing distance and intensity with better
 * contrast than {@link GrayscaleLidarPanorama} */
public class HueLidarPanorama implements LidarPanorama {
  private static final double DISTANCE_WRAP = VelodyneStatics.TO_METER * 0.1; // wrap every 10[m]
  private static final double INTENSITY_WRAP = 0.00976563;
  // ---
  private final int cutoff;
  private final int[] offset;
  private final BufferedImage distancesImage;
  private final int[] distances;
  private final BufferedImage intensityImage;
  private final int[] intensity;
  // ---
  private int width = -1;

  public HueLidarPanorama(int max_width, int height) {
    cutoff = max_width - 1;
    offset = new int[height];
    IntStream.range(0, height).forEach(i -> offset[i] = i * max_width);
    distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_INT_ARGB);
    distances = ((DataBufferInt) distancesImage.getRaster().getDataBuffer()).getData();
    intensityImage = new BufferedImage(max_width, height, BufferedImage.TYPE_INT_ARGB);
    intensity = ((DataBufferInt) intensityImage.getRaster().getDataBuffer()).getData();
  }

  @Override // from LidarPanorama
  public void setRotational(int rotational) {
    ++width;
    width = Math.min(width, cutoff);
  }

  /** @param width
   * @param y_abs
   * @param distance 256 == 0.512[m] */
  @Override // from LidarPanorama
  public void setReading(int piy, int distance, byte _intensity) {
    int address = offset[piy];
    // Hue is sufficiently fast, but could use lookup table
    distances[width + address] = Hue.of(distance * DISTANCE_WRAP, 1, 1, 1).getRGB();
    intensity[width + address] = Hue.of(_intensity * INTENSITY_WRAP + 0.5, 1, 1, 1).getRGB();
  }

  @Override // from LidarPanorama
  public BufferedImage distances() {
    return distancesImage.getSubimage(0, 0, width, distancesImage.getHeight());
  }

  @Override // from LidarPanorama
  public BufferedImage intensity() {
    return intensityImage.getSubimage(0, 0, width, distancesImage.getHeight());
  }

  @Override
  public int getMaxWidth() {
    return distancesImage.getWidth();
  }
}
