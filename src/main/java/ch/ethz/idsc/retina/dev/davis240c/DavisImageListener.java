// code by jph
package ch.ethz.idsc.retina.dev.davis240c;

import java.awt.image.BufferedImage;

public interface DavisImageListener {
  void image(int time, BufferedImage bufferedImage);
}
