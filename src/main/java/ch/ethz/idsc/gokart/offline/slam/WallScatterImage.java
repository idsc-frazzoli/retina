// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.core.slam.LocalizationImage;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class WallScatterImage implements ScatterImage {
  public static ScatterImage of(LocalizationImage localizationImage, Color color) {
    return new WallScatterImage(localizationImage.getImage(), localizationImage.getModel2Pixel(), color);
  }

  private final Tensor model2pixel;
  private final Color color;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics2d;

  public WallScatterImage(BufferedImage background, Tensor model2pixel, Color color) {
    this.model2pixel = model2pixel;
    this.color = color;
    bufferedImage = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
    graphics2d = bufferedImage.createGraphics();
    graphics2d.drawImage(background, 0, 0, null);
  }

  @Override // from ScatterImage
  public void render(Tensor model_dot_lidar, Tensor points) {
    GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
    geometricLayer.pushMatrix(model_dot_lidar);
    graphics2d.setColor(color);
    for (Tensor x : points) {
      Point2D p = geometricLayer.toPoint2D(x);
      graphics2d.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
    }
  }

  @Override // from ScatterImage
  public BufferedImage getImage() {
    return bufferedImage;
  }
}
