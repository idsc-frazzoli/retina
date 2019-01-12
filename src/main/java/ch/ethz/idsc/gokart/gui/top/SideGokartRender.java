// code by vc and jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.CopySign;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ class SideGokartRender extends AbstractGokartRender {
  private static final Scalar RAY_CUTOFF = RealScalar.of(40);
  private static final Tensor CIRCLE = CirclePoints.of(20);
  private final Tensor polygon;

  public SideGokartRender() {
    super(GokartPoseLocal.INSTANCE);
    // ---
    MinMax minMax = MinMax.of(RimoSinusIonModel.standard().footprint());
    Scalar min = minMax.min().Get(0);
    Scalar max = minMax.max().Get(0);
    Scalar groundClearance = Magnitude.METER.apply(ChassisGeometry.GLOBAL.groundClearance);
    polygon = Tensors.of( //
        Tensors.of(min, groundClearance), //
        Tensors.of(max, groundClearance), //
        Tensors.of(max, RealScalar.of(0.24)), //
        Tensors.vectorDouble(+1.19 - 0.17, 0.59), //
        Tensors.vectorDouble(+0.15, 0.59), //
        Tensors.vectorDouble(+0.15, 1.112), //
        Tensors.vectorDouble(+0.00, 1.112), //
        Tensors.vectorDouble(-0.18, 1.04), // height of box
        Tensors.vectorDouble(-0.18, 0.38), // height of box
        Tensors.of(min, RealScalar.of(0.38)) //
    );
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
      final Scalar py = Magnitude.METER.apply(SensorsConfig.GLOBAL.vlp16Height);
      Tensor translate = Se2Utils.toSE2Matrix(Tensors.of( //
          SensorsConfig.GLOBAL.vlp16.Get(0), // translation right (in pixel space)
          py, // translation up (in pixel space) to
          RealScalar.ZERO // rotation is pixel space
      ));
      geometricLayer.pushMatrix(translate);
      /** negate incline for rotation in pixel space */
      Scalar incline = SensorsConfig.GLOBAL.vlp16_incline.negate();
      graphics.setStroke(new BasicStroke(1.0f));
      graphics.setColor(new Color(0, 0, 255, 64));
      for (int i = -15; i < 16; i += 2) {
        Tensor dir = AngleVector.of(Degree.of(i).add(incline));
        Scalar dy = dir.Get(1);
        Scalar lambda = Chop._06.allZero(dy) ? RAY_CUTOFF : py.divide(dy).negate();
        Scalar factor = CopySign.of(RAY_CUTOFF, lambda);
        Tensor line = Tensors.of(dir.multiply(lambda), dir.multiply(factor.negate()));
        graphics.draw(geometricLayer.toPath2D(line));
      }
      geometricLayer.popMatrix();
    }
    { // draw lateral shape of the go-kart
      graphics.setColor(new Color(128, 128, 128, 128));
      graphics.fill(geometricLayer.toPath2D(polygon));
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
          Magnitude.METER.toDouble(ChassisGeometry.GLOBAL.xAxleRtoF), // translation right (in pixel space)
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
