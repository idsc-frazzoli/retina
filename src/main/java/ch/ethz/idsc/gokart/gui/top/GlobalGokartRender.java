// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public class GlobalGokartRender extends GokartRender {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    protected_render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
