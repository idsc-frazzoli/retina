// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractGokartRender implements RenderInterface {
  final GokartPoseInterface gokartPoseInterface;

  public AbstractGokartRender(GokartPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor state = gokartPoseInterface.getPose(); // units {x[m], y[m], angle[]}
    geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(state));
    // ---
    protected_render(geometricLayer, graphics);
    // ---
    geometricLayer.popMatrix();
  }

  /** function is invoked with geometricLayer set to location of gokart
   * 
   * @param geometricLayer
   * @param graphics */
  public abstract void protected_render(GeometricLayer geometricLayer, Graphics2D graphics);
}
