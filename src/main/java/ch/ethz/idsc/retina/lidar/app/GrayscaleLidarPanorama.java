// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.stream.IntStream;

/** grayscale images visualizing distance and intensity */
public class GrayscaleLidarPanorama implements LidarPanorama {
  private final int cutoff;
  private final int[] offset;
  private final BufferedImage distancesImage;
  private final byte[] distances;
  private final BufferedImage intensityImage;
  private final byte[] intensity;
  // ---
  private int width = -1;

  public GrayscaleLidarPanorama(int max_width, int height) {
    cutoff = max_width - 1;
    offset = new int[height];
    IntStream.range(0, height).forEach(i -> offset[i] = i * max_width);
    distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
    intensityImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    intensity = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();
  }

  @Override // from LidarPanorama
  public void setRotational(int rotational) {
    ++width;
    width = Math.min(width, cutoff);
  }

  @Override // from LidarPanorama
  public void setReading(int piy, int distance, byte ivalue) {
    int address = offset[piy];
    distances[width + address] = (byte) (distance >> 5); // loss of least significant bits
    intensity[width + address] = ivalue; // confirmed for vlp16
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
