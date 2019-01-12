// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.math.CircleClearanceTracker;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** renders point of rotation as small dot in plane */
// TODO class could be improved a lot: filter points in listener
class Vlp16ClearanceRender extends LidarRender {
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  private final SpacialXZObstaclePredicate predicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();

  public Vlp16ClearanceRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final GokartStatusEvent gokartStatusEvent = this.gokartStatusEvent;
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteerColumnCalibrated()) {
      // final Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      if (Objects.nonNull(_points)) {
        Tensor points = Tensor.of(_points.stream().filter(predicate::isObstacle)); // in reference frame of lidar
        // ---
        Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
        CircleClearanceTracker[] circleClearanceCollectors = new CircleClearanceTracker[] { //
            (CircleClearanceTracker) SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(+1), gokartStatusEvent), //
            (CircleClearanceTracker) SafetyConfig.GLOBAL.getClearanceTracker(DoubleScalar.of(-1), gokartStatusEvent) //
        };
        Color[] colors = new Color[] { Color.RED, Color.ORANGE };
        for (int count = 0; count < colors.length; ++count) {
          CircleClearanceTracker circleClearanceCollector = circleClearanceCollectors[count];
          graphics.setColor(colors[count]);
          points.stream().filter(circleClearanceCollector::isObstructed).forEach(point -> {
            Point2D point2D = geometricLayer.toPoint2D(point); // can also visualize v here
            graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
          });
          Optional<Tensor> optional = circleClearanceCollector.violation();
          if (optional.isPresent()) {
            Tensor m = Se2Utils.toSE2Matrix(optional.get());
            geometricLayer.pushMatrix(m);
            graphics.setStroke(new BasicStroke(3));
            {
              Path2D path2D = geometricLayer.toPath2D(Tensors.of( //
                  Tensors.of(RealScalar.ZERO, half.negate()), //
                  Tensors.of(RealScalar.ZERO, half)));
              graphics.draw(path2D);
            }
            graphics.setStroke(new BasicStroke(1));
            geometricLayer.popMatrix();
          }
        }
      }
    }
  }
}
