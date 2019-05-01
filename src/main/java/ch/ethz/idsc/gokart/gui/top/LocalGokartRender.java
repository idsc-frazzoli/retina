// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;

public class LocalGokartRender extends GokartRender {
  private final Tensor xya;

  public LocalGokartRender(Tensor xya) {
    this.xya = xya;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(xya));
    protected_render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
