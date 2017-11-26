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

import ch.ethz.idsc.owl.bot.se2.Se2AxisYProject;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owly.car.math.TurningGeometry;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
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
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteeringCalibrated()) {
      // TODO this could easily be unit
      Scalar XAD = ChassisGeometry.GLOBAL.xAxleDistanceMeter(); // axle distance
      final Scalar angle = gokartStatusEvent.getSteeringAngle();
      Optional<Scalar> optional = TurningGeometry.offset_y(XAD, angle);
      final Scalar XAR = ChassisGeometry.GLOBAL.xAxleRearMeter();
      double xar = XAR.number().doubleValue();
      // System.out.println(xar);
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(Tensors.vector(xar, 0, 0)));
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
        Tensor times = Tensors.empty();
        Scalar xAxleD = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
        // System.out.println(xAxleD);
        for (Tensor point : points) {
          point = point.add(Tensors.vectorDouble(xAxleD.number().doubleValue() + 0.43, 0)).unmodifiable();
          Tensor u = Tensors.of(RealScalar.ONE, RealScalar.ZERO, angle);
          Scalar t = Se2AxisYProject.of(u, point).negate();
          Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u.multiply(t))); // TODO make more efficient
          Tensor v = m.dot(point.copy().append(RealScalar.ONE));
          if (clip.isInside(v.Get(1))) {
            times.append(t.negate());
            // {
            // Point2D point2D = geometricLayer.toPoint2D(v);
            // graphics.setColor(Color.CYAN);
            // graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
            // }
            {
              Point2D point2D = geometricLayer.toPoint2D(point);
              graphics.setColor(Color.RED);
              graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
            }
          }
          // Tensor dir = point.subtract(center);
          // Scalar angle = ArcTan.of(dir.Get(0), dir.Get(1).abs()); // TODO check
          // angles.append(angle);
          // Scalar norm = Norm._2.ofVector(dir);
          // if (clip.isInside(norm)) {
          // Point2D point2D = geometricLayer.toPoint2D(point);
          // graphics.setColor(Color.PINK);
          // graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
          // }
          // }
        }
        Optional<Scalar> op = times.stream().map(Scalar.class::cast).reduce(Min::of);
        if (op.isPresent()) {
          Scalar times_min = op.get().Get();
          Tensor u = Tensors.of(RealScalar.ONE, RealScalar.ZERO, angle);
          Tensor m = Se2Utils.toSE2Matrix(Se2Utils.integrate_g0(u.multiply(times_min)));
          geometricLayer.pushMatrix(m);
          graphics.setStroke(new BasicStroke(3));
          {
            Path2D path2D = geometricLayer.toPath2D(Tensors.of( //
                Tensors.of(RealScalar.ZERO, half.negate()), //
                Tensors.of(RealScalar.ZERO, half)));
            // geometricLayer.toPoint2D(m.get(Tensor.ALL, 2));
            graphics.setColor(Color.RED);
            graphics.draw(path2D);
          }
          graphics.setStroke(new BasicStroke(1));
          geometricLayer.popMatrix();
          graphics.setColor(Color.BLACK);
          graphics.drawString("JANS " + times_min, 10, 50);
        }
      }
      geometricLayer.popMatrix();
    }
  }
}
