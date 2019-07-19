// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoWheelConfigurations;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvents;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvents;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;

public abstract class GokartRender implements RenderInterface {
  private static final Tensor[] OFFSET_TORQUE = new Tensor[] { Tensors.vector(0, -0.15, 0), Tensors.vector(0, +0.15, 0) };
  private static final Tensor[] OFFSET_RATE = new Tensor[] { Tensors.vector(0, +0.15, 0), Tensors.vector(0, -0.15, 0) };
  private static final Tensor MATRIX_BRAKE = Se2Utils.toSE2Translation(Tensors.vector(1.0, 0.05));
  private static final Color COLOR_WHEEL = new Color(128, 128, 128, 128);
  private static final Color COLOR_SLIP = new Color(255, 128, 64, 128 + 64);
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  public static final Scalar SLIP_FACTOR = RealScalar.of(0.20);
  // ---
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = //
      new AxisAlignedBox(RimoTireConfiguration._REAR.halfWidth().multiply(RealScalar.of(0.8)));
  /** linmot push indicator */
  private static final AxisAlignedBox AXIS_ALIGNED_LMT = new AxisAlignedBox(RealScalar.of(0.2));
  // ---
  /** gokart pose event is also used in rendering */
  protected GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
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
  private SteerColumnEvent steerColumnEvent = SteerColumnEvents.UNKNOWN;
  public final SteerColumnListener steerColumnListener = getEvent -> steerColumnEvent = getEvent;

  public final void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    { // footprint
      graphics.setColor(new Color(224, 224, 224, 192));
      graphics.fill(geometricLayer.toPath2D(VEHICLE_MODEL.footprint()));
    }
    { // rear wheel torques and rear wheel odometry
      Tensor tarms_pair = rimoPutEvent.getTorque_Y_pair().map(Magnitude.ARMS).multiply(RealScalar.of(5E-4));
      Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair();
      Tensor rateY_draw = rateY_pair.map(Magnitude.PER_SECOND).multiply(RealScalar.of(0.03));
      graphics.setStroke(new BasicStroke());
      AxleConfiguration axleConfiguration = RimoAxleConfiguration.rear();
      for (int wheel = 0; wheel < 2; ++wheel) {
        geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(axleConfiguration.wheel(wheel).local()));
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
      graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_LMT.alongX(value)));
      geometricLayer.popMatrix();
    }
    if (steerColumnEvent.isSteerColumnCalibrated()) {
      graphics.setStroke(new BasicStroke());
      // draw wheels
      for (WheelConfiguration wheelConfiguration : RimoWheelConfigurations.fromSCE(steerColumnEvent.getSteerColumnEncoderCentered())) {
        geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(wheelConfiguration.local()));
        // draw tire
        graphics.setColor(COLOR_WHEEL);
        graphics.fill(geometricLayer.toPath2D(wheelConfiguration.tireConfiguration().footprint()));
        // draw slip
        Tensor tensor = wheelConfiguration.adjoint(gokartPoseEvent.getVelocity());
        graphics.setColor(COLOR_SLIP);
        graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(tensor.Get(1).multiply(SLIP_FACTOR))));
        geometricLayer.popMatrix();
      }
      // graphics.setColor(Color.BLACK);
      // graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
      // graphics.drawString("" + gokartStatusEvent.getSteerColumnEncoderCentered().map(Round._2), 100, 100);
      // see BicycleAngularSlip if angular slip is still needed
      // ChassisGeometry.GLOBAL.getBicycleAngularSlip()
    }
    graphics.setStroke(new BasicStroke());
  }
}
