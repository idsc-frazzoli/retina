// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.gokart.core.pure.PurePursuitModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

// TODO this is not the final API:
// the points should be resampled after each scan and not before each draw!
/* package */ class CurveRender implements RenderInterface {
  @Override // from AbstractGokartRender
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor curve = PurePursuitModule.CURVE;
    graphics.setColor(Color.YELLOW);
    graphics.draw(geometricLayer.toPath2D(curve));
    graphics.setColor(new Color(255, 0, 0, 128));
    for (Tensor pnt : curve) {
      Point2D point2D = geometricLayer.toPoint2D(pnt);
      graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 2, 2);
    }
  }
}
