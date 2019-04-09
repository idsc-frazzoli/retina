// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.FrontWheelSteerMapping;
import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvents;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;

public class GokartRender implements RenderInterface {
  private static final Tensor[] OFFSET_TORQUE = new Tensor[] { Tensors.vector(0, -0.15, 0), Tensors.vector(0, +0.15, 0) };
  private static final Tensor[] OFFSET_RATE = new Tensor[] { Tensors.vector(0, +0.15, 0), Tensors.vector(0, -0.15, 0) };
  private static final Tensor MATRIX_BRAKE = Se2Utils.toSE2Translation(Tensors.vector(1.0, 0.05));
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  private final AxisAlignedBox aabRimoRate = //
      new AxisAlignedBox(ChassisGeometry.GLOBAL.tireHalfWidthRear().multiply(RealScalar.of(0.8)));
  private final AxisAlignedBox aabLinmotPos = new AxisAlignedBox(RealScalar.of(0.2));
  // ---
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  // ---
  private RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
  public final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  // ---
  private RimoPutEvent rimoPutEvent = RimoPutEvent.PASSIVE;
  public final RimoPutListener rimoPutListener = getEvent -> rimoPutEvent = getEvent;
  // ---
  private LinmotGetEvent linmotGetEvent = LinmotGetEvents.ZEROS;
  public final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  // ---
  private GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;
  public final GokartStatusListener gokartStatusListener = getEvent -> gokartStatusEvent = getEvent;
  // ---
  public final GokartAngularSlip gokartAngularSlip = new GokartAngularSlip(SteerConfig.GLOBAL.getSteerMapping());
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  // ---
  private final Tensor TIRE_FRONT;
  private final Tensor TIRE_REAR;
  // private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();

  public GokartRender() {
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

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    { // footprint
      graphics.setColor(new Color(224, 224, 224, 192));
      graphics.fill(geometricLayer.toPath2D(VEHICLE_MODEL.footprint()));
    }
    { // rear wheel odometry
      final Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair();
      graphics.setColor(new Color(0, 160, 0));
      Tensor rateY_draw = rateY_pair.map(Magnitude.PER_SECOND).multiply(RealScalar.of(0.03));
      for (int wheel = 0; wheel < 2; ++wheel) {
        Tensor matrix = Se2Utils.toSE2Translation(VEHICLE_MODEL.wheel(2 + wheel).lever().add(OFFSET_RATE[wheel]));
        geometricLayer.pushMatrix(matrix);
        graphics.fill(geometricLayer.toPath2D(aabRimoRate.alongX(rateY_draw.Get(0 + wheel))));
        geometricLayer.popMatrix();
      }
    }
    { // rear wheel torques
      double factor = 5E-4;
      double[] trq = new double[] { //
          -Magnitude.ARMS.toDouble(rimoPutEvent.putTireL.getTorque()) * factor, //
          +Magnitude.ARMS.toDouble(rimoPutEvent.putTireR.getTorque()) * factor //
      };
      graphics.setColor(Color.BLUE);
      for (int wheel = 0; wheel < 2; ++wheel) {
        Tensor matrix = Se2Utils.toSE2Translation(VEHICLE_MODEL.wheel(2 + wheel).lever().add(OFFSET_TORQUE[wheel]));
        geometricLayer.pushMatrix(matrix);
        graphics.fill(geometricLayer.toPath2D(aabRimoRate.alongX(RealScalar.of(trq[0 + wheel]))));
        geometricLayer.popMatrix();
      }
    }
    { // brake
      // TODO JPH use colors to indicate operational
      // boolean isOperational = linmotGetEvent.isOperational();
      Scalar temperatureMax = linmotGetEvent.getWindingTemperatureMax();
      Scalar rescaled = LinmotConfig.CLIP_TEMPERATURE.rescale(temperatureMax);
      Color color = ColorFormat.toColor(ColorDataGradients.ROSE.apply(rescaled));
      graphics.setColor(color);
      geometricLayer.pushMatrix(MATRIX_BRAKE);
      Scalar value = linmotGetEvent.getActualPosition().multiply(DoubleScalar.of(-12.0));
      graphics.fill(geometricLayer.toPath2D(aabLinmotPos.alongX(value)));
      geometricLayer.popMatrix();
    }
    if (gokartStatusEvent.isSteerColumnCalibrated()) {
      // Scalar angle = steerMapping.getAngleFromSCE(gokartStatusEvent); // <- calibration checked
      // Tensor pair = ChassisGeometry.GLOBAL.getAckermannSteering().pair(angle);
      Scalar angleL = FrontWheelSteerMapping._LEFT.getAngleFromSCE(gokartStatusEvent); // pair.Get(0);
      Scalar angleR = FrontWheelSteerMapping.RIGHT.getAngleFromSCE(gokartStatusEvent); // pair.Get(1);
      graphics.setStroke(new BasicStroke(2));
      graphics.setColor(new Color(128, 128, 128, 128));
      Tensor angles = Tensors.of(angleL, angleR, RealScalar.ZERO, RealScalar.ZERO);
      for (int index = 0; index < 4; ++index) {
        Tensor matrix = Se2Utils.toSE2Matrix(Join.of(VEHICLE_MODEL.wheel(index).lever().extract(0, 2), Tensors.of(angles.Get(index))));
        geometricLayer.pushMatrix(matrix);
        graphics.fill(geometricLayer.toPath2D(index < 2 ? TIRE_FRONT : TIRE_REAR));
        geometricLayer.popMatrix();
      }
      // Tensor pose = gokartPoseInterface.getPose();
      // TODO JPH use of lidarLocalizationModule in display functionality is prohibited
      if (Objects.nonNull(lidarLocalizationModule)) {
        Scalar gyroZ = lidarLocalizationModule.getGyroZ(); // unit s^-1
        Scalar angularSlip = gokartAngularSlip.getAngularSlip(gokartStatusEvent, gyroZ);
        Tensor alongX = aabRimoRate.alongY(Magnitude.PER_SECOND.apply(angularSlip).negate());
        Path2D path = geometricLayer.toPath2D(alongX);
        path.closePath();
        graphics.setColor(new Color(255, 0, 0, 128));
        graphics.fill(path);
      }
    }
    graphics.setStroke(new BasicStroke());
    geometricLayer.popMatrix();
  }
}
