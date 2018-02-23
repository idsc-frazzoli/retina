// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.ClearanceTracker;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.math.TurningGeometry;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** renders point of rotation as small dot in plane */
class TrigonometryRender extends LidarRender {
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  public TrigonometryRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteerColumnCalibrated()) {
      final Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      { // draw point of rotation when assuming no slip
        // TODO this could easily be unit
        Scalar XAD = ChassisGeometry.GLOBAL.xAxleDistanceMeter(); // axle distance
        Optional<Scalar> optional = TurningGeometry.offset_y(XAD, angle);
        if (optional.isPresent()) {
          Tensor center = Tensors.of(RealScalar.ZERO, optional.get());
          Point2D point2D = geometricLayer.toPoint2D(center);
          graphics.setColor(Color.PINK);
          graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
        }
      }
      if (Objects.nonNull(_points)) {
        Tensor points = _points; // TODO document which reference frame these are in!!!!
        // ---
        Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
        ClearanceTracker clearanceTracker = new ClearanceTracker(half, angle, SensorsConfig.GLOBAL.urg04lx);
        // ---
        for (Tensor local : points)
          clearanceTracker.feed(local); // TODO stream
        for (Tensor point : clearanceTracker.getPointsInViolation()) {
          Point2D point2D = geometricLayer.toPoint2D(point); // can also visualize v here
          graphics.setColor(Color.RED);
          graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
        }
        Optional<Tensor> optional = clearanceTracker.violation();
        if (optional.isPresent()) {
          Tensor m = Se2Utils.toSE2Matrix(optional.get());
          geometricLayer.pushMatrix(m);
          graphics.setStroke(new BasicStroke(3));
          {
            Path2D path2D = geometricLayer.toPath2D(Tensors.of( //
                Tensors.of(RealScalar.ZERO, half.negate()), //
                Tensors.of(RealScalar.ZERO, half)));
            graphics.setColor(Color.RED);
            graphics.draw(path2D);
          }
          graphics.setStroke(new BasicStroke(1));
          geometricLayer.popMatrix();
        }
      }
    }
  }
}
