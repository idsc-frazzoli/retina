// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.gui.top.ViewLcmFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class WallScatterImage implements ScatterImage {
  private final Tensor lidar;
  private final BufferedImage sum_image;
  private final Graphics2D graphics2d;

  public WallScatterImage(BufferedImage vis_image, Tensor lidar) {
    this.lidar = lidar;
    sum_image = new BufferedImage(vis_image.getWidth(), vis_image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    graphics2d = sum_image.createGraphics();
    graphics2d.drawImage(vis_image, 0, 0, null);
  }

  @Override
  public void render(Tensor model, Tensor points) {
    GeometricLayer geometricLayer = GeometricLayer.of(ViewLcmFrame.MODEL2PIXEL_INITIAL);
    geometricLayer.pushMatrix(model);
    geometricLayer.pushMatrix(lidar);
    graphics2d.setColor(Color.WHITE);
    for (Tensor x : points) {
      Point2D p = geometricLayer.toPoint2D(x);
      graphics2d.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
    }
  }

  @Override
  public BufferedImage getImage() {
    return sum_image;
  }
}
