// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusListener;
import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class AngularSlipRender implements RenderInterface {
  private static final AxisAlignedBox AXIS_ALIGNED_BOX = new AxisAlignedBox(RealScalar.of(0.8));
  /** max range in model space == 12
   * max rate of gokart == pi [rad/s] */
  private static final Scalar SCALE = RealScalar.of(12 / Math.PI);
  // ---
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  GokartPoseListener gokartPoseListener = gokartPoseEvent -> this.gokartPoseEvent = gokartPoseEvent;
  // ---
  private GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;
  GokartStatusListener gokartStatusListener = gokartStatusEvent -> this.gokartStatusEvent = gokartStatusEvent;
  private final Tensor matrix;

  public AngularSlipRender(Tensor matrix) {
    this.matrix = matrix;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (gokartStatusEvent.isSteerColumnCalibrated()) {
      geometricLayer.pushMatrix(matrix);
      AngularSlip angularSlip = new AngularSlip( //
          gokartPoseEvent.getVelocity(), //
          steerMapping.getRatioFromSCE(gokartStatusEvent));
      Scalar gyroZ = angularSlip.gyroZ();
      Scalar wantedRotationRate = angularSlip.wantedRotationRate();
      // ---
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(Tensors.vectorDouble(-11, 0)));
      graphics.setColor(GroundSpeedRender.COLOR_VELOCITY);
      graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(Magnitude.PER_SECOND.apply(gyroZ).multiply(SCALE))));
      geometricLayer.popMatrix();
      // ---
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(Tensors.vectorDouble(-12, 0)));
      graphics.setColor(Color.BLUE);
      graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(Magnitude.PER_SECOND.apply(wantedRotationRate).multiply(SCALE))));
      geometricLayer.popMatrix();
      // // ---
      // geometricLayer.pushMatrix(Se2Utils.toSE2Translation(Tensors.vectorDouble(-13, 0)));
      // graphics.setColor(Color.GREEN);
      // graphics.fill(geometricLayer.toPath2D(AXIS_ALIGNED_BOX.alongY(RealScalar.of(10))));
      // geometricLayer.popMatrix();
      // ---
      geometricLayer.popMatrix();
    }
  }
}
