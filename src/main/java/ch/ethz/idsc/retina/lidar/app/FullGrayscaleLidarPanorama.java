// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.lidar.VelodyneStatics;

/** grayscale images visualizing distance and intensity */
public class FullGrayscaleLidarPanorama implements LidarPanorama {
  private static final int RESOLUTION = VelodyneStatics.AZIMUTH_RESOLUTION;
  // ---
  private final int[] offset;
  private final BufferedImage distancesImage;
  private final byte[] distances;
  private final BufferedImage intensityImage;
  private final byte[] intensity;
  // ---
  private int rotational = -1;

  public FullGrayscaleLidarPanorama(int height) {
    offset = new int[height];
    IntStream.range(0, height).forEach(i -> offset[i] = i * RESOLUTION);
    distancesImage = new BufferedImage(RESOLUTION, height, BufferedImage.TYPE_BYTE_GRAY);
    distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
    intensityImage = new BufferedImage(RESOLUTION, height, BufferedImage.TYPE_BYTE_GRAY);
    intensity = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();
  }

  @Override // from LidarPanorama
  public void setRotational(int rotational) {
    this.rotational = rotational;
  }

  @Override // from LidarPanorama
  public void setReading(int piy, int distance, byte ivalue) {
    int address = offset[piy];
    distances[rotational + address] = (byte) (distance >> 4); // loss of least significant bits
    intensity[rotational + address] = ivalue; // confirmed for vlp16
  }

  @Override // from LidarPanorama
  public BufferedImage distances() {
    return distancesImage;
  }

  @Override // from LidarPanorama
  public BufferedImage intensity() {
    return intensityImage;
  }

  @Override
  public int getMaxWidth() {
    return RESOLUTION;
  }
}
