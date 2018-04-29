// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/** grayscale images visualizing distance and intensity */
public class GrayscaleLidarPanorama implements LidarPanorama {
  private static final double DISTANCE_WRAP = 25.6; // wrap every 10[m] and multiply by 256
  // ---
  private final int max_width;
  private final BufferedImage distancesImage;
  private final byte[] distances;
  private final BufferedImage intensityImage;
  private final byte[] intensity;
  // ---
  private int width = -1;

  public GrayscaleLidarPanorama(int max_width, int height) {
    this.max_width = max_width;
    distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
    intensityImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    intensity = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();
  }

  @Override // from LidarPanorama
  public void setRotational(int rotational) {
    ++width;
    width = Math.min(width, max_width - 1);
  }

  @Override // from LidarPanorama
  public void setReading(int address, float distance, byte ivalue) {
    distances[width + address] = (byte) (distance * DISTANCE_WRAP); // loss of least significant bits
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
}
