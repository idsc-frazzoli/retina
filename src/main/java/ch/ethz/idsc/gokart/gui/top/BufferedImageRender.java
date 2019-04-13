// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

/** draws image in upper left corner (0, 0) of component */
/* package */ class BufferedImageRender implements RenderInterface {
  private final BufferedImage bufferedImage;

  public BufferedImageRender(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(bufferedImage, 0, 0, null);
  }
}
