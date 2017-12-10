// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** grayscale images visualizing distance and intensity */
public class LidarGrayscalePanorama implements LidarPanorama {
  private static final double DISTANCE_WRAP = 25.6; // wrap every 10[m] and multiply by 256
  // ---
  private final Tensor angle = Tensors.empty();
  // ---
  private final BufferedImage distancesImage;
  private final byte[] distances;
  // ---
  private final BufferedImage intensityImage;
  private final byte[] intensity;

  public LidarGrayscalePanorama(int max_width, int height) {
    distancesImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
    intensityImage = new BufferedImage(max_width, height, BufferedImage.TYPE_BYTE_GRAY);
    intensity = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();
  }

  @Override
  public int getWidth() {
    return angle.length();
  }

  @Override
  public void setAngle(Scalar scalar) {
    angle.append(scalar);
  }

  @Override
  public void setReading(int address, float distance, byte ivalue) {
    distances[address] = (byte) (distance * DISTANCE_WRAP); // loss of least significant bits
    intensity[address] = ivalue;
    // intensity[address] = (byte) (255-ivalue); // TODO check
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
