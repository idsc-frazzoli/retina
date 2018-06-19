// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class CurveRender implements RenderInterface {
  private final Tensor curve;

  public CurveRender(Tensor curve) {
    this.curve = curve;
  }

  @Override // from AbstractGokartRender
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.YELLOW);
    Path2D path2d = geometricLayer.toPath2D(curve);
    path2d.closePath();
    graphics.draw(path2d);
    // graphics.setColor(new Color(255, 0, 0, 128));
    // for (Tensor pnt : curve) {
    // Point2D point2D = geometricLayer.toPoint2D(pnt);
    // graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 2, 2);
    // }
  }
}
