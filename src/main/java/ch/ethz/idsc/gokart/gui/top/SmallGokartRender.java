// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.RimoSinusIonModel;
import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.gokart.calib.steer.RimoTireConfiguration;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.AxisAlignedBox;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Round;

public final class SmallGokartRender implements RenderInterface {
  private static final Tensor[] OFFSET_TORQUE = new Tensor[] { Tensors.vector(0, -0.15, 0), Tensors.vector(0, +0.15, 0) };
  private static final Tensor[] OFFSET_RATE = new Tensor[] { Tensors.vector(0, +0.15, 0), Tensors.vector(0, -0.15, 0) };
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = //
      new AxisAlignedBox(RimoTireConfiguration._REAR.halfWidth().multiply(RealScalar.of(0.8)));
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

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Tensor pose = gokartPoseEvent.getPose();
    {
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
      graphics.setColor(Color.WHITE);
      graphics.drawString("" + pose.map(Round._3), 0, 24);
    }
    geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
    { // footprint
      Tensor footprint = VEHICLE_MODEL.footprint();
      graphics.setColor(new Color(224, 224, 224, 192));
      graphics.fill(geometricLayer.toPath2D(footprint));
      graphics.setColor(new Color(224, 224, 224, 255));
      graphics.draw(geometricLayer.toPath2D(footprint, true));
    }
    { // rear wheel torques and rear wheel odometry
      Tensor tarms_pair = rimoPutEvent.getTorque_Y_pair().map(Magnitude.ARMS).multiply(RealScalar.of(5E-4));
      Tensor rateY_pair = rimoGetEvent.getAngularRate_Y_pair();
      Tensor rateY_draw = rateY_pair.map(Magnitude.PER_SECOND).multiply(RealScalar.of(0.03));
      AxleConfiguration axleConfiguration = RimoAxleConfiguration.rear();
      for (int wheel = 0; wheel < 2; ++wheel) {
        geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(axleConfiguration.wheel(wheel).local()));
        // ---
        geometricLayer.pushMatrix(Se2Matrix.translation(OFFSET_TORQUE[wheel]));
        graphics.setColor(Color.BLUE);
        graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongX(tarms_pair.Get(wheel))));
        geometricLayer.popMatrix();
        // ---
        geometricLayer.pushMatrix(Se2Matrix.translation(OFFSET_RATE[wheel]));
        graphics.setColor(new Color(0, 160, 0));
        graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongX(rateY_draw.Get(wheel))));
        geometricLayer.popMatrix();
        // ---
        geometricLayer.popMatrix();
      }
    }
    geometricLayer.popMatrix();
  }
}
