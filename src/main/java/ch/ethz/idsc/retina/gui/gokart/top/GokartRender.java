// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.core.WheelInterface;
import ch.ethz.idsc.owly.car.math.AckermannSteering;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusListener;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.sca.Round;

public class GokartRender extends AbstractGokartRender {
  private final VehicleModel vehicleModel;
  // ---
  private RimoGetEvent rimoGetEvent;
  public final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // ---
  private LinmotGetEvent linmotGetEvent;
  public final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  // ---
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;

  public GokartRender(GokartPoseInterface gokartPoseInterface, VehicleModel vehicleModel) {
    super(gokartPoseInterface);
    this.vehicleModel = vehicleModel;
  }

  // TODO magic const
  private static final double TR = 0.13;
  private static final double TW = 0.07;
  public static final Tensor FRONT_TIRE = Tensors.matrixDouble( //
      new double[][] { { TR, TW }, { -TR, TW }, { -TR, -TW }, { TR, -TW } });

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      graphics.setColor(new Color(192, 192, 192, 64));
      graphics.fill(geometricLayer.toPath2D(vehicleModel.footprint()));
    }
    {
      graphics.setColor(Color.RED);
      int wheels = vehicleModel.wheels();
      for (int index = 0; index < wheels; ++index) {
        WheelInterface wheelInterface = vehicleModel.wheel(index);
        Tensor pos = wheelInterface.lever();
        Point2D point2D = geometricLayer.toPoint2D(pos);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 2, 2);
      }
    }
    // rear wheels
    if (Objects.nonNull(rimoGetEvent)) {
      Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair().unmodifiable();
      graphics.setColor(Color.BLACK);
      graphics.drawString(rateY_pair.map(Round._2).toString(), 0, 40);
      // ---
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.GREEN);
      // TODO use quantity
      Tensor rateY_draw = rateY_pair.multiply(RealScalar.of(0.1));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(2).lever(), //
          Tensors.vector(rateY_draw.Get(0).number().doubleValue(), 0)));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(3).lever(), //
          Tensors.vector(rateY_draw.Get(1).number().doubleValue(), 0)));
    }
    if (Objects.nonNull(linmotGetEvent)) {
      Tensor brakePosition = Tensors.vector(1.0, 0.05);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.BLACK);
      graphics.draw(geometricLayer.toVector( //
          brakePosition, //
          Tensors.vector(linmotGetEvent.getActualPosition().number().doubleValue() * -10, 0)));
    }
    if (Objects.nonNull(gokartStatusEvent) && gokartStatusEvent.isSteerColumnCalibrated()) {
      Scalar angle = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      Tensor pair = new AckermannSteering( //
          ChassisGeometry.GLOBAL.xAxleDistanceMeter(), //
          ChassisGeometry.GLOBAL.yTireFrontMeter()).pair(angle);
      Scalar angleL = pair.Get(0);
      Scalar angleR = pair.Get(1);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(new Color(128, 128, 128, 128));
      Tensor angles = Tensors.of(angleL, angleR, RealScalar.ZERO, RealScalar.ZERO);
      for (int index = 0; index < 4; ++index) {
        Tensor matrix = Se2Utils.toSE2Matrix(Join.of(vehicleModel.wheel(index).lever().extract(0, 2), Tensors.of(angles.Get(index))));
        geometricLayer.pushMatrix(matrix);
        graphics.fill(geometricLayer.toPath2D(FRONT_TIRE));
        geometricLayer.popMatrix();
      }
    }
    graphics.setStroke(new BasicStroke());
  }
}
