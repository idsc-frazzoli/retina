// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

public class TimeFrameRender extends AbstractFrameRender {
  private final Tensor tensor;

  public TimeFrameRender(Tensor tensor) {
    this.tensor = tensor;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
    graphics.setColor(Color.LIGHT_GRAY);
    Scalar time = tensor.Get(render_index);
    graphics.drawString(String.format("time:%7s[s]", time.map(Round._3)), 0, 25);
  }
}
