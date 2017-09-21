// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Scalar;

public interface LidarPanorama {
  int getWidth();

  // TODO is this function really needed?
  void setAngle(Scalar scalar); // probably should work with basic type

  /** in the outdoors the values for distance typically range from [0, ..., ~52592]
   * 1 bit represents a 2 mm increments, i.e. distance == 500 corresponds to 1[m]
   * distance == 50000 corresponds to 100[m] distance == 0 -> no return within
   * 100[m] distance == 256 corresponds to 0.512[m]
   * 
   * @param address in image space, typically address == x + y * width
   * @param distance in meter
   * @param intensity */
  void setReading(int address, float distance, byte intensity);

  BufferedImage distances();

  BufferedImage intensity();
}
