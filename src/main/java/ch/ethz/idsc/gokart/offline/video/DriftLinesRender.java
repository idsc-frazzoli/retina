// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** draws brief history of rear axle center with orientation
 * to indicate drift in video playback */
public class DriftLinesRender implements GokartPoseListener, RenderInterface {
  private final Color color;
  private final Tensor path;
  // ---
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  public DriftLinesRender(int limit) {
    this(limit, new Color(128, 128, 128, 64), 0.4);
  }

  public DriftLinesRender(int limit, Color color, double dx) {
    boundedLinkedList = new BoundedLinkedList<>(limit);
    this.color = color;
    path = Tensors.of( //
        Tensors.vector(0.0, 0.0), //
        Tensors.vector(dx, 0.0));
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    synchronized (boundedLinkedList) {
      boundedLinkedList.add(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    synchronized (boundedLinkedList) {
      graphics.setColor(color);
      for (Tensor matrix : boundedLinkedList) {
        geometricLayer.pushMatrix(matrix);
        graphics.draw(geometricLayer.toPath2D(path));
        geometricLayer.popMatrix();
      }
    }
  }
}
