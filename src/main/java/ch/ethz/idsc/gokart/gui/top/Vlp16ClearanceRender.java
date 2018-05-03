// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.CircleClearanceTracker;
import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** renders point of rotation as small dot in plane */
class Vlp16ClearanceRender extends LidarRender {
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate //
      = SimpleSpacialObstaclePredicate.createVlp16();

  public Vlp16ClearanceRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteerColumnCalibrated()) {
      final Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      if (Objects.nonNull(_points)) {
        Tensor points = _points; // in reference frame of lidar
        // ---
        Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
        CircleClearanceTracker clearanceTracker = new CircleClearanceTracker(half, angle, SensorsConfig.GLOBAL.vlp16);
        points.stream() //
            .filter(spacialXZObstaclePredicate::isObstacle) //
            .forEach(clearanceTracker::feed);
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
