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
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.car.math.Se2ExpFixpoint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Norm;

public class Se2ExpFixpointRender implements GokartPoseListener, RenderInterface {
  private static final Tensor CIRCLE = CirclePoints.of(101).unmodifiable();
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  private boolean drawCircles = false;

  @Override
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Optional<Tensor> optional = Se2ExpFixpoint.of(gokartPoseEvent.getVelocity());
    if (optional.isPresent()) {
      Tensor fixpoint = optional.get().map(Magnitude.METER);
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(fixpoint));
      {
        graphics.setColor(new Color(255, 0, 0, 192));
        Point2D point2d = geometricLayer.toPoint2D(0, 0);
        graphics.fill(new Ellipse2D.Double(point2d.getX() - 5, point2d.getY() - 5, 11, 11));
      }
      if (drawCircles) {
        graphics.setColor(new Color(255, 0, 0, 192));
        graphics.draw(geometricLayer.toPath2D(CIRCLE.multiply(Norm._2.ofVector(fixpoint))));
        // ---
        graphics.setColor(new Color(0, 255, 0, 192));
        Scalar radius2 = Norm._2.between(fixpoint, //
            Tensors.of(ChassisGeometry.GLOBAL.xAxleRtoCoM.map(Magnitude.METER), RealScalar.ZERO));
        graphics.draw(geometricLayer.toPath2D(CIRCLE.multiply(radius2)));
      }
      geometricLayer.popMatrix();
      geometricLayer.popMatrix();
    }
  }
}
