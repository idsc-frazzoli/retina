// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.Refactor;
import ch.ethz.idsc.tensor.Tensor;

@Refactor // TODO JPH make class obsolete. constructor is ugly
public abstract class AbstractGokartRender implements RenderInterface, GokartPoseListener {
  protected GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor state = gokartPoseEvent.getPose(); // units {x[m], y[m], angle[]}
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

  @Override
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
