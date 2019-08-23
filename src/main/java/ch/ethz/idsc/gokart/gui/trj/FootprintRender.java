// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class FootprintRender implements RenderInterface {
  private static final Tensor POLYGON = RimoSinusIonModel.standard().footprint();
  // ---
  private final Color color;

  public FootprintRender(Color color) {
    this.color = color;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Path2D path2d = geometricLayer.toPath2D(POLYGON, true);
    graphics.setColor(color);
    graphics.fill(path2d);
    graphics.setColor(color.darker());
    graphics.draw(path2d);
  }
}
