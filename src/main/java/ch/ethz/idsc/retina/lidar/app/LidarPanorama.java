// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.awt.image.BufferedImage;

public interface LidarPanorama {
  /** @param rotational */
  void setRotational(int rotational);

  /** For Velodyne:
   * in the outdoors the values for distance typically range from [0, ..., ~52592]
   * 1 bit represents a 2 mm increments, i.e. distance == 500 corresponds to 1[m]
   * distance == 50000 corresponds to 100[m] distance == 0 -> no return within
   * 100[m] distance == 256 corresponds to 0.512[m]
   * 
   * @param index from top to bottom
   * @param distance in meter
   * @param intensity */
  void setReading(int index, int distance, byte intensity);

  /** @return */
  BufferedImage distances();

  /** @return */
  BufferedImage intensity();

  int getMaxWidth();
}
