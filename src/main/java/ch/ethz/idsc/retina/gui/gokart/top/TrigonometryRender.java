// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2ForwardAction;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owly.car.math.TurningGeometry;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.retina.util.math.Se2AxisYProject;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Clip;

/** renders point of rotation as small dot in plane */
class TrigonometryRender extends LidarRender {
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  public TrigonometryRender(Supplier<Tensor> supplier) {
    super(supplier);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // System.out.println(geometricLayer.getMatrix());
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteerColumnCalibrated()) {
      // TODO this could easily be unit
      Scalar XAD = ChassisGeometry.GLOBAL.xAxleDistanceMeter(); // axle distance
      final Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      Optional<Scalar> optional = TurningGeometry.offset_y(XAD, angle);
      // final Scalar XAR = ChassisGeometry.GLOBAL.xAxleRearMeter();
      // double xar = XAR.number().doubleValue();
      // System.out.println(xar);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.vector(0, 0, 0)));
      if (optional.isPresent()) {
        Scalar offset_y = optional.get();
        final Tensor center = Tensors.of(RealScalar.ZERO, offset_y);
        {
          Point2D point2D = geometricLayer.toPoint2D(center);
          graphics.setColor(Color.PINK);
          graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
        }
      }
      if (Objects.nonNull(_points)) {
        Tensor points = _points;
        Scalar half = ChassisGeometry.GLOBAL.yHalfWidthMeter();
        Clip clip = Clip.function(half.negate(), half);
        Scalar max = DoubleScalar.POSITIVE_INFINITY;
        Scalar xAxleD = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
        // System.out.println(xAxleD);
        for (Tensor point : points) {
          point = point.add(Tensors.vectorDouble(xAxleD.number().doubleValue() + 0.43, 0)).unmodifiable();
          Tensor u = Tensors.of(RealScalar.ONE, RealScalar.ZERO, angle); // TODO replace by actual speed
          Scalar t = Se2AxisYProject.of(u, point).negate();
          Se2ForwardAction se2ForwardAction = new Se2ForwardAction(Se2Utils.integrate_g0(u.multiply(t)));
          Tensor v = se2ForwardAction.apply(point);
          if (clip.isInside(v.Get(1))) {
            max = Min.of(max, t.negate());
            Point2D point2D = geometricLayer.toPoint2D(point); // can also visualize v here
            graphics.setColor(Color.RED);
            graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
          }
        }
        if (Scalars.lessThan(max, DoubleScalar.POSITIVE_INFINITY)) {
          Scalar times_min = max;
          Tensor u = Tensors.of(RealScalar.ONE, RealScalar.ZERO, angle);
          Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u.multiply(times_min)));
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
          graphics.setColor(Color.BLACK);
          graphics.drawString("RTC=" + times_min, 10, 50);
        }
      }
      geometricLayer.popMatrix();
    }
  }
}
