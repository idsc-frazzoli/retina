// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public class SideGokartRender extends AbstractGokartRender {
  public static final Tensor CIRCLE = CirclePoints.of(20);

  public SideGokartRender() {
    super(GokartPoseLocal.INSTANCE);
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // draw line as floor
      Tensor polygon = Tensors.of(Tensors.vector(-20, 0), Tensors.vector(+20, 0));
      Path2D path2D = geometricLayer.toPath2D(polygon);
      graphics.setStroke(new BasicStroke(2.0f));
      graphics.setColor(new Color(0, 0, 0, 128));
      graphics.draw(path2D);
      graphics.setStroke(new BasicStroke(1.0f));
    }
    { // draw the 16 lidar rays from -15deg to 15deg
      Tensor translate = Se2Utils.toSE2Matrix(Tensors.of( //
          SensorsConfig.GLOBAL.vlp16.Get(0), // translation right (in pixel space)
          Magnitude.METER.apply(SensorsConfig.GLOBAL.vlp16Height), // translation up (in pixel space) to
          /** negate incline for rotation in pixel space */
          SensorsConfig.GLOBAL.vlp16_incline.negate() // rotation is pixel space
      ));
      geometricLayer.pushMatrix(translate);
      for (int i = -15; i < 16; i += 2) {
        Tensor polygon = Tensors.of(Tensors.vector(-Math.cos(Math.toRadians(i)) * 30, -Math.sin(Math.toRadians(i)) * 30),
            Tensors.vector(Math.cos(Math.toRadians(i)) * 30, Math.sin(Math.toRadians(i)) * 30));
        Path2D path2D = geometricLayer.toPath2D(polygon);
        graphics.setStroke(new BasicStroke(1.0f));
        graphics.setColor(new Color(0, 0, 255, 128));
        graphics.draw(path2D);
      }
      geometricLayer.popMatrix();
    }
    {// draw lateral shape of the go-kart
      Scalar radius = Magnitude.METER.apply(ChassisGeometry.GLOBAL.tireRadiusRear);
      Tensor translate = Se2Utils.toSE2Matrix(Tensors.vector( //
          0, // translation right (in pixel space)
          radius.number().doubleValue(), // translation up (in pixel space)
          0 // rotation is pixel space
      ));
      geometricLayer.pushMatrix(translate);
      Point2D p1 = geometricLayer.toPoint2D(Tensors.vector(-0.25, 0.2));
      Point2D p2 = geometricLayer.toPoint2D(Tensors.vector(1.5, 0.2));
      Point2D p3 = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D p4 = geometricLayer.toPoint2D(Tensors.vector(0, 0.3));
      double w = p1.distance(p2);
      double w1 = p3.distance(p4);
      graphics.setColor(new Color(128, 128, 128, 128));
      graphics.fill(new Rectangle2D.Double(p1.getX(), p2.getY(), w, w1));
      geometricLayer.popMatrix();
    }
    { // draw rear tire
      Scalar radius = Magnitude.METER.apply(ChassisGeometry.GLOBAL.tireRadiusRear);
      Tensor translate = Se2Utils.toSE2Matrix(Tensors.vector( //
          0, // translation right (in pixel space)
          radius.number().doubleValue(), // translation up (in pixel space)
          0 // rotation is pixel space
      ));
      geometricLayer.pushMatrix(translate);
      Tensor polygon = CIRCLE.multiply(radius);
      Path2D path2D = geometricLayer.toPath2D(polygon);
      graphics.setColor(new Color(128, 128, 128, 128));
      graphics.fill(path2D);
      geometricLayer.popMatrix();
    }
    { // draw front tire
      Scalar radius = Magnitude.METER.apply(ChassisGeometry.GLOBAL.tireRadiusFront);
      Tensor translate = Se2Utils.toSE2Matrix(Tensors.vector( //
          Magnitude.METER.apply(ChassisGeometry.GLOBAL.xAxleRtoF).number().doubleValue(), // translation right (in pixel space)
          radius.number().doubleValue(), // translation up (in pixel space)
          0 // rotation is pixel space
      ));
      geometricLayer.pushMatrix(translate);
      Tensor polygon = CIRCLE.multiply(radius);
      Path2D path2D = geometricLayer.toPath2D(polygon);
      graphics.setColor(new Color(128, 128, 128, 128));
      graphics.fill(path2D);
      geometricLayer.popMatrix();
    }
  }
}
