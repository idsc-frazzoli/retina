// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;

public class PoseTrailRender implements GokartPoseListener, RenderInterface {
  private static final Color COLOR = Color.CYAN;
  private static final int MAX_SIZE = 100;
  private static final Scalar THRESHOLD_ADD = Quantity.of(0.3, SI.METER);
  private static final Scalar THRESHOLD_CLEAR = Quantity.of(4.0, SI.METER);
  // ---
  private final BoundedLinkedList<Tensor> boundedLinkedList = new BoundedLinkedList<>(MAX_SIZE);

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    synchronized (boundedLinkedList) {
      Tensor pose = gokartPoseEvent.getPose();
      if (!boundedLinkedList.isEmpty()) {
        Tensor prev = boundedLinkedList.getLast();
        Scalar norm = Norm._2.between( //
            prev.extract(0, 2), //
            pose.extract(0, 2));
        if (Scalars.lessThan(norm, THRESHOLD_ADD))
          return;
        if (Scalars.lessThan(THRESHOLD_CLEAR, norm))
          boundedLinkedList.clear();
      }
      boundedLinkedList.add(pose);
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor tensor;
    synchronized (boundedLinkedList) {
      tensor = Tensor.of(boundedLinkedList.stream());
    }
    Path2D path2d = geometricLayer.toPath2D(tensor);
    graphics.setColor(COLOR);
    graphics.draw(path2d);
  }
}
