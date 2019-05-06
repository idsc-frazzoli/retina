// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;

public class GlobalGokartRender extends GokartRender {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    protected_render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
