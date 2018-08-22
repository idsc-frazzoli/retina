// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.core.WheelInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutListener;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

public class GokartRender extends AbstractGokartRender {
  private final VehicleModel vehicleModel;
  // ---
  private RimoGetEvent rimoGetEvent;
  public final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // ---
  private RimoPutEvent rimoPutEvent;
  public final RimoPutListener rimoPutListener = getEvent -> rimoPutEvent = getEvent;
  // ---
  private LinmotGetEvent linmotGetEvent;
  public final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  // ---
  private GokartStatusEvent gokartStatusEvent;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  // ---
  private JoystickEvent joystickEvent;
  public final JoystickListener joystickListener = getEvent -> joystickEvent = getEvent;
  // ---
  private final Tensor TIRE_FRONT;
  private final Tensor TIRE_REAR;

  public GokartRender(GokartPoseInterface gokartPoseInterface, VehicleModel vehicleModel) {
    super(gokartPoseInterface);
    this.vehicleModel = vehicleModel;
    {
      double TR = ChassisGeometry.GLOBAL.tireRadiusFront.number().doubleValue();
      double TW = ChassisGeometry.GLOBAL.tireHalfWidthFront().number().doubleValue();
      TIRE_FRONT = Tensors.matrixDouble( //
          new double[][] { { TR, TW }, { -TR, TW }, { -TR, -TW }, { TR, -TW } });
    }
    {
      double TR = ChassisGeometry.GLOBAL.tireRadiusRear.number().doubleValue();
      double TW = ChassisGeometry.GLOBAL.tireHalfWidthRear().number().doubleValue();
      TIRE_REAR = Tensors.matrixDouble( //
          new double[][] { { TR, TW }, { -TR, TW }, { -TR, -TW }, { TR, -TW } });
    }
  }

  @Override
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    {
      Color color = new Color(192, 192, 192, 64);
      if (Objects.nonNull(joystickEvent)) {
        GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystickEvent;
        if (gokartJoystickInterface.isAutonomousPressed())
          color = new Color(255, 128, 128, 64);
      }
      graphics.setColor(color);
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
      final Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair();
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.GREEN);
      Tensor rateY_draw = rateY_pair.map(Magnitude.PER_SECOND).multiply(RealScalar.of(0.1));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(2).lever(), //
          Tensors.vector(rateY_draw.Get(0).number().doubleValue(), 0)));
      graphics.draw(geometricLayer.toVector( //
          vehicleModel.wheel(3).lever(), //
          Tensors.vector(rateY_draw.Get(1).number().doubleValue(), 0)));
      graphics.setStroke(new BasicStroke(1));
    }
    if (Objects.nonNull(rimoPutEvent)) {
      double factor = 5E-4;
      double trqL = -Magnitude.ARMS.toDouble(rimoPutEvent.putTireL.getTorque()) * factor;
      double trqR = -Magnitude.ARMS.toDouble(rimoPutEvent.putTireR.getTorque()) * factor;
      graphics.setColor(Color.BLUE);
      graphics.setStroke(new BasicStroke(2));
      graphics.draw(geometricLayer.toVector(vehicleModel.wheel(2).lever(), Tensors.vector(0.0, trqL)));
      graphics.draw(geometricLayer.toVector(vehicleModel.wheel(3).lever(), Tensors.vector(0.0, trqR)));
      // graphics.drawString(Tensors.vector(trqL, trqR).toString(), 0, 100);
      graphics.setStroke(new BasicStroke(1));
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
      Tensor pair = ChassisGeometry.GLOBAL.getAckermannSteering().pair(angle);
      Scalar angleL = pair.Get(0);
      Scalar angleR = pair.Get(1);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(new Color(128, 128, 128, 128));
      Tensor angles = Tensors.of(angleL, angleR, RealScalar.ZERO, RealScalar.ZERO);
      for (int index = 0; index < 4; ++index) {
        Tensor matrix = Se2Utils.toSE2Matrix(Join.of(vehicleModel.wheel(index).lever().extract(0, 2), Tensors.of(angles.Get(index))));
        geometricLayer.pushMatrix(matrix);
        graphics.fill(geometricLayer.toPath2D(index < 2 ? TIRE_FRONT : TIRE_REAR));
        geometricLayer.popMatrix();
      }
    }
    graphics.setStroke(new BasicStroke());
  }
}
