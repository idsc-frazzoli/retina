// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

public class GlobalGokartRender extends GokartRender {
  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
    graphics.setColor(Color.WHITE);
    Tensor pose = gokartPoseEvent.getPose();
    graphics.drawString("" + pose.map(Round._3), 0, 24);
    geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
    protected_render(geometricLayer, graphics);
    geometricLayer.popMatrix();
  }
}
