// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;

public abstract class AbstractFrameRender implements RenderInterface {
  protected int render_index;

  public final void render(int render_index, GeometricLayer geometricLayer, Graphics2D graphics) {
    this.render_index = render_index;
    render(geometricLayer, graphics);
  }
}
