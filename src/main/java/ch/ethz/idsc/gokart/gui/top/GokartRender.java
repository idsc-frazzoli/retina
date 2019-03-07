// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.core.WheelInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.joystick.JoystickEvent;
import ch.ethz.idsc.retina.joystick.JoystickListener;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

// TODO breakup class in separate 
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
  public final GokartAngularSlip gokartAngularSlip = new GokartAngularSlip(SteerConfig.GLOBAL.getSteerMapping());
  // ---
  private final Tensor TIRE_FRONT;
  private final Tensor TIRE_REAR;
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();

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
        ManualControlInterface manualControlInterface = (ManualControlInterface) joystickEvent;
        if (manualControlInterface.isAutonomousPressed())
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
    final AxisAlignedBox axisAlignedBox = //
        new AxisAlignedBox(ChassisGeometry.GLOBAL.tireHalfWidthRear().multiply(RealScalar.of(.8)));
    if (Objects.nonNull(rimoGetEvent)) {
      final Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair();
      graphics.setColor(Color.GREEN);
      Tensor rateY_draw = rateY_pair.map(Magnitude.PER_SECOND).multiply(RealScalar.of(0.03));
      Tensor[] ofs = new Tensor[] { Tensors.vector(0, +.13, 0), Tensors.vector(0, -.13, 0) };
      for (int wheel = 0; wheel < 2; ++wheel) {
        Tensor matrix = Se2Utils.toSE2Translation(vehicleModel.wheel(2 + wheel).lever().add(ofs[wheel]));
        geometricLayer.pushMatrix(matrix);
        Path2D path = geometricLayer.toPath2D(axisAlignedBox.alongY(rateY_draw.Get(0 + wheel)));
        path.closePath();
        graphics.fill(path);
        geometricLayer.popMatrix();
      }
    }
    if (Objects.nonNull(rimoPutEvent)) {
      double factor = 5E-4;
      double[] trq = new double[] { //
          -Magnitude.ARMS.toDouble(rimoPutEvent.putTireL.getTorque()) * factor, //
          +Magnitude.ARMS.toDouble(rimoPutEvent.putTireR.getTorque()) * factor //
      };
      Tensor[] ofs = new Tensor[] { Tensors.vector(0, -.13 * 2, 0), Tensors.vector(0, +.13 * 2, 0) };
      graphics.setColor(Color.BLUE);
      for (int wheel = 0; wheel < 2; ++wheel) {
        Tensor vector = vehicleModel.wheel(2 + wheel).lever();
        Tensor matrix = Se2Utils.toSE2Translation(vector.add(ofs[wheel]));
        geometricLayer.pushMatrix(matrix);
        Path2D path = geometricLayer.toPath2D(axisAlignedBox.alongY(RealScalar.of(trq[0 + wheel])));
        path.closePath();
        graphics.fill(path);
        geometricLayer.popMatrix();
      }
    }
    if (Objects.nonNull(linmotGetEvent)) {
      Tensor brakePosition = Tensors.vector(1.0, 0.05);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(Color.BLACK);
      graphics.draw(geometricLayer.toVector( //
          brakePosition, //
          Tensors.vector(linmotGetEvent.getActualPosition().number().doubleValue() * -10, 0)));
    }
    if (Objects.nonNull(gokartStatusEvent))
      if (gokartStatusEvent.isSteerColumnCalibrated()) {
        Scalar angle = steerMapping.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
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
        {
          Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ(); // unit s^-1
          Scalar angularSlip = gokartAngularSlip.getAngularSlip(gokartStatusEvent, gyroZ);
          Tensor alongX = axisAlignedBox.alongX(Magnitude.PER_SECOND.apply(angularSlip).negate());
          Path2D path = geometricLayer.toPath2D(alongX);
          path.closePath();
          graphics.setColor(new Color(255, 0, 0, 128));
          graphics.fill(path);
        }
      }
    graphics.setStroke(new BasicStroke());
  }
}
