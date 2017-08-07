// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.image.BufferedImage;

public interface Hdl32ePanorama {
  void setReading(int x, int y, int distance, byte intensity);

  int getWidth();

  BufferedImage distances();

  BufferedImage intensity();
}
