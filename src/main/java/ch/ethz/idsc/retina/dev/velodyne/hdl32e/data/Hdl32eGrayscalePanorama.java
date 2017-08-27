// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e.data;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** [2304 x 32] grayscale images visualizing distance and intensity */
public class Hdl32eGrayscalePanorama implements Hdl32ePanorama {
  private final Tensor angle = Tensors.empty();
  // ---
  private final BufferedImage distancesImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] distances = ((DataBufferByte) distancesImage.getRaster().getDataBuffer()).getData();
  // ---
  private final BufferedImage intensityImage = new BufferedImage(MAX_WIDTH, 32, BufferedImage.TYPE_BYTE_GRAY);
  private final byte[] intensity = ((DataBufferByte) intensityImage.getRaster().getDataBuffer()).getData();

  @Override
  public int getWidth() {
    return angle.length();
  }

  @Override
  public void setAngle(Scalar scalar) {
    angle.append(scalar);
  }

  @Override
  public void setReading(int x, int y_abs, int distance, byte ivalue) {
    distances[y_abs + x] = (byte) (distance >> 8); // loss of least significant bits
    intensity[y_abs + x] = ivalue;
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
