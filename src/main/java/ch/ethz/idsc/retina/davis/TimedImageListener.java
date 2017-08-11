// code by jph
package ch.ethz.idsc.retina.davis;

import java.awt.image.BufferedImage;

public interface TimedImageListener {
  void image(int time, BufferedImage bufferedImage);
}
