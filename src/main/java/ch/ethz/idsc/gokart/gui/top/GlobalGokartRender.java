// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;

public class GlobalGokartRender extends GokartRender {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor pose = gokartPoseEvent.getPose();
    geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
    protected_render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
