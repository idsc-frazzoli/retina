// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoWheelConfigurations;
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
import ch.ethz.idsc.gokart.offline.video.TireConfiguration;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
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
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;

public class GokartRender implements RenderInterface {
  private static final Tensor[] OFFSET_TORQUE = new Tensor[] { Tensors.vector(0, -0.15, 0), Tensors.vector(0, +0.15, 0) };
  private static final Tensor[] OFFSET_RATE = new Tensor[] { Tensors.vector(0, +0.15, 0), Tensors.vector(0, -0.15, 0) };
  private static final Tensor MATRIX_BRAKE = Se2Utils.toSE2Translation(Tensors.vector(1.0, 0.05));
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = //
      new AxisAlignedBox(RimoTireConfiguration._REAR.halfWidth().multiply(RealScalar.of(0.8)));
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

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseEvent.getPose()));
    { // footprint
      graphics.setColor(new Color(224, 224, 224, 192));
      graphics.fill(geometricLayer.toPath2D(VEHICLE_MODEL.footprint()));
    }
    { // rear wheel torques and rear wheel odometry
      Tensor tarms_pair = rimoPutEvent.getTorque_Y_pair().map(Magnitude.ARMS).multiply(RealScalar.of(5E-4));
      Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair();
      Tensor rateY_draw = rateY_pair.map(Magnitude.PER_SECOND).multiply(RealScalar.of(0.03));
      graphics.setStroke(new BasicStroke(1));
      AxleConfiguration axleConfiguration = RimoAxleConfiguration.rear();
      for (int wheel = 0; wheel < 2; ++wheel) {
        geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(axleConfiguration.wheel(wheel).local()));
        // ---
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(OFFSET_TORQUE[wheel]));
        graphics.setColor(Color.BLUE);
        graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongX(tarms_pair.Get(wheel))));
        geometricLayer.popMatrix();
        // ---
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(OFFSET_RATE[wheel]));
        graphics.setColor(new Color(0, 160, 0));
        graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongX(rateY_draw.Get(wheel))));
        geometricLayer.popMatrix();
        // ---
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
      graphics.setStroke(new BasicStroke(1));
      graphics.setColor(new Color(128, 128, 128, 128));
      for (WheelConfiguration wheelConfiguration : RimoWheelConfigurations.fromSCE(gokartStatusEvent.getSteerColumnEncoderCentered())) {
        geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(wheelConfiguration.local()));
        TireConfiguration tireConfiguration = wheelConfiguration.tireConfiguration();
        graphics.fill(geometricLayer.toPath2D(tireConfiguration.footprint()));
        geometricLayer.popMatrix();
      }
      // TODO JPH use of lidarLocalizationModule in display functionality is prohibited
      if (Objects.nonNull(lidarLocalizationModule)) {
        Scalar gyroZ = lidarLocalizationModule.getGyroZ(); // unit s^-1
        Scalar angularSlip = gokartAngularSlip.getAngularSlip(gokartStatusEvent, gyroZ);
        Tensor alongX = AXIS_ALIGNED_BOX.alongY(Magnitude.PER_SECOND.apply(angularSlip).negate());
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
