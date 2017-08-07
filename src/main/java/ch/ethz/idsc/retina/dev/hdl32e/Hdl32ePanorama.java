// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.image.BufferedImage;

public interface Hdl32ePanorama {
  static final int BIT_WIDTH = 12;
  /** 2 ^ 12, power of two enables bitshift for computing pixel address
   * max width has to be greater than ~2150 */
  static final int MAX_WIDTH = 1 << BIT_WIDTH;

  void setReading(int x, int y, int distance, byte intensity);

  /** at motor RPM == 600 the width ~2170
   * at motor RPM == 1200 the width ~1083
   * 
   * @return */
  int getWidth();

  BufferedImage distances();

  BufferedImage intensity();
}
