// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** draws brief history of rear axle center with orientation
 * to indicate drift in video playback */
/* package */ class DriftLinesRender implements GokartPoseListener, RenderInterface {
  private static final Color COLOR = new Color(128, 128, 128, 64);
  private static final Tensor PATH = Tensors.of( //
      Tensors.vector(0.0, 0), //
      Tensors.vector(0.4, 0));
  // ---
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  public DriftLinesRender(int limit) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    synchronized (boundedLinkedList) {
      graphics.setColor(COLOR);
      for (Tensor matrix : boundedLinkedList) {
        geometricLayer.pushMatrix(matrix);
        graphics.draw(geometricLayer.toPath2D(PATH));
        geometricLayer.popMatrix();
      }
    }
  }
}
