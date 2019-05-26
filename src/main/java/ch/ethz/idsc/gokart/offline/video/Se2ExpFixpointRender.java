// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.car.math.Se2ExpFixpoint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;

public class Se2ExpFixpointRender implements RenderInterface, GokartPoseListener {
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();

  @Override
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Optional<Tensor> optional = Se2ExpFixpoint.of(gokartPoseEvent.getVelocity());
    if (optional.isPresent()) {
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      graphics.setColor(new Color(255, 0, 0, 192));
      Point2D point2d = geometricLayer.toPoint2D(optional.get());
      graphics.fill(new Ellipse2D.Double(point2d.getX() - 5, point2d.getY() - 5, 11, 11));
      geometricLayer.popMatrix();
    }
  }
}
