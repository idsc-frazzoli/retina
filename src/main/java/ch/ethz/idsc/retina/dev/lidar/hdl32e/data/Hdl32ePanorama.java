// code by jph
package ch.ethz.idsc.retina.dev.lidar.hdl32e.data;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Scalar;

public interface Hdl32ePanorama {
  static final int MAX_WIDTH = 2304;

  /** at motor RPM == 600 the max width ~2170
   * at motor RPM == 1200 the max width ~1083
   * 
   * @return */
  int getWidth();

  void setAngle(Scalar scalar);

  /** in the outdoors the values for distance typically range from [0, ..., ~52592]
   * 1 bit represents a 2 mm increments, i.e.
   * distance == 500 corresponds to 1[m]
   * distance == 50000 corresponds to 100[m]
   * distance == 0 -> no return within 100[m]
   * distance == 256 corresponds to 0.512[m]
   * 
   * @param x
   * @param y_abs
   * @param distance
   * @param intensity */
  void setReading(int x, int y_abs, int distance, byte intensity);

  BufferedImage distances();

  BufferedImage intensity();
}
