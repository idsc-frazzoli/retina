// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import java.awt.image.BufferedImage;

public interface TimedImageListener {
  void image(int time, BufferedImage bufferedImage);
}
