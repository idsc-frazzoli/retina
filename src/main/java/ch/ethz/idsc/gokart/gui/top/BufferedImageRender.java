// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public class BufferedImageRender implements RenderInterface {
  private final BufferedImage bufferedImage;

  public BufferedImageRender(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, 0, 0, null);
  }
}
